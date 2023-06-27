package com.zsmarter.hulk

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.blankj.utilcode.util.SPUtils
import com.zsmarter.hulk.callback.ActionLogsCallback
import com.zsmarter.hulk.callback.ManufacturersCallback
import com.zsmarter.hulk.callback.NotificationClick
import com.zsmarter.hulk.net.HttpHelper
import com.zsmarter.hulk.notification.NotificationEntity
import com.zsmarter.hulk.notification.NotificationHelper
import com.zsmarter.hulk.notification.WithdrawEntity
import com.zsmarter.hulk.push.PushManager
import com.zsmarter.hulk.push.utils.DeviceUtils
import com.zsmarter.hulk.socket.*
import com.zsmarter.hulk.socket.ext.toJsonString
import com.zsmarter.hulk.socket.ext.toJsonStringV3
import com.zsmarter.hulk.util.BadgeUtils
import com.zsmarter.hulk.util.MoshiHelper
import com.zsmarter.hulk.util.printLogE
import com.zsmarter.hulk.util.printLogI
import org.json.JSONObject


/**
 * 开放给外部的api
 */
@Keep
object Hulk {

    private const val COUNT_URL = "countUrl"
    private lateinit var configuration: Configuration
    private var hulkDeviceInfo: HulkDeviceInfo? = null
    private var hulkUserInfo: HulkUserInfo? = null
    private var manufacturersTokenRecode: Pair<String, String>? = null

    // 操作日志回调
    var actionLogsCallBack: ActionLogsCallback? = null

    // 厂商初始化回调
    var manufacturersCallback: ManufacturersCallback? = null

    // 通知栏消息点击事件
    var notificationClick: NotificationClick? = null

    /**
     * 初始化
     * @param application 当前application
     * @param configuration 配置信息
     */
    fun init(
        application: Application,
        configuration: Configuration,
        hulkMsgListener: HulkMsgListener? = null
    ) {
        hulkDeviceInfo = configuration.hulkDeviceInfo
        SPUtils.getInstance("HulkPush").put("logEnabled", configuration.printEnable)
        Hulk.configuration = configuration
        // 通知
        NotificationHelper.init {
            notificationClick(it.toString())
        }
        configuration.notificationIcon?.let {
            NotificationHelper.notificationIcon = it
        }
        // ztp长连接
        ZTPSocketManager.connect(
            host = configuration.hulkHost,
            port = configuration.hulkPort,
            isSSL = configuration.hulkIsSSL,
            isKeepAlive = configuration.isKeepAlive,
            printLogEnable = configuration.printEnable,
            heartbeatInterval = configuration.hulkHeartbeat,
            ztpDeviceInfo = configuration.hulkDeviceInfo,
            ztpListener = object : ZTPListener() {
                override fun onOpen(ztpData: ZTPData?) {
                    actionLogsCallBack?.actionLog(
                        ActionLogsCallback.TYPE_DEVICE_INFO,
                        hulkDeviceInfo?.toJsonStringV3().toString()
                    )
                    val data = ztpData?.data?.toString(Charsets.UTF_8)
                    if (data != null) {
                        val json = JSONObject(data)
                        if (json.has("tid")) {
                            hulkMsgListener?.onToken(json.getString("tid"))
                            initUpdateManufactures()
                            printLogI("HulkPush初始化成功")
                            return
                        }
                    }
                    hulkMsgListener?.onToken(null)
                }

                override fun onNotificationOpen(isOpen: Boolean) {
                    hulkMsgListener?.onNotificationOpen(isOpen)
                }

                override fun onReceived(ztpData: ZTPData) {
                    val isOpen = DeviceUtils.isOpenNotice(application)
                    ZTPSocketManager.isOpenNotice = isOpen
                    when {
                        // 心跳
                        ztpData.type == 2 && ztpData.subType == 1 -> {
                            // 心跳日志回调
                            actionLogsCallBack?.actionLog(
                                ActionLogsCallback.TYPE_HEARTBEAT_CONNECT,
                                "成功"
                            )
                        }
                        // 消息类
                        ztpData.type == 0x7 -> {
                            val data = ztpData.data.toString(Charsets.UTF_8)
                            when (ztpData.subType) {
                                // 通知栏消息
                                0x2 -> {
                                    MoshiHelper.fromJson<NotificationEntity>(data).also {
                                        it?.let { hulkMsgListener?.onMessage(it) }
                                    }
                                    onZtpArrival(application, data)
                                }
                                // 透传消息
                                0x4 -> {
                                    hulkMsgListener?.onTranceMsg(data)
                                }
                                // 消息撤回
                                0x6 -> {
                                    val notification = MoshiHelper.fromJson<WithdrawEntity>(data)
                                    val msgId = notification?.task_id ?: 0
                                    NotificationHelper.cancelNotify(msgId.toInt())
                                    hulkMsgListener?.onWithdrawn(msgId)
                                }
                            }
                        }
                    }
                }

                override fun onError(code: Int, msg: String) {
                    hulkMsgListener?.onError(code, msg)
                }
            }
        )

    }

    /**
     * 是否是连接状态
     */
    fun isConnected() = ZTPSocketManager.isConnected()

    /**
     * 断开连接
     */
    fun disconnect() = ZTPSocketManager.disconnect()

    /**
     * 更新厂商推送token
     */
    internal fun updatePushToken(token: Pair<String, String>) {
        if (manufacturersTokenRecode != null) {
            return
        }
        manufacturersTokenRecode = token
        manufacturersCallback?.initManufacturers(
            token.first,
            token.second
        )
        if (isConnected()) {
            initUpdateManufactures()
        }
    }

    /**
     * 检测是否有未上报的厂商token，有就上报
     */
    private fun initUpdateManufactures() {
        if (manufacturersTokenRecode == null) {
            return
        }
        updateUserInfo(HulkUserInfo().apply {
            manufacturersToken = manufacturersTokenRecode
        })
    }

    /**
     * 更新用户信息
     */
    fun updateUserInfo(hulkUserInfo: HulkUserInfo) {
        this.hulkUserInfo = hulkUserInfo
        // 操作日志
        actionLogsCallBack?.actionLog(
            ActionLogsCallback.TYPE_USER_INFO,
            this.hulkUserInfo?.toJsonString() ?: ""
        )
        ZTPSocketManager.updateUserInfo(hulkUserInfo)
    }

    /**
     * 启动Activity#onNewIntent，兼容通知Extras数据在启动Activity的intent中
     */
    fun onLauncherActivityIntent(intent: Intent) {
        if (this::configuration.isInitialized) {
            val extras = PushManager.getNotificationExtras(intent)
            if (extras != "{}") {
                notificationClick(extras)
            }
        }
    }

    /**
     * 清除桌面app角标数量（暂时只支持华为）
     */
    fun clearBadgeCount(context: Context) {
        BadgeUtils.clearBadgeNumb(context)
    }

    internal fun notificationClick(extras: String) {
        if (extras.isEmpty()) return
        try {
            val json = JSONObject(extras)
            if (extras.contains(COUNT_URL)) {
                val countUrl = json.getString(COUNT_URL)
                if (countUrl.isNullOrEmpty().not() && countUrl.startsWith("http")) {
                    HttpHelper.httpGet(countUrl)
                }
            }
            printLogI(extras)
            if (isConnected()) {
                ZTPSocketManager.sendClick(json.getString("m_id"))
            }
        } catch (e: Exception) {
            printLogE("数据格式解析失败, data: $extras")
        } finally {
            notificationClick?.click(extras)
        }
    }

    internal fun onZtpArrival(context: Context, data: String) {
        // 通知
        val notification = MoshiHelper.fromJson<NotificationEntity>(data)
        NotificationHelper.showNotification(context, notification)
        printLogI("HulkPush收到的推送通知: \n$data")
    }


}