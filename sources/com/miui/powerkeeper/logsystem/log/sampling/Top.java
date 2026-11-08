package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.OctVmNativeProxy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Top extends SamplingCatchedLog {
    private boolean mInReading;
    private ArrayList<String> mResultString;
    private static final String TOP_CATCH_FILES_PREFIX = "/data/system/mcd/ls_top_file";
    private static String[] ARGS = {"sudebug", "top", TOP_CATCH_FILES_PREFIX, "ls"};

    public Top(Context context) {
        super(context);
        this.mResultString = new ArrayList<>();
        this.mInReading = false;
    }

    private void execMcdCommand() {
        int i2 = 0;
        while (this.mInReading) {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
                Thread.currentThread().interrupt();
            }
            int i3 = i2 + 1;
            if (i2 > 5) {
                return;
            } else {
                i2 = i3;
            }
        }
        this.mResultString.add("Time: " + LSUtils.getStringDate(System.currentTimeMillis()));
        OctVmNativeProxy.sudebug_command_execute(ARGS);
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e3) {
            e3.printStackTrace();
            Thread.currentThread().interrupt();
        }
        this.mResultString.add(readLogFile());
    }

    private String readLogFile() throws Throwable {
        this.mInReading = true;
        String fromFile = null;
        for (int i2 = 0; i2 < 4; i2++) {
            fromFile = FileUtil.readFromFile(TOP_CATCH_FILES_PREFIX);
            if (!LSUtils.isEmptyString(fromFile)) {
                break;
            }
            OctVmNativeProxy.sudebug_command_execute(ARGS);
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        this.mInReading = false;
        return fromFile;
    }

    @Override // com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog
    public void catchLog() {
        for (int i2 = 0; i2 < this.mResultString.size(); i2++) {
            addContent(this.mResultString.get(i2));
        }
        File file = new File(TOP_CATCH_FILES_PREFIX);
        if (file.exists()) {
            file.delete();
        }
        this.mResultString.clear();
        super.catchLog();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 16777216;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_TOP_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        File file = new File(TOP_CATCH_FILES_PREFIX);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        execMcdCommand();
        return true;
    }
}
