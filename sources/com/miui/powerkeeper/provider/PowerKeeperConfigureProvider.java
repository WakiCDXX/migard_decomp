package com.miui.powerkeeper.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.active.ActiveFakeProvider;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.ThermalDB.ThermalDurationConfigure;
import com.miui.powerkeeper.provider.ThermalDB.ThermalHelper;
import com.miui.powerkeeper.provider.ThermalDB.ThermalInfo;
import com.miui.powerkeeper.statemachine.MiawareBcasHandler;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import com.miui.powerkeeper.utils.OctVmNativeProxy;
import com.miui.powerkeeper.utils.Utils;
import java.io.File;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperConfigureProvider extends ContentProvider {
    public static final String METHOD_GET_BCAS_TEMP_MODE_STATE = "getBcasTempModeState";
    public static final String METHOD_GET_PROCESS_ID_BY_NAME = "getProcessIdByName";
    public static final String METHOD_GET_SLEEP_MODE_STATE = "getSleepModeState";
    public static final String METHOD_SUPPORT_SLEEP_MODE = "isSupportSleepMode";
    private static final int TYPE_APP_ACTIVE = 9;
    private static final int TYPE_APP_ACTIVE_UID = 10;
    private static final int TYPE_BROADCAST_MANAGE_CLOUD_APP = 21;
    private static final int TYPE_BROADCAST_MANAGE_CLOUD_APP_ID = 22;
    private static final int TYPE_BROADCAST_MANAGE_CLOUD_APP_PKG = 23;
    private static final int TYPE_BROADCAST_MANAGE_GLOBAL = 19;
    private static final int TYPE_BROADCAST_MANAGE_GLOBAL_ID = 20;
    private static final int TYPE_CLOUD_APP = 3;
    private static final int TYPE_CLOUD_APP_ID = 4;
    private static final int TYPE_CLOUD_APP_PKG = 5;
    private static final int TYPE_GLOBAL = 1;
    private static final int TYPE_GLOBAL_ID = 2;
    private static final int TYPE_HIGH_REFRESH_RATE = 36;
    private static final int TYPE_POWERCHECKER = 33;
    private static final int TYPE_THERMALINFO = 34;
    private static final int TYPE_THERMAL_DURATION = 35;
    private static final int TYPE_TRACK = 24;
    private static final int TYPE_TRACK_ID = 25;
    private static final int TYPE_USER_APP = 6;
    private static final int TYPE_USER_APP_ID = 7;
    private static final String USER_AUTHORITY_PREFIX = "0@";
    private static final UriMatcher sMatcher;
    private ActiveFakeProvider mActiveProvider;
    private CloudDatabaseHelper mCloudHelper;
    private HighRefreshRateHelper mHighRefreshRateHelper;
    private HomeGestureStatusProvider mHomeGestureStatusProvider;
    private PowerKeeperInterfaceDatabaseHelper mInterfaceDatabaseHelper;
    private PowerCheckerDatabaseHelper mPowerCheckerDatabaseHelper;
    private PowerKeeperStatusFakeProvider mPowerKeeperStatusFakeProvider;
    private SimpleSettingsFakeProvider mSimpleSettingsFakeProvider;
    private ThermalHelper mThermalHelper;
    private UserDatabaseHelper mUserHelper;
    private static final String TAG = PowerKeeperConfigureProvider.class.getSimpleName();
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, GlobalFeatureConfigure.TABLE, 1);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "GlobalFeatureTable/#", 2);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, CloudAppConfigure.TABLE, 3);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "cloudAppTable/#", 4);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "cloudAppTable/*", 5);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, UserConfigure.TABLE, 6);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "userTable/#", 7);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, AppActiveConfigure.TABLE, 9);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "appActiveTable/#", 10);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, BroadcastManageGlobalFeatureConfigure.TABLE, 19);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "BCGlobalFeatureTable/#", 20);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, BroadcastManageCloudAppConfigure.TABLE, 21);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "BcCloudAppTable/#", 22);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "BcCloudAppTable/*", 23);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, TrackConfigure.TABLE, 24);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "TrackTable/#", 25);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, PowerCheckerConfigure.TABLE, 33);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, ThermalInfo.TABLE, TYPE_THERMALINFO);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "thermal_duration", TYPE_THERMAL_DURATION);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, HighRefreshRateConfigure.TABLE, TYPE_HIGH_REFRESH_RATE);
    }

    private static Uri addOwnerUserIdForUri(Uri uri) {
        if (uri == null || !TextUtils.isEmpty(uri.getUserInfo())) {
            return uri;
        }
        Uri.Builder builderBuildUpon = uri.buildUpon();
        builderBuildUpon.encodedAuthority(USER_AUTHORITY_PREFIX + uri.getEncodedAuthority());
        return builderBuildUpon.build();
    }

    private long[] getIdList(Cursor cursor, String str) {
        long[] jArr = new long[cursor.getCount()];
        int columnIndex = cursor.getColumnIndex(str);
        int i2 = 0;
        while (cursor.moveToNext()) {
            jArr[i2] = cursor.getLong(columnIndex);
            i2++;
        }
        return jArr;
    }

    private String getPackage(Cursor cursor, String str) {
        return cursor.getString(cursor.getColumnIndex(str));
    }

    private String[] getPackageList(Cursor cursor, String str) {
        String[] strArr = new String[cursor.getCount()];
        int columnIndex = cursor.getColumnIndex(str);
        int i2 = 0;
        while (cursor.moveToNext()) {
            strArr[i2] = cursor.getString(columnIndex);
            i2++;
        }
        return strArr;
    }

    private void notifyChange(Uri uri, ContentObserver contentObserver) {
        try {
            getContext().getContentResolver().notifyChange(uri, contentObserver);
        } catch (SecurityException e2) {
            e2.printStackTrace();
        }
    }

    private void removeObsoleteDatabase() {
        File file = new File("/data/data/com.miui.powerkeeper/databases/hidden_mode_cloud.db");
        if (file.exists()) {
            if (file.delete()) {
                Log.e(TAG, "/data/data/com.miui.powerkeeper/databases/hidden_mode_cloud.db delete fail");
            }
            if (new File("/data/data/com.miui.powerkeeper/databases/hidden_mode_cloud.db-journal").delete()) {
                Log.e(TAG, "/data/data/com.miui.powerkeeper/databases/hidden_mode_cloud.db-journal delete fail");
            }
        }
        File file2 = new File("/data/data/com.miui.powerkeeper/databases/powerkeeper_interface.db");
        if (file2.exists()) {
            if (file2.delete()) {
                Log.e(TAG, "/data/data/com.miui.powerkeeper/databases/powerkeeper_interface.db delete fail");
            }
            if (new File("/data/data/com.miui.powerkeeper/databases/powerkeeper_interface.db-journal").delete()) {
                Log.e(TAG, "/data/data/com.miui.powerkeeper/databases/powerkeeper_interface.db-journal delete fail");
            }
        }
    }

    @Override // android.content.ContentProvider
    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        if (UserHandle.myUserId() != 0) {
            if (DEBUG) {
                Log.d(TAG, "bulkInsert, userId = " + UserHandle.myUserId());
            }
            return getContext().getContentResolver().bulkInsert(addOwnerUserIdForUri(uri), contentValuesArr);
        }
        if (this.mSimpleSettingsFakeProvider.checkUri(uri)) {
            return this.mSimpleSettingsFakeProvider.bulkInsert(uri, contentValuesArr);
        }
        int length = contentValuesArr.length;
        int iMatch = sMatcher.match(uri);
        int i2 = 0;
        if (iMatch == 1) {
            SQLiteDatabase writableDatabase = this.mCloudHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("configureName", "lastUpdated");
            contentValues.put("configureParam", String.valueOf(System.currentTimeMillis()));
            writableDatabase.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues, "configureName = 'lastUpdated'", null, 5);
            writableDatabase.beginTransaction();
            while (i2 < length) {
                try {
                    writableDatabase.insertWithOnConflict(GlobalFeatureConfigure.TABLE, null, contentValuesArr[i2], 5);
                    i2++;
                } catch (Throwable th) {
                    writableDatabase.endTransaction();
                    throw th;
                }
            }
            writableDatabase.setTransactionSuccessful();
            writableDatabase.endTransaction();
            if (DEBUG) {
                Log.d(TAG, "GlobalFeatureTable bulkInsert");
            }
        } else if (iMatch == 3) {
            SQLiteDatabase writableDatabase2 = this.mCloudHelper.getWritableDatabase();
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("configureName", "lastUpdated");
            contentValues2.put("configureParam", String.valueOf(System.currentTimeMillis()));
            writableDatabase2.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues2, "configureName = 'lastUpdated'", null, 5);
            writableDatabase2.beginTransaction();
            while (i2 < length) {
                try {
                    try {
                        writableDatabase2.insertWithOnConflict(CloudAppConfigure.TABLE, null, contentValuesArr[i2], 5);
                        i2++;
                    } catch (Throwable th2) {
                        writableDatabase2.endTransaction();
                        throw th2;
                    }
                } catch (SQLiteFullException unused) {
                    Log.d(TAG, "cloudAppTable bulkInsert is full.");
                }
            }
            writableDatabase2.setTransactionSuccessful();
            writableDatabase2.endTransaction();
            if (DEBUG) {
                Log.d(TAG, "cloudAppTable bulkInsert");
            }
        } else if (iMatch == 6) {
            SQLiteDatabase writableDatabase3 = this.mUserHelper.getWritableDatabase();
            writableDatabase3.beginTransaction();
            while (i2 < length) {
                try {
                    contentValuesArr[i2].put(UserConfigure.Columns.LAST_CONFIGURED, Long.valueOf(System.currentTimeMillis()));
                    writableDatabase3.insertWithOnConflict(UserConfigure.TABLE, null, contentValuesArr[i2], 5);
                    i2++;
                } catch (Throwable th3) {
                    writableDatabase3.endTransaction();
                    throw th3;
                }
            }
            writableDatabase3.setTransactionSuccessful();
            writableDatabase3.endTransaction();
            if (DEBUG) {
                Log.d(TAG, "userTable bulkInsert");
            }
        } else {
            if (iMatch != 9) {
                throw new IllegalArgumentException("Unsupported URI " + uri);
            }
            SQLiteDatabase writableDatabase4 = this.mInterfaceDatabaseHelper.getWritableDatabase();
            writableDatabase4.beginTransaction();
            while (i2 < length) {
                try {
                    writableDatabase4.insertWithOnConflict(AppActiveConfigure.TABLE, null, contentValuesArr[i2], 5);
                    i2++;
                } catch (Throwable th4) {
                    writableDatabase4.endTransaction();
                    throw th4;
                }
            }
            writableDatabase4.setTransactionSuccessful();
            writableDatabase4.endTransaction();
            if (DEBUG) {
                Log.d(TAG, "appActiveTable bulkInsert");
            }
        }
        notifyChange(uri, null);
        return length;
    }

    /* JADX WARN: Code duplicated, block: B:202:0x057e  */
    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        int callingUserId;
        long j2;
        int callingUserId2;
        int callingUserId3;
        String str3;
        int callingUserId4;
        if (UserHandle.myUserId() != 0) {
            if (DEBUG) {
                Log.d(TAG, "call, userId = " + UserHandle.myUserId());
            }
            return getContext().getContentResolver().call(addOwnerUserIdForUri(PowerKeeperConfigure.CONTENT_URI), str, str2, bundle);
        }
        if (this.mHomeGestureStatusProvider.checkMethod(str)) {
            return this.mHomeGestureStatusProvider.call(str, str2, bundle);
        }
        if (METHOD_GET_PROCESS_ID_BY_NAME.equals(str)) {
            Bundle bundle2 = OctVmNativeProxy.get_pid_by_name(bundle.getStringArray("process_name"));
            if (DEBUG) {
                Log.d(TAG, "call method=" + str + " arg=" + str2 + " extras=" + bundle + " ret=" + bundle2);
            }
            return bundle2;
        }
        if (METHOD_GET_SLEEP_MODE_STATE.equals(str)) {
            Bundle bundle3 = new Bundle();
            SleepModeControllerNew sleepModeControllerNew = SleepModeControllerNew.getInstance();
            if (sleepModeControllerNew != null) {
                bundle3.putBoolean("isInSleep", sleepModeControllerNew.isInSleep());
            } else {
                bundle3.putBoolean("isInSleep", false);
            }
            if (DEBUG) {
                Log.d(TAG, "call method=" + str + " arg=" + str2 + " extras=" + bundle + " ret=" + bundle3);
            }
            return bundle3;
        }
        if (METHOD_SUPPORT_SLEEP_MODE.equals(str)) {
            Bundle bundle4 = new Bundle();
            bundle4.putBoolean("power_support_sleep_mode", Utils.isSleepModeViewVisable(true));
            if (DEBUG) {
                Log.d(TAG, "call method=" + str + " arg=" + str2 + " extras=" + bundle + " supportBundle=" + bundle4);
            }
            return bundle4;
        }
        if (METHOD_GET_BCAS_TEMP_MODE_STATE.equals(str)) {
            Bundle bundle5 = new Bundle();
            bundle5.putInt("BcasTempMode", MiawareBcasHandler.getCurBcasTempMode());
            if (DEBUG) {
                Log.d(TAG, "call method=" + str + " arg=" + str2 + " extras=" + bundle + " ret=" + bundle5);
            }
            return bundle5;
        }
        if (this.mSimpleSettingsFakeProvider.checkMethod(str)) {
            return this.mSimpleSettingsFakeProvider.call(str, str2, bundle);
        }
        if (this.mActiveProvider.checkMethod(str)) {
            return this.mActiveProvider.call(str, str2, bundle);
        }
        if (this.mPowerKeeperStatusFakeProvider.checkMethod(str)) {
            return this.mPowerKeeperStatusFakeProvider.call(str, str2, bundle);
        }
        if (str.equals(GlobalFeatureConfigure.Method.METHOD_QUERY)) {
            SQLiteDatabase readableDatabase = this.mCloudHelper.getReadableDatabase();
            int callingUserId5 = (bundle == null || !bundle.containsKey("userId")) ? UserHandle.getCallingUserId() : bundle.getInt("userId");
            Bundle bundle6 = new Bundle();
            Cursor cursorQuery = readableDatabase.query(GlobalFeatureConfigure.TABLE, null, null, null, null, null, null);
            if (cursorQuery == null) {
                return bundle6;
            }
            if (cursorQuery.getCount() > 0) {
                int columnIndex = cursorQuery.getColumnIndex("userId");
                int columnIndex2 = cursorQuery.getColumnIndex("configureName");
                int columnIndex3 = cursorQuery.getColumnIndex("configureParam");
                while (cursorQuery.moveToNext()) {
                    String string = cursorQuery.getString(columnIndex2);
                    String string2 = cursorQuery.getString(columnIndex3);
                    if (!string.equals(GlobalFeatureConfigure.USER_CONFIGURE_STATUS) || columnIndex < 0) {
                        bundle6.putString(string, string2);
                    } else if (cursorQuery.getInt(columnIndex) == callingUserId5) {
                        bundle6.putString(string, string2);
                    }
                }
            }
            cursorQuery.close();
            return bundle6;
        }
        if (str.equals(GlobalFeatureConfigure.Method.METHOD_INSERT)) {
            if (bundle == null) {
                return null;
            }
            SQLiteDatabase writableDatabase = this.mCloudHelper.getWritableDatabase();
            if (bundle.containsKey("userId")) {
                callingUserId4 = bundle.getInt("userId");
                bundle.remove("userId");
            } else {
                callingUserId4 = UserHandle.getCallingUserId();
            }
            bundle.remove("lastUpdated");
            ContentValues contentValues = new ContentValues();
            if (bundle.containsKey(GlobalFeatureConfigure.USER_CONFIGURE_STATUS)) {
                String string3 = bundle.getString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS);
                str3 = string3.equals("normal") ? "enhance" : string3;
                contentValues.put("userId", Integer.valueOf(callingUserId4));
                contentValues.put("configureName", GlobalFeatureConfigure.USER_CONFIGURE_STATUS);
                contentValues.put("configureParam", str3);
                writableDatabase.insertWithOnConflict(GlobalFeatureConfigure.TABLE, null, contentValues, 5);
                if (callingUserId4 == 0) {
                    EventsAggregator.getInstance(getContext()).notifyPowerModeListeners(str3);
                }
            } else {
                for (String str4 : bundle.keySet()) {
                    contentValues.put("configureName", str4);
                    contentValues.put("configureParam", bundle.getString(str4));
                    writableDatabase.insertWithOnConflict(GlobalFeatureConfigure.TABLE, null, contentValues, 5);
                }
                contentValues.put("configureName", "lastUpdated");
                contentValues.put("configureParam", String.valueOf(System.currentTimeMillis()));
                writableDatabase.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues, "configureName = 'lastUpdated'", null, 5);
            }
            notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
        } else if (str.equals(GlobalFeatureConfigure.Method.METHOD_UPDATE)) {
            if (bundle == null) {
                return null;
            }
            SQLiteDatabase writableDatabase2 = this.mCloudHelper.getWritableDatabase();
            if (bundle.containsKey("userId")) {
                callingUserId3 = bundle.getInt("userId");
                bundle.remove("userId");
            } else {
                callingUserId3 = UserHandle.getCallingUserId();
            }
            bundle.remove("lastUpdated");
            ContentValues contentValues2 = new ContentValues();
            if (bundle.containsKey(GlobalFeatureConfigure.USER_CONFIGURE_STATUS)) {
                String[] strArr = {GlobalFeatureConfigure.USER_CONFIGURE_STATUS, Integer.toString(callingUserId3)};
                Cursor cursorQuery2 = writableDatabase2.query(GlobalFeatureConfigure.TABLE, new String[]{"configureParam"}, "configureName = ? AND userId = ?", strArr, null, null, null);
                if (cursorQuery2 != null && cursorQuery2.getCount() > 0) {
                    cursorQuery2.moveToFirst();
                    int columnIndex4 = cursorQuery2.getColumnIndex("configureParam");
                    if (!cursorQuery2.isNull(columnIndex4)) {
                        cursorQuery2.getString(columnIndex4);
                    }
                }
                if (cursorQuery2 != null) {
                    cursorQuery2.close();
                }
                String string4 = bundle.getString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS);
                str3 = string4.equals("normal") ? "enhance" : string4;
                contentValues2.put("userId", Integer.valueOf(callingUserId3));
                contentValues2.put("configureName", GlobalFeatureConfigure.USER_CONFIGURE_STATUS);
                contentValues2.put("configureParam", str3);
                writableDatabase2.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues2, "configureName = ? AND userId = ?", strArr, 5);
                if (callingUserId3 == 0) {
                    EventsAggregator.getInstance(getContext()).notifyPowerModeListeners(str3);
                }
            } else {
                for (String str5 : bundle.keySet()) {
                    contentValues2.put("configureName", str5);
                    contentValues2.put("configureParam", bundle.getString(str5));
                    writableDatabase2.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues2, "configureName = '" + str5 + "'", null, 5);
                }
                contentValues2.put("configureName", "lastUpdated");
                contentValues2.put("configureParam", String.valueOf(System.currentTimeMillis()));
                writableDatabase2.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues2, "configureName = 'lastUpdated'", null, 5);
            }
            notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
        } else if (str.equals(GlobalFeatureConfigure.Method.METHOD_DELETE)) {
            if (bundle == null) {
                return null;
            }
            SQLiteDatabase writableDatabase3 = this.mCloudHelper.getWritableDatabase();
            if (bundle.containsKey("userId")) {
                callingUserId2 = bundle.getInt("userId");
                bundle.remove("userId");
            } else {
                callingUserId2 = UserHandle.getCallingUserId();
            }
            bundle.remove("lastUpdated");
            ContentValues contentValues3 = new ContentValues();
            if (bundle.containsKey(GlobalFeatureConfigure.USER_CONFIGURE_STATUS)) {
                writableDatabase3.delete(GlobalFeatureConfigure.TABLE, "configureName = ? AND userId = ?", new String[]{GlobalFeatureConfigure.USER_CONFIGURE_STATUS, Integer.toString(callingUserId2)});
                if (callingUserId2 == 0) {
                    EventsAggregator.getInstance(getContext()).notifyPowerModeListeners("disable");
                }
            } else {
                Iterator<String> it = bundle.keySet().iterator();
                while (it.hasNext()) {
                    writableDatabase3.delete(GlobalFeatureConfigure.TABLE, "configureName = '" + it.next() + "'", null);
                }
                contentValues3.put("configureName", "lastUpdated");
                contentValues3.put("configureParam", String.valueOf(System.currentTimeMillis()));
                writableDatabase3.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues3, "configureName = 'lastUpdated'", null, 5);
            }
            notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
        } else if (str.equals(CloudAppConfigure.Method.METHOD_OVERRIDE)) {
            SQLiteDatabase writableDatabase4 = this.mCloudHelper.getWritableDatabase();
            if (bundle == null || !bundle.containsKey("userId")) {
                UserHandle.getCallingUserId();
            } else {
                bundle.getInt("userId");
                bundle.remove("userId");
            }
            ContentValues contentValues4 = new ContentValues();
            contentValues4.put("configureName", "lastUpdated");
            contentValues4.put("configureParam", String.valueOf(System.currentTimeMillis()));
            writableDatabase4.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues4, "configureName = 'lastUpdated'", null, 5);
            writableDatabase4.delete(CloudAppConfigure.TABLE, null, null);
            if (bundle == null) {
                notifyChange(CloudAppConfigure.CONTENT_URI, null);
                return null;
            }
            Parcelable[] parcelableArray = bundle.getParcelableArray(CloudAppConfigure.Method.METHOD_OVERRIDE);
            if (parcelableArray != null) {
                int length = parcelableArray.length;
                ContentValues[] contentValuesArr = new ContentValues[length];
                for (int i2 = 0; i2 < parcelableArray.length; i2++) {
                    contentValuesArr[i2] = (ContentValues) parcelableArray[i2];
                }
                writableDatabase4.beginTransaction();
                for (int i3 = 0; i3 < length; i3++) {
                    try {
                        writableDatabase4.insertWithOnConflict(CloudAppConfigure.TABLE, null, contentValuesArr[i3], 5);
                    } catch (Throwable th) {
                        writableDatabase4.endTransaction();
                        throw th;
                    }
                }
                writableDatabase4.setTransactionSuccessful();
                writableDatabase4.endTransaction();
            }
            notifyChange(CloudAppConfigure.CONTENT_URI, null);
        } else if (str.equals(UserConfigure.Method.METHOD_UPDATE)) {
            if (bundle == null || !bundle.containsKey("userId")) {
                callingUserId = UserHandle.getCallingUserId();
            } else {
                callingUserId = bundle.getInt("userId");
                bundle.remove("userId");
            }
            boolean z2 = DEBUG;
            if (z2) {
                Log.d(TAG, "call, callingUserId = " + callingUserId);
            }
            SQLiteDatabase writableDatabase5 = this.mUserHelper.getWritableDatabase();
            if (bundle == null) {
                return null;
            }
            ContentValues contentValues5 = new ContentValues();
            contentValues5.put("userId", Integer.valueOf(callingUserId));
            String string5 = bundle.getString("pkgName");
            if (string5 == null) {
                throw new IllegalArgumentException("Missed pkgName");
            }
            contentValues5.put("pkgName", string5);
            String string6 = bundle.getString(UserConfigure.Columns.BG_CONTROL);
            if (string6 == null) {
                throw new IllegalArgumentException("Missed bgControl");
            }
            contentValues5.put(UserConfigure.Columns.BG_CONTROL, string6);
            Cursor cursorQuery3 = writableDatabase5.query(UserConfigure.TABLE, new String[]{"_id"}, "userId = ? AND pkgName = ?", new String[]{Integer.toString(callingUserId), string5}, null, null, null);
            if (cursorQuery3 == null || cursorQuery3.getCount() <= 0) {
                j2 = -1;
            } else {
                cursorQuery3.moveToFirst();
                int columnIndex5 = cursorQuery3.getColumnIndex("_id");
                if (cursorQuery3.isNull(columnIndex5)) {
                    j2 = -1;
                } else {
                    j2 = cursorQuery3.getLong(columnIndex5);
                }
            }
            if (cursorQuery3 != null) {
                cursorQuery3.close();
            }
            if (j2 == -1) {
                Log.e(TAG, "Missed _id, callingUserId = " + callingUserId + ", pkgName = " + string5);
                return null;
            }
            int i4 = bundle.getInt(UserConfigure.Columns.BG_DELAY_MIN, -1);
            if (i4 < 0) {
                Cursor cursorQuery4 = writableDatabase5.query(UserConfigure.TABLE, new String[]{UserConfigure.Columns.BG_DELAY_MIN}, "userId = ? AND pkgName = ?", new String[]{Integer.toString(callingUserId), string5}, null, null, null);
                if (cursorQuery4 != null && cursorQuery4.getCount() > 0) {
                    cursorQuery4.moveToFirst();
                    int columnIndex6 = cursorQuery4.getColumnIndex(UserConfigure.Columns.BG_DELAY_MIN);
                    if (!cursorQuery4.isNull(columnIndex6)) {
                        i4 = cursorQuery4.getInt(columnIndex6);
                    } else if (z2) {
                        Log.d(TAG, "User Table: packageName = " + string5 + ", bgDelayMin = null");
                    }
                }
                if (cursorQuery4 != null) {
                    cursorQuery4.close();
                }
            }
            if (i4 >= 0) {
                contentValues5.put(UserConfigure.Columns.BG_DELAY_MIN, Integer.valueOf(i4));
            } else if (string6.equals(UserConfigure.BG_CONTROL_RESTRICT_BG)) {
                contentValues5.put(UserConfigure.Columns.BG_DELAY_MIN, (Integer) 10);
            }
            contentValues5.put(UserConfigure.Columns.LAST_CONFIGURED, Long.valueOf(System.currentTimeMillis()));
            writableDatabase5.update(UserConfigure.TABLE, contentValues5, "userId = ? AND pkgName = ?", new String[]{Integer.toString(callingUserId), string5});
            notifyChange(ContentUris.withAppendedId(UserConfigure.CONTENT_URI, j2), null);
        } else if (str.equals(BroadcastManageCloudAppConfigure.Method.METHOD_OVERRIDE)) {
            SQLiteDatabase writableDatabase6 = this.mCloudHelper.getWritableDatabase();
            writableDatabase6.delete(BroadcastManageCloudAppConfigure.TABLE, null, null);
            if (bundle == null) {
                notifyChange(BroadcastManageCloudAppConfigure.CONTENT_URI, null);
                return null;
            }
            Parcelable[] parcelableArray2 = bundle.getParcelableArray(BroadcastManageCloudAppConfigure.Method.METHOD_OVERRIDE);
            if (parcelableArray2 != null) {
                int length2 = parcelableArray2.length;
                ContentValues[] contentValuesArr2 = new ContentValues[length2];
                for (int i5 = 0; i5 < parcelableArray2.length; i5++) {
                    contentValuesArr2[i5] = (ContentValues) parcelableArray2[i5];
                }
                writableDatabase6.beginTransaction();
                for (int i6 = 0; i6 < length2; i6++) {
                    try {
                        writableDatabase6.insertWithOnConflict(BroadcastManageCloudAppConfigure.TABLE, null, contentValuesArr2[i6], 5);
                    } catch (Throwable th2) {
                        writableDatabase6.endTransaction();
                        throw th2;
                    }
                }
                writableDatabase6.setTransactionSuccessful();
                writableDatabase6.endTransaction();
            }
            notifyChange(BroadcastManageCloudAppConfigure.CONTENT_URI, null);
        } else if (str.equals(BroadcastManageGlobalFeatureConfigure.Method.METHOD_UPDATE)) {
            if (bundle == null) {
                return null;
            }
            SQLiteDatabase writableDatabase7 = this.mCloudHelper.getWritableDatabase();
            ContentValues contentValues6 = new ContentValues();
            if (bundle.containsKey(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS)) {
                String[] strArr2 = {BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS};
                contentValues6.put("configureName", BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS);
                contentValues6.put("configureParam", bundle.getString(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS));
                writableDatabase7.updateWithOnConflict(BroadcastManageGlobalFeatureConfigure.TABLE, contentValues6, "configureName = ?", strArr2, 5);
            }
            if (bundle.containsKey("b_delay")) {
                contentValues6.put("configureName", "b_delay");
                contentValues6.put("configureParam", bundle.getString("b_delay"));
                writableDatabase7.updateWithOnConflict(BroadcastManageGlobalFeatureConfigure.TABLE, contentValues6, "configureName = ?", new String[]{"b_delay"}, 5);
            }
            notifyChange(BroadcastManageGlobalFeatureConfigure.CONTENT_URI, null);
        } else if (str.equals(BroadcastManageGlobalFeatureConfigure.Method.METHOD_QUERY)) {
            SQLiteDatabase readableDatabase2 = this.mCloudHelper.getReadableDatabase();
            Bundle bundle7 = new Bundle();
            Cursor cursorQuery5 = readableDatabase2.query(BroadcastManageGlobalFeatureConfigure.TABLE, null, null, null, null, null, null);
            if (cursorQuery5 == null) {
                return bundle7;
            }
            int columnIndex7 = cursorQuery5.getColumnIndex("configureName");
            int columnIndex8 = cursorQuery5.getColumnIndex("configureParam");
            while (cursorQuery5.moveToNext()) {
                String string7 = cursorQuery5.getString(columnIndex7);
                if (string7.equals(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS)) {
                    bundle7.putString(string7, cursorQuery5.getString(columnIndex8));
                }
                if (string7.equals("b_delay")) {
                    bundle7.putInt(string7, cursorQuery5.getInt(columnIndex8));
                }
            }
            cursorQuery5.close();
            return bundle7;
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        int[] iArr;
        int iDelete;
        String[] packageList;
        long[] idList;
        String strConcatenateWhere = str;
        if (UserHandle.myUserId() != 0) {
            if (DEBUG) {
                Log.d(TAG, "delete, userId = " + UserHandle.myUserId());
            }
            return getContext().getContentResolver().delete(addOwnerUserIdForUri(uri), strConcatenateWhere, strArr);
        }
        if (this.mSimpleSettingsFakeProvider.checkUri(uri)) {
            return this.mSimpleSettingsFakeProvider.delete(uri, strConcatenateWhere, strArr);
        }
        UriMatcher uriMatcher = sMatcher;
        int iMatch = uriMatcher.match(uri);
        if (iMatch == 2) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), strConcatenateWhere);
        } else if (iMatch == 7) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), strConcatenateWhere);
        } else if (iMatch == 10) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("uid = " + ContentUris.parseId(uri), strConcatenateWhere);
        } else if (iMatch == 4) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), strConcatenateWhere);
        } else if (iMatch == 5) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("pkgName = '" + uri.getLastPathSegment() + "'", strConcatenateWhere);
        }
        String str2 = strConcatenateWhere;
        int iMatch2 = uriMatcher.match(uri);
        int i2 = 0;
        if (iMatch2 == 9 || iMatch2 == 10) {
            SQLiteDatabase writableDatabase = this.mInterfaceDatabaseHelper.getWritableDatabase();
            Cursor cursorQuery = writableDatabase.query(AppActiveConfigure.TABLE, new String[]{"uid"}, str2, strArr, null, null, null);
            if (cursorQuery != null) {
                iArr = new int[cursorQuery.getCount()];
                int columnIndex = cursorQuery.getColumnIndex("uid");
                int i3 = 0;
                while (cursorQuery.moveToNext()) {
                    iArr[i3] = cursorQuery.getInt(columnIndex);
                    i3++;
                }
                cursorQuery.close();
            } else {
                iArr = null;
            }
            iDelete = writableDatabase.delete(AppActiveConfigure.TABLE, str2, strArr);
            if (iArr != null) {
                int length = iArr.length;
                while (i2 < length) {
                    notifyChange(ContentUris.withAppendedId(AppActiveConfigure.CONTENT_URI, iArr[i2]), null);
                    i2++;
                }
            }
        } else {
            if (iMatch2 == 25) {
                int iDelete2 = this.mCloudHelper.getWritableDatabase().delete(TrackConfigure.TABLE, str2, strArr);
                notifyChange(uri, null);
                return iDelete2;
            }
            switch (iMatch2) {
                case 1:
                case 2:
                    this.mCloudHelper.getWritableDatabase().delete(GlobalFeatureConfigure.TABLE, str2, strArr);
                    notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
                    return 0;
                case 3:
                case 4:
                case 5:
                    SQLiteDatabase writableDatabase2 = this.mCloudHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("configureName", "lastUpdated");
                    contentValues.put("configureParam", String.valueOf(System.currentTimeMillis()));
                    writableDatabase2.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues, "configureName = 'lastUpdated'", null, 5);
                    Cursor cursorQuery2 = writableDatabase2.query(CloudAppConfigure.TABLE, new String[]{"pkgName"}, str2, strArr, null, null, null);
                    if (cursorQuery2 != null) {
                        packageList = getPackageList(cursorQuery2, "pkgName");
                        cursorQuery2.close();
                    } else {
                        packageList = null;
                    }
                    iDelete = writableDatabase2.delete(CloudAppConfigure.TABLE, str2, strArr);
                    if (packageList != null) {
                        int length2 = packageList.length;
                        while (i2 < length2) {
                            notifyChange(Uri.withAppendedPath(CloudAppConfigure.CONTENT_URI, packageList[i2]), null);
                            i2++;
                        }
                    }
                    break;
                case 6:
                case 7:
                    SQLiteDatabase writableDatabase3 = this.mUserHelper.getWritableDatabase();
                    Cursor cursorQuery3 = writableDatabase3.query(UserConfigure.TABLE, new String[]{"_id"}, str2, strArr, null, null, null);
                    if (cursorQuery3 != null) {
                        idList = getIdList(cursorQuery3, "_id");
                        cursorQuery3.close();
                    } else {
                        idList = null;
                    }
                    iDelete = writableDatabase3.delete(UserConfigure.TABLE, str2, strArr);
                    if (idList != null) {
                        int length3 = idList.length;
                        while (i2 < length3) {
                            notifyChange(ContentUris.withAppendedId(UserConfigure.CONTENT_URI, idList[i2]), null);
                            i2++;
                        }
                    }
                    break;
                default:
                    switch (iMatch2) {
                        case TYPE_THERMALINFO /* 34 */:
                            int iDelete3 = this.mThermalHelper.getWritableDatabase().delete(ThermalInfo.TABLE, str2, strArr);
                            notifyChange(uri, null);
                            return iDelete3;
                        case TYPE_THERMAL_DURATION /* 35 */:
                            int iDelete4 = this.mThermalHelper.getWritableDatabase().delete("thermal_duration", str2, strArr);
                            notifyChange(uri, null);
                            return iDelete4;
                        case TYPE_HIGH_REFRESH_RATE /* 36 */:
                            int iDelete5 = this.mHighRefreshRateHelper.getWritableDatabase().delete(HighRefreshRateConfigure.TABLE, str2, strArr);
                            notifyChange(uri, null);
                            return iDelete5;
                        default:
                            return 0;
                    }
            }
        }
        return iDelete;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        if (UserHandle.myUserId() == 0) {
            if (this.mSimpleSettingsFakeProvider.checkUri(uri)) {
                return this.mSimpleSettingsFakeProvider.getType(uri);
            }
            return null;
        }
        if (DEBUG) {
            Log.d(TAG, "getType, userId = " + UserHandle.myUserId());
        }
        return getContext().getContentResolver().getType(addOwnerUserIdForUri(uri));
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri uriWithAppendedId;
        if (UserHandle.myUserId() != 0) {
            if (DEBUG) {
                Log.d(TAG, "insert, userId = " + UserHandle.myUserId());
            }
            return getContext().getContentResolver().insert(addOwnerUserIdForUri(uri), contentValues);
        }
        if (this.mSimpleSettingsFakeProvider.checkUri(uri)) {
            return this.mSimpleSettingsFakeProvider.insert(uri, contentValues);
        }
        if (this.mActiveProvider.checkUri(uri)) {
            return this.mActiveProvider.insert(uri, contentValues);
        }
        int iMatch = sMatcher.match(uri);
        if (iMatch == 1) {
            SQLiteDatabase writableDatabase = this.mCloudHelper.getWritableDatabase();
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("configureName", "lastUpdated");
            contentValues2.put("configureParam", String.valueOf(System.currentTimeMillis()));
            writableDatabase.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues2, "configureName = 'lastUpdated'", null, 5);
            long jInsertWithOnConflict = writableDatabase.insertWithOnConflict(GlobalFeatureConfigure.TABLE, null, contentValues, 5);
            if (DEBUG) {
                Log.d(TAG, "GlobalFeatureTable insert " + jInsertWithOnConflict);
            }
            uriWithAppendedId = ContentUris.withAppendedId(uri, jInsertWithOnConflict);
        } else if (iMatch == 3) {
            SQLiteDatabase writableDatabase2 = this.mCloudHelper.getWritableDatabase();
            String asString = contentValues.getAsString("pkgName");
            if (asString == null) {
                throw new IllegalArgumentException("Missed pkgName");
            }
            ContentValues contentValues3 = new ContentValues();
            contentValues3.put("configureName", "lastUpdated");
            contentValues3.put("configureParam", String.valueOf(System.currentTimeMillis()));
            writableDatabase2.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues3, "configureName = 'lastUpdated'", null, 5);
            long jInsertWithOnConflict2 = writableDatabase2.insertWithOnConflict(CloudAppConfigure.TABLE, null, contentValues, 5);
            if (DEBUG) {
                Log.d(TAG, "cloudAppTable insert " + jInsertWithOnConflict2);
            }
            uriWithAppendedId = Uri.withAppendedPath(CloudAppConfigure.CONTENT_URI, asString);
        } else if (iMatch == 6) {
            SQLiteDatabase writableDatabase3 = this.mUserHelper.getWritableDatabase();
            if (contentValues.getAsInteger("userId") == null) {
                throw new IllegalArgumentException("Missed userId");
            }
            if (contentValues.getAsString("pkgName") == null) {
                throw new IllegalArgumentException("Missed pkgName");
            }
            contentValues.put(UserConfigure.Columns.LAST_CONFIGURED, Long.valueOf(System.currentTimeMillis()));
            long jInsertWithOnConflict3 = writableDatabase3.insertWithOnConflict(UserConfigure.TABLE, null, contentValues, 5);
            if (DEBUG) {
                Log.d(TAG, "userTable insert " + jInsertWithOnConflict3);
            }
            uriWithAppendedId = ContentUris.withAppendedId(UserConfigure.CONTENT_URI, jInsertWithOnConflict3);
        } else if (iMatch == 9) {
            SQLiteDatabase writableDatabase4 = this.mInterfaceDatabaseHelper.getWritableDatabase();
            Integer asInteger = contentValues.getAsInteger("uid");
            if (asInteger == null) {
                throw new IllegalArgumentException("Missed uid");
            }
            long jInsertWithOnConflict4 = writableDatabase4.insertWithOnConflict(AppActiveConfigure.TABLE, null, contentValues, 5);
            if (DEBUG) {
                Log.d(TAG, "appActiveTable insert " + jInsertWithOnConflict4);
            }
            uriWithAppendedId = ContentUris.withAppendedId(AppActiveConfigure.CONTENT_URI, asInteger.intValue());
        } else if (iMatch != 25) {
            switch (iMatch) {
                case TYPE_THERMALINFO /* 34 */:
                    long jInsertWithOnConflict5 = this.mThermalHelper.getReadableDatabase().insertWithOnConflict(ThermalInfo.TABLE, null, contentValues, 5);
                    if (DEBUG) {
                        Log.d(TAG, "ThermalInfo insert " + jInsertWithOnConflict5);
                    }
                    uriWithAppendedId = ContentUris.withAppendedId(ThermalInfo.CONTENT_URI, jInsertWithOnConflict5);
                    break;
                case TYPE_THERMAL_DURATION /* 35 */:
                    long jInsertWithOnConflict6 = this.mThermalHelper.getWritableDatabase().insertWithOnConflict("thermal_duration", null, contentValues, 5);
                    if (DEBUG) {
                        Log.d(TAG, "thermal_duration insert " + jInsertWithOnConflict6);
                    }
                    uriWithAppendedId = ContentUris.withAppendedId(ThermalDurationConfigure.CONTENT_URI, jInsertWithOnConflict6);
                    break;
                case TYPE_HIGH_REFRESH_RATE /* 36 */:
                    long jInsertWithOnConflict7 = this.mHighRefreshRateHelper.getWritableDatabase().insertWithOnConflict(HighRefreshRateConfigure.TABLE, null, contentValues, 5);
                    if (DEBUG) {
                        Log.d(TAG, "highRefreshRateTable insert " + jInsertWithOnConflict7);
                    }
                    uriWithAppendedId = ContentUris.withAppendedId(HighRefreshRateConfigure.CONTENT_URI, jInsertWithOnConflict7);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported URI " + uri);
            }
        } else {
            long jInsertWithOnConflict8 = this.mCloudHelper.getWritableDatabase().insertWithOnConflict(TrackConfigure.TABLE, null, contentValues, 5);
            if (DEBUG) {
                Log.d(TAG, "TrackTable insert " + jInsertWithOnConflict8);
            }
            uriWithAppendedId = ContentUris.withAppendedId(TrackConfigure.CONTENT_URI, jInsertWithOnConflict8);
        }
        notifyChange(uriWithAppendedId, null);
        return uriWithAppendedId;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        if (UserHandle.myUserId() != 0) {
            if (DEBUG) {
                Log.d(TAG, "onCreate, userId = " + UserHandle.myUserId());
            }
            return true;
        }
        removeObsoleteDatabase();
        this.mCloudHelper = new CloudDatabaseHelper(getContext());
        this.mUserHelper = new UserDatabaseHelper(getContext());
        this.mPowerCheckerDatabaseHelper = PowerCheckerDatabaseHelper.getInstance(getContext());
        this.mInterfaceDatabaseHelper = new PowerKeeperInterfaceDatabaseHelper(getContext());
        this.mThermalHelper = new ThermalHelper(getContext());
        this.mSimpleSettingsFakeProvider = new SimpleSettingsFakeProvider(getContext(), this.mUserHelper);
        this.mPowerKeeperStatusFakeProvider = new PowerKeeperStatusFakeProvider(getContext());
        this.mHomeGestureStatusProvider = new HomeGestureStatusProvider(getContext());
        this.mActiveProvider = new ActiveFakeProvider();
        this.mHighRefreshRateHelper = HighRefreshRateHelper.getInstance(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteDatabase readableDatabase;
        if (UserHandle.myUserId() != 0) {
            if (DEBUG) {
                Log.d(TAG, "query, userId = " + UserHandle.myUserId());
            }
            return getContext().getContentResolver().query(addOwnerUserIdForUri(uri), strArr, str, strArr2, str2);
        }
        if (this.mSimpleSettingsFakeProvider.checkUri(uri)) {
            return this.mSimpleSettingsFakeProvider.query(uri, strArr, str, strArr2, str2);
        }
        if (this.mActiveProvider.checkUri(uri)) {
            return this.mActiveProvider.query(uri, strArr, str, strArr2, str2);
        }
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        UriMatcher uriMatcher = sMatcher;
        int iMatch = uriMatcher.match(uri);
        if (iMatch == 2) {
            str = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), str);
        } else if (iMatch == 7) {
            str = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), str);
        } else if (iMatch == 10) {
            str = DatabaseUtils.concatenateWhere("uid = " + ContentUris.parseId(uri), str);
        } else if (iMatch == 20) {
            str = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), str);
        } else if (iMatch == 33) {
            str = "flag = ? ";
        } else if (iMatch == 4) {
            str = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), str);
        } else if (iMatch != 5) {
            switch (iMatch) {
                case 22:
                    str = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), str);
                    break;
                case 23:
                    str = DatabaseUtils.concatenateWhere("pkgName = '" + uri.getLastPathSegment() + "'", str);
                    break;
                case 24:
                    str = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), str);
                    Log.d(TAG, "query id= " + str);
                    break;
            }
        } else {
            str = DatabaseUtils.concatenateWhere("pkgName = '" + uri.getLastPathSegment() + "'", str);
        }
        String str3 = str;
        int iMatch2 = uriMatcher.match(uri);
        if (iMatch2 == 9 || iMatch2 == 10) {
            readableDatabase = this.mInterfaceDatabaseHelper.getReadableDatabase();
            sQLiteQueryBuilder.setTables(AppActiveConfigure.TABLE);
        } else if (iMatch2 != 25) {
            switch (iMatch2) {
                case 1:
                case 2:
                    readableDatabase = this.mCloudHelper.getReadableDatabase();
                    sQLiteQueryBuilder.setTables(GlobalFeatureConfigure.TABLE);
                    break;
                case 3:
                case 4:
                case 5:
                    readableDatabase = this.mCloudHelper.getReadableDatabase();
                    sQLiteQueryBuilder.setTables(CloudAppConfigure.TABLE);
                    break;
                case 6:
                case 7:
                    readableDatabase = this.mUserHelper.getReadableDatabase();
                    sQLiteQueryBuilder.setTables(UserConfigure.TABLE);
                    break;
                default:
                    switch (iMatch2) {
                        case 19:
                        case 20:
                            readableDatabase = this.mCloudHelper.getReadableDatabase();
                            sQLiteQueryBuilder.setTables(BroadcastManageGlobalFeatureConfigure.TABLE);
                            break;
                        case 21:
                        case 22:
                        case 23:
                            readableDatabase = this.mCloudHelper.getReadableDatabase();
                            sQLiteQueryBuilder.setTables(BroadcastManageCloudAppConfigure.TABLE);
                            break;
                        default:
                            switch (iMatch2) {
                                case 33:
                                    readableDatabase = this.mPowerCheckerDatabaseHelper.getReadableDatabase();
                                    sQLiteQueryBuilder.setTables(PowerCheckerConfigure.TABLE);
                                    break;
                                case TYPE_THERMALINFO /* 34 */:
                                    readableDatabase = this.mThermalHelper.getReadableDatabase();
                                    sQLiteQueryBuilder.setTables(ThermalInfo.TABLE);
                                    break;
                                case TYPE_THERMAL_DURATION /* 35 */:
                                    readableDatabase = this.mThermalHelper.getReadableDatabase();
                                    sQLiteQueryBuilder.setTables("thermal_duration");
                                    break;
                                case TYPE_HIGH_REFRESH_RATE /* 36 */:
                                    readableDatabase = this.mHighRefreshRateHelper.getReadableDatabase();
                                    sQLiteQueryBuilder.setTables(HighRefreshRateConfigure.TABLE);
                                    break;
                                default:
                                    return null;
                            }
                            break;
                    }
                    break;
            }
        } else {
            readableDatabase = this.mCloudHelper.getReadableDatabase();
            sQLiteQueryBuilder.setTables(TrackConfigure.TABLE);
        }
        Cursor cursorQuery = sQLiteQueryBuilder.query(readableDatabase, strArr, str3, strArr2, null, null, str2);
        cursorQuery.setNotificationUri(getContext().getContentResolver(), uri);
        return cursorQuery;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int[] iArr;
        int iUpdateWithOnConflict;
        int iUpdateWithOnConflict2;
        String[] packageList;
        long[] idList;
        String strConcatenateWhere = str;
        if (UserHandle.myUserId() != 0) {
            if (DEBUG) {
                Log.d(TAG, "update, userId = " + UserHandle.myUserId());
            }
            return getContext().getContentResolver().update(addOwnerUserIdForUri(uri), contentValues, strConcatenateWhere, strArr);
        }
        if (this.mSimpleSettingsFakeProvider.checkUri(uri)) {
            return this.mSimpleSettingsFakeProvider.update(uri, contentValues, strConcatenateWhere, strArr);
        }
        UriMatcher uriMatcher = sMatcher;
        int iMatch = uriMatcher.match(uri);
        if (iMatch == 2) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), strConcatenateWhere);
        } else if (iMatch == 7) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), strConcatenateWhere);
        } else if (iMatch == 10) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("uid = " + ContentUris.parseId(uri), strConcatenateWhere);
        } else if (iMatch == 25) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("timeId = '" + uri.getLastPathSegment() + "'", strConcatenateWhere);
            Log.d(TAG, "update  TYPE_RECORD_ID " + uri.getLastPathSegment());
        } else if (iMatch == 4) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("_id = " + ContentUris.parseId(uri), strConcatenateWhere);
        } else if (iMatch == 5) {
            strConcatenateWhere = DatabaseUtils.concatenateWhere("pkgName = '" + uri.getLastPathSegment() + "'", strConcatenateWhere);
        }
        String str2 = strConcatenateWhere;
        int iMatch2 = uriMatcher.match(uri);
        if (iMatch2 != 9 && iMatch2 != 10) {
            if (iMatch2 == 25) {
                iUpdateWithOnConflict2 = this.mCloudHelper.getWritableDatabase().updateWithOnConflict(TrackConfigure.TABLE, contentValues, str2, strArr, 5);
                notifyChange(uri, null);
            } else if (iMatch2 != TYPE_HIGH_REFRESH_RATE) {
                switch (iMatch2) {
                    case 1:
                    case 2:
                        SQLiteDatabase writableDatabase = this.mCloudHelper.getWritableDatabase();
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put("configureName", "lastUpdated");
                        contentValues2.put("configureParam", String.valueOf(System.currentTimeMillis()));
                        writableDatabase.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues2, "configureName = 'lastUpdated'", null, 5);
                        iUpdateWithOnConflict = writableDatabase.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues, str2, strArr, 5);
                        notifyChange(GlobalFeatureConfigure.CONTENT_URI, null);
                        break;
                    case 3:
                    case 4:
                    case 5:
                        SQLiteDatabase writableDatabase2 = this.mCloudHelper.getWritableDatabase();
                        ContentValues contentValues3 = new ContentValues();
                        contentValues3.put("configureName", "lastUpdated");
                        contentValues3.put("configureParam", String.valueOf(System.currentTimeMillis()));
                        writableDatabase2.updateWithOnConflict(GlobalFeatureConfigure.TABLE, contentValues3, "configureName = 'lastUpdated'", null, 5);
                        Cursor cursorQuery = writableDatabase2.query(CloudAppConfigure.TABLE, new String[]{"pkgName"}, str2, strArr, null, null, null);
                        if (cursorQuery != null) {
                            packageList = getPackageList(cursorQuery, "pkgName");
                            cursorQuery.close();
                        } else {
                            packageList = null;
                        }
                        iUpdateWithOnConflict = writableDatabase2.updateWithOnConflict(CloudAppConfigure.TABLE, contentValues, str2, strArr, 5);
                        if (packageList != null) {
                            for (String str3 : packageList) {
                                notifyChange(Uri.withAppendedPath(CloudAppConfigure.CONTENT_URI, str3), null);
                            }
                        }
                        break;
                    case 6:
                    case 7:
                        SQLiteDatabase writableDatabase3 = this.mUserHelper.getWritableDatabase();
                        Cursor cursorQuery2 = writableDatabase3.query(UserConfigure.TABLE, new String[]{"_id"}, str2, strArr, null, null, null);
                        if (cursorQuery2 != null) {
                            idList = getIdList(cursorQuery2, "_id");
                            cursorQuery2.close();
                        } else {
                            idList = null;
                        }
                        contentValues.put(UserConfigure.Columns.LAST_CONFIGURED, Long.valueOf(System.currentTimeMillis()));
                        iUpdateWithOnConflict = writableDatabase3.updateWithOnConflict(UserConfigure.TABLE, contentValues, str2, strArr, 5);
                        if (idList != null) {
                            for (long j2 : idList) {
                                notifyChange(ContentUris.withAppendedId(UserConfigure.CONTENT_URI, j2), null);
                            }
                        }
                        break;
                    default:
                        return 0;
                }
            } else {
                iUpdateWithOnConflict2 = this.mHighRefreshRateHelper.getWritableDatabase().updateWithOnConflict(HighRefreshRateConfigure.TABLE, contentValues, str2, strArr, 5);
                notifyChange(uri, null);
            }
            return iUpdateWithOnConflict2;
        }
        SQLiteDatabase writableDatabase4 = this.mInterfaceDatabaseHelper.getWritableDatabase();
        Cursor cursorQuery3 = writableDatabase4.query(AppActiveConfigure.TABLE, new String[]{"uid"}, str2, strArr, null, null, null);
        if (cursorQuery3 != null) {
            iArr = new int[cursorQuery3.getCount()];
            int columnIndex = cursorQuery3.getColumnIndex("uid");
            int i2 = 0;
            while (cursorQuery3.moveToNext()) {
                iArr[i2] = cursorQuery3.getInt(columnIndex);
                i2++;
            }
            cursorQuery3.close();
        } else {
            iArr = null;
        }
        iUpdateWithOnConflict = writableDatabase4.updateWithOnConflict(AppActiveConfigure.TABLE, contentValues, str2, strArr, 5);
        if (iArr != null) {
            for (int i3 : iArr) {
                notifyChange(ContentUris.withAppendedId(AppActiveConfigure.CONTENT_URI, i3), null);
            }
        }
        return iUpdateWithOnConflict;
    }
}
