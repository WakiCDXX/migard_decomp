package t;

import android.text.TextUtils;
import com.ot.pubsub.util.t;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class l {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static volatile l f2360i;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private ConcurrentHashMap<String, r.b> f2361a = new ConcurrentHashMap<>();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private String f2362b = "project_id";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private String f2363c = "private_key_id";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private String f2364d = "app_id";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private String f2365e = "international";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private String f2366f = "region";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private String f2367g = "override_miui_region_setting";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private String f2368h = "need_gzip_and_encrypt";

    private l() {
    }

    public static l b() {
        if (f2360i == null) {
            synchronized (l.class) {
                try {
                    if (f2360i == null) {
                        f2360i = new l();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f2360i;
    }

    private void c(String str, r.b bVar) {
        try {
            if (!TextUtils.isEmpty(str) && bVar != null) {
                com.ot.pubsub.util.e.a(new m(this, bVar, str));
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("ConfigEntityManager", "putProjectIdConfigBySP Exception:" + e2.getMessage());
        }
    }

    private r.b f(String str) {
        JSONObject jSONObjectOptJSONObject;
        try {
            String strL = t.l();
            if (TextUtils.isEmpty(strL) || (jSONObjectOptJSONObject = new JSONObject(u.a.j(strL, u.a.f2373b)).optJSONObject(str)) == null) {
                return null;
            }
            return new r.b.a().l(jSONObjectOptJSONObject.optString(this.f2364d)).r(jSONObjectOptJSONObject.optString(this.f2362b)).q(jSONObjectOptJSONObject.optString(this.f2363c)).n(jSONObjectOptJSONObject.optBoolean(this.f2365e)).s(jSONObjectOptJSONObject.optString(this.f2366f)).p(jSONObjectOptJSONObject.optBoolean(this.f2367g)).o(jSONObjectOptJSONObject.optBoolean(this.f2368h)).c();
        } catch (Exception unused) {
            return null;
        }
    }

    public r.b a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        r.b bVar = this.f2361a.get(str);
        return bVar == null ? f(str) : bVar;
    }

    public void d(r.b bVar) {
        if (bVar == null || TextUtils.isEmpty(bVar.f())) {
            return;
        }
        this.f2361a.put(bVar.f(), bVar);
        c(bVar.f(), bVar);
    }

    public JSONObject e(r.b bVar) {
        if (bVar == null) {
            return null;
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(this.f2364d, bVar.b());
            jSONObject.put(this.f2362b, bVar.f());
            jSONObject.put(this.f2363c, bVar.e());
            jSONObject.put(this.f2365e, bVar.h());
            jSONObject.put(this.f2366f, bVar.g());
            jSONObject.put(this.f2367g, bVar.j());
            jSONObject.put(this.f2368h, bVar.i());
        } catch (Exception unused) {
        }
        return jSONObject;
    }
}
