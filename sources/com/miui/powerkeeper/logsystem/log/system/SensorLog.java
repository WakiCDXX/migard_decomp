package com.miui.powerkeeper.logsystem.log.system;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class SensorLog extends BaseLog {
    public SensorLog(Context context) {
        super(context);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 16;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SENSOR_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected IBinder getService() {
        return ServiceManager.getService(LogType.LOG_TYPE_SENSOR_NAME);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    public String readFile(String str) {
        return LSUtils.readFromFile(str, "Active sensors:", "");
    }
}
