package com.zsmarter.hulk.socket.crypto

import org.bouncycastle.crypto.digests.SM3Digest
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.*

object SM3 {

    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    /**
     * 计算SM3摘要值
     *
     * @param srcData 原文
     * @return 摘要值，对于SM3算法来说是32字节
     */
    fun hash(srcData: ByteArray): ByteArray {
        val digest = SM3Digest()
        digest.update(srcData, 0, srcData.size)
        val hash = ByteArray(digest.digestSize)
        digest.doFinal(hash, 0)
        return hash
    }

    /**
     * 验证摘要
     *
     * @param srcData 原文
     * @param sm3Hash 摘要值
     * @return 返回true标识验证成功，false标识验证失败
     */
    fun verify(srcData: ByteArray, sm3Hash: ByteArray?): Boolean {
        val newHash = hash(srcData)
        return Arrays.equals(newHash, sm3Hash)
    }
}