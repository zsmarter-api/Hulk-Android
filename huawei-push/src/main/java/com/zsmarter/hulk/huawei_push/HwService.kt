package com.zsmarter.hulk.huawei_push

import androidx.annotation.Keep
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage

/**
 * Create By YP On 2022/8/8.
 * 用途：
 */
@Keep
class HwService : HmsMessageService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logI("华为-onNewToken: $token")
        sendToken(token)
    }

    override fun onTokenError(e: Exception) {
        super.onTokenError(e)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            logI("华为-onMessageReceived-data: " + remoteMessage.data)
        }
        if (remoteMessage.notification != null) {
            logI("华为-onMessageReceived-notification: " + remoteMessage.notification.body)
        }
    }

    override fun onMessageSent(s: String) {
        logI("华为-onMessageSent: $s")
    }

    override fun onSendError(s: String, e: Exception) {
        logE("华为-onSendError: $s, $e")
    }
}