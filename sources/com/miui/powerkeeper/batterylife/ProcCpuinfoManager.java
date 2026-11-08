package com.miui.powerkeeper.batterylife;

import android.annotation.NonNull;
import android.content.Context;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.data.AppCpuinfoData;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.ToLongFunction;
import miui.os.Build;
import miui.process.ForegroundInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ProcCpuinfoManager {
    private static final int CPUINFO_DETAILS_NUM = 5;
    private static final int CPUINFO_TOPN_1 = 5;
    private static final int CPUINFO_TOPN_2 = 10;
    private static int CURRENT_LEVEL_1 = 500;
    private static int CURRENT_LEVEL_2 = 650;
    private static int CURRENT_LEVEL_3 = 1000;
    private static final long DELAY_TIME_MS0 = 120000;
    private static final long DELAY_TIME_MS1 = 1800000;
    private static final long DELAY_TIME_MS2 = 3600000;
    private static int MAX_LOG_FILES = 3;
    private static final String POWER_STAT_FILE_DIR = "/data/miuilog/power/";
    private static final String STATS_FILE_SUFFIX = "-abnormal_cpuinfo.log";
    public static final String TAG = "ProcCpuinfoManager";
    private Context mContext;
    private ForegroundInfo mCurrentInfo;
    EventsAggregator mEventsAggregator;
    private boolean mOnBattery;
    private String[] mSupportPkgs;
    private ForegroundInfo mLastFgInfo = null;
    private ArrayMap<String, AppCpuinfoData> mTotalAppCpuInfoMap = new ArrayMap<>();
    private Runnable mStatsRunnable0 = new Runnable() { // from class: com.miui.powerkeeper.batterylife.e
        @Override // java.lang.Runnable
        public final void run() throws Throwable {
            this.f376a.lambda$new$0();
        }
    };
    private Runnable mStatsRunnable1 = new Runnable() { // from class: com.miui.powerkeeper.batterylife.f
        @Override // java.lang.Runnable
        public final void run() throws Throwable {
            this.f377a.lambda$new$1();
        }
    };
    private Runnable mStatsRunnable2 = new Runnable() { // from class: com.miui.powerkeeper.batterylife.g
        @Override // java.lang.Runnable
        public final void run() throws Throwable {
            this.f378a.lambda$new$2();
        }
    };
    private Handler mHandler = new Handler(BackgroundThread.getHandler().getLooper()) { // from class: com.miui.powerkeeper.batterylife.ProcCpuinfoManager.1
        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) throws Throwable {
            ProcCpuinfoManager.this.handleMessage(message);
        }
    };

    ProcCpuinfoManager(Context context) {
        this.mContext = context;
        String string = SimpleSettings.Misc.getString(this.mContext, BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE, "");
        if (string.length() <= 0) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            String strOptString = Build.IS_INTERNATIONAL_BUILD ? jSONObject.optString("pkgList_intl") : jSONObject.optString("pkgList");
            if (strOptString.length() > 0) {
                if ("none".equalsIgnoreCase(strOptString)) {
                    this.mSupportPkgs = null;
                } else {
                    this.mSupportPkgs = strOptString.split(";");
                }
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        int intExtra = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4).getIntExtra("status", -1);
        this.mOnBattery = (intExtra == 2 || intExtra == 5) ? false : true;
        Log.d(TAG, "mOnBattery is " + this.mOnBattery);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        this.mEventsAggregator = eventsAggregator;
        if (!this.mOnBattery || eventsAggregator == null) {
            return;
        }
        eventsAggregator.registerForForegroundInfo(this.mHandler);
        this.mEventsAggregator.registerForScreenOn(this.mHandler, null);
        this.mEventsAggregator.registerForScreenOff(this.mHandler, null);
    }

    private void fgChanged(ForegroundInfo foregroundInfo, int i2) throws Throwable {
        String str = foregroundInfo.mForegroundPackageName;
        Log.d(TAG, "pkg is " + str + ",  i = " + i2);
        if (isAppSupport(str)) {
            AppCpuinfoData appCpuinfoData = this.mTotalAppCpuInfoMap.containsKey(str) ? this.mTotalAppCpuInfoMap.get(str) : new AppCpuinfoData(str);
            if (i2 >= 0) {
                String[] strArr = appCpuinfoData.appCpuInfo;
                if (i2 < strArr.length) {
                    if (strArr[i2].isEmpty() || appCpuinfoData.appCpuInfo[i2].equals("")) {
                        String topNProcessInfoFromCpuInfoV1 = getTopNProcessInfoFromCpuInfoV1(5);
                        Log.d(TAG, topNProcessInfoFromCpuInfoV1);
                        appCpuinfoData.appCpuInfo[i2] = topNProcessInfoFromCpuInfoV1;
                        this.mTotalAppCpuInfoMap.put(str, appCpuinfoData);
                    }
                }
            }
        }
    }

    private float getCurrentNow() {
        BatteryManager batteryManager = (BatteryManager) PowerKeeperApplication.b().getSystemService("batterymanager");
        if (batteryManager != null) {
            float intProperty = batteryManager.getIntProperty(2) / 1000.0f;
            Log.d(TAG, "current1 = " + intProperty);
            return intProperty;
        }
        String sysNodeInfo = BLUtils.readSysNodeInfo("/sys/class/power_supply/battery/current_now");
        Log.d(TAG, "current2 =" + sysNodeInfo);
        if (sysNodeInfo == null || sysNodeInfo.length() <= 0) {
            return 0.0f;
        }
        try {
            return Integer.parseInt(sysNodeInfo) / 1000.0f;
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return 0.0f;
        }
    }

    private String getTopNProcessInfoFromCpuInfoV1(int i2) throws Throwable {
        InputStreamReader inputStreamReader;
        int i3;
        int i4 = i2 + 2;
        String[] strArr = new String[i4];
        try {
            Process processExec = Runtime.getRuntime().exec("dumpsys cpuinfo");
            BufferedReader bufferedReader = null;
            try {
                inputStreamReader = new InputStreamReader(processExec.getInputStream());
                try {
                    try {
                        BufferedReader bufferedReader2 = new BufferedReader(inputStreamReader);
                        int i5 = 0;
                        while (true) {
                            try {
                                String line = bufferedReader2.readLine();
                                if (line != null) {
                                    if (i5 < 2) {
                                        i3 = i5 + 1;
                                        strArr[i5] = line.trim();
                                    } else if (line.contains("%") && !line.contains("iowait") && !line.contains("    ") && !line.contains("   +")) {
                                        if (i5 >= i4) {
                                            String string = Arrays.toString(strArr);
                                            try {
                                                bufferedReader2.close();
                                            } catch (IOException e2) {
                                                Log.e(TAG, "close file stream error", e2);
                                            }
                                            try {
                                                inputStreamReader.close();
                                            } catch (IOException e3) {
                                                Log.e(TAG, "close file stream error", e3);
                                            }
                                            processExec.destroy();
                                            return string;
                                        }
                                        i3 = i5 + 1;
                                        strArr[i5] = line.trim();
                                    }
                                    i5 = i3;
                                } else {
                                    try {
                                        bufferedReader2.close();
                                    } catch (IOException e4) {
                                        Log.e(TAG, "close file stream error", e4);
                                    }
                                    try {
                                        inputStreamReader.close();
                                        break;
                                    } catch (IOException e5) {
                                        e = e5;
                                        Log.e(TAG, "close file stream error", e);
                                    }
                                }
                            } catch (IOException e6) {
                                e = e6;
                                bufferedReader = bufferedReader2;
                                Log.e(TAG, "io error when read stream", e);
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e7) {
                                        Log.e(TAG, "close file stream error", e7);
                                    }
                                }
                                if (inputStreamReader != null) {
                                    try {
                                        inputStreamReader.close();
                                    } catch (IOException e8) {
                                        e = e8;
                                        Log.e(TAG, "close file stream error", e);
                                    }
                                }
                            } catch (Throwable th) {
                                th = th;
                                bufferedReader = bufferedReader2;
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e9) {
                                        Log.e(TAG, "close file stream error", e9);
                                    }
                                }
                                if (inputStreamReader != null) {
                                    try {
                                        inputStreamReader.close();
                                    } catch (IOException e10) {
                                        Log.e(TAG, "close file stream error", e10);
                                    }
                                }
                                processExec.destroy();
                                throw th;
                            }
                        }
                    } catch (IOException e11) {
                        e = e11;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException e12) {
                e = e12;
                inputStreamReader = null;
            } catch (Throwable th3) {
                th = th3;
                inputStreamReader = null;
            }
            processExec.destroy();
            return Arrays.toString(strArr);
        } catch (IOException e13) {
            Log.e(TAG, "can't exec the cmd ", e13);
            return Arrays.toString(strArr);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v0 */
    /* JADX WARN: Type inference failed for: r5v1 */
    /* JADX WARN: Type inference failed for: r5v10 */
    /* JADX WARN: Type inference failed for: r5v13 */
    /* JADX WARN: Type inference failed for: r5v16 */
    /* JADX WARN: Type inference failed for: r5v2, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r5v23 */
    /* JADX WARN: Type inference failed for: r5v26 */
    /* JADX WARN: Type inference failed for: r5v27 */
    /* JADX WARN: Type inference failed for: r5v28 */
    /* JADX WARN: Type inference failed for: r5v29 */
    /* JADX WARN: Type inference failed for: r5v30 */
    /* JADX WARN: Type inference failed for: r5v31 */
    /* JADX WARN: Type inference failed for: r5v7 */
    private String getTopNProcessInfoFromCpuInfoV2(int i2) throws Throwable {
        Throwable th;
        InputStreamReader inputStreamReader;
        IOException iOException;
        StringBuilder sb = new StringBuilder();
        try {
            Process processExec = Runtime.getRuntime().exec("dumpsys cpuinfo");
            ?? r5 = 0;
            bufferedReader = null;
            BufferedReader bufferedReader = null;
            ?? r6 = 0;
            try {
                try {
                    inputStreamReader = new InputStreamReader(processExec.getInputStream());
                    try {
                        BufferedReader bufferedReader2 = new BufferedReader(inputStreamReader);
                        int i3 = 0;
                        int i4 = 0;
                        while (true) {
                            try {
                                String line = bufferedReader2.readLine();
                                if (line == null) {
                                    try {
                                        bufferedReader2.close();
                                        r5 = i3;
                                    } catch (IOException e2) {
                                        IOException iOException2 = e2;
                                        Log.e(TAG, "close file stream error", iOException2);
                                        r5 = iOException2;
                                    }
                                    try {
                                        inputStreamReader.close();
                                        break;
                                    } catch (IOException e3) {
                                        iOException = e3;
                                        Log.e(TAG, "close file stream error", iOException);
                                    }
                                } else if (i3 < 2) {
                                    sb.append(line.trim() + ShellUtils.COMMAND_LINE_END);
                                    i3++;
                                } else {
                                    if (i4 > 0) {
                                        if (line.contains("%") && (line.contains("    ") || line.contains("   +"))) {
                                            sb.append(line + ShellUtils.COMMAND_LINE_END);
                                            i4 += -1;
                                        } else {
                                            i4 = 0;
                                        }
                                    }
                                    if (line.contains("%") && !line.contains("iowait") && !line.contains("    ") && !line.contains("   +")) {
                                        if (i3 >= i2 + 2) {
                                            String string = sb.toString();
                                            try {
                                                bufferedReader2.close();
                                            } catch (IOException e4) {
                                                Log.e(TAG, "close file stream error", e4);
                                            }
                                            try {
                                                inputStreamReader.close();
                                            } catch (IOException e5) {
                                                Log.e(TAG, "close file stream error", e5);
                                            }
                                            processExec.destroy();
                                            return string;
                                        }
                                        sb.append(line + ShellUtils.COMMAND_LINE_END);
                                        i3++;
                                        i4 = 5;
                                    }
                                }
                            } catch (IOException e6) {
                                e = e6;
                                bufferedReader = bufferedReader2;
                                Log.e(TAG, "io error when read stream", e);
                                r5 = bufferedReader;
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                        r5 = bufferedReader;
                                    } catch (IOException e7) {
                                        IOException iOException3 = e7;
                                        Log.e(TAG, "close file stream error", iOException3);
                                        r5 = iOException3;
                                    }
                                }
                                if (inputStreamReader != null) {
                                    try {
                                        inputStreamReader.close();
                                    } catch (IOException e8) {
                                        iOException = e8;
                                        Log.e(TAG, "close file stream error", iOException);
                                    }
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                r6 = bufferedReader2;
                                if (r6 != 0) {
                                    try {
                                        r6.close();
                                    } catch (IOException e9) {
                                        Log.e(TAG, "close file stream error", e9);
                                    }
                                }
                                if (inputStreamReader != null) {
                                    try {
                                        inputStreamReader.close();
                                    } catch (IOException e10) {
                                        Log.e(TAG, "close file stream error", e10);
                                    }
                                }
                                processExec.destroy();
                                throw th;
                            }
                        }
                    } catch (IOException e11) {
                        e = e11;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    r6 = r5;
                }
            } catch (IOException e12) {
                e = e12;
                inputStreamReader = null;
            } catch (Throwable th4) {
                th = th4;
                inputStreamReader = null;
            }
            processExec.destroy();
            return sb.toString();
        } catch (IOException e13) {
            Log.e(TAG, "can't exec the cmd ", e13);
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Message message) throws Throwable {
        ForegroundInfo foregroundInfo;
        int i2 = message.what;
        if (i2 == -9) {
            ForegroundInfo foregroundInfo2 = (ForegroundInfo) message.obj;
            this.mCurrentInfo = foregroundInfo2;
            ForegroundInfo foregroundInfo3 = this.mLastFgInfo;
            if (foregroundInfo3 != null) {
                if (foregroundInfo3.mForegroundUid == foregroundInfo2.mForegroundUid) {
                    return;
                }
                if (isAppSupport(foregroundInfo3.mForegroundPackageName)) {
                    this.mHandler.removeCallbacks(this.mStatsRunnable0);
                    this.mHandler.removeCallbacks(this.mStatsRunnable1);
                    this.mHandler.removeCallbacks(this.mStatsRunnable2);
                    writeCpuinfoToFile();
                }
            }
            if (isAppSupport(this.mCurrentInfo.mForegroundPackageName)) {
                this.mHandler.postDelayed(this.mStatsRunnable0, DELAY_TIME_MS0);
                this.mHandler.postDelayed(this.mStatsRunnable1, 1800000L);
                this.mHandler.postDelayed(this.mStatsRunnable2, 3600000L);
            }
            this.mLastFgInfo = this.mCurrentInfo;
            return;
        }
        if (i2 != -4) {
            if (i2 == -5 && (foregroundInfo = this.mLastFgInfo) != null && isAppSupport(foregroundInfo.mForegroundPackageName)) {
                this.mHandler.removeCallbacks(this.mStatsRunnable0);
                this.mHandler.removeCallbacks(this.mStatsRunnable1);
                this.mHandler.removeCallbacks(this.mStatsRunnable2);
                writeCpuinfoToFile();
                return;
            }
            return;
        }
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            this.mCurrentInfo = eventsAggregator.getForegroundInfo();
            this.mHandler.removeCallbacksAndMessages(null);
            if (isAppSupport(this.mCurrentInfo.mForegroundPackageName)) {
                this.mHandler.postDelayed(this.mStatsRunnable0, DELAY_TIME_MS0);
                this.mHandler.postDelayed(this.mStatsRunnable1, 1800000L);
                this.mHandler.postDelayed(this.mStatsRunnable2, 3600000L);
            }
            this.mLastFgInfo = this.mCurrentInfo;
        }
    }

    private boolean isAppSupport(String str) {
        String[] strArr = this.mSupportPkgs;
        if (strArr == null) {
            Log.d(TAG, "mSupportPkgs is null");
            return false;
        }
        for (String str2 : strArr) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() throws Throwable {
        fgChanged(this.mCurrentInfo, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() throws Throwable {
        fgChanged(this.mCurrentInfo, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() throws Throwable {
        fgChanged(this.mCurrentInfo, 2);
    }

    private void setFilePermission(String str, boolean z2) {
        try {
            Path path = Paths.get(str, new String[0]);
            HashSet hashSet = new HashSet();
            if (z2) {
                hashSet.add(PosixFilePermission.OWNER_READ);
                hashSet.add(PosixFilePermission.OWNER_WRITE);
                hashSet.add(PosixFilePermission.OWNER_EXECUTE);
                hashSet.add(PosixFilePermission.GROUP_READ);
                hashSet.add(PosixFilePermission.GROUP_WRITE);
                hashSet.add(PosixFilePermission.GROUP_EXECUTE);
                hashSet.add(PosixFilePermission.OTHERS_READ);
                hashSet.add(PosixFilePermission.OTHERS_EXECUTE);
            } else {
                hashSet.add(PosixFilePermission.OWNER_READ);
                hashSet.add(PosixFilePermission.GROUP_READ);
                hashSet.add(PosixFilePermission.OTHERS_READ);
            }
            Files.setPosixFilePermissions(path, hashSet);
        } catch (Exception e2) {
            Log.e(TAG, "setFilePermission: e: " + e2);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:20:0x005c  */
    private void writeCpuinfoToFile() throws Throwable {
        byte b2;
        float currentNow = getCurrentNow();
        Log.d(TAG, "currentNow is " + currentNow);
        String str = this.mLastFgInfo.mForegroundPackageName;
        switch (str.hashCode()) {
            case -973170826:
                if (!str.equals("com.tencent.mm")) {
                    b2 = -1;
                } else {
                    b2 = 2;
                }
                break;
            case 313184810:
                if (!str.equals("com.ss.android.ugc.aweme")) {
                    b2 = -1;
                } else {
                    b2 = 1;
                }
                break;
            case 1855462465:
                if (!str.equals("com.taobao.taobao")) {
                    b2 = -1;
                } else {
                    b2 = 4;
                }
                break;
            case 1994036591:
                if (!str.equals("tv.danmaku.bili")) {
                    b2 = -1;
                } else {
                    b2 = 3;
                }
                break;
            case 2095214256:
                if (!str.equals("com.miui.home")) {
                    b2 = -1;
                } else {
                    b2 = 0;
                }
                break;
            default:
                b2 = -1;
                break;
        }
        if (b2 != 0) {
            if (b2 == 1 || b2 == 2 || b2 == 3 || b2 == 4) {
                if (currentNow <= CURRENT_LEVEL_2) {
                    return;
                }
            } else if (currentNow <= CURRENT_LEVEL_3) {
                return;
            }
        } else if (currentNow <= CURRENT_LEVEL_1) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long jCurrentTimeMillis = System.currentTimeMillis();
        sb.append(this.mLastFgInfo.mForegroundPackageName + " (");
        sb.append(simpleDateFormat.format(new Date(jCurrentTimeMillis)) + ") : \n");
        String topNProcessInfoFromCpuInfoV2 = getTopNProcessInfoFromCpuInfoV2(10);
        Log.d(TAG, "cpuinfo: " + topNProcessInfoFromCpuInfoV2);
        sb.append(topNProcessInfoFromCpuInfoV2);
        sb.append(ShellUtils.COMMAND_LINE_END);
        String string = sb.toString();
        File file = getFile();
        if (file != null) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(randomAccessFile.length());
                randomAccessFile.writeBytes(string);
                randomAccessFile.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void checkFile() {
        File[] fileArrListFiles = new File(POWER_STAT_FILE_DIR).listFiles();
        if (fileArrListFiles == null || fileArrListFiles.length <= MAX_LOG_FILES) {
            return;
        }
        Arrays.sort(fileArrListFiles, Comparator.comparingLong(new ToLongFunction() { // from class: com.miui.powerkeeper.batterylife.h
            @Override // java.util.function.ToLongFunction
            public final long applyAsLong(Object obj) {
                return ((File) obj).lastModified();
            }
        }));
        for (int i2 = 0; i2 < fileArrListFiles.length - MAX_LOG_FILES; i2++) {
            fileArrListFiles[i2].delete();
        }
    }

    void destroy() {
        ArrayMap<String, AppCpuinfoData> arrayMap = this.mTotalAppCpuInfoMap;
        if (arrayMap != null) {
            arrayMap.clear();
            this.mTotalAppCpuInfoMap = null;
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.unregisterForForegroundInfo(this.mHandler);
            eventsAggregator.unregisterForScreenOn(this.mHandler);
            eventsAggregator.unregisterForScreenOff(this.mHandler);
        }
    }

    public List<AppCpuinfoData> doUploadAppCpuinfoData() {
        ArrayList arrayList = new ArrayList();
        for (String str : this.mTotalAppCpuInfoMap.keySet()) {
            if (isAppSupport(str)) {
                arrayList.add(new AppCpuinfoData(str, this.mTotalAppCpuInfoMap.get(str).appCpuInfo));
            }
        }
        return arrayList;
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("mSupportPkgs:" + Arrays.toString(this.mSupportPkgs) + ShellUtils.COMMAND_LINE_END);
        sb.append("AppCpuInfo:\n");
        for (String str : this.mTotalAppCpuInfoMap.keySet()) {
            sb.append("  pkg:" + str + ShellUtils.COMMAND_LINE_END);
            for (String str2 : this.mTotalAppCpuInfoMap.get(str).appCpuInfo) {
                sb.append("      " + str2 + ShellUtils.COMMAND_LINE_END);
            }
        }
        return sb.toString();
    }

    public File getFile() {
        Date date = new Date(System.currentTimeMillis());
        String str = new SimpleDateFormat("yyyy-MM-dd").format(date) + STATS_FILE_SUFFIX;
        Log.d(TAG, "fileName: " + str);
        File fileCreateFile = FileUtil.createFile(POWER_STAT_FILE_DIR + str);
        setFilePermission(fileCreateFile.getAbsolutePath(), true);
        return fileCreateFile;
    }

    public void onBatteryChanged(boolean z2) {
        Log.i(TAG, "onBatteryChanged, onBattery is " + z2);
        if (this.mOnBattery == z2) {
            return;
        }
        this.mOnBattery = z2;
        if (z2) {
            EventsAggregator eventsAggregator = this.mEventsAggregator;
            if (eventsAggregator != null) {
                eventsAggregator.registerForForegroundInfo(this.mHandler);
                this.mEventsAggregator.registerForScreenOn(this.mHandler, null);
                this.mEventsAggregator.registerForScreenOff(this.mHandler, null);
                return;
            }
            return;
        }
        this.mHandler.removeCallbacksAndMessages(null);
        EventsAggregator eventsAggregator2 = this.mEventsAggregator;
        if (eventsAggregator2 != null) {
            eventsAggregator2.unregisterForForegroundInfo(this.mHandler);
            this.mEventsAggregator.unregisterForScreenOn(this.mHandler);
            this.mEventsAggregator.unregisterForScreenOff(this.mHandler);
        }
    }

    public void resetAllData() {
        this.mTotalAppCpuInfoMap.clear();
    }

    public void setSupportPkgs(String[] strArr) {
        this.mSupportPkgs = strArr;
    }
}
