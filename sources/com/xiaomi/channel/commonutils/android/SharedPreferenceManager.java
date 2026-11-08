package com.xiaomi.channel.commonutils.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class SharedPreferenceManager {
    private static volatile SharedPreferenceManager sInstance;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Map<String, Map<String, String>> mCaches = new HashMap();

    private SharedPreferenceManager(Context context) {
        this.mContext = context;
    }

    private synchronized String getDataFromCache(String str, String str2) {
        if (this.mCaches == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return "";
        }
        try {
            Map<String, String> map = this.mCaches.get(str);
            if (map == null) {
                return "";
            }
            return map.get(str2);
        } catch (Throwable unused) {
            return "";
        }
    }

    public static SharedPreferenceManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SharedPreferenceManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new SharedPreferenceManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private synchronized void putData2Cache(String str, String str2, String str3) {
        try {
            if (this.mCaches == null) {
                this.mCaches = new HashMap();
            }
            Map<String, String> map = this.mCaches.get(str);
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(str2, str3);
            this.mCaches.put(str, map);
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized boolean getBooleanValue(String str, String str2, boolean z2) {
        try {
            String dataFromCache = getDataFromCache(str, str2);
            if (TextUtils.isEmpty(dataFromCache)) {
                return this.mContext.getSharedPreferences(str, 4).getBoolean(str2, z2);
            }
            return Boolean.parseBoolean(dataFromCache);
        } catch (Throwable unused) {
            return z2;
        }
    }

    public synchronized String getStringValue(String str, String str2, String str3) {
        String dataFromCache = getDataFromCache(str, str2);
        if (!TextUtils.isEmpty(dataFromCache)) {
            return dataFromCache;
        }
        return this.mContext.getSharedPreferences(str, 4).getString(str2, str3);
    }

    public synchronized void setBooleanValue(final String str, final String str2, final Boolean bool) {
        putData2Cache(str, str2, String.valueOf(bool));
        this.mHandler.post(new Runnable() { // from class: com.xiaomi.channel.commonutils.android.SharedPreferenceManager.1
            @Override // java.lang.Runnable
            public void run() {
                SharedPreferences.Editor editorEdit = SharedPreferenceManager.this.mContext.getSharedPreferences(str, 4).edit();
                editorEdit.putBoolean(str2, bool.booleanValue());
                editorEdit.commit();
            }
        });
    }

    public synchronized void setStringnValue(final String str, final String str2, final String str3) {
        putData2Cache(str, str2, str3);
        this.mHandler.post(new Runnable() { // from class: com.xiaomi.channel.commonutils.android.SharedPreferenceManager.2
            @Override // java.lang.Runnable
            public void run() {
                SharedPreferences.Editor editorEdit = SharedPreferenceManager.this.mContext.getSharedPreferences(str, 4).edit();
                editorEdit.putString(str2, str3);
                editorEdit.commit();
            }
        });
    }
}
