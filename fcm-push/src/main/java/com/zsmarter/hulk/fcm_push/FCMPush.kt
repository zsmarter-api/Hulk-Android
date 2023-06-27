package com.zsmarter.hulk.fcm_push

import androidx.annotation.Keep
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

@Keep
object FCMPush {

    /**
     * 初始化厂商
     * @param isAutoInitEnabled 是否自动初始化
     */
    fun init(isAutoInitEnabled: Boolean = true) {
        Firebase.messaging.isAutoInitEnabled = isAutoInitEnabled
        if (isAutoInitEnabled) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    logE("Fetching FCM registration token failed= ${task.exception}")
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                logI("FCM registration token success = $token")
                sendToken(token)
            })
        }
    }
}