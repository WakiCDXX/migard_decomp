package com.miui.powerkeeper.thermalcollector.trigger;

import android.os.Message;
import com.miui.powerkeeper.PowerKeeperManager;

/* JADX INFO: loaded from: classes.dex */
public class FlashTempTrigger extends BaseTrigger {
    @Override // com.miui.powerkeeper.thermalcollector.trigger.BaseTrigger
    protected void onStateChanged(Message message) {
        if (message.what == -39) {
            handleRuleMessage(message);
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public void startWork() {
        PowerKeeperManager.getInstance().getEventsAggregator().registerForFlashTemperature(this.mHandler);
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public void stopWork() {
        this.mHandler.removeMessages(-39);
    }
}
