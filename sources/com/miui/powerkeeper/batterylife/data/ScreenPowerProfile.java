package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes.dex */
public class ScreenPowerProfile {

    @SerializedName("pkg_name")
    public String pkgName;

    @SerializedName("screen_full")
    public double screenFullMah;

    @SerializedName("screen_hbm")
    public double screenHbm;

    @SerializedName("screen_low")
    public double screenLowMah;

    public String toString() {
        return "ScreenPowerProfile{pkgName='" + this.pkgName + "', screenLow=" + this.screenLowMah + ", screenFull=" + this.screenFullMah + ", screenHbm=" + this.screenHbm + '}';
    }
}
