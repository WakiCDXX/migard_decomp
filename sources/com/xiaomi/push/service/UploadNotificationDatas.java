package com.xiaomi.push.service;

import android.app.Notification;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.push.service.notification.StatusBarNotificationWrapper;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class UploadNotificationDatas implements XMPushService.PingCallBack {
    private static final String NOTIFICATION_FROM = "notification_from";
    private static Context sContext;
    private static final String TAG = "UNDatas";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final Map<Integer, Map<String, List<String>>> sAllRemovedNotifications = new HashMap();

    public UploadNotificationDatas(Context context) {
        sContext = context;
    }

    private static boolean disableReport(Notification notification, int i2) {
        int i3 = ToolUtils.parseInt((String) NotificationUtils.getValue(notification, MIPushNotificationHelper.NOTIFICATION_DISABLE_FLAGS, ""), 0);
        return i3 > 0 && (i3 & i2) == i2;
    }

    private static XmPushActionNotification generateReportNotification(String str, String str2, String str3, String str4) {
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        if (str3 != null) {
            xmPushActionNotification.setType(str3);
        }
        if (str != null) {
            xmPushActionNotification.setAppId(str);
        }
        if (str2 != null) {
            xmPushActionNotification.setId(str2);
        }
        if (str4 != null) {
            xmPushActionNotification.setPackageName(str4);
        }
        xmPushActionNotification.setRequireAck(false);
        return xmPushActionNotification;
    }

    public static void onNotificationPosted(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper, int i2) {
        if (statusBarNotificationWrapper == null || statusBarNotificationWrapper.getNotification() == null || !NotificationUtils.isNotificationFromXmsf(context, statusBarNotificationWrapper)) {
            return;
        }
        Notification notification = statusBarNotificationWrapper.getNotification();
        String msgId = NotificationUtils.getMsgId(notification);
        if (disableReport(notification, 1)) {
            MyLog.i("UNDatas disable report: 1 " + msgId);
            return;
        }
        String targetAppId = NotificationUtils.getTargetAppId(notification);
        String targetPackage = NotificationUtils.getTargetPackage(notification);
        if (TextUtils.isEmpty(msgId) || TextUtils.isEmpty(targetAppId)) {
            return;
        }
        MyLog.i("UNDatas message arrived statusbar : " + msgId);
        XmPushActionNotification xmPushActionNotificationGenerateReportNotification = generateReportNotification(targetAppId, msgId, NotificationType.NotificationArrived.value, targetPackage);
        if (xmPushActionNotificationGenerateReportNotification.extra == null) {
            xmPushActionNotificationGenerateReportNotification.extra = new HashMap();
        }
        xmPushActionNotificationGenerateReportNotification.extra.put(NOTIFICATION_FROM, String.valueOf(i2));
        uploadNotificationMessage(context, xmPushActionNotificationGenerateReportNotification);
    }

    public static void onNotificationRemoved(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper, int i2) {
        Notification notification;
        if (statusBarNotificationWrapper == null || !NotificationUtils.isNotificationFromXmsf(context, statusBarNotificationWrapper) || (notification = statusBarNotificationWrapper.getNotification()) == null) {
            return;
        }
        int i3 = i2 == 1 ? 2 : 4;
        String msgId = NotificationUtils.getMsgId(notification);
        MyLog.persist(TAG, "onNotificationRemoved: msgId=" + msgId);
        if (disableReport(notification, i3)) {
            MyLog.i("UNDatas disable report: " + i3 + " " + msgId);
            return;
        }
        String targetAppId = NotificationUtils.getTargetAppId(notification);
        String targetPackage = NotificationUtils.getTargetPackage(notification);
        if (TextUtils.isEmpty(msgId) || TextUtils.isEmpty(targetAppId)) {
            return;
        }
        if (i2 != 1) {
            switch (i2) {
                case PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_COLORFUL /* -1004 */:
                case PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_3 /* -1003 */:
                case PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_2 /* -1002 */:
                case PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_1 /* -1001 */:
                    break;
                default:
                    Map<Integer, Map<String, List<String>>> map = sAllRemovedNotifications;
                    synchronized (map) {
                        try {
                            Map<String, List<String>> map2 = map.get(Integer.valueOf(i2));
                            if (map2 == null) {
                                map2 = new HashMap<>();
                                map.put(Integer.valueOf(i2), map2);
                            }
                            List<String> arrayList = map2.get(targetAppId);
                            if (arrayList == null) {
                                arrayList = new ArrayList<>();
                                map2.put(targetAppId, arrayList);
                            }
                            arrayList.add(msgId);
                            MyLog.i("UNDatas removed statusbar message : " + msgId + " reason : " + i2);
                        } catch (Throwable th) {
                            throw th;
                        }
                        break;
                    }
                    return;
            }
        }
        XmPushActionNotification xmPushActionNotificationGenerateReportNotification = generateReportNotification(targetAppId, msgId, NotificationType.NotificationClicked.value, targetPackage);
        xmPushActionNotificationGenerateReportNotification.putToExtra("removed_reason", String.valueOf(i2));
        MyLog.i("UNDatas click statusbar message : " + msgId + " reason : " + i2);
        uploadNotificationMessage(context, xmPushActionNotificationGenerateReportNotification);
    }

    private static void processAllUpLoadMessagesLocked() {
        HashMap map = new HashMap();
        map.putAll(sAllRemovedNotifications);
        if (map.size() > 0) {
            for (Integer num : map.keySet()) {
                Map map2 = (Map) map.get(num);
                if (map2 != null && map2.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (String str : map2.keySet()) {
                        sb.append(str);
                        sb.append(":");
                        List list = (List) map2.get(str);
                        if (!ToolUtils.isEmptyList(list)) {
                            for (int i2 = 0; i2 < list.size(); i2++) {
                                if (i2 != 0) {
                                    sb.append(",");
                                }
                                sb.append((String) list.get(i2));
                            }
                        }
                        sb.append(";");
                    }
                    XmPushActionNotification xmPushActionNotificationGenerateReportNotification = generateReportNotification(null, PacketHelper.generatePacketID(), NotificationType.NotificationRemoved.value, null);
                    xmPushActionNotificationGenerateReportNotification.putToExtra("removed_reason", String.valueOf(num));
                    xmPushActionNotificationGenerateReportNotification.putToExtra(PushConstants.KEY_DELETE_ALL, sb.toString());
                    MyLog.i("UNDatas upload all removed messages reason: " + num + " allIds: " + sb.toString());
                    uploadNotificationMessage(sContext, xmPushActionNotificationGenerateReportNotification);
                }
                sAllRemovedNotifications.remove(num);
            }
        }
    }

    private static void uploadNotificationMessage(Context context, final XmPushActionNotification xmPushActionNotification) {
        if (DEBUG) {
            MyLog.i("UNDatas upload message notification:" + xmPushActionNotification);
        }
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.service.UploadNotificationDatas.1
            @Override // java.lang.Runnable
            public void run() {
                byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(MIPushHelper.generateRequestContainer(xmPushActionNotification.getPackageName(), xmPushActionNotification.getAppId(), xmPushActionNotification, ActionType.Notification));
                if (UploadNotificationDatas.sContext instanceof XMPushService) {
                    ((XMPushService) UploadNotificationDatas.sContext).sendMessage(xmPushActionNotification.getPackageName(), bArrConvertThriftObjectToBytes, true);
                } else {
                    MyLog.w("UNDatas UploadNotificationDatas failed because not xmsf");
                }
            }
        });
    }

    @Override // com.xiaomi.push.service.XMPushService.PingCallBack
    public void pingFollowUpAction() {
        Map<Integer, Map<String, List<String>>> map = sAllRemovedNotifications;
        if (map.size() > 0) {
            synchronized (map) {
                processAllUpLoadMessagesLocked();
            }
        }
    }
}
