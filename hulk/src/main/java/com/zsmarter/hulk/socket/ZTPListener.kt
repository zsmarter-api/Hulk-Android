package com.zsmarter.hulk.socket

/**
 * ZTPSocket消息监听接口
 */
abstract class ZTPListener {
    open val tag = "default"
    open fun onOpen(ztpData: ZTPData? = null) {}
    open fun onNotificationOpen(isOpen: Boolean) {}
    open fun onReceived(ztpData: ZTPData) {}
    open fun onError(code: Int, msg: String) {}
}