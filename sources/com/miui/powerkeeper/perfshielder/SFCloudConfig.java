package com.miui.powerkeeper.perfshielder;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class SFCloudConfig extends PerfCloudConfig {
    private static final String TAG = "SFCloudConfig";
    private static List<String> sMoudleName;

    static {
        ArrayList arrayList = new ArrayList();
        sMoudleName = arrayList;
        arrayList.add("perf_shielder_SF");
    }

    public SFCloudConfig(String str) {
        super(str);
    }

    @Override // com.miui.powerkeeper.perfshielder.PerfCloudConfig
    public void parseResult(Context context, String str, String str2, String str3) {
        if (str2.equals("perf_shielder_SF")) {
            try {
                Settings.System.putStringForUser(context.getContentResolver(), PerfShielderConstants.SF_PARAMETER, new JSONObject(str).optString(PerfShielderConstants.SF_PARAMETER, "1"), -2);
                Settings.System.putStringForUser(context.getContentResolver(), str2, str3, -2);
            } catch (Exception e2) {
                Log.d(TAG, "ScrollFriction updateConfig " + e2.getMessage());
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
