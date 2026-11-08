package com.miui.powerkeeper.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.internal.os.BackgroundThread;

/* JADX INFO: loaded from: classes.dex */
public class WatchDog {
    private static final int DEFAULT_DELAY_TIME = 60000;
    public static final int EVENT_NETD_TIMEOUT = 1;
    private final Handler mHandler;
    private static final String TAG = "PowerKeeper.WD";
    private static final boolean DBG_WATCH = Log.isLoggable(TAG, 3);

    public interface Action {
        void doAction();
    }

    private static class Holder {
        private static final WatchDog sInstance = new WatchDog();

        private Holder() {
        }
    }

    public static WatchDog get() {
        return Holder.sInstance;
    }

    public void cancel(int i2, Object obj) {
        if (DBG_WATCH) {
            Log.d(TAG, "cancelWatch what=" + i2 + " token=" + obj);
        }
        this.mHandler.removeMessages(i2, obj);
    }

    public void start(int i2, Action action) {
        if (DBG_WATCH) {
            Log.d(TAG, "startWatch what=" + i2 + " token=" + action);
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(i2, action), 60000L);
    }

    private WatchDog() {
        this.mHandler = new Handler(BackgroundThread.getHandler().getLooper()) { // from class: com.miui.powerkeeper.utils.WatchDog.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                try {
                    if (message.what != 1) {
                        return;
                    }
                    if (WatchDog.DBG_WATCH) {
                        Log.d(WatchDog.TAG, "handleMessage EVENT_NETD_TIMEOUT obj=" + message.obj);
                    }
                    Object obj = message.obj;
                    if (obj instanceof Action) {
                        ((Action) obj).doAction();
                    } else {
                        Log.w(WatchDog.TAG, "handleMessage wrong argument");
                    }
                } catch (Exception e2) {
                    Log.w(WatchDog.TAG, "handleMessage exception", e2);
                }
            }
        };
    }
}
