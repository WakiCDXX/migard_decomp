package com.miui.powerkeeper.thermal.callback;

import android.annotation.NonNull;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.thermal.IThermalScenarioListener;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes.dex */
public class ScenarioCallbackManager {
    private static final int MESSAGE_UNREGISTER = 0;
    private static ScenarioCallbackManager sInstance;
    private final EventsAggregator mAggregator;
    private final Set<Callback> mCallbacks;
    private final Handler mHandler;
    private final Register mRegister = new Register();
    private int mCurrentTemp = 0;

    private static class CallbackHandler extends Handler {
        private final SoftReference<ScenarioCallbackManager> mManagerSoftReference;

        public CallbackHandler(@NonNull Looper looper, ScenarioCallbackManager scenarioCallbackManager) {
            super(looper);
            this.mManagerSoftReference = new SoftReference<>(scenarioCallbackManager);
        }

        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            ScenarioCallbackManager scenarioCallbackManager = this.mManagerSoftReference.get();
            int i2 = message.what;
            if (i2 == -37) {
                scenarioCallbackManager.onTempChanged(((Integer) message.obj).intValue());
            } else if (i2 == -32) {
                scenarioCallbackManager.onScenarioChanged(((Integer) message.obj).intValue());
            } else {
                if (i2 != 0) {
                    return;
                }
                scenarioCallbackManager.unregister((IThermalScenarioListener) message.obj);
            }
        }
    }

    private class Register implements Runnable {
        IThermalScenarioListener mListener;
        int[] mThreshold;

        @Override // java.lang.Runnable
        public void run() {
            Callback callback = new Callback(this.mThreshold, this.mListener);
            ScenarioCallbackManager.this.mCallbacks.add(callback);
            callback.onTempChanged(ScenarioCallbackManager.this.mCurrentTemp);
        }

        public void set(int[] iArr, IThermalScenarioListener iThermalScenarioListener) {
            this.mThreshold = iArr;
            this.mListener = iThermalScenarioListener;
        }

        private Register() {
        }
    }

    private ScenarioCallbackManager() {
        CallbackHandler callbackHandler = new CallbackHandler(BackgroundThread.getHandler().getLooper(), this);
        this.mHandler = callbackHandler;
        this.mCallbacks = new HashSet(1);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        this.mAggregator = eventsAggregator;
        eventsAggregator.registerScenarioListener(callbackHandler);
        eventsAggregator.registerForBoardTemperature(callbackHandler);
    }

    public static synchronized ScenarioCallbackManager getInstance() {
        try {
            if (sInstance == null) {
                sInstance = new ScenarioCallbackManager();
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$unregister$0(IThermalScenarioListener iThermalScenarioListener, Callback callback) {
        return callback.listenerEquals(iThermalScenarioListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onScenarioChanged(final int i2) {
        this.mCallbacks.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.callback.b
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Callback) obj).onScenarioChanged(i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTempChanged(final int i2) {
        this.mCurrentTemp = i2;
        this.mCallbacks.forEach(new Consumer() { // from class: com.miui.powerkeeper.thermal.callback.a
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Callback) obj).onTempChanged(i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregister(final IThermalScenarioListener iThermalScenarioListener) {
        this.mCallbacks.removeAll((Set) this.mCallbacks.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.thermal.callback.c
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ScenarioCallbackManager.lambda$unregister$0(iThermalScenarioListener, (Callback) obj);
            }
        }).collect(Collectors.toSet()));
    }

    public void registerCallback(int[] iArr, IThermalScenarioListener iThermalScenarioListener) {
        this.mRegister.set(iArr, iThermalScenarioListener);
        this.mHandler.post(this.mRegister);
    }

    public void unregisterCallback(IThermalScenarioListener iThermalScenarioListener) {
        this.mHandler.obtainMessage(0, iThermalScenarioListener).sendToTarget();
    }
}
