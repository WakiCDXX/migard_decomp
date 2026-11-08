package com.miui.powerkeeper.utils;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.event.EventsAggregator;

/* JADX INFO: loaded from: classes.dex */
public class FloatSceneObserver {
    private static String TAG = "PowerKeeper.FloatSceneObserver";
    private boolean DEBUG;
    private Context mContext;
    private EventsAggregator mEventsAggregator;

    public class FloatWindowState {
        String floatWinTitle;
        boolean isFloated;
        String pkgName;
        int uid;

        public FloatWindowState(FloatSceneObserver floatSceneObserver, String str, String str2) {
            this(str, str2, true);
        }

        public boolean getIsFloated() {
            return this.isFloated;
        }

        public String getPkgName() {
            return this.pkgName;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("FloatSceneObserver: {");
            sb.append("pkgName: " + this.pkgName);
            sb.append(",floatWinTitle: " + this.floatWinTitle);
            sb.append(",isFloated: " + this.isFloated);
            sb.append("}");
            return sb.toString();
        }

        public FloatWindowState(String str, String str2, boolean z2) {
            this.pkgName = str;
            this.floatWinTitle = str2;
            this.isFloated = z2;
        }
    }

    public FloatSceneObserver(Context context, EventsAggregator eventsAggregator) {
        boolean zIsLoggable = Log.isLoggable(TAG, 3);
        this.DEBUG = zIsLoggable;
        if (zIsLoggable) {
            Log.d(TAG, "FloatSceneObserver init");
        }
        this.mContext = context;
        this.mEventsAggregator = eventsAggregator;
    }

    public void notifyFloatSceneStateChange(String str, String str2, boolean z2) {
        EventsAggregator eventsAggregator = this.mEventsAggregator;
        if (eventsAggregator == null) {
            return;
        }
        eventsAggregator.notifyForFloatSceneEvent(new FloatWindowState(str, str2, z2));
    }
}
