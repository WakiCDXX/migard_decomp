package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;

/* JADX INFO: loaded from: classes.dex */
public class SettingCollector extends BaseCollector {
    private static final String KEY_SCREEN_PAPER_MODE = "screen_paper_mode";
    private static final String TAG = "CollectorSetting";

    public SettingCollector() {
        super("CollectorSetting");
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    public void collect() {
        try {
            getJson().put(KEY_SCREEN_PAPER_MODE, -1);
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
