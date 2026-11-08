package com.miui.powerkeeper.batterylife.monitor;

import android.annotation.Nullable;
import android.os.SystemClock;
import android.util.ArrayMap;
import com.miui.powerkeeper.batterylife.TrackData;
import com.miui.powerkeeper.dfs.DfsUtils;
import com.miui.powerkeeper.logsystem.utils.GsonUtils;
import com.miui.powerkeeper.siming.SiMing;
import com.miui.powerkeeper.siming.shaosi.DeviceIdleN;
import h.a;
import h.c;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class DeviceIdle implements Resource {
    private boolean bOutOfServer;
    DeviceIdleN mDeviceIdleN;
    int mKey;

    public DeviceIdle() {
        this.mKey = -1;
        this.bOutOfServer = false;
        DeviceIdleN deviceIdleN = (DeviceIdleN) SiMing.getInstance().getDivision(100);
        this.mDeviceIdleN = deviceIdleN;
        if (deviceIdleN != null) {
            this.mKey = deviceIdleN.startTracking();
        }
        if (this.mKey == -1) {
            this.bOutOfServer = true;
        }
    }

    private void printTimerStatus(PrintWriter printWriter, DeviceIdleN.TimerCollector timerCollector) {
        if (timerCollector != null) {
            long jB = a.f1865a.b() * 1000;
            printWriter.println("Timer running status:");
            printWriter.print("  " + timerCollector.getKeyString(1) + ":  ");
            printWriter.println(timerCollector.getTimer(1).g() ? "running" : "stopped");
            printWriter.print("  " + timerCollector.getKeyString(2) + ":  ");
            printWriter.println(timerCollector.getTimer(2).g() ? "running" : "stopped");
            printWriter.print("  " + timerCollector.getKeyString(4) + ":  ");
            printWriter.println(timerCollector.getTimer(4).g() ? "running" : "stopped");
            printWriter.print("  " + timerCollector.getKeyString(8) + ":  ");
            printWriter.println(timerCollector.getTimer(8).g() ? "running" : "stopped");
            printWriter.print("  " + timerCollector.getKeyString(16) + ":  ");
            printWriter.println(timerCollector.getTimer(16).g() ? "running" : "stopped");
            printWriter.print("  " + timerCollector.getKeyString(32) + ":  ");
            printWriter.println(timerCollector.getTimer(32).g() ? "running" : "stopped");
            printWriter.print("  " + timerCollector.getKeyString(64) + ":  ");
            printWriter.println(timerCollector.getTimer(64).g() ? "running" : "stopped");
            printWriter.print("  mOnBatteryTimeBase:  ");
            StringBuilder sb = new StringBuilder();
            sb.append(this.mDeviceIdleN.mOnBatteryTimeBase.g() ? "running" : "stopped");
            sb.append(", startTime:");
            sb.append(this.mDeviceIdleN.mOnBatteryTimeBase.d());
            sb.append(", runningTime:");
            sb.append(this.mDeviceIdleN.mOnBatteryTimeBase.c(jB));
            printWriter.println(sb.toString());
            printWriter.print("  mScreenOffTimeBase:  ");
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.mDeviceIdleN.mScreenOffTimeBase.g() ? "running" : "stopped");
            sb2.append(", startTime:");
            sb2.append(this.mDeviceIdleN.mScreenOffTimeBase.d());
            sb2.append(", runningTime:");
            sb2.append(this.mDeviceIdleN.mScreenOffTimeBase.c(jB));
            printWriter.println(sb2.toString());
            printWriter.print("  mSleepTimeBase:  ");
            StringBuilder sb3 = new StringBuilder();
            sb3.append(this.mDeviceIdleN.mSleepTimeBase.g() ? "running" : "stopped");
            sb3.append(", startTime:");
            sb3.append(this.mDeviceIdleN.mSleepTimeBase.d());
            sb3.append(", runningTime:");
            sb3.append(this.mDeviceIdleN.mSleepTimeBase.c(jB));
            printWriter.println(sb3.toString());
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:35:0x0071  */
    @Override // com.miui.powerkeeper.batterylife.monitor.Resource
    public void dump(PrintWriter printWriter, String[] strArr) {
        byte b2;
        if (this.bOutOfServer) {
            printWriter.println("Out of server, please check SM.");
            return;
        }
        DeviceIdleN.TimerCollector collector = this.mDeviceIdleN.getCollector(this.mKey);
        if (strArr.length > 0) {
            String str = strArr[0];
            switch (str.hashCode()) {
                case 1509:
                    if (!str.equals("-r")) {
                        b2 = -1;
                    } else {
                        b2 = 1;
                    }
                    break;
                case 1510:
                    if (!str.equals("-s")) {
                        b2 = -1;
                    } else {
                        b2 = 3;
                    }
                    break;
                case 46910:
                    if (!str.equals("-sd")) {
                        b2 = -1;
                    } else {
                        b2 = 7;
                    }
                    break;
                case 46911:
                    if (!str.equals("-se")) {
                        b2 = -1;
                    } else {
                        b2 = 6;
                    }
                    break;
                case 46925:
                    if (!str.equals("-ss")) {
                        b2 = -1;
                    } else {
                        b2 = 5;
                    }
                    break;
                case 45099439:
                    if (!str.equals("-stop")) {
                        b2 = -1;
                    } else {
                        b2 = 2;
                    }
                    break;
                case 390478431:
                    if (!str.equals("-status")) {
                        b2 = -1;
                    } else {
                        b2 = 4;
                    }
                    break;
                case 1396715842:
                    if (!str.equals("-reset")) {
                        b2 = -1;
                    } else {
                        b2 = 0;
                    }
                    break;
                default:
                    b2 = -1;
                    break;
            }
            switch (b2) {
                case 0:
                case 1:
                    this.mDeviceIdleN.resetTracking(this.mKey);
                    break;
                case 2:
                case 3:
                    this.mDeviceIdleN.stopTracking(this.mKey);
                    break;
                case 4:
                case 5:
                    printTimerStatus(printWriter, collector);
                    break;
                case 6:
                    this.mDeviceIdleN.noteSleepModeChanged(true);
                    break;
                case 7:
                    this.mDeviceIdleN.noteSleepModeChanged(false);
                    break;
            }
            return;
        }
        printWriter.println(collector);
    }

    @Override // com.miui.powerkeeper.batterylife.monitor.Resource
    @Nullable
    public ArrayList<Map<String, Object>> getUploadData() {
        DeviceIdleN.TimerCollector collector;
        if (this.bOutOfServer || (collector = this.mDeviceIdleN.getCollector(this.mKey)) == null) {
            return null;
        }
        ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
        this.mDeviceIdleN.stopTracking(this.mKey);
        c timer = collector.getTimer(2);
        c timer2 = collector.getTimer(4);
        ArrayMap arrayMap = new ArrayMap();
        arrayMap.put(TrackData.DI_DEEP_DURATION, Long.valueOf(timer.e(SystemClock.elapsedRealtime() * 1000, 2) / DfsUtils.SECOND_TO_MICROS));
        arrayMap.put(TrackData.DI_LIGHT_DURATION, Long.valueOf(timer2.e(SystemClock.elapsedRealtime() * 1000, 2) / DfsUtils.SECOND_TO_MICROS));
        ArrayMap<String, DeviceIdleN.WakeupReason> wakeupReasonMap = collector.getWakeupReasonMap();
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = 0; i2 < wakeupReasonMap.size(); i2++) {
            ArrayMap arrayMap2 = new ArrayMap();
            arrayMap2.put(TrackData.DI_WAKEUP_REASON_NAME, wakeupReasonMap.valueAt(i2).reason);
            arrayMap2.put(TrackData.DI_WAKEUP_REASON_DEEP_COUNT, Integer.valueOf(wakeupReasonMap.valueAt(i2).deepCount));
            arrayMap2.put(TrackData.DI_WAKEUP_REASON_LIGHT_COUNT, Integer.valueOf(wakeupReasonMap.valueAt(i2).lightCount));
            arrayMap2.put(TrackData.DI_WAKEUP_REASON_SUSPEND_DEEP, Integer.valueOf(wakeupReasonMap.valueAt(i2).suspendDeep));
            arrayList2.add(arrayMap2);
        }
        arrayMap.put(TrackData.DI_WAKEUP_REASON, GsonUtils.getGson().toJson(arrayList2));
        arrayList.add(arrayMap);
        this.mDeviceIdleN.resetTracking(this.mKey);
        return arrayList;
    }

    @Override // com.miui.powerkeeper.batterylife.monitor.Resource
    public void reset() {
        if (this.bOutOfServer) {
            return;
        }
        int i2 = this.mKey;
        if (i2 != -1) {
            this.mDeviceIdleN.resetTracking(i2);
        } else {
            this.mKey = this.mDeviceIdleN.startTracking();
        }
    }
}
