package com.xiaomi.channel.commonutils.android;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import java.util.List;
import java.util.Map;
import miui.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class AppInfoUtils {
    private static final String ANDROID_PERMISSION_PREF = "android.permission.";
    public static final int PATTERN = 100000;
    public static final String SEPARATE_ITEM = "#";
    private static final String TAG = "AppInfoUtils.";
    private static AppInfoAgent sAppInfoAgent;

    public interface AppInfoAgent {
        Map<String, String> divideAppStatus(Context context, String str);

        boolean isAppRunning(Context context, String str);

        boolean isApplicationForeground(Context context, String str);
    }

    public enum AppNotificationOp {
        UNKNOWN(0),
        ALLOWED(1),
        NOT_ALLOWED(2);

        public static final int MASK = 3;
        private final int value;

        AppNotificationOp(int i2) {
            this.value = i2;
        }

        public static AppNotificationOp findByValue(int i2) {
            if (i2 == 0) {
                return UNKNOWN;
            }
            if (i2 == 1) {
                return ALLOWED;
            }
            if (i2 != 2) {
                return null;
            }
            return NOT_ALLOWED;
        }

        public int getValue() {
            return this.value;
        }
    }

    private AppInfoUtils() {
    }

    private static AppNotificationOp areNotificationsEnabled(Context context, ApplicationInfo applicationInfo) {
        Boolean boolValueOf;
        if (applicationInfo == null) {
            return AppNotificationOp.UNKNOWN;
        }
        try {
            if (applicationInfo.packageName.equals(context.getPackageName())) {
                boolValueOf = Boolean.valueOf(((NotificationManager) context.getSystemService("notification")).areNotificationsEnabled());
            } else {
                Object objCallMethod = JavaCalls.callMethod(context.getSystemService("notification"), "getService", new Object[0]);
                boolValueOf = objCallMethod != null ? (Boolean) JavaCalls.callMethodOrThrow(objCallMethod, "areNotificationsEnabledForPackage", applicationInfo.packageName, Integer.valueOf(applicationInfo.uid)) : null;
            }
            if (boolValueOf != null) {
                return boolValueOf.booleanValue() ? AppNotificationOp.ALLOWED : AppNotificationOp.NOT_ALLOWED;
            }
        } catch (Exception e2) {
            MyLog.w("are notifications enabled error " + e2);
        }
        return AppNotificationOp.UNKNOWN;
    }

    public static boolean checkSelfPermission(Context context, String str) {
        return context.getPackageManager().checkPermission(str, context.getPackageName()) == 0;
    }

    @Deprecated
    public static String convertPermissionString(String[] strArr) {
        boolean z2;
        AppPermissionType[] appPermissionTypeArrValues = AppPermissionType.values();
        byte[] bArr = new byte[(int) Math.ceil(((double) appPermissionTypeArrValues.length) / 8.0d)];
        if (strArr == null) {
            MyLog.v("AppInfoUtils.: no permissions");
            return "";
        }
        int i2 = -1;
        for (String str : strArr) {
            if (!TextUtils.isEmpty(str) && str.startsWith(ANDROID_PERMISSION_PREF)) {
                int i3 = 0;
                while (true) {
                    if (i3 >= appPermissionTypeArrValues.length) {
                        z2 = false;
                        break;
                    }
                    if (TextUtils.equals(ANDROID_PERMISSION_PREF + appPermissionTypeArrValues[i3].name(), str)) {
                        i2 = i3;
                        z2 = true;
                        break;
                    }
                    i3++;
                }
                if (z2 && i2 != -1) {
                    int i4 = i2 / 8;
                    bArr[i4] = (byte) (bArr[i4] | (1 << (7 - (i2 % 8))));
                }
            }
        }
        return new String(Base64.encode(bArr, 0));
    }

    public static Map<String, String> divideAppStatus(Context context, String str) {
        AppInfoAgent appInfoAgent = sAppInfoAgent;
        if (appInfoAgent == null) {
            return null;
        }
        return appInfoAgent.divideAppStatus(context, str);
    }

    public static Drawable getAppIconDrawable(Context context, String str) {
        ApplicationInfo applicationInfo = getApplicationInfo(context, str);
        Drawable drawableLoadIcon = null;
        if (applicationInfo != null) {
            try {
                drawableLoadIcon = applicationInfo.loadIcon(context.getPackageManager());
                if (drawableLoadIcon == null) {
                    drawableLoadIcon = applicationInfo.loadLogo(context.getPackageManager());
                }
            } catch (Exception e2) {
                MyLog.w("get app icon drawable failed, " + e2);
            }
        }
        return drawableLoadIcon != null ? drawableLoadIcon : new ColorDrawable(0);
    }

    public static int getAppIconId(Context context, String str) {
        ApplicationInfo applicationInfo = getApplicationInfo(context, str);
        if (applicationInfo == null) {
            return 0;
        }
        int i2 = applicationInfo.icon;
        return i2 == 0 ? applicationInfo.logo : i2;
    }

    public static String getAppLabel(Context context, String str) {
        ApplicationInfo applicationInfo;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
            return (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null) ? str : packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (PackageManager.NameNotFoundException unused) {
            return str;
        }
    }

    @TargetApi(19)
    public static AppNotificationOp getAppNotificationOp(Context context, String str, boolean z2) {
        if (context == null || TextUtils.isEmpty(str)) {
            return AppNotificationOp.UNKNOWN;
        }
        try {
            ApplicationInfo applicationInfo = str.equals(context.getPackageName()) ? context.getApplicationInfo() : context.getPackageManager().getApplicationInfo(str, 0);
            AppNotificationOp appNotificationOpAreNotificationsEnabled = areNotificationsEnabled(context, applicationInfo);
            AppNotificationOp appNotificationOp = AppNotificationOp.UNKNOWN;
            if (appNotificationOpAreNotificationsEnabled != appNotificationOp) {
                return appNotificationOpAreNotificationsEnabled;
            }
            Integer num = (Integer) JavaCalls.getStaticField((Class<? extends Object>) AppOpsManager.class, "OP_POST_NOTIFICATION");
            if (num == null) {
                return appNotificationOp;
            }
            Integer num2 = (Integer) JavaCalls.callMethod((AppOpsManager) context.getSystemService("appops"), "checkOpNoThrow", num, Integer.valueOf(applicationInfo.uid), str);
            int i2 = (Integer) JavaCalls.getStaticField((Class<? extends Object>) AppOpsManager.class, "MODE_ALLOWED");
            int i3 = (Integer) JavaCalls.getStaticField((Class<? extends Object>) AppOpsManager.class, "MODE_IGNORED");
            MyLog.i(String.format("get app mode %s|%s|%s", num2, i2, i3));
            if (i2 == null) {
                i2 = 0;
            }
            if (i3 == null) {
                i3 = 1;
            }
            if (num2 != null) {
                if (z2) {
                    return !num2.equals(i3) ? AppNotificationOp.ALLOWED : AppNotificationOp.NOT_ALLOWED;
                }
                return num2.equals(i2) ? AppNotificationOp.ALLOWED : AppNotificationOp.NOT_ALLOWED;
            }
            return AppNotificationOp.UNKNOWN;
        } catch (Throwable th) {
            MyLog.w("get app op error " + th);
        }
    }

    @Deprecated
    public static String getAppPermissionBase64Str(Context context, String str) {
        try {
            return convertPermissionString(context.getPackageManager().getPackageInfo(str, 4096).requestedPermissions);
        } catch (PackageManager.NameNotFoundException unused) {
            return "";
        }
    }

    private static ApplicationInfo getApplicationInfo(Context context, String str) {
        if (str.equals(context.getPackageName())) {
            return context.getApplicationInfo();
        }
        try {
            return context.getPackageManager().getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            MyLog.w("not found app info " + str);
            return null;
        }
    }

    public static int getArchiveVersionCode(Context context, String str) {
        try {
            return context.getPackageManager().getPackageArchiveInfo(str, 1).versionCode;
        } catch (Exception unused) {
            return 0;
        }
    }

    public static String getCurrentProcessName() {
        String processName = Application.getProcessName();
        return !TextUtils.isEmpty(processName) ? processName : "";
    }

    public static String getForegroundApp(Context context) {
        return null;
    }

    @Deprecated
    public static List<ActivityManager.RunningAppProcessInfo> getForegroundProcess(Context context) {
        return null;
    }

    @Deprecated
    public static String getProcessName(Context context) {
        return "";
    }

    public static Signature[] getSignature(Context context, String str) {
        try {
            return context.getPackageManager().getPackageArchiveInfo(str, 64).signatures;
        } catch (Exception unused) {
            return null;
        }
    }

    public static int getSystemUIForXmsfCode(Context context) {
        Bundle bundle;
        if (context == null) {
            return 0;
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.android.systemui", 128);
            if (applicationInfo == null || (bundle = applicationInfo.metaData) == null) {
                return 0;
            }
            return bundle.getInt("SupportForPushVersionCode");
        } catch (PackageManager.NameNotFoundException unused) {
            return 0;
        }
    }

    public static int getVersionCode(Context context, String str) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, DateUtils.FORMAT_ABBREV_TIME);
        } catch (Exception unused) {
            packageInfo = null;
        }
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    public static String getVersionName(Context context, String str) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, DateUtils.FORMAT_ABBREV_TIME);
        } catch (Exception unused) {
            packageInfo = null;
        }
        return packageInfo != null ? packageInfo.versionName : "1.0";
    }

    public static boolean isAppMainProc(Context context) {
        String currentProcessName = getCurrentProcessName();
        if (TextUtils.isEmpty(currentProcessName) || context == null) {
            return false;
        }
        return currentProcessName.equals(context.getPackageName());
    }

    public static boolean isAppRunning(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return false;
        }
        if (!MIUIUtils.isMIUI()) {
            return context.getPackageName().equals(str);
        }
        AppInfoAgent appInfoAgent = sAppInfoAgent;
        return appInfoAgent != null && appInfoAgent.isAppRunning(context, str);
    }

    public static boolean isApplicationForeground(Context context, String str) {
        AppInfoAgent appInfoAgent = sAppInfoAgent;
        return appInfoAgent != null && appInfoAgent.isApplicationForeground(context, str);
    }

    public static boolean isApplicationInSmallWindow(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            if (Settings.Secure.getInt(context.getContentResolver(), "freeform_window_state", -1) >= 0) {
                return str.equals(Settings.Secure.getString(context.getContentResolver(), "freeform_package_name"));
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isForeground(Context context) {
        return TextUtils.equals(context.getPackageName(), getForegroundApp(context));
    }

    public static boolean isPkgInstalled(Context context, String str) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            packageInfo = null;
        }
        return packageInfo != null;
    }

    public static void setAppInfoAgent(AppInfoAgent appInfoAgent) {
        sAppInfoAgent = appInfoAgent;
    }

    public static void setNotificationsEnabledForPackage(Context context, ApplicationInfo applicationInfo, boolean z2) {
        if (AppNotificationOp.ALLOWED != areNotificationsEnabled(context, applicationInfo)) {
            try {
                Object objCallMethod = JavaCalls.callMethod(context.getSystemService("notification"), "getService", new Object[0]);
                if (objCallMethod != null) {
                    JavaCalls.callMethodOrThrow(objCallMethod, "setNotificationsEnabledForPackage", applicationInfo.packageName, Integer.valueOf(applicationInfo.uid), Boolean.valueOf(z2));
                }
            } catch (Exception e2) {
                MyLog.w("set notifications enabled error " + e2);
            }
        }
    }
}
