package miui.util;

import android.content.Context;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class FBEUtils {
    private FBEUtils() {
    }

    private static Context createDeviceProtectedStorageContext(Context context) {
        try {
            Class[] clsArr = new Class[0];
            Method method = context.getClass().getMethod("createDeviceProtectedStorageContext", null);
            method.setAccessible(true);
            return (Context) method.invoke(context, null);
        } catch (Exception unused) {
            return context;
        }
    }

    @Deprecated
    public static Context getSafeStorageContext(Context context) {
        return createDeviceProtectedStorageContext(context);
    }
}
