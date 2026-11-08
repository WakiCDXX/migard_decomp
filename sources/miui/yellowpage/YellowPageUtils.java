package miui.yellowpage;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class YellowPageUtils {
    private static final int ANTISPAM_COLUMN_CID = 1;
    private static final int ANTISPAM_COLUMN_MARKED_COUNT = 3;
    private static final int ANTISPAM_COLUMN_NORMALIZED_NUMBER = 4;
    private static final int ANTISPAM_COLUMN_NUMBER_TYPE = 5;
    private static final int ANTISPAM_COLUMN_PID = 0;
    private static final int ANTISPAM_COLUMN_TYPE = 2;
    private static final String CLOUD_ANTISPAM = "cloud_antispam";
    private static final int CLOUD_ANTISPAM_DISABLE = 0;
    private static final int CLOUD_ANTISPAM_ENANBLE = 1;
    private static final int PHONE_COLUMN_ATD_COUNT = 15;
    private static final int PHONE_COLUMN_ATD_ID = 13;
    private static final int PHONE_COLUMN_ATD_PROVIDER = 16;
    private static final int PHONE_COLUMN_CALL_MENU = 11;
    private static final int PHONE_COLUMN_CREDIT_IMG = 17;
    private static final int PHONE_COLUMN_NORMALIZED_NUMBER = 9;
    private static final int PHONE_COLUMN_NUMBER_TYPE = 18;
    private static final int PHONE_COLUMN_PHOTO_URL = 3;
    private static final int PHONE_COLUMN_PROVIDER_ID = 1;
    private static final int PHONE_COLUMN_SLOGAN = 14;
    private static final int PHONE_COLUMN_SUSPECT = 10;
    private static final int PHONE_COLUMN_T9_RANK = 12;
    private static final int PHONE_COLUMN_TAG = 2;
    private static final int PHONE_COLUMN_TAG_PINYIN = 7;
    private static final int PHONE_COLUMN_THUMBNAIL_URL = 4;
    private static final int PHONE_COLUMN_VISIBLE = 8;
    private static final int PHONE_COLUMN_YID = 0;
    private static final int PHONE_COLUMN_YP_NAME = 5;
    private static final int PHONE_COLUMN_YP_NAME_PINYIN = 6;
    private static final String TAG = "YellowPageUtils";
    private static final String[] PHONE_PROJECTION = {"yid", YellowPageContract.PhoneLookup.PROVIDER_ID, "tag", YellowPageContract.PhoneLookup.PHOTO_URL, YellowPageContract.PhoneLookup.THUMBNAIL_URL, YellowPageContract.PhoneLookup.YELLOW_PAGE_NAME, YellowPageContract.PhoneLookup.YELLOW_PAGE_NAME_PINYIN, YellowPageContract.PhoneLookup.TAG_PINYIN, "hide", "normalized_number", YellowPageContract.PhoneLookup.SUSPECT, YellowPageContract.PhoneLookup.CALL_MENU, "t9_rank", YellowPageContract.PhoneLookup.ATD_ID, "slogan", YellowPageContract.PhoneLookup.ATD_COUNT, YellowPageContract.PhoneLookup.ATD_PROVIDER, "credit_img", "number_type"};
    private static final String[] ANTISPAM_PROJECTION = {"pid", "cid", "type", YellowPageContract.AntispamNumber.MARKED_COUNT, "normalized_number", "number_type"};

    @SuppressLint({"UseSparseArrays"})
    private static final HashMap<Integer, YellowPageProvider> sProviders = new HashMap<>();

    @SuppressLint({"UseSparseArrays"})
    private static final ConcurrentHashMap<Integer, AntispamCategory> sCidCategories = new ConcurrentHashMap<>();

    private YellowPageUtils() {
    }

    private static boolean antispamNumberNotInPresetFile(Cursor cursor) {
        if (cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
            while (cursor.getInt(2) != 5) {
                if (!cursor.moveToNext()) {
                }
            }
            return true;
        }
        return false;
    }

    private static String buildAntispamCategorySelection(Integer... numArr) {
        StringBuilder sb = new StringBuilder();
        for (Integer num : numArr) {
            if (sb.length() > 0) {
                sb.append(" OR ");
            }
            sb.append("type");
            sb.append("=");
            sb.append(num);
        }
        sb.insert(0, Constants.SEPARATOR_LEFT_PARENTESIS);
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    private static YellowPagePhone buildAntispamInfoFromCursor(Context context, Cursor cursor, String str) {
        YellowPagePhone yellowPagePhone = null;
        while (cursor.moveToNext()) {
            int i2 = cursor.getInt(1);
            if (i2 == 0) {
                android.util.Log.d(TAG, "invalid cid");
            } else {
                String cidName = getCidName(context, i2);
                if (TextUtils.isEmpty(cidName)) {
                    continue;
                } else {
                    int i3 = cursor.getInt(0);
                    int i4 = cursor.getInt(2) == 3 ? 3 : 2;
                    int i5 = cursor.getInt(3);
                    String string = cursor.getString(4);
                    int i6 = cursor.getInt(5);
                    if (2 == i4 && i6 != 0) {
                        cidName = "";
                    }
                    int i7 = i4;
                    YellowPagePhone yellowPagePhone2 = new YellowPagePhone(-1L, (String) null, cidName, str, string, i4, i3, i5, true, (String) null, (String) null, i2);
                    yellowPagePhone2.setNumberType(i6);
                    if (yellowPagePhone == null || i7 == 3) {
                        yellowPagePhone = yellowPagePhone2;
                        if (i7 == 3) {
                            break;
                        }
                    }
                }
            }
        }
        return yellowPagePhone;
    }

    private static YellowPagePhone buildYellowPagePhoneFromCursor(Cursor cursor, String str) {
        int i2;
        int i3 = cursor.getInt(1);
        String string = cursor.getString(2);
        String string2 = cursor.getString(5);
        long j2 = cursor.getLong(0);
        String string3 = cursor.getString(6);
        String string4 = cursor.getString(7);
        String string5 = cursor.getString(9);
        boolean z2 = cursor.getInt(8) > 0;
        boolean z3 = cursor.getInt(10) > 0;
        boolean z4 = cursor.getInt(11) > 0;
        long j3 = cursor.getLong(12);
        int i4 = cursor.getInt(13);
        String string6 = cursor.getString(14);
        int i5 = cursor.getInt(15);
        int i6 = cursor.getInt(16);
        String string7 = cursor.getString(17);
        int i7 = cursor.getInt(18);
        if (j2 != -1) {
            i2 = 1;
        } else {
            i2 = i4 > 0 ? 2 : 0;
        }
        YellowPagePhone yellowPagePhone = new YellowPagePhone(j2, string2, string, str, string5, i2, i3, i5, z2, string3, string4, z3, z4);
        yellowPagePhone.setT9Rank(j3);
        yellowPagePhone.setRawSlogan(string6);
        yellowPagePhone.setCreditImg(string7);
        yellowPagePhone.setCid(i4);
        yellowPagePhone.setAntispamProviderId(i6);
        yellowPagePhone.setNumberType(i7);
        return yellowPagePhone;
    }

    public static int createAntispamCategory(Context context, String str) {
        int i2;
        if (TextUtils.isEmpty(str)) {
            android.util.Log.d(TAG, "The category name must not be null");
        }
        Uri uri = YellowPageContract.AntispamCategory.CONTENT_URI;
        if (!isContentProviderInstalled(context, uri)) {
            return 0;
        }
        Cursor cursorQuery = context.getContentResolver().query(uri, new String[]{"MAX(cid)"}, null, null, null);
        int i3 = 10000;
        if (cursorQuery != null) {
            try {
                try {
                    if (cursorQuery.moveToFirst() && (i2 = cursorQuery.getInt(0)) >= 10000) {
                        i3 = i2 + 1;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                cursorQuery.close();
            } catch (Throwable th) {
                cursorQuery.close();
                throw th;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("cid", Integer.valueOf(i3));
        contentValues.put("names", str);
        contentValues.put("type", (Integer) 1);
        insert(context, YellowPageContract.AntispamCategory.CONTENT_URI, contentValues);
        return i3;
    }

    public static String formatPreferenceKey(String str) {
        return String.format(Tag.TagPreference.PREF_FORMAT, str);
    }

    private static AntispamCustomCategory getAntispamCategoryFromCursor(Context context, Cursor cursor, int i2, String str) {
        if (i2 != -1 && cursor.moveToPosition(i2)) {
            int i3 = cursor.getInt(1);
            int i4 = cursor.getInt(2);
            int i5 = cursor.getInt(3);
            int i6 = cursor.getInt(5);
            boolean z2 = i4 == 3;
            if (z2) {
                getCategories(context);
            }
            AntispamCategory antispamCategory = sCidCategories.get(Integer.valueOf(i3));
            if (antispamCategory != null) {
                AntispamCustomCategory antispamCustomCategory = new AntispamCustomCategory(antispamCategory.getCategoryId(), antispamCategory.getCategoryAllNames(), antispamCategory.getCategoryType(), antispamCategory.getIcon(), antispamCategory.getOrder(), str, i5, z2);
                antispamCustomCategory.setNumberType(i6);
                return antispamCustomCategory;
            }
        }
        return null;
    }

    public static AntispamCustomCategory getAntispamNumberCategory(Context context, String str) {
        return getAntispamNumberCategory(context, str, true);
    }

    private static boolean getBooleanSettings(Context context, String str) {
        Cursor cursorQuery;
        Uri uriWithAppendedPath = Uri.withAppendedPath(YellowPageContract.Settings.CONTENT_URI, str);
        if (isContentProviderInstalled(context, uriWithAppendedPath) && (cursorQuery = context.getContentResolver().query(uriWithAppendedPath, null, null, null, null)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return cursorQuery.getInt(0) > 0;
                }
            } finally {
                cursorQuery.close();
            }
        }
        return false;
    }

    public static List<AntispamCategory> getCategories(Context context) {
        Uri uri = YellowPageContract.AntispamCategory.CONTENT_URI;
        if (!isContentProviderInstalled(context, uri)) {
            return Collections.emptyList();
        }
        Cursor cursorQuery = context.getContentResolver().query(uri, new String[]{"cid", "names", "type", "icon", YellowPageContract.AntispamCategory.ORDER}, null, null, null);
        if (cursorQuery != null) {
            while (cursorQuery.moveToNext()) {
                try {
                    try {
                        int i2 = cursorQuery.getInt(0);
                        sCidCategories.put(Integer.valueOf(i2), new AntispamCategory(i2, cursorQuery.getString(1), cursorQuery.getInt(2), cursorQuery.getString(3), cursorQuery.getInt(4)));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            }
            cursorQuery.close();
        }
        ConcurrentHashMap<Integer, AntispamCategory> concurrentHashMap = sCidCategories;
        if (concurrentHashMap.values() == null) {
            return null;
        }
        return new ArrayList(concurrentHashMap.values());
    }

    public static String getCidName(Context context, int i2) {
        ConcurrentHashMap<Integer, AntispamCategory> concurrentHashMap = sCidCategories;
        if (concurrentHashMap.containsKey(Integer.valueOf(i2))) {
            AntispamCategory antispamCategory = concurrentHashMap.get(Integer.valueOf(i2));
            if (antispamCategory == null) {
                return null;
            }
            return antispamCategory.getCategoryName();
        }
        getCategories(context);
        AntispamCategory antispamCategory2 = concurrentHashMap.get(Integer.valueOf(i2));
        if (antispamCategory2 == null) {
            return null;
        }
        return antispamCategory2.getCategoryName();
    }

    public static String getIvrMenuByNumber(Context context, String str) {
        Cursor cursorQuery;
        Uri uriWithAppendedPath = Uri.withAppendedPath(YellowPageContract.Ivr.CONTENT_URI, str);
        if (isContentProviderInstalled(context, uriWithAppendedPath) && (cursorQuery = context.getContentResolver().query(uriWithAppendedPath, new String[]{"data"}, null, null, null)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return cursorQuery.getString(0);
                }
            } finally {
                cursorQuery.close();
            }
        }
        return null;
    }

    public static HashMap<String, YellowPagePhone> getLocalYellowPagePhones(Context context, List<String> list) {
        return getLocalYellowPagePhones(context, list, null);
    }

    private static String getLogNumber(String str) {
        if (str == null || str.length() == 0) {
            return "[empty number]";
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        if (length <= 4) {
            for (int i3 = 0; i3 < length - 2; i3++) {
                sb.append(str.charAt(i3));
            }
            int iMin = Math.min(length, 2);
            while (i2 < iMin) {
                sb.append("*");
                i2++;
            }
        } else if (length == 5 || length == 6) {
            while (i2 < length - 3) {
                sb.append(str.charAt(i2));
                i2++;
            }
            sb.append("***");
        } else if (length > 6) {
            while (i2 < length - 4) {
                sb.append(str.charAt(i2));
                i2++;
            }
            sb.append("****");
        }
        return sb.toString();
    }

    public static String getNormalizedNumber(Context context, String str) {
        return getNormalizedNumber(context, str, true, null);
    }

    public static YellowPagePhone getPhoneInfo(Context context, String str, boolean z2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        YellowPagePhone yellowPagePhoneQueryPhoneInfo = queryPhoneInfo(context, str, false);
        if (yellowPagePhoneQueryPhoneInfo == null && z2) {
            return queryPhoneInfo(context, str, true);
        }
        if (!z2) {
            return yellowPagePhoneQueryPhoneInfo;
        }
        updateCloudPhoneInfo(context, str);
        return yellowPagePhoneQueryPhoneInfo;
    }

    public static YellowPageProvider getProvider(Context context, int i2) {
        YellowPageProvider yellowPageProvider = sProviders.get(Integer.valueOf(i2));
        if (yellowPageProvider != null && !TextUtils.isEmpty(yellowPageProvider.getName())) {
            return yellowPageProvider;
        }
        Uri uri = YellowPageContract.Provider.CONTENT_URI;
        if (!isContentProviderInstalled(context, uri)) {
            return null;
        }
        Cursor cursorQuery = context.getContentResolver().query(uri, new String[]{"name", "icon", "pid", YellowPageContract.Provider.ICON_BIG}, null, null, null);
        if (cursorQuery != null) {
            while (cursorQuery.moveToNext()) {
                try {
                    try {
                        String string = cursorQuery.getString(0);
                        byte[] blob = cursorQuery.getBlob(1);
                        Bitmap bitmapDecodeByteArray = blob == null ? null : BitmapFactory.decodeByteArray(blob, 0, blob.length);
                        byte[] blob2 = cursorQuery.getBlob(3);
                        Bitmap bitmapDecodeByteArray2 = blob2 == null ? null : BitmapFactory.decodeByteArray(blob2, 0, blob2.length);
                        int i3 = cursorQuery.getInt(2);
                        sProviders.put(Integer.valueOf(i3), new YellowPageProvider(i3, string, bitmapDecodeByteArray, bitmapDecodeByteArray2));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            }
            cursorQuery.close();
        }
        YellowPageProvider yellowPageProvider2 = sProviders.get(Integer.valueOf(i2));
        return yellowPageProvider2 == null ? YellowPageProvider.DEFAULT_PROVIDER : yellowPageProvider2;
    }

    public static String getUserAreaCode(Context context) {
        Cursor cursorQuery;
        Uri uri = YellowPageContract.UserArea.CONTENT_URI;
        if (isContentProviderInstalled(context, uri) && (cursorQuery = context.getContentResolver().query(uri, new String[]{YellowPageContract.UserArea.AREA_CODE}, null, null, null)) != null) {
            try {
                try {
                    if (cursorQuery.moveToFirst()) {
                        return cursorQuery.getString(0);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } finally {
                cursorQuery.close();
            }
        }
        return null;
    }

    @Deprecated
    public static void informYellowpagePhoneEvent(Context context, long j2, long j3, String str, String str2, int i2, String str3, int i3) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_DIRECTION, i2);
            jSONObject.put("number", str3);
            jSONObject.put(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, i3);
            Uri uriBuild = YellowPageContract.MipubPhoneEvent.CONTENT_URI_MIPUB_PHONE_EVENT.buildUpon().appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_START_TIME, String.valueOf(j2)).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_END_TIME, String.valueOf(j3)).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_SOURCE, str).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_TYPE, str2).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_EXTRA_DATA, Uri.encode(jSONObject.toString())).build();
            if (isContentProviderInstalled(context, uriBuild)) {
                try {
                    Cursor cursorQuery = context.getContentResolver().query(uriBuild, null, null, null, null);
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (JSONException e3) {
            e3.printStackTrace();
        }
    }

    private static Uri insert(Context context, Uri uri, ContentValues contentValues) {
        if (isContentProviderInstalled(context, uri)) {
            return context.getContentResolver().insert(uri, contentValues);
        }
        android.util.Log.d(TAG, "insert-The provider is not installed");
        return null;
    }

    private static boolean isCloudAntispamEnable(Context context) {
        return Settings.System.getInt(context.getContentResolver(), CLOUD_ANTISPAM, 0) == 1;
    }

    public static boolean isContentProviderInstalled(Context context, Uri uri) {
        if (context == null || uri == null) {
            return false;
        }
        ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = context.getContentResolver().acquireUnstableContentProviderClient(uri);
        if (contentProviderClientAcquireUnstableContentProviderClient == null) {
            android.util.Log.e(TAG, "The content provider is not installed");
            return false;
        }
        contentProviderClientAcquireUnstableContentProviderClient.release();
        return true;
    }

    public static boolean isFraudIncomingNumber(Context context, int i2, String str, String str2) {
        Cursor cursorQuery;
        Uri.Builder builderBuildUpon = YellowPageContract.PhoneLookup.CONTENT_FRAUD_VERIFY.buildUpon();
        builderBuildUpon.appendQueryParameter("simIndex", String.valueOf(i2));
        builderBuildUpon.appendQueryParameter("incoming", str);
        builderBuildUpon.appendQueryParameter("yid", str2);
        Uri uriBuild = builderBuildUpon.build();
        if (isContentProviderInstalled(context, uriBuild) && (cursorQuery = context.getContentResolver().query(uriBuild, null, null, null, null)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return cursorQuery.getInt(0) > 0;
                }
            } finally {
                cursorQuery.close();
            }
        }
        return false;
    }

    public static boolean isFraudNumOnlineIdentificationEnabled(Context context) {
        return getBooleanSettings(context, YellowPageContract.Settings.ONLINE_FRAUD_ENABLE);
    }

    public static boolean isInSmsWhiteList(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Uri uri = YellowPageContract.AntispamWhiteList.CONTNET_URI;
        if (!isContentProviderInstalled(context, uri)) {
            return false;
        }
        Cursor cursorQuery = context.getContentResolver().query(uri, new String[]{"number"}, "number LIKE '" + str + "%'", null, null);
        if (cursorQuery != null) {
            while (cursorQuery.moveToNext()) {
                try {
                    try {
                        if (TextUtils.equals(str, cursorQuery.getString(0)) || str.startsWith("106")) {
                            cursorQuery.close();
                            return true;
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            }
            cursorQuery.close();
        }
        return false;
    }

    public static boolean isIvrMenuExist(Context context, String str) {
        Cursor cursorQuery;
        Uri uriWithAppendedPath = Uri.withAppendedPath(YellowPageContract.Ivr.CONTENT_URI, str);
        if (isContentProviderInstalled(context, uriWithAppendedPath) && (cursorQuery = context.getContentResolver().query(uriWithAppendedPath, new String[]{"exist"}, null, null, null)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return cursorQuery.getInt(0) == 1;
                }
            } finally {
                cursorQuery.close();
            }
        }
        return false;
    }

    public static boolean isNeverRemindSmartAntispamEnable(Context context) {
        return getBooleanSettings(context, YellowPageContract.Settings.NEVER_REMIND_ENABLE_ANTISPAM);
    }

    public static boolean isRemindIgnoredUserSuspectNumber(Context context) {
        return getBooleanSettings(context, YellowPageContract.Settings.REMIND_USER_SUSPECT_NUMBER);
    }

    public static boolean isYellowPageAvailable(Context context) {
        Locale locale = Locale.getDefault();
        return (!Build.IS_INTERNATIONAL_BUILD && (Locale.SIMPLIFIED_CHINESE.equals(locale) || Locale.TRADITIONAL_CHINESE.equals(locale) || Locale.US.equals(locale))) || "IN".equals(Build.getRegion());
    }

    public static boolean isYellowPageEnable(Context context) {
        return isYellowPageAvailable(context) && isCloudAntispamEnable(context);
    }

    public static void markAntiSpam(Context context, String str, int i2, boolean z2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", str);
        contentValues.put("categoryId", Integer.valueOf(i2));
        contentValues.put("delete", Boolean.valueOf(z2));
        update(context, YellowPageContract.AntispamNumber.CONTENT_MARK_NUMBER_URI, contentValues, null, null);
    }

    private static void queryCloudExpressInfo(Context context, String str, String str2) {
        if (!isYellowPageAvailable(context) || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("bizName", str);
        bundle.putString("serialNumber", str2);
        InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_REMOTE_EXPRESS_INFO, null, bundle);
    }

    public static void queryExpressInfo(Context context, String str, String str2) {
        if (queryLocalExpressInfo(context, str, str2)) {
            return;
        }
        queryCloudExpressInfo(context, str, str2);
    }

    private static boolean queryLocalExpressInfo(Context context, String str, String str2) {
        if (!isYellowPageAvailable(context) || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("bizName", str);
        bundle.putString("serialNumber", str2);
        return InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_LOCAL_EXPRESS_INFO, null, bundle).getBoolean("res");
    }

    private static YellowPagePhone queryPhoneInfo(Context context, String str, boolean z2) {
        Uri uriWithAppendedPath = Uri.withAppendedPath(z2 ? YellowPageContract.PhoneLookup.CONTENT_URI_CLOUD : YellowPageContract.PhoneLookup.CONTENT_URI, str);
        if (!isContentProviderInstalled(context, uriWithAppendedPath)) {
            return null;
        }
        Cursor cursorQuery = context.getContentResolver().query(uriWithAppendedPath, PHONE_PROJECTION, null, null, null);
        try {
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        YellowPagePhone yellowPagePhoneBuildYellowPagePhoneFromCursor = buildYellowPagePhoneFromCursor(cursorQuery, str);
                        cursorQuery.close();
                        return yellowPagePhoneBuildYellowPagePhoneFromCursor;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                cursorQuery.close();
            }
            if (!z2) {
                Cursor cursorQuery2 = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PHONE_LOOKUP_URI, str), ANTISPAM_PROJECTION, "type<>4", null, null);
                try {
                    if (cursorQuery2 != null) {
                        try {
                            YellowPagePhone yellowPagePhoneBuildAntispamInfoFromCursor = buildAntispamInfoFromCursor(context, cursorQuery2, str);
                            if (yellowPagePhoneBuildAntispamInfoFromCursor != null) {
                                cursorQuery2.close();
                                return yellowPagePhoneBuildAntispamInfoFromCursor;
                            }
                            if (antispamNumberNotInPresetFile(cursorQuery2)) {
                                cursorQuery2.close();
                                return null;
                            }
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                        cursorQuery2.close();
                    }
                    Cursor cursorQuery3 = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PRESET_PHONE_LOOKUP_URI, str), ANTISPAM_PROJECTION, null, null, null);
                    if (cursorQuery3 != null) {
                        try {
                            try {
                                YellowPagePhone yellowPagePhoneBuildAntispamInfoFromCursor2 = buildAntispamInfoFromCursor(context, cursorQuery3, str);
                                if (yellowPagePhoneBuildAntispamInfoFromCursor2 != null) {
                                    cursorQuery3.close();
                                    return yellowPagePhoneBuildAntispamInfoFromCursor2;
                                }
                            } catch (Exception e4) {
                                e4.printStackTrace();
                            }
                            cursorQuery3.close();
                        } catch (Throwable th) {
                            cursorQuery3.close();
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    cursorQuery2.close();
                    throw th2;
                }
            }
            return null;
        } catch (Throwable th3) {
            cursorQuery.close();
            throw th3;
        }
    }

    private static int update(Context context, Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (isContentProviderInstalled(context, uri)) {
            return context.getContentResolver().update(uri, contentValues, str, strArr);
        }
        android.util.Log.d(TAG, "update-The provider is not installed");
        return 0;
    }

    private static void updateCloudPhoneInfo(Context context, String str) {
        update(context, Uri.withAppendedPath(YellowPageContract.PhoneLookup.CONTENT_URI_CLOUD, str), new ContentValues(), null, null);
    }

    public static void updatePhoneInfo(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        android.util.Log.d(TAG, "updatePhoneInfo updateCloud");
        updateCloudPhoneInfo(context, str);
    }

    public static AntispamCustomCategory getAntispamNumberCategory(Context context, String str, boolean z2) {
        AntispamCustomCategory antispamCategoryFromCursor = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Uri uriWithAppendedPath = Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PHONE_LOOKUP_URI, str);
        if (!isContentProviderInstalled(context, uriWithAppendedPath)) {
            return null;
        }
        Cursor cursorQuery = context.getContentResolver().query(uriWithAppendedPath, ANTISPAM_PROJECTION, buildAntispamCategorySelection(3, 1, 2, 5), null, null, null);
        if (cursorQuery != null) {
            int i2 = -1;
            int position = -1;
            int position2 = -1;
            int position3 = -1;
            while (cursorQuery.moveToNext()) {
                try {
                    try {
                        int i3 = cursorQuery.getInt(2);
                        if (i3 == 1 || i3 == 2) {
                            position2 = cursorQuery.getPosition();
                        } else if (i3 == 3) {
                            position = cursorQuery.getPosition();
                        } else if (i3 == 5) {
                            position3 = cursorQuery.getPosition();
                        }
                    } catch (Throwable th) {
                        cursorQuery.close();
                        throw th;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (position != -1) {
                android.util.Log.d(TAG, "getAntispamNumberCategory(): found type user marked in db");
                i2 = position;
            } else if (position2 != -1) {
                android.util.Log.d(TAG, "getAntispamNumberCategory(): found type preset/cloud in db");
                i2 = position2;
            } else if (position3 != -1) {
                android.util.Log.d(TAG, "getAntispamNumberCategory(): found type not in preset in db");
                cursorQuery.close();
                return null;
            }
            antispamCategoryFromCursor = getAntispamCategoryFromCursor(context, cursorQuery, i2, str);
            if (antispamCategoryFromCursor == null) {
                android.util.Log.d(TAG, "getAntispamNumberCategory(): find nothing in db");
            }
            cursorQuery.close();
        } else {
            android.util.Log.d(TAG, "getAntispamNumberCategory(): find nothing in db");
        }
        if (antispamCategoryFromCursor == null) {
            Cursor cursorQuery2 = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PRESET_PHONE_LOOKUP_URI, str), ANTISPAM_PROJECTION, null, null, null);
            if (cursorQuery2 != null) {
                try {
                    try {
                        antispamCategoryFromCursor = getAntispamCategoryFromCursor(context, cursorQuery2, 0, str);
                        if (antispamCategoryFromCursor != null) {
                            android.util.Log.d(TAG, "getAntispamNumberCategory(): found in preset");
                        } else {
                            android.util.Log.d(TAG, "getAntispamNumberCategory(): not found in preset");
                        }
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    cursorQuery2.close();
                } catch (Throwable th2) {
                    cursorQuery2.close();
                    throw th2;
                }
            } else {
                android.util.Log.d(TAG, "getAntispamNumberCategory(): find nothing in preset");
            }
        }
        if (antispamCategoryFromCursor != null) {
            android.util.Log.d(TAG, String.format("getAntispamNumberCategory(): number=%s, numberType=%s, category=%s", getLogNumber(str), Integer.valueOf(antispamCategoryFromCursor.getNumberType()), antispamCategoryFromCursor.getCategoryName()));
        } else {
            android.util.Log.d(TAG, String.format("getAntispamNumberCategory(): number=%s, not found", getLogNumber(str)));
        }
        return antispamCategoryFromCursor;
    }

    public static HashMap<String, YellowPagePhone> getLocalYellowPagePhones(Context context, List<String> list, HashMap<String, String> map) {
        String normalizedNumber;
        HashMap<String, YellowPagePhone> map2 = null;
        if (isYellowPageAvailable(context) && list != null && list.size() != 0) {
            if (!isContentProviderInstalled(context, YellowPageContract.PhoneLookup.CONTENT_URI)) {
                return null;
            }
            HashMap map3 = new HashMap();
            map2 = new HashMap<>();
            for (int i2 = 0; i2 < list.size(); i2++) {
                String str = list.get(i2);
                if (map == null || TextUtils.isEmpty(map.get(str))) {
                    normalizedNumber = getNormalizedNumber(context, str);
                    if (!TextUtils.isEmpty(normalizedNumber)) {
                        if (map != null) {
                            map.put(str, normalizedNumber);
                        }
                    }
                } else {
                    normalizedNumber = map.get(str);
                }
                map3.put(normalizedNumber, str);
                if (map3.size() > 50 || i2 == list.size() - 1) {
                    StringBuilder sb = new StringBuilder();
                    for (String str2 : map3.keySet()) {
                        if (sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append("'");
                        sb.append(str2);
                        sb.append("'");
                    }
                    Cursor cursorQuery = context.getContentResolver().query(YellowPageContract.PhoneLookup.CONTENT_URI, PHONE_PROJECTION, "normalized_number IN (" + sb.toString() + Constants.SEPARATOR_RIGHT_PARENTESIS, null, null);
                    if (cursorQuery != null) {
                        while (cursorQuery.moveToNext()) {
                            try {
                                try {
                                    String str3 = (String) map3.get(cursorQuery.getString(9));
                                    map2.put(str3, buildYellowPagePhoneFromCursor(cursorQuery, str3));
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            } catch (Throwable th) {
                                cursorQuery.close();
                                throw th;
                            }
                        }
                        map3.clear();
                        cursorQuery.close();
                    }
                }
            }
        }
        return map2;
    }

    public static String getNormalizedNumber(Context context, String str, boolean z2, String str2) {
        Cursor cursorQuery;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Uri.Builder builderBuildUpon = Uri.withAppendedPath(YellowPageContract.PhoneLookup.CONTENT_NORMALIZED_NUMBER, str).buildUpon();
        builderBuildUpon.appendQueryParameter("withCountryCode", z2 ? "true" : "false");
        if (!TextUtils.isEmpty(str2)) {
            builderBuildUpon.appendQueryParameter("defaultCountryCode", str2);
        }
        Uri uriBuild = builderBuildUpon.build();
        if (isContentProviderInstalled(context, uriBuild) && (cursorQuery = context.getContentResolver().query(uriBuild, null, null, null, null)) != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    return cursorQuery.getString(0);
                }
            } finally {
                cursorQuery.close();
            }
        }
        return null;
    }
}
