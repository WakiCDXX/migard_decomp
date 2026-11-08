package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerProcessStats {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final int MAX_HISTORY_ITEMS = 5;
    private static final int STATS_REST_PERIOD = 86400000;
    private static final String TAG = "PowerChecker.PrStats";
    private Context mContext;
    private long mLastProcessTime;
    private long mStatsResetTime;
    private int mTotalProcessCount;
    private final ArrayMap<String, StatsInfo> mAppStatsInfo = new ArrayMap<>();
    private final ArrayMap<String, StatsInfo> mSystemStatsInfo = new ArrayMap<>();
    private final ArrayMap<String, StatsInfo> mKwlStatsInfo = new ArrayMap<>();
    private StatsInfo mUnknownStatsInfo = new StatsInfo();
    private StatsInfo mSubSystemStatsInfo = new StatsInfo();

    public static class HistoryInfo {
        private boolean act_result;
        private int act_rule;
        private int level;

        public HistoryInfo(int i2, int i3, boolean z2) {
            this.level = i2;
            this.act_rule = i3;
            this.act_result = z2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append(" [" + this.level + ", " + this.act_rule + ", " + this.act_result + "]");
            return sb.toString();
        }
    }

    public class StatsInfo {
        private static final int DEFAULT_WAKELOCK_COUNT_WEIGHT = 2;
        private SparseArray<Integer> ignoreCount = new SparseArray<>();
        private boolean[] flags = new boolean[8];
        private SparseArray<LinkedList> historySparseArray = new SparseArray<>(8);
        private int processCount = 0;
        private long lastProcessTime = 0;
        private int notifyCount = 0;
        private long lastNotifyTime = 0;
        private int recordCount = 0;
        private long lastRecordTime = 0;

        public StatsInfo() {
            this.ignoreCount.clear();
        }

        public boolean[] getFlags() {
            return this.flags;
        }

        public int getIgnoreCount(int i2) {
            return this.ignoreCount.get(i2, 0).intValue();
        }

        public int getNotifyCount() {
            return this.notifyCount;
        }

        public int getProcessCount() {
            return this.processCount;
        }

        public long getProcessTimeInterval(long j2) {
            long j3 = this.lastProcessTime;
            if (j3 == 0) {
                return -1L;
            }
            return j2 - j3;
        }

        public int getRecordCount() {
            return this.recordCount;
        }

        public boolean isContinuousAbnormal(int i2, int i3, int i4, int i5, int i6) {
            int i7;
            if (i3 == 0) {
                LinkedList linkedList = this.historySparseArray.get(i2);
                if (linkedList != null && linkedList.size() >= (i7 = i5 + 1)) {
                    Iterator itDescendingIterator = linkedList.descendingIterator();
                    int i8 = 0;
                    while (itDescendingIterator.hasNext()) {
                        HistoryInfo historyInfo = (HistoryInfo) itDescendingIterator.next();
                        if (historyInfo.act_rule == i6 && historyInfo.level == i3) {
                            i8++;
                            if (i8 == i7) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
            if (i3 >= i4) {
                return true;
            }
            int i9 = (i4 - i3) + (i5 - 1);
            LinkedList linkedList2 = this.historySparseArray.get(i2);
            if (linkedList2 != null && linkedList2.size() >= i9) {
                Iterator itDescendingIterator2 = linkedList2.descendingIterator();
                int i10 = 0;
                while (itDescendingIterator2.hasNext()) {
                    HistoryInfo historyInfo2 = (HistoryInfo) itDescendingIterator2.next();
                    if (historyInfo2.act_rule != i6 || historyInfo2.level < i3) {
                        break;
                    }
                    i10++;
                    if (i10 == i9) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isHeavyPolicyLevel(int i2, int i3, int i4, int i5) {
            int i6;
            if (i3 < i4) {
                return false;
            }
            if (i5 > 1) {
                LinkedList linkedList = this.historySparseArray.get(i2);
                if (linkedList == null || linkedList.size() < (i6 = i5 - 1)) {
                    return false;
                }
                Iterator itDescendingIterator = linkedList.descendingIterator();
                int i7 = 0;
                while (itDescendingIterator.hasNext()) {
                    if (((HistoryInfo) itDescendingIterator.next()).level < i4) {
                        return false;
                    }
                    i7++;
                    if (i7 == i6) {
                        return true;
                    }
                }
            }
            return true;
        }

        public boolean isLoosePolicyLevel(int i2, int i3, int i4, int i5, int i6) {
            int i7;
            if (i3 < i4) {
                return false;
            }
            if (i5 > 1) {
                LinkedList linkedList = this.historySparseArray.get(i2);
                if (linkedList != null && linkedList.size() >= (i7 = i5 - 1)) {
                    Iterator itDescendingIterator = linkedList.descendingIterator();
                    int i8 = 0;
                    while (itDescendingIterator.hasNext()) {
                        HistoryInfo historyInfo = (HistoryInfo) itDescendingIterator.next();
                        if (historyInfo.act_rule == i6 && historyInfo.level >= i4) {
                            i8++;
                            if (i8 == i7) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
            return true;
        }

        public void reset() {
            this.processCount = 0;
            this.lastProcessTime = 0L;
            this.notifyCount = 0;
            this.lastNotifyTime = 0L;
            this.recordCount = 0;
            this.lastRecordTime = 0L;
            this.ignoreCount.clear();
            int i2 = 0;
            while (true) {
                boolean[] zArr = this.flags;
                if (i2 >= zArr.length) {
                    break;
                }
                zArr[i2] = false;
                i2++;
            }
            int size = this.historySparseArray.size();
            for (int i3 = 0; i3 < size; i3++) {
                LinkedList linkedListValueAt = this.historySparseArray.valueAt(i3);
                if (linkedListValueAt != null) {
                    linkedListValueAt.clear();
                }
            }
        }

        public void setFlag(int i2, boolean z2) {
            this.flags[i2] = z2;
        }

        public void setFlags(boolean z2) {
            int i2 = 0;
            while (true) {
                boolean[] zArr = this.flags;
                if (i2 >= zArr.length) {
                    return;
                }
                zArr[i2] = z2;
                i2++;
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("lastProcessTime : " + this.lastProcessTime);
            sb.append(", processCount : " + this.processCount);
            sb.append(", lastNotifyTime : " + this.lastNotifyTime);
            sb.append(", notifyCount : " + this.notifyCount);
            sb.append(", lastRecordTime : " + this.lastRecordTime);
            sb.append(", recordCount : " + this.recordCount + ShellUtils.COMMAND_LINE_END);
            sb.append("flags: ");
            for (int i2 = 0; i2 < this.flags.length; i2++) {
                sb.append(this.flags[i2] + " ");
            }
            int size = this.historySparseArray.size();
            for (int i3 = 0; i3 < size; i3++) {
                if (this.historySparseArray.valueAt(i3) != null) {
                    sb.append(ShellUtils.COMMAND_LINE_END);
                    sb.append(this.historySparseArray.keyAt(i3) + ": ");
                    Iterator it = this.historySparseArray.valueAt(i3).iterator();
                    while (it.hasNext()) {
                        sb.append(((HistoryInfo) it.next()).toString());
                    }
                }
            }
            sb.append(ShellUtils.COMMAND_LINE_END);
            int size2 = this.ignoreCount.size();
            for (int i4 = 0; i4 < size2; i4++) {
                sb.append("iCount[" + this.ignoreCount.keyAt(i4) + "]:" + this.ignoreCount.valueAt(i4) + " ");
            }
            sb.append(ShellUtils.COMMAND_LINE_END);
            return sb.toString();
        }

        public void updateHistoryInfo(int i2, HistoryInfo historyInfo) {
            LinkedList linkedList = this.historySparseArray.get(i2);
            if (linkedList == null) {
                LinkedList linkedList2 = new LinkedList();
                linkedList2.add(historyInfo);
                this.historySparseArray.put(i2, linkedList2);
            } else if (linkedList.size() < 5) {
                linkedList.add(historyInfo);
            } else {
                linkedList.poll();
                linkedList.add(historyInfo);
            }
        }

        public void updateIgnore(int i2) {
            this.ignoreCount.put(i2, Integer.valueOf(this.ignoreCount.get(i2, 0).intValue() + 1));
        }

        public void updateNotify(long j2) {
            this.lastNotifyTime = j2;
            this.notifyCount++;
        }

        public void updateProcess(long j2) {
            this.lastProcessTime = j2;
            this.processCount++;
        }

        public void updateRecord(long j2) {
            this.lastRecordTime = j2;
            this.recordCount++;
        }
    }

    public PowerCheckerProcessStats(Context context) {
        this.mContext = context;
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = SimpleSettings.Misc.getLong(context, SimpleSettingKeys.KEY_ABNORMAL_CONSUME_RESET_TIME, jCurrentTimeMillis);
        this.mStatsResetTime = j2;
        if (j2 == jCurrentTimeMillis) {
            if (DEBUG) {
                Log.d(TAG, "PowerCheckerProcessStats, no reset stats in shared pref file");
            }
            reset();
            return;
        }
        this.mTotalProcessCount = SimpleSettings.Misc.getInt(context, SimpleSettingKeys.KEY_ABNORMAL_CONSUME_TOTAL_COUNT, 0);
        if (DEBUG) {
            Log.d(TAG, "PowerCheckerProcessStats, " + toString());
        }
        this.mLastProcessTime = 0L;
        tryReset();
    }

    public void clearAppStatsInfoFlag() {
        LinkedList linkedList;
        ArrayMap<String, StatsInfo> arrayMap = this.mAppStatsInfo;
        if (arrayMap == null || arrayMap.size() <= 0) {
            return;
        }
        int size = this.mAppStatsInfo.size();
        for (int i2 = 0; i2 < size; i2++) {
            StatsInfo statsInfoValueAt = this.mAppStatsInfo.valueAt(i2);
            if (statsInfoValueAt != null) {
                boolean[] flags = statsInfoValueAt.getFlags();
                for (int i3 = 0; i3 < flags.length; i3++) {
                    if (!flags[i3] && (linkedList = (LinkedList) statsInfoValueAt.historySparseArray.get(i3)) != null) {
                        linkedList.clear();
                    }
                }
            }
        }
    }

    public void clearSystemStatsInfoFlag() {
        LinkedList linkedList;
        ArrayMap<String, StatsInfo> arrayMap = this.mSystemStatsInfo;
        if (arrayMap == null || arrayMap.size() <= 0) {
            return;
        }
        int size = this.mSystemStatsInfo.size();
        for (int i2 = 0; i2 < size; i2++) {
            StatsInfo statsInfoValueAt = this.mSystemStatsInfo.valueAt(i2);
            if (statsInfoValueAt != null) {
                boolean[] flags = statsInfoValueAt.getFlags();
                for (int i3 = 0; i3 < flags.length; i3++) {
                    if (!flags[i3] && (linkedList = (LinkedList) statsInfoValueAt.historySparseArray.get(i3)) != null) {
                        linkedList.clear();
                    }
                }
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("PowerCheckerProcessStats dump info");
        printWriter.println(toString());
        for (int i2 = 0; i2 < this.mSystemStatsInfo.size(); i2++) {
            String strKeyAt = this.mSystemStatsInfo.keyAt(i2);
            StatsInfo statsInfoValueAt = this.mSystemStatsInfo.valueAt(i2);
            if (statsInfoValueAt != null) {
                printWriter.println("mSystemStatsInfo[" + i2 + "]:  proc[" + strKeyAt + "] " + statsInfoValueAt.toString());
            }
        }
        for (int i3 = 0; i3 < this.mAppStatsInfo.size(); i3++) {
            String strKeyAt2 = this.mAppStatsInfo.keyAt(i3);
            StatsInfo statsInfoValueAt2 = this.mAppStatsInfo.valueAt(i3);
            if (statsInfoValueAt2 != null) {
                printWriter.println("AppStatsInfo[" + i3 + "]:  key[" + strKeyAt2 + "] " + statsInfoValueAt2.toString());
            }
        }
        for (int i4 = 0; i4 < this.mKwlStatsInfo.size(); i4++) {
            String strKeyAt3 = this.mKwlStatsInfo.keyAt(i4);
            StatsInfo statsInfoValueAt3 = this.mKwlStatsInfo.valueAt(i4);
            if (statsInfoValueAt3 != null) {
                printWriter.println("KwlStatsInfo[" + i4 + "]:  kwl[" + strKeyAt3 + "] " + statsInfoValueAt3.toString());
            }
        }
        printWriter.println("UnknownStatsInfo: " + this.mUnknownStatsInfo.toString());
        printWriter.println("SubSystemStatsInfo: " + this.mSubSystemStatsInfo.toString());
        printWriter.println();
    }

    public StatsInfo getAppStatsInfo(int i2, String str) {
        String str2 = i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + str;
        StatsInfo statsInfo = this.mAppStatsInfo.get(str2);
        if (statsInfo != null) {
            return statsInfo;
        }
        this.mAppStatsInfo.put(str2, new StatsInfo());
        return this.mAppStatsInfo.get(str2);
    }

    public StatsInfo getKwlStatsInfo(String str) {
        StatsInfo statsInfo = this.mKwlStatsInfo.get(str);
        if (statsInfo != null) {
            return statsInfo;
        }
        this.mKwlStatsInfo.put(str, new StatsInfo());
        return this.mKwlStatsInfo.get(str);
    }

    public StatsInfo getSubSystemStatsInfo() {
        return this.mSubSystemStatsInfo;
    }

    public StatsInfo getSystemStatsInfo(int i2, String str) {
        String str2 = i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + str;
        StatsInfo statsInfo = this.mSystemStatsInfo.get(str2);
        if (statsInfo != null) {
            return statsInfo;
        }
        this.mSystemStatsInfo.put(str2, new StatsInfo());
        return this.mSystemStatsInfo.get(str2);
    }

    public int getTotalProcessCount() {
        return this.mTotalProcessCount;
    }

    public long getTotalProcessTimeInterval(long j2) {
        long j3 = this.mLastProcessTime;
        if (j3 == 0) {
            return -1L;
        }
        return j2 - j3;
    }

    public StatsInfo getUnknownStatsInfo() {
        return this.mUnknownStatsInfo;
    }

    public void reset() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mStatsResetTime = jCurrentTimeMillis;
        SimpleSettings.Misc.putLong(this.mContext, SimpleSettingKeys.KEY_ABNORMAL_CONSUME_RESET_TIME, jCurrentTimeMillis);
        this.mTotalProcessCount = 0;
        SimpleSettings.Misc.putInt(this.mContext, SimpleSettingKeys.KEY_ABNORMAL_CONSUME_TOTAL_COUNT, 0);
        this.mLastProcessTime = 0L;
        for (int i2 = 0; i2 < this.mAppStatsInfo.size(); i2++) {
            StatsInfo statsInfoValueAt = this.mAppStatsInfo.valueAt(i2);
            if (statsInfoValueAt != null) {
                statsInfoValueAt.reset();
            }
        }
        for (int i3 = 0; i3 < this.mSystemStatsInfo.size(); i3++) {
            StatsInfo statsInfoValueAt2 = this.mSystemStatsInfo.valueAt(i3);
            if (statsInfoValueAt2 != null) {
                statsInfoValueAt2.reset();
            }
        }
        for (int i4 = 0; i4 < this.mKwlStatsInfo.size(); i4++) {
            StatsInfo statsInfoValueAt3 = this.mKwlStatsInfo.valueAt(i4);
            if (statsInfoValueAt3 != null) {
                statsInfoValueAt3.reset();
            }
        }
        this.mUnknownStatsInfo.reset();
        this.mSubSystemStatsInfo.reset();
    }

    public void setAppStatsInfoFlag() {
        ArrayMap<String, StatsInfo> arrayMap = this.mAppStatsInfo;
        if (arrayMap == null || arrayMap.size() <= 0) {
            return;
        }
        int size = this.mAppStatsInfo.size();
        for (int i2 = 0; i2 < size; i2++) {
            StatsInfo statsInfoValueAt = this.mAppStatsInfo.valueAt(i2);
            if (statsInfoValueAt != null) {
                statsInfoValueAt.setFlags(false);
            }
        }
    }

    public void setSystemStatsInfoFlag() {
        ArrayMap<String, StatsInfo> arrayMap = this.mSystemStatsInfo;
        if (arrayMap == null || arrayMap.size() <= 0) {
            return;
        }
        int size = this.mSystemStatsInfo.size();
        for (int i2 = 0; i2 < size; i2++) {
            StatsInfo statsInfoValueAt = this.mSystemStatsInfo.valueAt(i2);
            if (statsInfoValueAt != null) {
                statsInfoValueAt.setFlags(false);
            }
        }
    }

    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        StringBuilder sb = new StringBuilder();
        sb.append("mStatsResetTime : " + simpleDateFormat.format(new Date(this.mStatsResetTime)));
        sb.append(", mTotalProcessCount : " + this.mTotalProcessCount);
        sb.append(", mLastProcessTime : " + this.mLastProcessTime);
        sb.append(ShellUtils.COMMAND_LINE_END);
        return sb.toString();
    }

    public void tryReset() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        boolean z2 = DEBUG;
        if (z2) {
            Log.d(TAG, "tryReset, mTotalResetTime = " + new Date(this.mStatsResetTime) + ", nowUTC = " + new Date(jCurrentTimeMillis));
        }
        long j2 = this.mStatsResetTime;
        if (jCurrentTimeMillis < j2 || jCurrentTimeMillis - j2 >= 86400000) {
            if (z2) {
                Log.d(TAG, "tryReset, reset stats");
            }
            reset();
        }
    }

    public void update(long j2) {
        this.mLastProcessTime = j2;
        int i2 = this.mTotalProcessCount + 1;
        this.mTotalProcessCount = i2;
        SimpleSettings.Misc.putLong(this.mContext, SimpleSettingKeys.KEY_ABNORMAL_CONSUME_TOTAL_COUNT, i2);
    }

    public void updateAppIgnoreStats(int i2, String str, int i3, HistoryInfo historyInfo, int i4) {
        StatsInfo appStatsInfo = getAppStatsInfo(i2, str);
        appStatsInfo.updateHistoryInfo(i4, historyInfo);
        appStatsInfo.updateIgnore(i3);
    }

    public void updateAppNotifyStats(int i2, String str, long j2, HistoryInfo historyInfo, int i3) {
        StatsInfo appStatsInfo = getAppStatsInfo(i2, str);
        appStatsInfo.updateNotify(j2);
        appStatsInfo.updateHistoryInfo(i3, historyInfo);
        update(j2);
    }

    public void updateAppProcessStats(int i2, String str, long j2, HistoryInfo historyInfo, int i3) {
        StatsInfo appStatsInfo = getAppStatsInfo(i2, str);
        appStatsInfo.updateProcess(j2);
        appStatsInfo.updateHistoryInfo(i3, historyInfo);
        update(j2);
    }

    public void updateAppRecordStats(int i2, String str, long j2, HistoryInfo historyInfo, int i3) {
        StatsInfo appStatsInfo = getAppStatsInfo(i2, str);
        appStatsInfo.updateRecord(j2);
        appStatsInfo.updateHistoryInfo(i3, historyInfo);
        update(j2);
    }

    public void updateKwlIgnoreStats(String str, int i2) {
        getKwlStatsInfo(str).updateIgnore(i2);
    }

    public void updateKwlProcessStats(String str, long j2) {
        getKwlStatsInfo(str).updateProcess(j2);
        update(j2);
    }

    public void updateSubSystemIgnoreStats(int i2) {
        this.mSubSystemStatsInfo.updateIgnore(i2);
    }

    public void updateSubSystemProcessStats(long j2) {
        this.mSubSystemStatsInfo.updateProcess(j2);
        update(j2);
    }

    public void updateSystemIgnoreStats(int i2, String str, int i3, HistoryInfo historyInfo, int i4) {
        StatsInfo systemStatsInfo = getSystemStatsInfo(i2, str);
        systemStatsInfo.updateHistoryInfo(i4, historyInfo);
        systemStatsInfo.updateIgnore(i3);
    }

    public void updateSystemProcessStats(int i2, String str, long j2, HistoryInfo historyInfo, int i3) {
        StatsInfo systemStatsInfo = getSystemStatsInfo(i2, str);
        systemStatsInfo.updateProcess(j2);
        systemStatsInfo.updateHistoryInfo(i3, historyInfo);
        update(j2);
    }

    public void updateSystemRecordStats(int i2, String str, long j2, HistoryInfo historyInfo, int i3) {
        StatsInfo systemStatsInfo = getSystemStatsInfo(i2, str);
        systemStatsInfo.updateRecord(j2);
        systemStatsInfo.updateHistoryInfo(i3, historyInfo);
        update(j2);
    }
}
