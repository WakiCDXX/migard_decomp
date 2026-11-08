package com.ot.pubsub.util;

import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.push.service.clientReport.ReportConstants;

/* JADX INFO: loaded from: classes.dex */
public class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static boolean f724a = false;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static boolean f725b = false;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static boolean f726c = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static boolean f727d = false;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static boolean f728e = false;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static boolean f729f = false;

    public static String a(String str) {
        return "OT-PubSub-" + str;
    }

    public static void b() {
        try {
            String strE = b.e();
            String strA = u.a("debug.pubsub.log");
            boolean z2 = false;
            f726c = (TextUtils.isEmpty(strA) || TextUtils.isEmpty(strE) || !TextUtils.equals(strE, strA)) ? false : true;
            String strA2 = u.a("debug.pubsub.upload");
            f727d = (TextUtils.isEmpty(strA2) || TextUtils.isEmpty(strE) || !TextUtils.equals(strE, strA2)) ? false : true;
            String strA3 = u.a("debug.pubsub.test");
            if (!TextUtils.isEmpty(strA3) && !TextUtils.isEmpty(strE) && TextUtils.equals(strE, strA3)) {
                z2 = true;
            }
            f729f = z2;
            g();
        } catch (Exception e2) {
            Log.e("OT-PubSub-Sdk", "LogUtil static initializer: " + e2.toString());
        }
        Log.d("OT-PubSub-Sdk", "log on: " + f726c + ", quick upload on: " + f727d);
    }

    public static void c(String str, String str2) {
        if (f724a) {
            d(a(str), str2, 3);
        }
    }

    private static void d(String str, String str2, int i2) {
        if (str2 == null) {
            return;
        }
        int i3 = 0;
        while (i3 <= str2.length() / ReportConstants.AWAKE_TYPE) {
            int i4 = i3 * ReportConstants.AWAKE_TYPE;
            i3++;
            int iMin = Math.min(str2.length(), i3 * ReportConstants.AWAKE_TYPE);
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

    public static void e(String str, String str2, Throwable th) {
        if (f724a) {
            Log.d(a(str), str2, th);
        }
    }

    public static void f(boolean z2) {
        f725b = z2;
        g();
    }

    private static void g() {
        f724a = f725b || f726c;
        Log.d("OT-PubSub-Sdk", "updateDebugSwitch sEnable: " + f724a + " sDebugMode：" + f725b + " sDebugProperty：" + f726c);
    }

    public static void h(String str, String str2) {
        if (f724a) {
            d(a(str), str2, 0);
        }
    }

    public static void i(String str, String str2, Throwable th) {
        if (f724a) {
            Log.e(a(str), str2, th);
        }
    }

    public static void j(String str, String str2, Object... objArr) {
        if (f724a) {
            d(a(str), String.format(str2, objArr), 0);
        }
    }

    public static void k(String str, String str2) {
        if (f724a) {
            d(a(str), str2, 1);
        }
    }

    public static void l(String str, String str2) {
        if (f724a) {
            d(a(str), str2, 2);
        }
    }
}
