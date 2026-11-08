package com.xiaomi.onetrack.a;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.xiaomi.onetrack.b.n;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f892a = "AdMonitorManager";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final int f893b = 204800;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final int f894c = 100;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final int f895d = 4;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final int f896e = 300;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f897f = "_id ASC";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final int f898g = 7;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static a f899h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private final C0009a f900i = new C0009a(com.xiaomi.onetrack.f.a.a());

    private a() {
        c();
    }

    private void f() {
        try {
            this.f900i.getWritableDatabase().delete(C0009a.f902b, null, null);
            p.a(f892a, "delete table monitor");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX WARN: Code duplicated, block: B:43:0x00ec A[Catch: all -> 0x00d9, PHI: r10
      0x00ec: PHI (r10v4 android.database.Cursor) = (r10v3 android.database.Cursor), (r10v5 android.database.Cursor) binds: [B:42:0x00ea, B:46:0x00fa] A[DONT_GENERATE, DONT_INLINE], TRY_ENTER, TRY_LEAVE, TryCatch #3 {all -> 0x00d9, blocks: (B:30:0x00d4, B:31:0x00d7, B:51:0x0101, B:52:0x0104, B:43:0x00ec, B:48:0x00fd), top: B:56:0x0006 }] */
    /* JADX WARN: Code duplicated, block: B:51:0x0101 A[Catch: all -> 0x00d9, TryCatch #3 {all -> 0x00d9, blocks: (B:30:0x00d4, B:31:0x00d7, B:51:0x0101, B:52:0x0104, B:43:0x00ec, B:48:0x00fd), top: B:56:0x0006 }] */
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x0095: MOVE (r9 I:??[OBJECT, ARRAY]) = (r10 I:??[OBJECT, ARRAY]), block:B:17:0x0095 */
    public com.xiaomi.onetrack.a.c.a b() {
        Cursor cursorQuery;
        Cursor cursor;
        int i2;
        synchronized (this.f900i) {
            Cursor cursor2 = null;
            try {
                try {
                    try {
                        cursorQuery = this.f900i.getReadableDatabase().query(C0009a.f902b, null, null, null, null, null, f897f);
                        try {
                            int columnIndex = cursorQuery.getColumnIndex("_id");
                            cursorQuery.getColumnIndex("appid");
                            cursorQuery.getColumnIndex("package");
                            cursorQuery.getColumnIndex("event_name");
                            int columnIndex2 = cursorQuery.getColumnIndex("timestamp");
                            int columnIndex3 = cursorQuery.getColumnIndex("url");
                            int columnIndex4 = cursorQuery.getColumnIndex(C0009a.f909i);
                            ArrayList arrayList = new ArrayList();
                            long jCurrentTimeMillis = System.currentTimeMillis();
                            int i3 = 0;
                            while (true) {
                                if (!cursorQuery.moveToNext()) {
                                    i2 = i3;
                                    break;
                                }
                                int i4 = cursorQuery.getInt(columnIndex4);
                                long j2 = cursorQuery.getLong(columnIndex2);
                                if (a(jCurrentTimeMillis, j2, i4)) {
                                    int i5 = cursorQuery.getInt(columnIndex);
                                    String string = cursorQuery.getString(columnIndex3);
                                    com.xiaomi.onetrack.a.b.a aVar = new com.xiaomi.onetrack.a.b.a();
                                    aVar.b(string);
                                    aVar.a(i5);
                                    aVar.a(j2);
                                    aVar.b(i4);
                                    arrayList.add(aVar);
                                    i2 = i3 + 1;
                                    if (i2 >= 100) {
                                        break;
                                    }
                                    i3 = i2;
                                }
                            }
                            boolean z2 = true;
                            if (arrayList.size() > 0) {
                                p.a(f892a, "get ad monitor size :" + arrayList.size());
                                if (cursorQuery.isAfterLast()) {
                                    p.a(f892a, "cursor isAfterLast");
                                } else {
                                    z2 = false;
                                }
                            }
                            com.xiaomi.onetrack.a.c.a aVar2 = new com.xiaomi.onetrack.a.c.a(i2, arrayList, z2);
                            cursorQuery.close();
                            return aVar2;
                        } catch (SQLiteBlobTooBigException e2) {
                            e = e2;
                            p.b(f892a, "blob too big ***", e);
                            f();
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return null;
                        } catch (Exception e3) {
                            e = e3;
                            p.a(f892a, "", e);
                            if (cursorQuery != null) {
                                cursorQuery.close();
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
                } catch (SQLiteBlobTooBigException e4) {
                    e = e4;
                    cursorQuery = null;
                } catch (Exception e5) {
                    e = e5;
                    cursorQuery = null;
                } catch (Throwable th2) {
                    th = th2;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }

    public void c() {
        com.xiaomi.onetrack.a.a.a.a(new c(this));
    }

    public void d() {
        synchronized (this.f900i) {
            Cursor cursorQuery = null;
            try {
                try {
                    SQLiteDatabase writableDatabase = this.f900i.getWritableDatabase();
                    String[] strArr = {Long.toString(4L)};
                    cursorQuery = writableDatabase.query(C0009a.f902b, new String[]{"timestamp"}, "send_count >= ? ", strArr, null, null, f897f);
                    if (cursorQuery.getCount() != 0) {
                        p.a(f892a, "*** deleted obsolete ad monitor count=" + writableDatabase.delete(C0009a.f902b, "send_count >= ? ", strArr));
                    }
                    if (p.f1665a) {
                        p.a(f892a, "after delete obsolete ad monitor record remains=" + e());
                    }
                } catch (Exception e2) {
                    p.d(f892a, "remove obsolete ad monitor failed with " + e2);
                    if (cursorQuery != null) {
                    }
                }
                cursorQuery.close();
            } catch (Throwable th) {
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                throw th;
            }
        }
    }

    public long e() {
        return DatabaseUtils.queryNumEntries(this.f900i.getReadableDatabase(), C0009a.f902b);
    }

    public static a a() {
        if (f899h == null) {
            a(com.xiaomi.onetrack.f.a.a());
        }
        return f899h;
    }

    public static void a(Context context) {
        if (f899h == null) {
            synchronized (a.class) {
                try {
                    if (f899h == null) {
                        f899h = new a();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public void a(com.xiaomi.onetrack.f.b bVar) {
        try {
            com.xiaomi.onetrack.a.a.a.a(new b(this, bVar));
        } catch (Throwable th) {
            p.a(f892a, "filterAdMonitor Throwable：" + th.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long a(com.xiaomi.onetrack.a.b.a aVar) {
        synchronized (this.f900i) {
            try {
                if (!aVar.h()) {
                    p.c(f892a, "addAdMonitorToDatabase event is inValid, event:" + aVar.a());
                    return -1L;
                }
                SQLiteDatabase writableDatabase = this.f900i.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("appid", aVar.e());
                contentValues.put("package", aVar.f());
                contentValues.put("event_name", aVar.a());
                contentValues.put("timestamp", Long.valueOf(aVar.d()));
                contentValues.put("url", aVar.c());
                long jInsert = writableDatabase.insert(C0009a.f902b, null, contentValues);
                p.a(f892a, "DB-Thread: AdMonitorManager.addAdMonitorToDatabase , row=" + jInsert);
                if (p.f1665a) {
                    p.a(f892a, "添加后，ad monitor url 中事件个数为 " + e());
                }
                return jInsert;
            } catch (Throwable th) {
                p.a(f892a, "addAdMonitorToDatabase Throwable：" + th.getMessage());
                return -1L;
            }
        }
    }

    public int a(ArrayList<Integer> arrayList) {
        synchronized (this.f900i) {
            if (arrayList != null) {
                try {
                    if (arrayList.size() != 0) {
                        try {
                            SQLiteDatabase writableDatabase = this.f900i.getWritableDatabase();
                            StringBuilder sb = new StringBuilder(((Long.toString(arrayList.get(0).intValue()).length() + 1) * arrayList.size()) + 16);
                            sb.append("_id");
                            sb.append(" in (");
                            sb.append(arrayList.get(0));
                            int size = arrayList.size();
                            for (int i2 = 1; i2 < size; i2++) {
                                sb.append(",");
                                sb.append(arrayList.get(i2));
                            }
                            sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
                            int iDelete = writableDatabase.delete(C0009a.f902b, sb.toString(), null);
                            p.a(f892a, "*** *** deleted ad monitor count " + iDelete);
                            if (p.f1665a) {
                                p.a(f892a, "after delete ad monitor record remains=" + e());
                            }
                            return iDelete;
                        } catch (Exception e2) {
                            p.b(f892a, "e=" + e2);
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

    public void b(ArrayList<Integer> arrayList) {
        String str;
        String str2;
        synchronized (this.f900i) {
            if (arrayList != null) {
                try {
                    try {
                        if (arrayList.size() > 0) {
                            SQLiteDatabase writableDatabase = null;
                            try {
                                try {
                                    writableDatabase = this.f900i.getWritableDatabase();
                                    writableDatabase.beginTransaction();
                                    for (Integer num : arrayList) {
                                        num.intValue();
                                        writableDatabase.execSQL(String.format("update %s set %s = %s + 1 where %s = %s", C0009a.f902b, C0009a.f909i, C0009a.f909i, "_id", num));
                                    }
                                    writableDatabase.setTransactionSuccessful();
                                    try {
                                        writableDatabase.endTransaction();
                                        writableDatabase.close();
                                    } catch (Exception e2) {
                                        str = f892a;
                                        str2 = "addAdMonitorsRetryCount endTransaction error: " + e2.getMessage();
                                        p.b(str, str2);
                                    }
                                } catch (Throwable th) {
                                    if (writableDatabase != null) {
                                        try {
                                            writableDatabase.endTransaction();
                                            writableDatabase.close();
                                        } catch (Exception e3) {
                                            p.b(f892a, "addAdMonitorsRetryCount endTransaction error: " + e3.getMessage());
                                        }
                                    }
                                    throw th;
                                }
                            } catch (Exception e4) {
                                p.b(f892a, "addAdMonitorsRetryCount Exception: " + e4.getMessage());
                                if (writableDatabase != null) {
                                    try {
                                        writableDatabase.endTransaction();
                                        writableDatabase.close();
                                    } catch (Exception e5) {
                                        str = f892a;
                                        str2 = "addAdMonitorsRetryCount endTransaction error: " + e5.getMessage();
                                        p.b(str, str2);
                                    }
                                }
                            }
                        }
                    } catch (Throwable th2) {
                        throw th2;
                    }
                } catch (Throwable th3) {
                    p.a(f892a, "addAdMonitorsRetryCount Throwable:" + th3.getMessage());
                }
            }
        }
    }

    public boolean a(long j2, long j3, int i2) {
        if (i2 <= 0) {
            return true;
        }
        if (i2 < 4) {
            return Math.abs(j2 - j3) >= ((long) n.a(i2 - 1));
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: com.xiaomi.onetrack.a.a$a, reason: collision with other inner class name */
    static class C0009a extends SQLiteOpenHelper {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public static final String f901a = "onetrack_ad";

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public static final String f902b = "monitor";

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public static final String f903c = "_id";

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        public static final String f904d = "appid";

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public static final String f905e = "package";

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        public static final String f906f = "event_name";

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        public static final String f907g = "url";

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        public static final String f908h = "timestamp";

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        public static final String f909i = "send_count";

        /* JADX INFO: renamed from: j, reason: collision with root package name */
        private static final int f910j = 1;

        /* JADX INFO: renamed from: k, reason: collision with root package name */
        private static final String f911k = "CREATE TABLE monitor (_id INTEGER PRIMARY KEY AUTOINCREMENT,appid TEXT,package TEXT,event_name TEXT,url TEXT,send_count INTEGER DEFAULT 0,timestamp INTEGER)";

        public C0009a(Context context) {
            super(context, f901a, (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(f911k);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        }
    }
}
