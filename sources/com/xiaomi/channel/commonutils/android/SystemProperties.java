package com.xiaomi.channel.commonutils.android;

import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
public class SystemProperties {
    public static String get(String str, String str2) {
        try {
            return (String) SystemUtils.loadClass(null, "android.os.SystemProperties").getMethod("get", String.class, String.class).invoke(null, str, str2);
        } catch (Exception e2) {
            MyLog.w("SystemProperties.get: " + e2);
            return str2;
        }
    }

    public static String get(String str) {
        return get(str, "");
    }
}
