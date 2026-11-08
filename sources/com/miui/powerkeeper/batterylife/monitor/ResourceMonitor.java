package com.miui.powerkeeper.batterylife.monitor;

import android.annotation.Nullable;
import android.os.SystemProperties;
import android.util.SparseArray;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ResourceMonitor {
    public static final int TYPE_DEVICE_IDLE = 2;
    public static final int TYPE_SLEEP_MODE = 4;
    public static final int TYPE_SUBSYSTEM = 1;
    SparseArray<Resource> mResourceArray = new SparseArray<>();

    public ResourceMonitor() {
        if (SystemProperties.get("ro.boot.hardware", "Unknown").equals("qcom")) {
            this.mResourceArray.put(1, new Subsystem());
        }
        this.mResourceArray.put(2, new DeviceIdle());
        this.mResourceArray.put(4, new SleepMode());
        resetAllData();
    }

    public void dump(PrintWriter printWriter, String[] strArr) {
        int i2 = 0;
        if (strArr == null || strArr.length <= 1 || !strArr[0].equals("-i")) {
            while (i2 < this.mResourceArray.size()) {
                this.mResourceArray.valueAt(i2).dump(printWriter, strArr);
                i2++;
            }
            return;
        }
        if (strArr[1].equals("list")) {
            while (i2 < this.mResourceArray.size()) {
                printWriter.println(i2 + "  " + this.mResourceArray.valueAt(i2).toString());
                i2++;
            }
            return;
        }
        try {
            int i3 = Integer.parseInt(strArr[1]);
            if (i3 < this.mResourceArray.size()) {
                this.mResourceArray.valueAt(i3).dump(printWriter, (String[]) Arrays.copyOfRange(strArr, 2, strArr.length));
            }
        } catch (NumberFormatException unused) {
            printWriter.println("Number format invalid. args=" + strArr[1]);
        }
    }

    @Nullable
    public ArrayList<Map<String, Object>> getResourceData(int i2) {
        if (this.mResourceArray.get(i2) != null) {
            return this.mResourceArray.get(i2).getUploadData();
        }
        return null;
    }

    public void resetAllData() {
        for (int i2 = 0; i2 < this.mResourceArray.size(); i2++) {
            this.mResourceArray.valueAt(i2).reset();
        }
    }
}
