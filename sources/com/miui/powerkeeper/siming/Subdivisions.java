package com.miui.powerkeeper.siming;

import android.util.SparseBooleanArray;
import com.miui.powerkeeper.batterylife.TrackData;

/* JADX INFO: loaded from: classes.dex */
public class Subdivisions {
    public static final int DI_APP_USAGE = 104;
    public static final int DI_COMMON_STATUS = 103;
    public static final int DI_DEVICE_IDLE = 100;
    public static final int DI_DS_OBSERVED = 1000;
    public static final int DI_OBSERVED = 0;
    public static final int DI_SLEEP_MODE = 102;
    public static final int DI_SUBSYSTEM = 101;
    public static final int DI_THEME_MONITOR = 300;
    public static final SparseBooleanArray sList;

    static {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        sList = sparseBooleanArray;
        sparseBooleanArray.put(100, true);
        sparseBooleanArray.put(101, true);
        sparseBooleanArray.put(102, false);
        sparseBooleanArray.put(103, true);
        sparseBooleanArray.put(104, true);
        sparseBooleanArray.put(300, false);
    }

    public static void enable(int i2, boolean z2) {
        if (i2 == 103) {
            return;
        }
        sList.put(i2, z2);
    }

    public static String idToString(int i2) {
        if (i2 == 0) {
            return "observed";
        }
        if (i2 == 100) {
            return TrackData.DI_TRACK_NAME;
        }
        if (i2 == 300) {
            return "theme_monitor";
        }
        switch (i2) {
            case 102:
                return TrackData.SM_TRACK_NAME;
            case 103:
                return "common_status";
            case 104:
                return "app_usage";
            default:
                return i2 + "";
        }
    }

    public static boolean isDisabled(int i2) {
        return !sList.get(i2);
    }
}
