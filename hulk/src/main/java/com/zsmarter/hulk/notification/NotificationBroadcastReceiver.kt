package com.zsmarter.hulk.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zsmarter.hulk.util.BadgeUtils

/**
 * Create By YP On 2023/4/17.
 * 用途：
 */


/**
 * 广播监听通知栏消息
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TYPE = "type"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val type = intent.getIntExtra(TYPE, -1)
        if (type != -1) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(type)
        }
        if (action == "notification_cancelled") {
            BadgeUtils.clearBadgeNumb(context)
        }
    }
}