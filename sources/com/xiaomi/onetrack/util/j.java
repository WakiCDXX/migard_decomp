package com.xiaomi.onetrack.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;

/* JADX INFO: loaded from: classes.dex */
public class j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1564a = "FbeUtil";

    private j() {
    }

    public static Context a(Context context) {
        if (e(context)) {
            p.a(f1564a, "getSafeContext return origin ctx");
            return context;
        }
        p.a(f1564a, "getSafeContext , create the safe ctx");
        return context.createDeviceProtectedStorageContext();
    }

    public static boolean b(Context context) {
        try {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
            return a() && keyguardManager != null && keyguardManager.isKeyguardSecure();
        } catch (Exception e2) {
            p.a(f1564a, "FBEDeviceAndSetedUpScreenLock Exception: " + e2.getMessage());
            return false;
        }
    }

    public static boolean c(Context context) {
        return b(context) && !e(context);
    }

    public static boolean d(Context context) {
        return !e(context);
    }

    private static boolean e(Context context) {
        try {
            UserManager userManager = (UserManager) context.getSystemService(NightStandbyRecord.KEY_USER);
            return userManager != null && userManager.isUserUnlocked();
        } catch (Exception e2) {
            p.a(f1564a, "isUserUnlocked Exception: " + e2.getMessage());
            return false;
        }
    }

    private static boolean f(Context context) {
        return false;
    }

    public static boolean a() {
        try {
            Class[] clsArr = new Class[0];
            return ((Boolean) StorageManager.class.getDeclaredMethod("isFileEncryptedNativeOrEmulated", null).invoke(null, null).getClass().getDeclaredMethod("isFileEncryptedNativeOrEmulated", Boolean.TYPE).invoke(null, null)).booleanValue();
        } catch (Exception e2) {
            p.b(f1564a, "*** " + e2);
            return false;
        }
    }

    public static void a(PreferenceManager preferenceManager) {
        preferenceManager.setStorageDeviceProtected();
    }
}
