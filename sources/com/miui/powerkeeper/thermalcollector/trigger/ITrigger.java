package com.miui.powerkeeper.thermalcollector.trigger;

import com.miui.powerkeeper.thermal.resource.IDumpable;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.DTO.EventInfoDTO;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public interface ITrigger extends IDumpable {
    public static final ITrigger sNullTrigger = new ITrigger() { // from class: com.miui.powerkeeper.thermalcollector.trigger.ITrigger.1
        @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
        public boolean readyBeDestroy() {
            return false;
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
        public void startWork() {
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
        public void stopWork() {
        }

        @Override // com.miui.powerkeeper.thermal.resource.IDumpable
        public void dump(PrintWriter printWriter) {
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
        public void removeRule(String str) {
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
        public void setCallback(CollectorManager.TriggerCallback triggerCallback) {
        }

        @Override // com.miui.powerkeeper.thermalcollector.trigger.ITrigger
        public void setRule(String str, EventInfoDTO eventInfoDTO) {
        }
    };

    boolean readyBeDestroy();

    void removeRule(String str);

    void setCallback(CollectorManager.TriggerCallback triggerCallback);

    void setRule(String str, EventInfoDTO eventInfoDTO);

    void startWork();

    void stopWork();
}
