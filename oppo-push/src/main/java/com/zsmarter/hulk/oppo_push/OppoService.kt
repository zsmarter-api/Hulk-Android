package com.zsmarter.hulk.oppo_push

import android.content.Context
import androidx.annotation.Keep
import com.heytap.msp.push.mode.DataMessage
import com.heytap.msp.push.service.DataMessageCallbackService

/**
 * Create By YP On 2022/8/8.
 * 用途：
 */
@Keep
class OppoService : DataMessageCallbackService() {

    override fun processMessage(p0: Context?, p1: DataMessage?) {
        super.processMessage(p0, p1)
        val content = p1?.content

        logI("OPPO-processMessage: $content")
    }
}