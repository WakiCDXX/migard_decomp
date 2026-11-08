package com.xiaomi.push.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.service.notification.StatusBarNotificationWrapper;
import com.xiaomi.xmpush.thrift.ConfigKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(24)
class NotificationGroupHelper {
    private static final String EXTRA_SRC_GROUP_NAME = "push_src_group_name";
    private static final String EXTRA_SRC_GROUP_TIME = "push_src_group_time";
    private static final String GROUP_MASK_FORMAT = "pushmask_%s_%s";
    private static final int GROUP_MIN_SIZE = 2;
    private static final String GROUP_SUMMARY_CHANNEL_ID = "groupSummary";
    private static final String GROUP_SUMMARY_TITLE = "GroupSummary";
    private static NotificationGroupHelper sInstance = new NotificationGroupHelper();

    private class AutoGroupItem {
        List<NotificationInfo> childList;
        List<NotificationInfo> summaryList;

        private AutoGroupItem() {
            this.childList = new ArrayList();
            this.summaryList = new ArrayList();
        }
    }

    private class NotificationInfo {
        Notification notification;
        int notifyId;

        public NotificationInfo(int i2, Notification notification) {
            this.notifyId = i2;
            this.notification = notification;
        }

        public String toString() {
            return "id:" + this.notifyId;
        }
    }

    private NotificationGroupHelper() {
    }

    private void cancelGroupSummary(Context context, String str, String str2) {
        MyLog.i("group cancel summary:" + str2);
        NotificationManagerHelper.from(context, str).cancel(getGroupSummaryNotifyId(str, str2));
    }

    private List<StatusBarNotification> getActiveNotifications(NotificationManagerHelper notificationManagerHelper) {
        List<StatusBarNotification> activeNotifications = notificationManagerHelper != null ? notificationManagerHelper.getActiveNotifications() : null;
        if (activeNotifications == null || activeNotifications.size() == 0) {
            return null;
        }
        return activeNotifications;
    }

    private int getGroupSummaryNotifyId(String str, String str2) {
        return (GROUP_SUMMARY_TITLE + str + str2).hashCode();
    }

    public static NotificationGroupHelper getInstance() {
        return sInstance;
    }

    private String getSrcGroup(Notification notification) {
        Bundle bundle;
        if (notification == null || (bundle = notification.extras) == null) {
            return null;
        }
        return bundle.getString(EXTRA_SRC_GROUP_NAME);
    }

    private String getTrueGroup(Notification notification) {
        if (notification == null) {
            return null;
        }
        return isMaskGroup(notification) ? getSrcGroup(notification) : notification.getGroup();
    }

    private void handleAutoGroup(Context context, int i2, Notification notification, boolean z2) {
        Notification notification2;
        String targetPackage = NotificationUtils.getTargetPackage(notification);
        if (TextUtils.isEmpty(targetPackage)) {
            MyLog.w("group auto not extract pkg from notification:" + i2);
            return;
        }
        List<StatusBarNotification> activeNotifications = getActiveNotifications(NotificationManagerHelper.from(context, targetPackage));
        if (activeNotifications == null) {
            MyLog.w("group auto not get notifications");
            return;
        }
        String trueGroup = getTrueGroup(notification);
        HashMap map = new HashMap();
        for (StatusBarNotification statusBarNotification : activeNotifications) {
            if (statusBarNotification.getNotification() != null && statusBarNotification.getId() != i2) {
                putAutoGroupItem(map, statusBarNotification);
            }
        }
        for (Map.Entry<String, AutoGroupItem> entry : map.entrySet()) {
            String key = entry.getKey();
            if (!TextUtils.isEmpty(key)) {
                AutoGroupItem value = entry.getValue();
                if (z2 && key.equals(trueGroup) && !isMaskGroup(notification)) {
                    NotificationInfo notificationInfo = new NotificationInfo(i2, notification);
                    if (isGroupSummary(notification)) {
                        value.summaryList.add(notificationInfo);
                    } else {
                        value.childList.add(notificationInfo);
                    }
                }
                int size = value.childList.size();
                if (value.summaryList.size() <= 0) {
                    if (z2 && size >= 2) {
                        showGroupSummary(context, targetPackage, key, value.childList.get(0).notification);
                    }
                } else if (size <= 0) {
                    cancelGroupSummary(context, targetPackage, key);
                } else if (OnlineConfig.getInstance(context).getBooleanValue(ConfigKey.NotificationGroupUpdateTimeSwitch.getValue(), false) && (notification2 = value.summaryList.get(0).notification) != null) {
                    notification2.when = System.currentTimeMillis();
                    showGroupSummary(context, targetPackage, key, notification2);
                }
            }
        }
    }

    private void handleRestoreGroup(Context context, int i2, Notification notification) {
        String targetPackage = NotificationUtils.getTargetPackage(notification);
        if (TextUtils.isEmpty(targetPackage)) {
            MyLog.w("group restore not extract pkg from notification:" + i2);
            return;
        }
        NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, targetPackage);
        List<StatusBarNotification> activeNotifications = getActiveNotifications(notificationManagerHelperFrom);
        if (activeNotifications == null) {
            MyLog.w("group restore not get notifications");
            return;
        }
        for (StatusBarNotification statusBarNotification : activeNotifications) {
            Notification notification2 = statusBarNotification.getNotification();
            if (notification2 != null && isMaskGroup(notification2) && statusBarNotification.getId() != i2) {
                Notification.Builder builderRecoverBuilder = Notification.Builder.recoverBuilder(context, statusBarNotification.getNotification());
                builderRecoverBuilder.setGroup(getSrcGroup(notification2));
                NotificationUtils.suppressNotificationEffects(builderRecoverBuilder, isGroupSummary(notification2));
                notificationManagerHelperFrom.notify(statusBarNotification.getId(), builderRecoverBuilder.build());
                MyLog.i("group restore notification:" + statusBarNotification.getId());
            }
        }
    }

    private boolean isEnableLatestNotificationNotIntoGroup(Context context) {
        if (isEnableNotificationAutoGroup(context) && NotificationManagerHelper.isRomSupportNotificationBelongToApp(context)) {
            return OnlineConfig.getInstance(context).getBooleanValue(ConfigKey.LatestNotificationNotIntoGroupSwitch.getValue(), false);
        }
        return false;
    }

    private boolean isEnableNotificationAutoGroup(Context context) {
        return OnlineConfig.getInstance(context).getBooleanValue(ConfigKey.NotificationAutoGroupSwitch.getValue(), true);
    }

    private boolean isGroupSummary(Notification notification) {
        if (notification == null) {
            return false;
        }
        Object objCallMethod = JavaCalls.callMethod(notification, "isGroupSummary", null);
        if (objCallMethod instanceof Boolean) {
            return ((Boolean) objCallMethod).booleanValue();
        }
        return false;
    }

    private boolean isMaskGroup(Notification notification) {
        Bundle bundle;
        if (notification == null || notification.getGroup() == null || (bundle = notification.extras) == null) {
            return false;
        }
        return notification.getGroup().equals(String.format(GROUP_MASK_FORMAT, Long.valueOf(bundle.getLong(EXTRA_SRC_GROUP_TIME)), getSrcGroup(notification)));
    }

    private boolean isSupportPlatform() {
        return true;
    }

    private void putAutoGroupItem(Map<String, AutoGroupItem> map, StatusBarNotification statusBarNotification) {
        String trueGroup = getTrueGroup(statusBarNotification.getNotification());
        AutoGroupItem autoGroupItem = map.get(trueGroup);
        if (autoGroupItem == null) {
            autoGroupItem = new AutoGroupItem();
            map.put(trueGroup, autoGroupItem);
        }
        NotificationInfo notificationInfo = new NotificationInfo(statusBarNotification.getId(), statusBarNotification.getNotification());
        if (isGroupSummary(statusBarNotification.getNotification())) {
            autoGroupItem.summaryList.add(notificationInfo);
        } else {
            autoGroupItem.childList.add(notificationInfo);
        }
    }

    private void showGroupSummary(Context context, String str, String str2, Notification notification) {
        try {
            if (TextUtils.isEmpty(str2)) {
                MyLog.w("group show summary group is null");
                return;
            }
            int idForSmallIconFromTargetPkg = NotificationUtils.getIdForSmallIconFromTargetPkg(context, str);
            if (idForSmallIconFromTargetPkg == 0) {
                MyLog.w("group show summary not get icon from " + str);
                return;
            }
            NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, str);
            String groupSummaryChannelId = notificationManagerHelperFrom.getGroupSummaryChannelId(notification.getChannelId(), GROUP_SUMMARY_CHANNEL_ID);
            NotificationChannel notificationChannel = notificationManagerHelperFrom.getNotificationChannel(groupSummaryChannelId);
            if (GROUP_SUMMARY_CHANNEL_ID.equals(groupSummaryChannelId) && notificationChannel == null) {
                notificationManagerHelperFrom.createNotificationChannel(new NotificationChannel(groupSummaryChannelId, "group_summary", 3));
            }
            Notification.Builder builder = new Notification.Builder(context, groupSummaryChannelId);
            NotificationUtils.suppressNotificationEffects(builder, true);
            Notification notificationBuild = builder.setContentTitle(GROUP_SUMMARY_TITLE).setContentText(GROUP_SUMMARY_TITLE).setSmallIcon(Icon.createWithResource(str, idForSmallIconFromTargetPkg)).setAutoCancel(true).setGroup(str2).setGroupSummary(true).build();
            if (!MIUIUtils.isXMS() && PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName())) {
                NotificationUtils.setTargetPackage(notificationBuild, str);
            }
            int groupSummaryNotifyId = getGroupSummaryNotifyId(str, str2);
            notificationManagerHelperFrom.notify(groupSummaryNotifyId, notificationBuild);
            MyLog.i("group show summary notify:" + groupSummaryNotifyId);
        } catch (Exception e2) {
            MyLog.w("group show summary error " + e2);
        }
    }

    public String maskGroup(Context context, Notification.Builder builder, String str) {
        if (!isSupportPlatform() || !isEnableLatestNotificationNotIntoGroup(context)) {
            return str;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        Bundle extras = builder.getExtras();
        extras.putString(EXTRA_SRC_GROUP_NAME, str);
        extras.putLong(EXTRA_SRC_GROUP_TIME, jCurrentTimeMillis);
        return String.format(GROUP_MASK_FORMAT, Long.valueOf(jCurrentTimeMillis), str);
    }

    public void onNotificationNotify(Context context, int i2, Notification notification) {
        if (isSupportPlatform()) {
            if (isEnableLatestNotificationNotIntoGroup(context)) {
                try {
                    handleRestoreGroup(context, i2, notification);
                } catch (Exception e2) {
                    MyLog.w("group notify handle restore error " + e2);
                }
            }
            if (isEnableNotificationAutoGroup(context)) {
                try {
                    handleAutoGroup(context, i2, notification, true);
                } catch (Exception e3) {
                    MyLog.w("group notify handle auto error " + e3);
                }
            }
        }
    }

    public void onNotificationRemoved(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper) {
        if (isSupportPlatform() && isEnableNotificationAutoGroup(context) && statusBarNotificationWrapper.getNotification() != null) {
            try {
                handleAutoGroup(context, statusBarNotificationWrapper.getId(), statusBarNotificationWrapper.getNotification(), false);
            } catch (Exception e2) {
                MyLog.w("group remove handle auto error " + e2);
            }
        }
    }
}
