package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class BoardCollector extends BaseCollector {
    private static final String TAG = "CollectorBoard";

    public BoardCollector() {
        super("CollectorBoard");
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        JSONObject json = getJson();
        try {
            json.put(CollectorConfig.KEY_PROCESS_TRIGGER_ID, CollectorManager.getInstance().getBoardProcessTriggerId());
            json.put(ScenarioCollectorUtils.KEY_CHARGER_PROTOCOL, ScenarioCollectorUtils.getInstance().getChargingProtocol());
            json.put(ScenarioCollectorUtils.KEY_TORCH_STATE, ScenarioCollectorUtils.getTorchState());
            json.put(ScenarioCollectorUtils.KEY_VIDEO_TOOL_BOX_STATE, ScenarioCollectorUtils.getInstance().getVideoToolStyle());
            json.put(ScenarioCollectorUtils.KEY_FAST_CHARGER_MODE, ScenarioCollectorUtils.getFastChargeEnabled());
            json.put(ScenarioCollectorUtils.KEY_HAND_STATE, ScenarioCollectorUtils.getInstance().getHandState());
            json.put(ScenarioCollectorUtils.KEY_CHARGER_TEMP, ScenarioCollectorUtils.getInstance().getChargerTemp());
            json.put(ScenarioCollectorUtils.KEY_HDR_STATE, ScenarioCollectorUtils.getInstance().getHDRState());
            json.put(ScenarioCollectorUtils.KEY_NET_DOWN_SPEED, ScenarioCollectorUtils.getInstance().getDownloadSpeed());
            json.put(ScenarioCollectorUtils.KEY_NET_UP_SPEED, ScenarioCollectorUtils.getInstance().getUploadSpeed());
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
