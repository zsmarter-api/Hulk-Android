package com.zsmarter.hulk.fcm_push

import android.annotation.SuppressLint
import androidx.annotation.Keep
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Create By YP On 2023/2/9.
 * 用途：
 */
@Keep
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FCMService: FirebaseMessagingService() {

    /**
     * 获取token
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logI("GoogleToken=$token")
    }

    /**
     * 接收消息
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        logI("GoogleMsgTitle=${message.notification?.title}")
        logI("GoogleMsgBody=${message.notification?.body}")
        sendMsg(message.notification?.title, message.notification?.body)
    }
}