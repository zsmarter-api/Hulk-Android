package com.zsmarter.hulk.huawei_push

import android.app.Application
import androidx.annotation.Keep
import com.huawei.hms.push.HmsMessaging

/**
 * Create By YP On 2023/2/7.
 * 用途：
 */
@Keep
object HuaWeiPush {

    /**
     * @param isAutoInitEnabled 是否开启自动初始化
     */
    fun init(application: Application, isAutoInitEnabled: Boolean = true) {
        HmsMessaging.getInstance(application).isAutoInitEnabled = isAutoInitEnabled
    }
}