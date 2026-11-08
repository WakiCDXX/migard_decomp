package c0;

import android.text.TextUtils;
import com.ot.pubsub.util.k;
import com.ot.pubsub.util.m;
import com.ot.pubsub.util.t;
import d0.d;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;
import t.l;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f57c = "b";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static volatile b f58d;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private volatile boolean f59a = false;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private ConcurrentHashMap<String, a> f60b = new ConcurrentHashMap<>();

    private b() {
    }

    public static b a() {
        if (f58d == null) {
            synchronized (b.class) {
                try {
                    if (f58d == null) {
                        f58d = new b();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f58d;
    }

    private void c(String str, String str2) {
        try {
            String strF = t.f();
            if (!TextUtils.isEmpty(strF)) {
                String str3 = u.a.f2373b;
                String strJ = u.a.j(strF, str3);
                if (!TextUtils.isEmpty(strJ)) {
                    JSONObject jSONObject = new JSONObject(strJ);
                    jSONObject.put(str, str2);
                    t.e(u.a.b(jSONObject.toString(), str3));
                    return;
                }
            }
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put(str, str2);
            t.e(u.a.b(jSONObject2.toString(), u.a.f2373b));
        } catch (Exception e2) {
            k.c(f57c, "putProjectIdTokenBySP Exception:" + e2.getMessage());
        }
    }

    private void e(a aVar) {
        d.a().d(false);
        d.a().b(0, false);
        d.a().b(1, false);
    }

    private a g(String str) {
        try {
            String strF = t.f();
            if (TextUtils.isEmpty(strF)) {
                return null;
            }
            return a.a(new JSONObject(u.a.j(strF, u.a.f2373b)).optString(str));
        } catch (Exception unused) {
            return null;
        }
    }

    private a h(String str) {
        a aVarA = null;
        try {
            if (!this.f59a && !m.i(f57c)) {
                this.f59a = true;
                r.b bVarA = l.b().a(str);
                if (bVarA != null) {
                    String strE = bVarA.e();
                    HashMap map = new HashMap();
                    map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    aVarA = a.a(b0.a.b(strE, y.l.c().e(), map));
                }
                return aVarA;
            }
            return null;
        } catch (Exception unused) {
        } finally {
            this.f59a = false;
        }
    }

    public String b(String str) {
        if (d(this.f60b.get(str))) {
            return this.f60b.get(str).f53d;
        }
        a aVarG = g(str);
        if (d(aVarG)) {
            this.f60b.put(str, aVarG);
            return aVarG.f53d;
        }
        a aVarH = h(str);
        if (!d(aVarH)) {
            return "";
        }
        this.f60b.put(str, aVarH);
        c(str, aVarH.f56g);
        e(aVarH);
        return aVarH.f53d;
    }

    public boolean d(a aVar) {
        if (aVar == null) {
            return false;
        }
        try {
            if (TextUtils.isEmpty(aVar.f53d)) {
                return false;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            long j2 = aVar.f55f;
            if (jCurrentTimeMillis < j2) {
                return false;
            }
            long j3 = ((jCurrentTimeMillis - j2) / 1000) + 180;
            if (j3 < aVar.f54e) {
                return true;
            }
            k.c(f57c, "interval:" + j3 + "_expires:" + aVar.f54e + "_isValid:false");
            return false;
        } catch (Exception unused) {
            return true;
        }
    }

    public void f(String str) {
        try {
            this.f60b.put(str, new a());
            t.e("");
            a aVarH = h(str);
            if (d(aVarH)) {
                this.f60b.put(str, aVarH);
                c(str, aVarH.f56g);
                e(aVarH);
            }
        } catch (Exception e2) {
            k.h(f57c, "forceRefreshToken Exception：" + e2.getMessage());
        }
    }
}
