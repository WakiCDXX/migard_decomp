package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class HeadCollector extends BaseCollector {
    private static final String KEY_HARDWARE = "hardware_id";
    private static final String KEY_VERSION = "system_version";
    private static final String TAG = "CollectorHead";

    public HeadCollector() {
        super("CollectorHead");
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    public void collect() {
        JSONObject json = getJson();
        try {
            String dataCollectorVersion = CollectorManager.getDataCollectorVersion();
            if (dataCollectorVersion.isEmpty()) {
                dataCollectorVersion = String.valueOf(1.0f);
            }
            json.put(KEY_VERSION, dataCollectorVersion);
            json.put(KEY_HARDWARE, ThermalInfoUtils.getHardwareId());
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
