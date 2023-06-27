package com.zsmarter.hulk.socket

import androidx.annotation.Keep
import org.json.JSONObject
import java.util.*

/**
 * ZTPSocket用户信息
 */
@Keep
class HulkUserInfo constructor(
    // uid
    var userId: String? = null,
    // alias
    var alias: String? = null,
    // phone
    var phone: String? = null,
    // location
    var location: String? = null,
    // 扩展字段
    var ext: String? = null,
    // 极光id
    var jPushId: String? = null,
    // 绑定三方内容，如邮箱、微信公众号
    var thirdParty: List<ThirdParty>? = null,
) {
    // 厂商类型及厂商token
    internal var manufacturersToken: Pair<String, String>? = null

    constructor(
        userId: String? = null,
        alias: String? = null,
        phone: String? = null,
        location: String?
    ): this(userId, alias, phone, location, null)
}

/**
 * 三方自定义渠道
 */
@Keep
data class ThirdParty(
    val channel: ChannelType,
    val token: String
) {

    internal fun toJson() = JSONObject().apply {
        put("channel", channel)
        put("token", token)
    }
}

/**
 * 渠道类型
 */
@Suppress("EnumEntryName")
@Keep
enum class ChannelType {
    email,
    wechat
}