package com.miui.powerkeeper.event;

/* JADX INFO: loaded from: classes.dex */
public class EventLog {
    private final String mLog;
    private final int mTagId;

    public EventLog(String str, int i2) {
        this.mLog = str;
        this.mTagId = i2;
    }

    public Integer getInteger(int i2) {
        String[] strArrSplit = this.mLog.split(",");
        if (i2 >= strArrSplit.length) {
            return null;
        }
        for (int i3 = 0; i3 < strArrSplit[i2].length(); i3++) {
            if (strArrSplit[i2].charAt(i3) < '0' || strArrSplit[i2].charAt(i3) > '9') {
                return null;
            }
        }
        return Integer.valueOf(strArrSplit[i2]);
    }

    public Long getLong(int i2) {
        String[] strArrSplit = this.mLog.split(",");
        if (i2 >= strArrSplit.length) {
            return null;
        }
        for (int i3 = 0; i3 < strArrSplit[i2].length(); i3++) {
            if (strArrSplit[i2].charAt(i3) < '0' || strArrSplit[i2].charAt(i3) > '9') {
                return null;
            }
        }
        return Long.valueOf(strArrSplit[i2]);
    }

    public String getString(int i2) {
        String[] strArrSplit = this.mLog.split(",");
        if (i2 >= strArrSplit.length) {
            return null;
        }
        return strArrSplit[i2];
    }

    public int getTag() {
        return this.mTagId;
    }
}
