package com.zsmarter.hulk.vivo_push

import android.content.Context
import androidx.annotation.Keep
import com.blankj.utilcode.util.GsonUtils
import com.vivo.push.model.UPSNotificationMessage
import com.vivo.push.sdk.OpenClientPushMessageReceiver

/**
 * Create By YP On 2022/8/8.
 * 用途：
 */
@Keep
class VivoReceiver : OpenClientPushMessageReceiver() {

    override fun onNotificationMessageClicked(context: Context?, msg: UPSNotificationMessage?) {
        val params: Map<String, String> = msg?.params ?: mapOf()
        val content = GsonUtils.toJson(params).toString()
        logI("vivo-onNotificationMessageClicked: $content")
        sendClick(content)
    }

    override fun onReceiveRegId(context: Context?, regId: String?) {
        logI("vivo-onReceiveRegId:$regId")
    }
}