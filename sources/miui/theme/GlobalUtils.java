package miui.theme;

import android.content.Context;
import miui.os.Build;
import q.a;

/* JADX INFO: loaded from: classes.dex */
public class GlobalUtils {
    private GlobalUtils() {
    }

    public static boolean isEU(Context context) {
        String[] stringArray;
        if (context != null && (stringArray = context.getResources().getStringArray(a.f2179k)) != null) {
            String region = Build.getRegion();
            for (String str : stringArray) {
                if (str.equals(region)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isReligiousArea(Context context) {
        if (context != null) {
            String[] stringArray = context.getResources().getStringArray(a.f2186r);
            if (stringArray != null) {
                String region = Build.getRegion();
                for (String str : stringArray) {
                    if (str.equals(region)) {
                        return true;
                    }
                }
            }
            String[] stringArray2 = context.getResources().getStringArray(a.f2185q);
            if (stringArray2 != null) {
                String language = context.getResources().getConfiguration().locale.getLanguage();
                for (String str2 : stringArray2) {
                    if (str2.equals(language)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
