package com.xiaomi.push.crash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.service.PushConstants;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class SdkCrashStorage {
    private static final String FILE_NAME = "mipush_scr_file";
    private static final String KEY_CRASH_ERROR = "k_e";
    private static final String KEY_CRASH_LAST_UPLOAD_TIME = "k_t";
    private Context mAppCxt;
    private SharedPreferences mSp;

    public SdkCrashStorage(Context context) {
        this.mAppCxt = context.getApplicationContext();
        String str = FILE_NAME;
        try {
            String currentProcessName = AppInfoUtils.getCurrentProcessName();
            if (currentProcessName != null) {
                str = FILE_NAME + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + currentProcessName.hashCode();
            }
        } catch (Throwable unused) {
        }
        this.mSp = context.getSharedPreferences(str, 0);
    }

    private JSONArray getAllCrash(boolean z2) {
        String string = this.mSp.getString(KEY_CRASH_ERROR, "");
        JSONArray jSONArray = null;
        if (!TextUtils.isEmpty(string)) {
            try {
                JSONArray jSONArray2 = new JSONArray(string);
                if (z2) {
                    try {
                        String secretInfos = SdkCrashUtils.getSecretInfos(this.mAppCxt);
                        for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                            jSONArray2.getJSONObject(i2).put(SdkCrashConstants.PA_SECRET_PARAMS, secretInfos);
                        }
                    } catch (Throwable unused) {
                    }
                }
                jSONArray = jSONArray2;
            } catch (Throwable unused2) {
            }
        }
        return jSONArray == null ? new JSONArray() : jSONArray;
    }

    @SuppressLint({"ApplySharedPref"})
    public void deleteAll() {
        try {
            MyLog.i("scr delete");
            this.mSp.edit().remove(KEY_CRASH_ERROR).putLong(KEY_CRASH_LAST_UPLOAD_TIME, System.currentTimeMillis()).commit();
        } catch (Throwable th) {
            MyLog.w("scr delete error " + th);
        }
    }

    public JSONArray getAll() {
        return getAllCrash(true);
    }

    public long getLastUploadTime() {
        return this.mSp.getLong(KEY_CRASH_LAST_UPLOAD_TIME, 0L);
    }

    @SuppressLint({"ApplySharedPref"})
    public void insert(String str, int i2) {
        JSONObject jSONObject;
        try {
            JSONArray allCrash = getAllCrash(false);
            if (allCrash.length() >= 20) {
                MyLog.w("scr too max count, ignore this ex");
                return;
            }
            int i3 = 0;
            while (true) {
                if (i3 >= allCrash.length()) {
                    jSONObject = null;
                    break;
                }
                jSONObject = allCrash.getJSONObject(i3);
                if (str.equalsIgnoreCase(jSONObject.optString(SdkCrashConstants.PA_STACK, ""))) {
                    jSONObject.put(SdkCrashConstants.PA_COUNT, jSONObject.optInt(SdkCrashConstants.PA_COUNT, 1) + 1);
                    jSONObject.put(SdkCrashConstants.PA_MODIFIED_TIME, System.currentTimeMillis());
                    MyLog.i("scr update");
                    break;
                }
                i3++;
            }
            if (jSONObject == null) {
                MyLog.i("scr insert");
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put(SdkCrashConstants.PA_SDK_VERSION, PushConstants.PUSH_VERSION_CODE);
                jSONObject2.put(SdkCrashConstants.PA_APP_VERSION_NAME, SdkCrashUtils.getAppVersionName(this.mAppCxt));
                jSONObject2.put(SdkCrashConstants.PA_ANDROID_VERSION, SdkCrashUtils.getAndroidVersionCode());
                jSONObject2.put(SdkCrashConstants.PA_ROM_NAME, SdkCrashUtils.getRomVersionName());
                jSONObject2.put(SdkCrashConstants.PA_TYPE, 0);
                jSONObject2.put(SdkCrashConstants.PA_PRIORITY, i2);
                jSONObject2.put(SdkCrashConstants.PA_STACK, str);
                jSONObject2.put(SdkCrashConstants.PA_COUNT, 1);
                jSONObject2.put(SdkCrashConstants.PA_EXCEPTION_TYPE, SdkCrashUtils.getExceptionType(str));
                jSONObject2.put(SdkCrashConstants.PA_CREATE_TIME, System.currentTimeMillis());
                jSONObject2.put(SdkCrashConstants.PA_MODIFIED_TIME, System.currentTimeMillis());
                allCrash.put(jSONObject2);
            }
            this.mSp.edit().putString(KEY_CRASH_ERROR, allCrash.toString()).commit();
        } catch (Throwable th) {
            MyLog.w("scr insert error " + th);
        }
    }
}
