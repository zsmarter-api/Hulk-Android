package com.zsmarter.hulk

import androidx.annotation.Keep
import com.zsmarter.hulk.socket.HulkDeviceInfo
import com.zsmarter.hulk.socket.ZTPSocketManager

/**
 * 配置参数清单
 */
@Keep
class Configuration constructor(
    val hulkHost: String,
    val hulkPort: Int,
    val hulkDeviceInfo: HulkDeviceInfo
) {
    var hulkIsSSL: Boolean = true
    var isKeepAlive: Boolean = true
    var printEnable: Boolean = ZTPSocketManager.printLogEnable
    var hulkHeartbeat: Long = 55 * 1000L
    var notificationIcon: Int? = null
    var unknownThirdPartyDevicePush: ArrayList<Pair<String, List<String>>>? = arrayListOf()


    constructor(
        hulkHost: String,
        hulkPort: Int,
        hulkDeviceInfo: HulkDeviceInfo,
        hulkIsSSL: Boolean? = true,
        isKeepAlive: Boolean? = true,
        printEnable: Boolean? = ZTPSocketManager.printLogEnable,
        hulkHeartbeat: Long? = 55 * 1000L,
        notificationIcon: Int? = null,
    ) : this(hulkHost, hulkPort, hulkDeviceInfo) {
        this.hulkIsSSL = hulkIsSSL ?: true
        this.isKeepAlive = isKeepAlive ?: true
        this.printEnable = printEnable ?: ZTPSocketManager.printLogEnable
        this.hulkHeartbeat = hulkHeartbeat ?: (55 * 1000L)
        this.notificationIcon = notificationIcon
        this.unknownThirdPartyDevicePush = unknownThirdPartyDevicePush ?: arrayListOf()
    }
}