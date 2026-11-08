package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes.dex */
public class SsruSystemData {

    @SerializedName("cumulative_battery_discharge")
    private int cumulativeBatteryDischarge;

    @SerializedName("cumulative_loss")
    private long cumulativeLoss;

    @SerializedName("cumulative_negative_regulations")
    private long cumulativeNegativeRegulations;

    @SerializedName("cumulative_positive_regulations")
    private long cumulativePositiveRegulations;

    @SerializedName("cumulative_profit")
    private long cumulativeProfit;

    @SerializedName("cumulative_rewards")
    private long cumulativeRewards;

    @SerializedName("default_Consumption_Limit")
    private long mDefaultConsumptionLimit;

    @SerializedName("flag")
    private int mFlag;

    @SerializedName("satiated_Consumption_Limit")
    private long mSatiatedConsumptionLimit;

    @SerializedName("num_Negative_regulations")
    private int numNegativeRegulations;

    @SerializedName("num_positive_regulations")
    private int numPositiveRegulations;

    @SerializedName("num_profitable_actions")
    private int numProfitableActions;

    @SerializedName("num_rewards")
    private int numRewards;

    @SerializedName("num_unprofitable_actions")
    private int numUnprofitableActions;

    @SerializedName("screen_off_discharge_mah")
    private long screenOffDischargeMah;

    @SerializedName("screen_off_duration_ms")
    private long screenOffDurationMs;

    public SsruSystemData(long j2, long j3, int i2, int i3, long j4, int i4, long j5, int i5, long j6, int i6, long j7, int i7, long j8, int i8, long j9, long j10) {
        this.mSatiatedConsumptionLimit = j2;
        this.mDefaultConsumptionLimit = j3;
        this.mFlag = i2;
        this.cumulativeBatteryDischarge = i3;
        this.cumulativeProfit = j4;
        this.numProfitableActions = i4;
        this.cumulativeLoss = j5;
        this.numUnprofitableActions = i5;
        this.cumulativeRewards = j6;
        this.numRewards = i6;
        this.cumulativePositiveRegulations = j7;
        this.numPositiveRegulations = i7;
        this.cumulativeNegativeRegulations = j8;
        this.numNegativeRegulations = i8;
        this.screenOffDischargeMah = j9;
        this.screenOffDurationMs = j10;
    }

    public String toString() {
        return "SsruSystemData{mSatiatedConsumptionLimit=" + this.mSatiatedConsumptionLimit + ", mDefaultConsumptionLimit=" + this.mDefaultConsumptionLimit + ", mFlag=" + this.mFlag + ", cumulativeBatteryDischarge=" + this.cumulativeBatteryDischarge + ", cumulativeProfit=" + this.cumulativeProfit + ", numProfitableActions=" + this.numProfitableActions + ", cumulativeLoss=" + this.cumulativeLoss + ", numUnprofitableActions=" + this.numUnprofitableActions + ", cumulativeRewards=" + this.cumulativeRewards + ", numRewards=" + this.numRewards + ", cumulativePositiveRegulations=" + this.cumulativePositiveRegulations + ", numPositiveRegulations=" + this.numPositiveRegulations + ", cumulativeNegativeRegulations=" + this.cumulativeNegativeRegulations + ", numNegativeRegulations=" + this.numNegativeRegulations + ", screenOffDischargeMah=" + this.screenOffDischargeMah + ", screenOffDurationMs=" + this.screenOffDurationMs + '}';
    }
}
