package com.miui.powerkeeper.thermalcollector.trigger;

import android.os.Message;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;

/* JADX INFO: loaded from: classes.dex */
public class BatteryTrigger extends BaseTrigger {
    private final PowerKeeperInterface.m mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.thermalcollector.trigger.BatteryTrigger.1
        @Override // com.miui.powerkeeper.PowerKeeperInterface.m
        public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
            Message messageObtainMessage = BatteryTrigger.this.mHandler.obtainMessage();
            messageObtainMessage.obj = new int[]{i2, i3, i4, i5, i6, i7};
            BatteryTrigger.this.handleRuleMessage(messageObtainMessage);
        }
    };

    private void registerListener() {
        PowerKeeperManager.getInstance().registerBatteryChangedListener(new PowerKeeperInterface.d(this.mHandler, this.mBatteryStatusReceiver));
    }

    private void unregisterListener() {
        PowerKeeperManager.getInstance().getEventsAggregator().unregisterBatteryLevelListener(this.mHandler);
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public void startWork() {
        registerListener();
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public void stopWork() {
        unregisterListener();
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.BaseTrigger
    protected void onStateChanged(Message message) {
    }
}
