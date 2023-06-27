package com.zsmarter.hulk.socket.util

import com.zsmarter.hulk.socket.excption.ProtocolException
import java.io.BufferedInputStream
import java.security.SecureRandom

/**
 * 检查协议异常
 */
internal fun checkProtocolException(boolean: Boolean, message: () -> String) {
    if (boolean.not()) {
        throw ProtocolException(message.invoke())
    }
}

/**
 * 从inputStream中读取指定长度的ByteArray
 */
internal fun BufferedInputStream.readByteArray(length: Int): ByteArray {
    val byteArray = ByteArray(length)
    var readed = 0
    while (readed < length) {
        readed += this.read(byteArray, readed, length - readed)
            .coerceAtLeast(0)
    }
    return byteArray
}

/**
 * 获取指定长度的随机字符串
 */
internal fun getRandomString(length: Int): String {
    val str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890"
    val random = SecureRandom()
    val sb = StringBuffer()
    for (i in 0 until length) {
        val number = random.nextInt(62)
        sb.append(str[number])
    }
    return sb.toString()
}