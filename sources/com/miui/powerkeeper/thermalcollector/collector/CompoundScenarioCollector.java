package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CompoundScenarioCollector extends BaseCollector {
    public CompoundScenarioCollector() {
        super(CollectorConfig.COLLECTOR_COMPOUND_SCENARIO);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        JSONObject json = getJson();
        try {
            if (ScenarioCollectorUtils.getInstance().peekUploadScenarioId().isEmpty()) {
                json.put("scenario_name", ScenarioCollectorUtils.getCurrentScenarioId());
            } else {
                json.put("scenario_name", ScenarioCollectorUtils.getInstance().popUploadScenarioId());
            }
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
