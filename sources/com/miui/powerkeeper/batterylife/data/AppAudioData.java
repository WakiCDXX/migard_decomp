package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;
import com.miui.powerkeeper.batterylife.TrackData;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class AppAudioData {

    @SerializedName(TrackData.BL_PKG_NAME)
    public String blPkgName;

    @SerializedName("app_volume_bin_time")
    public long[] volumeBinTimes;

    @SerializedName("app_volume_total_time")
    public long volumeBinTotalTimes = 0;

    public AppAudioData(String str, long[] jArr) {
        this.blPkgName = str;
        this.volumeBinTimes = jArr;
        int i2 = 0;
        while (true) {
            long[] jArr2 = this.volumeBinTimes;
            if (i2 >= jArr2.length) {
                return;
            }
            this.volumeBinTotalTimes += jArr2[i2];
            i2++;
        }
    }

    public String toString() {
        return "AppScreenPowerData{blPkgName='" + this.blPkgName + "', volumeBinTimes=" + Arrays.toString(this.volumeBinTimes) + ", volumeBinTotalTimes=" + this.volumeBinTotalTimes + '}';
    }
}
