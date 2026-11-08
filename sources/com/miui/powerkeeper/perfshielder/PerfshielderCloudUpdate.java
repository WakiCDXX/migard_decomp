package com.miui.powerkeeper.perfshielder;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.cloudcontrol.HttpUtils;
import com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils;
import com.miui.powerkeeper.utils.Device;
import com.miui.powerkeeper.utils.PowerLog;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PerfshielderCloudUpdate {
    public static final String TAG = "PerfShielderCloudUpdate";
    private static List<PerfCloudConfig> perfCloudList;

    static {
        ArrayList arrayList = new ArrayList();
        perfCloudList = arrayList;
        arrayList.add(new SPTMCloudConfig("perf_shielder"));
        perfCloudList.add(new RTCloudConfig(PerfShielderConstants.RT_BUCKET_NAME));
        perfCloudList.add(new BackLightCloudConfig(PerfShielderConstants.BACKLIGHT_BUCKET_NAME));
        perfCloudList.add(new SFCloudConfig(PerfShielderConstants.SF_BUCKET_NAME));
        perfCloudList.add(new ProcessCloudConfig(PerfShielderConstants.PROC_BUCKET_NAME));
        perfCloudList.add(new PowerCloudConfig(PerfShielderConstants.POWER_BUCKET_NAME));
    }

    public static void startCloudUpdate(Context context, boolean z2) {
        for (PerfCloudConfig perfCloudConfig : perfCloudList) {
            String strHttpGet = HttpUtils.httpGet(Constants.CLOUD_NEW_SERVER + Constants.CLOUD_NEW_SERVER_UUID + LocalUpdateUtils.getUUID(context) + Constants.CLOUD_NEW_SERVER_UPID + LocalUpdateUtils.getUPID(context, Constants.CLOUD_PERF_SHIELDER_UPID, z2) + Constants.CLOUD_NEW_SERVER_REGION + "perf_shielder" + Constants.CLOUD_NEW_SERVER_BUCKET + perfCloudConfig.mBucketName + Constants.CLOUD_NEW_SERVER_TYPE + Device.getInstance().getType(context), Constants.CLOUD_PERF_SHIELDER_SID);
            if (PowerLog.V) {
                PowerLog.v(TAG, "result:" + strHttpGet);
            }
            if (strHttpGet == null || strHttpGet.isEmpty()) {
                Log.d(TAG, perfCloudConfig.mBucketName + " startUpdate result is empty");
            } else {
                try {
                    JSONObject jSONObject = new JSONObject(strHttpGet);
                    int i2 = jSONObject.getInt(Constants.CLOUD_NEW_SERVER_KEY_CODE);
                    switch (i2) {
                        case 200:
                            Log.d(TAG, "hit profile");
                            break;
                        case 201:
                            Log.d(TAG, "none profile match the type");
                            break;
                        case 202:
                            Log.d(TAG, "gray test, not hit");
                            break;
                        case 203:
                            Log.d(TAG, "no update profile");
                            break;
                        default:
                            Log.d(TAG, "unknown");
                            break;
                    }
                    if (i2 != 200) {
                        Log.i(TAG, perfCloudConfig.mBucketName + " no new clould configuration, returnCode = " + i2);
                    } else {
                        perfCloudConfig.updateResult(context, jSONObject.getString(Constants.CLOUD_NEW_SERVER_KEY_UPID), jSONObject.getString("profile"));
                    }
                } catch (JSONException e2) {
                    Log.e(TAG, "updateConfig" + e2.getMessage());
                } catch (Exception e3) {
                    Log.e(TAG, "updateConfig" + e3.getMessage());
                }
            }
        }
    }
}
