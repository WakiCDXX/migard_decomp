package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class Dns extends SamplingCatchedLog {
    public Dns(Context context) {
        super(context);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 8388608;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_DNS_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        addContent("Time: " + LSUtils.getStringDate(System.currentTimeMillis()));
        addContent(LogType.LOG_TYPE_SAMPLING_DNS_NAME);
        return true;
    }
}
