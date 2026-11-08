package com.miui.powerkeeper.dfs;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class StandbyOriginalData {
    protected long mDeviceIdleFullTimeUs;
    protected long mDeviceIdleLightTimeUs;
    protected double mEndChargeCounter;
    protected ArrayList<DfsUtils.TimerEntry> mEndKernelWakelockList;
    protected ArrayList<DfsUtils.TimerEntry> mEndPartialWakelockList;
    protected long mEndRealtimeMs;
    protected ArrayList<DfsUtils.TimerEntry> mEndWakeupReasonMap;
    protected long mFlashlightOnS;
    protected long mScreenDozeS;
    protected double mStartChargeCounter;
    protected ArrayList<DfsUtils.TimerEntry> mStartKernelWakelockList;
    protected ArrayList<DfsUtils.TimerEntry> mStartPartialWakelockList;
    protected long mStartRealtimeMs;
    protected ArrayList<DfsUtils.TimerEntry> mStartWakeupReasonMap;
    protected StandbyAbnormalData.SignalStrengthData mPhoneSignalStrengthStartData = new StandbyAbnormalData.SignalStrengthData();
    protected StandbyAbnormalData.SignalStrengthData mWifiSignalStrengthStartData = new StandbyAbnormalData.SignalStrengthData();

    public void reset() {
        this.mStartPartialWakelockList = null;
        this.mEndPartialWakelockList = null;
        this.mStartKernelWakelockList = null;
        this.mEndKernelWakelockList = null;
        this.mStartWakeupReasonMap = null;
        this.mEndWakeupReasonMap = null;
        this.mStartChargeCounter = -1.0d;
        this.mEndChargeCounter = -1.0d;
        this.mStartRealtimeMs = 0L;
        this.mEndRealtimeMs = 0L;
        this.mFlashlightOnS = 0L;
        this.mScreenDozeS = 0L;
    }
}
