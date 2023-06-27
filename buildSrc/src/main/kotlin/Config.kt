import org.gradle.api.plugins.PluginContainer
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create By YP On 2023/1/3.
 * 用途：build.gradle版本号和基础信息配置
 */
object Config {

    object SDKBasic {
        const val compileSdk = 28
        const val minSdk = 21
    }

    object Library {
        // 基础库
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val multidex = "androidx.multidex:multidex:2.0.1"
        const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:1.6.20"
        const val kotlinReflet = "org.jetbrains.kotlin:kotlin-reflect:1.6.20"
        const val annotation = "androidx.annotation:annotation:1.3.0"
        const val coreKtx = "androidx.core:core-ktx:1.6.0"
        const val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2"
        const val kotlinCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2"
        const val lifecleProcess = "androidx.lifecycle:lifecycle-process:2.3.0"
        const val lifecleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.4.1"
        const val lifecleViewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1"
        const val material = "com.google.android.material:material:1.6.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        const val junit = "junit:junit:4.13.2"
        const val androidxJunit = "androidx.test.ext:junit:1.1.3"
        const val espressoCore = "androidx.test.espresso:espresso-core:3.4.0"
        const val preferenceKtx = "androidx.preference:preference-ktx:1.1.1"

        // 推送厂商
        const val huawei = "com.huawei.hms:push:5.3.0.304"
        const val oppo = "com.umeng.umsdk:oppo-push:3.0.0"
        const val xiaomi = "com.umeng.umsdk:xiaomi-push:5.0.8"
        const val vivo = "com.umeng.umsdk:vivo-push:3.0.0.4"
        const val meizu = "com.meizu.flyme.internet:push-internal:4.1.4"

        // fcm海外厂商推送
        const val firebaseBom = "com.google.firebase:firebase-bom:31.1.1"
        const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
        const val firebaseMessaging = "com.google.firebase:firebase-messaging-ktx"

        // socket 加解密
        const val bcpkixJdk15on = "org.bouncycastle:bcpkix-jdk15on:1.65"

        // 图片加载
        const val glide = "com.github.bumptech.glide:glide:4.13.2"

        // json解析工具
        const val moshi = "com.squareup.moshi:moshi:1.11.0"
        const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:1.11.0"

        // Android工具库
        const val utilCodex = "com.blankj:utilcodex:1.31.0"

        // 沉浸式状态栏
        const val immersionbar = "com.geyifeng.immersionbar:immersionbar:3.2.2"
        const val immersionbarKtx = "com.geyifeng.immersionbar:immersionbar-ktx:3.2.2"

        // 弹窗
        const val dialogX = "com.github.kongzue.DialogX:DialogX:0.0.45.beta12"

        // 订阅，通知
        const val liveEventBusX = "io.github.jeremyliao:live-event-bus-x:1.8.0"

        // shape控件
        const val shapeView = "com.github.getActivity:ShapeView:6.2"

        // 指示条
        const val magicIndicator = "com.github.hackware1993:MagicIndicator:1.7.0"

        // RecyclerView适配器
        const val adapterHelper = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4"
    }

    /**
     * 获取当前时间
     */
    fun getCurrentTime() = SimpleDateFormat("yyMMddHH", Locale.CHINA).format(Date().time)
}