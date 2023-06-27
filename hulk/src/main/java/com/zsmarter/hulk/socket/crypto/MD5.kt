package com.zsmarter.hulk.socket.crypto

import java.security.MessageDigest

internal object MD5 {

    @Throws(Exception::class)
    fun md5(data: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(data)
        return digest.digest()
    }

    fun check(data1: String, data2: String) =
        md5(data1.toByteArray()).contentEquals(md5(data2.toByteArray()))
}