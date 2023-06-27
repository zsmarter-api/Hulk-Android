package com.zsmarter.hulk.callback

import androidx.annotation.Keep

/**
 * Create By YP On 2023/2/8.
 * 用途：消息点击回调
 */
@Keep
interface NotificationClick {

    fun click(msgContent: String)
}