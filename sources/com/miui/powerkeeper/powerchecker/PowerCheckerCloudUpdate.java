package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.cloudcontrol.HttpUtils;
import com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Device;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.Utils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerCloudUpdate {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static String TAG = "PowerChecker.ClUpdate";

    /* JADX WARN: Code duplicated, block: B:29:0x010e  */
    public static boolean startCloudUpdate(Context context, boolean z2) {
        boolean z3 = true;
        if (!LocalUpdateUtils.useNewCloudControl()) {
            StringBuilder sb = new StringBuilder();
            String str = Constants.CLOUD_ABNORMAL_CONSUME_CONTENT_URL;
            sb.append(str);
            sb.append(Device.getInstance().getType(context));
            sb.append("/md5");
            String string = sb.toString();
            String strHttpGet = HttpUtils.httpGet(string, Constants.CLOUD_POWER_SID);
            if (strHttpGet == null || strHttpGet.isEmpty()) {
                Log.d(TAG, "get no data for md5 : " + string);
                return false;
            }
            try {
                String string2 = new JSONObject(strHttpGet).getString("data");
                if (SimpleSettings.Misc.getString(context, SimpleSettingKeys.KEY_ABNORMAL_CONSUME_LAST_DATA_MD5, "").equalsIgnoreCase(string2) && !z2) {
                    Log.i(TAG, "No need to get remote data: " + string2);
                    return false;
                }
                String str2 = str + Device.getInstance().getType(context);
                String strHttpGet2 = HttpUtils.httpGet(str2, Constants.CLOUD_POWER_SID);
                if (strHttpGet2 != null && !strHttpGet2.isEmpty()) {
                    if (DEBUG) {
                        Log.i(TAG, "Receive remote data succeed = " + strHttpGet2);
                    }
                    if (Utils.parseRemoteResultToFile(context.getFilesDir().getAbsolutePath() + "/" + PowerCheckerCloudConfigure.PROFILE_NAME, strHttpGet2)) {
                        SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_ABNORMAL_CONSUME_LAST_DATA_MD5, string2);
                    } else {
                        z3 = false;
                    }
                }
                Log.d(TAG, "get no data for content : " + str2);
                return false;
            } catch (Exception e2) {
                Log.e(TAG, "startCloudUpdate", e2);
                return false;
            }
        }
        String str3 = Constants.CLOUD_NEW_SERVER + Constants.CLOUD_NEW_SERVER_UUID + LocalUpdateUtils.getUUID(context) + Constants.CLOUD_NEW_SERVER_UPID + LocalUpdateUtils.getUPID(context, Constants.CLOUD_POWER_DETECTION_UPID, z2) + Constants.CLOUD_NEW_SERVER_REGION + "power_update" + Constants.CLOUD_NEW_SERVER_BUCKET + Constants.BUCKET_NAME_POWER_DETECTION + Constants.CLOUD_NEW_SERVER_TYPE + Device.getInstance().getType(context);
        String strHttpGet3 = HttpUtils.httpGet(str3, Constants.CLOUD_POWER_NEW_SID);
        if (strHttpGet3 == null || strHttpGet3.isEmpty()) {
            Log.d(TAG, "get no data for md5 : " + str3);
            return false;
        }
        try {
            JSONObject jSONObject = new JSONObject(strHttpGet3);
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
                Log.i(TAG, "no new clould configuration, returnCode = " + i2);
                return false;
            }
            if (DEBUG) {
                Log.i(TAG, "Receive remote data succeed = " + strHttpGet3);
            }
            if (FileUtil.writeToFile(context.getFilesDir().getAbsolutePath() + "/" + PowerCheckerCloudConfigure.PROFILE_NAME, jSONObject.getString("profile"))) {
                String string3 = jSONObject.getString(Constants.CLOUD_NEW_SERVER_KEY_UPID);
                LocalUpdateUtils.storeUPID(context, Constants.CLOUD_POWER_DETECTION_UPID, string3);
                SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_ABNORMAL_CONSUME_LAST_DATA_MD5, string3);
            } else {
                z3 = false;
            }
        } catch (Exception e3) {
            Log.e(TAG, "startCloudUpdate", e3);
            return false;
        }
        return z3;
    }
}
