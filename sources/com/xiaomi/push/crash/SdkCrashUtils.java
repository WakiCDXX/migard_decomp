package com.xiaomi.push.crash;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.push.service.MIPushAccount;
import com.xiaomi.push.service.MIPushAccountUtils;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.push.service.RsaUtils;
import com.xiaomi.xmpush.thrift.ConfigKey;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class SdkCrashUtils {
    private static String sAppVersionName;
    private static Boolean sIsChinaRegion;

    SdkCrashUtils() {
    }

    public static String formatJavaStack(String str) {
        try {
            String[] strArrSplit = str.replaceAll("\\t", "").split("\\n");
            StringBuilder sb = new StringBuilder();
            int length = strArrSplit.length;
            for (int i2 = 0; i2 < length; i2++) {
                String strReplaceAll = strArrSplit[i2].replaceAll("((java:)|(length=)|(index=)|(Index:)|(Size:))\\d+", "$1XX").replaceAll("\\$[0-9a-fA-F]{1,10}@[0-9a-fA-F]{1,10}|@[0-9a-fA-F]{1,10}|0x[0-9a-fA-F]{1,10}", "XX");
                strArrSplit[i2] = strReplaceAll;
                sb.append(strReplaceAll);
                sb.append("#n");
            }
            return sb.toString();
        } catch (Throwable unused) {
            return str;
        }
    }

    public static int getAndroidVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    public static String getAppVersionName(Context context) {
        if (sAppVersionName == null) {
            String packageName = context.getPackageName();
            sAppVersionName = packageName + "|" + AppInfoUtils.getVersionName(context, packageName);
        }
        return sAppVersionName;
    }

    public static String getExceptionType(String str) {
        try {
            String str2 = str.split("#n")[0];
            if (str2.contains("android.os.DeadSystemException")) {
                return "android.os.DeadSystemException";
            }
            return str2.contains(":") ? str2.substring(0, str2.indexOf(":")) : str2;
        } catch (Exception unused) {
            return "";
        }
    }

    public static String getRomVersionName() {
        return Build.MANUFACTURER + "|" + Build.VERSION.INCREMENTAL;
    }

    /* JADX WARN: Code duplicated, block: B:9:0x002f  */
    public static String getSecretInfos(Context context) {
        String deviceId;
        MIPushAccount mIPushAccount = MIPushAccountUtils.getMIPushAccount(context);
        if (mIPushAccount == null || TextUtils.isEmpty(mIPushAccount.account)) {
            deviceId = null;
        } else {
            String[] strArrSplit = mIPushAccount.account.split("@");
            if (strArrSplit.length > 0) {
                deviceId = "@-" + strArrSplit[0];
            } else {
                deviceId = null;
            }
        }
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = DeviceInfo.getDeviceId(context, true);
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("uniqueId", deviceId);
        } catch (Exception unused) {
        }
        if (jSONObject.length() > 0) {
            return RsaUtils.rsaEncrypt(jSONObject.toString());
        }
        return null;
    }

    public static boolean isChinaRegion() {
        if (sIsChinaRegion == null) {
            sIsChinaRegion = Boolean.valueOf(!MIUIUtils.isGlobalRegion());
        }
        return sIsChinaRegion.booleanValue();
    }

    public static boolean isMipushStack(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        for (String str2 : SdkCrashConstants.CRASH_FILTER) {
            if (str.contains(str2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOcEnabled(Context context) {
        return OnlineConfig.getInstance(context).getBooleanValue(ConfigKey.SdkExceptionMonitorSwitch.getValue(), true);
    }
}
