package com.xiaomi.mipush.sdk;

import android.content.Context;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.log.MIPushDebugLog;
import com.xiaomi.push.log.MIPushLog2File;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class Logger {
    private static boolean sDisablePushLog = false;
    private static LoggerInterface sUserLogger;

    public static void disablePushFileLog(Context context) {
        sDisablePushLog = true;
        setPushLog(context);
    }

    public static void enablePushFileLog(Context context) {
        sDisablePushLog = false;
        setPushLog(context);
    }

    @Deprecated
    public static File getLogFile(String str) {
        return null;
    }

    protected static LoggerInterface getUserLogger() {
        return sUserLogger;
    }

    public static void setLogger(Context context, LoggerInterface loggerInterface) {
        sUserLogger = loggerInterface;
        setPushLog(context);
    }

    public static void setPushLog(Context context) {
        LoggerInterface loggerInterface = sUserLogger;
        boolean z2 = loggerInterface != null;
        boolean z3 = sDisablePushLog;
        boolean z4 = true ^ z3;
        if (!(z3 ? false : z2)) {
            loggerInterface = null;
        }
        MyLog.setLogger(new MIPushDebugLog(loggerInterface, z4 ? MIPushLog2File.getInstance(context) : null));
    }

    @Deprecated
    public static void uploadLogFile(Context context, boolean z2) {
    }
}
