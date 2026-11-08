package com.miui.powerkeeper.event;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import com.miui.whetstone.IPowerEventCallback;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class EventMonitor {
    public static final long MONTH = 2592000000L;
    public static final int RESOURCE_ID_ALARM = 2;
    public static final int RESOURCE_ID_AUDIO = 1;
    public static final int RESOURCE_ID_AlARM_TRIGGER = 128;
    public static final int RESOURCE_ID_DEVICE_IDLE = 4;
    public static final int RESOURCE_ID_FOCUS_WINDOW = 32;
    public static final int RESOURCE_ID_GPS = 8;
    public static final int RESOURCE_ID_INTERACTIVE = 16;
    public static final int RESOURCE_ID_JOBSCHEDULER_TRIGGER = 256;
    public static final int RESOURCE_ID_MOTION_EVENT = 64;
    public static final String TAG = "EventMonitor";
    private static EventMonitor sInstance;
    private final IWhetstoneActivityManager mWs;
    private final SparseArray<ArrayList<EventCallback>> mCallbackArray = new SparseArray<>();
    private int mResFlags = 0;
    private final SparseArray<EventTracker> mEventTrackerList = new SparseArray<>();
    private final IPowerEventCallback mEventCallbackForRemote = new IPowerEventCallback.Stub() { // from class: com.miui.powerkeeper.event.EventMonitor.1
        public void notifyEvent(int i2, Bundle bundle) {
            Log.i(EventMonitor.TAG, "notifyEvent resId=" + i2);
            EventMonitor.this.noteEvent(i2, bundle.getLong("startTime"));
            EventMonitor.this.notifyCallbacks(i2, bundle);
        }
    };

    public interface EventCallback {
        void notifyEvent(int i2, Bundle bundle);
    }

    public static class EventTracker {
        long totalDuration;
        int times = 0;
        long startTime = 0;
        SparseArray<EventTrackerTarget> targetList = new SparseArray<>();
    }

    public static class EventTrackerTarget {
        String className;
        int times = 0;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ResourceIds {
    }

    private EventMonitor() {
        IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        this.mWs = iWhetstoneActivityManagerAsInterface;
        if (iWhetstoneActivityManagerAsInterface != null) {
            registerEventCallback();
        }
    }

    public static synchronized EventMonitor getInstance() {
        try {
            if (sInstance == null) {
                sInstance = new EventMonitor();
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void noteEvent(int i2, long j2) {
        EventTracker eventTracker = this.mEventTrackerList.get(i2);
        if (eventTracker == null) {
            eventTracker = new EventTracker();
            eventTracker.startTime = SystemClock.elapsedRealtime();
            this.mEventTrackerList.put(i2, eventTracker);
        }
        if (SystemClock.elapsedRealtime() - eventTracker.startTime > MONTH) {
            eventTracker.startTime = SystemClock.elapsedRealtime();
            eventTracker.times = 0;
            eventTracker.totalDuration = 0L;
            eventTracker.targetList.clear();
        }
        eventTracker.times++;
        eventTracker.totalDuration += SystemClock.elapsedRealtime() - j2;
        Iterator<EventCallback> it = this.mCallbackArray.get(i2).iterator();
        while (it.hasNext()) {
            String name = it.next().getClass().getName();
            int iHashCode = name.hashCode();
            EventTrackerTarget eventTrackerTarget = eventTracker.targetList.get(iHashCode);
            if (eventTrackerTarget == null) {
                eventTrackerTarget = new EventTrackerTarget();
                eventTrackerTarget.className = name;
                eventTracker.targetList.put(iHashCode, eventTrackerTarget);
            }
            eventTrackerTarget.times++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCallbacks(int i2, Bundle bundle) {
        ArrayList<EventCallback> arrayList = this.mCallbackArray.get(i2);
        if (arrayList == null) {
            updateResIdsIfNeeded((~i2) & this.mResFlags);
        } else {
            Iterator<EventCallback> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().notifyEvent(i2, bundle);
            }
        }
    }

    private void printTracker(int i2, EventTracker eventTracker, PrintWriter printWriter) {
        if (eventTracker == null) {
            Log.d(TAG, "No track for resId = " + i2);
            return;
        }
        printWriter.println(String.format(Locale.getDefault(), "Res: 0x%x", Integer.valueOf(i2)));
        printWriter.println("  times=" + eventTracker.times + ", totalDuration=" + eventTracker.totalDuration + ", avgDuration=" + (eventTracker.totalDuration / ((long) eventTracker.times)));
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        sb.append("targetList:");
        printWriter.println(sb.toString());
        for (int i3 = 0; i3 < eventTracker.targetList.size(); i3++) {
            printWriter.println("    " + eventTracker.targetList.keyAt(i3) + "  " + eventTracker.targetList.valueAt(i3).className + "  " + eventTracker.targetList.valueAt(i3).times);
        }
    }

    private void registerEventCallback() {
        Log.i(TAG, "registerEventCallback");
        try {
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                return;
            }
            iWhetstoneActivityManager.registerPowerEventCallback(this.mEventCallbackForRemote);
        } catch (RemoteException unused) {
            Log.e(TAG, "registerEventCallback work abnormal");
        }
    }

    private void updateResIdsIfNeeded(int i2) {
        if (this.mResFlags != i2) {
            this.mResFlags = i2;
            try {
                IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
                if (iWhetstoneActivityManager == null) {
                    return;
                }
                iWhetstoneActivityManager.updateResIds(i2);
            } catch (RemoteException unused) {
                Log.e(TAG, "registerEventCallback work abnormal");
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("====EventMonitor Start=====");
        printWriter.println("mResFlags=0x" + String.format(Locale.getDefault(), "%x", Integer.valueOf(this.mResFlags)));
        printWriter.println("EventTracker:");
        for (int i2 = 0; i2 < 30; i2++) {
            int i3 = 1 << i2;
            if ((this.mResFlags & i3) != 0) {
                printTracker(i3, this.mEventTrackerList.get(i3), printWriter);
            }
        }
        printWriter.println("====EventMonitor End=====");
    }

    public void registerCallback(int i2, EventCallback eventCallback) {
        ArrayList<EventCallback> arrayList = this.mCallbackArray.get(i2);
        if (arrayList == null) {
            Log.i(TAG, "registerCallback list for resId=" + i2 + " is empty.");
            arrayList = new ArrayList<>();
            this.mCallbackArray.put(i2, arrayList);
        }
        if (arrayList.contains(eventCallback)) {
            return;
        }
        arrayList.add(eventCallback);
        updateResIdsIfNeeded(i2 | this.mResFlags);
    }

    public void unregisterCallback(int i2, EventCallback eventCallback) {
        ArrayList<EventCallback> arrayList = this.mCallbackArray.get(i2);
        if (arrayList == null) {
            return;
        }
        arrayList.remove(eventCallback);
        if (arrayList.size() == 0) {
            Log.i(TAG, "unregisterCallback list for resId=" + i2 + " is empty.");
            this.mCallbackArray.delete(i2);
        }
        updateResIdsIfNeeded((~i2) & this.mResFlags);
    }
}
