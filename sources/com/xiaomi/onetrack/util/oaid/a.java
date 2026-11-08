package com.xiaomi.onetrack.util.oaid;

import android.content.Context;
import com.xiaomi.onetrack.util.n;
import com.xiaomi.onetrack.util.oaid.helpers.b;
import com.xiaomi.onetrack.util.oaid.helpers.g;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.w;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1591a = "a";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static volatile a f1592b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private volatile boolean f1593c = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private volatile String f1594d = "";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final int f1595e = 3;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private volatile int f1596f = 0;

    public static a a() {
        if (f1592b == null) {
            synchronized (a.class) {
                try {
                    if (f1592b == null) {
                        f1592b = new a();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f1592b;
    }

    private boolean b() {
        return this.f1596f >= 3;
    }

    public String a(Context context) {
        synchronized (this.f1594d) {
            try {
                if (w.a()) {
                    if (!p.f1665a) {
                        p.b(f1591a, "getOaid() throw exception : Don't use it on the main thread");
                        return "";
                    }
                    throw new IllegalStateException("Don't use it on the main thread");
                }
                if (this.f1594d != null && !this.f1594d.equals("")) {
                    return this.f1594d;
                }
                if (b()) {
                    p.a(f1591a, "isNotAllowedGetOaid");
                    return this.f1594d;
                }
                if (q.a()) {
                    this.f1594d = n.b(context);
                    this.f1596f++;
                    return this.f1594d;
                }
                String strA = new g().a(context);
                if (strA != null && !strA.equals("")) {
                    this.f1594d = strA;
                    this.f1596f++;
                    return strA;
                }
                String strA2 = new b().a(context);
                if (strA2 != null && !strA2.equals("")) {
                    this.f1594d = strA2;
                    this.f1596f++;
                    return strA2;
                }
                this.f1596f++;
                return this.f1594d;
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
