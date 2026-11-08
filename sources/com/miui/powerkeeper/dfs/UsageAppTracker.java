package com.miui.powerkeeper.dfs;

import android.annotation.NonNull;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.miui.powerkeeper.siming.SiMing;
import com.miui.powerkeeper.siming.dasi.MIY;
import com.miui.powerkeeper.siming.shaosi.AppUsageN;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import miui.os.Build;
import miui.process.ForegroundInfo;
import miui.provider.Weather;
import miui.system.R;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class UsageAppTracker {
    public static final int MSG_DELAY_FOR_STABILIZED = 0;
    public static final int REASON_INDEX_RADIO_SIGNAL = 1;
    public static final int REASON_INDEX_WIFI_SIGNAL = 0;
    public static final String TAG = "DFS-AppUsageTracker";
    private final CloudData mCloudData;
    private final Context mContext;
    private ArrayList<AbnormalReason> mDebugReasonList;
    private ForegroundInfo mFgInfo;
    private final Handler mHandler;
    private boolean mOnBattery;
    private volatile boolean mTracking;
    UsageManager mUsageManager;
    private h.c mVpnTimer;
    private boolean mWifiApEnabled;
    public static final String[] REASONS = {"wifi_signal_poor", "radio_signal_poor"};
    public static final ArrayList<String> TRACKING_APPS = new ArrayList<>();
    private final Object mTrackingLock = new Object();
    private TrackingData mTrackingData = null;
    private boolean mScreenOff = false;
    private volatile boolean mVpnEnabled = false;
    private long mWifiApDurationMs = 0;
    private long mWifiApStartTime = 0;

    public static class AbnormalReason {
        public static final int ID_HIGH_CPU_BG_USAGE = 0;
        public static final int ID_HIGH_CPU_LOAD = 7;
        public static final int ID_MOBILE_RXTX = 6;
        public static final int ID_RADIO_SIGNAL_POOR = 4;
        public static final int ID_UNKNOWN = 32;
        public static final int ID_VPN = 1;
        public static final int ID_WIFI_AP = 2;
        public static final int ID_WIFI_RXTX = 5;
        public static final int ID_WIFI_SIGNAL_POOR = 3;
        String reasonDesc;
        int reasonId;
        String[] reasonParams;

        public String toString() {
            return "reasonId=" + this.reasonId + ", reasonDesc=" + this.reasonDesc + ", reasonParams=" + Arrays.toString(this.reasonParams);
        }
    }

    static class TrackingData {
        ArrayList<String> bgCpuHighList = new ArrayList<>();
        long[] cpuTime;
        long mobileRxTxBytes;
        long mobileRxTxPackets;
        long radioActiveTime;
        long radioPoorSignalDuration;
        double startPower;
        long startTime;
        String trackingPkg;
        int trackingUid;
        long wifiActiveTime;
        long wifiPoorSignalDuration;
        long wifiRxTxBytes;
        long wifiRxTxPackets;

        TrackingData() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("mTrackingData = {trackingPkg=");
            sb.append(this.trackingPkg);
            sb.append(";trackingUid=");
            sb.append(this.trackingUid);
            sb.append(";wifiPoorSignalDuration=");
            sb.append(this.wifiPoorSignalDuration);
            sb.append(";wifiActiveTime=");
            sb.append(this.wifiActiveTime);
            sb.append(";radioPoorSignalDuration=");
            sb.append(this.radioPoorSignalDuration);
            sb.append(";radioActiveTime=");
            sb.append(this.radioActiveTime);
            sb.append(";startPower=");
            sb.append(this.startPower);
            sb.append(";startTime=");
            sb.append(this.startTime);
            sb.append(";mobileRxTxBytes=");
            sb.append(this.mobileRxTxBytes);
            sb.append(";mobileRxTxPackets=");
            sb.append(this.mobileRxTxPackets);
            sb.append(";wifiRxTxBytes=");
            sb.append(this.wifiRxTxBytes);
            sb.append(";wifiRxTxPackets=");
            sb.append(this.wifiRxTxPackets);
            if (this.cpuTime != null) {
                sb.append(";cpuTime=[");
                int i2 = 0;
                while (true) {
                    long[] jArr = this.cpuTime;
                    if (i2 >= jArr.length) {
                        break;
                    }
                    sb.append(jArr[i2]);
                    if (i2 != this.cpuTime.length - 1) {
                        sb.append(",");
                    }
                    i2++;
                }
                sb.append("]");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public UsageAppTracker(CloudData cloudData, UsageManager usageManager, Looper looper) {
        this.mOnBattery = false;
        this.mWifiApEnabled = false;
        this.mCloudData = cloudData;
        this.mUsageManager = usageManager;
        Context contextB = PowerKeeperApplication.b();
        this.mContext = contextB;
        TRACKING_APPS.addAll(Arrays.asList(contextB.getResources().getStringArray(Build.IS_INTERNATIONAL_BUILD ? R.array.dfs_app_tracking_international : R.array.dfs_app_tracking)));
        this.mOnBattery = !PowerKeeperManager.getInstance().isCharging();
        WifiManager wifiManager = (WifiManager) contextB.getSystemService(WifiManager.class);
        if (wifiManager != null) {
            this.mWifiApEnabled = wifiManager.isWifiApEnabled();
        }
        this.mHandler = new Handler(looper) { // from class: com.miui.powerkeeper.dfs.UsageAppTracker.1
            @Override // android.os.Handler
            public void handleMessage(@NonNull Message message) {
                UsageAppTracker.this.handleMessage(message);
            }
        };
        UsageMonitor.getInstance(contextB).registerUsageCallback(new UsageMonitor.UsageCallback() { // from class: com.miui.powerkeeper.dfs.d
            @Override // com.miui.powerkeeper.dfs.UsageMonitor.UsageCallback
            public final void used(int i2, Bundle bundle) {
                this.f393a.lambda$new$0(i2, bundle);
            }
        });
    }

    /* JADX WARN: Code duplicated, block: B:17:0x00a2  */
    private void checkPower() {
        float f2;
        int iAddLogBit;
        int i2;
        int iAddLogBit2;
        if (this.mTrackingData == null) {
            return;
        }
        long jB = this.mUsageManager.getmClock().b();
        long jCurrentTimeMillis = System.currentTimeMillis() - this.mTrackingData.startTime;
        if (jCurrentTimeMillis < ((long) this.mCloudData.getAppUsageAbnormalCheck()) * 1000) {
            Log.d(TAG, "Track time is too short. trackingTimeMs=" + jCurrentTimeMillis + ", configTimeMs=" + (this.mCloudData.getAppUsageAbnormalCheck() * 1000));
            return;
        }
        float currentAm = getCurrentAm(this.mTrackingData.startPower - BSHelper.getCurrentBatteryCap(), jCurrentTimeMillis);
        AppUsageN appUsageN = (AppUsageN) SiMing.getInstance().getDivision(104);
        if (appUsageN == null) {
            return;
        }
        float avgCurrent = appUsageN.getAvgCurrent(this.mTrackingData.trackingPkg);
        boolean z2 = DfsUtils.DEBUG;
        if (z2) {
            Log.d(TAG, "avgCurrent=" + currentAm + ", historyCurrent=" + avgCurrent);
            if (avgCurrent <= 0.0f) {
                Log.d(TAG, "No history data, just set to 100mA for debug.");
                f2 = 100.0f;
            } else {
                f2 = avgCurrent;
            }
        } else {
            f2 = avgCurrent;
        }
        if (f2 < 0.0f || currentAm < ((this.mCloudData.getAppUsageAbnormalThreshold() * f2) / 100.0f) + f2) {
            Log.d(TAG, "The current is normal. avgCurrent=" + currentAm + ", historyCurrent=" + f2);
            return;
        }
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        if (batteryStats == null) {
            Log.d(TAG, "startTracking failed! IBatteryStats is null.");
            return;
        }
        if (DfsUtils.VERSION < 4.0f) {
            long poorSignalStrengthTime = UsageReader.getPoorSignalStrengthTime(batteryStats, 2, true) - this.mTrackingData.wifiPoorSignalDuration;
            long poorSignalStrengthTime2 = UsageReader.getPoorSignalStrengthTime(batteryStats, 2, false) - this.mTrackingData.radioPoorSignalDuration;
            if (z2) {
                Log.d(TAG, "startTracking wifiSignalPoorS=" + poorSignalStrengthTime + ", trackingTimeMs=" + jCurrentTimeMillis);
            }
            if (poorSignalStrengthTime > (((long) this.mCloudData.getPoorSignalRatio(1)) * jCurrentTimeMillis) / 100000) {
                MiEventConstructor.getInstance().upToMiSightAppUsageSignalPool(this.mTrackingData.trackingPkg, currentAm, REASONS[0]);
            }
            if (poorSignalStrengthTime2 > (jCurrentTimeMillis * ((long) this.mCloudData.getPoorSignalRatio(1))) / 100000) {
                MiEventConstructor.getInstance().upToMiSightAppUsageSignalPool(this.mTrackingData.trackingPkg, currentAm, REASONS[1]);
                return;
            }
            return;
        }
        ArrayList<AbnormalReason> arrayList = new ArrayList<>();
        AbnormalReason wifiSignalPoor = getWifiSignalPoor(batteryStats, jCurrentTimeMillis);
        if (wifiSignalPoor != null) {
            arrayList.add(wifiSignalPoor);
            iAddLogBit = DfsUtils.addLogBit(2, 0);
        } else {
            iAddLogBit = 0;
        }
        AbnormalReason radioSignalPoor = getRadioSignalPoor(batteryStats, jCurrentTimeMillis);
        if (radioSignalPoor != null) {
            arrayList.add(radioSignalPoor);
            iAddLogBit = DfsUtils.addLogBit(13, DfsUtils.addLogBit(5, iAddLogBit));
        }
        AbnormalReason bgCpuHigh = getBgCpuHigh(this.mTrackingData);
        if (bgCpuHigh != null) {
            arrayList.add(bgCpuHigh);
            iAddLogBit = DfsUtils.addLogBit(17, iAddLogBit);
        }
        AbnormalReason vpnEnabled = getVpnEnabled(jCurrentTimeMillis, jB);
        if (vpnEnabled != null) {
            arrayList.add(vpnEnabled);
            iAddLogBit = DfsUtils.addLogBit(8, iAddLogBit);
        }
        AbnormalReason[] rxTxReason = getRxTxReason(jCurrentTimeMillis, this.mTrackingData);
        AbnormalReason abnormalReason = rxTxReason[0];
        if (abnormalReason != null) {
            arrayList.add(abnormalReason);
            iAddLogBit = DfsUtils.addLogBit(13, DfsUtils.addLogBit(6, DfsUtils.addLogBit(5, iAddLogBit)));
        }
        AbnormalReason abnormalReason2 = rxTxReason[1];
        if (abnormalReason2 != null) {
            arrayList.add(abnormalReason2);
            i2 = 2;
            iAddLogBit = DfsUtils.addLogBit(6, DfsUtils.addLogBit(2, iAddLogBit));
        } else {
            i2 = 2;
        }
        AbnormalReason wifiApEnabled = getWifiApEnabled(batteryStats, jCurrentTimeMillis);
        if (wifiApEnabled != null) {
            arrayList.add(wifiApEnabled);
            iAddLogBit = DfsUtils.addLogBit(9, DfsUtils.addLogBit(i2, iAddLogBit));
        }
        AbnormalReason cpuTimeStatus = getCpuTimeStatus(jCurrentTimeMillis, this.mTrackingData);
        if (cpuTimeStatus != null) {
            arrayList.add(cpuTimeStatus);
            iAddLogBit = DfsUtils.addLogBit(17, iAddLogBit);
        }
        if (arrayList.isEmpty()) {
            Log.d(TAG, "Checking finished. Not find any abnormal reason!");
            if (currentAm < ((double) f2) * 2.4d) {
                Log.d(TAG, "The current is abnormal but acceptable. avgCurrent=" + currentAm + ", historyCurrent=" + f2);
                return;
            }
            AbnormalReason abnormalReason3 = new AbnormalReason();
            abnormalReason3.reasonId = 32;
            abnormalReason3.reasonDesc = "Unknown";
            abnormalReason3.reasonParams = new String[]{Weather.AlertInfo.ABNORMAL};
            arrayList.add(abnormalReason3);
            Log.d(TAG, "Power consumption is too high, report it anyway. Reason: " + abnormalReason3);
            iAddLogBit2 = -1;
        } else {
            iAddLogBit2 = DfsUtils.addLogBit(0, DfsUtils.addLogBit(1, iAddLogBit));
        }
        MiEventConstructor.getInstance().upToMiSightAppUsage(this.mTrackingData.trackingPkg, f2, currentAm, jCurrentTimeMillis, arrayList, iAddLogBit2);
        if (z2) {
            this.mDebugReasonList = arrayList;
        }
    }

    private AbnormalReason getBgCpuHigh(TrackingData trackingData) {
        if (trackingData.bgCpuHighList.isEmpty()) {
            return null;
        }
        AbnormalReason abnormalReason = new AbnormalReason();
        abnormalReason.reasonId = 0;
        abnormalReason.reasonDesc = "high_cpu_usage";
        abnormalReason.reasonParams = new String[]{"abnormalApps=["};
        for (String str : trackingData.bgCpuHighList) {
            StringBuilder sb = new StringBuilder();
            String[] strArr = abnormalReason.reasonParams;
            sb.append(strArr[0]);
            sb.append(str);
            sb.append(";");
            strArr[0] = sb.toString();
        }
        String[] strArr2 = abnormalReason.reasonParams;
        StringBuilder sb2 = new StringBuilder();
        String str2 = abnormalReason.reasonParams[0];
        sb2.append(str2.substring(0, str2.length() - 1));
        sb2.append("]");
        strArr2[0] = sb2.toString();
        return abnormalReason;
    }

    private AbnormalReason getCpuTimeStatus(long j2, TrackingData trackingData) {
        if (trackingData.cpuTime == null) {
            return null;
        }
        long[] cpuTime = UsageReader.getCpuTime();
        long j3 = cpuTime[0];
        long[] jArr = trackingData.cpuTime;
        long j4 = j3 - jArr[0];
        long j5 = cpuTime[1] - jArr[1];
        long j6 = cpuTime[2] - jArr[2];
        long j7 = cpuTime[3] - jArr[3];
        long j8 = cpuTime[4] - jArr[4];
        long j9 = cpuTime[5] - jArr[5];
        long j10 = j4 + j5 + j6 + j7 + j8 + j9;
        Log.d(TAG, "getCpuTimeStatus totalTime=" + j10 + ", idleTime=" + j6 + ", trackingTime=" + j2);
        if (j10 == 0) {
            return null;
        }
        int i2 = (int) ((j4 * 100) / j10);
        int i3 = (int) ((j5 * 100) / j10);
        int i4 = (int) ((((j8 + j7) + j9) * 100) / j10);
        boolean z2 = DfsUtils.DEBUG;
        int i5 = z2 ? 20 : 90;
        int i6 = z2 ? 10 : 70;
        if (i2 + i3 < i5 && i4 < i6) {
            Log.d(TAG, "getCpuTimeStatus: cpu load normal");
            return null;
        }
        AbnormalReason abnormalReason = new AbnormalReason();
        abnormalReason.reasonId = 7;
        abnormalReason.reasonDesc = "high_cpu_load";
        String[] strArr = new String[4];
        abnormalReason.reasonParams = strArr;
        strArr[0] = "user=" + i2 + "%";
        abnormalReason.reasonParams[1] = "kernel=" + i3 + "%";
        abnormalReason.reasonParams[2] = "irq=" + i4 + "%";
        abnormalReason.reasonParams[3] = "totalTime=" + j10;
        Log.d(TAG, "Reason: " + abnormalReason);
        return abnormalReason;
    }

    private float getCurrentAm(double d2, long j2) {
        return (float) (((d2 * 3600.0d) * 1000.0d) / j2);
    }

    private AbnormalReason getRadioSignalPoor(IBatteryStats iBatteryStats, long j2) {
        long poorSignalStrengthTime = UsageReader.getPoorSignalStrengthTime(iBatteryStats, 2, false) - this.mTrackingData.radioPoorSignalDuration;
        if (!isNetworkActive(iBatteryStats, j2 / 3, false) || poorSignalStrengthTime <= (((long) this.mCloudData.getPoorSignalRatio(1)) * j2) / 100000) {
            return null;
        }
        AbnormalReason abnormalReason = new AbnormalReason();
        abnormalReason.reasonId = 4;
        abnormalReason.reasonDesc = "radio_signal_poor";
        String[] strArr = new String[2];
        abnormalReason.reasonParams = strArr;
        strArr[0] = "trackingTime=" + (j2 / 1000);
        abnormalReason.reasonParams[1] = "radioSignalPoorS=" + poorSignalStrengthTime;
        Log.d(TAG, "Reason: " + abnormalReason);
        return abnormalReason;
    }

    private AbnormalReason[] getRxTxReason(long j2, TrackingData trackingData) {
        AbnormalReason[] abnormalReasonArr = {null, null};
        if (j2 == 0) {
            return abnormalReasonArr;
        }
        long mobileRxBytes = UsageReader.getMobileRxBytes() + UsageReader.getMobileTxBytes();
        long j3 = mobileRxBytes - trackingData.mobileRxTxBytes;
        long mobileRxPackets = UsageReader.getMobileRxPackets() + UsageReader.getMobileTxPackets();
        long j4 = mobileRxPackets - trackingData.mobileRxTxPackets;
        float f2 = j2;
        float f3 = (j3 * 1000) / f2;
        float f4 = (j4 * 1000) / f2;
        long totalRxBytes = (UsageReader.getTotalRxBytes() + UsageReader.getTotalTxBytes()) - mobileRxBytes;
        long j5 = totalRxBytes - trackingData.wifiRxTxBytes;
        long totalRxPackets = (UsageReader.getTotalRxPackets() + UsageReader.getTotalTxPackets()) - mobileRxPackets;
        long j6 = totalRxPackets - trackingData.wifiRxTxPackets;
        float f5 = (j5 * 8000) / f2;
        float f6 = (j6 * 1000) / f2;
        Log.d(TAG, "mobileRxTxBytes:" + mobileRxBytes + ", deltaMobileBytes:" + j3 + ", mobileRxTxPackets:" + mobileRxPackets + ", deltaMobilePackets:" + j4 + ", mobileBitRate:" + f3 + ", mobilePacketRate:" + f4 + ", wifiRxTxBytes:" + totalRxBytes + ", deltaWifiBytes:" + j5 + ", wifiRxTxPackets:" + totalRxPackets + ", deltaWifiPackets:" + j6 + ", wifiBitRate:" + f5 + ", wifiPacketRate:" + f6);
        if (f3 > this.mCloudData.getRxtxByteRate(1) && f4 > this.mCloudData.getRxtxPacketRate(1)) {
            AbnormalReason abnormalReason = new AbnormalReason();
            abnormalReason.reasonId = 6;
            abnormalReason.reasonDesc = "mobile_rxtx";
            String[] strArr = new String[3];
            abnormalReason.reasonParams = strArr;
            strArr[0] = "rxtxByte=" + j3;
            abnormalReason.reasonParams[1] = "rxtxPackets=" + j4;
            abnormalReason.reasonParams[2] = "rxtxTime=" + (j2 / 1000);
            Log.d(TAG, "Reason: " + abnormalReason);
            abnormalReasonArr[0] = abnormalReason;
        }
        if (f5 > this.mCloudData.getRxtxByteRate(1) && f6 > this.mCloudData.getRxtxPacketRate(1)) {
            AbnormalReason abnormalReason2 = new AbnormalReason();
            abnormalReason2.reasonId = 5;
            abnormalReason2.reasonDesc = "wifi_rxtx";
            String[] strArr2 = new String[3];
            abnormalReason2.reasonParams = strArr2;
            strArr2[0] = "rxtxByte=" + j5;
            abnormalReason2.reasonParams[1] = "rxtxPackets=" + j6;
            abnormalReason2.reasonParams[2] = "rxtxTime=" + (j2 / 1000);
            Log.d(TAG, "Reason: " + abnormalReason2);
            abnormalReasonArr[1] = abnormalReason2;
        }
        return abnormalReasonArr;
    }

    private AbnormalReason getVpnEnabled(long j2, long j3) {
        h.c cVar = this.mVpnTimer;
        if (cVar == null) {
            return null;
        }
        long jE = cVar.e(j3 * 1000, 0);
        if (j2 >= (3 * jE) / 1000) {
            return null;
        }
        AbnormalReason abnormalReason = new AbnormalReason();
        abnormalReason.reasonId = 1;
        abnormalReason.reasonDesc = "vpn_enabled";
        String[] strArr = new String[2];
        abnormalReason.reasonParams = strArr;
        strArr[0] = "trackingTime=" + (j2 / 1000);
        abnormalReason.reasonParams[1] = "vpnDurationS=" + (jE / DfsUtils.SECOND_TO_MICROS);
        Log.d(TAG, "Reason: " + abnormalReason);
        return abnormalReason;
    }

    private AbnormalReason getWifiApEnabled(IBatteryStats iBatteryStats, long j2) {
        if (this.mWifiApEnabled) {
            this.mWifiApDurationMs += System.currentTimeMillis() - this.mWifiApStartTime;
        }
        if (!isNetworkActive(iBatteryStats, j2 / 3, false) || j2 >= this.mWifiApDurationMs * 3) {
            return null;
        }
        AbnormalReason abnormalReason = new AbnormalReason();
        abnormalReason.reasonId = 2;
        abnormalReason.reasonDesc = "wifi_ap_enabled";
        String[] strArr = new String[2];
        abnormalReason.reasonParams = strArr;
        strArr[0] = "wifiApDurationS=" + (this.mWifiApDurationMs / 1000);
        abnormalReason.reasonParams[1] = "trackingTimeS=" + (j2 / 1000);
        Log.d(TAG, "Reason: " + abnormalReason);
        return abnormalReason;
    }

    private AbnormalReason getWifiSignalPoor(IBatteryStats iBatteryStats, long j2) {
        long poorSignalStrengthTime = UsageReader.getPoorSignalStrengthTime(iBatteryStats, 2, true) - this.mTrackingData.wifiPoorSignalDuration;
        if (!isNetworkActive(iBatteryStats, j2 / 3, true) || poorSignalStrengthTime <= (((long) this.mCloudData.getPoorSignalRatio(1)) * j2) / 100000) {
            return null;
        }
        AbnormalReason abnormalReason = new AbnormalReason();
        abnormalReason.reasonId = 3;
        abnormalReason.reasonDesc = "wifi_signal_poor";
        String[] strArr = new String[2];
        abnormalReason.reasonParams = strArr;
        strArr[0] = "trackingTime=" + (j2 / 1000);
        abnormalReason.reasonParams[1] = "wifiSignalPoorS=" + poorSignalStrengthTime;
        Log.d(TAG, "Reason: " + abnormalReason);
        return abnormalReason;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Message message) {
        ForegroundInfo foregroundInfo;
        Log.d(TAG, "handleMessage msg=" + message.what);
        if (message.what != 0 || (foregroundInfo = (ForegroundInfo) message.getData().get(MIY.BUNDLE_PARAM_FG_INFO)) == null) {
            return;
        }
        if (!foregroundInfo.mForegroundPackageName.equals(this.mFgInfo.mForegroundPackageName)) {
            Log.d(TAG, "The foreground maybe changed.");
        } else {
            synchronized (this.mTrackingLock) {
                startTracking(foregroundInfo);
            }
        }
    }

    private boolean isNetworkActive(IBatteryStats iBatteryStats, long j2, boolean z2) {
        long mobileRadioActiveTime;
        long j3;
        try {
            if (z2) {
                mobileRadioActiveTime = iBatteryStats.getWifiBatteryStats().getKernelActiveTimeMillis();
                j3 = this.mTrackingData.wifiActiveTime;
            } else {
                mobileRadioActiveTime = iBatteryStats.getMobileRadioActiveTime(SystemClock.elapsedRealtime() * 1000, 2) / 1000;
                j3 = this.mTrackingData.radioActiveTime;
            }
            long j4 = mobileRadioActiveTime - j3;
            boolean z3 = DfsUtils.DEBUG;
            if (z3) {
                Log.d(TAG, "isNetworkActive: δ activeTime=" + j4 + ", isWifi=" + z2);
            }
            if (j4 <= 1000) {
                Log.d(TAG, "Discard! Maybe the network is not connected.");
                return false;
            }
            if (j4 >= j2) {
                return true;
            }
            Log.d(TAG, "Discard! The network active time too short. threshold=" + j2);
            return z3;
        } catch (RemoteException unused) {
            Log.d(TAG, "Can't get the active status. Just discard.");
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i2, Bundle bundle) {
        boolean z2 = true;
        if (i2 == 1) {
            synchronized (this.mTrackingLock) {
                try {
                    if (this.mTracking) {
                        int i3 = bundle.getInt("uid");
                        TrackingData trackingData = this.mTrackingData;
                        if (trackingData != null) {
                            trackingData.bgCpuHighList.add(BLUtils.getAppNameByUid(this.mContext, i3));
                        }
                        return;
                    }
                    return;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        if (i2 == 2) {
            synchronized (this.mTrackingLock) {
                try {
                    if (bundle.getInt("status") != 1) {
                        z2 = false;
                    }
                    this.mVpnEnabled = z2;
                    if (this.mTracking) {
                        startRunningVpnTimer(this.mVpnEnabled);
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
            return;
        }
        if (i2 != 3) {
            return;
        }
        synchronized (this.mTrackingLock) {
            try {
                boolean z3 = bundle.getBoolean(YellowPageContract.Permission.STATE);
                if (!this.mTracking) {
                    this.mWifiApEnabled = z3;
                } else {
                    if (this.mWifiApEnabled == z3) {
                        return;
                    }
                    if (z3) {
                        this.mWifiApStartTime = System.currentTimeMillis();
                    } else {
                        this.mWifiApDurationMs += System.currentTimeMillis() - this.mWifiApStartTime;
                    }
                    this.mWifiApEnabled = z3;
                }
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }

    private void resetTimerIfNeeded() {
        long jB = this.mUsageManager.getmClock().b();
        h.c cVar = this.mVpnTimer;
        if (cVar == null || !cVar.g()) {
            return;
        }
        this.mVpnTimer.j(jB);
        this.mVpnTimer.f(false, jB * 1000);
    }

    private void startRunningVpnTimer(boolean z2) {
        if (this.mVpnTimer == null) {
            this.mVpnTimer = new h.c(h.a.f1865a, null, 0, null, this.mUsageManager.getmOnBatteryTimeBase());
        }
        if (z2) {
            this.mVpnTimer.i(this.mUsageManager.getmClock().b());
        } else {
            this.mVpnTimer.j(this.mUsageManager.getmClock().b());
        }
    }

    private void startTracking(ForegroundInfo foregroundInfo) {
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        if (batteryStats == null) {
            Log.d(TAG, "startTracking failed! IBatteryStats is null.");
            return;
        }
        this.mTracking = true;
        TrackingData trackingData = new TrackingData();
        this.mTrackingData = trackingData;
        trackingData.trackingPkg = foregroundInfo.mForegroundPackageName;
        trackingData.trackingUid = foregroundInfo.mForegroundUid;
        trackingData.startPower = BSHelper.getCurrentBatteryCap();
        TrackingData trackingData2 = this.mTrackingData;
        if (trackingData2.startPower == -1.0d) {
            this.mTrackingData = null;
            Log.d(TAG, "startTracking failed! Failed to get the charge counter.");
            return;
        }
        try {
            trackingData2.wifiPoorSignalDuration = UsageReader.getPoorSignalStrengthTime(batteryStats, 2, true);
            this.mTrackingData.wifiActiveTime = batteryStats.getWifiBatteryStats().getKernelActiveTimeMillis();
            this.mTrackingData.radioPoorSignalDuration = UsageReader.getPoorSignalStrengthTime(batteryStats, 2, false);
            this.mTrackingData.radioActiveTime = batteryStats.getMobileRadioActiveTime(SystemClock.elapsedRealtime() * 1000, 2) / 1000;
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
        }
        this.mTrackingData.startTime = System.currentTimeMillis();
        Log.d(TAG, "startTracking! startTime=" + this.mTrackingData.startTime);
        if (this.mVpnEnabled) {
            if (this.mVpnTimer != null) {
                long jB = this.mUsageManager.getmClock().b();
                this.mVpnTimer.j(jB);
                this.mVpnTimer.f(false, jB);
            }
            startRunningVpnTimer(true);
        }
        this.mWifiApDurationMs = 0L;
        if (this.mWifiApEnabled) {
            this.mWifiApStartTime = System.currentTimeMillis();
        }
        this.mTrackingData.mobileRxTxBytes = UsageReader.getMobileTxBytes() + UsageReader.getMobileRxBytes();
        this.mTrackingData.mobileRxTxPackets = UsageReader.getMobileTxPackets() + UsageReader.getMobileRxPackets();
        TrackingData trackingData3 = this.mTrackingData;
        long totalRxBytes = UsageReader.getTotalRxBytes() + UsageReader.getTotalTxBytes();
        TrackingData trackingData4 = this.mTrackingData;
        trackingData3.wifiRxTxBytes = totalRxBytes - trackingData4.mobileRxTxBytes;
        long totalRxPackets = UsageReader.getTotalRxPackets() + UsageReader.getTotalTxPackets();
        TrackingData trackingData5 = this.mTrackingData;
        trackingData4.wifiRxTxPackets = totalRxPackets - trackingData5.mobileRxTxPackets;
        trackingData5.cpuTime = UsageReader.getCpuTime();
        Log.d(TAG, this.mTrackingData.toString());
    }

    private void stopTracking(String str) {
        synchronized (this.mTrackingLock) {
            Log.d(TAG, "StopTracking. reason=" + str);
            checkPower();
            this.mTrackingData = null;
            resetTimerIfNeeded();
            this.mTracking = false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:17:0x002c  */
    public void dump(PrintWriter printWriter, String[] strArr) {
        byte b2;
        if (strArr != null && strArr.length > 0) {
            String str = strArr[0];
            int iHashCode = str.hashCode();
            if (iHashCode != 98728) {
                if (iHashCode == 116980 && str.equals("vpn")) {
                    b2 = 1;
                } else {
                    b2 = -1;
                }
            } else if (str.equals(LogType.LOG_TYPE_CPU_NAME)) {
                b2 = 0;
            } else {
                b2 = -1;
            }
            if (b2 == 0) {
                int i2 = this.mFgInfo.mForegroundUid;
                if (strArr.length > 1) {
                    i2 = Integer.parseInt(strArr[1]);
                }
                UsageMonitor.getInstance(this.mContext).notifyCpuAbnormal(i2);
                printWriter.println("Report CPU abnormal success. uid = " + i2);
                return;
            }
            if (b2 == 1) {
                if (strArr.length > 1) {
                    UsageMonitor.getInstance(this.mContext).notifyVpnConnect(0, Integer.parseInt(strArr[1]));
                    StringBuilder sb = new StringBuilder();
                    sb.append("Changed VPN status to ");
                    sb.append(Integer.parseInt(strArr[1]) == 1 ? "enabled" : "disabled");
                    printWriter.println(sb.toString());
                    return;
                }
                return;
            }
        }
        printWriter.println("mTracking=" + this.mTracking);
        printWriter.println("mOnBattery=" + this.mOnBattery);
        printWriter.println("mWifiApEnabled=" + this.mWifiApEnabled);
        printWriter.println("mVpnEnabled=" + this.mVpnEnabled);
        if (this.mVpnTimer != null) {
            printWriter.println("mVpnTimer=" + this.mVpnTimer.g());
            printWriter.println("mVpnTimerCount=" + this.mVpnTimer.d(0));
            printWriter.println("mVpnTimerTotalTime=" + this.mVpnTimer.e(this.mUsageManager.getmClock().b() * 1000, 0));
        } else {
            printWriter.println("mVpnTimer is Null.");
        }
        if (this.mTrackingData != null) {
            printWriter.println("mTrackingData=" + this.mTrackingData);
        }
        if (this.mDebugReasonList != null) {
            printWriter.println("Last Report Events:");
            for (AbnormalReason abnormalReason : this.mDebugReasonList) {
                printWriter.println("  " + abnormalReason.reasonId + " " + abnormalReason.reasonDesc + " " + Arrays.toString(abnormalReason.reasonParams));
            }
        }
    }

    public void notifyForegroundInfoChanged(ForegroundInfo foregroundInfo) {
        if (this.mFgInfo == null || foregroundInfo == null || !this.mTracking || !foregroundInfo.mForegroundPackageName.equals(this.mFgInfo.mForegroundPackageName)) {
            synchronized (this.mTrackingLock) {
                try {
                    if (this.mTracking) {
                        stopTracking("fg-changed");
                    }
                    this.mFgInfo = foregroundInfo;
                    if (foregroundInfo == null) {
                        return;
                    }
                    if (!this.mOnBattery) {
                        Log.d(TAG, "Fg changed, new=" + foregroundInfo.mForegroundPackageName + ", but charging.");
                        return;
                    }
                    if (this.mHandler.hasMessages(0)) {
                        this.mHandler.removeMessages(0);
                    }
                    if (!TRACKING_APPS.contains(foregroundInfo.mForegroundPackageName)) {
                        Log.d(TAG, foregroundInfo.mForegroundPackageName + " is not in tracking list.");
                        return;
                    }
                    Log.d(TAG, foregroundInfo.mForegroundPackageName + " is in tracking list.");
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putObject(MIY.BUNDLE_PARAM_FG_INFO, foregroundInfo);
                    message.setData(bundle);
                    this.mHandler.sendMessageDelayed(message, 60000L);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public void notifyOnBattery(boolean z2) {
        this.mOnBattery = z2;
        synchronized (this.mTrackingLock) {
            if (!z2) {
                try {
                    if (this.mTracking) {
                        stopTracking(MIY.BUNDLE_PARAM_CHARGING);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (z2) {
                notifyForegroundInfoChanged(this.mFgInfo);
            }
        }
    }

    public void notifyScreenOff(boolean z2) {
        this.mScreenOff = z2;
        synchronized (this.mTrackingLock) {
            try {
                if (this.mScreenOff && this.mTracking) {
                    stopTracking("screen-off");
                }
                if (!this.mScreenOff) {
                    notifyForegroundInfoChanged(EventsAggregator.getInstance(this.mContext).getForegroundInfo());
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
