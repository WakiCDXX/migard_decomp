package com.miui.powerkeeper.dfs;

import android.net.TrafficStats;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import com.android.internal.app.IBatteryStats;

/* JADX INFO: loaded from: classes.dex */
public class UsageReader {
    public static final String TAG = "DFS-UsageReader";
    private static final int[] SYSTEM_CPU_FORMAT = {288, 8224, 8224, 8224, 8224, 8224, 8224, 8224};
    private static final long[] mSystemCpuData = new long[7];

    public static long[] getCpuTime() {
        long[] jArr = new long[6];
        long[] jArr2 = mSystemCpuData;
        long jSysconf = Os.sysconf(OsConstants._SC_CLK_TCK);
        long j2 = 1000 / jSysconf;
        if (Process.readProcFile("/proc/stat", SYSTEM_CPU_FORMAT, null, jArr2, null)) {
            jArr[0] = (jArr2[0] + jArr2[1]) * j2;
            jArr[1] = jArr2[2] * j2;
            jArr[2] = jArr2[3] * j2;
            jArr[3] = jArr2[4] * j2;
            jArr[4] = jArr2[5] * j2;
            jArr[5] = jArr2[6] * j2;
        }
        if (DfsUtils.DEBUG_INTERNAL) {
            Log.d(TAG, "getCpuTime jiffyHz = " + jSysconf + ", mJiffyMillis = " + j2 + ", sysCpuIdle = " + jArr2[3] + ", resultUser = " + jArr[0] + ", resultIdle = " + jArr[2]);
        }
        return jArr;
    }

    public static long getMobileRxBytes() {
        return TrafficStats.getMobileRxBytes();
    }

    public static long getMobileRxPackets() {
        return TrafficStats.getMobileRxPackets();
    }

    public static final long getMobileRxTxBytes() {
        return TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
    }

    public static final long getMobileRxTxPackets() {
        return TrafficStats.getMobileTxPackets() + TrafficStats.getMobileRxPackets();
    }

    public static long getMobileTxBytes() {
        return TrafficStats.getMobileTxBytes();
    }

    public static long getMobileTxPackets() {
        return TrafficStats.getMobileTxPackets();
    }

    public static long getPoorSignalStrengthTime(IBatteryStats iBatteryStats, int i2, boolean z2) {
        long wifiSignalStrengthTime;
        long j2 = 0;
        for (int i3 = 0; i3 < 5; i3++) {
            if (z2) {
                try {
                    wifiSignalStrengthTime = (iBatteryStats.getWifiSignalStrengthTime(i3, SystemClock.elapsedRealtime() * 1000, 2) + 500) / 1000;
                } catch (RemoteException e2) {
                    Log.e(TAG, e2.getMessage());
                }
            } else {
                wifiSignalStrengthTime = (iBatteryStats.getPhoneSignalStrengthTime(i3, SystemClock.elapsedRealtime() * 1000, 2) + 500) / 1000;
            }
            j2 += wifiSignalStrengthTime;
            if (DfsUtils.DEBUG_INTERNAL) {
                Log.d(TAG, "poorSignalTime numSignal = 5, ns = " + i3 + ", poorSignalTime = " + wifiSignalStrengthTime + ", totalBadSignalTime = " + j2 + ", isWifi = " + z2);
            }
            if (i3 == i2) {
                return j2 / 1000;
            }
        }
        return j2 / 1000;
    }

    public static long getTotalRxBytes() {
        return TrafficStats.getTotalRxBytes();
    }

    public static long getTotalRxPackets() {
        return TrafficStats.getTotalRxPackets();
    }

    public static final long getTotalRxTxBytes() {
        return TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes();
    }

    public static final long getTotalRxTxPackets() {
        return TrafficStats.getTotalTxPackets() + TrafficStats.getTotalRxPackets();
    }

    public static long getTotalTxBytes() {
        return TrafficStats.getTotalTxBytes();
    }

    public static long getTotalTxPackets() {
        return TrafficStats.getTotalTxPackets();
    }
}
