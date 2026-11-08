package com.miui.powerkeeper.thermal.dfsreport;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class DfsEventData {
    private static final String TAG = "powerkeeper.dfsevdata";
    private final Map<String, String> mData = new HashMap();
    private ReportEventNum mReportEventType;

    public DfsEventData(ReportEventNum reportEventNum) {
        Log.d(TAG, "get " + reportEventNum.getDataNum() + " event data");
        this.mReportEventType = reportEventNum;
    }

    public boolean addEventData(String str, String str2) {
        Map<String, String> map = this.mData;
        if (map == null) {
            return false;
        }
        synchronized (map) {
            this.mData.put(str, str2);
        }
        return true;
    }

    public Map<String, String> getData() {
        Map<String, String> map;
        synchronized (this.mData) {
            map = this.mData;
        }
        return map;
    }

    public ReportEventNum getReportEventType() {
        return this.mReportEventType;
    }
}
