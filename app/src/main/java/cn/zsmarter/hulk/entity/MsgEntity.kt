package cn.zsmarter.hulk.entity

import com.blankj.utilcode.util.TimeUtils

/**
 * Author: YP
 * Time: 2022/4/1
 * Describe: 消息体
 */
data class MsgEntity(
    val title: String,
    val content: String? = null,
    val time: String = TimeUtils.getNowString(TimeUtils.getSafeDateFormat("HH:mm")),
    val id: Long? = null
)