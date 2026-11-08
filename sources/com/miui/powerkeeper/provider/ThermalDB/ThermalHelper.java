package com.miui.powerkeeper.provider.ThermalDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.ThermalCollector;
import com.miui.powerkeeper.provider.SQLiteHelper;

/* JADX INFO: loaded from: classes.dex */
public class ThermalHelper extends SQLiteHelper {
    private static final String DATABASE_NAME = "thermal.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TAG = "ThermalHelper";

    public ThermalHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
        Log.v(ThermalCollector.TAG, "thermalHelper construct");
    }

    private void updateDatabase(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        if (i3 != 3) {
            Log.e(ThermalCollector.TAG, "Illegal update request. Got " + i3 + ", expected 3");
            throw new IllegalArgumentException();
        }
        if (i2 > i3) {
            Log.e(ThermalCollector.TAG, "Illegal update request: can't downgrade from " + i2 + " to " + i3 + ". Did you forget to wipe data?");
            throw new IllegalArgumentException();
        }
        if (i2 < 1) {
            sQLiteDatabase.beginTransaction();
            try {
                try {
                    sQLiteDatabase.execSQL(ThermalDurationConfigure.CREATE_TABLE);
                    i2++;
                    sQLiteDatabase.setTransactionSuccessful();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                sQLiteDatabase.endTransaction();
            } catch (Throwable th) {
                sQLiteDatabase.endTransaction();
                throw th;
            }
        }
        if (i2 == 1) {
            sQLiteDatabase.beginTransaction();
            try {
                try {
                    sQLiteDatabase.execSQL("ALTER TABLE thermal_duration ADD COLUMN column5 TEXT;");
                    i2++;
                    sQLiteDatabase.setTransactionSuccessful();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                sQLiteDatabase.endTransaction();
            } catch (Throwable th2) {
                sQLiteDatabase.endTransaction();
                throw th2;
            }
        }
        if (i2 == 2) {
            sQLiteDatabase.beginTransaction();
            try {
                try {
                    sQLiteDatabase.execSQL(ThermalInfo.CREATE_TABLE);
                    sQLiteDatabase.setTransactionSuccessful();
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            } finally {
                sQLiteDatabase.endTransaction();
            }
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        updateDatabase(sQLiteDatabase, 0, 3);
        Log.v(ThermalCollector.TAG, "thermalHelper on create");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        if (i2 >= i3) {
            Log.e(TAG, "error db version");
        }
        updateDatabase(sQLiteDatabase, i2, 3);
    }
}
