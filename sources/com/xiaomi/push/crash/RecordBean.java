package com.xiaomi.push.crash;

import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class RecordBean {
    public long _id;
    public int androidVersion;
    public long appVersion;
    public long count;
    public long createTime;
    public String eType;
    public long modifiedTime;
    public int priority;
    public String romName;
    public String romType;
    public int romVersion;
    public String secretParams;
    public String stack;
    public String stackSign;
    public int status;
    public int type;

    RecordBean() {
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(CrashDb.DbTables.COL_APP_VERSION, this.appVersion);
            jSONObject.put(CrashDb.DbTables.COL_ANDROID_VERSION, this.androidVersion);
            jSONObject.put(CrashDb.DbTables.COL_ROM_VERSION, this.romVersion);
            jSONObject.put(CrashDb.DbTables.COL_ROM_NAME, this.romName);
            jSONObject.put(CrashDb.DbTables.COL_SECRET_PARAMS, this.secretParams);
            jSONObject.put("type", this.type);
            jSONObject.put(CrashDb.DbTables.COL_PRIORITY, this.priority);
            jSONObject.put(CrashDb.DbTables.COL_STACK, this.stack);
            jSONObject.put(CrashDb.DbTables.COL_STACK_SIGN, this.stackSign);
            jSONObject.put(CrashDb.DbTables.COL_ROM_TYPE, this.romType);
            jSONObject.put(CrashDb.DbTables.COL_COUNT, this.count);
            jSONObject.put(CrashDb.DbTables.COL_EXCEPTION_TYPE, this.eType);
            jSONObject.put("time", this.createTime);
            jSONObject.put(CrashDb.DbTables.COL_MODIFIED_TIME, this.modifiedTime);
            return jSONObject;
        } catch (Throwable unused) {
            return null;
        }
    }

    public String toString() {
        return "" + this._id;
    }
}
