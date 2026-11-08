package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class OldThermalCollector extends BaseCollector {
    private static final String KEY_AMBIENT = "ambient";
    private static final String KEY_APP_END_TIME = "apet";
    private static final String KEY_BATTERY_TEMP = "bt_temp";
    private static final String KEY_BROAD_TEMP = "xo_temp";
    private static final String KEY_CAPACITY = "capend";
    private static final String KEY_EXCEP = "excep";
    private static final String KEY_PLUG_TYPE = "e_plug_type";
    private static final String KEY_TIMESTAMP = "toet";
    private static final String TAG = "CollectorOldThermal";
    private PowerKeeperInterface.d mBatteryChangedCallback;
    private PowerKeeperInterface.m mBatteryStatusReceiver;
    private int mBatteryTemp;
    private int mPlugType;

    public OldThermalCollector() {
        super("CollectorOldThermal");
        this.mPlugType = 0;
        this.mBatteryTemp = 0;
        this.mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.thermalcollector.collector.OldThermalCollector.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.m
            public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
                OldThermalCollector.this.mBatteryTemp = i5;
                OldThermalCollector.this.mPlugType = i6;
            }
        };
        registerListener();
    }

    private void registerListener() {
        this.mBatteryChangedCallback = new PowerKeeperInterface.d(this.mHandler, this.mBatteryStatusReceiver);
        PowerKeeperManager.getInstance().registerBatteryChangedListener(this.mBatteryChangedCallback);
    }

    private void unregisterListener() {
        PowerKeeperManager.getInstance().unregisterBatteryChangedListener(this.mBatteryChangedCallback);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        JSONObject json = getJson();
        try {
            boolean z2 = ThermalManager.ThermalTempInfoHelper.getXoThermalTemp() * 1000 >= 48000;
            json.put(KEY_CAPACITY, ThermalInfoUtils.getCurrentCapacity());
            json.put(KEY_PLUG_TYPE, this.mPlugType);
            json.put(KEY_BROAD_TEMP, ThermalManager.ThermalTempInfoHelper.getXoThermalTemp());
            json.put(KEY_BATTERY_TEMP, this.mBatteryTemp);
            json.put(KEY_AMBIENT, ThermalManager.ThermalTempInfoHelper.getAmbientTemp());
            json.put(KEY_TIMESTAMP, System.currentTimeMillis());
            json.put(KEY_APP_END_TIME, System.currentTimeMillis());
            json.put(KEY_EXCEP, z2);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
        unregisterListener();
    }
}
