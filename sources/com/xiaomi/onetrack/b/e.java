package com.xiaomi.onetrack.b;

import android.text.TextUtils;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.onetrack.BuildConfig;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.x;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f1185a = "l";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1186b = "CommonConfigUpdater";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1187c = "t";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1188d = "levels";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1189e = "Android";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static ConcurrentHashMap<Integer, Integer> f1190f = new ConcurrentHashMap<>();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final long f1191g = 172800000;

    public static void a() {
        com.xiaomi.onetrack.util.i.a(new f());
    }

    public static void b() {
        if (e()) {
            f();
        } else {
            p.a(f1186b, "CommonConfigUpdater Does not meet prerequisites for request");
        }
    }

    public static Map<Integer, Integer> c() {
        try {
            if (!f1190f.isEmpty()) {
                return f1190f;
            }
            String strL = aa.l();
            if (!TextUtils.isEmpty(strL)) {
                JSONArray jSONArrayOptJSONArray = new JSONObject(strL).optJSONArray(f1188d);
                for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                    JSONObject jSONObject = jSONArrayOptJSONArray.getJSONObject(i2);
                    int iOptInt = jSONObject.optInt("l");
                    int iOptInt2 = jSONObject.optInt("t");
                    if (iOptInt > 0 && iOptInt2 > 0) {
                        f1190f.put(Integer.valueOf(iOptInt), Integer.valueOf(iOptInt2));
                    }
                }
            }
            return f1190f.isEmpty() ? g() : f1190f;
        } catch (Exception e2) {
            p.a(f1186b, "getLevelIntervalConfig: " + e2.toString());
        }
    }

    private static boolean e() {
        if (!com.xiaomi.onetrack.g.c.a()) {
            p.b(f1186b, "net is not connected!");
            return false;
        }
        if (TextUtils.isEmpty(aa.l())) {
            return true;
        }
        long j2 = aa.j();
        return j2 < System.currentTimeMillis() || j2 - System.currentTimeMillis() > f1191g;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void f() {
        if (q.a(f1186b)) {
            return;
        }
        HashMap map = new HashMap();
        try {
            map.put(m.f1219a, com.xiaomi.onetrack.util.oaid.a.a().a(com.xiaomi.onetrack.f.a.b()));
            map.put("ov", q.d());
            map.put(m.f1221c, q.c());
            map.put(m.f1222d, q.h() ? "1" : "0");
            map.put(m.f1223e, BuildConfig.SDK_VERSION);
            map.put(m.f1231m, com.xiaomi.onetrack.f.a.c());
            map.put(m.f1224f, q.e());
            map.put(m.f1225g, DeviceUtil.c());
            map.put(m.f1226h, q.i());
            map.put("platform", f1189e);
            String strD = x.a().d();
            String strB = com.xiaomi.onetrack.g.b.b(strD, map, true);
            p.a(f1186b, "url:" + strD + " response:" + strB);
            a(strB);
        } catch (IOException e2) {
            p.a(f1186b, "requestCloudData: " + e2.toString());
        }
    }

    private static HashMap<Integer, Integer> g() {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1, 1000);
        map.put(2, 15000);
        map.put(3, Integer.valueOf(Constants.MAX_VALID_TIME_FOR_REGISTRATION_REQUEST));
        return map;
    }

    private static void a(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.optInt("code") == 0) {
                String strOptString = jSONObject.optString(com.xiaomi.onetrack.g.a.f1425e);
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("data");
                if (jSONObjectOptJSONObject != null) {
                    JSONObject jSONObjectOptJSONObject2 = jSONObjectOptJSONObject.optJSONObject("regionUrl");
                    if (jSONObjectOptJSONObject2 != null) {
                        x.a().a(jSONObjectOptJSONObject2);
                    }
                    aa.d(jSONObjectOptJSONObject.toString());
                    aa.c(strOptString);
                }
                aa.j(System.currentTimeMillis() + 86400000 + ((long) new Random().nextInt(86400000)));
            }
        } catch (JSONException e2) {
            p.a(f1186b, "saveCommonCloudData: " + e2.toString());
        }
    }
}
