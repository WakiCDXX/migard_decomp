package com.miui.powerkeeper.logsystem.log.system;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import com.miui.powerkeeper.logsystem.log.BaseLog;

/* JADX INFO: loaded from: classes.dex */
public class AlarmLog extends BaseLog {
    public AlarmLog(Context context) {
        super(context);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 512;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return "alarm";
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected IBinder getService() {
        return ServiceManager.getService("alarm");
    }
}
