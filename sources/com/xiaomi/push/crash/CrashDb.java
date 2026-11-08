package com.xiaomi.push.crash;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.push.extension.LogPools;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class CrashDb {
    private static final String DB_NAME = "crash.db";
    private static final int DB_VERSION = 2;
    private static final String EMPTY_TEXT = "";
    private static final String ORDER_BY_STACK_AND_PRIORITY = "CASE WHEN stack <> '' THEN 1 ELSE 0 END DESC,pri DESC";
    private static final String SELECT_COUNT = "SELECT COUNT(*) FROM records";
    private static final int STATUS_INIT = 0;
    private static final int STATUS_UPLOADED = 1;
    public static final int TYPE_ANR = 1;
    public static final int TYPE_CRASH = 0;
    private static final String WHERE_HAS_STACK = "stack <> ''";
    private static final String WHERE_NEED_UPLOAD = "status = 0";
    private Context mAppContext;
    private SQLiteDatabase mDb;

    static class DbException extends Exception {
        public DbException(String str) {
            super(str);
        }

        public DbException(String str, Throwable th) {
            super(str, th);
        }
    }

    private static class DbSqlite extends SQLiteOpenHelper {
        static final String DB_VERSION_KEY = "key_db_ver";
        static final String DB_VERSION_SP_NAME = "sp_crash_db_ver";
        int mNewVersion;
        int mOldVersion;

        public DbSqlite(Context context) {
            super(context, CrashDb.DB_NAME, (SQLiteDatabase.CursorFactory) null, 1);
            initAndUpdateVersion(context);
        }

        private void createDatable(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS records");
                sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS records(_id INTEGER PRIMARY KEY AUTOINCREMENT,ap_ver INTEGER,an_ver INTEGER,ro_ver INTEGER,ro_name TEXT,se_pa TEXT DEFAULT '',type INTEGER,pri INTEGER,stack TEXT,stack_sn TEXT,time INTEGER,status INTEGER,ro_type TEXT DEFAULT '',cnt INTEGER  DEFAULT 1,etype TEXT  DEFAULT '',mod_time INTEGER  DEFAULT 0)");
                sQLiteDatabase.setTransactionSuccessful();
                LogPools.CrashLog.i("create table succ");
            } catch (Throwable th) {
                try {
                    LogPools.CrashLog.w("create table error " + th);
                } finally {
                    sQLiteDatabase.endTransaction();
                }
            }
        }

        private void initAndUpdateVersion(Context context) {
            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences(DB_VERSION_SP_NAME, 0);
                this.mOldVersion = sharedPreferences.getInt(DB_VERSION_KEY, 1);
                this.mNewVersion = 2;
                sharedPreferences.edit().putInt(DB_VERSION_KEY, this.mNewVersion).apply();
            } catch (Throwable th) {
                LogPools.CrashLog.w("update ver error " + th);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            LogPools.CrashLog.i("should create db");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
            LogPools.CrashLog.w("warn downgrade " + i2 + " " + i3);
            try {
                super.onDowngrade(sQLiteDatabase, i2, i3);
            } catch (Throwable th) {
                LogPools.CrashLog.w("downgrade error " + th);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onOpen(SQLiteDatabase sQLiteDatabase) {
            super.onOpen(sQLiteDatabase);
            if (this.mOldVersion != this.mNewVersion) {
                LogPools.CrashLog.w("ver changed, recreate db " + this.mOldVersion + " " + this.mNewVersion);
                createDatable(sQLiteDatabase);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
            LogPools.CrashLog.w("warn upgrade " + i2 + " " + i3);
        }
    }

    interface DbTables extends BaseColumns {
        public static final String COL_ANDROID_VERSION = "an_ver";
        public static final String COL_APP_VERSION = "ap_ver";
        public static final String COL_COUNT = "cnt";
        public static final String COL_CREATE_TIME = "time";
        public static final String COL_EXCEPTION_TYPE = "etype";
        public static final String COL_MODIFIED_TIME = "mod_time";
        public static final String COL_PRIORITY = "pri";
        public static final String COL_ROM_NAME = "ro_name";
        public static final String COL_ROM_TYPE = "ro_type";
        public static final String COL_ROM_VERSION = "ro_ver";

        @Deprecated
        public static final String COL_SECRET_PARAMS = "se_pa";
        public static final String COL_STACK = "stack";
        public static final String COL_STACK_SIGN = "stack_sn";
        public static final String COL_STATUS = "status";
        public static final String COL_TYPE = "type";
        public static final String TABLE_RECORDS = "records";
    }

    public CrashDb(Context context) {
        this.mAppContext = context.getApplicationContext();
        this.mDb = new DbSqlite(context).getWritableDatabase();
    }

    private String buildWhereIdIn(String str) {
        return "_id IN (" + str + Constants.SEPARATOR_RIGHT_PARENTESIS;
    }

    private boolean containsStack(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("stack_sn = '");
        sb.append(str);
        sb.append("' AND ");
        sb.append(WHERE_HAS_STACK);
        return queryCount(sb.toString()) > 0;
    }

    private List<RecordBean> cursorToList(Cursor cursor) {
        String secretInfos = CrashUtils.getSecretInfos(this.mAppContext);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        do {
            RecordBean recordBean = new RecordBean();
            recordBean._id = cursor.getLong(cursor.getColumnIndex("_id"));
            recordBean.appVersion = cursor.getLong(cursor.getColumnIndex(DbTables.COL_APP_VERSION));
            recordBean.androidVersion = cursor.getInt(cursor.getColumnIndex(DbTables.COL_ANDROID_VERSION));
            recordBean.romVersion = cursor.getInt(cursor.getColumnIndex(DbTables.COL_ROM_VERSION));
            recordBean.romName = cursor.getString(cursor.getColumnIndex(DbTables.COL_ROM_NAME));
            recordBean.romType = cursor.getString(cursor.getColumnIndex(DbTables.COL_ROM_TYPE));
            recordBean.secretParams = secretInfos;
            recordBean.type = cursor.getInt(cursor.getColumnIndex("type"));
            recordBean.priority = cursor.getInt(cursor.getColumnIndex(DbTables.COL_PRIORITY));
            recordBean.stack = cursor.getString(cursor.getColumnIndex(DbTables.COL_STACK));
            recordBean.stackSign = cursor.getString(cursor.getColumnIndex(DbTables.COL_STACK_SIGN));
            recordBean.count = cursor.getLong(cursor.getColumnIndex(DbTables.COL_COUNT));
            recordBean.eType = cursor.getString(cursor.getColumnIndex(DbTables.COL_EXCEPTION_TYPE));
            recordBean.createTime = cursor.getLong(cursor.getColumnIndex("time"));
            recordBean.modifiedTime = cursor.getLong(cursor.getColumnIndex(DbTables.COL_MODIFIED_TIME));
            recordBean.status = cursor.getInt(cursor.getColumnIndex("status"));
            arrayList.add(recordBean);
        } while (cursor.moveToNext());
        return arrayList;
    }

    private int doDelete(String str) {
        return this.mDb.delete("records", str, null);
    }

    private void doUpdateBatch(String str, String str2) throws DbException {
        this.mDb.beginTransaction();
        try {
            try {
                if (!TextUtils.isEmpty(str)) {
                    int iDoDelete = doDelete(buildWhereIdIn(str));
                    LogPools.CrashLog.d("do batch del: " + str + "=" + iDoDelete);
                }
                if (!TextUtils.isEmpty(str2)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("status", (Integer) 1);
                    int iUpdate = this.mDb.update("records", contentValues, buildWhereIdIn(str2), null);
                    LogPools.CrashLog.d("do batch upd: " + str2 + "=" + iUpdate);
                }
                this.mDb.setTransactionSuccessful();
                this.mDb.endTransaction();
            } catch (Exception e2) {
                throw new DbException("update", e2);
            }
        } catch (Throwable th) {
            this.mDb.endTransaction();
            throw th;
        }
    }

    private String getExceptionType(String str) {
        try {
            String str2 = str.split("#n")[0];
            if (str2.contains("android.os.DeadSystemException")) {
                return "android.os.DeadSystemException";
            }
            return str2.contains(":") ? str2.substring(0, str2.indexOf(":")) : str2;
        } catch (Exception e2) {
            LogPools.CrashLog.w("get etype error " + e2);
            return "";
        }
    }

    private long queryCount(String str) {
        Cursor cursorRawQuery = null;
        try {
            String str2 = SELECT_COUNT;
            if (!TextUtils.isEmpty(str)) {
                str2 = SELECT_COUNT + " WHERE " + str;
            }
            cursorRawQuery = this.mDb.rawQuery(str2, null);
            if (cursorRawQuery != null && cursorRawQuery.moveToFirst()) {
                long j2 = cursorRawQuery.getLong(0);
                cursorRawQuery.close();
                return j2;
            }
            if (cursorRawQuery == null) {
                return -1L;
            }
        } catch (Exception unused) {
            if (cursorRawQuery == null) {
                return -1L;
            }
        } catch (Throwable th) {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            throw th;
        }
        cursorRawQuery.close();
        return -1L;
    }

    private boolean tryFindAndUpdateCount(ContentValues contentValues) {
        Cursor cursor = null;
        try {
            String[] strArr = new String[contentValues.size()];
            String str = "";
            int i2 = 0;
            for (Map.Entry<String, Object> entry : contentValues.valueSet()) {
                if (i2 > 0) {
                    str = str + " AND ";
                }
                str = str + entry.getKey() + " = ?";
                strArr[i2] = String.valueOf(entry.getValue());
                i2++;
            }
            Cursor cursorQuery = this.mDb.query("records", null, str, strArr, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        long j2 = cursorQuery.getLong(cursorQuery.getColumnIndex("_id"));
                        long j3 = cursorQuery.getLong(cursorQuery.getColumnIndex(DbTables.COL_COUNT));
                        cursorQuery.close();
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(DbTables.COL_COUNT, Long.valueOf(1 + j3));
                        contentValues2.put(DbTables.COL_MODIFIED_TIME, Long.valueOf(System.currentTimeMillis()));
                        int iUpdate = this.mDb.update("records", contentValues2, "_id = " + j2, null);
                        LogPools.CrashLog.d("find and update id=" + j2 + " cnt=" + j3 + " upt=" + iUpdate);
                        boolean z2 = iUpdate > 0;
                        if (!cursorQuery.isClosed()) {
                            cursorQuery.close();
                        }
                        return z2;
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    try {
                        LogPools.CrashLog.w("find and update error " + th);
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                    }
                }
            }
            if (cursorQuery != null && !cursorQuery.isClosed()) {
                cursorQuery.close();
            }
        } catch (Throwable th2) {
            th = th2;
        }
        return false;
    }

    public boolean checkDatas() {
        return queryCount(WHERE_NEED_UPLOAD) > 0;
    }

    public Context getContext() {
        return this.mAppContext;
    }

    public void insert(String str, int i2, int i3) throws DbException {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mDb.beginTransaction();
        try {
            try {
                long jQueryCount = queryCount(null);
                if (jQueryCount >= 500) {
                    int iDoDelete = doDelete("status = 1");
                    LogPools.CrashLog.d("too max count:" + jQueryCount + com.xiaomi.mipush.sdk.Constants.ACCEPT_TIME_SEPARATOR_SERVER + iDoDelete);
                    if (iDoDelete <= 0) {
                        throw new DbException("too max " + jQueryCount);
                    }
                }
                String exceptionType = getExceptionType(str);
                String md5Digest = XMStringUtils.getMd5Digest(str);
                if (containsStack(md5Digest)) {
                    str = "";
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(DbTables.COL_APP_VERSION, Integer.valueOf(CrashUtils.getXmsfVersionCode(getContext())));
                contentValues.put(DbTables.COL_ANDROID_VERSION, Integer.valueOf(CrashUtils.getAndroidVersionCode()));
                contentValues.put(DbTables.COL_ROM_VERSION, Integer.valueOf(CrashUtils.getRomVersionCode(getContext())));
                contentValues.put(DbTables.COL_ROM_NAME, CrashUtils.getRomVersionName());
                contentValues.put(DbTables.COL_ROM_TYPE, CrashUtils.getRomType());
                contentValues.put("type", Integer.valueOf(i2));
                contentValues.put(DbTables.COL_PRIORITY, Integer.valueOf(i3));
                contentValues.put(DbTables.COL_STACK_SIGN, md5Digest);
                contentValues.put("status", (Integer) 0);
                if (!tryFindAndUpdateCount(contentValues)) {
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.putAll(contentValues);
                    contentValues2.put(DbTables.COL_COUNT, (Integer) 1);
                    contentValues2.put(DbTables.COL_STACK, str);
                    contentValues2.put(DbTables.COL_EXCEPTION_TYPE, exceptionType);
                    contentValues2.put("time", Long.valueOf(System.currentTimeMillis()));
                    contentValues2.put(DbTables.COL_MODIFIED_TIME, Long.valueOf(System.currentTimeMillis()));
                    long jInsert = this.mDb.insert("records", null, contentValues2);
                    LogPools.CrashLog.d("insert rec: " + jInsert + " " + md5Digest + " " + str + " " + exceptionType);
                }
                this.mDb.setTransactionSuccessful();
                this.mDb.endTransaction();
            } catch (RuntimeException e2) {
                throw new DbException("insert", e2);
            }
        } catch (Throwable th) {
            this.mDb.endTransaction();
            throw th;
        }
    }

    public List<RecordBean> query(int i2) throws DbException {
        Cursor cursorRawQuery = null;
        try {
            try {
                cursorRawQuery = this.mDb.rawQuery("SELECT * FROM records WHERE status = 0 ORDER BY CASE WHEN stack <> '' THEN 1 ELSE 0 END DESC,pri DESC LIMIT " + i2, null);
                List<RecordBean> listCursorToList = cursorToList(cursorRawQuery);
                if (cursorRawQuery != null) {
                    cursorRawQuery.close();
                }
                LogPools.CrashLog.d("query recs:" + listCursorToList);
                return listCursorToList;
            } catch (Exception e2) {
                throw new DbException("query", e2);
            }
        } catch (Throwable th) {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            throw th;
        }
    }

    public void updateWhenUploaded(List<RecordBean> list, boolean z2) throws DbException {
        if (ToolUtils.isEmptyList(list)) {
            return;
        }
        LogPools.CrashLog.d("upd when " + list);
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        int size = list.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            RecordBean recordBean = list.get(i3);
            StringBuilder sb3 = (z2 && TextUtils.isEmpty(recordBean.stack)) ? sb : sb2;
            if (sb3.length() > 0) {
                sb3.append(",");
            }
            sb3.append(recordBean._id);
            i2++;
            if (i2 >= 100 || i3 == size - 1) {
                doUpdateBatch(sb.toString(), sb2.toString());
                sb.delete(0, sb.length());
                sb2.delete(0, sb2.length());
                i2 = 0;
            }
        }
    }
}
