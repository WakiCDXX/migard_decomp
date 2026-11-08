package com.miui.powerkeeper.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class CustomerPowerCheckerDatabaseHelper extends SQLiteHelper {
    private static final String DATABASE_NAME = "customerpowerchecker.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = CustomerPowerCheckerDatabaseHelper.class.getSimpleName();

    @SuppressLint({"StaticFieldLeak"})
    private static CustomerPowerCheckerDatabaseHelper pcDatabaseHelper = null;

    private CustomerPowerCheckerDatabaseHelper(Context context) {
        super(context, "customerpowerchecker.db", null, 1);
    }

    public static synchronized CustomerPowerCheckerDatabaseHelper getInstance(Context context) {
        try {
            if (pcDatabaseHelper == null) {
                pcDatabaseHelper = new CustomerPowerCheckerDatabaseHelper(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return pcDatabaseHelper;
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
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS CustomerPowerRecordTable");
                    sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS CustomerPowerRecordTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER NOT NULL, type_name TEXT NOT NULL, detail_name TEXT, record_time TEXT NOT NULL );");
                    sQLiteDatabase.setTransactionSuccessful();
                } catch (Exception e2) {
                    Log.e(TAG, e2.getMessage());
                }
            } finally {
                sQLiteDatabase.endTransaction();
            }
        }
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
