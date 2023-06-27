package com.zsmarter.hulk.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import androidx.preference.PreferenceManager
import com.zsmarter.hulk.push.utils.DeviceUtils
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and

object UniqueIdUtils {

    private const val deviceKey = "device_id"
    private var editor: SharedPreferences.Editor? = null
    private var share: SharedPreferences? = null

    fun getDeviceId(): String {
        val context = DeviceUtils.getApplication().applicationContext
        if (share == null) {
            share = PreferenceManager.getDefaultSharedPreferences(context)
            editor = share!!.edit()
        } else if (editor == null) {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        }
        var deviceId = share?.getString(deviceKey, "") ?: ""
        if (deviceId.isEmpty()) {
            deviceId = getDeviceID(context)
            share?.run {
                editor?.putString(deviceKey, deviceId)
                editor?.apply()
                editor?.commit()
            }
        }
        return deviceId
    }

    /**
     * 获得设备硬件标识
     *
     * @param context 上下文
     * @return 设备硬件标识
     */
    private fun getDeviceID(context: Context): String {
        val sbDeviceId = StringBuilder()
        // 获得AndroidId
        val androidId = getAndroidId(context)
        // 获得设备序列号
        val serial = getSERIAL()
        // 获得硬件uuid（根据硬件相关属性，生成uuid）
        val uuid = getDeviceUUID().replace("-", "")

        if (androidId.isNullOrEmpty().not()) {
            sbDeviceId.append(androidId)
            sbDeviceId.append("|")
        }
        if (serial.isNullOrEmpty().not()) {
            sbDeviceId.append(serial)
            sbDeviceId.append("|")
        }
        if (uuid.isNotEmpty()) {
            sbDeviceId.append(uuid)
        }
        // 生成SHA1，统一DeviceId长度
        if (sbDeviceId.isNotEmpty()) {
            val hash = getHashByString(sbDeviceId.toString())
            val sha1 = bytesToHex(hash)
            if (sha1.isNotEmpty()) {
                //返回最终的DeviceId
                return sha1
            }
        }
        return UUID.randomUUID().toString().replace("-", "").uppercase(Locale.CHINA)
    }

    /**
     * 获得设备的AndroidId
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    @SuppressLint("HardwareIds")
    private fun getAndroidId(context: Context) = try {
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    } catch (ex: Exception) {
        ""
    }

    /**
     * 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取
     *
     * @return 设备序列号
     */
    @Suppress("DEPRECATION")
    @SuppressLint("HardwareIds")
    private fun getSERIAL() =
        try {
            Build.SERIAL
        } catch (ex: Exception) {
            ""
        }

    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     *
     * @return 设备硬件uuid
     */
    @Suppress("DEPRECATION")
    @SuppressLint("HardwareIds")
    private fun getDeviceUUID(): String {

        try {
            val dev = "2022719" +
                    Build.BOARD.length % 10 +
                    Build.BRAND.length % 10 +
                    Build.DEVICE.length % 10 +
                    Build.HARDWARE.length % 10 +
                    Build.ID.length % 10 +
                    Build.MODEL.length % 10 +
                    Build.PRODUCT.length % 10 +
                    Build.SERIAL.length % 10
            return UUID(
                dev.hashCode().toLong(),
                Build.SERIAL.hashCode().toLong()
            ).toString()
        } catch (ex: Exception) {
            return ""
        }
    }

    /**
     * 取SHA1
     * @param data 数据
     * @return 对应的hash值
     */
    private fun getHashByString(data: String) = try {
        val messageDigest = MessageDigest.getInstance("SHA1")
        messageDigest.reset()
        messageDigest.update(data.toByteArray(Charsets.UTF_8))
        messageDigest.digest()
    } catch (e: Exception) {
        "".toByteArray()
    }

    /**
     * 转16进制字符串并限制40位
     * @param data 数据
     * @return 16进制字符串
     */
    private fun bytesToHex(data: ByteArray): String {
        val sb = StringBuilder()
        for (i in data.indices) {
            val stmp = Integer.toHexString((data[i] and 0xFF.toByte()).toInt())
            when {
                stmp.length > 2 -> sb.append(stmp.substring(stmp.length - 2))
                stmp.length == 2 -> sb.append(stmp)
                stmp.length == 1 -> sb.append("0$stmp")
                else -> sb.append("00")
            }
        }
        return sb.toString().uppercase(Locale.CHINA)
    }
}