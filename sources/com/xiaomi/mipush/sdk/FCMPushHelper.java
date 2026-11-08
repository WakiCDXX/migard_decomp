package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.FCMChannelHelper;
import com.xiaomi.push.service.MIPushEventProcessor;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionSendMessage;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class FCMPushHelper {
    private static final long MIN_XMSF_VC = 50002000;
    private static final String MIN_XMSF_VC_KEY = "is_xmsf_sup_decrypt";

    private static void broadcastFCMPassThoughNotificationCome(Context context, XmPushActionContainer xmPushActionContainer) {
        try {
            MiPushMessage miPushMessageGenerateMessage = PushMessageHelper.generateMessage((XmPushActionSendMessage) PushContainerHelper.getResponseMessageBodyFromContainer(context, xmPushActionContainer), xmPushActionContainer.getMetaInfo(), false);
            PushMessageReceiver miPushReceiver = AssemblePushHelper.getMiPushReceiver(context);
            if (miPushReceiver != null) {
                miPushReceiver.onNotificationMessageArrived(context, miPushMessageGenerateMessage);
            }
        } catch (Throwable th) {
            MyLog.e("fcm broadcast notification come error ", th);
        }
    }

    public static void clearToken(Context context) {
        AssemblePushHelper.clearToken(context, AssemblePush.ASSEMBLE_PUSH_FCM);
    }

    private static Map<String, String> constructFCMAckMessage(Context context) {
        HashMap map = new HashMap();
        map.put("actionType", String.valueOf(ActionType.AckMessage.getValue()));
        map.put("deviceStatus", String.valueOf((int) XmPushThriftSerializeUtils.getDeviceStatus(context, context.getPackageName())));
        map.put(PushConstants.MESSAGE_ACK_TIME, Long.toString(System.currentTimeMillis()));
        return map;
    }

    public static void convertMessage(Intent intent) {
        AssemblePushHelper.convertMessage(intent);
    }

    public static boolean isFCMSwitchOpen(Context context) {
        return AssemblePushHelper.isOpenAssemblePushOnlineSwitch(context, AssemblePush.ASSEMBLE_PUSH_FCM) && MiPushClient.getOpenFCMPush(context);
    }

    private static boolean isXmsfCanDencryptFcmMessage(Context context) {
        return context.getSharedPreferences("mipush_extra", 0).getBoolean(MIN_XMSF_VC_KEY, false);
    }

    private static boolean isXmsfSupportFCMNotification(Context context) {
        return ((long) MIUIUtils.getXmsfVersionCode(context)) >= MIN_XMSF_VC && isXmsfCanDencryptFcmMessage(context);
    }

    public static void notifyFCMNotificationCome(Context context, Map<String, String> map) {
        PushMessageReceiver miPushReceiver;
        String str = map.get("pushMsg");
        if (TextUtils.isEmpty(str) || (miPushReceiver = AssemblePushHelper.getMiPushReceiver(context)) == null) {
            return;
        }
        miPushReceiver.onNotificationMessageArrived(context, AssemblePushHelper.parseMiPushMessage(str));
    }

    public static Map<String, String> notifyFCMPassThoughMessageCome(Context context, Map<String, String> map) {
        PushMessageReceiver miPushReceiver;
        String str = map.get("pushMsg");
        if (!TextUtils.isEmpty(str) && (miPushReceiver = AssemblePushHelper.getMiPushReceiver(context)) != null) {
            miPushReceiver.onReceivePassThroughMessage(context, AssemblePushHelper.parseMiPushMessage(str));
        }
        String str2 = map.get("mipushContainer");
        if (TextUtils.isEmpty(str2)) {
            return new HashMap();
        }
        try {
            byte[] bArrDecode = Base64.decode(str2, 2);
            broadcastFCMPassThoughNotificationCome(context, MIPushEventProcessor.buildContainer(bArrDecode));
            notifyPushMessage(context, bArrDecode);
        } catch (Throwable th) {
            MyLog.e("fcm notify notification error ", th);
        }
        return constructFCMAckMessage(context);
    }

    private static void notifyPushMessage(Context context, byte[] bArr) {
        boolean zShouldUseMIUIPush = PushServiceClient.getInstance(context).shouldUseMIUIPush();
        boolean z2 = true;
        boolean z3 = !PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName());
        boolean zIsXmsfSupportFCMNotification = isXmsfSupportFCMNotification(context);
        boolean z4 = false;
        if (zShouldUseMIUIPush && z3 && zIsXmsfSupportFCMNotification) {
            bArr = FCMChannelHelper.encryptContainer(bArr, AppInfoHolder.getInstance(context).getRegSecret());
            if (bArr == null) {
                MyLog.w("fcm message encrypt failed");
            } else {
                String strEncodeToString = Base64.encodeToString(bArr, 2);
                if (TextUtils.isEmpty(strEncodeToString)) {
                    MyLog.w("fcm message buf base64 encode failed");
                    z2 = false;
                } else {
                    Intent intent = new Intent(PushConstants.ACTION_HANDLE_FCM_MSG);
                    intent.setPackage(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
                    intent.setClassName(PushConstants.PUSH_SERVICE_PACKAGE_NAME, PushConstants.PUSH_SERVICE_CLASS_NAME_JAR);
                    intent.putExtra(PushConstants.EXTRA_FCM_CONTAINER_BUFFER_BASE64, strEncodeToString);
                    intent.putExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE, context.getPackageName());
                    context.startService(intent);
                    MyLog.w("fcm message reroute to xmsf");
                }
                z4 = z2;
            }
        } else {
            MyLog.w(String.format("xmsf can not receive fcm msg - shouldUseMIUIPush=%s;isNotXmsf=%s;xmsfSupport=%s", Boolean.valueOf(zShouldUseMIUIPush), Boolean.valueOf(z3), Boolean.valueOf(zIsXmsfSupportFCMNotification)));
        }
        if (z4) {
            return;
        }
        MyLog.i("fcm message post local");
        MIPushNotificationHelper.notifyPushMessage(context, MIPushEventProcessor.buildContainer(bArr), bArr);
    }

    public static void persistIfXmsfSupDecrypt(Context context) {
        context.getSharedPreferences("mipush_extra", 0).edit().putBoolean(MIN_XMSF_VC_KEY, ((long) MIUIUtils.getXmsfVersionCode(context)) >= MIN_XMSF_VC).apply();
    }

    public static void reportFCMMessageDelete() {
        MiTinyDataClient.upload(AssemblePushHelper.getSPErrorKey(AssemblePush.ASSEMBLE_PUSH_FCM), "fcm", 1L, "some fcm messages was deleted ");
    }

    public static void uploadToken(Context context, String str) {
        AssemblePushHelper.uploadToken(context, AssemblePush.ASSEMBLE_PUSH_FCM, str);
    }
}
