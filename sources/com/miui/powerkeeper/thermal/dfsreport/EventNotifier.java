package com.miui.powerkeeper.thermal.dfsreport;

import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class EventNotifier {
    private static final String TAG = "powerkeeper.dfsenotify";
    private static EventNotifier mDfsEventNotifierInstance;
    private List<ThermalDfsInterface.EventListener> mListenerList = new ArrayList();

    private EventNotifier() {
    }

    public static synchronized EventNotifier getDfsEventNotifierInstance() {
        try {
            if (mDfsEventNotifierInstance == null) {
                mDfsEventNotifierInstance = new EventNotifier();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mDfsEventNotifierInstance;
    }

    public void clearListener() {
        synchronized (this.mListenerList) {
            this.mListenerList.clear();
        }
    }

    public void notifyListeners(DfsEventData dfsEventData) {
        List<ThermalDfsInterface.EventListener> list = this.mListenerList;
        if (list == null || list.size() <= 0) {
            Log.d(TAG, "notify list have not listener");
            return;
        }
        Log.d(TAG, "get listener and run");
        Iterator<ThermalDfsInterface.EventListener> it = this.mListenerList.iterator();
        while (it.hasNext()) {
            it.next().onEventReceived(dfsEventData);
        }
    }

    public void registerListener(ThermalDfsInterface.EventListener eventListener) {
        List<ThermalDfsInterface.EventListener> list = this.mListenerList;
        if (list == null) {
            Log.d(TAG, "list is null can not register listener in listenerList");
            return;
        }
        synchronized (list) {
            try {
                if (!this.mListenerList.contains(eventListener)) {
                    this.mListenerList.add(eventListener);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        Log.d(TAG, "register event listener size is " + this.mListenerList.size());
    }
}
