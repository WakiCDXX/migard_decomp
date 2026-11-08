package com.miui.powerkeeper.logsystem.log.system;

import android.content.Context;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.OctVmNativeProxy;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/* JADX INFO: loaded from: classes.dex */
public class KernelLog extends BaseLog {
    private static final String KERNEL_CATCH_FILES_PREFIX = "/data/system/mcd/ls_kernel_file";

    public KernelLog(Context context) {
        super(context);
    }

    private void execMcdCommand(final File file, final File file2) {
        OctVmNativeProxy.sudebug_command_execute(new String[]{"sudebug", "dmesg", KERNEL_CATCH_FILES_PREFIX});
        new Timer().schedule(new TimerTask() { // from class: com.miui.powerkeeper.logsystem.log.system.KernelLog.1
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                for (int i2 = 0; i2 < 5 && !FileUtil.copyFile(file, file2); i2++) {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }, 10000L);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 2;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_KERNEL_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    public String readFile(String str) {
        return LSUtils.readFromFile(str, 200);
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        File file = new File(KERNEL_CATCH_FILES_PREFIX);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        execMcdCommand(file, new File(str));
        return true;
    }
}
