package miui.yellowpage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import miui.payment.PaymentManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MiPubUtils {
    private static final int COLUMN_THUMBNAIL_URL = 1;
    private static final int COLUMN_YELLOWPAGE_NAME = 2;
    private static final int COLUMN_YID = 0;
    private static final String EXTRA_MULTI_MODULE_ENTRY_RAW = "com.miui.yellowpage.extra.MULTI_MODULE_ENTRY_RAW";
    private static final String FOLLOW_CONFIRM_IN_YP_DETAIL = "follow_confirm_in_yp_detail";
    private static final String FOLLOW_HAS_CONFIRMED_IN_YP_DETAIL = "follow_has_confirmed_in_yp_detail";
    private static final String MENU_HAS_BEEN_FIRSTLY_READ = "pref_menu_has_been_firstly_read";
    private static final String MENU_READ_IN_SMS_CONVERSATION = "pref_menu_read_in_sms_conversation";
    private static final String MIPUB_DEVICE_ID = "pref_mipub_random_device_id";
    private static final String RANDOM_BASE_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String TAG = "MipubUtils";
    private static final String[] YELLOWPAGE_PROJECTION = {"yid", YellowPageContract.PhoneLookup.THUMBNAIL_URL, YellowPageContract.PhoneLookup.YELLOW_PAGE_NAME};
    private static final Pattern XM_ACCOUNT_PATTERN = Pattern.compile(".*@xiaomi.com(/.*)?");

    public static class NetworkAccessDeniedException extends Exception {
    }

    private MiPubUtils() {
    }

    private static ArrayList<ModuleIntent> convertBundleToModules(Bundle bundle) {
        if (bundle.getSerializable(PaymentManager.KEY_INTENT) == null) {
            return null;
        }
        ArrayList<ModuleIntent> arrayList = new ArrayList<>();
        ArrayList<String> stringArrayList = bundle.getStringArrayList("title");
        ArrayList<Integer> integerArrayList = bundle.getIntegerArrayList("id");
        ArrayList parcelableArrayList = bundle.getParcelableArrayList(PaymentManager.KEY_INTENT);
        for (int i2 = 0; i2 < stringArrayList.size(); i2++) {
            arrayList.add(new ModuleIntent(stringArrayList.get(i2), (Intent) parcelableArrayList.get(i2), integerArrayList.get(i2).intValue()));
        }
        return arrayList;
    }

    public static YellowPage getCloudYellowPage(Context context, String str) {
        try {
            return getCloudYellowPageThrowException(context, str);
        } catch (NetworkAccessDeniedException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static YellowPage getCloudYellowPageThrowException(Context context, String str) throws NetworkAccessDeniedException {
        Bundle bundleInvoke = InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_REMOTE_YELLOWPAGE_BY_MIID, str);
        String string = bundleInvoke.getString("status");
        android.util.Log.d(TAG, "getYellowPage:The status is " + string);
        if ("ok".equals(string)) {
            return parseYellowPage(context, bundleInvoke.getString("data"));
        }
        if ("network_access_denied".equals(string)) {
            throw new NetworkAccessDeniedException();
        }
        return null;
    }

    public static String getDeviceId(Context context) {
        String string = Settings.System.getString(context.getContentResolver(), MIPUB_DEVICE_ID);
        if (!TextUtils.isEmpty(string)) {
            return string;
        }
        String hashedDeviceInfo = getHashedDeviceInfo(context);
        android.util.Log.d(TAG, "The random device id is " + hashedDeviceInfo);
        Settings.System.putString(context.getContentResolver(), MIPUB_DEVICE_ID, hashedDeviceInfo);
        return hashedDeviceInfo;
    }

    private static SharedPreferences getFollowConfirmPref(Context context) {
        return context.getSharedPreferences(FOLLOW_CONFIRM_IN_YP_DETAIL, 0);
    }

    private static String getHashedDeviceInfo(Context context) {
        return InvocationHandler.invoke(context, YellowPageContract.Method.DEVICE_HASHED_DEVICE_INFO).getString("hashed_device_info");
    }

    public static YellowPage getLocalYellowPage(Context context, String str) {
        Cursor cursorQuery;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Uri uri = YellowPageContract.YellowPage.CONTENT_URI;
        if (YellowPageUtils.isContentProviderInstalled(context, uri) && (cursorQuery = context.getContentResolver().query(uri, new String[]{"content"}, "miid=?", new String[]{String.valueOf(str)}, null)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return YellowPage.fromJson(cursorQuery.getString(0));
                }
            } finally {
                cursorQuery.close();
            }
        }
        return null;
    }

    public static String getLocalYellowPageNavContent(Context context, int i2) {
        Uri uri = YellowPageContract.NavigationTab.CONTENT_URI;
        if (!YellowPageUtils.isContentProviderInstalled(context, uri)) {
            return null;
        }
        Cursor cursorQuery = context.getContentResolver().query(uri, new String[]{"value"}, "type=?", new String[]{String.valueOf(i2)}, null);
        String string = "";
        if (cursorQuery != null) {
            try {
                string = cursorQuery.moveToFirst() ? cursorQuery.getString(cursorQuery.getColumnIndex("value")) : "";
            } finally {
                cursorQuery.close();
            }
        }
        return string;
    }

    @Deprecated
    public static HashMap<String, YellowPage> getLocalYellowPages(final Context context, final Set<String> set) {
        Cursor cursorQuery;
        if (set == null) {
            return null;
        }
        Uri uri = YellowPageContract.YellowPage.CONTENT_URI;
        if (!YellowPageUtils.isContentProviderInstalled(context, uri) || (cursorQuery = context.getContentResolver().query(uri, new String[]{"miid", "content"}, "miid IS NOT NULL", null, null)) == null) {
            return null;
        }
        HashMap<String, YellowPage> map = new HashMap<>();
        try {
            android.util.Log.d(TAG, "Query miids count " + set.size());
            while (cursorQuery.moveToNext()) {
                String string = cursorQuery.getString(0);
                YellowPage yellowPageFromJson = YellowPage.fromJson(cursorQuery.getString(1));
                if (set.contains(string)) {
                    map.put(string, yellowPageFromJson);
                    set.remove(string);
                }
            }
            android.util.Log.d(TAG, "Query remote yellowpage by miid with cout " + set.size());
            if (set.size() > 0) {
                ThreadPool.execute(new Runnable() { // from class: miui.yellowpage.MiPubUtils.1
                    @Override // java.lang.Runnable
                    public void run() {
                        for (String str : set) {
                            if (MiPubUtils.getCloudYellowPage(context, str) == null) {
                                android.util.Log.e(MiPubUtils.TAG, "No yellowpage matched miid " + str);
                            }
                        }
                    }
                });
            }
            return map;
        } finally {
            cursorQuery.close();
        }
    }

    private static SharedPreferences getMenuReadInSmsPref(Context context) {
        return context.getSharedPreferences(MENU_READ_IN_SMS_CONVERSATION, 0);
    }

    @Deprecated
    public static YellowPageMipub getMipub(Context context, String str) {
        Cursor cursorQuery;
        Uri uriWithAppendedPath = Uri.withAppendedPath(YellowPageContract.MiPub.CONTENT_URI_YELLOWPAGE, Uri.encode(str));
        if (YellowPageUtils.isContentProviderInstalled(context, uriWithAppendedPath) && (cursorQuery = context.getContentResolver().query(uriWithAppendedPath, YELLOWPAGE_PROJECTION, null, null, null)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return new YellowPageMipub(str, cursorQuery.getString(2), cursorQuery.getLong(0), cursorQuery.getString(1));
                }
            } finally {
                cursorQuery.close();
            }
        }
        return null;
    }

    public static String getMipubName(Context context, String str) {
        Cursor cursorQuery;
        Uri uriWithAppendedPath = Uri.withAppendedPath(YellowPageContract.MiPub.CONTENT_URI_YELLOWPAGE, Uri.encode(str));
        if (YellowPageUtils.isContentProviderInstalled(context, uriWithAppendedPath) && (cursorQuery = context.getContentResolver().query(uriWithAppendedPath, YELLOWPAGE_PROJECTION, null, null, null)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return cursorQuery.getString(2);
                }
            } finally {
                cursorQuery.close();
            }
        }
        return null;
    }

    public static Map<String, Service> getRechargeMap(JSONObject jSONObject) {
        HashMap map = new HashMap();
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(Tag.TagServicesData.DATA_RECHARGE);
        if (jSONObjectOptJSONObject != null) {
            map.put(Tag.TagServicesData.DATA_RECHARGE, Service.fromJson(jSONObjectOptJSONObject));
        }
        JSONObject jSONObjectOptJSONObject2 = jSONObject.optJSONObject(Tag.TagServicesData.PHONE_RECHAGE);
        if (jSONObjectOptJSONObject2 != null) {
            map.put(Tag.TagServicesData.PHONE_RECHAGE, Service.fromJson(jSONObjectOptJSONObject2));
        }
        return map;
    }

    public static String getRemoteYellowPageNavContent(Context context, int i2) {
        Bundle bundleInvoke = InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_REMOTE_NAVIGATION_INFO, String.valueOf(i2));
        if (!"ok".equals(bundleInvoke.getString("status"))) {
            return "";
        }
        String string = bundleInvoke.getString("data");
        Uri uri = YellowPageContract.NavigationTab.CONTENT_URI;
        if (!YellowPageUtils.isContentProviderInstalled(context, uri)) {
            return string;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("value", string);
        if (context.getContentResolver().update(uri, contentValues, "type = ? ", new String[]{String.valueOf(i2)}) >= 1) {
            return string;
        }
        context.getContentResolver().insert(uri, contentValues);
        return string;
    }

    public static String getSearchHint(JSONObject jSONObject) {
        return jSONObject.optString(Tag.TagServicesData.SEARCH_HINT);
    }

    public static List<ServicesDataEntry> getServicesList(JSONObject jSONObject) {
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = jSONObject.getJSONArray(Tag.TagServicesData.NAV_GROUPS);
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                arrayList.add(ServicesDataEntry.fromJson(jSONArray.getJSONObject(i2)));
            }
        } catch (JSONException e2) {
            android.util.Log.e(TAG, "", e2);
        }
        return arrayList;
    }

    public static boolean getShowPhoneRecharge(JSONObject jSONObject) {
        return jSONObject.optBoolean(Tag.TagServicesData.SHOW_PHONE_RECHARGE, false);
    }

    public static YellowPage getYellowPage(Context context, String str) {
        YellowPage localYellowPage = getLocalYellowPage(context, str);
        return localYellowPage == null ? getCloudYellowPage(context, str) : localYellowPage;
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, long j2, boolean z2) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", j2);
        bundle.putBoolean(YellowPageContract.Search.REMOTE_SEARCH, z2);
        bundle.putBoolean("hasScope", false);
        return convertBundleToModules(InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_MENU, null, bundle));
    }

    @Deprecated
    public static YellowPage getYellowPageThrowException(Context context, String str) throws NetworkAccessDeniedException {
        YellowPage localYellowPage = getLocalYellowPage(context, str);
        return localYellowPage == null ? getCloudYellowPageThrowException(context, str) : localYellowPage;
    }

    @Deprecated
    public static boolean isFollowConfirmed(Context context) {
        return getFollowConfirmPref(context).getBoolean(FOLLOW_HAS_CONFIRMED_IN_YP_DETAIL, false);
    }

    private static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static boolean isServiceNumber(Context context, String str) {
        return isServiceNumber(str) || isXiaomiJID(str);
    }

    public static boolean isSmsMenuRead(Context context, long j2) {
        SharedPreferences menuReadInSmsPref = getMenuReadInSmsPref(context);
        return menuReadInSmsPref.getBoolean(String.valueOf(j2), menuReadInSmsPref.getBoolean(MENU_HAS_BEEN_FIRSTLY_READ, false));
    }

    public static boolean isXiaomiAccount(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return XM_ACCOUNT_PATTERN.matcher(str).matches();
    }

    private static boolean isXiaomiJID(String str) {
        if (isXiaomiAccount(str)) {
            return isNumeric(trimDomainSuffix(str));
        }
        return false;
    }

    public static boolean isYellowPageNetworkAllowed(Context context) {
        return InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_IS_NETWORK_ALLOWED, null, null).getBoolean("networkAllowed", false);
    }

    private static YellowPage parseYellowPage(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_INSERT, jSONObject.getString(Tag.TagYellowPage.KEY));
            return YellowPage.fromJson(jSONObject.getString(Tag.TagYellowPage.KEY));
        } catch (JSONException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static final String randomString(int i2) {
        StringBuilder sb = new StringBuilder(i2);
        Random random = new Random();
        for (int i3 = 0; i3 < i2; i3++) {
            sb.append(RANDOM_BASE_STRING.charAt(random.nextInt(62)));
        }
        return sb.toString();
    }

    @Deprecated
    public static void setFollowConfirmed(Context context, boolean z2) {
        getFollowConfirmPref(context).edit().putBoolean(FOLLOW_HAS_CONFIRMED_IN_YP_DETAIL, z2).commit();
    }

    public static void setSmsMenuRead(Context context, long j2, boolean z2) {
        SharedPreferences menuReadInSmsPref = getMenuReadInSmsPref(context);
        menuReadInSmsPref.edit().putBoolean(String.valueOf(j2), z2).commit();
        menuReadInSmsPref.edit().putBoolean(MENU_HAS_BEEN_FIRSTLY_READ, true).commit();
    }

    public static boolean setYellowPageNetworkAllowed(Context context) {
        return InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_SET_NETWORK_ALLOWED, null, null).getBoolean("networkAllowed", false);
    }

    public static String trimDomainSuffix(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int iIndexOf = str.indexOf("@");
        return iIndexOf > 0 ? str.substring(0, iIndexOf) : str;
    }

    private static boolean isServiceNumber(String str) {
        try {
            Method declaredMethod = Class.forName("miui.provider.ExtraTelephony").getDeclaredMethod("isServiceNumber", String.class);
            declaredMethod.setAccessible(true);
            return ((Boolean) declaredMethod.invoke(null, str)).booleanValue();
        } catch (Exception e2) {
            android.util.Log.w(TAG, "invoke ExtraTelephony.isServiceNumber failed", e2);
            return false;
        }
    }

    public static List<ServicesDataEntry> getServicesList(String str) {
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONObject(str).getJSONArray(Tag.TagServicesData.NAV_GROUPS);
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                arrayList.add(ServicesDataEntry.fromJson(jSONArray.getJSONObject(i2)));
            }
        } catch (JSONException e2) {
            android.util.Log.e(TAG, "", e2);
        }
        return arrayList;
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, long j2, boolean z2, int i2) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", j2);
        bundle.putBoolean(YellowPageContract.Search.REMOTE_SEARCH, z2);
        bundle.putBoolean("hasScope", true);
        return convertBundleToModules(context, InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_MENU, null, bundle), j2, i2);
    }

    private static ArrayList<ModuleIntent> convertBundleToModules(Context context, Bundle bundle, long j2, int i2) {
        if (bundle.getSerializable(PaymentManager.KEY_INTENT) == null) {
            return null;
        }
        ArrayList<ModuleIntent> arrayList = new ArrayList<>();
        ArrayList<String> stringArrayList = bundle.getStringArrayList("title");
        ArrayList<Integer> integerArrayList = bundle.getIntegerArrayList("id");
        ArrayList<Integer> integerArrayList2 = bundle.getIntegerArrayList("scope");
        ArrayList<Integer> integerArrayList3 = bundle.getIntegerArrayList("hotId");
        ArrayList<Integer> integerArrayList4 = bundle.getIntegerArrayList("hotShowCount");
        ArrayList parcelableArrayList = bundle.getParcelableArrayList(PaymentManager.KEY_INTENT);
        boolean[] booleanArray = bundle.getBooleanArray("subItemsFlag");
        int i3 = 0;
        while (i3 < stringArrayList.size()) {
            if (i2 == 0 || (integerArrayList2.get(i3).intValue() & i2) > 0) {
                Intent intent = (Intent) parcelableArrayList.get(i3);
                ModuleIntent moduleIntent = new ModuleIntent(stringArrayList.get(i3), intent, integerArrayList.get(i3).intValue(), booleanArray[i3], integerArrayList3.get(i3).intValue(), integerArrayList4.get(i3).intValue());
                if (booleanArray[i3]) {
                    try {
                        try {
                            moduleIntent.setSubModuleIntent(getYellowPageMenu(context, new JSONObject(intent.getStringExtra(EXTRA_MULTI_MODULE_ENTRY_RAW)).optString("subItems"), j2, i2));
                        } catch (JSONException e2) {
                            e = e2;
                            e.printStackTrace();
                        }
                    } catch (JSONException e3) {
                        e = e3;
                    }
                }
                arrayList.add(moduleIntent);
            }
            i3++;
            parcelableArrayList = parcelableArrayList;
        }
        return arrayList;
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, String str, long j2) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", j2);
        bundle.putString("jsonString", str);
        return convertBundleToModules(InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_PARSE_MENU, null, bundle));
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, String str, long j2, int i2) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", j2);
        bundle.putString("jsonString", str);
        return convertBundleToModules(context, InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_PARSE_SUB_MENU, null, bundle), j2, i2);
    }
}
