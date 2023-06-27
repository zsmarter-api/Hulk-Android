package com.zsmarter.hulk.push.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.lang.reflect.Field;

import androidx.annotation.Keep;
import androidx.core.app.NotificationManagerCompat;

import static com.zsmarter.hulk.util.LogUtil.printLogE;


/**
 * @author yue
 */
@Keep
public final class DeviceUtils {

    public static final String HUAWEI = "HUAWEI";
    public static final String VIVO = "vivo";

    private DeviceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return the manufacturer of the product/hardware.
     * <p>e.g. Xiaomi</p>
     *
     * @return the manufacturer of the product/hardware
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * Return the model of device.
     * <p>e.g. MI2SC</p>
     *
     * @return the model of device
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    public static boolean isOpenNotice(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        return manager.areNotificationsEnabled();
    }

    /**
     * 跳转App通知设置界面
     */
    @Keep
    public static void openNotificationSetting(Context _context) {
        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 26) {
                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, _context.getPackageName());
                localIntent.putExtra(Settings.EXTRA_CHANNEL_ID, _context.getApplicationInfo().uid);
                //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            } else if (Build.VERSION.SDK_INT>=21) {
                localIntent.putExtra("app_package", _context.getPackageName());
                localIntent.putExtra("app_uid", _context.getApplicationInfo().uid);
            }
            _context.startActivity(localIntent);
        }catch (Exception e){
            try {
                // 出现异常则跳转到应用设置界面
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", _context.getPackageName(), null);
                intent.setData(uri);
                _context.startActivity(intent);
            } catch (Exception ex) {
                printLogE("跳转通知栏设置失败");
            }
        }

    }

    public static String getAppPackageName() {
        Application app = getApplicationByReflect();
        if (app == null) {
            throw new NullPointerException("reflect failed.");
        }
        return app.getPackageName();
    }

    public static Application getApplication () {
        return getApplicationByReflect();
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi") Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object thread = getActivityThread();
            if (thread == null) return null;
            Object app = activityThreadClass.getMethod("getApplication").invoke(thread);
            if (app == null) return null;
            return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private static Object getActivityThread() {
        Object activityThread = getActivityThreadInActivityThreadStaticField();
        if (activityThread != null) return activityThread;
        return getActivityThreadInActivityThreadStaticMethod();
    }

    private static Object getActivityThreadInActivityThreadStaticField() {
        try {
            @SuppressLint("PrivateApi") Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            @SuppressLint("DiscouragedPrivateApi") Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            return sCurrentActivityThreadField.get(null);
        } catch (Exception e) {
            printLogE("getActivityThreadInActivityThreadStaticField:" + e.getMessage());
            return null;
        }
    }

    private static Object getActivityThreadInActivityThreadStaticMethod() {
        try {
            @SuppressLint("PrivateApi") Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            return activityThreadClass.getMethod("currentActivityThread").invoke(null);
        } catch (Exception e) {
            printLogE("getActivityThreadInActivityThreadStaticMethod:" + e.getMessage());
            return null;
        }
    }

    public static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }
        return info.activityInfo.name;
    }
}
