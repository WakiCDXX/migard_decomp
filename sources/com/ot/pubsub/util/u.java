package com.ot.pubsub.util;

import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class u {
    public static String a(String str) {
        return b(str, "");
    }

    public static String b(String str, String str2) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class).invoke(null, str, str2);
        } catch (Exception e2) {
            Log.d(k.a("SystemProperties"), "get e", e2);
            return str2;
        }
    }
}
