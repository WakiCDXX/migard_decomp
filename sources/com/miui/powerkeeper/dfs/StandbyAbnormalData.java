package com.miui.powerkeeper.dfs;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.util.Log;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class StandbyAbnormalData {
    public static final int IGNORED_WL_TYPE_AUDIO = 0;
    public static final int IGNORED_WL_TYPE_MUSIC = 2;
    public static final int IGNORED_WL_TYPE_PHONE = 1;
    public static final int IGNORED_WL_TYPE_START = 0;
    public static final int LOG_TYPE_BT_LOG = 256;
    public static final int LOG_TYPE_OFFLINE_LOGCAT_AUDIO = 16;
    public static final int LOG_TYPE_OFFLINE_LOGCAT_CHARGER = 128;
    public static final int LOG_TYPE_OFFLINE_LOGCAT_GPS = 32;
    public static final int LOG_TYPE_OFFLINE_LOGCAT_MODEM = 8;
    public static final int LOG_TYPE_OFFLINE_LOGCAT_SENSOR = 64;
    public static final int LOG_TYPE_OFFLINE_LOG_KERNEL = 1;
    public static final int LOG_TYPE_OFFLINE_LOG_LOGCAT = 2;
    public static final int LOG_TYPE_QSEE_LOG = 1024;
    public static final int LOG_TYPE_STABILITY = 4;
    public static final int LOG_TYPE_TOTAL = 12;
    public static final int LOG_TYPE_UART = 2048;
    public static final int LOG_TYPE_WIFI_LOG = 512;
    public static final String TAG = "DFS-AbnormalData";
    private int mAodTime;
    private int mAvgCurrent;
    public long mSamplingTimeMs;
    private ArrayList<DfsUtils.TimerEntry> mWakelocks;
    private ArrayList<DfsUtils.TimerEntry> mWakelocksKernel;
    private ArrayList<DfsUtils.TimerEntry> mWakeupReasons;
    public int mEventId = 0;
    private int mLogTypes = 0;
    public int mLogFlags = 0;
    private final HashMap<Integer, WakeupData> mWakeupMap = new HashMap<>();
    private final HashMap<Integer, WakelockData> mWakelockMap = new HashMap<>();
    private int mDeviceIdleDeepRadio = -1;
    private int mDeviceIdleRadio = -1;
    private SignalStrengthData phoneSignalStrengthData = null;
    private SignalStrengthData wifiSignalStrengthData = null;
    public int scene = 1;

    public static class SignalStrengthData {
        long activeTimeMs;
        long bytes;
        long packages;
        long poorTimeS;
        long totalTimeS;
    }

    public static class SubsystemData {
        long durationS;
        String name;
        int ratio;
    }

    public static class WakelockData {
        String wakelockPkgs;
        ArrayList<DfsUtils.TimerEntry> wakelockSource = new ArrayList<>();
    }

    public static class WakeupData {
        ArrayList<DfsUtils.TimerEntry> wakeupSource = new ArrayList<>();
        String wakeupType;
    }

    private void addStringToMaps(HashMap<Integer, WakeupData> map, int i2, DfsUtils.TimerEntry timerEntry) {
        synchronized (this) {
            try {
                WakeupData wakeupData = map.get(Integer.valueOf(i2));
                if (wakeupData != null) {
                    wakeupData.wakeupSource.add(timerEntry);
                } else {
                    WakeupData wakeupData2 = new WakeupData();
                    wakeupData2.wakeupSource.add(timerEntry);
                    wakeupData2.wakeupType = DfsUtils.eventIdToString(i2);
                    map.put(Integer.valueOf(i2), wakeupData2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static int getWakelockIgnoredType(String str) {
        if (str.contains("AudioMix")) {
            return 0;
        }
        if (str.contains("Music") || str.contains("music")) {
            return 2;
        }
        return str.contains("ImsPhoneConnection") ? 1 : -1;
    }

    public static int getWakeupType(String str) {
        if (str.contains("WLAN") || str.contains("wlan") || str.contains("qcom_rx_wakelock")) {
            return DfsUtils.EVENT_ID_WAKEUP_WIFI;
        }
        if (str.contains("bluetooth") || str.contains("bt_drv_io") || str.contains("hs_uart_wakeup")) {
            return DfsUtils.EVENT_ID_WAKEUP_BT;
        }
        if (str.contains("smp2p")) {
            return DfsUtils.EVENT_ID_WAKEUP_SENSOR;
        }
        if (str.contains("IPA_")) {
            return DfsUtils.EVENT_ID_WAKEUP_MODEM;
        }
        if (str.contains("fts") || str.contains("xiaomi_tp")) {
            return DfsUtils.EVENT_ID_WAKEUP_TP;
        }
        return str.contains("AON") ? DfsUtils.EVENT_ID_WAKEUP_CAMERA : DfsUtils.EVENT_ID_WAKEUP_POWER;
    }

    public void addLogTypes(int i2) {
        this.mLogTypes = i2 | this.mLogTypes;
    }

    public int getAod() {
        return this.mAodTime;
    }

    public int getAvgCurrent() {
        return this.mAvgCurrent;
    }

    public int getLogFlags() {
        return this.mLogFlags;
    }

    @Nullable
    public String[] getLogTypes() {
        if (this.mLogTypes == 0) {
            Log.d(TAG, "No logs are enabled.");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if ((this.mLogTypes & 1) != 0) {
            sb.append("offline_kernel ");
        }
        if ((this.mLogTypes & 2) != 0) {
            sb.append("offline_logcat ");
        }
        if ((this.mLogTypes & 4) != 0) {
            sb.append("stability_log ");
        }
        if ((this.mLogTypes & 8) != 0) {
            sb.append("offline_modem ");
        }
        if ((this.mLogTypes & 16) != 0) {
            sb.append("offline_audio ");
        }
        if ((this.mLogTypes & 32) != 0) {
            sb.append("offline_gps ");
        }
        if ((this.mLogTypes & 64) != 0) {
            sb.append("offline_sensor ");
        }
        if ((this.mLogTypes & 128) != 0) {
            sb.append("offline_charger ");
        }
        if ((this.mLogTypes & 512) != 0) {
            sb.append("wifi_log ");
        }
        if ((this.mLogTypes & 256) != 0) {
            sb.append("bt_log ");
        }
        if ((this.mLogTypes & 1024) != 0) {
            sb.append("qsee_log ");
        }
        if ((this.mLogTypes & 2048) != 0) {
            sb.append("uart_log ");
        }
        return sb.toString().trim().split(" ");
    }

    public SignalStrengthData getSignalStrengthData(boolean z2) {
        return z2 ? this.wifiSignalStrengthData : this.phoneSignalStrengthData;
    }

    public HashMap<Integer, WakelockData> getWakelocks() {
        return this.mWakelockMap;
    }

    public HashMap<Integer, WakeupData> getWakeupReasons() {
        return this.mWakeupMap;
    }

    public int getmDeviceIdleDeepRadio() {
        return this.mDeviceIdleDeepRadio;
    }

    public int getmDeviceIdleRadio() {
        return this.mDeviceIdleRadio;
    }

    public void setAodTime(int i2) {
        this.mAodTime = i2;
        this.mLogFlags = DfsUtils.addLogBit(18, this.mLogFlags);
    }

    public void setAvgCurrent(int i2) {
        this.mAvgCurrent = i2;
    }

    public void setKernelWakelocks(ArrayList<DfsUtils.TimerEntry> arrayList) {
        if (DfsUtils.DEBUG) {
            ArrayList<DfsUtils.TimerEntry> arrayList2 = this.mWakelocksKernel;
            if (arrayList2 == null) {
                this.mWakelocksKernel = arrayList;
            } else {
                arrayList2.addAll(arrayList);
            }
        }
        DfsUtils.TimerEntry timerEntry = null;
        DfsUtils.TimerEntry timerEntry2 = null;
        for (DfsUtils.TimerEntry timerEntry3 : arrayList) {
            if (!timerEntry3.name.contains("PowerManagerService.WakeLocks")) {
                timerEntry = timerEntry3;
                break;
            }
            Log.d(TAG, "Special kernel wakelocks. name=" + timerEntry3.name);
            timerEntry2 = timerEntry3;
        }
        if (timerEntry != null) {
            timerEntry2 = timerEntry;
        }
        if (timerEntry2 != null) {
            WakelockData wakelockData = new WakelockData();
            wakelockData.wakelockSource.add(timerEntry2);
            wakelockData.wakelockPkgs = LogType.LOG_TYPE_KERNEL_NAME;
            this.mWakelockMap.put(Integer.valueOf(DfsUtils.EVENT_ID_KERNEL_WAKELOCK), wakelockData);
            this.mLogFlags = DfsUtils.addLogBit(7, this.mLogFlags);
        }
    }

    public void setPartialWakelocks(ArrayList<DfsUtils.TimerEntry> arrayList) {
        if (DfsUtils.DEBUG) {
            ArrayList<DfsUtils.TimerEntry> arrayList2 = this.mWakelocks;
            if (arrayList2 == null) {
                this.mWakelocks = arrayList;
            } else {
                arrayList2.addAll(arrayList);
            }
        }
        for (DfsUtils.TimerEntry timerEntry : arrayList) {
            if (getWakelockIgnoredType(timerEntry.name) > 0) {
                Log.d(TAG, "Ignore the partial wakelocks. name=" + timerEntry.name);
                return;
            }
            if (getWakelockIgnoredType(timerEntry.name) == 0 && timerEntry.timeUs / 900 < this.mSamplingTimeMs) {
                return;
            }
            WakelockData wakelockData = this.mWakelockMap.get(Integer.valueOf(DfsUtils.EVENT_ID_PARTIAL_WAKELOCK));
            if (wakelockData == null) {
                WakelockData wakelockData2 = new WakelockData();
                wakelockData2.wakelockSource.add(timerEntry);
                wakelockData2.wakelockPkgs = DfsUtils.getPackageForUid(timerEntry.id);
                this.mWakelockMap.put(Integer.valueOf(DfsUtils.EVENT_ID_PARTIAL_WAKELOCK), wakelockData2);
                int iAddLogBit = DfsUtils.addLogBit(7, this.mLogFlags);
                this.mLogFlags = iAddLogBit;
                this.mLogFlags = DfsUtils.addLogBit(1, iAddLogBit);
                if (timerEntry.name.toLowerCase(Locale.getDefault()).contains("job")) {
                    this.mLogFlags = DfsUtils.addLogBit(21, this.mLogFlags);
                    return;
                }
                return;
            }
            wakelockData.wakelockSource.add(timerEntry);
            wakelockData.wakelockPkgs += "," + DfsUtils.getPackageForUid(timerEntry.id);
        }
    }

    public void setSignalStrengthData(SignalStrengthData signalStrengthData, boolean z2) {
        if (signalStrengthData == null) {
            return;
        }
        if (z2) {
            int iAddLogBit = DfsUtils.addLogBit(2, this.mLogFlags);
            this.mLogFlags = iAddLogBit;
            this.mLogFlags = DfsUtils.addLogBit(12, iAddLogBit);
            this.wifiSignalStrengthData = signalStrengthData;
            return;
        }
        int iAddLogBit2 = DfsUtils.addLogBit(5, this.mLogFlags);
        this.mLogFlags = iAddLogBit2;
        this.mLogFlags = DfsUtils.addLogBit(13, iAddLogBit2);
        this.phoneSignalStrengthData = signalStrengthData;
    }

    public void setWakeupReasons(ArrayList<DfsUtils.TimerEntry> arrayList) {
        String str;
        if (DfsUtils.DEBUG) {
            this.mWakeupReasons = arrayList;
        }
        for (DfsUtils.TimerEntry timerEntry : arrayList) {
            int wakeupType = getWakeupType(timerEntry.name);
            if (wakeupType != 903111399) {
                if (!DfsUtils.DEBUG || (str = DfsUtils.debugWakeup) == null || wakeupType == getWakeupType(str)) {
                    switch (wakeupType) {
                        case DfsUtils.EVENT_ID_WAKEUP_BT /* 903111321 */:
                            addStringToMaps(this.mWakeupMap, DfsUtils.EVENT_ID_WAKEUP_BT, timerEntry);
                            this.mLogFlags = DfsUtils.addLogBit(3, this.mLogFlags);
                            break;
                        case DfsUtils.EVENT_ID_WAKEUP_TP /* 903111322 */:
                            addStringToMaps(this.mWakeupMap, DfsUtils.EVENT_ID_WAKEUP_TP, timerEntry);
                            this.mLogFlags = DfsUtils.addLogBit(10, this.mLogFlags);
                            break;
                        case DfsUtils.EVENT_ID_WAKEUP_CAMERA /* 903111323 */:
                            addStringToMaps(this.mWakeupMap, DfsUtils.EVENT_ID_WAKEUP_CAMERA, timerEntry);
                            this.mLogFlags = DfsUtils.addLogBit(11, this.mLogFlags);
                            break;
                        case DfsUtils.EVENT_ID_WAKEUP_WIFI /* 903111325 */:
                            addStringToMaps(this.mWakeupMap, DfsUtils.EVENT_ID_WAKEUP_WIFI, timerEntry);
                            int iAddLogBit = DfsUtils.addLogBit(2, this.mLogFlags);
                            this.mLogFlags = iAddLogBit;
                            this.mLogFlags = DfsUtils.addLogBit(12, iAddLogBit);
                            break;
                        case DfsUtils.EVENT_ID_WAKEUP_MODEM /* 903111328 */:
                            addStringToMaps(this.mWakeupMap, DfsUtils.EVENT_ID_WAKEUP_MODEM, timerEntry);
                            int iAddLogBit2 = DfsUtils.addLogBit(5, this.mLogFlags);
                            this.mLogFlags = iAddLogBit2;
                            this.mLogFlags = DfsUtils.addLogBit(13, iAddLogBit2);
                            break;
                        case DfsUtils.EVENT_ID_WAKEUP_SENSOR /* 903111330 */:
                            addStringToMaps(this.mWakeupMap, DfsUtils.EVENT_ID_WAKEUP_SENSOR, timerEntry);
                            this.mLogFlags = DfsUtils.addLogBit(4, this.mLogFlags);
                            break;
                    }
                }
                Log.d(TAG, "Debugging for " + DfsUtils.debugWakeup + ", So ignore " + timerEntry.name);
            }
        }
        addStringToMaps(this.mWakeupMap, DfsUtils.EVENT_ID_WAKEUP_POWER, arrayList.get(0));
        int iAddLogBit3 = DfsUtils.addLogBit(1, this.mLogFlags);
        this.mLogFlags = iAddLogBit3;
        this.mLogFlags = DfsUtils.addLogBit(20, iAddLogBit3);
    }

    public void setmDeviceIdleDeepRadio(int i2) {
        this.mDeviceIdleDeepRadio = i2;
        int iAddLogBit = DfsUtils.addLogBit(16, this.mLogFlags);
        this.mLogFlags = iAddLogBit;
        this.mLogFlags = DfsUtils.addLogBit(14, iAddLogBit);
    }

    public void setmDeviceIdleRadio(int i2) {
        this.mDeviceIdleRadio = i2;
        int iAddLogBit = DfsUtils.addLogBit(16, this.mLogFlags);
        this.mLogFlags = iAddLogBit;
        this.mLogFlags = DfsUtils.addLogBit(14, iAddLogBit);
    }

    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("mLogFlags = 0x%x", Integer.valueOf(this.mLogFlags)));
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append("SamplingDurationMs:");
        sb.append(this.mSamplingTimeMs);
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append("avgCurrent=");
        sb.append(getAvgCurrent());
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append("aod enabled timeS=");
        sb.append(this.mAodTime);
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append("Idle_ratio_deep:");
        sb.append(this.mDeviceIdleDeepRadio);
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append("Idle_ratio_all:");
        sb.append(this.mDeviceIdleRadio);
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append(String.format("mLogTypes = 0x%x", Integer.valueOf(this.mLogTypes)));
        if (this.mWakeupReasons != null) {
            for (int i2 = 0; i2 < this.mWakeupReasons.size(); i2++) {
                sb.append(ShellUtils.COMMAND_LINE_END);
                sb.append("Wakeup Reason: ");
                sb.append(this.mWakeupReasons.get(i2).name);
                sb.append(" time:");
                sb.append(this.mWakeupReasons.get(i2).timeUs);
                sb.append(" (");
                sb.append(this.mWakeupReasons.get(i2).count);
                sb.append(" times)");
            }
        } else {
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("Wakeup reasons is empty.");
        }
        if (this.mWakelocks != null) {
            for (int i3 = 0; i3 < this.mWakelocks.size(); i3++) {
                sb.append(ShellUtils.COMMAND_LINE_END);
                sb.append("Partial Wakelocks: uid=");
                sb.append(this.mWakelocks.get(i3).id);
                sb.append(Constants.SEPARATOR_LEFT_PARENTESIS);
                sb.append(DfsUtils.getPackageForUid(this.mWakelocks.get(i3).id));
                sb.append(") ");
                sb.append(this.mWakelocks.get(i3).name);
                sb.append(" time:");
                sb.append(this.mWakelocks.get(i3).timeUs);
                sb.append(" (");
                sb.append(this.mWakelocks.get(i3).count);
                sb.append(" times)");
            }
        } else {
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("Partial Wakelocks is empty.");
        }
        if (this.mWakelocksKernel != null) {
            for (int i4 = 0; i4 < this.mWakelocksKernel.size(); i4++) {
                sb.append(ShellUtils.COMMAND_LINE_END);
                sb.append("Kernel Wakelocks: uid=");
                sb.append(this.mWakelocksKernel.get(i4).id);
                sb.append(Constants.SEPARATOR_LEFT_PARENTESIS);
                sb.append(DfsUtils.getPackageForUid(this.mWakelocksKernel.get(i4).id));
                sb.append(") ");
                sb.append(this.mWakelocksKernel.get(i4).name);
                sb.append(" time:");
                sb.append(this.mWakelocksKernel.get(i4).timeUs);
                sb.append(" (");
                sb.append(this.mWakelocksKernel.get(i4).count);
                sb.append(" times)");
            }
        } else {
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("Kernel Wakelocks is empty.");
        }
        if (this.mLogTypes != 0) {
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("Log enabled types: ");
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("  ");
            sb.append(Arrays.toString(getLogTypes()));
        }
        if (this.phoneSignalStrengthData != null) {
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("phoneSignalStrengthData data: total=");
            sb.append(this.phoneSignalStrengthData.totalTimeS);
            sb.append("s, poor=");
            sb.append(this.phoneSignalStrengthData.poorTimeS);
            sb.append(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
        }
        if (this.wifiSignalStrengthData != null) {
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append("wifiSignalStrengthData data: total=");
            sb.append(this.wifiSignalStrengthData.totalTimeS);
            sb.append("s, poor=");
            sb.append(this.wifiSignalStrengthData.poorTimeS);
            sb.append(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
        }
        return sb.toString();
    }
}
