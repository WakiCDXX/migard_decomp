package com.xiaomi.push.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.PushServiceConstants;
import com.xiaomi.push.service.ServiceClient;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.push.service.timers.Alarm;

/* JADX INFO: loaded from: classes.dex */
public class PingReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        MyLog.v(intent.getPackage() + " is the package name");
        if (XMPushService.isUseDynamicBroadcast()) {
            return;
        }
        if (!PushConstants.ACTION_PING_TIMER.equals(intent.getAction())) {
            MyLog.w("cancel the old ping timer");
            Alarm.stop();
        } else if (TextUtils.equals(context.getPackageName(), intent.getPackage())) {
            MyLog.v("Ping XMChannelService on timer");
            try {
                Intent intent2 = new Intent(context, (Class<?>) XMPushService.class);
                intent2.putExtra("time_stamp", System.currentTimeMillis());
                intent2.setAction(PushServiceConstants.ACTION_TIMER);
                ServiceClient.getInstance(context).startServiceSafely(intent2);
            } catch (Exception e2) {
                MyLog.e(e2);
            }
        }
    }
}
