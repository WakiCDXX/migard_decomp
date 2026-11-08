package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class HighFpsTopActivityListener extends TopActivityListener {
    private final Map<String, Integer> mHighFpsActivityMap = new HashMap(1);

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        super.dump(printWriter);
        printWriter.println("\tmHighFpsActivityMap: " + this.mHighFpsActivityMap);
    }

    @Override // com.miui.powerkeeper.thermal.listener.TopActivityListener, com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 != -29) {
            return;
        }
        String str = (String) obj;
        if (this.mHighFpsActivityMap.containsKey(str)) {
            super.modifyCurrentState(this.mHighFpsActivityMap.getOrDefault(str, 97).intValue());
        } else {
            super.elementChanged(i2, obj, i3, i4);
        }
    }

    @Override // com.miui.powerkeeper.thermal.listener.TopActivityListener, com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        super.updateCloudData();
        this.mHighFpsActivityMap.clear();
        for (String str : SimpleSettings.Misc.getString(AutoRegister.getContext(), "thermal_highfps_group_activities", "").split(",")) {
            this.mHighFpsActivityMap.put(str, 2);
        }
    }
}
