package com.miui.powerkeeper.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.UserHandle;
import android.util.Log;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoCollector;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.provider.ThermalDB.ThermalInfo;
import com.miui.powerkeeper.tracker.TrackerManager;
import com.miui.powerkeeper.utils.FileUtil;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class ThermalInfoHelper {
    private static final String TAG = "ThermalInfoHelper";
    public static final Object sThermalLogLock = new Object();
    private static boolean sIsTestMode = ThermalInfoUtils.isTestMode();
    private static String TOTAL_EVENT = "total";
    private static String APP_EVENT = "apps";
    private static String APP_EXCP_EVENT = ThermalInfoCollector.TYPICAL_APPS_DATA_EXCP_TYPE;
    private static final String[] UPLOAD_EVENTS = {ThermalInfoCollector.PHONE_HIGHTEMP_TYPE, ThermalInfoCollector.PHONE_TORCH_BRIGHTNESS_LEVEL_TYPE, ThermalInfoCollector.PHONE_THERMAL_IEC_STATE_TYPE};

    private ThermalInfoHelper() {
    }

    public static void cleanThemalLog(Context context) {
        if (sIsTestMode) {
            Log.i(TAG, "cleanThemalLog");
        }
        if (ThermalInfoCollector.get(context).catchLogEnable()) {
            try {
                synchronized (sThermalLogLock) {
                    FileUtil.deleteFiles(new File("/data/vendor/thermal/log").listFiles());
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void cleanThermalInfo(Context context) {
        if (sIsTestMode) {
            Log.d(TAG, "cleanThermalInfo");
        }
        Cursor cursor = null;
        try {
            try {
                try {
                    Cursor cursorQuery = context.getContentResolverForUser(UserHandle.OWNER).query(ThermalInfo.CONTENT_URI, null, null, null, null);
                    if (cursorQuery == null) {
                        if (cursorQuery != null) {
                            try {
                                cursorQuery.close();
                                return;
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                return;
                            }
                        }
                        return;
                    }
                    while (cursorQuery.moveToNext()) {
                        System.currentTimeMillis();
                        long jCurrentTimeMillis = System.currentTimeMillis() - Long.parseLong(cursorQuery.getString(cursorQuery.getColumnIndex("time")));
                        if (jCurrentTimeMillis < 0 || jCurrentTimeMillis >= 86400000) {
                            deleteThermalInfo(context, cursorQuery.getString(cursorQuery.getColumnIndex("_id")));
                        }
                    }
                    cursorQuery.close();
                    cleanThemalLog(context);
                } catch (Throwable th) {
                    if (0 != 0) {
                        try {
                            cursor.close();
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception e4) {
                if (sIsTestMode) {
                    Log.e(TAG, "cleanThermalInfo", e4);
                }
                if (0 != 0) {
                    cursor.close();
                }
                cleanThemalLog(context);
            }
        } catch (Exception e5) {
            e5.printStackTrace();
        }
    }

    private static void deleteThermalInfo(Context context, String str) {
        if (sIsTestMode) {
            Log.d(TAG, "deleteThermalInfo");
        }
        if (str == null || str.isEmpty()) {
            return;
        }
        try {
            context.getContentResolverForUser(UserHandle.OWNER).delete(ThermalInfo.CONTENT_URI, DatabaseUtils.concatenateWhere("_id = '" + str + "'", null), null);
        } catch (Exception e2) {
            Log.e(TAG, "deleteThermalInfo", e2);
        }
    }

    public static void insertThermalInfo(Context context, String str, String str2) {
        if (str == null || str.isEmpty()) {
            return;
        }
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("type", str2);
            contentValues.put(ThermalInfo.Columns.INFO, str);
            contentValues.put("time", Long.valueOf(System.currentTimeMillis()));
            context.getContentResolverForUser(UserHandle.OWNER).insert(ThermalInfo.CONTENT_URI, contentValues);
        } catch (Exception e2) {
            Log.e(TAG, "insertThermalInfo", e2);
        }
    }

    public static void uploadThermalInfo(Context context) {
        TrackerManager trackerManager = TrackerManager.getInstance(context);
        Cursor cursorQuery = context.getContentResolverForUser(UserHandle.OWNER).query(ThermalInfo.CONTENT_URI, null, null, null, null);
        if (cursorQuery == null) {
            return;
        }
        while (cursorQuery.moveToNext()) {
            try {
                try {
                    String string = cursorQuery.getString(cursorQuery.getColumnIndex(ThermalInfo.Columns.INFO));
                    String string2 = cursorQuery.getString(cursorQuery.getColumnIndex("type"));
                    deleteThermalInfo(context, cursorQuery.getString(cursorQuery.getColumnIndex("_id")));
                    if (string != null && string2 != null) {
                        trackerManager.track(1, string2, string);
                        if (APP_EVENT.equals(string2)) {
                            trackerManager.track(3, string2, string);
                        }
                        if (sIsTestMode) {
                            Log.d(TAG, "uploadThermalInfo : " + string);
                        }
                    }
                } catch (Exception unused) {
                    Log.e(TAG, "uploadThermalInfo error!");
                }
            } catch (Throwable th) {
                try {
                    cursorQuery.close();
                } catch (Exception unused2) {
                    Log.e(TAG, "close cursor error!");
                }
                throw th;
            }
        }
        try {
            cursorQuery.close();
        } catch (Exception unused3) {
            Log.e(TAG, "close cursor error!");
        }
    }
}
