package com.miui.powerkeeper.thermal.listener;

import android.provider.Settings;
import com.miui.powerkeeper.PowerKeeperApplication;

/* JADX INFO: loaded from: classes.dex */
public class SatelliteCallListener extends CallListener {
    @Override // com.miui.powerkeeper.thermal.listener.CallListener, com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (Settings.System.getInt(PowerKeeperApplication.b().getContentResolver(), "satellite_state", 0) == 1) {
            modifyCurrentState(((Boolean) obj).booleanValue() ? 1 : 99);
        } else {
            super.elementChanged(i2, obj, i3, i4);
        }
    }
}
