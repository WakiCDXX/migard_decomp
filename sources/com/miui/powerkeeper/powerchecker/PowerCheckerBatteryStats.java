package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.net.NetworkStats;
import android.net.TrafficStats;
import android.os.Build;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;
import com.miui.powerkeeper.utils.NetUtils;
import com.xiaomi.push.mpcd.Constants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerBatteryStats {
    public static final int AUDIO_TURNED_ON = 1;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public static final int FOREGROUND_ACTIVITY = 0;
    private static final int MAXLOG = 50;
    public static final int NETWORK_RX_DATA = 0;
    public static final int NETWORK_TX_DATA = 1;
    public static final int NUM_NETWORK_ACTIVITY_TYPES = 2;
    public static final int STATS_CURRENT = 1;
    public static final int STATS_SINCE_CHARGED = 0;
    public static final int STATS_SINCE_UNPLUGGED = 2;
    private static final String TAG = "PowerChecker.BaStats";
    private final SparseBooleanArray mAudioOnAppIds;
    int mAudioOnNesting;
    StopwatchTimer mAudioOnTimer;
    private Clocks mClocks;
    private Context mContext;
    private boolean mDummyOnBattery;
    private final TimeBase mDummyTimeBase;
    private boolean mEnabled;
    private NetworkStats mLastNetworkStats;
    private LocalLog mLog = new LocalLog(50);
    final LongSamplingCounter[] mNetworkByteActivityCounters;
    private boolean mOnBattery;
    private final TimeBase mOnBatteryTimeBase;
    private boolean mScreenOff;
    private final SparseLongArray mTmpNetworkStatsArray;
    private final SparseArray<Uid> mUidStats;

    public static class BackgroundStopwatchTimer extends StopwatchTimer {
        boolean mRunning;

        public BackgroundStopwatchTimer(Clocks clocks, Uid uid, int i2, TimeBase timeBase, boolean z2) {
            super(clocks, uid, i2, timeBase);
            this.mRunning = !z2;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.StopwatchTimer
        public boolean isRunningLocked() {
            return this.mRunning && super.isRunningLocked();
        }

        public boolean setRunning(boolean z2, long j2) {
            if (this.mRunning == z2) {
                return false;
            }
            this.mRunning = z2;
            if (z2) {
                super.startRunningLocked(j2);
                return true;
            }
            super.stopRunningLocked(j2);
            return true;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.StopwatchTimer
        public void startRunningLocked(long j2) {
            if (this.mRunning) {
                super.startRunningLocked(j2);
            }
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.StopwatchTimer
        public void stopRunningLocked(long j2) {
            if (this.mRunning) {
                super.stopRunningLocked(j2);
            }
        }
    }

    public interface Clocks {
        long elapsedRealtime();

        long uptimeMillis();
    }

    public static class LongSamplingCounter implements TimeBaseObs {
        long mCount;
        long mLoadedCount;
        long mPluggedCount;
        final TimeBase mTimeBase;
        long mUnpluggedCount;

        LongSamplingCounter(TimeBase timeBase) {
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        void addCountLocked(long j2) {
            this.mCount += j2;
        }

        void detach() {
            this.mTimeBase.remove(this);
        }

        public long getCountLocked(int i2) {
            long j2;
            long j3 = this.mTimeBase.isRunning() ? this.mCount : this.mPluggedCount;
            if (i2 == 2) {
                j2 = this.mUnpluggedCount;
            } else {
                if (i2 == 0) {
                    return j3;
                }
                j2 = this.mLoadedCount;
            }
            return j3 - j2;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.TimeBaseObs
        public void onTimeStarted(long j2, long j3, long j4) {
            long j5 = this.mPluggedCount;
            this.mUnpluggedCount = j5;
            this.mCount = j5;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.TimeBaseObs
        public void onTimeStopped(long j2, long j3, long j4) {
            this.mPluggedCount = this.mCount;
        }

        void readSummary() {
            long j2 = this.mCount;
            this.mLoadedCount = j2;
            this.mCount = j2;
            this.mPluggedCount = j2;
            this.mUnpluggedCount = j2;
        }

        void reset(boolean z2) {
            this.mCount = 0L;
            this.mUnpluggedCount = 0L;
            this.mPluggedCount = 0L;
            this.mLoadedCount = 0L;
            if (z2) {
                detach();
            }
        }
    }

    public static class StopwatchTimer extends Timer {
        long mAcquireTime;
        int mNesting;
        long mUpdateTime;

        public StopwatchTimer(Clocks clocks, Uid uid, int i2, TimeBase timeBase) {
            super(clocks, i2, timeBase);
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.Timer
        protected int computeCurrentCountLocked() {
            return this.mCount;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.Timer
        protected long computeRunTimeLocked(long j2) {
            return this.mTotalTime + (this.mNesting > 0 ? j2 - this.mUpdateTime : 0L);
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.Timer
        public void detach() {
            super.detach();
        }

        public boolean isRunningLocked() {
            return this.mNesting > 0;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.Timer, com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.TimeBaseObs
        public void onTimeStopped(long j2, long j3, long j4) {
            if (this.mNesting > 0) {
                if (PowerCheckerBatteryStats.DEBUG && this.mType < 0) {
                    Log.v(PowerCheckerBatteryStats.TAG, "old mUpdateTime=" + this.mUpdateTime);
                }
                super.onTimeStopped(j2, j3, j4);
                this.mUpdateTime = j4;
                if (!PowerCheckerBatteryStats.DEBUG || this.mType >= 0) {
                    return;
                }
                Log.v(PowerCheckerBatteryStats.TAG, "new mUpdateTime=" + this.mUpdateTime);
            }
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.Timer
        public void readSummary(long j2) {
            super.readSummary(j2);
            this.mNesting = 0;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.Timer
        public boolean reset(boolean z2) {
            boolean z3 = false;
            boolean z4 = this.mNesting <= 0;
            if (z4 && z2) {
                z3 = true;
            }
            super.reset(z3);
            if (this.mNesting > 0) {
                this.mUpdateTime = this.mTimeBase.getRealtime(this.mClocks.elapsedRealtime() * 1000);
            }
            this.mAcquireTime = this.mTotalTime;
            return z4;
        }

        public void startRunningLocked(long j2) {
            int i2 = this.mNesting;
            this.mNesting = i2 + 1;
            if (i2 == 0) {
                this.mUpdateTime = this.mTimeBase.getRealtime(j2 * 1000);
                this.mCount++;
                this.mAcquireTime = this.mTotalTime;
                if (!PowerCheckerBatteryStats.DEBUG || this.mType >= 0) {
                    return;
                }
                Log.v(PowerCheckerBatteryStats.TAG, "start #" + this.mType + ": mUpdateTime=" + this.mUpdateTime + " mTotalTime=" + this.mTotalTime + " mCount=" + this.mCount + " mAcquireTime=" + this.mAcquireTime);
            }
        }

        public void stopRunningLocked(long j2) {
            int i2 = this.mNesting;
            if (i2 == 0) {
                return;
            }
            int i3 = i2 - 1;
            this.mNesting = i3;
            if (i3 == 0) {
                long realtime = this.mTimeBase.getRealtime(j2 * 1000);
                this.mNesting = 1;
                this.mTotalTime = computeRunTimeLocked(realtime);
                this.mNesting = 0;
                if (PowerCheckerBatteryStats.DEBUG && this.mType < 0) {
                    Log.v(PowerCheckerBatteryStats.TAG, "stop #" + this.mType + ": mUpdateTime=" + this.mUpdateTime + " mTotalTime=" + this.mTotalTime + " mCount=" + this.mCount + " mAcquireTime=" + this.mAcquireTime);
                }
                if (this.mTotalTime == this.mAcquireTime) {
                    this.mCount--;
                }
            }
        }
    }

    public static class SystemClocks implements Clocks {
        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.Clocks
        public long elapsedRealtime() {
            return SystemClock.elapsedRealtime();
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.Clocks
        public long uptimeMillis() {
            return SystemClock.uptimeMillis();
        }
    }

    public static class TimeBase {
        protected final ArrayList<TimeBaseObs> mObservers = new ArrayList<>();
        protected long mPastRealtime;
        protected long mPastUptime;
        protected long mRealtime;
        protected long mRealtimeStart;
        protected boolean mRunning;
        protected long mUnpluggedRealtime;
        protected long mUnpluggedUptime;
        protected long mUptime;
        protected long mUptimeStart;

        public void add(TimeBaseObs timeBaseObs) {
            this.mObservers.add(timeBaseObs);
        }

        public long computeRealtime(long j2, int i2) {
            if (i2 == 0) {
                return this.mRealtime + getRealtime(j2);
            }
            if (i2 == 1) {
                return getRealtime(j2);
            }
            if (i2 != 2) {
                return 0L;
            }
            return getRealtime(j2) - this.mUnpluggedRealtime;
        }

        public long computeUptime(long j2, int i2) {
            if (i2 == 0) {
                return this.mUptime + getUptime(j2);
            }
            if (i2 == 1) {
                return getUptime(j2);
            }
            if (i2 != 2) {
                return 0L;
            }
            return getUptime(j2) - this.mUnpluggedUptime;
        }

        public long getRealtime(long j2) {
            long j3 = this.mPastRealtime;
            return this.mRunning ? j3 + (j2 - this.mRealtimeStart) : j3;
        }

        public long getRealtimeStart() {
            return this.mRealtimeStart;
        }

        public long getUptime(long j2) {
            long j3 = this.mPastUptime;
            return this.mRunning ? j3 + (j2 - this.mUptimeStart) : j3;
        }

        public long getUptimeStart() {
            return this.mUptimeStart;
        }

        public boolean hasObserver(TimeBaseObs timeBaseObs) {
            return this.mObservers.contains(timeBaseObs);
        }

        public void init(long j2, long j3) {
            this.mRealtime = 0L;
            this.mUptime = 0L;
            this.mPastUptime = 0L;
            this.mPastRealtime = 0L;
            this.mUptimeStart = j2;
            this.mRealtimeStart = j3;
            this.mUnpluggedUptime = getUptime(j2);
            this.mUnpluggedRealtime = getRealtime(this.mRealtimeStart);
        }

        public boolean isRunning() {
            return this.mRunning;
        }

        public void readSummary(long j2, long j3) {
            this.mUptime = computeUptime(j2, 0);
            this.mRealtime = computeRealtime(j3, 0);
            this.mPastUptime = 0L;
            this.mPastRealtime = 0L;
            this.mUptimeStart = j2;
            this.mRealtimeStart = j3;
            this.mUnpluggedUptime = getUptime(j2);
            this.mUnpluggedRealtime = getRealtime(this.mRealtimeStart);
        }

        public void remove(TimeBaseObs timeBaseObs) {
            if (this.mObservers.remove(timeBaseObs)) {
                return;
            }
            Log.d(PowerCheckerBatteryStats.TAG, "Removed unknown observer: " + timeBaseObs);
        }

        public void reset(long j2, long j3) {
            if (!this.mRunning) {
                this.mPastUptime = 0L;
                this.mPastRealtime = 0L;
            } else {
                this.mUptimeStart = j2;
                this.mRealtimeStart = j3;
                this.mUnpluggedUptime = getUptime(j2);
                this.mUnpluggedRealtime = getRealtime(j3);
            }
        }

        public boolean setRunning(boolean z2, long j2, long j3) {
            if (this.mRunning == z2) {
                return false;
            }
            this.mRunning = z2;
            if (z2) {
                this.mUptimeStart = j2;
                this.mRealtimeStart = j3;
                long uptime = getUptime(j2);
                this.mUnpluggedUptime = uptime;
                long realtime = getRealtime(j3);
                this.mUnpluggedRealtime = realtime;
                for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                    this.mObservers.get(size).onTimeStarted(j3, uptime, realtime);
                }
            } else {
                this.mPastUptime += j2 - this.mUptimeStart;
                this.mPastRealtime += j3 - this.mRealtimeStart;
                long uptime2 = getUptime(j2);
                long realtime2 = getRealtime(j3);
                for (int size2 = this.mObservers.size() - 1; size2 >= 0; size2--) {
                    this.mObservers.get(size2).onTimeStopped(j3, uptime2, realtime2);
                }
            }
            return true;
        }
    }

    public interface TimeBaseObs {
        void onTimeStarted(long j2, long j3, long j4);

        void onTimeStopped(long j2, long j3, long j4);
    }

    public static abstract class Timer implements TimeBaseObs {
        protected final Clocks mClocks;
        protected int mCount;
        protected int mLastCount;
        protected long mLastTime;
        protected int mLoadedCount;
        protected long mLoadedTime;
        protected final TimeBase mTimeBase;
        protected long mTotalTime;
        protected final int mType;
        protected int mUnpluggedCount;
        protected long mUnpluggedTime;

        public Timer(Clocks clocks, int i2, TimeBase timeBase) {
            this.mClocks = clocks;
            this.mType = i2;
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        protected abstract int computeCurrentCountLocked();

        protected abstract long computeRunTimeLocked(long j2);

        public void detach() {
            this.mTimeBase.remove(this);
        }

        public int getCountLocked(int i2) {
            int i3;
            int iComputeCurrentCountLocked = computeCurrentCountLocked();
            if (i2 == 2) {
                i3 = this.mUnpluggedCount;
            } else {
                if (i2 == 0) {
                    return iComputeCurrentCountLocked;
                }
                i3 = this.mLoadedCount;
            }
            return iComputeCurrentCountLocked - i3;
        }

        public long getTotalTimeLocked(long j2, int i2) {
            long j3;
            long jComputeRunTimeLocked = computeRunTimeLocked(this.mTimeBase.getRealtime(j2));
            if (i2 == 2) {
                j3 = this.mUnpluggedTime;
            } else {
                if (i2 == 0) {
                    return jComputeRunTimeLocked;
                }
                j3 = this.mLoadedTime;
            }
            return jComputeRunTimeLocked - j3;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.TimeBaseObs
        public void onTimeStarted(long j2, long j3, long j4) {
            if (PowerCheckerBatteryStats.DEBUG && this.mType < 0) {
                Log.v(PowerCheckerBatteryStats.TAG, "unplug #" + this.mType + ": realtime=" + j4 + " old mUnpluggedTime=" + this.mUnpluggedTime + " old mUnpluggedCount=" + this.mUnpluggedCount);
            }
            this.mUnpluggedTime = computeRunTimeLocked(j4);
            this.mUnpluggedCount = computeCurrentCountLocked();
            if (!PowerCheckerBatteryStats.DEBUG || this.mType >= 0) {
                return;
            }
            Log.v(PowerCheckerBatteryStats.TAG, "unplug #" + this.mType + ": new mUnpluggedTime=" + this.mUnpluggedTime + " new mUnpluggedCount=" + this.mUnpluggedCount);
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerBatteryStats.TimeBaseObs
        public void onTimeStopped(long j2, long j3, long j4) {
            if (PowerCheckerBatteryStats.DEBUG && this.mType < 0) {
                Log.v(PowerCheckerBatteryStats.TAG, "plug #" + this.mType + ": realtime=" + j4 + " old mTotalTime=" + this.mTotalTime);
            }
            this.mTotalTime = computeRunTimeLocked(j4);
            this.mCount = computeCurrentCountLocked();
            if (!PowerCheckerBatteryStats.DEBUG || this.mType >= 0) {
                return;
            }
            Log.v(PowerCheckerBatteryStats.TAG, "plug #" + this.mType + ": new mTotalTime=" + this.mTotalTime);
        }

        public void readSummary(long j2) {
            long jComputeRunTimeLocked = computeRunTimeLocked(this.mTimeBase.getRealtime(j2));
            this.mLoadedTime = jComputeRunTimeLocked;
            this.mTotalTime = jComputeRunTimeLocked;
            this.mLastTime = 0L;
            this.mUnpluggedTime = jComputeRunTimeLocked;
            int iComputeCurrentCountLocked = computeCurrentCountLocked();
            this.mLoadedCount = iComputeCurrentCountLocked;
            this.mCount = iComputeCurrentCountLocked;
            this.mLastCount = 0;
            this.mUnpluggedCount = iComputeCurrentCountLocked;
        }

        public boolean reset(boolean z2) {
            this.mLastTime = 0L;
            this.mLoadedTime = 0L;
            this.mTotalTime = 0L;
            this.mLastCount = 0;
            this.mLoadedCount = 0;
            this.mCount = 0;
            if (!z2) {
                return true;
            }
            detach();
            return true;
        }
    }

    public static class Uid {
        StopwatchTimer mAudioTurnedOnTimer;
        BackgroundStopwatchTimer mBackgroundAudioTurnedOnTimer;
        LongSamplingCounter mBackgroundNetworkByteActivityCounter;
        PowerCheckerBatteryStats mBsi;
        StopwatchTimer mForegroundActivityTimer;
        boolean mIsForeground = false;
        LongSamplingCounter[] mNetworkByteActivityCounters;
        final int mUid;

        public Uid(PowerCheckerBatteryStats powerCheckerBatteryStats, int i2) {
            this.mBsi = powerCheckerBatteryStats;
            this.mUid = i2;
        }

        private void initBackgroundNetworkActivityLocked() {
            this.mBackgroundNetworkByteActivityCounter = new LongSamplingCounter(this.mBsi.mDummyTimeBase);
        }

        private void initNetworkActivityLocked() {
            this.mNetworkByteActivityCounters = new LongSamplingCounter[2];
            for (int i2 = 0; i2 < 2; i2++) {
                this.mNetworkByteActivityCounters[i2] = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            }
        }

        public StopwatchTimer createAudioTurnedOnTimerLocked() {
            if (this.mAudioTurnedOnTimer == null) {
                this.mAudioTurnedOnTimer = new StopwatchTimer(this.mBsi.mClocks, this, 1, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mAudioTurnedOnTimer;
        }

        public BackgroundStopwatchTimer createBackgroundAudioTurnedOnTimerLocked() {
            if (this.mBackgroundAudioTurnedOnTimer == null) {
                this.mBackgroundAudioTurnedOnTimer = new BackgroundStopwatchTimer(this.mBsi.mClocks, this, 1, this.mBsi.mDummyTimeBase, this.mIsForeground);
            }
            return this.mBackgroundAudioTurnedOnTimer;
        }

        public StopwatchTimer createForegroundActivityTimerLocked() {
            if (this.mForegroundActivityTimer == null) {
                this.mForegroundActivityTimer = new StopwatchTimer(this.mBsi.mClocks, this, 0, this.mBsi.mDummyTimeBase);
            }
            return this.mForegroundActivityTimer;
        }

        public Timer getAudioTurnedOnTimer() {
            return this.mAudioTurnedOnTimer;
        }

        public Timer getBackgroundAudioTurnedOnTimer() {
            return this.mBackgroundAudioTurnedOnTimer;
        }

        public long getBackgroundNetworkActivityBytes(int i2) {
            LongSamplingCounter longSamplingCounter = this.mBackgroundNetworkByteActivityCounter;
            if (longSamplingCounter != null) {
                return longSamplingCounter.getCountLocked(i2);
            }
            return 0L;
        }

        public PowerCheckerBatteryStats getBatteryStats() {
            return this.mBsi;
        }

        public Timer getForegroundActivityTimer() {
            return this.mForegroundActivityTimer;
        }

        public long getNetworkActivityBytes(int i2, int i3) {
            LongSamplingCounter[] longSamplingCounterArr = this.mNetworkByteActivityCounters;
            if (longSamplingCounterArr == null || i2 < 0 || i2 >= longSamplingCounterArr.length) {
                return 0L;
            }
            return longSamplingCounterArr[i2].getCountLocked(i3);
        }

        public int getUid() {
            return this.mUid;
        }

        public boolean isBackground() {
            return !this.mIsForeground;
        }

        public void noteActivityPausedLocked(long j2) {
            StopwatchTimer stopwatchTimer = this.mForegroundActivityTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j2);
            }
            this.mIsForeground = false;
            BackgroundStopwatchTimer backgroundStopwatchTimer = this.mBackgroundAudioTurnedOnTimer;
            if (backgroundStopwatchTimer != null) {
                backgroundStopwatchTimer.setRunning(true, j2);
            }
        }

        public void noteActivityResumedLocked(long j2) {
            createForegroundActivityTimerLocked().startRunningLocked(j2);
            this.mIsForeground = true;
            BackgroundStopwatchTimer backgroundStopwatchTimer = this.mBackgroundAudioTurnedOnTimer;
            if (backgroundStopwatchTimer != null) {
                backgroundStopwatchTimer.setRunning(false, j2);
            }
        }

        public void noteAudioTurnedOffLocked(long j2) {
            StopwatchTimer stopwatchTimer = this.mAudioTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j2);
            }
        }

        public void noteAudioTurnedOnLocked(long j2) {
            createAudioTurnedOnTimerLocked().startRunningLocked(j2);
        }

        public void noteBackgroundAudioTurnedOffLocked(long j2) {
            BackgroundStopwatchTimer backgroundStopwatchTimer = this.mBackgroundAudioTurnedOnTimer;
            if (backgroundStopwatchTimer != null) {
                backgroundStopwatchTimer.stopRunningLocked(j2);
            }
        }

        public void noteBackgroundAudioTurnedOnLocked(long j2) {
            createBackgroundAudioTurnedOnTimerLocked().startRunningLocked(j2);
        }

        public void noteBackgroundNetworkActivityLocked(long j2) {
            if (this.mBackgroundNetworkByteActivityCounter == null) {
                initBackgroundNetworkActivityLocked();
            }
            this.mBackgroundNetworkByteActivityCounter.addCountLocked(j2);
        }

        public void noteNetworkActivityLocked(int i2, long j2) {
            if (this.mNetworkByteActivityCounters == null) {
                initNetworkActivityLocked();
            }
            if (i2 >= 0 && i2 < 2) {
                this.mNetworkByteActivityCounters[i2].addCountLocked(j2);
                return;
            }
            Log.d(PowerCheckerBatteryStats.TAG, "Unknown network activity type " + i2 + " was specified.", new Throwable());
        }

        boolean reset() {
            this.mIsForeground = false;
            StopwatchTimer stopwatchTimer = this.mAudioTurnedOnTimer;
            boolean z2 = stopwatchTimer != null ? !stopwatchTimer.reset(false) : false;
            if (this.mNetworkByteActivityCounters != null) {
                for (int i2 = 0; i2 < 2; i2++) {
                    this.mNetworkByteActivityCounters[i2].reset(false);
                }
            }
            if (!z2) {
                StopwatchTimer stopwatchTimer2 = this.mAudioTurnedOnTimer;
                if (stopwatchTimer2 != null) {
                    stopwatchTimer2.detach();
                    this.mAudioTurnedOnTimer = null;
                }
                if (this.mNetworkByteActivityCounters != null) {
                    for (int i3 = 0; i3 < 2; i3++) {
                        this.mNetworkByteActivityCounters[i3].detach();
                    }
                }
            }
            StopwatchTimer stopwatchTimer3 = this.mForegroundActivityTimer;
            boolean z3 = stopwatchTimer3 != null ? !stopwatchTimer3.reset(false) : false;
            BackgroundStopwatchTimer backgroundStopwatchTimer = this.mBackgroundAudioTurnedOnTimer;
            if (backgroundStopwatchTimer != null) {
                z3 |= !backgroundStopwatchTimer.reset(false);
            }
            LongSamplingCounter longSamplingCounter = this.mBackgroundNetworkByteActivityCounter;
            if (longSamplingCounter != null) {
                longSamplingCounter.reset(false);
            }
            if (!z3) {
                StopwatchTimer stopwatchTimer4 = this.mForegroundActivityTimer;
                if (stopwatchTimer4 != null) {
                    stopwatchTimer4.detach();
                    this.mForegroundActivityTimer = null;
                }
                BackgroundStopwatchTimer backgroundStopwatchTimer2 = this.mBackgroundAudioTurnedOnTimer;
                if (backgroundStopwatchTimer2 != null) {
                    backgroundStopwatchTimer2.detach();
                    this.mBackgroundAudioTurnedOnTimer = null;
                }
                LongSamplingCounter longSamplingCounter2 = this.mBackgroundNetworkByteActivityCounter;
                if (longSamplingCounter2 != null) {
                    longSamplingCounter2.detach();
                }
            }
            return !(z2 | z3);
        }
    }

    public PowerCheckerBatteryStats(Context context) {
        TimeBase timeBase = new TimeBase();
        this.mOnBatteryTimeBase = timeBase;
        this.mDummyTimeBase = new TimeBase();
        this.mUidStats = new SparseArray<>();
        this.mTmpNetworkStatsArray = new SparseLongArray();
        this.mAudioOnAppIds = new SparseBooleanArray();
        this.mNetworkByteActivityCounters = new LongSamplingCounter[2];
        this.mContext = context;
        init(new SystemClocks());
        this.mAudioOnTimer = new StopwatchTimer(this.mClocks, null, 1, timeBase);
        for (int i2 = 0; i2 < 2; i2++) {
            this.mNetworkByteActivityCounters[i2] = new LongSamplingCounter(this.mOnBatteryTimeBase);
        }
        initTimes(this.mClocks.uptimeMillis() * 1000, this.mClocks.elapsedRealtime() * 1000);
        this.mOnBattery = false;
        this.mDummyOnBattery = false;
        this.mEnabled = false;
        this.mScreenOff = false;
    }

    private long getNetworkActivityTotalBytes(int i2, int i3) {
        if (i2 < 0) {
            return 0L;
        }
        LongSamplingCounter[] longSamplingCounterArr = this.mNetworkByteActivityCounters;
        if (i2 < longSamplingCounterArr.length) {
            return longSamplingCounterArr[i2].getCountLocked(i3);
        }
        return 0L;
    }

    private NetworkStats getNetworkStatsDeltaLocked() {
        NetworkStats networkStats = NetUtils.getNetworkStats();
        if (networkStats == null) {
            return null;
        }
        NetworkStats networkStatsSubtract = networkStats.subtract(this.mLastNetworkStats);
        this.mLastNetworkStats = networkStats;
        return networkStatsSubtract;
    }

    private void init(Clocks clocks) {
        this.mClocks = clocks;
        this.mLastNetworkStats = new NetworkStats(this.mClocks.elapsedRealtime(), 50);
    }

    private void initTimes(long j2, long j3) {
        this.mOnBatteryTimeBase.init(j2, j3);
        this.mDummyTimeBase.init(j2, j3);
    }

    private void logLastStatsCurrent() {
        this.mLog.log("Last power checker battery statistics since current:");
        StringBuilder sb = new StringBuilder(128);
        this.mLog.log(" B:" + this.mOnBattery + " D:" + this.mDummyOnBattery + " E:" + this.mEnabled + " S:" + this.mScreenOff);
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        long jComputeBatteryUptime = computeBatteryUptime(jUptimeMillis, 1);
        long jComputeBatteryRealtime = computeBatteryRealtime(jElapsedRealtime, 1);
        long jComputeDummyUptime = computeDummyUptime(jUptimeMillis, 1);
        long jComputeDummyRealtime = computeDummyRealtime(jElapsedRealtime, 1);
        sb.setLength(0);
        sb.append(" Time on battery: ");
        PowerCheckerUtils.formatTimeMs(sb, jComputeBatteryRealtime / 1000);
        sb.append(" realtime, ");
        PowerCheckerUtils.formatTimeMs(sb, jComputeBatteryUptime / 1000);
        sb.append(" uptime");
        this.mLog.log(sb.toString());
        sb.setLength(0);
        sb.append(" Time on dummy: ");
        PowerCheckerUtils.formatTimeMs(sb, jComputeDummyRealtime / 1000);
        sb.append(" realtime, ");
        PowerCheckerUtils.formatTimeMs(sb, jComputeDummyUptime / 1000);
        sb.append(" uptime");
        this.mLog.log(sb.toString());
        long networkActivityTotalBytes = getNetworkActivityTotalBytes(0, 1);
        long networkActivityTotalBytes2 = getNetworkActivityTotalBytes(1, 1);
        sb.setLength(0);
        sb.append(" Total Network: ");
        sb.append(PowerCheckerUtils.formatBytesLocked(networkActivityTotalBytes));
        sb.append(" received, ");
        sb.append(PowerCheckerUtils.formatBytesLocked(networkActivityTotalBytes2));
        sb.append(" sent");
        this.mLog.log(sb.toString());
        sb.setLength(0);
        int i2 = 0;
        if (logTimer(sb, this.mAudioOnTimer, jElapsedRealtime, 1, "Audio on")) {
            this.mLog.log(sb.toString());
        }
        SparseArray<Uid> uidStats = getUidStats();
        int size = uidStats.size();
        int i3 = 0;
        while (i3 < size) {
            int iKeyAt = uidStats.keyAt(i3);
            Uid uidValueAt = uidStats.valueAt(i3);
            sb.setLength(i2);
            sb.append("  ");
            UserHandle.formatUid(sb, iKeyAt);
            long networkActivityBytes = uidValueAt.getNetworkActivityBytes(i2, 1);
            long networkActivityBytes2 = uidValueAt.getNetworkActivityBytes(1, 1);
            if (networkActivityBytes > 0 || networkActivityBytes2 > 0) {
                sb.append(" N: ");
                sb.append(PowerCheckerUtils.formatBytesLocked(networkActivityBytes));
                sb.append(" r, ");
                sb.append(PowerCheckerUtils.formatBytesLocked(networkActivityBytes2));
                sb.append(" s;");
            }
            boolean zLogTimer = logTimer(sb, uidValueAt.getAudioTurnedOnTimer(), jElapsedRealtime, 1, "A");
            if (zLogTimer) {
                sb.append(";");
            }
            boolean zLogTimer2 = logTimer(sb, uidValueAt.getForegroundActivityTimer(), jElapsedRealtime, 1, "F");
            if (zLogTimer2) {
                sb.append(";");
            }
            long backgroundNetworkActivityBytes = uidValueAt.getBackgroundNetworkActivityBytes(1);
            if (backgroundNetworkActivityBytes > 0) {
                sb.append(" BN: ");
                sb.append(PowerCheckerUtils.formatBytesLocked(backgroundNetworkActivityBytes));
                sb.append(";");
            }
            boolean zLogTimer3 = logTimer(sb, uidValueAt.getBackgroundAudioTurnedOnTimer(), jElapsedRealtime, 1, "BA");
            if (networkActivityBytes > 0 || networkActivityBytes2 > 0 || backgroundNetworkActivityBytes > 0 || zLogTimer || zLogTimer2 || zLogTimer3) {
                this.mLog.log(sb.toString());
            }
            i3++;
            i2 = 0;
        }
    }

    private static final boolean logTimer(StringBuilder sb, Timer timer, long j2, int i2, String str) {
        if (timer == null) {
            return false;
        }
        long totalTimeLocked = (timer.getTotalTimeLocked(j2, i2) + 500) / 1000;
        int countLocked = timer.getCountLocked(i2);
        if (totalTimeLocked == 0) {
            return false;
        }
        sb.append(" ");
        sb.append(str);
        sb.append(": ");
        PowerCheckerUtils.formatTimeMs(sb, totalTimeLocked);
        sb.append(Constants.SEPARATOR_LEFT_PARENTESIS);
        sb.append(countLocked);
        sb.append(" times)");
        return true;
    }

    private static final boolean printTimer(PrintWriter printWriter, StringBuilder sb, Timer timer, long j2, int i2, String str) {
        if (timer != null) {
            long totalTimeLocked = (timer.getTotalTimeLocked(j2, i2) + 500) / 1000;
            int countLocked = timer.getCountLocked(i2);
            if (totalTimeLocked != 0) {
                sb.setLength(0);
                sb.append("    ");
                sb.append(str);
                sb.append(": ");
                PowerCheckerUtils.formatTimeMs(sb, totalTimeLocked);
                sb.append("realtime (");
                sb.append(countLocked);
                sb.append(" times)");
                printWriter.println(sb.toString());
                return true;
            }
        }
        return false;
    }

    private void readLocked() {
        long jUptimeMillis = this.mClocks.uptimeMillis() * 1000;
        long jElapsedRealtime = this.mClocks.elapsedRealtime() * 1000;
        this.mAudioOnNesting = 0;
        this.mAudioOnTimer.readSummary(jElapsedRealtime);
        for (int i2 = 0; i2 < 2; i2++) {
            this.mNetworkByteActivityCounters[i2].readSummary();
        }
        SparseArray<Uid> uidStats = getUidStats();
        int size = uidStats.size();
        for (int i3 = 0; i3 < size; i3++) {
            uidStats.keyAt(i3);
            Uid uidValueAt = uidStats.valueAt(i3);
            StopwatchTimer stopwatchTimer = uidValueAt.mAudioTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.readSummary(jElapsedRealtime);
            }
            if (uidValueAt.mNetworkByteActivityCounters != null) {
                for (int i4 = 0; i4 < 2; i4++) {
                    uidValueAt.mNetworkByteActivityCounters[i4].readSummary();
                }
            }
            StopwatchTimer stopwatchTimer2 = uidValueAt.mForegroundActivityTimer;
            if (stopwatchTimer2 != null) {
                stopwatchTimer2.readSummary(jElapsedRealtime);
                uidValueAt.mIsForeground = uidValueAt.mForegroundActivityTimer.isRunningLocked();
            }
            BackgroundStopwatchTimer backgroundStopwatchTimer = uidValueAt.mBackgroundAudioTurnedOnTimer;
            if (backgroundStopwatchTimer != null) {
                backgroundStopwatchTimer.readSummary(jElapsedRealtime);
            }
            LongSamplingCounter longSamplingCounter = uidValueAt.mBackgroundNetworkByteActivityCounter;
            if (longSamplingCounter != null) {
                longSamplingCounter.readSummary();
            }
        }
        this.mOnBatteryTimeBase.readSummary(jUptimeMillis, jElapsedRealtime);
        this.mDummyTimeBase.readSummary(jUptimeMillis, jElapsedRealtime);
    }

    private void resetAllStatsLocked() {
        long jUptimeMillis = this.mClocks.uptimeMillis() * 1000;
        long jElapsedRealtime = this.mClocks.elapsedRealtime() * 1000;
        initTimes(jUptimeMillis, jElapsedRealtime);
        int i2 = 0;
        this.mAudioOnTimer.reset(false);
        for (int i3 = 0; i3 < 2; i3++) {
            this.mNetworkByteActivityCounters[i3].reset(false);
        }
        this.mAudioOnAppIds.clear();
        while (i2 < this.mUidStats.size()) {
            if (this.mUidStats.valueAt(i2).reset()) {
                SparseArray<Uid> sparseArray = this.mUidStats;
                sparseArray.remove(sparseArray.keyAt(i2));
                i2--;
            }
            i2++;
        }
        this.mOnBatteryTimeBase.reset(jUptimeMillis, jElapsedRealtime);
        this.mDummyTimeBase.reset(jUptimeMillis, jElapsedRealtime);
    }

    private void setBatteryStateLocked(boolean z2) {
        if (DEBUG) {
            Log.d(TAG, "setBatteryStateLocked, screenOff = " + z2);
        }
        long jUptimeMillis = this.mClocks.uptimeMillis() * 1000;
        long jElapsedRealtime = this.mClocks.elapsedRealtime() * 1000;
        this.mScreenOff = z2;
        updateNetworkStatsLocked();
        boolean z3 = this.mEnabled && z2;
        if (z3 != this.mOnBattery) {
            if (z3) {
                this.mOnBattery = true;
                updateTimeBasesLocked(true, jUptimeMillis, jElapsedRealtime);
            } else {
                this.mOnBattery = false;
                updateTimeBasesLocked(false, jUptimeMillis, jElapsedRealtime);
            }
        }
    }

    private void setDummyStateLocked(boolean z2) {
        if (DEBUG) {
            Log.d(TAG, "setDummyStateLocked, dummy = " + z2);
        }
        long jUptimeMillis = this.mClocks.uptimeMillis() * 1000;
        long jElapsedRealtime = this.mClocks.elapsedRealtime() * 1000;
        updateBackgroundNetworkStatsLocked();
        boolean z3 = this.mEnabled && z2;
        if (z3 != this.mDummyOnBattery) {
            if (z3) {
                this.mDummyOnBattery = true;
                updateDummyTimeBasesLocked(true, jUptimeMillis, jElapsedRealtime);
            } else {
                this.mDummyOnBattery = false;
                updateDummyTimeBasesLocked(false, jUptimeMillis, jElapsedRealtime);
            }
        }
    }

    private void setEnabledStateLocked(boolean z2, boolean z3) {
        if (DEBUG) {
            Log.d(TAG, "setEnabledStateLocked, enable = " + z2 + ", reset = " + z3);
        }
        long jUptimeMillis = this.mClocks.uptimeMillis() * 1000;
        long jElapsedRealtime = 1000 * this.mClocks.elapsedRealtime();
        updateNetworkStatsLocked();
        updateBackgroundNetworkStatsLocked();
        if (z3 && z2) {
            resetAllStatsLocked();
        }
        this.mEnabled = z2;
        boolean z4 = z2 && this.mScreenOff;
        if (z4 != this.mOnBattery) {
            if (z4) {
                this.mOnBattery = true;
                updateTimeBasesLocked(true, jUptimeMillis, jElapsedRealtime);
            } else {
                this.mOnBattery = false;
                updateTimeBasesLocked(false, jUptimeMillis, jElapsedRealtime);
            }
        }
        if (z2 != this.mDummyOnBattery) {
            if (z2) {
                this.mDummyOnBattery = true;
                updateDummyTimeBasesLocked(true, jUptimeMillis, jElapsedRealtime);
            } else {
                this.mDummyOnBattery = false;
                updateDummyTimeBasesLocked(false, jUptimeMillis, jElapsedRealtime);
            }
        }
    }

    private void updateBackgroundNetworkStatsLocked() {
        if (DEBUG) {
            Log.d(TAG, "update background network stats");
        }
        new SparseLongArray();
        NetUtils.getNetworkStats();
    }

    private void updateNetworkStatsLocked() {
        if (DEBUG) {
            Log.d(TAG, "Updating network stats");
        }
        getNetworkStatsDeltaLocked();
    }

    public long computeBatteryRealtime(long j2, int i2) {
        return this.mOnBatteryTimeBase.computeRealtime(j2, i2);
    }

    public long computeBatteryUptime(long j2, int i2) {
        return this.mOnBatteryTimeBase.computeUptime(j2, i2);
    }

    public long computeDummyRealtime(long j2, int i2) {
        return this.mDummyTimeBase.computeRealtime(j2, i2);
    }

    public long computeDummyUptime(long j2, int i2) {
        return this.mDummyTimeBase.computeUptime(j2, i2);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i2) {
        if (i2 == 0) {
            printWriter.println("Power checker battery statistics since last charge:");
        } else if (i2 == 1) {
            printWriter.println("Power checker battery statistics since current:");
        } else {
            printWriter.println("Power checker battery statistics since last unplugged:");
        }
        StringBuilder sb = new StringBuilder(128);
        synchronized (this) {
            try {
                printWriter.println("  mOnBattery: " + this.mOnBattery);
                printWriter.println("  mDummyOnBattery: " + this.mDummyOnBattery);
                printWriter.println("  mEnabled: " + this.mEnabled);
                printWriter.println("  mScreenOff: " + this.mScreenOff);
                long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
                long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
                long jComputeBatteryUptime = computeBatteryUptime(jUptimeMillis, i2);
                long jComputeBatteryRealtime = computeBatteryRealtime(jElapsedRealtime, i2);
                long jComputeDummyUptime = computeDummyUptime(jUptimeMillis, i2);
                long jComputeDummyRealtime = computeDummyRealtime(jElapsedRealtime, i2);
                sb.setLength(0);
                sb.append("  Time on battery: ");
                PowerCheckerUtils.formatTimeMs(sb, jComputeBatteryRealtime / 1000);
                sb.append(" realtime, ");
                PowerCheckerUtils.formatTimeMs(sb, jComputeBatteryUptime / 1000);
                sb.append(" uptime");
                printWriter.println(sb.toString());
                sb.setLength(0);
                sb.append("  Time on dummy: ");
                PowerCheckerUtils.formatTimeMs(sb, jComputeDummyRealtime / 1000);
                sb.append(" realtime, ");
                PowerCheckerUtils.formatTimeMs(sb, jComputeDummyUptime / 1000);
                sb.append(" uptime");
                printWriter.println(sb.toString());
                long networkActivityTotalBytes = getNetworkActivityTotalBytes(0, i2);
                long networkActivityTotalBytes2 = getNetworkActivityTotalBytes(1, i2);
                printWriter.print("  Total Network: ");
                printWriter.print(PowerCheckerUtils.formatBytesLocked(networkActivityTotalBytes));
                printWriter.print(" received, ");
                printWriter.print(PowerCheckerUtils.formatBytesLocked(networkActivityTotalBytes2));
                printWriter.print(" sent");
                printWriter.println();
                printTimer(printWriter, sb, this.mAudioOnTimer, jElapsedRealtime, i2, "Audio on");
                SparseArray<Uid> uidStats = getUidStats();
                int size = uidStats.size();
                int i3 = 0;
                while (i3 < size) {
                    int iKeyAt = uidStats.keyAt(i3);
                    Uid uidValueAt = uidStats.valueAt(i3);
                    printWriter.print("  ");
                    UserHandle.formatUid(printWriter, iKeyAt);
                    printWriter.println(":");
                    long networkActivityBytes = uidValueAt.getNetworkActivityBytes(0, i2);
                    long networkActivityBytes2 = uidValueAt.getNetworkActivityBytes(1, i2);
                    if (networkActivityBytes > 0 || networkActivityBytes2 > 0) {
                        printWriter.print("    Network: ");
                        printWriter.print(PowerCheckerUtils.formatBytesLocked(networkActivityBytes));
                        printWriter.print(" received, ");
                        printWriter.print(PowerCheckerUtils.formatBytesLocked(networkActivityBytes2));
                        printWriter.print(" sent");
                        printWriter.println();
                    }
                    int i4 = i3;
                    printTimer(printWriter, sb, uidValueAt.getAudioTurnedOnTimer(), jElapsedRealtime, i2, "Audio on");
                    printTimer(printWriter, sb, uidValueAt.getForegroundActivityTimer(), jElapsedRealtime, i2, "Foreground activities");
                    long backgroundNetworkActivityBytes = uidValueAt.getBackgroundNetworkActivityBytes(i2);
                    if (backgroundNetworkActivityBytes > 0) {
                        printWriter.print("    Background Network: ");
                        printWriter.print(PowerCheckerUtils.formatBytesLocked(backgroundNetworkActivityBytes));
                        printWriter.println();
                    }
                    printTimer(printWriter, sb, uidValueAt.getBackgroundAudioTurnedOnTimer(), jElapsedRealtime, i2, "Background Audio on");
                    i3 = i4 + 1;
                }
                printWriter.println();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void dumpLastStatsCurrent(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this) {
            this.mLog.dump(fileDescriptor, printWriter, strArr);
        }
    }

    public long getAudioOnTime(int i2) {
        long totalTimeLocked;
        synchronized (this) {
            totalTimeLocked = (this.mAudioOnTimer.getTotalTimeLocked(SystemClock.elapsedRealtime() * 1000, i2) + 500) / 1000;
        }
        return totalTimeLocked;
    }

    public int getAudioTurnedOnCount(int i2, int i3) {
        synchronized (this) {
            try {
                Uid uidStatsLocked = getUidStatsLocked(i2);
                if (uidStatsLocked.getAudioTurnedOnTimer() == null) {
                    return 0;
                }
                return uidStatsLocked.getAudioTurnedOnTimer().getCountLocked(i3);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public long getAudioTurnedOnTime(int i2, int i3) {
        synchronized (this) {
            try {
                Uid uidStatsLocked = getUidStatsLocked(i2);
                long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
                if (uidStatsLocked.getAudioTurnedOnTimer() == null) {
                    return 0L;
                }
                return (uidStatsLocked.getAudioTurnedOnTimer().getTotalTimeLocked(jElapsedRealtime, i3) + 500) / 1000;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public int getBackgroundAudioTurnedOnCount(int i2, int i3) {
        synchronized (this) {
            try {
                Uid uidStatsLocked = getUidStatsLocked(i2);
                if (uidStatsLocked.getBackgroundAudioTurnedOnTimer() == null) {
                    return 0;
                }
                return uidStatsLocked.getBackgroundAudioTurnedOnTimer().getCountLocked(i3);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public long getBackgroundAudioTurnedOnTime(int i2, int i3) {
        synchronized (this) {
            try {
                Uid uidStatsLocked = getUidStatsLocked(i2);
                long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
                if (uidStatsLocked.getBackgroundAudioTurnedOnTimer() == null) {
                    return 0L;
                }
                return (uidStatsLocked.getBackgroundAudioTurnedOnTimer().getTotalTimeLocked(jElapsedRealtime, i3) + 500) / 1000;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public long getBackgroundNetworkActivityBytes(int i2, int i3) {
        long backgroundNetworkActivityBytes;
        synchronized (this) {
            backgroundNetworkActivityBytes = getUidStatsLocked(i2).getBackgroundNetworkActivityBytes(i3);
        }
        return backgroundNetworkActivityBytes;
    }

    public int getForegroundActivityCount(int i2, int i3) {
        synchronized (this) {
            try {
                Uid uidStatsLocked = getUidStatsLocked(i2);
                if (uidStatsLocked.getForegroundActivityTimer() == null) {
                    return 0;
                }
                return uidStatsLocked.getForegroundActivityTimer().getCountLocked(i3);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public long getForegroundActivityTime(int i2, int i3) {
        synchronized (this) {
            try {
                Uid uidStatsLocked = getUidStatsLocked(i2);
                long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
                if (uidStatsLocked.getForegroundActivityTimer() == null) {
                    return 0L;
                }
                return (uidStatsLocked.getForegroundActivityTimer().getTotalTimeLocked(jElapsedRealtime, i3) + 500) / 1000;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public long getNetworkActivityBytes(int i2, int i3) {
        long networkActivityBytes;
        synchronized (this) {
            Uid uidStatsLocked = getUidStatsLocked(i2);
            networkActivityBytes = uidStatsLocked.getNetworkActivityBytes(0, i3) + uidStatsLocked.getNetworkActivityBytes(1, i3);
        }
        return networkActivityBytes;
    }

    public SparseArray<Uid> getUidStats() {
        return this.mUidStats;
    }

    public Uid getUidStatsLocked(int i2) {
        Uid uid = this.mUidStats.get(i2);
        if (uid != null) {
            return uid;
        }
        Uid uid2 = new Uid(this, i2);
        this.mUidStats.put(i2, uid2);
        return uid2;
    }

    public boolean hasUidStats(int i2) {
        return this.mUidStats.get(i2) != null;
    }

    public void noteActivityPausedLocked(int i2) {
        getUidStatsLocked(i2).noteActivityPausedLocked(this.mClocks.elapsedRealtime());
    }

    public void noteActivityResumedLocked(int i2) {
        getUidStatsLocked(i2).noteActivityResumedLocked(this.mClocks.elapsedRealtime());
    }

    public void noteAudioOffLocked(int i2) {
        if (this.mAudioOnNesting == 0) {
            return;
        }
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        int i3 = this.mAudioOnNesting - 1;
        this.mAudioOnNesting = i3;
        if (i3 == 0) {
            this.mAudioOnTimer.stopRunningLocked(jElapsedRealtime);
        }
        getUidStatsLocked(i2).noteAudioTurnedOffLocked(jElapsedRealtime);
    }

    public void noteAudioOnLocked(int i2) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        if (this.mAudioOnNesting == 0) {
            this.mAudioOnTimer.startRunningLocked(jElapsedRealtime);
        }
        this.mAudioOnNesting++;
        getUidStatsLocked(i2).noteAudioTurnedOnLocked(jElapsedRealtime);
    }

    public void noteBackgroundAudioOffLocked(int i2) {
        getUidStatsLocked(i2).noteBackgroundAudioTurnedOffLocked(this.mClocks.elapsedRealtime());
    }

    public void noteBackgroundAudioOnLocked(int i2) {
        getUidStatsLocked(i2).noteBackgroundAudioTurnedOnLocked(this.mClocks.elapsedRealtime());
    }

    public void noteBackgroundNetworkActivityLocked(int i2, long j2) {
        getUidStatsLocked(i2).noteBackgroundNetworkActivityLocked(j2);
    }

    public void noteForegroundActivitiesChanged(int i2, boolean z2) {
        synchronized (this) {
            noteForegroundActivitiesChangedLocked(i2, z2);
        }
    }

    public void noteForegroundActivitiesChangedLocked(int i2, boolean z2) {
        long j2 = this.mTmpNetworkStatsArray.get(i2);
        long uidRxBytes = TrafficStats.getUidRxBytes(i2) + TrafficStats.getUidTxBytes(i2);
        long j3 = uidRxBytes - j2;
        if (DEBUG) {
            Log.d(TAG, "uid = " + i2 + ", uidForeground = " + z2 + ", lastBytes = " + j2 + ", curBytes = " + uidRxBytes + ", deltaBytes = " + j3);
        }
        this.mTmpNetworkStatsArray.put(i2, uidRxBytes);
        if (!z2) {
            noteActivityPausedLocked(i2);
        } else {
            noteBackgroundNetworkActivityLocked(i2, j3);
            noteActivityResumedLocked(i2);
        }
    }

    public void notifyAudioStatusChanged(int i2, boolean z2) {
        synchronized (this) {
            notifyAudioStatusChangedLocked(i2, z2);
        }
    }

    public void notifyAudioStatusChangedLocked(int i2, boolean z2) {
        if (DEBUG) {
            Log.d(TAG, "notifyAudioStatusChangedLocked, uid = " + i2 + ", audioOn = " + z2);
        }
        if (z2) {
            this.mAudioOnAppIds.put(i2, true);
            noteAudioOnLocked(i2);
            noteBackgroundAudioOnLocked(i2);
        } else {
            this.mAudioOnAppIds.put(i2, false);
            noteAudioOffLocked(i2);
            noteBackgroundAudioOffLocked(i2);
        }
    }

    public void resetAllStatsCurrent(int i2, boolean z2) {
        synchronized (this) {
            try {
                logLastStatsCurrent();
                readLocked();
                long jUptimeMillis = this.mClocks.uptimeMillis() * 1000;
                long jElapsedRealtime = 1000 * this.mClocks.elapsedRealtime();
                updateTimeBasesLocked(this.mOnBattery, jUptimeMillis, jElapsedRealtime);
                updateDummyTimeBasesLocked(this.mDummyOnBattery, jUptimeMillis, jElapsedRealtime);
                if (i2 >= 0) {
                    noteForegroundActivitiesChangedLocked(i2, z2);
                }
                int size = this.mAudioOnAppIds.size();
                for (int i3 = 0; i3 < size; i3++) {
                    int iKeyAt = this.mAudioOnAppIds.keyAt(i3);
                    if (this.mAudioOnAppIds.valueAt(i3)) {
                        noteAudioOnLocked(iKeyAt);
                        noteBackgroundAudioOnLocked(iKeyAt);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void setBatteryState(boolean z2) {
        synchronized (this) {
            setBatteryStateLocked(z2);
        }
    }

    public void setDummyState(boolean z2) {
        synchronized (this) {
            setDummyStateLocked(z2);
        }
    }

    public void setEnabledState(boolean z2, boolean z3) {
        synchronized (this) {
            setEnabledStateLocked(z2, z3);
        }
    }

    public void updateDummyTimeBasesLocked(boolean z2, long j2, long j3) {
        this.mDummyTimeBase.setRunning(z2, j2, j3);
    }

    public void updateExternalStats() {
        synchronized (this) {
            updateNetworkStatsLocked();
            updateBackgroundNetworkStatsLocked();
        }
    }

    public void updateTimeBasesLocked(boolean z2, long j2, long j3) {
        this.mOnBatteryTimeBase.setRunning(z2, j2, j3);
    }

    public long getNetworkActivityTotalBytes(int i2) {
        long networkActivityTotalBytes;
        synchronized (this) {
            networkActivityTotalBytes = getNetworkActivityTotalBytes(0, i2) + getNetworkActivityTotalBytes(1, i2);
        }
        return networkActivityTotalBytes;
    }
}
