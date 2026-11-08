package com.xiaomi.onetrack.b;

import android.database.Cursor;
import android.text.TextUtils;
import com.xiaomi.onetrack.util.p;
import java.util.concurrent.Callable;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class k implements Callable<l> {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1212a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ h f1213b;

    k(h hVar, String str) {
        this.f1213b = hVar;
        this.f1212a = str;
    }

    /* JADX WARN: Code duplicated, block: B:36:0x0099 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // java.util.concurrent.Callable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public l call() throws Exception {
        Throwable th;
        Cursor cursorQuery;
        try {
            cursorQuery = this.f1213b.f1204b.getWritableDatabase().query(g.f1193b, null, "app_id=?", new String[]{this.f1212a}, null, null, null);
            try {
                try {
                    int columnIndex = cursorQuery.getColumnIndex("app_id");
                    int columnIndex2 = cursorQuery.getColumnIndex(g.f1196e);
                    int columnIndex3 = cursorQuery.getColumnIndex(g.f1197f);
                    int columnIndex4 = cursorQuery.getColumnIndex("timestamp");
                    if (cursorQuery.moveToNext()) {
                        l lVar = new l();
                        lVar.f1214a = cursorQuery.getString(columnIndex);
                        String string = cursorQuery.getString(columnIndex2);
                        if (!TextUtils.isEmpty(string)) {
                            lVar.f1218e = new JSONObject(string);
                        }
                        lVar.f1215b = h.b(lVar.f1218e);
                        lVar.f1217d = cursorQuery.getString(columnIndex3);
                        lVar.f1216c = cursorQuery.getLong(columnIndex4);
                        try {
                            cursorQuery.close();
                        } catch (Exception unused) {
                            p.a("ConfigDbManager", "getConfig  cursor.close");
                        }
                        return lVar;
                    }
                } catch (Exception e2) {
                    e = e2;
                    p.a("ConfigDbManager", e.getMessage());
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
                        p.a("ConfigDbManager", "getConfig  cursor.close");
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
            p.a("ConfigDbManager", "getConfig  cursor.close");
        }
        return null;
    }
}
