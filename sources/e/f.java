package e;

import android.os.SystemProperties;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.statemachine.PowerModeHandler;
import miui.os.Build;
import miui.util.FeatureParser;

/* JADX INFO: compiled from: ModulesUtils.java */
/* JADX INFO: loaded from: classes.dex */
public class f {
    public static boolean a() {
        return SystemProperties.getBoolean("ro.mi.development", false) || Build.IS_DEBUGGABLE;
    }

    public static boolean b() {
        return true;
    }

    public static boolean c() {
        return ArrayUtils.contains(new String[]{"venus", "star", "mars", "haydn", "renoir"}, android.os.Build.DEVICE);
    }

    public static boolean d() {
        return SystemProperties.getBoolean("ro.vendor.cabc.enable", false) || FeatureParser.getBoolean("support_cabc", false);
    }

    public static boolean e() {
        return SystemProperties.getBoolean("ro.vendor.dfps.enable", false);
    }

    public static boolean f() {
        return SystemProperties.getBoolean("ro.vendor.standard.video.enable", false);
    }

    public static boolean g() {
        return ArrayUtils.contains(new String[]{"thor", "ishtar", "babylon", "aurora", "shennong", "houji", "goku", "ruyi"}, android.os.Build.DEVICE);
    }

    public static boolean h() {
        return ArrayUtils.contains(new String[]{"ares", "aresin"}, android.os.Build.DEVICE);
    }

    public static boolean i() {
        return PowerModeHandler.isSupportDevice();
    }

    public static boolean j() {
        return FeatureParser.getBoolean("support_power_save_new", !Build.IS_INTERNATIONAL_BUILD);
    }

    public static boolean k() {
        int[] intArray = FeatureParser.getIntArray("screen_resolution_supported");
        return (intArray != null && intArray.length > 0) || FeatureParser.getBoolean("screen_compat_supported", false);
    }

    public static boolean l() {
        return ArrayUtils.contains(new String[]{"zeus", "cupid", "thor", "zizhan", "liuqin", "yudi", "sheng", "dizi", "ruan"}, android.os.Build.DEVICE);
    }

    public static boolean m() {
        return ArrayUtils.contains(new String[]{"haotian", "dada", "miro", "zorn"}, android.os.Build.DEVICE);
    }

    public static boolean n() {
        return ArrayUtils.contains(new String[]{"haotian", "dada"}, android.os.Build.DEVICE);
    }
}
