package com.zsmarter.hulk.socket

import androidx.annotation.Keep
import com.zsmarter.hulk.notification.NotificationEntity

/**
 * Author: YP
 * Time: 2022/3/18
 * Describe:
 */
@Keep
abstract class HulkMsgListener {

    // tid为-1表示不成功
    open fun onToken(tid: String?) {}

    // 通知栏消息内容
    open fun onMessage(data: NotificationEntity) {}

    // 自定义消息
    open fun onTranceMsg(str: String) {}

    // 报错
    open fun onError(code: Int, msg: String) {}

    // 通知栏消息已被撤回回调
    open fun onWithdrawn(msgId: Long) {}

    // 通知栏是否开启
    open fun onNotificationOpen(isOpen: Boolean) {}
}