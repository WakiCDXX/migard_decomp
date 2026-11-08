package com.miui.powerkeeper.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerDatabaseHelper extends SQLiteHelper {
    private static final String DATABASE_NAME = "powerchecker.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = PowerCheckerDatabaseHelper.class.getSimpleName();

    @SuppressLint({"StaticFieldLeak"})
    private static PowerCheckerDatabaseHelper pcDatabaseHelper = null;

    private PowerCheckerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    public static synchronized PowerCheckerDatabaseHelper getInstance(Context context) {
        try {
            if (pcDatabaseHelper == null) {
                pcDatabaseHelper = new PowerCheckerDatabaseHelper(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return pcDatabaseHelper;
    }

    /* JADX WARN: Code duplicated, block: B:31:0x00b0  */
    /* JADX WARN: Code duplicated, block: B:32:0x00ca  */
    /* JADX WARN: Instruction removed from duplicated block: B:31:0x00b0, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:32:0x00ca, please report this as an issue */
    private void updateDatabase(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        String str = TAG;
        Log.i(str, "Upgrading database from version " + i2 + " to " + i3);
        int i4 = 2;
        if (i3 != 2) {
            Log.e(str, "Illegal update request. Got " + i3 + ", expected 2");
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
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS abnormalTable");
                    sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS abnormalTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT, uid INTEGER NOT NULL, package_name TEXT NOT NULL, type INTEGER NOT NULL, paction INTEGER NOT NULL, priority INTEGER NOT NULL, package_version TEXT NOT NULL, record_time TEXT NOT NULL );");
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
        if (i2 == 1) {
            sQLiteDatabase.beginTransaction();
            try {
                try {
                    Log.i(TAG, "Upgrading database from version " + i2 + " to " + i3);
                    sQLiteDatabase.execSQL("ALTER TABLE abnormalTable ADD COLUMN flag INTEGER NOT NULL DEFAULT 0 ");
                    sQLiteDatabase.setTransactionSuccessful();
                    sQLiteDatabase.endTransaction();
                } catch (Exception e3) {
                    Log.e(TAG, e3.getMessage());
                    sQLiteDatabase.endTransaction();
                    i4 = i2;
                    if (i3 != i4) {
                        Log.e(TAG, "Upgrade database to version " + i3 + " fails");
                        return;
                    }
                    Log.i(TAG, "Upgrade database to version " + i3 + " success");
                }
            } catch (Throwable th2) {
                sQLiteDatabase.endTransaction();
                throw th2;
            }
        } else {
            i4 = i2;
        }
        if (i3 != i4) {
            Log.e(TAG, "Upgrade database to version " + i3 + " fails");
            return;
        }
        Log.i(TAG, "Upgrade database to version " + i3 + " success");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        updateDatabase(sQLiteDatabase, 0, 2);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        Log.e(TAG, "Downgrade database from version " + i2 + " to " + i3);
        updateDatabase(sQLiteDatabase, 0, i3);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        updateDatabase(sQLiteDatabase, i2, 2);
    }
}
