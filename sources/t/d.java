package t;

import android.text.TextUtils;
import com.ot.pubsub.util.DeviceUtil;
import com.ot.pubsub.util.t;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.mipush.sdk.Constants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static ConcurrentHashMap<Integer, Integer> f2345a = new ConcurrentHashMap<>();

    public static void a() {
        com.ot.pubsub.util.e.a(new e());
    }

    private static void b(String str) {
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
                        y.l.c().d(jSONObjectOptJSONObject2);
                    }
                    t.t(jSONObjectOptJSONObject.toString());
                    t.x(strOptString);
                }
                t.p(System.currentTimeMillis() + 86400000 + ((long) new Random().nextInt(86400000)));
            }
        } catch (JSONException e2) {
            com.ot.pubsub.util.k.c("CommonConfigUpdater", "saveCommonCloudData: " + e2.toString());
        }
    }

    public static void c() {
        if (e()) {
            f();
        } else {
            com.ot.pubsub.util.k.c("CommonConfigUpdater", "CommonConfigUpdater Does not meet prerequisites for request");
        }
    }

    public static Map<Integer, Integer> d() {
        try {
            if (!f2345a.isEmpty()) {
                return f2345a;
            }
            String strW = t.w();
            if (!TextUtils.isEmpty(strW)) {
                JSONArray jSONArrayOptJSONArray = new JSONObject(strW).optJSONArray("levels");
                for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                    JSONObject jSONObject = jSONArrayOptJSONArray.getJSONObject(i2);
                    int iOptInt = jSONObject.optInt("l");
                    int iOptInt2 = jSONObject.optInt(RegisterAppChannelInfoCollectionJob.JSON_KEY_CHANNEL_TYPE);
                    if (iOptInt > 0 && iOptInt2 > 0) {
                        f2345a.put(Integer.valueOf(iOptInt), Integer.valueOf(iOptInt2));
                    }
                }
            }
            return f2345a.isEmpty() ? g() : f2345a;
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("CommonConfigUpdater", "getLevelIntervalConfig: " + e2.toString());
        }
    }

    private static boolean e() {
        if (!com.ot.pubsub.util.l.a()) {
            com.ot.pubsub.util.k.h("CommonConfigUpdater", "net is not connected!");
            return false;
        }
        if (TextUtils.isEmpty(t.w())) {
            return true;
        }
        long jY = t.y();
        return jY < System.currentTimeMillis() || jY - System.currentTimeMillis() > 172800000;
    }

    private static void f() {
        if (com.ot.pubsub.util.m.i("CommonConfigUpdater")) {
            return;
        }
        HashMap map = new HashMap();
        try {
            map.put(com.xiaomi.onetrack.b.m.f1219a, e0.a.a().b(com.ot.pubsub.util.b.c()));
            map.put("ov", com.ot.pubsub.util.m.j());
            map.put(com.xiaomi.onetrack.b.m.f1221c, com.ot.pubsub.util.m.h());
            map.put(com.xiaomi.onetrack.b.m.f1222d, com.ot.pubsub.util.m.n() ? "1" : "0");
            map.put(com.xiaomi.onetrack.b.m.f1223e, "2.0.0");
            map.put(com.xiaomi.onetrack.b.m.f1231m, com.ot.pubsub.util.b.d());
            map.put(com.xiaomi.onetrack.b.m.f1224f, com.ot.pubsub.util.m.k());
            map.put(com.xiaomi.onetrack.b.m.f1225g, DeviceUtil.f());
            map.put(com.xiaomi.onetrack.b.m.f1226h, com.ot.pubsub.util.m.o());
            map.put("platform", "Android");
            String strG = y.l.c().g();
            String strD = b0.a.d(strG, map, true);
            com.ot.pubsub.util.k.c("CommonConfigUpdater", "url:" + strG + " response:" + strD);
            b(strD);
        } catch (IOException e2) {
            com.ot.pubsub.util.k.c("CommonConfigUpdater", "requestCloudData: " + e2.toString());
        }
    }

    private static HashMap<Integer, Integer> g() {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1, 1000);
        map.put(2, 15000);
        map.put(3, Integer.valueOf(Constants.MAX_VALID_TIME_FOR_REGISTRATION_REQUEST));
        return map;
    }
}
