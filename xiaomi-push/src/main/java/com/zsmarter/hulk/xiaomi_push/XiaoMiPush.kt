package com.zsmarter.hulk.xiaomi_push

import android.app.Application
import androidx.annotation.Keep
import com.xiaomi.mipush.sdk.MiPushClient

/**
 * Create By YP On 2023/2/7.
 * 用途：
 */
@Keep
object XiaoMiPush {

    fun init(application: Application, appId: String, appKey: String) {
        // 由于小米制造商兼容的机型过多，容易造成厂商初始化到小米，所以做一个区分
        // 例如一加手机就会初始化成功小米厂商通道
        if (android.os.Build.MANUFACTURER != "Xiaomi") {
            return
        }
        MiPushClient.registerPush(application, appId, appKey)
    }
}