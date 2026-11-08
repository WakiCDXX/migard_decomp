package com.xiaomi.push.service.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.xiaomi.channel.commonutils.android.ReceiverHelper;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.mipush.sdk.AppInfoHolder;
import com.xiaomi.mipush.sdk.AssemblePush;
import com.xiaomi.mipush.sdk.COSPushHelper;
import com.xiaomi.mipush.sdk.FTOSPushHelper;
import com.xiaomi.mipush.sdk.HWPushHelper;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.OperatePushHelper;
import com.xiaomi.mipush.sdk.PushServiceClient;
import com.xiaomi.mipush.sdk.RetryType;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.PushServiceConstants;
import com.xiaomi.push.service.ServiceClient;
import com.xiaomi.smack.util.TrafficUtils;

/* JADX INFO: loaded from: classes.dex */
public class NetworkStatusReceiver extends BroadcastReceiver {
    private static boolean isRegister = false;
    private boolean isXmlRegister = true;

    public NetworkStatusReceiver() {
    }

    public static boolean isRegister() {
        return isRegister;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyNetworkChanged(Context context) {
        if (!PushServiceClient.getInstance(context).shouldUseMIUIPush() && AppInfoHolder.getInstance(context).isRegistrationValid() && !AppInfoHolder.getInstance(context).invalidated()) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(context, PushConstants.PUSH_SERVICE_CLASS_NAME_JAR));
                intent.setAction(PushServiceConstants.ACTION_NETWORK_STATUS_CHANGED);
                ServiceClient.getInstance(context).startServiceSafely(intent);
            } catch (Exception e2) {
                MyLog.e(e2);
            }
        }
        TrafficUtils.notifyNetworkChanage(context);
        if (Network.hasNetwork(context) && PushServiceClient.getInstance(context).isProvisioned()) {
            PushServiceClient.getInstance(context).processRegisterTask();
        }
        if (Network.hasNetwork(context)) {
            if (OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(context).getSyncStatus(RetryType.DISABLE_PUSH))) {
                MiPushClient.disablePush(context);
            }
            if (OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(context).getSyncStatus(RetryType.ENABLE_PUSH))) {
                MiPushClient.enablePush(context);
            }
            OperatePushHelper operatePushHelper = OperatePushHelper.getInstance(context);
            RetryType retryType = RetryType.UPLOAD_HUAWEI_TOKEN;
            if (OperatePushHelper.SYNCING.equals(operatePushHelper.getSyncStatus(retryType))) {
                PushServiceClient.getInstance(context).sendAssemblePushTokenCommon(null, retryType, AssemblePush.ASSEMBLE_PUSH_HUAWEI, "net");
            }
            if (OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(context).getSyncStatus(RetryType.UPLOAD_FCM_TOKEN))) {
                PushServiceClient.getInstance(context).sendAssemblePushTokenCommon(null, retryType, AssemblePush.ASSEMBLE_PUSH_HUAWEI, "net");
            }
            OperatePushHelper operatePushHelper2 = OperatePushHelper.getInstance(context);
            RetryType retryType2 = RetryType.UPLOAD_COS_TOKEN;
            if (OperatePushHelper.SYNCING.equals(operatePushHelper2.getSyncStatus(retryType2))) {
                PushServiceClient.getInstance(context).sendAssemblePushTokenCommon(null, retryType2, AssemblePush.ASSEMBLE_PUSH_COS, "net");
            }
            OperatePushHelper operatePushHelper3 = OperatePushHelper.getInstance(context);
            RetryType retryType3 = RetryType.UPLOAD_FTOS_TOKEN;
            if (OperatePushHelper.SYNCING.equals(operatePushHelper3.getSyncStatus(retryType3))) {
                PushServiceClient.getInstance(context).sendAssemblePushTokenCommon(null, retryType3, AssemblePush.ASSEMBLE_PUSH_FTOS, "net");
            }
            if (HWPushHelper.needConnect() && HWPushHelper.shouldTryConnect(context)) {
                HWPushHelper.setConnectTime(context);
                HWPushHelper.registerHuaWeiAssemblePush(context);
            }
            COSPushHelper.doInNetworkChange(context);
            FTOSPushHelper.doInNetworkChange(context);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        if (this.isXmlRegister) {
            return;
        }
        Network.onNetworkStatusReceive();
        ReceiverHelper.getTaskHandler().post(new Runnable() { // from class: com.xiaomi.push.service.receivers.NetworkStatusReceiver.1
            @Override // java.lang.Runnable
            public void run() {
                NetworkStatusReceiver.this.notifyNetworkChanged(context);
            }
        });
    }

    public NetworkStatusReceiver(Object obj) {
        isRegister = true;
    }
}
