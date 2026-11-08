package miui.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public final class ExtraNetwork {
    private static final String ACTION_NETWORK_ASSISTANT_SMS_REPORT = "miui.intent.action.NETWORKASSISTANT_SMS_REPORT";
    public static final String ACTION_NETWORK_BLOCKED = "miui.intent.action.NETWORK_BLOCKED";
    public static final String ACTION_NETWORK_CONNECTED = "miui.intent.action.NETWORK_CONNECTED";
    private static final String ACTION_TRAFFIC_SETTING = "miui.intent.action.NETWORKASSISTANT_OPERATOR_SETTING";
    private static final String ACTION_TRAFFIC_SETTING_INTERNATIONAL = "miui.intent.action.NETWORKASSISTANT_MONTH_PACKAGE_SETTING";
    public static final String BUNDLE_KEY_COMMON = "bundle_key_com";
    public static final String BUNDLE_KEY_HAS_MENU = "bundle_key_has_menu";
    private static final String BUNDLE_KEY_OTHER_APP = "bundle_key_from_other_task";
    public static final String BUNDLE_KEY_PURCHASE_FROM = "bundle_key_purchase_from";
    public static final String BUNDLE_KEY_SLOTID = "bundle_key_slotid";
    private static final String BUNDLE_KEY_SLOT_ID = "sim_slot_num_tag";
    public static final String BUNDLE_KEY_TITLE = "bundle_key_title";
    public static final String BUNDLE_KEY_URL = "bundle_key_url";
    private static final String COLUMN_NAME_MONTH_USED = "month_used";
    private static final String COLUMN_NAME_MONTH_WARNING = "month_warning";
    private static final String COLUMN_NAME_PACKAGE_REMAINED = "package_remained";
    private static final String COLUMN_NAME_PACKAGE_TOTAL = "package_total";
    private static final String COLUMN_NAME_PACKAGE_USED = "package_used";
    private static final String COLUMN_NAME_SLOT_NUM = "slot_num";
    private static final String COLUMN_NAME_SUPPORT = "package_setted";
    private static final String COLUMN_NAME_TODAY_USED = "today_used";
    private static final String COLUMN_NAME_TOTAL_LIMIT = "total_limit";
    public static final int CORRECTION_TYPE_BILL = 2;
    public static final int CORRECTION_TYPE_CALLTIME = 4;
    public static final int CORRECTION_TYPE_TRAFFIC = 1;
    private static final String EXTRA_MIUI_STARTING_WINDOW_LABEL = ":miui:starting_window_label";
    public static final String FIREWALL_MOBILE_RULE = "mobile_rule";
    private static final String FIREWALL_MOBILE_RULE_SLOTNUM = "mobile_rule_slot";
    public static final String FIREWALL_PACKAGE_NAME = "package_name";
    private static final String FIREWALL_SOURCE_PACKAGE_NAME = "source_package_name";
    private static final String FIREWALL_TEMP_MOBILE_RULE = "temp_mobile_rule";
    private static final String FIREWALL_TEMP_MOBILE_RULE_SLOTNUM = "temp_mobile_rule_slot";
    private static final String FIREWALL_TEMP_WIFI_RULE = "temp_wifi_rule";
    private static final String FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/firewall/%s";
    public static final String FIREWALL_WIFI_RULE = "wifi_rule";
    public static final String FROM_PKGNAME = "from_pkgname";
    public static final String IMSI = "imsi";
    private static final String KEY_CORRECTION_TYPE = "correction_type";
    private static final String MOBILE_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/mobile_firewall/%s/%s";
    public static final String MOBILE_RXBYTES = "mobile_rxbytes";
    public static final String MOBILE_TXBYTES = "mobile_txbytes";
    private static final String NETWORKASSISTANT_PURCHASE_ACTION = "miui.intent.action.NETWORKASSISTANT_TRAFFIC_PURCHASE";
    public static final String STORAGE_TIME = "storage_time";
    private static final String TAG = "ExtraNetwork";
    private static final String TEMP_MOBILE_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/temp_mobile_firewall/%s/%s";
    private static final String TEMP_WIFI_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/temp_wifi_firewall/%s";
    public static final String TO_PKGNAME = "to_pkgname";
    public static final String TRACK_PURCHASE_FROM_LOCK_SCREEN_TRAFFIC = "100010";
    public static final String TRACK_PURCHASE_FROM_NETWORK_ASSISTANT_MAIN_PAGE = "100002";
    public static final String TRACK_PURCHASE_FROM_NETWORK_ASSISTANT_MAIN_TOOLBAR = "100001";
    public static final String TRACK_PURCHASE_FROM_PUSH = "100007";
    public static final String TRACK_PURCHASE_FROM_SERCURITY_CENTER_EXAM = "100008";
    public static final String TRACK_PURCHASE_FROM_STATUS_BAR = "100003";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_OVER_LIMIT_DIALOG = "100006";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_OVER_LIMIT_NOTIFY = "100005";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_SORTED = "100009";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_WARNING_NOTIFY = "100004";
    private static final String TRAFFIC_DISTRIBUTION_URI_STR = "content://com.miui.networkassistant.provider/traffic_distribution";
    public static final String TRAFFIC_PURCHASE_ENABLED = "traffic_purchase_enabled";
    private static final String TRAFFIC_PURCHASE_STATUS_URI_STR = "content://com.miui.networkassistant.provider/na_traffic_purchase";
    private static final String TRAFFIC_PURCHASE_STATUS_URI_STR_ISMI = "content://com.miui.networkassistant.provider/na_traffic_purchase/slotId/%d";
    private static final String URI_BILL_PACKAGE_DETAIL = "content://com.miui.networkassistant.provider/bill_detail";
    private static final String URI_CALL_TIME_PACKAGE_DETAIL = "content://com.miui.networkassistant.provider/calltime_detail";
    private static final String URI_NETWORK_TRAFFIC_INFO = "content://com.miui.networkassistant.provider/datausage_status";
    private static final String URI_SMS_CORRECTION = "content://com.miui.networkassistant.provider/sms_correction";
    private static final String WIFI_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/wifi_firewall/%s";
    public static final String WIFI_RXBYTES = "wifi_rxbytes";
    public static final String WIFI_TXBYTES = "wifi_txbytes";

    public static final class DataUsageDetail {
        public long monthTotal;
        public long monthUsed;
        public long monthWarning;
        public long todayUsed;

        public DataUsageDetail(long j2, long j3, long j4, long j5) {
            this.monthTotal = j2;
            this.monthUsed = j3;
            this.monthWarning = j4;
            this.todayUsed = j5;
        }

        public String toString() {
            return String.format("monthTotal:%s, monthUsed:%s, monthWarning:%s, todayUsed:%s", Long.valueOf(this.monthTotal), Long.valueOf(this.monthUsed), Long.valueOf(this.monthWarning), Long.valueOf(this.todayUsed));
        }
    }

    public static final class PackageDetail {
        public boolean isSupport;
        public long packageRemained;
        public long packageTotal;
        public long packageUsed;
        public int slotNum;

        public PackageDetail(long j2, long j3, long j4, int i2, boolean z2) {
            this.packageTotal = j2;
            this.packageUsed = j3;
            this.packageRemained = j4;
            this.slotNum = i2;
            this.isSupport = z2;
        }

        public String toString() {
            return String.format("packageTotal:%s, packageUsed:%s, packageRemained:%s, slotNum:%s, isSupport:%s", Long.valueOf(this.packageTotal), Long.valueOf(this.packageUsed), Long.valueOf(this.packageRemained), Integer.valueOf(this.slotNum), Boolean.valueOf(this.isSupport));
        }
    }

    public static List<PackageDetail> getBillPackageDetail(Context context) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(Uri.parse(URI_BILL_PACKAGE_DETAIL), null, null, null, null);
                if (cursorQuery != null) {
                    while (cursorQuery.moveToNext()) {
                        arrayList.add(new PackageDetail(cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_PACKAGE_TOTAL)), cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_PACKAGE_USED)), cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_PACKAGE_REMAINED)), cursorQuery.getInt(cursorQuery.getColumnIndex(COLUMN_NAME_SLOT_NUM)), "true".equals(cursorQuery.getString(cursorQuery.getColumnIndex(COLUMN_NAME_SUPPORT)))));
                    }
                }
                return arrayList;
            } catch (Exception e2) {
                Log.e(TAG, "getBillPackageDetail", e2);
                return arrayList;
            }
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public static List<PackageDetail> getCallTimePackageDetail(Context context) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(Uri.parse(URI_CALL_TIME_PACKAGE_DETAIL), null, null, null, null);
                if (cursorQuery != null) {
                    while (cursorQuery.moveToNext()) {
                        arrayList.add(new PackageDetail(cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_PACKAGE_TOTAL)), cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_PACKAGE_USED)), cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_PACKAGE_REMAINED)), cursorQuery.getInt(cursorQuery.getColumnIndex(COLUMN_NAME_SLOT_NUM)), "true".equals(cursorQuery.getString(cursorQuery.getColumnIndex(COLUMN_NAME_SUPPORT)))));
                    }
                }
                return arrayList;
            } catch (Exception e2) {
                Log.e(TAG, "getCallTimePackageDetail", e2);
                return arrayList;
            }
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:16:0x0055 A[PHI: r12
      0x0055: PHI (r12v7 android.database.Cursor) = (r12v6 android.database.Cursor), (r12v8 android.database.Cursor) binds: [B:22:0x0067, B:15:0x0053] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:25:0x006c  */
    public static DataUsageDetail getUserDataUsageDetail(Context context) throws Throwable {
        Throwable th;
        Cursor cursorQuery;
        if (context != null) {
            try {
                cursorQuery = context.getContentResolver().query(Uri.parse(URI_NETWORK_TRAFFIC_INFO), null, null, null, null);
                if (cursorQuery != null) {
                    try {
                        try {
                            if (cursorQuery.moveToFirst()) {
                                DataUsageDetail dataUsageDetail = new DataUsageDetail(cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_TOTAL_LIMIT)), cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_MONTH_USED)), cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_MONTH_WARNING)), cursorQuery.getLong(cursorQuery.getColumnIndex(COLUMN_NAME_TODAY_USED)));
                                cursorQuery.close();
                                return dataUsageDetail;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            Log.e(TAG, "getUserDataUsageDetail", e);
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        throw th;
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Exception e3) {
                e = e3;
                cursorQuery = null;
            } catch (Throwable th3) {
                th = th3;
                cursorQuery = null;
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                throw th;
            }
        }
        return null;
    }

    @Deprecated
    public static boolean insertTrafficDistribution(Context context, String str, long j2, long j3, long j4, long j5) {
        if (context != null && !TextUtils.isEmpty(str)) {
            try {
                Uri uri = Uri.parse(TRAFFIC_DISTRIBUTION_URI_STR);
                if (uri != null) {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                    String subscriberId = telephonyManager != null ? telephonyManager.getSubscriberId() : "";
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FROM_PKGNAME, context.getPackageName());
                    contentValues.put(TO_PKGNAME, str);
                    contentValues.put(MOBILE_RXBYTES, Long.valueOf(j5));
                    contentValues.put(MOBILE_TXBYTES, Long.valueOf(j4));
                    contentValues.put(WIFI_RXBYTES, Long.valueOf(j3));
                    contentValues.put(WIFI_TXBYTES, Long.valueOf(j2));
                    contentValues.put("imsi", subscriberId);
                    contentValues.put(STORAGE_TIME, Long.valueOf(System.currentTimeMillis()));
                    if (!TextUtils.isEmpty(context.getContentResolver().insert(uri, contentValues).getLastPathSegment())) {
                        return true;
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, "insertTrafficDistribution", e2);
            }
        }
        return false;
    }

    public static boolean isMobileRestrict(Context context, String str) {
        return isMobileRestrict(context, str, -1);
    }

    public static boolean isMobileTempRestrict(Context context, String str, int i2) {
        if (context == null || TextUtils.isEmpty(str)) {
            return false;
        }
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(Uri.parse(String.format(TEMP_MOBILE_FIREWALL_URI_STR, Integer.valueOf(i2), str)), null, null, new String[]{str}, null);
                if (cursorQuery == null || !cursorQuery.moveToFirst()) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return false;
                }
                boolean z2 = cursorQuery.getInt(cursorQuery.getColumnIndex(FIREWALL_TEMP_MOBILE_RULE)) == 1;
                cursorQuery.close();
                return z2;
            } catch (Exception e2) {
                Log.e(TAG, "isMobileTempRestrict", e2);
                if (cursorQuery != null) {
                }
            }
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    public static boolean isTrafficPurchaseSupported(Context context) {
        if (context == null) {
            return false;
        }
        try {
            return queryTrafficPurchaseStatus(context, Uri.parse(TRAFFIC_PURCHASE_STATUS_URI_STR));
        } catch (Exception e2) {
            Log.e(TAG, "isTrafficPurchaseSupported", e2);
            return false;
        }
    }

    public static boolean isWifiRestrict(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return false;
        }
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(Uri.parse(String.format(WIFI_FIREWALL_URI_STR, str)), null, null, new String[]{str}, null);
                if (cursorQuery == null || !cursorQuery.moveToFirst()) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return false;
                }
                boolean z2 = cursorQuery.getInt(cursorQuery.getColumnIndex(FIREWALL_WIFI_RULE)) == 1;
                cursorQuery.close();
                return z2;
            } catch (Exception e2) {
                Log.e(TAG, "isWifiRestrict", e2);
                if (cursorQuery != null) {
                }
            }
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    public static boolean isWifiTempRestrict(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return false;
        }
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(Uri.parse(String.format(TEMP_WIFI_FIREWALL_URI_STR, str)), null, null, new String[]{str}, null);
                if (cursorQuery == null || !cursorQuery.moveToFirst()) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return false;
                }
                boolean z2 = cursorQuery.getInt(cursorQuery.getColumnIndex(FIREWALL_TEMP_WIFI_RULE)) == 1;
                cursorQuery.close();
                return z2;
            } catch (Exception e2) {
                Log.e(TAG, "isWifiTempRestrict", e2);
                if (cursorQuery != null) {
                }
            }
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    public static void navigateToOperatorSettingActivity(Context context, int i2) {
        Intent intent = Build.IS_INTERNATIONAL_BUILD ? new Intent(ACTION_TRAFFIC_SETTING_INTERNATIONAL) : new Intent(ACTION_TRAFFIC_SETTING);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SLOT_ID, i2);
        bundle.putBoolean(BUNDLE_KEY_OTHER_APP, true);
        intent.putExtras(bundle);
        intent.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
        context.startActivity(intent);
    }

    public static void navigateToRichWebActivity(Context context, String str, String str2, boolean z2, String str3, boolean z3) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_URL, str);
        bundle.putString(BUNDLE_KEY_TITLE, str2);
        bundle.putBoolean(BUNDLE_KEY_HAS_MENU, z2);
        bundle.putString(BUNDLE_KEY_PURCHASE_FROM, str3);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.putExtra(EXTRA_MIUI_STARTING_WINDOW_LABEL, str2);
        if (z3) {
            intent.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
        }
        context.startActivity(intent);
    }

    public static void navigateToSmsReportActivity(Context context, int i2, int i3) {
        Intent intent = new Intent(ACTION_NETWORK_ASSISTANT_SMS_REPORT);
        intent.putExtra(BUNDLE_KEY_SLOT_ID, i2);
        intent.putExtra(KEY_CORRECTION_TYPE, i3);
        intent.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
        context.startActivity(intent);
    }

    @Deprecated
    public static void navigateToTrafficPurchasePage(Context context) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        intent.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
        context.startActivity(intent);
    }

    /* JADX WARN: Code duplicated, block: B:15:0x0038 A[DONT_GENERATE, PHI: r0 r1
      0x0038: PHI (r0v4 android.database.Cursor) = (r0v2 android.database.Cursor), (r0v5 android.database.Cursor) binds: [B:14:0x0036, B:19:0x0042] A[DONT_GENERATE, DONT_INLINE]
      0x0038: PHI (r1v3 boolean) = (r1v0 boolean), (r1v5 boolean) binds: [B:14:0x0036, B:19:0x0042] A[DONT_GENERATE, DONT_INLINE]] */
    private static boolean queryTrafficPurchaseStatus(Context context, Uri uri) {
        Cursor cursorQuery = null;
        boolean zBooleanValue = false;
        if (uri != null) {
            try {
                cursorQuery = context.getContentResolver().query(uri, null, null, null, null);
                if (cursorQuery != null && cursorQuery.moveToFirst()) {
                    zBooleanValue = Boolean.valueOf(cursorQuery.getString(cursorQuery.getColumnIndex(TRAFFIC_PURCHASE_ENABLED))).booleanValue();
                }
            } catch (Exception e2) {
                Log.e(TAG, "queryTrafficPurchaseStatus", e2);
            } finally {
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            }
        }
        return zBooleanValue;
    }

    private static void registerContentObserver(Context context, String str, ContentObserver contentObserver) {
        try {
            Method declaredMethod = Class.forName("android.content.ContentResolver").getDeclaredMethod("registerContentObserver", Uri.class, Boolean.TYPE, ContentObserver.class, Integer.TYPE);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(context.getContentResolver(), Uri.parse(str), Boolean.TRUE, contentObserver, 0);
        } catch (Exception e2) {
            Log.e(TAG, "registerContentObserver error", e2);
        }
    }

    public static void registerFirewallContentObserver(Context context, ContentObserver contentObserver) {
        registerContentObserver(context, String.format(FIREWALL_URI_STR, ""), contentObserver);
    }

    public static void registerPackageContentObserver(Context context, ContentObserver contentObserver, int i2) {
        String str;
        if (i2 == 2) {
            str = URI_BILL_PACKAGE_DETAIL;
        } else {
            str = i2 == 4 ? URI_CALL_TIME_PACKAGE_DETAIL : URI_NETWORK_TRAFFIC_INFO;
        }
        registerContentObserver(context, str, contentObserver);
    }

    public static boolean setMobileRestrict(Context context, String str, boolean z2) {
        return setMobileRestrict(context, str, z2, -1);
    }

    public static boolean setMobileTempRestrict(Context context, String str, int i2, boolean z2) {
        try {
            Uri uri = Uri.parse(String.format(TEMP_MOBILE_FIREWALL_URI_STR, Integer.valueOf(i2), str));
            if (uri != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIREWALL_TEMP_MOBILE_RULE_SLOTNUM, Integer.valueOf(i2));
                contentValues.put(FIREWALL_TEMP_MOBILE_RULE, Boolean.valueOf(z2));
                contentValues.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                return contentResolver.update(uri, contentValues, null, null) == 1;
            }
        } catch (Exception e2) {
            Log.e(TAG, "setMobileTempRestrict", e2);
        }
        return false;
    }

    public static boolean setWifiRestrict(Context context, String str, boolean z2) {
        try {
            Uri uri = Uri.parse(String.format(WIFI_FIREWALL_URI_STR, str));
            if (uri != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIREWALL_WIFI_RULE, Boolean.valueOf(z2));
                contentValues.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                return contentResolver.update(uri, contentValues, null, null) == 1;
            }
        } catch (Exception e2) {
            Log.e(TAG, "setWifiTempRestrict", e2);
        }
        return false;
    }

    public static boolean setWifiTempRestrict(Context context, String str, boolean z2) {
        try {
            Uri uri = Uri.parse(String.format(TEMP_WIFI_FIREWALL_URI_STR, str));
            if (uri != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIREWALL_TEMP_WIFI_RULE, Boolean.valueOf(z2));
                contentValues.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                return contentResolver.update(uri, contentValues, null, null) == 1;
            }
        } catch (Exception e2) {
            Log.e(TAG, "setWifiTempRestrict", e2);
        }
        return false;
    }

    public static boolean startCorrection(Context context, int i2, int i3) {
        try {
            Uri uri = Uri.parse(URI_SMS_CORRECTION);
            if (uri != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(BUNDLE_KEY_SLOT_ID, Integer.valueOf(i2));
                contentValues.put(KEY_CORRECTION_TYPE, Integer.valueOf(i3));
                return contentResolver.update(uri, contentValues, null, null) == 1;
            }
        } catch (Exception e2) {
            Log.e(TAG, "startCorrection", e2);
        }
        return false;
    }

    public static void unRegisterFirewallContentObserver(Context context, ContentObserver contentObserver) {
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }

    public static void unRegisterPackageContentObserver(Context context, ContentObserver contentObserver) {
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }

    public static boolean isMobileRestrict(Context context, String str, int i2) {
        if (context == null || TextUtils.isEmpty(str)) {
            return false;
        }
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(Uri.parse(String.format(MOBILE_FIREWALL_URI_STR, Integer.valueOf(i2), str)), null, null, new String[]{str}, null);
                if (cursorQuery == null || !cursorQuery.moveToFirst()) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return false;
                }
                boolean z2 = cursorQuery.getInt(cursorQuery.getColumnIndex(FIREWALL_MOBILE_RULE)) == 1;
                cursorQuery.close();
                return z2;
            } catch (Exception e2) {
                Log.e(TAG, "isMobileRestrict", e2);
                if (cursorQuery != null) {
                }
            }
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    public static boolean setMobileRestrict(Context context, String str, boolean z2, int i2) {
        try {
            Uri uri = Uri.parse(String.format(MOBILE_FIREWALL_URI_STR, Integer.valueOf(i2), str));
            if (uri != null) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(FIREWALL_MOBILE_RULE_SLOTNUM, Integer.valueOf(i2));
                contentValues.put(FIREWALL_MOBILE_RULE, Boolean.valueOf(z2));
                contentValues.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                return contentResolver.update(uri, contentValues, null, null) == 1;
            }
        } catch (Exception e2) {
            Log.e(TAG, "setMobileTempRestrict", e2);
        }
        return false;
    }

    public static boolean isTrafficPurchaseSupported(Context context, int i2) {
        if (context == null || i2 < 0 || i2 >= 2) {
            return false;
        }
        try {
            return queryTrafficPurchaseStatus(context, Uri.parse(String.format(TRAFFIC_PURCHASE_STATUS_URI_STR_ISMI, Integer.valueOf(i2))));
        } catch (Exception e2) {
            Log.e(TAG, "isTrafficPurchaseSupported", e2);
            return false;
        }
    }

    public static void navigateToTrafficPurchasePage(Context context, String str) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_PURCHASE_FROM, str);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
        context.startActivity(intent);
    }

    @Deprecated
    public static void navigateToTrafficPurchasePage(Context context, int i2) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SLOTID, i2);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
        context.startActivity(intent);
    }

    public static void navigateToTrafficPurchasePage(Context context, int i2, String str) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SLOTID, i2);
        bundle.putString(BUNDLE_KEY_PURCHASE_FROM, str);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.addFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
        context.startActivity(intent);
    }
}
