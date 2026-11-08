package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermal.resource.Couple;
import com.miui.powerkeeper.thermal.resource.IDumpable;
import java.io.PrintWriter;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public interface ICollector extends IDumpable {
    public static final ICollector sNullCollector = new ICollector() { // from class: com.miui.powerkeeper.thermalcollector.collector.ICollector.1
        @Override // com.miui.powerkeeper.thermalcollector.collector.ICollector
        public Couple<String, JSONObject> collectData() {
            return null;
        }

        @Override // com.miui.powerkeeper.thermalcollector.collector.ICollector
        public boolean readyBeDestroy() {
            return false;
        }

        @Override // com.miui.powerkeeper.thermal.resource.IDumpable
        public void dump(PrintWriter printWriter) {
        }

        @Override // com.miui.powerkeeper.thermalcollector.collector.ICollector
        public void removeEvent(String str) {
        }

        @Override // com.miui.powerkeeper.thermalcollector.collector.ICollector
        public void setEvent(String str) {
        }
    };

    Couple<String, JSONObject> collectData();

    boolean readyBeDestroy();

    void removeEvent(String str);

    void setEvent(String str);
}
