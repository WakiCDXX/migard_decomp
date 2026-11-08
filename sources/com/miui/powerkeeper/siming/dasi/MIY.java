package com.miui.powerkeeper.siming.dasi;

import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.siming.SiUtils;
import com.miui.powerkeeper.siming.shaosi.ShaoSi;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class MIY {
    public static final String BUNDLE_PARAM_CHARGING = "charging";
    public static final String BUNDLE_PARAM_DEVICE_IDLE = "diState";
    public static final String BUNDLE_PARAM_FG_INFO = "fgInfo";
    public static final String BUNDLE_PARAM_SLEEP = "inSleep";
    public static final int INFO_MASK_BIT = 30;
    public static final int MI_CHARGING = 2;
    public static final int MI_DEVICE_IDLE = 6;
    public static final int MI_FG = 4;
    private static final int MI_MAX = 6;
    public static final int MI_SCREEN_OFF = 1;
    public static final int MI_SCREEN_ON = 0;
    public static final int MI_SLEEP_MODE = 5;
    public static final int MI_USER_PRESENT = 3;
    public static final String TAG = "SM_D_MIY";
    private static final SparseArray<SparseIntArray> sMessageArray = new SparseArray<>();

    private static ArrayList<Integer> get(SparseIntArray sparseIntArray) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i2 = 0; i2 < sparseIntArray.size(); i2++) {
            for (int i3 = 0; i3 < 30; i3++) {
                if (((sparseIntArray.valueAt(i2) >> i3) & 1) != 0) {
                    arrayList.add(Integer.valueOf((sparseIntArray.keyAt(i2) * 30) + i3));
                }
            }
        }
        return arrayList;
    }

    public static void notify(int i2) {
        notify(i2, new Bundle());
    }

    public static void register(int i2, int... iArr) {
        for (int i3 : iArr) {
            update(i3, i2, true);
        }
    }

    public static void unregister(int i2) {
        int[] iArr = new int[6];
        for (int i3 = 0; i3 < 6; i3++) {
            iArr[i3] = i3;
        }
        unregister(i2, iArr);
    }

    private static void update(int i2, int i3, boolean z2) {
        SparseArray<SparseIntArray> sparseArray = sMessageArray;
        SparseIntArray sparseIntArray = sparseArray.get(i2);
        if (sparseIntArray == null) {
            if (!z2) {
                return;
            }
            sparseIntArray = new SparseIntArray();
            sparseArray.put(i2, sparseIntArray);
        }
        int i4 = i3 / 30;
        if (z2) {
            sparseIntArray.put(i4, (1 << (i3 % 30)) | sparseIntArray.get(i4));
        } else {
            sparseIntArray.put(i4, (~(1 << (i3 % 30))) & sparseIntArray.get(i4));
        }
        StringBuilder sb = new StringBuilder();
        sb.append(z2 ? "" : NightStandbyRecord.KEY_UNACCOUNTED);
        sb.append("register: mi=");
        sb.append(i2);
        sb.append(",type=");
        sb.append(i4);
        sb.append("(si=");
        sb.append(i3);
        sb.append("), value=");
        sb.append(Integer.toBinaryString(sparseIntArray.get(i4)));
        SiUtils.Log.d(TAG, sb.toString());
    }

    public static void notify(int i2, Bundle bundle) {
        SparseIntArray sparseIntArray = sMessageArray.get(i2);
        if (sparseIntArray == null) {
            SiUtils.Log.d(TAG, "No monitor for " + i2);
            return;
        }
        for (Integer num : get(sparseIntArray)) {
            SiUtils.Log.d(TAG, "mi=" + i2 + ", si=" + num + ", bundle=" + bundle);
            if (bundle == null) {
                bundle = new Bundle();
            }
            if (num.intValue() < 1000) {
                ShaoSi.getInstance().getDivision(num.intValue()).notify(i2, bundle);
            } else {
                SiUtils.Log.d(TAG, "Undefined for DS.");
            }
        }
    }

    public static void unregister(int i2, int... iArr) {
        for (int i3 : iArr) {
            if (sMessageArray.get(i3) != null) {
                update(i3, i2, false);
            }
        }
    }
}
