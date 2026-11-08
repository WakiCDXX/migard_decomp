package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class TopActivityListener extends ElementListener {
    private final Map<String, Integer> mActivityMap = new HashMap(1);

    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 != -29) {
            return;
        }
        modifyCurrentState(this.mActivityMap.getOrDefault((String) obj, 97).intValue());
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        PowerKeeperManager.getInstance().getEventsAggregator().registerCurrentActivityChangeListener(this.mElementHandler);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 97;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        for (String str : SimpleSettings.Misc.getString(AutoRegister.getContext(), "thermal_camera_group_activities", "").split(",")) {
            this.mActivityMap.put(str, 1);
        }
    }
}
