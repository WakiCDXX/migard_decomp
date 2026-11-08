package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;

/* JADX INFO: loaded from: classes.dex */
public class SplitScreenListener extends ElementListener {
    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        modifyCurrentState((((Long) obj).longValue() & ScenarioConfig.INTERACTIVE_ACTION_TYPE_SPLIT) != 0 ? 98 : 99);
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
