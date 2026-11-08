package com.xiaomi.push.service.timers;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
class HybridTimer extends AlarmManagerTimer {
    private static int pingInterval = 3600000;

    public HybridTimer(Context context) {
        super(context);
    }

    long getPingInteval() {
        return pingInterval;
    }
}
