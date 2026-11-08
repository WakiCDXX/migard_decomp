package com.miui.powerkeeper.utils;

import com.miui.powerkeeper.event.EventsAggregator;
import miui.smartpower.IScenarioCallback;
import miui.smartpower.SmartPowerManager;

/* JADX INFO: loaded from: classes.dex */
public class SmartScenarioObserver {
    private final EventsAggregator mAggregator;
    private final IScenarioCallback.Stub mScenarioCallback;

    public SmartScenarioObserver(EventsAggregator eventsAggregator) {
        IScenarioCallback.Stub stub = new IScenarioCallback.Stub() { // from class: com.miui.powerkeeper.utils.SmartScenarioObserver.1
            public void onCurrentScenarioChanged(long j2, long j3) {
                SmartScenarioObserver.this.mAggregator.notifySmartPowerScenarioIdChanged(Long.valueOf(j3));
            }
        };
        this.mScenarioCallback = stub;
        this.mAggregator = eventsAggregator;
        SmartPowerManager.getService().registThermalScenarioCallback(stub);
    }
}
