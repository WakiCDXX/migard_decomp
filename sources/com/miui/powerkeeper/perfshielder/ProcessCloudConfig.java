package com.miui.powerkeeper.perfshielder;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class ProcessCloudConfig extends PerfCloudConfig {
    private static final String TAG = "ProcessCloudConfig";
    private static List<String> sMoudleName;

    static {
        ArrayList arrayList = new ArrayList();
        sMoudleName = arrayList;
        arrayList.add("perf_shielder_Proc");
        sMoudleName.add("perf_proc_power");
        sMoudleName.add("perf_proc_threshold");
    }

    public ProcessCloudConfig(String str) {
        super(str);
    }

    private void parseJsonData(Context context, JSONObject jSONObject, String str, String str2) {
        Settings.System.putStringForUser(context.getContentResolver(), str, jSONObject.optString(str, str2), -2);
    }

    @Override // com.miui.powerkeeper.perfshielder.PerfCloudConfig
    public void parseResult(Context context, String str, String str2, String str3) {
        if (str2.equals("perf_shielder_Proc")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                parseJsonData(context, jSONObject, PerfShielderConstants.PROC_GAME_LIST, "");
                parseJsonData(context, jSONObject, PerfShielderConstants.PROC_GAME_OOM_CLOUD_ENABLE, "");
                Settings.System.putStringForUser(context.getContentResolver(), str2, str3, -2);
            } catch (Exception e2) {
                Log.d(TAG, "Process updateConfig " + e2.getMessage());
            }
        }
        if (str2.equals("perf_proc_power")) {
            try {
                parseJsonData(context, new JSONObject(str), PerfShielderConstants.PROC_THERMAL_WHITE_LIST, "");
                Settings.System.putStringForUser(context.getContentResolver(), str2, str3, -2);
            } catch (Exception e3) {
                Log.d(TAG, "Process updateConfig " + e3.getMessage());
            }
        }
        if (str2.equals("perf_proc_threshold")) {
            try {
                JSONObject jSONObject2 = new JSONObject(str);
                parseJsonData(context, jSONObject2, PerfShielderConstants.PROC_SWITCH_BG_DELAY_TIME, "");
                parseJsonData(context, jSONObject2, PerfShielderConstants.PROC_COMM_PSS_LIMIT, "");
                parseJsonData(context, jSONObject2, PerfShielderConstants.PROC_MEM_EXCEPTION_THRESHOLD, "");
                parseJsonData(context, jSONObject2, PerfShielderConstants.PROC_PROTECT_LIST, "");
                parseJsonData(context, jSONObject2, PerfShielderConstants.PROC_SCREEN_OFF_THRESHOLD, "");
                parseJsonData(context, jSONObject2, PerfShielderConstants.PROC_SCREEN_OFF_MIX_THRESHOLD, "");
                parseJsonData(context, jSONObject2, PerfShielderConstants.PROC_ADJ_BIND_LIST, "");
                Settings.System.putStringForUser(context.getContentResolver(), str2, str3, -2);
            } catch (Exception e4) {
                Log.d(TAG, "Process updateConfig " + e4.getMessage());
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
