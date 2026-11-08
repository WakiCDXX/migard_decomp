package com.miui.powerkeeper.statemachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.miui.powerkeeper.event.EventsAggregator;

/* JADX INFO: loaded from: classes.dex */
public class FKVideoReceiver extends BroadcastReceiver {
    private static final int FPS_60 = 60;
    private static final int QUALITY_2160P = 8;
    private static final int QUALITY_8KUHD = 3001;
    private static final String RECORD_END = "record_end";
    private static final String RECORD_START = "record_start";
    private static final String TAG = "FKVideoReceiver";
    private EventsAggregator mEventsAggregator;

    public FKVideoReceiver(Context context, EventsAggregator eventsAggregator) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECORD_START);
        intentFilter.addAction(RECORD_END);
        context.registerReceiver(this, intentFilter, 2);
        this.mEventsAggregator = eventsAggregator;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        int intExtra = intent.getIntExtra("quality", -1);
        int intExtra2 = intent.getIntExtra("fps", -1);
        String action = intent.getAction();
        if (!RECORD_START.equals(action)) {
            if (RECORD_END.equals(action)) {
                PowerStateMachineProxy.getInstance().sendEvent(4017, null);
                this.mEventsAggregator.notifyForVideoRecordState(3);
                return;
            }
            return;
        }
        this.mEventsAggregator.notifyForVideoRecordState(4);
        if (intExtra != 8) {
            if (intExtra == 3001) {
                PowerStateMachineProxy.getInstance().sendEvent(4018, null);
                this.mEventsAggregator.notifyForVideoRecordState(2);
                return;
            }
            return;
        }
        if (intExtra2 == 60) {
            PowerStateMachineProxy.getInstance().sendEvent(4018, null);
            this.mEventsAggregator.notifyForVideoRecordState(2);
        } else {
            PowerStateMachineProxy.getInstance().sendEvent(4016, null);
            this.mEventsAggregator.notifyForVideoRecordState(1);
        }
    }
}
