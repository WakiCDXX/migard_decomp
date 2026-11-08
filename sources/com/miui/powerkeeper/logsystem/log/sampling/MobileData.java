package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import android.net.TrafficStats;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class MobileData extends SamplingCatchedLog {
    private long mMobileRxBytes;
    private long mMobileTxBytes;
    private long mStartTime;
    private boolean mStarted;

    class MData extends SamplingCatchedLog.Data {
        long deltaMobileRxBytes;
        long deltaMobileTxBytes;
        long startTime;
        long stopTime;

        MData() {
            super();
        }
    }

    public MobileData(Context context) {
        super(context);
        this.mStarted = false;
    }

    private void sampling() {
        MData mData = new MData();
        mData.startTime = this.mStartTime;
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mStartTime = jCurrentTimeMillis;
        mData.stopTime = jCurrentTimeMillis;
        mData.deltaMobileRxBytes = TrafficStats.getMobileRxBytes() - this.mMobileRxBytes;
        long mobileTxBytes = TrafficStats.getMobileTxBytes();
        long j2 = this.mMobileTxBytes;
        long j3 = mobileTxBytes - j2;
        mData.deltaMobileTxBytes = j3;
        this.mMobileRxBytes += mData.deltaMobileRxBytes;
        this.mMobileTxBytes = j2 + j3;
        this.mList.add(mData);
    }

    private void startCompute() {
        this.mMobileTxBytes = TrafficStats.getMobileTxBytes();
        this.mMobileRxBytes = TrafficStats.getMobileRxBytes();
        this.mStartTime = System.currentTimeMillis();
    }

    @Override // com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog
    public void catchLog() {
        for (SamplingCatchedLog.Data data : this.mList) {
            StringBuilder sb = new StringBuilder();
            sb.append("From ");
            MData mData = (MData) data;
            sb.append(LSUtils.getStringDate(mData.startTime));
            sb.append(" to ");
            sb.append(LSUtils.getStringDate(mData.stopTime));
            addContent(sb.toString());
            addContent("Totle Mobile Tx Bytes: " + mData.deltaMobileTxBytes);
            addContent("Totle Mobile Rx Bytes: " + mData.deltaMobileRxBytes);
        }
        this.mStarted = false;
        super.catchLog();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return LogType.LOG_TYPE_SAMPLING_MOBILE_DATA;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_MOBILE_DATA_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        if (this.mStarted) {
            sampling();
        } else {
            this.mStarted = true;
            startCompute();
        }
        return true;
    }
}
