package t;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private f f2346a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private ConcurrentHashMap<String, j> f2347b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private ConcurrentHashMap<String, Boolean> f2348c;

    private static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final g f2349a = new g(null);
    }

    /* synthetic */ g(h hVar) {
        this();
    }

    public static g e() {
        return a.f2349a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int j(JSONObject jSONObject) {
        try {
            int iOptInt = jSONObject.optInt(com.xiaomi.onetrack.b.a.f1159e, 100);
            if (iOptInt < 0 || iOptInt > 100) {
                return 100;
            }
            return iOptInt;
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("ConfigDbManager", "getCommonSample Exception:" + e2.getMessage());
            return 100;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void m(ArrayList<j> arrayList) throws Throwable {
        StringBuilder sb;
        SQLiteDatabase sQLiteDatabase = null;
        try {
            try {
                SQLiteDatabase writableDatabase = this.f2346a.getWritableDatabase();
                try {
                    writableDatabase.beginTransaction();
                    for (j jVar : arrayList) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("app_id", jVar.f2354a);
                        contentValues.put("timestamp", Long.valueOf(jVar.f2356c));
                        JSONObject jSONObject = jVar.f2358e;
                        if (jSONObject != null) {
                            contentValues.put(com.xiaomi.onetrack.b.g.f1196e, jSONObject.toString());
                        }
                        String str = jVar.f2357d;
                        if (str != null) {
                            contentValues.put(com.xiaomi.onetrack.b.g.f1197f, str);
                        }
                        if (DatabaseUtils.queryNumEntries(writableDatabase, com.xiaomi.onetrack.b.g.f1193b, "app_id=?", new String[]{jVar.f2354a}) > 0) {
                            com.ot.pubsub.util.k.c("ConfigDbManager", "database updated, row: " + writableDatabase.update(com.xiaomi.onetrack.b.g.f1193b, contentValues, "app_id=?", new String[]{jVar.f2354a}));
                        } else {
                            com.ot.pubsub.util.k.c("ConfigDbManager", "database inserted, row: " + writableDatabase.insert(com.xiaomi.onetrack.b.g.f1193b, null, contentValues));
                        }
                        this.f2348c.put(jVar.f2354a, Boolean.TRUE);
                    }
                    writableDatabase.setTransactionSuccessful();
                    try {
                        writableDatabase.endTransaction();
                    } catch (Exception e2) {
                        e = e2;
                        sb = new StringBuilder();
                        sb.append("Exception while endTransaction:");
                        sb.append(e);
                        com.ot.pubsub.util.k.h("ConfigDbManager", sb.toString());
                    }
                } catch (Exception e3) {
                    e = e3;
                    sQLiteDatabase = writableDatabase;
                    com.ot.pubsub.util.k.i("ConfigDbManager", "updateToDb error: ", e);
                    if (sQLiteDatabase != null) {
                        try {
                            sQLiteDatabase.endTransaction();
                        } catch (Exception e4) {
                            e = e4;
                            sb = new StringBuilder();
                            sb.append("Exception while endTransaction:");
                            sb.append(e);
                            com.ot.pubsub.util.k.h("ConfigDbManager", sb.toString());
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    sQLiteDatabase = writableDatabase;
                    if (sQLiteDatabase != null) {
                        try {
                            sQLiteDatabase.endTransaction();
                        } catch (Exception e5) {
                            com.ot.pubsub.util.k.h("ConfigDbManager", "Exception while endTransaction:" + e5);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e6) {
            e = e6;
        }
    }

    private JSONObject o(String str, String str2) {
        JSONObject jSONObject;
        JSONArray jSONArrayOptJSONArray;
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                if (this.f2347b.get(str) == null || (this.f2348c.containsKey(str) && this.f2348c.get(str).booleanValue())) {
                    l(str);
                }
                j jVar = this.f2347b.get(str);
                if (jVar != null && (jSONObject = jVar.f2358e) != null && (jSONArrayOptJSONArray = jSONObject.optJSONArray("events")) != null) {
                    for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                        JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i2);
                        if (TextUtils.equals(str2, jSONObject2.optString("event"))) {
                            if (com.ot.pubsub.util.k.f724a) {
                                com.ot.pubsub.util.k.c("ConfigDbManager", "getEventConfig:" + jSONObject2.toString());
                            }
                            return jSONObject2;
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e("ConfigDbManager", "getEventConfig error: " + e2.toString());
            }
        }
        return null;
    }

    public int a(String str, String str2, String str3, int i2) {
        try {
            JSONObject jSONObjectO = o(str, str2);
            if (jSONObjectO != null) {
                return jSONObjectO.getInt(str3);
            }
            com.ot.pubsub.util.k.c("ConfigDbManager", "config not available, use default value");
            return i2;
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("ConfigDbManager", "getInt: " + e2.toString());
            return i2;
        }
    }

    public String c(String str, String str2, String str3, String str4) {
        try {
            JSONObject jSONObjectO = o(str, str2);
            if (jSONObjectO != null) {
                return jSONObjectO.getString(str3);
            }
            com.ot.pubsub.util.k.c("ConfigDbManager", "config not available, use default value");
            return str4;
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("ConfigDbManager", "getString: " + e2.toString());
            return str4;
        }
    }

    public void f(ArrayList<j> arrayList) {
        y.a.b(new h(this, arrayList));
    }

    public boolean h(String str, String str2) {
        JSONObject jSONObject;
        try {
            j jVarR = r(str);
            if (jVarR == null || (jSONObject = jVarR.f2358e) == null || !jSONObject.has(str2)) {
                return false;
            }
            return jVarR.f2358e.optBoolean(str2);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("ConfigDbManager", "getAppLevelBoolean" + e2.toString());
            return false;
        }
    }

    public boolean i(String str, String str2, String str3, boolean z2) {
        try {
            JSONObject jSONObjectO = o(str, str2);
            if (jSONObjectO != null) {
                return jSONObjectO.getBoolean(str3);
            }
            com.ot.pubsub.util.k.c("ConfigDbManager", "config not available, use default value");
            return z2;
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("ConfigDbManager", "getBoolean: " + e2.toString());
            return z2;
        }
    }

    public long k(String str, String str2) {
        j jVar;
        if (TextUtils.isEmpty(str)) {
            return 100L;
        }
        try {
            if (this.f2347b.get(str) == null) {
                l(str);
            }
            if (this.f2347b.get(str) != null) {
                int iA = a(str, str2, com.xiaomi.onetrack.b.a.f1159e, -1);
                if (iA != -1 || (jVar = this.f2347b.get(str)) == null) {
                    com.ot.pubsub.util.k.c("ConfigDbManager", "will return event sample " + iA);
                    return iA;
                }
                com.ot.pubsub.util.k.c("ConfigDbManager", "will return common sample " + jVar.f2355b);
                return jVar.f2355b;
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("ConfigDbManager", "getAppEventSample" + e2.toString());
        }
        com.ot.pubsub.util.k.c("ConfigDbManager", "will return def sample");
        return 100L;
    }

    public void l(String str) {
        FutureTask futureTask = new FutureTask(new i(this, str));
        y.a.b(futureTask);
        try {
            j jVar = (j) futureTask.get(5L, TimeUnit.SECONDS);
            if (jVar != null) {
                this.f2347b.put(str, jVar);
                this.f2348c.put(str, Boolean.FALSE);
                if (com.ot.pubsub.util.k.f724a) {
                    com.ot.pubsub.util.k.c("ConfigDbManager", "getConfig   appId :" + str + " config: " + jVar.toString());
                }
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("ConfigDbManager", "getConfig error: " + e2.toString());
        }
    }

    public String n(String str) {
        JSONObject jSONObject;
        j jVarR = r(str);
        return (jVarR == null || (jSONObject = jVarR.f2358e) == null) ? "" : jSONObject.optString(com.xiaomi.onetrack.b.a.f1161g);
    }

    public String p(String str) {
        j jVarR = r(str);
        return jVarR != null ? jVarR.f2357d : "";
    }

    public int q(String str) {
        JSONObject jSONObject;
        j jVarR = r(str);
        if (jVarR == null || (jSONObject = jVarR.f2358e) == null) {
            return 0;
        }
        return jSONObject.optInt("version");
    }

    public j r(String str) {
        com.ot.pubsub.util.k.c("ConfigDbManager", "getAppConfigData start, appId: " + str);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            if (this.f2347b.get(str) == null || (this.f2348c.containsKey(str) && this.f2348c.get(str).booleanValue())) {
                l(str);
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("ConfigDbManager", "getConfig error: " + e2.getMessage());
        }
        return this.f2347b.get(str);
    }

    private g() {
        this.f2347b = new ConcurrentHashMap<>();
        this.f2348c = new ConcurrentHashMap<>();
        this.f2346a = new f(com.ot.pubsub.util.b.a());
    }
}
