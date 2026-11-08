package com.miui.powerkeeper.logsystem.log.system;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.log.BaseLog;

/* JADX INFO: loaded from: classes.dex */
public class PerfserviceLog extends BaseLog {
    public PerfserviceLog(Context context) {
        super(context);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 128;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_PERFSERVICE_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected IBinder getService() {
        return ServiceManager.getService("vendor.perfservice");
    }
}
