package com.miui.powerkeeper.thermalcollector.event;

import android.annotation.NonNull;
import android.os.Message;
import com.miui.powerkeeper.thermal.resource.Couple;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.DTO.EventCloudDTO;
import com.miui.powerkeeper.thermalcollector.DTO.EventInfoDTO;
import com.miui.powerkeeper.thermalcollector.DTO.ThermalScenarioDTO;
import com.miui.powerkeeper.thermalcollector.collector.ThermalCollector;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import com.miui.powerkeeper.thermalcollector.trigger.IRule;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class Scenario extends BaseEvent {
    private static final String TAG = "thermal_scenario";
    private final IRule mRule = new TimeRule();
    private final Map<ThermalScenarioDTO.ThermalScenarioKey, ThermalScenarioDTO> mCache = new HashMap();
    private final ThermalScenarioDTO.ThermalScenarioKey mKey = new ThermalScenarioDTO.ThermalScenarioKey();
    private final ThermalScenarioDTO.ThermalScenarioValue mValue = new ThermalScenarioDTO.ThermalScenarioValue();

    private static class TimeRule implements IRule {
        boolean mTrigger;

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public String getName() {
            return CollectorConfig.RULE_THERMAL_SCENARIO_TIME;
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.IRule
        public boolean isTriggerRule(Message message) {
            boolean z2 = !this.mTrigger;
            this.mTrigger = z2;
            return z2;
        }

        private TimeRule() {
            this.mTrigger = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$settlement$0(ThermalScenarioDTO thermalScenarioDTO) {
        thermalScenarioDTO.settlement();
        commitData("thermal_scenario", this.mGson.toJson(thermalScenarioDTO));
    }

    /* JADX WARN: Code duplicated, block: B:13:0x002a  */
    private void parseCollectedData(Couple<String, JSONObject> couple) throws JSONException {
        byte b2;
        String k2 = couple.getK();
        int iHashCode = k2.hashCode();
        if (iHashCode != -709829014) {
            if (iHashCode == 1875707677 && k2.equals(CollectorConfig.COLLECTOR_SCENARIO)) {
                b2 = 0;
            } else {
                b2 = -1;
            }
        } else if (k2.equals("CollectorThermal")) {
            b2 = 1;
        } else {
            b2 = -1;
        }
        if (b2 == 0) {
            parseScenarioCollector(couple.getV());
        } else {
            if (b2 != 1) {
                return;
            }
            parseThermalCollector(couple.getV());
        }
    }

    private void parseScenarioCollector(JSONObject jSONObject) throws JSONException {
        this.mKey.setScreenState(jSONObject.getString("screen"));
        this.mKey.setThermalGroupState(jSONObject.getString("thermal_group"));
        this.mKey.setFloatWindowState(jSONObject.getString("float_window"));
        this.mKey.setCameraState(jSONObject.getString("camera"));
        this.mKey.setCastingState(jSONObject.getString("casting"));
        this.mKey.setDownloadState(jSONObject.getString("download"));
        this.mKey.setGPSState(jSONObject.getString("gps"));
        this.mKey.setSplit(jSONObject.getString("split"));
        this.mKey.setAudioState(jSONObject.getString("audio"));
        this.mValue.setForegroundName(jSONObject.getString("foreground_pkn"));
        this.mValue.setFloatWindowName(jSONObject.getString("float_pkn"));
    }

    private void parseThermalCollector(JSONObject jSONObject) throws JSONException {
        this.mKey.setPlugState(((Integer) jSONObject.get(ThermalCollector.KEY_PLUG_TYPE)).intValue());
        this.mValue.setTemp(((Integer) jSONObject.get(ThermalCollector.KEY_BROAD_TEMP)).intValue());
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.BaseEvent, com.miui.powerkeeper.thermalcollector.event.IEvent, com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        super.dump(printWriter);
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
    public EventInfoDTO parseParameters(EventCloudDTO.EventParameter eventParameter) {
        if (eventParameter.getEventName().equals("thermal_scenario")) {
            return new EventInfoDTO(eventParameter, new HashMap<String, IRule>(1) { // from class: com.miui.powerkeeper.thermalcollector.event.Scenario.1
                {
                    put(Scenario.this.mRule.getName(), Scenario.this.mRule);
                }
            });
        }
        return null;
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.BaseEvent, com.miui.powerkeeper.thermalcollector.event.IEvent
    public void settlement() {
        this.mCache.values().forEach(new Consumer() { // from class: com.miui.powerkeeper.thermalcollector.event.r
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f610a.lambda$settlement$0((ThermalScenarioDTO) obj);
            }
        });
        this.mCache.clear();
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
    public void updateData(Set<String> set, @NonNull List<Couple<String, JSONObject>> list) {
        if (set.contains(CollectorConfig.RULE_THERMAL_SCENARIO_TIME)) {
            Iterator<Couple<String, JSONObject>> it = list.iterator();
            while (it.hasNext()) {
                try {
                    parseCollectedData(it.next());
                } catch (JSONException e2) {
                    StringWriter stringWriter = new StringWriter();
                    e2.printStackTrace(new PrintWriter(stringWriter));
                    CollectorManager.getHistory().log(stringWriter.toString());
                    return;
                }
            }
            ThermalScenarioDTO thermalScenarioDTO = this.mCache.get(this.mKey);
            if (thermalScenarioDTO == null) {
                thermalScenarioDTO = new ThermalScenarioDTO(this.mKey);
                this.mCache.put(this.mKey.m2clone(), thermalScenarioDTO);
            }
            thermalScenarioDTO.setValue(this.mValue);
        }
    }
}
