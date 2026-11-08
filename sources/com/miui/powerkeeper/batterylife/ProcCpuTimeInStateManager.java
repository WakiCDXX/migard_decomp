package com.miui.powerkeeper.batterylife;

import android.annotation.NonNull;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.data.CpuTimeStatsData;
import com.miui.powerkeeper.batterylife.systeminfo.CpuStatus;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import com.xiaomi.push.service.PushServiceConstants;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.ToLongFunction;
import miui.os.Build;
import miui.process.ForegroundInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ProcCpuTimeInStateManager {
    private static final long DELAY_TIME_MS = 60000;
    public static final String TAG = "CpuTimeInStateManager";
    private static final String WS_REMOTE_EXCEPTION = "IWhetstoneActivityManager remote exception: ";
    private boolean isSupprotAllPkgs;
    private boolean mAppCpuReseted;
    private Context mContext;
    private ForegroundInfo mCurrentInfo;
    private boolean mDeviceCpuReseted;
    private boolean mDeviceGpuReseted;
    private Handler mHandler;
    private long[] mInitGpuTimes;
    private long[] mLastDeviceCpuTimes;
    private long[] mLastGpuTimes;
    private int[] mLastTimes;
    private long mStartTime;
    private String[] mSupportPkgs;
    private int mTotalFreqs;
    private int mTotalGpus;
    private IWhetstoneActivityManager mWs;
    private ForegroundInfo mLastFgInfo = null;
    private ArrayMap<String, UidTimeStats> mCpuTimeStatsMap = new ArrayMap<>();
    private Runnable mStatsRunnable = new Runnable() { // from class: com.miui.powerkeeper.batterylife.b
        @Override // java.lang.Runnable
        public final void run() {
            this.f372a.lambda$new$1();
        }
    };

    static class TimeInStateHelper {
        private TimeInStateHelper() {
        }

        static int[] addTimes(int[] iArr, int[] iArr2) {
            if (iArr == null || iArr.length == 0) {
                return iArr2;
            }
            if (iArr2 == null || iArr2.length == 0 || iArr.length != iArr2.length) {
                return iArr;
            }
            for (int i2 = 0; i2 < iArr.length; i2++) {
                iArr[i2] = iArr[i2] + iArr2[i2];
            }
            return iArr;
        }

        static long[] stringToTimes(String str) {
            return (str == null || str.length() == 0) ? new long[0] : Arrays.asList(str.trim().split(" ")).stream().mapToLong(new ToLongFunction() { // from class: com.miui.powerkeeper.batterylife.d
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    return Long.parseLong((String) obj);
                }
            }).toArray();
        }

        static String timesToString(int[] iArr) {
            if (iArr == null || iArr.length == 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (int i2 : iArr) {
                sb.append(i2);
                sb.append(" ");
            }
            return sb.toString();
        }
    }

    static class UidTimeStats {
        public int[] mAppCpuTimeStats;
        public long[] mDeviceCpuTimeStats;
        public long mFgTime;
        public long[] mGpuTimeStats;

        public UidTimeStats(int i2, int i3) {
            this.mAppCpuTimeStats = new int[i2];
            this.mDeviceCpuTimeStats = new long[i2];
            this.mGpuTimeStats = new long[i3];
        }
    }

    ProcCpuTimeInStateManager(Context context) throws Throwable {
        this.mContext = context;
        IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        this.mWs = iWhetstoneActivityManagerAsInterface;
        if (iWhetstoneActivityManagerAsInterface == null) {
            Log.d(TAG, "mWs interface error.");
            return;
        }
        Iterator<String[]> it = CpuStatus.mAvailableFrequencies.iterator();
        while (it.hasNext()) {
            this.mTotalFreqs += it.next().length;
        }
        String[] availableGpuFreqs = CpuStatus.getAvailableGpuFreqs();
        if (availableGpuFreqs != null) {
            this.mTotalGpus = availableGpuFreqs.length;
            this.mInitGpuTimes = CpuStatus.readGpuTimeStats();
        } else {
            this.mTotalGpus = 0;
        }
        this.isSupprotAllPkgs = false;
        String string = SimpleSettings.Misc.getString(this.mContext, BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE, "");
        if (string.length() <= 0) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            if (!jSONObject.has("pkgList_all")) {
                String strOptString = Build.IS_INTERNATIONAL_BUILD ? jSONObject.optString("pkgList_intl") : jSONObject.optString("pkgList");
                if (strOptString.length() > 0) {
                    if ("none".equalsIgnoreCase(strOptString)) {
                        this.mSupportPkgs = null;
                    } else {
                        this.mSupportPkgs = strOptString.split(";");
                    }
                }
            } else if (PushServiceConstants.EXTENSION_ELE_NAME_ALL.equalsIgnoreCase(jSONObject.optString("pkgList_all"))) {
                this.mSupportPkgs = null;
                this.isSupprotAllPkgs = true;
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        this.mHandler = new Handler(BackgroundThread.getHandler().getLooper()) { // from class: com.miui.powerkeeper.batterylife.ProcCpuTimeInStateManager.1
            @Override // android.os.Handler
            public void handleMessage(@NonNull Message message) {
                ProcCpuTimeInStateManager.this.handleMessage(message);
            }
        };
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.registerForForegroundInfo(this.mHandler);
            eventsAggregator.registerForScreenOn(this.mHandler, null);
            eventsAggregator.registerForScreenOff(this.mHandler, null);
        }
    }

    private void calculate(UidTimeStats uidTimeStats) {
        if (uidTimeStats == null) {
            return;
        }
        try {
            int[] timeInStateByPid = this.mWs.getTimeInStateByPid(this.mLastFgInfo.mForegroundPid);
            long[] deviceTimeInState = CpuStatus.readDeviceTimeInState();
            long[] gpuTimeStats = CpuStatus.readGpuTimeStats();
            if (timeInStateByPid != null && deviceTimeInState != null) {
                int length = timeInStateByPid.length;
                int i2 = this.mTotalFreqs;
                if (length == i2 && deviceTimeInState.length == i2 && this.mLastTimes.length == i2 && this.mLastDeviceCpuTimes.length == i2) {
                    uidTimeStats.mFgTime += System.currentTimeMillis() - this.mStartTime;
                    for (int i3 = 0; i3 < this.mTotalFreqs; i3++) {
                        long j2 = timeInStateByPid[i3] - this.mLastTimes[i3];
                        if (j2 < 0) {
                            this.mAppCpuReseted = true;
                        }
                        long j3 = deviceTimeInState[i3] - this.mLastDeviceCpuTimes[i3];
                        if (j3 < 0) {
                            this.mDeviceCpuReseted = true;
                        }
                        if (!this.mAppCpuReseted) {
                            int[] iArr = uidTimeStats.mAppCpuTimeStats;
                            iArr[i3] = (int) (((long) iArr[i3]) + j2);
                        }
                        if (!this.mDeviceCpuReseted) {
                            long[] jArr = uidTimeStats.mDeviceCpuTimeStats;
                            jArr[i3] = jArr[i3] + j3;
                        }
                    }
                    int length2 = gpuTimeStats.length;
                    int i4 = this.mTotalGpus;
                    if (length2 == i4 && this.mLastGpuTimes.length == i4) {
                        for (int i5 = 0; i5 < this.mTotalGpus; i5++) {
                            long j4 = gpuTimeStats[i5] - this.mLastGpuTimes[i5];
                            if (j4 < 0) {
                                this.mDeviceGpuReseted = true;
                            }
                            if (!this.mDeviceCpuReseted) {
                                long[] jArr2 = uidTimeStats.mGpuTimeStats;
                                jArr2[i5] = jArr2[i5] + j4;
                            }
                        }
                    }
                }
            }
        } catch (RemoteException | NullPointerException e2) {
            Log.e(TAG, WS_REMOTE_EXCEPTION + e2.getMessage());
        }
    }

    private void fgChanged(ForegroundInfo foregroundInfo) {
        if (isInterfaceEnabled()) {
            try {
                if (isAppSupport(foregroundInfo.mForegroundPackageName)) {
                    if (!this.mCpuTimeStatsMap.containsKey(foregroundInfo.mForegroundPackageName)) {
                        this.mCpuTimeStatsMap.put(foregroundInfo.mForegroundPackageName, new UidTimeStats(this.mTotalFreqs, this.mTotalGpus));
                    }
                    if (android.os.Build.IS_DEBUGGABLE) {
                        Log.d(TAG, "support app:" + foregroundInfo.mForegroundPackageName);
                    }
                    this.mLastTimes = this.mWs.getTimeInStateByPid(foregroundInfo.mForegroundPid);
                    this.mLastDeviceCpuTimes = CpuStatus.readDeviceTimeInState();
                    this.mLastGpuTimes = CpuStatus.readGpuTimeStats();
                    this.mStartTime = System.currentTimeMillis();
                }
            } catch (RemoteException | NullPointerException e2) {
                Log.e(TAG, WS_REMOTE_EXCEPTION + e2.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -9) {
            this.mCurrentInfo = (ForegroundInfo) message.obj;
            boolean z2 = android.os.Build.IS_DEBUGGABLE;
            if (z2) {
                Log.d(TAG, "foreground pkg is " + this.mCurrentInfo.mForegroundPackageName);
            }
            ForegroundInfo foregroundInfo = this.mLastFgInfo;
            if (foregroundInfo != null) {
                if (foregroundInfo.mForegroundUid == this.mCurrentInfo.mForegroundUid) {
                    return;
                }
                if (System.currentTimeMillis() - this.mStartTime < DELAY_TIME_MS) {
                    this.mHandler.removeCallbacks(this.mStatsRunnable);
                    if (z2) {
                        Log.d(TAG, "cancel, running less than 1min");
                    }
                } else if (isAppSupport(this.mLastFgInfo.mForegroundPackageName)) {
                    if (z2) {
                        Log.d(TAG, "start static:" + this.mLastFgInfo.mForegroundPackageName);
                    }
                    calculate(this.mCpuTimeStatsMap.get(this.mLastFgInfo.mForegroundPackageName));
                }
            }
            this.mHandler.postDelayed(this.mStatsRunnable, DELAY_TIME_MS);
            this.mStartTime = System.currentTimeMillis();
            this.mLastFgInfo = this.mCurrentInfo;
            return;
        }
        if (i2 != -4) {
            if (i2 == -5) {
                ForegroundInfo foregroundInfo2 = this.mLastFgInfo;
                if (foregroundInfo2 != null && isAppSupport(foregroundInfo2.mForegroundPackageName) && System.currentTimeMillis() - this.mStartTime >= DELAY_TIME_MS) {
                    if (android.os.Build.IS_DEBUGGABLE) {
                        Log.d(TAG, "screen off, start static:" + this.mLastFgInfo.mForegroundPackageName);
                    }
                    calculate(this.mCpuTimeStatsMap.get(this.mLastFgInfo.mForegroundPackageName));
                }
                this.mLastFgInfo = null;
                return;
            }
            return;
        }
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            ForegroundInfo foregroundInfo3 = eventsAggregator.getForegroundInfo();
            this.mCurrentInfo = foregroundInfo3;
            if (android.os.Build.IS_DEBUGGABLE && foregroundInfo3 != null) {
                Log.d(TAG, "screen on, foreground pkg is " + this.mCurrentInfo.mForegroundPackageName);
            }
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler.postDelayed(this.mStatsRunnable, DELAY_TIME_MS);
            this.mStartTime = System.currentTimeMillis();
            this.mLastFgInfo = this.mCurrentInfo;
        }
    }

    private boolean isAppSupport(String str) {
        String[] strArr = this.mSupportPkgs;
        if (strArr != null) {
            for (String str2 : strArr) {
                if (str2.equals(str)) {
                    return true;
                }
            }
        } else if (this.isSupprotAllPkgs) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getProcCpuGpuTimes$0(String[] strArr, List list, String str, UidTimeStats uidTimeStats) {
        HashMap map;
        CpuTimeStatsData cpuTimeStatsData;
        HashMap map2;
        String str2;
        int i2;
        HashMap map3;
        HashMap map4;
        String str3;
        CpuTimeStatsData cpuTimeStatsData2 = new CpuTimeStatsData();
        cpuTimeStatsData2.blPkgName = str;
        cpuTimeStatsData2.appFgTime = uidTimeStats.mFgTime;
        HashMap map5 = new HashMap(16);
        HashMap map6 = new HashMap(16);
        HashMap map7 = new HashMap(4);
        HashMap map8 = new HashMap(4);
        HashMap map9 = new HashMap(4);
        int i3 = 0;
        int length = 0;
        while (i3 < CpuStatus.mAvailableFrequencies.size()) {
            String str4 = CpuTimeStatsData.APP_CLUSTER + i3;
            String str5 = CpuTimeStatsData.DEVICE_CLUSTER + i3;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            String[] strArr2 = CpuStatus.mAvailableFrequencies.get(i3);
            HashMap map10 = map9;
            if (this.mAppCpuReseted) {
                cpuTimeStatsData = cpuTimeStatsData2;
                map2 = map6;
                str2 = str5;
                i2 = i3;
            } else {
                int i4 = 0;
                long j2 = 0;
                while (i4 < strArr2.length) {
                    int i5 = length + i4;
                    arrayList.add(new CpuTimeStatsData.FreqStats(strArr2[i4], uidTimeStats.mAppCpuTimeStats[i5]));
                    j2 += (long) uidTimeStats.mAppCpuTimeStats[i5];
                    i4++;
                    cpuTimeStatsData2 = cpuTimeStatsData2;
                    i3 = i3;
                    map6 = map6;
                    str5 = str5;
                }
                cpuTimeStatsData = cpuTimeStatsData2;
                map2 = map6;
                str2 = str5;
                i2 = i3;
                map7.put(str4, Long.valueOf(j2));
            }
            if (this.mDeviceCpuReseted) {
                map3 = map5;
                map4 = map7;
                str3 = str2;
            } else {
                int i6 = 0;
                long j3 = 0;
                while (i6 < strArr2.length) {
                    int i7 = length + i6;
                    arrayList2.add(new CpuTimeStatsData.FreqStats(strArr2[i6], uidTimeStats.mDeviceCpuTimeStats[i7]));
                    j3 += uidTimeStats.mDeviceCpuTimeStats[i7];
                    i6++;
                    map5 = map5;
                    map7 = map7;
                }
                map3 = map5;
                map4 = map7;
                str3 = str2;
                map8.put(str3, Long.valueOf(j3));
            }
            length += strArr2.length;
            map3.put(str4, arrayList);
            HashMap map11 = map2;
            map11.put(str3, arrayList2);
            map6 = map11;
            map5 = map3;
            map9 = map10;
            map7 = map4;
            i3 = i2 + 1;
            cpuTimeStatsData2 = cpuTimeStatsData;
        }
        HashMap map12 = map9;
        cpuTimeStatsData2.appClusterMap = map5;
        cpuTimeStatsData2.deviceClusterMap = map6;
        cpuTimeStatsData2.appTotalMap = map7;
        cpuTimeStatsData2.deviceTotalMap = map8;
        if (strArr == null || strArr.length <= 0) {
            map = map12;
        } else {
            ArrayList arrayList3 = new ArrayList();
            if (this.mDeviceGpuReseted) {
                map = map12;
            } else {
                long j4 = 0;
                for (int i8 = 0; i8 < this.mTotalGpus; i8++) {
                    long[] jArr = uidTimeStats.mGpuTimeStats;
                    if (jArr != null) {
                        arrayList3.add(new CpuTimeStatsData.FreqStats(strArr[i8], jArr[i8]));
                        j4 += uidTimeStats.mGpuTimeStats[i8];
                    }
                }
                map = map12;
                map.put(CpuTimeStatsData.GPU_TOTAL, Long.valueOf(j4));
            }
            cpuTimeStatsData2.gpuTimeList = arrayList3;
        }
        cpuTimeStatsData2.gpuTotalMap = map;
        list.add(cpuTimeStatsData2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        fgChanged(this.mCurrentInfo);
    }

    void distroy() {
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

    void dump(PrintWriter printWriter) throws Throwable {
        if (this.mWs != null) {
            printWriter.println("mTotalFreqs = " + this.mTotalFreqs);
            printWriter.println("CpuFreqs = ");
            Iterator<String[]> it = CpuStatus.mAvailableFrequencies.iterator();
            while (it.hasNext()) {
                printWriter.println("  " + Arrays.deepToString(it.next()));
            }
            String[] availableGpuFreqs = CpuStatus.getAvailableGpuFreqs();
            if (availableGpuFreqs != null && availableGpuFreqs.length > 0) {
                printWriter.println("GpuFreqs = ");
                printWriter.println(Arrays.toString(availableGpuFreqs));
            }
            if (this.isSupprotAllPkgs || this.mSupportPkgs != null) {
                printWriter.println("CpuTimes for Pkgs:");
                for (int i2 = 0; i2 < this.mCpuTimeStatsMap.size(); i2++) {
                    String strKeyAt = this.mCpuTimeStatsMap.keyAt(i2);
                    UidTimeStats uidTimeStats = this.mCpuTimeStatsMap.get(strKeyAt);
                    if (uidTimeStats != null && uidTimeStats.mAppCpuTimeStats != null && uidTimeStats.mDeviceCpuTimeStats != null) {
                        printWriter.println(strKeyAt + ":");
                        printWriter.print("  app cpu: ");
                        printWriter.println(Arrays.toString(uidTimeStats.mAppCpuTimeStats));
                        printWriter.print("  device cpu: ");
                        printWriter.println(Arrays.toString(uidTimeStats.mDeviceCpuTimeStats));
                        printWriter.print("  device gpu: ");
                        printWriter.println(Arrays.toString(uidTimeStats.mGpuTimeStats));
                        printWriter.print("  fg_time: ");
                        printWriter.println(uidTimeStats.mFgTime);
                    }
                }
            }
        }
    }

    ArrayList<Object> getDeviceGpuTimes(String[] strArr) {
        ArrayList<Object> arrayList = new ArrayList<>();
        if (strArr != null && strArr.length > 0) {
            long[] gpuTimeStats = CpuStatus.readGpuTimeStats();
            long[] jArr = this.mInitGpuTimes;
            if (jArr != null && jArr.length == gpuTimeStats.length) {
                for (int i2 = 0; i2 < this.mTotalGpus; i2++) {
                    ArrayMap arrayMap = new ArrayMap();
                    arrayMap.put(TrackData.CPU_FREQ_TIME_IN_STATE_FREQ, strArr[i2]);
                    arrayMap.put("time", Long.valueOf(gpuTimeStats[i2] - this.mInitGpuTimes[i2]));
                    arrayList.add(arrayMap);
                }
            }
        }
        return arrayList;
    }

    List<CpuTimeStatsData> getProcCpuGpuTimes(final String[] strArr) {
        final ArrayList arrayList = new ArrayList();
        this.mCpuTimeStatsMap.forEach(new BiConsumer() { // from class: com.miui.powerkeeper.batterylife.c
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                this.f373a.lambda$getProcCpuGpuTimes$0(strArr, arrayList, (String) obj, (ProcCpuTimeInStateManager.UidTimeStats) obj2);
            }
        });
        return arrayList;
    }

    SparseArray<Map<String, Object>> getProcCpuTimes() {
        int[] iArr;
        SparseArray<Map<String, Object>> sparseArray = new SparseArray<>();
        if (!this.isSupprotAllPkgs && this.mSupportPkgs == null) {
            return sparseArray;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < this.mCpuTimeStatsMap.size(); i3++) {
            ArrayMap arrayMap = new ArrayMap();
            String strKeyAt = this.mCpuTimeStatsMap.keyAt(i3);
            UidTimeStats uidTimeStats = this.mCpuTimeStatsMap.get(strKeyAt);
            if (uidTimeStats != null && (iArr = uidTimeStats.mAppCpuTimeStats) != null && iArr.length != 0) {
                if (iArr.length != this.mTotalFreqs) {
                    Log.d(TAG, "Data is not matched. mTotalFreqs = " + this.mTotalFreqs);
                    Log.d(TAG, "times = " + TimeInStateHelper.timesToString(iArr));
                } else {
                    arrayMap.put(TrackData.BL_PKG_NAME, strKeyAt);
                    arrayMap.put(TrackData.BL_APP_NAME, BLUtils.getAppNameByPkg(this.mContext, strKeyAt));
                    arrayMap.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
                    arrayMap.put(TrackData.PROC_CT_FG_TIME, Long.valueOf(uidTimeStats.mFgTime / 1000));
                    int length = 0;
                    for (int i4 = 0; i4 < CpuStatus.mAvailableFrequencies.size(); i4++) {
                        ArrayList arrayList = new ArrayList();
                        for (int i5 = 0; i5 < CpuStatus.mAvailableFrequencies.get(i4).length; i5++) {
                            ArrayMap arrayMap2 = new ArrayMap();
                            arrayMap2.put(TrackData.CPU_FREQ_TIME_IN_STATE_FREQ, CpuStatus.mAvailableFrequencies.get(i4)[i5]);
                            arrayMap2.put("time", Integer.valueOf(iArr[length + i5]));
                            arrayList.add(arrayMap2);
                        }
                        length += CpuStatus.mAvailableFrequencies.get(i4).length;
                        arrayMap.put(TrackData.PROC_CT_CLUSTER + i4, arrayList);
                    }
                    sparseArray.append(i2, arrayMap);
                    i2++;
                }
            }
        }
        return sparseArray;
    }

    public String[] getSupportPkgs() {
        return this.mSupportPkgs;
    }

    boolean isInterfaceEnabled() {
        return this.mWs != null;
    }

    public void resetAllData() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (this.mInitGpuTimes != null) {
            this.mInitGpuTimes = CpuStatus.readGpuTimeStats();
        }
        this.mCpuTimeStatsMap.clear();
        this.mAppCpuReseted = false;
        this.mDeviceCpuReseted = false;
        this.mDeviceGpuReseted = false;
        this.mLastFgInfo = null;
    }

    public void setSupportPkgs(String[] strArr, boolean z2) {
        this.mSupportPkgs = strArr;
        this.isSupprotAllPkgs = z2;
    }
}
