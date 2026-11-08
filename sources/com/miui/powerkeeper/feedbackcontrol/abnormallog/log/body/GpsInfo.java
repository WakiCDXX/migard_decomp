package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class GpsInfo extends LogCollector {
    private final String TABLE;
    private final String TAG;
    private int mSnr;

    public GpsInfo(Context context) {
        super(context);
        this.TAG = "GpsInfoCollector";
        this.TABLE = "Gps Info:";
        this.mSnr = 0;
        this.mLog.d("GpsInfoCollector", "create");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("GpsInfoCollector", "logout");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
    }
}
