package com.miui.powerkeeper.powerchecker;

import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class BatteryUsageAttribute {
    private static int BACK_HOME_HOUR_OF_DAY = 18;
    private static final boolean DEBUG = PowerCheckerService.DEBUG;
    private static int LEAST_CHARGING_TIME = 60000;
    private static int NIGHT_DURATION_HOUR = 7;
    private static int NIGHT_HOUR_START = 23;
    private static final String TAG = "PowerChecker.UsageAttr";
    private final Object mLock;
    private final PowerCheckerService mService;
    private long mTotalAverageTPP = 0;
    private long mScreenOnAverageTPP = 0;
    private long mScreenOffAverageTPP = 0;
    private long mScreenOffBottomTPP = 0;
    private long mScreenOffNightTPP = 0;
    private float mScreenOnTimeProportion = 0.0f;
    private float mScreenOffTimeProportion = 0.0f;
    private int mTotalScreenOnCount = 0;
    private int mTotalScreenOffCount = 0;
    private long mScreenOnAverageDuration = 0;
    private long mScreenOffAverageDuration = 0;
    private long mScreenOnAverageTemp = 0;
    private long mScreenOffAverageTemp = 0;
    private long mLastChargeTimeOfDay = 0;
    private int mLastPluginRemainingLevel = -1;
    private long mLastRecordStartTime = 0;
    private long mLastRecordEndTime = 0;
    private final ArrayList<BatteryTPPItem> tppRecords = new ArrayList<>();

    class BatteryTPPItem {
        BatteryUsageCondition condition;
        int endLevel;
        long endTime;
        int startLevel;
        long startTime;

        public BatteryTPPItem(BatteryUsageCondition batteryUsageCondition, long j2, long j3, int i2, int i3) {
            this.condition = new BatteryUsageCondition(batteryUsageCondition);
            this.startTime = j2;
            this.endTime = j3;
            this.startLevel = i2;
            this.endLevel = i3;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[" + this.startTime + "ms ~ " + this.endTime + "ms]");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("[");
            sb2.append(this.endTime - this.startTime);
            sb2.append("ms]");
            sb.append(sb2.toString());
            sb.append("[" + this.startLevel + "->" + this.endLevel + "]");
            sb.append(this.condition.toString());
            return sb.toString();
        }
    }

    public BatteryUsageAttribute(PowerCheckerService powerCheckerService, Object obj) {
        this.mService = powerCheckerService;
        this.mLock = obj;
    }

    private long getConditionalAverageTPP(ArrayList<BatteryUsageCondition.MatchCondition> arrayList) {
        int i2 = 0;
        long j2 = 0;
        for (BatteryTPPItem batteryTPPItem : this.tppRecords) {
            if (batteryTPPItem.startTime <= batteryTPPItem.endTime) {
                Iterator<BatteryUsageCondition.MatchCondition> it = arrayList.iterator();
                do {
                    if (!it.hasNext()) {
                        j2 += batteryTPPItem.endTime - batteryTPPItem.startTime;
                        i2++;
                        break;
                    }
                } while (it.next().matched(batteryTPPItem.condition));
            }
        }
        if (i2 > 0) {
            return j2 / ((long) i2);
        }
        return 0L;
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0074 A[PHI: r16
      0x0074: PHI (r16v3 java.util.Calendar) = (r16v2 java.util.Calendar), (r16v6 java.util.Calendar), (r16v6 java.util.Calendar) binds: [B:31:0x0089, B:22:0x0070, B:23:0x0072] A[DONT_GENERATE, DONT_INLINE]] */
    private long getTimeRestrictAverageTPP(boolean z2, int i2, int i3) {
        char c2;
        Calendar calendar;
        int i4 = i2 + i3;
        int i5 = i4 % 24;
        int i6 = 0;
        char c3 = 24;
        boolean z3 = i4 < 24;
        Calendar calendar2 = Calendar.getInstance();
        long j2 = 0;
        for (BatteryTPPItem batteryTPPItem : this.tppRecords) {
            if (BatteryUsageCondition.checkScreenCondition(batteryTPPItem.condition, z2)) {
                long j3 = batteryTPPItem.startTime;
                Calendar calendar3 = calendar2;
                long j4 = batteryTPPItem.endTime;
                if (j3 <= j4) {
                    long j5 = j4 - j3;
                    if (i3 >= 24) {
                        j2 += j5;
                        i6++;
                    } else {
                        calendar3.setTimeInMillis(j3);
                        int i7 = calendar3.get(11);
                        calendar3.setTimeInMillis(batteryTPPItem.endTime);
                        int i8 = calendar3.get(11);
                        if (z3) {
                            if (i7 <= i2 || i7 >= i5) {
                                calendar = calendar3;
                            } else {
                                calendar = calendar3;
                                if (j5 >= ((long) (i3 * 3600)) * 1000 || i8 < i5) {
                                    j2 += j5;
                                    i6++;
                                }
                            }
                            c2 = 24;
                        } else {
                            calendar = calendar3;
                            if (i7 <= i2 && i7 >= i5) {
                                c2 = 24;
                            } else if (j5 >= ((long) (i3 * 3600)) * 1000) {
                                j2 += j5;
                                i6++;
                                c2 = 24;
                            } else {
                                c2 = 24;
                                if (i7 > i2 && ((i8 > i2 && i8 < 24) || i8 < i5)) {
                                    j2 += j5;
                                    i6++;
                                }
                                if (i7 < i5 && i8 < i5) {
                                    j2 += j5;
                                    i6++;
                                }
                            }
                        }
                    }
                }
                calendar2 = calendar3;
                c3 = 24;
            } else {
                c2 = c3;
                calendar = calendar2;
            }
            c3 = c2;
            calendar2 = calendar;
        }
        if (i6 > 0) {
            return j2 / ((long) i6);
        }
        return 0L;
    }

    private void updateUsageAttributeLocked(IBatteryStats iBatteryStats) {
        if (iBatteryStats == null) {
            return;
        }
        new BatteryUsageCondition();
    }

    void dumpBatteryUsageAttribute(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, IBatteryStats iBatteryStats) {
        printWriter.println("-------------BatteryUsageAttribute-------------");
        synchronized (this.mLock) {
            try {
                this.tppRecords.clear();
                updateUsageAttributeLocked(iBatteryStats);
                printWriter.println(toString());
                printWriter.println("-------------BatteryUsageTPPRecord-------------");
                Iterator<BatteryTPPItem> it = this.tppRecords.iterator();
                while (it.hasNext()) {
                    printWriter.println(it.next().toString());
                }
                printWriter.println("-----------------------------------------------");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    JSONObject dumpBatteryUsageAttributeJson(IBatteryStats iBatteryStats) {
        JSONObject json;
        synchronized (this.mLock) {
            this.tppRecords.clear();
            updateUsageAttributeLocked(iBatteryStats);
            json = toJson();
        }
        return json;
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("start_time", this.mLastRecordStartTime);
            jSONObject.put("end_time", this.mLastRecordEndTime);
            jSONObject.put("total_avg", this.mTotalAverageTPP);
            jSONObject.put("screenOn_avg", this.mScreenOnAverageTPP);
            jSONObject.put("screenOff_avg", this.mScreenOffAverageTPP);
            jSONObject.put("screenOff_bottom", this.mScreenOffNightTPP);
            jSONObject.put("screenOff_night", this.mScreenOffNightTPP);
            jSONObject.put("screenOn_time_proportion", this.mScreenOnTimeProportion);
            jSONObject.put("screenOff_time_proportion", this.mScreenOffTimeProportion);
            jSONObject.put("screenOn_duration_avg", this.mScreenOnAverageDuration);
            jSONObject.put("screenOff_duration_avg", this.mScreenOffAverageDuration);
            jSONObject.put("screenOn_count", this.mTotalScreenOnCount);
            jSONObject.put("screenOff_count", this.mTotalScreenOffCount);
            jSONObject.put("screenOn_temp_avg", this.mScreenOnAverageTemp);
            jSONObject.put("screenOff_temp_avg", this.mScreenOffAverageTemp);
            jSONObject.put("last_charge_time", this.mLastChargeTimeOfDay);
            jSONObject.put("last_charge_level", this.mLastPluginRemainingLevel);
        } catch (JSONException e2) {
            Log.e(TAG, "toJson", e2);
        }
        return jSONObject;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("total_avg = " + this.mTotalAverageTPP + " ms\n");
        sb.append("screenOn_avg = " + this.mScreenOnAverageTPP + " ms\n");
        sb.append("screenOff_avg = " + this.mScreenOffAverageTPP + " ms\n");
        sb.append("screenOff_bottom = " + this.mScreenOffNightTPP + " ms\n");
        sb.append("screenOff_night = " + this.mScreenOffNightTPP + " ms\n");
        sb.append("screenOn_time_proportion = " + this.mScreenOnTimeProportion + ShellUtils.COMMAND_LINE_END);
        sb.append("screenOff_time_proportion = " + this.mScreenOffTimeProportion + ShellUtils.COMMAND_LINE_END);
        sb.append("screenOn_duration_avg = " + this.mScreenOnAverageDuration + " ms\n");
        sb.append("screenOff_duration_avg = " + this.mScreenOffAverageDuration + " ms\n");
        sb.append("screenOn_count = " + this.mTotalScreenOnCount + ShellUtils.COMMAND_LINE_END);
        sb.append("screenOff_count = " + this.mTotalScreenOffCount + ShellUtils.COMMAND_LINE_END);
        sb.append("screenOn_temp_avg = " + this.mScreenOnAverageTemp + ShellUtils.COMMAND_LINE_END);
        sb.append("screenOff_temp_avg = " + this.mScreenOffAverageTemp + ShellUtils.COMMAND_LINE_END);
        sb.append("last_charge_time = " + this.mLastChargeTimeOfDay + ShellUtils.COMMAND_LINE_END);
        sb.append("last_charge_level = " + this.mLastPluginRemainingLevel + ShellUtils.COMMAND_LINE_END);
        return sb.toString();
    }
}
