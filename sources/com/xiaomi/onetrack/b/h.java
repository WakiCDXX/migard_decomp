package com.xiaomi.onetrack.b;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.onetrack.util.p;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1202a = "ConfigDbManager";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final int f1203e = 100;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private g f1204b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private ConcurrentHashMap<String, l> f1205c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private ConcurrentHashMap<String, Boolean> f1206d;

    private static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final h f1207a = new h(null);

        private a() {
        }
    }

    /* synthetic */ h(i iVar) {
        this();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(ArrayList<l> arrayList) throws Throwable {
        StringBuilder sb;
        SQLiteDatabase sQLiteDatabase = null;
        try {
            try {
                SQLiteDatabase writableDatabase = this.f1204b.getWritableDatabase();
                try {
                    writableDatabase.beginTransaction();
                    for (l lVar : arrayList) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("app_id", lVar.f1214a);
                        contentValues.put("timestamp", Long.valueOf(lVar.f1216c));
                        JSONObject jSONObject = lVar.f1218e;
                        if (jSONObject != null) {
                            contentValues.put(g.f1196e, jSONObject.toString());
                        }
                        String str = lVar.f1217d;
                        if (str != null) {
                            contentValues.put(g.f1197f, str);
                        }
                        if (DatabaseUtils.queryNumEntries(writableDatabase, g.f1193b, "app_id=?", new String[]{lVar.f1214a}) > 0) {
                            p.a(f1202a, "database updated, row: " + writableDatabase.update(g.f1193b, contentValues, "app_id=?", new String[]{lVar.f1214a}));
                        } else {
                            p.a(f1202a, "database inserted, row: " + writableDatabase.insert(g.f1193b, null, contentValues));
                        }
                        this.f1206d.put(lVar.f1214a, Boolean.TRUE);
                    }
                    writableDatabase.setTransactionSuccessful();
                    try {
                        writableDatabase.endTransaction();
                    } catch (Exception e2) {
                        e = e2;
                        sb = new StringBuilder();
                        sb.append("Exception while endTransaction:");
                        sb.append(e);
                        p.b(f1202a, sb.toString());
                    }
                } catch (Exception e3) {
                    e = e3;
                    sQLiteDatabase = writableDatabase;
                    p.b(f1202a, "updateToDb error: ", e);
                    if (sQLiteDatabase != null) {
                        try {
                            sQLiteDatabase.endTransaction();
                        } catch (Exception e4) {
                            e = e4;
                            sb = new StringBuilder();
                            sb.append("Exception while endTransaction:");
                            sb.append(e);
                            p.b(f1202a, sb.toString());
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    sQLiteDatabase = writableDatabase;
                    if (sQLiteDatabase != null) {
                        try {
                            sQLiteDatabase.endTransaction();
                        } catch (Exception e5) {
                            p.b(f1202a, "Exception while endTransaction:" + e5);
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

    private JSONObject c(String str, String str2) {
        JSONObject jSONObject;
        JSONArray jSONArrayOptJSONArray;
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                if (this.f1205c.get(str) == null || (this.f1206d.containsKey(str) && this.f1206d.get(str).booleanValue())) {
                    b(str);
                }
                l lVar = this.f1205c.get(str);
                if (lVar != null && (jSONObject = lVar.f1218e) != null && (jSONArrayOptJSONArray = jSONObject.optJSONArray("events")) != null) {
                    for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                        JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i2);
                        if (TextUtils.equals(str2, jSONObject2.optString("event"))) {
                            if (p.f1665a) {
                                p.a(f1202a, "getEventConfig:" + jSONObject2.toString());
                            }
                            return jSONObject2;
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e(f1202a, "getEventConfig error: " + e2.toString());
            }
        }
        return null;
    }

    public String d(String str) {
        l lVarF = f(str);
        return lVarF != null ? lVarF.f1217d : "";
    }

    public int e(String str) {
        JSONObject jSONObject;
        l lVarF = f(str);
        if (lVarF == null || (jSONObject = lVarF.f1218e) == null) {
            return 0;
        }
        return jSONObject.optInt("version");
    }

    public l f(String str) {
        p.a(f1202a, "getAppConfigData start, appId: " + str);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        p.a(f1202a, "mUpdated: " + this.f1206d + ",ruleDataMap.get(appId): " + this.f1205c.get(str));
        try {
            if (this.f1205c.get(str) == null || (this.f1206d.containsKey(str) && this.f1206d.get(str).booleanValue())) {
                b(str);
            }
        } catch (Exception e2) {
            p.b(f1202a, "getConfig error: " + e2.getMessage());
        }
        return this.f1205c.get(str);
    }

    private h() {
        this.f1205c = new ConcurrentHashMap<>();
        this.f1206d = new ConcurrentHashMap<>();
        this.f1204b = new g(com.xiaomi.onetrack.f.a.a());
    }

    public static h a() {
        return a.f1207a;
    }

    public void a(ArrayList<l> arrayList) {
        com.xiaomi.onetrack.c.b.a(new i(this, arrayList));
    }

    public void a(String str) {
        FutureTask futureTask = new FutureTask(new j(this, str), null);
        com.xiaomi.onetrack.c.b.a(futureTask);
        try {
            futureTask.get();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public boolean a(String str, String str2, String str3, boolean z2) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(f1202a, "config not available, use default value");
                return z2;
            }
            return jSONObjectC.getBoolean(str3);
        } catch (Exception e2) {
            p.b(f1202a, "getBoolean: " + e2.toString());
            return z2;
        }
    }

    public String c(String str) {
        JSONObject jSONObject;
        l lVarF = f(str);
        if (lVarF != null && (jSONObject = lVarF.f1218e) != null) {
            return jSONObject.optString(com.xiaomi.onetrack.b.a.f1161g);
        }
        return "";
    }

    public String a(String str, String str2, String str3, String str4) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(f1202a, "config not available, use default value");
                return str4;
            }
            return jSONObjectC.getString(str3);
        } catch (Exception e2) {
            p.b(f1202a, "getString: " + e2.toString());
            return str4;
        }
    }

    public int a(String str, String str2, String str3, int i2) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(f1202a, "config not available, use default value");
                return i2;
            }
            return jSONObjectC.getInt(str3);
        } catch (Exception e2) {
            p.b(f1202a, "getInt: " + e2.toString());
            return i2;
        }
    }

    public long a(String str, String str2, String str3, long j2) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(f1202a, "config not available, use default value");
                return j2;
            }
            return jSONObjectC.getLong(str3);
        } catch (Exception e2) {
            p.b(f1202a, "getLong: " + e2.toString());
            return j2;
        }
    }

    public double a(String str, String str2, String str3, double d2) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(f1202a, "config not available, use default value");
                return d2;
            }
            return jSONObjectC.getDouble(str3);
        } catch (Exception e2) {
            p.b(f1202a, "getDouble: " + e2.toString());
            return d2;
        }
    }

    public void b(String str) {
        FutureTask futureTask = new FutureTask(new k(this, str));
        com.xiaomi.onetrack.c.b.a(futureTask);
        try {
            l lVar = (l) futureTask.get(5L, TimeUnit.SECONDS);
            if (lVar != null) {
                this.f1205c.put(str, lVar);
                this.f1206d.put(str, Boolean.FALSE);
                if (p.f1665a) {
                    p.a(f1202a, "getConfig   appId :" + str + " config: " + lVar.toString());
                }
            }
        } catch (Exception e2) {
            p.b(f1202a, "getConfig error: " + e2.toString());
        }
    }

    public boolean a(String str, String str2) {
        JSONObject jSONObject;
        try {
            l lVarF = f(str);
            if (lVarF == null || (jSONObject = lVarF.f1218e) == null || !jSONObject.has(str2)) {
                return false;
            }
            return lVarF.f1218e.optBoolean(str2);
        } catch (Exception e2) {
            p.b(f1202a, "getAppLevelBoolean" + e2.toString());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int b(JSONObject jSONObject) {
        try {
            int iOptInt = jSONObject.optInt(com.xiaomi.onetrack.b.a.f1159e, 100);
            if (iOptInt < 0 || iOptInt > 100) {
                return 100;
            }
            return iOptInt;
        } catch (Exception e2) {
            p.a(f1202a, "getCommonSample Exception:" + e2.getMessage());
            return 100;
        }
    }

    public long b(String str, String str2) {
        l lVar;
        if (TextUtils.isEmpty(str)) {
            return 100L;
        }
        try {
            if (this.f1205c.get(str) == null) {
                b(str);
            }
            if (this.f1205c.get(str) != null) {
                int iA = a(str, str2, com.xiaomi.onetrack.b.a.f1159e, -1);
                if (iA == -1 && (lVar = this.f1205c.get(str)) != null) {
                    p.a(f1202a, "will return common sample " + lVar.f1215b);
                    return lVar.f1215b;
                }
                p.a(f1202a, "will return event sample " + iA);
                return iA;
            }
        } catch (Exception e2) {
            p.b(f1202a, "getAppEventSample" + e2.toString());
        }
        p.a(f1202a, "will return def sample");
        return 100L;
    }
}
