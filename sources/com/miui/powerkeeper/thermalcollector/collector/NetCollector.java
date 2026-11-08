package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorUtils;
import com.miui.powerkeeper.utils.CommonAdapter;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class NetCollector extends BaseCollector {
    private static final String KEY_B1_GPS_MEAN = "B1_gps_mean";
    private static final String KEY_GPS = "gps";
    private static final String KEY_GPS_RSSI = "gps_rssi";
    private static final String KEY_L1_GPS_MEAN = "L1_gps_mean";
    private static final String KEY_L5_GPS_MEAN = "L5_gps_mean";
    private static final String KEY_MODEM_SIG = "modem_sig";
    private static final String KEY_NET_TYPE = "net_type";
    private static final String KEY_WIFI_AP = "wifi_ap";
    private static final String KEY_WIFI_SIG = "wifi_sig";
    private static final String TAG = "CollectorNet";

    public NetCollector() {
        super("CollectorNet");
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    public void collect() {
        JSONObject json = getJson();
        try {
            json.put(KEY_NET_TYPE, ThermalInfoUtils.getNetType(this.mContext));
            json.put(KEY_WIFI_SIG, ThermalInfoUtils.getWifiSignal(this.mContext));
            json.put(KEY_MODEM_SIG, ThermalInfoUtils.getModemSignal(this.mContext));
            json.put(KEY_WIFI_AP, CollectorUtils.getWifiApStatus(this.mContext));
            json.put("gps", CommonAdapter.isGpsEnabled(this.mContext));
            json.put(KEY_GPS_RSSI, -1);
            json.put(KEY_L1_GPS_MEAN, -1);
            json.put(KEY_L5_GPS_MEAN, -1);
            json.put(KEY_B1_GPS_MEAN, -1);
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
