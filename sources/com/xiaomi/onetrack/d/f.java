package com.xiaomi.onetrack.d;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.x;
import java.util.HashMap;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final JSONObject f1346a = new JSONObject();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1347b = "SecretKeyManager";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1348c = "secretKey";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1349d = "sid";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1350e = "key";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private Context f1351f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private JSONObject f1352g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private String[] f1353h;

    private static final class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final f f1354a = new f();

        private a() {
        }
    }

    public static f a() {
        return a.f1354a;
    }

    private void d() {
        if (p.f1665a) {
            if (TextUtils.isEmpty(this.f1353h[0]) || TextUtils.isEmpty(this.f1353h[1])) {
                p.a(f1347b, "key or sid is invalid!");
            } else {
                p.a(f1347b, "key  and sid is valid! ");
            }
        }
    }

    private JSONObject e() {
        JSONObject jSONObjectF = this.f1352g;
        if (jSONObjectF == null && (jSONObjectF = f()) != null) {
            this.f1352g = jSONObjectF;
        }
        return jSONObjectF == null ? c() : jSONObjectF;
    }

    private JSONObject f() {
        try {
            String strG = aa.g();
            if (TextUtils.isEmpty(strG)) {
                return null;
            }
            return new JSONObject(b.b(this.f1351f, strG));
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public synchronized String[] b() {
        try {
            JSONObject jSONObjectE = e();
            this.f1353h[0] = jSONObjectE != null ? jSONObjectE.optString("key") : "";
            this.f1353h[1] = jSONObjectE != null ? jSONObjectE.optString("sid") : "";
            d();
        } catch (Throwable th) {
            throw th;
        }
        return this.f1353h;
    }

    public JSONObject c() {
        try {
            if (q.a(f1347b)) {
                return f1346a;
            }
            byte[] bArrA = com.xiaomi.onetrack.d.a.a();
            String strA = c.a(e.a(bArrA));
            HashMap map = new HashMap();
            map.put(f1348c, strA);
            String strB = com.xiaomi.onetrack.g.b.b(x.a().e(), map, true);
            if (!TextUtils.isEmpty(strB)) {
                JSONObject jSONObject = new JSONObject(strB);
                int iOptInt = jSONObject.optInt("code");
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("data");
                if (iOptInt == 0 && jSONObjectOptJSONObject != null) {
                    String strOptString = jSONObjectOptJSONObject.optString("key");
                    String strOptString2 = jSONObjectOptJSONObject.optString("sid");
                    if (!TextUtils.isEmpty(strOptString) && !TextUtils.isEmpty(strOptString2)) {
                        String strA2 = c.a(com.xiaomi.onetrack.d.a.b(c.a(strOptString), bArrA));
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("key", strA2);
                        jSONObject2.put("sid", strOptString2);
                        this.f1352g = jSONObject2;
                        aa.a(b.a(this.f1351f, jSONObject2.toString()));
                        aa.i(System.currentTimeMillis());
                    }
                }
            }
            return this.f1352g;
        } catch (Exception e2) {
            p.b(f1347b, "requestSecretData: " + e2.toString());
        }
    }

    private f() {
        this.f1352g = null;
        this.f1353h = new String[2];
        this.f1351f = com.xiaomi.onetrack.f.a.a();
    }
}
