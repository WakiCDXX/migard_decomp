package com.miui.powerkeeper.perfengine;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.cloudcontrol.HttpUtils;
import com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Device;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.PowerLog;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PeCloudUpdate {
    public static boolean a(Context context) {
        return FileUtil.deleteFile(context.getFilesDir().getAbsolutePath() + "/perf_engine.config");
    }

    public static JSONObject b(b.a aVar, int i2) {
        if (aVar.s(i2)) {
            return aVar.l(i2);
        }
        return null;
    }

    public static String c(Context context) {
        return FileUtil.readFromFile(context.getFilesDir().getAbsolutePath() + "/perf_engine.config");
    }

    public static String d(Context context, b.a aVar, int i2) {
        JSONObject jSONObjectB = b(aVar, i2);
        return jSONObjectB == null ? c(context) : jSONObjectB.toString();
    }

    public static boolean e(Context context, String str) {
        try {
            String str2 = context.getFilesDir().getAbsolutePath() + "/perf_engine.config";
            String string = new JSONObject(str).toString(2);
            if (PowerLog.V) {
                PowerLog.v("PeCloudUpdate", "parseResult jString:" + string);
            }
            return FileUtil.writeToFile(str2, string);
        } catch (Exception e2) {
            Log.e("PeCloudUpdate", "parseResult" + e2.getMessage());
            return false;
        }
    }

    public static void f(Context context, boolean z2) {
        String strHttpGet = HttpUtils.httpGet(LocalUpdateUtils.getURL(context, Constants.KEY_MD5_URL, Constants.CLOUD_NEW_SERVER) + Constants.CLOUD_NEW_SERVER_UUID + LocalUpdateUtils.getUUID(context) + Constants.CLOUD_NEW_SERVER_UPID + LocalUpdateUtils.getUPID(context, Constants.CLOUD_PERF_ENGINE_UPID, z2) + Constants.CLOUD_NEW_SERVER_REGION + "perf_engine" + Constants.CLOUD_NEW_SERVER_BUCKET + "perf_engine" + Constants.CLOUD_NEW_SERVER_TYPE + Device.getInstance().getType(context), Constants.CLOUD_PERF_ENGINE_SID);
        if (PowerLog.V) {
            PowerLog.v("PeCloudUpdate", "result:" + strHttpGet);
        }
        if (strHttpGet == null || strHttpGet.isEmpty()) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(strHttpGet);
            int i2 = jSONObject.getInt(Constants.CLOUD_NEW_SERVER_KEY_CODE);
            switch (i2) {
                case 200:
                    if (PowerLog.V) {
                        PowerLog.v("PeCloudUpdate", "hit profile");
                    }
                    break;
                case 201:
                    if (PowerLog.V) {
                        PowerLog.v("PeCloudUpdate", "none profile match the type");
                    }
                    break;
                case 202:
                    if (PowerLog.V) {
                        PowerLog.v("PeCloudUpdate", "gray test, not hit");
                    }
                    break;
                case 203:
                    if (PowerLog.V) {
                        PowerLog.v("PeCloudUpdate", "no update profile");
                    }
                    break;
                default:
                    if (PowerLog.V) {
                        PowerLog.v("PeCloudUpdate", "unknown");
                    }
                    break;
            }
            if (i2 != 200) {
                Log.i("PeCloudUpdate", "no new clould configuration, returnCode = " + i2);
                return;
            }
            if (e(context, jSONObject.getString("profile"))) {
                String string = jSONObject.getString(Constants.CLOUD_NEW_SERVER_KEY_UPID);
                LocalUpdateUtils.storeUPID(context, Constants.CLOUD_PERF_ENGINE_UPID, string);
                SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_PERF_ENGINE_UPID, string);
                Log.i("PeCloudUpdate", "update cloud config done, update upid:" + string);
                PeCloudManager.e(context).i(1);
            }
        } catch (JSONException e2) {
            Log.e("PeCloudUpdate", "updateConfig" + e2.getMessage());
        } catch (Exception e3) {
            Log.e("PeCloudUpdate", "updateConfig" + e3.getMessage());
        }
    }
}
