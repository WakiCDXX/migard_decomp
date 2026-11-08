package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.SharedPrefsCompat;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.channel.commonutils.string.MD5;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class AssemblePushHelper {
    private static final String COS_PUSH_ERROR = "cos_push_error";
    private static final String COS_PUSH_TOKEN = "cos_push_token";
    private static final String FCM_PUSH_ERROR = "fcm_push_error";
    private static final String FCM_PUSH_TOKEN = "fcm_push_token_v2";
    private static final String FTOS_PUSH_ERROR = "ftos_push_error";
    private static final String FTOS_PUSH_TOKEN = "ftos_push_token";
    protected static final String HMS_NOTIFICATION_CONTENT = "pushMsg";
    protected static final String HMS_PASS_MESSAGE_CONTENT = "content";
    private static final String HMS_PUSH_ERROR = "hms_push_error";
    private static final String HMS_PUSH_TOKEN = "hms_push_token";
    private static final String KEY_ALIAS = "alias";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DESC = "description";
    private static final String KEY_EXTRA = "extra";
    private static final String KEY_MESSAGE_ID = "messageId";
    private static final String KEY_MESSAGE_TYPE = "messageType";
    private static final String KEY_NOTIFIED = "isNotified";
    private static final String KEY_NOTIFY_ID = "notifyId";
    private static final String KEY_NOTIFY_TYPE = "notifyType";
    private static final String KEY_PASS_THROUGH = "passThrough";
    public static final String KEY_SYNCING_TOKEN = "syncingToken";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TOPIC = "topic";
    private static final String KEY_USER_ACCOUNT = "user_account";
    private static final String LAST_CHECK_TOKEN = "last_check_token";
    private static final int UPLOAD_TOKEN_REASON_FIRST_UPLOAD = 1;
    private static final int UPLOAD_TOKEN_REASON_NOT_UPLOAD = 0;
    private static final int UPLOAD_TOKEN_REASON_REGID_CHANED = 3;
    private static final int UPLOAD_TOKEN_REASON_TOKEN_CHANED = 2;
    private static final int UPLOAD_TOKEN_REASON_VERSION_CHANED = 4;

    /* JADX INFO: renamed from: com.xiaomi.mipush.sdk.AssemblePushHelper$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush;

        static {
            int[] iArr = new int[AssemblePush.values().length];
            $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush = iArr;
            try {
                iArr[AssemblePush.ASSEMBLE_PUSH_HUAWEI.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[AssemblePush.ASSEMBLE_PUSH_FCM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[AssemblePush.ASSEMBLE_PUSH_COS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[AssemblePush.ASSEMBLE_PUSH_FTOS.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public static byte[] checkAndGetAssemblePushKeys(Context context, XmPushActionContainer xmPushActionContainer, AssemblePush assemblePush) {
        if (checkAssemblePushMessage(xmPushActionContainer, assemblePush)) {
            return MD5.getMd5Digest(getToken(context, assemblePush));
        }
        return null;
    }

    public static boolean checkAssemblePushMessage(XmPushActionContainer xmPushActionContainer, AssemblePush assemblePush) {
        if (xmPushActionContainer == null || xmPushActionContainer.getMetaInfo() == null || xmPushActionContainer.getMetaInfo().getExtra() == null) {
            return false;
        }
        return (assemblePush == AssemblePush.ASSEMBLE_PUSH_FCM ? "FCM" : "").equalsIgnoreCase(xmPushActionContainer.getMetaInfo().getExtra().get("assemble_push_type"));
    }

    static void checkAssemblePushStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mipush_extra", 0);
        String tokenKey = getTokenKey(AssemblePush.ASSEMBLE_PUSH_HUAWEI);
        String tokenKey2 = getTokenKey(AssemblePush.ASSEMBLE_PUSH_FCM);
        if (TextUtils.isEmpty(sharedPreferences.getString(tokenKey, "")) || !TextUtils.isEmpty(sharedPreferences.getString(tokenKey2, ""))) {
            return;
        }
        PushServiceClient.getInstance(context).send3rdPushHint(2, tokenKey);
    }

    public static void clearToken(Context context, AssemblePush assemblePush) {
        String tokenKey = getTokenKey(assemblePush);
        if (TextUtils.isEmpty(tokenKey)) {
            return;
        }
        SharedPrefsCompat.apply(context.getSharedPreferences("mipush_extra", 0).edit().putString(tokenKey, ""));
    }

    public static void convertMessage(Intent intent) {
        Bundle extras;
        if (intent == null || (extras = intent.getExtras()) == null || !extras.containsKey(HMS_NOTIFICATION_CONTENT)) {
            return;
        }
        intent.putExtra(PushMessageHelper.KEY_MESSAGE, parseMiPushMessage(extras.getString(HMS_NOTIFICATION_CONTENT)));
    }

    public static HashMap<String, String> getAssemblePushExtra(Context context, AssemblePush assemblePush) {
        HashMap<String, String> map = new HashMap<>();
        int i2 = AnonymousClass2.$SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[assemblePush.ordinal()];
        String string = null;
        ApplicationInfo applicationInfo = null;
        if (i2 == 1) {
            try {
                applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            } catch (Exception e2) {
                MyLog.e(e2.toString());
            }
            string = new ToolUtils.StringMap(":", Constants.WAVE_SEPARATOR).put(Constants.PHONE_BRAND, PhoneBrand.HUAWEI.name()).put("token", getAssemblePushToken(context, assemblePush, true)).put("package_name", context.getPackageName()).put("app_id", Integer.valueOf(applicationInfo != null ? applicationInfo.metaData.getInt(Constants.HUAWEI_HMS_CLIENT_APPID) : -1)).toString();
        } else if (i2 == 2) {
            ToolUtils.StringMap stringMapPut = new ToolUtils.StringMap(":", Constants.WAVE_SEPARATOR).put(Constants.PHONE_BRAND, PhoneBrand.FCM.name()).put("token", getAssemblePushToken(context, assemblePush, false)).put("package_name", context.getPackageName());
            int assembleVersionCode = getAssembleVersionCode();
            if (assembleVersionCode != 0) {
                stringMapPut.put("version", Integer.valueOf(assembleVersionCode));
            } else {
                stringMapPut.put("version", Integer.valueOf(PushConstants.PUSH_VERSION_CODE));
            }
            string = stringMapPut.toString();
        } else if (i2 == 3) {
            string = new ToolUtils.StringMap(":", Constants.WAVE_SEPARATOR).put(Constants.PHONE_BRAND, PhoneBrand.OPPO.name()).put("token", getAssemblePushToken(context, assemblePush, true)).put("package_name", context.getPackageName()).toString();
        } else if (i2 == 4) {
            ToolUtils.StringMap stringMapPut2 = new ToolUtils.StringMap(":", Constants.WAVE_SEPARATOR).put(Constants.PHONE_BRAND, PhoneBrand.VIVO.name()).put("token", getAssemblePushToken(context, assemblePush, true)).put("package_name", context.getPackageName());
            int assembleVersionCode2 = getAssembleVersionCode();
            if (assembleVersionCode2 != 0) {
                stringMapPut2.put("version", Integer.valueOf(assembleVersionCode2));
            }
            string = stringMapPut2.toString();
        }
        map.put(Constants.ASSEMBLE_PUSH_REG_INFO, string);
        return map;
    }

    protected static synchronized String getAssemblePushToken(Context context, AssemblePush assemblePush, boolean z2) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mipush_extra", 0);
        if (z2) {
            String string = sharedPreferences.getString(KEY_SYNCING_TOKEN, "");
            if (!TextUtils.isEmpty(string)) {
                return string;
            }
        }
        String tokenKey = getTokenKey(assemblePush);
        if (TextUtils.isEmpty(tokenKey)) {
            return "";
        }
        return sharedPreferences.getString(tokenKey, "");
    }

    public static int getAssembleVersionCode() {
        Integer num = (Integer) JavaCalls.getStaticField("com.xiaomi.assemble.control.AssembleConstants", "ASSEMBLE_VERSION_CODE");
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    protected static PushMessageReceiver getMiPushReceiver(Context context) {
        ResolveInfo next;
        Intent intent = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
        intent.setPackage(context.getPackageName());
        try {
            List<ResolveInfo> listQueryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, 32);
            if (listQueryBroadcastReceivers != null) {
                Iterator<ResolveInfo> it = listQueryBroadcastReceivers.iterator();
                while (it.hasNext()) {
                    next = it.next();
                    ActivityInfo activityInfo = next.activityInfo;
                    if (activityInfo == null || !activityInfo.packageName.equals(context.getPackageName())) {
                    }
                }
                next = null;
            } else {
                next = null;
            }
            if (next != null) {
                return (PushMessageReceiver) SystemUtils.loadClass(context, next.activityInfo.name).newInstance();
            }
            return null;
        } catch (Exception e2) {
            MyLog.e(e2.toString());
            return null;
        }
    }

    public static String getSPErrorKey(AssemblePush assemblePush) {
        int i2 = AnonymousClass2.$SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[assemblePush.ordinal()];
        if (i2 == 1) {
            return HMS_PUSH_ERROR;
        }
        if (i2 == 2) {
            return FCM_PUSH_ERROR;
        }
        if (i2 == 3) {
            return COS_PUSH_ERROR;
        }
        if (i2 != 4) {
            return null;
        }
        return FTOS_PUSH_ERROR;
    }

    static String getToken(Context context, AssemblePush assemblePush) {
        return getAssemblePushToken(context, assemblePush, false);
    }

    public static String getTokenKey(AssemblePush assemblePush) {
        int i2 = AnonymousClass2.$SwitchMap$com$xiaomi$mipush$sdk$AssemblePush[assemblePush.ordinal()];
        if (i2 == 1) {
            return HMS_PUSH_TOKEN;
        }
        if (i2 == 2) {
            return FCM_PUSH_TOKEN;
        }
        if (i2 == 3) {
            return COS_PUSH_TOKEN;
        }
        if (i2 != 4) {
            return null;
        }
        return FTOS_PUSH_TOKEN;
    }

    public static String getTokenVersionKey(AssemblePush assemblePush) {
        return getTokenKey(assemblePush) + "_version";
    }

    public static boolean hasNetwork(Context context) {
        if (context == null) {
            return false;
        }
        return Network.hasNetwork(context);
    }

    public static boolean isOpenAssemblePushOnlineSwitch(Context context, AssemblePush assemblePush) {
        if (AssemblePushInfoHelper.getConfigKeyByType(assemblePush) != null) {
            return OnlineConfig.getInstance(context).getBooleanValue(AssemblePushInfoHelper.getConfigKeyByType(assemblePush).getValue(), true);
        }
        return false;
    }

    public static boolean isTypeCareAboutVersion(AssemblePush assemblePush) {
        return assemblePush == AssemblePush.ASSEMBLE_PUSH_FTOS || assemblePush == AssemblePush.ASSEMBLE_PUSH_FCM;
    }

    public static MiPushMessage parseMiPushMessage(String str) {
        MiPushMessage miPushMessage = new MiPushMessage();
        if (!TextUtils.isEmpty(str)) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("messageId")) {
                    miPushMessage.setMessageId(jSONObject.getString("messageId"));
                }
                if (jSONObject.has("description")) {
                    miPushMessage.setDescription(jSONObject.getString("description"));
                }
                if (jSONObject.has("title")) {
                    miPushMessage.setTitle(jSONObject.getString("title"));
                }
                if (jSONObject.has("content")) {
                    miPushMessage.setContent(jSONObject.getString("content"));
                }
                if (jSONObject.has(KEY_PASS_THROUGH)) {
                    miPushMessage.setPassThrough(jSONObject.getInt(KEY_PASS_THROUGH));
                }
                if (jSONObject.has(KEY_NOTIFY_TYPE)) {
                    miPushMessage.setNotifyType(jSONObject.getInt(KEY_NOTIFY_TYPE));
                }
                if (jSONObject.has(KEY_MESSAGE_TYPE)) {
                    miPushMessage.setMessageType(jSONObject.getInt(KEY_MESSAGE_TYPE));
                }
                if (jSONObject.has(KEY_ALIAS)) {
                    miPushMessage.setAlias(jSONObject.getString(KEY_ALIAS));
                }
                if (jSONObject.has(KEY_TOPIC)) {
                    miPushMessage.setTopic(jSONObject.getString(KEY_TOPIC));
                }
                if (jSONObject.has(KEY_USER_ACCOUNT)) {
                    miPushMessage.setUserAccount(jSONObject.getString(KEY_USER_ACCOUNT));
                }
                if (jSONObject.has(KEY_NOTIFY_ID)) {
                    miPushMessage.setNotifyId(jSONObject.getInt(KEY_NOTIFY_ID));
                }
                if (jSONObject.has("category")) {
                    miPushMessage.setCategory(jSONObject.getString("category"));
                }
                if (jSONObject.has(KEY_NOTIFIED)) {
                    miPushMessage.setNotified(jSONObject.getBoolean(KEY_NOTIFIED));
                }
                if (jSONObject.has("extra")) {
                    JSONObject jSONObject2 = jSONObject.getJSONObject("extra");
                    Iterator<String> itKeys = jSONObject2.keys();
                    HashMap map = new HashMap();
                    while (itKeys != null && itKeys.hasNext()) {
                        String next = itKeys.next();
                        map.put(next, jSONObject2.getString(next));
                    }
                    if (map.size() > 0) {
                        miPushMessage.setExtra(map);
                    }
                }
            } catch (Exception e2) {
                MyLog.e(e2.toString());
            }
        }
        return miPushMessage;
    }

    public static void registerAssemblePush(Context context) {
        AssemblePushCollectionsManager.getInstance(context).register();
    }

    public static void reportError(String str, int i2) {
        MiTinyDataClient.upload(HMS_PUSH_ERROR, str, 1L, "error code = " + i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void saveAssemblePushToken(Context context, AssemblePush assemblePush, String str) {
        String tokenKey = getTokenKey(assemblePush);
        if (TextUtils.isEmpty(tokenKey)) {
            MyLog.w("ASSEMBLE_PUSH : can not find the key of token used in sp file");
            return;
        }
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("mipush_extra", 0).edit();
        editorEdit.putString(tokenKey, str).putString(LAST_CHECK_TOKEN, AppInfoHolder.getInstance(context).getRegID());
        if (isTypeCareAboutVersion(assemblePush)) {
            editorEdit.putInt(getTokenVersionKey(assemblePush), getAssembleVersionCode());
        }
        editorEdit.putString(KEY_SYNCING_TOKEN, "");
        SharedPrefsCompat.apply(editorEdit);
        MyLog.w("ASSEMBLE_PUSH : update sp file success!  " + str);
    }

    public static void saveAssemblePushTokenAfterAck(final Context context, final AssemblePush assemblePush, final String str) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.mipush.sdk.AssemblePushHelper.1
            @Override // java.lang.Runnable
            public void run() {
                String strSubstring;
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                String[] strArrSplit = str.split(Constants.WAVE_SEPARATOR);
                int length = strArrSplit.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        strSubstring = "";
                        break;
                    }
                    String str2 = strArrSplit[i2];
                    if (!TextUtils.isEmpty(str2) && str2.startsWith("token:")) {
                        strSubstring = str2.substring(str2.indexOf(":") + 1);
                        break;
                    }
                    i2++;
                }
                if (TextUtils.isEmpty(strSubstring)) {
                    MyLog.w("ASSEMBLE_PUSH : receive incorrect token");
                    return;
                }
                MyLog.w("ASSEMBLE_PUSH : receive correct token");
                AssemblePushHelper.saveAssemblePushToken(context, assemblePush, strSubstring);
                AssemblePushHelper.checkAssemblePushStatus(context);
            }
        });
    }

    private static synchronized void saveAssembleSyncingToken(Context context, String str) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("mipush_extra", 0).edit();
        editorEdit.putString(KEY_SYNCING_TOKEN, str);
        editorEdit.apply();
    }

    public static void unregisterAssemblePush(Context context) {
        AssemblePushCollectionsManager.getInstance(context).unregister();
    }

    public static void uploadToken(Context context, AssemblePush assemblePush, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        int iUploadTokenReason = uploadTokenReason(context, assemblePush, str);
        if (iUploadTokenReason == 0) {
            MyLog.w("ASSEMBLE_PUSH : do not need to send token");
            return;
        }
        MyLog.w("ASSEMBLE_PUSH : send token upload, check:" + iUploadTokenReason);
        saveAssembleSyncingToken(context, str);
        RetryType retryType = AssemblePushInfoHelper.getRetryType(assemblePush);
        if (retryType == null) {
            return;
        }
        PushServiceClient.getInstance(context).sendAssemblePushTokenCommon(null, retryType, assemblePush, "upload");
    }

    private static int uploadTokenReason(Context context, AssemblePush assemblePush, String str) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mipush_extra", 0);
        String tokenKey = getTokenKey(assemblePush);
        String string = sharedPreferences.getString(tokenKey, "");
        String regID = AppInfoHolder.getInstance(context).getRegID();
        String string2 = sharedPreferences.getString(LAST_CHECK_TOKEN, "");
        if (TextUtils.isEmpty(tokenKey)) {
            MyLog.w("ASSEMBLE_PUSH : can not find the key of token used in sp file");
            return 0;
        }
        if (TextUtils.isEmpty(string)) {
            return 1;
        }
        if (!string.equals(str)) {
            return 2;
        }
        if (!TextUtils.equals(regID, string2)) {
            return 3;
        }
        if (isTypeCareAboutVersion(assemblePush)) {
            if (getAssembleVersionCode() != sharedPreferences.getInt(getTokenVersionKey(assemblePush), 0)) {
                return 4;
            }
        }
        return 0;
    }
}
