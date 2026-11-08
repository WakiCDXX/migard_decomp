package com.xiaomi.push.clientreport;

import android.content.Context;
import com.xiaomi.push.providers.TrafficProvider;
import com.xiaomi.push.service.MIPushAccountUtils;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.stat.Reporter;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DisconnectStatsReporter {
    private static final String KEY_CONNECTED_NETWORK_TYPE = "connected_network_type";
    private final String EVENT_NAME = "disconnection_event";
    private final String KEY_DISCONNECT_COUNT = "count";
    private final String KEY_HOST = "host";
    private final String KEY_NETWORK_STATE = "network_state";
    private final String KEY_DISCONNECT_REASON = "reason";
    private final String KEY_DISCONNECT_PING_INTERVAL = "ping_interval";
    private final String KEY_DISCONNECT_NETWORK_TYPE = TrafficProvider.TrafficColumns.NETWORK_TYPE;
    private final String KEY_DISCONNECT_WIFI_DIGEST = "wifi_digest";
    private final String KEY_DISCONNECT_DURATION = "duration";
    private final String KEY_DISCONNECT_TIME = "disconnect_time";
    private final String KEY_RECONNECTED_TIME = "connect_time";
    private final String KEY_XMSF_VC = PushConstants.KEY_XMSF_VERSION_CODE;
    private final String KEY_ANDROID_VC = "android_vc";
    private final String KEY_UUID = "uuid";

    public void upload(Context context, List<DisconnectStatsModel> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        DisconnectStatsHelper.log("upload size = " + list.size());
        String uuid = MIPushAccountUtils.getUUID(context);
        for (DisconnectStatsModel disconnectStatsModel : list) {
            HashMap map = new HashMap();
            map.put("count", Integer.valueOf(disconnectStatsModel.getCount()));
            map.put("host", disconnectStatsModel.getHost());
            map.put("network_state", Integer.valueOf(disconnectStatsModel.getNetState()));
            map.put("reason", Integer.valueOf(disconnectStatsModel.getReason()));
            map.put("ping_interval", Long.valueOf(disconnectStatsModel.getPingInterval()));
            map.put(TrafficProvider.TrafficColumns.NETWORK_TYPE, Integer.valueOf(disconnectStatsModel.getNetType()));
            map.put("wifi_digest", disconnectStatsModel.getWifiDigest());
            map.put(KEY_CONNECTED_NETWORK_TYPE, Integer.valueOf(disconnectStatsModel.getConnectedNetType()));
            map.put("duration", Long.valueOf(disconnectStatsModel.getDuration()));
            map.put("disconnect_time", Long.valueOf(disconnectStatsModel.getDisconnectionTime()));
            map.put("connect_time", Long.valueOf(disconnectStatsModel.getReconnectionTime()));
            map.put(PushConstants.KEY_XMSF_VERSION_CODE, Integer.valueOf(disconnectStatsModel.getXmsfVc()));
            map.put("android_vc", Integer.valueOf(disconnectStatsModel.getAndroidVc()));
            map.put("uuid", uuid);
            Reporter.getInstance().trackEvent("disconnection_event", map);
        }
    }
}
