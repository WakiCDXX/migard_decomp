package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;

/* JADX INFO: loaded from: classes.dex */
public class CameraStateCollector extends BaseCollector {
    public CameraStateCollector() {
        super(CollectorConfig.COLLECTOR_CAMERA_STATE);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        try {
            getJson().put("camera_state", ScenarioCollectorUtils.getInstance().getCameraState());
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
