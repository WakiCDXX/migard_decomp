package com.miui.powerkeeper.powerchecker;

import android.annotation.NonNull;
import android.util.ArrayMap;
import android.util.SparseArray;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UidStatsData {
    private long[] cpuFreqTimes;
    private Timer fgActivityTimer;
    private long fullWifiLockTime;
    private List<Timer> jobTimer;
    private long[] networkActivityBytes;
    private long[] networkActivityPackets;
    private ArrayMap<String, Integer> pkgWakeUpAlarmCount;
    private List<Proc> procList;
    private Timer radioActiveTimer;
    private SparseArray<Timer> sensorTimer;
    private List<Timer> syncTimer;
    private long systemCpuTimeUs;
    private int uid;
    private long userCpuTimeUs;
    private List<WakeLock> wakeLockList;
    private long wifiScanTime;

    public static final class Builder {
        private long[] cpuFreqTimes;
        private Timer fgActivityTimer;
        private long fullWifiLockTime;
        private List<Timer> jobTimer;
        private long[] networkActivityBytes = new long[10];
        private long[] networkActivityPackets = new long[10];
        private ArrayMap<String, Integer> pkgWakeUpAlarmCount;
        private List<Proc> procList;
        private Timer radioActiveTimer;
        private SparseArray<Timer> sensorTimer;
        private List<Timer> syncTimer;
        private long systemCpuTimeUs;
        private int uid;
        private long userCpuTimeUs;
        private List<WakeLock> wakeLockList;
        private long wifiScanTime;

        public Builder(int i2) {
            this.uid = i2;
        }

        public UidStatsData build() {
            return new UidStatsData(this);
        }

        public Builder cpuFreqTimes(long[] jArr) {
            this.cpuFreqTimes = jArr;
            return this;
        }

        public Builder fgActivityTimer(Timer timer) {
            this.fgActivityTimer = timer;
            return this;
        }

        public Builder fullWifiLockTime(long j2) {
            this.fullWifiLockTime = j2;
            return this;
        }

        public Builder jobTimer(List<Timer> list) {
            this.jobTimer = list;
            return this;
        }

        public Builder networkActivityBytes(long[] jArr) {
            this.networkActivityBytes = jArr;
            return this;
        }

        public Builder networkActivityPackets(long[] jArr) {
            this.networkActivityPackets = jArr;
            return this;
        }

        public Builder pkgWakeUpAlarmCount(ArrayMap<String, Integer> arrayMap) {
            this.pkgWakeUpAlarmCount = arrayMap;
            return this;
        }

        public Builder procList(List<Proc> list) {
            this.procList = list;
            return this;
        }

        public Builder radioActiveTimer(Timer timer) {
            this.radioActiveTimer = timer;
            return this;
        }

        public Builder sensorTimer(SparseArray<Timer> sparseArray) {
            this.sensorTimer = sparseArray;
            return this;
        }

        public Builder syncTimer(List<Timer> list) {
            this.syncTimer = list;
            return this;
        }

        public Builder systemCpuTimeUs(long j2) {
            this.systemCpuTimeUs = j2;
            return this;
        }

        public Builder userCpuTimeUs(long j2) {
            this.userCpuTimeUs = j2;
            return this;
        }

        public Builder wakeTime(List<WakeLock> list) {
            this.wakeLockList = list;
            return this;
        }

        public Builder wifiScanTime(long j2) {
            this.wifiScanTime = j2;
            return this;
        }
    }

    public static class Proc {
        public long procFgTime;
        public String procName;
        public long procSystemTime;
        public long procUserTime;

        public Proc(String str, long j2, long j3, long j4) {
            this.procName = str;
            this.procUserTime = j2;
            this.procSystemTime = j3;
            this.procFgTime = j4;
        }
    }

    public static class Timer {
        public int count;
        public String name;
        public long totalTime;

        public Timer() {
        }

        public Timer(String str, long j2, int i2) {
            this.name = str;
            this.totalTime = j2;
            this.count = i2;
        }
    }

    public static class WakeLock {
        public int wlFullCount;
        public long wlFullTime;
        public String wlName;
        public int wlPartialCount;
        public long wlPartialTime;
        public int wlWindowCount;
        public long wlWindowTime;

        public WakeLock(String str, long j2, int i2, long j3, int i3, long j4, int i4) {
            this.wlName = str;
            this.wlFullTime = j2;
            this.wlFullCount = i2;
            this.wlPartialTime = j3;
            this.wlPartialCount = i3;
            this.wlWindowTime = j4;
            this.wlWindowCount = i4;
        }
    }

    public long[] getCpuFreqTimes() {
        return this.cpuFreqTimes;
    }

    public Timer getFgActivityTimer() {
        return this.fgActivityTimer;
    }

    public long getFullWifiLockTime() {
        return this.fullWifiLockTime;
    }

    public List<Timer> getJobTimer() {
        return this.jobTimer;
    }

    public long getNetworkActivityBytes(int i2) {
        if (i2 < 0) {
            return 0L;
        }
        long[] jArr = this.networkActivityBytes;
        if (i2 < jArr.length) {
            return jArr[i2];
        }
        return 0L;
    }

    public long getNetworkActivityPackets(int i2) {
        if (i2 < 0) {
            return 0L;
        }
        long[] jArr = this.networkActivityPackets;
        if (i2 < jArr.length) {
            return jArr[i2];
        }
        return 0L;
    }

    public ArrayMap<String, Integer> getPkgWakeUpAlarmCount() {
        return this.pkgWakeUpAlarmCount;
    }

    public List<Proc> getProcList() {
        return this.procList;
    }

    public Timer getRadioActiveTimer() {
        return this.radioActiveTimer;
    }

    public SparseArray<Timer> getSensorTimer() {
        return this.sensorTimer;
    }

    public List<Timer> getSyncTimer() {
        return this.syncTimer;
    }

    public long getSystemCpuTimeUs() {
        return this.systemCpuTimeUs;
    }

    public int getUid() {
        return this.uid;
    }

    public long getUserCpuTimeUs() {
        return this.userCpuTimeUs;
    }

    public List<WakeLock> getWakeLockList() {
        return this.wakeLockList;
    }

    public long getWifiScanTime() {
        return this.wifiScanTime;
    }

    public String toString() {
        return "UidStatsData{uid=" + this.uid + ", userCpuTimeUs=" + this.userCpuTimeUs + ", systemCpuTimeUs=" + this.systemCpuTimeUs + ", cpuFreqTimes=" + Arrays.toString(this.cpuFreqTimes) + ", fgActivityTimer=" + this.fgActivityTimer + ", procList=" + this.procList + ", wakeLockList=" + this.wakeLockList + ", syncTimer=" + this.syncTimer + ", jobTimer=" + this.jobTimer + ", sensorTimer=" + this.sensorTimer + ", fullWifiLockTime=" + this.fullWifiLockTime + ", wifiScanTime=" + this.wifiScanTime + ", pkgWakeUpAlarmCount=" + this.pkgWakeUpAlarmCount + ", radioActiveTimer=" + this.radioActiveTimer + ", networkActivityBytes=" + Arrays.toString(this.networkActivityBytes) + ", networkActivityPackets=" + Arrays.toString(this.networkActivityPackets) + '}';
    }

    private UidStatsData(@NonNull Builder builder) {
        this.uid = builder.uid;
        this.userCpuTimeUs = builder.userCpuTimeUs;
        this.systemCpuTimeUs = builder.systemCpuTimeUs;
        this.cpuFreqTimes = builder.cpuFreqTimes;
        this.fgActivityTimer = builder.fgActivityTimer;
        this.procList = builder.procList;
        this.wakeLockList = builder.wakeLockList;
        this.syncTimer = builder.syncTimer;
        this.jobTimer = builder.jobTimer;
        this.sensorTimer = builder.sensorTimer;
        this.fullWifiLockTime = builder.fullWifiLockTime;
        this.wifiScanTime = builder.wifiScanTime;
        this.pkgWakeUpAlarmCount = builder.pkgWakeUpAlarmCount;
        this.radioActiveTimer = builder.radioActiveTimer;
        this.networkActivityBytes = builder.networkActivityBytes;
        this.networkActivityPackets = builder.networkActivityPackets;
    }
}
