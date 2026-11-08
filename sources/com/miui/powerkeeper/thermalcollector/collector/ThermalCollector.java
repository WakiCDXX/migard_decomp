package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ThermalCollector extends BaseCollector {
    public static final String KEY_AMBIENT = "ambient_temp";
    public static final String KEY_BATTERY_TEMP = "battery_temp";
    public static final String KEY_BROAD_TEMP = "broad_temp";
    public static final String KEY_CAPACITY = "capacity";
    public static final String KEY_PLUG_TYPE = "plug_type";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String TAG = "CollectorThermal";
    private PowerKeeperInterface.d mBatteryChangedCallback;
    private PowerKeeperInterface.m mBatteryStatusReceiver;
    private int mBatteryTemp;
    private int mPlugType;

    public ThermalCollector() {
        super("CollectorThermal");
        this.mPlugType = 0;
        this.mBatteryTemp = 0;
        this.mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.thermalcollector.collector.ThermalCollector.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.m
            public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
                ThermalCollector.this.mBatteryTemp = i5;
                ThermalCollector.this.mPlugType = i6;
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
    public void collect() {
        JSONObject json = getJson();
        try {
            json.put(KEY_CAPACITY, ThermalInfoUtils.getCurrentCapacity());
            json.put(KEY_PLUG_TYPE, this.mPlugType);
            json.put(KEY_BROAD_TEMP, ThermalManager.ThermalTempInfoHelper.getXoThermalTemp());
            json.put(KEY_BATTERY_TEMP, this.mBatteryTemp);
            json.put(KEY_AMBIENT, ThermalManager.ThermalTempInfoHelper.getAmbientTemp());
            json.put("timestamp", System.currentTimeMillis());
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
        unregisterListener();
    }
}
