package com.xiaomi.channel.commonutils.misc;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import com.xiaomi.channel.commonutils.android.ReceiverHelper;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
public class MobileStatusUtils {
    private MobileStatusUtils() {
    }

    public static boolean isCharging(Context context) {
        Intent intentRegisterReceiver = null;
        try {
            intentRegisterReceiver = ReceiverHelper.registerReceiver(context, (BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), (String) null, (Handler) null);
        } catch (Exception unused) {
        }
        if (intentRegisterReceiver == null) {
            return false;
        }
        int intExtra = intentRegisterReceiver.getIntExtra("status", -1);
        return intExtra == 2 || intExtra == 5;
    }

    public static boolean isScreenLocked(Context context) {
        try {
            return ((KeyguardManager) context.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
        } catch (Exception e2) {
            MyLog.e(e2);
            return false;
        }
    }
}
