package com.miui.powerkeeper.thermalcollector.event;

import android.annotation.NonNull;
import com.miui.powerkeeper.thermal.resource.Couple;
import com.miui.powerkeeper.thermal.resource.IDumpable;
import com.miui.powerkeeper.thermalcollector.DTO.EventCloudDTO;
import com.miui.powerkeeper.thermalcollector.DTO.EventInfoDTO;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public interface IEvent extends IDumpable {
    public static final IEvent sNullEvent = new IEvent() { // from class: com.miui.powerkeeper.thermalcollector.event.IEvent.1
        @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
        public EventInfoDTO parseParameters(EventCloudDTO.EventParameter eventParameter) {
            return null;
        }

        @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
        public void settlement() {
        }

        @Override // com.miui.powerkeeper.thermalcollector.event.IEvent, com.miui.powerkeeper.thermal.resource.IDumpable
        public void dump(PrintWriter printWriter) {
        }

        @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
        public void updateData(Set<String> set, @NonNull List<Couple<String, JSONObject>> list) {
        }
    };

    @Override // com.miui.powerkeeper.thermal.resource.IDumpable
    void dump(PrintWriter printWriter);

    EventInfoDTO parseParameters(EventCloudDTO.EventParameter eventParameter);

    void settlement();

    void updateData(Set<String> set, @NonNull List<Couple<String, JSONObject>> list);
}
