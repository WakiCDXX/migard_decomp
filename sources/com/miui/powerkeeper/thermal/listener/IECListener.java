package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.PowerKeeperManager;

/* JADX INFO: loaded from: classes.dex */
public class IECListener extends ElementListener {
    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 != -28) {
            return;
        }
        modifyCurrentState(((Integer) obj).intValue());
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        PowerKeeperManager.getInstance().getEventsAggregator().registerIECStateChanged(this.mElementHandler);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 99;
    }
}
