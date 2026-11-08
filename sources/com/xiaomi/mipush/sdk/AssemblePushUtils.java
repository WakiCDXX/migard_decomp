package com.xiaomi.mipush.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;

/* JADX INFO: loaded from: classes.dex */
public class AssemblePushUtils {
    public static final String HMS_PUSH_CLASS_NAME = "com.huawei.hms.core.service.HMSCoreService";
    public static final String HMS_PUSH_PACKAGE_NAME = "com.huawei.hwid";
    public static final String KEY_SYNC_TOKEN_REASON = "third_sync_reason";
    public static final String REASON_SYNC_TOKEN_INIT = "init";
    public static final String REASON_SYNC_TOKEN_NET_CHANGED = "net";
    public static final String REASON_SYNC_TOKEN_RETRY = "retry";
    public static final String REASON_SYNC_TOKEN_UPDATE = "update";
    public static final String REASON_SYNC_TOKEN_UPLOAD = "upload";
    private static int isGoogleServiceSatisfied = -1;

    public static PhoneBrand getPhoneBrand(Context context) {
        if (isHmsSupport(context)) {
            return PhoneBrand.HUAWEI;
        }
        if (isColorOSPushSupport(context)) {
            return PhoneBrand.OPPO;
        }
        return isFunTouchOSPushSupport(context) ? PhoneBrand.VIVO : PhoneBrand.OTHER;
    }

    private static boolean isAvailableEMUI() {
        try {
            String str = (String) JavaCalls.callStaticMethod("android.os.SystemProperties", "get", "ro.build.hw_emui_api_level", "");
            return !TextUtils.isEmpty(str) && Integer.parseInt(str) >= 9;
        } catch (Exception e2) {
            MyLog.e(e2);
            return false;
        }
    }

    public static boolean isColorOSPushSupport(Context context) {
        Object objCallStaticMethod = JavaCalls.callStaticMethod("com.xiaomi.assemble.control.COSPushManager", "isSupportPush", context);
        boolean zBooleanValue = (objCallStaticMethod == null || !(objCallStaticMethod instanceof Boolean)) ? false : ((Boolean) Boolean.class.cast(objCallStaticMethod)).booleanValue();
        MyLog.v("color os push  is avaliable ? :" + zBooleanValue);
        return zBooleanValue;
    }

    public static boolean isFunTouchOSPushSupport(Context context) {
        Object objCallStaticMethod = JavaCalls.callStaticMethod("com.xiaomi.assemble.control.FTOSPushManager", "isSupportPush", context);
        boolean zBooleanValue = (objCallStaticMethod == null || !(objCallStaticMethod instanceof Boolean)) ? false : ((Boolean) Boolean.class.cast(objCallStaticMethod)).booleanValue();
        MyLog.v("fun touch os push  is avaliable ? :" + zBooleanValue);
        return zBooleanValue;
    }

    public static boolean isGoogleServiceSatisfied(Context context) {
        Object objCallMethod = JavaCalls.callMethod(JavaCalls.callStaticMethod("com.google.android.gms.common.GoogleApiAvailability", "getInstance", new Object[0]), "isGooglePlayServicesAvailable", context);
        Object staticField = JavaCalls.getStaticField("com.google.android.gms.common.ConnectionResult", "SUCCESS");
        if (staticField == null || !(staticField instanceof Integer)) {
            MyLog.v("google service is not avaliable");
            isGoogleServiceSatisfied = 0;
            return false;
        }
        int iIntValue = ((Integer) Integer.class.cast(staticField)).intValue();
        if (objCallMethod != null) {
            if (objCallMethod instanceof Integer) {
                isGoogleServiceSatisfied = ((Integer) Integer.class.cast(objCallMethod)).intValue() == iIntValue ? 1 : 0;
            } else {
                isGoogleServiceSatisfied = 0;
                MyLog.v("google service is not avaliable");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("is google service can be used");
        sb.append(isGoogleServiceSatisfied > 0);
        MyLog.v(sb.toString());
        return isGoogleServiceSatisfied > 0;
    }

    public static boolean isHmsSupport(Context context) {
        try {
            return context.getPackageManager().getServiceInfo(new ComponentName(HMS_PUSH_PACKAGE_NAME, HMS_PUSH_CLASS_NAME), 128) != null && isAvailableEMUI();
        } catch (Throwable unused) {
        }
    }
}
