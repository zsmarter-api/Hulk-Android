package com.zsmarter.hulk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zsmarter.hulk.notification.NotificationHelper
import com.zsmarter.hulk.util.BadgeUtils
import com.zsmarter.hulk.util.printLogI

/**
 * Author: YP
 * Time: 2022/3/14
 * Describe:透明的中转Activity
 */
internal class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BadgeUtils.changeHWBadgeNumb(this, false)
        val extras = intent.getStringExtra(NotificationHelper.PARAM_DATA)
        printLogI("HulkPush-MainActivity:${extras ?: "无"}")
        NotificationHelper.onNotificationClickListener?.invoke(extras)
        finish()
    }
}