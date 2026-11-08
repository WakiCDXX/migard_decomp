package com.miui.powerkeeper.thermal.processor;

import android.annotation.NonNull;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class DSwitchProcessor extends SwitchProcessor {
    public static final Set<String> DELAY_DEVICES;
    public static final boolean IS_DELAY_DEVICES;
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    static {
        HashSet hashSet = new HashSet(Arrays.asList("sheng", "ruyi"));
        DELAY_DEVICES = hashSet;
        IS_DELAY_DEVICES = hashSet.contains(Build.DEVICE);
    }

    private boolean isDdelay(int i2, int i3) throws Throwable {
        int i4 = i2 % 100;
        int i5 = i3 % 100;
        int boardThermalTemp = ThermalManager.ThermalTempInfoHelper.getBoardThermalTemp();
        if (!IS_DELAY_DEVICES || boardThermalTemp < 40) {
            return false;
        }
        return (i4 == 18 || i4 == 19) && (i5 == 7 || i5 == 11 || i5 == 0);
    }

    @Override // com.miui.powerkeeper.thermal.processor.SwitchProcessor
    protected void calculateThermalId(int i2) {
        int iMapToThermalId = mapToThermalId(i2);
        if (this.mHandler == null) {
            super.calculateThermalId(i2);
            return;
        }
        int i3 = this.mCurrentThermalId;
        if (i3 != iMapToThermalId) {
            if (!isDdelay(i3, iMapToThermalId)) {
                this.mCurrentThermalId = iMapToThermalId;
                if (this.mHandler.hasMessages(1)) {
                    return;
                }
                this.mHandler.sendEmptyMessage(0);
                return;
            }
            this.mCurrentThermalId = iMapToThermalId;
            if (this.mHandler.hasMessages(1)) {
                Log.d("DSwitchProcessor", "recalculateThermalId: sleep 10s write");
                this.mHandler.removeMessages(1);
            }
            Log.d("DSwitchProcessor", "calculateThermalId: sleep 10s write");
            this.mHandler.sendEmptyMessageDelayed(1, 10000L);
        }
    }

    @Override // com.miui.powerkeeper.thermal.processor.SwitchProcessor, com.miui.powerkeeper.thermal.processor.ThermalProcessor
    public void init(Map<String, Set<Integer>> map) {
        super.init(map);
        HandlerThread handlerThread = new HandlerThread("WriteThermalIdHandlerThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: com.miui.powerkeeper.thermal.processor.DSwitchProcessor.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                DSwitchProcessor dSwitchProcessor = DSwitchProcessor.this;
                dSwitchProcessor.writeThermalId(dSwitchProcessor.mCurrentThermalId);
            }
        };
    }

    @Override // com.miui.powerkeeper.thermal.processor.SwitchProcessor
    @NonNull
    public String toString() {
        return "--- DSwitchProcessor \n" + super.toString();
    }
}
