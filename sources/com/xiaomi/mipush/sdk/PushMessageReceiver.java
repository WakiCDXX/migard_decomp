package com.xiaomi.mipush.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.clientReport.PushClientReportManager;
import com.xiaomi.push.service.clientReport.ReportConstants;

/* JADX INFO: loaded from: classes.dex */
public abstract class PushMessageReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        if (intent != null) {
            MyLog.persist("[CRcv] receive a msg broadcast: " + intent.getAction());
        }
        MessageHandleService.addJob(context.getApplicationContext(), new MessageHandleService.MessageHandleJob(intent, this));
        try {
            int intExtra = intent.getIntExtra(ReportConstants.EVENT_MESSAGE_TYPE, -1);
            if (intExtra == 2000) {
                PushClientReportManager.getInstance(context.getApplicationContext()).reportEvent(context.getPackageName(), intent, 2003, (String) null);
            } else if (intExtra == 6000) {
                PushClientReportManager.getInstance(context.getApplicationContext()).reportEvent(context.getPackageName(), intent, ReportConstants.REGISTER_TYPE_APP_RECEIVE_BROADCAST, (String) null);
            }
        } catch (Exception e2) {
            MyLog.e("meet error in PushMessageReceiver. " + e2);
        }
    }

    public void onCommandResult(Context context, MiPushCommandMessage miPushCommandMessage) {
    }

    public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {
    }

    public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {
    }

    @Deprecated
    public void onReceiveMessage(Context context, MiPushMessage miPushMessage) {
    }

    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
    }

    public void onReceiveRegisterResult(Context context, MiPushCommandMessage miPushCommandMessage) {
    }

    public void onRequirePermissions(Context context, String[] strArr) {
    }
}
