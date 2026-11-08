package com.miui.powerkeeper.dfs;

import android.annotation.NonNull;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CloudData {
    private static final int DEFAULT_APP_USAGE_ABNORMAL;
    private static final int DEFAULT_APP_USAGE_CHECK_TIME;
    private static final int DEFAULT_CURRENT;
    private static final int DEFAULT_CURRENT_AOD;
    private static final int DEFAULT_DEVICE_IDLE_RATIO_ALL;
    private static final int DEFAULT_DEVICE_IDLE_RATIO_DEEP;
    private static final int DEFAULT_DURATION;
    private static final int DEFAULT_FLASHLIGHT;
    private static final int DEFAULT_POOR_SIGNAL_RATIO;
    private static final int DEFAULT_RXTX_BYTE_RATE;
    private static final int DEFAULT_RXTX_PACKET_RATE;
    private static final int DEFAULT_SUBSYSTEM_RATIO;
    private static final int DEFAULT_WAKELOCK;
    private static final int DEFAULT_WAKEUP;
    public static final String TAG = "DFS-CloudData";
    public int maxWakeupCountPerHour = DEFAULT_WAKEUP;
    public int maxWakelockTimePerHour = DEFAULT_WAKELOCK;
    public int abnormalCurrent = DEFAULT_CURRENT;
    public int abnormalCurrentAod = DEFAULT_CURRENT_AOD;
    public long minDurationMs = DEFAULT_DURATION;
    public int flashlightMin = DEFAULT_FLASHLIGHT;
    public int deviceIdleRatioAll = DEFAULT_DEVICE_IDLE_RATIO_ALL;
    public int deviceIdleRatioDeep = DEFAULT_DEVICE_IDLE_RATIO_DEEP;
    public int subsystemRatio = DEFAULT_SUBSYSTEM_RATIO;
    public int poorSignalRatio = DEFAULT_POOR_SIGNAL_RATIO;
    public int appUsageAbnormalThreshold = DEFAULT_APP_USAGE_ABNORMAL;
    public int appUsageAbnormalCheck = DEFAULT_APP_USAGE_CHECK_TIME;
    public int rxtxByteRate = DEFAULT_RXTX_BYTE_RATE;
    public int rxtxPacketRate = DEFAULT_RXTX_PACKET_RATE;
    private boolean bUseCloudConfig = false;
    public float factorBit = 1.0f;

    static {
        boolean z2 = DfsUtils.DEBUG;
        DEFAULT_CURRENT = z2 ? 5 : 25;
        DEFAULT_WAKEUP = z2 ? 1 : 100;
        DEFAULT_WAKELOCK = z2 ? 1 : 5;
        DEFAULT_CURRENT_AOD = z2 ? 20 : 45;
        DEFAULT_POOR_SIGNAL_RATIO = z2 ? 5 : 25;
        DEFAULT_RXTX_BYTE_RATE = z2 ? 1024 : 51200;
        DEFAULT_RXTX_PACKET_RATE = z2 ? 10 : 50;
        DEFAULT_SUBSYSTEM_RATIO = z2 ? 100 : 20;
        DEFAULT_FLASHLIGHT = z2 ? 1 : 3;
        DEFAULT_DEVICE_IDLE_RATIO_ALL = z2 ? 99 : 60;
        DEFAULT_DEVICE_IDLE_RATIO_DEEP = z2 ? 99 : 40;
        DEFAULT_APP_USAGE_ABNORMAL = z2 ? 5 : 100;
        DEFAULT_APP_USAGE_CHECK_TIME = z2 ? 20 : 900;
        DEFAULT_DURATION = z2 ? 120000 : 14400000;
    }

    private float getFactor(int i2) {
        float f2;
        float f3;
        if (i2 == 2) {
            f2 = 2.0f;
            f3 = this.factorBit;
        } else if (i2 == 1) {
            f2 = 4.0f;
            f3 = this.factorBit;
        } else {
            f2 = 1.0f;
            f3 = this.factorBit;
        }
        return f3 * f2;
    }

    public int getAbnormalCurrent(int i2) {
        float f2;
        float factor;
        if (this.bUseCloudConfig) {
            f2 = this.abnormalCurrent;
            factor = getFactor(i2);
        } else {
            f2 = DEFAULT_CURRENT;
            factor = getFactor(i2);
        }
        return (int) (f2 * factor);
    }

    public int getAbnormalCurrentAod(int i2) {
        float f2;
        float factor;
        if (this.bUseCloudConfig) {
            f2 = this.abnormalCurrentAod;
            factor = getFactor(i2);
        } else {
            f2 = DEFAULT_CURRENT_AOD;
            factor = getFactor(i2);
        }
        return (int) (f2 * factor);
    }

    public int getAppUsageAbnormalCheck() {
        return this.bUseCloudConfig ? this.appUsageAbnormalCheck : DEFAULT_APP_USAGE_CHECK_TIME;
    }

    public int getAppUsageAbnormalThreshold() {
        return this.bUseCloudConfig ? this.appUsageAbnormalThreshold : DEFAULT_APP_USAGE_ABNORMAL;
    }

    public int getDeviceIdleRatioAll() {
        return this.bUseCloudConfig ? this.deviceIdleRatioAll : DEFAULT_DEVICE_IDLE_RATIO_ALL;
    }

    public int getDeviceIdleRatioDeep() {
        return this.bUseCloudConfig ? this.deviceIdleRatioDeep : DEFAULT_CURRENT_AOD;
    }

    public int getFlashlightMin() {
        return this.bUseCloudConfig ? this.flashlightMin : DEFAULT_FLASHLIGHT;
    }

    public int getMaxWakelockTimePerHour(int i2) {
        float f2;
        float factor;
        if (this.bUseCloudConfig) {
            f2 = this.maxWakelockTimePerHour;
            factor = getFactor(i2);
        } else {
            f2 = DEFAULT_WAKELOCK;
            factor = getFactor(i2);
        }
        return (int) (f2 * factor);
    }

    public int getMaxWakeupCountPerHour(int i2) {
        float f2;
        float factor;
        if (this.bUseCloudConfig) {
            f2 = this.maxWakeupCountPerHour;
            factor = getFactor(i2);
        } else {
            f2 = DEFAULT_WAKEUP;
            factor = getFactor(i2);
        }
        return (int) (f2 * factor);
    }

    public long getMinDurationMs() {
        return this.bUseCloudConfig ? this.minDurationMs : DEFAULT_DURATION;
    }

    public int getPoorSignalRatio(int i2) {
        float f2;
        float factor;
        if (this.bUseCloudConfig) {
            f2 = this.poorSignalRatio;
            factor = getFactor(i2);
        } else {
            f2 = DEFAULT_POOR_SIGNAL_RATIO;
            factor = getFactor(i2);
        }
        return (int) (f2 * factor);
    }

    public int getRxtxByteRate(int i2) {
        float f2;
        float factor;
        if (this.bUseCloudConfig) {
            f2 = this.rxtxByteRate;
            factor = getFactor(i2);
        } else {
            f2 = DEFAULT_RXTX_BYTE_RATE;
            factor = getFactor(i2);
        }
        return (int) (f2 * factor);
    }

    public int getRxtxPacketRate(int i2) {
        float f2;
        float factor;
        if (this.bUseCloudConfig) {
            f2 = this.rxtxPacketRate;
            factor = getFactor(i2);
        } else {
            f2 = DEFAULT_RXTX_PACKET_RATE;
            factor = getFactor(i2);
        }
        return (int) (f2 * factor);
    }

    public int getSubsystemRatio() {
        return this.bUseCloudConfig ? this.subsystemRatio : DEFAULT_SUBSYSTEM_RATIO;
    }

    public void reset() {
        this.bUseCloudConfig = false;
    }

    @NonNull
    public String toString() {
        String str = "CloudData(Default){factor=(" + getFactor(0) + "," + getFactor(1) + "," + getFactor(2) + "), abnormalCurrent=" + DEFAULT_CURRENT + ", abnormalCurrentAod=" + DEFAULT_CURRENT_AOD + ", maxWakeupCountPerHour=" + DEFAULT_WAKEUP + ", maxWakelockTimePerHour=" + DEFAULT_WAKELOCK + ", minDurationMs=" + DEFAULT_DURATION + ", flashlightMin=" + DEFAULT_FLASHLIGHT + ", deviceIdleRatioAll=" + DEFAULT_DEVICE_IDLE_RATIO_ALL + ", deviceIdleRatioDeep=" + DEFAULT_DEVICE_IDLE_RATIO_DEEP + ", subsystemRatio=" + DEFAULT_SUBSYSTEM_RATIO + ", poorSignalRatio=" + DEFAULT_POOR_SIGNAL_RATIO + ", appUsageAbnormalThreshold=" + DEFAULT_APP_USAGE_ABNORMAL + ", appUsageAbnormalCheck=" + DEFAULT_APP_USAGE_CHECK_TIME + ", rxtxByteRate=" + DEFAULT_RXTX_BYTE_RATE + ", rxtxPacketRate=" + DEFAULT_RXTX_PACKET_RATE + '}';
        if (!this.bUseCloudConfig) {
            return str;
        }
        return str + "\nCloudData(cloud){,abnormalCurrent=" + this.abnormalCurrent + ", abnormalCurrentAod=" + this.abnormalCurrentAod + ", maxWakeupCountPerHour=" + this.maxWakeupCountPerHour + ", maxWakelockTimePerHour=" + this.maxWakelockTimePerHour + ", minDurationMs=" + this.minDurationMs + ", flashlightMin=" + this.flashlightMin + ", deviceIdleRatioAll=" + this.deviceIdleRatioAll + ", deviceIdleRatioDeep=" + this.deviceIdleRatioDeep + ", subsystemRatio=" + this.subsystemRatio + ", poorSignalRatio=" + this.poorSignalRatio + ", appUsageAbnormalThreshold=" + this.appUsageAbnormalThreshold + ", appUsageAbnormalCheck=" + this.appUsageAbnormalCheck + ", rxtxByteRate=" + this.rxtxByteRate + ", rxtxPacketRate=" + this.rxtxPacketRate + '}';
    }

    public void updateCloudConfig(JSONObject jSONObject) {
        try {
            if (jSONObject.has("minDurationMs")) {
                this.minDurationMs = jSONObject.getLong("minDurationMs");
            }
            if (jSONObject.has("maxWakelockTimePerHour")) {
                this.maxWakelockTimePerHour = jSONObject.getInt("maxWakelockTimePerHour");
            }
            if (jSONObject.has("maxWakeupCountPerHour")) {
                this.maxWakeupCountPerHour = jSONObject.getInt("maxWakeupCountPerHour");
            }
            if (jSONObject.has("abnormalCurrent")) {
                this.abnormalCurrent = jSONObject.getInt("abnormalCurrent");
            }
            if (jSONObject.has("abnormalCurrentAod")) {
                this.abnormalCurrentAod = jSONObject.getInt("abnormalCurrentAod");
            }
            if (jSONObject.has("flashlightMin")) {
                this.flashlightMin = jSONObject.getInt("flashlightMin");
            }
            if (jSONObject.has("deviceIdleRatioAll")) {
                this.deviceIdleRatioAll = jSONObject.getInt("deviceIdleRatioAll");
            }
            if (jSONObject.has("deviceIdleRatioDeep")) {
                this.deviceIdleRatioDeep = jSONObject.getInt("deviceIdleRatioDeep");
            }
            if (jSONObject.has("subsystemRatio")) {
                this.subsystemRatio = jSONObject.getInt("subsystemRatio");
            }
            if (jSONObject.has("poorSignalRatio")) {
                this.poorSignalRatio = jSONObject.getInt("poorSignalRatio");
            }
            if (jSONObject.has("appUsageAbnormalThreshold")) {
                this.appUsageAbnormalThreshold = jSONObject.getInt("appUsageAbnormalThreshold");
            }
            if (jSONObject.has("appUsageAbnormalCheck")) {
                this.appUsageAbnormalCheck = jSONObject.getInt("appUsageAbnormalCheck");
            }
            Log.d(TAG, toString());
            this.bUseCloudConfig = true;
        } catch (JSONException unused) {
            Log.d(TAG, "Cloud config parse error. CloudData=" + this);
        }
    }

    public String toString(int i2) {
        return "CloudData(scene=" + i2 + "){factorBit=" + this.factorBit + ", factor=" + getFactor(i2) + ", abnormalCurrent=" + getAbnormalCurrent(i2) + ", abnormalCurrentAod=" + getAbnormalCurrentAod(i2) + ", maxWakeupCountPerHour=" + getMaxWakeupCountPerHour(i2) + ", maxWakelockTimePerHour=" + getMaxWakelockTimePerHour(i2) + ", minDurationMs=" + getMinDurationMs() + ", flashlightMin=" + getFlashlightMin() + ", deviceIdleRatioAll=" + getDeviceIdleRatioAll() + ", deviceIdleRatioDeep=" + getDeviceIdleRatioDeep() + ", subsystemRatio=" + getSubsystemRatio() + ", poorSignalRatio=" + getPoorSignalRatio(i2) + ", appUsageAbnormalThreshold=" + getAppUsageAbnormalThreshold() + ", appUsageAbnormalCheck=" + getAppUsageAbnormalCheck() + ", rxtxByteRate=" + getRxtxByteRate(i2) + ", rxtxPacketRate=" + getRxtxPacketRate(i2) + '}';
    }
}
