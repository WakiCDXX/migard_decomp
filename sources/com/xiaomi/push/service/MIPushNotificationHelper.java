package com.xiaomi.push.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.RemoteViews;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.clientReport.PushClientReportManager;
import com.xiaomi.push.service.clientReport.ReportConstants;
import com.xiaomi.push.service.notification.BannerBuilder;
import com.xiaomi.push.service.notification.BuilderCompat;
import com.xiaomi.push.service.notification.ColorfulBuilder;
import com.xiaomi.push.service.notification.StatusBarNotificationWrapper;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MIPushNotificationHelper {
    private static final String ALLOW_DYNAMIC_ICON_ON_MIUI = "__adiom";
    public static final String ANDROID_RESOURCE = "android.resource://";
    private static final String DYNAMIC_ICON_URI = "__dynamic_icon_uri";
    public static final String EXTRA_PARAM_FLOAT_SMALL_WINDOW = "float_small_win";
    private static final String EXTRA_PARAM_IMPORTANCE_IS_PRIORITY = "section_is_prr";
    private static final String EXTRA_PARAM_IMPORTANCE_PRIORITY_CLASS = "section_prr_cl";
    public static final String EXTRA_PARAM_NOTIFY_FOREGROUND = "notify_foreground";
    public static final int FLAG_DISABLE_REPORT_NOTIFICATION_ARRIVED = 1;
    public static final int FLAG_DISABLE_REPORT_NOTIFICATION_CLICKED = 2;
    public static final int FLAG_DISABLE_REPORT_NOTIFICATION_REMOVED = 4;
    public static final String FLAG_DISABLE_SUPPRESS_NOTIFICATION_EFFECTS = "not_suppress";
    public static final int FLAG_IGNORE_REJECT_BACKGROUND_START_ACTIVITY = 2;
    private static final String FORMAT_CUSTOM_BUTTON_INTENT_CLASS = "cust_btn_%s_ic";
    private static final String FORMAT_CUSTOM_BUTTON_INTENT_URI = "cust_btn_%s_iu";
    private static final String FORMAT_CUSTOM_BUTTON_NAME = "cust_btn_%s_n";
    private static final String FORMAT_CUSTOM_BUTTON_NOTIFY_EFFECT = "cust_btn_%s_ne";
    private static final String FORMAT_CUSTOM_BUTTON_WEB_URI = "cust_btn_%s_wu";
    public static final String FROM_NOTIFICATION = "mipush_notified";
    public static final String KEY_MIPUSH_CUSTOM_EXTRA = "mipush_custom_extra";
    private static final int MAX_CUSTOM_BUTTONS = 3;
    public static final int MAX_DOWNLOAD_ONLINE_PICTURE_WAIT = 180;
    public static final int MAX_NOTIFY_ID_CACHE_SIZE = 100;
    private static final String MESSAGE_TYPE_INDEX = "satuigmo";
    public static final String MIUI_PACKAGE_NAME = "miui_package_name";
    public static final String MSG_BUSI_TYPE_NORMAL = "";
    public static final String MSG_BUSI_TYPE_PULLDOWN = "pulldown";
    public static final String MSG_BUSI_TYPE_SCENE = "scene";
    public static final String MSG_BUSI_TYPE_SWEET = "remind";
    public static final String MSG_BUSI_TYPE_VOIP = "voip";
    public static final String MSG_BUSY_TYPE_TTS = "tts";
    public static final int NOTIFICATION_ACTION_BUTTON_PLACE_1 = 1;
    public static final int NOTIFICATION_ACTION_BUTTON_PLACE_2 = 2;
    public static final int NOTIFICATION_ACTION_BUTTON_PLACE_3 = 3;
    public static final int NOTIFICATION_ACTION_BUTTON_PLACE_COLORFUL = 4;
    public static final int NOTIFICATION_ACTION_BUTTON_PLACE_CONTENT = 0;
    private static final String NOTIFICATION_BANNER_ICON_URI = "notification_banner_icon_uri";
    private static final String NOTIFICATION_BANNER_IMAGE_URI = "notification_banner_image_uri";
    private static final String NOTIFICATION_BKG_COLOR = "background_color";
    protected static final String NOTIFICATION_CHANNEL_DESCRIPTION = "channel_description";
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    protected static final String NOTIFICATION_CHANNEL_IMPORTANCE = "channel_importance";
    protected static final String NOTIFICATION_CHANNEL_NAME = "channel_name";
    protected static final String NOTIFICATION_CHANNEL_PERMISSIONS = "channel_perm";
    public static final String NOTIFICATION_CLICK_BUTTON = "notification_click_button";
    private static final String NOTIFICATION_COLORFUL_BG_COLOR = "notification_colorful_bg_color";
    private static final String NOTIFICATION_COLORFUL_BG_IMAGE_URI = "notification_colorful_bg_image_uri";
    private static final String NOTIFICATION_COLORFUL_BUTTON_BG_COLOR = "notification_colorful_button_bg_color";
    private static final String NOTIFICATION_COLORFUL_BUTTON_INTENT_CLASS = "notification_colorful_button_intent_class";
    private static final String NOTIFICATION_COLORFUL_BUTTON_INTENT_URI = "notification_colorful_button_intent_uri";
    private static final String NOTIFICATION_COLORFUL_BUTTON_NOTIFY_EFFECT = "notification_colorful_button_notify_effect";
    private static final String NOTIFICATION_COLORFUL_BUTTON_TEXT = "notification_colorful_button_text";
    private static final String NOTIFICATION_COLORFUL_BUTTON_WEB_URI = "notification_colorful_button_web_uri";
    public static final String NOTIFICATION_CUSTOM_BUILDER_SET_TITLE = "custom_builder_set_title";
    public static final String NOTIFICATION_DISABLE_FLAGS = "disable_notification_flags";
    private static final String NOTIFICATION_ENABLE_FLOAT = "enable_float";
    private static final String NOTIFICATION_ENABLE_KEYGUARD = "enable_keyguard";
    public static final String NOTIFICATION_EXTRA_JOB_KEY_STRING = "job_key";
    public static final String NOTIFICATION_EXTRA_LOCAL_PAID_STRING = "local_paid";
    public static final String NOTIFICATION_EXTRA_MESSAGE_ID_STRING = "message_id";
    public static final String NOTIFICATION_EXTRA_MSG_BUSI_TYPE = "msg_busi_type";
    public static final String NOTIFICATION_EXTRA_MSG_SEQ = "sequence";
    private static final String NOTIFICATION_EXTRA_PARAM_PUSH_UID = "pushUid";
    public static final String NOTIFICATION_EXTRA_TARGET_COMPONENT_STRING = "target_component";
    public static final String NOTIFICATION_EXTRA_TARGET_PACKAGE_STRING = "target_package";
    public static final String NOTIFICATION_GROUP = "notification_group";
    public static final String NOTIFICATION_GROUP_DISABLE_DEFAULT = "notification_group_disable_default";
    private static final String NOTIFICATION_ICON = "mipush_notification";
    public static final String NOTIFICATION_IMAGE_TEXT_COLOR = "notification_image_text_color";
    public static final String NOTIFICATION_IS_SUMMARY = "notification_is_summary";
    public static final String NOTIFICATION_LARGE_ICON_URI = "notification_large_icon_uri";
    protected static final String NOTIFICATION_LOCAL_EXTRA_CREATE_TIME_LONG = "mipush_org_when";
    private static final String NOTIFICATION_MIUI_IS_GRAYSCALE_ICON = "miui.isGrayscaleIcon";
    private static final String NOTIFICATION_PRIORITY = "notification_priority";
    public static final String NOTIFICATION_SHOW_WHEN = "notification_show_when";
    private static final String NOTIFICATION_SMALL_ICON = "mipush_small_notification";
    private static final String NOTIFICATION_SMALL_ICON_COLOR = "notification_small_icon_color";
    private static final String NOTIFICATION_SMALL_ICON_URI = "notification_small_icon_uri";
    public static final String NOTIFICATION_SOUND_URI = "sound_uri";
    private static final String NOTIFICATION_STATS_EXPOSE = "n_stats_expose";
    private static final String NOTIFICATION_STYLE_BANNER = "4";
    private static final String NOTIFICATION_STYLE_BIG_PICTURE = "2";
    private static final String NOTIFICATION_STYLE_BIG_PICTURE_URI = "notification_bigPic_uri";
    private static final String NOTIFICATION_STYLE_BIG_TEXT = "1";
    private static final String NOTIFICATION_STYLE_BUTTON_LEFT_INTENT_CLASS = "notification_style_button_left_intent_class";
    private static final String NOTIFICATION_STYLE_BUTTON_LEFT_INTENT_URI = "notification_style_button_left_intent_uri";
    private static final String NOTIFICATION_STYLE_BUTTON_LEFT_NAME = "notification_style_button_left_name";
    private static final String NOTIFICATION_STYLE_BUTTON_LEFT_NOTIFY_EFFECT = "notification_style_button_left_notify_effect";
    private static final String NOTIFICATION_STYLE_BUTTON_LEFT_WEB_URI = "notification_style_button_left_web_uri";
    private static final String NOTIFICATION_STYLE_BUTTON_MID_INTENT_CLASS = "notification_style_button_mid_intent_class";
    private static final String NOTIFICATION_STYLE_BUTTON_MID_INTENT_URI = "notification_style_button_mid_intent_uri";
    private static final String NOTIFICATION_STYLE_BUTTON_MID_NAME = "notification_style_button_mid_name";
    private static final String NOTIFICATION_STYLE_BUTTON_MID_NOTIFY_EFFECT = "notification_style_button_mid_notify_effect";
    private static final String NOTIFICATION_STYLE_BUTTON_MID_WEB_URI = "notification_style_button_mid_web_uri";
    private static final String NOTIFICATION_STYLE_BUTTON_RIGHT_INTENT_CLASS = "notification_style_button_right_intent_class";
    private static final String NOTIFICATION_STYLE_BUTTON_RIGHT_INTENT_URI = "notification_style_button_right_intent_uri";
    private static final String NOTIFICATION_STYLE_BUTTON_RIGHT_NAME = "notification_style_button_right_name";
    private static final String NOTIFICATION_STYLE_BUTTON_RIGHT_NOTIFY_EFFECT = "notification_style_button_right_notify_effect";
    private static final String NOTIFICATION_STYLE_BUTTON_RIGHT_WEB_URI = "notification_style_button_right_web_uri";
    private static final String NOTIFICATION_STYLE_COLORFUL = "3";
    public static final String NOTIFICATION_STYLE_SWEET_CARD = "5";
    public static final String NOTIFICATION_STYLE_TYPE = "notification_style_type";
    public static final String NOTIFICATION_STYLE_VOIP_BIG = "6";
    public static final String NOTIFICATION_TICKER = "ticker";
    private static final String NOTIFICATION_TIMEOUT = "timeout";
    public static final String NOTIFICATION_USE_CLICKED_ACTIVITY = "use_clicked_activity";
    public static final int NOTIFY_ALL = -1;
    public static final long NOTIFY_INTERVAL = 10000;
    public static final int NO_NOTIFY_ID = -2;
    private static final String PREF_KEY_NOTIFY_TYPE = "pref_notify_type";
    protected static final String TOP_NOTIFICATION_EXTRA_FREQUENCY_INT = "notification_top_frequency";
    protected static final String TOP_NOTIFICATION_EXTRA_PERIOD_INT = "notification_top_period";
    protected static final String TOP_NOTIFICATION_EXTRA_REPEAT_BOOLEAN = "notification_top_repeat";
    protected static final String TOP_NOTIFICATION_LOCAL_EXTRA_FLAG_BOOLEAN = "mipush_n_top_flag";
    protected static final String TOP_NOTIFICATION_LOCAL_EXTRA_FREQUENCY_INT = "mipush_n_top_fre";
    protected static final String TOP_NOTIFICATION_LOCAL_EXTRA_PERIOD_INT = "mipush_n_top_prd";
    public static long lastNotify;
    private static volatile NotificationAgent sNotificationAgent;
    private static final LinkedList<Pair<Integer, XmPushActionContainer>> notifyContainerCache = new LinkedList<>();
    private static ExecutorService sThreadPool = Executors.newCachedThreadPool();

    /* JADX INFO: renamed from: com.xiaomi.push.service.MIPushNotificationHelper$1, reason: invalid class name */
    class AnonymousClass1 extends ScheduledJobManager.Job {
        final /* synthetic */ String val$jobId;
        final /* synthetic */ NotificationManagerHelper val$nmh;
        final /* synthetic */ int val$notificationId;

        AnonymousClass1(String str, NotificationManagerHelper notificationManagerHelper, int i2) {
            this.val$jobId = str;
            this.val$nmh = notificationManagerHelper;
            this.val$notificationId = i2;
        }

        @Override // com.xiaomi.channel.commonutils.misc.ScheduledJobManager.Job
        public String getJobId() {
            return this.val$jobId;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.val$nmh.cancel(this.val$notificationId);
        }
    }

    private static class DownloadOnlinePicTask implements Callable<Bitmap> {
        private Context context;
        private boolean isSizeLimited;
        private String picUrl;

        public DownloadOnlinePicTask(String str, Context context, boolean z2) {
            this.context = context;
            this.picUrl = str;
            this.isSizeLimited = z2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public Bitmap call() throws Exception {
            if (TextUtils.isEmpty(this.picUrl)) {
                MyLog.w("Failed get online picture/icon resource cause picUrl is empty");
                return null;
            }
            if (this.picUrl.startsWith("http")) {
                NotificationIconHelper.GetIconResult iconFromUrl = NotificationIconHelper.getIconFromUrl(this.context, this.picUrl, this.isSizeLimited);
                if (iconFromUrl != null) {
                    return iconFromUrl.bitmap;
                }
                MyLog.w("Failed get online picture/icon resource");
                return null;
            }
            Bitmap iconFromUri = NotificationIconHelper.getIconFromUri(this.context, this.picUrl);
            if (iconFromUri != null) {
                return iconFromUri;
            }
            MyLog.w("Failed get online picture/icon resource");
            return iconFromUri;
        }
    }

    public static class GetNotificationResult {
        Notification notification;
        long trafficSize = 0;
    }

    public static class NotifyPushMessageInfo {
        public String targetPkgName;
        public long traffic = 0;
        public boolean isNotifyFromStatusBar = false;
    }

    private static boolean checkMatch(String str, String str2) {
        return TextUtils.isEmpty(str) || str2.contains(str);
    }

    public static void clearHeadsUpNotification(Context context, String str) {
        if (!MIUIUtils.isXMSF(context) || sNotificationAgent == null || TextUtils.isEmpty(str)) {
            return;
        }
        sNotificationAgent.clearHeadsUpNotification(str);
    }

    static void clearLocalNotifyType(Context context, String str) {
        context.getSharedPreferences(PREF_KEY_NOTIFY_TYPE, 0).edit().remove(str).commit();
    }

    public static void clearNotification(Context context, String str) {
        clearNotification(context, str, -1);
    }

    @TargetApi(16)
    private static BuilderCompat createBuilderWithStyle(Context context, XmPushActionContainer xmPushActionContainer, byte[] bArr, String str, int i2) {
        PendingIntent stylePendingIntent;
        String targetPackage = getTargetPackage(xmPushActionContainer);
        Map<String, String> extra = xmPushActionContainer.getMetaInfo().getExtra();
        String str2 = extra.get(NOTIFICATION_STYLE_TYPE);
        BuilderCompat builderCompatOnCustomNotificationBuilder = (!MIUIUtils.isXMSF(context) || sNotificationAgent == null) ? null : sNotificationAgent.onCustomNotificationBuilder(context, i2, targetPackage, extra);
        if (builderCompatOnCustomNotificationBuilder != null) {
            builderCompatOnCustomNotificationBuilder.setPushExtra(extra);
            return builderCompatOnCustomNotificationBuilder;
        }
        if ("2".equals(str2)) {
            BuilderCompat builderCompat = new BuilderCompat(context);
            Bitmap onlinePictureResource = TextUtils.isEmpty(extra.get(NOTIFICATION_STYLE_BIG_PICTURE_URI)) ? null : getOnlinePictureResource(context, extra.get(NOTIFICATION_STYLE_BIG_PICTURE_URI), false);
            if (onlinePictureResource == null) {
                MyLog.w("can not get big picture.");
                return builderCompat;
            }
            Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle(builderCompat);
            bigPictureStyle.bigPicture(onlinePictureResource);
            bigPictureStyle.setSummaryText(str);
            bigPictureStyle.bigLargeIcon((Bitmap) null);
            builderCompat.setStyle(bigPictureStyle);
            return builderCompat;
        }
        if ("1".equals(str2)) {
            BuilderCompat builderCompat2 = new BuilderCompat(context);
            builderCompat2.setStyle(new Notification.BigTextStyle().bigText(str));
            return builderCompat2;
        }
        if ("4".equals(str2) && MIUIUtils.isMIUI()) {
            BannerBuilder bannerBuilder = new BannerBuilder(context, targetPackage);
            if (!TextUtils.isEmpty(extra.get(NOTIFICATION_BANNER_IMAGE_URI))) {
                bannerBuilder.setBanner(getOnlinePictureResource(context, extra.get(NOTIFICATION_BANNER_IMAGE_URI), false));
            }
            if (!TextUtils.isEmpty(extra.get(NOTIFICATION_BANNER_ICON_URI))) {
                bannerBuilder.setIcon(getOnlinePictureResource(context, extra.get(NOTIFICATION_BANNER_ICON_URI), false));
            }
            bannerBuilder.setPushExtra(extra);
            return bannerBuilder;
        }
        if (!"3".equals(str2) || !MIUIUtils.isMIUI()) {
            return new BuilderCompat(context);
        }
        ColorfulBuilder colorfulBuilder = new ColorfulBuilder(context, i2, targetPackage);
        if (!TextUtils.isEmpty(extra.get(NOTIFICATION_COLORFUL_BUTTON_TEXT)) && (stylePendingIntent = getStylePendingIntent(context, targetPackage, xmPushActionContainer, bArr, i2, 4)) != null) {
            colorfulBuilder.addAction(extra.get(NOTIFICATION_COLORFUL_BUTTON_TEXT), stylePendingIntent).setActionBackground(extra.get(NOTIFICATION_COLORFUL_BUTTON_BG_COLOR));
        }
        if (!TextUtils.isEmpty(extra.get(NOTIFICATION_COLORFUL_BG_COLOR))) {
            colorfulBuilder.setBackground(extra.get(NOTIFICATION_COLORFUL_BG_COLOR));
        } else if (!TextUtils.isEmpty(extra.get(NOTIFICATION_COLORFUL_BG_IMAGE_URI))) {
            colorfulBuilder.setBackground(getOnlinePictureResource(context, extra.get(NOTIFICATION_COLORFUL_BG_IMAGE_URI), false));
        }
        colorfulBuilder.setPushExtra(extra);
        return colorfulBuilder;
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0072 A[PHI: r0 r3
      0x0072: PHI (r0v4 java.lang.String) = (r0v2 java.lang.String), (r0v5 java.lang.String) binds: [B:18:0x0070, B:10:0x004e] A[DONT_GENERATE, DONT_INLINE]
      0x0072: PHI (r3v14 java.lang.String) = (r3v13 java.lang.String), (r3v20 java.lang.String) binds: [B:18:0x0070, B:10:0x004e] A[DONT_GENERATE, DONT_INLINE]] */
    private static String[] determineTitleAndDespByDIP(Context context, PushMetaInfo pushMetaInfo) {
        String str;
        String title = pushMetaInfo.getTitle();
        String description = pushMetaInfo.getDescription();
        Map<String, String> extra = pushMetaInfo.getExtra();
        if (extra != null) {
            int iIntValue = Float.valueOf((context.getResources().getDisplayMetrics().widthPixels / context.getResources().getDisplayMetrics().density) + 0.5f).intValue();
            if (iIntValue <= 320) {
                String str2 = extra.get("title_short");
                if (!TextUtils.isEmpty(str2)) {
                    title = str2;
                }
                str = extra.get("description_short");
                if (!TextUtils.isEmpty(str)) {
                    description = str;
                }
            } else if (iIntValue > 360) {
                String str3 = extra.get("title_long");
                if (!TextUtils.isEmpty(str3)) {
                    title = str3;
                }
                str = extra.get("description_long");
                if (!TextUtils.isEmpty(str)) {
                    description = str;
                }
            }
        }
        return new String[]{title, description};
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        if (intrinsicWidth <= 0) {
            intrinsicWidth = 1;
        }
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight > 0 ? intrinsicHeight : 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    private static Notification enableCustomIconForMiui(Notification notification) {
        Object field = JavaCalls.getField(notification, "extraNotification");
        if (field != null) {
            JavaCalls.callMethod(field, "setCustomizedIcon", Boolean.TRUE);
        }
        return notification;
    }

    private static void filterIntentFlag(Intent intent) {
        if (intent == null) {
            return;
        }
        intent.setFlags(intent.getFlags() & (-196));
    }

    private static Bitmap getBitmapFromId(Context context, int i2) {
        return drawableToBitmap(context.getResources().getDrawable(i2));
    }

    private static int getChannelImportance(Map<String, String> map) {
        if (map == null) {
            return 3;
        }
        String str = map.get(NOTIFICATION_CHANNEL_IMPORTANCE);
        if (TextUtils.isEmpty(str)) {
            return 3;
        }
        try {
            MyLog.v("importance=" + str);
            return Integer.parseInt(str);
        } catch (Exception e2) {
            MyLog.e("parsing channel importance error: " + e2);
            return 3;
        }
    }

    private static String getChannelName(Context context, String str, Map<String, String> map) {
        return (map == null || TextUtils.isEmpty(map.get("channel_name"))) ? AppInfoUtils.getAppLabel(context, str) : map.get("channel_name");
    }

    private static int getClickNotificationIntentComponentHash(Context context, String str, Map<String, String> map, int i2) {
        ComponentName componentNameCheckAndGetComponent;
        Intent notificationMessageIntent = getNotificationMessageIntent(context, str, map, i2);
        if (notificationMessageIntent == null || (componentNameCheckAndGetComponent = ComponentHelper.checkAndGetComponent(context, notificationMessageIntent)) == null) {
            return 0;
        }
        return componentNameCheckAndGetComponent.hashCode();
    }

    public static ComponentName getClickedActivity(String str) {
        return new ComponentName(str, "com.xiaomi.mipush.sdk.NotificationClickedActivity");
    }

    private static PendingIntent getClickedPendingIntent(Context context, XmPushActionContainer xmPushActionContainer, String str, byte[] bArr, int i2) {
        return getClickedPendingIntentByButton(context, xmPushActionContainer, str, bArr, i2, 0, shouldUseClickedActivity(context, xmPushActionContainer, str));
    }

    private static PendingIntent getClickedPendingIntentByButton(Context context, XmPushActionContainer xmPushActionContainer, String str, byte[] bArr, int i2, int i3, boolean z2) {
        int i4;
        Intent intent;
        if (isNormalNotificationMessage(xmPushActionContainer)) {
            i4 = 1000;
        } else {
            i4 = isBusinessMessage(xmPushActionContainer) ? ReportConstants.AWAKE_TYPE : -1;
        }
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        String id = metaInfo != null ? metaInfo.getId() : "";
        boolean zIsBusinessMessage = isBusinessMessage(xmPushActionContainer);
        if (metaInfo != null && !TextUtils.isEmpty(metaInfo.url)) {
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setData(Uri.parse(metaInfo.url));
            try {
                String protocol = new URL(metaInfo.url).getProtocol();
                if ("http".equals(protocol) || "https".equals(protocol)) {
                    NotificationUtils.setBestPkgForUrlIfNeed(context, str, intent2);
                } else {
                    intent2.setPackage(str);
                }
            } catch (MalformedURLException unused) {
                MyLog.w("meet URL exception : " + metaInfo.url);
                intent2.setPackage(str);
            }
            intent2.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
            intent2.putExtra("messageId", id);
            intent2.putExtra(ReportConstants.EVENT_MESSAGE_TYPE, i4);
            return PendingIntent.getActivity(context, 0, intent2, 167772160);
        }
        if (zIsBusinessMessage) {
            intent = new Intent();
            intent.setComponent(new ComponentName(PushConstants.PUSH_SERVICE_PACKAGE_NAME, "com.xiaomi.mipush.sdk.PushMessageHandler"));
            intent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArr);
            intent.putExtra(FROM_NOTIFICATION, true);
            intent.addCategory(String.valueOf(i2));
            intent.addCategory(String.valueOf(id));
        } else {
            intent = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
            intent.setComponent(new ComponentName(str, "com.xiaomi.mipush.sdk.PushMessageHandler"));
            intent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArr);
            intent.putExtra(FROM_NOTIFICATION, true);
            intent.addCategory(String.valueOf(i2));
            intent.addCategory(String.valueOf(id));
        }
        intent.putExtra("notification_click_button", i3);
        intent.putExtra("messageId", id);
        intent.putExtra(ReportConstants.EVENT_MESSAGE_TYPE, i4);
        if (zIsBusinessMessage || !z2) {
            insertInfoForStartActivityStatistics(context, intent, xmPushActionContainer, metaInfo, id, i3);
            return PendingIntent.getService(context, 0, intent, 167772160);
        }
        Intent intent3 = new Intent();
        intent3.setComponent(getClickedActivity(str));
        intent3.addFlags(276824064);
        intent3.putExtra(PushConstants.MIPUSH_EXTRA_INTENT_PAYLOAD, intent);
        intent3.addCategory(String.valueOf(i2));
        intent3.addCategory(String.valueOf(id));
        intent3.addCategory(String.valueOf(i3));
        insertInfoForStartActivityStatistics(context, intent3, xmPushActionContainer, metaInfo, id, i3);
        return PendingIntent.getActivity(context, 0, intent3, 167772160);
    }

    private static int getIconId(Context context, String str, String str2) {
        if (str.equals(context.getPackageName())) {
            return context.getResources().getIdentifier(str2, "drawable", str);
        }
        return 0;
    }

    private static int getIdForSmallIcon(Context context, String str) {
        int iconId = getIconId(context, str, NOTIFICATION_ICON);
        int iconId2 = getIconId(context, str, NOTIFICATION_SMALL_ICON);
        if (iconId <= 0) {
            iconId = iconId2 > 0 ? iconId2 : context.getApplicationInfo().icon;
        }
        return iconId == 0 ? context.getApplicationInfo().logo : iconId;
    }

    public static String getInterfaceId(XmPushActionContainer xmPushActionContainer) {
        if (isBusinessMessage(xmPushActionContainer)) {
            return ReportConstants.AWAKE_EVENT_CHAIN_INTERFACE_ID;
        }
        if (isNormalNotificationMessage(xmPushActionContainer)) {
            return ReportConstants.NOTIFICATION_EVENT_CHAIN_INTERFACE_ID;
        }
        if (isPassThoughMessage(xmPushActionContainer)) {
            return ReportConstants.THROUGH_EVENT_CHAIN_INTERFACE_ID;
        }
        return isRegisterMessage(xmPushActionContainer) ? ReportConstants.REGISTER_EVENT_CHAIN_INTERFACE_ID : "";
    }

    static int getLocalNotifyType(Context context, String str) {
        return context.getSharedPreferences(PREF_KEY_NOTIFY_TYPE, 0).getInt(str, Integer.MAX_VALUE);
    }

    private static String getMapValue(Map<String, String> map, String str) {
        if (map != null) {
            return map.get(str);
        }
        return null;
    }

    private static RemoteViews getNotificationForCustomLayout(Context context, XmPushActionContainer xmPushActionContainer, byte[] bArr) {
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        String targetPackage = getTargetPackage(xmPushActionContainer);
        if (metaInfo != null && metaInfo.getExtra() != null) {
            Map<String, String> extra = metaInfo.getExtra();
            String str = extra.get("layout_name");
            String str2 = extra.get("layout_value");
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
                try {
                    Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(targetPackage);
                    int identifier = resourcesForApplication.getIdentifier(str, "layout", targetPackage);
                    if (identifier == 0) {
                        return null;
                    }
                    RemoteViews remoteViews = new RemoteViews(targetPackage, identifier);
                    try {
                        JSONObject jSONObject = new JSONObject(str2);
                        if (jSONObject.has("text")) {
                            JSONObject jSONObject2 = jSONObject.getJSONObject("text");
                            Iterator<String> itKeys = jSONObject2.keys();
                            while (itKeys.hasNext()) {
                                String next = itKeys.next();
                                String string = jSONObject2.getString(next);
                                int identifier2 = resourcesForApplication.getIdentifier(next, "id", targetPackage);
                                if (identifier2 > 0) {
                                    remoteViews.setTextViewText(identifier2, string);
                                }
                            }
                        }
                        if (jSONObject.has(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                            JSONObject jSONObject3 = jSONObject.getJSONObject(YellowPageContract.ImageLookup.DIRECTORY_IMAGE);
                            Iterator<String> itKeys2 = jSONObject3.keys();
                            while (itKeys2.hasNext()) {
                                String next2 = itKeys2.next();
                                String string2 = jSONObject3.getString(next2);
                                int identifier3 = resourcesForApplication.getIdentifier(next2, "id", targetPackage);
                                int identifier4 = resourcesForApplication.getIdentifier(string2, "drawable", targetPackage);
                                if (identifier3 > 0) {
                                    remoteViews.setImageViewResource(identifier3, identifier4);
                                }
                            }
                        }
                        if (jSONObject.has("time")) {
                            JSONObject jSONObject4 = jSONObject.getJSONObject("time");
                            Iterator<String> itKeys3 = jSONObject4.keys();
                            while (itKeys3.hasNext()) {
                                String next3 = itKeys3.next();
                                String string3 = jSONObject4.getString(next3);
                                if (string3.length() == 0) {
                                    string3 = "yy-MM-dd hh:mm";
                                }
                                int identifier5 = resourcesForApplication.getIdentifier(next3, "id", targetPackage);
                                if (identifier5 > 0) {
                                    remoteViews.setTextViewText(identifier5, new SimpleDateFormat(string3).format(new Date(System.currentTimeMillis())));
                                }
                            }
                        }
                        return remoteViews;
                    } catch (JSONException e2) {
                        MyLog.e(e2);
                        return null;
                    }
                } catch (PackageManager.NameNotFoundException e3) {
                    MyLog.e(e3);
                }
            }
        }
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:45:0x0151  */
    /* JADX WARN: Code duplicated, block: B:88:0x0264  */
    @SuppressLint({"NewApi"})
    private static GetNotificationResult getNotificationForLargeIcons(Context context, XmPushActionContainer xmPushActionContainer, byte[] bArr, RemoteViews remoteViews, PendingIntent pendingIntent, int i2) throws Throwable {
        BuilderCompat builderCompat;
        boolean z2;
        boolean z3;
        boolean z4;
        String str;
        boolean z5;
        boolean z6;
        GetNotificationResult getNotificationResult;
        int localNotifyType;
        Bitmap iconFromUri;
        Bitmap bitmap;
        GetNotificationResult getNotificationResult2 = new GetNotificationResult();
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        String targetPackage = getTargetPackage(xmPushActionContainer);
        Map<String, String> extra = metaInfo.getExtra();
        String[] strArrDetermineTitleAndDespByDIP = determineTitleAndDespByDIP(context, metaInfo);
        if (remoteViews != null) {
            builderCompat = new BuilderCompat(context);
            builderCompat.setCustomContentView(remoteViews);
        } else {
            builderCompat = (extra == null || !extra.containsKey(NOTIFICATION_STYLE_TYPE)) ? new BuilderCompat(context) : createBuilderWithStyle(context, xmPushActionContainer, bArr, strArrDetermineTitleAndDespByDIP[1], i2);
        }
        BuilderCompat builderCompat2 = builderCompat;
        setNotificationStyleAction(builderCompat2, context, xmPushActionContainer.getPackageName(), xmPushActionContainer, bArr, i2);
        builderCompat2.setContentTitle(strArrDetermineTitleAndDespByDIP[0]);
        builderCompat2.setContentText(strArrDetermineTitleAndDespByDIP[1]);
        long jCurrentTimeMillis = System.currentTimeMillis();
        builderCompat2.setWhen(jCurrentTimeMillis);
        String mapValue = getMapValue(extra, NOTIFICATION_SHOW_WHEN);
        if (TextUtils.isEmpty(mapValue)) {
            builderCompat2.setShowWhen(true);
        } else {
            builderCompat2.setShowWhen(Boolean.parseBoolean(mapValue));
        }
        builderCompat2.setContentIntent(pendingIntent);
        setLocalSmallAndLargeIcon(context, targetPackage, builderCompat2, extra);
        Bitmap onlinePictureResource = extra == null ? null : getOnlinePictureResource(context, extra.get(NOTIFICATION_SMALL_ICON_URI), true);
        if (onlinePictureResource != null) {
            Object objCallStaticMethod = JavaCalls.callStaticMethod("android.graphics.drawable.Icon", "createWithBitmap", onlinePictureResource);
            if (objCallStaticMethod != null) {
                JavaCalls.callMethod(builderCompat2, "setSmallIcon", objCallStaticMethod);
                Bundle bundle = new Bundle();
                bundle.putBoolean(NOTIFICATION_MIUI_IS_GRAYSCALE_ICON, true);
                builderCompat2.addExtras(bundle);
            } else {
                MyLog.w("failed te get small icon with url:" + extra.get(NOTIFICATION_SMALL_ICON_URI));
            }
        } else {
            MyLog.w("failed to get small icon url:" + getMapValue(extra, NOTIFICATION_SMALL_ICON_URI));
        }
        builderCompat2.setColorStr(getMapValue(extra, NOTIFICATION_SMALL_ICON_COLOR));
        String mapValue2 = getMapValue(extra, DYNAMIC_ICON_URI);
        boolean z7 = Boolean.parseBoolean(getMapValue(extra, ALLOW_DYNAMIC_ICON_ON_MIUI)) || !MIUIUtils.isMIUI();
        if (TextUtils.isEmpty(mapValue2) || !z7) {
            iconFromUri = bitmap;
            z2 = false;
        } else {
            if (mapValue2.startsWith("http")) {
                NotificationIconHelper.GetIconResult iconFromUrl = NotificationIconHelper.getIconFromUrl(context, mapValue2, true);
                if (iconFromUrl != null) {
                    bitmap = iconFromUrl.bitmap;
                    getNotificationResult2.trafficSize = iconFromUrl.downloadSize;
                } else {
                    iconFromUri = null;
                }
            } else {
                iconFromUri = NotificationIconHelper.getIconFromUri(context, mapValue2);
            }
            if (iconFromUri != null) {
                builderCompat2.setLargeIcon(iconFromUri);
                z2 = true;
            } else {
                iconFromUri = bitmap;
                z2 = false;
            }
        }
        Bitmap onlinePictureResource2 = extra == null ? null : getOnlinePictureResource(context, extra.get(NOTIFICATION_LARGE_ICON_URI), true);
        if (onlinePictureResource2 != null) {
            iconFromUri = bitmap;
            iconFromUri = bitmap;
            builderCompat2.setLargeIcon(onlinePictureResource2);
        }
        iconFromUri = bitmap;
        iconFromUri = bitmap;
        String str2 = PushConstants.PUSH_SERVICE_PACKAGE_NAME;
        if (extra != null) {
            String str3 = extra.get(NOTIFICATION_GROUP);
            z5 = Boolean.parseBoolean(extra.get(NOTIFICATION_IS_SUMMARY));
            String targetPackage2 = (!TextUtils.isEmpty(str3) || (!MIUIUtils.isMIUI() && Boolean.parseBoolean(extra.get(NOTIFICATION_GROUP_DISABLE_DEFAULT)))) ? str3 : getTargetPackage(xmPushActionContainer);
            JavaCalls.callMethod(builderCompat2, "setGroupSummary", Boolean.valueOf(z5));
            String str4 = extra.get(NOTIFICATION_STYLE_TYPE);
            if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName()) && ("4".equals(str4) || "3".equals(str4))) {
                str = getTargetPackage(xmPushActionContainer) + "_custom_" + jCurrentTimeMillis;
                z3 = true;
                z4 = true;
            } else {
                str = targetPackage2;
                z3 = true;
                z4 = false;
            }
        } else {
            z3 = true;
            z4 = false;
            str = null;
            z5 = false;
        }
        builderCompat2.setAutoCancel(z3);
        long jCurrentTimeMillis2 = System.currentTimeMillis();
        if (extra != null && extra.containsKey(NOTIFICATION_TICKER)) {
            builderCompat2.setTicker(extra.get(NOTIFICATION_TICKER));
        }
        if (jCurrentTimeMillis2 - lastNotify > 10000) {
            lastNotify = jCurrentTimeMillis2;
            localNotifyType = metaInfo.notifyType;
            if (hasLocalNotifyType(context, targetPackage)) {
                localNotifyType = getLocalNotifyType(context, targetPackage);
            }
            builderCompat2.setDefaults(localNotifyType);
            if (extra == null || (localNotifyType & 1) == 0) {
                z6 = z2;
                getNotificationResult = getNotificationResult2;
            } else {
                String str5 = extra.get(NOTIFICATION_SOUND_URI);
                if (TextUtils.isEmpty(str5)) {
                    z6 = z2;
                    getNotificationResult = getNotificationResult2;
                } else {
                    getNotificationResult = getNotificationResult2;
                    StringBuilder sb = new StringBuilder();
                    z6 = z2;
                    sb.append(ANDROID_RESOURCE);
                    sb.append(targetPackage);
                    if (str5.startsWith(sb.toString())) {
                        builderCompat2.setDefaults(localNotifyType ^ 1);
                        builderCompat2.setSound(Uri.parse(str5));
                    }
                }
            }
        } else {
            z6 = z2;
            getNotificationResult = getNotificationResult2;
            localNotifyType = -100;
        }
        if (extra != null) {
            NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, targetPackage);
            int timeout = getTimeout(extra);
            if (timeout > 0) {
                JavaCalls.callMethod(builderCompat2, "setTimeoutAfter", Long.valueOf(timeout * 1000));
            }
            NotificationChannelHelper.removeChannelInfoIfNeed(metaInfo);
            String str6 = extra.get(NOTIFICATION_CHANNEL_ID);
            if (!TextUtils.isEmpty(str6) || context.getApplicationInfo().targetSdkVersion >= 26) {
                String channelName = getChannelName(context, targetPackage, extra);
                int channelImportance = getChannelImportance(extra);
                int i3 = metaInfo.notifyType;
                String str7 = extra.get(NOTIFICATION_CHANNEL_DESCRIPTION);
                String str8 = extra.get(NOTIFICATION_SOUND_URI);
                String str9 = extra.get(NOTIFICATION_CHANNEL_PERMISSIONS);
                TopNotificationHelper.setupPutTopIfNeed(context, extra, builderCompat2, jCurrentTimeMillis);
                JavaCalls.callMethod(builderCompat2, "setChannelId", NotificationChannelHelper.createChannel(notificationManagerHelperFrom, str6, channelName, str7, i3, channelImportance, str8, str9));
                if (localNotifyType == -100 && NotificationUtils.isSuppressNotificationEffects(extra)) {
                    NotificationUtils.suppressNotificationEffects(builderCompat2, z5);
                }
                if (MSG_BUSI_TYPE_PULLDOWN.equals(NotificationUtils.getMsgBusinessType(extra)) && NotificationUtils.isSuppressNotificationEffects(extra) && Objects.equals(extra.get(PluginConstants.PULL_DOWN_POP_TYPE), "0")) {
                    NotificationUtils.suppressNotificationEffects(builderCompat2, z5);
                }
                if (MSG_BUSY_TYPE_TTS.equals(NotificationUtils.getMsgBusinessType(extra)) && NotificationUtils.isSuppressNotificationEffects(extra)) {
                    NotificationUtils.suppressNotificationEffects(builderCompat2, z5);
                }
            }
            String str10 = extra.get(NOTIFICATION_BKG_COLOR);
            if (!TextUtils.isEmpty(str10)) {
                try {
                    int i4 = Integer.parseInt(str10);
                    builderCompat2.setOngoing(true);
                    builderCompat2.setColor(i4);
                    JavaCalls.callMethod(builderCompat2, "setColorized", Boolean.TRUE);
                } catch (Exception e2) {
                    MyLog.e(e2);
                }
            }
        } else {
            str2 = PushConstants.PUSH_SERVICE_PACKAGE_NAME;
            str = str;
        }
        if (str != null) {
            JavaCalls.callMethod(builderCompat2, "setGroup", !z4 ? NotificationGroupHelper.getInstance().maskGroup(context, builderCompat2, str) : str);
        }
        if (MIUIUtils.isXMS()) {
            if (str2.equals(context.getPackageName())) {
                JavaCalls.callStaticMethod("miui.util.NotificationHelper", "setTargetPkg", context, builderCompat2, getTargetPackage(xmPushActionContainer));
            }
        }
        Notification notification = builderCompat2.getNotification();
        if (z6 && MIUIUtils.isMIUI()) {
            enableCustomIconForMiui(notification);
        }
        if (extra != null) {
            if (notification.extras == null) {
                notification.extras = new Bundle();
            }
            if (!TextUtils.isEmpty(extra.get(NOTIFICATION_ENABLE_KEYGUARD))) {
                NotificationUtils.setEnableKeyguard(notification, Boolean.parseBoolean(extra.get(NOTIFICATION_ENABLE_KEYGUARD)));
            }
            if (!TextUtils.isEmpty(extra.get(NOTIFICATION_ENABLE_FLOAT))) {
                NotificationUtils.setEnableFloat(notification, Boolean.parseBoolean(extra.get(NOTIFICATION_ENABLE_FLOAT)));
            }
            if (!TextUtils.isEmpty(extra.get(EXTRA_PARAM_FLOAT_SMALL_WINDOW)) && "0".equals(extra.get(EXTRA_PARAM_FLOAT_SMALL_WINDOW)) && AppInfoUtils.isApplicationInSmallWindow(context, targetPackage)) {
                NotificationUtils.setEnableFloat(notification, false);
            }
            int i5 = ToolUtils.parseInt(extra.get(EXTRA_PARAM_IMPORTANCE_IS_PRIORITY), -1);
            int i6 = ToolUtils.parseInt(extra.get(EXTRA_PARAM_IMPORTANCE_PRIORITY_CLASS), -1);
            if (i5 >= 0 && i6 >= 0) {
                NotificationUtils.setImportanceSection(notification, i5, i6);
            }
        }
        GetNotificationResult getNotificationResult3 = getNotificationResult;
        getNotificationResult3.notification = notification;
        return getNotificationResult3;
    }

    public static Intent getNotificationMessageIntent(Context context, String str, Map<String, String> map, int i2) {
        Intent launchIntentForPackage;
        String str2;
        Intent intent;
        Intent uri;
        if (map == null) {
            return null;
        }
        if (i2 != 0) {
            return getStyleButtonIntent(context, str, map, i2);
        }
        if (!map.containsKey(PushConstants.EXTRA_PARAM_NOTIFY_EFFECT)) {
            return null;
        }
        String str3 = map.get(PushConstants.EXTRA_PARAM_NOTIFY_EFFECT);
        String str4 = map.get(PushConstants.EXTRA_PARAM_INTENT_FLAG);
        int i3 = -1;
        try {
            if (!TextUtils.isEmpty(str4)) {
                i3 = Integer.parseInt(str4);
            }
        } catch (NumberFormatException e2) {
            MyLog.e("Cause by intent_flag: " + e2.getMessage());
        }
        if (PushConstants.NOTIFICATION_CLICK_DEFAULT.equals(str3)) {
            try {
                launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
            } catch (Exception e3) {
                MyLog.e("Cause: " + e3.getMessage());
                launchIntentForPackage = null;
            }
        } else if (PushConstants.NOTIFICATION_CLICK_INTENT.equals(str3)) {
            if (map.containsKey(PushConstants.EXTRA_PARAM_INTENT_URI)) {
                String str5 = map.get(PushConstants.EXTRA_PARAM_INTENT_URI);
                if (str5 != null) {
                    try {
                        uri = Intent.parseUri(str5, 1);
                        try {
                            uri.setPackage(str);
                        } catch (URISyntaxException e4) {
                            e = e4;
                            MyLog.e("Cause: " + e.getMessage());
                        }
                    } catch (URISyntaxException e5) {
                        e = e5;
                        uri = null;
                    }
                    launchIntentForPackage = uri;
                }
            } else if (map.containsKey(PushConstants.EXTRA_PARAM_CLASS_NAME)) {
                String str6 = map.get(PushConstants.EXTRA_PARAM_CLASS_NAME);
                intent = new Intent();
                intent.setComponent(new ComponentName(str, str6));
                launchIntentForPackage = intent;
            }
            launchIntentForPackage = null;
        } else {
            if (PushConstants.NOTIFICATION_CLICK_WEB_PAGE.equals(str3) && (str2 = map.get(PushConstants.EXTRA_PARAM_WEB_URI)) != null) {
                String strTrim = str2.trim();
                if (!strTrim.startsWith("http://") && !strTrim.startsWith("https://")) {
                    strTrim = "http://" + strTrim;
                }
                try {
                    String protocol = new URL(strTrim).getProtocol();
                    if ("http".equals(protocol) || "https".equals(protocol)) {
                        intent = new Intent("android.intent.action.VIEW");
                        try {
                            intent.setData(Uri.parse(strTrim));
                            NotificationUtils.setBestPkgForUrlIfNeed(context, str, intent);
                        } catch (MalformedURLException e6) {
                            e = e6;
                            MyLog.e("Cause: " + e.getMessage());
                        }
                        launchIntentForPackage = intent;
                    }
                } catch (MalformedURLException e7) {
                    e = e7;
                    intent = null;
                }
            }
            launchIntentForPackage = null;
        }
        if (launchIntentForPackage != null) {
            if (i3 >= 0) {
                launchIntentForPackage.setFlags(i3);
            }
            filterIntentFlag(launchIntentForPackage);
            launchIntentForPackage.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
            try {
                if (context.getPackageManager().resolveActivity(launchIntentForPackage, LogType.LOG_TYPE_SAMPLING_INTERRUPT) != null) {
                    return launchIntentForPackage;
                }
                if (!MIUIUtils.isXMSF(context) && PushConstants.NOTIFICATION_CLICK_WEB_PAGE.equals(str3)) {
                    return launchIntentForPackage;
                }
                MyLog.w("not resolve activity:" + launchIntentForPackage);
            } catch (Exception e8) {
                MyLog.e("Cause: " + e8.getMessage());
            }
        }
        return null;
    }

    public static String getNotifyEffectByButton(Map<String, String> map, int i2) {
        String str;
        if (i2 == 0) {
            str = PushConstants.EXTRA_PARAM_NOTIFY_EFFECT;
        } else if (isSupportCustomStyleButton(map)) {
            str = String.format(FORMAT_CUSTOM_BUTTON_NOTIFY_EFFECT, Integer.valueOf(i2));
        } else if (i2 == 1) {
            str = NOTIFICATION_STYLE_BUTTON_LEFT_NOTIFY_EFFECT;
        } else if (i2 == 2) {
            str = NOTIFICATION_STYLE_BUTTON_MID_NOTIFY_EFFECT;
        } else if (i2 == 3) {
            str = NOTIFICATION_STYLE_BUTTON_RIGHT_NOTIFY_EFFECT;
        } else {
            str = i2 == 4 ? NOTIFICATION_COLORFUL_BUTTON_NOTIFY_EFFECT : null;
        }
        if (map == null || str == null) {
            return null;
        }
        return map.get(str);
    }

    private static Bitmap getOnlinePictureResource(Context context, String str, boolean z2) {
        Future futureSubmit = sThreadPool.submit(new DownloadOnlinePicTask(str, context, z2));
        try {
            try {
                try {
                    Bitmap bitmap = (Bitmap) futureSubmit.get(180L, TimeUnit.SECONDS);
                    if (bitmap != null) {
                        return bitmap;
                    }
                    futureSubmit.cancel(true);
                    return bitmap;
                } catch (ExecutionException e2) {
                    MyLog.e(e2);
                    futureSubmit.cancel(true);
                    return null;
                }
            } catch (InterruptedException e3) {
                MyLog.e(e3);
                futureSubmit.cancel(true);
                return null;
            } catch (TimeoutException e4) {
                MyLog.e(e4);
                futureSubmit.cancel(true);
                return null;
            }
        } catch (Throwable th) {
            futureSubmit.cancel(true);
            throw th;
        }
    }

    private static Intent getPendingIntentFromExtra(Context context, String str, Map<String, String> map, String str2, String str3, String str4, String str5) {
        Intent launchIntentForPackage;
        Intent intent;
        Intent uri;
        String str6 = map.get(str2);
        if (TextUtils.isEmpty(str6)) {
            return null;
        }
        if (PushConstants.NOTIFICATION_CLICK_DEFAULT.equals(str6)) {
            try {
                launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
            } catch (Exception e2) {
                MyLog.e("Cause: " + e2.getMessage());
                launchIntentForPackage = null;
            }
        } else if (PushConstants.NOTIFICATION_CLICK_INTENT.equals(str6)) {
            if (map.containsKey(str3)) {
                String str7 = map.get(str3);
                if (str7 != null) {
                    try {
                        uri = Intent.parseUri(str7, 1);
                        try {
                            uri.setPackage(str);
                        } catch (URISyntaxException e3) {
                            e = e3;
                            MyLog.e("Cause: " + e.getMessage());
                        }
                    } catch (URISyntaxException e4) {
                        e = e4;
                        uri = null;
                    }
                    launchIntentForPackage = uri;
                }
            } else if (map.containsKey(str4)) {
                String str8 = map.get(str4);
                intent = new Intent();
                intent.setComponent(new ComponentName(str, str8));
                launchIntentForPackage = intent;
            }
            launchIntentForPackage = null;
        } else {
            if (PushConstants.NOTIFICATION_CLICK_WEB_PAGE.equals(str6)) {
                String str9 = map.get(str5);
                if (!TextUtils.isEmpty(str9)) {
                    String strTrim = str9.trim();
                    if (!strTrim.startsWith("http://") && !strTrim.startsWith("https://")) {
                        strTrim = "http://" + strTrim;
                    }
                    try {
                        String protocol = new URL(strTrim).getProtocol();
                        if ("http".equals(protocol) || "https".equals(protocol)) {
                            intent = new Intent("android.intent.action.VIEW");
                            try {
                                intent.setData(Uri.parse(strTrim));
                                NotificationUtils.setBestPkgForUrlIfNeed(context, str, intent);
                            } catch (MalformedURLException e5) {
                                e = e5;
                                MyLog.e("Cause: " + e.getMessage());
                            }
                            launchIntentForPackage = intent;
                        }
                    } catch (MalformedURLException e6) {
                        e = e6;
                        intent = null;
                    }
                }
            }
            launchIntentForPackage = null;
        }
        if (launchIntentForPackage != null) {
            launchIntentForPackage.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
            try {
                if (context.getPackageManager().resolveActivity(launchIntentForPackage, LogType.LOG_TYPE_SAMPLING_INTERRUPT) != null) {
                    return launchIntentForPackage;
                }
                if (!MIUIUtils.isXMSF(context) && PushConstants.NOTIFICATION_CLICK_WEB_PAGE.equals(str6)) {
                    return launchIntentForPackage;
                }
                MyLog.w("not resolve activity:" + launchIntentForPackage + "for buttons");
            } catch (Exception e7) {
                MyLog.e("Cause: " + e7.getMessage());
            }
        }
        return null;
    }

    private static int getPriority(Map<String, String> map) {
        if (map == null) {
            return 0;
        }
        String str = map.get(NOTIFICATION_PRIORITY);
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        try {
            MyLog.v("priority=" + str);
            return Integer.parseInt(str);
        } catch (Exception e2) {
            MyLog.e("parsing notification priority error: " + e2);
            return 0;
        }
    }

    public static Intent getStyleButtonIntent(Context context, String str, Map<String, String> map, int i2) {
        if (isSupportCustomStyleButton(map)) {
            return getPendingIntentFromExtra(context, str, map, String.format(FORMAT_CUSTOM_BUTTON_NOTIFY_EFFECT, Integer.valueOf(i2)), String.format(FORMAT_CUSTOM_BUTTON_INTENT_URI, Integer.valueOf(i2)), String.format(FORMAT_CUSTOM_BUTTON_INTENT_CLASS, Integer.valueOf(i2)), String.format(FORMAT_CUSTOM_BUTTON_WEB_URI, Integer.valueOf(i2)));
        }
        if (i2 == 1) {
            return getPendingIntentFromExtra(context, str, map, NOTIFICATION_STYLE_BUTTON_LEFT_NOTIFY_EFFECT, NOTIFICATION_STYLE_BUTTON_LEFT_INTENT_URI, NOTIFICATION_STYLE_BUTTON_LEFT_INTENT_CLASS, NOTIFICATION_STYLE_BUTTON_LEFT_WEB_URI);
        }
        if (i2 == 2) {
            return getPendingIntentFromExtra(context, str, map, NOTIFICATION_STYLE_BUTTON_MID_NOTIFY_EFFECT, NOTIFICATION_STYLE_BUTTON_MID_INTENT_URI, NOTIFICATION_STYLE_BUTTON_MID_INTENT_CLASS, NOTIFICATION_STYLE_BUTTON_MID_WEB_URI);
        }
        if (i2 == 3) {
            return getPendingIntentFromExtra(context, str, map, NOTIFICATION_STYLE_BUTTON_RIGHT_NOTIFY_EFFECT, NOTIFICATION_STYLE_BUTTON_RIGHT_INTENT_URI, NOTIFICATION_STYLE_BUTTON_RIGHT_INTENT_CLASS, NOTIFICATION_STYLE_BUTTON_RIGHT_WEB_URI);
        }
        if (i2 != 4) {
            return null;
        }
        return getPendingIntentFromExtra(context, str, map, NOTIFICATION_COLORFUL_BUTTON_NOTIFY_EFFECT, NOTIFICATION_COLORFUL_BUTTON_INTENT_URI, NOTIFICATION_COLORFUL_BUTTON_INTENT_CLASS, NOTIFICATION_COLORFUL_BUTTON_WEB_URI);
    }

    private static PendingIntent getStylePendingIntent(Context context, String str, XmPushActionContainer xmPushActionContainer, byte[] bArr, int i2, int i3) {
        Map<String, String> extra = xmPushActionContainer.getMetaInfo().getExtra();
        if (extra == null) {
            return null;
        }
        boolean zShouldUseClickedActivity = shouldUseClickedActivity(context, xmPushActionContainer, str);
        if (zShouldUseClickedActivity) {
            return getClickedPendingIntentByButton(context, xmPushActionContainer, str, bArr, i2, i3, zShouldUseClickedActivity);
        }
        Intent styleButtonIntent = getStyleButtonIntent(context, str, extra, i3);
        if (styleButtonIntent != null) {
            return PendingIntent.getActivity(context, 0, styleButtonIntent, 167772160);
        }
        return null;
    }

    public static String getTargetPackage(XmPushActionContainer xmPushActionContainer) {
        PushMetaInfo metaInfo;
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(xmPushActionContainer.packageName) && (metaInfo = xmPushActionContainer.getMetaInfo()) != null && metaInfo.getExtra() != null) {
            String str = metaInfo.getExtra().get(MIUI_PACKAGE_NAME);
            if (!TextUtils.isEmpty(str)) {
                return str;
            }
        }
        return xmPushActionContainer.packageName;
    }

    private static int getTimeout(Map<String, String> map) {
        String str = map == null ? null : map.get(NOTIFICATION_TIMEOUT);
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception unused) {
            return 0;
        }
    }

    static boolean hasLocalNotifyType(Context context, String str) {
        return context.getSharedPreferences(PREF_KEY_NOTIFY_TYPE, 0).contains(str);
    }

    private static void insertInfoForStartActivityStatistics(Context context, Intent intent, XmPushActionContainer xmPushActionContainer, PushMetaInfo pushMetaInfo, String str, int i2) {
        if (xmPushActionContainer == null || pushMetaInfo == null || TextUtils.isEmpty(str)) {
            return;
        }
        String notifyEffectByButton = getNotifyEffectByButton(pushMetaInfo.getExtra(), i2);
        if (TextUtils.isEmpty(notifyEffectByButton)) {
            return;
        }
        if (PushConstants.NOTIFICATION_CLICK_DEFAULT.equals(notifyEffectByButton) || PushConstants.NOTIFICATION_CLICK_INTENT.equals(notifyEffectByButton) || PushConstants.NOTIFICATION_CLICK_WEB_PAGE.equals(notifyEffectByButton)) {
            intent.putExtra("messageId", str);
            intent.putExtra(NOTIFICATION_EXTRA_LOCAL_PAID_STRING, xmPushActionContainer.appid);
            if (!TextUtils.isEmpty(xmPushActionContainer.packageName)) {
                intent.putExtra("target_package", xmPushActionContainer.packageName);
            }
            intent.putExtra(NOTIFICATION_EXTRA_JOB_KEY_STRING, getMapValue(pushMetaInfo.getExtra(), "jobkey"));
            intent.putExtra(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + NOTIFICATION_EXTRA_TARGET_COMPONENT_STRING, getClickNotificationIntentComponentHash(context, xmPushActionContainer.packageName, pushMetaInfo.getExtra(), i2));
        }
    }

    public static boolean isApplicationForeground(Context context, String str) {
        return AppInfoUtils.isApplicationForeground(context, str);
    }

    public static boolean isBusinessMessage(XmPushActionContainer xmPushActionContainer) {
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        return isIdVaild(metaInfo) && metaInfo.isIgnoreRegInfo();
    }

    private static boolean isIdVaild(PushMetaInfo pushMetaInfo) {
        if (pushMetaInfo == null) {
            return false;
        }
        String id = pushMetaInfo.getId();
        return !TextUtils.isEmpty(id) && id.length() == 22 && MESSAGE_TYPE_INDEX.indexOf(id.charAt(0)) >= 0;
    }

    public static boolean isLimitNotifyForeground(Context context, String str, boolean z2) {
        return MIUIUtils.isMIUI() && !z2 && isApplicationForeground(context, str);
    }

    public static boolean isNPBMessage(XmPushActionContainer xmPushActionContainer) {
        return isBusinessMessage(xmPushActionContainer) || isNormalNotificationMessage(xmPushActionContainer) || isPassThoughMessage(xmPushActionContainer);
    }

    public static boolean isNormalNotificationMessage(XmPushActionContainer xmPushActionContainer) {
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        return isIdVaild(metaInfo) && metaInfo.passThrough == 0 && !isBusinessMessage(xmPushActionContainer);
    }

    public static boolean isNotifyForeground(Map<String, String> map) {
        if (map == null || !map.containsKey(EXTRA_PARAM_NOTIFY_FOREGROUND)) {
            return true;
        }
        return "1".equals(map.get(EXTRA_PARAM_NOTIFY_FOREGROUND));
    }

    public static boolean isPassThoughMessage(XmPushActionContainer xmPushActionContainer) {
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        return isIdVaild(metaInfo) && metaInfo.passThrough == 1 && !isBusinessMessage(xmPushActionContainer);
    }

    public static boolean isRegisterMessage(XmPushActionContainer xmPushActionContainer) {
        return xmPushActionContainer.getAction() == ActionType.Registration;
    }

    private static boolean isSupportCustomStyleButton(Map<String, String> map) {
        if (map != null) {
            return "6".equals(map.get(NOTIFICATION_STYLE_TYPE));
        }
        MyLog.w("meta extra is null");
        return false;
    }

    public static NotifyPushMessageInfo notifyPushMessage(Context context, XmPushActionContainer xmPushActionContainer, byte[] bArr) {
        int notifyId;
        Map<String, String> extra;
        int i2;
        NotifyPushMessageInfo notifyPushMessageInfo = new NotifyPushMessageInfo();
        AppInfoUtils.AppNotificationOp appNotificationOp = AppInfoUtils.getAppNotificationOp(context, getTargetPackage(xmPushActionContainer), true);
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        if (metaInfo != null) {
            notifyId = metaInfo.getNotifyId();
            extra = metaInfo.getExtra();
        } else {
            notifyId = 0;
            extra = null;
        }
        int iGenerateRealID = ToolUtils.generateRealID(getTargetPackage(xmPushActionContainer), notifyId);
        if (MIUIUtils.isXMSF(context) && appNotificationOp == AppInfoUtils.AppNotificationOp.NOT_ALLOWED) {
            if (metaInfo != null) {
                PushClientReportManager.getInstance(context.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainer.getPackageName(), getInterfaceId(xmPushActionContainer), metaInfo.getId(), "10:" + getTargetPackage(xmPushActionContainer));
            }
            MyLog.w("Do not notify because user block " + getTargetPackage(xmPushActionContainer) + "‘s notification");
            return notifyPushMessageInfo;
        }
        if (MIUIUtils.isXMSF(context) && sNotificationAgent != null && sNotificationAgent.shouldBlockNotification(context, iGenerateRealID, getTargetPackage(xmPushActionContainer), extra)) {
            if (metaInfo != null) {
                PushClientReportManager.getInstance(context.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainer.getPackageName(), getInterfaceId(xmPushActionContainer), metaInfo.getId(), "14:" + getTargetPackage(xmPushActionContainer));
            }
            MyLog.w("Do not notify because card notification is canceled or sequence incorrect");
            return notifyPushMessageInfo;
        }
        RemoteViews notificationForCustomLayout = getNotificationForCustomLayout(context, xmPushActionContainer, bArr);
        PendingIntent clickedPendingIntent = getClickedPendingIntent(context, xmPushActionContainer, xmPushActionContainer.getPackageName(), bArr, iGenerateRealID);
        if (clickedPendingIntent == null) {
            if (metaInfo != null) {
                PushClientReportManager.getInstance(context.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainer.getPackageName(), getInterfaceId(xmPushActionContainer), metaInfo.getId(), "11");
            }
            MyLog.w("The click PendingIntent is null. ");
            return notifyPushMessageInfo;
        }
        GetNotificationResult notificationForLargeIcons = getNotificationForLargeIcons(context, xmPushActionContainer, bArr, notificationForCustomLayout, clickedPendingIntent, iGenerateRealID);
        notifyPushMessageInfo.traffic = notificationForLargeIcons.trafficSize;
        notifyPushMessageInfo.targetPkgName = getTargetPackage(xmPushActionContainer);
        Notification notification = notificationForLargeIcons.notification;
        if (MIUIUtils.isMIUI()) {
            if (!TextUtils.isEmpty(metaInfo.getId())) {
                notification.extras.putString("message_id", metaInfo.getId());
            }
            notification.extras.putString(NOTIFICATION_EXTRA_LOCAL_PAID_STRING, xmPushActionContainer.getAppid());
            NotificationUtils.cpMapToBundle(extra, notification.extras, NOTIFICATION_EXTRA_MSG_BUSI_TYPE);
            NotificationUtils.cpMapToBundle(extra, notification.extras, NOTIFICATION_DISABLE_FLAGS);
            String str = metaInfo.getInternal() == null ? null : metaInfo.getInternal().get("score_info");
            if (!TextUtils.isEmpty(str)) {
                notification.extras.putString("score_info", str);
            }
            notification.extras.putString(NOTIFICATION_EXTRA_PARAM_PUSH_UID, getMapValue(metaInfo.extra, NOTIFICATION_STATS_EXPOSE));
            if (isNormalNotificationMessage(xmPushActionContainer)) {
                i2 = 1000;
            } else {
                i2 = isBusinessMessage(xmPushActionContainer) ? ReportConstants.AWAKE_TYPE : -1;
            }
            notification.extras.putString(ReportConstants.EVENT_MESSAGE_TYPE, String.valueOf(i2));
            notification.extras.putString("target_package", getTargetPackage(xmPushActionContainer));
        }
        String str2 = metaInfo.getExtra() != null ? metaInfo.getExtra().get("message_count") : null;
        if (MIUIUtils.isMIUI() && str2 != null) {
            try {
                NotificationUtils.setMessageCount(notification, Integer.parseInt(str2));
            } catch (NumberFormatException e2) {
                PushClientReportManager.getInstance(context.getApplicationContext()).reportEvent4ERROR(xmPushActionContainer.getPackageName(), getInterfaceId(xmPushActionContainer), metaInfo.getId(), "8");
                MyLog.e("fail to set message count. " + e2);
            }
        }
        String targetPackage = getTargetPackage(xmPushActionContainer);
        NotificationUtils.setTargetPackage(notification, targetPackage);
        NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, targetPackage);
        if (MIUIUtils.isXMSF(context) && sNotificationAgent != null) {
            sNotificationAgent.onBeforeNotify(xmPushActionContainer, metaInfo.getExtra(), iGenerateRealID, notification);
        }
        if (MIUIUtils.isXMSF(context) && sNotificationAgent != null && sNotificationAgent.handleNotify(metaInfo.getExtra(), iGenerateRealID, notification)) {
            MyLog.i("consume this notificaiton by agent");
        } else {
            notificationManagerHelperFrom.notify(iGenerateRealID, notification);
            notifyPushMessageInfo.isNotifyFromStatusBar = true;
            MyLog.w("notification: " + metaInfo.getId() + " is notifyied");
        }
        if (MIUIUtils.isMIUI() && MIUIUtils.isXMSF(context)) {
            NotificationGroupHelper.getInstance().onNotificationNotify(context, iGenerateRealID, notification);
            TopNotificationHelper.startScheduleTopNotificationUpdateIfNeed(context, targetPackage, iGenerateRealID, metaInfo.getId(), notification);
        }
        if (isBusinessMessage(xmPushActionContainer)) {
            PushClientReportManager.getInstance(context.getApplicationContext()).reportEvent(xmPushActionContainer.getPackageName(), getInterfaceId(xmPushActionContainer), metaInfo.getId(), ReportConstants.AWAKE_TYPE_TRY_SHOW, null);
        }
        if (isNormalNotificationMessage(xmPushActionContainer)) {
            PushClientReportManager.getInstance(context.getApplicationContext()).reportEvent(xmPushActionContainer.getPackageName(), getInterfaceId(xmPushActionContainer), metaInfo.getId(), 1002, null);
        }
        Pair<Integer, XmPushActionContainer> pair = new Pair<>(Integer.valueOf(iGenerateRealID), xmPushActionContainer);
        LinkedList<Pair<Integer, XmPushActionContainer>> linkedList = notifyContainerCache;
        synchronized (linkedList) {
            try {
                linkedList.add(pair);
                if (linkedList.size() > 100) {
                    linkedList.remove();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return notifyPushMessageInfo;
    }

    public static void onNotificationRemoved(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper) {
        if (statusBarNotificationWrapper == null || !NotificationUtils.isNotificationFromXmsf(context, statusBarNotificationWrapper)) {
            return;
        }
        NotificationGroupHelper.getInstance().onNotificationRemoved(context, statusBarNotificationWrapper);
        Notification notification = statusBarNotificationWrapper.getNotification();
        if (notification != null) {
            TopNotificationHelper.cancelScheduleTopNotificationUpdateIfNeed(context, notification, statusBarNotificationWrapper.getId());
        }
    }

    private static void setChannelDescription(Object obj, Map<String, String> map) {
        if (map == null || TextUtils.isEmpty(map.get(NOTIFICATION_CHANNEL_DESCRIPTION))) {
            return;
        }
        JavaCalls.callMethod(obj, "setDescription", map.get(NOTIFICATION_CHANNEL_DESCRIPTION));
    }

    static void setLocalNotifyType(Context context, String str, int i2) {
        context.getSharedPreferences(PREF_KEY_NOTIFY_TYPE, 0).edit().putInt(str, i2).commit();
    }

    private static void setLocalSmallAndLargeIcon(Context context, String str, BuilderCompat builderCompat, Map<String, String> map) {
        int iconId = getIconId(context, str, NOTIFICATION_SMALL_ICON);
        int iconId2 = getIconId(context, str, NOTIFICATION_ICON);
        if (MIUIUtils.isXMSF(context)) {
            if (iconId <= 0 || iconId2 <= 0) {
                setSmallIconBackup(context, str, builderCompat, map);
                return;
            } else {
                builderCompat.setSmallIcon(iconId);
                builderCompat.setLargeIcon(getBitmapFromId(context, iconId2));
                return;
            }
        }
        if (iconId > 0) {
            builderCompat.setSmallIcon(iconId);
        } else {
            setSmallIconBackup(context, str, builderCompat, map);
        }
        if (iconId2 > 0) {
            builderCompat.setLargeIcon(getBitmapFromId(context, iconId2));
        }
    }

    public static void setNotificationAgent(NotificationAgent notificationAgent) {
        sNotificationAgent = notificationAgent;
    }

    @TargetApi(16)
    private static void setNotificationStyleAction(BuilderCompat builderCompat, Context context, String str, XmPushActionContainer xmPushActionContainer, byte[] bArr, int i2) {
        PendingIntent stylePendingIntent;
        PendingIntent stylePendingIntent2;
        PendingIntent stylePendingIntent3;
        PendingIntent stylePendingIntent4;
        Map<String, String> extra = xmPushActionContainer.getMetaInfo().getExtra();
        if (TextUtils.equals("3", extra.get(NOTIFICATION_STYLE_TYPE)) || TextUtils.equals("4", extra.get(NOTIFICATION_STYLE_TYPE))) {
            return;
        }
        if (isSupportCustomStyleButton(extra)) {
            for (int i3 = 1; i3 <= 3; i3++) {
                String str2 = extra.get(String.format(FORMAT_CUSTOM_BUTTON_NAME, Integer.valueOf(i3)));
                if (!TextUtils.isEmpty(str2) && (stylePendingIntent4 = getStylePendingIntent(context, str, xmPushActionContainer, bArr, i2, i3)) != null) {
                    builderCompat.addAction(0, str2, stylePendingIntent4);
                }
            }
            return;
        }
        if (!TextUtils.isEmpty(extra.get(NOTIFICATION_STYLE_BUTTON_LEFT_NAME)) && (stylePendingIntent3 = getStylePendingIntent(context, str, xmPushActionContainer, bArr, i2, 1)) != null) {
            builderCompat.addAction(0, extra.get(NOTIFICATION_STYLE_BUTTON_LEFT_NAME), stylePendingIntent3);
        }
        if (!TextUtils.isEmpty(extra.get(NOTIFICATION_STYLE_BUTTON_MID_NAME)) && (stylePendingIntent2 = getStylePendingIntent(context, str, xmPushActionContainer, bArr, i2, 2)) != null) {
            builderCompat.addAction(0, extra.get(NOTIFICATION_STYLE_BUTTON_MID_NAME), stylePendingIntent2);
        }
        if (TextUtils.isEmpty(extra.get(NOTIFICATION_STYLE_BUTTON_RIGHT_NAME)) || (stylePendingIntent = getStylePendingIntent(context, str, xmPushActionContainer, bArr, i2, 3)) == null) {
            return;
        }
        builderCompat.addAction(0, extra.get(NOTIFICATION_STYLE_BUTTON_RIGHT_NAME), stylePendingIntent);
    }

    private static void setSmallIconBackup(Context context, String str, BuilderCompat builderCompat, Map<String, String> map) {
        int iconId;
        if (!MIUIUtils.isXMSF(context)) {
            String mapValue = getMapValue(map, "fcm_icon_uri");
            String mapValue2 = getMapValue(map, "fcm_icon_color");
            if (!TextUtils.isEmpty(mapValue) && !TextUtils.isEmpty(mapValue2) && (iconId = getIconId(context, str, mapValue)) > 0) {
                builderCompat.setSmallIcon(iconId);
                builderCompat.setColorStr(mapValue2);
                return;
            }
        }
        builderCompat.setSmallIcon(Icon.createWithResource(str, NotificationUtils.getIdForSmallIconFromTargetPkg(context, str)));
    }

    private static boolean shouldUseClickedActivity(Context context, XmPushActionContainer xmPushActionContainer, String str) {
        if (xmPushActionContainer != null && xmPushActionContainer.getMetaInfo() != null && xmPushActionContainer.getMetaInfo().getExtra() != null && !TextUtils.isEmpty(str)) {
            return Boolean.parseBoolean(xmPushActionContainer.getMetaInfo().getExtra().get(NOTIFICATION_USE_CLICKED_ACTIVITY)) && ComponentHelper.checkActivity(context, getClickedActivity(str));
        }
        MyLog.w("should clicked activity params are null.");
        return false;
    }

    public static void uploadClearMessageData(Context context, LinkedList<? extends Object> linkedList) {
        if (linkedList == null || linkedList.size() <= 0) {
            return;
        }
        TinyDataHelper.cacheTinyData(context, PushConstants.DOT_CATEGORY_CLEAR_NOTIFICATION, PushConstants.CLEAR_NOTIFICATION, linkedList.size(), "");
    }

    public static void clearNotification(Context context, String str, int i2) {
        clearNotification(context, str, i2, -1);
    }

    public static void clearNotification(Context context, String str, int i2, int i3) {
        int iHashCode;
        if (context == null || TextUtils.isEmpty(str) || i2 < -1) {
            return;
        }
        NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, str);
        List<StatusBarNotification> activeNotifications = notificationManagerHelperFrom.getActiveNotifications();
        if (ToolUtils.isEmptyList(activeNotifications)) {
            return;
        }
        LinkedList linkedList = new LinkedList();
        boolean z2 = false;
        if (i2 == -1) {
            z2 = true;
            iHashCode = 0;
        } else {
            iHashCode = ((str.hashCode() / 10) * 10) + i2;
        }
        for (StatusBarNotification statusBarNotification : activeNotifications) {
            if (!TextUtils.isEmpty(String.valueOf(statusBarNotification.getId()))) {
                int id = statusBarNotification.getId();
                if (!z2) {
                    if (iHashCode == id) {
                        ButonClickRemoveNotificationHelper.appRemovedNotification(context, statusBarNotification, i3);
                        linkedList.add(statusBarNotification);
                        notificationManagerHelperFrom.cancel(id);
                        break;
                    }
                } else {
                    linkedList.add(statusBarNotification);
                    notificationManagerHelperFrom.cancel(id);
                }
            }
        }
        uploadClearMessageData(context, linkedList);
    }

    public static void clearNotification(Context context, String str, String str2, String str3) {
        if (context == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3)) {
            return;
        }
        NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, str);
        List<StatusBarNotification> activeNotifications = notificationManagerHelperFrom.getActiveNotifications();
        if (ToolUtils.isEmptyList(activeNotifications)) {
            return;
        }
        LinkedList linkedList = new LinkedList();
        for (StatusBarNotification statusBarNotification : activeNotifications) {
            Notification notification = statusBarNotification.getNotification();
            if (notification != null && !TextUtils.isEmpty(String.valueOf(statusBarNotification.getId()))) {
                int id = statusBarNotification.getId();
                String strResolveTitle = NotificationUtils.resolveTitle(notification);
                String strResolveContent = NotificationUtils.resolveContent(notification);
                if (!TextUtils.isEmpty(strResolveTitle) && !TextUtils.isEmpty(strResolveContent) && checkMatch(strResolveTitle, str2) && checkMatch(strResolveContent, str3)) {
                    linkedList.add(statusBarNotification);
                    notificationManagerHelperFrom.cancel(id);
                }
            }
        }
        uploadClearMessageData(context, linkedList);
    }
}
