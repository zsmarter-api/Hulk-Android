package com.zsmarter.hulk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.JsonUtils

/**
 * Create By YP On 2023/2/8.
 * 用途：接收各厂商的初始化信息及点击事件
 */
class HulkReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TOKEN = "com.zsmarter.hulk.ACTION_TOKEN"
        const val ACTION_CLICK = "com.zsmarter.hulk.ACTION_CLICK"
        const val ACTION_MSG = "com.zsmarter.hulk.ACTION_MSG"
        const val PARAMS_TOKEN_TYPE = "tokenType"
        const val PARAMS_TOKEN = "token"
        const val PARAMS_EXTRA = "extra"
        const val PARAMS_TITLE = "msg_title"
        const val PARAMS_BODY = "msg_body"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        if (intent.action == null) return
        when (intent.action) {
            ACTION_TOKEN -> {
                val tokenType = intent.getStringExtra(PARAMS_TOKEN_TYPE)
                val token = intent.getStringExtra(PARAMS_TOKEN)
                if (token.isNullOrEmpty() || tokenType.isNullOrEmpty()) return
                Hulk.updatePushToken(tokenType to token)
            }
            ACTION_CLICK -> {
                val extra = intent.getStringExtra(PARAMS_EXTRA)
                if (extra.isNullOrEmpty()) return
                Hulk.notificationClick(extra)
            }
            ACTION_MSG -> {
                context?.applicationContext?.let {
                    val title = intent.getStringExtra(PARAMS_TITLE)
                    val body = intent.getStringExtra(PARAMS_BODY)
                    if (body.isNullOrEmpty() || title.isNullOrEmpty()) return
                    val data = mapOf(
                        "title" to title,
                        "alert" to body
                    )
                    Hulk.onZtpArrival(it, GsonUtils.toJson(data))
                }
            }
        }
    }
}