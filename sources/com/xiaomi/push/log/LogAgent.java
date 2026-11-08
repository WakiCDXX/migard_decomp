package com.xiaomi.push.log;

import android.content.Context;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class LogAgent {
    private static ILogAgent sLogAgent;

    public static File getXmsfLogDir(Context context) {
        if (context == null) {
            MyLog.e("ERROR: Context cannot be null.");
            return null;
        }
        ILogAgent iLogAgent = sLogAgent;
        if (iLogAgent != null) {
            return iLogAgent.getXmsfLogDir(context);
        }
        MyLog.e("ERROR: XMSF not configure the instance of LogAgent.");
        return null;
    }

    public static void setLogAgent(ILogAgent iLogAgent) {
        sLogAgent = iLogAgent;
    }
}
