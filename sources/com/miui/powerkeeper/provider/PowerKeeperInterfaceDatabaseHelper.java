package com.miui.powerkeeper.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperInterfaceDatabaseHelper extends SQLiteHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = PowerKeeperInterfaceDatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = null;

    public PowerKeeperInterfaceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    private void updateDatabase(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        String str = TAG;
        Log.i(str, "Upgrading database from version " + i2 + " to " + i3);
        if (i3 != 1) {
            Log.e(str, "Illegal update request. Got " + i3 + ", expected 1");
            throw new IllegalArgumentException();
        }
        if (i2 > i3) {
            Log.e(str, "Illegal update request: can't downgrade from " + i2 + " to " + i3 + ". Did you forget to wipe data?");
            throw new IllegalArgumentException();
        }
        if (i2 < 1) {
            sQLiteDatabase.beginTransaction();
            try {
                try {
                    Log.i(str, "Upgrading database from version " + i2 + " to " + i3 + ", which will destroy all old data");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS appActiveTable");
                    sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS appActiveTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT, uid INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE, property INTEGER NOT NULL, active TEXT NOT NULL );");
                    sQLiteDatabase.setTransactionSuccessful();
                    sQLiteDatabase.endTransaction();
                    i2 = 1;
                } catch (Exception e2) {
                    Log.e(TAG, e2.getMessage());
                    sQLiteDatabase.endTransaction();
                }
            } catch (Throwable th) {
                sQLiteDatabase.endTransaction();
                throw th;
            }
        }
        if (i3 != i2) {
            Log.e(TAG, "Upgrade database to version " + i3 + " fails");
            return;
        }
        Log.i(TAG, "Upgrade database to version " + i3 + " success");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        updateDatabase(sQLiteDatabase, 0, 1);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        Log.e(TAG, "Downgrade database from version " + i2 + " to " + i3);
        updateDatabase(sQLiteDatabase, 0, i3);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        updateDatabase(sQLiteDatabase, i2, 1);
    }
}
