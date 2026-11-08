package com.miui.powerkeeper.siming.shaosi;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventMonitor;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.siming.SiUtils;
import com.miui.powerkeeper.siming.dasi.MIY;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class CommonStatus extends N {
    public static final String TAG = "SM_S_CommonStatus";

    protected CommonStatus(Looper looper) {
        super(looper, 103);
        Context contextB = PowerKeeperApplication.b();
        EventsAggregator.getInstance(contextB).registerForScreenOn(this.mHandler, null);
        EventsAggregator.getInstance(contextB).registerForScreenOff(this.mHandler, null);
        EventsAggregator.getInstance(contextB).registerForCharging(this.mHandler);
        EventsAggregator.getInstance(contextB).registerForUserPresent(this.mHandler, null);
        EventsAggregator.getInstance(contextB).registerForForegroundInfo(this.mHandler);
        if (PowerKeeperManager.getInstance().isScreenOn()) {
            notifyScreenOn();
        } else {
            notifyScreenOff();
        }
        notifyChargingStatusChanged(PowerKeeperManager.getInstance().isCharging());
        EventMonitor.getInstance().registerCallback(16, new EventMonitor.EventCallback() { // from class: com.miui.powerkeeper.siming.shaosi.CommonStatus.1
            @Override // com.miui.powerkeeper.event.EventMonitor.EventCallback
            public void notifyEvent(int i2, Bundle bundle) {
                SiUtils.Log.d(CommonStatus.TAG, "notifyEvent resId=" + i2 + ", interactive=" + bundle.getBoolean("interactive"));
            }
        });
    }

    private void notifyChargingStatusChanged(boolean z2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MIY.BUNDLE_PARAM_CHARGING, z2);
        MIY.notify(2, bundle);
    }

    private void notifyFgInfoChanged(ForegroundInfo foregroundInfo) {
        Bundle bundle = new Bundle();
        bundle.putObject(MIY.BUNDLE_PARAM_FG_INFO, foregroundInfo);
        MIY.notify(4, bundle);
    }

    private void notifyScreenOff() {
        MIY.notify(1);
    }

    private void notifyScreenOn() {
        MIY.notify(0);
    }

    private void notifyUserPresent() {
        MIY.notify(3);
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S, com.miui.powerkeeper.siming.IDivision
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Common Status, SI=" + this.SI);
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -9) {
            notifyFgInfoChanged((ForegroundInfo) message.obj);
            return;
        }
        if (i2 == -8) {
            notifyChargingStatusChanged(((Boolean) ((AsyncResult) message.obj).result).booleanValue());
            return;
        }
        if (i2 == -7) {
            notifyUserPresent();
        } else if (i2 == -5) {
            notifyScreenOff();
        } else {
            if (i2 != -4) {
                return;
            }
            notifyScreenOn();
        }
    }
}
