package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ForegroundEndCollector extends BaseCollector {
    public ForegroundEndCollector() {
        super(CollectorConfig.COLLECTOR_FOREGROUND_END);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        JSONObject json = getJson();
        try {
            ScenarioCollectorUtils scenarioCollectorUtils = ScenarioCollectorUtils.getInstance();
            json.put(ScenarioCollectorUtils.KEY_BOOT_STATE, ScenarioCollectorUtils.getInstance().getFirstBootState());
            json.put("scenario_name", ScenarioCollectorUtils.getCurrentScenarioId());
            json.put("power_mode", ScenarioCollectorUtils.getCurrentPowerMode());
            json.put("casting", scenarioCollectorUtils.getCastingState());
            json.put(ScenarioCollectorUtils.KEY_CHARGER_PROTOCOL, scenarioCollectorUtils.getChargingProtocol());
            json.put(ScenarioCollectorUtils.KEY_FAST_CHARGER_MODE, ScenarioCollectorUtils.getFastChargeEnabled());
            json.put(ScenarioCollectorUtils.KEY_TORCH_STATE, ScenarioCollectorUtils.getTorchState());
            json.put(ScenarioCollectorUtils.KEY_VIDEO_TOOL_BOX_STATE, scenarioCollectorUtils.getVideoToolStyle());
            json.put(ScenarioCollectorUtils.KEY_MAX_BOARD_TEMP_TIMESTAMP, scenarioCollectorUtils.getMaxBoardTempTimeStamp());
            json.put(ScenarioCollectorUtils.KEY_MAX_BOARD_TEMP, scenarioCollectorUtils.getMaxBoardTemp());
            json.put(ScenarioCollectorUtils.KEY_MAX_FLASH_TEMP_TIMESTAMP, scenarioCollectorUtils.getMaxFlashTempTimeStamp());
            json.put(ScenarioCollectorUtils.KEY_MAX_FLASH_TEMP, scenarioCollectorUtils.getMaxFlashTemp());
            json.put(ScenarioCollectorUtils.KEY_POPUP_WINDOW_STATE, ScenarioCollectorUtils.getInstance().getPopUpWindowStatus());
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
