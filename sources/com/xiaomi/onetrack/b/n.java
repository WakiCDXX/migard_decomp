package com.xiaomi.onetrack.b;

import android.text.TextUtils;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;

/* JADX INFO: loaded from: classes.dex */
public class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static String f1233a = "ConfigProvider";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static volatile boolean f1234b = false;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static volatile boolean f1235c = true;

    public static boolean a() {
        try {
            String[] strArrB = com.xiaomi.onetrack.d.f.a().b();
            return (!TextUtils.isEmpty(strArrB[0]) && !TextUtils.isEmpty(strArrB[1])) && !q.a(f1233a);
        } catch (Exception e2) {
            p.a(f1233a, "ConfigProvider.available", e2);
            return false;
        }
    }

    public static synchronized boolean b() {
        return f1234b;
    }

    public static boolean c() {
        return com.xiaomi.onetrack.g.c.a();
    }

    public static int a(int i2) {
        int iIntValue;
        if (p.f1666b) {
            p.a(f1233a, "debug upload mode, send events immediately");
            return 0;
        }
        try {
            iIntValue = e.c().get(Integer.valueOf(i2 + 1)).intValue();
        } catch (Exception unused) {
            iIntValue = 60000;
        }
        p.a(f1233a, "getUploadInterval " + iIntValue);
        return iIntValue;
    }

    public static synchronized void a(boolean z2) {
        f1234b = z2;
    }
}
