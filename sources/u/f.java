package u;

import android.content.Context;
import android.text.TextUtils;
import com.ot.pubsub.util.k;
import com.ot.pubsub.util.m;
import com.ot.pubsub.util.t;
import java.util.HashMap;
import org.json.JSONObject;
import y.l;

/* JADX INFO: loaded from: classes.dex */
public class f {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final JSONObject f2380d = new JSONObject();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Context f2381a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private JSONObject f2382b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private String[] f2383c;

    private static final class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final f f2384a = new f();
    }

    public static f a() {
        return a.f2384a;
    }

    private void d() {
        if (k.f724a) {
            if (TextUtils.isEmpty(this.f2383c[0]) || TextUtils.isEmpty(this.f2383c[1])) {
                k.c("SecretKeyManager", "key or sid is invalid!");
            } else {
                k.c("SecretKeyManager", "key  and sid is valid! ");
            }
        }
    }

    private JSONObject e() {
        JSONObject jSONObjectF = this.f2382b;
        if (jSONObjectF == null && (jSONObjectF = f()) != null) {
            this.f2382b = jSONObjectF;
        }
        return jSONObjectF == null ? c() : jSONObjectF;
    }

    private JSONObject f() {
        try {
            String strO = t.o();
            if (TextUtils.isEmpty(strO)) {
                return null;
            }
            return new JSONObject(b.e(this.f2381a, strO));
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public synchronized String[] b() {
        try {
            JSONObject jSONObjectE = e();
            this.f2383c[0] = jSONObjectE != null ? jSONObjectE.optString("key") : "";
            this.f2383c[1] = jSONObjectE != null ? jSONObjectE.optString("sid") : "";
            d();
        } catch (Throwable th) {
            throw th;
        }
        return this.f2383c;
    }

    public JSONObject c() {
        try {
            if (m.i("SecretKeyManager")) {
                return f2380d;
            }
            byte[] bArrC = u.a.c();
            String strA = c.a(e.b(bArrC));
            HashMap map = new HashMap();
            map.put("secretKey", strA);
            String strD = b0.a.d(l.c().f(), map, true);
            if (!TextUtils.isEmpty(strD)) {
                JSONObject jSONObject = new JSONObject(strD);
                int iOptInt = jSONObject.optInt("code");
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("data");
                if (iOptInt == 0 && jSONObjectOptJSONObject != null) {
                    String strOptString = jSONObjectOptJSONObject.optString("key");
                    String strOptString2 = jSONObjectOptJSONObject.optString("sid");
                    if (!TextUtils.isEmpty(strOptString) && !TextUtils.isEmpty(strOptString2)) {
                        String strA2 = c.a(u.a.h(c.c(strOptString), bArrC));
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("key", strA2);
                        jSONObject2.put("sid", strOptString2);
                        this.f2382b = jSONObject2;
                        t.n(b.a(this.f2381a, jSONObject2.toString()));
                        t.g(System.currentTimeMillis());
                    }
                }
            }
            return this.f2382b;
        } catch (Exception e2) {
            k.h("SecretKeyManager", "requestSecretData: " + e2.toString());
        }
    }

    private f() {
        this.f2382b = null;
        this.f2383c = new String[2];
        this.f2381a = com.ot.pubsub.util.b.a();
    }
}
