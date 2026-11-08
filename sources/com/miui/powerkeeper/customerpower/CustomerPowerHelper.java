package com.miui.powerkeeper.customerpower;

import android.content.Context;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.miui.powerkeeper.powerchecker.UidStatsData;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CustomerPowerHelper {
    private static final String TAG = CustomerPowerConstant.TAG + CustomerPowerHelper.class.getSimpleName();
    private static final int WAKE_TYPE_PARTIAL = 0;

    /* JADX WARN: Code duplicated, block: B:53:0x0177  */
    public static ArrayList<CPAppStatusRecord> getAppStatusRecords(Context context, long j2, int i2) {
        ArrayList<CPAppStatusRecord> arrayList = new ArrayList<>();
        SparseArray<UidStatsData> uidStats = BatteryStatsHelper.getUidStats();
        if (uidStats.size() > 0) {
            for (int i3 = 0; i3 < uidStats.size(); i3++) {
                int iKeyAt = uidStats.keyAt(i3);
                UidStatsData uidStatsDataValueAt = uidStats.valueAt(i3);
                if (CustomerPowerConstant.DEBUG) {
                    Log.d(TAG, "getAppStatusRecords uid = " + iKeyAt);
                }
                List<UidStatsData.WakeLock> wakeLockList = uidStatsDataValueAt.getWakeLockList();
                long j3 = 0;
                for (int size = wakeLockList.size() - 1; size >= 0; size--) {
                    UidStatsData.WakeLock wakeLock = wakeLockList.get(size);
                    String str = wakeLock.wlName;
                    long j4 = wakeLock.wlPartialTime;
                    if (j4 > 0 && (!"AudioMix".equals(str) || !"ProximitySensor".equals(str))) {
                        j3 += j4;
                        if (CustomerPowerConstant.DEBUG) {
                            Log.d(TAG, "getAppStatusRecords wakelockName = " + str + ", totalTimeMicros = " + j4);
                        }
                    }
                }
                long userCpuTimeUs = uidStatsDataValueAt.getUserCpuTimeUs();
                long systemCpuTimeUs = uidStatsDataValueAt.getSystemCpuTimeUs();
                long j5 = uidStatsDataValueAt.getFgActivityTimer().totalTime;
                if (iKeyAt == 1000 || iKeyAt == 0) {
                    List<UidStatsData.Proc> procList = uidStatsDataValueAt.getProcList();
                    int size2 = procList.size() - 1;
                    while (size2 >= 0) {
                        UidStatsData.Proc proc = procList.get(size2);
                        String str2 = proc.procName;
                        long j6 = proc.procUserTime;
                        List<UidStatsData.Proc> list = procList;
                        int i4 = size2;
                        long j7 = proc.procSystemTime;
                        long j8 = systemCpuTimeUs;
                        long j9 = proc.procFgTime;
                        if (CustomerPowerConstant.DEBUG) {
                            Log.d(TAG, "getAppStatusRecords procName = " + str2 + " procUserTime = " + j6 + ", procSystemTime = " + j7 + ", foregroundTime = " + j9);
                        }
                        if ((j3 >= 0 || userCpuTimeUs > 0 || j8 > 0 || j5 >= 0) && str2 != null) {
                            long j10 = j7 + j6;
                            if (j10 - j9 > 1800000) {
                                arrayList.add(new CPAppStatusRecord(iKeyAt, null, str2, j10, null, j3, j9, null, 0, 0L, 0L));
                            }
                        }
                        size2 = i4 - 1;
                        systemCpuTimeUs = j8;
                        procList = list;
                        userCpuTimeUs = userCpuTimeUs;
                    }
                } else {
                    String packageNameByUid = CustomerPowerUtil.getPackageNameByUid(context, iKeyAt);
                    if (CustomerPowerConstant.DEBUG) {
                        Log.d(TAG, "getAppStatusRecords pkgName = " + packageNameByUid + " userCpuTimeUs = " + userCpuTimeUs + ", systemCpuTimeUs = " + systemCpuTimeUs + ", foregroundActivityTime = " + j5);
                    }
                    if (packageNameByUid != null && (j3 > 0 || userCpuTimeUs > 0 || systemCpuTimeUs > 0 || j5 > 0)) {
                        arrayList.add(new CPAppStatusRecord(iKeyAt, packageNameByUid, null, userCpuTimeUs + systemCpuTimeUs, null, j3, j5, null, 0, 0L, 0L));
                    }
                }
            }
        }
        return arrayList;
    }

    public static long getBadSignalTime(IBatteryStats iBatteryStats, long j2, int i2) {
        long j3 = 0;
        for (int i3 = 0; i3 < 5; i3++) {
            try {
                long phoneSignalStrengthTime = (iBatteryStats.getPhoneSignalStrengthTime(i3, j2, i2) + 500) / 1000;
                j3 += phoneSignalStrengthTime;
                if (CustomerPowerConstant.DEBUG) {
                    Log.d(TAG, "badSignalTime numSingnal = 5, ns = " + i3 + ", badSignalTime = " + phoneSignalStrengthTime + ", totalBadSignalTime = " + j3);
                }
                if (i3 == 2) {
                    return j3;
                }
            } catch (RemoteException e2) {
                Log.e(TAG, e2.getMessage());
            }
        }
        return j3;
    }

    public static long getHighBrightnessTime(IBatteryStats iBatteryStats, long j2, int i2) {
        long j3 = 0;
        for (int i3 = 99; i3 >= 0; i3--) {
            try {
                long screenBrightnessTime = (iBatteryStats.getScreenBrightnessTime(i3, j2, i2) + 500) / 1000;
                j3 += screenBrightnessTime;
                if (CustomerPowerConstant.DEBUG) {
                    Log.d(TAG, "highBrightnessTime numberBright = 100, i = " + i3 + ", screenBrightnessTime = " + screenBrightnessTime + ", brightnessHigtTime = " + j3);
                }
                if (i3 == 97) {
                    return j3;
                }
            } catch (RemoteException e2) {
                Log.e(TAG, e2.getMessage());
            }
        }
        return j3;
    }

    public static ArrayList<CustomerPowerUtil.TimerEntry> getKernelWakelockStats(IBatteryStats iBatteryStats, long j2, int i2) {
        ArrayList<CustomerPowerUtil.TimerEntry> arrayList = new ArrayList<>();
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                byte[] kernelWakelockStats = iBatteryStats.getKernelWakelockStats();
                if (kernelWakelockStats != null) {
                    parcelObtain.unmarshall(kernelWakelockStats, 0, kernelWakelockStats.length);
                    parcelObtain.setDataPosition(0);
                    int i3 = parcelObtain.readInt();
                    for (int i4 = 0; i4 < i3; i4++) {
                        String string = parcelObtain.readString();
                        int i5 = parcelObtain.readInt();
                        long j3 = parcelObtain.readLong();
                        if (string != null && string.length() > 0 && j3 > 0) {
                            if (CustomerPowerConstant.DEBUG) {
                                Log.d(TAG, "kernel wakelock wakelockName = " + string + ", length = " + string.length() + ", wakelockCount = " + i5 + ", wakelockTime = " + j3);
                            }
                            arrayList.add(new CustomerPowerUtil.TimerEntry(-1, string, j3, i5));
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
            return arrayList;
        } finally {
            parcelObtain.recycle();
        }
    }

    public static ArrayList<CustomerPowerUtil.TimerEntry> getKernelWakeups(IBatteryStats iBatteryStats, long j2, int i2) {
        ArrayList<CustomerPowerUtil.TimerEntry> arrayList = new ArrayList<>();
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                byte[] wakeupReasonStats = iBatteryStats.getWakeupReasonStats();
                if (wakeupReasonStats != null) {
                    parcelObtain.unmarshall(wakeupReasonStats, 0, wakeupReasonStats.length);
                    parcelObtain.setDataPosition(0);
                    int i3 = parcelObtain.readInt();
                    for (int i4 = 0; i4 < i3; i4++) {
                        String string = parcelObtain.readString();
                        int i5 = parcelObtain.readInt();
                        long j3 = parcelObtain.readLong();
                        if (string != null && string.length() > 0 && j3 > 0) {
                            if (CustomerPowerConstant.DEBUG) {
                                Log.d(TAG, "kernel wakeup wakeupName = " + string + ", length = " + string.length() + ", wakeupCount = " + i5 + ", wakeupTime = " + j3);
                            }
                            arrayList.add(new CustomerPowerUtil.TimerEntry(-1, string, j3, i5));
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
            return arrayList;
        } finally {
            parcelObtain.recycle();
        }
    }

    public static long getPhoneSignalStrengthTime(IBatteryStats iBatteryStats, long j2, int i2, int i3) {
        try {
            return (iBatteryStats.getPhoneSignalStrengthTime(i3, j2, i2) + 500) / 1000;
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0L;
        }
    }

    public static long getScreenBrightnessTime(IBatteryStats iBatteryStats, long j2, int i2, int i3) {
        try {
            return (iBatteryStats.getScreenBrightnessTime(i3, j2, i2) + 500) / 1000;
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0L;
        }
    }

    public static int getScreenOffDischargedLevel(IBatteryStats iBatteryStats) {
        try {
            return iBatteryStats.getDischargeAmountScreenOffSinceCharge();
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0;
        }
    }

    public static long getScreenOffTime(IBatteryStats iBatteryStats, long j2, int i2) {
        try {
            return (iBatteryStats.computeBatteryScreenOffRealtime(j2, i2) + 500) / 1000;
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0L;
        }
    }

    public static int getScreenOnDischargedLevel(IBatteryStats iBatteryStats) {
        try {
            return iBatteryStats.getDischargeAmountScreenOnSinceCharge();
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0;
        }
    }

    public static long getScreenOnTime(IBatteryStats iBatteryStats, long j2, int i2) {
        try {
            return (iBatteryStats.getScreenOnTime(j2, i2) + 500) / 1000;
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0L;
        }
    }

    public static long getStartClockTime(IBatteryStats iBatteryStats) {
        try {
            return iBatteryStats.getStartClockTime();
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0L;
        }
    }

    public static long getWhichBatteryRealtime(IBatteryStats iBatteryStats, long j2, int i2) {
        try {
            return (iBatteryStats.computeBatteryRealtime(j2, i2) + 500) / 1000;
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0L;
        }
    }
}
