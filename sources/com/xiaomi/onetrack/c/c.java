package com.xiaomi.onetrack.c;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1242a = "EventManager";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final boolean f1243b = false;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final int f1244c = 204800;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final int f1245d = 307200;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final int f1246e = 300;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f1247f = "priority ASC, _id ASC";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final int f1248g = 7;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static c f1249h;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static BroadcastReceiver f1250j = new d();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private a f1251i = new a(com.xiaomi.onetrack.f.a.a());

    private c() {
        b();
    }

    private static void b(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(f1250j, intentFilter);
    }

    private void d() {
        try {
            this.f1251i.getWritableDatabase().delete("events", null, null);
            com.xiaomi.onetrack.util.p.a(f1242a, "delete table events");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public long c() {
        return DatabaseUtils.queryNumEntries(this.f1251i.getReadableDatabase(), "events");
    }

    public static c a() {
        if (f1249h == null) {
            a(com.xiaomi.onetrack.f.a.b());
        }
        return f1249h;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(com.xiaomi.onetrack.f.b bVar) {
        synchronized (this.f1251i) {
            try {
                if (!bVar.i()) {
                    com.xiaomi.onetrack.util.p.c(f1242a, "addEventToDatabase event is inValid, event:" + bVar.e());
                    return;
                }
                SQLiteDatabase writableDatabase = this.f1251i.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("appid", bVar.c());
                contentValues.put("package", bVar.d());
                contentValues.put("event_name", bVar.e());
                contentValues.put("priority", Integer.valueOf(bVar.f()));
                contentValues.put("timestamp", Long.valueOf(System.currentTimeMillis()));
                byte[] bArrA = a(bVar.g().toString());
                if (bArrA.length > f1244c) {
                    com.xiaomi.onetrack.util.p.b(f1242a, "Too large data, discard ***");
                    return;
                }
                contentValues.put("data", bArrA);
                long jInsert = writableDatabase.insert("events", null, contentValues);
                com.xiaomi.onetrack.util.p.a(f1242a, "DB-Thread: EventManager.addEventToDatabase , row=" + jInsert);
                if (jInsert != -1) {
                    if (com.xiaomi.onetrack.util.p.f1665a) {
                        com.xiaomi.onetrack.util.p.a(f1242a, "添加后，DB 中事件个数为 " + c());
                    }
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    if (com.xiaomi.onetrack.util.a.f1487c.equals(bVar.e())) {
                        aa.a(jCurrentTimeMillis);
                    }
                    com.xiaomi.onetrack.b.n.a(false);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void a(Context context) {
        if (f1249h == null) {
            synchronized (c.class) {
                try {
                    if (f1249h == null) {
                        f1249h = new c();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        b(context);
    }

    public synchronized void a(com.xiaomi.onetrack.f.b bVar) {
        b.a(new f(this, bVar));
    }

    /* JADX WARN: Code duplicated, block: B:62:0x0129  */
    /* JADX WARN: Not initialized variable reg: 5, insn: 0x006e: MOVE (r3 I:??[OBJECT, ARRAY]) = (r5 I:??[OBJECT, ARRAY]), block:B:11:0x006e */
    public h a(int i2) {
        Cursor cursorQuery;
        Cursor cursor;
        boolean z2;
        boolean z3;
        Cursor cursor2 = null;
        try {
            try {
                cursorQuery = this.f1251i.getReadableDatabase().query("events", null, null, null, null, null, f1247f);
                try {
                    int columnIndex = cursorQuery.getColumnIndex("_id");
                    int columnIndex2 = cursorQuery.getColumnIndex("appid");
                    cursorQuery.getColumnIndex("package");
                    cursorQuery.getColumnIndex("event_name");
                    int columnIndex3 = cursorQuery.getColumnIndex("priority");
                    int columnIndex4 = cursorQuery.getColumnIndex("data");
                    int columnIndex5 = cursorQuery.getColumnIndex("timestamp");
                    JSONArray jSONArray = new JSONArray();
                    ArrayList arrayList = new ArrayList();
                    boolean z4 = true;
                    int i3 = 0;
                    int i4 = 0;
                    while (cursorQuery.moveToNext()) {
                        long j2 = cursorQuery.getLong(columnIndex);
                        cursorQuery.getLong(columnIndex5);
                        cursorQuery.getString(columnIndex2);
                        byte[] blob = cursorQuery.getBlob(columnIndex4);
                        String strA = blob != null ? a(blob) : null;
                        if (z4) {
                            int i5 = cursorQuery.getInt(columnIndex3);
                            if (i5 > i2) {
                                com.xiaomi.onetrack.util.p.a(f1242a, "No records of priority[" + i2 + "], first record priority=" + i5);
                                cursorQuery.close();
                                return null;
                            }
                            z3 = false;
                        } else {
                            z3 = z4;
                        }
                        try {
                            jSONArray.put(new JSONObject(strA));
                            arrayList.add(Long.valueOf(j2));
                            i3++;
                        } catch (Exception e2) {
                            com.xiaomi.onetrack.util.p.b(f1242a, "*** error ***", e2);
                        }
                        int length = i4 + strA.length();
                        if (length >= f1245d) {
                            com.xiaomi.onetrack.util.p.a(f1242a, "reached max len: " + length);
                            break;
                        }
                        i4 = length;
                        z4 = z3;
                    }
                    if (arrayList.size() > 0) {
                        if (cursorQuery.isAfterLast()) {
                            com.xiaomi.onetrack.util.p.a(f1242a, "cursor isAfterLast");
                        } else {
                            if (cursorQuery.getInt(columnIndex3) <= i2) {
                                z2 = false;
                            }
                            h hVar = new h(jSONArray, i3, arrayList, z2);
                            cursorQuery.close();
                            return hVar;
                        }
                        z2 = true;
                        h hVar2 = new h(jSONArray, i3, arrayList, z2);
                        cursorQuery.close();
                        return hVar2;
                    }
                } catch (SQLiteBlobTooBigException e3) {
                    e = e3;
                    com.xiaomi.onetrack.util.p.b(f1242a, "blob too big ***", e);
                    d();
                    if (cursorQuery != null) {
                    }
                    return null;
                } catch (Exception e4) {
                    e = e4;
                    com.xiaomi.onetrack.util.p.a(f1242a, "", e);
                    if (cursorQuery != null) {
                    }
                    return null;
                }
            } catch (Throwable th) {
                th = th;
                cursor2 = cursor;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (SQLiteBlobTooBigException e5) {
            e = e5;
            cursorQuery = null;
        } catch (Exception e6) {
            e = e6;
            cursorQuery = null;
        } catch (Throwable th2) {
            th = th2;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
        cursorQuery.close();
        return null;
    }

    public void b() {
        b.a(new g(this));
    }

    public int a(ArrayList<Long> arrayList) {
        synchronized (this.f1251i) {
            if (arrayList != null) {
                try {
                    if (arrayList.size() != 0) {
                        try {
                            SQLiteDatabase writableDatabase = this.f1251i.getWritableDatabase();
                            boolean z2 = true;
                            StringBuilder sb = new StringBuilder(((Long.toString(arrayList.get(0).longValue()).length() + 1) * arrayList.size()) + 16);
                            sb.append("_id");
                            sb.append(" in (");
                            sb.append(arrayList.get(0));
                            int size = arrayList.size();
                            for (int i2 = 1; i2 < size; i2++) {
                                sb.append(",");
                                sb.append(arrayList.get(i2));
                            }
                            sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
                            int iDelete = writableDatabase.delete("events", sb.toString(), null);
                            com.xiaomi.onetrack.util.p.a(f1242a, "deleted events count " + iDelete);
                            long jC = a().c();
                            if (jC != 0) {
                                z2 = false;
                            }
                            com.xiaomi.onetrack.b.n.a(z2);
                            com.xiaomi.onetrack.util.p.a(f1242a, "after delete DB record remains=" + jC);
                            return iDelete;
                        } catch (Exception e2) {
                            com.xiaomi.onetrack.util.p.b(f1242a, "e=" + e2);
                            return 0;
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            return 0;
        }
    }

    public static byte[] a(String str) {
        return com.xiaomi.onetrack.d.a.a(str.getBytes(), com.xiaomi.onetrack.d.d.a(com.xiaomi.onetrack.d.c.a(), true).getBytes());
    }

    public static String a(byte[] bArr) {
        return new String(com.xiaomi.onetrack.d.a.b(bArr, com.xiaomi.onetrack.d.d.a(com.xiaomi.onetrack.d.c.a(), true).getBytes()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class a extends SQLiteOpenHelper {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public static final String f1252a = "onetrack";

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public static final String f1253b = "events";

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public static final String f1254c = "_id";

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        public static final String f1255d = "appid";

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public static final String f1256e = "package";

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        public static final String f1257f = "event_name";

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        public static final String f1258g = "priority";

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        public static final String f1259h = "data";

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        public static final String f1260i = "timestamp";

        /* JADX INFO: renamed from: j, reason: collision with root package name */
        private static final int f1261j = 1;

        /* JADX INFO: renamed from: k, reason: collision with root package name */
        private static final String f1262k = "CREATE TABLE events (_id INTEGER PRIMARY KEY AUTOINCREMENT,appid TEXT,package TEXT,event_name TEXT,priority INTEGER,data BLOB,timestamp INTEGER)";

        public a(Context context) {
            super(context, f1252a, (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(f1262k);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        }
    }
}
