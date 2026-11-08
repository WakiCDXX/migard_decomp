package y;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import com.ot.pubsub.util.v;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class f {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static f f2422b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static volatile boolean f2423c = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static volatile int f2424d = 1;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private a f2425a = new a(com.ot.pubsub.util.b.a());

    private f() {
        m();
    }

    public static String c(byte[] bArr) {
        return new String(u.a.h(bArr, u.d.a(u.c.b(), true).getBytes()));
    }

    public static f e() {
        if (f2422b == null) {
            k();
        }
        return f2422b;
    }

    public static byte[] i(String str) {
        return u.a.e(str.getBytes(), u.d.a(u.c.b(), true).getBytes());
    }

    public static void k() {
        if (f2422b == null) {
            synchronized (f.class) {
                try {
                    if (f2422b == null) {
                        f2422b = new f();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean l(String str, String str2, z.a aVar, Map<String, String> map) {
        try {
            synchronized (this.f2425a) {
                if (aVar != null) {
                    try {
                        if (aVar.n() && !TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
                            byte[] bArrI = i(aVar.m().toString());
                            if (bArrI.length > 512000) {
                                com.ot.pubsub.util.k.h("MessageOTManager", "Too large data, discard ***");
                                return false;
                            }
                            SQLiteDatabase writableDatabase = this.f2425a.getWritableDatabase();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("appid", aVar.f());
                            contentValues.put("package", aVar.i());
                            contentValues.put("event_name", aVar.k());
                            contentValues.put("projectid", str);
                            contentValues.put("topic", str2);
                            contentValues.put("attribute", com.ot.pubsub.util.c.d(map));
                            contentValues.put("gzipandencrypt", Integer.valueOf(f2424d));
                            contentValues.put("timestamp", Long.valueOf(System.currentTimeMillis()));
                            contentValues.put("data", bArrI);
                            contentValues.put("priority", Integer.valueOf(aVar.l()));
                            long jInsert = writableDatabase.insert("othbpubsub", null, contentValues);
                            com.ot.pubsub.util.k.c("MessageOTManager", "DB-Thread: addOTMessageToDatabase , row=" + jInsert);
                            if (jInsert != -1) {
                                if (com.ot.pubsub.util.k.f724a) {
                                    com.ot.pubsub.util.k.c("MessageOTManager", "添加后，DB 中事件个数为 " + o());
                                }
                                g(false);
                            }
                            return jInsert != -1;
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                com.ot.pubsub.util.k.k("MessageOTManager", "addEventToDatabase message is inValid. topic:" + str2 + ", data EventName:" + aVar.k());
                return false;
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.i("MessageOTManager", "addEvent exception: ", e2);
            return false;
        }
    }

    private void p() {
        try {
            this.f2425a.getWritableDatabase().delete("othbpubsub", null, null);
            g(true);
            com.ot.pubsub.util.k.c("MessageOTManager", "delete table othbpubsub");
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("MessageOTManager", "delete table error: " + e2.getMessage());
        }
    }

    public int a(List<v.a> list) {
        synchronized (this.f2425a) {
            if (list != null) {
                try {
                    if (list.size() != 0) {
                        try {
                            SQLiteDatabase writableDatabase = this.f2425a.getWritableDatabase();
                            boolean z2 = true;
                            StringBuilder sb = new StringBuilder(((Long.toString(list.get(0).a()).length() + 1) * list.size()) + 16);
                            sb.append("_id");
                            sb.append(" in (");
                            sb.append(list.get(0).a());
                            int size = list.size();
                            for (int i2 = 1; i2 < size; i2++) {
                                sb.append(",");
                                sb.append(list.get(i2).a());
                            }
                            sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
                            int iDelete = writableDatabase.delete("othbpubsub", sb.toString(), null);
                            com.ot.pubsub.util.k.c("MessageOTManager", "*** *** deleted events count " + iDelete);
                            long jO = e().o();
                            if (jO != 0) {
                                z2 = false;
                            }
                            g(z2);
                            com.ot.pubsub.util.k.c("MessageOTManager", "after delete DB record remains=" + jO);
                            return iDelete;
                        } catch (Exception e2) {
                            com.ot.pubsub.util.k.h("MessageOTManager", "e=" + e2);
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

    /* JADX WARN: Code duplicated, block: B:42:0x010d  */
    /* JADX WARN: Code duplicated, block: B:46:0x0129 A[LOOP:0: B:5:0x0063->B:46:0x0129, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:82:0x017d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:97:0x010c A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:98:0x0112 A[SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 5, insn: 0x00ae: MOVE (r3 I:??[OBJECT, ARRAY]) = (r5 I:??[OBJECT, ARRAY]) (LINE:175), block:B:15:0x00ae */
    public d0.a b(int i2) throws Throwable {
        Cursor cursor;
        Cursor cursorQuery;
        Cursor cursor2;
        boolean z2;
        Map map;
        Map map2;
        int length;
        d0.a aVar = null;
        try {
            try {
                cursorQuery = this.f2425a.getReadableDatabase().query("othbpubsub", null, null, null, null, null, "priority ASC, _id ASC");
                try {
                    int columnIndex = cursorQuery.getColumnIndex("_id");
                    cursorQuery.getColumnIndex("appid");
                    int columnIndex2 = cursorQuery.getColumnIndex("projectid");
                    int columnIndex3 = cursorQuery.getColumnIndex("topic");
                    int columnIndex4 = cursorQuery.getColumnIndex("data");
                    int columnIndex5 = cursorQuery.getColumnIndex("attribute");
                    int columnIndex6 = cursorQuery.getColumnIndex("gzipandencrypt");
                    int columnIndex7 = cursorQuery.getColumnIndex("timestamp");
                    int columnIndex8 = cursorQuery.getColumnIndex("priority");
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    boolean z3 = true;
                    boolean z4 = true;
                    int i3 = 0;
                    int i4 = 0;
                    while (cursorQuery.moveToNext()) {
                        long j2 = cursorQuery.getLong(columnIndex);
                        String string = cursorQuery.getString(columnIndex2);
                        String string2 = cursorQuery.getString(columnIndex3);
                        byte[] blob = cursorQuery.getBlob(columnIndex5);
                        int i5 = cursorQuery.getInt(columnIndex6);
                        byte[] blob2 = cursorQuery.getBlob(columnIndex4);
                        long j3 = cursorQuery.getLong(columnIndex7);
                        if (z4) {
                            int i6 = cursorQuery.getInt(columnIndex8);
                            if (i6 > i2) {
                                com.ot.pubsub.util.k.c("MessageOTManager", "No records of priority[" + i2 + "], first record priority=" + i6);
                                try {
                                    cursorQuery.close();
                                } catch (Exception unused) {
                                }
                                return aVar;
                            }
                            z2 = false;
                        } else {
                            z2 = z4;
                        }
                        String strC = blob2 != null ? c(blob2) : aVar;
                        Object objA = com.ot.pubsub.util.c.a(blob);
                        try {
                            if (objA == null || !(objA instanceof Map)) {
                                if (objA == null) {
                                    map2 = new HashMap();
                                } else {
                                    map = null;
                                }
                                arrayList.add(new v.a(j2, string, string2, strC, map, i5, j3));
                                arrayList2.add(Long.valueOf(j2));
                                i3++;
                                length = i4 + strC.length();
                                if (arrayList.size() < 1000) {
                                    break;
                                }
                                if (length >= 1024000) {
                                    com.ot.pubsub.util.k.c("MessageOTManager", "reached max len: " + length);
                                    break;
                                }
                                i4 = length;
                                z4 = z2;
                                aVar = null;
                            } else {
                                map2 = (Map) objA;
                            }
                            arrayList.add(new v.a(j2, string, string2, strC, map, i5, j3));
                            arrayList2.add(Long.valueOf(j2));
                            i3++;
                        } catch (Exception e2) {
                            com.ot.pubsub.util.k.i("MessageOTManager", "*** error ***", e2);
                        }
                        map = map2;
                        length = i4 + strC.length();
                        if (arrayList.size() < 1000) {
                            break;
                            break;
                        }
                        if (length >= 1024000) {
                            com.ot.pubsub.util.k.c("MessageOTManager", "reached max len: " + length);
                            break;
                        }
                        i4 = length;
                        z4 = z2;
                        aVar = null;
                    }
                    int i7 = i3;
                    if (arrayList2.size() > 0) {
                        if (cursorQuery.isAfterLast()) {
                            com.ot.pubsub.util.k.c("MessageOTManager", "cursor isAfterLast");
                        } else if (cursorQuery.getInt(columnIndex8) <= i2) {
                            z3 = false;
                        }
                        d0.a aVar2 = new d0.a(arrayList, i7, arrayList2, z3);
                        try {
                            cursorQuery.close();
                        } catch (Exception unused2) {
                        }
                        return aVar2;
                    }
                } catch (SQLiteBlobTooBigException e3) {
                    e = e3;
                    com.ot.pubsub.util.k.i("MessageOTManager", "blob too big ***", e);
                    p();
                    if (cursorQuery == null) {
                        return null;
                    }
                } catch (Exception e4) {
                    e = e4;
                    com.ot.pubsub.util.k.e("MessageOTManager", "", e);
                    if (cursorQuery == null) {
                        return null;
                    }
                }
            } catch (Throwable th) {
                th = th;
                cursor = cursor2;
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception unused3) {
                    }
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
            cursor = null;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        try {
            cursorQuery.close();
            return null;
        } catch (Exception unused4) {
            return null;
        }
    }

    public synchronized void f(String str, String str2, z.a aVar, Map<String, String> map) {
        y.a.b(new g(this, map, str, str2, aVar));
    }

    public synchronized void g(boolean z2) {
        f2423c = z2;
    }

    public int j(List<v.a> list) {
        synchronized (this.f2425a) {
            if (list != null) {
                try {
                    if (list.size() != 0) {
                        try {
                            SQLiteDatabase writableDatabase = this.f2425a.getWritableDatabase();
                            boolean z2 = true;
                            StringBuilder sb = new StringBuilder(((Long.toString(list.get(0).a()).length() + 1) * list.size()) + 16);
                            sb.append("_id");
                            sb.append(" in (");
                            int size = list.size();
                            boolean z3 = false;
                            for (int i2 = 0; i2 < size; i2++) {
                                if (v.b(list.get(i2).f(), 86400000L)) {
                                    if (i2 == size - 1) {
                                        sb.append(list.get(i2).a());
                                    } else {
                                        sb.append(list.get(i2).a());
                                        sb.append(",");
                                    }
                                    z3 = true;
                                }
                            }
                            sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
                            if (!z3) {
                                return 0;
                            }
                            com.ot.pubsub.util.k.c("MessageOTManager", "*** *** deleted events sb id " + sb.toString());
                            int iDelete = writableDatabase.delete("othbpubsub", sb.toString(), null);
                            com.ot.pubsub.util.k.c("MessageOTManager", "*** *** deleted events count " + iDelete);
                            long jO = e().o();
                            if (jO != 0) {
                                z2 = false;
                            }
                            g(z2);
                            com.ot.pubsub.util.k.c("MessageOTManager", "after delete DB record remains=" + jO);
                            return iDelete;
                        } catch (Exception e2) {
                            com.ot.pubsub.util.k.h("MessageOTManager", "e=" + e2);
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

    public void m() {
        y.a.b(new h(this));
    }

    public synchronized boolean n() {
        return f2423c;
    }

    public long o() {
        return DatabaseUtils.queryNumEntries(this.f2425a.getReadableDatabase(), "othbpubsub");
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class a extends SQLiteOpenHelper {
        public a(Context context) {
            super(context, "othbpubsub.db", (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE othbpubsub (_id INTEGER PRIMARY KEY AUTOINCREMENT,appid TEXT,package TEXT,event_name TEXT,projectid TEXT,topic TEXT,data BLOB,attribute BLOB,gzipandencrypt INTEGER,priority INTEGER,timestamp INTEGER)");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        }
    }
}
