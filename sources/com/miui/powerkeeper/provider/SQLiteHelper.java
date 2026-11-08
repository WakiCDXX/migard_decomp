package com.miui.powerkeeper.provider;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public abstract class SQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHelper.class.getSimpleName();
    private boolean mInitialized;

    public SQLiteHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i2) {
        super(context, str, cursorFactory, i2);
        this.mInitialized = false;
    }

    private synchronized SQLiteDatabase getDatabase(boolean z2) {
        SQLiteDatabase writableDatabase;
        try {
            if (this.mInitialized) {
                return z2 ? super.getWritableDatabase() : super.getReadableDatabase();
            }
            do {
                writableDatabase = super.getWritableDatabase();
                if (!isValidDatabase(writableDatabase)) {
                    writableDatabase.setVersion(0);
                    super.close();
                    Log.e(TAG, "Try to getWritableDatabase again");
                    writableDatabase = null;
                }
            } while (writableDatabase == null);
            this.mInitialized = true;
            if (!z2) {
                super.close();
                writableDatabase = super.getReadableDatabase();
            }
            return writableDatabase;
        } catch (Throwable th) {
            throw th;
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public SQLiteDatabase getReadableDatabase() {
        return getDatabase(false);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public SQLiteDatabase getWritableDatabase() {
        return getDatabase(true);
    }

    public boolean isValidDatabase(SQLiteDatabase sQLiteDatabase) {
        return true;
    }

    public SQLiteHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i2, DatabaseErrorHandler databaseErrorHandler) {
        super(context, str, cursorFactory, i2, databaseErrorHandler);
        this.mInitialized = false;
    }
}
