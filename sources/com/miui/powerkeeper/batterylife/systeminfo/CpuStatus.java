package com.miui.powerkeeper.batterylife.systeminfo;

import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.powerchecker.PowerCheckerCloudConfigure;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class CpuStatus {
    private static final String CPU_AVAILABLE_FREQ_PATH = "/sys/devices/system/cpu/cpufreq/%s/scaling_available_frequencies";
    public static final String CPU_CLUSTER_PREFIX = "cluster";
    private static final String CPU_FREQ_DIR = "/sys/devices/system/cpu/cpufreq";
    private static final String CPU_FREQ_TIME_IN_STATE = "/sys/devices/system/cpu/cpufreq/%s/stats/time_in_state";
    public static final String GPU_AVAILABLE_FREQS = "/sys/class/kgsl/kgsl-3d0/devfreq/available_frequencies";
    public static final String GPU_FREQ_TIME_STAT = "/sys/class/kgsl/kgsl-3d0/devfreq/trans_stat";
    public static String[] mCpuClusters = getCpuCluster();
    public static ArrayList<String[]> mAvailableFrequencies = getAvailableFrequencies();

    static class MyComparator implements Comparator<String> {
        MyComparator() {
        }

        @Override // java.util.Comparator
        public int compare(String str, String str2) {
            if (str.length() > str2.length()) {
                return 1;
            }
            if (str.length() < str2.length()) {
                return -1;
            }
            return str.compareTo(str2);
        }
    }

    private CpuStatus() {
    }

    private static ArrayList<String[]> getAvailableFrequencies() {
        ArrayList<String[]> arrayList = new ArrayList<>();
        if (isCpuClustersNull()) {
            return arrayList;
        }
        for (String str : mCpuClusters) {
            String sysNodeInfo = BLUtils.readSysNodeInfo(String.format(Locale.getDefault(), "/sys/devices/system/cpu/cpufreq/%s/stats/time_in_state", str));
            if (sysNodeInfo != null) {
                String[] strArrSplit = sysNodeInfo.split(ShellUtils.COMMAND_LINE_END);
                for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                    strArrSplit[i2] = strArrSplit[i2].split("\\s+")[0];
                }
                arrayList.add(strArrSplit);
            }
        }
        return arrayList;
    }

    public static String[] getAvailableGpuFreqs() throws Throwable {
        String[] strArr;
        File file = new File(GPU_AVAILABLE_FREQS);
        BufferedReader bufferedReader = null;
        String[] strArrSplit = null;
        BufferedReader bufferedReader2 = null;
        BufferedReader bufferedReader3 = null;
        try {
            if (!file.exists()) {
                return null;
            }
            try {
                BufferedReader bufferedReader4 = new BufferedReader(new FileReader(file));
                while (true) {
                    try {
                        String line = bufferedReader4.readLine();
                        if (line == null) {
                            try {
                                bufferedReader4.close();
                                return strArrSplit;
                            } catch (IOException e2) {
                                e2.printStackTrace();
                                return strArrSplit;
                            }
                        }
                        strArrSplit = line.trim().split("\\s+");
                    } catch (FileNotFoundException e3) {
                        e = e3;
                        String[] strArr2 = strArrSplit;
                        bufferedReader2 = bufferedReader4;
                        strArr = strArr2;
                        e.printStackTrace();
                        if (bufferedReader2 != null) {
                            try {
                                bufferedReader2.close();
                            } catch (IOException e4) {
                                e = e4;
                                e.printStackTrace();
                                return strArr;
                            }
                        }
                        return strArr;
                    } catch (IOException e5) {
                        e = e5;
                        String[] strArr3 = strArrSplit;
                        bufferedReader3 = bufferedReader4;
                        strArr = strArr3;
                        Log.e("GpuReader", e.getMessage());
                        e.printStackTrace();
                        if (bufferedReader3 != null) {
                            try {
                                bufferedReader3.close();
                            } catch (IOException e6) {
                                e = e6;
                                e.printStackTrace();
                                return strArr;
                            }
                        }
                        return strArr;
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader = bufferedReader4;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e7) {
                                e7.printStackTrace();
                            }
                        }
                        throw th;
                    }
                }
            } catch (FileNotFoundException e8) {
                e = e8;
                strArr = null;
            } catch (IOException e9) {
                e = e9;
                strArr = null;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static String[] getCpuCluster() {
        String[] list;
        File file = new File(CPU_FREQ_DIR);
        if (!file.exists() || (list = file.list(new FilenameFilter() { // from class: com.miui.powerkeeper.batterylife.systeminfo.a
            @Override // java.io.FilenameFilter
            public final boolean accept(File file2, String str) {
                return CpuStatus.lambda$getCpuCluster$0(file2, str);
            }
        })) == null) {
            return null;
        }
        Arrays.sort(list, new MyComparator());
        return list;
    }

    public static HashMap<String, String> getTimeInStateMap() {
        HashMap<String, String> map = new HashMap<>();
        if (isCpuClustersNull()) {
            return map;
        }
        for (int i2 = 0; i2 < mCpuClusters.length; i2++) {
            String sysNodeInfo = BLUtils.readSysNodeInfo(String.format(Locale.getDefault(), "/sys/devices/system/cpu/cpufreq/%s/stats/time_in_state", mCpuClusters[i2]));
            if (!TextUtils.isEmpty(sysNodeInfo)) {
                map.put(mCpuClusters[i2], sysNodeInfo);
            }
        }
        return map;
    }

    private static boolean isCpuClustersNull() {
        if (mCpuClusters == null) {
            mCpuClusters = getCpuCluster();
        }
        return mCpuClusters == null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getCpuCluster$0(File file, String str) {
        return str.startsWith(PowerCheckerCloudConfigure.POLICY_ITEM);
    }

    public static long[] readDeviceTimeInState() {
        ArrayList arrayList = new ArrayList();
        if (isCpuClustersNull()) {
            return null;
        }
        for (String str : mCpuClusters) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(String.format("/sys/devices/system/cpu/cpufreq/%s/stats/time_in_state", str)).getPath()));
                while (true) {
                    try {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        if (!line.trim().startsWith(LogType.LOG_TYPE_CPU_NAME)) {
                            arrayList.add(Long.valueOf(Long.parseLong(line.split("\\s+")[1])));
                        }
                    } catch (Throwable th) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                    e.printStackTrace();
                }
                bufferedReader.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        long[] jArr = new long[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            jArr[i2] = ((Long) arrayList.get(i2)).longValue();
        }
        return jArr;
    }

    /* JADX WARN: Code duplicated, block: B:30:0x007d A[LOOP:1: B:28:0x0077->B:30:0x007d, LOOP_END] */
    public static long[] readGpuTimeStats() {
        long[] jArr;
        int i2;
        ArrayList arrayList = new ArrayList();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(GPU_FREQ_TIME_STAT).getPath()));
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (!line.trim().startsWith("From") && !line.trim().endsWith("time(ms)") && !line.trim().startsWith("Total transition")) {
                        String[] strArrSplit = line.split("\\s+");
                        arrayList.add(Long.valueOf(Long.parseLong(strArrSplit[strArrSplit.length - 1])));
                    }
                    jArr = new long[arrayList.size()];
                    for (i2 = 0; i2 < arrayList.size(); i2++) {
                        jArr[i2] = ((Long) arrayList.get(i2)).longValue();
                    }
                    return jArr;
                } catch (Throwable th) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
                e.printStackTrace();
            }
            bufferedReader.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        jArr = new long[arrayList.size()];
        while (i2 < arrayList.size()) {
            jArr[i2] = ((Long) arrayList.get(i2)).longValue();
        }
        return jArr;
    }
}
