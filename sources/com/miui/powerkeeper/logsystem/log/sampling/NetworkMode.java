package com.miui.powerkeeper.logsystem.log.sampling;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class NetworkMode extends SamplingCatchedLog {
    ConnectivityManager mManager;

    class NMData extends SamplingCatchedLog.Data {
        String networkType;
        int signalStrength;

        NMData() {
            super();
        }
    }

    public NetworkMode(Context context) {
        super(context);
        this.mManager = (ConnectivityManager) this.mContext.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME);
    }

    private String getNetworkType() {
        NetworkInfo activeNetworkInfo = this.mManager.getActiveNetworkInfo();
        return activeNetworkInfo == null ? "unknown" : activeNetworkInfo.getTypeName();
    }

    @TargetApi(29)
    private int getSignalStrength() {
        ConnectivityManager connectivityManager = this.mManager;
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if (networkCapabilities == null) {
            return 0;
        }
        return networkCapabilities.getSignalStrength();
    }

    @Override // com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog
    public void catchLog() {
        for (SamplingCatchedLog.Data data : this.mList) {
            addContent("Time: " + LSUtils.getStringDate(data.time));
            StringBuilder sb = new StringBuilder();
            sb.append("NetworkType: ");
            NMData nMData = (NMData) data;
            sb.append(nMData.networkType);
            addContent(sb.toString());
            addContent("SignalStrength: " + nMData.signalStrength);
        }
        super.catchLog();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return LogType.LOG_TYPE_SAMPLING_NETWORK_MODE;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_NETWORK_MODE_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        NMData nMData = new NMData();
        nMData.time = System.currentTimeMillis();
        nMData.networkType = getNetworkType();
        nMData.signalStrength = getSignalStrength();
        this.mList.add(nMData);
        return true;
    }
}
