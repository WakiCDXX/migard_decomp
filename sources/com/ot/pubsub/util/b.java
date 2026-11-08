package com.ot.pubsub.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Context f707a = null;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Context f708b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static int f709c = 0;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static String f710d = null;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static String f711e = null;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static long f712f = 0;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static volatile boolean f713g = false;

    public static Context a() {
        if (!f.b(f707a)) {
            return f707a;
        }
        Context context = f708b;
        if (context != null) {
            return context;
        }
        synchronized (b.class) {
            try {
                if (f708b == null) {
                    f708b = f.a(f707a);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return f708b;
    }

    public static void b(Context context) {
        if (f713g) {
            return;
        }
        synchronized (b.class) {
            try {
                if (f713g) {
                    return;
                }
                f707a = context;
                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(f707a.getPackageName(), 0);
                    f709c = packageInfo.versionCode;
                    f710d = packageInfo.versionName;
                    f712f = packageInfo.lastUpdateTime;
                    f711e = f707a.getPackageName();
                } catch (PackageManager.NameNotFoundException e2) {
                    e2.printStackTrace();
                }
                f713g = true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static Context c() {
        return f707a;
    }

    public static String d() {
        return f710d;
    }

    public static String e() {
        return f711e;
    }
}
