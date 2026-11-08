package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import android.net.TrafficStats;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;

/* JADX INFO: loaded from: classes.dex */
public class NetworkInfo extends LogCollector {
    private final String[] NET_TYPES;
    private final String TABLE;
    private final String TAG;
    private long mLastMobileDownBytes;
    private long mLastMobileUpBytes;
    private long mLastTime;
    private long mLastWifiDownBytes;
    private long mLastWifiUpBytes;
    private long mMobileDownSpeed;
    private long mMobileUpSpeed;
    private long mWifiDownSpeed;
    private long mWifiUpSpeed;

    public NetworkInfo(Context context) {
        super(context);
        this.TAG = "NetworkInfoCollector";
        this.TABLE = "Network Info:";
        this.NET_TYPES = new String[]{"4G", "5G", "wifi", "wifi6"};
        this.mLastTime = 0L;
        this.mLastMobileDownBytes = 0L;
        this.mLastMobileUpBytes = 0L;
        this.mLastWifiDownBytes = 0L;
        this.mLastWifiUpBytes = 0L;
        this.mMobileUpSpeed = 0L;
        this.mMobileDownSpeed = 0L;
        this.mWifiUpSpeed = 0L;
        this.mWifiDownSpeed = 0L;
        calculateNetworkSpeed();
        this.mLog.d("NetworkInfoCollector", "create");
    }

    private void calculateNetworkSpeed() {
        this.mLog.d("NetworkInfoCollector", "calculate network speed");
        long mobileTxBytes = TrafficStats.getMobileTxBytes();
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes() - mobileTxBytes;
        long totalRxBytes = TrafficStats.getTotalRxBytes() - mobileRxBytes;
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = mobileTxBytes - this.mLastMobileUpBytes;
        long j3 = this.mLastTime;
        this.mMobileUpSpeed = j2 / (jCurrentTimeMillis - j3);
        this.mMobileDownSpeed = (mobileRxBytes - this.mLastMobileDownBytes) / (jCurrentTimeMillis - j3);
        this.mWifiUpSpeed = (totalTxBytes - this.mLastWifiUpBytes) / (jCurrentTimeMillis - j3);
        this.mWifiDownSpeed = (totalRxBytes - this.mLastWifiDownBytes) / (jCurrentTimeMillis - j3);
        this.mLastMobileUpBytes = mobileTxBytes;
        this.mLastMobileDownBytes = mobileRxBytes;
        this.mLastWifiUpBytes = totalTxBytes;
        this.mLastWifiDownBytes = totalRxBytes;
        this.mLastTime = jCurrentTimeMillis;
        this.mLog.d("NetworkInfoCollector", "mMobileUpSpeed: " + this.mMobileUpSpeed + " mMobileDownSpeed: " + this.mMobileDownSpeed + " mWifiUpSpeed: " + this.mWifiUpSpeed + " mWifiDownSpeed: " + this.mWifiDownSpeed + " mLastMobileUpBytes: " + this.mLastMobileUpBytes + " mLastMobileDownBytes: " + this.mLastMobileDownBytes + " mLastWifiUpBytes: " + this.mLastWifiUpBytes + " mLastWifiDownBytes: " + this.mLastWifiDownBytes);
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
        if (this.mPublicSpace.isContinueCollect()) {
            this.mLog.d("NetworkInfoCollector", "collect");
            calculateNetworkSpeed();
            int netType = ThermalInfoUtils.getNetType(this.mContext);
            int modemSignal = ThermalInfoUtils.getModemSignal(this.mContext);
            int wifiSignal = ThermalInfoUtils.getWifiSignal(this.mContext);
            this.mCache.append("Network Info:[Network type:" + this.NET_TYPES[netType] + " Modem Signal:" + modemSignal + " Wifi Signal:" + wifiSignal + " Wifi Up Speed:" + this.mWifiUpSpeed + "kb/s Wifi Down Speed:" + this.mWifiDownSpeed + "kb/s Mobile Up Speed:" + this.mMobileUpSpeed + "kb/s Mobile Down Speed:" + this.mMobileDownSpeed + "kb/s] ");
        }
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("NetworkInfoCollector", "logout");
    }
}
