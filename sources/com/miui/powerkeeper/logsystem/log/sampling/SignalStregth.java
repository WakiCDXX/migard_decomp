package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class SignalStregth extends SamplingCatchedLog {
    public SignalStregth(Context context) {
        super(context);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return LogType.LOG_TYPE_SAMPLING_SIGNAL_STRENGTH;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_SIGNAL_STRENGTH_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        addContent("Time: " + LSUtils.getStringDate(System.currentTimeMillis()));
        return true;
    }
}
