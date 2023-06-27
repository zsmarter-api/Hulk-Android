package com.zsmarter.hulk.socket.crypto

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by xiaojianjun on 2019-09-19.
 */
internal class AES(transformation: String) {

    companion object {

        private val DEFAULT_KEY = byteArrayOf(
            49, 50, 51, 52,
            53, 54, 55, 56,
            49, 50, 51, 52,
            53, 54, 55, 56
        )

        val CBC = "AES/CBC/NoPadding"
        val ECB = "AES/ECB/NoPadding"
        val CFB = "AES/CFB/NoPadding"
    }

    private val cipher = Cipher.getInstance(transformation)

    /**
     * 加密
     * @param content 需要加密的内容
     * @param key key
     * @return 加密后的密文
     */
    @JvmOverloads
    @Throws(Exception::class)
    fun encrypt(content: ByteArray, key: ByteArray = DEFAULT_KEY): ByteArray {
        val keySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(ByteArray(cipher.blockSize)))
        return cipher.doFinal(content)
    }

    /**
     * 解密
     * @param content 需要解密的密文
     * @param key key
     * @return 解密后的明文
     */
    @JvmOverloads
    @Throws(Exception::class)
    fun decrypt(content: String, key: ByteArray = DEFAULT_KEY): ByteArray {
        val keySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(ByteArray(cipher.blockSize)))
        val base64Decode = Base64.decode(content, Base64.DEFAULT)
        return cipher.doFinal(base64Decode)
    }


}