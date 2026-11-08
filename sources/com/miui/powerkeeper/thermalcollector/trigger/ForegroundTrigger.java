package com.miui.powerkeeper.thermalcollector.trigger;

import android.annotation.NonNull;
import android.os.Message;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;

/* JADX INFO: loaded from: classes.dex */
public class ForegroundTrigger extends BaseTrigger {
    private final int MSG_DELAY_HANDLER_WORK = 0;

    private void registerListener() {
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        eventsAggregator.registerForForegroundInfo(this.mHandler);
        eventsAggregator.registerForScreenOff(this.mHandler, null);
        eventsAggregator.registerForScreenOn(this.mHandler, null);
    }

    private void unregisterListener() {
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        eventsAggregator.unregisterForForegroundInfo(this.mHandler);
        eventsAggregator.unregisterForScreenOff(this.mHandler);
        eventsAggregator.unregisterForScreenOn(this.mHandler);
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.BaseTrigger
    protected void onStateChanged(@NonNull Message message) {
        int i2 = message.what;
        if (i2 == 0) {
            Message messageObtain = Message.obtain(message);
            messageObtain.what = messageObtain.arg1;
            handleRuleMessage(messageObtain);
        } else {
            Message messageObtain2 = Message.obtain(message);
            messageObtain2.what = 0;
            messageObtain2.arg1 = i2;
            this.mHandler.sendMessageDelayed(messageObtain2, 3000L);
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public void startWork() {
        registerListener();
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public void stopWork() {
        unregisterListener();
        this.mHandler.removeMessages(-9);
        this.mHandler.removeMessages(-5);
        this.mHandler.removeMessages(-4);
    }
}
