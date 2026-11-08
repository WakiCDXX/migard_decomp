package com.miui.powerkeeper.perfshielder;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class SPTMCloudConfig extends PerfCloudConfig {
    private static List<String> moudleName;
    private String TAG;

    static {
        ArrayList arrayList = new ArrayList();
        moudleName = arrayList;
        arrayList.add("perf_shielder_SPTM");
    }

    public SPTMCloudConfig(String str) {
        super(str);
        this.TAG = PerfshielderCloudUpdate.TAG;
    }

    @Override // com.miui.powerkeeper.perfshielder.PerfCloudConfig
    public void parseResult(Context context, String str, String str2, String str3) {
        if (str2.equals("perf_shielder_SPTM")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                Settings.System.putStringForUser(context.getContentResolver(), PerfShielderConstants.SPTM_ENABLE, jSONObject.optString(PerfShielderConstants.SPTM_ENABLE, "true"), -2);
                Settings.System.putStringForUser(context.getContentResolver(), PerfShielderConstants.SPTM_ANIMATION_ENABLE, jSONObject.optString(PerfShielderConstants.SPTM_ANIMATION_ENABLE, "true"), -2);
                Settings.System.putStringForUser(context.getContentResolver(), PerfShielderConstants.SPTM_PRELOAD, jSONObject.optString(PerfShielderConstants.SPTM_PRELOAD, "0"), -2);
                Settings.System.putStringForUser(context.getContentResolver(), PerfShielderConstants.SPTM_APP_LIST, jSONObject.optString(PerfShielderConstants.SPTM_APP_LIST, ""), -2);
                Settings.System.putStringForUser(context.getContentResolver(), str2, str3, -2);
            } catch (Exception e2) {
                Log.d(this.TAG, "SPTM updateConfig " + e2.getMessage());
            }
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
