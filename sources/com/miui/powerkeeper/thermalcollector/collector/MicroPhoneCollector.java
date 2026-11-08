package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;

/* JADX INFO: loaded from: classes.dex */
public class MicroPhoneCollector extends BaseCollector {
    public MicroPhoneCollector() {
        super(CollectorConfig.COLLECTOR_MICRO_PHONE_STATE);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        try {
            getJson().put(ScenarioCollectorUtils.KEY_MICRO_PHONE_STATE, ScenarioCollectorUtils.getInstance().getMicroPhoneState());
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
