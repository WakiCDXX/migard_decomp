package com.miui.powerkeeper.logsystem.log.system;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.miui.powerkeeper.logsystem.log.LogInterface;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class DisplayLog extends BaseLog implements LogInterface {
    public DisplayLog(Context context) {
        super(context);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 8;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return "display";
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected IBinder getService() {
        return ServiceManager.getService("display");
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    public String readFile(String str) {
        return LSUtils.readFromFile(str, "Display Devices:", "DisplayModeDirector");
    }
}
