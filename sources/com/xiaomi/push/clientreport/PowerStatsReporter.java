package com.xiaomi.push.clientreport;

import android.content.Context;
import com.xiaomi.push.service.MIPushAccountUtils;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.stat.Reporter;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class PowerStatsReporter {
    private final String EVENT_NAME = "power_consumption_stats";
    private final String KEY_SCREEN_OFF_UP_COUNT = "off_up_ct";
    private final String KEY_SCREEN_OFF_DOWN_COUNT = "off_dn_ct";
    private final String KEY_SCREEN_OFF_PING_COUNT = "off_ping_ct";
    private final String KEY_SCREEN_OFF_PONG_COUNT = "off_pong_ct";
    private final String KEY_SCREEN_OFF_DURATION = "off_dur";
    private final String KEY_SCREEN_ON_UP_COUNT = "on_up_ct";
    private final String KEY_SCREEN_ON_DOWN_COUNT = "on_dn_ct";
    private final String KEY_SCREEN_ON_PING_COUNT = "on_ping_ct";
    private final String KEY_SCREEN_ON_PONG_COUNT = "on_pong_ct";
    private final String KEY_SCREEN_ON_DURATION = "on_dur";
    private final String KEY_START_TIME = "start_time";
    private final String KEY_END_TIME = "end_time";
    private final String KEY_XMSF_VC = PushConstants.KEY_XMSF_VERSION_CODE;
    private final String KEY_ANDROID_VC = "android_vc";
    private final String KEY_UUID = "uuid";

    public void upload(Context context, PowerStatsModel powerStatsModel) {
        if (powerStatsModel == null) {
            return;
        }
        HashMap map = new HashMap();
        map.put("off_up_ct", Integer.valueOf(powerStatsModel.getOffUpCount()));
        map.put("off_dn_ct", Integer.valueOf(powerStatsModel.getOffDownCount()));
        map.put("off_ping_ct", Integer.valueOf(powerStatsModel.getOffPingCount()));
        map.put("off_pong_ct", Integer.valueOf(powerStatsModel.getOffPongCount()));
        map.put("off_dur", Long.valueOf(powerStatsModel.getOffDuration()));
        map.put("on_up_ct", Integer.valueOf(powerStatsModel.getOnUpCount()));
        map.put("on_dn_ct", Integer.valueOf(powerStatsModel.getOnDownCount()));
        map.put("on_ping_ct", Integer.valueOf(powerStatsModel.getOnPingCount()));
        map.put("on_pong_ct", Integer.valueOf(powerStatsModel.getOnPongCount()));
        map.put("on_dur", Long.valueOf(powerStatsModel.getOnDuration()));
        map.put("start_time", Long.valueOf(powerStatsModel.getStartTime()));
        map.put("end_time", Long.valueOf(powerStatsModel.getEndTime()));
        map.put(PushConstants.KEY_XMSF_VERSION_CODE, Integer.valueOf(powerStatsModel.getXmsfVc()));
        map.put("android_vc", Integer.valueOf(powerStatsModel.getAndroidVc()));
        map.put("uuid", MIPushAccountUtils.getUUID(context));
        Reporter.getInstance().trackEvent("power_consumption_stats", map);
    }
}
