package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.PushConstants;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class HWPushHelper {
    private static final String LAST_CONNECT_TIME = "last_connect_time";
    private static final String LAST_GET_TOKEN_TIME = "last_get_token_time";
    private static boolean isFailed = false;

    public static void convertMessage(Intent intent) {
        AssemblePushHelper.convertMessage(intent);
    }

    public static boolean hasNetwork(Context context) {
        return AssemblePushHelper.hasNetwork(context);
    }

    public static boolean isHmsTokenSynced(Context context) {
        String assemblePushToken = AssemblePushHelper.getAssemblePushToken(context, AssemblePush.ASSEMBLE_PUSH_HUAWEI, false);
        String syncStatus = OperatePushHelper.getInstance(context).getSyncStatus(RetryType.UPLOAD_HUAWEI_TOKEN);
        return (TextUtils.isEmpty(assemblePushToken) || TextUtils.isEmpty(syncStatus) || !OperatePushHelper.SYNCED.equals(syncStatus)) ? false : true;
    }

    public static boolean isUserOpenHmsPush(Context context) {
        return MiPushClient.getOpenHmsPush(context);
    }

    public static boolean needConnect() {
        return isFailed;
    }

    public static void notifyHmsNotificationMessageClicked(Context context, String str) {
        String string = "";
        if (!TextUtils.isEmpty(str)) {
            try {
                JSONArray jSONArray = new JSONArray(str);
                if (jSONArray.length() > 0) {
                    for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                        JSONObject jSONObject = jSONArray.getJSONObject(i2);
                        if (jSONObject.has("pushMsg")) {
                            string = jSONObject.getString("pushMsg");
                            break;
                        }
                    }
                }
            } catch (Exception e2) {
                MyLog.e(e2.toString());
            }
        }
        PushMessageReceiver miPushReceiver = AssemblePushHelper.getMiPushReceiver(context);
        if (miPushReceiver != null) {
            MiPushMessage miPushMessage = AssemblePushHelper.parseMiPushMessage(string);
            if (miPushMessage.getExtra().containsKey(PushConstants.EXTRA_PARAM_NOTIFY_EFFECT)) {
                return;
            }
            miPushReceiver.onNotificationMessageClicked(context, miPushMessage);
        }
    }

    public static void notifyHmsPassThoughMessageArrived(Context context, String str) {
        String string = "";
        try {
            if (!TextUtils.isEmpty(str)) {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("content")) {
                    string = jSONObject.getString("content");
                }
            }
        } catch (Exception e2) {
            MyLog.e(e2.toString());
        }
        PushMessageReceiver miPushReceiver = AssemblePushHelper.getMiPushReceiver(context);
        if (miPushReceiver != null) {
            miPushReceiver.onReceivePassThroughMessage(context, AssemblePushHelper.parseMiPushMessage(string));
        }
    }

    public static void registerHuaWeiAssemblePush(Context context) {
        AbstractPushManager manager = AssemblePushCollectionsManager.getInstance(context).getManager(AssemblePush.ASSEMBLE_PUSH_HUAWEI);
        if (manager != null) {
            manager.register();
        }
    }

    public static void reportError(String str, int i2) {
        AssemblePushHelper.reportError(str, i2);
    }

    public static synchronized void setConnectTime(Context context) {
        context.getSharedPreferences("mipush_extra", 0).edit().putLong(LAST_CONNECT_TIME, System.currentTimeMillis()).commit();
    }

    public static synchronized void setGetTokenTime(Context context) {
        context.getSharedPreferences("mipush_extra", 0).edit().putLong(LAST_GET_TOKEN_TIME, System.currentTimeMillis()).commit();
    }

    public static void setNeedConnect(boolean z2) {
        isFailed = z2;
    }

    public static synchronized boolean shouldGetToken(Context context) {
        return Math.abs(System.currentTimeMillis() - context.getSharedPreferences("mipush_extra", 0).getLong(LAST_GET_TOKEN_TIME, -1L)) > 172800000;
    }

    public static synchronized boolean shouldTryConnect(Context context) {
        return Math.abs(System.currentTimeMillis() - context.getSharedPreferences("mipush_extra", 0).getLong(LAST_CONNECT_TIME, -1L)) > PluginConstants.TTS_REMOVE_BEAN_TIME;
    }

    public static void uploadToken(Context context, String str) {
        AssemblePushHelper.uploadToken(context, AssemblePush.ASSEMBLE_PUSH_HUAWEI, str);
    }
}
