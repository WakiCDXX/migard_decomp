package com.miui.powerkeeper.siming.shaosi;

import android.annotation.Nullable;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.siming.SiUtils;
import com.miui.powerkeeper.siming.dasi.MIY;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class AppUsageN extends N {
    public static final long DAY_TO_MILLIS = 86400000;
    public static final int MSG_INIT = 1;
    public static final String TAG = "SM_S_AppUsageN";
    private static final int TRACKING_TIME_MS;
    public static final int WRITE_FILE_THRESHOLD;
    public static final long _2_MONTH_TO_MILLIS = 5184000000L;
    public static final long _6_MONTH_TO_MILLIS = 15552000000L;
    private final Context mContext;
    private ForegroundInfo mFgInfo;
    private String mFileDir;
    Handler mHandler;
    private long mLastCheckTime;
    private final Object mLockAppUsageDeque;
    private TrackingInfo mTracking;
    private final HashMap<String, UsageHistory> mUsageMap;

    public static class AppUsage implements Serializable {
        int consumption;
        long startTimeMs;
        int timeS;
    }

    public static class TrackingInfo {
        int chargeCounter;
        String pkgName;
        long startTimeMs;
        int uid;
    }

    public static class UsageHistory implements Serializable {
        private static final long serialVersionUID = 1;
        String pkgName;
        int totalConsumption;
        int totalTimeS;
        int uid;
        ArrayDeque<Long> coldStartDeque = new ArrayDeque<>();
        ArrayDeque<AppUsage> appUsageDeque = new ArrayDeque<>();
    }

    static {
        boolean z2 = SiUtils.DEBUG;
        TRACKING_TIME_MS = z2 ? 10000 : 600000;
        WRITE_FILE_THRESHOLD = z2 ? 3 : 30;
    }

    protected AppUsageN(Looper looper) {
        super(looper, 104);
        this.mUsageMap = new HashMap<>();
        this.mTracking = null;
        this.mLastCheckTime = -1L;
        this.mFileDir = "/";
        this.mLockAppUsageDeque = new Object();
        this.mHandler = new Handler(BackgroundThread.getHandler().getLooper()) { // from class: com.miui.powerkeeper.siming.shaosi.AppUsageN.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i2 = message.what;
                if (i2 == -2) {
                    AppUsageN.this.deleteFile((String) message.obj);
                    AppUsageN.this.mUsageMap.remove((String) message.obj);
                } else {
                    if (i2 != 1) {
                        return;
                    }
                    AppUsageN.this.init();
                }
            }
        };
        this.mContext = PowerKeeperApplication.b();
        this.mHandler.sendEmptyMessage(1);
        MIY.register(this.SI, 2, 1, 0, 4);
    }

    private boolean checkAllHistoryIfNeed() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = this.mLastCheckTime;
        if (j2 != -1 && jCurrentTimeMillis - j2 <= 259200000) {
            return false;
        }
        this.mLastCheckTime = jCurrentTimeMillis;
        Iterator<Map.Entry<String, UsageHistory>> it = this.mUsageMap.entrySet().iterator();
        while (it.hasNext()) {
            String key = it.next().getKey();
            UsageHistory andMerge = readAndMerge(key);
            if (andMerge.coldStartDeque.size() > 100) {
                while (jCurrentTimeMillis - andMerge.coldStartDeque.getFirst().longValue() > _6_MONTH_TO_MILLIS) {
                    andMerge.coldStartDeque.pop();
                    if (andMerge.coldStartDeque.isEmpty()) {
                        break;
                    }
                }
                while (andMerge.coldStartDeque.size() > 500) {
                    andMerge.coldStartDeque.pop();
                    if (andMerge.coldStartDeque.isEmpty()) {
                        break;
                    }
                }
            }
            synchronized (this.mLockAppUsageDeque) {
                try {
                    if (andMerge.appUsageDeque.size() > 100) {
                        while (jCurrentTimeMillis - andMerge.appUsageDeque.getFirst().startTimeMs > _6_MONTH_TO_MILLIS) {
                            andMerge.appUsageDeque.pop();
                            if (andMerge.appUsageDeque.isEmpty()) {
                                break;
                            }
                        }
                        while (andMerge.appUsageDeque.size() > 300 && jCurrentTimeMillis - andMerge.appUsageDeque.getFirst().startTimeMs >= _2_MONTH_TO_MILLIS) {
                            andMerge.appUsageDeque.pop();
                            if (andMerge.appUsageDeque.isEmpty()) {
                                break;
                            }
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            writeToFile(key, andMerge);
            it.remove();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteFile(String str) {
        String filePath = getFilePath(str);
        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            SiUtils.Log.d(TAG, "Delete failed. file=" + filePath);
        }
    }

    private int getChargeCounter() {
        return (int) BSHelper.getCurrentBatteryCap();
    }

    private String getFilePath(String str) {
        return this.mFileDir + str;
    }

    private UsageHistory getOrCreateUsageHistory(String str) {
        UsageHistory usageHistory = this.mUsageMap.get(str);
        if (usageHistory != null) {
            return usageHistory;
        }
        UsageHistory usageHistory2 = new UsageHistory();
        this.mUsageMap.put(str, usageHistory2);
        return usageHistory2;
    }

    private String getPrefix(int i2) {
        String str = " ";
        while (i2 > 0) {
            str = str + " ";
            i2--;
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void init() {
        initFile();
        Context contextB = PowerKeeperApplication.b();
        EventsAggregator.getInstance(contextB).registerPackageAddedListener(this.mHandler);
        EventsAggregator.getInstance(contextB).registerPackageRemovedListener(this.mHandler);
    }

    private void initFile() {
        String str = this.mContext.getFilesDir().getAbsolutePath() + "/appUsage";
        File file = new File(str);
        if (file.exists() || file.mkdir()) {
            this.mFileDir = str + "/";
        }
    }

    private void markCodeStartTime(TrackingInfo trackingInfo) {
        UsageHistory orCreateUsageHistory = getOrCreateUsageHistory(trackingInfo.pkgName);
        orCreateUsageHistory.coldStartDeque.addLast(Long.valueOf(trackingInfo.startTimeMs));
        orCreateUsageHistory.uid = trackingInfo.uid;
        orCreateUsageHistory.pkgName = trackingInfo.pkgName;
    }

    private void notifyCharging(boolean z2) {
        synchronized (this) {
            try {
                if (z2) {
                    ForegroundInfo foregroundInfo = this.mFgInfo;
                    if (foregroundInfo != null) {
                        stopTracking(foregroundInfo.mForegroundUid);
                    }
                    if (!checkAllHistoryIfNeed() && this.mUsageMap.size() > 20) {
                        writeAllCachedDataToFile();
                    }
                } else {
                    ForegroundInfo foregroundInfo2 = this.mFgInfo;
                    if (foregroundInfo2 != null) {
                        startTracking(foregroundInfo2.mForegroundUid, foregroundInfo2.mForegroundPackageName, false);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void notifyFgChanged(ForegroundInfo foregroundInfo) {
        this.mFgInfo = foregroundInfo;
        stopTracking(foregroundInfo.mLastForegroundUid);
        ForegroundInfo foregroundInfo2 = this.mFgInfo;
        startTracking(foregroundInfo2.mForegroundUid, foregroundInfo2.mForegroundPackageName, (foregroundInfo2.mFlags & 1) != 0);
    }

    private void notifyScreenOff() {
        ForegroundInfo foregroundInfo = this.mFgInfo;
        if (foregroundInfo == null) {
            return;
        }
        stopTracking(foregroundInfo.mForegroundUid);
    }

    private void notifyScreenOn() {
        ForegroundInfo foregroundInfo = this.mFgInfo;
        if (foregroundInfo == null) {
            return;
        }
        startTracking(foregroundInfo.mForegroundUid, foregroundInfo.mForegroundPackageName, false);
    }

    private void printUsageHistory(UsageHistory usageHistory, PrintWriter printWriter) {
        if (usageHistory == null) {
            return;
        }
        printWriter.print(getPrefix(1) + usageHistory.uid);
        printWriter.print(getPrefix(0) + usageHistory.totalConsumption);
        printWriter.print(getPrefix(0) + usageHistory.totalTimeS);
        printWriter.println(getPrefix(0) + Arrays.toString(usageHistory.coldStartDeque.toArray()));
        if (usageHistory.appUsageDeque != null) {
            printWriter.print(getPrefix(1) + "appUsageDeque(" + usageHistory.appUsageDeque.size() + "):");
            int i2 = 0;
            for (AppUsage appUsage : usageHistory.appUsageDeque) {
                if (i2 % 4 == 0) {
                    printWriter.println();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(getPrefix(2));
                sb.append(i2);
                sb.append(":");
                printWriter.print(sb.toString());
                printWriter.print(getPrefix(0) + appUsage.consumption);
                printWriter.print(getPrefix(0) + appUsage.timeS);
                printWriter.print(getPrefix(0) + appUsage.startTimeMs);
                i2++;
            }
        }
        printWriter.println();
    }

    private UsageHistory readAndMerge(String str) {
        UsageHistory usageHistory = this.mUsageMap.get(str);
        UsageHistory fromFile = readFromFile(str);
        if (usageHistory == null) {
            return fromFile;
        }
        if (fromFile == null) {
            return usageHistory;
        }
        fromFile.totalTimeS += usageHistory.totalTimeS;
        fromFile.totalConsumption += usageHistory.totalConsumption;
        synchronized (this.mLockAppUsageDeque) {
            fromFile.appUsageDeque.addAll(usageHistory.appUsageDeque);
        }
        return fromFile;
    }

    private UsageHistory readFromFile(String str) {
        UsageHistory usageHistory;
        synchronized (this) {
            usageHistory = (UsageHistory) BLUtils.readFile(getFilePath(str));
        }
        return usageHistory;
    }

    private void startTracking(int i2, String str, boolean z2) {
        synchronized (this) {
            try {
                long jCurrentTimeMillis = System.currentTimeMillis();
                int chargeCounter = getChargeCounter();
                if (chargeCounter == -1) {
                    return;
                }
                TrackingInfo trackingInfo = new TrackingInfo();
                this.mTracking = trackingInfo;
                trackingInfo.uid = i2;
                trackingInfo.pkgName = str;
                trackingInfo.startTimeMs = jCurrentTimeMillis;
                trackingInfo.chargeCounter = chargeCounter;
                if (z2) {
                    markCodeStartTime(trackingInfo);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void stopTracking(int i2) {
        synchronized (this) {
            try {
                TrackingInfo trackingInfo = this.mTracking;
                if (trackingInfo != null && i2 == trackingInfo.uid) {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    if (jCurrentTimeMillis - this.mTracking.startTimeMs < TRACKING_TIME_MS) {
                        return;
                    }
                    int chargeCounter = getChargeCounter();
                    if (this.mTracking.chargeCounter - chargeCounter <= 0) {
                        return;
                    }
                    AppUsage appUsage = new AppUsage();
                    TrackingInfo trackingInfo2 = this.mTracking;
                    long j2 = trackingInfo2.startTimeMs;
                    appUsage.startTimeMs = j2;
                    appUsage.consumption = trackingInfo2.chargeCounter - chargeCounter;
                    appUsage.timeS = (int) ((jCurrentTimeMillis - j2) / 1000);
                    UsageHistory orCreateUsageHistory = getOrCreateUsageHistory(trackingInfo2.pkgName);
                    TrackingInfo trackingInfo3 = this.mTracking;
                    orCreateUsageHistory.uid = trackingInfo3.uid;
                    orCreateUsageHistory.pkgName = trackingInfo3.pkgName;
                    orCreateUsageHistory.totalConsumption += appUsage.consumption;
                    orCreateUsageHistory.totalTimeS += appUsage.timeS;
                    synchronized (this.mLockAppUsageDeque) {
                        orCreateUsageHistory.appUsageDeque.addLast(appUsage);
                    }
                    if (orCreateUsageHistory.appUsageDeque.size() >= WRITE_FILE_THRESHOLD) {
                        String str = orCreateUsageHistory.pkgName;
                        writeToFile(str, readAndMerge(str));
                        this.mUsageMap.remove(orCreateUsageHistory.pkgName);
                    }
                    this.mTracking = null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void writeAll() {
        Iterator<Map.Entry<String, UsageHistory>> it = this.mUsageMap.entrySet().iterator();
        while (it.hasNext()) {
            String key = it.next().getKey();
            writeToFile(key, readAndMerge(key));
            it.remove();
        }
    }

    private void writeAllCachedDataToFile() {
        Iterator<String> it = this.mUsageMap.keySet().iterator();
        while (it.hasNext()) {
            UsageHistory usageHistory = this.mUsageMap.get(it.next());
            if (usageHistory != null) {
                writeToFile(usageHistory.pkgName, usageHistory);
            }
        }
        this.mUsageMap.clear();
    }

    private void writeToFile(String str, UsageHistory usageHistory) {
        synchronized (this) {
            BLUtils.writeFile(getFilePath(str), usageHistory);
        }
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S, com.miui.powerkeeper.siming.IDivision
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr != null && strArr.length >= 1) {
            if (strArr[0].equals("-wa")) {
                writeAll();
                return;
            }
            return;
        }
        printWriter.println("APP Usage(" + this.mUsageMap.size() + "):");
        for (String str : this.mUsageMap.keySet()) {
            UsageHistory usageHistory = this.mUsageMap.get(str);
            printWriter.println(getPrefix(0) + str + ":");
            printUsageHistory(usageHistory, printWriter);
        }
        File file = new File(this.mFileDir);
        if (!file.exists() || !file.canRead()) {
            SiUtils.Log.d(TAG, "Dir " + this.mFileDir + " does not exist or is not readable.");
            return;
        }
        String[] list = file.list();
        printWriter.println("APP Usage in File(" + list.length + "):");
        int length = list.length;
        for (int i2 = 0; i2 < length; i2++) {
            String str2 = list[i2];
            UsageHistory fromFile = readFromFile(str2);
            printWriter.println(getPrefix(0) + str2 + ":");
            printUsageHistory(fromFile, printWriter);
        }
    }

    public float getAvgCurrent(String str) {
        int i2;
        UsageHistory usageHistory = getUsageHistory(str);
        if (usageHistory == null || usageHistory.appUsageDeque.size() < 7 || (i2 = usageHistory.totalTimeS) == 0) {
            return -1.0f;
        }
        return (usageHistory.totalConsumption * 3600) / i2;
    }

    @Nullable
    public UsageHistory getUsageHistory(String str) {
        UsageHistory usageHistory = this.mUsageMap.get(str);
        UsageHistory fromFile = readFromFile(str);
        if (usageHistory == null) {
            return fromFile;
        }
        if (fromFile == null) {
            return usageHistory;
        }
        fromFile.totalTimeS += usageHistory.totalTimeS;
        fromFile.totalConsumption += usageHistory.totalConsumption;
        synchronized (this.mLockAppUsageDeque) {
            fromFile.appUsageDeque.addAll(usageHistory.appUsageDeque);
        }
        return fromFile;
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S, com.miui.powerkeeper.siming.IDivision
    public void notify(int i2, Bundle bundle) {
        SiUtils.Log.d(TAG, "notify msgId=" + i2);
        if (i2 == 0) {
            notifyScreenOn();
            return;
        }
        if (i2 == 1) {
            notifyScreenOff();
        } else if (i2 == 2) {
            notifyCharging(bundle.getBoolean(MIY.BUNDLE_PARAM_CHARGING));
        } else {
            if (i2 != 4) {
                return;
            }
            notifyFgChanged((ForegroundInfo) bundle.get(MIY.BUNDLE_PARAM_FG_INFO));
        }
    }
}
