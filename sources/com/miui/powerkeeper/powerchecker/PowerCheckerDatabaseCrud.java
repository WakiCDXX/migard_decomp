package com.miui.powerkeeper.powerchecker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.miui.powerkeeper.provider.PowerCheckerDatabaseHelper;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerDatabaseCrud {
    private static PowerCheckerDatabaseCrud pcDatabaseCrud;
    private PowerCheckerDatabaseHelper helper;

    private PowerCheckerDatabaseCrud(Context context) {
        this.helper = PowerCheckerDatabaseHelper.getInstance(context);
    }

    public static PowerCheckerDatabaseCrud getInstance(Context context) {
        if (pcDatabaseCrud == null) {
            pcDatabaseCrud = new PowerCheckerDatabaseCrud(context);
        }
        return pcDatabaseCrud;
    }

    public synchronized int deleteByDate(int i2) {
        SQLiteDatabase writableDatabase = this.helper.getWritableDatabase();
        try {
            writableDatabase.execSQL(" DELETE FROM abnormalTable WHERE record_time < " + (System.currentTimeMillis() - (((long) i2) * 86400000)));
            writableDatabase.close();
        } catch (Throwable th) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            throw th;
        }
        return 0;
    }

    public synchronized long insert(String str, ContentValues contentValues) {
        long jInsert;
        SQLiteDatabase writableDatabase = this.helper.getWritableDatabase();
        try {
            jInsert = writableDatabase.insert(str, null, contentValues);
            writableDatabase.close();
        } catch (Throwable th) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            throw th;
        }
        return jInsert;
    }
}
