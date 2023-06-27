package com.zsmarter.hulk.meizu_push

import android.content.Context
import androidx.annotation.Keep
import com.meizu.cloud.pushsdk.MzPushMessageReceiver
import com.meizu.cloud.pushsdk.handler.MzPushMessage
import com.meizu.cloud.pushsdk.platform.message.*

/**
 * Create By YP On 2022/8/8.
 * 用途：魅族消息接收广播
 */
@Keep
class MeiZuReceiver : MzPushMessageReceiver() {

    /**
     * 调用订阅后，会在此方法中回调结果
     */
    override fun onRegisterStatus(p0: Context?, p1: RegisterStatus?) {
        p1?.pushId?.let {
            sendToken(it)
        }
    }

    /**
     * 请取消订阅后，会在此方法回调结果
     */
    override fun onUnRegisterStatus(p0: Context?, p1: UnRegisterStatus?) {

    }

    /**
     * 调用开关转换或检查开关状态方法后，会在此方法回调开关状态
     */
    override fun onPushStatus(p0: Context?, p1: PushSwitchStatus?) {

    }

    /**
     * 检查开关状态方法：PushManager.checkPush(context, appId, appKey, pushId)
     */
    override fun onSubTagsStatus(p0: Context?, p1: SubTagsStatus?) {

    }

    /**
     * 调用标签订阅、取消标签订阅、取消所有标签订阅和获取标签列表方法后，会在此方法回调标签相关信息
     */
    override fun onSubAliasStatus(p0: Context?, p1: SubAliasStatus?) {

    }

    /**
     * 当用户点击通知栏消息后会在此方法回调
     */
    override fun onNotificationClicked(p0: Context?, p1: MzPushMessage?) {
        super.onNotificationClicked(p0, p1)
        sendClick(p1?.content)
    }

    /**
     * 当推送的通知栏消息展示后且应用进程存在时会在此方法回调
     */
    override fun onNotificationArrived(p0: Context?, p1: MzPushMessage?) {
        super.onNotificationArrived(p0, p1)
    }

}