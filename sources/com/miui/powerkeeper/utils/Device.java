package com.miui.powerkeeper.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import miui.telephony.phonenumber.CountryCode;

/* JADX INFO: loaded from: classes.dex */
public class Device {
    private static final String GROUP_SPLIT = "__";
    private static final String KEY_AVS = "avs";
    private static final String KEY_BVS = "bvs";
    private static final String KEY_MCC = "mcc";
    private static final String KEY_MNC = "mnc";
    private static final String KEY_MOD = "mod";
    private static final String KEY_PVS = "pvs";
    private static final String KEY_RGN = "rgn";
    private static final String TAG = "CloudUpdate";
    private static final String WORD_SPLIT = "-";
    private String sType = null;

    public static class Holder {
        private static final Device sInstance = new Device();
    }

    public static String getAndroidVersion() {
        return Integer.toString(Build.VERSION.SDK_INT);
    }

    public static Device getInstance() {
        return Holder.sInstance;
    }

    public static String getMCC() {
        String networkCountryCode = CountryCode.getNetworkCountryCode();
        return TextUtils.isEmpty(networkCountryCode) ? "-1" : networkCountryCode;
    }

    public static String getMNC() {
        String iddCode = CountryCode.getIddCode();
        return TextUtils.isEmpty(iddCode) ? "-1" : iddCode;
    }

    public static String getModel() {
        String str = SystemProperties.get("ro.product.real_model", "");
        if (str.isEmpty()) {
            str = Build.MODEL;
        }
        String strReplaceAll = str.replaceAll(" ", "");
        if (miui.os.Build.IS_ALPHA_BUILD) {
            return strReplaceAll + "_alpha";
        }
        if (miui.os.Build.IS_STABLE_VERSION) {
            return strReplaceAll + "_stable";
        }
        return strReplaceAll + "_dev";
    }

    public static String getModelNew() {
        return Build.MODEL.replaceAll(" ", "-");
    }

    public static String getPowerKeeperVersion(Context context) {
        Context applicationContext = context.getApplicationContext();
        try {
            return String.valueOf(applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e(TAG, "getPowerKeeperVersion", e2);
            return "0000";
        } catch (Exception e3) {
            Log.e(TAG, "getPowerKeeperVersion", e3);
            return "0000";
        }
    }

    public static String getUTCTimeStr() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public static String getVersion() {
        return Build.VERSION.INCREMENTAL;
    }

    public String getType(Context context) {
        if (this.sType == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("mod-" + getModel() + GROUP_SPLIT);
            sb.append("avs-" + getAndroidVersion() + GROUP_SPLIT);
            sb.append("bvs-" + getVersion() + GROUP_SPLIT);
            sb.append("mnc-" + getMNC() + GROUP_SPLIT);
            sb.append("mcc-" + getMCC() + GROUP_SPLIT);
            sb.append("pvs-" + getPowerKeeperVersion(context) + GROUP_SPLIT);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("rgn-");
            sb2.append(Utils.getRegion());
            sb.append(sb2.toString());
            this.sType = sb.toString();
        }
        return this.sType;
    }
}
