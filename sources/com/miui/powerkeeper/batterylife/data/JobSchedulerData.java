package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class JobSchedulerData implements Comparable<JobSchedulerData>, Serializable {

    @SerializedName("pkg_name")
    private String pkgName;
    private transient int uid;

    @SerializedName("fg_count")
    private int foregroundCount = 0;

    @SerializedName("bg_count")
    private int backgroundCount = 0;

    @SerializedName("bg_switch_count")
    private int backgroundSwitchSwitchCount = 0;

    @SerializedName("fg_all_time")
    private long foregroundAllTime = 0;

    @SerializedName("bg_all_time")
    private long backgroundAllTime = 0;

    @SerializedName("bg_notcharge_count")
    private long backgroundUnChargeCount = 0;

    @SerializedName("bg_notcharge_time")
    private long backgroundUnChargeAllTime = 0;

    @SerializedName("white_list_app")
    private boolean whitelistApp = false;

    public JobSchedulerData(String str, int i2) {
        this.pkgName = str;
        this.uid = i2;
    }

    public long getBackgroundAllTime() {
        return this.backgroundAllTime;
    }

    public int getBackgroundCount() {
        return this.backgroundCount;
    }

    public int getBackgroundSwitchSwitchCount() {
        return this.backgroundSwitchSwitchCount;
    }

    public long getBackgroundUnChargeAllTime() {
        return this.backgroundUnChargeAllTime;
    }

    public long getBackgroundUnChargeCount() {
        return this.backgroundUnChargeCount;
    }

    public long getForegroundAllTime() {
        return this.foregroundAllTime;
    }

    public int getForegroundCount() {
        return this.foregroundCount;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public int getUid() {
        return this.uid;
    }

    public void setBackgroundAllTime(long j2) {
        this.backgroundAllTime = j2;
    }

    public void setBackgroundCount(int i2) {
        this.backgroundCount = i2;
    }

    public void setBackgroundSwitchSwitchCount(int i2) {
        this.backgroundSwitchSwitchCount = i2;
    }

    public void setBackgroundUnChargeAllTime(long j2) {
        this.backgroundUnChargeAllTime = j2;
    }

    public void setBackgroundUnChargeCount(long j2) {
        this.backgroundUnChargeCount = j2;
    }

    public void setForegroundAllTime(long j2) {
        this.foregroundAllTime = j2;
    }

    public void setForegroundCount(int i2) {
        this.foregroundCount = i2;
    }

    public void setPkgName(String str) {
        this.pkgName = str;
    }

    public void setUid(int i2) {
        this.uid = i2;
    }

    public void setWhitelistApp(boolean z2) {
        this.whitelistApp = z2;
    }

    public String toString() {
        return "JobSchedulerData{pkgName='" + this.pkgName + "', uid=" + this.uid + ", foregroundCount=" + this.foregroundCount + ", backgroundCount=" + this.backgroundCount + ", backgroundSwitchSwitchCount=" + this.backgroundSwitchSwitchCount + ", foregroundAllTime=" + this.foregroundAllTime + ", backgroundAllTime=" + this.backgroundAllTime + ", backgroundUnChargeCount=" + this.backgroundUnChargeCount + ", backgroundUnChargeAllTime=" + this.backgroundUnChargeAllTime + ", whitelistApp=" + this.whitelistApp + '}';
    }

    @Override // java.lang.Comparable
    public int compareTo(JobSchedulerData jobSchedulerData) {
        return Long.compare(jobSchedulerData.backgroundAllTime, this.backgroundAllTime);
    }
}
