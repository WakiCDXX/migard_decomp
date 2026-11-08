package com.miui.powerkeeper.resource.bigdata.resourcelight;

/* JADX INFO: loaded from: classes.dex */
public class OriginalData {
    public double mStartChargeCounter;
    public long mStartTimeMs;
    public double mStopChargeCounter;
    public long mStopTimeMs;

    public void reset() {
        this.mStartChargeCounter = -1.0d;
        this.mStopChargeCounter = -1.0d;
        this.mStartTimeMs = 0L;
        this.mStopTimeMs = 0L;
    }
}
