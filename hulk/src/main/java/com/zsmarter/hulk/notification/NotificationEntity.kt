package com.zsmarter.hulk.notification

import androidx.annotation.Keep
import org.json.JSONObject
import java.io.Serializable

@Keep
data class NotificationEntity(
    val alert: String? = null,
    val extras: String? = null,
    val intent: IntentObject? = null,
    val title: String? = null,
    val m_id: Long? = null,
    val small_icon_url: String? = null,
    val task_id: Long? = null,
    val vibrate: Int = 0,
    val voice: Int = 0,
) {
    override fun toString() = JSONObject()
        .also {
            it.put("alert", alert)
            it.put("extras", extras)
            it.put("intent", intent.toString())
            it.put("title", title)
            it.put("m_id", m_id)
            it.put("small_icon_url", small_icon_url)
            it.put("task_id", task_id)
            it.put("vibrate", vibrate)
            it.put("voice", voice)
        }
        .toString()
}

@Keep
data class IntentObject(
    val url: String
) {
    override fun toString() = JSONObject()
        .also {
            it.put("url", url)
        }
        .toString()
}