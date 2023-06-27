package com.zsmarter.hulk.oppo_push

import android.app.Application
import androidx.annotation.Keep
import com.heytap.msp.push.HeytapPushManager
import com.heytap.msp.push.callback.ICallBackResultService

/**
 * Create By YP On 2023/2/7.
 * 用途：
 */
@Keep
object OPPOPush {

    fun init(application: Application, appKey: String, appSecret: String) {
        kotlin.runCatching {
            HeytapPushManager.init(application, true)
            if (!HeytapPushManager.isSupportPush(application)) {
                return
            }
            HeytapPushManager.register(
                application,
                appKey,
                appSecret,
                object : ICallBackResultService {
                    override fun onRegister(p0: Int, p1: String?) {
                        if (p0 == 0) {
                            p1?.let {
                                sendToken(it)
                            }
                        }
                    }

                    override fun onUnRegister(p0: Int) {
                        logI("OPPO-onUnRegister:$p0")
                    }

                    override fun onSetPushTime(p0: Int, p1: String?) {
                        logI("OPPO-onSetPushTime:code=$p0;s=$p1")
                    }

                    override fun onGetPushStatus(p0: Int, p1: Int) {
                        logI("OPPO-onGetPushStatus:code=$p0;status=$p1")
                    }

                    override fun onGetNotificationStatus(p0: Int, p1: Int) {
                        logI("onGetNotificationStatus:code=$p0;status=$p1")
                    }

                    override fun onError(p0: Int, p1: String?) {
                        logE("OPPO-onError:i=$p0;s=$p1")
                    }

                })
            HeytapPushManager.requestNotificationPermission()
        }
    }
}