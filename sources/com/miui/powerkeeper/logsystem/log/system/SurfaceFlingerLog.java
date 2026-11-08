package com.miui.powerkeeper.logsystem.log.system;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.miui.powerkeeper.logsystem.utils.FileReadBoundary;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SurfaceFlingerLog extends BaseLog {
    public SurfaceFlingerLog(Context context) {
        super(context);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 4;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SF_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected IBinder getService() {
        return ServiceManager.getService(LogType.LOG_TYPE_SF_NAME);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    public String readFile(String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FileReadBoundary("Layer name", "h/w composer state"));
        arrayList.add(new FileReadBoundary("------------SDM", "Current Color Mode"));
        arrayList.add(new FileReadBoundary("ROI", "Color Sampling"));
        return LSUtils.readFromFile(str, (ArrayList<FileReadBoundary>) arrayList);
    }
}
