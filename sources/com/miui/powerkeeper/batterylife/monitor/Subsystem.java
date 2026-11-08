package com.miui.powerkeeper.batterylife.monitor;

import android.annotation.Nullable;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import com.miui.powerkeeper.batterylife.TrackData;
import com.miui.powerkeeper.siming.SiMing;
import com.miui.powerkeeper.siming.shaosi.SubsystemN;
import com.miui.powerkeeper.utils.PowerLog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class Subsystem implements Resource {
    private ArrayList<SubsystemN.SubsystemDataQCom> mLastDataList;
    private long mLastReadingRealtimeMs;
    private ArrayList<SubsystemN.SubsystemDataQCom> mNewDataList;
    private long mNewReadingRealtimeMs;
    SubsystemN mSubsystemN = (SubsystemN) SiMing.getInstance().getDivision(101);

    protected Subsystem() {
    }

    private void printMap(Map<String, Object> map, PrintWriter printWriter) {
        printWriter.print(map.get(TrackData.SS_SUBSYSTEM_NAME));
        printWriter.println(": " + map.get(TrackData.SS_SAMPLING_DURATION_MS) + "ms, " + map.get(TrackData.SS_SLEEP_TIME_MS) + "ms, " + map.get(TrackData.SS_SLEEP_RATIO) + ", " + map.get(TrackData.SS_SLEEP_STATUS));
    }

    private void printSubSystem(PrintWriter printWriter, String str) {
        printWriter.println();
        if (this.mNewDataList == null || this.mLastDataList == null) {
            return;
        }
        for (int i2 = 0; i2 < this.mNewDataList.size(); i2++) {
            SubsystemN.SubsystemDataQCom subsystemDataQCom = this.mNewDataList.get(i2);
            SubsystemN.SubsystemDataQCom subsystemDataQCom2 = this.mLastDataList.get(i2);
            if (subsystemDataQCom != null && ((subsystemDataQCom.lastEnteredAt != 0 || subsystemDataQCom.lastExitedAt != 0) && (str == null || str.equals(subsystemDataQCom.name)))) {
                printWriter.print(subsystemDataQCom.name + "(new): ");
                printWriter.println(subsystemDataQCom.durationMs + "ms, " + subsystemDataQCom.lastExitedAt + ", " + subsystemDataQCom.lastEnteredAt + ", " + subsystemDataQCom.count);
                StringBuilder sb = new StringBuilder();
                sb.append(subsystemDataQCom2.name);
                sb.append("(last): ");
                printWriter.print(sb.toString());
                printWriter.println(subsystemDataQCom2.durationMs + "ms, " + subsystemDataQCom2.lastExitedAt + ", " + subsystemDataQCom2.lastEnteredAt + ", " + subsystemDataQCom2.count);
            }
        }
    }

    private void readSubsystemData() {
        SubsystemN subsystemN = this.mSubsystemN;
        if (subsystemN == null) {
            return;
        }
        this.mLastDataList = subsystemN.readSubsystemQCom();
        this.mLastReadingRealtimeMs = SystemClock.elapsedRealtime();
    }

    @Override // com.miui.powerkeeper.batterylife.monitor.Resource
    public void dump(PrintWriter printWriter, String[] strArr) {
        ArrayList<Map<String, Object>> uploadData = getUploadData();
        if (uploadData == null) {
            printWriter.println("Subsystem data is empty.");
            return;
        }
        printWriter.println("Subsystem:");
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("LastRealtimeMs=" + this.mLastReadingRealtimeMs);
        indentingPrintWriter.println("NewRealtimeMs=" + this.mNewReadingRealtimeMs);
        String str = (strArr == null || strArr.length <= 0) ? null : strArr[0];
        for (int i2 = 0; i2 < uploadData.size(); i2++) {
            if (str == null || str.equals(uploadData.get(i2).get(TrackData.SS_SUBSYSTEM_NAME))) {
                printMap(uploadData.get(i2), indentingPrintWriter);
            }
        }
        if (PowerLog.D) {
            printSubSystem(indentingPrintWriter, str);
        }
        indentingPrintWriter.println();
    }

    @Override // com.miui.powerkeeper.batterylife.monitor.Resource
    @Nullable
    public ArrayList<Map<String, Object>> getUploadData() {
        if (this.mSubsystemN == null) {
            return null;
        }
        ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
        ArrayList<SubsystemN.SubsystemDataQCom> subsystemQCom = this.mSubsystemN.readSubsystemQCom();
        if (subsystemQCom == null || this.mLastDataList == null) {
            return null;
        }
        if (PowerLog.D) {
            this.mNewDataList = subsystemQCom;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        this.mNewReadingRealtimeMs = jElapsedRealtime;
        long j2 = jElapsedRealtime - this.mLastReadingRealtimeMs;
        for (int i2 = 0; i2 < subsystemQCom.size(); i2++) {
            SubsystemN.SubsystemDataQCom subsystemDataQCom = subsystemQCom.get(i2);
            SubsystemN.SubsystemDataQCom subsystemDataQCom2 = this.mLastDataList.get(i2);
            if (subsystemDataQCom.name.equals(subsystemDataQCom2.name) && (subsystemDataQCom.lastExitedAt != 0 || subsystemDataQCom.lastEnteredAt != 0)) {
                ArrayMap arrayMap = new ArrayMap();
                arrayMap.put(TrackData.SS_SAMPLING_DURATION_MS, Long.valueOf(j2));
                arrayMap.put(TrackData.SS_SLEEP_TIME_MS, Long.valueOf(subsystemDataQCom.durationMs - subsystemDataQCom2.durationMs));
                arrayMap.put(TrackData.SS_SUBSYSTEM_NAME, subsystemDataQCom.name);
                arrayMap.put(TrackData.SS_SLEEP_RATIO, String.format(Locale.getDefault(), "%.2f", Float.valueOf(((subsystemDataQCom.durationMs - subsystemDataQCom2.durationMs) * 100.0f) / j2)) + "%");
                arrayMap.put(TrackData.SS_SLEEP_STATUS, Integer.valueOf(subsystemDataQCom.lastEnteredAt >= subsystemDataQCom.lastExitedAt ? 0 : 1));
                arrayList.add(arrayMap);
            }
        }
        return arrayList;
    }

    @Override // com.miui.powerkeeper.batterylife.monitor.Resource
    public void reset() {
        readSubsystemData();
    }
}
