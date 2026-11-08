package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.utils.Utils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothCollector extends BaseCollector {
    private static final String KEY_BLUETOOTH_STATE = "bluetooth";
    private static final String KEY_OT_DEVICE_TYPE = "ot_device_type";
    private static final String KEY_OT_RSSI = "ot_rssi";
    private static final String TAG = "CollectorBluetooth";
    private int mBlueToothRssi;

    public BluetoothCollector() {
        super("CollectorBluetooth");
        this.mBlueToothRssi = -1;
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    public void collect() {
        JSONObject json = getJson();
        try {
            json.put(KEY_BLUETOOTH_STATE, Utils.isBlueToothConnectedOrConnecting(this.mContext));
            json.put(KEY_OT_RSSI, this.mBlueToothRssi);
            json.put(KEY_OT_DEVICE_TYPE, Utils.getBlueToothType(this.mContext));
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
