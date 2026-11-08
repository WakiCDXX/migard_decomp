package y;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
class h implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ f f2431a;

    h(f fVar) {
        this.f2431a = fVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.f2431a.f2425a) {
            Cursor cursorQuery = null;
            try {
                try {
                    SQLiteDatabase writableDatabase = this.f2431a.f2425a.getWritableDatabase();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(6, calendar.get(6) - 7);
                    calendar.set(11, 0);
                    calendar.set(12, 0);
                    calendar.set(13, 0);
                    String[] strArr = {Long.toString(calendar.getTimeInMillis())};
                    cursorQuery = writableDatabase.query("othbpubsub", new String[]{"timestamp"}, "timestamp < ? ", strArr, null, null, "timestamp ASC");
                    if (cursorQuery.getCount() != 0) {
                        com.ot.pubsub.util.k.c("MessageOTManager", "*** deleted obsolete item count=" + writableDatabase.delete("othbpubsub", "timestamp < ? ", strArr));
                    }
                    long jO = f.e().o();
                    this.f2431a.g(jO == 0);
                    com.ot.pubsub.util.k.c("MessageOTManager", "after delete obsolete record remains=" + jO);
                } catch (Exception e2) {
                    com.ot.pubsub.util.k.l("MessageOTManager", "remove obsolete messages failed with " + e2);
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
