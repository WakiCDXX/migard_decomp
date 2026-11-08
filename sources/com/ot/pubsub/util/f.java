package com.ot.pubsub.util;

import android.content.Context;
import android.os.UserManager;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;

/* JADX INFO: loaded from: classes.dex */
public class f {
    public static Context a(Context context) {
        if (c(context)) {
            k.c("FbeUtil", "getSafeContext return origin ctx");
            return context;
        }
        k.c("FbeUtil", "getSafeContext , create the safe ctx");
        return context.createDeviceProtectedStorageContext();
    }

    public static boolean b(Context context) {
        return !c(context);
    }

    private static boolean c(Context context) {
        UserManager userManager = (UserManager) context.getSystemService(NightStandbyRecord.KEY_USER);
        return userManager != null && userManager.isUserUnlocked();
    }
}
