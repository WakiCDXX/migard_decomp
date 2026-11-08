package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;
import com.miui.powerkeeper.batterylife.TrackData;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class CpuTimeStatsData {
    public static final String APP_CLUSTER = "app_cluster_";
    public static final String DEVICE_CLUSTER = "device_cluster_";
    public static final String GPU_TOTAL = "gpu_total";

    @SerializedName("cpu_app")
    public Map<String, List<FreqStats>> appClusterMap;

    @SerializedName(TrackData.APP_FG_TIME)
    public long appFgTime;

    @SerializedName("cpu_app_total")
    public Map<String, Long> appTotalMap;

    @SerializedName(TrackData.BL_PKG_NAME)
    public String blPkgName;

    @SerializedName("cpu_device")
    public Map<String, List<FreqStats>> deviceClusterMap;

    @SerializedName("cpu_device_total")
    public Map<String, Long> deviceTotalMap;

    @SerializedName(TrackData.DEVICE_GPU_STATS)
    public List<FreqStats> gpuTimeList;

    @SerializedName(GPU_TOTAL)
    public Map<String, Long> gpuTotalMap;

    public static class FreqStats {

        @SerializedName(TrackData.CPU_FREQ_TIME_IN_STATE_FREQ)
        public String freq;

        @SerializedName("time")
        public long time;

        public FreqStats(String str, long j2) {
            this.freq = str;
            this.time = j2;
        }
    }

    public String toString() {
        return "CpuTimeStatsData{blPkgName='" + this.blPkgName + "', appFgTime=" + this.appFgTime + ", appClusterMap=" + this.appClusterMap + ", deviceClusterMap=" + this.deviceClusterMap + ", gpuTimeList=" + this.gpuTimeList + ", appTotalMap=" + this.appTotalMap + ", deviceTotalMap=" + this.deviceTotalMap + ", gpuTotalMap=" + this.gpuTotalMap + '}';
    }
}
