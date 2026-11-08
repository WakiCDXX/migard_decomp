package com.xiaomi.onetrack.util;

import android.text.TextUtils;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class p {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static boolean f1665a = false;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static boolean f1666b = false;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static boolean f1667c = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final int f1668d = 3000;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1669e = "OneTrack-Api-";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final int f1670f = 0;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final int f1671g = 1;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static final int f1672h = 2;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static final int f1673i = 3;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final int f1674j = 4;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static boolean f1675k = false;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static boolean f1676l = false;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static boolean f1677m = false;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static boolean f1678n = false;

    public static void a() {
        try {
            String strE = com.xiaomi.onetrack.f.a.e();
            String strA = ab.a("debug.onetrack.log");
            boolean z2 = false;
            f1676l = (TextUtils.isEmpty(strA) || TextUtils.isEmpty(strE) || !TextUtils.equals(strE, strA)) ? false : true;
            String strA2 = ab.a("debug.onetrack.upload");
            f1666b = (TextUtils.isEmpty(strA2) || TextUtils.isEmpty(strE) || !TextUtils.equals(strE, strA2)) ? false : true;
            String strA3 = ab.a("debug.onetrack.test");
            if (!TextUtils.isEmpty(strA3) && !TextUtils.isEmpty(strE) && TextUtils.equals(strE, strA3)) {
                z2 = true;
            }
            f1678n = z2;
            b();
            c();
        } catch (Exception e2) {
            Log.e("OneTrackSdk", "LogUtil static initializer: " + e2.toString());
        }
        Log.d("OneTrackSdk", "log on: " + f1676l + ", quick upload on: " + f1666b);
    }

    public static void b(String str, String str2) {
        if (f1665a) {
            a(a(str), str2, 0);
        }
    }

    public static void c(String str, String str2) {
        if (f1665a) {
            a(a(str), str2, 1);
        }
    }

    public static void d(String str, String str2) {
        if (f1665a) {
            a(a(str), str2, 2);
        }
    }

    public static void b(String str, String str2, Throwable th) {
        if (f1665a) {
            Log.e(a(str), str2, th);
        }
    }

    public static void c(String str, String str2, Throwable th) {
        if (f1665a) {
            Log.w(a(str), str2, th);
        }
    }

    public static void d(String str, String str2, Throwable th) {
        if (f1665a) {
            Log.i(a(str), str2, th);
        }
    }

    private static void b() {
        f1665a = f1675k || f1676l;
        Log.d("OneTrackSdk", "updateDebugSwitch sEnable: " + f1665a + " sDebugMode：" + f1675k + " sDebugProperty：" + f1676l);
    }

    private static void c() {
        f1667c = f1677m || f1678n;
        Log.d("OneTrackSdk", "updateTestSwitch sTestEnable: " + f1667c + " sTestMode：" + f1677m + " sTestProperty：" + f1678n);
    }

    public static void b(boolean z2) {
        f1677m = z2;
        c();
    }

    public static void a(String str, String str2) {
        if (f1665a) {
            a(a(str), str2, 3);
        }
    }

    public static void a(String str, String str2, Throwable th) {
        if (f1665a) {
            Log.d(a(str), str2, th);
        }
    }

    private static void a(String str, String str2, int i2) {
        if (str2 == null) {
            return;
        }
        int i3 = 0;
        while (i3 <= str2.length() / 3000) {
            int i4 = i3 * 3000;
            i3++;
            int iMin = Math.min(str2.length(), i3 * 3000);
            if (i4 < iMin) {
                String strSubstring = str2.substring(i4, iMin);
                if (i2 == 0) {
                    Log.e(str, strSubstring);
                } else if (i2 == 1) {
                    Log.w(str, strSubstring);
                } else if (i2 == 2) {
                    Log.i(str, strSubstring);
                } else if (i2 == 3) {
                    Log.d(str, strSubstring);
                } else if (i2 == 4) {
                    Log.v(str, strSubstring);
                }
            }
        }
    }

    public static String a(String str) {
        return f1669e + str;
    }

    public static void a(boolean z2) {
        f1675k = z2;
        b();
    }
}
