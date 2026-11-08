package com.miui.powerkeeper.thermal.processor;

import android.annotation.NonNull;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;
import com.miui.powerkeeper.utils.ShellUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class CallSwitchProcessor extends CSwitchProcessor {
    private static final String KEY_CALL_GAME = "callGame";
    private final Set<Integer> mCallScenarios = new HashSet(1);

    @Override // com.miui.powerkeeper.thermal.processor.SwitchProcessor, com.miui.powerkeeper.thermal.processor.ThermalProcessor
    public void init(Map<String, Set<Integer>> map) {
        this.mCallScenarios.addAll(map.get(KEY_CALL_GAME));
        map.remove(KEY_CALL_GAME);
        super.init(map);
    }

    @Override // com.miui.powerkeeper.thermal.processor.CSwitchProcessor, com.miui.powerkeeper.thermal.processor.SwitchProcessor
    protected int mapToThermalId(int i2) {
        int virtualBoardSensorTemp;
        if (!this.mCallScenarios.contains(Integer.valueOf(i2))) {
            return super.mapToThermalId(i2);
        }
        if (CollectorManager.getInstance().isCompoundScenarioCollector() && ScenarioCollectorUtils.getInstance().isCompoundScenarioId(i2) && (virtualBoardSensorTemp = ThermalManager.ThermalTempInfoHelper.getVirtualBoardSensorTemp()) != -1) {
            PowerKeeperManager.getInstance().getEventsAggregator().notifyForBoardTemperature(virtualBoardSensorTemp);
        }
        return ScenarioConfig.SCONFIG_MGAME;
    }

    @Override // com.miui.powerkeeper.thermal.processor.CSwitchProcessor, com.miui.powerkeeper.thermal.processor.SwitchProcessor
    @NonNull
    public String toString() {
        return "--- CallSwitchProcessor CallScenarios: " + this.mCallScenarios.toString() + ShellUtils.COMMAND_LINE_END + super.toString();
    }
}
