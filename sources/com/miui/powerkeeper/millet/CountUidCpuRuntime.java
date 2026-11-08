package com.miui.powerkeeper.millet;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IProcessObserver;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.systeminfo.CpuStatus;
import com.miui.powerkeeper.dfs.DfsUtils;
import com.miui.powerkeeper.provider.UserConfigureHelper;
import com.miui.powerkeeper.tracker.TrackerConfig;
import com.miui.powerkeeper.tracker.TrackerManager;
import com.miui.powerkeeper.utils.PackageUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CountUidCpuRuntime {
    private static final String CPU_CAPACITY_PATH_PREFIX = "/sys/devices/system/cpu/cpu";
    private static final int MSG_DELAY_UPDATE = 2;
    private static final int MSG_UPDATE = 1;
    private static final int MSG_UPLOAD = 3;
    private static final String PKG_RUNTIME_INFO_TOTAL_USAGE_PATH = "/proc/package/pkg/show_all";
    private long delayTime;
    private ActivityManager mActivityManager;
    private Context mContext;
    private PackageManager mPackageManager;
    private final String TAG = "CountUidCpuRuntime";
    private boolean DEBUG = false;
    private boolean isSupportWaltMode = false;
    private boolean isSupportBpfMode = false;
    private final String HOME_APPNAME = "com.miui.home";
    private final int SYSTEM_SERVICE_UID = 1000;
    private Map<Integer, UidCpuRuntimeInfo> mPackageMap = new HashMap();
    private Map<Integer, List<Integer>> mFgAppUidList = new HashMap();
    private List<Integer> mNoRestrictAppUids = new ArrayList();
    private List<String> mSupportWaltWayDeviceList = new ArrayList(Arrays.asList("liuqin", "pipa", "thor", "cupid", "yudi", "babylon", "shennong", "houji", "sheng", "aurora", "ruyi", "fuxi"));
    private List<String> mSupportBpfWayDeviceList = new ArrayList(Arrays.asList("yuechu", "dizi", "uke", "muyu"));
    private IProcessObserver mProcessObserver = null;
    private Handler mHandler = null;
    private IBatteryStats mIBatteryStats = null;
    private boolean screenOn = true;
    private boolean mCharging = false;
    private long mLastStart = -1;
    private final String ALARM_ACTION = "millet_cpuruntime";
    private long DELTA_TIME = 43200000;

    private static class CpuCoreInfo {
        static int coreNum;
        static int[] cpuCapacity;
        static int[] cpuCoreAvailableLength;
        static long[] cpuCoreMaxFreq;
        static long[] cpuFreqs;

        private CpuCoreInfo() {
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        public MyBroadcastReceiver() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("millet_cpuruntime");
            intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
            intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
            CountUidCpuRuntime.this.mContext.registerReceiver(this, intentFilter, 2);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) throws Throwable {
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_ON".equals(action)) {
                CountUidCpuRuntime.this.screenOn = true;
                CountUidCpuRuntime.this.mHandler.removeMessages(1);
                CountUidCpuRuntime.this.mHandler.removeMessages(2);
                CountUidCpuRuntime.this.mHandler.sendMessage(CountUidCpuRuntime.this.mHandler.obtainMessage(1));
                return;
            }
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                CountUidCpuRuntime.this.screenOn = false;
                CountUidCpuRuntime.this.mHandler.removeMessages(1);
                CountUidCpuRuntime.this.mHandler.removeMessages(2);
                CountUidCpuRuntime.this.mHandler.sendMessageDelayed(CountUidCpuRuntime.this.mHandler.obtainMessage(1), 6000L);
                return;
            }
            if ("millet_cpuruntime".equals(action)) {
                CountUidCpuRuntime.this.mHandler.sendMessage(CountUidCpuRuntime.this.mHandler.obtainMessage(3));
                return;
            }
            if ("android.intent.action.ACTION_POWER_CONNECTED".equals(action)) {
                CountUidCpuRuntime.this.cancelAlarm();
                CountUidCpuRuntime.this.updateUidCpuRuntimeInfo();
                CountUidCpuRuntime.this.mCharging = true;
                CountUidCpuRuntime.this.updateUidState();
                return;
            }
            if ("android.intent.action.ACTION_POWER_DISCONNECTED".equals(action)) {
                CountUidCpuRuntime.this.startAlarm();
                CountUidCpuRuntime.this.updateUidCpuRuntimeInfo();
                CountUidCpuRuntime.this.mCharging = false;
                CountUidCpuRuntime.this.updateUidState();
            }
        }
    }

    private class UidCpuRuntimeInfo {
        CoreTimeInfo initCoreTime;
        boolean isInclue;
        CoreTimeInfo lastCoreTime;
        UidState lastState;
        long lastTime;
        CoreTimeInfo offBgCoreTime;
        long offBgTime;
        long offBgTotalRuntime;
        CoreTimeInfo offFgCoreTime;
        long offFgTime;
        long offFgTotalRuntime;
        CoreTimeInfo onBgCoreTime;
        long onBgTime;
        long onBgTotalRuntime;
        CoreTimeInfo onFgCoreTime;
        long onFgTime;
        long onFgTotalRuntime;
        String pkgName;
        int systemApp;
        int uid;

        public String toString() {
            return "uid=" + this.uid + " pkgName=" + this.pkgName + " isSystemApp=" + this.systemApp + " offBgTime=" + this.offBgTime + " offBgCoreTime=" + this.offBgCoreTime.toString();
        }

        private UidCpuRuntimeInfo() {
            this.uid = -1;
            this.isInclue = true;
            this.pkgName = null;
            this.systemApp = -1;
            this.onBgTotalRuntime = 0L;
            this.onFgTotalRuntime = 0L;
            this.offBgTotalRuntime = 0L;
            this.offFgTotalRuntime = 0L;
            this.onFgCoreTime = CountUidCpuRuntime.this.new CoreTimeInfo();
            this.onBgCoreTime = CountUidCpuRuntime.this.new CoreTimeInfo();
            this.offFgCoreTime = CountUidCpuRuntime.this.new CoreTimeInfo();
            this.offBgCoreTime = CountUidCpuRuntime.this.new CoreTimeInfo();
            this.onFgTime = 0L;
            this.offFgTime = 0L;
            this.onBgTime = 0L;
            this.offBgTime = 0L;
            this.lastState = UidState.NOT_RUN;
            this.lastTime = 0L;
            this.lastCoreTime = CountUidCpuRuntime.this.new CoreTimeInfo();
            this.initCoreTime = CountUidCpuRuntime.this.new CoreTimeInfo();
        }
    }

    enum UidState {
        NOT_RUN,
        SCREEN_ON_IN_FG,
        SCREEN_ON_IN_BG,
        SCREEN_OFF_IN_FG,
        SCREEN_OFF_IN_BG,
        CHARGING
    }

    public CountUidCpuRuntime(Context context) {
        this.mPackageManager = null;
        this.mActivityManager = null;
        this.delayTime = -1L;
        this.mContext = context;
        if (initCpuStatisticsMode()) {
            if (supportBpfMode()) {
                identifyCpuCoreInfo();
            }
            this.mPackageManager = this.mContext.getPackageManager();
            this.mActivityManager = (ActivityManager) this.mContext.getSystemService("activity");
            new MyBroadcastReceiver();
            initHandler();
            initProcessObserver();
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1), 3000L);
            this.delayTime = this.DELTA_TIME;
            startAlarm();
        }
    }

    private void addDiffToUidCpuRuntimeInfo(CoreTimeInfo coreTimeInfo, UidCpuRuntimeInfo uidCpuRuntimeInfo) {
        UidState uidState;
        if (coreTimeInfo == null || uidCpuRuntimeInfo == null) {
            return;
        }
        long totalCoreTime = coreTimeInfo.getTotalCoreTime();
        if (!coreTimeInfo.checkNeedAdd() || (uidState = uidCpuRuntimeInfo.lastState) == UidState.NOT_RUN || uidState == UidState.CHARGING) {
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis() - uidCpuRuntimeInfo.lastTime;
        if (this.DEBUG) {
            Slog.i("CountUidCpuRuntime", "uid=" + uidCpuRuntimeInfo.uid + " duration=" + coreTimeInfo.toString() + " realDeltaTime=" + jCurrentTimeMillis);
        }
        int iOrdinal = uidCpuRuntimeInfo.lastState.ordinal();
        if (iOrdinal == 1) {
            uidCpuRuntimeInfo.onFgTime += jCurrentTimeMillis;
            uidCpuRuntimeInfo.onFgTotalRuntime += totalCoreTime;
            uidCpuRuntimeInfo.onFgCoreTime.addDiff(coreTimeInfo);
            return;
        }
        if (iOrdinal == 2) {
            uidCpuRuntimeInfo.onBgTime += jCurrentTimeMillis;
            uidCpuRuntimeInfo.onBgTotalRuntime += totalCoreTime;
            uidCpuRuntimeInfo.onBgCoreTime.addDiff(coreTimeInfo);
        } else if (iOrdinal == 3) {
            uidCpuRuntimeInfo.offFgTime += jCurrentTimeMillis;
            uidCpuRuntimeInfo.offFgTotalRuntime += totalCoreTime;
            uidCpuRuntimeInfo.offFgCoreTime.addDiff(coreTimeInfo);
        } else {
            if (iOrdinal != 4) {
                return;
            }
            uidCpuRuntimeInfo.offBgTime += jCurrentTimeMillis;
            uidCpuRuntimeInfo.offBgTotalRuntime += totalCoreTime;
            uidCpuRuntimeInfo.offBgCoreTime.addDiff(coreTimeInfo);
        }
    }

    private CoreTimeInfo calculateCoreTimeDiff(CoreTimeInfo coreTimeInfo, CoreTimeInfo coreTimeInfo2) {
        if (coreTimeInfo == null || coreTimeInfo2 == null) {
            return null;
        }
        CoreTimeInfo coreTimeInfo3 = new CoreTimeInfo();
        coreTimeInfo3.supCoreTime = coreTimeInfo.supCoreTime - coreTimeInfo2.supCoreTime;
        coreTimeInfo3.bigCoreTime = coreTimeInfo.bigCoreTime - coreTimeInfo2.bigCoreTime;
        coreTimeInfo3.litCoreTime = coreTimeInfo.litCoreTime - coreTimeInfo2.litCoreTime;
        return coreTimeInfo3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelAlarm() {
        ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(this.mContext, 0, new Intent("millet_cpuruntime"), 201326592));
        this.delayTime -= SystemClock.elapsedRealtime() - this.mLastStart;
    }

    private long[] cyclesToNormalCoreTimes(long[] jArr) {
        if (jArr == null || jArr.length != CpuCoreInfo.cpuCapacity.length) {
            return null;
        }
        long[] jArr2 = new long[3];
        int i2 = 0;
        while (i2 < jArr.length) {
            if (jArr.length <= 3) {
                jArr2[i2] = (long) ((jArr[i2] / CpuCoreInfo.cpuCoreMaxFreq[i2]) * (((double) CpuCoreInfo.cpuCapacity[i2]) / 1024.0d));
            } else if (i2 == 0 || i2 == jArr.length - 1) {
                jArr2[i2 == 0 ? (char) 0 : (char) 2] = (long) ((jArr[i2] / CpuCoreInfo.cpuCoreMaxFreq[i2]) * (((double) CpuCoreInfo.cpuCapacity[i2]) / 1024.0d));
            } else {
                jArr2[1] = jArr2[1] + ((long) ((jArr[i2] / CpuCoreInfo.cpuCoreMaxFreq[i2]) * (((double) CpuCoreInfo.cpuCapacity[i2]) / 1024.0d)));
            }
            i2++;
        }
        return jArr2;
    }

    private long[] freqTimesToCycles(long[] jArr) {
        long[] jArr2;
        if (jArr == null || (jArr2 = CpuCoreInfo.cpuFreqs) == null || jArr2.length != jArr.length) {
            return null;
        }
        long[] jArr3 = new long[CpuCoreInfo.coreNum];
        for (int i2 = 0; i2 < CpuCoreInfo.coreNum; i2++) {
            int i3 = 0;
            for (int i4 = 0; i4 < i2; i4++) {
                i3 += CpuCoreInfo.cpuCoreAvailableLength[i4];
            }
            int i5 = 0;
            for (int i6 = 0; i6 <= i2; i6++) {
                i5 += CpuCoreInfo.cpuCoreAvailableLength[i6];
            }
            while (i3 < i5) {
                jArr3[i2] = jArr3[i2] + (jArr[i3] * CpuCoreInfo.cpuFreqs[i3]);
                i3++;
            }
        }
        return jArr3;
    }

    /* JADX WARN: Code duplicated, block: B:44:0x0097 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:56:0x00a2 A[SYNTHETIC] */
    private int[] getCpuCapacity(String[] strArr) throws Throwable {
        StringBuilder sb;
        int[] iArr = new int[strArr.length];
        for (int i2 = 0; i2 < strArr.length; i2++) {
            BufferedReader bufferedReader = null;
            try {
                try {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(CPU_CAPACITY_PATH_PREFIX);
                    String str = strArr[i2];
                    sb2.append(str.charAt(str.length() - 1));
                    sb2.append("/cpu_capacity");
                    File file = new File(sb2.toString());
                    if (!file.exists()) {
                        Slog.i("CountUidCpuRuntime", "file not exist");
                        return null;
                    }
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    while (true) {
                        try {
                            String line = bufferedReader2.readLine();
                            if (line != null) {
                                iArr[i2] = Integer.parseInt(line.trim());
                            } else {
                                try {
                                    break;
                                } catch (IOException e2) {
                                    e = e2;
                                    sb = new StringBuilder();
                                    sb.append("BufferedReader close erro");
                                    sb.append(e);
                                    Slog.i("CountUidCpuRuntime", sb.toString());
                                }
                            }
                        } catch (Exception e3) {
                            e = e3;
                            bufferedReader = bufferedReader2;
                            Slog.i("CountUidCpuRuntime", "get Cpu Capacity Err " + e);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e4) {
                                    e = e4;
                                    sb = new StringBuilder();
                                    sb.append("BufferedReader close erro");
                                    sb.append(e);
                                    Slog.i("CountUidCpuRuntime", sb.toString());
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader = bufferedReader2;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e5) {
                                    Slog.i("CountUidCpuRuntime", "BufferedReader close erro" + e5);
                                }
                            }
                            throw th;
                        }
                    }
                    bufferedReader2.close();
                } catch (Exception e6) {
                    e = e6;
                }
            } catch (Throwable th2) {
                th = th2;
            }
            Slog.i("CountUidCpuRuntime", "get Cpu Capacity Err " + e);
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return iArr;
    }

    private long[] getCpuFreqTimes(int i2) {
        try {
            if (this.mIBatteryStats == null) {
                this.mIBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
            }
            IBatteryStats iBatteryStats = this.mIBatteryStats;
            if (iBatteryStats == null) {
                return null;
            }
            return iBatteryStats.getCpuFreqTime(i2);
        } catch (RemoteException e2) {
            Slog.i("CountUidCpuRuntime", "getCpuFreqTimes exception: " + e2.getMessage());
            return null;
        }
    }

    private long[] getCpuFreqs() {
        try {
            if (this.mIBatteryStats == null) {
                this.mIBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
            }
            IBatteryStats iBatteryStats = this.mIBatteryStats;
            if (iBatteryStats == null) {
                return null;
            }
            return iBatteryStats.getCpuFreqs();
        } catch (RemoteException e2) {
            Slog.i("CountUidCpuRuntime", "getCpuFreqs exception: " + e2.getMessage());
            return null;
        }
    }

    private void identifyCpuCoreInfo() {
        int i2 = 0;
        int i3 = 0;
        int i4 = 1;
        while (true) {
            long[] jArr = CpuCoreInfo.cpuFreqs;
            if (i4 >= jArr.length) {
                CpuCoreInfo.cpuCoreAvailableLength[i2] = jArr.length - i3;
                CpuCoreInfo.cpuCoreMaxFreq[i2] = jArr[jArr.length - 1];
                Slog.i("CountUidCpuRuntime", "CpuFreqs=" + Arrays.toString(CpuCoreInfo.cpuFreqs) + " cpuCoreAvailableLength=" + Arrays.toString(CpuCoreInfo.cpuCoreAvailableLength) + " CpuCoreInfo.cpuCoreMaxFreq=" + Arrays.toString(CpuCoreInfo.cpuCoreMaxFreq));
                return;
            }
            long j2 = jArr[i4];
            long j3 = jArr[i4 - 1];
            if (j2 < j3 && i2 < CpuCoreInfo.coreNum) {
                CpuCoreInfo.cpuCoreAvailableLength[i2] = i4 - i3;
                CpuCoreInfo.cpuCoreMaxFreq[i2] = j3;
                i2++;
                i3 = i4;
            }
            i4++;
        }
    }

    private boolean initCpuCoreInfo() throws Throwable {
        String[] cpuCluster = CpuStatus.getCpuCluster();
        if (cpuCluster == null) {
            return false;
        }
        int length = cpuCluster.length;
        CpuCoreInfo.coreNum = length;
        CpuCoreInfo.cpuCoreMaxFreq = new long[length];
        CpuCoreInfo.cpuCoreAvailableLength = new int[length];
        int[] cpuCapacity = getCpuCapacity(cpuCluster);
        CpuCoreInfo.cpuCapacity = cpuCapacity;
        return cpuCapacity.length == cpuCluster.length;
    }

    private boolean initCpuStatisticsMode() {
        String str = Build.HARDWARE;
        Slog.w("CountUidCpuRuntime", "platform=" + str);
        if (str.matches("qcom") && this.mSupportWaltWayDeviceList.contains(Build.DEVICE)) {
            this.isSupportWaltMode = true;
        }
        if (!this.isSupportWaltMode && this.mSupportBpfWayDeviceList.contains(Build.DEVICE)) {
            long[] cpuFreqs = getCpuFreqs();
            CpuCoreInfo.cpuFreqs = cpuFreqs;
            if (cpuFreqs != null && initCpuCoreInfo()) {
                this.isSupportBpfMode = true;
            }
        }
        boolean z2 = this.isSupportWaltMode;
        if (z2 || this.isSupportBpfMode) {
            Slog.i("CountUidCpuRuntime", z2 ? "Walt Mode" : "Bpf Mode");
            return true;
        }
        Slog.w("CountUidCpuRuntime", "not support cpu statistics");
        return false;
    }

    private void initHandler() {
        this.mHandler = new Handler(BackgroundThread.getHandler().getLooper()) { // from class: com.miui.powerkeeper.millet.CountUidCpuRuntime.1
            @Override // android.os.Handler
            public void handleMessage(Message message) throws Throwable {
                int i2 = message.what;
                if (i2 == 1 || i2 == 2) {
                    CountUidCpuRuntime.this.updateUidCpuRuntimeInfo();
                } else {
                    if (i2 != 3) {
                        return;
                    }
                    CountUidCpuRuntime.this.dealMessageUpload();
                    CountUidCpuRuntime countUidCpuRuntime = CountUidCpuRuntime.this;
                    countUidCpuRuntime.delayTime = countUidCpuRuntime.DELTA_TIME;
                    CountUidCpuRuntime.this.startAlarm();
                }
            }
        };
    }

    private void initProcessObserver() {
        this.mProcessObserver = new IProcessObserver.Stub() { // from class: com.miui.powerkeeper.millet.CountUidCpuRuntime.2
            public void onForegroundActivitiesChanged(int i2, int i3, boolean z2) {
                if (CountUidCpuRuntime.this.supportBpfMode() && i3 != 1000 && !CountUidCpuRuntime.this.mPackageMap.containsKey(Integer.valueOf(i3)) && !CountUidCpuRuntime.this.mNoRestrictAppUids.contains(Integer.valueOf(i3)) && MilletUtils.isAppUid(i3)) {
                    UidCpuRuntimeInfo uidCpuRuntimeInfo = new UidCpuRuntimeInfo();
                    uidCpuRuntimeInfo.uid = i3;
                    String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i3);
                    if (pkgNameByUid == null) {
                        return;
                    }
                    uidCpuRuntimeInfo.pkgName = pkgNameByUid;
                    uidCpuRuntimeInfo.systemApp = MilletUtils.isSystemApp(CountUidCpuRuntime.this.mContext, i3) ? 1 : 0;
                    synchronized (CountUidCpuRuntime.this.mPackageMap) {
                        CountUidCpuRuntime.this.mPackageMap.put(Integer.valueOf(i3), uidCpuRuntimeInfo);
                    }
                }
                if (CountUidCpuRuntime.this.screenOn) {
                    if (CountUidCpuRuntime.this.mPackageMap.containsKey(Integer.valueOf(i3)) && ((UidCpuRuntimeInfo) CountUidCpuRuntime.this.mPackageMap.get(Integer.valueOf(i3))).pkgName.equals("com.miui.home")) {
                        return;
                    }
                    if (z2) {
                        synchronized (CountUidCpuRuntime.this.mFgAppUidList) {
                            try {
                                List arrayList = (List) CountUidCpuRuntime.this.mFgAppUidList.get(Integer.valueOf(i3));
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                    CountUidCpuRuntime.this.mFgAppUidList.put(Integer.valueOf(i3), arrayList);
                                }
                                arrayList.add(Integer.valueOf(i2));
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    } else {
                        synchronized (CountUidCpuRuntime.this.mFgAppUidList) {
                            CountUidCpuRuntime.this.mFgAppUidList.remove(Integer.valueOf(i3));
                        }
                    }
                    CountUidCpuRuntime.this.mHandler.removeMessages(1);
                    CountUidCpuRuntime.this.mHandler.removeMessages(2);
                    CountUidCpuRuntime.this.mHandler.sendMessage(CountUidCpuRuntime.this.mHandler.obtainMessage(1));
                }
            }

            public void onProcessDied(int i2, int i3) {
                synchronized (CountUidCpuRuntime.this.mFgAppUidList) {
                    try {
                        List list = (List) CountUidCpuRuntime.this.mFgAppUidList.get(Integer.valueOf(i3));
                        if (list != null) {
                            list.remove(Integer.valueOf(i2));
                            if (list.size() == 0) {
                                CountUidCpuRuntime.this.mFgAppUidList.remove(Integer.valueOf(i3));
                                CountUidCpuRuntime.this.mHandler.removeMessages(1);
                                CountUidCpuRuntime.this.mHandler.removeMessages(2);
                                CountUidCpuRuntime.this.mHandler.sendMessage(CountUidCpuRuntime.this.mHandler.obtainMessage(1));
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            public void onForegroundServicesChanged(int i2, int i3, int i4) {
            }

            public void onProcessStarted(int i2, int i3, int i4, String str, String str2) {
            }
        };
        try {
            ActivityManager.getService().registerProcessObserver(this.mProcessObserver);
        } catch (RemoteException e2) {
            Slog.i("CountUidCpuRuntime", "registerProcessObserver exception: " + e2.getMessage());
        }
    }

    private boolean isCurCpuRuntimeInclude(int i2) {
        return (this.mCharging || MilletUtils.getDownload(i2, null) || PowerKeeperManager.getInstance().getActiveController().isAppActive(i2) || (BluetoothEvent.getInstance(null, null) != null && BluetoothEvent.getInstance(null, null).isBluetoothActive(i2)) || PowerKeeperManager.getInstance().getProcessObserver().isUidForegroundService(i2)) ? false : true;
    }

    private boolean isUidCpuRuntimeInfoValid(UidCpuRuntimeInfo uidCpuRuntimeInfo) {
        return uidCpuRuntimeInfo != null && uidCpuRuntimeInfo.offBgTime >= 180000 && uidCpuRuntimeInfo.offBgTotalRuntime >= 0 && uidCpuRuntimeInfo.offBgCoreTime.supCoreTime >= 0 && uidCpuRuntimeInfo.onBgTime >= 180000 && uidCpuRuntimeInfo.onBgTotalRuntime >= 0 && uidCpuRuntimeInfo.onBgCoreTime.supCoreTime >= 0;
    }

    private JSONObject makeJson() {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            synchronized (this.mPackageMap) {
                try {
                    Iterator<Integer> it = this.mPackageMap.keySet().iterator();
                    while (it.hasNext()) {
                        UidCpuRuntimeInfo uidCpuRuntimeInfo = this.mPackageMap.get(it.next());
                        if (isUidCpuRuntimeInfoValid(uidCpuRuntimeInfo)) {
                            JSONObject jSONObject2 = new JSONObject();
                            jSONObject2.put("packageName", uidCpuRuntimeInfo.pkgName);
                            jSONObject2.put("screenOnBgTime", uidCpuRuntimeInfo.onBgTime);
                            jSONObject2.put("screenOffBgTime", uidCpuRuntimeInfo.offBgTime);
                            jSONObject2.put("screenOnBgTotalRuntime", uidCpuRuntimeInfo.onBgTotalRuntime);
                            jSONObject2.put("screenOffBgTotalRuntime", uidCpuRuntimeInfo.offBgTotalRuntime);
                            jSONObject2.put("screenOffBgSuperCoreTime", uidCpuRuntimeInfo.offBgCoreTime.supCoreTime);
                            jSONObject2.put("screenOnBgSuperCoreTime", uidCpuRuntimeInfo.onBgCoreTime.supCoreTime);
                            jSONObject2.put("screenOffBgMidCoreTime", uidCpuRuntimeInfo.offBgCoreTime.bigCoreTime);
                            jSONObject2.put("screenOnBgMidCoreTime", uidCpuRuntimeInfo.onBgCoreTime.bigCoreTime);
                            jSONArray.put(jSONObject2);
                            Log.d("CountUidCpuRuntime", "makejson: " + uidCpuRuntimeInfo.toString());
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            jSONObject.put("CountUidCpuRuntime", jSONArray);
            return jSONObject;
        } catch (Exception e2) {
            Log.e("CountUidCpuRuntime", "makeJson error", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAlarm() {
        this.mLastStart = SystemClock.elapsedRealtime();
        ((AlarmManager) this.mContext.getSystemService("alarm")).setExact(2, SystemClock.elapsedRealtime() + this.delayTime, PendingIntent.getBroadcast(this.mContext, 0, new Intent("millet_cpuruntime"), 201326592));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean supportBpfMode() {
        return this.isSupportBpfMode;
    }

    private boolean supportWaltMode() {
        return this.isSupportWaltMode;
    }

    private void updateAndRemoveNoRestrictApp() {
        List<String> noRestrictApps = UserConfigureHelper.getNoRestrictApps(this.mContext);
        if (noRestrictApps == null || noRestrictApps.size() == 0) {
            return;
        }
        this.mNoRestrictAppUids.clear();
        Iterator<String> it = noRestrictApps.iterator();
        while (it.hasNext()) {
            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, it.next());
            this.mNoRestrictAppUids.add(Integer.valueOf(uidByPackageName));
            if (this.mPackageMap.containsKey(Integer.valueOf(uidByPackageName))) {
                synchronized (this.mPackageMap) {
                    this.mPackageMap.remove(Integer.valueOf(uidByPackageName));
                }
            }
        }
    }

    private void updateSingleUidCpuRuntime(String str) {
        String[] strArrSplit;
        UidCpuRuntimeInfo uidCpuRuntimeInfo;
        if (!Pattern.compile("<\\S\\d*>").matcher(str).find() || (strArrSplit = Pattern.compile("<").split(str)) == null) {
            return;
        }
        String str2 = strArrSplit[0];
        int i2 = Integer.parseInt(str2.substring(0, str2.indexOf(":")).trim());
        if (this.mNoRestrictAppUids.contains(Integer.valueOf(i2))) {
            return;
        }
        int length = strArrSplit.length;
        CoreTimeInfo coreTimeInfo = new CoreTimeInfo();
        for (int i3 = 1; i3 < length; i3++) {
            if (this.DEBUG) {
                Slog.d("CountUidCpuRuntime", strArrSplit[i3]);
            }
            if ('s' == strArrSplit[i3].charAt(0)) {
                long j2 = coreTimeInfo.supCoreTime;
                String str3 = strArrSplit[i3];
                coreTimeInfo.supCoreTime = j2 + (Long.parseLong(str3.substring(str3.indexOf(" "), strArrSplit[i3].indexOf("[")).trim()) / DfsUtils.SECOND_TO_MICROS);
            } else if ('b' == strArrSplit[i3].charAt(0)) {
                long j3 = coreTimeInfo.bigCoreTime;
                String str4 = strArrSplit[i3];
                coreTimeInfo.bigCoreTime = j3 + (Long.parseLong(str4.substring(str4.indexOf(" "), strArrSplit[i3].indexOf("[")).trim()) / DfsUtils.SECOND_TO_MICROS);
            } else if ('l' == strArrSplit[i3].charAt(0)) {
                long j4 = coreTimeInfo.litCoreTime;
                String str5 = strArrSplit[i3];
                coreTimeInfo.litCoreTime = j4 + (Long.parseLong(str5.substring(str5.indexOf(" "), strArrSplit[i3].indexOf("[")).trim()) / DfsUtils.SECOND_TO_MICROS);
            }
        }
        if (MilletUtils.isAppUid(i2)) {
            if (this.mPackageMap.containsKey(Integer.valueOf(i2))) {
                uidCpuRuntimeInfo = this.mPackageMap.get(Integer.valueOf(i2));
                if (uidCpuRuntimeInfo.isInclue) {
                    if (uidCpuRuntimeInfo.systemApp == -1) {
                        uidCpuRuntimeInfo.systemApp = MilletUtils.isSystemApp(this.mContext, i2) ? 1 : 0;
                    }
                    if (uidCpuRuntimeInfo.pkgName == null) {
                        String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i2);
                        if (pkgNameByUid == null) {
                            return;
                        } else {
                            uidCpuRuntimeInfo.pkgName = pkgNameByUid;
                        }
                    }
                    addDiffToUidCpuRuntimeInfo(calculateCoreTimeDiff(coreTimeInfo, uidCpuRuntimeInfo.lastCoreTime), uidCpuRuntimeInfo);
                }
            } else {
                uidCpuRuntimeInfo = new UidCpuRuntimeInfo();
                uidCpuRuntimeInfo.uid = i2;
                String pkgNameByUid2 = MilletUidObserver.getPkgNameByUid(i2);
                if (pkgNameByUid2 == null) {
                    return;
                }
                uidCpuRuntimeInfo.pkgName = pkgNameByUid2;
                uidCpuRuntimeInfo.systemApp = MilletUtils.isSystemApp(this.mContext, i2) ? 1 : 0;
                uidCpuRuntimeInfo.initCoreTime = coreTimeInfo;
                synchronized (this.mPackageMap) {
                    this.mPackageMap.put(Integer.valueOf(i2), uidCpuRuntimeInfo);
                }
            }
            uidCpuRuntimeInfo.isInclue = isCurCpuRuntimeInclude(i2);
            uidCpuRuntimeInfo.lastTime = System.currentTimeMillis();
            uidCpuRuntimeInfo.lastCoreTime = coreTimeInfo;
        }
    }

    private void updateUidCpuRuntimeBpfMode() {
        updateAndRemoveNoRestrictApp();
        synchronized (this.mPackageMap) {
            try {
                ArrayList<Integer> arrayList = null;
                for (Map.Entry<Integer, UidCpuRuntimeInfo> entry : this.mPackageMap.entrySet()) {
                    Integer key = entry.getKey();
                    int iIntValue = key.intValue();
                    long[] cpuFreqTimes = getCpuFreqTimes(iIntValue);
                    if (cpuFreqTimes == null) {
                        if (this.DEBUG) {
                            Slog.i("CountUidCpuRuntime", "curBpfFreqTime = null!");
                        }
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(key);
                    } else {
                        CoreTimeInfo coreTimeInfo = new CoreTimeInfo(cyclesToNormalCoreTimes(freqTimesToCycles(cpuFreqTimes)));
                        UidCpuRuntimeInfo value = entry.getValue();
                        if (MilletUtils.isAppUid(iIntValue)) {
                            if (value.initCoreTime == null) {
                                value.initCoreTime = coreTimeInfo;
                            } else if (value.isInclue) {
                                if (value.systemApp == -1) {
                                    value.systemApp = MilletUtils.isSystemApp(this.mContext, iIntValue) ? 1 : 0;
                                }
                                if (value.pkgName == null) {
                                    String pkgNameByUid = MilletUidObserver.getPkgNameByUid(iIntValue);
                                    if (pkgNameByUid == null) {
                                        return;
                                    } else {
                                        value.pkgName = pkgNameByUid;
                                    }
                                }
                                addDiffToUidCpuRuntimeInfo(calculateCoreTimeDiff(coreTimeInfo, value.lastCoreTime), value);
                            }
                            value.isInclue = isCurCpuRuntimeInclude(iIntValue);
                            value.lastTime = System.currentTimeMillis();
                            value.lastCoreTime = coreTimeInfo;
                            if (this.DEBUG) {
                                Slog.i("CountUidCpuRuntime", value.toString());
                            }
                        } else {
                            continue;
                        }
                    }
                }
                if (arrayList != null && arrayList.size() != 0) {
                    for (Integer num : arrayList) {
                        num.intValue();
                        this.mPackageMap.remove(num);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUidCpuRuntimeInfo() throws Throwable {
        if (supportWaltMode()) {
            updateUidCpuRuntimeWaltMode();
        }
        if (supportBpfMode()) {
            updateUidCpuRuntimeBpfMode();
        }
        updateUidState();
    }

    private void updateUidCpuRuntimeWaltMode() throws Throwable {
        StringBuilder sb;
        BufferedReader bufferedReader = null;
        try {
            try {
                File file = new File(PKG_RUNTIME_INFO_TOTAL_USAGE_PATH);
                if (!file.exists()) {
                    Slog.i("CountUidCpuRuntime", "file not exist");
                    return;
                }
                updateAndRemoveNoRestrictApp();
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                while (true) {
                    try {
                        String line = bufferedReader2.readLine();
                        if (line != null) {
                            updateSingleUidCpuRuntime(line);
                        } else {
                            try {
                                bufferedReader2.close();
                                return;
                            } catch (IOException e2) {
                                e = e2;
                                sb = new StringBuilder();
                            }
                        }
                    } catch (Exception e3) {
                        e = e3;
                        bufferedReader = bufferedReader2;
                        Slog.i("CountUidCpuRuntime", "updateAllUidsCpuRuntime " + e);
                        if (bufferedReader == null) {
                            return;
                        }
                        try {
                            bufferedReader.close();
                            return;
                        } catch (IOException e4) {
                            e = e4;
                            sb = new StringBuilder();
                        }
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e5) {
                                Slog.i("CountUidCpuRuntime", "BufferedReader close erro" + e5);
                            }
                        }
                        throw th;
                    }
                    sb.append("BufferedReader close erro");
                    sb.append(e);
                    Slog.i("CountUidCpuRuntime", sb.toString());
                    return;
                }
            } catch (Exception e6) {
                e = e6;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUidState() {
        if (this.mActivityManager == null) {
            this.mActivityManager = (ActivityManager) this.mContext.getSystemService("activity");
        }
        if (this.mActivityManager != null) {
            HashSet<Integer> hashSet = new HashSet();
            try {
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : this.mActivityManager.getRunningAppProcesses()) {
                    if (runningAppProcessInfo != null) {
                        hashSet.add(Integer.valueOf(runningAppProcessInfo.uid));
                    }
                }
            } catch (Exception e2) {
                Log.e("CountUidCpuRuntime", "getRunningApp", e2);
            }
            for (Integer num : hashSet) {
                num.intValue();
                if (this.mPackageMap.containsKey(num)) {
                    if (this.mCharging) {
                        this.mPackageMap.get(num).lastState = UidState.CHARGING;
                    } else if (this.screenOn) {
                        synchronized (this.mFgAppUidList) {
                            try {
                                if (this.mFgAppUidList.containsKey(num)) {
                                    this.mPackageMap.get(num).lastState = UidState.SCREEN_ON_IN_FG;
                                } else {
                                    this.mPackageMap.get(num).lastState = UidState.SCREEN_ON_IN_BG;
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    } else {
                        synchronized (this.mFgAppUidList) {
                            try {
                                if (this.mFgAppUidList.containsKey(num)) {
                                    this.mPackageMap.get(num).lastState = UidState.SCREEN_OFF_IN_FG;
                                } else {
                                    this.mPackageMap.get(num).lastState = UidState.SCREEN_OFF_IN_BG;
                                }
                            } catch (Throwable th2) {
                                throw th2;
                            }
                        }
                    }
                }
            }
            synchronized (this.mPackageMap) {
                try {
                    for (Map.Entry<Integer, UidCpuRuntimeInfo> entry : this.mPackageMap.entrySet()) {
                        if (!hashSet.contains(entry.getKey())) {
                            entry.getValue().lastState = UidState.NOT_RUN;
                        }
                    }
                } catch (Throwable th3) {
                    throw th3;
                }
            }
        }
    }

    public void dealMessageUpload() throws Throwable {
        updateUidCpuRuntimeInfo();
        JSONObject jSONObjectMakeJson = makeJson();
        if (jSONObjectMakeJson == null) {
            Log.d("CountUidCpuRuntime", "json null error");
            return;
        }
        TrackerManager.getInstance(this.mContext).track(3, TrackerConfig.EVENT_APP_CPU_RUNTIME_AND_MEM_TIME, jSONObjectMakeJson);
        synchronized (this.mPackageMap) {
            this.mPackageMap.clear();
        }
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(1));
    }

    public class CoreTimeInfo {
        long bigCoreTime;
        long litCoreTime;
        long supCoreTime;

        CoreTimeInfo() {
            this.supCoreTime = 0L;
            this.bigCoreTime = 0L;
            this.litCoreTime = 0L;
        }

        public void addDiff(CoreTimeInfo coreTimeInfo) {
            this.litCoreTime += coreTimeInfo.litCoreTime;
            this.bigCoreTime += coreTimeInfo.bigCoreTime;
            this.supCoreTime += coreTimeInfo.supCoreTime;
        }

        public boolean checkNeedAdd() {
            return (this.litCoreTime == 0 && this.bigCoreTime == 0 && this.supCoreTime == 0) ? false : true;
        }

        public long getTotalCoreTime() {
            return this.litCoreTime + this.bigCoreTime + this.supCoreTime;
        }

        public String toString() {
            return "[" + this.litCoreTime + ", " + this.bigCoreTime + ", " + this.supCoreTime + "]";
        }

        CoreTimeInfo(long[] jArr) {
            this.supCoreTime = 0L;
            this.bigCoreTime = 0L;
            this.litCoreTime = 0L;
            if (jArr != null) {
                this.litCoreTime = jArr[0];
                this.bigCoreTime = jArr[1];
                this.supCoreTime = jArr[2];
            }
        }
    }
}
