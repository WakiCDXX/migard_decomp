package miui.theme;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class ThemeManagerHelper {
    private static final String TAG = "ThemeManagerHelper";

    private ThemeManagerHelper() {
    }

    public static String getStringFromProp(String str, String str2) {
        if (str.length() > 31) {
            return str2;
        }
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class).invoke(null, str, str2);
        } catch (Exception e2) {
            Log.e(TAG, "get prop failed:" + e2);
            return str2;
        }
    }

    private static boolean isHideTheme() {
        String stringFromProp = getStringFromProp("ro.miui.customized.region", "");
        return "mx_telcel".equals(stringFromProp) || "lm_cr".equals(stringFromProp) || "mx_at".equals(stringFromProp);
    }

    public static boolean needDisableTheme(Context context) {
        if (Build.IS_TABLET || isHideTheme()) {
            return true;
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            if (GlobalUtils.isEU(context)) {
                String miUiVersionCode = Build.getMiUiVersionCode();
                return TextUtils.isEmpty(miUiVersionCode) || Integer.valueOf(miUiVersionCode).intValue() < 8;
            }
            if (GlobalUtils.isReligiousArea(context)) {
                return true;
            }
        }
        return false;
    }
}
