package com.xiaomi.channel.commonutils.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.mipush.sdk.Constants;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class MIUIUtils {
    private static final String ANDROID_SYSTEM_PROPERTIES = "android.os.SystemProperties";
    public static final int IS_MIUI = 1;
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    public static final String MIUI_OS_VERSION_ALPHA = "alpha";
    public static final String MIUI_OS_VERSION_DEVELOPMENT = "development";
    public static final String MIUI_OS_VERSION_STABLE = "stable";
    public static final int NOT_MIUI = 2;
    private static final String PUSH_SERVICE_PACKAGE_NAME = "com.xiaomi.xmsf";
    public static final int UNKNOWN = 0;
    private static int isInXMS = -1;
    private static volatile int isMIUI;
    private static Map<String, Region> locale2RegionMap;

    private MIUIUtils() {
    }

    private static String adapterRegion(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String[] strArrSplit = str.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
        return strArrSplit.length > 0 ? strArrSplit[0] : str;
    }

    public static String bundleToString(Bundle bundle) {
        StringBuilder sb = new StringBuilder("Bundle[");
        if (bundle == null) {
            sb.append("null");
        } else {
            boolean z2 = true;
            for (String str : bundle.keySet()) {
                if (!z2) {
                    sb.append(", ");
                }
                sb.append(str);
                sb.append('=');
                Object obj = bundle.get(str);
                if (obj instanceof int[]) {
                    sb.append(Arrays.toString((int[]) obj));
                } else if (obj instanceof byte[]) {
                    sb.append(Arrays.toString((byte[]) obj));
                } else if (obj instanceof boolean[]) {
                    sb.append(Arrays.toString((boolean[]) obj));
                } else if (obj instanceof short[]) {
                    sb.append(Arrays.toString((short[]) obj));
                } else if (obj instanceof long[]) {
                    sb.append(Arrays.toString((long[]) obj));
                } else if (obj instanceof float[]) {
                    sb.append(Arrays.toString((float[]) obj));
                } else if (obj instanceof double[]) {
                    sb.append(Arrays.toString((double[]) obj));
                } else if (obj instanceof String[]) {
                    sb.append(Arrays.toString((String[]) obj));
                } else if (obj instanceof CharSequence[]) {
                    sb.append(Arrays.toString((CharSequence[]) obj));
                } else if (obj instanceof Parcelable[]) {
                    sb.append(Arrays.toString((Parcelable[]) obj));
                } else if (obj instanceof Bundle) {
                    sb.append(bundleToString((Bundle) obj));
                } else {
                    sb.append(obj);
                }
                z2 = false;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static Bundle deepCopyForBundle(Bundle bundle) {
        new Bundle();
        return bundle.deepCopy();
    }

    private static Region findServerRegionByLocale(String str) {
        initLocale2RegionMap();
        return locale2RegionMap.get(str.toUpperCase());
    }

    public static String getCountryCode() {
        String strAdapterRegion = SystemProperties.get("ro.miui.region", "");
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = SystemProperties.get("persist.sys.oppo.region", "");
        }
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = SystemProperties.get("ro.oppo.regionmark", "");
        }
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = SystemProperties.get("ro.vendor.oplus.regionmark", "");
        }
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = SystemProperties.get("ro.hw.country", "");
        }
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = SystemProperties.get("ro.csc.countryiso_code", "");
        }
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = adapterRegion(SystemProperties.get("ro.product.country.region", ""));
        }
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = SystemProperties.get("gsm.vivo.countrycode", "");
        }
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = SystemProperties.get("persist.sys.oem.region", "");
        }
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = SystemProperties.get("ro.product.locale.region", "");
        }
        if (TextUtils.isEmpty(strAdapterRegion)) {
            strAdapterRegion = SystemProperties.get("persist.sys.country", "");
        }
        if (!TextUtils.isEmpty(strAdapterRegion)) {
            MyLog.w("get region from system, region = " + strAdapterRegion);
        }
        if (!TextUtils.isEmpty(strAdapterRegion)) {
            return strAdapterRegion;
        }
        String country = Locale.getDefault().getCountry();
        MyLog.w("locale.default.country = " + country);
        return country;
    }

    public static String getDeviceCharacteristics() {
        return getProperty("ro.build.characteristics");
    }

    public static String getDeviceManufacture() {
        return getProperty("ro.product.manufacturer");
    }

    public static int getIsMIUI() {
        if (isMIUI == 0) {
            try {
                isMIUI = (TextUtils.isEmpty(getProperty(KEY_MIUI_VERSION_CODE)) && TextUtils.isEmpty(getProperty(KEY_MIUI_VERSION_NAME))) ? 2 : 1;
            } catch (Throwable th) {
                MyLog.e("get isMIUI failed", th);
                isMIUI = 0;
            }
            MyLog.i("isMIUI's value is: " + isMIUI);
        }
        return isMIUI;
    }

    public static String getMIUIType() {
        int mIUIType = SystemUtils.getMIUIType();
        if (!isMIUI() || mIUIType <= 0) {
            return "";
        }
        if (mIUIType < 2) {
            return MIUI_OS_VERSION_ALPHA;
        }
        return mIUIType < 3 ? MIUI_OS_VERSION_DEVELOPMENT : MIUI_OS_VERSION_STABLE;
    }

    public static int getMiuiVersionCode(Context context) {
        String property = getProperty(KEY_MIUI_VERSION_CODE);
        if (TextUtils.isEmpty(property) || !TextUtils.isDigitsOnly(property)) {
            return 0;
        }
        return Integer.parseInt(property);
    }

    public static String getMiuiVersionName() {
        return getProperty(KEY_MIUI_VERSION_NAME);
    }

    public static String getProperty(String str) {
        try {
            try {
                return (String) JavaCalls.callStaticMethod(ANDROID_SYSTEM_PROPERTIES, "get", str, "");
            } catch (Exception e2) {
                MyLog.e("fail to get property. " + e2);
                return null;
            }
        } catch (Throwable unused) {
            return null;
        }
    }

    public static Region getRegion(String str) {
        Region regionFindServerRegionByLocale = findServerRegionByLocale(str);
        return regionFindServerRegionByLocale == null ? Region.Global : regionFindServerRegionByLocale;
    }

    public static int getVisualMiuiVersionCode(Context context) {
        int miuiVersionCode = getMiuiVersionCode(context);
        if (miuiVersionCode <= 0) {
            return 0;
        }
        return miuiVersionCode + 2;
    }

    public static int getXmsfVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.xiaomi.xmsf", 0).versionCode;
        } catch (Exception unused) {
            return 0;
        }
    }

    private static void initLocale2RegionMap() {
        if (locale2RegionMap != null) {
            return;
        }
        HashMap map = new HashMap();
        locale2RegionMap = map;
        map.put("CN", Region.China);
        Map<String, Region> map2 = locale2RegionMap;
        Region region = Region.Europe;
        map2.put("FI", region);
        locale2RegionMap.put("SE", region);
        locale2RegionMap.put("NO", region);
        locale2RegionMap.put("FO", region);
        locale2RegionMap.put("EE", region);
        locale2RegionMap.put("LV", region);
        locale2RegionMap.put("LT", region);
        locale2RegionMap.put("BY", region);
        locale2RegionMap.put("MD", region);
        locale2RegionMap.put("UA", region);
        locale2RegionMap.put("PL", region);
        locale2RegionMap.put("CZ", region);
        locale2RegionMap.put("SK", region);
        locale2RegionMap.put("HU", region);
        locale2RegionMap.put("DE", region);
        locale2RegionMap.put("AT", region);
        locale2RegionMap.put("CH", region);
        locale2RegionMap.put("LI", region);
        locale2RegionMap.put("GB", region);
        locale2RegionMap.put("IE", region);
        locale2RegionMap.put("NL", region);
        locale2RegionMap.put("BE", region);
        locale2RegionMap.put("LU", region);
        locale2RegionMap.put("FR", region);
        locale2RegionMap.put("RO", region);
        locale2RegionMap.put("BG", region);
        locale2RegionMap.put("RS", region);
        locale2RegionMap.put("MK", region);
        locale2RegionMap.put("AL", region);
        locale2RegionMap.put("GR", region);
        locale2RegionMap.put("SI", region);
        locale2RegionMap.put("HR", region);
        locale2RegionMap.put("IT", region);
        locale2RegionMap.put("SM", region);
        locale2RegionMap.put("MT", region);
        locale2RegionMap.put("ES", region);
        locale2RegionMap.put("PT", region);
        locale2RegionMap.put("AD", region);
        locale2RegionMap.put("CY", region);
        locale2RegionMap.put("DK", region);
        locale2RegionMap.put("IS", region);
        locale2RegionMap.put("UK", region);
        locale2RegionMap.put("EL", region);
        locale2RegionMap.put("RU", Region.Russia);
        locale2RegionMap.put("IN", Region.India);
    }

    public static String intentToString(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.toString() + " " + bundleToString(intent.getExtras());
    }

    public static boolean isGlobalRegion() {
        return !Region.China.name().equalsIgnoreCase(getRegion(getCountryCode()).name());
    }

    public static boolean isMIUI() {
        return getIsMIUI() == 1;
    }

    public static boolean isMiuiSystem() {
        String str = "";
        try {
            str = SystemProperties.get(KEY_MIUI_VERSION_CODE, "");
        } catch (Exception unused) {
        }
        return !TextUtils.isEmpty(str);
    }

    public static boolean isNotMIUI() {
        return getIsMIUI() == 2;
    }

    public static boolean isXMS() {
        if (isInXMS < 0) {
            isInXMS = !isMiuiSystem() ? 1 : 0;
        }
        return isInXMS > 0;
    }

    public static boolean isXMSF(Context context) {
        return context != null && isXMSF(context.getPackageName());
    }

    public static boolean isXMSF(String str) {
        return "com.xiaomi.xmsf".equals(str);
    }
}
