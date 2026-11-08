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
public class c {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static c f2410b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static volatile boolean f2411c = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static volatile int f2412d = 1;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static volatile int f2413e;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private a f2414a = new a(com.ot.pubsub.util.b.a());

    /* JADX INFO: Access modifiers changed from: private */
    static class a extends SQLiteOpenHelper {
        public a(Context context) {
            super(context, "otpubsub.db", (SQLiteDatabase.CursorFactory) null, 2);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE otpubsub (_id INTEGER PRIMARY KEY AUTOINCREMENT,projectid TEXT,topic TEXT,data BLOB,attribute BLOB,gzipandencrypt INTEGER,timestamp INTEGER)");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
            com.ot.pubsub.util.k.c("MessageManager", "onUpgrade, old=" + i2 + ", new = " + i3);
            if (i3 == 2 && i2 == 1) {
                try {
                    sQLiteDatabase.execSQL("ALTER TABLE otpubsub ADD COLUMN gzipandencrypt INTEGER DEFAULT 0");
                } catch (Throwable th) {
                    com.ot.pubsub.util.k.h("MessageManager", "onUpgrade Throwable" + th.getMessage());
                }
            }
        }
    }

    private c() {
        m();
    }

    public static String b(byte[] bArr) {
        return new String(u.a.h(bArr, u.d.a(u.c.b(), true).getBytes()));
    }

    public static c d() {
        if (f2410b == null) {
            j();
        }
        return f2410b;
    }

    public static byte[] h(String str) {
        return u.a.e(str.getBytes(), u.d.a(u.c.b(), true).getBytes());
    }

    public static void j() {
        if (f2410b == null) {
            synchronized (c.class) {
                try {
                    if (f2410b == null) {
                        f2410b = new c();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean k(String str, String str2, String str3, Map<String, String> map, boolean z2) {
        try {
            synchronized (this.f2414a) {
                try {
                    if (TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str3)) {
                        com.ot.pubsub.util.k.k("MessageManager", "addEventToDatabase message is inValid. topic:" + str2 + ", data:" + str3);
                        return false;
                    }
                    if (!z2) {
                        str3 = u.c.d(str3);
                    }
                    byte[] bArrH = h(str3);
                    if (bArrH.length > 512000) {
                        com.ot.pubsub.util.k.h("MessageManager", "Too large data, discard ***");
                        return false;
                    }
                    int i2 = f2413e;
                    if (z2) {
                        i2 = f2412d;
                    }
                    SQLiteDatabase writableDatabase = this.f2414a.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("projectid", str);
                    contentValues.put("topic", str2);
                    contentValues.put("attribute", com.ot.pubsub.util.c.d(map));
                    contentValues.put("gzipandencrypt", Integer.valueOf(i2));
                    contentValues.put("timestamp", Long.valueOf(System.currentTimeMillis()));
                    contentValues.put("data", bArrH);
                    long jInsert = writableDatabase.insert("otpubsub", null, contentValues);
                    com.ot.pubsub.util.k.c("MessageManager", "DB-Thread: EventManager.addEventToDatabase , row=" + jInsert);
                    if (jInsert != -1) {
                        if (com.ot.pubsub.util.k.f724a) {
                            com.ot.pubsub.util.k.c("MessageManager", "添加后，DB 中事件个数为 " + o());
                        }
                        f(false);
                    }
                    return jInsert != -1;
                } catch (Throwable th) {
                    throw th;
                }
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.i("MessageManager", "EventManager.addEvent exception: ", e2);
            return false;
        }
    }

    private void p() {
        try {
            this.f2414a.getWritableDatabase().delete("otpubsub", null, null);
            f(true);
            com.ot.pubsub.util.k.c("MessageManager", "delete table otpubsub");
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("MessageManager", "delete table error: " + e2.getMessage());
        }
    }

    public int a(List<v.a> list) {
        synchronized (this.f2414a) {
            if (list != null) {
                try {
                    if (list.size() != 0) {
                        try {
                            SQLiteDatabase writableDatabase = this.f2414a.getWritableDatabase();
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
                            int iDelete = writableDatabase.delete("otpubsub", sb.toString(), null);
                            com.ot.pubsub.util.k.c("MessageManager", "*** *** deleted events count " + iDelete);
                            long jO = d().o();
                            if (jO != 0) {
                                z2 = false;
                            }
                            f(z2);
                            com.ot.pubsub.util.k.c("MessageManager", "after delete DB record remains=" + jO);
                            return iDelete;
                        } catch (Exception e2) {
                            com.ot.pubsub.util.k.h("MessageManager", "e=" + e2);
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

    public synchronized void e(String str, String str2, String str3, Map<String, String> map, boolean z2) {
        y.a.b(new d(this, map, str, str2, str3, z2));
    }

    public synchronized void f(boolean z2) {
        f2411c = z2;
    }

    public int i(List<v.a> list) {
        synchronized (this.f2414a) {
            if (list != null) {
                try {
                    if (list.size() != 0) {
                        try {
                            SQLiteDatabase writableDatabase = this.f2414a.getWritableDatabase();
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
                            com.ot.pubsub.util.k.c("MessageManager", "*** *** deleted events sb id " + sb.toString());
                            int iDelete = writableDatabase.delete("otpubsub", sb.toString(), null);
                            com.ot.pubsub.util.k.c("MessageManager", "*** *** deleted events count " + iDelete);
                            long jO = d().o();
                            if (jO != 0) {
                                z2 = false;
                            }
                            f(z2);
                            com.ot.pubsub.util.k.c("MessageManager", "after delete DB record remains=" + jO);
                            return iDelete;
                        } catch (Exception e2) {
                            com.ot.pubsub.util.k.h("MessageManager", "e=" + e2);
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

    /* JADX WARN: Code duplicated, block: B:34:0x00cb  */
    /* JADX WARN: Code duplicated, block: B:37:0x00e5 A[LOOP:0: B:5:0x004e->B:37:0x00e5, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:68:0x012a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:81:0x00ca A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:82:0x00d0 A[SYNTHETIC] */
    /* JADX WARN: Not initialized variable reg: 4, insn: 0x007a: MOVE (r2 I:??[OBJECT, ARRAY]) = (r4 I:??[OBJECT, ARRAY]) (LINE:123), block:B:11:0x007a */
    public d0.a l() throws Throwable {
        Cursor cursor;
        Cursor cursorQuery;
        Cursor cursor2;
        boolean z2;
        Map map;
        Map map2;
        int length;
        try {
            try {
                cursorQuery = this.f2414a.getReadableDatabase().query("otpubsub", null, null, null, null, null, "timestamp");
                try {
                    int columnIndex = cursorQuery.getColumnIndex("_id");
                    int columnIndex2 = cursorQuery.getColumnIndex("projectid");
                    int columnIndex3 = cursorQuery.getColumnIndex("topic");
                    int columnIndex4 = cursorQuery.getColumnIndex("data");
                    int columnIndex5 = cursorQuery.getColumnIndex("attribute");
                    int columnIndex6 = cursorQuery.getColumnIndex("gzipandencrypt");
                    int columnIndex7 = cursorQuery.getColumnIndex("timestamp");
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    int i2 = 0;
                    int i3 = 0;
                    while (cursorQuery.moveToNext()) {
                        long j2 = cursorQuery.getLong(columnIndex);
                        String string = cursorQuery.getString(columnIndex2);
                        String string2 = cursorQuery.getString(columnIndex3);
                        byte[] blob = cursorQuery.getBlob(columnIndex5);
                        int i4 = cursorQuery.getInt(columnIndex6);
                        byte[] blob2 = cursorQuery.getBlob(columnIndex4);
                        long j3 = cursorQuery.getLong(columnIndex7);
                        String strB = blob2 != null ? b(blob2) : null;
                        Object objA = com.ot.pubsub.util.c.a(blob);
                        try {
                            if (objA == null || !(objA instanceof Map)) {
                                if (objA == null) {
                                    map2 = new HashMap();
                                } else {
                                    map = null;
                                }
                                arrayList.add(new v.a(j2, string, string2, strB, map, i4, j3));
                                arrayList2.add(Long.valueOf(j2));
                                i2++;
                                length = i3 + strB.length();
                                if (arrayList.size() < 1000) {
                                    break;
                                }
                                if (length >= 1024000) {
                                    com.ot.pubsub.util.k.c("MessageManager", "reached max len: " + length);
                                    break;
                                }
                                i3 = length;
                            } else {
                                map2 = (Map) objA;
                            }
                            arrayList.add(new v.a(j2, string, string2, strB, map, i4, j3));
                            arrayList2.add(Long.valueOf(j2));
                            i2++;
                        } catch (Exception e2) {
                            com.ot.pubsub.util.k.i("MessageManager", "*** error ***", e2);
                        }
                        map = map2;
                        length = i3 + strB.length();
                        if (arrayList.size() < 1000) {
                            break;
                            break;
                        }
                        if (length >= 1024000) {
                            com.ot.pubsub.util.k.c("MessageManager", "reached max len: " + length);
                            break;
                        }
                        i3 = length;
                    }
                    if (arrayList2.size() > 0) {
                        if (cursorQuery.isAfterLast()) {
                            com.ot.pubsub.util.k.c("MessageManager", "cursor isAfterLast");
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        d0.a aVar = new d0.a(arrayList, i2, arrayList2, z2);
                        try {
                            cursorQuery.close();
                        } catch (Exception unused) {
                        }
                        return aVar;
                    }
                } catch (SQLiteBlobTooBigException e3) {
                    e = e3;
                    com.ot.pubsub.util.k.i("MessageManager", "blob too big ***", e);
                    p();
                    if (cursorQuery == null) {
                        return null;
                    }
                } catch (Exception e4) {
                    e = e4;
                    com.ot.pubsub.util.k.e("MessageManager", "", e);
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
                    } catch (Exception unused2) {
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
        } catch (Exception unused3) {
            return null;
        }
    }

    public void m() {
        y.a.b(new e(this));
    }

    public synchronized boolean n() {
        return f2411c;
    }

    public long o() {
        return DatabaseUtils.queryNumEntries(this.f2414a.getReadableDatabase(), "otpubsub");
    }
}
