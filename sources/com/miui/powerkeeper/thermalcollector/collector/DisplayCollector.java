package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorUtils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DisplayCollector extends BaseCollector {
    private static final String KEY_AMBIENT_LUX = "ambient_lux";
    private static final String KEY_BRIGHTNESS = "brightness";
    private static final String KEY_REFRESH = "refresh";
    private static final String KEY_RESOLUTION = "resolution";
    private static final String KEY_SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
    private static final String KEY_UI_NIGHT_MODE = "ui_night_mode";
    private static final String TAG = "CollectorDisplay";

    public DisplayCollector() {
        super("CollectorDisplay");
        registerListeners();
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    public void collect() {
        JSONObject json = getJson();
        try {
            json.put(KEY_BRIGHTNESS, ThermalInfoUtils.getScreenBrightness(this.mContext));
            json.put(KEY_REFRESH, ThermalInfoUtils.getRefreshRate(this.mContext));
            json.put(KEY_RESOLUTION, ThermalInfoUtils.getResolution(this.mContext));
            json.put(KEY_SCREEN_BRIGHTNESS_MODE, CollectorUtils.getScreenBrightnessMode(this.mContext));
            json.put(KEY_AMBIENT_LUX, -1);
            json.put(KEY_UI_NIGHT_MODE, CollectorUtils.getNightMode(this.mContext));
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
        unregisterListeners();
    }

    private void registerListeners() {
    }

    private void unregisterListeners() {
    }
}
