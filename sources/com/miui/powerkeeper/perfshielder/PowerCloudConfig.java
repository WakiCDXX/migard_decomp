package com.miui.powerkeeper.perfshielder;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import d.a;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class PowerCloudConfig extends PerfCloudConfig {
    private static final String TAG = "PowerCloudConfig";
    private static List<String> sMoudleName;

    static {
        ArrayList arrayList = new ArrayList();
        sMoudleName = arrayList;
        arrayList.add("perf_shielder_smartpower");
    }

    public PowerCloudConfig(String str) {
        super(str);
    }

    private void parseJsonData(Context context, JSONObject jSONObject, String str, String str2) {
        String strOptString = jSONObject.optString(str, str2);
        Settings.System.putStringForUser(context.getContentResolver(), str, strOptString, -2);
        if (str == PerfShielderConstants.POWER_PKG_WHITE_LIST) {
            a.e().f(strOptString);
        }
    }

    @Override // com.miui.powerkeeper.perfshielder.PerfCloudConfig
    public void parseResult(Context context, String str, String str2, String str3) {
        if (str2.equals("perf_shielder_smartpower")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_APPSTATE_ENABLE, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_FREEZE_ENABLE, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_DISPLAY_ENABLE, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_WINDOW_ENABLE, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_PKG_WHITE_LIST, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_SCREENON_WHITE_LIST, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_PROC_WHITE_LIST, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_BROADCAST_WHITE_LIST, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_CLOUD_INTERCEPT_ENABLE, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_PROVIDER_WHITE_LIST, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_ALARM_WHITE_LIST, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.POWER_SERVICE_WHITE_LIST, "");
                Settings.System.putStringForUser(context.getContentResolver(), str2, str3, -2);
            } catch (Exception e2) {
                Log.d(TAG, "Power updateConfig " + e2.getMessage());
            }
        }
    }

    @Override // com.miui.powerkeeper.perfshielder.PerfCloudConfig
    public void updateResult(Context context, String str, String str2) {
        if (str2 == null) {
            return;
        }
        try {
            for (String str3 : sMoudleName) {
                String strOptString = new JSONObject(str2).optString(str3);
                if (strOptString != null) {
                    parseResult(context, strOptString, str3, str);
                }
            }
        } catch (Exception e2) {
            Log.e(TAG, "updateConfig" + e2.getMessage());
        }
    }
}
