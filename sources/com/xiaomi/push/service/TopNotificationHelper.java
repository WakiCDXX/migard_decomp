package com.xiaomi.push.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobConstants;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.service.notification.BuilderCompat;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class TopNotificationHelper {
    private static final String EXTRA_FREQUENCY_INT = "notification_top_frequency";
    private static final String EXTRA_PERIOD_INT = "notification_top_period";
    private static final String EXTRA_REPEAT_BOOLEAN = "notification_top_repeat";
    private static final String LOCAL_CREATE_TIME_LONG = "mipush_org_when";
    private static final String LOCAL_FLAG_BOOLEAN = "mipush_n_top_flag";
    private static final String LOCAL_FREQUENCY_INT = "mipush_n_top_fre";
    private static final String LOCAL_MESSAGE_ID_STRING = "message_id";
    private static final String LOCAL_PERIOD_INT = "mipush_n_top_prd";

    private static ScheduledJobManager.Job buildTopNotificationUpdateJob(final Context context, final String str, final int i2, final String str2, final Notification notification) {
        return new ScheduledJobManager.Job() { // from class: com.xiaomi.push.service.TopNotificationHelper.1
            @Override // com.xiaomi.channel.commonutils.misc.ScheduledJobManager.Job
            public String getJobId() {
                return TopNotificationHelper.generateTopNotificationUpdateJobId(i2, str2);
            }

            @Override // java.lang.Runnable
            @TargetApi(19)
            public void run() {
                TopNotificationHelper.realScheduleTopNotificationUpdate(context, str, i2, str2, notification);
            }
        };
    }

    @TargetApi(19)
    static void cancelScheduleTopNotificationUpdateIfNeed(Context context, Notification notification, int i2) {
        if (context == null || notification == null) {
            return;
        }
        String string = notification.extras.getString("message_id");
        if (!notification.extras.getBoolean(LOCAL_FLAG_BOOLEAN, false) || TextUtils.isEmpty(string)) {
            return;
        }
        ScheduledJobManager.getInstance(context).cancelJob(generateTopNotificationUpdateJobId(i2, string));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String generateTopNotificationUpdateJobId(int i2, String str) {
        return ScheduledJobConstants.TOP_NOTIFICATION_UPDATE_JOB_ID + i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + str;
    }

    @TargetApi(19)
    private static Notification getNotificationByMsgId(Notification notification, int i2, String str, NotificationManagerHelper notificationManagerHelper) {
        if (notification != null) {
            if (!str.equals(notification.extras.getString("message_id"))) {
                notification = null;
            }
            return notification;
        }
        List<StatusBarNotification> activeNotifications = notificationManagerHelper.getActiveNotifications();
        if (activeNotifications == null) {
            return null;
        }
        for (StatusBarNotification statusBarNotification : activeNotifications) {
            Notification notification2 = statusBarNotification.getNotification();
            String string = notification2.extras.getString("message_id");
            if (i2 == statusBarNotification.getId() && str.equals(string)) {
                return notification2;
            }
        }
        return null;
    }

    private static int getNotificationPeriod(Map<String, String> map) {
        return Math.max(0, ToolUtils.parseInt(map.get(EXTRA_PERIOD_INT), 0));
    }

    private static int getNotificationUpdateFrequency(Map<String, String> map) {
        return Math.max(0, ToolUtils.parseInt(map.get(EXTRA_FREQUENCY_INT), 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(19)
    public static void realScheduleTopNotificationUpdate(Context context, String str, int i2, String str2, Notification notification) {
        NotificationManagerHelper notificationManagerHelperFrom;
        Notification notificationByMsgId;
        int iMin;
        if (context == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || (notificationByMsgId = getNotificationByMsgId(notification, i2, str2, (notificationManagerHelperFrom = NotificationManagerHelper.from(context, str)))) == null) {
            return;
        }
        boolean z2 = notification != null;
        if (notificationByMsgId.getGroupAlertBehavior() != 1) {
            JavaCalls.setField(notificationByMsgId, "mGroupAlertBehavior", 1);
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = notificationByMsgId.extras.getLong(LOCAL_CREATE_TIME_LONG, 0L);
        int i3 = notificationByMsgId.extras.getInt(LOCAL_FREQUENCY_INT, 0);
        int i4 = notificationByMsgId.extras.getInt(LOCAL_PERIOD_INT, 0);
        if (i4 <= 0 || i4 < i3) {
            return;
        }
        long j3 = ((long) (i4 * 1000)) + j2;
        if (j2 >= jCurrentTimeMillis || jCurrentTimeMillis >= j3) {
            iMin = 0;
        } else if (i3 > 0) {
            iMin = (int) Math.min((j3 - jCurrentTimeMillis) / 1000, i3);
        }
        if (!z2) {
            if (iMin > 0) {
                notificationByMsgId.when = jCurrentTimeMillis;
                MyLog.w("update top notification: " + str2);
                notificationManagerHelperFrom.notify(i2, notificationByMsgId);
            } else {
                Notification.Builder builderRecoverBuilder = Notification.Builder.recoverBuilder(context, notificationByMsgId);
                builderRecoverBuilder.setPriority(0);
                builderRecoverBuilder.setWhen(jCurrentTimeMillis);
                Bundle extras = builderRecoverBuilder.getExtras();
                if (extras != null) {
                    extras.remove(LOCAL_FLAG_BOOLEAN);
                    extras.remove(LOCAL_CREATE_TIME_LONG);
                    extras.remove(LOCAL_FREQUENCY_INT);
                    extras.remove(LOCAL_PERIOD_INT);
                    builderRecoverBuilder.setExtras(extras);
                }
                MyLog.w("update top notification to common: " + str2);
                notificationManagerHelperFrom.notify(i2, builderRecoverBuilder.build());
            }
        }
        if (iMin > 0) {
            MyLog.w("schedule top notification next update delay: " + iMin);
            ScheduledJobManager.getInstance(context).cancelJob(generateTopNotificationUpdateJobId(i2, str2));
            ScheduledJobManager.getInstance(context).addOneShootJob(buildTopNotificationUpdateJob(context, str, i2, str2, null), iMin);
        }
    }

    static void setupPutTopIfNeed(Context context, Map<String, String> map, BuilderCompat builderCompat, long j2) {
        if (map == null || builderCompat == null || !MIUIUtils.isXMSF(context) || !shouldRepeatSetTopAndToCommon(map)) {
            return;
        }
        int notificationPeriod = getNotificationPeriod(map);
        int notificationUpdateFrequency = getNotificationUpdateFrequency(map);
        if (notificationPeriod <= 0 || notificationUpdateFrequency > notificationPeriod) {
            MyLog.e("set top notification failed - period:" + notificationPeriod + " frequency:" + notificationUpdateFrequency);
            return;
        }
        builderCompat.setPriority(2);
        Bundle bundle = new Bundle();
        bundle.putLong(LOCAL_CREATE_TIME_LONG, j2);
        bundle.putBoolean(LOCAL_FLAG_BOOLEAN, true);
        if (notificationUpdateFrequency > 0) {
            bundle.putInt(LOCAL_FREQUENCY_INT, notificationUpdateFrequency);
        }
        bundle.putInt(LOCAL_PERIOD_INT, notificationPeriod);
        builderCompat.addExtras(bundle);
    }

    private static boolean shouldRepeatSetTopAndToCommon(Map<String, String> map) {
        String str = map.get(EXTRA_REPEAT_BOOLEAN);
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        boolean z2 = Boolean.parseBoolean(str);
        MyLog.v("top notification' repeat is " + z2);
        return z2;
    }

    @TargetApi(19)
    static void startScheduleTopNotificationUpdateIfNeed(Context context, String str, int i2, String str2, Notification notification) {
        if (MIUIUtils.isXMSF(context) && notification != null && notification.extras.getBoolean(LOCAL_FLAG_BOOLEAN, false)) {
            realScheduleTopNotificationUpdate(context, str, i2, str2, notification);
        }
    }
}
