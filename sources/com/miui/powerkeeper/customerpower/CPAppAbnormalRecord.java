package com.miui.powerkeeper.customerpower;

/* JADX INFO: loaded from: classes.dex */
public class CPAppAbnormalRecord {
    private int appType;
    private int count;
    private String pkgName;
    private String procName;
    private long time;
    private String typeName;
    private int uid;

    public CPAppAbnormalRecord(int i2, String str, String str2, int i3, String str3, long j2, int i4) {
        this.uid = i2;
        this.pkgName = str;
        this.procName = str2;
        this.appType = i3;
        this.typeName = str3;
        this.time = j2;
        this.count = i4;
    }

    public int getAppType() {
        return this.appType;
    }

    public int getCount() {
        return this.count;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public String getProcName() {
        return this.procName;
    }

    public long getTime() {
        return this.time;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public int getUid() {
        return this.uid;
    }

    public void setAppType(int i2) {
        this.appType = i2;
    }

    public void setCount(int i2) {
        this.count = i2;
    }

    public void setPkgName(String str) {
        this.pkgName = str;
    }

    public void setProcName(String str) {
        this.procName = str;
    }

    public void setTime(long j2) {
        this.time = j2;
    }

    public void setTypeName(String str) {
        this.typeName = str;
    }

    public void setUid(int i2) {
        this.uid = i2;
    }
}
