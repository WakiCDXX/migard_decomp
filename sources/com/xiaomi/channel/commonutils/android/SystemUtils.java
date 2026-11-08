package com.xiaomi.channel.commonutils.android;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;

/* JADX INFO: loaded from: classes.dex */
public class SystemUtils {
    private static String cachedMOSVersion;
    private static Context sContext;

    private static String getCOSVersion() {
        String str = SystemProperties.get("ro.build.version.opporom", "");
        if (!TextUtils.isEmpty(str) && !str.startsWith("ColorOS_")) {
            cachedMOSVersion = "ColorOS_" + str;
        }
        return cachedMOSVersion;
    }

    public static Context getContext() {
        return sContext;
    }

    private static String getEMUIVersion() {
        String str = SystemProperties.get("ro.build.version.emui", "");
        cachedMOSVersion = str;
        return str;
    }

    private static String getFOSVersion() {
        String str = SystemProperties.get("ro.vivo.os.version", "");
        if (!TextUtils.isEmpty(str) && !str.startsWith("FuntouchOS_")) {
            cachedMOSVersion = "FuntouchOS_" + str;
        }
        return cachedMOSVersion;
    }

    public static int getMIUIType() {
        try {
            Class<?> clsLoadClass = loadClass(null, "miui.os.Build");
            if (clsLoadClass.getField("IS_STABLE_VERSION").getBoolean(null)) {
                return 3;
            }
            return clsLoadClass.getField("IS_DEVELOPMENT_VERSION").getBoolean(null) ? 2 : 1;
        } catch (Exception unused) {
            return 0;
        }
    }

    public static synchronized String getManufacturerOSVersion() {
        try {
            String str = cachedMOSVersion;
            if (str != null) {
                return str;
            }
            String strValueOf = Build.VERSION.INCREMENTAL;
            if (getMIUIType() <= 0) {
                String eMUIVersion = getEMUIVersion();
                if (TextUtils.isEmpty(eMUIVersion)) {
                    eMUIVersion = getCOSVersion();
                    if (TextUtils.isEmpty(eMUIVersion)) {
                        eMUIVersion = getFOSVersion();
                        if (TextUtils.isEmpty(eMUIVersion)) {
                            strValueOf = String.valueOf(SystemProperties.get("ro.product.brand", "Android") + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + strValueOf);
                        }
                    }
                    strValueOf = eMUIVersion;
                } else {
                    strValueOf = eMUIVersion;
                }
            }
            cachedMOSVersion = strValueOf;
            return strValueOf;
        } catch (Throwable th) {
            throw th;
        }
    }

    public static void initialize(Context context) {
        sContext = context.getApplicationContext();
    }

    public static boolean isBootCompleted() {
        return TextUtils.equals((String) JavaCalls.callStaticMethod("android.os.SystemProperties", "get", "sys.boot_completed"), "1");
    }

    public static boolean isDebuggable(Context context) {
        try {
            return (context.getApplicationInfo().flags & 2) != 0;
        } catch (Exception e2) {
            MyLog.e(e2);
            return false;
        }
    }

    public static boolean isGlobalVersion() {
        try {
            return loadClass(null, "miui.os.Build").getField("IS_GLOBAL_BUILD").getBoolean(Boolean.FALSE);
        } catch (ClassNotFoundException unused) {
            MyLog.e("miui.os.Build ClassNotFound");
            return false;
        } catch (Exception e2) {
            MyLog.e(e2);
            return false;
        }
    }

    public static Class<?> loadClass(Context context, String str) throws ClassNotFoundException {
        if (str == null || str.trim().length() == 0) {
            throw new ClassNotFoundException("class is empty");
        }
        boolean z2 = context != null;
        if (z2) {
            try {
                return context.getClassLoader().loadClass(str);
            } catch (Throwable unused) {
            }
        }
        try {
            return Class.forName(str);
        } catch (Throwable th) {
            MyLog.w(String.format("loadClass fail hasContext= %s, errMsg = %s", Boolean.valueOf(z2), th.getLocalizedMessage()));
            throw new ClassNotFoundException("loadClass fail ", th);
        }
    }
}
