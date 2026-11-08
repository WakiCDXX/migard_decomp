package com.miui.powerkeeper.siming.shaosi;

import android.annotation.Nullable;
import android.os.Bundle;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.SparseArray;
import com.miui.powerkeeper.dfs.DfsUtils;
import com.miui.powerkeeper.event.EventMonitor;
import com.miui.powerkeeper.provider.GlobalFeatureConfigure;
import com.miui.powerkeeper.siming.SiUtils;
import com.miui.powerkeeper.siming.dasi.MIY;
import com.miui.powerkeeper.utils.ShellUtils;
import h.c;
import h.d;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class DeviceIdleN extends N {
    public static final int ERROR_KEY = -1;
    public static final int MAX_KEYS = 4;
    private static final int STATE_ACTIVE = 0;
    private static final int STATE_IDLE = 5;
    private static final int STATE_LIGHT_IDLE = 10;
    private static final int STATE_WAKEUP_REASON = 21;
    private static final int STATE_WAKEUP_REASON_DETAIL = 22;
    public static final String TAG = "SM_S_DeviceIdleN";
    public static final int WAKEUP_TYPE_DEEP = 1;
    public static final int WAKEUP_TYPE_LIGHT = 2;
    public static final int WAKEUP_TYPE_SUSPEND = 3;
    h.a mClock;
    private volatile boolean mDeepIdle;
    private volatile boolean mDeepMaintenance;
    int mKey;
    private volatile boolean mLightIdle;
    private boolean mOnBattery;
    public d mOnBatteryTimeBase;
    public d mScreenOffTimeBase;
    public d mSleepTimeBase;
    SparseArray<TimerCollector> mTimerCollectorArray;
    private volatile int mWaitingForAlarmReason;

    public static class TimerCollector {
        public static final int KEY_DEEP = 2;
        public static final int KEY_DEEP_IN_SLEEP = 32;
        public static final int KEY_LIGHT = 4;
        public static final int KEY_LIGHT_IN_SLEEP = 64;
        public static final int KEY_SAMPLING = 1;
        public static final int KEY_SCREEN_OFF = 8;
        public static final int KEY_SLEEP = 16;
        SparseArray<c> timerSparseArray = new SparseArray<>();
        ArrayMap<String, WakeupReason> wakeupReasonMap = new ArrayMap<>();

        public void addTimer(int i2, c cVar) {
            this.timerSparseArray.put(i2, cVar);
        }

        public void addWakeupReason(String str, int i2) {
            if (str.equals("unknown")) {
                return;
            }
            WakeupReason wakeupReason = this.wakeupReasonMap.get(str);
            if (wakeupReason == null) {
                wakeupReason = new WakeupReason();
                this.wakeupReasonMap.put(str, wakeupReason);
            }
            if (i2 == 1) {
                wakeupReason.deepCount++;
            } else if (i2 == 2) {
                wakeupReason.lightCount++;
            } else {
                wakeupReason.suspendDeep++;
            }
            wakeupReason.reason = str;
        }

        public String getKeyString(int i2) {
            if (i2 == 1) {
                return "sampling";
            }
            if (i2 == 2) {
                return "deep";
            }
            if (i2 == 4) {
                return "light";
            }
            if (i2 == 8) {
                return "screenOff";
            }
            if (i2 == 16) {
                return GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_SLEEP;
            }
            if (i2 != 32) {
                return i2 != 64 ? "unknown" : "light_in_sleep";
            }
            return "deep_in_sleep";
        }

        public c getTimer(int i2) {
            return this.timerSparseArray.get(i2);
        }

        public SparseArray<c> getTimerSparseArray() {
            return this.timerSparseArray;
        }

        public ArrayMap<String, WakeupReason> getWakeupReasonMap() {
            return this.wakeupReasonMap;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("DeviceIdle Timer:\n");
            long jB = h.a.f1865a.b();
            for (int i2 = 0; i2 < this.timerSparseArray.size(); i2++) {
                sb.append("  ");
                sb.append("key=");
                int i3 = 1 << i2;
                sb.append(getKeyString(i3));
                sb.append("  count=");
                sb.append(this.timerSparseArray.get(i3).d(0));
                sb.append("  time=");
                sb.append(this.timerSparseArray.get(i3).e(1000 * jB, 0) / DfsUtils.SECOND_TO_MICROS);
                sb.append("s\n");
            }
            for (int i4 = 0; i4 < this.wakeupReasonMap.size(); i4++) {
                sb.append("  ");
                sb.append("reason=");
                sb.append(this.wakeupReasonMap.keyAt(i4));
                sb.append("  lightCount=");
                sb.append(this.wakeupReasonMap.valueAt(i4).lightCount);
                sb.append("  deepCount=");
                sb.append(this.wakeupReasonMap.valueAt(i4).deepCount);
                sb.append("  suspendDeep=");
                sb.append(this.wakeupReasonMap.valueAt(i4).suspendDeep);
                sb.append(ShellUtils.COMMAND_LINE_END);
            }
            return sb.toString();
        }
    }

    public static class WakeupReason {
        public int deepCount;
        public int lightCount;
        public String reason;
        public int suspendDeep;
    }

    protected DeviceIdleN(Looper looper) {
        super(looper, 100);
        this.mLightIdle = false;
        this.mDeepIdle = false;
        this.mDeepMaintenance = false;
        this.mWaitingForAlarmReason = 0;
        this.mOnBattery = false;
        this.mTimerCollectorArray = new SparseArray<>(4);
        this.mKey = 0;
        this.mClock = h.a.f1865a;
        this.mOnBatteryTimeBase = new d();
        this.mScreenOffTimeBase = new d();
        this.mSleepTimeBase = new d();
        long jC = this.mClock.c() * 1000;
        long jB = this.mClock.b() * 1000;
        this.mOnBatteryTimeBase.f(jC, jB);
        this.mScreenOffTimeBase.f(jC, jB);
        this.mSleepTimeBase.f(jC, jB);
        EventMonitor.getInstance().registerCallback(4, new EventMonitor.EventCallback() { // from class: com.miui.powerkeeper.siming.shaosi.DeviceIdleN.1
            @Override // com.miui.powerkeeper.event.EventMonitor.EventCallback
            public void notifyEvent(int i2, Bundle bundle) {
                int i3 = bundle.getInt("type");
                if (i3 != 0) {
                    if (i3 == 1) {
                        String string = bundle.getString("statsTag");
                        String string2 = bundle.getString("sourcePackage");
                        SiUtils.Log.w(DeviceIdleN.TAG, "EventLog1: resId=" + i2 + ", statsTag=" + string + ", sourcePackage=" + string2);
                        DeviceIdleN deviceIdleN = DeviceIdleN.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append(string2);
                        sb.append("#");
                        sb.append(string);
                        deviceIdleN.stepState(22, sb.toString());
                        return;
                    }
                    return;
                }
                boolean z2 = bundle.getBoolean("nowIdling");
                boolean z3 = bundle.getBoolean("nowLightIdling");
                int i4 = bundle.getInt("mode");
                String string3 = bundle.getString("activeReason");
                SiUtils.Log.w(DeviceIdleN.TAG, "EventLog0: resId=" + i2 + ", nowIdling=" + z2 + ", nowLightIdling=" + z3 + ", mode=" + i4 + ", activeReason=" + string3);
                Bundle bundle2 = new Bundle();
                if (z2 && i4 == 2) {
                    DeviceIdleN.this.mDeepIdle = true;
                    DeviceIdleN.this.mLightIdle = false;
                    DeviceIdleN.this.mDeepMaintenance = false;
                    DeviceIdleN.this.stepState(5);
                    bundle2.putString(MIY.BUNDLE_PARAM_DEVICE_IDLE, "deep");
                    MIY.notify(6, bundle2);
                    return;
                }
                if (z2 && i4 == 0) {
                    DeviceIdleN.this.mDeepMaintenance = true;
                    bundle2.putString(MIY.BUNDLE_PARAM_DEVICE_IDLE, "deep_maintenance");
                    MIY.notify(6, bundle2);
                    return;
                }
                if (z3) {
                    if (i4 == 0) {
                        bundle2.putString(MIY.BUNDLE_PARAM_DEVICE_IDLE, "light_maintenance");
                    } else {
                        bundle2.putString(MIY.BUNDLE_PARAM_DEVICE_IDLE, "light");
                    }
                    MIY.notify(6, bundle2);
                    if (DeviceIdleN.this.mLightIdle) {
                        return;
                    }
                    DeviceIdleN.this.mLightIdle = true;
                    DeviceIdleN.this.stepState(10);
                    return;
                }
                if (z2 || string3 == null) {
                    DeviceIdleN.this.stepState(0);
                    DeviceIdleN.this.mDeepIdle = false;
                    DeviceIdleN.this.mLightIdle = false;
                    DeviceIdleN.this.mDeepMaintenance = false;
                    bundle2.putString(MIY.BUNDLE_PARAM_DEVICE_IDLE, "inactive");
                    MIY.notify(6, bundle2);
                    return;
                }
                DeviceIdleN.this.stepState(0);
                DeviceIdleN.this.stepState(21, string3);
                DeviceIdleN.this.mDeepIdle = false;
                DeviceIdleN.this.mLightIdle = false;
                DeviceIdleN.this.mDeepMaintenance = false;
                bundle2.putString(MIY.BUNDLE_PARAM_DEVICE_IDLE, "inactive");
                MIY.notify(6, bundle2);
            }
        });
        MIY.register(this.SI, 2, 0, 1, 3, 5);
    }

    private void notifyOnBattery(boolean z2) {
        this.mOnBattery = z2;
        SiUtils.Log.d(TAG, "charging mode changed onBattery=" + z2);
        if (z2 != this.mOnBatteryTimeBase.g()) {
            this.mOnBatteryTimeBase.i(z2, this.mClock.c() * 1000, this.mClock.b() * 1000);
        }
        if (z2) {
            return;
        }
        noteSleepModeChanged(false);
        if (this.mScreenOffTimeBase.g()) {
            this.mScreenOffTimeBase.i(false, this.mClock.c() * 1000, this.mClock.b() * 1000);
        }
    }

    private void notifyScreenOff() {
        SiUtils.Log.d(TAG, "Screen off");
        if (this.mScreenOffTimeBase.g()) {
            return;
        }
        this.mScreenOffTimeBase.i(this.mOnBattery, this.mClock.c() * 1000, 1000 * this.mClock.b());
    }

    private void notifyScreenOn() {
        SiUtils.Log.d(TAG, "Screen on");
        noteSleepModeChanged(false);
        this.mWaitingForAlarmReason = 0;
    }

    private void notifyUserPresent() {
        SiUtils.Log.d(TAG, "User present");
        if (this.mScreenOffTimeBase.g()) {
            this.mScreenOffTimeBase.i(false, this.mClock.c() * 1000, this.mClock.b() * 1000);
        }
    }

    private void startTimer(TimerCollector timerCollector, int i2) {
        SiUtils.Log.d(TAG, "start Timer " + String.format(Locale.getDefault(), "0#%x", Integer.valueOf(i2)));
        long jB = this.mClock.b();
        if ((i2 & 1) != 0) {
            startTimerIfNeeded(timerCollector.getTimer(1), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(1) + " isRuning=" + timerCollector.getTimer(1).g());
        }
        if ((i2 & 2) != 0) {
            startTimerIfNeeded(timerCollector.getTimer(2), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(2) + " isRuning=" + timerCollector.getTimer(2).g());
        }
        if ((i2 & 4) != 0) {
            startTimerIfNeeded(timerCollector.getTimer(4), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(4) + " isRuning=" + timerCollector.getTimer(4).g());
        }
        if ((i2 & 8) != 0) {
            startTimerIfNeeded(timerCollector.getTimer(8), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(8) + " isRuning=" + timerCollector.getTimer(8).g());
        }
        if ((i2 & 16) != 0) {
            startTimerIfNeeded(timerCollector.getTimer(16), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(16) + " isRuning=" + timerCollector.getTimer(16).g());
        }
        if ((i2 & 32) != 0 && timerCollector.getTimer(2).g()) {
            startTimerIfNeeded(timerCollector.getTimer(32), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(32) + " isRuning=" + timerCollector.getTimer(32).g());
        }
        if ((i2 & 64) == 0 || !timerCollector.getTimer(4).g()) {
            return;
        }
        startTimerIfNeeded(timerCollector.getTimer(64), jB);
        SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(64) + " isRuning=" + timerCollector.getTimer(64).g());
    }

    private void startTimerIfNeeded(c cVar, long j2) {
        if (cVar.g()) {
            return;
        }
        SiUtils.Log.d(TAG, "really start Timer ");
        cVar.i(j2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stepState(int i2) {
        stepState(i2, null);
    }

    private void stopTimer(TimerCollector timerCollector, int i2) {
        SiUtils.Log.d(TAG, "stop Timer " + String.format(Locale.getDefault(), "0#%x", Integer.valueOf(i2)));
        long jB = this.mClock.b();
        if ((i2 & 1) != 0) {
            stopTimerIfNeeded(timerCollector.getTimer(1), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(1) + " isRuning=" + timerCollector.getTimer(1).g());
        }
        if ((i2 & 2) != 0) {
            stopTimerIfNeeded(timerCollector.getTimer(2), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(2) + " isRuning=" + timerCollector.getTimer(2).g());
        }
        if ((i2 & 4) != 0) {
            stopTimerIfNeeded(timerCollector.getTimer(4), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(4) + " isRuning=" + timerCollector.getTimer(4).g());
        }
        if ((i2 & 8) != 0) {
            stopTimerIfNeeded(timerCollector.getTimer(8), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(8) + " isRuning=" + timerCollector.getTimer(8).g());
        }
        if ((i2 & 16) != 0) {
            stopTimerIfNeeded(timerCollector.getTimer(16), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(16) + " isRuning=" + timerCollector.getTimer(16).g());
        }
        if ((i2 & 32) != 0) {
            stopTimerIfNeeded(timerCollector.getTimer(32), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(32) + " isRuning=" + timerCollector.getTimer(32).g());
        }
        if ((i2 & 64) != 0) {
            stopTimerIfNeeded(timerCollector.getTimer(64), jB);
            SiUtils.Log.d(TAG, "Timer " + timerCollector.getKeyString(64) + " isRuning=" + timerCollector.getTimer(64).g());
        }
    }

    private void stopTimerIfNeeded(c cVar, long j2) {
        if (cVar.g()) {
            cVar.j(j2);
        }
    }

    private void writeDetailReasonIfNeeded(TimerCollector timerCollector, String str) {
        if (this.mWaitingForAlarmReason > 0) {
            timerCollector.addWakeupReason(str, this.mWaitingForAlarmReason);
            this.mWaitingForAlarmReason = 0;
        }
    }

    private void writeOffReasonIfNeeded(TimerCollector timerCollector, String str) {
        SiUtils.Log.d(TAG, "writeOffReasonIfNeeded msg=" + str);
        if (this.mDeepIdle || this.mDeepMaintenance) {
            if (str.equals("alarm")) {
                this.mWaitingForAlarmReason = 1;
                return;
            } else {
                timerCollector.addWakeupReason(str, 1);
                return;
            }
        }
        if (!this.mLightIdle) {
            SiUtils.Log.d(TAG, "writeOffReasonIfNeeded State maybe error.");
        } else if (str.equals("alarm")) {
            this.mWaitingForAlarmReason = 3;
        } else {
            timerCollector.addWakeupReason(str, 2);
        }
    }

    public void deleteTracking(int i2) {
        this.mTimerCollectorArray.delete(i2);
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S, com.miui.powerkeeper.siming.IDivision
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i2 = 0; i2 < this.mTimerCollectorArray.size(); i2++) {
            printWriter.println(this.mTimerCollectorArray.valueAt(i2));
        }
    }

    @Nullable
    public TimerCollector getCollector(int i2) {
        return this.mTimerCollectorArray.get(i2, null);
    }

    public void noteSleepModeChanged(boolean z2) {
        SiUtils.Log.d(TAG, "sleep mode changed inSleep=" + z2);
        if (z2 != this.mSleepTimeBase.g()) {
            this.mSleepTimeBase.i(z2, this.mClock.c() * 1000, this.mClock.b() * 1000);
        }
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
            return;
        }
        if (i2 == 2) {
            notifyOnBattery(!bundle.getBoolean(MIY.BUNDLE_PARAM_CHARGING));
        } else if (i2 == 3) {
            notifyUserPresent();
        } else {
            if (i2 != 5) {
                return;
            }
            noteSleepModeChanged(bundle.getBoolean(MIY.BUNDLE_PARAM_SLEEP));
        }
    }

    public void resetTracking(int i2) {
        TimerCollector timerCollector = this.mTimerCollectorArray.get(i2, null);
        if (timerCollector == null) {
            return;
        }
        stopTracking(i2);
        long jB = this.mClock.b();
        for (int i3 = 0; i3 < timerCollector.timerSparseArray.size(); i3++) {
            timerCollector.timerSparseArray.valueAt(i3).f(false, 1000 * jB);
        }
        timerCollector.wakeupReasonMap.clear();
        timerCollector.getTimer(1).i(jB);
        timerCollector.getTimer(8).i(jB);
        timerCollector.getTimer(16).i(jB);
        if (this.mDeepIdle) {
            timerCollector.getTimer(2).i(jB);
            timerCollector.getTimer(32).i(jB);
        } else if (this.mLightIdle) {
            timerCollector.getTimer(4).i(jB);
            timerCollector.getTimer(64).i(jB);
        }
    }

    public int startTracking() {
        if (this.mKey >= 4) {
            return -1;
        }
        TimerCollector timerCollector = new TimerCollector();
        timerCollector.addTimer(1, new c(this.mClock, null, -1, null, this.mOnBatteryTimeBase));
        timerCollector.addTimer(2, new c(this.mClock, null, -2, null, this.mScreenOffTimeBase));
        timerCollector.addTimer(4, new c(this.mClock, null, -3, null, this.mScreenOffTimeBase));
        timerCollector.addTimer(8, new c(this.mClock, null, -4, null, this.mScreenOffTimeBase));
        timerCollector.addTimer(16, new c(this.mClock, null, -5, null, this.mSleepTimeBase));
        timerCollector.addTimer(32, new c(this.mClock, null, -6, null, this.mSleepTimeBase));
        timerCollector.addTimer(64, new c(this.mClock, null, -7, null, this.mSleepTimeBase));
        this.mTimerCollectorArray.put(this.mKey, timerCollector);
        timerCollector.getTimer(1).i(this.mClock.b());
        timerCollector.getTimer(8).i(this.mClock.b());
        timerCollector.getTimer(16).i(this.mClock.b());
        int i2 = this.mKey;
        this.mKey = i2 + 1;
        return i2;
    }

    public void stopTracking(int i2) {
        TimerCollector timerCollector = this.mTimerCollectorArray.get(i2, null);
        if (timerCollector == null) {
            return;
        }
        for (int i3 = 0; i3 < timerCollector.timerSparseArray.size(); i3++) {
            timerCollector.timerSparseArray.valueAt(i3).j(this.mClock.b());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stepState(int i2, String str) {
        SiUtils.Log.d(TAG, "stepState state=" + i2);
        for (int i3 = 0; i3 < 4; i3++) {
            TimerCollector timerCollector = this.mTimerCollectorArray.get(i3, null);
            if (timerCollector != null) {
                if (i2 == 0) {
                    stopTimer(timerCollector, 102);
                } else if (i2 == 5) {
                    startTimer(timerCollector, 34);
                    stopTimer(timerCollector, 68);
                } else if (i2 == 10) {
                    startTimer(timerCollector, 68);
                } else if (i2 == 21) {
                    writeOffReasonIfNeeded(timerCollector, str);
                } else if (i2 == 22) {
                    writeDetailReasonIfNeeded(timerCollector, str);
                }
            }
        }
    }
}
