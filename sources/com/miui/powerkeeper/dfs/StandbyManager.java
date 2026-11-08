package com.miui.powerkeeper.dfs;

import android.content.Context;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.WakeLockStats;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class StandbyManager {
    public static final String TAG = "DFS-StandbyManager";
    private StandbyAbnormalData mAbnormalData;
    private final StandbyAbnormalDetector mAbnormalDetector;
    private final CloudData mCloudData;
    private final Context mContext;
    volatile boolean bWorking = false;
    private volatile int mWorkingScene = 1;
    private final StandbyOriginalData mOriginalData = new StandbyOriginalData();

    public StandbyManager(Context context, CloudData cloudData) {
        this.mContext = context;
        this.mCloudData = cloudData;
        this.mAbnormalDetector = new StandbyAbnormalDetector(cloudData);
    }

    public void dump(PrintWriter printWriter) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
        indentingPrintWriter.println("====Standby START=====");
        indentingPrintWriter.println(this.mCloudData);
        indentingPrintWriter.println(this.mCloudData.toString(this.mWorkingScene));
        if (this.bWorking) {
            indentingPrintWriter.println("mWorking=" + this.bWorking + ", mWorkingScene=" + this.mWorkingScene + ", time=" + ((SystemClock.elapsedRealtime() - this.mOriginalData.mStartRealtimeMs) / 1000) + RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
            indentingPrintWriter.println("Should stop work first.");
            indentingPrintWriter.println("====Standby END=====");
            return;
        }
        indentingPrintWriter.println("mWorking=" + this.bWorking + ", mWorkingScene=" + this.mWorkingScene);
        indentingPrintWriter.println("Standby Original Data:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("charge_counter(Start): " + this.mOriginalData.mStartChargeCounter + "mAh");
        indentingPrintWriter.println("charge_counter(End): " + this.mOriginalData.mEndChargeCounter + "mAh");
        indentingPrintWriter.println("realTime(Start): " + this.mOriginalData.mStartRealtimeMs + "ms");
        indentingPrintWriter.println("realTime(End): " + this.mOriginalData.mEndRealtimeMs + "ms");
        indentingPrintWriter.println("flashlight: " + this.mOriginalData.mFlashlightOnS + RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
        indentingPrintWriter.println("mScreenDozeS: " + this.mOriginalData.mScreenDozeS + RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
        indentingPrintWriter.println("lightDoze: " + this.mOriginalData.mDeviceIdleLightTimeUs + "us");
        indentingPrintWriter.println("deepDoze: " + this.mOriginalData.mDeviceIdleFullTimeUs + "us");
        StringBuilder sb = new StringBuilder();
        int i2 = DfsUtils.DEBUG ? 10 : 5;
        int i3 = 0;
        if (this.mOriginalData.mStartWakeupReasonMap != null) {
            indentingPrintWriter.println("All wakeup reasons(Start):");
            indentingPrintWriter.increaseIndent();
            for (int i4 = 0; i4 < this.mOriginalData.mStartWakeupReasonMap.size() && i4 <= i2; i4++) {
                DfsUtils.TimerEntry timerEntry = this.mOriginalData.mStartWakeupReasonMap.get(i4);
                sb.setLength(0);
                sb.append("Wakeup Reason: ");
                sb.append(timerEntry.name);
                sb.append(" | ");
                sb.append(timerEntry.timeUs);
                sb.append("us (");
                sb.append(timerEntry.count);
                sb.append(" times)");
                indentingPrintWriter.println(sb);
            }
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mOriginalData.mEndWakeupReasonMap != null) {
            indentingPrintWriter.println("All wakeup reasons(End):");
            indentingPrintWriter.increaseIndent();
            int i5 = 0;
            while (i5 < this.mOriginalData.mEndWakeupReasonMap.size() && i5 <= i2) {
                DfsUtils.TimerEntry timerEntry2 = this.mOriginalData.mEndWakeupReasonMap.get(i5);
                sb.setLength(i3);
                sb.append("Wakeup Reason: ");
                sb.append(timerEntry2.name);
                sb.append(" | ");
                sb.append(timerEntry2.timeUs);
                sb.append("us (");
                sb.append(timerEntry2.count);
                sb.append(" times)");
                indentingPrintWriter.println(sb);
                i5++;
                i3 = 0;
            }
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mOriginalData.mStartPartialWakelockList != null) {
            indentingPrintWriter.println("All partial wakelocks(Start):");
            indentingPrintWriter.increaseIndent();
            for (int i6 = 0; i6 < this.mOriginalData.mStartPartialWakelockList.size() && i6 <= i2; i6++) {
                DfsUtils.TimerEntry timerEntry3 = this.mOriginalData.mStartPartialWakelockList.get(i6);
                sb.setLength(0);
                sb.append("Partial Wakelock: ");
                sb.append(timerEntry3.name);
                sb.append(" | ");
                sb.append("uid=");
                sb.append(timerEntry3.id);
                sb.append(" | ");
                sb.append(timerEntry3.timeUs);
                sb.append("us (");
                sb.append(timerEntry3.count);
                sb.append(" times)");
                indentingPrintWriter.println(sb);
            }
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mOriginalData.mEndPartialWakelockList != null) {
            indentingPrintWriter.println("All partial wakelocks(End):");
            indentingPrintWriter.increaseIndent();
            for (int i7 = 0; i7 < this.mOriginalData.mEndPartialWakelockList.size() && i7 <= i2; i7++) {
                DfsUtils.TimerEntry timerEntry4 = this.mOriginalData.mEndPartialWakelockList.get(i7);
                sb.setLength(0);
                sb.append("Partial Wakelock: ");
                sb.append(timerEntry4.name);
                sb.append(" | ");
                sb.append("uid=");
                sb.append(timerEntry4.id);
                sb.append(" | ");
                sb.append(timerEntry4.timeUs);
                sb.append("us (");
                sb.append(timerEntry4.count);
                sb.append(" times)");
                indentingPrintWriter.println(sb);
            }
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mOriginalData.mStartKernelWakelockList != null) {
            indentingPrintWriter.println("All kernel wakelocks(Start):");
            indentingPrintWriter.increaseIndent();
            for (int i8 = 0; i8 < this.mOriginalData.mStartKernelWakelockList.size() && i8 <= i2; i8++) {
                DfsUtils.TimerEntry timerEntry5 = this.mOriginalData.mStartKernelWakelockList.get(i8);
                sb.setLength(0);
                sb.append("Kernel Wakelock: ");
                sb.append(timerEntry5.name);
                sb.append(" | ");
                sb.append("uid=");
                sb.append(timerEntry5.id);
                sb.append(" | ");
                sb.append(timerEntry5.timeUs);
                sb.append("us (");
                sb.append(timerEntry5.count);
                sb.append(" times)");
                indentingPrintWriter.println(sb);
            }
            indentingPrintWriter.decreaseIndent();
        }
        if (this.mOriginalData.mEndKernelWakelockList != null) {
            indentingPrintWriter.println("All kernel wakelocks(End):");
            indentingPrintWriter.increaseIndent();
            for (int i9 = 0; i9 < this.mOriginalData.mEndKernelWakelockList.size() && i9 <= i2; i9++) {
                DfsUtils.TimerEntry timerEntry6 = this.mOriginalData.mEndKernelWakelockList.get(i9);
                sb.setLength(0);
                sb.append("Kernel Wakelock: ");
                sb.append(timerEntry6.name);
                sb.append(" | ");
                sb.append("uid=");
                sb.append(timerEntry6.id);
                sb.append(" | ");
                sb.append(timerEntry6.timeUs);
                sb.append("us (");
                sb.append(timerEntry6.count);
                sb.append(" times)");
                indentingPrintWriter.println(sb);
            }
            indentingPrintWriter.decreaseIndent();
        }
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        if (this.mOriginalData.mPhoneSignalStrengthStartData != null && batteryStats != null) {
            indentingPrintWriter.println("phoneSignalStrengthData(Start):");
            indentingPrintWriter.increaseIndent();
            sb.setLength(0);
            sb.append("phoneSignalStrengthData: total=");
            sb.append(this.mOriginalData.mPhoneSignalStrengthStartData.totalTimeS);
            sb.append("s, poor=");
            sb.append(this.mOriginalData.mPhoneSignalStrengthStartData.poorTimeS);
            sb.append("s, active=");
            sb.append(this.mOriginalData.mPhoneSignalStrengthStartData.activeTimeMs);
            sb.append(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
            indentingPrintWriter.println(sb);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("phoneSignalStrengthData(End):");
            indentingPrintWriter.increaseIndent();
            sb.setLength(0);
            sb.append("phoneSignalStrengthData: total=");
            sb.append(UsageReader.getPoorSignalStrengthTime(batteryStats, 5, false));
            sb.append("s, poor=");
            sb.append(UsageReader.getPoorSignalStrengthTime(batteryStats, 2, false));
            sb.append("s, active=");
            try {
                sb.append(batteryStats.getMobileRadioActiveTime(SystemClock.elapsedRealtime() * 1000, 2) / 1000);
                sb.append(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
                indentingPrintWriter.println(sb);
                indentingPrintWriter.decreaseIndent();
            } catch (RemoteException e2) {
                throw new RuntimeException(e2);
            }
        }
        if (this.mOriginalData.mWifiSignalStrengthStartData != null && batteryStats != null) {
            indentingPrintWriter.println("wifiSignalStrengthData(Start):");
            indentingPrintWriter.increaseIndent();
            sb.setLength(0);
            sb.append("wifiSignalStrengthData: total=");
            sb.append(this.mOriginalData.mWifiSignalStrengthStartData.totalTimeS);
            sb.append("s, poor=");
            sb.append(this.mOriginalData.mWifiSignalStrengthStartData.poorTimeS);
            sb.append("s, active=");
            sb.append(this.mOriginalData.mWifiSignalStrengthStartData.activeTimeMs);
            sb.append(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
            indentingPrintWriter.println(sb);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("wifiSignalStrengthData(End):");
            indentingPrintWriter.increaseIndent();
            sb.setLength(0);
            sb.append("wifiSignalStrengthData: total=");
            sb.append(UsageReader.getPoorSignalStrengthTime(batteryStats, 5, true));
            sb.append("s, poor=");
            sb.append(UsageReader.getPoorSignalStrengthTime(batteryStats, 2, true));
            sb.append("s, active=");
            try {
                sb.append(batteryStats.getWifiBatteryStats().getKernelActiveTimeMillis());
                sb.append(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
                indentingPrintWriter.println(sb);
                indentingPrintWriter.decreaseIndent();
            } catch (RemoteException e3) {
                throw new RuntimeException(e3);
            }
        }
        indentingPrintWriter.decreaseIndent();
        if (this.mAbnormalData != null) {
            indentingPrintWriter.println("Standby Abnormal Data:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println(this.mAbnormalData);
            indentingPrintWriter.decreaseIndent();
        }
        MiEventConstructor.getInstance().dump(indentingPrintWriter);
        indentingPrintWriter.println("====Standby END=====");
    }

    public ArrayList<DfsUtils.TimerEntry> getKernelWakelocks(IBatteryStats iBatteryStats, long j2, boolean z2) {
        ArrayList<DfsUtils.TimerEntry> arrayList = new ArrayList<>();
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                byte[] kernelWakelockStats = iBatteryStats.getKernelWakelockStats();
                if (kernelWakelockStats != null) {
                    parcelObtain.unmarshall(kernelWakelockStats, 0, kernelWakelockStats.length);
                    parcelObtain.setDataPosition(0);
                    int i2 = parcelObtain.readInt();
                    for (int i3 = 0; i3 < i2; i3++) {
                        String string = parcelObtain.readString();
                        int i4 = parcelObtain.readInt();
                        long j3 = parcelObtain.readLong();
                        if (z2) {
                            if (j3 > 0) {
                                arrayList.add(new DfsUtils.TimerEntry(string, j3 * 1000, i4, 0));
                            }
                        } else if (j3 >= (((long) this.mCloudData.getMaxWakelockTimePerHour(0)) * j2) / 60) {
                            arrayList.add(new DfsUtils.TimerEntry(string, j3 * 1000, i4, 0));
                        }
                    }
                    arrayList.sort(DfsUtils.timerComparator);
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
            return arrayList;
        } finally {
            parcelObtain.recycle();
        }
    }

    public ArrayList<DfsUtils.TimerEntry> getPartialWakelocks(IBatteryStats iBatteryStats, long j2, boolean z2) {
        ArrayList<DfsUtils.TimerEntry> arrayList = new ArrayList<>();
        try {
            List wakeLocks = iBatteryStats.getWakeLockStats().getWakeLocks();
            for (int size = wakeLocks.size() - 1; size >= 0; size--) {
                WakeLockStats.WakeLock wakeLock = (WakeLockStats.WakeLock) wakeLocks.get(size);
                int i2 = wakeLock.uid;
                if (i2 >= 10000) {
                    WakeLockStats.WakeLockData wakeLockData = wakeLock.totalWakeLockData;
                    long j3 = wakeLockData.totalTimeHeldMs * 1000;
                    int i3 = wakeLockData.timesAcquired;
                    if (z2) {
                        if (j3 > 5) {
                            arrayList.add(new DfsUtils.TimerEntry(wakeLock.name, j3, i3, i2));
                        }
                    } else if (j3 > (((long) this.mCloudData.getMaxWakelockTimePerHour(0)) * j2) / 60) {
                        arrayList.add(new DfsUtils.TimerEntry(wakeLock.name, j3, i3, wakeLock.uid));
                    }
                }
            }
            arrayList.sort(DfsUtils.timerComparator);
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
        }
        return arrayList;
    }

    public ArrayList<DfsUtils.TimerEntry> getWakeupReasons(IBatteryStats iBatteryStats, long j2) {
        ArrayList<DfsUtils.TimerEntry> arrayList = new ArrayList<>();
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                byte[] wakeupReasonStats = iBatteryStats.getWakeupReasonStats();
                if (wakeupReasonStats != null) {
                    parcelObtain.unmarshall(wakeupReasonStats, 0, wakeupReasonStats.length);
                    parcelObtain.setDataPosition(0);
                    int i2 = parcelObtain.readInt();
                    for (int i3 = 0; i3 < i2; i3++) {
                        String string = parcelObtain.readString();
                        int i4 = parcelObtain.readInt();
                        long j3 = parcelObtain.readLong();
                        if (string != null && !string.isEmpty() && j3 > 0 && StandbyAbnormalData.getWakeupType(string) >= 0) {
                            arrayList.add(new DfsUtils.TimerEntry(string, j3 * 1000, i4));
                        }
                    }
                    arrayList.sort(DfsUtils.countComparator);
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
            return arrayList;
        } finally {
            parcelObtain.recycle();
        }
    }

    public boolean startWork(int i2) {
        Log.d(TAG, "startWork scene=" + i2);
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        if (batteryStats == null) {
            Log.d(TAG, "BatteryStatsImpl get failed! Stop.");
            return false;
        }
        this.mWorkingScene = i2;
        this.bWorking = true;
        this.mOriginalData.reset();
        this.mOriginalData.mStartRealtimeMs = SystemClock.elapsedRealtime();
        StandbyOriginalData standbyOriginalData = this.mOriginalData;
        standbyOriginalData.mStartWakeupReasonMap = getWakeupReasons(batteryStats, standbyOriginalData.mStartRealtimeMs * 1000);
        this.mOriginalData.mStartKernelWakelockList = getKernelWakelocks(batteryStats, -1L, true);
        this.mOriginalData.mStartPartialWakelockList = getPartialWakelocks(batteryStats, -1L, true);
        this.mOriginalData.mStartChargeCounter = BSHelper.getCurrentBatteryCap();
        try {
            StandbyOriginalData standbyOriginalData2 = this.mOriginalData;
            standbyOriginalData2.mFlashlightOnS = batteryStats.getFlashlightOnTime(standbyOriginalData2.mStartRealtimeMs * 1000, DfsUtils.WHICH) / DfsUtils.SECOND_TO_MICROS;
            this.mOriginalData.mScreenDozeS = batteryStats.getScreenDozeTime(SystemClock.elapsedRealtime() * 1000, DfsUtils.WHICH) / DfsUtils.SECOND_TO_MICROS;
            StandbyOriginalData standbyOriginalData3 = this.mOriginalData;
            standbyOriginalData3.mDeviceIdleLightTimeUs = batteryStats.getDeviceIdleModeTime(1, standbyOriginalData3.mStartRealtimeMs * 1000, DfsUtils.WHICH);
            StandbyOriginalData standbyOriginalData4 = this.mOriginalData;
            standbyOriginalData4.mDeviceIdleFullTimeUs = batteryStats.getDeviceIdleModeTime(2, standbyOriginalData4.mStartRealtimeMs * 1000, DfsUtils.WHICH);
            this.mOriginalData.mPhoneSignalStrengthStartData.poorTimeS = UsageReader.getPoorSignalStrengthTime(batteryStats, 2, false);
            this.mOriginalData.mPhoneSignalStrengthStartData.totalTimeS = UsageReader.getPoorSignalStrengthTime(batteryStats, 5, false);
            this.mOriginalData.mPhoneSignalStrengthStartData.activeTimeMs = batteryStats.getMobileRadioActiveTime(SystemClock.elapsedRealtime() * 1000, 2) / 1000;
            this.mOriginalData.mPhoneSignalStrengthStartData.packages = UsageReader.getMobileRxTxPackets();
            this.mOriginalData.mPhoneSignalStrengthStartData.bytes = UsageReader.getMobileRxTxBytes();
            this.mOriginalData.mWifiSignalStrengthStartData.poorTimeS = UsageReader.getPoorSignalStrengthTime(batteryStats, 2, true);
            this.mOriginalData.mWifiSignalStrengthStartData.totalTimeS = UsageReader.getPoorSignalStrengthTime(batteryStats, 5, true);
            this.mOriginalData.mWifiSignalStrengthStartData.activeTimeMs = batteryStats.getWifiBatteryStats().getKernelActiveTimeMillis();
            StandbyAbnormalData.SignalStrengthData signalStrengthData = this.mOriginalData.mWifiSignalStrengthStartData;
            long totalRxTxPackets = UsageReader.getTotalRxTxPackets();
            StandbyOriginalData standbyOriginalData5 = this.mOriginalData;
            signalStrengthData.packages = totalRxTxPackets - standbyOriginalData5.mPhoneSignalStrengthStartData.packages;
            standbyOriginalData5.mWifiSignalStrengthStartData.bytes = UsageReader.getTotalRxTxBytes() - this.mOriginalData.mPhoneSignalStrengthStartData.bytes;
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
        }
        return this.bWorking;
    }

    public void stopWork() {
        Log.d(TAG, "stopWork");
        if (!this.bWorking) {
            Log.d(TAG, "Discard! Work not start.");
            return;
        }
        this.bWorking = false;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (jElapsedRealtime - this.mOriginalData.mStartRealtimeMs < this.mCloudData.getMinDurationMs()) {
            Log.d(TAG, "Discard! Sampling time is too short. time=" + (jElapsedRealtime - this.mOriginalData.mStartRealtimeMs) + "ms");
            return;
        }
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        if (batteryStats == null) {
            return;
        }
        StandbyOriginalData standbyOriginalData = this.mOriginalData;
        long j2 = jElapsedRealtime - standbyOriginalData.mStartRealtimeMs;
        long j3 = jElapsedRealtime * 1000;
        standbyOriginalData.mEndWakeupReasonMap = getWakeupReasons(batteryStats, j3);
        this.mOriginalData.mEndKernelWakelockList = getKernelWakelocks(batteryStats, j2, false);
        this.mOriginalData.mEndPartialWakelockList = getPartialWakelocks(batteryStats, j2, false);
        this.mOriginalData.mEndChargeCounter = BSHelper.getCurrentBatteryCap();
        StandbyOriginalData standbyOriginalData2 = this.mOriginalData;
        standbyOriginalData2.mEndRealtimeMs = jElapsedRealtime;
        try {
            long flashlightOnTime = batteryStats.getFlashlightOnTime(j3, DfsUtils.WHICH) / DfsUtils.SECOND_TO_MICROS;
            StandbyOriginalData standbyOriginalData3 = this.mOriginalData;
            standbyOriginalData2.mFlashlightOnS = flashlightOnTime - standbyOriginalData3.mFlashlightOnS;
            long screenDozeTime = batteryStats.getScreenDozeTime(j3, DfsUtils.WHICH) / DfsUtils.SECOND_TO_MICROS;
            StandbyOriginalData standbyOriginalData4 = this.mOriginalData;
            standbyOriginalData3.mScreenDozeS = screenDozeTime - standbyOriginalData4.mScreenDozeS;
            long deviceIdleModeTime = batteryStats.getDeviceIdleModeTime(1, standbyOriginalData4.mStartRealtimeMs * 1000, DfsUtils.WHICH);
            StandbyOriginalData standbyOriginalData5 = this.mOriginalData;
            standbyOriginalData4.mDeviceIdleLightTimeUs = deviceIdleModeTime - standbyOriginalData5.mDeviceIdleLightTimeUs;
            standbyOriginalData5.mDeviceIdleFullTimeUs = batteryStats.getDeviceIdleModeTime(2, standbyOriginalData5.mStartRealtimeMs * 1000, DfsUtils.WHICH) - this.mOriginalData.mDeviceIdleFullTimeUs;
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
        }
        StandbyAbnormalData abnormalData = this.mAbnormalDetector.getAbnormalData(this.mOriginalData, this.mWorkingScene);
        this.mAbnormalData = abnormalData;
        if (abnormalData == null) {
            Log.d(TAG, "No abnormal");
        } else {
            MiEventConstructor.getInstance().upToMiSight(this.mAbnormalData);
        }
    }
}
