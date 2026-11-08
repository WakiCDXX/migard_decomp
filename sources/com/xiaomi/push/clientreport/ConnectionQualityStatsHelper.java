package com.xiaomi.push.clientreport;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.push.service.MIPushAccountUtils;

/* JADX INFO: loaded from: classes.dex */
public class ConnectionQualityStatsHelper {
    private static final String PER_THOUSAND_SAMPLING_SUFFIX_CONNECTION_QUALITY_STATS = "001";
    private static final String TAG = "Push-ConnectionQualityStatsHelper";
    private static Boolean shouldSampling;

    public static void log(String str, String str2) {
    }

    public static boolean shouldSampling(Context context) {
        if (shouldSampling == null) {
            if (!MIUIUtils.isXMSF(context)) {
                shouldSampling = Boolean.FALSE;
            }
            String uuid = MIPushAccountUtils.getUUID(context);
            if (TextUtils.isEmpty(uuid) || uuid.length() < 3) {
                shouldSampling = Boolean.FALSE;
            } else {
                String strSubstring = uuid.substring(uuid.length() - 3);
                log("shouldSampling uuid suffix = " + strSubstring);
                shouldSampling = Boolean.valueOf(TextUtils.equals(strSubstring, PER_THOUSAND_SAMPLING_SUFFIX_CONNECTION_QUALITY_STATS));
            }
            log("shouldSampling = " + shouldSampling);
        }
        return shouldSampling.booleanValue();
    }

    static void log(String str) {
        log(TAG, str);
    }
}
