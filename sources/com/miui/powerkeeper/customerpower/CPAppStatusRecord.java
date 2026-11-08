package com.miui.powerkeeper.customerpower;

/* JADX INFO: loaded from: classes.dex */
public class CPAppStatusRecord {
    private long cpuTime;
    private long foregroundTime;
    private long gpsTime;
    private long mobileTime;
    private String procName;
    private int uid;
    private String uidName;
    private String wakelockName;
    private long wakelockTime;
    private String wakeupName;
    private int wakeupTime;

    public CPAppStatusRecord(int i2, String str, String str2, long j2, String str3, long j3, long j4, String str4, int i3, long j5, long j6) {
        this.uid = i2;
        this.uidName = str;
        this.procName = str2;
        this.cpuTime = j2;
        this.wakelockName = str3;
        this.wakelockTime = j3;
        this.foregroundTime = j4;
        this.wakeupName = str4;
        this.wakeupTime = i3;
        this.mobileTime = j5;
        this.gpsTime = j6;
    }

    public long getCpuTime() {
        return this.cpuTime;
    }

    public long getForegroundTime() {
        return this.foregroundTime;
    }

    public long getGpsTime() {
        return this.gpsTime;
    }

    public long getMobileTime() {
        return this.mobileTime;
    }

    public String getProcName() {
        return this.procName;
    }

    public int getUid() {
        return this.uid;
    }

    public String getUidName() {
        return this.uidName;
    }

    public String getWakelockName() {
        return this.wakelockName;
    }

    public long getWakelockTime() {
        return this.wakelockTime;
    }

    public String getWakeupName() {
        return this.wakeupName;
    }

    public int getWakeupTime() {
        return this.wakeupTime;
    }

    public void setCpuTime(long j2) {
        this.cpuTime = j2;
    }

    public void setForegroundTime(long j2) {
        this.foregroundTime = j2;
    }

    public void setGpsTime(long j2) {
        this.gpsTime = j2;
    }

    public void setMobileTime(long j2) {
        this.mobileTime = j2;
    }

    public void setProcName(String str) {
        this.procName = str;
    }

    public void setUid(int i2) {
        this.uid = i2;
    }

    public void setUidName(String str) {
        this.uidName = str;
    }

    public void setWakelockName(String str) {
        this.wakelockName = str;
    }

    public void setWakelockTime(long j2) {
        this.wakelockTime = j2;
    }

    public void setWakeupName(String str) {
        this.wakeupName = str;
    }

    public void setWakeupTime(int i2) {
        this.wakeupTime = i2;
    }
}
