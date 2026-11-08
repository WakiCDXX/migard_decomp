package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;

/* JADX INFO: loaded from: classes.dex */
public class PopUpWindowCollector extends BaseCollector {
    public PopUpWindowCollector() {
        super(CollectorConfig.COLLECTOR_POPUP_WINDOW);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        try {
            getJson().put(ScenarioCollectorUtils.KEY_POPUP_WINDOW_STATE, ScenarioCollectorUtils.getInstance().getPopUpWindowStatus());
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
