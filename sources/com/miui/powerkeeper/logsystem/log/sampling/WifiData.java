package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import android.net.TrafficStats;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class WifiData extends SamplingCatchedLog {
    private long mMobileRxBytes;
    private long mMobileTxBytes;
    private long mStartTime;
    private boolean mStarted;
    private long mTotleRxBytes;
    private long mTotleTxBytes;

    class WData extends SamplingCatchedLog.Data {
        long deltaMobileRxBytes;
        long deltaMobileTxBytes;
        long deltaTotleRxBytes;
        long deltaTotleTxBytes;
        long startTime;
        long stopTime;

        WData() {
            super();
        }
    }

    public WifiData(Context context) {
        super(context);
        this.mStarted = false;
    }

    private void sampling() {
        WData wData = new WData();
        wData.startTime = this.mStartTime;
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mStartTime = jCurrentTimeMillis;
        wData.stopTime = jCurrentTimeMillis;
        wData.deltaMobileTxBytes = TrafficStats.getMobileTxBytes() - this.mMobileTxBytes;
        wData.deltaMobileRxBytes = TrafficStats.getMobileRxBytes() - this.mMobileRxBytes;
        wData.deltaTotleTxBytes = TrafficStats.getTotalTxBytes() - this.mTotleTxBytes;
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long j2 = this.mTotleRxBytes;
        long j3 = totalRxBytes - j2;
        wData.deltaTotleRxBytes = j3;
        this.mMobileRxBytes += wData.deltaMobileRxBytes;
        this.mMobileTxBytes += wData.deltaMobileTxBytes;
        this.mTotleTxBytes += wData.deltaTotleTxBytes;
        this.mTotleRxBytes = j2 + j3;
        this.mList.add(wData);
    }

    private void startCompute() {
        this.mMobileTxBytes = TrafficStats.getMobileTxBytes();
        this.mMobileRxBytes = TrafficStats.getMobileRxBytes();
        this.mTotleTxBytes = TrafficStats.getTotalTxBytes();
        this.mTotleRxBytes = TrafficStats.getTotalRxBytes();
        this.mStartTime = System.currentTimeMillis();
    }

    @Override // com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog
    public void catchLog() {
        addContent("Wifi data statistics (the totle data except mobile data):");
        for (SamplingCatchedLog.Data data : this.mList) {
            StringBuilder sb = new StringBuilder();
            sb.append("From ");
            WData wData = (WData) data;
            sb.append(LSUtils.getStringDate(wData.startTime));
            sb.append(" to ");
            sb.append(LSUtils.getStringDate(wData.stopTime));
            addContent(sb.toString());
            addContent("Totle Wifi Tx Bytes: " + (wData.deltaTotleTxBytes - wData.deltaMobileTxBytes));
            addContent("Totle Wifi Rx Bytes: " + (wData.deltaTotleRxBytes - wData.deltaMobileRxBytes));
        }
        this.mStarted = false;
        super.catchLog();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return LogType.LOG_TYPE_SAMPLING_WIFI_DATA;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_WIFI_DATA_NAME;
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
