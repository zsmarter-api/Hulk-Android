@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

android {
    namespace = "com.zsmarter.hulk"
    compileSdk = Config.SDKBasic.compileSdk

    defaultConfig {
        minSdk = Config.SDKBasic.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(Config.Library.appcompat)
//    implementation(Config.Library.kotlinStdlib)
//    implementation(Config.Library.kotlinCoroutines)
    implementation(Config.Library.lifecleProcess)
    testImplementation(Config.Library.junit)
    androidTestImplementation(Config.Library.espressoCore)

    // socket相关三方库
    implementation(Config.Library.bcpkixJdk15on)

    // preference包
    implementation(Config.Library.preferenceKtx)

    // 图片加载框架
    implementation(Config.Library.glide)

    // Json解析工具
    implementation(Config.Library.moshi)
    implementation(Config.Library.moshiKotlin)

    // 工具
    implementation(Config.Library.utilCodex)
}

apply {
    from("${rootDir.absolutePath}/gradles/publish-maven.gradle")
}