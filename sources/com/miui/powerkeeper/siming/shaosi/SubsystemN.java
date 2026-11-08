package com.miui.powerkeeper.siming.shaosi;

import android.annotation.Nullable;
import android.os.Looper;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class SubsystemN extends N {
    public static final String SUBSYSTEM_FILE_DIR_QCOM = "/sys/class/power_debug/mi_power_device/qcom_sleep_stats";
    public static final String TAG = "SM_S_SubsystemN";

    public static class SubsystemDataQCom {
        public long count;
        public long durationMs;
        public long lastEnteredAt;
        public long lastExitedAt;
        public String name;
    }

    protected SubsystemN(Looper looper) {
        super(looper, 101);
    }

    @Nullable
    public ArrayList<SubsystemDataQCom> readSubsystemQCom() {
        File file = new File(SUBSYSTEM_FILE_DIR_QCOM);
        if (!file.exists() || !file.canRead()) {
            Log.d(TAG, "Dir /sys/class/power_debug/mi_power_device/qcom_sleep_stats does not exist or is not readable.");
            return null;
        }
        String[] list = file.list();
        if (list == null) {
            Log.d(TAG, "There are no files under /sys/class/power_debug/mi_power_device/qcom_sleep_stats.");
            return null;
        }
        Arrays.sort(list);
        ArrayList<SubsystemDataQCom> arrayList = new ArrayList<>(list.length + 1);
        for (String str : list) {
            if (!str.equals("ddr_stats")) {
                SubsystemDataQCom subsystemDataQCom = new SubsystemDataQCom();
                arrayList.add(subsystemDataQCom);
                subsystemDataQCom.name = str;
                try {
                    FileInputStream fileInputStream = new FileInputStream("/sys/class/power_debug/mi_power_device/qcom_sleep_stats/" + str);
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                        try {
                            for (String line = bufferedReader.readLine(); line != null && !line.isEmpty(); line = bufferedReader.readLine()) {
                                String strReplaceAll = line.substring(line.indexOf(61) + 1).replaceAll(" ", "");
                                if (line.contains("Count")) {
                                    subsystemDataQCom.count = Long.parseLong(strReplaceAll);
                                } else if (line.contains("Last Entered")) {
                                    subsystemDataQCom.lastEnteredAt = Long.parseLong(strReplaceAll);
                                } else if (line.contains("Last Exited")) {
                                    subsystemDataQCom.lastExitedAt = Long.parseLong(strReplaceAll);
                                } else if (line.contains("Duration")) {
                                    subsystemDataQCom.durationMs = Long.parseLong(strReplaceAll) / 19200;
                                }
                            }
                            bufferedReader.close();
                            fileInputStream.close();
                        } catch (Throwable th) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                        throw th3;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return arrayList;
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S, com.miui.powerkeeper.siming.IDivision
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
