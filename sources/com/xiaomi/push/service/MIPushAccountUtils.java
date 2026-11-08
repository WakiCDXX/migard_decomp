package com.xiaomi.push.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.Region;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.BuildSettings;
import com.xiaomi.channel.commonutils.msa.MsaIdManager;
import com.xiaomi.channel.commonutils.network.HttpResponse;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.onetrack.OneTrack;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import miui.date.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MIPushAccountUtils {
    private static final int MAX_ATTEMPTS = 2;
    private static final String MIPUSH_CHINA_ACCOUNT_HOST_P = "cn.register.xmpush.xiaomi.com";
    public static final String MIPUSH_MIUI_APPID = "1000271";
    public static final String MIPUSH_MIUI_APP_TOKEN = "420100086271";
    private static final String PREF_KEY_ACCOUNT = "uuid";
    private static final String PREF_KEY_APP_ID = "app_id";
    private static final String PREF_KEY_APP_TOKEN = "app_token";
    private static final String PREF_KEY_DEVICE_ID = "device_id";
    private static final String PREF_KEY_ENC_REQ_FAIL_COUNT = "enc_req_fail_count";
    private static final String PREF_KEY_ENV_TYPE = "env_type";
    private static final String PREF_KEY_GAID = "gaid";
    private static final String PREF_KEY_PACKAGENAME = "package_name";
    private static final String PREF_KEY_SECURITY = "security";
    private static final String PREF_KEY_TOKEN = "token";
    private static final String PREF_NAME = "mipush_account";
    private static PushAccountChangeListener accountChangeListener;
    private static MIPushAccount sAccount;

    public interface PushAccountChangeListener {
        void onChange();
    }

    public static void clearAccount(Context context) {
        context.getSharedPreferences(PREF_NAME, 0).edit().clear().commit();
        sAccount = null;
        notifyAccountChange();
    }

    @Deprecated
    public static String getAccountURL(Context context) {
        return "";
    }

    private static int getEncFailCount(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0).getInt(PREF_KEY_ENC_REQ_FAIL_COUNT, 0);
    }

    public static synchronized MIPushAccount getMIPushAccount(Context context) {
        try {
            MIPushAccount mIPushAccount = sAccount;
            if (mIPushAccount != null) {
                return mIPushAccount;
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
            String string = sharedPreferences.getString("uuid", null);
            String string2 = sharedPreferences.getString("token", null);
            String string3 = sharedPreferences.getString("security", null);
            String string4 = sharedPreferences.getString("app_id", null);
            String string5 = sharedPreferences.getString("app_token", null);
            String string6 = sharedPreferences.getString("package_name", null);
            String string7 = sharedPreferences.getString("device_id", null);
            int i2 = sharedPreferences.getInt(PREF_KEY_ENV_TYPE, 1);
            if (!TextUtils.isEmpty(string7) && DeviceInfo.startsWithDevPrefix(string7)) {
                string7 = DeviceInfo.getSimpleDeviceId(context);
                sharedPreferences.edit().putString("device_id", string7).commit();
            }
            if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2) || TextUtils.isEmpty(string3)) {
                return null;
            }
            String simpleDeviceId = DeviceInfo.getSimpleDeviceId(context);
            if (!PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName()) && !TextUtils.isEmpty(simpleDeviceId) && !TextUtils.isEmpty(string7) && !string7.equals(simpleDeviceId)) {
                MyLog.w("read_phone_state permission changes.");
            }
            MIPushAccount mIPushAccount2 = new MIPushAccount(string, string2, string3, string4, string5, string6, i2);
            sAccount = mIPushAccount2;
            return mIPushAccount2;
        } catch (Throwable th) {
            throw th;
        }
    }

    public static String getUUID(Context context) {
        MIPushAccount mIPushAccount = getMIPushAccount(context);
        if (mIPushAccount != null && !TextUtils.isEmpty(mIPushAccount.account)) {
            String[] strArrSplit = mIPushAccount.account.split("@");
            if (strArrSplit.length > 0) {
                return strArrSplit[0];
            }
        }
        return null;
    }

    private static boolean isMIUIPush(Context context) {
        return context.getPackageName().equals(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
    }

    public static void notifyAccountChange() {
        PushAccountChangeListener pushAccountChangeListener = accountChangeListener;
        if (pushAccountChangeListener != null) {
            pushAccountChangeListener.onChange();
        }
    }

    public static void persist(Context context, MIPushAccount mIPushAccount) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences(PREF_NAME, 0).edit();
        editorEdit.putString("uuid", mIPushAccount.account);
        editorEdit.putString("security", mIPushAccount.security);
        editorEdit.putString("token", mIPushAccount.token);
        editorEdit.putString("app_id", mIPushAccount.appId);
        editorEdit.putString("package_name", mIPushAccount.packageName);
        editorEdit.putString("app_token", mIPushAccount.appToken);
        editorEdit.putString("device_id", DeviceInfo.getSimpleDeviceId(context));
        editorEdit.putInt(PREF_KEY_ENV_TYPE, mIPushAccount.envType);
        editorEdit.commit();
        notifyAccountChange();
    }

    /* JADX WARN: Code duplicated, block: B:105:0x0318  */
    /* JADX WARN: Code duplicated, block: B:13:0x0046  */
    public static synchronized MIPushAccount register(Context context, String str, String str2, String str3) {
        String strSubstring;
        PackageInfo packageInfo;
        boolean z2;
        HttpResponse httpResponseDoHttpPost;
        try {
            TreeMap treeMap = new TreeMap();
            treeMap.put("devid", DeviceInfo.getDeviceId(context, false));
            MIPushAccount mIPushAccount = sAccount;
            if (mIPushAccount == null || TextUtils.isEmpty(mIPushAccount.account)) {
                strSubstring = null;
            } else {
                treeMap.put("uuid", sAccount.account);
                int iLastIndexOf = sAccount.account.lastIndexOf("/");
                if (iLastIndexOf != -1) {
                    strSubstring = sAccount.account.substring(iLastIndexOf + 1);
                } else {
                    strSubstring = null;
                }
            }
            MsaIdManager.getInstance(context).fillData(treeMap);
            String str4 = isMIUIPush(context) ? MIPUSH_MIUI_APPID : str2;
            String str5 = isMIUIPush(context) ? MIPUSH_MIUI_APP_TOKEN : str3;
            String str6 = isMIUIPush(context) ? PushConstants.PUSH_SERVICE_PACKAGE_NAME : str;
            treeMap.put("appid", str4);
            treeMap.put("apptoken", str5);
            try {
                packageInfo = context.getPackageManager().getPackageInfo(str6, DateUtils.FORMAT_ABBREV_TIME);
            } catch (Exception e2) {
                MyLog.e(e2);
                packageInfo = null;
            }
            treeMap.put("appversion", packageInfo != null ? String.valueOf(packageInfo.versionCode) : "0");
            treeMap.put("sdkversion", Integer.toString(PushConstants.PUSH_VERSION_CODE));
            treeMap.put("packagename", str6);
            treeMap.put(OneTrack.Param.MODEL, Build.MODEL);
            treeMap.put("board", Build.BOARD);
            if (!MIUIUtils.isGlobalRegion()) {
                String strBlockingGetIMEI = DeviceInfo.blockingGetIMEI(context);
                String str7 = TextUtils.isEmpty(strBlockingGetIMEI) ? "" : "" + XMStringUtils.getMd5Digest(strBlockingGetIMEI);
                String strBlockingGetSubIMEISMd5 = DeviceInfo.blockingGetSubIMEISMd5(context);
                if (!TextUtils.isEmpty(str7) && !TextUtils.isEmpty(strBlockingGetSubIMEISMd5)) {
                    str7 = str7 + "," + strBlockingGetSubIMEISMd5;
                }
                if (!TextUtils.isEmpty(str7)) {
                    treeMap.put(Constants.EXTRA_KEY_IMEI_MD5, str7);
                }
            }
            treeMap.put("os", Build.VERSION.RELEASE + Constants.ACCEPT_TIME_SEPARATOR_SERVER + Build.VERSION.INCREMENTAL);
            int spaceId = DeviceInfo.getSpaceId();
            if (spaceId >= 0) {
                treeMap.put("space_id", Integer.toString(spaceId));
            }
            treeMap.put(Constants.PHONE_BRAND, Build.BRAND + "");
            treeMap.put("ram", DeviceInfo.getRamSize());
            treeMap.put("rom", DeviceInfo.getRomSize());
            JSONObject jSONObject = new JSONObject();
            for (Map.Entry entry : treeMap.entrySet()) {
                try {
                    jSONObject.put((String) entry.getKey(), entry.getValue());
                } catch (JSONException e3) {
                    MyLog.e("failed to add data in json format: k=" + ((String) entry.getKey()) + ",v=" + ((String) entry.getValue()) + ". " + e3);
                }
            }
            String strRsaEncrypt = RsaUtils.rsaEncrypt(jSONObject.toString());
            TreeMap treeMap2 = new TreeMap();
            treeMap2.put("requestData", strRsaEncrypt);
            treeMap2.put("keyPairVer", "1");
            if (getEncFailCount(context) >= 2 || TextUtils.isEmpty(strRsaEncrypt)) {
                z2 = false;
            } else {
                MyLog.w("r.data = " + strRsaEncrypt);
                z2 = true;
            }
            String accountURL = getAccountURL(context, z2);
            if (TextUtils.isEmpty(accountURL)) {
                return null;
            }
            if (z2) {
                treeMap = treeMap2;
            }
            try {
                httpResponseDoHttpPost = Network.doHttpPost(context, accountURL, treeMap);
            } catch (IOException e4) {
                MyLog.e("device registration request failed. " + e4);
                httpResponseDoHttpPost = null;
            }
            if (httpResponseDoHttpPost != null && httpResponseDoHttpPost.responseCode == 200) {
                String responseString = httpResponseDoHttpPost.getResponseString();
                if (!TextUtils.isEmpty(responseString)) {
                    try {
                        JSONObject jSONObject2 = new JSONObject(responseString);
                        try {
                            if (jSONObject2.getInt("code") == 0) {
                                JSONObject jSONObject3 = jSONObject2.getJSONObject("data");
                                String string = jSONObject3.getString("ssecurity");
                                String string2 = jSONObject3.getString("token");
                                String string3 = jSONObject3.getString("userId");
                                if (TextUtils.isEmpty(strSubstring)) {
                                    strSubstring = "an" + XMStringUtils.generateRandomString(6);
                                }
                                MIPushAccount mIPushAccount2 = new MIPushAccount(string3 + "@xiaomi.com/" + strSubstring, string2, string, str4, str5, str6, BuildSettings.getEnvType());
                                persist(context, mIPushAccount2);
                                sAccount = mIPushAccount2;
                                setEncFailCount(context, 0);
                                MyLog.w("device registration is successful. " + string3);
                                return mIPushAccount2;
                            }
                            MIPushClientManager.notifyRegisterError(context, jSONObject2.getInt("code"), jSONObject2.optString("description"));
                            MyLog.w("device registration resp: " + responseString);
                        } catch (JSONException e5) {
                            e = e5;
                            MyLog.e("failed to parse respone json data. " + e);
                        } catch (Throwable th) {
                            th = th;
                            MyLog.e("unknow throwable. " + th);
                        }
                    } catch (JSONException e6) {
                        e = e6;
                    } catch (Throwable th2) {
                        th = th2;
                    }
                    MyLog.e("failed to parse respone json data. " + e);
                }
            }
            if (z2 && Network.isConnectivity(context)) {
                setEncFailCount(context, getEncFailCount(context) + 1);
            }
            MyLog.w("fail to register push account. meet error.");
            return null;
        } catch (Throwable th3) {
            throw th3;
        }
    }

    public static void setAccountChangeListener(PushAccountChangeListener pushAccountChangeListener) {
        accountChangeListener = pushAccountChangeListener;
    }

    private static void setEncFailCount(Context context, int i2) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences(PREF_NAME, 0).edit();
        editorEdit.putInt(PREF_KEY_ENC_REQ_FAIL_COUNT, i2);
        editorEdit.commit();
    }

    private static String getAccountURL(Context context, boolean z2) {
        String region = AppRegionStorage.getInstance(context).getRegion();
        String str = z2 ? "/pass/v2/register/encrypt" : "/pass/v2/register";
        if (BuildSettings.IsOneBoxBuild()) {
            return "http://10.38.162.35:9085" + str;
        }
        if (!Region.China.name().equals(region)) {
            return null;
        }
        return "https://cn.register.xmpush.xiaomi.com" + str;
    }
}
