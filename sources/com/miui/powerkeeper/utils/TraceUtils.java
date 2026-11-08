package com.miui.powerkeeper.utils;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import com.xiaomi.channel.commonutils.misc.DateTimeHelper;

/* JADX INFO: loaded from: classes.dex */
public class TraceUtils {
    private static final String INTENT_ACTION = "ACTION";
    private static final String TAG = "PowerKeeper_TraceUtils";
    private static final String TRACEUR_PACKAGE = "com.android.traceur";
    private static final String TRACE_ACTION = "com.android.traceur.AppReceiver";

    public static boolean traceDelete(Context context) {
        PowerLog.i(TAG, "traceDelete");
        Intent intent = new Intent(TRACE_ACTION);
        intent.setPackage(TRACEUR_PACKAGE);
        intent.putExtra(INTENT_ACTION, "traceutil_delete");
        intent.putExtra("ACTION_DELETE_FILE", "MiPower-");
        context.sendBroadcast(intent);
        return true;
    }

    public static boolean traceDump(Context context) {
        PowerLog.i(TAG, "traceDump");
        Intent intent = new Intent(TRACE_ACTION);
        intent.setPackage(TRACEUR_PACKAGE);
        intent.putExtra(INTENT_ACTION, "traceutil_dump");
        intent.putExtra("FILE_PREFIX", "MiPower-");
        context.sendBroadcast(intent);
        return true;
    }

    public static boolean traceStart(Context context) {
        PowerLog.i(TAG, "traceStart");
        Intent intent = new Intent(TRACE_ACTION);
        intent.setPackage(TRACEUR_PACKAGE);
        intent.putExtra(INTENT_ACTION, "traceutil_start");
        intent.putExtra("TAGS", "gfx,input,view,audio,hal,power,sched,freq,idle,binder_driver,binder_lock");
        intent.putExtra("SINGLE_CPU_BUFFER", 4096);
        intent.putExtra("MAX_FILE_SIZE_MB", SystemProperties.getInt("max.file.size.mb", 40));
        intent.putExtra("ATRACE_APPS", true);
        intent.putExtra("LONG_TRACE", false);
        intent.putExtra("DURATION_MIN", SystemProperties.getInt("max.duration.min", DateTimeHelper.DAY_IN_MINUTE));
        context.sendBroadcast(intent);
        return true;
    }

    public static boolean traceStop(Context context) {
        PowerLog.i(TAG, "traceStop");
        Intent intent = new Intent(TRACE_ACTION);
        intent.setPackage(TRACEUR_PACKAGE);
        intent.putExtra(INTENT_ACTION, "traceutil_stop");
        context.sendBroadcast(intent);
        return true;
    }
}
