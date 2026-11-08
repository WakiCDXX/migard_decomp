package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ForegroundStartCollector extends BaseCollector {
    private long mCurrentTimeStamp;

    public ForegroundStartCollector() {
        super(CollectorConfig.COLLECTOR_FOREGROUND_START);
        this.mCurrentTimeStamp = 0L;
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        JSONObject json = getJson();
        try {
            json.put(ScenarioCollectorUtils.KEY_CHARGER_PROTOCOL, ScenarioCollectorUtils.getInstance().getChargingProtocol());
            json.put(ScenarioCollectorUtils.KEY_FAST_CHARGER_MODE, ScenarioCollectorUtils.getFastChargeEnabled());
            json.put(ScenarioCollectorUtils.KEY_TORCH_STATE, ScenarioCollectorUtils.getTorchState());
            json.put(ScenarioCollectorUtils.KEY_VIDEO_TOOL_BOX_STATE, ScenarioCollectorUtils.getInstance().getVideoToolStyle());
            json.put("scenario_name", ScenarioCollectorUtils.getCurrentScenarioId());
            json.put(ScenarioCollectorUtils.KEY_HAND_STATE, ScenarioCollectorUtils.getInstance().getHandState());
            json.put(ScenarioCollectorUtils.KEY_CHARGER_TEMP, ScenarioCollectorUtils.getInstance().getChargerTemp());
            json.put(ScenarioCollectorUtils.KEY_HDR_STATE, ScenarioCollectorUtils.getInstance().getHDRState());
            ScenarioCollectorUtils.getInstance().resetTrafficData();
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
