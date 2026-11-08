package com.miui.powerkeeper.dfs;

import android.annotation.Nullable;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.appcontrol.HideModeStateMachineConfig;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class StandbyAbnormalDetector {
    public static final String TAG = "DFS-AbnormalDetector";
    private int mAvgCurrent;
    private final CloudData mCloudData;
    private int mWorkingScene = 1;

    public static class Logs {
        private static final String CONFIG_BT = "persist.bluetooth.btsnoopenable";
        private static final String CONFIG_OFFLINE_KERNEL = "persist.sys.offlinelog.logcatkernel";
        private static final String CONFIG_OFFLINE_LOGCAT = "persist.sys.offlinelog.logcat";
        private static final String CONFIG_OFFLINE_LOG_DIAG_BUFFER = "debug.sensors.diag_buffer_log";
        private static final String CONFIG_OFFLINE_LOG_ENABLE = "debug.offline_log.enable";
        private static final String CONFIG_OFFLINE_MODULE = "debug.offline_log.module";
        private static final String CONFIG_QSEE = "ro.boot.dp";
        private static final String CONFIG_STABILITY_LOGCAT = "persist.sys.miuiofflinedb";
        private static final String CONFIG_UART_CONFIG = getUartConfig();
        private static final String CONFIG_WIFI_CNSS = "init.svc.vendor.cnss_diag";
        private static final String CONFIG_WIFI_TCPDUMP = "init.svc.vendor.tcpdump";
        private static final String UART_FILE = "proc/cmdline";

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code duplicated, block: B:23:0x004b  */
        private static String getUartConfig() {
            byte b2;
            String str = Build.DEVICE;
            switch (str.hashCode()) {
                case -1406381216:
                    if (!str.equals("aurora")) {
                        b2 = -1;
                    } else {
                        b2 = 3;
                    }
                    break;
                case -632653320:
                    if (!str.equals("shennong")) {
                        b2 = -1;
                    } else {
                        b2 = 1;
                    }
                    break;
                case -347707723:
                    if (!str.equals("babylon")) {
                        b2 = -1;
                    } else {
                        b2 = 0;
                    }
                    break;
                case 99468813:
                    if (!str.equals("houji")) {
                        b2 = -1;
                    } else {
                        b2 = 2;
                    }
                    break;
                case 103662473:
                    if (!str.equals("manet")) {
                        b2 = -1;
                    } else {
                        b2 = 4;
                    }
                    break;
                case 351425128:
                    if (!str.equals("vermeer")) {
                        b2 = -1;
                    } else {
                        b2 = 5;
                    }
                    break;
                default:
                    b2 = -1;
                    break;
            }
            if (b2 == 0) {
                return "qcom_geni_serial.con_enabled=1";
            }
            if (b2 == 1 || b2 == 2 || b2 == 3) {
                return "console=ttyMSM0";
            }
            if (b2 == 4 || b2 == 5) {
                return "unknown_todo";
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isLogEnabled(int i2) {
            String sysNodeInfo;
            if (i2 == 1) {
                return SystemProperties.getBoolean(CONFIG_OFFLINE_KERNEL, false);
            }
            if (i2 == 2) {
                return SystemProperties.getBoolean(CONFIG_OFFLINE_LOGCAT, false);
            }
            if (i2 == 4) {
                return SystemProperties.getBoolean(CONFIG_STABILITY_LOGCAT, false);
            }
            if (i2 == 8) {
                return isOfflineModuleEnabled("modem");
            }
            if (i2 == 16) {
                return isOfflineModuleEnabled("audio");
            }
            if (i2 == 32) {
                return isOfflineModuleEnabled("gps");
            }
            if (i2 == 64) {
                return isOfflineModuleEnabled(HideModeStateMachineConfig.STRATEGY_SENSOR);
            }
            if (i2 == 128) {
                return isOfflineModuleEnabled("charger");
            }
            if (i2 == 256) {
                return SystemProperties.getBoolean(CONFIG_BT, false);
            }
            if (i2 == 512) {
                return SystemProperties.get(CONFIG_WIFI_CNSS, "stopped").equals("running") && SystemProperties.get(CONFIG_WIFI_TCPDUMP, "stopped").equals("running");
            }
            if (i2 == 1024) {
                return SystemProperties.getInt(CONFIG_QSEE, 32) == 11;
            }
            if (i2 != 2048) {
                return false;
            }
            String str = CONFIG_UART_CONFIG;
            if (str == null) {
                Log.d(StandbyAbnormalDetector.TAG, "Unable to parse the uart log configuration info.");
                return false;
            }
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            if (iWhetstoneActivityManagerAsInterface != null) {
                try {
                    sysNodeInfo = iWhetstoneActivityManagerAsInterface.readSysNodeInfo(UART_FILE);
                } catch (RemoteException e2) {
                    throw new RuntimeException(e2);
                }
            } else {
                sysNodeInfo = null;
            }
            Log.d(StandbyAbnormalDetector.TAG, "cmdline=" + sysNodeInfo + ", uartEnabledStatus:" + str);
            return sysNodeInfo != null && sysNodeInfo.contains(str);
        }

        private boolean isOfflineModuleEnabled(String str) {
            return !SystemProperties.getBoolean(CONFIG_OFFLINE_LOG_DIAG_BUFFER, true) && SystemProperties.getBoolean(CONFIG_OFFLINE_LOG_ENABLE, false) && SystemProperties.get(CONFIG_OFFLINE_MODULE, "").equals(str);
        }
    }

    public StandbyAbnormalDetector(CloudData cloudData) {
        this.mCloudData = cloudData;
    }

    private StandbyAbnormalData checkAbnormalByPriority(StandbyOriginalData standbyOriginalData) {
        int i2;
        StandbyAbnormalData standbyAbnormalData = new StandbyAbnormalData();
        standbyAbnormalData.mSamplingTimeMs = standbyOriginalData.mEndRealtimeMs - standbyOriginalData.mStartRealtimeMs;
        standbyAbnormalData.setAvgCurrent(this.mAvgCurrent);
        if (checkFlashlight(standbyAbnormalData, standbyOriginalData)) {
            return null;
        }
        for (int i3 = 0; i3 < 10; i3++) {
            standbyAbnormalData.mEventId = DfsUtils.priorityToEventId(i3);
            boolean z2 = DfsUtils.DEBUG;
            if (z2 && (i2 = DfsUtils.debugEventId) != 0) {
                standbyAbnormalData.mEventId = i2;
            }
            switch (standbyAbnormalData.mEventId) {
                case DfsUtils.EVENT_ID_AOD /* 903111101 */:
                    if (checkAod(standbyAbnormalData, standbyOriginalData)) {
                        Log.d(TAG, "checkAbnormalByPriority result: aod");
                        return standbyAbnormalData;
                    }
                    break;
                case DfsUtils.EVENT_ID_LOG /* 903111201 */:
                    if (checkLogs(standbyAbnormalData)) {
                        Log.d(TAG, "checkAbnormalByPriority result: logs");
                        return standbyAbnormalData;
                    }
                    break;
                case DfsUtils.EVENT_ID_WAKEUP_ALARM /* 903111311 */:
                    if (checkWakeupReason(standbyAbnormalData, standbyOriginalData)) {
                        Log.d(TAG, "checkAbnormalByPriority result: wakeup");
                        return standbyAbnormalData;
                    }
                    break;
                case DfsUtils.EVENT_ID_PARTIAL_WAKELOCK /* 903111401 */:
                    if (checkWakelocks(standbyAbnormalData, standbyOriginalData, 0)) {
                        Log.d(TAG, "checkAbnormalByPriority result: partial wakelock");
                        return standbyAbnormalData;
                    }
                    break;
                case DfsUtils.EVENT_ID_KERNEL_WAKELOCK /* 903111402 */:
                    if (checkWakelocks(standbyAbnormalData, standbyOriginalData, 1)) {
                        Log.d(TAG, "checkAbnormalByPriority result: kernel wakelock");
                        return standbyAbnormalData;
                    }
                    break;
                case DfsUtils.EVENT_ID_DOZE /* 903111412 */:
                    if (checkDeviceIdle(standbyAbnormalData, standbyOriginalData)) {
                        Log.d(TAG, "checkAbnormalByPriority result: doze");
                        return standbyAbnormalData;
                    }
                    break;
                case DfsUtils.EVENT_ID_PHONE_SIGNAL_POOR /* 903111413 */:
                    if (checkSignalStrength(standbyAbnormalData, standbyOriginalData, false)) {
                        Log.d(TAG, "checkAbnormalByPriority result: modem signal poor");
                        return standbyAbnormalData;
                    }
                    break;
                case DfsUtils.EVENT_ID_WIFI_SIGNAL_POOR /* 903111414 */:
                    if (checkSignalStrength(standbyAbnormalData, standbyOriginalData, true)) {
                        Log.d(TAG, "checkAbnormalByPriority result: wifi signal poor");
                        return standbyAbnormalData;
                    }
                    break;
                default:
                    return standbyAbnormalData;
            }
            if (z2 && DfsUtils.debugEventId != 0) {
                Log.d(TAG, "checkAbnormalByPriority check for " + standbyAbnormalData.mEventId + " but NO Abnormal.");
                return standbyAbnormalData;
            }
        }
        standbyAbnormalData.mEventId = DfsUtils.EVENT_ID_OTHER;
        return standbyAbnormalData;
    }

    private boolean checkAod(StandbyAbnormalData standbyAbnormalData, StandbyOriginalData standbyOriginalData) {
        if (((long) this.mAvgCurrent) - (((standbyOriginalData.mScreenDozeS * 1000) * ((long) (DfsUtils.DEBUG ? 5 : 50))) / standbyAbnormalData.mSamplingTimeMs) <= this.mCloudData.getAbnormalCurrent(this.mWorkingScene)) {
            return false;
        }
        standbyAbnormalData.setAodTime((int) standbyOriginalData.mScreenDozeS);
        return true;
    }

    private boolean checkDeviceIdle(StandbyAbnormalData standbyAbnormalData, StandbyOriginalData standbyOriginalData) {
        boolean z2;
        int i2 = (int) ((((standbyOriginalData.mDeviceIdleFullTimeUs + standbyOriginalData.mDeviceIdleLightTimeUs) / 1000) * 100) / standbyAbnormalData.mSamplingTimeMs);
        if (i2 < 0 || i2 >= this.mCloudData.getDeviceIdleRatioAll()) {
            z2 = false;
        } else {
            standbyAbnormalData.setmDeviceIdleRadio(i2);
            z2 = true;
        }
        int i3 = (int) (standbyOriginalData.mDeviceIdleFullTimeUs / (standbyAbnormalData.mSamplingTimeMs * 10));
        if (i3 < 0 || i3 >= this.mCloudData.getDeviceIdleRatioDeep()) {
            return z2;
        }
        standbyAbnormalData.setmDeviceIdleDeepRadio(i3);
        return true;
    }

    private boolean checkFlashlight(StandbyAbnormalData standbyAbnormalData, StandbyOriginalData standbyOriginalData) {
        return standbyOriginalData.mFlashlightOnS >= ((long) this.mCloudData.getFlashlightMin()) * 60 && standbyOriginalData.mFlashlightOnS * 10000 > standbyAbnormalData.mSamplingTimeMs;
    }

    private boolean checkLogs(StandbyAbnormalData standbyAbnormalData) {
        Logs logs = new Logs();
        boolean z2 = false;
        for (int i2 = 0; i2 < 12; i2++) {
            int i3 = 1 << i2;
            if (logs.isLogEnabled(i3)) {
                Log.d(TAG, "Add log type " + String.format("%#x", Integer.valueOf(i3)) + " to AbnormalData.");
                standbyAbnormalData.addLogTypes(i3);
                z2 = true;
            }
        }
        return z2;
    }

    private boolean checkSignalStrength(StandbyAbnormalData standbyAbnormalData, StandbyOriginalData standbyOriginalData, boolean z2) {
        long j2;
        long j3;
        long totalRxTxPackets;
        long totalRxTxBytes;
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        if (batteryStats == null) {
            return false;
        }
        if (z2) {
            StandbyAbnormalData.SignalStrengthData signalStrengthData = standbyOriginalData.mWifiSignalStrengthStartData;
            j2 = signalStrengthData.poorTimeS;
            j3 = signalStrengthData.totalTimeS;
        } else {
            StandbyAbnormalData.SignalStrengthData signalStrengthData2 = standbyOriginalData.mPhoneSignalStrengthStartData;
            j2 = signalStrengthData2.poorTimeS;
            j3 = signalStrengthData2.totalTimeS;
        }
        StandbyAbnormalData.SignalStrengthData signalStrengthData3 = new StandbyAbnormalData.SignalStrengthData();
        signalStrengthData3.poorTimeS = UsageReader.getPoorSignalStrengthTime(batteryStats, 2, z2) - j2;
        long poorSignalStrengthTime = UsageReader.getPoorSignalStrengthTime(batteryStats, 5, z2) - j3;
        signalStrengthData3.totalTimeS = poorSignalStrengthTime;
        if (signalStrengthData3.poorTimeS < (poorSignalStrengthTime * ((long) this.mCloudData.getPoorSignalRatio(this.mWorkingScene))) / 100 || signalStrengthData3.poorTimeS < standbyAbnormalData.mSamplingTimeMs / 2500) {
            Log.d(TAG, "Discard! the poorTimes is too short.");
            if (!DfsUtils.DEBUG) {
                return false;
            }
        }
        long mobileRxTxPackets = UsageReader.getMobileRxTxPackets();
        long mobileRxTxBytes = UsageReader.getMobileRxTxBytes();
        if (z2) {
            Log.d(TAG, "checkSignalStrength originalDataWifi packages=" + standbyOriginalData.mWifiSignalStrengthStartData.packages + ", bytes=" + standbyOriginalData.mWifiSignalStrengthStartData.bytes);
            totalRxTxPackets = (UsageReader.getTotalRxTxPackets() - mobileRxTxPackets) - standbyOriginalData.mWifiSignalStrengthStartData.packages;
            totalRxTxBytes = (UsageReader.getTotalRxTxBytes() - mobileRxTxBytes) - standbyOriginalData.mWifiSignalStrengthStartData.bytes;
        } else {
            Log.d(TAG, "checkSignalStrength originalDataRadio packages=" + standbyOriginalData.mPhoneSignalStrengthStartData.packages + ", bytes=" + standbyOriginalData.mPhoneSignalStrengthStartData.bytes);
            StandbyAbnormalData.SignalStrengthData signalStrengthData4 = standbyOriginalData.mPhoneSignalStrengthStartData;
            totalRxTxPackets = mobileRxTxPackets - signalStrengthData4.packages;
            totalRxTxBytes = mobileRxTxBytes - signalStrengthData4.bytes;
        }
        Log.d(TAG, "checkSignalStrength packages=" + totalRxTxPackets + ", bytes=" + totalRxTxBytes);
        if (totalRxTxPackets < 2048000 && totalRxTxBytes < 524288000) {
            Log.d(TAG, "Discard! The tx/rx data is too little.");
            if (!DfsUtils.DEBUG) {
                return false;
            }
        }
        try {
            if (z2) {
                long kernelActiveTimeMillis = batteryStats.getWifiBatteryStats().getKernelActiveTimeMillis();
                if (kernelActiveTimeMillis - standbyOriginalData.mWifiSignalStrengthStartData.activeTimeMs < standbyAbnormalData.mSamplingTimeMs / 3) {
                    Log.d(TAG, "Discard! Maybe the wifi is not connect. activeTime=" + kernelActiveTimeMillis);
                    if (!DfsUtils.DEBUG) {
                        return false;
                    }
                }
            } else {
                long mobileRadioActiveTime = batteryStats.getMobileRadioActiveTime(SystemClock.elapsedRealtime() * 1000, 2) / 1000;
                if (mobileRadioActiveTime - standbyOriginalData.mPhoneSignalStrengthStartData.activeTimeMs < standbyAbnormalData.mSamplingTimeMs / 3) {
                    Log.d(TAG, "Discard! Maybe the mobile is not connect. activeTime=" + mobileRadioActiveTime);
                    if (!DfsUtils.DEBUG) {
                        return false;
                    }
                }
            }
            standbyAbnormalData.setSignalStrengthData(signalStrengthData3, z2);
            return true;
        } catch (RemoteException unused) {
            Log.d(TAG, "Can't get the active status. Just discard.");
            return false;
        }
    }

    private boolean checkWakelocks(StandbyAbnormalData standbyAbnormalData, StandbyOriginalData standbyOriginalData, int i2) {
        ArrayList<DfsUtils.TimerEntry> arrayList;
        ArrayList<DfsUtils.TimerEntry> arrayList2;
        int i3;
        long j2;
        int i4;
        long j3;
        long j4;
        ArrayList<DfsUtils.TimerEntry> arrayList3;
        DfsUtils.TimerEntry timerEntry;
        ArrayList<DfsUtils.TimerEntry> arrayList4 = new ArrayList<>();
        if (i2 == 0) {
            arrayList = standbyOriginalData.mStartPartialWakelockList;
            arrayList2 = standbyOriginalData.mEndPartialWakelockList;
        } else {
            arrayList = standbyOriginalData.mStartKernelWakelockList;
            arrayList2 = standbyOriginalData.mEndKernelWakelockList;
        }
        long j5 = standbyOriginalData.mEndRealtimeMs - standbyOriginalData.mStartRealtimeMs;
        DfsUtils.TimerEntry timerEntry2 = new DfsUtils.TimerEntry();
        int i5 = 0;
        long j6 = 0;
        while (i5 < arrayList2.size()) {
            DfsUtils.TimerEntry timerEntry3 = arrayList2.get(i5);
            String str = timerEntry3.name;
            int i6 = timerEntry3.id;
            ArrayList<DfsUtils.TimerEntry> arrayList5 = arrayList2;
            DfsUtils.TimerEntry timerEntry4 = null;
            for (int i7 = 0; i7 < arrayList.size(); i7++) {
                if (arrayList.get(i7).name.equals(str) && arrayList.get(i7).id == i6) {
                    timerEntry4 = arrayList.get(i7);
                }
            }
            if (timerEntry4 != null) {
                i3 = timerEntry4.count;
                j2 = timerEntry4.timeUs;
            } else {
                i3 = 0;
                j2 = 0;
            }
            int i8 = timerEntry3.count - i3;
            DfsUtils.TimerEntry timerEntry5 = timerEntry2;
            long j7 = timerEntry3.timeUs - j2;
            if (j7 == 0) {
                j3 = j5;
                i4 = i5;
                j4 = j6;
                arrayList3 = arrayList4;
                timerEntry = timerEntry5;
            } else {
                i4 = i5;
                if (DfsUtils.DEBUG || j7 >= ((((long) this.mCloudData.getMaxWakelockTimePerHour(this.mWorkingScene)) * j5) * 1000) / 60) {
                    if (j7 >= j6 || arrayList4.size() != 5) {
                        j3 = j5;
                        if (!arrayList4.isEmpty() || (j7 <= 0 && i8 <= 0)) {
                            j4 = j6;
                            long j8 = j2;
                            ArrayList<DfsUtils.TimerEntry> arrayList6 = arrayList4;
                            if (arrayList4.size() < 5) {
                                timerEntry = timerEntry5;
                                timerEntry.count = i8;
                                timerEntry.timeUs = j7;
                                timerEntry.name = str;
                                timerEntry.id = i6;
                                if (DfsUtils.DEBUG_INTERNAL) {
                                    Log.d(TAG, "timeStart=" + j8 + " timeEnd=" + timerEntry3.timeUs);
                                    Log.d(TAG, "Add new wakelock1 entry: " + str + " " + j7 + "us " + i8 + "times uid=" + i6);
                                }
                                int i9 = 0;
                                while (true) {
                                    arrayList3 = arrayList6;
                                    if (i9 < arrayList6.size()) {
                                        if (j7 > arrayList3.get(i9).timeUs) {
                                            arrayList3.add(i9, timerEntry);
                                            timerEntry2 = new DfsUtils.TimerEntry();
                                            j6 = j4;
                                        } else {
                                            i9++;
                                            if (i9 == arrayList3.size()) {
                                                arrayList3.add(i9, timerEntry);
                                                j6 = timerEntry.timeUs;
                                                timerEntry2 = new DfsUtils.TimerEntry();
                                            } else {
                                                arrayList6 = arrayList3;
                                            }
                                        }
                                    }
                                }
                            } else {
                                arrayList3 = arrayList6;
                                timerEntry = timerEntry5;
                                String str2 = "times uid=";
                                int i10 = 0;
                                while (true) {
                                    if (i10 < arrayList3.size()) {
                                        int i11 = i10;
                                        if (j7 > arrayList3.get(i10).timeUs) {
                                            timerEntry.count = i8;
                                            timerEntry.timeUs = j7;
                                            timerEntry.name = str;
                                            timerEntry.id = i6;
                                            if (DfsUtils.DEBUG_INTERNAL) {
                                                Log.d(TAG, "timeStart=" + j8 + " timeEnd=" + timerEntry3.timeUs);
                                                Log.d(TAG, "Add new wakelock2 entry: " + str + " " + j7 + "us " + i8 + str2 + i6);
                                            }
                                            arrayList3.add(i11, timerEntry);
                                            timerEntry2 = timerEntry;
                                            while (arrayList3.size() > 5) {
                                                timerEntry2 = arrayList3.remove(5);
                                            }
                                            j6 = arrayList3.get(arrayList3.size() - 1).timeUs;
                                        } else {
                                            str2 = str2;
                                            i10 = i11 + 1;
                                        }
                                    }
                                }
                            }
                        } else {
                            arrayList4.add(new DfsUtils.TimerEntry(str, j7, i8, i6));
                            if (DfsUtils.DEBUG_INTERNAL) {
                                Log.d(TAG, "timeStart=" + j2 + " timeEnd=" + timerEntry3.timeUs);
                                Log.d(TAG, "Add first wakelock entry: " + str + " " + j7 + "us " + i8 + "times uid=" + i6);
                            }
                            j6 = j7;
                            arrayList3 = arrayList4;
                            timerEntry2 = timerEntry5;
                        }
                        i5 = i4 + 1;
                        arrayList4 = arrayList3;
                        arrayList2 = arrayList5;
                        arrayList = arrayList;
                        j5 = j3;
                    }
                } else if (DfsUtils.DEBUG_INTERNAL) {
                    Log.d(TAG, "deltaTime=" + j7 + " is smaller than config=" + ((((long) this.mCloudData.getMaxWakelockTimePerHour(this.mWorkingScene)) * j5) / 3600000));
                    Log.d(TAG, "Discard this entry: " + str + ", uid=" + timerEntry3.id);
                }
                arrayList3 = arrayList4;
                j3 = j5;
                j4 = j6;
                timerEntry = timerEntry5;
            }
            timerEntry2 = timerEntry;
            j6 = j4;
            i5 = i4 + 1;
            arrayList4 = arrayList3;
            arrayList2 = arrayList5;
            arrayList = arrayList;
            j5 = j3;
        }
        ArrayList<DfsUtils.TimerEntry> arrayList7 = arrayList4;
        if (arrayList7.isEmpty()) {
            return false;
        }
        if (i2 == 0) {
            standbyAbnormalData.setPartialWakelocks(arrayList7);
            return true;
        }
        standbyAbnormalData.setKernelWakelocks(arrayList7);
        return true;
    }

    private boolean checkWakeupReason(StandbyAbnormalData standbyAbnormalData, StandbyOriginalData standbyOriginalData) {
        long j2;
        int i2;
        DfsUtils.TimerEntry timerEntry;
        DfsUtils.TimerEntry timerEntry2;
        int i3;
        DfsUtils.TimerEntry timerEntry3;
        StandbyOriginalData standbyOriginalData2 = standbyOriginalData;
        ArrayList<DfsUtils.TimerEntry> arrayList = new ArrayList<>();
        long j3 = standbyOriginalData2.mEndRealtimeMs - standbyOriginalData2.mStartRealtimeMs;
        DfsUtils.TimerEntry timerEntry4 = new DfsUtils.TimerEntry();
        int i4 = 0;
        int i5 = 0;
        while (i4 < standbyOriginalData2.mEndWakeupReasonMap.size()) {
            DfsUtils.TimerEntry timerEntry5 = standbyOriginalData2.mEndWakeupReasonMap.get(i4);
            String str = timerEntry5.name;
            DfsUtils.TimerEntry timerEntry6 = null;
            for (int i6 = 0; i6 < standbyOriginalData2.mStartWakeupReasonMap.size(); i6++) {
                if (standbyOriginalData2.mStartWakeupReasonMap.get(i6).name.equals(str)) {
                    timerEntry6 = standbyOriginalData2.mStartWakeupReasonMap.get(i6);
                }
            }
            if (timerEntry6 != null) {
                i2 = timerEntry6.count;
                j2 = timerEntry6.timeUs;
            } else {
                j2 = 0;
                i2 = 0;
            }
            int i7 = timerEntry5.count - i2;
            long j4 = timerEntry5.timeUs - j2;
            boolean z2 = DfsUtils.DEBUG;
            if ((!z2 || DfsUtils.debugWakeup == null || StandbyAbnormalData.getWakeupType(timerEntry5.name) == StandbyAbnormalData.getWakeupType(DfsUtils.debugWakeup)) && i7 != 0) {
                if (z2) {
                    timerEntry = timerEntry4;
                    timerEntry2 = timerEntry5;
                } else {
                    timerEntry2 = timerEntry5;
                    timerEntry = timerEntry4;
                    if (i7 < (((long) this.mCloudData.getMaxWakeupCountPerHour(this.mWorkingScene)) * j3) / 3600000) {
                        if (DfsUtils.DEBUG_INTERNAL) {
                            Log.d(TAG, "deltaCount=" + i7 + " is smaller than config=" + ((((long) this.mCloudData.getMaxWakeupCountPerHour(this.mWorkingScene)) * j3) / 3600000));
                            StringBuilder sb = new StringBuilder();
                            sb.append("Discard this entry: ");
                            sb.append(str);
                            Log.d(TAG, sb.toString());
                        }
                    }
                    j3 = j3;
                    i3 = i5;
                    timerEntry3 = timerEntry;
                }
                if (i7 >= i5 || arrayList.size() < 5) {
                    if (!arrayList.isEmpty() || (j4 <= 0 && i7 <= 0)) {
                        j3 = j3;
                        DfsUtils.TimerEntry timerEntry7 = timerEntry2;
                        long j5 = j2;
                        i3 = i5;
                        if (arrayList.size() < 5) {
                            timerEntry3 = timerEntry;
                            timerEntry3.count = i7;
                            timerEntry3.timeUs = j4;
                            timerEntry3.name = str;
                            if (DfsUtils.DEBUG_INTERNAL) {
                                Log.d(TAG, "timeStart=" + j5 + " timeEnd=" + timerEntry7.timeUs);
                                Log.d(TAG, "Add new wakeup1 entry: " + str + " " + j4 + "us " + i7 + "times");
                            }
                            int i8 = 0;
                            while (true) {
                                if (i8 < arrayList.size()) {
                                    if (i7 > arrayList.get(i8).count) {
                                        arrayList.add(i8, timerEntry3);
                                        timerEntry4 = new DfsUtils.TimerEntry();
                                        i5 = i3;
                                    } else {
                                        i8++;
                                        if (i8 == arrayList.size()) {
                                            arrayList.add(i8, timerEntry3);
                                            i5 = timerEntry3.count;
                                            timerEntry4 = new DfsUtils.TimerEntry();
                                        }
                                    }
                                }
                            }
                        } else {
                            timerEntry3 = timerEntry;
                            String str2 = "times";
                            int i9 = 0;
                            while (true) {
                                if (i9 < arrayList.size()) {
                                    if (i7 > arrayList.get(i9).count) {
                                        timerEntry3.count = i7;
                                        timerEntry3.timeUs = j4;
                                        timerEntry3.name = str;
                                        if (DfsUtils.DEBUG_INTERNAL) {
                                            Log.d(TAG, "timeStart=" + j5 + " timeEnd=" + timerEntry7.timeUs);
                                            Log.d(TAG, "Add new wakeup2 entry: " + str + " " + j4 + "us " + i7 + str2);
                                        }
                                        arrayList.add(i9, timerEntry3);
                                        timerEntry4 = timerEntry3;
                                        while (arrayList.size() > 5) {
                                            timerEntry4 = arrayList.remove(5);
                                        }
                                        i5 = arrayList.get(arrayList.size() - 1).count;
                                    } else {
                                        i9++;
                                        str2 = str2;
                                    }
                                }
                            }
                        }
                    } else {
                        arrayList.add(new DfsUtils.TimerEntry(str, j4, i7));
                        if (DfsUtils.DEBUG_INTERNAL) {
                            Log.d(TAG, "timeStart=" + j2 + " timeEnd=" + timerEntry2.timeUs);
                            Log.d(TAG, "Add first wakeup entry: " + str + " " + j4 + "us " + i7 + "times");
                        }
                        i5 = i7;
                        timerEntry4 = timerEntry;
                    }
                    i4++;
                    standbyOriginalData2 = standbyOriginalData;
                    j3 = j3;
                } else {
                    j3 = j3;
                    i3 = i5;
                    timerEntry3 = timerEntry;
                }
            } else {
                j3 = j3;
                timerEntry3 = timerEntry4;
                i3 = i5;
            }
            timerEntry4 = timerEntry3;
            i5 = i3;
            i4++;
            standbyOriginalData2 = standbyOriginalData;
            j3 = j3;
        }
        if (arrayList.isEmpty()) {
            return false;
        }
        standbyAbnormalData.setWakeupReasons(arrayList);
        return true;
    }

    @Nullable
    public StandbyAbnormalData getAbnormalData(StandbyOriginalData standbyOriginalData, int i2) {
        this.mWorkingScene = i2;
        if (isPowerAbnormal(standbyOriginalData)) {
            return checkAbnormalByPriority(standbyOriginalData);
        }
        return null;
    }

    boolean isPowerAbnormal(StandbyOriginalData standbyOriginalData) {
        this.mAvgCurrent = (int) (((standbyOriginalData.mStartChargeCounter - standbyOriginalData.mEndChargeCounter) * 3600000.0d) / (standbyOriginalData.mEndRealtimeMs - standbyOriginalData.mStartRealtimeMs));
        Log.d(TAG, "isPowerAbnormal mStartChargeCounter=" + standbyOriginalData.mStartChargeCounter + ", mEndChargeCounter=" + standbyOriginalData.mEndChargeCounter);
        Log.d(TAG, "isPowerAbnormal current=" + this.mAvgCurrent + ", config current=" + this.mCloudData.getAbnormalCurrent(this.mWorkingScene));
        return this.mAvgCurrent > this.mCloudData.getAbnormalCurrent(this.mWorkingScene);
    }
}
