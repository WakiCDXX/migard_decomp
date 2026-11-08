package t;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.ot.pubsub.util.DeviceUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.provider.Wifi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static AtomicBoolean f2338a = new AtomicBoolean(false);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static ConcurrentHashMap<String, Long> f2339b = new ConcurrentHashMap<>();

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static HandlerC0027a f2340c = new HandlerC0027a(Looper.getMainLooper(), null);

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static ConcurrentHashMap<String, Boolean> f2341d = new ConcurrentHashMap<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: t.a$a, reason: collision with other inner class name */
    static class HandlerC0027a extends Handler {
        /* synthetic */ HandlerC0027a(Looper looper, b bVar) {
            this(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "ScheduleCloudHandler.handleMessage, msg.what=" + message.what);
            if (message.what == 0) {
                Object obj = message.obj;
                if (obj == null) {
                    com.ot.pubsub.util.k.c("AppConfigUpdater", "ScheduleCloudHandler.handleMessage, msg.obj is null");
                    return;
                }
                try {
                    String str = (String) obj;
                    com.ot.pubsub.util.k.c("AppConfigUpdater", "ScheduleCloudHandler.handleMessage, appId: " + str);
                    com.ot.pubsub.util.e.a(new c(this, str));
                } catch (Exception e2) {
                    com.ot.pubsub.util.k.h("AppConfigUpdater", "handleMessage error: " + e2.getMessage());
                }
            }
        }

        private HandlerC0027a(Looper looper) {
            super(looper);
        }
    }

    private static JSONArray b(JSONArray jSONArray, JSONArray jSONArray2) {
        int i2 = 0;
        while (jSONArray2 != null) {
            try {
                if (i2 >= jSONArray2.length()) {
                    break;
                }
                JSONObject jSONObjectOptJSONObject = jSONArray2.optJSONObject(i2);
                String strOptString = jSONObjectOptJSONObject.optString("event");
                for (int i3 = 0; jSONArray != null && i3 < jSONArray.length(); i3++) {
                    if (TextUtils.equals(strOptString, jSONArray.optJSONObject(i3).optString("event"))) {
                        jSONArray.remove(i3);
                        break;
                    }
                }
                if (!jSONObjectOptJSONObject.has("status") || (jSONObjectOptJSONObject.has("status") && !TextUtils.equals(jSONObjectOptJSONObject.optString("status"), Wifi.DELETED))) {
                    if (jSONArray == null) {
                        jSONArray = new JSONArray();
                    }
                    jSONArray.put(jSONObjectOptJSONObject);
                }
                i2++;
            } catch (Exception e2) {
                com.ot.pubsub.util.k.h("AppConfigUpdater", "mergeEventsElement error:" + e2.toString());
            }
        }
        return jSONArray;
    }

    private static JSONObject c(String str, JSONObject jSONObject) {
        try {
            j jVarR = g.e().r(str);
            jSONObject.put("events", b(jVarR != null ? jVarR.f2358e.optJSONArray("events") : null, jSONObject.optJSONArray("events")));
            return jSONObject;
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("AppConfigUpdater", "mergeConfig: " + e2.toString());
            return null;
        }
    }

    public static void d(String str) {
        com.ot.pubsub.util.e.a(new b(str));
    }

    public static void e(String str, List<String> list) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.optInt("code") == 0) {
                r(list);
                j(jSONObject.optJSONObject("data").optJSONArray("apps"), list);
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "saveAppCloudData: " + e2.toString());
        }
    }

    private static void f(String str, JSONObject jSONObject, long j2) throws JSONException {
        int iOptInt = jSONObject == null ? 0 : jSONObject.optInt("version");
        int iQ = g.e().q(str);
        com.ot.pubsub.util.k.c("AppConfigUpdater", "local version: " + iQ + ", server version: " + iOptInt);
        if (iQ > 0 && iOptInt <= iQ) {
            k(jSONObject, j2);
            return;
        }
        int iOptInt2 = jSONObject != null ? jSONObject.optInt("type") : -1;
        com.ot.pubsub.util.k.c("AppConfigUpdater", "type: " + iOptInt2);
        if (iOptInt2 == 0 || iOptInt2 == 1) {
            l(jSONObject, j2, iOptInt);
        } else if (iOptInt2 == 2) {
            p(jSONObject, j2);
        } else {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "handleData do nothing!");
        }
    }

    private static void h(List<String> list, long j2) {
        try {
            ArrayList<j> arrayList = new ArrayList<>();
            for (int i2 = 0; i2 < list.size(); i2++) {
                j jVar = new j();
                jVar.f2354a = list.get(i2);
                jVar.f2355b = 100L;
                jVar.f2356c = j2;
                arrayList.add(jVar);
            }
            g.e().f(arrayList);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("AppConfigUpdater", "handleError" + e2.toString());
        }
    }

    private static void i(List<String> list, long j2, List<String> list2) {
        try {
            if (list.size() != list2.size()) {
                list.removeAll(list2);
                h(list, j2);
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("AppConfigUpdater", "handleInvalidAppIds error:" + e2.toString());
        }
    }

    private static void j(JSONArray jSONArray, List<String> list) throws JSONException {
        com.ot.pubsub.util.k.c("AppConfigUpdater", "updateDataToDb start");
        long jCurrentTimeMillis = System.currentTimeMillis() + 86400000 + ((long) new Random().nextInt(86400000));
        if (jSONArray == null || jSONArray.length() <= 0) {
            h(list, jCurrentTimeMillis);
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i2);
            String strOptString = jSONObjectOptJSONObject == null ? "" : jSONObjectOptJSONObject.optString("appId");
            com.ot.pubsub.util.k.c("AppConfigUpdater", "appId: " + strOptString);
            if (!TextUtils.isEmpty(strOptString)) {
                arrayList.add(strOptString);
                f(strOptString, jSONObjectOptJSONObject, jCurrentTimeMillis);
            }
        }
        i(list, jCurrentTimeMillis, arrayList);
    }

    private static void k(JSONObject jSONObject, long j2) {
        ArrayList<j> arrayList = new ArrayList<>();
        if (jSONObject != null) {
            j jVar = new j();
            jVar.f2354a = jSONObject.optString("appId");
            jVar.f2356c = j2;
            arrayList.add(jVar);
        }
        if (arrayList.isEmpty()) {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "updateMinVersionData no timestamp can be updated!");
        } else {
            g.e().f(arrayList);
        }
    }

    private static void l(JSONObject jSONObject, long j2, int i2) throws JSONException {
        j jVarR;
        JSONObject jSONObject2;
        ArrayList<j> arrayList = new ArrayList<>();
        if (jSONObject != null) {
            j jVar = new j();
            jVar.f2357d = jSONObject.optString(com.xiaomi.onetrack.g.a.f1425e);
            jVar.f2354a = jSONObject.optString("appId");
            jVar.f2355b = m(jSONObject);
            jVar.f2356c = j2;
            if (!jSONObject.has("events") && (jVarR = g.e().r(jVar.f2354a)) != null && (jSONObject2 = jVarR.f2358e) != null && jSONObject2.optJSONArray("events") != null) {
                jSONObject.put("events", jVarR.f2358e.optJSONArray("events"));
            }
            jVar.f2358e = jSONObject;
            arrayList.add(jVar);
        }
        if (arrayList.isEmpty()) {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "handleFullOrNoNewData no configuration can be updated!");
        } else {
            g.e().f(arrayList);
        }
    }

    private static int m(JSONObject jSONObject) {
        try {
            int iOptInt = jSONObject.optInt(com.xiaomi.onetrack.b.a.f1159e, 100);
            if (iOptInt < 0 || iOptInt > 100) {
                return 100;
            }
            return iOptInt;
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "getCommonSample Exception:" + e2.getMessage());
            return 100;
        }
    }

    public static void n(String str) {
        if (!s(str)) {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "AppConfigUpdater Does not meet prerequisites for request");
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        o(arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void o(List<String> list) {
        com.ot.pubsub.util.k.c("AppConfigUpdater", "pullCloudData start, appIds: " + list.toString());
        if (com.ot.pubsub.util.m.i("AppConfigUpdater")) {
            return;
        }
        if (f2338a.compareAndSet(false, true)) {
            HashMap map = new HashMap();
            try {
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
                    map.put(com.xiaomi.onetrack.b.m.f1227i, q(list));
                    map.put(com.xiaomi.onetrack.b.m.f1228j, com.ot.pubsub.util.b.e());
                    map.put("platform", "Android");
                    map.put(com.xiaomi.onetrack.b.m.f1232n, "1");
                    String strH = y.l.c().h();
                    com.ot.pubsub.util.k.c("AppConfigUpdater", "pullData:" + strH);
                    String strD = b0.a.d(strH, map, true);
                    com.ot.pubsub.util.k.c("AppConfigUpdater", "response:" + strD);
                    e(strD, list);
                } catch (Exception e2) {
                    com.ot.pubsub.util.k.h("AppConfigUpdater", "pullCloudData error: " + e2.getMessage());
                }
            } finally {
                f2338a.set(false);
            }
        }
    }

    private static void p(JSONObject jSONObject, long j2) {
        ArrayList<j> arrayList = new ArrayList<>();
        if (jSONObject == null || !jSONObject.has("events")) {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "handleIncrementalUpdate config is not change!");
        } else {
            j jVar = new j();
            jVar.f2357d = jSONObject.optString(com.xiaomi.onetrack.g.a.f1425e);
            String strOptString = jSONObject.optString("appId");
            jVar.f2354a = strOptString;
            jVar.f2355b = m(jSONObject);
            jVar.f2356c = j2;
            jVar.f2358e = c(strOptString, jSONObject);
            arrayList.add(jVar);
        }
        if (arrayList.isEmpty()) {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "handleIncrementalUpdate no configuration can be updated!");
        } else {
            g.e().f(arrayList);
        }
    }

    private static String q(List<String> list) {
        JSONArray jSONArray = new JSONArray();
        try {
            JSONObject jSONObject = new JSONObject();
            for (int i2 = 0; i2 < list.size(); i2++) {
                String str = list.get(i2);
                jSONObject.put("appId", str);
                jSONObject.put(com.xiaomi.onetrack.g.a.f1425e, g.e().p(str));
                jSONArray.put(jSONObject);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return jSONArray.toString();
    }

    private static void r(List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            f2339b.put(list.get(i2), Long.valueOf(System.currentTimeMillis() + 1800000));
        }
    }

    private static boolean s(String str) {
        if (!com.ot.pubsub.util.l.a()) {
            com.ot.pubsub.util.k.c("AppConfigUpdater", "net is not connected!");
            return false;
        }
        j jVarR = g.e().r(str);
        if (jVarR == null) {
            return true;
        }
        long j2 = jVarR.f2356c;
        return j2 < System.currentTimeMillis() || j2 - System.currentTimeMillis() > 172800000;
    }
}
