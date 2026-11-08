package com.xiaomi.push.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.service.notification.CustomNotificationConstants;
import com.xiaomi.push.service.notification.StatusBarNotificationWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class NotificationUtils {
    public static final String EXTRA_CUSTOM_HEIGHT = "miui.customHeight";
    public static final String EXTRA_ENABLE_FLOAT = "miui.enableFloat";
    public static final String EXTRA_ENABLE_KEYGUARD = "miui.enableKeyguard";
    public static final String EXTRA_HIDE_BORDER = "miui.customHideBorder";
    private static final String EXTRA_IMPORTANCE_IS_PRIORITY = "is_priority";
    private static final String EXTRA_IMPORTANCE_PRIORITY_CLASS = "mipush_class";
    private static final String EXTRA_MESSAGE_COUNT = "miui.messageCount";
    private static final String FIELD_extraNotification = "extraNotification";
    private static final String METHOD_getTargetPkg = "getTargetPkg";
    private static final String METHOD_isEnableKeyguard = "isEnableKeyguard";
    private static final String METHOD_setEnableFloat = "setEnableFloat";
    private static final String METHOD_setEnableKeyguard = "setEnableKeyguard";
    private static final String METHOD_setMessageCount = "setMessageCount";
    private static final String METHOD_setTargetPkg = "setTargetPkg";
    private static final String NOTIFICATION_PROVIDER = "content://statusbar.notification";
    private static final String SETTINGS_USER_AGGREGATE = "user_aggregate";
    private static final String SETTINGS_USER_FOLD = "user_fold";
    public static final Third<String, String, String> SETTING_BADGE;
    public static final Third<String, String, String> SETTING_FLOAT;
    public static final Third<String, String, String> SETTING_KEYGUARD;
    public static final Third<String, String, String> SETTING_LIGHTS;
    public static final Third<String, String, String> SETTING_ONGOING;
    public static final Third<String, String, String> SETTING_SOUND;
    public static final Third<String, String, String> SETTING_TOTAL;
    public static final Third<String, String, String> SETTING_VIBRATE;
    public static final int STATUS_NONE = -1;
    public static final int STATUS_OFF = 0;
    public static final int STATUS_ON = 1;
    private static final int VALUE_DISABLE_DEFAULT = -2;
    private static final int VALUE_DISABLE_NOT_SUPPORT = -3;
    private static final int VALUE_DISABLE_USER = -1;
    private static final int VALUE_ENABLE_DEFAULT = 2;
    private static final int VALUE_ENABLE_USER = 1;
    private static final int VALUE_INVALID = 0;
    static Boolean mHasBatchSwitchInterface;
    private static final String[] XIAOMI_BROWSER_PKG_ARRAY = {PushConstants.XIAOMI_GLOBALBROWSER_PACKAGE_NAME, PushConstants.XIAOMI_BROWSER_PACKAGE_NAME};
    private static String sBestBrowserPkg = null;

    public static class Third<F, S, T> {
        S getter;
        T paramKey;
        F setter;

        private Third(F f2, S s2, T t2) {
            this.setter = f2;
            this.getter = s2;
            this.paramKey = t2;
        }
    }

    static {
        String str = "getNotificationSettings";
        SETTING_TOTAL = new Third<>(str, str, str);
        String str2 = "canSound";
        SETTING_SOUND = new Third<>("setSound", str2, str2);
        String str3 = "canVibrate";
        SETTING_VIBRATE = new Third<>("setVibrate", str3, str3);
        String str4 = "canLights";
        SETTING_LIGHTS = new Third<>("setLights", str4, str4);
        String str5 = "canShowOnKeyguard";
        SETTING_KEYGUARD = new Third<>("setShowOnKeyguard", str5, str5);
        SETTING_FLOAT = new Third<>("setFloat", "canFloat", "canShowFloat");
        String str6 = "canShowBadge";
        SETTING_BADGE = new Third<>("setShowBadge", str6, str6);
        String str7 = "canShowOngoing";
        SETTING_ONGOING = new Third<>("setShowOngoing", str7, str7);
    }

    private static Bundle callNotificationProvider(Context context, String str, String str2, String str3, Bundle bundle) throws IllegalArgumentException {
        if (context == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            throw new IllegalArgumentException("call notification provider failed!");
        }
        Bundle bundle2 = new Bundle();
        bundle2.putString("package", str2);
        if (!TextUtils.isEmpty(str3)) {
            bundle2.putString(MIPushNotificationHelper.NOTIFICATION_CHANNEL_ID, str3);
        }
        if (bundle != null) {
            bundle2.putAll(bundle);
        }
        return context.getContentResolver().call(Uri.parse(NOTIFICATION_PROVIDER), str, (String) null, bundle2);
    }

    public static Bundle checkAllNotificationSetting(Context context, String str, String str2) {
        try {
            return callNotificationProvider(context, SETTING_TOTAL.getter, str, str2, null);
        } catch (Exception unused) {
            return null;
        }
    }

    public static int checkNotificationSetting(Context context, String str, String str2, Third<String, String, String> third) {
        if (third == null) {
            return -1;
        }
        try {
            Bundle bundleCallNotificationProvider = callNotificationProvider(context, third.getter, str, str2, null);
            if (bundleCallNotificationProvider == null || !bundleCallNotificationProvider.containsKey(third.paramKey)) {
                return -1;
            }
            return bundleCallNotificationProvider.getBoolean(third.paramKey) ? 1 : 0;
        } catch (Exception unused) {
            return -1;
        }
    }

    public static void cpBundleToMap(Bundle bundle, Map<String, String> map, String str) {
        if (bundle == null || map == null || TextUtils.isEmpty(str)) {
            MyLog.w("cp b to map fail:" + str);
            return;
        }
        Object obj = bundle.get(str);
        if (obj == null) {
            map.remove(str);
        } else {
            map.put(str, String.valueOf(obj));
        }
    }

    public static void cpMapToBundle(Map<String, String> map, Bundle bundle, String str) {
        if (map == null || bundle == null || TextUtils.isEmpty(str)) {
            MyLog.w("cp map to b fail:" + str);
            return;
        }
        if (TextUtils.isEmpty(map.get(str))) {
            bundle.remove(str);
        } else {
            bundle.putString(str, map.get(str));
        }
    }

    public static float getDimenFromSystemUI(Context context, String str, int i2) {
        try {
            Context contextCreatePackageContext = context.createPackageContext("com.android.systemui", 0);
            if (contextCreatePackageContext != null) {
                Resources resources = contextCreatePackageContext.getResources();
                return resources.getDimension(resources.getIdentifier(str, "dimen", contextCreatePackageContext.getPackageName()));
            }
        } catch (Throwable th) {
            MyLog.e("get dimen from systemui failed: " + th);
        }
        return i2;
    }

    static int getIdForSmallIconFromTargetPkg(Context context, String str) {
        return AppInfoUtils.getAppIconId(context, str);
    }

    public static String getMsgBusinessType(Object obj) {
        return (String) getValue(obj, MIPushNotificationHelper.NOTIFICATION_EXTRA_MSG_BUSI_TYPE, "");
    }

    public static String getMsgId(Notification notification) {
        return (String) getValue(notification, "message_id", "");
    }

    public static String getTargetAppId(Notification notification) {
        return (String) getValue(notification, MIPushNotificationHelper.NOTIFICATION_EXTRA_LOCAL_PAID_STRING, "");
    }

    public static String getTargetPackage(Notification notification) {
        Object field;
        String string = null;
        try {
            Bundle bundle = notification.extras;
            string = bundle != null ? bundle.getString("target_package") : null;
            return (!TextUtils.isEmpty(string) || (field = JavaCalls.getField(notification, FIELD_extraNotification)) == null) ? string : (String) JavaCalls.callMethod(field, METHOD_getTargetPkg, new Object[0]);
        } catch (Exception unused) {
            return string;
        }
    }

    public static int getUserAggregate(ContentResolver contentResolver) {
        try {
            return Settings.Global.getInt(contentResolver, SETTINGS_USER_AGGREGATE, 0);
        } catch (Exception e2) {
            MyLog.w("get user aggregate failed, " + e2);
            return 0;
        }
    }

    public static int getUserFold(ContentResolver contentResolver) {
        try {
            return Settings.Global.getInt(contentResolver, SETTINGS_USER_FOLD, 0);
        } catch (Exception e2) {
            MyLog.w("get user fold failed, " + e2);
            return 0;
        }
    }

    public static <T> T getValue(Object obj, String str, T t2) {
        Object objResolveNotification = null;
        try {
            if (obj instanceof Notification) {
                objResolveNotification = resolveNotification((Notification) obj, str);
            } else if (obj instanceof Map) {
                objResolveNotification = ((Map) obj).get(str);
            } else if (obj instanceof Bundle) {
                objResolveNotification = ((Bundle) obj).get(str);
            } else {
                MyLog.w("not support get value from classType:" + obj);
            }
        } catch (Exception e2) {
            MyLog.w("get value error " + e2);
        }
        return objResolveNotification == null ? t2 : (T) objResolveNotification;
    }

    public static boolean hasBatchSwitchInterface() {
        if (mHasBatchSwitchInterface == null) {
            Bundle bundleCheckPermissionAllStatus = ChannelPermissions.checkPermissionAllStatus(PushConstants.PUSH_SERVICE_PACKAGE_NAME, null);
            if (bundleCheckPermissionAllStatus == null || bundleCheckPermissionAllStatus.isEmpty()) {
                mHasBatchSwitchInterface = Boolean.FALSE;
            } else {
                mHasBatchSwitchInterface = Boolean.TRUE;
            }
        }
        return mHasBatchSwitchInterface.booleanValue();
    }

    public static boolean isEnableKeyguard(Notification notification, boolean z2) {
        Boolean boolValueOf = Boolean.valueOf(z2);
        try {
            Bundle bundle = notification.extras;
            if (bundle != null && bundle.containsKey(EXTRA_ENABLE_KEYGUARD)) {
                return notification.extras.getBoolean(EXTRA_ENABLE_KEYGUARD, z2);
            }
            Object field = JavaCalls.getField(notification, FIELD_extraNotification);
            if (field != null) {
                Boolean bool = (Boolean) JavaCalls.callMethod(field, METHOD_isEnableKeyguard, new Object[0]);
                if (bool != null) {
                    try {
                        z2 = bool.booleanValue();
                    } catch (Exception unused) {
                        boolValueOf = bool;
                    }
                }
                boolValueOf = Boolean.valueOf(z2);
            }
            return boolValueOf.booleanValue();
        } catch (Exception unused2) {
        }
    }

    public static boolean isNotificationFromXmsf(Context context, StatusBarNotification statusBarNotification) {
        if (!MIUIUtils.isXMSF(context) || statusBarNotification == null) {
            return false;
        }
        return MIUIUtils.isXMSF(statusBarNotification.getPackageName()) || MIUIUtils.isXMSF(String.valueOf(JavaCalls.callMethod(statusBarNotification, "getOpPkg", new Object[0])));
    }

    public static boolean isSuppressNotificationEffects(Map<String, String> map) {
        return Boolean.parseBoolean((String) getValue(map, MIPushNotificationHelper.FLAG_DISABLE_SUPPRESS_NOTIFICATION_EFFECTS, "true"));
    }

    public static boolean isUserAggregate(ContentResolver contentResolver) {
        int userAggregate = getUserAggregate(contentResolver);
        return userAggregate == 1 || userAggregate == 2;
    }

    public static boolean isUserFold(ContentResolver contentResolver) {
        int userFold = getUserFold(contentResolver);
        return userFold == 1 || userFold == 2;
    }

    public static Notification.Action[] resolveActions(Notification notification) {
        Parcelable[] parcelableArray;
        Notification.Action[] actionArr = notification.actions;
        if (actionArr != null) {
            return actionArr;
        }
        Bundle bundle = notification.extras;
        if (bundle == null || (parcelableArray = bundle.getParcelableArray(CustomNotificationConstants.EXTRA_CUSTOM_ACTIONS)) == null) {
            return null;
        }
        return (Notification.Action[]) Arrays.copyOf(parcelableArray, parcelableArray.length, Notification.Action[].class);
    }

    public static String resolveContent(Notification notification) {
        CharSequence charSequence;
        Bundle bundle = notification.extras;
        if (bundle != null) {
            charSequence = bundle.getCharSequence("android.text");
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = notification.extras.getCharSequence("android.bigText");
            }
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = notification.extras.getCharSequence(CustomNotificationConstants.EXTRA_CUSTOM_CONTENT);
            }
        } else {
            charSequence = null;
        }
        return charSequence != null ? charSequence.toString() : "";
    }

    @TargetApi(23)
    public static Icon resolveLargeIcon(Context context, Notification notification, boolean z2) {
        Integer num;
        if (notification.getLargeIcon() != null) {
            return notification.getLargeIcon();
        }
        Bitmap bitmap = notification.largeIcon;
        if (bitmap != null) {
            return Icon.createWithBitmap(bitmap);
        }
        Bundle bundle = notification.extras;
        if (bundle != null && notification.contentView != null && bundle.getBoolean(CustomNotificationConstants.EXTRA_CUSTOM_NOTIFICATION)) {
            int i2 = notification.extras.getInt(CustomNotificationConstants.EXTRA_CUSTOM_LARGE_ICON_ID);
            ArrayList arrayList = (ArrayList) JavaCalls.getField(notification.contentView, "mActions");
            if (arrayList != null) {
                for (Object obj : arrayList) {
                    if ("BitmapReflectionAction".equals(obj.getClass().getSimpleName()) && (num = (Integer) JavaCalls.getField(obj, "viewId")) != null && num.intValue() == i2) {
                        return Icon.createWithBitmap((Bitmap) JavaCalls.getField(obj, "bitmap"));
                    }
                }
            }
        }
        if (!z2) {
            return null;
        }
        try {
            return Icon.createWithResource(getTargetPackage(notification), getIdForSmallIconFromTargetPkg(context, getTargetPackage(notification)));
        } catch (Exception unused) {
            return null;
        }
    }

    public static Bitmap resolveLargeIconBitmap(Context context, Notification notification, boolean z2) {
        try {
            Icon iconResolveLargeIcon = resolveLargeIcon(context, notification, z2);
            if (iconResolveLargeIcon != null) {
                return (Bitmap) JavaCalls.callMethodOrThrow(iconResolveLargeIcon, "getBitmap", new Object[0]);
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public static <T> T resolveNotification(Notification notification, String str) {
        Bundle bundle = notification.extras;
        if (bundle == null) {
            return null;
        }
        try {
            return (T) bundle.get(str);
        } catch (Exception unused) {
            return null;
        }
    }

    @TargetApi(23)
    public static Icon resolveSmallIcon(Context context, Notification notification, boolean z2) {
        Icon smallIcon = notification.getSmallIcon();
        if (smallIcon != null) {
            return smallIcon;
        }
        int idForSmallIconFromTargetPkg = notification.icon;
        if (idForSmallIconFromTargetPkg == 0 && z2) {
            idForSmallIconFromTargetPkg = getIdForSmallIconFromTargetPkg(context, getTargetPackage(notification));
        }
        try {
            return Icon.createWithResource(getTargetPackage(notification), idForSmallIconFromTargetPkg);
        } catch (Exception unused) {
            return smallIcon;
        }
    }

    public static String resolveTitle(Notification notification) {
        CharSequence charSequence;
        Bundle bundle = notification.extras;
        if (bundle != null) {
            charSequence = bundle.getCharSequence("android.title");
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = notification.extras.getCharSequence("android.title.big");
            }
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = notification.extras.getCharSequence(CustomNotificationConstants.EXTRA_CUSTOM_TITLE);
            }
        } else {
            charSequence = null;
        }
        return charSequence != null ? charSequence.toString() : "";
    }

    static void setBestPkgForUrlIfNeed(Context context, String str, Intent intent) {
        if (intent == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(str)) {
            arrayList.add(str);
        }
        arrayList.addAll(Arrays.asList(XIAOMI_BROWSER_PKG_ARRAY));
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            String str2 = (String) arrayList.get(i2);
            if (!TextUtils.isEmpty(str2)) {
                Intent intent2 = new Intent(intent);
                intent2.setPackage(str2);
                try {
                    if (context.getPackageManager().resolveActivity(intent2, LogType.LOG_TYPE_SAMPLING_INTERRUPT) != null) {
                        intent.setPackage(str2);
                        break;
                    }
                    continue;
                } catch (Exception e2) {
                    MyLog.w("can't match url intent. " + e2);
                }
            }
        }
        intent.setPackage(intent.getPackage());
    }

    static void setEnableFloat(Notification notification, boolean z2) {
        try {
            Bundle bundle = notification.extras;
            if (bundle != null) {
                bundle.putBoolean(EXTRA_ENABLE_FLOAT, z2);
            }
            Object field = JavaCalls.getField(notification, FIELD_extraNotification);
            if (field != null) {
                JavaCalls.callMethod(field, METHOD_setEnableFloat, Boolean.valueOf(z2));
            }
        } catch (Exception unused) {
        }
    }

    public static void setEnableKeyguard(Notification notification, boolean z2) {
        try {
            Bundle bundle = notification.extras;
            if (bundle != null) {
                bundle.putBoolean(EXTRA_ENABLE_KEYGUARD, z2);
            }
            Object field = JavaCalls.getField(notification, FIELD_extraNotification);
            if (field != null) {
                JavaCalls.callMethod(field, METHOD_setEnableKeyguard, Boolean.valueOf(z2));
            }
        } catch (Exception unused) {
        }
    }

    static void setImportanceSection(Notification notification, int i2, int i3) {
        if (notification != null) {
            if (notification.extras == null) {
                notification.extras = new Bundle();
            }
            notification.extras.putInt(EXTRA_IMPORTANCE_IS_PRIORITY, i2);
            notification.extras.putInt(EXTRA_IMPORTANCE_PRIORITY_CLASS, i3);
        }
    }

    static void setMessageCount(Notification notification, int i2) {
        try {
            Bundle bundle = notification.extras;
            if (bundle != null) {
                bundle.putInt(EXTRA_MESSAGE_COUNT, i2);
            }
            Object field = JavaCalls.getField(notification, FIELD_extraNotification);
            if (field != null) {
                JavaCalls.callMethod(field, METHOD_setMessageCount, Integer.valueOf(i2));
            }
        } catch (Exception unused) {
        }
    }

    public static boolean setNotificationSetting(Context context, String str, String str2, Third<String, String, String> third, boolean z2) {
        if (third != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putBoolean(third.paramKey, z2);
                callNotificationProvider(context, third.setter, str, str2, bundle);
                return true;
            } catch (Exception unused) {
            }
        }
        return false;
    }

    static void setTargetPackage(Notification notification, String str) {
        try {
            Bundle bundle = notification.extras;
            if (bundle != null) {
                bundle.putString("target_package", str);
            }
            Object field = JavaCalls.getField(notification, FIELD_extraNotification);
            if (field != null) {
                JavaCalls.callMethod(field, METHOD_setTargetPkg, str);
            }
        } catch (Exception unused) {
        }
    }

    public static boolean suppressNotificationEffects(Notification.Builder builder, boolean z2) {
        builder.setGroupAlertBehavior(z2 ? 2 : 1);
        return true;
    }

    public static boolean isNotificationFromXmsf(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper) {
        if (!MIUIUtils.isXMSF(context) || statusBarNotificationWrapper == null) {
            return false;
        }
        return MIUIUtils.isXMSF(statusBarNotificationWrapper.getPackageName()) || MIUIUtils.isXMSF(String.valueOf(statusBarNotificationWrapper.getOpPkg()));
    }
}
