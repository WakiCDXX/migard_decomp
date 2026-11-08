package com.miui.powerkeeper.customerpower;

/* JADX INFO: loaded from: classes.dex */
public class CustomerPowerResultBean {
    private int count;
    private String detailName;
    private long time;
    private int type;
    private String typeName;

    public CustomerPowerResultBean(int i2, String str, String str2, long j2, int i3) {
        this.type = i2;
        this.typeName = str;
        this.detailName = str2;
        this.time = j2;
        this.count = i3;
    }

    public int getCount() {
        return this.count;
    }

    public String getDetailName() {
        return this.detailName;
    }

    public long getTime() {
        return this.time;
    }

    public int getType() {
        return this.type;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setCount(int i2) {
        this.count = i2;
    }

    public void setDetailName(String str) {
        this.detailName = str;
    }

    public void setTime(long j2) {
        this.time = j2;
    }

    public void setType(int i2) {
        this.type = i2;
    }

    public void setTypeName(String str) {
        this.typeName = str;
    }
}
