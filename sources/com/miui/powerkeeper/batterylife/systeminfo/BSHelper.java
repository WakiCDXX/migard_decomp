package com.miui.powerkeeper.batterylife.systeminfo;

import android.content.Context;
import android.os.BatteryManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.PowerProfile;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.dfs.DfsUtils;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;

/* JADX INFO: loaded from: classes.dex */
public class BSHelper {
    public static final int DEFAULT_CURRENT_CAPACITY = -1;
    public static final long DEFAULT_CURRENT_VOLTAGE = -1;
    private static final String TAG = "BL-BSHelper";
    private static double sCachedChargeCounter = -1.0d;
    private static long sCachedStartTime = 0;
    private static long sChargeCounterShift = -1;
    private static long sChargeFullShift = -1;
    private static double sDesignedBatteryCap = 0.0d;
    private static double sLastChargeCounter = -1.0d;
    private static long sLastReadingTime;

    private BSHelper() {
    }

    private static long getChargeCounterShift(Context context) {
        int intProperty;
        long longProperty;
        if (sChargeCounterShift > 0) {
            Log.d(TAG, "getChargeCounterShift sChargeCounterShift=" + sChargeCounterShift);
            return sChargeCounterShift;
        }
        BatteryManager batteryManager = (BatteryManager) PowerKeeperApplication.b().getSystemService("batterymanager");
        if (batteryManager != null) {
            longProperty = batteryManager.getLongProperty(1);
            intProperty = batteryManager.getIntProperty(4);
        } else {
            intProperty = 0;
            longProperty = 0;
        }
        double d2 = sLastChargeCounter;
        if (d2 < 0.0d || Math.abs(longProperty - d2) < 1.0d) {
            BLUtils.LOCAL_LOG.log("DEBUG CHARGE COUNTER: FAILED!! sLastChargeCounter=" + sLastChargeCounter + ",counter=" + longProperty);
            sLastChargeCounter = (double) longProperty;
            return -1L;
        }
        if (longProperty == 0 || longProperty == Long.MIN_VALUE) {
            String sysNodeInfo = BLUtils.readSysNodeInfo("/sys/class/power_supply/battery/capacity");
            String sysNodeInfo2 = BLUtils.readSysNodeInfo("/sys/class/power_supply/battery/charge_counter");
            if (sysNodeInfo != null && sysNodeInfo2 != null) {
                try {
                    intProperty = Integer.parseInt(sysNodeInfo);
                    longProperty = Long.parseLong(sysNodeInfo2);
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (intProperty == 0) {
            Log.d(TAG, "getChargeCounterShift error!");
            return -1L;
        }
        long j2 = (100 * longProperty) / ((long) intProperty);
        long j3 = 1;
        while (j2 > 2.0d * getDesignedBatteryCapacity(context)) {
            j2 /= 10;
            j3 *= 10;
        }
        BLUtils.LOCAL_LOG.log("DEBUG CHARGE COUNTER: charge counter=" + longProperty + ",Level=" + intProperty + ",shift=" + j3);
        return j3;
    }

    private static long getChargeFullShift(Context context, long j2) {
        double d2;
        if (sChargeFullShift > 0) {
            Log.d(TAG, "getChargeCounterShift sChargeCounterShift=" + sChargeFullShift);
            return sChargeFullShift;
        }
        double designedBatteryCapacity = getDesignedBatteryCapacity(context);
        long j3 = 1;
        while (true) {
            d2 = j2;
            if (d2 <= designedBatteryCapacity * 2.0d) {
                break;
            }
            j2 /= 10;
            j3 *= 10;
        }
        if (Math.abs(d2 - designedBatteryCapacity) <= designedBatteryCapacity / 2.0d) {
            BLUtils.LOCAL_LOG.log("DEBUG CHARGE FULL: charge full=" + j2 + ",shift=" + j3);
            return j3;
        }
        Log.d(TAG, "getChargeFullShift failed. The number is abnormal. charge full=" + j2);
        BLUtils.LOCAL_LOG.log("DEBUG CHARGE FULL: FAILED!! charge full=" + j2 + ",designed=" + getDesignedBatteryCapacity(context));
        return -1L;
    }

    public static double getCurrentBatteryCap() {
        return getCurrentBatteryCap(1000);
    }

    public static int getCurrentBatteryCapLevel() {
        String sysNodeInfo = BLUtils.readSysNodeInfo("/sys/class/power_supply/battery/capacity");
        if (sysNodeInfo == null || sysNodeInfo.length() <= 0) {
            return -2;
        }
        try {
            return Integer.parseInt(sysNodeInfo);
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return -2;
        }
    }

    public static long getCurrentBatteryVoltage() {
        String sysNodeInfo = BLUtils.readSysNodeInfo("/sys/class/power_supply/battery/voltage_now");
        if (sysNodeInfo == null || sysNodeInfo.length() <= 0) {
            return -1L;
        }
        try {
            return Long.parseLong(sysNodeInfo);
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return -1L;
        }
    }

    public static double getDesignedBatteryCapacity(Context context) {
        double d2 = sDesignedBatteryCap;
        if (d2 > 10.0d) {
            return d2;
        }
        sDesignedBatteryCap = new PowerProfile(context).getBatteryCapacity();
        BLUtils.LOCAL_LOG.log("DESIGNED CAPACITY FROM power_profile: sDesignedBatteryCap=" + sDesignedBatteryCap);
        return sDesignedBatteryCap;
    }

    public static String getDeviceId() {
        return BLUtils.readSysNodeInfoByWhetstone("/sys/class/mi_memory/mi_memory_device/ufshcd0/dump_string_desc_serial");
    }

    public static long getDozeTimeSec(Context context) {
        if (!BLUtils.isFlagSupport(16)) {
            return 0L;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        int userId = context.getUserId();
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        if (batteryStats == null) {
            return 0L;
        }
        try {
            return (batteryStats.getDeviceIdleModeTime(1, jElapsedRealtime, userId) + batteryStats.getDeviceIdleModeTime(2, jElapsedRealtime, userId)) / DfsUtils.SECOND_TO_MICROS;
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0L;
        }
    }

    public static double getTotalBatteryCapacity(Context context) {
        String sysNodeInfo = BLUtils.readSysNodeInfo("/sys/class/power_supply/battery/charge_full");
        if (sysNodeInfo == null || sysNodeInfo.length() <= 0) {
            BLUtils.LOCAL_LOG.log("DEBUG CHARGE FULL INIT: charge full is EMPTY.");
        } else {
            try {
                Log.d(TAG, "getTotalBatteryCapacity charge_full=" + sysNodeInfo + ",sChargeFullShift=" + sChargeFullShift);
                long j2 = Long.parseLong(sysNodeInfo);
                long chargeFullShift = sChargeFullShift;
                if (chargeFullShift <= 0) {
                    chargeFullShift = getChargeFullShift(context, j2);
                    sChargeFullShift = chargeFullShift;
                    if (chargeFullShift <= 0) {
                        return -1.0d;
                    }
                }
                return (j2 * 1.0d) / chargeFullShift;
            } catch (NumberFormatException e2) {
                BLUtils.LOCAL_LOG.log("DEBUG CHARGE FULL INIT: charge full is NOT number.");
                e2.printStackTrace();
            }
        }
        return getDesignedBatteryCapacity(context);
    }

    public static double getCurrentBatteryCap(int i2) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - sCachedStartTime < ((long) i2) + sLastReadingTime) {
            double d2 = sCachedChargeCounter;
            if (d2 >= 0.0d) {
                return d2;
            }
        }
        sCachedChargeCounter = getCurrentBatteryCap(PowerKeeperApplication.b());
        long jCurrentTimeMillis2 = System.currentTimeMillis();
        sCachedStartTime = jCurrentTimeMillis2;
        sLastReadingTime = jCurrentTimeMillis2 - jCurrentTimeMillis;
        return sCachedChargeCounter;
    }

    /* JADX WARN: Code duplicated, block: B:21:0x003f  */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0047, code lost:
    
        if (r7 > 0) goto L23;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static double getCurrentBatteryCap(android.content.Context r11) {
        /*
            java.lang.String r0 = "batterymanager"
            java.lang.Object r0 = r11.getSystemService(r0)
            android.os.BatteryManager r0 = (android.os.BatteryManager) r0
            r1 = 0
            if (r0 == 0) goto L12
            r3 = 1
            long r3 = r0.getLongProperty(r3)
            goto L13
        L12:
            r3 = r1
        L13:
            int r0 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            r5 = -4616189618054758400(0xbff0000000000000, double:-1.0)
            if (r0 == 0) goto L1f
            r7 = -9223372036854775808
            int r0 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r0 != 0) goto L37
        L1f:
            java.lang.String r0 = "/sys/class/power_supply/battery/charge_counter"
            java.lang.String r0 = com.miui.powerkeeper.batterylife.BLUtils.readSysNodeInfo(r0)
            if (r0 == 0) goto L4e
            int r7 = r0.length()
            if (r7 != 0) goto L2e
            goto L4e
        L2e:
            long r3 = java.lang.Long.parseLong(r0)     // Catch: java.lang.NumberFormatException -> L33
            goto L37
        L33:
            r0 = move-exception
            r0.printStackTrace()
        L37:
            long r7 = com.miui.powerkeeper.batterylife.systeminfo.BSHelper.sChargeCounterShift
            int r0 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            r9 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            if (r0 > 0) goto L49
            long r7 = getChargeCounterShift(r11)
            com.miui.powerkeeper.batterylife.systeminfo.BSHelper.sChargeCounterShift = r7
            int r11 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r11 <= 0) goto L4e
        L49:
            double r0 = (double) r3
            double r0 = r0 * r9
            double r2 = (double) r7
            double r0 = r0 / r2
            return r0
        L4e:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powerkeeper.batterylife.systeminfo.BSHelper.getCurrentBatteryCap(android.content.Context):double");
    }
}
