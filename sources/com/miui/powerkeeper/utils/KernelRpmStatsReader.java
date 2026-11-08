package com.miui.powerkeeper.utils;

import android.os.Build;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.powerchecker.PowerCheckerNotifier;
import com.miui.powerkeeper.siming.shaosi.SubsystemN;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class KernelRpmStatsReader {
    private static final int[][] INDEX_VDD_LOW_MIN;
    private static final String RPM_ADSP_NAME = "ADSP";
    private static final String RPM_APSS_NAME = "APSS";
    private static final String RPM_CDSP_NAME = "CDSP";
    private static final String RPM_MPSS_NAME = "MPSS";
    private static final String RPM_SLPI_NAME = "SLPI";
    private static final String RPM_SPSS_NAME = "SPSS";
    private static final String RPM_SUBSYS_COUNT_NAME = "Sleep Count";
    private static final String RPM_SUBSYS_DURATION_NAME = "Sleep Accumulated Duration";
    private static final String RPM_SUBSYS_ENTERED_NAME = "Sleep Last Entered At";
    private static final String RPM_SUBSYS_EXITED_NAME = "Sleep Last Exited At";
    private static final String RPM_SUBSYS_STATUS_NAME = "Sleep Status";
    private static final String TAG = "KernelRpmStatsReader";
    private static final boolean VDBG = false;
    private static final String[] mRpmModeName;
    private static HashMap<String, PowerCheckerNotifier.ModuleRpmSubsysStats> mRpmSubsysStats;
    private static int sKernelRpmFileIndex;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String[] KERNEL_RPM_FILE_PATH = {"/d/system_stats", "/sys/power/system_sleep/stats", "/sys/power/rpmh_stats/master_stats", "/sys/power/soc_sleep/stats", SubsystemN.SUBSYSTEM_FILE_DIR_QCOM};
    private static String RPM_MODE = "RPM Mode";
    private static final long[] mRpmStatsData = new long[RPMSubsys.MAX_SIZE.ordinal()];

    public enum RPMSubsys {
        RPM_VDD_LOW,
        RPM_VDD_MIN,
        RPM_VDD_LOW_TIME,
        RPM_VDD_MIN_TIME,
        RPM_APSS,
        RPM_MPSS,
        RPM_ADSP,
        RPM_SLPI,
        RPM_CDSP,
        RPM_SPSS,
        MAX_SIZE
    }

    private static class SleepStatsEntry {
        private long mCount;
        private long mDuration;
        private String mKey;

        private SleepStatsEntry() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append("{mKey=");
            sb.append(this.mKey);
            sb.append(" mCount=");
            sb.append(this.mCount);
            sb.append(" mDuration=");
            sb.append(this.mDuration);
            sb.append('}');
            return sb.toString();
        }
    }

    static {
        int i2 = 0;
        while (true) {
            long[] jArr = mRpmStatsData;
            if (i2 >= jArr.length) {
                break;
            }
            jArr[i2] = -1;
            i2++;
        }
        mRpmSubsysStats = new HashMap<>();
        mRpmModeName = new String[RPMSubsys.MAX_SIZE.ordinal()];
        int i3 = 0;
        while (true) {
            String[] strArr = mRpmModeName;
            if (i3 >= strArr.length) {
                break;
            }
            strArr[i3] = null;
            i3++;
        }
        INDEX_VDD_LOW_MIN = new int[][]{new int[]{1, 8}, new int[]{1, 6, 4, 9}};
        sKernelRpmFileIndex = 0;
        while (true) {
            int i4 = sKernelRpmFileIndex;
            String[] strArr2 = KERNEL_RPM_FILE_PATH;
            if (i4 >= strArr2.length || new File(strArr2[sKernelRpmFileIndex]).exists()) {
                break;
            } else {
                sKernelRpmFileIndex++;
            }
        }
        if (DEBUG) {
            Slog.d(TAG, " sKernelRpmFileIndex is " + sKernelRpmFileIndex);
        }
    }

    public static void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("sKernelRpmFileIndex=" + sKernelRpmFileIndex);
    }

    private static void fillRpmHeadStats(long[] jArr, int i2, int i3, String str) {
        int iOrdinal;
        int i4 = 0;
        if (str.contains(RPM_MODE)) {
            if (str.contains("vlow")) {
                iOrdinal = RPMSubsys.RPM_VDD_LOW.ordinal();
            } else if (str.contains("vmin")) {
                iOrdinal = RPMSubsys.RPM_VDD_MIN.ordinal();
            } else if (str.contains("aosd")) {
                iOrdinal = RPMSubsys.RPM_VDD_LOW.ordinal();
            } else {
                iOrdinal = str.contains("cxsd") ? RPMSubsys.RPM_VDD_MIN.ordinal() : 0;
            }
            mRpmModeName[iOrdinal] = str.substring(str.indexOf(58) + 1);
        }
        while (true) {
            int[] iArr = INDEX_VDD_LOW_MIN[i2];
            if (i4 >= iArr.length) {
                return;
            }
            if (i3 == iArr[i4]) {
                jArr[i4] = parseLong(str.substring(str.indexOf(58) + 1), 10);
            }
            i4++;
        }
    }

    private static void fillRpmSubsysStatsDataType1(long[] jArr, String str) {
        String strTrim = str.trim();
        if (strTrim.startsWith("mpss")) {
            jArr[RPMSubsys.RPM_MPSS.ordinal()] = parseLong(str.substring(str.lastIndexOf(58) + 1), 10);
            return;
        }
        if (strTrim.startsWith("cdsp")) {
            jArr[RPMSubsys.RPM_CDSP.ordinal()] = parseLong(str.substring(str.lastIndexOf(58) + 1), 10);
            return;
        }
        if (strTrim.startsWith("slpi")) {
            jArr[RPMSubsys.RPM_SLPI.ordinal()] = parseLong(str.substring(str.lastIndexOf(58) + 1), 10);
            return;
        }
        if (strTrim.startsWith("adsp")) {
            jArr[RPMSubsys.RPM_ADSP.ordinal()] = parseLong(str.substring(str.lastIndexOf(58) + 1), 10);
        } else if (strTrim.startsWith("spss")) {
            jArr[RPMSubsys.RPM_SPSS.ordinal()] = parseLong(str.substring(str.lastIndexOf(58) + 1), 10);
        } else if (strTrim.startsWith("apps")) {
            jArr[RPMSubsys.RPM_APSS.ordinal()] = parseLong(str.substring(str.lastIndexOf(58) + 1), 10);
        }
    }

    private static void fillRpmSubsysStatsDataType2(BufferedReader bufferedReader, HashMap<String, PowerCheckerNotifier.ModuleRpmSubsysStats> map, String str) throws IOException {
        String str2;
        int oldRpmSubsysIndex;
        if (str.contains(":")) {
            str2 = null;
            oldRpmSubsysIndex = -1;
        } else {
            oldRpmSubsysIndex = getOldRpmSubsysIndex(str);
            str2 = str;
        }
        if (str2 == null || map == null) {
            return;
        }
        String line = bufferedReader.readLine();
        PowerCheckerNotifier.ModuleRpmSubsysStats moduleRpmSubsysStats = map.get(str2);
        if (moduleRpmSubsysStats == null) {
            moduleRpmSubsysStats = new PowerCheckerNotifier.ModuleRpmSubsysStats(-1L, -1L, -1L, -1L, -1L);
        }
        while (line != null) {
            getRpmSubsys(line, moduleRpmSubsysStats, mRpmStatsData, oldRpmSubsysIndex);
            line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
        }
        if (-1 == moduleRpmSubsysStats.rpmSubsysStatus) {
            long j2 = moduleRpmSubsysStats.rpmSubsysEntry;
            if (-1 != j2) {
                long j3 = moduleRpmSubsysStats.rpmSubsysExit;
                if (-1 != j3) {
                    moduleRpmSubsysStats.rpmSubsysStatus = j2 - j3 > 0 ? 1L : 0L;
                }
            }
        }
        if (DEBUG) {
            Slog.d(TAG, "RPM name " + str2 + ", RPM info = " + moduleRpmSubsysStats.toString());
        }
        map.put(str2, moduleRpmSubsysStats);
    }

    private static int getOldRpmSubsysIndex(String str) {
        String strTrim = str.trim();
        if (strTrim.equals(RPM_MPSS_NAME)) {
            return RPMSubsys.RPM_MPSS.ordinal();
        }
        if (strTrim.equals(RPM_CDSP_NAME)) {
            return RPMSubsys.RPM_CDSP.ordinal();
        }
        if (strTrim.equals(RPM_SLPI_NAME)) {
            return RPMSubsys.RPM_SLPI.ordinal();
        }
        if (strTrim.equals(RPM_ADSP_NAME)) {
            return RPMSubsys.RPM_ADSP.ordinal();
        }
        if (strTrim.equals(RPM_SPSS_NAME)) {
            return RPMSubsys.RPM_SPSS.ordinal();
        }
        if (strTrim.equals(RPM_APSS_NAME)) {
            return RPMSubsys.RPM_APSS.ordinal();
        }
        return -1;
    }

    public static String getRpmModeName(RPMSubsys rPMSubsys) {
        return mRpmModeName[rPMSubsys.ordinal()];
    }

    private static void getRpmSubsys(String str, PowerCheckerNotifier.ModuleRpmSubsysStats moduleRpmSubsysStats, long[] jArr, int i2) {
        if (str.contains(RPM_SUBSYS_COUNT_NAME)) {
            moduleRpmSubsysStats.rpmSubsysCount = rpmSubsysState(str, 3, 16);
            if (i2 >= 0) {
                jArr[i2] = rpmSubsysState(str, 3, 16);
                return;
            }
            return;
        }
        if (str.contains(RPM_SUBSYS_ENTERED_NAME)) {
            moduleRpmSubsysStats.rpmSubsysEntry = rpmSubsysState(str, 3, 16);
            return;
        }
        if (str.contains(RPM_SUBSYS_EXITED_NAME)) {
            moduleRpmSubsysStats.rpmSubsysExit = rpmSubsysState(str, 3, 16);
        } else if (str.contains(RPM_SUBSYS_DURATION_NAME)) {
            moduleRpmSubsysStats.rpmSubsysDuration = rpmSubsysState(str, 3, 16);
        } else if (str.contains(RPM_SUBSYS_STATUS_NAME)) {
            moduleRpmSubsysStats.rpmSubsysStatus = rpmSubsysState(str, 1, 10);
        }
    }

    public static HashMap<String, PowerCheckerNotifier.ModuleRpmSubsysStats> getRpmSubsysStats() {
        return mRpmSubsysStats;
    }

    public static long getSubsysSleepCnt(RPMSubsys rPMSubsys) {
        return mRpmStatsData[rPMSubsys.ordinal()];
    }

    public static boolean isSupportReadSubSystemStat() {
        return sKernelRpmFileIndex < KERNEL_RPM_FILE_PATH.length;
    }

    public static long parseLong(String str, int i2) {
        if (str == null || str.equals("")) {
            return -1L;
        }
        return Long.parseLong(str, i2);
    }

    public static long[] readRpmStats() throws IOException {
        if (!isSupportReadSubSystemStat()) {
            if (DEBUG) {
                Slog.d(TAG, " " + sKernelRpmFileIndex + " default value " + Arrays.toString(mRpmStatsData));
            }
            return mRpmStatsData;
        }
        int i2 = sKernelRpmFileIndex;
        if (i2 == 0) {
            return readRpmStatsType0();
        }
        if (i2 == 1) {
            return readRpmStatsType1();
        }
        if (i2 == 2) {
            readRpmStatsType2();
            readRpmStatsType3();
        } else if (i2 == 3) {
            readRpmStatsType3();
        } else if (i2 == 4) {
            readRpmStatsType4();
        }
        return mRpmStatsData;
    }

    /* JADX WARN: Not initialized variable reg: 4, insn: 0x0036: MOVE (r1 I:??[OBJECT, ARRAY]) = (r4 I:??[OBJECT, ARRAY]) (LINE:55), block:B:15:0x0036 */
    private static long[] readRpmStatsType0() throws Throwable {
        FileInputStream fileInputStream;
        Exception e2;
        IOException e3;
        BufferedReader bufferedReader;
        if (sKernelRpmFileIndex != 0) {
            return mRpmStatsData;
        }
        BufferedReader bufferedReader2 = null;
        try {
            try {
                fileInputStream = new FileInputStream(KERNEL_RPM_FILE_PATH[0]);
                try {
                    BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(fileInputStream));
                    try {
                        String line = bufferedReader3.readLine();
                        for (int i2 = 0; line != null && i2 < 13; i2++) {
                            fillRpmHeadStats(mRpmStatsData, 0, i2, line);
                            line = bufferedReader3.readLine();
                        }
                        int iOrdinal = 0;
                        for (String line2 = bufferedReader3.readLine(); line2 != null; line2 = bufferedReader3.readLine()) {
                            if (iOrdinal == 0) {
                                if (line2.trim().startsWith(RPM_APSS_NAME)) {
                                    iOrdinal = RPMSubsys.RPM_APSS.ordinal();
                                } else if (line2.trim().startsWith(RPM_MPSS_NAME)) {
                                    iOrdinal = RPMSubsys.RPM_MPSS.ordinal();
                                } else if (line2.trim().startsWith(RPM_ADSP_NAME)) {
                                    iOrdinal = RPMSubsys.RPM_ADSP.ordinal();
                                } else if (line2.trim().startsWith(RPM_SLPI_NAME)) {
                                    iOrdinal = RPMSubsys.RPM_SLPI.ordinal();
                                }
                            } else if (line2.trim().startsWith("XO Count")) {
                                mRpmStatsData[iOrdinal] = parseLong(line2.substring(line2.indexOf(58) + 3), 16);
                                iOrdinal = 0;
                            }
                        }
                        bufferedReader3.close();
                        fileInputStream.close();
                        if (DEBUG) {
                            Slog.i(TAG, "readRpmStatsType0 mRpmStatsData = " + Arrays.toString(mRpmStatsData));
                        }
                        return mRpmStatsData;
                    } catch (IOException e4) {
                        e3 = e4;
                        Slog.e(TAG, "Failure in reading rpm stats", e3);
                        throw new IOException(KERNEL_RPM_FILE_PATH[0] + "read failed");
                    } catch (Exception e5) {
                        e2 = e5;
                        Slog.e(TAG, "Exception ", e2);
                        String[] strArr = KERNEL_RPM_FILE_PATH;
                        sKernelRpmFileIndex = strArr.length;
                        throw new IOException(strArr[0] + " read failed");
                    }
                } catch (IOException e6) {
                    e3 = e6;
                } catch (Exception e7) {
                    e2 = e7;
                } catch (Throwable th) {
                    th = th;
                    if (bufferedReader2 != null) {
                        bufferedReader2.close();
                    }
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                bufferedReader2 = bufferedReader;
            }
        } catch (IOException e8) {
            e3 = e8;
        } catch (Exception e9) {
            e2 = e9;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = null;
        }
    }

    /* JADX WARN: Not initialized variable reg: 5, insn: 0x003e: MOVE (r2 I:??[OBJECT, ARRAY]) = (r5 I:??[OBJECT, ARRAY]) (LINE:63), block:B:17:0x003e */
    private static long[] readRpmStatsType1() throws Throwable {
        FileInputStream fileInputStream;
        Exception e2;
        IOException e3;
        BufferedReader bufferedReader;
        if (1 != sKernelRpmFileIndex) {
            return mRpmStatsData;
        }
        BufferedReader bufferedReader2 = null;
        try {
            try {
                fileInputStream = new FileInputStream(KERNEL_RPM_FILE_PATH[1]);
                try {
                    BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(fileInputStream));
                    try {
                        String line = bufferedReader3.readLine();
                        int i2 = 0;
                        while (line != null && line.contains(RPM_MODE)) {
                            do {
                                if (!line.contains("ddr")) {
                                    fillRpmHeadStats(mRpmStatsData, 1, i2, line);
                                }
                                line = bufferedReader3.readLine();
                                i2++;
                                if (line == null || line.isEmpty()) {
                                    break;
                                }
                            } while (!line.contains(RPM_MODE));
                            if (line == null) {
                                break;
                            }
                            while (line.isEmpty() && (line = bufferedReader3.readLine()) != null) {
                            }
                        }
                        if (line == null) {
                            readRpmStatsType2();
                        } else {
                            while (line != null && !line.contains("CXSD")) {
                                fillRpmSubsysStatsDataType1(mRpmStatsData, line);
                                line = bufferedReader3.readLine();
                            }
                        }
                        bufferedReader3.close();
                        fileInputStream.close();
                        if (DEBUG) {
                            Slog.i(TAG, "readRpmStatsType1 mRpmStatsData = " + Arrays.toString(mRpmStatsData));
                        }
                        return mRpmStatsData;
                    } catch (IOException e4) {
                        e3 = e4;
                        Slog.e(TAG, "Failure in reading rpm stats", e3);
                        throw new IOException(KERNEL_RPM_FILE_PATH[1] + " read failed");
                    } catch (Exception e5) {
                        e2 = e5;
                        Slog.e(TAG, "Exception ", e2);
                        String[] strArr = KERNEL_RPM_FILE_PATH;
                        sKernelRpmFileIndex = strArr.length;
                        throw new IOException(strArr[1] + " read failed");
                    }
                } catch (IOException e6) {
                    e3 = e6;
                } catch (Exception e7) {
                    e2 = e7;
                } catch (Throwable th) {
                    th = th;
                    if (bufferedReader2 != null) {
                        bufferedReader2.close();
                    }
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                bufferedReader2 = bufferedReader;
            }
        } catch (IOException e8) {
            e3 = e8;
        } catch (Exception e9) {
            e2 = e9;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v19 */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v5 */
    /* JADX WARN: Type inference failed for: r2v6, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.String[]] */
    /* JADX WARN: Type inference failed for: r3v5, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v1 */
    /* JADX WARN: Type inference failed for: r5v4, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r5v5, types: [java.io.FileInputStream, java.io.InputStream] */
    private static void readRpmStatsType2() throws Throwable {
        Exception e2;
        IOException e3;
        ?? r3 = KERNEL_RPM_FILE_PATH;
        ?? fileInputStream = r3[2];
        if (!new File((String) fileInputStream).exists()) {
            return;
        }
        ?? r2 = 0;
        r2 = 0;
        try {
            try {
                fileInputStream = new FileInputStream((String) r3[2]);
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    try {
                        mRpmSubsysStats = new HashMap<>();
                        for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                            fillRpmSubsysStatsDataType2(bufferedReader, mRpmSubsysStats, line);
                        }
                        bufferedReader.close();
                        fileInputStream.close();
                    } catch (IOException e4) {
                        e3 = e4;
                        Slog.e(TAG, "Failure reading rpm stats", e3);
                        throw new IOException(KERNEL_RPM_FILE_PATH[2] + " read rpm failed");
                    } catch (Exception e5) {
                        e2 = e5;
                        Slog.e(TAG, "Exception: ", e2);
                        String[] strArr = KERNEL_RPM_FILE_PATH;
                        sKernelRpmFileIndex = strArr.length;
                        throw new IOException(strArr[2] + " read rpm failed");
                    }
                } catch (IOException e6) {
                    e3 = e6;
                } catch (Exception e7) {
                    e2 = e7;
                } catch (Throwable th) {
                    th = th;
                    if (r2 != 0) {
                        r2.close();
                    }
                    if (fileInputStream != 0) {
                        fileInputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                r2 = r3;
            }
        } catch (IOException e8) {
            e3 = e8;
        } catch (Exception e9) {
            e2 = e9;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = 0;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v6, types: [java.io.BufferedReader, java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r4v1, types: [com.miui.powerkeeper.utils.a] */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v2, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r4v3 */
    private static void readRpmStatsType3() throws Throwable {
        FileInputStream fileInputStream;
        ?? file = new File(KERNEL_RPM_FILE_PATH[3]);
        if (!file.exists()) {
            return;
        }
        ArrayList<SleepStatsEntry> arrayList = new ArrayList(4);
        ?? r4 = 0;
        r4 = 0;
        try {
            try {
                fileInputStream = new FileInputStream((File) file);
                try {
                    file = new BufferedReader(new InputStreamReader(fileInputStream));
                    try {
                        String line = file.readLine();
                        while (line != null) {
                            if (line.isEmpty()) {
                                line = file.readLine();
                            } else {
                                SleepStatsEntry sleepStatsEntry = new SleepStatsEntry();
                                arrayList.add(sleepStatsEntry);
                                sleepStatsEntry.mKey = line;
                                line = file.readLine();
                                while (line != null && !line.isEmpty()) {
                                    if (line.contains("Count")) {
                                        sleepStatsEntry.mCount = parseLong(line.substring(line.indexOf(58) + 1), 10);
                                    } else if (line.contains("Accumulated Duration(sec)")) {
                                        sleepStatsEntry.mDuration = parseLong(line.substring(line.indexOf(58) + 1), 10) * 1000;
                                    }
                                    line = file.readLine();
                                }
                            }
                        }
                        Utils.closeQuietly(file);
                        Utils.closeQuietly(fileInputStream);
                        for (SleepStatsEntry sleepStatsEntry2 : arrayList) {
                            if (sleepStatsEntry2.mKey.equals("aosd")) {
                                String[] strArr = mRpmModeName;
                                RPMSubsys rPMSubsys = RPMSubsys.RPM_VDD_LOW;
                                strArr[rPMSubsys.ordinal()] = sleepStatsEntry2.mKey;
                                long[] jArr = mRpmStatsData;
                                jArr[rPMSubsys.ordinal()] = sleepStatsEntry2.mCount;
                                jArr[RPMSubsys.RPM_VDD_LOW_TIME.ordinal()] = sleepStatsEntry2.mDuration;
                            } else if (sleepStatsEntry2.mKey.equals("cxsd")) {
                                String[] strArr2 = mRpmModeName;
                                RPMSubsys rPMSubsys2 = RPMSubsys.RPM_VDD_MIN;
                                strArr2[rPMSubsys2.ordinal()] = sleepStatsEntry2.mKey;
                                long[] jArr2 = mRpmStatsData;
                                jArr2[rPMSubsys2.ordinal()] = sleepStatsEntry2.mCount;
                                jArr2[RPMSubsys.RPM_VDD_MIN_TIME.ordinal()] = sleepStatsEntry2.mDuration;
                            }
                        }
                        if (DEBUG) {
                            Slog.i(TAG, "readRpmStatsType3 allStats=" + arrayList + " mRpmStatsData = " + Arrays.toString(mRpmStatsData));
                        }
                    } catch (IOException e2) {
                        e = e2;
                        Slog.e(TAG, "readRpmStatsType3 Failure in reading rpm stats", e);
                        throw new IOException(KERNEL_RPM_FILE_PATH[3] + " read failed1");
                    } catch (Exception e3) {
                        e = e3;
                        Slog.e(TAG, "readRpmStatsType3", e);
                        String[] strArr3 = KERNEL_RPM_FILE_PATH;
                        sKernelRpmFileIndex = strArr3.length;
                        throw new IOException(strArr3[3] + " read failed2");
                    } catch (Throwable th) {
                        th = th;
                        r4 = file;
                        Utils.closeQuietly(r4);
                        Utils.closeQuietly(fileInputStream);
                        throw th;
                    }
                } catch (IOException e4) {
                    e = e4;
                } catch (Exception e5) {
                    e = e5;
                } catch (Throwable th2) {
                    th = th2;
                    Utils.closeQuietly(r4);
                    Utils.closeQuietly(fileInputStream);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fileInputStream = null;
            }
        } catch (IOException e6) {
            e = e6;
        } catch (Exception e7) {
            e = e7;
        } catch (Throwable th4) {
            th = th4;
            fileInputStream = null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:69:0x0147  */
    /* JADX WARN: Code duplicated, block: B:70:0x0164  */
    /* JADX WARN: Code duplicated, block: B:72:0x016e  */
    /* JADX WARN: Code duplicated, block: B:73:0x018b  */
    /* JADX WARN: Code duplicated, block: B:75:0x0191  */
    /* JADX WARN: Code duplicated, block: B:77:0x0196  */
    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    private static void readRpmStatsType4() throws Throwable {
        FileInputStream fileInputStream;
        long j2;
        long j3;
        long j4;
        long j5;
        long j6;
        long j7;
        long j8;
        long j9;
        String str = KERNEL_RPM_FILE_PATH[4];
        File file = new File(str);
        if (file.exists()) {
            if (!file.canRead()) {
                Log.d(TAG, "eee:" + str + " Cannot be read");
                return;
            }
            String[] list = new File(str).list();
            mRpmSubsysStats = new HashMap<>();
            int i2 = 0;
            while (i2 < list.length) {
                if (list[i2].equals("ddr_stats")) {
                    str = str;
                } else {
                    BufferedReader bufferedReader = null;
                    long j10 = -1;
                    try {
                        fileInputStream = new FileInputStream(str + "/" + list[i2]);
                        try {
                            try {
                                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(fileInputStream));
                                try {
                                    try {
                                        String line = bufferedReader2.readLine();
                                        j2 = -1;
                                        j3 = -1;
                                        j4 = -1;
                                        while (line != null) {
                                            try {
                                                if (line.isEmpty()) {
                                                    break;
                                                }
                                                str = str;
                                                long j11 = j10;
                                                try {
                                                    String strReplaceAll = line.substring(line.indexOf(61) + 1).replaceAll(" ", "");
                                                    if (line.contains("Count")) {
                                                        j10 = Long.parseLong(strReplaceAll);
                                                    } else {
                                                        if (line.contains("Last Entered")) {
                                                            j2 = Long.parseLong(strReplaceAll);
                                                        } else if (line.contains("Last Exited")) {
                                                            j3 = Long.parseLong(strReplaceAll);
                                                        } else if (line.contains("Duration")) {
                                                            j4 = Long.parseLong(strReplaceAll);
                                                        }
                                                        j10 = j11;
                                                    }
                                                    try {
                                                        line = bufferedReader2.readLine();
                                                        str = str;
                                                    } catch (Exception e2) {
                                                        e = e2;
                                                    }
                                                } catch (Exception e3) {
                                                    e = e3;
                                                    bufferedReader = bufferedReader2;
                                                    j10 = j11;
                                                }
                                            } catch (Exception e4) {
                                                e = e4;
                                                str = str;
                                            }
                                            bufferedReader = bufferedReader2;
                                            Slog.e(TAG, "Exception: ", e);
                                            Utils.closeQuietly(bufferedReader);
                                            Utils.closeQuietly(fileInputStream);
                                            j8 = j10;
                                            j7 = j2;
                                            j6 = j3;
                                            j5 = j4;
                                            if (list[i2].equals("aosd")) {
                                                long[] jArr = mRpmStatsData;
                                                RPMSubsys rPMSubsys = RPMSubsys.RPM_VDD_LOW;
                                                jArr[rPMSubsys.ordinal()] = j8;
                                                jArr[RPMSubsys.RPM_VDD_LOW_TIME.ordinal()] = j5 / 19200;
                                                mRpmModeName[rPMSubsys.ordinal()] = "aosd";
                                            } else if (list[i2].equals("cxsd")) {
                                                long[] jArr2 = mRpmStatsData;
                                                RPMSubsys rPMSubsys2 = RPMSubsys.RPM_VDD_MIN;
                                                jArr2[rPMSubsys2.ordinal()] = j8;
                                                jArr2[RPMSubsys.RPM_VDD_MIN_TIME.ordinal()] = j5 / 19200;
                                                mRpmModeName[rPMSubsys2.ordinal()] = "cxsd";
                                            } else {
                                                if (j7 > j6) {
                                                    j9 = 1;
                                                } else {
                                                    j9 = 0;
                                                }
                                                mRpmSubsysStats.put(list[i2].toUpperCase(), new PowerCheckerNotifier.ModuleRpmSubsysStats(j8, j7, j6, j5, j9));
                                            }
                                        }
                                        str = str;
                                        Utils.closeQuietly(bufferedReader2);
                                        Utils.closeQuietly(fileInputStream);
                                        j7 = j2;
                                        j6 = j3;
                                        j5 = j4;
                                        j8 = j10;
                                    } catch (Exception e5) {
                                        e = e5;
                                        str = str;
                                        j2 = -1;
                                        j3 = -1;
                                        j4 = -1;
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    bufferedReader = bufferedReader2;
                                    Utils.closeQuietly(bufferedReader);
                                    Utils.closeQuietly(fileInputStream);
                                    throw th;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        } catch (Exception e6) {
                            e = e6;
                            j2 = -1;
                            j3 = -1;
                            j4 = -1;
                        }
                    } catch (Exception e7) {
                        e = e7;
                        fileInputStream = null;
                    } catch (Throwable th3) {
                        th = th3;
                        fileInputStream = null;
                    }
                    if (list[i2].equals("aosd")) {
                        long[] jArr3 = mRpmStatsData;
                        RPMSubsys rPMSubsys3 = RPMSubsys.RPM_VDD_LOW;
                        jArr3[rPMSubsys3.ordinal()] = j8;
                        jArr3[RPMSubsys.RPM_VDD_LOW_TIME.ordinal()] = j5 / 19200;
                        mRpmModeName[rPMSubsys3.ordinal()] = "aosd";
                    } else if (list[i2].equals("cxsd")) {
                        long[] jArr4 = mRpmStatsData;
                        RPMSubsys rPMSubsys4 = RPMSubsys.RPM_VDD_MIN;
                        jArr4[rPMSubsys4.ordinal()] = j8;
                        jArr4[RPMSubsys.RPM_VDD_MIN_TIME.ordinal()] = j5 / 19200;
                        mRpmModeName[rPMSubsys4.ordinal()] = "cxsd";
                    } else {
                        if (j7 > j6) {
                            j9 = 1;
                        } else {
                            j9 = 0;
                        }
                        mRpmSubsysStats.put(list[i2].toUpperCase(), new PowerCheckerNotifier.ModuleRpmSubsysStats(j8, j7, j6, j5, j9));
                    }
                }
                i2++;
                str = str;
            }
        }
    }

    private static long rpmSubsysState(String str, int i2, int i3) {
        return parseLong(str.substring(str.lastIndexOf(58) + i2), i3);
    }
}
