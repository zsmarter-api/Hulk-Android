package com.zsmarter.hulk.xiaomi_push

import android.content.Context
import androidx.annotation.Keep
import com.blankj.utilcode.util.EncodeUtils
import com.xiaomi.mipush.sdk.*

/**
 * Create By YP On 2022/8/8.
 * 用途：
 */
@Keep
class MiReceiver : PushMessageReceiver() {

    override fun onReceivePassThroughMessage(context: Context?, message: MiPushMessage) {
        logI("小米-onReceivePassThroughMessage:$message")
    }

    override fun onNotificationMessageClicked(context: Context?, message: MiPushMessage) {
        logI("小米-onNotificationMessageClicked-message:$message")
        val content = EncodeUtils.urlDecode(message.content)
        logI("小米-onNotificationMessageClicked-message-content: $content")
        sendClick(content)
    }

    override fun onNotificationMessageArrived(context: Context?, message: MiPushMessage) {
        logI("小米-onNotificationMessageArrived:$message")
    }

    override fun onCommandResult(context: Context?, message: MiPushCommandMessage) {
        logI("小米-onCommandResult:$message")
        val command = message.command
        val arguments = message.commandArguments
        if (MiPushClient.COMMAND_REGISTER == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                val cmdArg1 = arguments[0]
                logI("小米-RegId: $cmdArg1")
                cmdArg1?.let {
                    sendToken(it)
                }
            }
        }
    }

    override fun onReceiveRegisterResult(context: Context?, message: MiPushCommandMessage) {
        logI("小米-onReceiveRegisterResult:$message")
    }
}