package com.zsmarter.hulk.vivo_push

import android.app.Application
import androidx.annotation.Keep
import com.vivo.push.PushClient


/**
 * Create By YP On 2023/2/7.
 * 用途：
 */
@Keep
object VivoPush {

    fun init(application: Application) {
        //初始化push
        //初始化push
        PushClient.getInstance(application).initialize()
        // 打开push开关, 关闭为turnOffPush
        PushClient.getInstance(application).turnOnPush {
            // 开关状态处理， 0代表成功
            val token = PushClient.getInstance(application).regId
            token?.let {
                sendToken(it)
            }
        }
    }
}