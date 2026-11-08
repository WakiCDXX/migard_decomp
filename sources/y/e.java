package y;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
class e implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ c f2421a;

    e(c cVar) {
        this.f2421a = cVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.f2421a.f2414a) {
            Cursor cursorQuery = null;
            try {
                try {
                    SQLiteDatabase writableDatabase = this.f2421a.f2414a.getWritableDatabase();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(6, calendar.get(6) - 7);
                    calendar.set(11, 0);
                    calendar.set(12, 0);
                    calendar.set(13, 0);
                    String[] strArr = {Long.toString(calendar.getTimeInMillis())};
                    cursorQuery = writableDatabase.query("otpubsub", new String[]{"timestamp"}, "timestamp < ? ", strArr, null, null, "timestamp ASC");
                    if (cursorQuery.getCount() != 0) {
                        com.ot.pubsub.util.k.c("MessageManager", "*** deleted obsolete item count=" + writableDatabase.delete("otpubsub", "timestamp < ? ", strArr));
                    }
                    long jO = c.d().o();
                    this.f2421a.f(jO == 0);
                    com.ot.pubsub.util.k.c("MessageManager", "after delete obsolete record remains=" + jO);
                } catch (Exception e2) {
                    com.ot.pubsub.util.k.l("MessageManager", "remove obsolete messages failed with " + e2);
                    if (cursorQuery != null) {
                    }
                }
                try {
                    cursorQuery.close();
                } catch (Exception unused) {
                }
            } catch (Throwable th) {
                if (cursorQuery != null) {
                    try {
                        cursorQuery.close();
                    } catch (Exception unused2) {
                    }
                }
                throw th;
            }
        }
    }
}
