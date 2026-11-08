package com.miui.powerkeeper.thermal.listener;

import android.os.AsyncResult;
import com.miui.powerkeeper.PowerKeeperManager;

/* JADX INFO: loaded from: classes.dex */
public class FoldListener extends ElementListener {
    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 != 3) {
            return;
        }
        modifyCurrentState(((Boolean) ((AsyncResult) obj).result).booleanValue() ? 99 : 98);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        PowerKeeperManager.getInstance().getEventsAggregator().registerForDisplayFold(this.mElementHandler, 3);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return PowerKeeperManager.getInstance().getEventsAggregator().getFoldedStatus() ? 99 : 98;
    }
}
