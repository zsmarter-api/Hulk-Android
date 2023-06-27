package com.zsmarter.hulk.socket

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.zsmarter.hulk.util.printLogI

internal object ZTPSocketManager {

    var printLogEnable = false
    private var socketClient: ZTPSocketClient? = null
    private var ztpListeners = mutableListOf<ZTPListener>()
    var isOpenNotice: Boolean? = null
        set(value) {
            if (value == false) {
                ztpListeners.forEach {
                    it.onNotificationOpen(value)
                }
            }
            if (value == null || field == value) {
                return
            }
            printLogI("通知栏开启状态:$value")
            field = value
            socketClient?.submitNoticeStatus(field!!)
        }

    /**
     * 初始化
     * @param host socket host
     * @param port socket port
     * @param version 版本，目前只支持1或3
     * @param retryInterval 重连间隔
     * @param heartbeatInterval 心跳间隔
     * @param isKeepAlive true-一直连接，false-只有应用在前台时会连接
     * @param ztpDeviceInfo 设备信息
     * @param ztpListener 消息监听
     */
    fun connect(
        host: String = "47.108.219.214",
        port: Int = 8443,
        version: Int = ZTPSocketClient.V3,
        isSSL: Boolean = true,
        retryInterval: Long = 5000L,
        heartbeatInterval: Long = 55000L,
        isKeepAlive: Boolean = true,
        printLogEnable: Boolean = false,
        ztpDeviceInfo: HulkDeviceInfo,
        ztpListener: ZTPListener,
    ) {
        ZTPSocketManager.printLogEnable = printLogEnable
        if (ztpListeners.any { it.tag == ztpListener.tag }.not()){
            ztpListeners.add(ztpListener)
        }
        socketClient?.disconnect()
        socketClient = ZTPSocketClient(
            host = host,
            port = port,
            version = version,
            isSSL = isSSL,
            retryInterval = retryInterval,
            heartbeatInterval = heartbeatInterval,
            ztpDeviceInfo = ztpDeviceInfo,
            ztpListener = internalZTPListener
        )

        if (isKeepAlive) {
            socketClient?.connect()
        } else {
            onlyWorkOnForeground()
        }
    }

    /**
     * 内部监听器
     */
    private val internalZTPListener = object : ZTPListener() {
        override fun onOpen(ztpData: ZTPData?) {
            ztpListeners.forEach {
                it.onOpen(ztpData)
            }
        }

        override fun onNotificationOpen(isOpen: Boolean) {
            ztpListeners.forEach {
                it.onNotificationOpen(isOpen)
            }
        }

        override fun onReceived(ztpData: ZTPData) {
            ztpListeners.forEach {
                it.onReceived(ztpData)
            }
        }

        override fun onError(code: Int, msg: String) {
            ztpListeners.forEach {
                it.onError(code, msg)
            }
        }
    }

    /**
     * ztpSocket只有在应用在前台时工作
     */
    private fun onlyWorkOnForeground() {
        // 应用在前台时开启socket连接，退到后台则断开socket连接
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        socketClient?.connect()
                    }
                    Lifecycle.Event.ON_STOP -> {
                        socketClient?.disconnect()
                    }
                    else -> return
                }
            }
        })
    }

    /**
     * 发送
     * @param data 协议数据
     * @param callback 发送回调
     */
    fun send(data: ZTPData, callback: ((Boolean) -> Unit)? = null) {
        socketClient?.send(data, callback)
    }

    /**
     * 更新用户信息
     * @param hulkUserInfo 用户信息
     */
    fun updateUserInfo(hulkUserInfo: HulkUserInfo) {
        socketClient?.submitUserInfo(hulkUserInfo)
    }

    /**
     * 上报已读状态
     */
    fun sendClick(mId: String) {
        socketClient?.submitClick(mId)
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        socketClient?.disconnect()
    }

    /**
     * 是否是连接中
     */
    fun isConnected() = socketClient?.isConnected() ?: false
}