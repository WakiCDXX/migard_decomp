package com.miui.powerkeeper.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import com.google.android.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerConfigureHelper implements Serializable {
    private static final long serialVersionUID = 1;
    private static final String TAG = PowerCheckerConfigureHelper.class.getSimpleName();
    private static boolean DEBUG = Build.IS_DEBUGGABLE;
    private long id = -1;
    private int uid = -1;
    private String packageName = null;
    private int type = -1;
    private int action = 0;
    private int priority = -1;
    private String version = null;
    private long record_time = 0;
    private int flag = 0;

    public static List<PowerCheckerConfigureHelper> createFromTable(Context context) {
        Cursor cursorQuery = context.getContentResolver().query(PowerCheckerConfigure.CONTENT_URI, null, null, null, null);
        List<PowerCheckerConfigureHelper> listCreateFromTable = createFromTable(cursorQuery);
        cursorQuery.close();
        return listCreateFromTable;
    }

    public int getAction() {
        return this.action;
    }

    public long getFlag() {
        return this.flag;
    }

    public long getId() {
        return this.id;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getPriority() {
        return this.priority;
    }

    public long getRecordTime() {
        return this.record_time;
    }

    public int getType() {
        return this.type;
    }

    public int getUid() {
        return this.uid;
    }

    public String getVersion() {
        return this.version;
    }

    public void setAction(int i2) {
        this.action = i2;
    }

    public void setFlag(long j2) {
        this.flag = this.flag;
    }

    public void setId(long j2) {
        this.id = j2;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public void setPriority(int i2) {
        this.priority = i2;
    }

    public void setRecordTime(long j2) {
        this.record_time = j2;
    }

    public void setType(int i2) {
        this.type = i2;
    }

    public void setUid(int i2) {
        this.uid = i2;
    }

    public void setVersion(String str) {
        this.version = str;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        long j2 = this.id;
        if (j2 >= 0) {
            contentValues.put("_id", Long.valueOf(j2));
        }
        contentValues.put("uid", Integer.valueOf(this.uid));
        contentValues.put("package_name", this.packageName);
        contentValues.put("type", Integer.valueOf(this.type));
        contentValues.put(PowerCheckerConfigure.Columns.ACTION, Integer.valueOf(this.action));
        contentValues.put("priority", Integer.valueOf(this.priority));
        contentValues.put("package_version", this.version);
        contentValues.put("record_time", Long.valueOf(this.record_time));
        contentValues.put("flag", Integer.valueOf(this.flag));
        return contentValues;
    }

    public static List<PowerCheckerConfigureHelper> createFromTable(Cursor cursor) {
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        try {
            int columnIndex = cursor.getColumnIndex("_id");
            int columnIndex2 = cursor.getColumnIndex("uid");
            int columnIndex3 = cursor.getColumnIndex("package_name");
            int columnIndex4 = cursor.getColumnIndex("type");
            int columnIndex5 = cursor.getColumnIndex(PowerCheckerConfigure.Columns.ACTION);
            int columnIndex6 = cursor.getColumnIndex("priority");
            int columnIndex7 = cursor.getColumnIndex("package_version");
            int columnIndex8 = cursor.getColumnIndex("record_time");
            int columnIndex9 = cursor.getColumnIndex("flag");
            while (cursor.moveToNext()) {
                PowerCheckerConfigureHelper powerCheckerConfigureHelper = new PowerCheckerConfigureHelper();
                powerCheckerConfigureHelper.id = cursor.getLong(columnIndex);
                powerCheckerConfigureHelper.uid = cursor.getInt(columnIndex2);
                powerCheckerConfigureHelper.packageName = cursor.getString(columnIndex3);
                powerCheckerConfigureHelper.type = cursor.getInt(columnIndex4);
                powerCheckerConfigureHelper.action = cursor.getInt(columnIndex5);
                powerCheckerConfigureHelper.priority = cursor.getInt(columnIndex6);
                powerCheckerConfigureHelper.version = cursor.getString(columnIndex7);
                powerCheckerConfigureHelper.record_time = cursor.getLong(columnIndex8);
                powerCheckerConfigureHelper.flag = cursor.getInt(columnIndex9);
                arrayListNewArrayList.add(powerCheckerConfigureHelper);
            }
        } catch (Exception e2) {
            Log.e(TAG, "createFromTable" + e2);
        }
        return arrayListNewArrayList;
    }
}
