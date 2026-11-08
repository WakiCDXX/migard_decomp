package com.xiaomi.push.service;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ChannelPermissions {
    private static final int MASK_CHANNEL_CAN_FLOAT = 4;
    private static final int MASK_CHANNEL_CAN_NOT_FLOAT = 8;
    private static final int MASK_CHANNEL_CLOSE = 2;
    private static final int MASK_CHANNEL_DISPLAY_LOCK_SCREEN = 16;
    private static final int MASK_CHANNEL_NOT_DISPLAY_LOCK_SCREEN = 32;
    private static final int MASK_CHANNEL_NOT_SOUND = 128;
    private static final int MASK_CHANNEL_NOT_VIBRATE = 512;
    private static final int MASK_CHANNEL_OPEN = 1;
    private static final int MASK_CHANNEL_SOUND = 64;
    private static final int MASK_CHANNEL_VIBRATE = 256;
    private static final SparseArray<NotificationUtils.Third<String, String, String>> METHODS;
    private static final int[] PERMISSION_ALL = {1, 2, 4, 8, 16};
    private static final int PERMISSION_FLAGS_NONE = 0;
    public static final int PERMISSION_FLOAT = 8;
    public static final int PERMISSION_KEYGUARD = 16;
    private static final int PERMISSION_LIGHTS = 4;
    public static final int PERMISSION_SOUND = 1;
    public static final int PERMISSION_VIBRATE = 2;
    private static final String SP_PERMISSION_CACHE_LIST_FILE = "ch_permission_cache_file";
    private static final String TAG = "ChannelPC:";
    private static final SparseArray<Integer> USER_LOCKED_FIELDS;

    static {
        int i2 = 5;
        METHODS = new SparseArray<NotificationUtils.Third<String, String, String>>(i2) { // from class: com.xiaomi.push.service.ChannelPermissions.1
            {
                put(1, NotificationUtils.SETTING_SOUND);
                put(2, NotificationUtils.SETTING_VIBRATE);
                put(4, NotificationUtils.SETTING_LIGHTS);
                put(8, NotificationUtils.SETTING_FLOAT);
                put(16, NotificationUtils.SETTING_KEYGUARD);
            }
        };
        USER_LOCKED_FIELDS = new SparseArray<Integer>(i2) { // from class: com.xiaomi.push.service.ChannelPermissions.2
            {
                put(1, 32);
                put(2, 16);
                put(4, 8);
                put(8, 4);
                put(16, 2);
            }
        };
    }

    static int channelPermFrom(String str, String str2) {
        int i2 = checkPermissionOne(str, str2, 8) ? 8 : 0;
        if (checkPermissionOne(str, str2, 16)) {
            i2 |= 16;
        }
        if (checkPermissionOne(str, str2, 1)) {
            i2 |= 1;
        }
        if (checkPermissionOne(str, str2, 2)) {
            i2 |= 2;
        }
        return checkPermissionOne(str, str2, 4) ? i2 | 4 : i2;
    }

    public static Bundle checkPermissionAllStatus(String str, String str2) {
        return NotificationUtils.checkAllNotificationSetting(SystemUtils.getContext(), str, str2);
    }

    private static boolean checkPermissionOne(String str, String str2, int i2) {
        boolean z2 = NotificationUtils.checkNotificationSetting(SystemUtils.getContext(), str, str2, METHODS.get(i2)) == 1;
        MyLog.w("ChannelPermissions.checkPermission:" + str + ":" + str2 + ": <" + i2 + "=" + z2 + ">");
        return z2;
    }

    public static int checkPermissionOneDetailStatus(String str, String str2, int i2) {
        return NotificationUtils.checkNotificationSetting(SystemUtils.getContext(), str, str2, METHODS.get(i2));
    }

    /* JADX WARN: Code duplicated, block: B:32:0x0073  */
    public static int getChannelAllStatus(Context context, String str, NotificationChannel notificationChannel) {
        if (context == null || TextUtils.isEmpty(str) || notificationChannel == null) {
            MyLog.w("context|packageName|channel must not be null ");
            return 0;
        }
        int i2 = notificationChannel.getImportance() != 0 ? 1 : 2;
        int i3 = 8;
        if (NotificationUtils.hasBatchSwitchInterface()) {
            Bundle bundleCheckPermissionAllStatus = checkPermissionAllStatus(str, notificationChannel.getId());
            NotificationUtils.Third<String, String, String> third = NotificationUtils.SETTING_FLOAT;
            if (bundleCheckPermissionAllStatus.containsKey(third.paramKey)) {
                if (bundleCheckPermissionAllStatus.getBoolean(third.paramKey) && notificationChannel.getImportance() >= 4) {
                    i3 = 4;
                }
                i2 |= i3;
            }
            NotificationUtils.Third<String, String, String> third2 = NotificationUtils.SETTING_KEYGUARD;
            if (bundleCheckPermissionAllStatus.containsKey(third2.paramKey)) {
                i2 |= bundleCheckPermissionAllStatus.getBoolean(third2.paramKey) ? 16 : 32;
            }
        } else {
            int iCheckPermissionOneDetailStatus = checkPermissionOneDetailStatus(str, notificationChannel.getId(), 8);
            if (iCheckPermissionOneDetailStatus == 1) {
                if (notificationChannel.getImportance() >= 4) {
                    i2 |= 4;
                } else {
                    i2 |= 8;
                }
            } else if (iCheckPermissionOneDetailStatus == 0) {
                i2 |= 8;
            }
            int iCheckPermissionOneDetailStatus2 = checkPermissionOneDetailStatus(str, notificationChannel.getId(), 16);
            if (iCheckPermissionOneDetailStatus2 == 1) {
                i2 |= 16;
            } else if (iCheckPermissionOneDetailStatus2 == 0) {
                i2 |= 32;
            }
        }
        return (notificationChannel.getSound() != null ? i2 | 64 : i2 | 128) | (notificationChannel.shouldVibrate() ? 256 : 512);
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0039  */
    public static int getChannelInfo(Context context, String str, String str2) {
        int i2;
        if (context == null || TextUtils.isEmpty(str)) {
            MyLog.w("Must greater than or equal android O and context|packageName not be null");
            return 0;
        }
        NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, str);
        if (notificationManagerHelperFrom == null) {
            MyLog.w("create NMHelper error");
            return 0;
        }
        NotificationChannel notificationChannel = notificationManagerHelperFrom.getNotificationChannel(notificationManagerHelperFrom.getAppChannelId(str2));
        if (notificationChannel == null) {
            MyLog.w("Channel must not be null");
            return 0;
        }
        int i3 = notificationChannel.getImportance() != 0 ? 1 : 2;
        int iCheckPermissionOneDetailStatus = checkPermissionOneDetailStatus(str, notificationChannel.getId(), 8);
        if (iCheckPermissionOneDetailStatus == 1) {
            if (notificationChannel.getImportance() >= 4) {
                i3 |= 4;
            } else {
                i3 |= 8;
            }
        } else if (iCheckPermissionOneDetailStatus == 0) {
            i3 |= 8;
        }
        int iCheckPermissionOneDetailStatus2 = checkPermissionOneDetailStatus(str, notificationChannel.getId(), 16);
        if (iCheckPermissionOneDetailStatus2 == 1) {
            i2 = i3 | 16;
        } else {
            if (iCheckPermissionOneDetailStatus2 != 0) {
                return i3;
            }
            i2 = i3 | 32;
        }
        return i2;
    }

    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(SP_PERMISSION_CACHE_LIST_FILE, 0);
    }

    static void grantOrRevokePermissions(String str, String str2, int i2, int i3) {
        for (int i4 : PERMISSION_ALL) {
            if ((USER_LOCKED_FIELDS.get(i4).intValue() & i3) == 0) {
                grantPermissionOne(str, str2, i4, (i2 & i4) > 0);
            } else {
                MyLog.w("ChannelPermissions.grantPermission:" + str + ":" + str2 + ": <" + i4 + "> :stoped by userLock");
            }
        }
    }

    private static void grantPermissionOne(String str, String str2, int i2, boolean z2) {
        MyLog.w("ChannelPermissions.grantPermission:" + str + ":" + str2 + ": <" + i2 + "=" + z2 + "> :" + NotificationUtils.setNotificationSetting(SystemUtils.getContext(), str, str2, METHODS.get(i2), z2));
    }

    static void handleChannelPermissions(Context context, String str, String str2, int i2, String str3, boolean z2, int i3) {
        if (!MIUIUtils.isXMSF(context) || TextUtils.isEmpty(str3) || TextUtils.isEmpty(str2) || TextUtils.isEmpty(str)) {
            if (MIUIUtils.isXMSF(context)) {
                MyLog.w("ChannelPC: can`t setup permission with permissionCode:" + String.valueOf(str3) + " channelId:" + String.valueOf(str2) + " targetPkg:" + str);
                return;
            }
            return;
        }
        int i4 = ToolUtils.parseInt(str3, 0);
        boolean zShouldCachePermission = shouldCachePermission(i2, i4);
        if (z2) {
            grantOrRevokePermissions(str, str2, i4, i3);
            if (zShouldCachePermission) {
                synchronized (ChannelPermissions.class) {
                    savePermissionByChannelId(getSP(context), i4, str2);
                }
                return;
            }
            return;
        }
        synchronized (ChannelPermissions.class) {
            try {
                SharedPreferences sp = getSP(context);
                if (zShouldCachePermission || sp.contains(str2)) {
                    updatePermissionIFNeedIt(sp, i4, str, str2, i3);
                    if (zShouldCachePermission) {
                        savePermissionByChannelId(sp, i4, str2);
                    } else {
                        removePermissionByChannelId(sp, str2);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private static void removePermissionByChannelId(SharedPreferences sharedPreferences, String str) {
        removePermissionByChannelIds(sharedPreferences, new ArrayList<String>(str) { // from class: com.xiaomi.push.service.ChannelPermissions.3
            final /* synthetic */ String val$channelId;

            {
                this.val$channelId = str;
                add(str);
            }
        });
    }

    private static void removePermissionByChannelIds(SharedPreferences sharedPreferences, List<String> list) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            editorEdit.remove(it.next());
        }
        editorEdit.commit();
    }

    static void removePermissionCacheByPackage(Context context, String str) {
        List<NotificationChannel> notificationChannels;
        if (!MIUIUtils.isXMSF(context) || TextUtils.isEmpty(str) || (notificationChannels = NotificationManagerHelper.from(context, str).getNotificationChannels()) == null) {
            return;
        }
        synchronized (ChannelPermissions.class) {
            try {
                SharedPreferences sp = getSP(context);
                ArrayList arrayList = new ArrayList();
                Iterator<NotificationChannel> it = notificationChannels.iterator();
                while (it.hasNext()) {
                    String str2 = (String) JavaCalls.getField(it.next(), "mId");
                    if (!TextUtils.isEmpty(str2) && sp.contains(str2)) {
                        arrayList.add(str2);
                    }
                }
                if (arrayList.size() > 0) {
                    removePermissionByChannelIds(sp, arrayList);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private static void savePermissionByChannelId(SharedPreferences sharedPreferences, int i2, String str) {
        sharedPreferences.edit().putInt(str, i2).commit();
    }

    private static boolean shouldCachePermission(int i2, int i3) {
        return i2 >= 4 || (i3 & 2) > 0 || (i3 & 1) > 0 || (i3 & 8) > 0 || (i3 & 16) > 0;
    }

    private static void updatePermissionIFNeedIt(SharedPreferences sharedPreferences, int i2, String str, String str2, int i3) {
        if (sharedPreferences.getInt(str2, 0) != i2) {
            grantOrRevokePermissions(str, str2, i2, i3);
        }
    }
}
