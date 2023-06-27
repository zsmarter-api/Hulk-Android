@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.huawei.agconnect")
    id("com.google.gms.google-services")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "cn.zsmarter.hulk"
        minSdk = 21
        targetSdk = 28
        versionCode = 19
        versionName = "2.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    signingConfigs {
        create("apkSignConfig") {
            storeFile = file("hulk.jks")
            storePassword = "hulk123"
            keyAlias = "hulk"
            keyPassword = "hulk123"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("apkSignConfig")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("apkSignConfig")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions("environment")
    productFlavors {
        create("Hulk") {
            applicationId = "cn.zsmarter.hulk"
        }
        // 手机银行-生产包名
        create("TFBank") {
            applicationId = "com.ionicframework.cgbank122507"
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName =
                "HulkDemo-v$versionName-${Config.getCurrentTime()}-${buildType.name}.apk"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(Config.Library.appcompat)
    implementation(Config.Library.material)
    implementation(Config.Library.constraintlayout)
    implementation(Config.Library.multidex)

    implementation(project(":hulk"))
//    implementation(project(":fcm-push"))
//    implementation(project(":oppo-push"))
//    implementation(project(":huawei-push"))
//    implementation(project(":xiaomi-push"))
//    implementation(project(":vivo-push"))
//    implementation(project(":meizu-push"))
    implementation("io.github.zsmarter-api:hulk-oppo:1.0.6")
    implementation("io.github.zsmarter-api:hulk-vivo:1.0.3")
    implementation("io.github.zsmarter-api:hulk-huawei:1.0.3")
    implementation("io.github.zsmarter-api:hulk-xiaomi:1.0.5")
    implementation("io.github.zsmarter-api:hulk-meizu:1.0.3")
//    implementation("io.github.zsmarter-api:hulk:2.0.9")
//    implementation("io.github.zsmarter-api:hulk-fcm:1.0.4")

    testImplementation(Config.Library.junit)
    androidTestImplementation(Config.Library.androidxJunit)
    androidTestImplementation(Config.Library.espressoCore)

    implementation(Config.Library.coreKtx)

    implementation(Config.Library.utilCodex)

    implementation(Config.Library.adapterHelper)

    implementation(Config.Library.kotlinReflet)
//    implementation(Config.Library.kotlinCoroutines)
//    implementation(Config.Library.kotlinCoroutinesCore)
    implementation(Config.Library.lifecleRuntimeKtx)
    implementation(Config.Library.lifecleViewmodelKtx)


    implementation(Config.Library.immersionbar)
    implementation(Config.Library.immersionbarKtx)
    implementation(Config.Library.dialogX)
    implementation(Config.Library.liveEventBusX)
    // shapeView框架：可以直接在xml控件中写入shapeUI
    implementation(Config.Library.shapeView)

    // json解析
    implementation(Config.Library.moshi)
    implementation(Config.Library.moshiKotlin)
    // 指示器
    implementation(Config.Library.magicIndicator)

}