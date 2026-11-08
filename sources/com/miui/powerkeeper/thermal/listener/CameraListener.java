package com.miui.powerkeeper.thermal.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class CameraListener extends ElementListener {
    private static final int STATE_CAMERA_CHANGED = 0;
    private static final int STATE_NIGHT_IN = -1;
    private static final int STATE_NIGHT_OUT = -2;
    private int mCameraState = 99;
    private int mSystemCamera = 99;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.thermal.listener.CameraListener.1
        /* JADX WARN: Code duplicated, block: B:22:0x0044  */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            byte b2;
            if (intent == null || intent.getAction() == null) {
                return;
            }
            String action = intent.getAction();
            int iHashCode = action.hashCode();
            if (iHashCode != 526481768) {
                if (iHashCode != 986814411) {
                    if (iHashCode == 1776107135 && action.equals(ScenarioConfig.ACTION_CAMERA_STATE)) {
                        b2 = 0;
                    } else {
                        b2 = -1;
                    }
                } else if (action.equals(ScenarioConfig.NIGHT_VIDEO_IN_ACTION)) {
                    b2 = 1;
                } else {
                    b2 = -1;
                }
            } else if (action.equals(ScenarioConfig.NIGHT_VIDEO_OUT_ACTION)) {
                b2 = 2;
            } else {
                b2 = -1;
            }
            if (b2 == 0) {
                CameraListener.this.mElementHandler.obtainMessage(0, intent).sendToTarget();
            } else if (b2 == 1) {
                CameraListener.this.mElementHandler.obtainMessage(-1).sendToTarget();
            } else {
                if (b2 != 2) {
                    return;
                }
                CameraListener.this.mElementHandler.obtainMessage(-2).sendToTarget();
            }
        }
    };

    private int calculateCameraState() {
        int i2;
        int i3 = this.mCameraState;
        return (i3 == 99 || (i2 = this.mSystemCamera) == 99) ? i3 : i2;
    }

    private int onRecordStateChanged(int i2) {
        if (i2 != 1) {
            return i2 != 2 ? 99 : 2;
        }
        return 1;
    }

    private int onSmartScenarioChanged(long j2) {
        int i2 = (j2 & ScenarioConfig.INTERACTIVE_ACTION_TYPE_CAMERA) != 0 ? 98 : 99;
        Log.d("ElementListener", "on smart scenario changed to " + i2);
        return i2;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        super.dump(printWriter);
        printWriter.println("\tCurrentState: " + this.mCurrentState);
        printWriter.println("\tCameraState: " + this.mCameraState);
        printWriter.println("\tmSystemCamera: " + this.mSystemCamera);
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 == -34) {
            this.mCameraState = onSmartScenarioChanged(((Long) obj).longValue());
        } else if (i2 == 1) {
            this.mSystemCamera = onRecordStateChanged(((Integer) obj).intValue());
        } else if (i2 == -2) {
            this.mSystemCamera = 99;
        } else if (i2 != -1) {
            return;
        } else {
            this.mSystemCamera = 3;
        }
        modifyCurrentState(calculateCameraState());
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        IntentFilter intentFilter = new IntentFilter(ScenarioConfig.ACTION_CAMERA_STATE);
        intentFilter.addAction(ScenarioConfig.NIGHT_VIDEO_IN_ACTION);
        intentFilter.addAction(ScenarioConfig.NIGHT_VIDEO_OUT_ACTION);
        AutoRegister.getContext().registerReceiver(this.mReceiver, intentFilter, 2);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        eventsAggregator.registerForVideoRecordState(this.mElementHandler, 1);
        eventsAggregator.registerSmartPowerScenarioIdChanged(this.mElementHandler);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 99;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.listener.IListener
    public void uninstall() {
        super.uninstall();
        AutoRegister.getContext().unregisterReceiver(this.mReceiver);
    }
}
