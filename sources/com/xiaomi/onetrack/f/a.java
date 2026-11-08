package com.xiaomi.onetrack.f;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.xiaomi.onetrack.util.j;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Context f1356a = null;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Context f1357b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static int f1358c = 0;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static String f1359d = null;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static String f1360e = null;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static long f1361f = 0;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static volatile boolean f1362g = false;

    public static void a(Context context) {
        if (f1362g) {
            return;
        }
        synchronized (a.class) {
            try {
                if (f1362g) {
                    return;
                }
                f1356a = context;
                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(f1356a.getPackageName(), 0);
                    f1358c = packageInfo.versionCode;
                    f1359d = packageInfo.versionName;
                    f1361f = packageInfo.lastUpdateTime;
                    f1360e = f1356a.getPackageName();
                } catch (PackageManager.NameNotFoundException e2) {
                    e2.printStackTrace();
                }
                f1362g = true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static boolean b(Context context, String str) {
        PackageInfo packageInfoA = a(context, str, 0);
        return (packageInfoA == null || packageInfoA.applicationInfo == null) ? false : true;
    }

    public static String c(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationInfo(str, 0).loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static int d() {
        return f1358c;
    }

    public static String e() {
        return f1360e;
    }

    public static long f() {
        return f1361f;
    }

    public static Context b() {
        return f1356a;
    }

    public static String c() {
        return f1359d;
    }

    public static boolean a(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) != 0;
    }

    public static boolean a(Context context, String str) {
        try {
            return a(a(context, str, 0).applicationInfo);
        } catch (Exception unused) {
            return false;
        }
    }

    public static PackageInfo a(Context context, String str, int i2) {
        try {
            return context.getPackageManager().getPackageInfo(str, i2);
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static Context a() {
        if (j.d(f1356a)) {
            Context context = f1357b;
            if (context != null) {
                return context;
            }
            synchronized (a.class) {
                try {
                    if (f1357b == null) {
                        f1357b = j.a(f1356a);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            return f1357b;
        }
        return f1356a;
    }
}
