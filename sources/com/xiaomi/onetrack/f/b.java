package com.xiaomi.onetrack.f;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.onetrack.BuildConfig;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.ac;
import com.xiaomi.onetrack.util.o;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.v;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final int f1363a = 0;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f1364b = 1;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final int f1365c = 2;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1366d = "Event";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private long f1367e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private String f1368f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private String f1369g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private String f1370h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private int f1371i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private JSONObject f1372j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private long f1373k;

    public static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private long f1374a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private String f1375b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private String f1376c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private String f1377d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private int f1378e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private JSONObject f1379f;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private long f1380g;

        public a a(long j2) {
            this.f1374a = this.f1374a;
            return this;
        }

        public a b(String str) {
            this.f1376c = str;
            return this;
        }

        public a c(String str) {
            this.f1377d = str;
            return this;
        }

        public a a(String str) {
            this.f1375b = str;
            return this;
        }

        public a b(long j2) {
            this.f1380g = j2;
            return this;
        }

        public a a(int i2) {
            this.f1378e = i2;
            return this;
        }

        public a a(JSONObject jSONObject) {
            this.f1379f = jSONObject;
            return this;
        }

        public b a() {
            return new b(this);
        }
    }

    /* JADX INFO: renamed from: com.xiaomi.onetrack.f.b$b, reason: collision with other inner class name */
    public static class C0012b {
        public static String A = "sdk_mode";
        public static String B = "ot_first_day";
        public static String C = "ot_test_env";
        public static String D = "ot_privacy_policy";
        public static String E = "market_name";
        public static String F = "ot_ad";
        public static String G = "ot_basic_mode";
        public static String H = "ot_ad_monitor";
        public static String I = "ot_hybrid_type";

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public static String f1381a = "event";

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public static String f1382b = "imei";

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public static String f1383c = "oaid";

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        public static String f1384d = "sn";

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public static String f1385e = "gaid";

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        public static String f1386f = "android_id";

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        public static String f1387g = "instance_id";

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        public static String f1388h = "mfrs";

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        public static String f1389i = "model";

        /* JADX INFO: renamed from: j, reason: collision with root package name */
        public static String f1390j = "platform";

        /* JADX INFO: renamed from: k, reason: collision with root package name */
        public static String f1391k = "miui";

        /* JADX INFO: renamed from: l, reason: collision with root package name */
        public static String f1392l = "build";

        /* JADX INFO: renamed from: m, reason: collision with root package name */
        public static String f1393m = "os_ver";

        /* JADX INFO: renamed from: n, reason: collision with root package name */
        public static String f1394n = "app_id";

        /* JADX INFO: renamed from: o, reason: collision with root package name */
        public static String f1395o = "app_ver";

        /* JADX INFO: renamed from: p, reason: collision with root package name */
        public static String f1396p = "pkg";

        /* JADX INFO: renamed from: q, reason: collision with root package name */
        public static String f1397q = "channel";

        /* JADX INFO: renamed from: r, reason: collision with root package name */
        public static String f1398r = "e_ts";

        /* JADX INFO: renamed from: s, reason: collision with root package name */
        public static String f1399s = "tz";

        /* JADX INFO: renamed from: t, reason: collision with root package name */
        public static String f1400t = "net";

        /* JADX INFO: renamed from: u, reason: collision with root package name */
        public static String f1401u = "region";

        /* JADX INFO: renamed from: v, reason: collision with root package name */
        public static String f1402v = "plugin_id";

        /* JADX INFO: renamed from: w, reason: collision with root package name */
        public static String f1403w = "sdk_ver";

        /* JADX INFO: renamed from: x, reason: collision with root package name */
        public static String f1404x = "uid";

        /* JADX INFO: renamed from: y, reason: collision with root package name */
        public static String f1405y = "uid_type";

        /* JADX INFO: renamed from: z, reason: collision with root package name */
        public static String f1406z = "sid";
    }

    public void a(long j2) {
        this.f1367e = j2;
    }

    public long b() {
        return this.f1367e;
    }

    public String c() {
        return this.f1368f;
    }

    public String d() {
        return this.f1369g;
    }

    public String e() {
        return this.f1370h;
    }

    public int f() {
        return this.f1371i;
    }

    public JSONObject g() {
        return this.f1372j;
    }

    public long h() {
        return this.f1373k;
    }

    public boolean i() {
        try {
            JSONObject jSONObject = this.f1372j;
            return (jSONObject == null || !jSONObject.has(com.xiaomi.onetrack.api.e.f1071b) || !this.f1372j.has(com.xiaomi.onetrack.api.e.f1070a) || TextUtils.isEmpty(this.f1368f) || TextUtils.isEmpty(this.f1369g)) ? false : true;
        } catch (Exception e2) {
            p.b(f1366d, "check event isValid error, ", e2);
            return false;
        }
    }

    public b() {
    }

    public void a(String str) {
        this.f1368f = str;
    }

    public void b(String str) {
        this.f1369g = str;
    }

    public void c(String str) {
        this.f1370h = str;
    }

    private b(a aVar) {
        this.f1367e = aVar.f1374a;
        this.f1368f = aVar.f1375b;
        this.f1369g = aVar.f1376c;
        this.f1370h = aVar.f1377d;
        this.f1371i = aVar.f1378e;
        this.f1372j = aVar.f1379f;
        this.f1373k = aVar.f1380g;
    }

    public void a(int i2) {
        this.f1371i = i2;
    }

    public void b(JSONObject jSONObject) {
        this.f1372j = jSONObject;
    }

    public static JSONObject a(String str, Configuration configuration, OneTrack.IEventHook iEventHook, v vVar, boolean z2, boolean z3) throws JSONException {
        return a(str, configuration, iEventHook, "", vVar, z2, z3);
    }

    public void b(long j2) {
        this.f1373k = j2;
    }

    public static JSONObject a(String str, Configuration configuration, OneTrack.IEventHook iEventHook, String str2, v vVar, boolean z2, boolean z3) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        Context contextB = com.xiaomi.onetrack.f.a.b();
        jSONObject.put(C0012b.f1381a, str);
        if (!z2) {
            if (q.a() ? q.h() : configuration.isInternational()) {
                if (iEventHook != null && iEventHook.isRecommendEvent(str)) {
                    String strP = DeviceUtil.p(contextB);
                    if (!TextUtils.isEmpty(strP)) {
                        jSONObject.put(C0012b.f1385e, strP);
                    }
                }
            } else {
                jSONObject.put(C0012b.f1382b, DeviceUtil.b(contextB));
                jSONObject.put(C0012b.f1383c, com.xiaomi.onetrack.util.oaid.a.a().a(contextB));
            }
            jSONObject.put(C0012b.f1387g, o.a().b());
            a(jSONObject, configuration, str2);
            a(jSONObject, contextB);
            jSONObject.put(C0012b.f1406z, q.f());
        }
        jSONObject.put(C0012b.f1388h, DeviceUtil.e());
        jSONObject.put(C0012b.f1389i, DeviceUtil.c());
        jSONObject.put(C0012b.f1390j, "Android");
        jSONObject.put(C0012b.f1391k, q.d());
        jSONObject.put(C0012b.f1392l, q.c());
        jSONObject.put(C0012b.f1393m, q.e());
        jSONObject.put(C0012b.f1395o, com.xiaomi.onetrack.f.a.c());
        jSONObject.put(C0012b.f1398r, System.currentTimeMillis());
        jSONObject.put(C0012b.f1399s, q.b());
        jSONObject.put(C0012b.f1400t, com.xiaomi.onetrack.g.c.a(contextB).toString());
        jSONObject.put(C0012b.f1401u, q.i());
        jSONObject.put(C0012b.f1403w, BuildConfig.SDK_VERSION);
        if (z3) {
            jSONObject.put(C0012b.f1394n, configuration.getAdEventAppId());
        } else {
            jSONObject.put(C0012b.f1394n, configuration.getAppId());
        }
        jSONObject.put(C0012b.F, z3);
        jSONObject.put(C0012b.f1396p, com.xiaomi.onetrack.f.a.e());
        jSONObject.put(C0012b.f1397q, !TextUtils.isEmpty(configuration.getChannel()) ? configuration.getChannel() : "default");
        jSONObject.put(C0012b.A, (configuration.getMode() != null ? configuration.getMode() : OneTrack.Mode.APP).getType());
        jSONObject.put(C0012b.B, ac.d(aa.B()));
        if (p.f1667c) {
            jSONObject.put(C0012b.C, true);
        }
        jSONObject.put(C0012b.D, vVar.a());
        jSONObject.put(C0012b.E, DeviceUtil.d());
        jSONObject.put(C0012b.G, z2);
        return jSONObject;
    }

    public static JSONObject a(com.xiaomi.onetrack.api.f fVar, Configuration configuration, OneTrack.IEventHook iEventHook, v vVar, boolean z2, boolean z3) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        Context contextB = com.xiaomi.onetrack.f.a.b();
        jSONObject.put(C0012b.f1381a, fVar.a());
        if (!z2) {
            if (q.a() ? q.h() : configuration.isInternational()) {
                if (iEventHook != null && iEventHook.isRecommendEvent(fVar.a())) {
                    String strP = DeviceUtil.p(contextB);
                    if (!TextUtils.isEmpty(strP)) {
                        jSONObject.put(C0012b.f1385e, strP);
                    }
                }
            } else {
                jSONObject.put(C0012b.f1382b, DeviceUtil.b(contextB));
                jSONObject.put(C0012b.f1383c, com.xiaomi.onetrack.util.oaid.a.a().a(contextB));
            }
            jSONObject.put(C0012b.f1387g, o.a().b());
            jSONObject.put(C0012b.f1402v, configuration.getPluginId());
            if (!TextUtils.isEmpty(fVar.e()) && !TextUtils.isEmpty(fVar.f())) {
                jSONObject.put(C0012b.f1404x, fVar.e());
                jSONObject.put(C0012b.f1405y, fVar.f());
            }
            jSONObject.put(C0012b.f1406z, q.f());
        }
        jSONObject.put(C0012b.F, z3);
        jSONObject.put(C0012b.f1388h, DeviceUtil.e());
        jSONObject.put(C0012b.f1389i, DeviceUtil.c());
        jSONObject.put(C0012b.f1390j, "Android");
        jSONObject.put(C0012b.f1391k, q.d());
        jSONObject.put(C0012b.f1392l, q.c());
        jSONObject.put(C0012b.f1393m, q.e());
        jSONObject.put(C0012b.f1395o, com.xiaomi.onetrack.f.a.c());
        jSONObject.put(C0012b.f1398r, fVar.b());
        jSONObject.put(C0012b.f1399s, q.b());
        jSONObject.put(C0012b.f1400t, com.xiaomi.onetrack.g.c.a(contextB).toString());
        jSONObject.put(C0012b.f1401u, q.i());
        jSONObject.put(C0012b.f1403w, BuildConfig.SDK_VERSION);
        jSONObject.put(C0012b.f1394n, fVar.c());
        jSONObject.put(C0012b.f1396p, com.xiaomi.onetrack.f.a.e());
        jSONObject.put(C0012b.f1397q, !TextUtils.isEmpty(fVar.d()) ? fVar.d() : "default");
        jSONObject.put(C0012b.A, (configuration.getMode() != null ? configuration.getMode() : OneTrack.Mode.APP).getType());
        jSONObject.put(C0012b.B, ac.d(aa.B()));
        if (p.f1667c) {
            jSONObject.put(C0012b.C, true);
        }
        jSONObject.put(C0012b.D, vVar.a());
        jSONObject.put(C0012b.E, DeviceUtil.d());
        jSONObject.put(C0012b.G, z2);
        jSONObject.put(C0012b.I, "JS");
        return jSONObject;
    }

    private static void a(JSONObject jSONObject, Context context) throws JSONException {
        String strU = aa.u();
        String strW = aa.w();
        if (TextUtils.isEmpty(strU) || TextUtils.isEmpty(strW)) {
            return;
        }
        jSONObject.put(C0012b.f1404x, strU);
        jSONObject.put(C0012b.f1405y, strW);
    }

    private static void a(JSONObject jSONObject, Configuration configuration, String str) throws JSONException {
        if (!TextUtils.isEmpty(str)) {
            jSONObject.put(C0012b.f1402v, str);
        } else {
            jSONObject.put(C0012b.f1402v, configuration.getPluginId());
        }
    }
}
