package com.zsmarter.hulk.socket.util

import java.security.MessageDigest

/**
 * 获取string的md5值
 */
internal fun String.md5(): String {
    return getMD5(this)
}

private fun getMD5(string: String): String {
    val md5 = MessageDigest.getInstance("MD5")
    md5.update(string.toByteArray())
    return bytes2Hex(md5.digest(), DIGITS_LOWER)
}

private val DIGITS_LOWER = charArrayOf(
    '0', '1', '2', '3', '4',
    '5', '6', '7', '8', '9',
    'a', 'b', 'c', 'd', 'e', 'f'
)

private fun bytes2Hex(data: ByteArray, toDigits: CharArray): String {
    val l = data.size
    val out = CharArray(l shl 1)
    // two characters form the hex value.
    var i = 0
    var j = 0
    while (i < l) {
        out[j++] = toDigits[0xF0 and data[i].toInt() ushr 4]
        out[j++] = toDigits[0x0F and data[i].toInt()]
        i++
    }
    return String(out)
}