package t;

import android.database.Cursor;
import android.text.TextUtils;
import java.util.concurrent.Callable;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class i implements Callable<j> {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f2352a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ g f2353b;

    i(g gVar, String str) {
        this.f2353b = gVar;
        this.f2352a = str;
    }

    /* JADX WARN: Code duplicated, block: B:36:0x0099 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // java.util.concurrent.Callable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public j call() throws Exception {
        Throwable th;
        Cursor cursorQuery;
        try {
            cursorQuery = this.f2353b.f2346a.getWritableDatabase().query(com.xiaomi.onetrack.b.g.f1193b, null, "app_id=?", new String[]{this.f2352a}, null, null, null);
            try {
                try {
                    int columnIndex = cursorQuery.getColumnIndex("app_id");
                    int columnIndex2 = cursorQuery.getColumnIndex(com.xiaomi.onetrack.b.g.f1196e);
                    int columnIndex3 = cursorQuery.getColumnIndex(com.xiaomi.onetrack.b.g.f1197f);
                    int columnIndex4 = cursorQuery.getColumnIndex("timestamp");
                    if (cursorQuery.moveToNext()) {
                        j jVar = new j();
                        jVar.f2354a = cursorQuery.getString(columnIndex);
                        String string = cursorQuery.getString(columnIndex2);
                        if (!TextUtils.isEmpty(string)) {
                            jVar.f2358e = new JSONObject(string);
                        }
                        jVar.f2355b = g.j(jVar.f2358e);
                        jVar.f2357d = cursorQuery.getString(columnIndex3);
                        jVar.f2356c = cursorQuery.getLong(columnIndex4);
                        try {
                            cursorQuery.close();
                        } catch (Exception unused) {
                            com.ot.pubsub.util.k.c("ConfigDbManager", "getConfig  cursor.close");
                        }
                        return jVar;
                    }
                } catch (Exception e2) {
                    e = e2;
                    com.ot.pubsub.util.k.c("ConfigDbManager", e.getMessage());
                    if (cursorQuery != null) {
                    }
                    return null;
                }
            } catch (Throwable th2) {
                th = th2;
                if (cursorQuery != null) {
                    try {
                        cursorQuery.close();
                    } catch (Exception unused2) {
                        com.ot.pubsub.util.k.c("ConfigDbManager", "getConfig  cursor.close");
                    }
                }
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            cursorQuery = null;
        } catch (Throwable th3) {
            th = th3;
            cursorQuery = null;
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
        try {
            cursorQuery.close();
        } catch (Exception unused3) {
            com.ot.pubsub.util.k.c("ConfigDbManager", "getConfig  cursor.close");
        }
        return null;
    }
}
