# Hulk Android SDK接入文档

## Hulk目录

[TOC]



## Hulk简介

> **Hulk(金融推送服务)，依赖于`自建ZTP长连接通道`和`各厂商通道sdk`，主要功能是支持高性能消息及通知推送。包含自建ZTP长连接通道以及vivo、oppo、小米、华为、魅族、FCM厂商系统级推送通道。**

## Hulk快速体验

> 该库仅支持AndroidX，请在项目根目录`gradle.properties`中添加如下内容

```properties
android.useAndroidX=true
android.enableJetifier=true
```

### 集成

> 在项目app下的`build.gradle`中增加如下内容

```groovy
dependencies {
	implementation("io.github.zsmarter-api:hulk:2.0.7")
}
```

### 混淆

> 如项目需要混淆，请在app下的`proguard-rules.pro`中添加以下内容

```properties
-keep class org.bouncycastle.** { *; }
```

### 初始化

> 首先需要在Hulk后管平台申请当前app包名的应用，并拿到以下信息

<img src="https://note.youdao.com/yws/api/personal/file/WEB31822a164a6ded064eac815b0c6c9a18?method=download&shareKey=b663eae116d506e73b458325d652581b" alt="image-20230213153907178" style="zoom:70%;" />

> 初始化建议在隐私政策后进行使用，使用kotlin代码进行接入体验更佳！

- Kotlin版本

```kotlin
Hulk.init(
	context = application,
    configuration = Configuration(
    	hulkHost = "xxxx", // 由Golang端给出具体的ip地址
        hulkPort = xxxx, // 由Golang端给出具体的端口号：Int
        hulkDeviceInfo = HulkDeviceInfo(
        	appKey = "xxxx",// 在Hulk后管中申请的应用里配置：App Id
            appSecret = "xxxx",// 在Hulk后管中申请的应用里配置：App Secret
            appVersion = "1.0.0.1"// 应用版本号，用于Hulk后管版本信息维度的统计数据
        )
    )
)
// Configuration属性中有日志开关字段：printEnable(Boolean)，默认是关闭状态，需要自行启动。
```

- Java版本

```Java
Hulk.INSTANCE.init(
                getApplication(),
                new Configuration(
                        "xxxx", // 由Golang端给出具体的ip地址
                        xxxx, // 由Golang端给出具体的端口号：int
                        new HulkDeviceInfo(
                                "xxxx", // 在Hulk后管中申请的应用里配置：App Id
                                "xxxx", // 在Hulk后管中申请的应用里配置：App Secret
                                "1.0.0.1" // 应用版本号，用于Hulk后管版本信息维度的统计数据
                        )
                ),
                null, // 消息监听事件回调，后面高阶用法会讲
        );
// Configuration属性中有日志开关字段：printEnable(Boolean)，默认是关闭状态，需要自行启动。
```

- 自查初始化结果：

> 在日志中可以查看到初始化成功的tid值，该值可以用于Hulk后管平台指定设备推送。如下：

![image-20230213155836095](https://note.youdao.com/yws/api/personal/file/WEB0ed5cafefd82fee567a7f1b8fbb74c02?method=download&shareKey=7269ec455e637297de5ce8172b6277ad)

## 高阶用法详解<span id="info"></span>

- kotlin版本

```kotlin
// 初始化中的更多数据设定
// 配置参数：
configuration = Configuration(
    hulkHost = "xxxx", // 由Golang端给出具体的ip地址
    hulkPort = xxxx, // 由Golang端给出具体的端口号：Int
    hulkDeviceInfo = HulkDeviceInfo(
        appKey = "xxxx",// 在Hulk后管中申请的应用里配置：App Id
        appSecret = "xxxx",// 在Hulk后管中申请的应用里配置：App Secret
        appVersion = "1.0.0.1",// 应用版本号，用于Hulk后管版本信息维度的统计数据
        hulkIsSSL = true, // 是否有https的SSL证书：如果该值设置不对，是会初始化失败的（询问后台是否有）
        isKeepAlive = true, // 是否持续保持链接状态：true=不管应用是否在前台还是后台都保持链接，false=只在前台时保持链接，默认true
        printEnable = false, // 是否打印日志：默认false
        hulkHeartbeat = 55 * 1000L, // 心跳间隔时长单位ms（即检查链接状态）
        notificationIcon: Int? = null, // 通知栏显示自定义icon
    )
)
    
Hulk.init(
	context = application,
    configuration = configuration,
    hulkMsgListener = object : HulkMsgListener {
        override fun onToken(tid: String?) {
            // 初始化回调，失败时为null
        }

        override fun onNotificationOpen(isOpen: Boolean) {
            // 通知栏是否开启，触发频率以心跳间隔时间为准
        }

        override fun onTranceMsg(str: String) {
            // 接收到的透传消息
        }

        override fun onMessage(data: NotificationEntity) {
            // 接收到的通知栏消息
        }

        override fun onError(code: Int, msg: String) {
            // 推送初始化错误
        }

        override fun onWithdrawn(msgId: Long) {
            // 消息撤回回调
        }
    }
)

// 厂商初始化回调：应用三方厂商包时，初始化成功的回调
Hulk.manufacturersCallback = object : ManufacturersCallback {
    override fun initManufacturers(type: String, token: String) {
        // 目前支持的type类型：vivo、oppo、mi、hw、meizu、fcm
    }
}

// 通知栏消息被点击后的回调
Hulk.notificationClick = object : NotificationClick {
    override fun click(msgContent: String) {
        // 自定义跳转Activity等等操作
    }
}

// 判断Hulk是否是链接状态
Hulk.isConnected()

// 断开Hulk链接
Hulk.disconnect()

// 更新用户信息：字段不为null时是覆盖，字段为null是跳过设置
Hulk.updateUserInfo(
	HulkUserInfo(
    	userId: String? = null, // 在后管可以通过该设定的值进行指定推送
        alias: String? = null, // 在后管可以通过该设定的值进行指定推送
        phone: String? = null, // 在后管可以通过该设定的值进行指定推送
        location: String? = null, // 地理位置：地理位置区域号码，可以定位取出来，如：四川省成都市=130300
        ext: String? = null, // 自定义扩展字段
        jPushId: String? = null, // 极光推送id
        thirdParty: List<ThirdParty>? = listOf(), // 三方渠道绑定：channel目前仅支持：email、wechat
    )
)
```

- Java版本

```java
Configuration configuration = new Configuration(
    "xxxx", // 由Golang端给出具体的ip地址
    xxxx, // 由Golang端给出具体的端口号：Int
    new HulkDeviceInfo(
        "xxxx",// 在Hulk后管中申请的应用里配置：App Id
        "xxxx",// 在Hulk后管中申请的应用里配置：App Secret
        "1.0.0.1",// 应用版本号，用于Hulk后管版本信息维度的统计数据
        true, // 是否有https的SSL证书：如果该值设置不对，是会初始化失败的（询问后台是否有）
        true, // 是否持续保持链接状态：true=不管应用是否在前台还是后台都保持链接，false=只在前台时保持链接，默认true
        false, // 是否打印日志：默认false
        55 * 1000L, // 心跳间隔时长单位ms（即检查链接状态）
        null, // 通知栏显示自定义icon
    )
)
Hulk.INSTANCE.init(
    getApplication(),
    configuration,
    new HulkMsgListener() {
        @Override
        public void onToken(@Nullable String tid) {
            // 初始化回调，失败时为null
        }

        @Override
        public void onMessage(@NonNull NotificationEntity data) {
            // 接收到的通知栏消息
        }

        @Override
        public void onTranceMsg(@NonNull String str) {
            // 接收到的透传消息
        }

        @Override
        public void onError(int code, @NonNull String msg) {
            // 推送初始化错误
        }

        @Override
        public void onWithdrawn(long msgId) {
            // 消息撤回回调
        }

        @Override
        public void onNotificationOpen(boolean isOpen) {
            // 通知栏是否开启，触发频率以心跳间隔时间为准
        }
    }
);

// 厂商初始化回调：应用三方厂商包时，初始化成功的回调
Hulk.INSTANCE.setManufacturersCallback(new ManufacturersCallback() {
    @Override
    public void initManufacturers(@NonNull String type, @NonNull String token) {
		// 目前支持的type类型：vivo、oppo、mi、hw、meizu、fcm
    }
});

// 通知栏消息被点击后的回调
Hulk.INSTANCE.setNotificationClick(new NotificationClick() {
    @Override
    public void click(@NonNull String msgContent) {
		// 自定义跳转Activity等等操作
    }
});

// 判断Hulk是否是链接状态
Hulk.INSTANCE.isConnected();

// 断开Hulk链接
Hulk.INSTANCE.disconnect();

// 更新用户信息：字段不为null时是覆盖，字段为null是跳过设置
Hulk.updateUserInfo(
	new HulkUserInfo(
    	null, // 在后管可以通过该设定的值进行指定推送
        null, // 在后管可以通过该设定的值进行指定推送
        null, // 在后管可以通过该设定的值进行指定推送
        null, // 地理位置：地理位置区域号码，可以定位取出来，如：四川省成都市=1303004
        null, // 自定义扩展字段
        null, // 极光推送id
        null, // 三方渠道绑定：channel目前仅支持：email、wechat
    )
)
```

## 第三方厂商集成

> 首先在后管平台的渠道配置中需要去设定好各个厂商渠道开通的内容，然后在App内进行集成和初始化，并在<a href="#info">ManufacturersCallback</a>中回调成功后，就可以在Hulk后管平台进行第三方厂商推送了。

![image-20230213172018339](https://note.youdao.com/yws/api/personal/file/WEBe1ca98cb60eb27d67e39f3741c7f4d49?method=download&shareKey=29cd988b2f75d65862d885b724b494ed)

### 小米

> [小米开放平台 (mi.com)](https://dev.mi.com/platform)

- 集成

> 在app下的`build.gradle`中增加以下内容

```groovy
dependencies {
	implementation("io.github.zsmarter-api:hulk-xiaomi:1.0.4")
}
```

- 初始化

```kotlin
// kotlin
XiaomiPush.init(application, "xxxx", "xxxx")
// java
XiaomiPush.INSTANCE.init(getApplication(), "xxxx", "xxxx");
// 参数分别对应：Application对象、小米AppId、小米AppKey
```

### OPPO

> [OPPO 开放平台-OPPO开发者服务中心 (oppomobile.com)](https://open.oppomobile.com/)

- 集成

```groovy
dependencies {
	implementation("io.github.zsmarter-api:hulk-oppo:1.0.6")
}
```

- 初始化

```kotlin
// kotlin
OPPOPush.init(application, "xxxx", "xxxx")
// java
OPPOPush.INSTANCE.init(getApplication(), "xxxx", "xxxx");
// 参数分别对应：Application对象、oppo的AppKey、oppo的AppSecret
```

### VIVO

> [vivo开放平台](https://dev.vivo.com.cn/)

- 集成

```groovy
dependencies {
	implementation("io.github.zsmarter-api:hulk-vivo:1.0.3")
}
```

- 配置

> 需要在`AndroidManifest.xml`文件中application节点下增加以下内容

```xml
<!--Vivo Push开放平台中应用的appid 和api key-->
        <meta-data
            android:name="com.vivo.push.api_key"
            android:value="xxxxx"/>

        <meta-data
            android:name="com.vivo.push.app_id"
            android:value="xxxx"/>
```

- 初始化

```kotlin
// kotlin
VivoPush.init(application)
// java
VivoPush.INSTANCE.init(getApplication());
// 参数分别对应：Application对象
```

### 华为

> [华为开发者联盟-智能终端能力开放,共建开发者生态 (huawei.com)](https://developer.huawei.com/consumer/cn/)

- 集成

> 在项目根目录下的`build.gradle`文件中添加以下内容

```groovy
buildscript {
    repositories {
        maven {
            url = uri("https://developer.huawei.com/repo/")
        }
    }
    dependencies {
        classpath "com.huawei.agconnect:agcp:1.5.2.300"
    }
}
```

> 在app目录下的`build.gradle`文件中添加以下内容

```groovy
apply plugin: 'com.huawei.agconnect'

dependencies {
    implementation("io.github.zsmarter-api:hulk-huawei:1.0.3")
}
```

- 配置

> 需要在华为平台的应用配置中下载`agconnect-services.json`文件放到app目录下

- 初始化

```kotlin
// kotlin
HuaWeiPush.init(application, true)
// java
HuaWeiPush.INSTANCE.init(getApplication(), true);
// 参数分别对应：Application对象、是否自动初始化（默认true）
```

### 魅族

> [魅族开放平台 (flyme.cn)](http://open.flyme.cn/)

- 集成

```groovy
dependencies {
	implementation("io.github.zsmarter-api:hulk-meizu:1.0.3")
}
```

- 初始化

```kotlin
// kotlin
MeiZuPush.init(application, "xxxx", "xxxx")
// java
MeiZuPush.INSTANCE.init(getApplication(), "xxxx", "xxxx");
// 参数分别对应：Application对象、魅族的AppId、魅族的AppKey
```

### FCM（海外厂商通道）

> 国内手机集成FCM时，需要手机安装Google服务，并使用境外网络环境。且发送消息到接收消息的时间在0-20分钟左右。
>
> [Firebase console (google.com)](https://console.firebase.google.com/)

- 集成

> 在项目根目录下的`build.gradle`文件中添加以下内容

```
buildscript {
    dependencies {
        classpath "com.google.gms:google-services:4.3.13"
    }
}
```

> 在app目录下的`build.gradle`文件中添加以下内容

```groovy
apply plugin: 'com.google.gms.google-services'

dependencies {
    implementation("io.github.zsmarter-api:hulk-fcm:1.0.2")
}
```

- 配置

> 需要在Firebase平台的应用配置中下载`google-services.json`文件放到app目录下

- 初始化

```kotlin
// kotlin
FCMPush.init(true)
// java
FCMPush.INSTANCE.init(true);
// 参数：是否自动初始化（默认true）
```

## 隐私政策

开发者请务必在《隐私政策》中向用户告知应用使用了Hulk推送 SDK，参考条款如下：
SDK 名称：Hulk推送 SDK
服务类型：消息推送服务
SDK 收集个人信息类型：

1. 设备信息（Android ID和设备硬件信息）：用于识别唯一用户，保证消息推送的精准送达；优化推送通道资源，我们会根据设备上不同APP的活跃情况，整合消息推送的通道资源，为开发者提高消息送达率；为开发者提供智能标签以及展示业务统计信息的服务；
2. 手机号码（可选）：用于向目标用户推送短信服务，保证消息推送的精准送达；
3. 地理位置（可选）：用于根据用户的所在区域进行定向的范围推送，以及便于统计用户遍布各个地区的情况。
