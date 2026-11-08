package com.xiaomi.push.service;

import android.content.Context;
import com.xiaomi.channel.commonutils.android.MIUIUtils;

/* JADX INFO: loaded from: classes.dex */
public class PingPongCallbackAgent implements XMPushService.PingCallBack {
    private static OnPingPongListener sListener;

    public interface OnPingPongListener {
        void onPong();
    }

    public static void setOnPingPongListener(Context context, OnPingPongListener onPingPongListener) {
        if (MIUIUtils.isXMSF(context)) {
            sListener = onPingPongListener;
        }
    }

    @Override // com.xiaomi.push.service.XMPushService.PingCallBack
    public void pingFollowUpAction() {
        OnPingPongListener onPingPongListener = sListener;
        if (onPingPongListener != null) {
            onPingPongListener.onPong();
        }
    }
}
