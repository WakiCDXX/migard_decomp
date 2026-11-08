package com.miui.powerkeeper.utils;

import android.os.RemoteException;
import android.view.IDisplayFoldListener;
import android.view.WindowManagerGlobal;
import com.miui.powerkeeper.event.EventsAggregator;

/* JADX INFO: loaded from: classes.dex */
public class DisplayFoldStateObserver {
    EventsAggregator mEventsAggregator;

    public DisplayFoldStateObserver(EventsAggregator eventsAggregator) {
        this.mEventsAggregator = eventsAggregator;
        monitorDisplayFold();
    }

    private void monitorDisplayFold() {
        IDisplayFoldListener.Stub stub = new IDisplayFoldListener.Stub() { // from class: com.miui.powerkeeper.utils.DisplayFoldStateObserver.1
            public void onDisplayFoldChanged(int i2, boolean z2) throws RemoteException {
                EventsAggregator eventsAggregator = DisplayFoldStateObserver.this.mEventsAggregator;
                if (eventsAggregator == null) {
                    return;
                }
                eventsAggregator.notifyDisplayFoldChanged(z2);
            }
        };
        try {
            EventsAggregator eventsAggregator = this.mEventsAggregator;
            if (eventsAggregator == null || !eventsAggregator.isFoldAbleDevice()) {
                return;
            }
            registerDisplayFoldListener(stub);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void registerDisplayFoldListener(Object obj) throws RemoteException {
        WindowManagerGlobal.getWindowManagerService().registerDisplayFoldListener((IDisplayFoldListener) obj);
    }
}
