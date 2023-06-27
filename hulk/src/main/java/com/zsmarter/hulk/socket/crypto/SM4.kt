package com.zsmarter.hulk.socket.crypto

import android.annotation.SuppressLint
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.Mac
import org.bouncycastle.crypto.engines.SM4Engine
import org.bouncycastle.crypto.macs.CBCBlockCipherMac
import org.bouncycastle.crypto.macs.GMac
import org.bouncycastle.crypto.modes.GCMBlockCipher
import org.bouncycastle.crypto.paddings.BlockCipherPadding
import org.bouncycastle.crypto.paddings.PKCS7Padding
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object SM4 {

    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    private const val SM4 = "SM4"
    private const val SM4_ECB_PKCS5_PADDING = "SM4/ECB/PKCS5Padding"
    private const val SM4_ECB_NO_PADDING = "SM4/ECB/NoPadding"
    private const val SM4_CBC_PKCS7_PADDING = "SM4/CBC/PKCS7Padding"
    private const val SM4_CBC_NO_PADDING = "SM4/CBC/NoPadding"

    /**
     * SM4算法目前只支持128位（即密钥16字节）
     */
    private const val DEFAULT_KEY_SIZE = 128

    /**
     * 生成SM4加密Key，长度只支持为128位/16字节
     */
    @JvmOverloads
    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class)
    fun generateKey(keySize: Int = DEFAULT_KEY_SIZE): ByteArray {
        val kg = KeyGenerator.getInstance(SM4, BouncyCastleProvider.PROVIDER_NAME)
        kg.init(keySize, SecureRandom())
        return kg.generateKey().encoded
    }

    /**
     * 加密，采用SM4/ECB/PKCS5Padding算法
     * @param key 秘钥
     * @param data 源数据
     * @return 加密结果
     */
    @Throws(
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encryptECBPKCS5Padding(key: ByteArray, data: ByteArray?): ByteArray {
        val cipher = generateECBCipher(SM4_ECB_PKCS5_PADDING, Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data)
    }

    /**
     * 解密，采用SM4/ECB/PKCS5Padding算法
     * @param key 秘钥
     * @param cipherText 密文
     * @return 解密后的原文
     */
    @Throws(
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class
    )
    fun decryptECBPKCS5Padding(key: ByteArray, cipherText: ByteArray?): ByteArray {
        val cipher = generateECBCipher(SM4_ECB_PKCS5_PADDING, Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(cipherText)
    }

    /**
     * 加密，采用SM4/ECB/NoPadding算法
     * @param key 秘钥
     * @param data 源数据
     * @return 加密结果
     */
    @Throws(
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encryptECBNoPadding(key: ByteArray, data: ByteArray?): ByteArray {
        val cipher = generateECBCipher(SM4_ECB_NO_PADDING, Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data)
    }

    /**
     * 解密，采用SM4/ECB/NoPadding算法
     * @param key 秘钥
     * @param cipherText 密文
     * @return 解密后的原文
     */
    @Throws(
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class
    )
    fun decryptECBNoPadding(key: ByteArray, cipherText: ByteArray?): ByteArray {
        val cipher = generateECBCipher(SM4_ECB_NO_PADDING, Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(cipherText)
    }

    /**
     * 加密，采用SM4/CBC/PKCS7Padding算法
     * @param key 秘钥
     * @param data 源数据
     * @return 加密结果
     */
    @Throws(
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidAlgorithmParameterException::class
    )
    fun encryptCBCPKCS7Padding(key: ByteArray, iv: ByteArray, data: ByteArray?): ByteArray {
        val cipher = generateCBCCipher(SM4_CBC_PKCS7_PADDING, Cipher.ENCRYPT_MODE, key, iv)
        return cipher.doFinal(data)
    }

    /**
     * 解密，采用SM4/CBC/PKCS7Padding算法
     * @param key 秘钥
     * @param cipherText 密文
     * @return 解密后的原文
     */
    @Throws(
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidAlgorithmParameterException::class
    )
    fun decryptCBCPKCS7Padding(key: ByteArray, iv: ByteArray, cipherText: ByteArray?): ByteArray {
        val cipher = generateCBCCipher(SM4_CBC_PKCS7_PADDING, Cipher.DECRYPT_MODE, key, iv)
        return cipher.doFinal(cipherText)
    }

    /**
     * 加密，采用SM4/CBC/NoPadding算法
     * @param key 秘钥
     * @param data 源数据
     * @return 加密结果
     */
    @Throws(
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidAlgorithmParameterException::class
    )
    fun encryptCBCNoPadding(key: ByteArray, iv: ByteArray, data: ByteArray?): ByteArray {
        val cipher = generateCBCCipher(SM4_CBC_NO_PADDING, Cipher.ENCRYPT_MODE, key, iv)
        return cipher.doFinal(data)
    }

    /**
     * 解密，采用SM4/CBC/NoPadding算法
     * @param key 秘钥
     * @param cipherText 密文
     * @return 解密后的原文
     */
    @Throws(
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidAlgorithmParameterException::class
    )
    fun decryptCBCNoPadding(key: ByteArray, iv: ByteArray, cipherText: ByteArray?): ByteArray {
        val cipher = generateCBCCipher(SM4_CBC_NO_PADDING, Cipher.DECRYPT_MODE, key, iv)
        return cipher.doFinal(cipherText)
    }

    @Throws(
        NoSuchProviderException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class
    )
    fun doCMac(key: ByteArray?, data: ByteArray): ByteArray {
        val keyObj: Key = SecretKeySpec(key, SM4)
        return doMac("SM4-CMAC", keyObj, data)
    }

    fun doGMac(key: ByteArray, iv: ByteArray, tagLength: Int, data: ByteArray): ByteArray {
        val mac: Mac = GMac(GCMBlockCipher(SM4Engine()), tagLength * 8)
        return doMac(mac, key, iv, data)
    }

    /**
     * 默认使用PKCS7Padding/PKCS5Padding填充的CBCMAC
     */
    fun doCBCMac(key: ByteArray, iv: ByteArray, data: ByteArray): ByteArray {
        val engine = SM4Engine()
        val mac: Mac = CBCBlockCipherMac(engine, engine.blockSize * 8, PKCS7Padding())
        return doMac(mac, key, iv, data)
    }

    @Throws(Exception::class)
    fun doCBCMac(
        key: ByteArray,
        iv: ByteArray,
        padding: BlockCipherPadding?,
        data: ByteArray
    ): ByteArray {
        val engine = SM4Engine()
        if (padding == null) {
            if (data.size % engine.blockSize != 0) {
                throw Exception("if no padding, data length must be multiple of SM4 BlockSize")
            }
        }
        val mac: Mac = CBCBlockCipherMac(engine, engine.blockSize * 8, padding)
        return doMac(mac, key, iv, data)
    }

    private fun doMac(mac: Mac, key: ByteArray, iv: ByteArray, data: ByteArray): ByteArray {
        val cipherParameters: CipherParameters = KeyParameter(key)
        mac.init(ParametersWithIV(cipherParameters, iv))
        mac.update(data, 0, data.size)
        val result = ByteArray(mac.macSize)
        mac.doFinal(result, 0)
        return result
    }

    @Throws(
        NoSuchProviderException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class
    )
    private fun doMac(algorithmName: String, key: Key, data: ByteArray): ByteArray {
        val mac = javax.crypto.Mac.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME)
        mac.init(key)
        mac.update(data)
        return mac.doFinal()
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class
    )
    private fun generateECBCipher(algorithmName: String, mode: Int, key: ByteArray): Cipher {
        val cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME)
        val sm4Key: Key = SecretKeySpec(key, SM4)
        cipher.init(mode, sm4Key)
        return cipher
    }

    @SuppressLint("已重新add BouncyCastleProvider.PROVIDER_NAME")
    @Throws(
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        NoSuchPaddingException::class
    )
    private fun generateCBCCipher(
        algorithmName: String,
        mode: Int,
        key: ByteArray,
        iv: ByteArray
    ): Cipher {
        val cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME)
        val sm4Key: Key = SecretKeySpec(key, SM4)
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(mode, sm4Key, ivParameterSpec)
        return cipher
    }
}