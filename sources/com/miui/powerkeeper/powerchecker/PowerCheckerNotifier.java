package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import com.miui.powerkeeper.provider.AppActiveConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigure;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerNotifier {
    protected static final int MAX_SUMMARY_ENTRY1 = 10;
    protected static final int MAX_SUMMARY_ENTRY2 = 5;
    public static final String RECORD_SPLITER = "======================================";
    private static final String TAG = "PowerChecker.Notifier";
    protected static final String TOTAL_KERNEL_WAKELOCKS_NAME = "kernel_wakelocks";
    protected static final String TOTAL_KERNEL_WAKEUPS_NAME = "kernel_wakeups";
    protected long mBatteryRealTimeInc;
    protected long mBatteryRealTimeSinceCurrent;
    protected PowerCheckerService.BatteryStatsTime mBatteryStatsTime;
    protected long mDummyRealTimeSinceCurrent;
    protected boolean mUnplugEventFlag = false;
    protected static final boolean DEBUG = PowerCheckerService.DEBUG;
    protected static int WHICH = PowerCheckerService.WHICH;

    public static class AppPowerExceedInfo extends PowerExceedInfo {
        CpuTime cpuTime;
        BaseInfo info;
        CpuTime lastCpuTime;
        BaseInfo lastInfo;
        ArrayMap<String, Timer> lastPartialWakelocks;
        ArrayMap<String, Timer> lastSyncs;
        int lastWakeupTimes;
        String packageName;
        String packageVersion;
        String pkg;
        String proc;
        int uid;
        UidStatsData uidObj;
        int wakeupTimes;

        public AppPowerExceedInfo(int i2, int i3, UidStatsInfo uidStatsInfo, UidRecordInfo uidRecordInfo) {
            super(i2, i3);
            this.lastPartialWakelocks = new ArrayMap<>();
            this.lastSyncs = new ArrayMap<>();
            this.uid = uidStatsInfo.uid;
            this.uidObj = uidStatsInfo.uidObj;
            this.info = new BaseInfo(uidStatsInfo.info);
            if (uidRecordInfo == null) {
                this.lastInfo = new BaseInfo();
            } else {
                this.lastInfo = new BaseInfo(uidRecordInfo.info);
            }
            this.lastPartialWakelocks = null;
            this.lastSyncs = null;
            this.proc = null;
            this.cpuTime = null;
            this.lastCpuTime = null;
            this.pkg = null;
            this.wakeupTimes = 0;
            this.lastWakeupTimes = 0;
            this.packageName = uidStatsInfo.packageName;
            this.packageVersion = uidStatsInfo.packageVersion;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerNotifier.PowerExceedInfo
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("  uid = " + this.uid + ShellUtils.COMMAND_LINE_END);
            sb.append(super.toString());
            BaseInfo baseInfo = this.info;
            if (baseInfo != null) {
                sb.append(baseInfo.toString());
            }
            BaseInfo baseInfo2 = this.lastInfo;
            if (baseInfo2 != null) {
                sb.append(baseInfo2.toString());
            }
            ArrayMap<String, Timer> arrayMap = this.lastPartialWakelocks;
            if (arrayMap != null && arrayMap.size() > 0) {
                sb.append("  last partial wake locks");
                for (Map.Entry<String, Timer> entry : this.lastPartialWakelocks.entrySet()) {
                    Timer value = entry.getValue();
                    if (value != null) {
                        sb.append("    " + entry.getKey() + " " + value.toString());
                    }
                }
            }
            ArrayMap<String, Timer> arrayMap2 = this.lastSyncs;
            if (arrayMap2 != null && arrayMap2.size() > 0) {
                sb.append("  last syncs");
                for (Map.Entry<String, Timer> entry2 : this.lastSyncs.entrySet()) {
                    Timer value2 = entry2.getValue();
                    if (value2 != null) {
                        sb.append("    " + entry2.getKey() + " " + value2.toString());
                    }
                }
            }
            sb.append("  proc = " + this.proc);
            sb.append(", cpuTime = " + this.cpuTime);
            sb.append(", lastCpuTime = " + this.lastCpuTime + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("  pkg = ");
            sb2.append(this.pkg);
            sb.append(sb2.toString());
            sb.append(", wakeupTimes = " + this.wakeupTimes);
            sb.append(", lastWakeupTimes = " + this.lastWakeupTimes + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb3 = new StringBuilder();
            sb3.append("  packageName = ");
            sb3.append(this.packageName);
            sb.append(sb3.toString());
            sb.append(", packageVersion = " + this.packageVersion + ShellUtils.COMMAND_LINE_END);
            return sb.toString();
        }
    }

    public static class BatteryHistoryItem {
        public static final int SUB_SYSTEM_ADSP_TYPE = 4;
        public static final int SUB_SYSTEM_APSS_TYPE = 1;
        public static final int SUB_SYSTEM_CDSP_TYPE = 16;
        public static final int SUB_SYSTEM_MPSS_TYPE = 2;
        public static final int SUB_SYSTEM_SLPI_TYPE = 8;
        public long time;
        public int batteryLevel = -1;
        public double chargeCounter = -1.0d;
        public int batteryStatus = -1;
        public int batteryPlugType = -1;
        public boolean screenOn = true;
        public boolean active = false;
        public long rpmVddLowCount = -1;
        public long rpmVddMinCount = -1;
        public long rpmAPSSXOCount = -1;
        public long rpmMPSSXOCount = -1;
        public long rpmADSPXOCount = -1;
        public long rpmSLPIXOCount = -1;
        public long rpmCDSPXOCount = -1;
        public long rpmVddLowDuration = -1;
        public long rpmVddMinDuration = -1;
        public String rpmVddLowName = null;
        public String rpmVddMinName = null;
        public HashMap<String, ModuleRpmSubsysStats> rpmSubsysStats = new HashMap<>();

        public BatteryHistoryItem(long j2) {
            this.time = j2;
        }

        public boolean checkSubSystemInfo(BatteryHistoryItem batteryHistoryItem) {
            long j2 = this.rpmVddMinCount;
            if (j2 != -1) {
                long j3 = batteryHistoryItem.rpmVddMinCount;
                if (j3 != -1 && j2 == j3) {
                    return true;
                }
            }
            return false;
        }

        public int getSubSystemAbnormalType(BatteryHistoryItem batteryHistoryItem) {
            int i2 = this.rpmAPSSXOCount == batteryHistoryItem.rpmAPSSXOCount ? 1 : 0;
            if (this.rpmMPSSXOCount == batteryHistoryItem.rpmMPSSXOCount) {
                i2 |= 2;
            }
            if (this.rpmADSPXOCount == batteryHistoryItem.rpmADSPXOCount) {
                i2 |= 4;
            }
            if (this.rpmSLPIXOCount == batteryHistoryItem.rpmSLPIXOCount) {
                i2 |= 8;
            }
            return this.rpmCDSPXOCount == batteryHistoryItem.rpmCDSPXOCount ? i2 | 16 : i2;
        }

        public boolean isOnBatteryScreenOffNonActive() {
            return (this.screenOn || (this.batteryPlugType & 15) != 0 || this.active) ? false : true;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(this.time);
            sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", calendar, calendar, calendar, calendar, calendar, calendar));
            sb.append(" level=" + this.batteryLevel);
            sb.append(" charge_counter=" + this.chargeCounter);
            sb.append(" status=" + this.batteryStatus);
            sb.append(" plug=" + this.batteryPlugType);
            if (this.screenOn) {
                sb.append(" screen=on");
            } else {
                sb.append(" screen=off");
            }
            sb.append(" active=" + this.active);
            sb.append(" RPM Mode:" + this.rpmVddLowName);
            sb.append("{ Count: " + this.rpmVddLowCount);
            sb.append(", Mode Duration: ");
            PowerCheckerUtils.formatTimeMs(sb, this.rpmVddLowDuration);
            sb.append("} RPM Mode:" + this.rpmVddMinName);
            sb.append("{ Count: " + this.rpmVddMinCount);
            sb.append(", Mode Duration: ");
            PowerCheckerUtils.formatTimeMs(sb, this.rpmVddMinDuration);
            sb.append("}");
            HashMap<String, ModuleRpmSubsysStats> map = this.rpmSubsysStats;
            if (map != null && map.size() != 0) {
                for (Map.Entry<String, ModuleRpmSubsysStats> entry : this.rpmSubsysStats.entrySet()) {
                    sb.append(" " + entry.getKey() + ": {");
                    ModuleRpmSubsysStats value = entry.getValue();
                    if (value != null) {
                        sb.append(value.toString());
                    }
                    sb.append("}");
                }
                return sb.toString();
            }
            sb.append(" rpmAPSSXOCount=" + this.rpmAPSSXOCount);
            sb.append(" rpmMPSSXOCount=" + this.rpmMPSSXOCount);
            sb.append(" rpmADSPXOCount=" + this.rpmADSPXOCount);
            sb.append(" rpmSLPIXOCount=" + this.rpmSLPIXOCount);
            sb.append(" rpmCDSPXOCount=" + this.rpmCDSPXOCount);
            return sb.toString();
        }
    }

    public static class CommonPowerExceedInfo extends PowerExceedInfo {
        long screenOffTotalAudioOnTimeMs;
        long screenOffTotalBytes;

        public CommonPowerExceedInfo(int i2, int i3) {
            super(i2, i3);
            this.screenOffTotalBytes = 0L;
            this.screenOffTotalAudioOnTimeMs = 0L;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerNotifier.PowerExceedInfo
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("  screenOffTotalBytes = " + this.screenOffTotalBytes + ShellUtils.COMMAND_LINE_END);
            sb.append("  screenOffAudioOnTimeMs = ");
            PowerCheckerUtils.formatTimeMs(sb, this.screenOffTotalAudioOnTimeMs);
            sb.append(ShellUtils.COMMAND_LINE_END);
            return sb.toString();
        }
    }

    public static class CpuTime {
        long foregroundTime;
        long systemTime;
        long userTime;

        public CpuTime(long j2, long j3, long j4) {
            this.userTime = j2;
            this.systemTime = j3;
            this.foregroundTime = j4;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            PowerCheckerUtils.formatTimeMs(sb, this.userTime);
            sb.append("usr + ");
            PowerCheckerUtils.formatTimeMs(sb, this.systemTime);
            sb.append("krn ; ");
            PowerCheckerUtils.formatTimeMs(sb, this.foregroundTime);
            sb.append(NightStandbyRecord.KEY_FOREGROUND_ACTIVITIES);
            return sb.toString();
        }
    }

    public static class KWLPowerExceedInfo extends CommonPowerExceedInfo {
        public static final int KERNEL_WAKELOCK_TYPE_COUNT = 2;
        public static final int KERNEL_WAKELOCK_TYPE_TIME = 1;
        public static final int KERNEL_WAKEUP_TYPE_COUNT = 4;
        public static final int KERNEL_WAKEUP_TYPE_TIME = 3;
        int kType;
        PowerCheckerUtils.TimerEntry ktimer;
        ArrayList<PowerCheckerUtils.TimerEntry> ktimers;
        Timer lastKtimer;
        int totalKCount;
        long totalKTime;

        public KWLPowerExceedInfo(int i2, int i3, int i4, ArrayList<PowerCheckerUtils.TimerEntry> arrayList, PowerCheckerUtils.TimerEntry timerEntry, Timer timer, long j2, int i5) {
            super(i2, i3);
            new ArrayList();
            this.kType = i4;
            this.ktimers = arrayList;
            this.ktimer = timerEntry;
            this.lastKtimer = timer;
            this.totalKTime = j2;
            this.totalKCount = i5;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerNotifier.CommonPowerExceedInfo, com.miui.powerkeeper.powerchecker.PowerCheckerNotifier.PowerExceedInfo
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("  kType = " + this.kType + ShellUtils.COMMAND_LINE_END);
            if (this.ktimer != null) {
                sb.append("  name = " + this.ktimer.mName);
                sb.append(", time = " + this.ktimer.mTime + ShellUtils.COMMAND_LINE_END);
            }
            Timer timer = this.lastKtimer;
            if (timer != null) {
                sb.append(timer.toString());
            }
            sb.append("  totalKWLTime = " + this.totalKTime);
            sb.append("  totalKWLCount = " + this.totalKCount);
            return sb.toString();
        }
    }

    public static class ModuleRpmSubsysStats {
        public long rpmSubsysCount;
        public long rpmSubsysDuration;
        public long rpmSubsysEntry;
        public long rpmSubsysExit;
        public long rpmSubsysStatus;

        public ModuleRpmSubsysStats(long j2, long j3, long j4, long j5, long j6) {
            this.rpmSubsysCount = j2;
            this.rpmSubsysEntry = j3;
            this.rpmSubsysExit = j4;
            this.rpmSubsysDuration = j5;
            this.rpmSubsysStatus = j6;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Count: ");
            sb.append(this.rpmSubsysCount);
            sb.append(", Duration: ");
            PowerCheckerUtils.formatTimeMs(sb, this.rpmSubsysDuration / 19200);
            sb.append(", Status: ");
            sb.append(this.rpmSubsysStatus == 1 ? GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_SLEEP : AppActiveConfigure.Columns.ACTIVE);
            return sb.toString();
        }
    }

    public static class PowerExceedInfo {
        int priority;
        int type;

        public PowerExceedInfo(int i2, int i3) {
            this.type = i2;
            this.priority = i3;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("  type = " + this.type);
            sb.append(", priority = " + this.priority + ShellUtils.COMMAND_LINE_END);
            return sb.toString();
        }
    }

    public static class SubSystemPowerExceedInfo extends CommonPowerExceedInfo {
        BatteryHistoryItem curItem;
        String debugInfo;
        BatteryHistoryItem lastItem;
        int subType;

        public SubSystemPowerExceedInfo(int i2, int i3, BatteryHistoryItem batteryHistoryItem, BatteryHistoryItem batteryHistoryItem2, String str) {
            super(i2, i3);
            this.curItem = batteryHistoryItem;
            this.lastItem = batteryHistoryItem2;
            this.debugInfo = str;
            this.subType = 0;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerNotifier.CommonPowerExceedInfo, com.miui.powerkeeper.powerchecker.PowerCheckerNotifier.PowerExceedInfo
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("  subType = " + this.subType + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("  lastItem = ");
            sb2.append(this.lastItem);
            sb.append(sb2.toString());
            sb.append(", curItem = " + this.curItem + ShellUtils.COMMAND_LINE_END);
            String str = this.debugInfo;
            if (str != null) {
                sb.append(str.toString());
            }
            return sb.toString();
        }
    }

    public static class Timer {
        int count;
        long time;

        public Timer(long j2, int i2) {
            this.time = j2;
            this.count = i2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            PowerCheckerUtils.formatTimeMs(sb, this.time);
            sb.append(" (" + this.count + " times) realtime");
            return sb.toString();
        }
    }

    public static class UidRecordInfo {
        BaseInfo info;
        int uid;

        public UidRecordInfo(UidStatsInfo uidStatsInfo) {
            this.uid = uidStatsInfo.uid;
            this.info = new BaseInfo(uidStatsInfo.info);
        }

        public String toString() {
            return this.info.toString();
        }
    }

    public static class UidStatsInfo {
        String packageName;
        String packageVersion;
        int uid;
        UidStatsData uidObj;
        ArrayMap<String, CpuTime> procCpuTimeStats = new ArrayMap<>();
        ArrayMap<String, Integer> pkgWakeupStats = new ArrayMap<>();
        BaseInfo info = new BaseInfo();

        public UidStatsInfo(Context context, int i2, UidStatsData uidStatsData) {
            this.uid = i2;
            this.uidObj = uidStatsData;
            this.packageName = null;
            this.packageVersion = null;
            if (this.uid >= 10000) {
                String[] packagesForUid = context.getPackageManager().getPackagesForUid(this.uid);
                if (packagesForUid == null || packagesForUid.length <= 0) {
                    this.packageName = Integer.toString(this.uid);
                } else {
                    this.packageName = packagesForUid[0];
                    this.packageVersion = PackageUtil.getAppVersion(context.getPackageManager(), this.packageName);
                }
            }
        }
    }

    void clearUnPluggedEvent() {
        this.mUnplugEventFlag = false;
    }

    long getBatteryRealTimeInc() {
        return this.mBatteryRealTimeInc;
    }

    long getBatteryRealTimeSinceCurrent() {
        return this.mBatteryRealTimeSinceCurrent;
    }

    long getDummyRealTimeSinceCurrent() {
        return this.mDummyRealTimeSinceCurrent;
    }

    void notifyStartSchedulePowerCheckerCycle() {
        onStartSchedulePowerCheckerCycle();
    }

    void notifyStopPowerCheckerImmediately() {
        onStopPowerCheckerImmediately();
    }

    void notifyUnPluggedEvent() {
        Log.d(TAG, "UnPlugged event happend after last check");
        this.mUnplugEventFlag = true;
    }

    void updateBatteryStatsTime(PowerCheckerService.BatteryStatsTime batteryStatsTime, long j2) {
        this.mBatteryStatsTime = batteryStatsTime;
        this.mBatteryRealTimeInc = j2;
    }

    void updatePowerCheckerBatteryStatsTime(long j2, long j3) {
        this.mBatteryRealTimeSinceCurrent = j2;
        this.mDummyRealTimeSinceCurrent = j3;
    }

    protected void onStartSchedulePowerCheckerCycle() {
    }

    protected void onStopPowerCheckerImmediately() {
    }

    public static class BaseInfo {
        long backgroundAudioOnTimeMs;
        int backgroundAudioOnTimes;
        long backgroundBytes;
        int countGPS;
        long fgActivityTimeMs;
        int fgActivityTimes;
        long fullWifiLockTime;
        long gpsSensorHoldTime;
        int mobileActiveCount;
        long mobileActiveTime;
        long mobileBgRxBytes;
        long mobileBgRxPackets;
        long mobileBgTxBytes;
        long mobileBgTxPackets;
        long mobileRxBytes;
        long mobileRxPackets;
        long mobileTxBytes;
        long mobileTxPackets;
        long screenOffAudioOnTimeMs;
        int screenOffAudioOnTimes;
        long screenOffBytes;
        long totalFgTimeMs;
        int totalFgTimes;
        long totalFull;
        long totalPartial;
        long totalPartialSinceCharged;
        long totalSystemCpuTime;
        long totalUserCpuTime;
        long totalWindow;
        long wifiBgRxBytes;
        long wifiBgRxPackets;
        long wifiBgTxBytes;
        long wifiBgTxPackets;
        long wifiRxBytes;
        long wifiRxPackets;
        long wifiScanTime;
        long wifiTxBytes;
        long wifiTxPackets;

        public BaseInfo() {
            this.totalFgTimeMs = 0L;
            this.totalFgTimes = 0;
            this.totalFull = 0L;
            this.totalPartial = 0L;
            this.totalWindow = 0L;
            this.totalPartialSinceCharged = 0L;
            this.fullWifiLockTime = 0L;
            this.wifiScanTime = 0L;
            this.countGPS = 0;
            this.gpsSensorHoldTime = 0L;
            this.totalUserCpuTime = 0L;
            this.totalSystemCpuTime = 0L;
            this.mobileActiveTime = 0L;
            this.mobileActiveCount = 0;
            this.mobileRxBytes = 0L;
            this.mobileTxBytes = 0L;
            this.wifiRxBytes = 0L;
            this.wifiTxBytes = 0L;
            this.mobileRxPackets = 0L;
            this.mobileTxPackets = 0L;
            this.wifiRxPackets = 0L;
            this.wifiTxPackets = 0L;
            this.mobileBgRxBytes = 0L;
            this.mobileBgTxBytes = 0L;
            this.mobileBgRxPackets = 0L;
            this.mobileBgTxPackets = 0L;
            this.wifiBgRxBytes = 0L;
            this.wifiBgTxBytes = 0L;
            this.wifiBgRxPackets = 0L;
            this.wifiBgTxPackets = 0L;
            this.screenOffBytes = 0L;
            this.backgroundBytes = 0L;
            this.fgActivityTimeMs = 0L;
            this.fgActivityTimes = 0;
            this.screenOffAudioOnTimeMs = 0L;
            this.screenOffAudioOnTimes = 0;
            this.backgroundAudioOnTimeMs = 0L;
            this.backgroundAudioOnTimes = 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("  totalFgTimeMs = ");
            PowerCheckerUtils.formatTimeMs(sb, this.totalFgTimeMs);
            sb.append(", totalFgTimes = " + this.totalFgTimes + ShellUtils.COMMAND_LINE_END);
            sb.append("  totalFull = ");
            PowerCheckerUtils.formatTimeMs(sb, this.totalFull);
            sb.append(", totalPartial = ");
            PowerCheckerUtils.formatTimeMs(sb, this.totalPartial);
            sb.append(", totalWindow = ");
            PowerCheckerUtils.formatTimeMs(sb, this.totalWindow);
            sb.append(", totalPartialSinceCharged = ");
            PowerCheckerUtils.formatTimeMs(sb, this.totalPartialSinceCharged);
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("  fullWifiLockTime = ");
            PowerCheckerUtils.formatTimeMs(sb, this.fullWifiLockTime);
            sb.append(", wifiScanTime = ");
            PowerCheckerUtils.formatTimeMs(sb, this.wifiScanTime);
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("  gpsSensorHoldTime = ");
            PowerCheckerUtils.formatTimeMs(sb, this.gpsSensorHoldTime);
            sb.append(", countGPS = " + this.countGPS + ShellUtils.COMMAND_LINE_END);
            sb.append("  totalUserCpuTime = ");
            PowerCheckerUtils.formatTimeMs(sb, this.totalUserCpuTime);
            sb.append(", totalSystemCpuTime = ");
            PowerCheckerUtils.formatTimeMs(sb, this.totalSystemCpuTime);
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("  mobileActiveTime = ");
            PowerCheckerUtils.formatTimeMs(sb, this.mobileActiveTime);
            sb.append(", mobileActiveCount = " + this.mobileActiveCount + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("  mobileRxBytes = ");
            sb2.append(this.mobileRxBytes);
            sb.append(sb2.toString());
            sb.append(", mobileTxBytes = " + this.mobileTxBytes + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb3 = new StringBuilder();
            sb3.append("  mobileRxPackets = ");
            sb3.append(this.mobileRxPackets);
            sb.append(sb3.toString());
            sb.append(", mobileTxPackets = " + this.mobileTxPackets + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb4 = new StringBuilder();
            sb4.append("  wifiRxBytes = ");
            sb4.append(this.wifiRxBytes);
            sb.append(sb4.toString());
            sb.append(", wifiTxBytes = " + this.wifiTxBytes + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb5 = new StringBuilder();
            sb5.append("  wifiRxPackets = ");
            sb5.append(this.wifiRxPackets);
            sb.append(sb5.toString());
            sb.append(", wifiTxPackets = " + this.wifiTxPackets + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb6 = new StringBuilder();
            sb6.append("  mobileBgRxBytes = ");
            sb6.append(this.mobileBgRxBytes);
            sb.append(sb6.toString());
            sb.append(", mobileBgTxBytes = " + this.mobileBgTxBytes + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb7 = new StringBuilder();
            sb7.append("  wifiBgRxBytes = ");
            sb7.append(this.wifiBgRxBytes);
            sb.append(sb7.toString());
            sb.append(", wifiBgTxBytes = " + this.wifiBgTxBytes + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb8 = new StringBuilder();
            sb8.append("  mobileBgRxPackets = ");
            sb8.append(this.mobileBgRxPackets);
            sb.append(sb8.toString());
            sb.append(", mobileBgTxPackets = " + this.mobileBgTxPackets + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb9 = new StringBuilder();
            sb9.append("  wifiBgRxPackets = ");
            sb9.append(this.wifiBgRxPackets);
            sb.append(sb9.toString());
            sb.append(", wifiBgTxPackets = " + this.wifiBgTxPackets + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb10 = new StringBuilder();
            sb10.append("  screenOffBytes = ");
            sb10.append(this.screenOffBytes);
            sb.append(sb10.toString());
            sb.append(", backgroundBytes = " + this.backgroundBytes + ShellUtils.COMMAND_LINE_END);
            sb.append("  fgActivityTimeMs = ");
            PowerCheckerUtils.formatTimeMs(sb, this.fgActivityTimeMs);
            sb.append(", fgActivityTimes = " + this.fgActivityTimes + ShellUtils.COMMAND_LINE_END);
            sb.append("  screenOffAudioOnTimeMs = ");
            PowerCheckerUtils.formatTimeMs(sb, this.screenOffAudioOnTimeMs);
            sb.append(", screenOffAudioOnTimes = " + this.screenOffAudioOnTimes + ShellUtils.COMMAND_LINE_END);
            sb.append("  backgroundAudioOnTimeMs = ");
            PowerCheckerUtils.formatTimeMs(sb, this.backgroundAudioOnTimeMs);
            sb.append(", backgroundAudioOnTimes = " + this.backgroundAudioOnTimes + ShellUtils.COMMAND_LINE_END);
            return sb.toString();
        }

        public BaseInfo(BaseInfo baseInfo) {
            this.totalFgTimeMs = baseInfo.totalFgTimeMs;
            this.totalFgTimes = baseInfo.totalFgTimes;
            this.totalFull = baseInfo.totalFull;
            this.totalPartial = baseInfo.totalPartial;
            this.totalWindow = baseInfo.totalWindow;
            this.totalPartialSinceCharged = baseInfo.totalPartialSinceCharged;
            this.fullWifiLockTime = baseInfo.fullWifiLockTime;
            this.wifiScanTime = baseInfo.wifiScanTime;
            this.countGPS = baseInfo.countGPS;
            this.gpsSensorHoldTime = baseInfo.gpsSensorHoldTime;
            this.totalUserCpuTime = baseInfo.totalUserCpuTime;
            this.totalSystemCpuTime = baseInfo.totalSystemCpuTime;
            this.mobileActiveTime = baseInfo.mobileActiveTime;
            this.mobileActiveCount = baseInfo.mobileActiveCount;
            this.mobileRxBytes = baseInfo.mobileRxBytes;
            this.mobileTxBytes = baseInfo.mobileTxBytes;
            this.wifiRxBytes = baseInfo.wifiRxBytes;
            this.wifiTxBytes = baseInfo.wifiTxBytes;
            this.mobileRxPackets = baseInfo.mobileRxPackets;
            this.mobileTxPackets = baseInfo.mobileTxPackets;
            this.wifiRxPackets = baseInfo.wifiRxPackets;
            this.wifiTxPackets = baseInfo.wifiTxPackets;
            this.mobileBgRxBytes = baseInfo.mobileBgRxBytes;
            this.mobileBgTxBytes = baseInfo.mobileBgTxBytes;
            this.wifiBgRxBytes = baseInfo.wifiBgRxBytes;
            this.wifiBgTxBytes = baseInfo.wifiBgTxBytes;
            this.mobileBgRxPackets = baseInfo.mobileBgRxPackets;
            this.mobileBgTxPackets = baseInfo.mobileBgTxPackets;
            this.wifiBgRxPackets = baseInfo.wifiBgRxPackets;
            this.wifiBgTxPackets = baseInfo.wifiBgTxPackets;
            this.screenOffBytes = baseInfo.screenOffBytes;
            this.backgroundBytes = baseInfo.backgroundBytes;
            this.fgActivityTimeMs = baseInfo.fgActivityTimeMs;
            this.fgActivityTimes = baseInfo.fgActivityTimes;
            this.screenOffAudioOnTimeMs = baseInfo.screenOffAudioOnTimeMs;
            this.screenOffAudioOnTimes = baseInfo.screenOffAudioOnTimes;
            this.backgroundAudioOnTimeMs = baseInfo.backgroundAudioOnTimeMs;
            this.backgroundAudioOnTimes = baseInfo.backgroundAudioOnTimes;
        }
    }
}
