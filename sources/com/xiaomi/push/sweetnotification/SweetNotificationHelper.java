package com.xiaomi.push.sweetnotification;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.SafeHandler;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.misc.ScheduledJobConstants;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.extension.AppRunningUtils;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationManagerHelper;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.notification.CustomNotificationBuilder;
import com.xiaomi.push.service.notification.StatusBarNotificationWrapper;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class SweetNotificationHelper {
    public static final int SWEET_DEFAULT_TIMEOUT_SECONDS = 180;
    private static final int SWEET_MAX_TIMEOUT_SECONDS = 7200;

    public static class SweetKeys {
        static final String CARD_ALERT = "notify_style_5_alert";
        static final String CARD_INFO_LEFT = "notify_style_5_left";
        static final String CARD_INFO_RIGHT = "notify_style_5_right";
        static final String CARD_MAP = "notify_style_5_bg_pic";
        static final String FLOAT_DESC = "notify_rich_desc";
        static final String FLOAT_TITLE = "notify_rich_title";
        static final String SWEET_DURATION = "remind_duration";
        static final String SWEET_END = "remind_end";
        static final String SWEET_SEQUENCE = "sequence";
        static final String SWEET_STATUS = "remind_status";
        static final String SWEET_TIMEOUT = "remind_timeout";
    }

    private static class Tools {
        static Handler sHandler;

        private Tools() {
        }

        static void checkoutEndNotify(Context context, int i2, String str, Map<String, String> map, Notification notification) {
            if (context == null || TextUtils.isEmpty(str) || map == null || !map.containsKey("remind_end")) {
                return;
            }
            SweetMilePostManager.deleteStatus(context, i2, str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Handler handler() {
            if (sHandler == null) {
                synchronized (Tools.class) {
                    try {
                        if (sHandler == null) {
                            HandlerThread handlerThread = new HandlerThread("sweet_notification_screen_on");
                            handlerThread.start();
                            sHandler = new SafeHandler(handlerThread.getLooper());
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
            return sHandler;
        }

        static boolean isScreenOn(Context context) {
            WindowManager windowManager = (WindowManager) context.getSystemService("window");
            Display defaultDisplay = windowManager != null ? windowManager.getDefaultDisplay() : null;
            return defaultDisplay != null && defaultDisplay.getState() == 2;
        }

        static boolean isSweetNotificationInSystemUI(Context context, int i2, String str) {
            List<StatusBarNotification> activeNotifications = NotificationManagerHelper.from(context, str).getActiveNotifications();
            if (activeNotifications == null) {
                return false;
            }
            for (StatusBarNotification statusBarNotification : activeNotifications) {
                if (statusBarNotification.getId() == i2 && SweetNotificationHelper.isSweetNotification(context, statusBarNotification)) {
                    return true;
                }
            }
            return false;
        }

        static boolean isTargetPackageForeground(Context context, String str) {
            List<ActivityManager.RunningAppProcessInfo> foregroundProcess = AppRunningUtils.getForegroundProcess(context);
            if (foregroundProcess == null) {
                return false;
            }
            Iterator<ActivityManager.RunningAppProcessInfo> it = foregroundProcess.iterator();
            if (it.hasNext()) {
                return TextUtils.equals(it.next().processName, str);
            }
            return false;
        }

        static void removeSweetNotification(Context context, int i2, String str) {
            NotificationManagerHelper notificationManagerHelperFrom;
            List<StatusBarNotification> activeNotifications;
            if (context == null || TextUtils.isEmpty(str) || (activeNotifications = (notificationManagerHelperFrom = NotificationManagerHelper.from(context, str)).getActiveNotifications()) == null) {
                return;
            }
            for (StatusBarNotification statusBarNotification : activeNotifications) {
                if (statusBarNotification.getId() == i2 && SweetNotificationHelper.isSweetNotification(context, statusBarNotification)) {
                    notificationManagerHelperFrom.cancel(i2);
                }
            }
        }

        static void resendNotificationIn(Context context, Set<String> set, String str) {
            NotificationManagerHelper notificationManagerHelperFrom;
            List<StatusBarNotification> activeNotifications;
            if (set == null || set.isEmpty() || TextUtils.isEmpty(str) || (activeNotifications = (notificationManagerHelperFrom = NotificationManagerHelper.from(context, str)).getActiveNotifications()) == null) {
                return;
            }
            for (StatusBarNotification statusBarNotification : activeNotifications) {
                if (SweetNotificationHelper.isSweetNotification(context, statusBarNotification)) {
                    Notification notification = statusBarNotification.getNotification();
                    boolean zIsEnableKeyguard = NotificationUtils.isEnableKeyguard(notification, true);
                    if (set.contains(String.valueOf(statusBarNotification.getId())) && !zIsEnableKeyguard) {
                        Notification notificationBuild = Notification.Builder.recoverBuilder(context, notification).build();
                        NotificationUtils.setEnableKeyguard(notificationBuild, true);
                        notificationManagerHelperFrom.notify(statusBarNotification.getId(), notificationBuild);
                    }
                }
            }
        }

        static void resendSweetNotificationWhenScreenOn(Context context) {
            for (Map.Entry<String, Set<String>> entry : SweetMilePostManager.getSweetNotifications(context).entrySet()) {
                resendNotificationIn(context, entry.getValue(), entry.getKey());
            }
        }
    }

    protected static SweetHightlightTitleBuilder addFloatStyle(Context context, int i2, String str, Map<String, String> map) {
        if (map == null || TextUtils.isEmpty(map.get("remind_status"))) {
            return null;
        }
        SweetHightlightTitleBuilder sweetHightlightTitleBuilder = new SweetHightlightTitleBuilder(context, i2, str);
        sweetHightlightTitleBuilder.setPushExtra(map);
        return sweetHightlightTitleBuilder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void autoFinishedSweetNotificationByTimeout(Context context, int i2, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return;
        }
        Tools.removeSweetNotification(context, i2, str);
        SweetMilePostManager.deleteStatus(context, i2, str);
    }

    private static void checkoutEndNotify(Context context, int i2, String str, Map<String, String> map, Notification notification) {
        Tools.checkoutEndNotify(context, i2, str, map, notification);
    }

    protected static int fixSweetTimeOut(int i2) {
        if (i2 > 7200) {
            i2 = 7200;
        }
        if (i2 < 180) {
            return 180;
        }
        return i2;
    }

    static String generateNotificationUpdateJobId(int i2, String str) {
        return ScheduledJobConstants.SWEET_NOTIFICATION_TIMEOUT_JOB_ID + i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + str;
    }

    protected static boolean isScreenOn(Context context) {
        return Tools.isScreenOn(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isSweetNotification(Context context, StatusBarNotification statusBarNotification) {
        Notification notification;
        return (context == null || statusBarNotification == null || (notification = statusBarNotification.getNotification()) == null || notification.extras == null || !NotificationUtils.isNotificationFromXmsf(context, statusBarNotification) || TextUtils.isEmpty(notification.extras.getString("remind_status"))) ? false : true;
    }

    public static boolean isSweetNotificationInSystemUI(Context context, int i2, String str) {
        return Tools.isSweetNotificationInSystemUI(context, i2, str);
    }

    public static void onBeforeNotify(Context context, Map<String, String> map, int i2, Notification notification) {
        checkoutEndNotify(context, i2, NotificationUtils.getTargetPackage(notification), map, notification);
        String targetPackage = NotificationUtils.getTargetPackage(notification);
        if (context != null && !TextUtils.isEmpty(targetPackage)) {
            ScheduledJobManager.getInstance(context).cancelJob(generateNotificationUpdateJobId(i2, targetPackage));
        }
        removeSweetNotificationByOtherNotification(context, i2, NotificationUtils.getTargetPackage(notification), map, notification);
    }

    public static void onNotificationPosted(final Context context, final StatusBarNotificationWrapper statusBarNotificationWrapper) {
        if (context == null || statusBarNotificationWrapper == null || !isSweetNotification(context, statusBarNotificationWrapper)) {
            return;
        }
        Tools.handler().post(new Runnable() { // from class: com.xiaomi.push.sweetnotification.SweetNotificationHelper.1
            @Override // java.lang.Runnable
            public void run() {
                Notification notification = statusBarNotificationWrapper.getNotification();
                if (notification == null || notification.extras == null) {
                    return;
                }
                final int id = statusBarNotificationWrapper.getId();
                final String targetPackage = NotificationUtils.getTargetPackage(notification);
                SweetMilePostManager.writeSequence(context, notification.extras.getString(MIPushNotificationHelper.NOTIFICATION_EXTRA_MSG_SEQ), id, targetPackage);
                int iFixSweetTimeOut = SweetNotificationHelper.fixSweetTimeOut(notification.extras.getInt("remind_timeout", 180));
                final String strGenerateNotificationUpdateJobId = SweetNotificationHelper.generateNotificationUpdateJobId(id, targetPackage);
                ScheduledJobManager.getInstance(context).addOneShootJob(new ScheduledJobManager.Job() { // from class: com.xiaomi.push.sweetnotification.SweetNotificationHelper.1.1
                    @Override // com.xiaomi.channel.commonutils.misc.ScheduledJobManager.Job
                    public String getJobId() {
                        return strGenerateNotificationUpdateJobId;
                    }

                    @Override // java.lang.Runnable
                    @TargetApi(19)
                    public void run() {
                        SweetNotificationHelper.autoFinishedSweetNotificationByTimeout(context, id, targetPackage);
                    }
                }, iFixSweetTimeOut);
                SweetMilePostManager.writeStatus(context, notification.extras.getString("remind_status"), id, targetPackage, iFixSweetTimeOut);
            }
        });
    }

    public static void onNotificationRemoved(final Context context, final StatusBarNotificationWrapper statusBarNotificationWrapper, int i2) {
        if (i2 == 19) {
            return;
        }
        if ((i2 == 2 || i2 == 1 || i2 == 3) && statusBarNotificationWrapper != null && isSweetNotification(context, statusBarNotificationWrapper)) {
            final String string = statusBarNotificationWrapper.getNotification().extras.getString("remind_status");
            Tools.handler().post(new Runnable() { // from class: com.xiaomi.push.sweetnotification.SweetNotificationHelper.3
                @Override // java.lang.Runnable
                public void run() {
                    String targetPackage = NotificationUtils.getTargetPackage(statusBarNotificationWrapper.getNotification());
                    ScheduledJobManager.getInstance(context).cancelJob(SweetNotificationHelper.generateNotificationUpdateJobId(statusBarNotificationWrapper.getId(), targetPackage));
                    SweetMilePostManager.clickOrCancelSweetNotification(context, statusBarNotificationWrapper.getId(), targetPackage, string);
                }
            });
        }
    }

    public static void onScreeanOn(final Context context, Intent intent) {
        if (intent != null && "android.intent.action.SCREEN_ON".equals(intent.getAction()) && MIUIUtils.isXMSF(context)) {
            Tools.handler().post(new Runnable() { // from class: com.xiaomi.push.sweetnotification.SweetNotificationHelper.2
                @Override // java.lang.Runnable
                public void run() {
                    Tools.resendSweetNotificationWhenScreenOn(context);
                }
            });
        }
    }

    private static void removeSweetNotificationByOtherNotification(Context context, int i2, String str, Map<String, String> map, Notification notification) {
        if (context == null || TextUtils.isEmpty(str) || map == null || !TextUtils.isEmpty(map.get("remind_status"))) {
            return;
        }
        Tools.removeSweetNotification(context, i2, str);
    }

    protected static void setSweetParams(Context context, int i2, String str, Bundle bundle, Map<String, String> map, CustomNotificationBuilder customNotificationBuilder) {
        if (map == null || customNotificationBuilder == null || bundle == null) {
            return;
        }
        String str2 = map.get("remind_status");
        if (SweetMilePostManager.isMilePost(context, str2, i2, str)) {
            boolean zIsTargetPackageForeground = Tools.isTargetPackageForeground(context, str);
            bundle.putBoolean(NotificationUtils.EXTRA_ENABLE_KEYGUARD, true);
            bundle.putBoolean(NotificationUtils.EXTRA_ENABLE_FLOAT, !zIsTargetPackageForeground);
        } else if (!TextUtils.isEmpty(str2)) {
            bundle.putBoolean(NotificationUtils.EXTRA_ENABLE_KEYGUARD, isScreenOn(context));
            bundle.putBoolean(NotificationUtils.EXTRA_ENABLE_FLOAT, false);
        }
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        bundle.putInt("remind_timeout", fixSweetTimeOut(ToolUtils.parseInt(map.get("remind_duration"), 0)));
        bundle.putString("remind_status", str2);
        String str3 = map.get(MIPushNotificationHelper.NOTIFICATION_EXTRA_MSG_SEQ);
        if (TextUtils.isEmpty(str3)) {
            return;
        }
        bundle.putString(MIPushNotificationHelper.NOTIFICATION_EXTRA_MSG_SEQ, str3);
    }

    public static boolean shouldDropSweetNotification(Context context, int i2, String str, Map<String, String> map) {
        if (MIUIUtils.isXMSF(context) && map != null) {
            String str2 = map.get("remind_status");
            if (!TextUtils.isEmpty(str2) && !SweetMilePostManager.isSequenceCorrect(context, map.get(MIPushNotificationHelper.NOTIFICATION_EXTRA_MSG_SEQ), i2, str)) {
                return true;
            }
            if (!TextUtils.isEmpty(str2) && !SweetMilePostManager.isMilePost(context, str2, i2, str) && !Tools.isSweetNotificationInSystemUI(context, i2, str)) {
                boolean zEquals = TextUtils.equals(SweetMilePostManager.clickOrCanceledStatus(context, i2, str), str2);
                if (zEquals) {
                    int iFixSweetTimeOut = fixSweetTimeOut(ToolUtils.parseInt(map.get("remind_duration"), 0));
                    ScheduledJobManager.getInstance(context).cancelJob(generateNotificationUpdateJobId(i2, str));
                    SweetMilePostManager.writeStatus(context, str2, i2, str, iFixSweetTimeOut);
                }
                return zEquals;
            }
        }
        return false;
    }

    protected static CharSequence stringFromHtml(Context context, String str) {
        if (TextUtils.isEmpty(str) || context == null) {
            return null;
        }
        if (str.startsWith("<")) {
            str = "<body>" + str + "</body>";
        }
        Spanned spannedHideEmptyStr = SweetTagHandler.hideEmptyStr(Html.fromHtml(str, 0, null, new SweetTagHandler(context)));
        return spannedHideEmptyStr == null ? str : spannedHideEmptyStr;
    }

    private static boolean isSweetNotification(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper) {
        Notification notification;
        return (context == null || statusBarNotificationWrapper == null || (notification = statusBarNotificationWrapper.getNotification()) == null || notification.extras == null || !NotificationUtils.isNotificationFromXmsf(context, statusBarNotificationWrapper) || TextUtils.isEmpty(notification.extras.getString("remind_status"))) ? false : true;
    }
}
