package com.xiaomi.push.service;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
public class AppPermissions {
    private static final int MASK_APP_BADGE = 4;
    private static final int MASK_APP_CAN_FLOAT = 64;
    private static final int MASK_APP_CAN_NOT_FLOAT = 128;
    private static final int MASK_APP_CLOSE = 2;
    private static final int MASK_APP_DISPLAY_LOCK_SCREEN = 16;
    private static final int MASK_APP_NOT_BADGE = 8;
    private static final int MASK_APP_NOT_DISPLAY_LOCK_SCREEN = 32;
    private static final int MASK_APP_NOT_ONGOING = 8192;
    private static final int MASK_APP_NOT_SOUND = 512;
    private static final int MASK_APP_NOT_VIBRATE = 2048;
    private static final int MASK_APP_ONGOING = 4096;
    private static final int MASK_APP_OPEN = 1;
    private static final int MASK_APP_SOUND = 256;
    private static final int MASK_APP_VIBRATE = 1024;
    private static final SparseArray<NotificationUtils.Third<String, String, String>> METHODS = new SparseArray<NotificationUtils.Third<String, String, String>>(6) { // from class: com.xiaomi.push.service.AppPermissions.1
        {
            put(1, NotificationUtils.SETTING_BADGE);
            put(2, NotificationUtils.SETTING_FLOAT);
            put(4, NotificationUtils.SETTING_KEYGUARD);
            put(8, NotificationUtils.SETTING_SOUND);
            put(16, NotificationUtils.SETTING_VIBRATE);
            put(32, NotificationUtils.SETTING_ONGOING);
        }
    };
    private static final int PERMISSION_BADGE = 1;
    private static final int PERMISSION_FLOAT = 2;
    private static final int PERMISSION_KEYGUARD = 4;
    private static final int PERMISSION_ONGOING = 32;
    private static final int PERMISSION_SOUND = 8;
    private static final int PERMISSION_VIBRATE = 16;

    private static Bundle checkAllPermissionStatus(String str) {
        return NotificationUtils.checkAllNotificationSetting(SystemUtils.getContext(), str, null);
    }

    private static int checkPermissionStatus(String str, int i2) {
        return NotificationUtils.checkNotificationSetting(SystemUtils.getContext(), str, null, METHODS.get(i2));
    }

    public static int getAppNotificationStatus(Context context, String str) {
        int i2;
        int i3 = 0;
        if (context == null || TextUtils.isEmpty(str)) {
            MyLog.w("context | packageName must not be null");
            return 0;
        }
        AppInfoUtils.AppNotificationOp appNotificationOp = AppInfoUtils.getAppNotificationOp(context, str, true);
        if (appNotificationOp == AppInfoUtils.AppNotificationOp.ALLOWED) {
            i3 = 1;
        } else if (appNotificationOp == AppInfoUtils.AppNotificationOp.NOT_ALLOWED) {
            i3 = 2;
        }
        if (NotificationUtils.hasBatchSwitchInterface()) {
            Bundle bundleCheckAllPermissionStatus = checkAllPermissionStatus(str);
            NotificationUtils.Third<String, String, String> third = NotificationUtils.SETTING_BADGE;
            if (bundleCheckAllPermissionStatus.containsKey(third.paramKey)) {
                i3 |= bundleCheckAllPermissionStatus.getBoolean(third.paramKey) ? 4 : 8;
            }
            NotificationUtils.Third<String, String, String> third2 = NotificationUtils.SETTING_KEYGUARD;
            if (bundleCheckAllPermissionStatus.containsKey(third2.paramKey)) {
                i3 |= bundleCheckAllPermissionStatus.getBoolean(third2.paramKey) ? 16 : 32;
            }
            NotificationUtils.Third<String, String, String> third3 = NotificationUtils.SETTING_FLOAT;
            if (bundleCheckAllPermissionStatus.containsKey(third3.paramKey)) {
                i3 |= bundleCheckAllPermissionStatus.getBoolean(third3.paramKey) ? 64 : 128;
            }
            NotificationUtils.Third<String, String, String> third4 = NotificationUtils.SETTING_SOUND;
            if (bundleCheckAllPermissionStatus.containsKey(third4.paramKey)) {
                i3 |= bundleCheckAllPermissionStatus.getBoolean(third4.paramKey) ? 256 : 512;
            }
            NotificationUtils.Third<String, String, String> third5 = NotificationUtils.SETTING_VIBRATE;
            if (bundleCheckAllPermissionStatus.containsKey(third5.paramKey)) {
                i3 |= bundleCheckAllPermissionStatus.getBoolean(third5.paramKey) ? 1024 : 2048;
            }
            NotificationUtils.Third<String, String, String> third6 = NotificationUtils.SETTING_ONGOING;
            if (bundleCheckAllPermissionStatus.containsKey(third6.paramKey)) {
                return i3 | (bundleCheckAllPermissionStatus.getBoolean(third6.paramKey) ? 4096 : 8192);
            }
            return i3;
        }
        int iCheckPermissionStatus = checkPermissionStatus(str, 1);
        if (iCheckPermissionStatus == 1) {
            i3 |= 4;
        } else if (iCheckPermissionStatus == 0) {
            i3 |= 8;
        }
        int iCheckPermissionStatus2 = checkPermissionStatus(str, 4);
        if (iCheckPermissionStatus2 == 1) {
            i3 |= 16;
        } else if (iCheckPermissionStatus2 == 0) {
            i3 |= 32;
        }
        int iCheckPermissionStatus3 = checkPermissionStatus(str, 2);
        if (iCheckPermissionStatus3 == 1) {
            i3 |= 64;
        } else if (iCheckPermissionStatus3 == 0) {
            i3 |= 128;
        }
        int iCheckPermissionStatus4 = checkPermissionStatus(str, 8);
        if (iCheckPermissionStatus4 == 1) {
            i3 |= 256;
        } else if (iCheckPermissionStatus4 == 0) {
            i3 |= 512;
        }
        int iCheckPermissionStatus5 = checkPermissionStatus(str, 16);
        if (iCheckPermissionStatus5 == 1) {
            i3 |= 1024;
        } else if (iCheckPermissionStatus5 == 0) {
            i3 |= 2048;
        }
        int iCheckPermissionStatus6 = checkPermissionStatus(str, 32);
        if (iCheckPermissionStatus6 == 1) {
            i2 = i3 | 4096;
        } else {
            if (iCheckPermissionStatus6 != 0) {
                return i3;
            }
            i2 = i3 | 8192;
        }
        return i2;
    }
}
