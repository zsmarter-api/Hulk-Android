package com.zsmarter.hulk.socket.ext

import android.os.Build
import com.zsmarter.hulk.push.utils.DeviceUtils
import com.zsmarter.hulk.socket.HulkDeviceInfo
import com.zsmarter.hulk.socket.HulkUserInfo
import com.zsmarter.hulk.socket.ZTPData
import com.zsmarter.hulk.socket.ZTPData.Companion.getZTPDataBytes
import com.zsmarter.hulk.socket.ZTPData.Companion.readFromInputStream
import com.zsmarter.hulk.socket.crypto.*
import com.zsmarter.hulk.socket.util.toHexString
import com.zsmarter.hulk.util.UniqueIdUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

/**************************************Int与ByteArray相互转换***************************************/

/**
 * Int转为指定长度的ByteArray
 * @param length 指定ByteArray的长度
 * @return 转化后的ByteArray
 */
internal fun Int.toByteArray(length: Int): ByteArray {
    val bytes = ByteArray(length)
    for (i in 0 until length.coerceAtMost(Int.SIZE_BYTES)) {
        bytes[i] = (this shr (i * 8) and 0xFF).toByte()
    }
    return bytes
}

/**
 * ByteArray转为Int
 * @param offset 偏移量
 * @param count 长度
 * @return 转化后的Int值
 */
internal fun ByteArray.toInt(offset: Int, count: Int): Int {
    var intValue = 0
    for (i in offset until offset + count) {
        intValue += (this[i].toInt() and 0xFF) shl 8 * (i - offset)
    }
    return intValue
}

/****************************************ZTPData字节流读写******************************************/

/**
 * 从InputStream中读取协议数据
 */
internal fun InputStream?.readProtocolData(): ZTPData? {
    return if (this == null) null else readFromInputStream(this)
}

/**
 * 协议数据转换为ByteArray字节流
 */
internal fun ZTPData.getBytes(): ByteArray {
    return getZTPDataBytes(this)
}

/**************************************ZTPData加解密相关********************************************/


/**
 * 将ZTPData转换为加密的ZTPData
 * @param key 加密key
 * @param iv 加密偏移量
 * @return 加密后的ZTPData
 */
internal fun ZTPData.toEncrypted(key: ByteArray, iv: ByteArray): ZTPData {
    return this.copy().also {
        it.data = encrypt(
            key = key,
            iv = iv,
            src = it.data,
            crypto = Crypto.from(it.encrypt)
        )
    }
}

/**
 * 将ZTPData转换为解密的ZTPData
 * @param key 解密key
 * @param iv 解密偏移量
 * @return 解密后的ZTPData
 */
internal fun ZTPData.toDecrypted(key: ByteArray, iv: ByteArray): ZTPData {
    return this.copy().also {
        it.data = decrypt(
            key = key,
            iv = iv,
            src = it.data,
            crypto = Crypto.from(it.encrypt)
        )
    }
}

/**************************************ZTPDeviceInfo相关********************************************/
internal fun HulkDeviceInfo.toJsonStringV1() = JSONObject()
    .also {
        it.put("device_id", UniqueIdUtils.getDeviceId())
        it.put("bundle", DeviceUtils.getAppPackageName())
        it.put("appkey", appKey)
        it.put("platform", "android")// 平台
        it.put("brand", Build.BRAND)// 品牌
        it.put("model", Build.MODEL)// 机型
        it.put("sys_ver", Build.VERSION.RELEASE)// android系统版本号
        it.put("version", appVersion)// app应用版本（用户自定义）
        it.put("jpush_id", jPushId ?: "")
        it.put("session", MD5.md5("$appSecret${UniqueIdUtils.getDeviceId()}".toByteArray()).toHexString())
    }
    .toString()

internal fun HulkDeviceInfo.toJsonStringV3() = JSONObject()
    .also {
        it.put("device_id", UniqueIdUtils.getDeviceId())
        it.put("bundle", DeviceUtils.getAppPackageName())
        it.put("appkey", appKey)
        it.put("platform", "android")// 平台
        it.put("brand", Build.BRAND)// 品牌
        it.put("model", Build.MODEL)// 机型
        it.put("manufacturer", Build.MANUFACTURER)// 制造商
        it.put("sys_ver", Build.VERSION.RELEASE)// android系统版本号
        it.put("version", appVersion)// app应用版本（用户自定义）
        it.put("jpush_id", jPushId ?: "")
        val ts = System.currentTimeMillis() / 1000
        it.put("ts", ts)
        val session = SM3.hash("$appSecret${UniqueIdUtils.getDeviceId()}$ts".toByteArray()).toHexString()
        it.put("session", session)
    }
    .toString()

/**************************************ZTPUserInfo相关********************************************/

internal fun HulkUserInfo.toJsonString() = JSONObject()
    .also {
        it.put("device_id", UniqueIdUtils.getDeviceId())
        if (userId != null) {
            it.put("uid", userId)
        }
        if (phone != null) {
            it.put("phone", phone)
        }
        if (alias != null) {
            it.put("alias", alias)
        }
        if (location != null) {
            it.put("location", location)
        }
        if (thirdParty?.isNotEmpty() == true) {
            val array = JSONArray()
            thirdParty!!.forEach { third -> array.put(third.toJson()) }
            it.put("third_party", array)
        }
        if (ext != null) {
            it.put("ext", ext)
        }
        if (jPushId != null) {
            it.put("jpush_id", jPushId ?: "")
        }
        manufacturersToken?.let { token ->
            it.put(token.first, token.second)
        }
    }
    .toString()