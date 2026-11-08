package com.miui.powerkeeper.perfengine;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import com.miui.powerkeeper.utils.PowerLog;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;

/* JADX INFO: loaded from: classes.dex */
public class PerfUtils {
    private PerfUtils() {
    }

    public static String a(Context context, String str) {
        try {
            RunningProcessInfo runningProcessInfoByProcessName = ProcessManager.getRunningProcessInfoByProcessName(str);
            if (runningProcessInfoByProcessName == null) {
                return null;
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController_PerfUtils", "RunningAppProcessInfo pid:" + runningProcessInfoByProcessName.mPid + ", processName:" + runningProcessInfoByProcessName.mProcessName);
            }
            return "" + runningProcessInfoByProcessName.mPid;
        } catch (RemoteException e2) {
            Log.e("PeGameController_PerfUtils", "getPerfPid: ", e2);
            return null;
        }
    }

    public static boolean b(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "gb_boosting", 0, -2) == 1;
    }

    public static void c(String str) {
        SystemProperties.set("mcd.extra.params", "sudebug sched " + str);
        SystemProperties.set("ctl.start", "mcd_init");
    }
}
