package miui.yellowpage;

import java.util.Arrays;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class Log {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = "YellowPage";

    private Log() {
    }

    public static void d(String str, String str2) {
        if (DEBUG) {
            android.util.Log.d(TAG, str + ":" + str2);
        }
    }

    public static void e(String str, String str2) {
        android.util.Log.e(TAG, str + ":" + str2);
    }

    public static void i(String str, String str2) {
        if (DEBUG) {
            android.util.Log.i(TAG, str + ":" + str2);
        }
    }

    public static String logify(String str) {
        if (str == null) {
            return null;
        }
        char[] cArr = new char[str.length()];
        Arrays.fill(cArr, '*');
        return new String(cArr);
    }

    public static void v(String str, String str2) {
        android.util.Log.v(TAG, str + ":" + str2);
    }

    public static void wtf(String str, String str2) {
        android.util.Log.wtf(TAG, str + ":" + str2);
    }

    public static void e(String str, String str2, Throwable th) {
        android.util.Log.e(TAG, str + ":" + str2, th);
    }

    public static void v(String str, String str2, Throwable th) {
        android.util.Log.v(TAG, str + ":" + str2, th);
    }

    public static void wtf(String str, String str2, Throwable th) {
        android.util.Log.wtf(TAG, str + ":" + str2, th);
    }

    public static void d(String str, String str2, Throwable th) {
        if (DEBUG) {
            android.util.Log.d(TAG, str + ":" + str2, th);
        }
    }

    public static void i(String str, String str2, Throwable th) {
        if (DEBUG) {
            android.util.Log.i(TAG, str + ":" + str2, th);
        }
    }
}
