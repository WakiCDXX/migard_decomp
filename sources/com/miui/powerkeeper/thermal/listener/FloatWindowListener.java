package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;

/* JADX INFO: loaded from: classes.dex */
public class FloatWindowListener extends ElementListener {
    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        int i5;
        long jLongValue = ((Long) obj).longValue();
        if ((4 & jLongValue) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WECHAT & jLongValue) == 0) {
            i5 = ((32 & jLongValue) == 0 && (jLongValue & ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_DINGTALK) == 0) ? 99 : 2;
        } else {
            i5 = 1;
        }
        modifyCurrentState(i5);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        PowerKeeperManager.getInstance().getEventsAggregator().registerSmartPowerScenarioIdChanged(this.mElementHandler);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 99;
    }
}
