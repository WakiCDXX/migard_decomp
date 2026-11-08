package com.xiaomi.mipush.sdk;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class AppPermission {
    public static String getMipushReceivePermission(Context context) {
        return context.getPackageName() + ".permission.MIPUSH_RECEIVE";
    }
}
