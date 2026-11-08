package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;

/* JADX INFO: loaded from: classes.dex */
public class PowerModeListener extends ElementListener {
    private boolean mEnable;

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (this.mEnable && i2 == -22) {
            modifyCurrentState(i3 == 1 ? 98 : 99);
        }
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        PowerKeeperManager.getInstance().getEventsAggregator().registerPowerModeListener(this.mElementHandler);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return PowerKeeperManager.getInstance().getEventsAggregator().getPowerMode() == 2 ? 99 : 98;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        this.mEnable = SimpleSettings.Misc.getBoolean(AutoRegister.getContext(), SimpleSettingKeys.KEY_THERMAL_ENHANCE_MODE_ENABLE, true);
    }
}
