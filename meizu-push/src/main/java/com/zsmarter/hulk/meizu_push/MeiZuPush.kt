package com.zsmarter.hulk.meizu_push

import android.app.Application
import androidx.annotation.Keep
import com.meizu.cloud.pushsdk.PushManager

/**
 * Create By YP On 2023/2/7.
 * 用途：
 */
@Keep
object MeiZuPush {

    fun init(application: Application, appId: String, appKey: String) {
        PushManager.register(application, appId, appKey)
    }
}