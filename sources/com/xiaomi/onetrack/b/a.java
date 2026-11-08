package com.xiaomi.onetrack.b;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.xiaomi.onetrack.BuildConfig;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.x;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f1155a = "disable_log";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f1156b = "event";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final String f1157c = "events";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final String f1158d = "level";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final String f1159e = "sample";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final String f1160f = "needIds";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final String f1161g = "bannedParams";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final String f1162h = "version";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static final String f1163i = "AppConfigUpdater";

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final long f1164j = 172800000;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static final String f1165k = "hash";

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static final String f1166l = "appId";

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static final String f1167m = "apps";

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static final String f1168n = "type";

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static final String f1169o = "status";

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private static final String f1170p = "deleted";

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private static final String f1171q = "Android";

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private static final int f1173s = 0;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private static final int f1174t = 1;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private static final int f1175u = 2;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private static final int f1176v = 100;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private static final long f1178x = 1800000;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private static final int f1180z = 0;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private static AtomicBoolean f1172r = new AtomicBoolean(false);

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private static ConcurrentHashMap<String, Long> f1177w = new ConcurrentHashMap<>();

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private static HandlerC0011a f1179y = new HandlerC0011a(Looper.getMainLooper(), null);
    private static ConcurrentHashMap<String, Boolean> A = new ConcurrentHashMap<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: com.xiaomi.onetrack.b.a$a, reason: collision with other inner class name */
    static class HandlerC0011a extends Handler {
        /* synthetic */ HandlerC0011a(Looper looper, b bVar) {
            this(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            p.a(a.f1163i, "ScheduleCloudHandler.handleMessage, msg.what=" + message.what);
            if (message.what == 0) {
                Object obj = message.obj;
                if (obj == null) {
                    p.a(a.f1163i, "ScheduleCloudHandler.handleMessage, msg.obj is null");
                    return;
                }
                try {
                    String str = (String) obj;
                    p.a(a.f1163i, "ScheduleCloudHandler.handleMessage, appId: " + str);
                    com.xiaomi.onetrack.util.i.a(new d(this, str));
                } catch (Exception e2) {
                    p.b(a.f1163i, "handleMessage error: " + e2.getMessage());
                }
            }
        }

        private HandlerC0011a(Looper looper) {
            super(looper);
        }
    }

    private static boolean d(String str) {
        if (!com.xiaomi.onetrack.g.c.a()) {
            p.a(f1163i, "net is not connected!");
            return false;
        }
        l lVarF = h.a().f(str);
        if (lVarF == null) {
            return true;
        }
        long j2 = lVarF.f1216c;
        return j2 < System.currentTimeMillis() || j2 - System.currentTimeMillis() > f1164j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean e(String str) {
        Long l2 = f1177w.get(str);
        return l2 == null || l2.longValue() - System.currentTimeMillis() < 0 || l2.longValue() - System.currentTimeMillis() > 1800000;
    }

    public static void b(String str) {
        if (!d(str)) {
            p.a(f1163i, "AppConfigUpdater Does not meet prerequisites for request");
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        b(arrayList);
    }

    private static String c(List<String> list) {
        JSONArray jSONArray = new JSONArray();
        try {
            JSONObject jSONObject = new JSONObject();
            for (int i2 = 0; i2 < list.size(); i2++) {
                String str = list.get(i2);
                jSONObject.put("appId", str);
                jSONObject.put("hash", h.a().d(str));
                jSONArray.put(jSONObject);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return jSONArray.toString();
    }

    public static void a(String str) {
        com.xiaomi.onetrack.util.i.a(new b(str));
    }

    public static void a(String str, List<String> list) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.optInt("code") == 0) {
                d(list);
                a(jSONObject.optJSONObject("data").optJSONArray("apps"), list);
            }
        } catch (Exception e2) {
            p.a(f1163i, "saveAppCloudData: " + e2.toString());
        }
    }

    private static void d(List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            f1177w.put(list.get(i2), Long.valueOf(System.currentTimeMillis() + 1800000));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void b(List<String> list) {
        p.a(f1163i, "pullCloudData start, appIds: " + list.toString());
        if (q.a(f1163i)) {
            return;
        }
        if (f1172r.compareAndSet(false, true)) {
            HashMap map = new HashMap();
            try {
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
                    map.put(m.f1227i, c(list));
                    map.put(m.f1228j, com.xiaomi.onetrack.f.a.e());
                    map.put("platform", f1171q);
                    map.put(m.f1232n, "1");
                    String strC = x.a().c();
                    p.a(f1163i, "pullData:" + strC);
                    String strB = com.xiaomi.onetrack.g.b.b(strC, map, true);
                    p.a(f1163i, "response:" + strB);
                    a(strB, list);
                } catch (Exception e2) {
                    p.b(f1163i, "pullCloudData error: " + e2.getMessage());
                }
            } finally {
                f1172r.set(false);
            }
        }
    }

    private static void a(JSONArray jSONArray, List<String> list) throws JSONException {
        p.a(f1163i, "updateDataToDb start");
        long jCurrentTimeMillis = System.currentTimeMillis() + 86400000 + ((long) new Random().nextInt(86400000));
        if (jSONArray != null && jSONArray.length() > 0) {
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i2);
                String strOptString = jSONObjectOptJSONObject == null ? "" : jSONObjectOptJSONObject.optString("appId");
                p.a(f1163i, "appId: " + strOptString);
                if (!TextUtils.isEmpty(strOptString)) {
                    arrayList.add(strOptString);
                    a(strOptString, jSONObjectOptJSONObject, jCurrentTimeMillis);
                }
            }
            a(list, jCurrentTimeMillis, arrayList);
            return;
        }
        a(list, jCurrentTimeMillis);
    }

    private static void a(String str, JSONObject jSONObject, long j2) throws JSONException {
        int iOptInt = jSONObject == null ? 0 : jSONObject.optInt("version");
        int iE = h.a().e(str);
        p.a(f1163i, "local version: " + iE + ", server version: " + iOptInt);
        if (iE > 0 && iOptInt <= iE) {
            a(jSONObject, j2);
            return;
        }
        int iOptInt2 = jSONObject != null ? jSONObject.optInt("type") : -1;
        p.a(f1163i, "type: " + iOptInt2);
        if (iOptInt2 == 0 || iOptInt2 == 1) {
            a(jSONObject, j2, iOptInt);
        } else if (iOptInt2 == 2) {
            b(jSONObject, j2);
        } else {
            p.a(f1163i, "handleData do nothing!");
        }
    }

    private static void a(JSONObject jSONObject, long j2) {
        ArrayList<l> arrayList = new ArrayList<>();
        if (jSONObject != null) {
            l lVar = new l();
            lVar.f1214a = jSONObject.optString("appId");
            lVar.f1216c = j2;
            arrayList.add(lVar);
        }
        if (!arrayList.isEmpty()) {
            h.a().a(arrayList);
        } else {
            p.a(f1163i, "updateMinVersionData no timestamp can be updated!");
        }
    }

    private static void b(JSONObject jSONObject, long j2) {
        ArrayList<l> arrayList = new ArrayList<>();
        if (jSONObject == null || !jSONObject.has("events")) {
            p.a(f1163i, "handleIncrementalUpdate config is not change!");
        } else {
            l lVar = new l();
            lVar.f1217d = jSONObject.optString("hash");
            String strOptString = jSONObject.optString("appId");
            lVar.f1214a = strOptString;
            lVar.f1215b = b(jSONObject);
            lVar.f1216c = j2;
            lVar.f1218e = a(strOptString, jSONObject);
            arrayList.add(lVar);
        }
        if (arrayList.isEmpty()) {
            p.a(f1163i, "handleIncrementalUpdate no configuration can be updated!");
        } else {
            h.a().a(arrayList);
        }
    }

    private static void a(JSONObject jSONObject, long j2, int i2) throws JSONException {
        l lVarF;
        JSONObject jSONObject2;
        ArrayList<l> arrayList = new ArrayList<>();
        if (jSONObject != null) {
            l lVar = new l();
            lVar.f1217d = jSONObject.optString("hash");
            lVar.f1214a = jSONObject.optString("appId");
            lVar.f1215b = b(jSONObject);
            lVar.f1216c = j2;
            if (!jSONObject.has("events") && (lVarF = h.a().f(lVar.f1214a)) != null && (jSONObject2 = lVarF.f1218e) != null && jSONObject2.optJSONArray("events") != null) {
                jSONObject.put("events", lVarF.f1218e.optJSONArray("events"));
            }
            lVar.f1218e = jSONObject;
            arrayList.add(lVar);
        }
        if (!arrayList.isEmpty()) {
            h.a().a(arrayList);
        } else {
            p.a(f1163i, "handleFullOrNoNewData no configuration can be updated!");
        }
    }

    private static int b(JSONObject jSONObject) {
        try {
            int iOptInt = jSONObject.optInt(f1159e, 100);
            if (iOptInt < 0 || iOptInt > 100) {
                return 100;
            }
            return iOptInt;
        } catch (Exception e2) {
            p.a(f1163i, "getCommonSample Exception:" + e2.getMessage());
            return 100;
        }
    }

    private static JSONObject a(String str, JSONObject jSONObject) {
        try {
            l lVarF = h.a().f(str);
            jSONObject.put("events", a(lVarF != null ? lVarF.f1218e.optJSONArray("events") : null, jSONObject.optJSONArray("events")));
            return jSONObject;
        } catch (Exception e2) {
            p.b(f1163i, "mergeConfig: " + e2.toString());
            return null;
        }
    }

    private static JSONArray a(JSONArray jSONArray, JSONArray jSONArray2) {
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
                if (!jSONObjectOptJSONObject.has("status") || (jSONObjectOptJSONObject.has("status") && !TextUtils.equals(jSONObjectOptJSONObject.optString("status"), "deleted"))) {
                    if (jSONArray == null) {
                        jSONArray = new JSONArray();
                    }
                    jSONArray.put(jSONObjectOptJSONObject);
                }
                i2++;
            } catch (Exception e2) {
                p.b(f1163i, "mergeEventsElement error:" + e2.toString());
            }
        }
        return jSONArray;
    }

    private static void a(List<String> list, long j2, List<String> list2) {
        try {
            if (list.size() != list2.size()) {
                list.removeAll(list2);
                a(list, j2);
            }
        } catch (Exception e2) {
            p.b(f1163i, "handleInvalidAppIds error:" + e2.toString());
        }
    }

    private static void a(List<String> list, long j2) {
        try {
            ArrayList<l> arrayList = new ArrayList<>();
            for (int i2 = 0; i2 < list.size(); i2++) {
                l lVar = new l();
                lVar.f1214a = list.get(i2);
                lVar.f1215b = 100L;
                lVar.f1216c = j2;
                arrayList.add(lVar);
            }
            h.a().a(arrayList);
        } catch (Exception e2) {
            p.b(f1163i, "handleError" + e2.toString());
        }
    }

    public static void a(JSONObject jSONObject) {
        com.xiaomi.onetrack.util.i.a(new c(jSONObject));
    }
}
