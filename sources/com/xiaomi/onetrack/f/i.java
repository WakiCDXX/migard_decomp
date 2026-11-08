package com.xiaomi.onetrack.f;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xiaomi.onetrack.util.p;

/* JADX INFO: loaded from: classes.dex */
final class i extends BroadcastReceiver {
    i() {
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.intent.action.SCREEN_OFF") || action.equals("android.intent.action.SCREEN_ON")) {
            p.a("OneTrackApp", action.equals("android.intent.action.SCREEN_ON") ? "屏幕亮" : "屏幕灭");
        }
    }
}
