package com.xiaomi.push.voip;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.extension.QueuedWork;
import com.xiaomi.push.extension.XmsfR;
import com.xiaomi.push.headsup.HeadsUpConstants;
import com.xiaomi.push.headsup.HeadsUpManager;
import com.xiaomi.push.headsup.HeadsUpNotification;
import com.xiaomi.push.headsup.HeadsUpNotificationListener;
import com.xiaomi.push.service.ChannelPushReportAgent;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationChannelHelper;
import com.xiaomi.push.service.NotificationManagerHelper;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.PacketHelper;
import com.xiaomi.push.service.notification.BuilderCompat;
import com.xiaomi.push.service.notification.StatusBarNotificationWrapper;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class VoipManager {
    private static Map<String, Long> sSeqCache = new HashMap();

    public static HeadsUpNotification.Builder createHeadsUpBuilder(Context context, int i2) {
        return new HeadsUpNotification.Builder(context).setSuppressEffects(false).setContentView(new VoipCardView(context)).setDiscardWhenChannelClosed(true).setKeyguardLockedMode(2);
    }

    public static BuilderCompat createNotificationBuilder(Context context, int i2, String str, Map<String, String> map) {
        if (HeadsUpManager.getInstance(context).canHeadsUp(map, str)) {
            return ToolUtils.isKeyguardLocked(context) ? new VoipNotificationBigBuilder(context, i2, str) : new VoipNotificationSmallBuilder(context, i2, str);
        }
        VoipLog.w("not support voip, switch to default builder");
        map.remove(MIPushNotificationHelper.NOTIFICATION_LARGE_ICON_URI);
        map.put(NotificationChannelHelper.REMOVE_CHANNEL_MARK, MIPushNotificationHelper.MSG_BUSI_TYPE_VOIP);
        return new VoipNotificationStandardBuilder(context);
    }

    static void fillVoipExtras(Map<String, String> map, Bundle bundle) {
        NotificationUtils.cpMapToBundle(map, bundle, VoipConstants.EXTRA_VOIP_TYPE);
        NotificationUtils.cpMapToBundle(map, bundle, "mipush_custom_extra");
    }

    static String getVoipDrawableByType(int i2, boolean z2) {
        if (i2 == 1) {
            return z2 ? XmsfR.drawable.VOIP_VOICE_ANSWER_ICON : XmsfR.drawable.VOIP_VOICE_ANSWER_NO_BG;
        }
        if (i2 != 2) {
            return null;
        }
        return z2 ? XmsfR.drawable.VOIP_VIDEO_ANSWER_ICON : XmsfR.drawable.VOIP_VIDEO_ANSWER_NO_BG;
    }

    static int getVoipType(Object obj) {
        return ToolUtils.parseInt((String) NotificationUtils.getValue(obj, VoipConstants.EXTRA_VOIP_TYPE, ""), 0);
    }

    public static void init(final Context context) {
        HeadsUpManager.getInstance(context).registerListener(1, new HeadsUpNotificationListener() { // from class: com.xiaomi.push.voip.VoipManager.1
            @Override // com.xiaomi.push.headsup.HeadsUpNotificationListener
            public void onNotificationPosted(HeadsUpNotification headsUpNotification) {
                VoipLog.d("onNotificationPosted hun:" + headsUpNotification);
            }

            @Override // com.xiaomi.push.headsup.HeadsUpNotificationListener
            public HeadsUpConstants.ActionAfterRemoved onNotificationRemoved(HeadsUpNotification headsUpNotification, int i2) {
                if (headsUpNotification != null) {
                    VoipLog.d("onNotificationRemoved hun:" + headsUpNotification + " " + i2);
                    if (i2 == 2 || i2 == 4 || (i2 == 5 && !headsUpNotification.recovered)) {
                        VoipManager.recoverToSmallVoipNotificationInQueued(context, headsUpNotification);
                        return HeadsUpConstants.ActionAfterRemoved.RecoverToStatusBar;
                    }
                } else {
                    VoipLog.w("onNotificationRemoved hun is null " + i2);
                }
                return HeadsUpConstants.ActionAfterRemoved.DidRemoved;
            }

            @Override // com.xiaomi.push.headsup.HeadsUpNotificationListener
            public void onSnoozedNotificationReplaced(HeadsUpNotification headsUpNotification, HeadsUpNotification headsUpNotification2) {
                super.onSnoozedNotificationReplaced(headsUpNotification, headsUpNotification2);
                if (headsUpNotification == null || !VoipManager.isVoipNotification(headsUpNotification.f1747n)) {
                    return;
                }
                VoipLog.d("onSnoozedNotificationReplaced hun:" + headsUpNotification + " by " + headsUpNotification2);
                if (headsUpNotification2 == null || headsUpNotification.id != headsUpNotification2.id) {
                    VoipLog.d("onSnoozedNotificationReplaced recover to small voip");
                    VoipManager.recoverToSmallVoipNotificationInQueued(context, headsUpNotification);
                }
            }
        });
    }

    private static boolean isVoipCallNotification(Object obj) {
        return isVoipNotification(obj) && getVoipType(obj) != 0;
    }

    private static boolean isVoipCancelNotification(Object obj) {
        return isVoipNotification(obj) && getVoipType(obj) == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isVoipNotification(Object obj) {
        return MIPushNotificationHelper.MSG_BUSI_TYPE_VOIP.equals(NotificationUtils.getMsgBusinessType(obj));
    }

    public static void onBeforeNotify(Context context, Map<String, String> map, int i2, Notification notification) {
        if (MIUIUtils.isXMSF(context) && isVoipCancelNotification(map)) {
            String targetPackage = NotificationUtils.getTargetPackage(notification);
            if (TextUtils.isEmpty(targetPackage)) {
                return;
            }
            NotificationManagerHelper.from(context, targetPackage).cancel(i2);
            VoipLog.d("cancel voip call notification");
        }
    }

    @TargetApi(19)
    public static boolean onNotificationRemoved(Context context, final StatusBarNotificationWrapper statusBarNotificationWrapper, final int i2) {
        VoipLog.d("onNotificationRemoved sbn:" + statusBarNotificationWrapper);
        if (!NotificationUtils.isNotificationFromXmsf(context, statusBarNotificationWrapper)) {
            VoipLog.d("not care this notification");
            return false;
        }
        if (i2 == 1) {
            VoipLog.d("click to remove notification not report");
            return false;
        }
        if (isVoipCallNotification(statusBarNotificationWrapper.getNotification())) {
            QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.voip.VoipManager.3
                @Override // java.lang.Runnable
                public void run() {
                    VoipManager.reportVoipNotificationRemoved(statusBarNotificationWrapper.getNotification(), i2);
                }
            });
            return true;
        }
        VoipLog.d("not voip call notification");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void recoverToSmallVoipNotificationInQueued(final Context context, final HeadsUpNotification headsUpNotification) {
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.voip.VoipManager.2
            @Override // java.lang.Runnable
            public void run() {
                VoipNotificationSmallBuilder voipNotificationSmallBuilder = new VoipNotificationSmallBuilder(context, headsUpNotification.f1747n);
                NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(context, headsUpNotification.pkgName);
                NotificationUtils.suppressNotificationEffects(voipNotificationSmallBuilder, false);
                Notification notificationBuild = voipNotificationSmallBuilder.build();
                notificationManagerHelperFrom.notify(headsUpNotification.id, notificationBuild);
                VoipLog.d("recovery to notification:" + notificationBuild);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void reportVoipNotificationRemoved(Notification notification, int i2) {
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        xmPushActionNotification.setType(NotificationType.NotificationRemoved.value);
        String msgId = NotificationUtils.getMsgId(notification);
        xmPushActionNotification.setId(TextUtils.isEmpty(msgId) ? PacketHelper.generatePacketID() : msgId);
        xmPushActionNotification.setRequireAck(false);
        xmPushActionNotification.setAppId(NotificationUtils.getTargetAppId(notification));
        xmPushActionNotification.setPackageName(NotificationUtils.getTargetPackage(notification));
        HashMap map = new HashMap();
        xmPushActionNotification.extra = map;
        map.put("message_id", msgId);
        xmPushActionNotification.extra.put("removed_reason", String.valueOf(i2));
        xmPushActionNotification.extra.put(MIPushNotificationHelper.NOTIFICATION_EXTRA_MSG_BUSI_TYPE, NotificationUtils.getMsgBusinessType(notification));
        NotificationUtils.cpBundleToMap(notification.extras, xmPushActionNotification.extra, "mipush_custom_extra");
        VoipLog.d("report voip " + xmPushActionNotification);
        ChannelPushReportAgent.report(xmPushActionNotification);
    }

    public static boolean shouldDropVoipNotification(Context context, String str, Map<String, String> map) {
        if (!MIUIUtils.isXMSF(context) || !isVoipNotification(map)) {
            return false;
        }
        Long l2 = sSeqCache.get(str);
        long jLongValue = l2 == null ? 0L : l2.longValue();
        long j2 = ToolUtils.parseLong((String) NotificationUtils.getValue(map, MIPushNotificationHelper.NOTIFICATION_EXTRA_MSG_SEQ, ""), 0L);
        if (jLongValue > j2) {
            VoipLog.w("discard this msg, because seq " + jLongValue + ">" + j2);
            return true;
        }
        VoipLog.d("update cache seq " + jLongValue + " to " + j2);
        sSeqCache.put(str, Long.valueOf(j2));
        return false;
    }
}
