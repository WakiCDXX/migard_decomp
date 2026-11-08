package com.xiaomi.push.headsup;

import android.text.TextUtils;
import com.xiaomi.push.extension.QueuedWork;
import com.xiaomi.push.service.ChannelPushReportAgent;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.PacketHelper;
import com.xiaomi.push.service.notification.CustomNotificationConstants;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
class HeadsUpReport {
    private HeadsUpReport() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static XmPushActionNotification buildReportData(HeadsUpNotification headsUpNotification, int i2) {
        String targetAppId = NotificationUtils.getTargetAppId(headsUpNotification.f1747n);
        String msgId = NotificationUtils.getMsgId(headsUpNotification.f1747n);
        String strGeneratePacketID = TextUtils.isEmpty(msgId) ? PacketHelper.generatePacketID() : msgId;
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        xmPushActionNotification.setId(strGeneratePacketID);
        xmPushActionNotification.setType(NotificationType.HeadsUpReporter.value);
        xmPushActionNotification.setRequireAck(false);
        xmPushActionNotification.setAppId(targetAppId);
        xmPushActionNotification.setPackageName(headsUpNotification.pkgName);
        HashMap map = new HashMap();
        xmPushActionNotification.extra = map;
        map.put(HeadsUpConstants.EXTRA_HEADSUP_REPORT_TYPE, String.valueOf(i2));
        int buttonPlace = CustomNotificationConstants.getButtonPlace(headsUpNotification.clickStyleButtonIndex);
        if (buttonPlace != -1) {
            xmPushActionNotification.extra.put("notification_click_button", String.valueOf(buttonPlace));
        }
        xmPushActionNotification.extra.put("message_id", msgId);
        xmPushActionNotification.extra.put(MIPushNotificationHelper.NOTIFICATION_EXTRA_MSG_BUSI_TYPE, NotificationUtils.getMsgBusinessType(headsUpNotification.f1747n));
        HeadsUpUtils.cpNotificationToMap(headsUpNotification.f1747n, xmPushActionNotification.extra, "mipush_custom_extra");
        return xmPushActionNotification;
    }

    public static void reportCancel(final HeadsUpNotification headsUpNotification, final int i2) {
        HeadsUpLog.d("reportCancel:" + headsUpNotification);
        if (headsUpNotification != null) {
            QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.headsup.HeadsUpReport.2
                @Override // java.lang.Runnable
                public void run() {
                    XmPushActionNotification xmPushActionNotificationBuildReportData = HeadsUpReport.buildReportData(headsUpNotification, 2);
                    xmPushActionNotificationBuildReportData.extra.put(HeadsUpConstants.EXTRA_HEADSUP_CANCEL_REASON, String.valueOf(i2));
                    HeadsUpReport.reportNotification(xmPushActionNotificationBuildReportData);
                }
            });
        }
    }

    public static void reportNotification(XmPushActionNotification xmPushActionNotification) {
        HeadsUpLog.d("report notification:" + xmPushActionNotification);
        ChannelPushReportAgent.report(xmPushActionNotification);
    }

    public static void reportShow(final HeadsUpNotification headsUpNotification) {
        HeadsUpLog.d("reportShow:" + headsUpNotification);
        if (headsUpNotification != null) {
            QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.headsup.HeadsUpReport.1
                @Override // java.lang.Runnable
                public void run() {
                    HeadsUpReport.reportNotification(HeadsUpReport.buildReportData(headsUpNotification, 1));
                }
            });
        }
    }
}
