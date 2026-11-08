package com.miui.powerkeeper.cloudcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Base64;
import android.util.Log;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.ui.CloudInfoActivity;
import com.miui.powerkeeper.utils.AssetUtils;
import com.miui.powerkeeper.utils.DebugUtils;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import com.xiaomi.channel.commonutils.misc.DebugSwitchConstants;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CloudUpdateReceiver extends BroadcastReceiver {
    private static final String LOCAL_CONFIG_FILE = "local.config";
    private static final String LOCAL_CONFIG_MD5 = "local_config_md5";
    private static final String LOCAL_CONFIG_MD5_CHECK_FILE = "local.config.md5";
    private static final String OVERLAY_INTERNATIONAL_CONFIG_FILE = "overlay/international/local.config";
    public static final String TAG = CloudUpdateReceiver.class.getSimpleName();
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String OVERLAY_LOCAL_CONFIG_FILE = "overlay/" + Build.DEVICE + "/local.config";
    private static final String[] OVERLAY_NEEDED_INTERNATIONAL_BUILD_DEVICES = {"rubypro", "marblein"};

    private void checkBatteryLifeInfoOfLocalConfig(Context context, String str) {
        String str2 = TAG;
        Log.d(str2, "checkBatteryLifeInfoOfLocalConfig");
        String str3 = new String(Base64.decode(str, 0));
        String[] strArr = {null, null, null};
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            String strFromAssetFile = AssetUtils.getStrFromAssetFile(context, OVERLAY_INTERNATIONAL_CONFIG_FILE);
            if (strFromAssetFile != null && !strFromAssetFile.isEmpty()) {
                strArr[0] = new String(Base64.decode(strFromAssetFile, 0));
            }
            String deviceInternationalOverlay = getDeviceInternationalOverlay(context);
            if (deviceInternationalOverlay != null && !deviceInternationalOverlay.isEmpty()) {
                Log.d(str2, "parseLocalConfigIfNeeded use international device overlay");
                strArr[2] = new String(Base64.decode(deviceInternationalOverlay, 0));
            }
        }
        String strFromAssetFile2 = AssetUtils.getStrFromAssetFile(context, OVERLAY_LOCAL_CONFIG_FILE);
        if (strFromAssetFile2 != null && !strFromAssetFile2.isEmpty()) {
            Log.d(str2, "parseLocalConfigIfNeeded use device overlay");
            strArr[1] = new String(Base64.decode(strFromAssetFile2, 0));
        }
        try {
            JSONObject jSONObject = new JSONObject(str3);
            for (int i2 = 0; i2 < 3; i2++) {
                String str4 = strArr[i2];
                if (str4 != null && !str4.isEmpty()) {
                    Log.d(TAG, "parseLocalConfig overlayStr=" + str4);
                    JSONObject jSONObject2 = new JSONObject(str4);
                    Iterator<String> itKeys = jSONObject2.keys();
                    while (itKeys.hasNext()) {
                        String next = itKeys.next();
                        Object obj = jSONObject2.get(next);
                        Log.d(TAG, "parseLocalConfig overlay key=" + next + " value=" + obj);
                        jSONObject.put(next, obj);
                    }
                }
            }
            e.e.i(LogType.LOG_TYPE_F_SAMPLING_CURRENT).a(jSONObject);
        } catch (NullPointerException e2) {
            Log.e(TAG, "checkBatteryLifeInfoOfLocalConfig-updateCloud", e2);
        } catch (JSONException e3) {
            Log.e(TAG, "checkBatteryLifeInfoOfLocalConfig-parseResult", e3);
        } catch (Exception e4) {
            Log.e(TAG, "checkBatteryLifeInfoOfLocalConfig", e4);
        }
    }

    private void checkSpecialInfoOfLocalConfig(Context context, String str) {
        List<String> dozeWhiteListApps = GlobalFeatureConfigureHelper.getDozeWhiteListApps(context);
        if (dozeWhiteListApps == null || dozeWhiteListApps.size() >= 15) {
            return;
        }
        CloudUpdateHideMode.parseResultForGlobalFeature(context, new String(Base64.decode(str, 0)), LocalUpdateUtils.DOZE_WHITELIST_APPS);
    }

    private String getDeviceInternationalOverlay(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("overlay/");
        String str = Build.DEVICE;
        sb.append(str);
        sb.append("_intl/local.config");
        String string = sb.toString();
        String strFromAssetFile = AssetUtils.getStrFromAssetFile(context, string);
        if (!Arrays.asList(OVERLAY_NEEDED_INTERNATIONAL_BUILD_DEVICES).contains(str)) {
            int length = str.length();
            if (strFromAssetFile == null && length > 2 && str.endsWith("in")) {
                string = "overlay/" + str.substring(0, length - 2) + "_intl/local.config";
                strFromAssetFile = AssetUtils.getStrFromAssetFile(context, string);
            }
            if (strFromAssetFile == null && length > 3 && str.endsWith("pro")) {
                string = "overlay/" + str.substring(0, length - 3) + "_intl/local.config";
                strFromAssetFile = AssetUtils.getStrFromAssetFile(context, string);
            }
            if (strFromAssetFile == null && length > 5 && (str.endsWith("inpro") || str.endsWith("proin"))) {
                string = "overlay/" + str.substring(0, length - 5) + "_intl/local.config";
                strFromAssetFile = AssetUtils.getStrFromAssetFile(context, string);
            }
        }
        if (DEBUG) {
            Log.d(TAG, "getDeviceInternationalOverlay path=" + string);
        }
        return strFromAssetFile;
    }

    private static long getFirstPowerOnTime(Context context) {
        return SharedPrefUtil.getInstance(context).getLongValue(Constants.FIRST_POWER_ON, 0L);
    }

    public static boolean isCtaAllowed(Context context) {
        return isCtaAllowed(context, true);
    }

    private static boolean isFirstPowerOn(Context context) {
        return SharedPrefUtil.getInstance(context).getBooleanValue(Constants.IS_FIRST_POWER_ON, true);
    }

    private void parseLocalConfigIfNeeded(Context context) {
        String string = SimpleSettings.Misc.getString(context, LOCAL_CONFIG_MD5, "");
        String strFromAssetFile = AssetUtils.getStrFromAssetFile(context, LOCAL_CONFIG_MD5_CHECK_FILE);
        String str = TAG;
        Log.d(str, "oldMd5=\"" + string + "\", newMd5=\"" + strFromAssetFile + "\"");
        if (strFromAssetFile != null && strFromAssetFile.equals(string)) {
            if (DEBUG) {
                Log.d(str, "parseLocalConfigIfNeeded md5 is same");
                return;
            }
            return;
        }
        String strFromAssetFile2 = AssetUtils.getStrFromAssetFile(context, "local.config");
        if (!SimpleSettings.Misc.getString(context, Constants.HIDEMODE_UPDATE_TIME, "").isEmpty()) {
            if (DEBUG) {
                Log.d(str, "cloud updated, check special info in local");
            }
            checkSpecialInfoOfLocalConfig(context, strFromAssetFile2);
            checkBatteryLifeInfoOfLocalConfig(context, strFromAssetFile2);
            return;
        }
        String str2 = new String(Base64.decode(strFromAssetFile2, 0));
        String[] strArr = {null, null, null};
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            String strFromAssetFile3 = AssetUtils.getStrFromAssetFile(context, OVERLAY_INTERNATIONAL_CONFIG_FILE);
            if (strFromAssetFile3 != null && !strFromAssetFile3.isEmpty()) {
                strArr[0] = new String(Base64.decode(strFromAssetFile3, 0));
            }
            String deviceInternationalOverlay = getDeviceInternationalOverlay(context);
            if (deviceInternationalOverlay != null && !deviceInternationalOverlay.isEmpty()) {
                if (DEBUG) {
                    Log.d(str, "parseLocalConfigIfNeeded use international device overlay");
                }
                strArr[2] = new String(Base64.decode(deviceInternationalOverlay, 0));
            }
        }
        String strFromAssetFile4 = AssetUtils.getStrFromAssetFile(context, OVERLAY_LOCAL_CONFIG_FILE);
        if (strFromAssetFile4 != null && !strFromAssetFile4.isEmpty()) {
            if (DEBUG) {
                Log.d(str, "parseLocalConfigIfNeeded use device overlay");
            }
            strArr[1] = new String(Base64.decode(strFromAssetFile4, 0));
        }
        CloudUpdateHideMode.parseResult(context, str2, strArr);
        CloudUpdateHideMode.parseLocalConfig(context, str2, strArr);
        if (!b.a.p().s(b.b.D)) {
            CloudUpdateHideMode.parseApplistResult(context, str2, true);
        }
        SimpleSettings.Misc.putString(context, LOCAL_CONFIG_MD5, strFromAssetFile);
    }

    private void saveFirstPowerOnTime(Context context) {
        long longValue = SharedPrefUtil.getInstance(context).getLongValue(Constants.FIRST_POWER_ON, 0L);
        if (longValue == 0 || longValue > System.currentTimeMillis()) {
            SharedPrefUtil.getInstance(context).save(Constants.FIRST_POWER_ON, System.currentTimeMillis());
        }
    }

    private void sendUpdateIntentToService(final Context context) {
        if (DEBUG) {
            Log.i(TAG, "sendUpdateIntentToService--> ");
        }
        new Thread(new Runnable() { // from class: com.miui.powerkeeper.cloudcontrol.CloudUpdateReceiver.1
            @Override // java.lang.Runnable
            public void run() {
                LocalUpdateUtils.startCloudSyncData(context, true);
            }
        }).start();
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (UserHandle.myUserId() != 0) {
            if (DEBUG) {
                Log.i(TAG, "onReceive, userId = " + UserHandle.myUserId() + ", action = " + intent.getAction());
            }
            context.sendBroadcastAsUser((Intent) intent.clone(), UserHandle.OWNER);
            return;
        }
        saveFirstPowerOnTime(context);
        String action = intent.getAction();
        if (DEBUG) {
            Log.i(TAG, "onReceive: " + action);
        }
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            PowerLog.recordKey("cloud receive BOOT_COMPLETED");
            CloudUpdateJobService.scheduleCloudUpdateJob(context);
            return;
        }
        if (Constants.UPDATE_PROFILE.equals(action)) {
            PowerLog.resetDebugSwitch();
            String stringExtra = intent.getStringExtra(SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT);
            if (stringExtra != null) {
                if (stringExtra.equals(DebugSwitchConstants.SETTINGS_VALUE_HOST_STAGING)) {
                    SimpleSettings.Misc.putString(context, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, Constants.CLOUD_NEW_SERVER_STAGING);
                } else if (stringExtra.equals("china")) {
                    SimpleSettings.Misc.putString(context, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, Constants.CLOUD_NEW_SERVER_CHINA);
                } else if (stringExtra.equals("international")) {
                    SimpleSettings.Misc.putString(context, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, Constants.CLOUD_NEW_SERVER_INTERNATIONAL);
                } else if (stringExtra.equals("russion")) {
                    SimpleSettings.Misc.putString(context, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, Constants.CLOUD_NEW_SERVER_RUSSION);
                }
            }
            sendUpdateIntentToService(context);
            return;
        }
        if (!"android.provider.Telephony.SECRET_CODE".equals(action)) {
            if (Constants.INIT_PROFILE.equals(action)) {
                parseLocalConfigIfNeeded(context);
            }
        } else if ("76937".equals(intent.getData().getHost())) {
            if (miui.os.Build.IS_DEBUGGABLE || !miui.os.Build.IS_STABLE_VERSION) {
                Log.i(TAG, "onReceive----secretcode--->");
                Intent intent2 = new Intent(context, (Class<?>) CloudInfoActivity.class);
                intent2.setFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
                intent2.putExtra(DebugUtils.CALLER, "contacts");
                context.startActivity(intent2);
            }
        }
    }

    public static boolean isCtaAllowed(Context context, boolean z2) {
        if (SystemProperties.getBoolean("persist.sys.sc_allow_conn", false)) {
            return true;
        }
        if (!z2) {
            return false;
        }
        long firstPowerOnTime = getFirstPowerOnTime(context);
        if (firstPowerOnTime > 0) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (firstPowerOnTime > jCurrentTimeMillis) {
                SharedPrefUtil.getInstance(context).save(Constants.FIRST_POWER_ON, System.currentTimeMillis());
            }
            if (isFirstPowerOn(context)) {
                if (SystemClock.elapsedRealtime() > 259200000) {
                    return true;
                }
            } else if (jCurrentTimeMillis - firstPowerOnTime > 259200000) {
                return true;
            }
        }
        return false;
    }
}
