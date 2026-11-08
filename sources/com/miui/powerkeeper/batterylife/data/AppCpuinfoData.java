package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;
import com.miui.powerkeeper.batterylife.TrackData;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class AppCpuinfoData {

    @SerializedName("app_cpu_info")
    public String[] appCpuInfo;

    @SerializedName(TrackData.BL_PKG_NAME)
    public String blPkgName;

    public AppCpuinfoData(String str) {
        this.blPkgName = str;
        this.appCpuInfo = new String[]{"", "", ""};
    }

    public String toString() {
        return "AppCpuinfoData{blPkgName='" + this.blPkgName + "', brightnessBinTimes=" + Arrays.toString(this.appCpuInfo) + '}';
    }

    public AppCpuinfoData(String str, String[] strArr) {
        this.blPkgName = str;
        this.appCpuInfo = strArr;
    }
}
