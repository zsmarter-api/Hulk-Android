package com.zsmarter.hulk.socket

import com.zsmarter.hulk.socket.crypto.Crypto
import com.zsmarter.hulk.socket.crypto.MD5
import com.zsmarter.hulk.socket.excption.HeartbeatTimeoutException
import com.zsmarter.hulk.socket.excption.ZTPConnectionDeadException
import com.zsmarter.hulk.socket.excption.ZTPSocketConnectException
import com.zsmarter.hulk.socket.ext.*
import com.zsmarter.hulk.socket.util.*
import com.zsmarter.hulk.util.printLogI
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.SocketFactory
import javax.net.ssl.SSLSocketFactory

/**
 * ZTPSocketClient连接步骤：
 *
 * 1.建立socket连接
 * 2.发送T1S1握手
 * 3.收到T1S2握手返回校验data
 * 4.发送T1S3提交设备信息
 * 5.设备信息提交成功开启心跳
 * 6.发送消息给服务端
 *
 * @param host 域名
 * @param port 端口
 * @param version 版本，目前支持持0x1和0x3
 * @param retryInterval 连接断开了重连的时间间隔
 * @param heartbeatInterval 心跳间隔
 * @param ztpDeviceInfo 获取设备信息
 * @param ztpListener 回调监听器
 */
@Suppress("BlockingMethodInNonBlockingContext")
internal class ZTPSocketClient(
    private val host: String,
    private val port: Int,
    private val version: Int = 3,
    private val retryInterval: Long = 5000L,
    private val heartbeatInterval: Long = 55000L,
    private val ztpDeviceInfo: HulkDeviceInfo,
    private val isSSL: Boolean,
    private val ztpListener: ZTPListener
) {

    companion object {
        const val ERROR_CODE_CONNECT = -0x1
        const val ERROR_CODE_HEARTBEAT = -0x2
        const val ERROR_CODE_OTHER = -0x3

        internal const val V1 = 0x1
        internal const val V3 = 0x3
    }

    private val readLock = Any()
    private val writeLock = Any()
    private var mainScope = MainScope()
    private var randomString: String = getRandomString(12)
    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    // 心跳序号
    private var pingNo = 0x1

    // 是否完成连接并握手成功
    private var done = false

    // 断开是否自动重连
    private var isAutoReconnect = true

    // 上一次接收到服务端推送的消息的时间
    private var lastReceivedTime = System.currentTimeMillis()

    // 加解密key
    private var key = byteArrayOf()

    // 加解密偏移量iv
    private var iv = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    // 用户信息
    private var hulkUserInfo: HulkUserInfo? = null

    init {
        if (version != V1 && version != V3) {
            throw IllegalArgumentException("version must be 0x1 or 0x3")
        }
    }

    private fun onMessage(message: ZTPData) {
        // 回复收到消息
        replyReceived(message)
        when {
            // T1S2 握手成功
            message.type == 0x1 && message.subType == 0x2 -> {
                when (version) {
                    V1 -> {
                        val check = MD5
                            .md5("${randomString}abcdefg".toByteArray())
                            .contentEquals(message.data)
                        if (check) {
                            submitDeviceInfo(ztpDeviceInfo.toJsonStringV1())
                        } else {
                            printLogI("握手md5校验失败.")
                        }
                    }
                    V3 -> {
                        key = message.data
                        submitDeviceInfo(ztpDeviceInfo.toJsonStringV3())
                    }
                    else -> {
                        printLogI("不支持的版本 $version")
                    }
                }
            }
            // T1S3 设备信息注册成功
            message.type == 0x1 && message.subType == 0x3 -> {
                done = true
                startHeartbeat()
                submitUserInfo(hulkUserInfo)
                ztpListener.onOpen(message.toDecrypted(key, iv))
            }
            else -> {
                ztpListener.onReceived(message.toDecrypted(key, iv))
            }
        }
    }

    /**
     * 连接
     */
    fun connect() {
        if (mainScope.isActive) {
            mainScope.cancel()
        }
        mainScope = MainScope()
        done = false
        pingNo = 0x1
        isAutoReconnect = true
        lastReceivedTime = System.currentTimeMillis()

        launchOnMainScope {
            try {
                doConnect()
            } catch (e: IOException) {
                throw ZTPSocketConnectException("socket connect failed: ${e.message}")
            }
            receive()
            handshake()
            // 约定时间内如果握手没有完成，则重连
            delay(retryInterval)
            if (done.not()) {
                throw ZTPSocketConnectException("socket handshake timeout.")
            }
        }
    }

    /**
     * 建立连接
     */
    private suspend fun doConnect() {
        withContext(Dispatchers.IO) {
            printLogI("[连接]: host: $host | port: $port | version: $version")
            socket = if (isSSL) {
                SSLSocketFactory.getDefault()
            } else {
                SocketFactory.getDefault()
            }.createSocket()
                .also { it.connect(InetSocketAddress(host, port), 1000) }
            inputStream = socket?.getInputStream()
            outputStream = socket?.getOutputStream()
        }
    }

    /**
     * 发送数据
     * @param data 协议数据
     * @param callback 发送回调
     */
    fun send(data: ZTPData, callback: ((Boolean) -> Unit)? = null) {
        val sendData = data
            .also {
                it.encrypt = getCryptoByVersion(version).value
                it.version = version
            }
            .toEncrypted(key, iv)
        launchOnMainScope {
            if (it.isActive) {
                try {
                    doSend(sendData)
                } catch (e: IOException) {
                    callback?.invoke(false)
                }
                callback?.invoke(true)
            } else {
                callback?.invoke(false)
            }
        }
    }

    /**
     * 执行发送
     */
    private suspend fun doSend(ztpData: ZTPData) {
        withContext(Dispatchers.IO) {
            synchronized(writeLock) {
                printLogI("[发送]: ${ztpData.toDecrypted(key, iv).content}")
                outputStream?.write(ztpData.getBytes())
            }
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        printLogI("断开HulkPush连接")
        launchOnMainScope {
            try {
                closeSocket()
            } catch (_: IOException) {
            }
            isAutoReconnect = false
            done = false
            mainScope.cancel()
        }
    }

    /**
     * 子线程关闭连接
     */
    private suspend fun closeSocket() {
        withContext(Dispatchers.IO) {
            inputStream?.close()
            outputStream?.close()
            socket?.close()
        }
    }

    /**
     * 接收数据
     */
    private fun receive() {
        launchOnMainScope {
            while (it.isActive) {
                try {
                    val data = doReceive()
                    if (data != null) {
                        lastReceivedTime = System.currentTimeMillis()
                        onMessage(data)
                    }
                } catch (e: IOException) {
                    throw ZTPConnectionDeadException("接收数据读取失败：${e.message}")
                }
            }
        }
    }

    /**
     * 执行接收
     */
    private suspend fun doReceive(): ZTPData? {
        return withContext(Dispatchers.IO) {
//            synchronized(readLock) {
                val receivedData = inputStream?.readProtocolData()
                printLogI("[接收]: ${receivedData?.toDecrypted(key, iv)?.content}")
                return@withContext receivedData
//            }
        }
    }

    /**
     * 开启心跳
     */
    private fun startHeartbeat() {
        launchOnMainScope {
            while (it.isActive) {
                if (System.currentTimeMillis() - lastReceivedTime >= 2.1 * heartbeatInterval) {
                    throw HeartbeatTimeoutException("heartbeat time out.")
                }
                // 心跳失败，忽略，因为有超时机制，超时后会自动重试
                kotlin.runCatching {
                    doHeartbeat()
                }
                delay(heartbeatInterval)
            }
        }
    }

    /**
     * 心跳，T2S1
     */
    private suspend fun doHeartbeat() {
        val heartbeatData = ZTPData().also {
            it.version = version
            it.type = 0x2
            it.subType = 0x1
            it.ack = 0x1
            it.seq = pingNo++
            it.encrypt = 0x0  // 不加密
        }
        doSend(heartbeatData)
    }

    /**
     * 握手，T1S1
     */
    private suspend fun handshake() {
        randomString = getRandomString(12)
        val handshakeData = ZTPData().also {
            it.version = version
            it.type = 0x1
            it.subType = 0x1
            it.data = randomString.toByteArray()
            it.ack = 0x1
            it.seq = 0x0
            it.encrypt = 0x0  // 不加密
        }
        doSend(handshakeData)
    }

    /**
     * 提交设备信息，T1S3
     */
    private fun submitDeviceInfo(deviceInfo: String) {
        val deviceInfoData = ZTPData().also {
            it.version = version
            it.type = 0x1
            it.subType = 0x3
            it.data = deviceInfo.toByteArray()
            it.ack = 0x1
            it.seq = 0x0
            it.encrypt = getCryptoByVersion(version).value
        }.toEncrypted(key, iv)
        launchOnMainScope { doSend(deviceInfoData) }
    }

    /**
     * 提交用户信息，T1S9
     */
    fun submitUserInfo(hulkUserInfo: HulkUserInfo?) {
        if (hulkUserInfo == null) return
        this.hulkUserInfo = hulkUserInfo
        if (isConnected().not()) return
        val userInfoData = ZTPData().also {
            it.version = version
            it.type = 0x1
            it.subType = 0x9
            it.data = hulkUserInfo.toJsonString().toByteArray()
            it.ack = 0x1
            it.seq = 0x0
            it.encrypt = getCryptoByVersion(version).value
        }.toEncrypted(key, iv)
        launchOnMainScope { doSend(userInfoData) }
    }

    /**
     * 提交用户已读消息（点击），T7S1
     */
    fun submitClick(mId: String?) {
        if (mId.isNullOrEmpty() || isConnected().not()) return
        val data = ZTPData().also {
            it.version = version
            it.type = 0x7
            it.subType = 0x1
            it.data = JSONObject().apply { put("m_id", mId) }.toString().toByteArray()
            it.ack = 0x1
            it.seq = 0x0
            it.encrypt = getCryptoByVersion(version).value
        }.toEncrypted(key, iv)
        launchOnMainScope { doSend(data) }
    }

    /**
     * 提交通知栏状态T7S3
     */
    fun submitNoticeStatus(isOpen: Boolean) {
        val data = ZTPData().also {
            it.version = version
            it.type = 0x7
            it.subType = 0x3
            it.data = JSONObject().apply { put("notification", isOpen) }.toString().toByteArray()
            it.ack = 0x1
            it.seq = 0x0
            it.encrypt = getCryptoByVersion(version).value
        }.toEncrypted(key, iv)
        launchOnMainScope { doSend(data) }
    }

    /**
     * 回复服务端已收到消息
     */
    private fun replyReceived(message: ZTPData) {
        if (message.ack != 0x1) return
        val replyData = ZTPData().also {
            it.version = version
            it.type = message.type
            it.subType = message.subType
            it.ack = 0x2
            it.seq = message.seq
            it.encrypt = 0x0  // 不加密
        }
        launchOnMainScope { doSend(replyData) }
    }

    /**
     * 在协程中执行
     * @param block suspend代码块
     */
    private fun launchOnMainScope(block: suspend (CoroutineScope) -> Unit) {
        mainScope.launch {
            try {
                block.invoke(this)
            } catch (e: Exception) {
                when (e) {
                    is ZTPSocketConnectException -> {
                        // 连接失败，重连
                        printLogI("连接失败: ${e.message.toString()}")
                        reconnect()
                    }
                    is HeartbeatTimeoutException -> {
                        // 心跳超时，重连
                        printLogI("心跳超时: ${e.message.toString()}")
                        reconnect()
                    }
                    is ZTPConnectionDeadException ->{
                        // 连接中断
                        printLogI("连接中断: ${e.message.toString()}")
                        reconnect()
                    }
                    is CancellationException -> {
                        println("mainScope协程取消了")
                    }
                    is IOException -> {
                        ztpListener.onError(ERROR_CODE_OTHER, "其他异常：${e.message.toString()}")
                        printLogI("其他异常：${e.message.toString()}")
                    }
                    else -> {
                        throw e
                    }
                }
            }
        }
    }

    /**
     * 根据对应版本获取加密方式
     * @param version 版本
     */
    private fun getCryptoByVersion(version: Int): Crypto {
        return when (version) {
            V1 -> Crypto.NONE
            V3 -> Crypto.SM4_CBC_PKCS7
            else -> Crypto.NONE
        }
    }

    /**
     *重新连接
     */
    private fun reconnect() {
        if (isAutoReconnect.not()) return
        launchOnMainScope {
            closeSocket()
            delay(retryInterval)

            printLogI("开始重连HulkPush...")
            connect()
        }
    }

    /**
     * 是否是连接上的
     */
    fun isConnected(): Boolean {
        return socket?.isConnected == true && done
    }
}