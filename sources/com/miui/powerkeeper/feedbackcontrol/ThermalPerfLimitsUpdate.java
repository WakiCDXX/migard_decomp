package com.miui.powerkeeper.feedbackcontrol;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Device;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ThermalPerfLimitsUpdate {
    private static String TAG = "PowerKeeper.Perf";

    public static void updatePerfLimits(Context context, boolean z2) {
        StringBuilder sb = new StringBuilder();
        String str = Constants.CLOUD_THERMAL_PERF_LIMITS_PATH;
        sb.append(str);
        sb.append(Device.getInstance().getType(context));
        sb.append("/md5");
        String string = sb.toString();
        String strHttpGet = com.miui.powerkeeper.cloudcontrol.HttpUtils.httpGet(string, Constants.CLOUD_THERMAL_SID);
        if (strHttpGet == null || strHttpGet.isEmpty()) {
            Log.d(TAG, "get no data for md5 : " + string);
            return;
        }
        try {
            String string2 = new JSONObject(strHttpGet).getString("data");
            if (SimpleSettings.Misc.getString(context, SimpleSettingKeys.KEY_THERMAL_PERF_LIMITS_MD5, "").equalsIgnoreCase(string2) && !z2) {
                Log.i(TAG, "No need to get remote data: " + string2);
                return;
            }
            SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_THERMAL_PERF_LIMITS_MD5, string2);
            String strHttpGet2 = com.miui.powerkeeper.cloudcontrol.HttpUtils.httpGet(str + Device.getInstance().getType(context), Constants.CLOUD_THERMAL_SID);
            if (strHttpGet2 != null && !strHttpGet2.isEmpty()) {
                String string3 = new JSONObject(strHttpGet2).getString("data");
                if (TextUtils.isEmpty(string3)) {
                    return;
                }
                SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_THERMAL_PERF_LIMITS, string3);
                return;
            }
            Log.e(TAG, "get no data for thermal perf limits: " + strHttpGet2);
        } catch (JSONException e2) {
            Log.e(TAG, "startUpdate", e2);
        } catch (Exception e3) {
            Log.e(TAG, "startUpdate", e3);
        }
    }
}
