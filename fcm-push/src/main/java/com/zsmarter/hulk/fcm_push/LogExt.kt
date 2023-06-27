package com.zsmarter.hulk.fcm_push

import android.content.ComponentName
import android.content.Intent
import android.util.Log
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils

/**
 * Create By YP On 2023/2/7.
 * 用途：
 */
internal fun logI(msg: String?) {
    val logEnabled = SPUtils.getInstance("HulkPush").getBoolean("logEnabled")
    if (logEnabled) {
        Log.i("HulkPush", msg.toString())
    }
}

internal fun logE(msg: String?) {
    val logEnabled = SPUtils.getInstance("HulkPush").getBoolean("logEnabled")
    if (logEnabled) {
        Log.e("HulkPush", msg.toString())
    }
}

fun sendToken(token: String) {
    val intent = Intent()
    intent.component = ComponentName(
        ActivityUtils.getTopActivity().application.packageName,
        "com.zsmarter.hulk.HulkReceiver"
    )
    intent.action = "com.zsmarter.hulk.ACTION_TOKEN"
    intent.putExtra("tokenType", "fcm")
    intent.putExtra("token", token)
    ActivityUtils.getTopActivity().application.sendBroadcast(intent)
}

fun sendClick(extra: String?) {
    if (extra.isNullOrEmpty()) return
    val intent = Intent()
    intent.component = ComponentName(
        ActivityUtils.getTopActivity().application.packageName,
        "com.zsmarter.hulk.HulkReceiver"
    )
    intent.action = "com.zsmarter.hulk.ACTION_CLICK"
    intent.putExtra("extra", extra)
    ActivityUtils.getTopActivity().application.sendBroadcast(intent)
}

fun sendMsg(title: String?, body: String?) {
    if (title == null || body == null) return
    val intent = Intent()
    intent.component = ComponentName(
        ActivityUtils.getTopActivity().application.packageName,
        "com.zsmarter.hulk.HulkReceiver"
    )
    intent.action = "com.zsmarter.hulk.ACTION_MSG"
    intent.putExtra("msg_title", title)
    intent.putExtra("msg_body", body)
    ActivityUtils.getTopActivity().application.sendBroadcast(intent)
}
