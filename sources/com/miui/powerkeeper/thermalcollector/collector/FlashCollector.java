package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class FlashCollector extends BaseCollector {
    private static final String KEY_FLASH_SCENARIO_ID = "scenario_id";
    private static final String KEY_FLASH_TEMP = "flash_temp";

    public FlashCollector() {
        super(CollectorConfig.COLLECTOR_FLASH);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        JSONObject json = getJson();
        try {
            json.put(CollectorConfig.KEY_PROCESS_TRIGGER_ID, "2");
            json.put(KEY_FLASH_SCENARIO_ID, ScenarioCollectorUtils.getInstance().getFlashScenarioId());
            json.put(KEY_FLASH_TEMP, ScenarioCollectorUtils.getInstance().getFlashTemp());
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
