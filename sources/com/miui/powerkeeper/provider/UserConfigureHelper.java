package com.miui.powerkeeper.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.UserHandle;
import android.util.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UserConfigureHelper implements Serializable {
    private static final long serialVersionUID = -6919461967497580385L;
    private static final String TAG = UserConfigureHelper.class.getSimpleName();
    private static boolean DEBUG = Build.IS_DEBUGGABLE;
    private long id = -1;
    private int userId = -10000;
    private String pkg = null;
    private long lastConfigured = -1;
    private String bgControl = "miuiAuto";
    private int bgDelayMin = -2;

    public static UserConfigureHelper createFirstFromTable(Cursor cursor) {
        List<UserConfigureHelper> listCreateFromTable = createFromTable(cursor);
        if (listCreateFromTable.size() > 0) {
            return listCreateFromTable.get(0);
        }
        return null;
    }

    public static List<UserConfigureHelper> createFromTable(Context context) {
        return createFromTable(context, context.getUserId());
    }

    public static List<String> getNoRestrictApps(Context context) {
        Cursor cursorQuery = context.getContentResolverForUser(UserHandle.SYSTEM).query(UserConfigure.CONTENT_URI, new String[]{"pkgName"}, "userId = ? AND bgControl = ?", new String[]{Integer.toString(0), UserConfigure.BG_CONTROL_NO_RESTRICT}, null);
        ArrayList arrayList = null;
        if (cursorQuery == null) {
            return null;
        }
        try {
            int columnIndex = cursorQuery.getColumnIndex("pkgName");
            while (cursorQuery.moveToNext()) {
                String string = cursorQuery.getString(columnIndex);
                if (arrayList == null) {
                    arrayList = new ArrayList(cursorQuery.getCount());
                }
                arrayList.add(string);
            }
        } catch (Exception e2) {
            Log.e(TAG, "getNoRestrictApps " + e2);
        } finally {
            cursorQuery.close();
        }
        return arrayList;
    }

    public static UserConfigureHelper getUserConfigureHelperByPkg(Context context, String str) {
        return getUserConfigureHelperByPkg(context, context.getUserId(), str);
    }

    public static void updateToTable(Context context, ContentValues contentValues) {
        ContentResolver contentResolverForUser = context.getContentResolverForUser(UserHandle.OWNER);
        if (contentValues.getAsInteger("userId") == null) {
            Log.e(TAG, "Missed userId");
            return;
        }
        if (contentValues.getAsString("pkgName") == null) {
            Log.e(TAG, "Missed pkgName");
        } else if (!contentValues.containsKey("_id")) {
            contentResolverForUser.insert(UserConfigure.CONTENT_URI, contentValues);
        } else {
            contentResolverForUser.update(ContentUris.withAppendedId(UserConfigure.CONTENT_URI, contentValues.getAsLong("_id").longValue()), contentValues, null, null);
        }
    }

    public String getBgControl() {
        return this.bgControl;
    }

    public int getBgDelayMin() {
        return this.bgDelayMin;
    }

    public long getId() {
        return this.id;
    }

    public long getLastConfigured() {
        return this.lastConfigured;
    }

    public String getPackageName() {
        return this.pkg;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setBgControl(String str) {
        this.bgControl = str;
    }

    public void setBgDelayMin(int i2) {
        this.bgDelayMin = i2;
    }

    public void setId(long j2) {
        this.id = j2;
    }

    public void setPackageName(String str) {
        this.pkg = str;
    }

    public void setUserId(int i2) {
        this.userId = i2;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        long j2 = this.id;
        if (j2 >= 0) {
            contentValues.put("_id", Long.valueOf(j2));
        }
        contentValues.put("userId", Integer.valueOf(this.userId));
        contentValues.put("pkgName", this.pkg);
        contentValues.put(UserConfigure.Columns.BG_CONTROL, this.bgControl);
        int i2 = this.bgDelayMin;
        if (i2 >= 0) {
            contentValues.put(UserConfigure.Columns.BG_DELAY_MIN, Integer.valueOf(i2));
        }
        return contentValues;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tuserId: " + this.userId);
        if (this.bgControl != null) {
            sb.append("\tbgControl: " + this.bgControl);
        }
        if (this.bgDelayMin >= 0) {
            sb.append("\tbgDelayMin: " + this.bgDelayMin);
        }
        return sb.toString();
    }

    public static List<UserConfigureHelper> createFromTable(Context context, int i2) {
        Cursor cursorQuery = context.getContentResolverForUser(UserHandle.OWNER).query(UserConfigure.CONTENT_URI, null, "userId = ?", new String[]{Integer.toString(i2)}, null);
        List<UserConfigureHelper> listCreateFromTable = createFromTable(cursorQuery);
        cursorQuery.close();
        return listCreateFromTable;
    }

    public static UserConfigureHelper getUserConfigureHelperByPkg(Context context, int i2, String str) {
        Cursor cursorQuery = context.getContentResolverForUser(UserHandle.OWNER).query(UserConfigure.CONTENT_URI, null, "userId = ? AND pkgName = ?", new String[]{Integer.toString(i2), str}, null);
        UserConfigureHelper userConfigureHelper = new UserConfigureHelper();
        if (cursorQuery == null) {
            return userConfigureHelper;
        }
        if (cursorQuery.getCount() <= 0) {
            cursorQuery.close();
            return userConfigureHelper;
        }
        int columnIndex = cursorQuery.getColumnIndex("_id");
        int columnIndex2 = cursorQuery.getColumnIndex("userId");
        int columnIndex3 = cursorQuery.getColumnIndex("pkgName");
        int columnIndex4 = cursorQuery.getColumnIndex(UserConfigure.Columns.LAST_CONFIGURED);
        int columnIndex5 = cursorQuery.getColumnIndex(UserConfigure.Columns.BG_CONTROL);
        int columnIndex6 = cursorQuery.getColumnIndex(UserConfigure.Columns.BG_DELAY_MIN);
        cursorQuery.moveToFirst();
        userConfigureHelper.id = cursorQuery.getLong(columnIndex);
        userConfigureHelper.userId = cursorQuery.getInt(columnIndex2);
        userConfigureHelper.pkg = cursorQuery.getString(columnIndex3);
        if (!cursorQuery.isNull(columnIndex4)) {
            userConfigureHelper.lastConfigured = cursorQuery.getLong(columnIndex4);
        }
        if (!cursorQuery.isNull(columnIndex5)) {
            userConfigureHelper.bgControl = cursorQuery.getString(columnIndex5);
        }
        if (!cursorQuery.isNull(columnIndex6)) {
            userConfigureHelper.bgDelayMin = cursorQuery.getInt(columnIndex6);
        }
        cursorQuery.close();
        return userConfigureHelper;
    }

    public static List<UserConfigureHelper> createFromTable(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        try {
            int columnIndex = cursor.getColumnIndex("_id");
            int columnIndex2 = cursor.getColumnIndex("userId");
            int columnIndex3 = cursor.getColumnIndex("pkgName");
            int columnIndex4 = cursor.getColumnIndex(UserConfigure.Columns.LAST_CONFIGURED);
            int columnIndex5 = cursor.getColumnIndex(UserConfigure.Columns.BG_CONTROL);
            int columnIndex6 = cursor.getColumnIndex(UserConfigure.Columns.BG_DELAY_MIN);
            while (cursor.moveToNext()) {
                UserConfigureHelper userConfigureHelper = new UserConfigureHelper();
                userConfigureHelper.id = cursor.getLong(columnIndex);
                userConfigureHelper.userId = cursor.getInt(columnIndex2);
                userConfigureHelper.pkg = cursor.getString(columnIndex3);
                if (!cursor.isNull(columnIndex4)) {
                    userConfigureHelper.lastConfigured = cursor.getLong(columnIndex4);
                }
                if (!cursor.isNull(columnIndex5)) {
                    userConfigureHelper.bgControl = cursor.getString(columnIndex5);
                }
                if (!cursor.isNull(columnIndex6)) {
                    userConfigureHelper.bgDelayMin = cursor.getInt(columnIndex6);
                }
                arrayList.add(userConfigureHelper);
            }
        } catch (Exception e2) {
            Log.e(TAG, "createFromTable" + e2);
        }
        return arrayList;
    }
}
