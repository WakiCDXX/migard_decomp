package com.miui.powerkeeper.logsystem.log.system;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class CpuLog extends BaseLog {
    public CpuLog(Context context) {
        super(context);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 1;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_CPU_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected IBinder getService() {
        return ServiceManager.getService("cpuinfo");
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    public String readFile(String str) {
        return LSUtils.readFromFile(str, 0, 12);
    }
}
