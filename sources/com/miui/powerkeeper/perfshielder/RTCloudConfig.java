package com.miui.powerkeeper.perfshielder;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class RTCloudConfig extends PerfCloudConfig {
    private static List<String> moudleName;
    private String TAG;

    static {
        ArrayList arrayList = new ArrayList();
        moudleName = arrayList;
        arrayList.add("perf_shielder_RTMODE");
        moudleName.add("perf_shielder_GESTURE");
    }

    public RTCloudConfig(String str) {
        super(str);
        this.TAG = PerfshielderCloudUpdate.TAG;
    }

    private void parseJsonData(Context context, JSONObject jSONObject, String str, String str2) {
        Settings.System.putStringForUser(context.getContentResolver(), str, jSONObject.optString(str, str2), -2);
    }

    @Override // com.miui.powerkeeper.perfshielder.PerfCloudConfig
    public void parseResult(Context context, String str, String str2, String str3) {
        if (!str2.equals("perf_shielder_RTMODE")) {
            if (str2.equals("perf_shielder_GESTURE")) {
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    parseJsonData(context, jSONObject, PerfShielderConstants.RT_GESTURE_ENABLE, "true");
                    parseJsonData(context, jSONObject, PerfShielderConstants.RT_GESTURE_WHITE_LIST, "");
                    Settings.System.putStringForUser(context.getContentResolver(), str2, str3, -2);
                    return;
                } catch (Exception e2) {
                    Log.d(this.TAG, "RTMODE GESTURE updateConfig " + e2.getMessage());
                    return;
                }
            }
            return;
        }
        try {
            JSONObject jSONObject2 = new JSONObject(str);
            parseJsonData(context, jSONObject2, PerfShielderConstants.RT_ENABLE, "true");
            parseJsonData(context, jSONObject2, PerfShielderConstants.RT_PKG_WHITE_LIST, "");
            parseJsonData(context, jSONObject2, PerfShielderConstants.RT_PKG_BLACK_LIST, "");
            parseJsonData(context, jSONObject2, PerfShielderConstants.RT_HIGHLEVEL_DEVICE_LIST, "");
            parseJsonData(context, jSONObject2, PerfShielderConstants.RT_TEMPLIMIT_BOTTOM, PerfShielderConstants.RT_TEMPLIMIT_BOTTOM_DEFAULT);
            parseJsonData(context, jSONObject2, PerfShielderConstants.RT_TEMPLIMIT_CEILING, PerfShielderConstants.RT_TEMPLIMIT_CEILING_DEFAULT);
            parseJsonData(context, jSONObject2, PerfShielderConstants.RT_TEMPLIMIT_ENABLED, PerfShielderConstants.RT_TEMPLIMIT_ENABLED_DEFAULT);
            Settings.System.putStringForUser(context.getContentResolver(), str2, str3, -2);
        } catch (Exception e3) {
            Log.d(this.TAG, "RTMODE updateConfig " + e3.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.perfshielder.PerfCloudConfig
    public void updateResult(Context context, String str, String str2) {
        if (str2 != null) {
            try {
                for (String str3 : moudleName) {
                    String strOptString = new JSONObject(str2).optString(str3);
                    if (strOptString != null) {
                        parseResult(context, strOptString, str3, str);
                    }
                }
            } catch (Exception e2) {
                Log.e(this.TAG, "updateConfig" + e2.getMessage());
            }
        }
    }
}
