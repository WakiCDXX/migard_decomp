package com.xiaomi.push.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MIPushAppInfo {
    private static final String PREF_KEY_DISABLE_PUSH_PKGS = "disable_push_pkg_names";
    private static final String PREF_KEY_DISABLE_PUSH_PKGS_CACHE = "disable_push_pkg_names_cache";
    private static final String PREF_KEY_UNREGISTERED_PKGS = "unregistered_pkg_names";
    private static final String PREF_NAME = "mipush_app_info";
    private static MIPushAppInfo sInstance;
    private Context appContext;
    private List<String> unRegisteredPkg = new ArrayList();
    private final List<String> disabledPushPkg = new ArrayList();
    private final List<String> disabledPushPkgCache = new ArrayList();

    private MIPushAppInfo(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.appContext = applicationContext;
        if (applicationContext == null) {
            this.appContext = context;
        }
        SharedPreferences sharedPreferences = this.appContext.getSharedPreferences(PREF_NAME, 0);
        for (String str : sharedPreferences.getString(PREF_KEY_UNREGISTERED_PKGS, "").split(",")) {
            if (TextUtils.isEmpty(str)) {
                this.unRegisteredPkg.add(str);
            }
        }
        for (String str2 : sharedPreferences.getString(PREF_KEY_DISABLE_PUSH_PKGS, "").split(",")) {
            if (!TextUtils.isEmpty(str2)) {
                this.disabledPushPkg.add(str2);
            }
        }
        for (String str3 : sharedPreferences.getString(PREF_KEY_DISABLE_PUSH_PKGS_CACHE, "").split(",")) {
            if (!TextUtils.isEmpty(str3)) {
                this.disabledPushPkgCache.add(str3);
            }
        }
    }

    public static MIPushAppInfo getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MIPushAppInfo(context);
        }
        return sInstance;
    }

    public void addDisablePushPkg(String str) {
        synchronized (this.disabledPushPkg) {
            try {
                if (!this.disabledPushPkg.contains(str)) {
                    this.disabledPushPkg.add(str);
                    this.appContext.getSharedPreferences(PREF_NAME, 0).edit().putString(PREF_KEY_DISABLE_PUSH_PKGS, XMStringUtils.join(this.disabledPushPkg, ",")).commit();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void addDisablePushPkgCache(String str) {
        synchronized (this.disabledPushPkgCache) {
            try {
                if (!this.disabledPushPkgCache.contains(str)) {
                    this.disabledPushPkgCache.add(str);
                    this.appContext.getSharedPreferences(PREF_NAME, 0).edit().putString(PREF_KEY_DISABLE_PUSH_PKGS_CACHE, XMStringUtils.join(this.disabledPushPkgCache, ",")).commit();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void addUnRegisteredPkg(String str) {
        synchronized (this.unRegisteredPkg) {
            try {
                if (!this.unRegisteredPkg.contains(str)) {
                    this.unRegisteredPkg.add(str);
                    this.appContext.getSharedPreferences(PREF_NAME, 0).edit().putString(PREF_KEY_UNREGISTERED_PKGS, XMStringUtils.join(this.unRegisteredPkg, ",")).commit();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean isPushDisabled(String str) {
        boolean zContains;
        synchronized (this.disabledPushPkg) {
            zContains = this.disabledPushPkg.contains(str);
        }
        return zContains;
    }

    public boolean isPushDisabled4User(String str) {
        boolean zContains;
        synchronized (this.disabledPushPkgCache) {
            zContains = this.disabledPushPkgCache.contains(str);
        }
        return zContains;
    }

    public boolean isUnRegistered(String str) {
        boolean zContains;
        synchronized (this.unRegisteredPkg) {
            zContains = this.unRegisteredPkg.contains(str);
        }
        return zContains;
    }

    public void removeDisablePushPkg(String str) {
        synchronized (this.disabledPushPkg) {
            try {
                if (this.disabledPushPkg.contains(str)) {
                    this.disabledPushPkg.remove(str);
                    this.appContext.getSharedPreferences(PREF_NAME, 0).edit().putString(PREF_KEY_DISABLE_PUSH_PKGS, XMStringUtils.join(this.disabledPushPkg, ",")).commit();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void removeDisablePushPkgCache(String str) {
        synchronized (this.disabledPushPkgCache) {
            try {
                if (this.disabledPushPkgCache.contains(str)) {
                    this.disabledPushPkgCache.remove(str);
                    this.appContext.getSharedPreferences(PREF_NAME, 0).edit().putString(PREF_KEY_DISABLE_PUSH_PKGS_CACHE, XMStringUtils.join(this.disabledPushPkgCache, ",")).commit();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void removeUnRegisteredPkg(String str) {
        synchronized (this.unRegisteredPkg) {
            try {
                if (this.unRegisteredPkg.contains(str)) {
                    this.unRegisteredPkg.remove(str);
                    this.appContext.getSharedPreferences(PREF_NAME, 0).edit().putString(PREF_KEY_UNREGISTERED_PKGS, XMStringUtils.join(this.unRegisteredPkg, ",")).commit();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
