package com.miui.powerkeeper.siming;

import android.os.SystemProperties;

/* JADX INFO: loaded from: classes.dex */
public class SiUtils {
    public static final boolean DEBUG = SystemProperties.getBoolean("persist.debug.power.siming", false);
    public static final String D_TAG = "SM_D_";
    public static final String SM_TAG = "SM_";
    public static final String S_TAG = "SM_S_";
    public static final String VERSION = "0.1";

    public static class Log {
        public static void d(String str, String str2) {
            if (SiUtils.DEBUG) {
                android.util.Log.d(str, str2);
            }
        }

        public static void w(String str, String str2) {
            android.util.Log.d(str, str2);
        }
    }
}
