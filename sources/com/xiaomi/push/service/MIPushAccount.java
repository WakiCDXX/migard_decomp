package com.xiaomi.push.service;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.miui.powerkeeper.powerchecker.PowerCheckerProcessNotifier;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class MIPushAccount {
    public static final String PREF_KEY_APP_ID = "app_id";
    public static final String PREF_KEY_APP_TOKEN = "app_token";
    public static final String PREF_KEY_DEVICE_ID = "device_id";
    public static final String PREF_KEY_PACKAGENAME = "package_name";
    public static final String PREF_KEY_SECURITY = "security";
    public static final String PREF_KEY_TOKEN = "token";
    public static final String PREF_KEY_UUID = "uuid";
    public final String account;
    public final String appId;
    public final String appToken;
    public final int envType;
    public final String packageName;
    public final String security;
    public final String token;

    public MIPushAccount(String str, String str2, String str3, String str4, String str5, String str6, int i2) {
        this.account = str;
        this.token = str2;
        this.security = str3;
        this.appId = str4;
        this.appToken = str5;
        this.packageName = str6;
        this.envType = i2;
    }

    private static String getCountryCode(Context context) {
        if (!PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context)) {
            return MIUIUtils.getCountryCode();
        }
        if (!TextUtils.isEmpty(null)) {
            return null;
        }
        String property = MIUIUtils.getProperty("ro.miui.region");
        return TextUtils.isEmpty(property) ? MIUIUtils.getProperty("ro.product.locale.region") : property;
    }

    public static boolean isAbTestSupported(Context context) {
        return PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName()) && isMIUIAlphaVersion();
    }

    public static boolean isMIUIAlphaVersion() {
        try {
            return SystemUtils.loadClass(null, "miui.os.Build").getField("IS_ALPHA_BUILD").getBoolean(null);
        } catch (Exception unused) {
            return false;
        }
    }

    private static boolean isMIUIPush(Context context) {
        return context.getPackageName().equals(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
    }

    public PushClientsManager.ClientLoginInfo toClientLoginInfo(XMPushService xMPushService) {
        PushClientsManager.ClientLoginInfo clientLoginInfo = new PushClientsManager.ClientLoginInfo(xMPushService);
        toClientLoginInfo(clientLoginInfo, xMPushService, xMPushService.getClientEventDispatcher(), "c");
        return clientLoginInfo;
    }

    public PushClientsManager.ClientLoginInfo toClientLoginInfo(Context context) {
        return toClientLoginInfo(new PushClientsManager.ClientLoginInfo(), context, new ClientEventDispatcher(), "d");
    }

    public PushClientsManager.ClientLoginInfo toClientLoginInfo(PushClientsManager.ClientLoginInfo clientLoginInfo, Context context, ClientEventDispatcher clientEventDispatcher, String str) {
        clientLoginInfo.pkgName = context.getPackageName();
        clientLoginInfo.userId = this.account;
        clientLoginInfo.security = this.security;
        clientLoginInfo.token = this.token;
        clientLoginInfo.chid = "5";
        clientLoginInfo.authMethod = "XMPUSH-PASS";
        clientLoginInfo.kick = false;
        ToolUtils.StringMap stringMap = new ToolUtils.StringMap();
        stringMap.put("sdk_ver", 48).put(PushConstants.KEY_CHANNEL_PUSH_VERSION_NAME, PushConstants.PUSH_VERSION_NAME).put(PushConstants.KEY_CHANNEL_PUSH_VERSION_CODE, Integer.valueOf(PushConstants.PUSH_VERSION_CODE)).put(PushConstants.KEY_COUNTRY_CODE, AppRegionStorage.getInstance(context).getCountryCode()).put("region", AppRegionStorage.getInstance(context).getRegion()).put(PushConstants.KEY_MIUI_VERSION_NAME, MIUIUtils.getMiuiVersionName()).put(PushConstants.KEY_MIUI_VERSION_CODE, Integer.valueOf(MIUIUtils.getMiuiVersionCode(context))).put(PushConstants.KEY_XMSF_VERSION_CODE, Integer.valueOf(AppInfoUtils.getVersionCode(context, PushConstants.PUSH_SERVICE_PACKAGE_NAME))).put(PushConstants.KEY_ANDROID_VERSION, Integer.valueOf(Build.VERSION.SDK_INT)).put(PushConstants.KEY_IS_NOTIFICATION_BELONG_TO_APP, Boolean.valueOf(NotificationManagerHelper.isRomSupportNotificationBelongToApp(context))).put(PushConstants.KEY_SYSTEMUI_SUPPORT_CODE, Integer.valueOf(AppInfoUtils.getSystemUIForXmsfCode(context)));
        String countryCode = getCountryCode(context);
        if (!TextUtils.isEmpty(countryCode)) {
            stringMap.put(PushConstants.KEY_LAST_COUNTRY_CODE, countryCode);
        }
        String deviceCharacteristics = MIUIUtils.getDeviceCharacteristics();
        if (!TextUtils.isEmpty(deviceCharacteristics)) {
            stringMap.put(PushConstants.KEY_DEVICE_CHARACTERISTICS, deviceCharacteristics);
        }
        String deviceManufacture = MIUIUtils.getDeviceManufacture();
        if (!TextUtils.isEmpty(deviceManufacture)) {
            stringMap.put(PushConstants.KEY_DEVICE_MANUFACTURER, deviceManufacture);
        }
        clientLoginInfo.clientExtra = stringMap.toString();
        String str2 = isMIUIPush(context) ? MIPushAccountUtils.MIPUSH_MIUI_APPID : this.appId;
        ToolUtils.StringMap stringMap2 = new ToolUtils.StringMap();
        stringMap2.put("appid", str2).put("locale", Locale.getDefault().toString()).put(PowerCheckerProcessNotifier.KEY_SYNC, 1);
        if (isAbTestSupported(context)) {
            stringMap2.put("ab", str);
        }
        clientLoginInfo.cloudExtra = stringMap2.toString();
        clientLoginInfo.mClientEventDispatcher = clientEventDispatcher;
        return clientLoginInfo;
    }
}
