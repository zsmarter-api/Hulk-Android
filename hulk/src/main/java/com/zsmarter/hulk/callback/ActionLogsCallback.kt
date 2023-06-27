package com.zsmarter.hulk.callback

import androidx.annotation.Keep

/**
 * Create By YP On 2023/2/8.
 * 用途：操作日志回调
 */
@Keep
interface ActionLogsCallback {

    companion object {
        const val TYPE_HEARTBEAT_CONNECT = "心跳连接"
        const val TYPE_DEVICE_INFO = "上报设备信息"
        const val TYPE_USER_INFO = "更新用户信息"
    }

    fun actionLog(type: String, content: String)
}