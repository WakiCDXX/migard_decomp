package com.miui.powerkeeper.logsystem.cloud;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.logsystem.config.Scene;
import com.miui.powerkeeper.logsystem.utils.GsonUtils;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class LSCloudConfigure {
    public static final String TAG = "LS-LSCloudConfigure";
    Context mContext;
    private boolean mEnabled = false;
    private float mThreshold = 1.0f;
    private ArrayList<Scene> mScenes = new ArrayList<>();
    private int mLogType = 0;

    public LSCloudConfigure(Context context) {
        this.mContext = context;
    }

    public int getLogType() {
        return this.mLogType;
    }

    public ArrayList<Scene> getScenes() {
        return this.mScenes;
    }

    public float getThreshold() {
        return this.mThreshold;
    }

    public boolean isFeatureEnabled() {
        return this.mEnabled;
    }

    public void updateCloudConfigure() {
        this.mEnabled = SimpleSettings.Misc.getBoolean(this.mContext, "log_system_config", false);
        String functionParameters = CloudFunctionConfig.getFunctionParameters(this.mContext, "log_system_config", "");
        if (!LSUtils.isEmptyString(functionParameters)) {
            try {
                JSONObject jSONObject = new JSONObject(functionParameters);
                this.mLogType = jSONObject.getInt("logtype");
                this.mThreshold = (float) jSONObject.getDouble("threshold");
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        String functionParameters2 = CloudFunctionConfig.getFunctionParameters2(this.mContext, "log_system_config", "");
        if (!LSUtils.isEmptyString(functionParameters2)) {
            try {
                this.mScenes.clear();
                JSONArray jSONArray = new JSONArray(functionParameters2);
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    this.mScenes.add((Scene) GsonUtils.GsonToBeanSafety(jSONArray.getJSONObject(i2).toString(), Scene.class));
                }
            } catch (JSONException e3) {
                e3.printStackTrace();
            }
        }
        Log.d(TAG, "CloudData update: param1=" + functionParameters + ", param2 = " + functionParameters2);
    }
}
