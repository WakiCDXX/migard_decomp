package com.miui.powerkeeper.dfs;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import java.io.PrintWriter;
import miui.process.ForegroundInfo;
import miui.provider.Weather;

/* JADX INFO: loaded from: classes.dex */
public class UsageManager {
    public static final String TAG = "DFS-UsageManager";
    private final UsageAppTracker mAppUsageTracker;
    private h.c mBatteryConsumptionTimer;
    h.a mClock;
    Context mContext;
    long mRealtimeStartUs;
    long mRealtimeUs;
    long mStartClockTimeMs;
    long mUptimeStartUs;
    long mUptimeUs;
    private boolean mOnBattery = false;
    private boolean mScreenOff = false;
    private int mLastBatteryLevel = -1;
    private double mChargeCounterUsed = 0.0d;
    h.d mOnBatteryTimeBase = new h.d();
    h.d mOnBatteryScreenOffTimeBase = new h.d();
    h.d mScreenOnTimeBase = new h.d();
    h.d mScreenOffTimeBase = new h.d();

    public UsageManager(Context context, CloudData cloudData, Looper looper) {
        this.mContext = context;
        h.a aVar = h.a.f1865a;
        this.mClock = aVar;
        initTimes(aVar.c() * 1000, this.mClock.b() * 1000);
        this.mAppUsageTracker = new UsageAppTracker(cloudData, this, looper);
    }

    private void checkPowerUsage() {
        isPowerAbnormal();
    }

    public static int getCurTemp(Context context) {
        Intent intentRegisterReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (intentRegisterReceiver == null) {
            return 0;
        }
        return intentRegisterReceiver.getIntExtra(Weather.WeatherBaseColumns.TEMPERATURE, 0);
    }

    private boolean isPowerAbnormal() {
        long jE = this.mBatteryConsumptionTimer.e(this.mClock.b() * 1000, DfsUtils.WHICH);
        double d2 = ((this.mChargeCounterUsed * 3600000.0d) * 1000.0d) / jE;
        Log.d(TAG, "avg current=" + d2 + ", time=" + jE);
        return d2 > 30.0d;
    }

    private void reset() {
        long jB = this.mClock.b();
        this.mChargeCounterUsed = 0.0d;
        this.mBatteryConsumptionTimer.f(false, jB * 1000);
    }

    public void dump(PrintWriter printWriter, String[] strArr) {
        PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
        indentingPrintWriter.println("====Usage START=====");
        indentingPrintWriter.increaseIndent();
        UsageAppTracker usageAppTracker = this.mAppUsageTracker;
        if (usageAppTracker != null) {
            usageAppTracker.dump(indentingPrintWriter, strArr);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("====Usage END=====");
    }

    public h.a getmClock() {
        return this.mClock;
    }

    public h.d getmOnBatteryScreenOffTimeBase() {
        return this.mOnBatteryScreenOffTimeBase;
    }

    public h.d getmOnBatteryTimeBase() {
        return this.mOnBatteryTimeBase;
    }

    public h.d getmScreenOffTimeBase() {
        return this.mScreenOffTimeBase;
    }

    public h.d getmScreenOnTimeBase() {
        return this.mScreenOnTimeBase;
    }

    void initTimes(long j2, long j3) {
        this.mStartClockTimeMs = this.mClock.a();
        this.mOnBatteryTimeBase.f(j2, j3);
        this.mOnBatteryScreenOffTimeBase.f(j2, j3);
        this.mScreenOnTimeBase.f(j2, j3);
        this.mScreenOffTimeBase.f(j2, j3);
        this.mRealtimeUs = 0L;
        this.mUptimeUs = 0L;
        this.mRealtimeStartUs = j3;
        this.mUptimeStartUs = j2;
    }

    public void notifyBatteryLevelChanged(int i2) {
        if (this.mBatteryConsumptionTimer == null) {
            this.mBatteryConsumptionTimer = new h.c(this.mClock, null, 0, null, this.mOnBatteryTimeBase);
        }
        long jB = this.mClock.b();
        if (i2 % 2 == 0) {
            if (i2 > this.mLastBatteryLevel) {
                this.mLastBatteryLevel = i2;
                if (this.mBatteryConsumptionTimer.g()) {
                    this.mBatteryConsumptionTimer.j(jB);
                    reset();
                    return;
                }
                return;
            }
            if (this.mBatteryConsumptionTimer.g()) {
                this.mBatteryConsumptionTimer.j(jB);
                this.mChargeCounterUsed = BSHelper.getCurrentBatteryCap() - this.mChargeCounterUsed;
                checkPowerUsage();
                reset();
            }
            this.mBatteryConsumptionTimer.i(jB);
            this.mChargeCounterUsed = BSHelper.getCurrentBatteryCap();
        }
        this.mLastBatteryLevel = i2;
    }

    public void notifyForegroundInfoChanged(ForegroundInfo foregroundInfo) {
        this.mAppUsageTracker.notifyForegroundInfoChanged(foregroundInfo);
    }

    public void notifyOnBattery(boolean z2) {
        synchronized (this) {
            this.mOnBattery = z2;
            updateTimeBasesLocked();
            this.mAppUsageTracker.notifyOnBattery(z2);
        }
    }

    public void notifyScreenOff(boolean z2) {
        synchronized (this) {
            this.mScreenOff = z2;
            updateTimeBasesLocked();
            this.mAppUsageTracker.notifyScreenOff(z2);
        }
    }

    public void updateTimeBasesLocked() {
        String str;
        long jC = this.mClock.c() * 1000;
        long jB = 1000 * this.mClock.b();
        boolean z2 = this.mScreenOff;
        boolean z3 = this.mOnBattery != this.mOnBatteryTimeBase.g();
        boolean z4 = (this.mOnBattery && z2) != this.mOnBatteryScreenOffTimeBase.g();
        if (z4 || z3) {
            h.d dVar = this.mOnBatteryTimeBase;
            boolean z5 = this.mOnBattery;
            str = TAG;
            dVar.i(z5, jC, jB);
            boolean z6 = DfsUtils.DEBUG;
            if (z6) {
                Log.d(str, "mOnBatteryTimeBase changed. status=" + this.mOnBattery + ", runningTime=" + this.mOnBatteryTimeBase.b(jC, 0));
            }
            if (z4) {
                this.mOnBatteryScreenOffTimeBase.i(this.mOnBattery && z2, jC, jB);
                if (z6) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("mOnBatteryScreenOffTimeBase changed. status=");
                    sb.append(this.mOnBattery && z2);
                    sb.append(", runningTime=");
                    sb.append(this.mOnBatteryScreenOffTimeBase.b(jC, 0));
                    Log.d(str, sb.toString());
                }
            }
        } else {
            str = TAG;
        }
        boolean z7 = this.mScreenOff != this.mScreenOffTimeBase.g();
        boolean z8 = this.mScreenOff == this.mScreenOnTimeBase.g();
        if (z7) {
            this.mScreenOffTimeBase.i(this.mScreenOff, jC, jB);
            if (DfsUtils.DEBUG) {
                Log.d(str, "mScreenOffTimeBase changed. status=" + this.mScreenOff + ", runningTime=" + this.mScreenOffTimeBase.b(jC, 0));
            }
        }
        if (z8) {
            this.mScreenOnTimeBase.i(!this.mScreenOff, jC, jB);
            if (DfsUtils.DEBUG) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("mScreenOnTimeBase changed. status=");
                sb2.append(!this.mScreenOff);
                sb2.append(", runningTime=");
                sb2.append(this.mScreenOnTimeBase.b(jC, 0));
                Log.d(str, sb2.toString());
            }
        }
    }
}
