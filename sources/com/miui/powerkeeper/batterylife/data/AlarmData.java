package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class AlarmData implements Comparable<AlarmData>, Serializable {

    @SerializedName("pkg_name")
    private String pkgName;
    private transient int uid;

    @SerializedName("fg_count")
    private int foregroundCount = 0;

    @SerializedName("bg_count")
    private int backgroundCount = 0;

    @SerializedName("bg_notwakeup_count")
    private int backgroundNotWakeupCount = 0;

    @SerializedName("bg_wakeup_count")
    private int backgroundWakeupCount = 0;

    @SerializedName("bg_notcharge_count")
    private int backgroundUnChargeCount = 0;

    @SerializedName("white_list_app")
    private boolean whitelistApp = false;

    public AlarmData(String str, int i2) {
        this.pkgName = str;
        this.uid = i2;
    }

    public int getBackgroundCount() {
        return this.backgroundCount;
    }

    public int getBackgroundNotWakeupCount() {
        return this.backgroundNotWakeupCount;
    }

    public int getBackgroundUnChargeCount() {
        return this.backgroundUnChargeCount;
    }

    public int getBackgroundWakeupCount() {
        return this.backgroundWakeupCount;
    }

    public int getForegroundCount() {
        return this.foregroundCount;
    }

    public void setBackgroundCount(int i2) {
        this.backgroundCount = i2;
    }

    public void setBackgroundNotWakeupCount(int i2) {
        this.backgroundNotWakeupCount = i2;
    }

    public void setBackgroundUnChargeCount(int i2) {
        this.backgroundUnChargeCount = i2;
    }

    public void setBackgroundWakeupCount(int i2) {
        this.backgroundWakeupCount = i2;
    }

    public void setForegroundCount(int i2) {
        this.foregroundCount = i2;
    }

    public void setWhitelistApp(boolean z2) {
        this.whitelistApp = z2;
    }

    public String toString() {
        return "AlarmData{pkgName='" + this.pkgName + "', uid=" + this.uid + ", foregroundCount=" + this.foregroundCount + ", backgroundCount=" + this.backgroundCount + ", backgroundNotWakeupCount=" + this.backgroundNotWakeupCount + ", backgroundWakeupCount=" + this.backgroundWakeupCount + ", backgroundUnChargeCount=" + this.backgroundUnChargeCount + ", whitelistApp=" + this.whitelistApp + '}';
    }

    @Override // java.lang.Comparable
    public int compareTo(AlarmData alarmData) {
        return Long.compare(alarmData.backgroundCount, this.backgroundCount);
    }
}
