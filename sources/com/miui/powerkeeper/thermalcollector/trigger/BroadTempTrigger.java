package com.miui.powerkeeper.thermalcollector.trigger;

import android.os.Message;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.Utils;

/* JADX INFO: loaded from: classes.dex */
public class BroadTempTrigger extends BaseTrigger {
    private static final long CYCLE = 30000;
    private static final int MESSAGE_TIMING_TRIGGER = 0;

    private void onTimingTrigger() {
        Message messageObtainMessage = this.mHandler.obtainMessage(0);
        String fileString = FileUtil.getFileString("/sys/class/thermal/thermal_message/board_sensor_temp");
        if (Utils.isNumber(fileString)) {
            messageObtainMessage.arg1 = Integer.parseInt(fileString);
            handleRuleMessage(messageObtainMessage);
        }
        this.mHandler.sendMessageDelayed(messageObtainMessage, 30000L);
    }

    private void startFileObserver() {
        this.mHandler.obtainMessage(0).sendToTarget();
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.BaseTrigger
    protected void onStateChanged(Message message) {
        if (message.what == -37) {
            handleRuleMessage(message);
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public void startWork() {
        PowerKeeperManager.getInstance().getEventsAggregator().registerForBoardTemperature(this.mHandler);
    }

    @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
    public void stopWork() {
        this.mHandler.removeMessages(-37);
    }
}
