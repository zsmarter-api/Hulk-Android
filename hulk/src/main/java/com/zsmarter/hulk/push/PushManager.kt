package com.zsmarter.hulk.push

import android.content.Intent
import com.zsmarter.hulk.util.MoshiHelper.toJsonString
import com.zsmarter.hulk.util.printLogE
import com.zsmarter.hulk.util.printLogI
import org.json.JSONObject

/**
 * @author yue
 */
object PushManager {

    /**
     * 从通知中获取extras数据
     */
    fun getNotificationExtras(intent: Intent): String {
        printLogI("PushManager-getNotificationExtras:${intent.extras.toJsonString()}")
        val bundle = intent.extras
        if (bundle == null || bundle.isEmpty) {
            return ""
        }
        val jsonObject = JSONObject()
        bundle.keySet().forEach { key ->
            try {
                jsonObject.put(key, bundle.getString(key))
            } catch (e: Exception) {
                printLogE("PushManager=${e.message}")
            }
        }
        printLogI("PushManager-data:$jsonObject")
        return jsonObject.toString()

    }
}