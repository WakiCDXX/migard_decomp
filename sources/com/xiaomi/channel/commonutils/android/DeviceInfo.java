package com.xiaomi.channel.commonutils.android;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.msa.MsaIdManager;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.channel.commonutils.string.Base64Coder;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.service.PushConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import miui.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class DeviceInfo {
    private static final String COMMA_SEPARATOR = ",";
    private static final int DEFAULT_IMEI_BLOCK_COUNT = 10;
    public static final int DUAL_SPACE_ID = 999;
    private static final String HW_DISABLE_OAID_PREFIX = "00000000-0000-0000-0000-000000000000";
    public static final String KEY_SUPPORT_GET_ANDROID_ID = "supportGetAndroidID";
    private static final int MAX_VDEVID_LEN = 128;
    private static final String PREF_FILE_NAME = "device_info";
    private static final String PREF_KEY_DEFAULT_ID = "default_id";
    private static final int RULE_1_IMIE = 1;
    private static final int RULE_2_UDID = 2;
    private static final int RULE_3_SDCARD = 3;
    private static final int RULE_4_OAID = 4;
    private static final int RULE_5_ANDROIDID = 5;
    private static final int RULE_6_DEFAULT = 6;
    private static final String STR_NULL = "null";
    private static final String STR_UNKNOWN = "unknown";
    private static final String TAG = "DeviceInfo";
    private static boolean sAllowedGetAndroidId = false;
    private static final Set<String> sAllowedList;
    private static String sCachedAndroidId = null;
    private static String sCachedDeviceId = null;
    private static String sCachedIMEI = null;
    private static String sCachedSimpleDeviceId = null;
    private static String sCachedSubIMEIS = "";
    private static final String SPLIT_CHAR = String.valueOf((char) 2);
    public static final String OLD_DEVICE_PREFIX = "a-";
    private static final String[] DEV_PREFIX_ARRAY = {"--", OLD_DEVICE_PREFIX, "u-", "v-", "o-", "g-", "d-"};

    static {
        HashSet hashSet = new HashSet();
        sAllowedList = hashSet;
        hashSet.add(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        hashSet.add("com.xiaomi.finddevice");
        hashSet.add("com.miui.securitycenter");
        sAllowedGetAndroidId = true;
    }

    @Deprecated
    public static String blockingGetIMEI(Context context) {
        return null;
    }

    @Deprecated
    private static String blockingGetIMEIWhenDeviceRegister(Context context) {
        return "";
    }

    @Deprecated
    public static String blockingGetSubIMEIS(Context context) {
        return null;
    }

    @Deprecated
    public static String blockingGetSubIMEISMd5(Context context) {
        return null;
    }

    @Deprecated
    public static ArrayList<String> blockinggetIMEIList(Context context) {
        return null;
    }

    private static boolean checkSupportGetAndroidID(Context context) {
        Bundle bundle;
        ApplicationInfo applicationInfo;
        Bundle bundle2;
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName())) {
            return true;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 128);
            if (packageInfo != null && (applicationInfo = packageInfo.applicationInfo) != null && (bundle2 = applicationInfo.metaData) != null && bundle2.containsKey(KEY_SUPPORT_GET_ANDROID_ID)) {
                boolean z2 = packageInfo.applicationInfo.metaData.getBoolean(KEY_SUPPORT_GET_ANDROID_ID, true);
                MyLog.v(TAG, "Get supportGetAndroidID from app metaData: " + z2);
                return z2;
            }
        } catch (Exception e2) {
            MyLog.e(TAG, "Check supportGetAndroidID from app metaData error: " + e2.getMessage());
        }
        try {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(context.getPackageName(), PushConstants.PUSH_SERVICE_CLASS_NAME_JAR);
            intent.setComponent(componentName);
            ServiceInfo serviceInfo = context.getPackageManager().getServiceInfo(componentName, 128);
            if (serviceInfo != null && (bundle = serviceInfo.metaData) != null && bundle.containsKey(KEY_SUPPORT_GET_ANDROID_ID)) {
                MyLog.v(TAG, "The metaData of XMPushService contains key supportGetAndroidID,so return false.");
                return false;
            }
        } catch (Exception e3) {
            MyLog.e(TAG, "Check supportGetAndroidID from XMPushService metaData error: " + e3.getMessage());
        }
        MyLog.v(TAG, "Not configure the metaData key of supportGetAndroidID，return true by default.");
        return true;
    }

    private static float formatRamFromProcMeminfo(int i2) {
        float f2 = (((((i2 + 102400) / LogType.LOG_TYPE_SAMPLING_CLOCK) + 1) * LogType.LOG_TYPE_SAMPLING_CLOCK) / 1024.0f) / 1024.0f;
        double d2 = f2;
        return d2 > 0.5d ? (float) Math.ceil(d2) : f2;
    }

    private static String generateDefaultId(Context context) {
        return Base64Coder.encodeString(Build.BRAND + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + Build.MODEL + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + Build.VERSION.SDK_INT + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + Build.VERSION.RELEASE + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + Build.VERSION.INCREMENTAL + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + getSpaceId() + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + context.getPackageName() + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + System.currentTimeMillis() + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + XMStringUtils.generateRandomString(16));
    }

    public static String getAndroidId(Context context) {
        String str = sCachedAndroidId;
        if (str != null || !sAllowedGetAndroidId) {
            return str;
        }
        boolean zCheckSupportGetAndroidID = checkSupportGetAndroidID(context);
        sAllowedGetAndroidId = zCheckSupportGetAndroidID;
        if (!zCheckSupportGetAndroidID) {
            return null;
        }
        try {
            sCachedAndroidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        } catch (Throwable th) {
            MyLog.w("failure to get androidId: " + th);
        }
        return sCachedAndroidId;
    }

    private static String getDefaultId(Context context) {
        String string = context.getSharedPreferences(PREF_FILE_NAME, 0).getString(PREF_KEY_DEFAULT_ID, null);
        if (!TextUtils.isEmpty(string)) {
            return string;
        }
        String strGenerateDefaultId = generateDefaultId(context);
        saveDefaultId(context, strGenerateDefaultId);
        return strGenerateDefaultId;
    }

    private static String getDevPrefix(int i2) {
        if (i2 > 0) {
            String[] strArr = DEV_PREFIX_ARRAY;
            if (i2 < strArr.length) {
                return strArr[i2];
            }
        }
        return DEV_PREFIX_ARRAY[0];
    }

    public static synchronized String getDeviceId(Context context, boolean z2) {
        int i2;
        try {
            if (sCachedDeviceId == null) {
                String androidId = getAndroidId(context);
                String strBlockingGetIMEI = "";
                if (!MIUIUtils.isGlobalRegion()) {
                    strBlockingGetIMEI = z2 ? blockingGetIMEI(context) : blockingGetIMEIWhenDeviceRegister(context);
                }
                String serialNum = getSerialNum(context);
                if (isInvalidStr(strBlockingGetIMEI) && isInvalidStr(serialNum)) {
                    String udid = MsaIdManager.getInstance(context).getUDID();
                    if (TextUtils.isEmpty(udid)) {
                        String oaid = MsaIdManager.getInstance(context).getOAID();
                        if (!TextUtils.isEmpty(oaid) && !oaid.startsWith(HW_DISABLE_OAID_PREFIX)) {
                            i2 = 4;
                            androidId = oaid;
                        } else if (TextUtils.isEmpty(androidId)) {
                            androidId = getDefaultId(context);
                            i2 = 6;
                        } else {
                            i2 = 5;
                        }
                    } else {
                        androidId = udid + androidId;
                        i2 = 2;
                    }
                } else {
                    androidId = strBlockingGetIMEI + androidId + serialNum;
                    i2 = 1;
                }
                MyLog.i("devid rule select:" + i2);
                if (i2 == 3) {
                    sCachedDeviceId = androidId;
                } else {
                    sCachedDeviceId = getDevPrefix(i2) + XMStringUtils.getSHA1Digest(androidId);
                }
            }
        } catch (Throwable th) {
            throw th;
        }
        return sCachedDeviceId;
    }

    public static synchronized String getDeviceId1(Context context) {
        return OLD_DEVICE_PREFIX + XMStringUtils.getSHA1Digest(((String) null) + getAndroidId(context) + ((String) null));
    }

    @Deprecated
    public static String getGaid(Context context) {
        return null;
    }

    @Deprecated
    public static ArrayList<String> getIMEIList(Context context) {
        return null;
    }

    private static int getImeiBlockCount() {
        return 0;
    }

    public static synchronized String getInstanceId(Context context) {
        return XMStringUtils.getSHA1Digest(getAndroidId(context) + ((String) null));
    }

    @Deprecated
    public static String getMacAddress(Context context) {
        return "";
    }

    private static double getNum(double d2) {
        int i2 = 1;
        while (true) {
            double d3 = i2;
            if (d3 >= d2) {
                return d3;
            }
            i2 <<= 1;
        }
    }

    static String getPhoneInfoHash() {
        return "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.DISPLAY.length() % 10) + (Build.HOST.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
    }

    public static int getRamFromProcMeminfo() throws Throwable {
        BufferedReader bufferedReader;
        Throwable th;
        String[] strArrSplit;
        int i2 = 0;
        if (new File("/proc/meminfo").exists()) {
            BufferedReader bufferedReader2 = null;
            try {
                try {
                    bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), DateUtils.FORMAT_ABBREV_WEEKDAY);
                    try {
                        String line = bufferedReader.readLine();
                        if (!TextUtils.isEmpty(line) && (strArrSplit = line.split("\\s+")) != null && strArrSplit.length >= 2 && TextUtils.isDigitsOnly(strArrSplit[1])) {
                            i2 = Integer.parseInt(strArrSplit[1]);
                        }
                        bufferedReader.close();
                    } catch (Exception unused) {
                        bufferedReader2 = bufferedReader;
                        if (bufferedReader2 != null) {
                            bufferedReader2.close();
                        }
                        return i2;
                    } catch (Throwable th2) {
                        th = th2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException unused2) {
                            }
                        }
                        throw th;
                    }
                } catch (IOException unused3) {
                }
            } catch (Exception unused4) {
            } catch (Throwable th3) {
                bufferedReader = null;
                th = th3;
            }
        }
        return i2;
    }

    public static String getRamSize() {
        return formatRamFromProcMeminfo(getRamFromProcMeminfo()) + "GB";
    }

    public static String getRamSizeOriginal() {
        return getRamFromProcMeminfo() + "KB";
    }

    public static String getRomSize() {
        return getNum(((getSize(Environment.getDataDirectory()) / 1024.0d) / 1024.0d) / 1024.0d) + "GB";
    }

    public static String getRomSizeOriginal() {
        return (getSize(Environment.getDataDirectory()) / 1024) + "KB";
    }

    @Deprecated
    public static String getSerialNum(Context context) {
        return null;
    }

    public static String getSimOperatorName(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getSimOperatorName();
    }

    public static synchronized String getSimpleDeviceId(Context context) {
        String str = sCachedSimpleDeviceId;
        if (str != null) {
            return str;
        }
        String sHA1Digest = XMStringUtils.getSHA1Digest(getAndroidId(context) + getSerialNum(context));
        sCachedSimpleDeviceId = sHA1Digest;
        return sHA1Digest;
    }

    private static long getSize(File file) {
        StatFs statFs = new StatFs(file.getPath());
        return statFs.getBlockSizeLong() * statFs.getBlockCountLong();
    }

    @TargetApi(17)
    public static int getSpaceId() {
        Object objCallStaticMethod = JavaCalls.callStaticMethod("android.os.UserHandle", "myUserId", new Object[0]);
        if (objCallStaticMethod == null) {
            return -1;
        }
        return ((Integer) Integer.class.cast(objCallStaticMethod)).intValue();
    }

    @Deprecated
    private static boolean isAllowedAccessDeviceIdentifiers(Context context) {
        return false;
    }

    public static boolean isCharging(Context context) {
        Intent intentRegisterReceiver = ReceiverHelper.registerReceiver(context, (BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), (String) null, (Handler) null);
        if (intentRegisterReceiver == null) {
            return false;
        }
        int intExtra = intentRegisterReceiver.getIntExtra("status", -1);
        return intExtra == 2 || intExtra == 5;
    }

    public static boolean isDualAppSpaceUid(int i2) {
        Integer num = (Integer) JavaCalls.callStaticMethod("android.os.UserHandle", "getUserId", Integer.valueOf(i2));
        return num != null && num.intValue() == 999;
    }

    public static boolean isDualAppUninstalled(Context context, String str) {
        ApplicationInfo applicationInfo;
        PackageInfo packageInfo = (PackageInfo) JavaCalls.callMethod(context.getPackageManager(), "getPackageInfoAsUser", str, 0, Integer.valueOf(DUAL_SPACE_ID));
        return packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null || (applicationInfo.flags & 8388608) != 8388608;
    }

    private static boolean isInvalidStr(String str) {
        if (str == null) {
            return true;
        }
        String strTrim = str.trim();
        return strTrim.length() == 0 || strTrim.equalsIgnoreCase(STR_NULL) || strTrim.equalsIgnoreCase(STR_UNKNOWN);
    }

    public static boolean isMainSpace() {
        return getSpaceId() <= 0;
    }

    public static boolean isSameSpace() {
        Integer num = (Integer) JavaCalls.callStaticMethod("android.app.ActivityManager", "getCurrentUser", new Object[0]);
        return num != null && num.intValue() == getSpaceId();
    }

    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(NightStandbyRecord.KEY_POWER);
        return powerManager == null || powerManager.isScreenOn();
    }

    private static boolean isSupportVDevid(Context context) {
        return false;
    }

    @Deprecated
    public static String quicklyGetIMEI(Context context) {
        return null;
    }

    @Deprecated
    public static String quicklyGetSubIMEIS(Context context) {
        return "";
    }

    @Deprecated
    public static String quicklyGetSubIMEISMd5(Context context) {
        return "";
    }

    private static void saveDefaultId(Context context, String str) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, 0);
        if (TextUtils.isEmpty(sharedPreferences.getString(PREF_KEY_DEFAULT_ID, null))) {
            sharedPreferences.edit().putString(PREF_KEY_DEFAULT_ID, str).apply();
        } else {
            MyLog.w("default_id exist,do not change it.");
        }
    }

    public static boolean startsWithDevPrefix(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        int i2 = 0;
        while (true) {
            String[] strArr = DEV_PREFIX_ARRAY;
            if (i2 >= strArr.length) {
                return false;
            }
            if (str.startsWith(strArr[i2])) {
                return true;
            }
            i2++;
        }
    }

    private static boolean verifyImei(String str) {
        return !TextUtils.isEmpty(str) && str.length() <= 15 && str.length() >= 14 && XMStringUtils.isNumberAndLetter(str) && !XMStringUtils.isTheSameChars(str);
    }

    private static boolean verifyMac(String str) {
        if (TextUtils.isEmpty(str) || str.length() != 17 || !Pattern.compile("^([A-Fa-f0-9]{2}[-,:]){5}[A-Fa-f0-9]{2}$").matcher(str).matches()) {
            return false;
        }
        char cCharAt = str.charAt(0);
        if (cCharAt == '0' || cCharAt == 'f' || cCharAt == 'F') {
            int i2 = 1;
            while (i2 < str.length()) {
                if (str.charAt(i2) != cCharAt) {
                    return true;
                }
                if (i2 < str.length() - 1) {
                    int i3 = i2 + 1;
                    if (str.charAt(i3) == '-' || str.charAt(i3) == ':') {
                        i2 = i3;
                    }
                }
                i2++;
            }
        }
        return false;
    }
}
