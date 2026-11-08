package com.xiaomi.clientreport.util;

import android.content.Context;
import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes.dex */
public class SPManager {
    private static volatile SPManager sInstance;
    private Context mContext;

    private SPManager(Context context) {
        this.mContext = context;
    }

    public static SPManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SPManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new SPManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public synchronized long getLongValue(String str, String str2, long j2) {
        try {
        } catch (Throwable unused) {
            return j2;
        }
        return this.mContext.getSharedPreferences(str, 4).getLong(str2, j2);
    }

    public synchronized String getStringValue(String str, String str2, String str3) {
        try {
        } catch (Throwable unused) {
            return str3;
        }
        return this.mContext.getSharedPreferences(str, 4).getString(str2, str3);
    }

    public synchronized void setLongValue(String str, String str2, long j2) {
        SharedPreferences.Editor editorEdit = this.mContext.getSharedPreferences(str, 4).edit();
        editorEdit.putLong(str2, j2);
        editorEdit.commit();
    }

    public synchronized void setStringnValue(String str, String str2, String str3) {
        SharedPreferences.Editor editorEdit = this.mContext.getSharedPreferences(str, 4).edit();
        editorEdit.putString(str2, str3);
        editorEdit.commit();
    }
}
