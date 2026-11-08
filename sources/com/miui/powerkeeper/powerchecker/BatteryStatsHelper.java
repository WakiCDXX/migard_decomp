package com.miui.powerkeeper.powerchecker;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.logsystem.config.LogType;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BatteryStatsHelper {
    public static final int NETWORK_MOBILE_BG_RX_DATA = 6;
    public static final int NETWORK_MOBILE_BG_TX_DATA = 7;
    public static final int NETWORK_MOBILE_RX_DATA = 0;
    public static final int NETWORK_MOBILE_TX_DATA = 1;
    public static final int NETWORK_WIFI_BG_RX_DATA = 8;
    public static final int NETWORK_WIFI_BG_TX_DATA = 9;
    public static final int NETWORK_WIFI_RX_DATA = 2;
    public static final int NETWORK_WIFI_TX_DATA = 3;
    private static final String TAG = "PowerChecker.Battery";
    private static final int WHICH = PowerCheckerService.WHICH;
    private static BatteryUsageStats sBatteryUsageStats;
    private static IBatteryStats sStats;

    private class BatteryHistoryItemV23 {
        public static final byte CMD_CURRENT_TIME = 5;
        public static final byte CMD_NULL = -1;
        public static final byte CMD_OVERFLOW = 6;
        public static final byte CMD_RESET = 7;
        public static final byte CMD_SHUTDOWN = 8;
        public static final byte CMD_START = 4;
        public static final byte CMD_UPDATE = 0;
        public static final int STATE2_BLUETOOTH_ON_FLAG = 8388608;
        public static final int STATE2_PHONE_IN_CALL_FLAG = 16777216;
        public static final int STATE2_WIFI_ON_FLAG = 268435456;

        private BatteryHistoryItemV23() {
        }
    }

    private BatteryStatsHelper() {
    }

    public static IBatteryStats getBatteryStats() {
        if (sStats == null) {
            sStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
        }
        return sStats;
    }

    public static BatteryUsageStats getBatteryUsageStats() {
        if (sBatteryUsageStats == null) {
            try {
                sBatteryUsageStats = (BatteryUsageStats) getBatteryStats().getBatteryUsageStats(List.of(new BatteryUsageStatsQuery.Builder().includeBatteryHistory().build())).get(0);
            } catch (RemoteException e2) {
                Log.e(TAG, e2.getMessage());
            }
        }
        return sBatteryUsageStats;
    }

    public static boolean getBluetoothState(BatteryStats.HistoryItem historyItem) {
        return (historyItem.states2 & 8388608) != 0;
    }

    public static double getComputedPower() {
        BatteryUsageStats batteryUsageStats = getBatteryUsageStats();
        if (batteryUsageStats == null) {
            return 0.0d;
        }
        return batteryUsageStats.getConsumedPower();
    }

    public static boolean getGpsState(BatteryStats.HistoryItem historyItem) {
        return (historyItem.states & 536870912) != 0;
    }

    public static double getMaxDrainedPower() {
        BatteryUsageStats batteryUsageStats = getBatteryUsageStats();
        if (batteryUsageStats == null) {
            return 0.0d;
        }
        return ((Double) batteryUsageStats.getDischargedPowerRange().getUpper()).doubleValue();
    }

    public static double getMinDrainedPower() {
        BatteryUsageStats batteryUsageStats = getBatteryUsageStats();
        if (batteryUsageStats == null) {
            return 0.0d;
        }
        return ((Double) batteryUsageStats.getDischargedPowerRange().getLower()).doubleValue();
    }

    public static int getMobileDataState(BatteryStats.HistoryItem historyItem) {
        return (historyItem.states & 15872) >> 9;
    }

    public static int getMobileState(BatteryStats.HistoryItem historyItem) {
        return (historyItem.states & 448) >> 6;
    }

    public static boolean getPhoneInCallState(BatteryStats.HistoryItem historyItem) {
        return (historyItem.states2 & 16777216) != 0;
    }

    public static boolean getPlugState(BatteryStats.HistoryItem historyItem) {
        return (historyItem.states & LogType.LOG_TYPE_SAMPLING_CLOCK) != 0;
    }

    public static boolean getScreenState(BatteryStats.HistoryItem historyItem) {
        return (historyItem.states & LogType.LOG_TYPE_SAMPLING_SIGNAL_STRENGTH) != 0;
    }

    public static SparseArray<UidStatsData> getUidStats() {
        SparseArray<UidStatsData> sparseArray = new SparseArray<>();
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                byte[] uidStats = getBatteryStats().getUidStats();
                if (uidStats != null) {
                    parcelObtain.unmarshall(uidStats, 0, uidStats.length);
                    parcelObtain.setDataPosition(0);
                    int i2 = parcelObtain.readInt();
                    for (int i3 = 0; i3 < i2; i3++) {
                        int i4 = parcelObtain.readInt();
                        sparseArray.put(i4, readFromParcelLocked(i4, parcelObtain));
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
            return sparseArray;
        } finally {
            parcelObtain.recycle();
        }
    }

    public static boolean getWifiState(BatteryStats.HistoryItem historyItem) {
        return (historyItem.states2 & BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG) != 0;
    }

    public static boolean isHistoryItemResetOnly(BatteryStats.HistoryItem historyItem) {
        return historyItem.cmd == 7;
    }

    public static boolean isHistoryItemShutdown(BatteryStats.HistoryItem historyItem) {
        return historyItem.cmd == 8;
    }

    public static boolean isHistoryItemStart(BatteryStats.HistoryItem historyItem) {
        byte b2 = historyItem.cmd;
        return b2 == 4 || b2 == 5 || b2 == 7;
    }

    public static boolean isHistoryItemStartOnly(BatteryStats.HistoryItem historyItem) {
        return historyItem.cmd == 4;
    }

    public static boolean isHistoryItemTimeOnly(BatteryStats.HistoryItem historyItem) {
        return historyItem.cmd == 5;
    }

    private static UidStatsData readFromParcelLocked(int i2, Parcel parcel) {
        long[] jArr;
        long[] jArr2;
        long[] jArr3;
        int i3;
        long[] jArr4 = new long[0];
        UidStatsData.Timer timer = new UidStatsData.Timer();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        SparseArray<UidStatsData.Timer> sparseArray = new SparseArray<>();
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
        UidStatsData.Timer timer2 = new UidStatsData.Timer();
        long[] jArr5 = new long[0];
        long[] jArr6 = new long[0];
        long j2 = parcel.readInt() != 0 ? parcel.readLong() : 0L;
        long j3 = parcel.readInt() != 0 ? parcel.readLong() : 0L;
        if (parcel.readInt() == 0 || (i3 = parcel.readInt()) == 0) {
            jArr = jArr4;
        } else {
            jArr = new long[i3];
            parcel.readLongArray(jArr);
        }
        if (parcel.readInt() != 0) {
            timer.totalTime = parcel.readLong();
            timer.count = parcel.readInt();
        }
        int i4 = 0;
        for (int i5 = parcel.readInt(); i4 < i5; i5 = i5) {
            arrayList.add(new UidStatsData.Proc(parcel.readString(), parcel.readLong(), parcel.readLong(), parcel.readLong()));
            i4++;
        }
        int i6 = 0;
        for (int i7 = parcel.readInt(); i6 < i7; i7 = i7) {
            arrayList2.add(new UidStatsData.WakeLock(parcel.readString(), parcel.readLong(), parcel.readInt(), parcel.readLong(), parcel.readInt(), parcel.readLong(), parcel.readInt()));
            i6++;
        }
        int i8 = parcel.readInt();
        int i9 = 0;
        while (i9 < i8) {
            arrayList3.add(new UidStatsData.Timer(parcel.readString(), parcel.readLong(), parcel.readInt()));
            i9++;
            i8 = i8;
            arrayList = arrayList;
            arrayList2 = arrayList2;
            jArr5 = jArr5;
            timer = timer;
        }
        UidStatsData.Timer timer3 = timer;
        ArrayList arrayList5 = arrayList;
        ArrayList arrayList6 = arrayList2;
        long[] jArr7 = jArr5;
        int i10 = 0;
        for (int i11 = parcel.readInt(); i10 < i11; i11 = i11) {
            arrayList4.add(new UidStatsData.Timer(parcel.readString(), parcel.readLong(), parcel.readInt()));
            i10++;
        }
        int i12 = parcel.readInt();
        int i13 = 0;
        while (i13 < i12) {
            sparseArray.put(parcel.readInt(), new UidStatsData.Timer("", parcel.readLong(), parcel.readInt()));
            i13++;
            i12 = i12;
            arrayList4 = arrayList4;
        }
        ArrayList arrayList7 = arrayList4;
        long j4 = parcel.readInt() != 0 ? parcel.readLong() : 0L;
        long j5 = parcel.readInt() != 0 ? parcel.readLong() : 0L;
        int i14 = 0;
        for (int i15 = parcel.readInt(); i14 < i15; i15 = i15) {
            arrayMap.put(parcel.readString(), Integer.valueOf(parcel.readInt()));
            i14++;
        }
        long j6 = j5;
        if (parcel.readInt() != 0) {
            timer2.totalTime = parcel.readLong();
        }
        if (parcel.readInt() != 0) {
            timer2.count = (int) parcel.readLong();
        }
        int i16 = parcel.readInt();
        if (i16 != 0) {
            jArr3 = new long[i16];
            jArr2 = new long[i16];
            for (int i17 = 0; i17 < i16; i17++) {
                jArr3[i17] = parcel.readLong();
                jArr2[i17] = parcel.readLong();
            }
        } else {
            jArr2 = jArr6;
            jArr3 = jArr7;
        }
        return new UidStatsData.Builder(i2).userCpuTimeUs(j2).systemCpuTimeUs(j3).cpuFreqTimes(jArr).fgActivityTimer(timer3).procList(arrayList5).wakeTime(arrayList6).syncTimer(arrayList3).jobTimer(arrayList7).sensorTimer(sparseArray).fullWifiLockTime(j4).wifiScanTime(j6).pkgWakeUpAlarmCount(arrayMap).radioActiveTimer(timer2).networkActivityBytes(jArr3).networkActivityPackets(jArr2).build();
    }
}
