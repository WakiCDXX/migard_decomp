package com.xiaomi.channelcollection;

import com.miui.powerkeeper.dfs.DfsUtils;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes.dex */
public class CallController<T> {
    private long mCount;
    private long mFirstCall;
    private long mLastCall;
    private long mLastCallsTimeNs;
    private long mMaxCallTimeNs;
    private long mMinCallTimeNs;
    private final long mTimeWindowMs;
    private long mTotalCallstime;
    private final long safeCallTimeNsMaxInSecond;

    public CallController(long j2) {
        this(j2, 1000L);
    }

    public T call(Callable<T> callable) {
        T tCall;
        long j2 = this.mLastCallsTimeNs;
        long j3 = this.safeCallTimeNsMaxInSecond;
        if (j2 > j3) {
            long j4 = (j2 / j3) * this.mTimeWindowMs;
            this.mLastCallsTimeNs = 0L;
            if (j4 > 0) {
                try {
                    Thread.sleep(j4);
                } catch (Exception unused) {
                }
            }
        }
        long jNanoTime = System.nanoTime();
        if (this.mFirstCall <= 0) {
            this.mFirstCall = jNanoTime;
        }
        try {
            tCall = callable.call();
        } catch (Exception e2) {
            e2.printStackTrace();
            tCall = null;
        }
        long jNanoTime2 = System.nanoTime() - jNanoTime;
        this.mLastCall = System.nanoTime();
        this.mCount++;
        if (this.mMaxCallTimeNs < jNanoTime2) {
            this.mMaxCallTimeNs = jNanoTime2;
        }
        if (jNanoTime2 > 0) {
            this.mTotalCallstime += jNanoTime2;
            long j5 = this.mMinCallTimeNs;
            if (j5 == 0 || j5 > jNanoTime2) {
                this.mMinCallTimeNs = jNanoTime2;
            }
        }
        this.mLastCallsTimeNs += Math.max(jNanoTime2, 0L);
        return tCall;
    }

    public long getAvgTime() {
        long j2 = this.mTotalCallstime;
        if (j2 > 0) {
            long j3 = this.mCount;
            if (j3 > 0) {
                return j2 / j3;
            }
        }
        return 0L;
    }

    public long getCount() {
        return this.mCount;
    }

    public long getMaxCallTimeNs() {
        return this.mMaxCallTimeNs;
    }

    public long getMinCallTimeNs() {
        return this.mMinCallTimeNs;
    }

    public long getTotalDuration() {
        long j2 = this.mLastCall;
        long j3 = this.mFirstCall;
        if (j2 > j3) {
            return j2 - j3;
        }
        return 0L;
    }

    public CallController(long j2, long j3) {
        this.safeCallTimeNsMaxInSecond = j2 * DfsUtils.SECOND_TO_MICROS;
        this.mTimeWindowMs = j3;
    }
}
