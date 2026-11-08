package com.miui.powerkeeper.statemachine;

import android.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class CastingHandler extends Handler {
    public static final String BOARD_FILE = "/sys/class/thermal/thermal_message/board_sensor_temp";
    private static final String BROADCAST_ACTION = "com.miui.powerKeeper.THERMAL_CASTING_STATE_CHANGED";
    private static final String EXTRA_NAME = "state";
    private static final long INTERACTIVE_ACTION_TYPE_CASTING = 562949953421312L;
    public static final int OFF = 0;
    public static final int ON = 1;
    private static final long TRIGGER_TEMPERATURE = 41000;
    private static final long UNTRIGGER_TEMPERATURE = 38000;
    private static CastingHandler sInstance;
    private int mCameraState;
    private int mCastingState;
    private final Context mContext;
    private int mCurrentState;
    private final EventsAggregator mEventsAggregator;
    private int mTempState;

    public CastingHandler(Context context, @NonNull Looper looper) {
        super(looper);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        this.mEventsAggregator = eventsAggregator;
        this.mContext = context;
        if (context == null) {
            return;
        }
        eventsAggregator.registerSmartPowerScenarioIdChanged(this);
        eventsAggregator.registerForBoardTemperature(this);
    }

    public static synchronized CastingHandler getInstance() {
        return sInstance;
    }

    public static synchronized CastingHandler init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new CastingHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private void onCameraChange(long j2) {
        this.mCameraState = (j2 & ScenarioConfig.INTERACTIVE_ACTION_TYPE_CAMERA) != 0 ? 1 : 0;
    }

    private void onCastingChange(long j2) {
        this.mCastingState = (j2 & 562949953421312L) != 0 ? 1 : 0;
    }

    private synchronized void onStateChange() {
        int i2 = this.mCameraState & this.mCastingState & this.mTempState;
        if (i2 != this.mCurrentState) {
            this.mCurrentState = i2;
            sendBroadcast();
        }
    }

    private void onTempChange(int i2) {
        long j2 = i2;
        if (j2 > TRIGGER_TEMPERATURE) {
            this.mTempState = 1;
        } else if (j2 < UNTRIGGER_TEMPERATURE) {
            this.mTempState = 0;
        }
    }

    private void sendBroadcast() {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("state", this.mCurrentState);
        this.mContext.sendStickyBroadcast(intent);
    }

    public void destroy() {
        this.mEventsAggregator.unregisterSmartPowerScenarioIdChanged(this);
        this.mEventsAggregator.unregisterForBoardTemperature(this);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("--- Casting Handler ---");
        printWriter.println("mCameraState: " + this.mCurrentState);
        printWriter.println("mCastingState: " + this.mCastingState);
        printWriter.println("mTempState： " + this.mTempState);
        printWriter.println();
    }

    public synchronized int getCurrentState() {
        return this.mCurrentState;
    }

    @Override // android.os.Handler
    public void handleMessage(@NonNull Message message) {
        int i2 = message.what;
        if (i2 == -37) {
            onTempChange(((Integer) message.obj).intValue());
        } else {
            if (i2 != -34) {
                return;
            }
            onCastingChange(((Long) message.obj).longValue());
            onCameraChange(((Long) message.obj).longValue());
        }
        onStateChange();
    }
}
