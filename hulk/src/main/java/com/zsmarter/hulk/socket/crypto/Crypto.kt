package com.zsmarter.hulk.socket.crypto

/**
 * 预先定义的加密类型，目前只有不加密和SM4_CBC_PKCS7加密与后端联调通过，其余的先定义，前端暂不支持所以未联调。
 */
enum class Crypto(val value: Int) {
    NONE(0x00),
    SM4_CFB_PKCS7(0x01),
    SM4_CBC_PKCS7(0x02),
    SM4_ECB_PKCS7(0x03),
    SM4_OFB_PKCS7(0x04),
    AES_CBC_PKCS5(0x05),
    AES_ECB(0x06),
    AES_CFB(0x07);

    companion object {
        fun from(value: Int): Crypto {
            return when (value) {
                0x00 -> NONE
                0x01 -> SM4_CFB_PKCS7
                0x02 -> SM4_CBC_PKCS7
                0x03 -> SM4_ECB_PKCS7
                0x04 -> SM4_OFB_PKCS7
                0x05 -> AES_CBC_PKCS5
                0x06 -> AES_ECB
                0x07 -> AES_CFB
                else -> NONE
            }
        }
    }
}


/**
 * 加密
 */
internal fun encrypt(key: ByteArray, iv: ByteArray, src: ByteArray, crypto: Crypto): ByteArray {
    return when (crypto) {
        Crypto.NONE -> src
        Crypto.SM4_CBC_PKCS7 -> SM4.encryptCBCPKCS7Padding(key, iv, src)
        else -> src
    }
}

/**
 * 解密
 */
internal fun decrypt(key: ByteArray, iv: ByteArray, src: ByteArray, crypto: Crypto): ByteArray {
    return when (crypto) {
        Crypto.NONE -> src
        Crypto.SM4_CBC_PKCS7 -> SM4.decryptCBCPKCS7Padding(key, iv, src)
        else -> src
    }
}