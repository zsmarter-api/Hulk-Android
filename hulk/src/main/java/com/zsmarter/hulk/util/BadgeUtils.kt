package com.zsmarter.hulk.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.Keep
import com.zsmarter.hulk.push.utils.DeviceUtils

/**
 * Create By YP On 2023/4/17.
 * 用途：角标工具类
 */
internal object BadgeUtils {

    var badgeNumber = 0

    internal fun setVIVOBadgeNum(context: Context) {
        try {
            val intent = Intent("launcher.actionLog.CHANGE_APPLICATION_NOTIFICATION_NUM").apply {
                putExtra("packageName", context.packageName)
                val launchClassName =
                    context.packageManager.getLaunchIntentForPackage(context.packageName)?.component?.className
                putExtra("className", launchClassName)
                putExtra("notificationNum", 1)
            }
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置华为的角标
     */
    private fun setHWBadgeNum(context: Context, count: Int) {
        val bundle = Bundle().apply {
            putString("package", context.packageName)
            putString("class", DeviceUtils.getLauncherClassName(context))
            putInt("badgenumber", count)
        }
        context.contentResolver.call(
            Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
            "change_badge",
            null,
            bundle
        )
    }

    /**
     * 清除桌面角标
     */
    internal fun clearBadgeNumb(context: Context) {
        when (DeviceUtils.getManufacturer()) {
            DeviceUtils.HUAWEI -> setHWBadgeNum(context, 0)
        }
    }

    /**
     * 改变华为角标数量
     */
    internal fun changeHWBadgeNumb(context: Context, isAdd: Boolean = true) {
        if (isAdd) {
            ++badgeNumber
        } else {
            --badgeNumber
        }
        setHWBadgeNum(context, badgeNumber)
    }
}