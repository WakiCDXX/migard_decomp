package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class AmbientCollector extends BaseCollector {
    public AmbientCollector() {
        super(CollectorConfig.COLLECTOR_AMBIENT);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        JSONObject json = getJson();
        try {
            json.put(CollectorConfig.KEY_PROCESS_TRIGGER_ID, "1");
            json.put(ThermalCollector.KEY_AMBIENT, ScenarioCollectorUtils.getInstance().getAmbientTemp());
            json.put(ThermalCollector.KEY_PLUG_TYPE, ScenarioCollectorUtils.getInstance().getChargingState());
            json.put(ThermalCollector.KEY_CAPACITY, ThermalInfoUtils.getCurrentCapacity());
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
