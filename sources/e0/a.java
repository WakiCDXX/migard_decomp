package e0;

import android.content.Context;
import com.ot.pubsub.util.j;
import com.ot.pubsub.util.k;
import com.ot.pubsub.util.m;
import com.ot.pubsub.util.oaid.helpers.b;
import com.ot.pubsub.util.r;
import f0.l;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1792e = "a";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static volatile a f1793f;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private volatile boolean f1794a = false;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private volatile String f1795b = "";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final int f1796c = 3;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private volatile int f1797d = 0;

    public static a a() {
        if (f1793f == null) {
            synchronized (a.class) {
                try {
                    if (f1793f == null) {
                        f1793f = new a();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f1793f;
    }

    private boolean c() {
        return this.f1797d >= 3;
    }

    public String b(Context context) {
        synchronized (this.f1795b) {
            try {
                if (r.a()) {
                    if (k.f724a) {
                        throw new IllegalStateException("Don't use it on the main thread");
                    }
                    k.h(f1792e, "getOaid() throw exception : Don't use it on the main thread");
                    return "";
                }
                if (this.f1795b != null && !this.f1795b.equals("")) {
                    return this.f1795b;
                }
                if (c()) {
                    k.c(f1792e, "isNotAllowedGetOaid");
                    return this.f1795b;
                }
                if (m.e()) {
                    this.f1795b = j.b(context);
                    this.f1797d++;
                    return this.f1795b;
                }
                String strA = new l().a(context);
                if (strA != null && !strA.equals("")) {
                    this.f1795b = strA;
                    this.f1797d++;
                    return strA;
                }
                String strA2 = new b().a(context);
                if (strA2 == null || strA2.equals("")) {
                    this.f1797d++;
                    return this.f1795b;
                }
                this.f1795b = strA2;
                this.f1797d++;
                return strA2;
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
