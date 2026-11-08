package com.xiaomi.push.pulldown;

import android.app.NotificationManager;
import android.content.Context;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;

/* JADX INFO: loaded from: classes.dex */
class PullDownSupport {
    private static final int FLAG_CHECK = -1;
    private static final int FLAG_HIDDEN = 1;
    private static final int FLAG_REVEAED = 2;
    private static final String PATH_PREFIX = "xmsf_fake_panel_status_path";
    private static Boolean sIsSystemSupportMonitorPullDown;

    PullDownSupport() {
    }

    public static boolean isSystemSupportMonitorPullDown(Context context) {
        if (sIsSystemSupportMonitorPullDown == null) {
            sIsSystemSupportMonitorPullDown = Boolean.valueOf(requestSystemFlag(context, -1));
        }
        PullDownLog.d("is system support pulldown " + sIsSystemSupportMonitorPullDown);
        return sIsSystemSupportMonitorPullDown.booleanValue();
    }

    private static boolean requestSystemFlag(Context context, int i2) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        String str = PATH_PREFIX;
        if (i2 != -1) {
            str = PATH_PREFIX + "/" + i2;
        }
        return Boolean.TRUE.equals((Boolean) JavaCalls.callMethod(notificationManager, "isSystemConditionProviderEnabled", str));
    }

    public static boolean subscribePullDownBroadcast(Context context) {
        boolean zRequestSystemFlag = requestSystemFlag(context, 2);
        PullDownLog.d("subscribe pulldown " + zRequestSystemFlag);
        return zRequestSystemFlag;
    }
}
