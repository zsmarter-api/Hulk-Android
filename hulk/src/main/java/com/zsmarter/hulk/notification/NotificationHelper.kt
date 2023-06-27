package com.zsmarter.hulk.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.zsmarter.hulk.MainActivity
import com.zsmarter.hulk.push.utils.DeviceUtils
import com.zsmarter.hulk.util.BadgeUtils
import com.zsmarter.hulk.util.LoadImageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * @Author: Created by zy on 2019/11/26.
 */

internal object NotificationHelper {


    const val PARAM_DATA = "param_data"
    var notificationIcon: Int? = null
    var onNotificationClickListener: ((String?) -> Unit)? = null
    private var notificationManager: NotificationManager? = null

    fun init(onNotificationClickListener: (String?) -> Unit) {
        this.onNotificationClickListener = onNotificationClickListener
    }

    @Suppress("DeferredResultUnused")
    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(context: Context, data: NotificationEntity?) {
        if (data == null || data.title.isNullOrEmpty() || data.alert.isNullOrEmpty()) {
            // 没有标题和内容时不做处理和展示
            return
        }
        if (notificationManager == null) {
            notificationManager = context.getSystemService(NOTIFICATION_SERVICE)
                    as NotificationManager
        }
        // 创建通知渠道
        val id = data.m_id?.toInt() ?: Random(Int.MAX_VALUE).nextInt()
        val channelId = "${context.packageName}.hulk_push"
        createNotificationChannel(context, channelId)

        // 生成intent参数
        val intent = Intent(context, MainActivity::class.java)
            .also {
                it.putExtra(PARAM_DATA, data.toString())
            }
        intent.setPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 监听通知栏状态
        val cancelIntent = Intent(context, NotificationBroadcastReceiver::class.java).also {
            it.action = "notification_cancelled"
            it.putExtra(NotificationBroadcastReceiver.TYPE, id)
        }
        val cancelPendingIntent =
            PendingIntent.getBroadcast(context, 0, cancelIntent, PendingIntent.FLAG_ONE_SHOT)

        MainScope().launch(Dispatchers.IO) {
            val rightIcon = if (data.small_icon_url.isNullOrEmpty().not()) {
                LoadImageUtil.bgLoadImage(data.small_icon_url!!)
            } else {
                null
            }

            // 生成通知栏消息展示
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(notificationIcon ?: DeviceUtils.getApplication().applicationInfo.icon)
                .setContentTitle(data.title)
                .setContentText(data.alert)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(cancelPendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(0)
                .setShowWhen(true).apply {
                    rightIcon?.let {
                        setLargeIcon(it)
                    }
                    when (DeviceUtils.getManufacturer()) {
                        DeviceUtils.HUAWEI -> BadgeUtils.changeHWBadgeNumb(context)
                        DeviceUtils.VIVO -> BadgeUtils.setVIVOBadgeNum(context)
                        else -> {
                            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                            setNumber(1)
                        }
                    }
                }
                .setStyle(
                    NotificationCompat.BigTextStyle().setBigContentTitle(data.title)
                        .bigText(data.alert)
                )
                .build()
            val unitList = mutableListOf(
                {
                    notificationManager?.notify(id, notification)
                }
            )
            when {
                data.vibrate == 1 && data.voice == 1 -> {
                    unitList.add { playNotificationVibrate(context) }
                    unitList.add { playNotificationRing(context) }
                }
                data.vibrate == 1 -> {
                    unitList.add { playNotificationVibrate(context) }
                }
                data.voice == 1 -> {
                    unitList.add { playNotificationRing(context) }
                }
            }
            unitList.forEach {
                async { it.invoke() }
            }
        }
    }

    /**
     * 取消通知栏消息
     */
    fun cancelNotify(taskId: Int) {
        notificationManager?.cancel(taskId)
    }

    // 生成通知渠道
    private fun createNotificationChannel(
        context: Context,
        channelId: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知渠道
            val channel = NotificationChannel(
                channelId,
                "${context.applicationInfo.loadLabel(context.packageManager)}推送通道",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setBypassDnd(true) // 跳过面打扰模式
            channel.setShowBadge(true)
            channel.enableVibration(false)
            channel.setSound(null, null)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * 播放通知声音
     */
    private fun playNotificationRing(context: Context) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        RingtoneManager.getRingtone(context, uri).play()
    }

    /**
     * 手机震动一下
     */
    @Suppress("DEPRECATION")
    private fun playNotificationVibrate(context: Context) {
        val vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, 20))
        } else {
            vibrator.vibrate(300)
        }
    }
}