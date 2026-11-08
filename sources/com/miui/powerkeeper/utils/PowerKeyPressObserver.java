package com.miui.powerkeeper.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.miui.powerkeeper.event.EventsAggregator;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeyPressObserver {
    private Context mContext;
    private EventsAggregator mEventsAggregator;
    private boolean mStarted = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.utils.PowerKeyPressObserver.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.KEYCODE_POWER_UP".equals(intent.getAction())) {
                PowerKeyPressObserver.this.mEventsAggregator.notifyForPowerKeyPressed();
            }
        }
    };

    public PowerKeyPressObserver(Context context, EventsAggregator eventsAggregator) {
        this.mContext = context;
        this.mEventsAggregator = eventsAggregator;
    }

    public void start() {
        if (this.mStarted) {
            return;
        }
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.KEYCODE_POWER_UP"), 2);
        this.mStarted = true;
    }

    public void stop() {
        if (this.mStarted) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mStarted = false;
        }
    }
}
