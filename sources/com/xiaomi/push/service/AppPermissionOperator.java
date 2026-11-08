package com.xiaomi.push.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionAckNotification;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AppPermissionOperator {
    private static final long CHECK_PERMISSION_WAITING_TIME_MS = 1000;
    private static final long CODE_APP_UNINSTALL = 4;
    private static final long CODE_DATA_ERROR = 6;
    private static final long CODE_NO_NEED_SET = 2;
    private static final long CODE_NO_REPEAT_EXECUTE = 3;
    private static final long CODE_SET_FAILED = 1;
    private static final long CODE_SET_SUCCESS = 0;
    private static final long CODE_THROW_EXCEPTION = 5;
    private static final String PERMISSION_STATUS = "permissionStatus";
    private static final String PERMISSION_STATUS_CLOSE = "0";
    private static final String PERMISSION_STATUS_OPEN = "1";
    private static final String PERMISSION_TYPE = "permissionType";
    private static final String PERMISSION_TYPE_BADGE = "badge";
    private static final String PERMISSION_TYPE_ONGOING = "ongoing";
    private static final String SP_KEY_BD_C_SUFFIX = "_np_bd_setted_c";
    private static final String SP_KEY_BD_O_SUFFIX = "_np_bd_setted_o";
    private static final String SP_KEY_OO_C_SUFFIX = "_np_oo_setted_c";
    private static final String SP_KEY_OO_O_SUFFIX = "_np_oo_setted_o";
    private static final String SP_MIPUSH_EXTRA = "mipush_extra";
    private static final String TARGET_APPID = "targetAppId";
    private static final String TARGET_PACKAGE_NAME = "targetPackageName";

    private static class ExecuteResult {
        public long code;
        public String reason;

        public ExecuteResult(long j2, String str) {
            this.code = j2;
            this.reason = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized long changeSwitch(Context context, String str, String str2, String str3) {
        long j2;
        NotificationUtils.Third<String, String, String> third;
        String str4;
        try {
            if (AppInfoUtils.isPkgInstalled(context, str)) {
                boolean zEquals = "1".equals(str3);
                if (PERMISSION_TYPE_BADGE.equals(str2)) {
                    third = NotificationUtils.SETTING_BADGE;
                    if (zEquals) {
                        str4 = str + SP_KEY_BD_O_SUFFIX;
                    } else {
                        str4 = str + SP_KEY_BD_C_SUFFIX;
                    }
                } else if (PERMISSION_TYPE_ONGOING.equals(str2)) {
                    third = NotificationUtils.SETTING_ONGOING;
                    if (zEquals) {
                        str4 = str + SP_KEY_OO_O_SUFFIX;
                    } else {
                        str4 = str + SP_KEY_OO_C_SUFFIX;
                    }
                } else {
                    third = null;
                    str4 = null;
                }
                if (third == null || TextUtils.isEmpty(str4)) {
                    j2 = CODE_DATA_ERROR;
                } else {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("mipush_extra", 0);
                    if (sharedPreferences.getBoolean(str4, false)) {
                        j2 = CODE_NO_REPEAT_EXECUTE;
                    } else {
                        if (TextUtils.equals(str3, NotificationUtils.checkNotificationSetting(SystemUtils.getContext(), str, null, third) == 1 ? "1" : "0")) {
                            sharedPreferences.edit().putBoolean(str4, true).apply();
                            j2 = 2;
                        } else {
                            NotificationUtils.setNotificationSetting(context, str, null, third, zEquals);
                            SystemClock.sleep(1000L);
                            if (TextUtils.equals(str3, NotificationUtils.checkNotificationSetting(context, str, null, third) == 1 ? "1" : "0")) {
                                sharedPreferences.edit().putBoolean(str4, true).apply();
                                j2 = 0;
                            } else {
                                j2 = 1;
                            }
                        }
                    }
                }
            } else {
                j2 = 4;
            }
        } catch (Throwable th) {
            throw th;
        }
        return j2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isDataValid(String str, String str2, String str3, String str4, ExecuteResult executeResult) {
        if (TextUtils.isEmpty(str)) {
            executeResult.reason = "appId is null";
            return false;
        }
        if (TextUtils.isEmpty(str2)) {
            executeResult.reason = "packageName is null";
            return false;
        }
        if (!PERMISSION_TYPE_BADGE.equals(str3) && !PERMISSION_TYPE_ONGOING.equals(str3)) {
            executeResult.reason = "permissionType illegal : " + str3;
            return false;
        }
        if ("1".equals(str4) || "0".equals(str4)) {
            return true;
        }
        executeResult.reason = "permissionStatus illegal : " + str4;
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendMessage(XMPushService xMPushService, XmPushActionContainer xmPushActionContainer, XmPushActionNotification xmPushActionNotification, ExecuteResult executeResult) {
        XmPushActionAckNotification xmPushActionAckNotification = new XmPushActionAckNotification();
        xmPushActionAckNotification.setId(xmPushActionNotification.id);
        xmPushActionAckNotification.setAppId(xmPushActionNotification.appId);
        xmPushActionAckNotification.setPackageName(xmPushActionNotification.packageName);
        xmPushActionAckNotification.setTarget(xmPushActionNotification.target);
        xmPushActionAckNotification.setType(NotificationType.SettingAppNotificationPermissionACK.value);
        xmPushActionAckNotification.setErrorCode(executeResult.code);
        xmPushActionAckNotification.setExtra(xmPushActionNotification.extra);
        if (!TextUtils.isEmpty(executeResult.reason)) {
            xmPushActionAckNotification.setReason(executeResult.reason);
        }
        XmPushActionContainer xmPushActionContainerGenerateRequestContainer = MIPushHelper.generateRequestContainer(xmPushActionContainer.packageName, xmPushActionContainer.appid, xmPushActionAckNotification, ActionType.Notification);
        xmPushActionContainerGenerateRequestContainer.setIsRequest(false);
        xMPushService.sendMessage(PushConstants.PUSH_SERVICE_PACKAGE_NAME, XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionContainerGenerateRequestContainer), true);
    }

    public static void setAppNotificationPermission(final Context context, final XmPushActionContainer xmPushActionContainer, final XmPushActionNotification xmPushActionNotification) {
        if (context == null || !(context instanceof XMPushService) || xmPushActionContainer == null || xmPushActionNotification == null) {
            MyLog.w("containner and notification data must not be null");
        } else {
            new Thread(new Runnable() { // from class: com.xiaomi.push.service.AppPermissionOperator.1
                @Override // java.lang.Runnable
                public void run() {
                    StringBuilder sb;
                    ExecuteResult executeResult = new ExecuteResult(0L, null);
                    try {
                        Map<String, String> extra = xmPushActionNotification.getExtra();
                        if (extra != null) {
                            String str = extra.get(AppPermissionOperator.PERMISSION_TYPE);
                            String str2 = extra.get(AppPermissionOperator.PERMISSION_STATUS);
                            String str3 = extra.get(AppPermissionOperator.TARGET_PACKAGE_NAME);
                            if (AppPermissionOperator.isDataValid(extra.get(AppPermissionOperator.TARGET_APPID), str3, str, str2, executeResult)) {
                                executeResult.code = AppPermissionOperator.changeSwitch(context, str3, str, str2);
                            } else {
                                executeResult.code = AppPermissionOperator.CODE_DATA_ERROR;
                            }
                        } else {
                            executeResult.code = AppPermissionOperator.CODE_DATA_ERROR;
                            executeResult.reason = "notification extra is null";
                        }
                        try {
                            AppPermissionOperator.sendMessage((XMPushService) context, xmPushActionContainer, xmPushActionNotification, executeResult);
                        } catch (Throwable th) {
                            th = th;
                            sb = new StringBuilder();
                            sb.append("exception occurred in sending ack message :");
                            sb.append(th);
                            String string = sb.toString();
                        }
                    } catch (Throwable th2) {
                        try {
                            executeResult.code = AppPermissionOperator.CODE_THROW_EXCEPTION;
                            executeResult.reason = "set switch error :" + th2;
                            MyLog.e("exception occurred in changing switch :" + th2);
                            try {
                                AppPermissionOperator.sendMessage((XMPushService) context, xmPushActionContainer, xmPushActionNotification, executeResult);
                            } catch (Throwable th3) {
                                th = th3;
                                sb = new StringBuilder();
                                sb.append("exception occurred in sending ack message :");
                                sb.append(th);
                                String string2 = sb.toString();
                            }
                        } finally {
                            try {
                                AppPermissionOperator.sendMessage((XMPushService) context, xmPushActionContainer, xmPushActionNotification, executeResult);
                            } catch (Throwable th4) {
                                MyLog.e("exception occurred in sending ack message :" + th4);
                            }
                        }
                    }
                }
            }).start();
        }
    }
}
