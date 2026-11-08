package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes.dex */
public class TagTransactionData {

    @SerializedName("tr_source_controller_count")
    public int mControllerCount;

    @SerializedName("tr_source_count")
    public int mCount;

    @SerializedName("tr_source_ctp")
    public long mCtp;

    @SerializedName("tr_cur_balance")
    public long mCurrentBalance;

    @SerializedName("tr_source_delta")
    public long mDelta;

    @SerializedName("tr_source_tag")
    public String mTag;

    @SerializedName("tr_source_time")
    public long mTotalTime;

    public TagTransactionData(long j2, String str, long j3, long j4, int i2, long j5, int i3) {
        this.mCurrentBalance = j2;
        this.mTag = str;
        this.mDelta = j3;
        this.mCtp = j4;
        this.mCount = i2;
        this.mTotalTime = j5;
        this.mControllerCount = i3;
    }

    public String toString() {
        return "TagTransactionData{mCurrentBalance=" + this.mCurrentBalance + ", mTag='" + this.mTag + "', mDelta=" + this.mDelta + ", mCtp=" + this.mCtp + ", mCount=" + this.mCount + ", mTatolTime=" + this.mTotalTime + ", mControllerCount=" + this.mControllerCount + '}';
    }
}
