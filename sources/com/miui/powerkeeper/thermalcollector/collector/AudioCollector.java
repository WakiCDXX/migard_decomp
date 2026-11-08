package com.miui.powerkeeper.thermalcollector.collector;

import android.os.SystemProperties;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class AudioCollector extends BaseCollector {
    private static final String KEY_ALLOW_SPEAKER_TO_RING_BUTTON = "allow_speaker_to_ring_button";
    private static final String KEY_MISOUND_HEADPHONE_TYPE = "misound_headphone_type";
    private static final String KEY_VOLUME = "volume";
    private static final String TAG = "CollectorAudio";

    public AudioCollector() {
        super("CollectorAudio");
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    public void collect() {
        JSONObject json = getJson();
        try {
            json.put(KEY_VOLUME, ThermalInfoUtils.getVolumeForApp(this.mContext));
            json.put(KEY_MISOUND_HEADPHONE_TYPE, SystemProperties.get("persist.vendor.audio.sfx.hd.type", "null"));
            json.put(KEY_ALLOW_SPEAKER_TO_RING_BUTTON, ThermalInfoUtils.getRingMode(this.mContext));
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
