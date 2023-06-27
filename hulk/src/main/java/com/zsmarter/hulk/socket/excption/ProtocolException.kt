package com.zsmarter.hulk.socket.excption

/**
 * 协议异常
 * @param message 异常信息
 */
class ProtocolException(override val message: String?) : RuntimeException(message) {
}