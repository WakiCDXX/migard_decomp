package com.miui.powerkeeper.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import com.google.android.collect.Sets;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class SimpleSettingsFakeProvider {
    private static final String TAG = "SimpleSettingsFakeProvider";
    private static final int TYPE_MISC = 2;
    private static final int TYPE_SIMPLE_SETTINGS = 1;
    private static final UriMatcher sMatcher;
    private static final Set<String> sMethods;
    private static final UriMatcher sSimpleSettingsMatcher;
    private Context mContext;
    private SQLiteOpenHelper mDbHelper;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sSimpleSettingsMatcher = uriMatcher;
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "SimpleSettings/*", 1);
        UriMatcher uriMatcher2 = new UriMatcher(-1);
        sMatcher = uriMatcher2;
        uriMatcher2.addURI(PowerKeeperConfigure.AUTHORITY, "SimpleSettings/misc", 2);
        ArraySet arraySetNewArraySet = Sets.newArraySet();
        sMethods = arraySetNewArraySet;
        arraySetNewArraySet.add(SimpleSettings.Misc.CALL_METHOD_GET_MISC);
        arraySetNewArraySet.add(SimpleSettings.Misc.CALL_METHOD_PUT_MISC);
        arraySetNewArraySet.add(SimpleSettings.Misc.CALL_METHOD_DEL_MISC);
    }

    public SimpleSettingsFakeProvider(Context context, SQLiteOpenHelper sQLiteOpenHelper) {
        this.mContext = context;
        this.mDbHelper = sQLiteOpenHelper;
    }

    private boolean delMiscSetting(String str, int i2) {
        if (this.mDbHelper.getWritableDatabase().delete(SimpleSettings.Misc.TABLE, SimpleSettings.NameValueTable.SELECTION, new String[]{str, Integer.toString(i2)}) <= 0) {
            return true;
        }
        this.mContext.getContentResolver().notifyChange(Uri.withAppendedPath(SimpleSettings.Misc.CONTENT_URI, str), null, false, i2);
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0045  */
    private String getMiscSetting(SQLiteDatabase sQLiteDatabase, String str, int i2) throws Throwable {
        Cursor cursorQuery;
        Throwable th;
        String string = null;
        try {
            cursorQuery = sQLiteDatabase.query(true, SimpleSettings.Misc.TABLE, SimpleSettings.NameValueTable.VALUE_COLUMN, SimpleSettings.NameValueTable.SELECTION, new String[]{str, Integer.toString(i2)}, null, null, null, null);
            if (cursorQuery != null) {
                try {
                    try {
                        if (cursorQuery.moveToFirst()) {
                            string = cursorQuery.getString(0);
                        }
                    } catch (Exception e2) {
                        e = e2;
                        Log.d(TAG, "cursor create fail ", e);
                        if (cursorQuery != null) {
                        }
                        return string;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (Exception e3) {
            e = e3;
            cursorQuery = null;
        } catch (Throwable th3) {
            cursorQuery = null;
            th = th3;
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
        return string;
    }

    private static int getRequestingUserId(Bundle bundle) {
        int callingUserId = UserHandle.getCallingUserId();
        return bundle != null ? bundle.getInt(SimpleSettings.CALL_METHOD_USER_KEY, callingUserId) : callingUserId;
    }

    private static String getSettingValue(Bundle bundle) {
        if (bundle != null) {
            return bundle.getString("value");
        }
        return null;
    }

    private boolean insertMiscSetting(String str, String str2, int i2) throws Throwable {
        SQLiteDatabase writableDatabase = this.mDbHelper.getWritableDatabase();
        String miscSetting = getMiscSetting(writableDatabase, str, i2);
        if (miscSetting != null && miscSetting.equals(str2)) {
            return true;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", Integer.valueOf(i2));
        contentValues.put("name", str);
        contentValues.put("value", str2);
        writableDatabase.insertWithOnConflict(SimpleSettings.Misc.TABLE, null, contentValues, 5);
        this.mContext.getContentResolver().notifyChange(Uri.withAppendedPath(SimpleSettings.Misc.CONTENT_URI, str), null, false, i2);
        return true;
    }

    private static Bundle packageValueForCallResult(String str) {
        return Bundle.forPair("value", str);
    }

    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        return 0;
    }

    public Bundle call(String str, String str2, Bundle bundle) throws Throwable {
        int requestingUserId = getRequestingUserId(bundle);
        if (str.equals(SimpleSettings.Misc.CALL_METHOD_GET_MISC)) {
            return packageValueForCallResult(getMiscSetting(this.mDbHelper.getReadableDatabase(), str2, requestingUserId));
        }
        if (str.equals(SimpleSettings.Misc.CALL_METHOD_PUT_MISC)) {
            insertMiscSetting(str2, getSettingValue(bundle), requestingUserId);
            return null;
        }
        if (str.equals(SimpleSettings.Misc.CALL_METHOD_DEL_MISC)) {
            delMiscSetting(str2, requestingUserId);
            return null;
        }
        Log.w(TAG, "call() with invalid method: " + str);
        return null;
    }

    public boolean checkMethod(String str) {
        return sMethods.contains(str);
    }

    public boolean checkUri(Uri uri) {
        return sSimpleSettingsMatcher.match(uri) == 1;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        if (sMatcher.match(uri) != 2) {
            return null;
        }
        SQLiteDatabase readableDatabase = this.mDbHelper.getReadableDatabase();
        sQLiteQueryBuilder.setTables(SimpleSettings.Misc.TABLE);
        Cursor cursorQuery = sQLiteQueryBuilder.query(readableDatabase, strArr, str, strArr2, null, null, str2);
        cursorQuery.setNotificationUri(this.mContext.getContentResolver(), uri);
        return cursorQuery;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
