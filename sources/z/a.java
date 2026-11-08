package z;

import android.content.Context;
import android.text.TextUtils;
import com.ot.pubsub.PubSubTrack;
import com.ot.pubsub.util.DeviceUtil;
import com.ot.pubsub.util.k;
import com.ot.pubsub.util.l;
import com.ot.pubsub.util.m;
import com.ot.pubsub.util.q;
import com.ot.pubsub.util.t;
import com.ot.pubsub.util.v;
import com.xiaomi.onetrack.api.e;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private String f2446a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private String f2447b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private String f2448c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private int f2449d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private JSONObject f2450e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private long f2451f;

    /* JADX INFO: renamed from: z.a$a, reason: collision with other inner class name */
    static class C0028a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public static String f2452a = "event";

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public static String f2453b = "imei";

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public static String f2454c = "oaid";

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        public static String f2455d = "gaid";

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public static String f2456e = "instance_id";

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        public static String f2457f = "mfrs";

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        public static String f2458g = "model";

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        public static String f2459h = "platform";

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        public static String f2460i = "miui";

        /* JADX INFO: renamed from: j, reason: collision with root package name */
        public static String f2461j = "build";

        /* JADX INFO: renamed from: k, reason: collision with root package name */
        public static String f2462k = "os_ver";

        /* JADX INFO: renamed from: l, reason: collision with root package name */
        public static String f2463l = "app_id";

        /* JADX INFO: renamed from: m, reason: collision with root package name */
        public static String f2464m = "app_ver";

        /* JADX INFO: renamed from: n, reason: collision with root package name */
        public static String f2465n = "pkg";

        /* JADX INFO: renamed from: o, reason: collision with root package name */
        public static String f2466o = "channel";

        /* JADX INFO: renamed from: p, reason: collision with root package name */
        public static String f2467p = "e_ts";

        /* JADX INFO: renamed from: q, reason: collision with root package name */
        public static String f2468q = "tz";

        /* JADX INFO: renamed from: r, reason: collision with root package name */
        public static String f2469r = "net";

        /* JADX INFO: renamed from: s, reason: collision with root package name */
        public static String f2470s = "region";

        /* JADX INFO: renamed from: t, reason: collision with root package name */
        public static String f2471t = "sdk_ver";

        /* JADX INFO: renamed from: u, reason: collision with root package name */
        public static String f2472u = "sid";

        /* JADX INFO: renamed from: v, reason: collision with root package name */
        public static String f2473v = "sdk_mode";

        /* JADX INFO: renamed from: w, reason: collision with root package name */
        public static String f2474w = "ot_first_day";

        /* JADX INFO: renamed from: x, reason: collision with root package name */
        public static String f2475x = "ot_test_env";

        /* JADX INFO: renamed from: y, reason: collision with root package name */
        public static String f2476y = "ot_privacy_policy";

        /* JADX INFO: renamed from: z, reason: collision with root package name */
        public static String f2477z = "market_name";
    }

    public static JSONObject a(String str, r.b bVar, PubSubTrack.a aVar, q qVar) throws JSONException {
        return b(str, bVar, aVar, "", qVar);
    }

    public static JSONObject b(String str, r.b bVar, PubSubTrack.a aVar, String str2, q qVar) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        Context contextC = com.ot.pubsub.util.b.c();
        jSONObject.put(C0028a.f2452a, str);
        if (!(m.e() ? m.n() : bVar.h())) {
            jSONObject.put(C0028a.f2453b, DeviceUtil.d(contextC));
            jSONObject.put(C0028a.f2454c, e0.a.a().b(contextC));
        } else if (aVar != null && aVar.isRecommendEvent(str)) {
            String strM = DeviceUtil.m(contextC);
            if (!TextUtils.isEmpty(strM)) {
                jSONObject.put(C0028a.f2455d, strM);
            }
        }
        jSONObject.put(C0028a.f2456e, y.b.a().c());
        jSONObject.put(C0028a.f2457f, DeviceUtil.i());
        jSONObject.put(C0028a.f2458g, DeviceUtil.f());
        jSONObject.put(C0028a.f2459h, "Android");
        jSONObject.put(C0028a.f2460i, m.j());
        jSONObject.put(C0028a.f2461j, m.h());
        jSONObject.put(C0028a.f2462k, m.k());
        jSONObject.put(C0028a.f2464m, com.ot.pubsub.util.b.d());
        jSONObject.put(C0028a.f2467p, System.currentTimeMillis());
        jSONObject.put(C0028a.f2468q, m.f());
        jSONObject.put(C0028a.f2469r, l.b(contextC).toString());
        jSONObject.put(C0028a.f2470s, m.o());
        jSONObject.put(C0028a.f2471t, "2.0.0");
        jSONObject.put(C0028a.f2463l, bVar.b());
        jSONObject.put(C0028a.f2465n, com.ot.pubsub.util.b.e());
        jSONObject.put(C0028a.f2466o, !TextUtils.isEmpty(bVar.c()) ? bVar.c() : "default");
        jSONObject.put(C0028a.f2472u, m.l());
        jSONObject.put(C0028a.f2473v, "sdk");
        jSONObject.put(C0028a.f2474w, v.c(t.u()));
        if (k.f728e) {
            jSONObject.put(C0028a.f2475x, true);
        }
        jSONObject.put(C0028a.f2476y, qVar.a());
        jSONObject.put(C0028a.f2477z, DeviceUtil.h());
        return jSONObject;
    }

    public void c(int i2) {
        this.f2449d = i2;
    }

    public void d(String str) {
        this.f2446a = str;
    }

    public void e(JSONObject jSONObject) {
        this.f2450e = jSONObject;
    }

    public String f() {
        return this.f2446a;
    }

    public void g(long j2) {
        this.f2451f = j2;
    }

    public void h(String str) {
        this.f2447b = str;
    }

    public String i() {
        return this.f2447b;
    }

    public void j(String str) {
        this.f2448c = str;
    }

    public String k() {
        return this.f2448c;
    }

    public int l() {
        return this.f2449d;
    }

    public JSONObject m() {
        return this.f2450e;
    }

    public boolean n() {
        try {
            JSONObject jSONObject = this.f2450e;
            return (jSONObject == null || !jSONObject.has(e.f1071b) || !this.f2450e.has(e.f1070a) || TextUtils.isEmpty(this.f2446a) || TextUtils.isEmpty(this.f2447b)) ? false : true;
        } catch (Exception e2) {
            k.i("BaseMessage", "check event isValid error, ", e2);
            return false;
        }
    }
}
