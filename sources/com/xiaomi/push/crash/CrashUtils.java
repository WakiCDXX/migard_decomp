package com.xiaomi.push.crash;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.Region;
import com.xiaomi.push.service.AppRegionStorage;
import com.xiaomi.push.service.MIPushAccount;
import com.xiaomi.push.service.MIPushAccountUtils;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.RsaUtils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class CrashUtils {
    private static String sRomType = null;
    private static int sRomVersionCode = -1;
    private static int sXmsfVersionCode = -1;

    CrashUtils() {
    }

    public static int getAndroidVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    public static String getRomType() {
        if (sRomType == null) {
            sRomType = MIUIUtils.getMIUIType();
        }
        return sRomType;
    }

    public static int getRomVersionCode(Context context) {
        if (sRomVersionCode < 0) {
            sRomVersionCode = MIUIUtils.getMiuiVersionCode(context);
        }
        return sRomVersionCode;
    }

    public static String getRomVersionName() {
        return Build.VERSION.INCREMENTAL;
    }

    /* JADX WARN: Code duplicated, block: B:9:0x001e  */
    public static String getSecretInfos(Context context) {
        String str;
        MIPushAccount mIPushAccount = MIPushAccountUtils.getMIPushAccount(context);
        if (mIPushAccount == null || TextUtils.isEmpty(mIPushAccount.account)) {
            str = null;
        } else {
            String[] strArrSplit = mIPushAccount.account.split("@");
            if (strArrSplit.length > 0) {
                str = strArrSplit[0];
            } else {
                str = null;
            }
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("uuid", str);
        } catch (Exception unused) {
        }
        if (jSONObject.length() > 0) {
            return RsaUtils.rsaEncrypt(jSONObject.toString());
        }
        return null;
    }

    public static int getXmsfVersionCode(Context context) {
        if (sXmsfVersionCode < 0) {
            sXmsfVersionCode = AppInfoUtils.getVersionCode(context, PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        }
        return sXmsfVersionCode;
    }

    public static boolean isChinaRegion(Context context) {
        return Region.China.name().equals(AppRegionStorage.getInstance(context).getRegion());
    }
}
