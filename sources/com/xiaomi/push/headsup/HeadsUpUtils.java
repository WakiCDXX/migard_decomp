package com.xiaomi.push.headsup;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.service.NotificationUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(28)
class HeadsUpUtils {
    private static final int DISABLE_CLOCK = 8388608;
    private static final int DISABLE_NOTIFICATION_ICONS = 131072;
    private static final int DISABLE_SYSTEM_INFO = 1048576;

    HeadsUpUtils() {
    }

    static boolean checkComponentSupportStyle(Context context, ComponentName componentName, int i2) {
        try {
            new Intent().setComponent(componentName);
            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(componentName, 128);
            if (i2 == 0) {
                return true;
            }
            Bundle bundle = activityInfo.metaData;
            if (bundle != null) {
                String string = bundle.getString(HeadsUpConstants.KEY_SUPPORT_STYLE);
                String styleStr = HeadsUpConstants.getStyleStr(i2);
                HeadsUpLog.d("check supportStyle:" + styleStr + " in " + string);
                String[] strArrSplit = string.split("\\|");
                int length = strArrSplit.length;
                for (int i3 = 0; i3 < length; i3++) {
                    if (strArrSplit[i3].equals(styleStr)) {
                        return true;
                    }
                }
            }
        } catch (Exception unused) {
        }
        return false;
    }

    static void cpMapToNotification(Map<String, String> map, Notification notification, String str) {
        if (map != null && notification != null && !TextUtils.isEmpty(str)) {
            if (notification.extras == null) {
                notification.extras = new Bundle();
            }
            NotificationUtils.cpMapToBundle(map, notification.extras, str);
        } else {
            HeadsUpLog.w("cp map to n fail:" + str);
        }
    }

    static void cpNotificationToMap(Notification notification, Map<String, String> map, String str) {
        if (notification != null && map != null && !TextUtils.isEmpty(str)) {
            NotificationUtils.cpBundleToMap(notification.extras, map, str);
            return;
        }
        HeadsUpLog.w("cp n to map fail:" + str);
    }

    static boolean disableStatusBar(Context context, boolean z2) {
        boolean z3 = false;
        try {
            Object systemService = context.getSystemService("statusbar");
            if (systemService != null) {
                JavaCalls.callMethodOrThrow(systemService, "disable", Integer.valueOf(z2 ? 9568256 : 0));
                z3 = true;
            }
        } catch (Exception e2) {
            HeadsUpLog.w("disable statusbar error:" + e2);
        }
        HeadsUpLog.d("disable statusbar succ:" + z3 + " isHidden:" + z2);
        return z3;
    }

    static boolean isDndMode(Context context) {
        Integer num = (Integer) JavaCalls.callMethod((NotificationManager) context.getSystemService("notification"), "getZenMode", new Object[0]);
        HeadsUpLog.d("isDndMode getZenMode:" + num);
        return num != null && num.intValue() == 1;
    }

    static boolean isDndModeAndDisableFloat(Context context) {
        if (isDndMode(context)) {
            try {
                int i2 = Settings.System.getInt(context.getContentResolver(), "show_notification", 1);
                HeadsUpLog.d("isDndMode disable float:" + i2);
                return i2 == 1;
            } catch (Exception e2) {
                HeadsUpLog.w("isDndMode disable float error:" + e2);
            }
        }
        return false;
    }

    static String resolveContent(Notification notification) {
        CharSequence charSequence;
        Bundle bundle = notification.extras;
        if (bundle != null) {
            charSequence = bundle.getCharSequence(HeadsUpConstants.EXTRA_HEADSUP_DESC);
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = NotificationUtils.resolveContent(notification);
            }
        } else {
            charSequence = null;
        }
        return charSequence != null ? charSequence.toString() : "";
    }

    static int resolveHeadsUpStyle(Object obj) {
        return ToolUtils.parseInt((String) NotificationUtils.getValue(obj, HeadsUpConstants.EXTRA_HEADSUP_STYLE, ""), -1);
    }

    static int resolveHeadsUpUiType(Object obj) {
        return ToolUtils.parseInt((String) NotificationUtils.getValue(obj, HeadsUpConstants.EXTRA_HEADSUP_UI_TYPE, ""), -1);
    }

    static String resolveTitle(Notification notification) {
        CharSequence charSequence;
        Bundle bundle = notification.extras;
        if (bundle != null) {
            charSequence = bundle.getCharSequence(HeadsUpConstants.EXTRA_HEADSUP_TITLE);
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = NotificationUtils.resolveTitle(notification);
            }
        } else {
            charSequence = null;
        }
        return charSequence != null ? charSequence.toString() : "";
    }
}
