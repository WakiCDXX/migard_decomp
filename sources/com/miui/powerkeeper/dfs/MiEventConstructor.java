package com.miui.powerkeeper.dfs;

import android.util.IndentingPrintWriter;
import android.util.Log;
import com.miui.misight.MiEvent;
import com.miui.misight.MiSight;
import com.miui.powerkeeper.utils.Utils;
import java.text.DateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class MiEventConstructor {
    public static final int REPORT_HISTORY_SIZE;
    public static final String TAG = "DFS-MiEventConstructor";
    private static MiEventConstructor sInstance;
    private final ArrayDeque<String> reportHistory = new ArrayDeque<>();

    static {
        REPORT_HISTORY_SIZE = DfsUtils.DEBUG ? 30 : 10;
        sInstance = null;
    }

    private MiEventConstructor() {
    }

    private void constructAodEvent(ArrayList<MiEvent> arrayList, StandbyAbnormalData standbyAbnormalData) {
        if (standbyAbnormalData.getAod() > 0) {
            MiEvent miEvent = new MiEvent(DfsUtils.EVENT_ID_AOD);
            miEvent.addBool("isRestore", false);
            miEvent.addInt("avgCurrent", standbyAbnormalData.getAvgCurrent());
            arrayList.add(miEvent);
        }
    }

    private void constructDeviceIdle(ArrayList<MiEvent> arrayList, StandbyAbnormalData standbyAbnormalData) {
        if (standbyAbnormalData.getmDeviceIdleRadio() >= 0) {
            MiEvent miEvent = new MiEvent(DfsUtils.EVENT_ID_DOZE);
            miEvent.addInt("idle_ratio_deep", standbyAbnormalData.getmDeviceIdleDeepRadio());
            miEvent.addInt("idle_ratio_all", standbyAbnormalData.getmDeviceIdleRadio());
            miEvent.addInt("screen_off_time", (int) (standbyAbnormalData.mSamplingTimeMs / 1000));
            arrayList.add(miEvent);
        }
    }

    private void constructLogEvent(ArrayList<MiEvent> arrayList, StandbyAbnormalData standbyAbnormalData) {
        String[] logTypes = standbyAbnormalData.getLogTypes();
        if (logTypes == null || logTypes.length == 0) {
            return;
        }
        MiEvent miEvent = new MiEvent(DfsUtils.EVENT_ID_LOG);
        miEvent.addStrArray("logTypes", logTypes);
        arrayList.add(miEvent);
    }

    private void constructOtherEvent(ArrayList<MiEvent> arrayList, StandbyAbnormalData standbyAbnormalData) {
        MiEvent miEvent = new MiEvent(DfsUtils.EVENT_ID_OTHER);
        miEvent.addInt("avgCurrent", standbyAbnormalData.getAvgCurrent());
        arrayList.add(miEvent);
    }

    private void constructSignalStrength(ArrayList<MiEvent> arrayList, StandbyAbnormalData standbyAbnormalData, boolean z2) {
        int i2 = z2 ? DfsUtils.EVENT_ID_WIFI_SIGNAL_POOR : DfsUtils.EVENT_ID_PHONE_SIGNAL_POOR;
        if (standbyAbnormalData.getSignalStrengthData(z2) != null) {
            MiEvent miEvent = new MiEvent(i2);
            miEvent.addInt("duration", (int) standbyAbnormalData.getSignalStrengthData(z2).totalTimeS);
            miEvent.addInt("poor_duration", (int) standbyAbnormalData.getSignalStrengthData(z2).poorTimeS);
            arrayList.add(miEvent);
        }
    }

    private void constructWakelockEvent(ArrayList<MiEvent> arrayList, StandbyAbnormalData standbyAbnormalData) {
        HashMap<Integer, StandbyAbnormalData.WakelockData> wakelocks = standbyAbnormalData.getWakelocks();
        for (Integer num : wakelocks.keySet()) {
            StandbyAbnormalData.WakelockData wakelockData = wakelocks.get(num);
            if (wakelockData != null && !wakelockData.wakelockSource.isEmpty()) {
                int size = wakelockData.wakelockSource.size();
                MiEvent[] miEventArr = new MiEvent[size];
                for (int i2 = 0; i2 < size; i2++) {
                    DfsUtils.TimerEntry timerEntry = wakelockData.wakelockSource.get(i2);
                    MiEvent miEvent = new MiEvent(DfsUtils.EVENT_ID_SUBCLASS);
                    if (num.intValue() == 903111401) {
                        miEvent.addStr("pkgName", DfsUtils.getPackageForUid(timerEntry.id));
                    } else {
                        miEvent.addStr("pkgName", "kernelWakelock");
                    }
                    miEvent.addStr("tag", timerEntry.name);
                    miEvent.addInt("count", timerEntry.count);
                    miEvent.addInt("duration", (int) (timerEntry.timeUs / 1000));
                    miEventArr[i2] = miEvent;
                }
                MiEvent miEvent2 = new MiEvent(num.intValue());
                if (num.intValue() == 903111401) {
                    miEvent2.addMiEventArray("partialWakelocks", miEventArr);
                    miEvent2.addStr("wakelockPkgs", wakelockData.wakelockPkgs);
                } else {
                    miEvent2.addMiEventArray("kernelWakelocks", miEventArr);
                }
                arrayList.add(miEvent2);
            }
        }
    }

    private void constructWakeupReasonEvent(ArrayList<MiEvent> arrayList, StandbyAbnormalData standbyAbnormalData) {
        HashMap<Integer, StandbyAbnormalData.WakeupData> wakeupReasons = standbyAbnormalData.getWakeupReasons();
        Iterator<Integer> it = wakeupReasons.keySet().iterator();
        while (it.hasNext()) {
            StandbyAbnormalData.WakeupData wakeupData = wakeupReasons.get(it.next());
            if (wakeupData != null && !wakeupData.wakeupSource.isEmpty()) {
                int size = wakeupData.wakeupSource.size();
                MiEvent[] miEventArr = new MiEvent[size];
                for (int i2 = 0; i2 < size; i2++) {
                    DfsUtils.TimerEntry timerEntry = wakeupData.wakeupSource.get(i2);
                    MiEvent miEvent = new MiEvent(DfsUtils.EVENT_ID_SUBCLASS);
                    miEvent.addStr("tag", timerEntry.name);
                    miEvent.addInt("count", timerEntry.count);
                    miEvent.addInt("duration", (int) (timerEntry.timeUs / 1000));
                    miEventArr[i2] = miEvent;
                }
                MiEvent miEvent2 = new MiEvent(DfsUtils.EVENT_ID_WAKEUP_ALARM);
                miEvent2.addMiEventArray("wakeupSource", miEventArr);
                miEvent2.addStr("wakeupType", wakeupData.wakeupType);
                arrayList.add(miEvent2);
            }
        }
    }

    public static synchronized MiEventConstructor getInstance() {
        try {
            if (sInstance == null) {
                sInstance = new MiEventConstructor();
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("Event report history:");
        indentingPrintWriter.increaseIndent();
        Iterator<String> it = this.reportHistory.iterator();
        while (it.hasNext()) {
            indentingPrintWriter.println(it.next());
        }
        indentingPrintWriter.decreaseIndent();
    }

    public void upToMiSight(StandbyAbnormalData standbyAbnormalData) {
        ArrayList<MiEvent> arrayList = new ArrayList<>();
        switch (standbyAbnormalData.mEventId) {
            case DfsUtils.EVENT_ID_AOD /* 903111101 */:
                constructAodEvent(arrayList, standbyAbnormalData);
                break;
            case DfsUtils.EVENT_ID_LOG /* 903111201 */:
                constructLogEvent(arrayList, standbyAbnormalData);
                break;
            case DfsUtils.EVENT_ID_WAKEUP_ALARM /* 903111311 */:
                constructWakeupReasonEvent(arrayList, standbyAbnormalData);
                break;
            case DfsUtils.EVENT_ID_PARTIAL_WAKELOCK /* 903111401 */:
            case DfsUtils.EVENT_ID_KERNEL_WAKELOCK /* 903111402 */:
                constructWakelockEvent(arrayList, standbyAbnormalData);
                break;
            case DfsUtils.EVENT_ID_DOZE /* 903111412 */:
                constructDeviceIdle(arrayList, standbyAbnormalData);
                break;
            case DfsUtils.EVENT_ID_PHONE_SIGNAL_POOR /* 903111413 */:
                constructSignalStrength(arrayList, standbyAbnormalData, false);
                break;
            case DfsUtils.EVENT_ID_WIFI_SIGNAL_POOR /* 903111414 */:
                constructSignalStrength(arrayList, standbyAbnormalData, true);
                break;
            case DfsUtils.EVENT_ID_OTHER /* 903111501 */:
                constructOtherEvent(arrayList, standbyAbnormalData);
                break;
        }
        if (arrayList.isEmpty()) {
            return;
        }
        String strLogFlagsToString = DfsUtils.logFlagsToString(DfsUtils.addLogBit(1, DfsUtils.addLogBit(0, standbyAbnormalData.getLogFlags())));
        MiEvent miEvent = arrayList.get(0);
        if (miEvent == null) {
            return;
        }
        if (miEvent.getEventId() == 903111501) {
            miEvent.addStr("logRequired", DfsUtils.logFlagsToString(-1));
        } else {
            miEvent.addStr("logRequired", strLogFlagsToString);
        }
        if (Utils.isDevelopmentOrDebugVersion()) {
            this.reportHistory.addLast(DateFormat.getDateTimeInstance().format(Long.valueOf(System.currentTimeMillis())) + " " + miEvent.getEventId() + ":" + miEvent.getMiEventStr());
            if (this.reportHistory.size() > REPORT_HISTORY_SIZE) {
                this.reportHistory.removeFirst();
            }
        }
        Log.d(TAG, "upToMiSight event=" + miEvent.getEventId() + ", logRequired=" + strLogFlagsToString);
        MiSight.sendEvent(miEvent);
    }

    public void upToMiSightAppUsage(String str, float f2, float f3, long j2, ArrayList<UsageAppTracker.AbnormalReason> arrayList, int i2) {
        if (arrayList.isEmpty()) {
            return;
        }
        MiEvent[] miEventArr = new MiEvent[arrayList.size()];
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            MiEvent miEvent = new MiEvent(DfsUtils.EVENT_ID_SUBCLASS_APP_USAGE);
            miEvent.addInt("reasonId", arrayList.get(i3).reasonId);
            miEvent.addStr("reasonDesc", arrayList.get(i3).reasonDesc);
            miEvent.addStrArray("reasonParam", arrayList.get(i3).reasonParams);
            miEventArr[i3] = miEvent;
        }
        MiEvent miEvent2 = new MiEvent(DfsUtils.EVENT_ID_APP_USAGE);
        miEvent2.addStr("packageName", str);
        miEvent2.addFloat("standardCurrent", f2);
        miEvent2.addFloat("avgCurrent", f3);
        miEvent2.addLong("durationS", j2 / 1000);
        miEvent2.addMiEventArray("reasons", miEventArr);
        miEvent2.addStr("logRequired", DfsUtils.logFlagsToString(i2));
        MiSight.sendEvent(miEvent2);
        Log.d(TAG, "upToMiSightAppUsage done. Event: " + miEvent2.getEventId() + ", " + miEvent2.getMiEventStr());
    }

    public void upToMiSightAppUsageSignalPool(String str, float f2, String str2) {
        Log.d(TAG, "upToMiSightAppUsageSignalPool pkgName=" + str + ", current=" + f2 + ", reason=" + str2);
        MiEvent miEvent = new MiEvent(DfsUtils.EVENT_ID_APP_USAGE);
        miEvent.addStr("packageName", str);
        miEvent.addFloat("avgCurrent", f2);
        miEvent.addStr("reason", str2);
        MiSight.sendEvent(miEvent);
    }
}
