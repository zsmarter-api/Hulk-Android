package com.zsmarter.hulk.socket

import androidx.annotation.Keep

/**
 * ZTPSocket连接所需的设备信息
 * @param appKey appKey
 * @param appSecret appSec
 * @param appVersion app版本
 * @param jPushId 极光推送id
 */
@Keep
data class HulkDeviceInfo constructor(
    val appKey: String,
    val appSecret: String,
    val appVersion: String
) {
    var jPushId: String? = ""

    constructor(
        appKey: String,
        appSecret: String,
        appVersion: String,
        jPushId: String
    ) : this(appKey, appSecret, appVersion) {
        this.jPushId = jPushId
    }
}