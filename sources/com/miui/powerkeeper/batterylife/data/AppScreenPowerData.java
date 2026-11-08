package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;
import com.miui.powerkeeper.batterylife.TrackData;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class AppScreenPowerData {

    @SerializedName(TrackData.BL_PKG_NAME)
    public String blPkgName;

    @SerializedName("app_power_mah")
    public double powerMah = 0.0d;

    @SerializedName(TrackData.APP_FG_TIME)
    public long fgTimeMs = 0;

    @SerializedName("app_brightness_bin_time")
    public long[] brightnessBinTimes = new long[100];

    @SerializedName("app_brightness_total_time")
    public long brightnessBinTotalTimes = 0;

    public AppScreenPowerData(String str) {
        this.blPkgName = str;
    }

    public String toString() {
        return "AppScreenPowerData{blPkgName='" + this.blPkgName + "', powerMah=" + this.powerMah + ", fgTimeMs=" + this.fgTimeMs + ", brightnessBinTimes=" + Arrays.toString(this.brightnessBinTimes) + ", brightnessBinTotalTimes=" + this.brightnessBinTotalTimes + '}';
    }
}
