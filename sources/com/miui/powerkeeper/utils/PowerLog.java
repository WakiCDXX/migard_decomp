package com.miui.powerkeeper.utils;

import android.annotation.NonNull;
import android.os.Build;
import android.os.SystemProperties;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public final class PowerLog {
    public static final boolean D;
    private static final int MAX_CAPACITY = 512;
    private static final int POOL_SIZE = 10;
    private static final String TAG = "PK";
    public static boolean V;
    private static LocalLog sLocal;
    private static int sObtainAll;
    private static int sObtainReuse;
    private static final StringBuilder[] sPool;
    private static int sRecycleAll;
    private static int sRecycleSuccess;

    static {
        boolean z2 = Build.IS_DEBUGGABLE;
        sLocal = new LocalLog(z2 ? 256 : 128);
        D = z2;
        V = SystemProperties.getInt("persist.sys.power.log", 1) == 0;
        sPool = new StringBuilder[10];
        sObtainAll = 0;
        sObtainReuse = 0;
        sRecycleAll = 0;
        sRecycleSuccess = 0;
    }

    private PowerLog() {
    }

    public static void d(String str, String str2) {
        if (D) {
            Log.d(str, str2);
        }
    }

    public static void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        sLocal.dump(fileDescriptor, printWriter, strArr);
        StringBuilder sbObtainStringBuilder = obtainStringBuilder();
        sbObtainStringBuilder.append("sObtainAll=");
        sbObtainStringBuilder.append(sObtainAll);
        sbObtainStringBuilder.append(" sObtainReuse=");
        sbObtainStringBuilder.append(sObtainReuse);
        sbObtainStringBuilder.append(" sRecycleAll=");
        sbObtainStringBuilder.append(sRecycleAll);
        sbObtainStringBuilder.append(" sRecycleSuccess=");
        sbObtainStringBuilder.append(sRecycleSuccess);
        printWriter.println(toStringAndRecycle(sbObtainStringBuilder));
    }

    public static void i(@NonNull String str, String str2) {
        Log.i(str, str2);
    }

    public static void iL(@NonNull LocalLog localLog, @NonNull String str, String str2) {
        Log.i(str, str2);
        localLog.log(str2);
    }

    public static StringBuilder obtainStringBuilder() {
        sObtainAll++;
        for (int i2 = 0; i2 < 10; i2++) {
            StringBuilder[] sbArr = sPool;
            StringBuilder sb = sbArr[i2];
            if (sb != null) {
                sbArr[i2] = null;
                sb.setLength(0);
                sObtainReuse++;
                return sb;
            }
        }
        return new StringBuilder(512);
    }

    public static void printStackTrace(String str) {
        Log.d("StackTrace", str + ShellUtils.COMMAND_LINE_END + Log.getStackTraceString(new Throwable()));
    }

    public static void recordKey(String str) {
        Slog.w(TAG, str);
        sLocal.log(str);
    }

    public static void resetDebugSwitch() {
        V = SystemProperties.getInt("persist.sys.power.log", 1) == 0;
    }

    public static void sD(String str, String str2) {
        if (D) {
            Slog.d(str, str2);
        }
    }

    public static void sI(@NonNull String str, String str2) {
        Log.i(str, str2);
    }

    public static void sIL(@NonNull LocalLog localLog, @NonNull String str, String str2) {
        Slog.i(str, str2);
        localLog.log(str2);
    }

    public static void sW(@NonNull String str, String str2) {
        Slog.w(str, str2);
    }

    public static void sWL(@NonNull LocalLog localLog, @NonNull String str, String str2) {
        Slog.w(str, str2);
        localLog.log(str2);
    }

    public static String toStr(Object obj, Object obj2, Object obj3) {
        sObtainAll++;
        StringBuilder sb = null;
        for (int i2 = 0; i2 < 10; i2++) {
            StringBuilder[] sbArr = sPool;
            StringBuilder sb2 = sbArr[i2];
            if (sb2 != null) {
                sbArr[i2] = null;
                sb2.setLength(0);
                sObtainReuse++;
                sb = sb2;
            }
        }
        if (sb == null) {
            sb = new StringBuilder(512);
        }
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        String string = sb.toString();
        sRecycleAll++;
        for (int i3 = 0; i3 < 10; i3++) {
            StringBuilder[] sbArr2 = sPool;
            if (sbArr2[i3] == null) {
                sbArr2[i3] = sb;
                sRecycleSuccess++;
                break;
            }
        }
        return string;
    }

    public static String toStringAndRecycle(StringBuilder sb) {
        String string = sb.toString();
        sRecycleAll++;
        for (int i2 = 0; i2 < 10; i2++) {
            StringBuilder[] sbArr = sPool;
            if (sbArr[i2] == null) {
                sbArr[i2] = sb;
                sRecycleSuccess++;
                break;
            }
        }
        return string;
    }

    public static void v(String str, String str2) {
        if (V) {
            Log.d(str, str2);
        }
    }

    public static void w(@NonNull String str, String str2) {
        Log.w(str, str2);
    }

    public static void wL(@NonNull LocalLog localLog, @NonNull String str, String str2) {
        Log.w(str, str2);
        localLog.log(str2);
    }

    public static void d(String str, String str2, Object obj) {
        if (D) {
            Log.d(str, str2 + obj);
        }
    }

    public static void i(@NonNull String str, String str2, Object obj) {
        Log.i(str, str2 + obj);
    }

    public static void sD(String str, String str2, Object obj) {
        if (D) {
            Slog.d(str, str2 + obj);
        }
    }

    public static void sI(@NonNull String str, String str2, Object obj) {
        Log.i(str, str2 + obj);
    }

    public static void sW(@NonNull String str, String str2, Object obj) {
        Slog.w(str, str2 + obj);
    }

    public static void v(String str, String str2, Object obj) {
        if (V) {
            Log.d(str, str2 + obj);
        }
    }

    public static void w(@NonNull String str, String str2, Object obj) {
        Log.w(str, str2 + obj);
    }

    public static void d(String str, String str2, Object obj, Object obj2) {
        if (D) {
            Log.d(str, toStr(str2, obj, obj2));
        }
    }

    public static void i(@NonNull String str, String str2, Object obj, Object obj2) {
        Log.i(str, toStr(str2, obj, obj2));
    }

    public static void sD(String str, String str2, Object obj, Object obj2) {
        if (D) {
            Slog.d(str, toStr(str2, obj, obj2));
        }
    }

    public static void sI(@NonNull String str, String str2, Object obj, Object obj2) {
        Log.i(str, toStr(str2, obj, obj2));
    }

    public static void sW(@NonNull String str, String str2, Object obj, Object obj2) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(str2);
        sb.append(obj);
        sb.append(obj2);
        Slog.w(str, sb.toString());
    }

    public static void v(String str, String str2, Object obj, Object obj2) {
        if (V) {
            Log.d(str, toStr(str2, obj, obj2));
        }
    }

    public static void w(@NonNull String str, String str2, Object obj, Object obj2) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(str2);
        sb.append(obj);
        sb.append(obj2);
        Log.w(str, sb.toString());
    }

    public static void d(String str, String str2, Object obj, Object obj2, Object obj3) {
        if (D) {
            Log.d(str, toStr(str2, obj, obj2, obj3));
        }
    }

    public static void i(@NonNull String str, String str2, Object obj, Object obj2, Object obj3) {
        Log.i(str, toStr(str2, obj, obj2, obj3));
    }

    public static void sD(String str, String str2, Object obj, Object obj2, Object obj3) {
        if (D) {
            Slog.d(str, toStr(str2, obj, obj2, obj3));
        }
    }

    public static void sI(@NonNull String str, String str2, Object obj, Object obj2, Object obj3) {
        Log.i(str, toStr(str2, obj, obj2, obj3));
    }

    public static void sW(@NonNull String str, String str2, Object obj, Object obj2, Object obj3) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(str2);
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        Slog.w(str, sb.toString());
    }

    public static void v(String str, String str2, Object obj, Object obj2, Object obj3) {
        if (V) {
            Log.d(str, toStr(str2, obj, obj2, obj3));
        }
    }

    public static void w(@NonNull String str, String str2, Object obj, Object obj2, Object obj3) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(str2);
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        Log.w(str, sb.toString());
    }

    public static void d(String str, String str2, Object obj, Object obj2, Object obj3, Object obj4) {
        if (D) {
            Log.d(str, toStr(str2, obj, obj2, obj3, obj4));
        }
    }

    public static void i(@NonNull String str, String str2, Object obj, Object obj2, Object obj3, Object obj4) {
        Log.i(str, toStr(str2, obj, obj2, obj3, obj4));
    }

    public static void sD(String str, String str2, Object obj, Object obj2, Object obj3, Object obj4) {
        if (D) {
            Slog.d(str, toStr(str2, obj, obj2, obj3, obj4));
        }
    }

    public static void sI(@NonNull String str, String str2, Object obj, Object obj2, Object obj3, Object obj4) {
        Log.i(str, toStr(str2, obj, obj2, obj3, obj4));
    }

    public static void sW(@NonNull String str, String str2, Object obj, Object obj2, Object obj3, Object obj4) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(str2);
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        Slog.w(str, sb.toString());
    }

    public static void v(String str, String str2, Object obj, Object obj2, Object obj3, Object obj4) {
        if (V) {
            Log.d(str, toStr(str2, obj, obj2, obj3, obj4));
        }
    }

    public static void w(@NonNull String str, String str2, Object obj, Object obj2, Object obj3, Object obj4) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(str2);
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        Log.w(str, sb.toString());
    }

    public static void d(String str, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (D) {
            Log.d(str, toStr(str2, obj, obj2, obj3, obj4, obj5));
        }
    }

    public static void i(@NonNull String str, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        Log.i(str, toStr(str2, obj, obj2, obj3, obj4, obj5));
    }

    public static void sD(String str, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (D) {
            Slog.d(str, toStr(str2, obj, obj2, obj3, obj4, obj5));
        }
    }

    public static void sI(@NonNull String str, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        Log.i(str, toStr(str2, obj, obj2, obj3, obj4, obj5));
    }

    public static void sW(@NonNull String str, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(str2);
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        sb.append(obj5);
        Slog.w(str, sb.toString());
    }

    public static void v(String str, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        if (V) {
            Log.d(str, toStr(str2, obj, obj2, obj3, obj4, obj5));
        }
    }

    public static void w(@NonNull String str, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(str2);
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        sb.append(obj5);
        Log.w(str, sb.toString());
    }

    public static String toStr(Object obj, Object obj2, Object obj3, Object obj4) {
        sObtainAll++;
        StringBuilder sb = null;
        for (int i2 = 0; i2 < 10; i2++) {
            StringBuilder[] sbArr = sPool;
            StringBuilder sb2 = sbArr[i2];
            if (sb2 != null) {
                sbArr[i2] = null;
                sb2.setLength(0);
                sObtainReuse++;
                sb = sb2;
            }
        }
        if (sb == null) {
            sb = new StringBuilder(512);
        }
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        String string = sb.toString();
        sRecycleAll++;
        for (int i3 = 0; i3 < 10; i3++) {
            StringBuilder[] sbArr2 = sPool;
            if (sbArr2[i3] == null) {
                sbArr2[i3] = sb;
                sRecycleSuccess++;
                break;
            }
        }
        return string;
    }

    public static String toStr(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        sObtainAll++;
        StringBuilder sb = null;
        for (int i2 = 0; i2 < 10; i2++) {
            StringBuilder[] sbArr = sPool;
            StringBuilder sb2 = sbArr[i2];
            if (sb2 != null) {
                sbArr[i2] = null;
                sb2.setLength(0);
                sObtainReuse++;
                sb = sb2;
            }
        }
        if (sb == null) {
            sb = new StringBuilder(512);
        }
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        sb.append(obj5);
        String string = sb.toString();
        sRecycleAll++;
        for (int i3 = 0; i3 < 10; i3++) {
            StringBuilder[] sbArr2 = sPool;
            if (sbArr2[i3] == null) {
                sbArr2[i3] = sb;
                sRecycleSuccess++;
                break;
            }
        }
        return string;
    }

    public static String toStr(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        sObtainAll++;
        StringBuilder sb = null;
        for (int i2 = 0; i2 < 10; i2++) {
            StringBuilder[] sbArr = sPool;
            StringBuilder sb2 = sbArr[i2];
            if (sb2 != null) {
                sbArr[i2] = null;
                sb2.setLength(0);
                sObtainReuse++;
                sb = sb2;
            }
        }
        if (sb == null) {
            sb = new StringBuilder(512);
        }
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        sb.append(obj5);
        sb.append(obj6);
        String string = sb.toString();
        sRecycleAll++;
        for (int i3 = 0; i3 < 10; i3++) {
            StringBuilder[] sbArr2 = sPool;
            if (sbArr2[i3] == null) {
                sbArr2[i3] = sb;
                sRecycleSuccess++;
                break;
            }
        }
        return string;
    }

    public static String toStr(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
        sObtainAll++;
        StringBuilder sb = null;
        for (int i2 = 0; i2 < 10; i2++) {
            StringBuilder[] sbArr = sPool;
            StringBuilder sb2 = sbArr[i2];
            if (sb2 != null) {
                sbArr[i2] = null;
                sb2.setLength(0);
                sObtainReuse++;
                sb = sb2;
            }
        }
        if (sb == null) {
            sb = new StringBuilder(512);
        }
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        sb.append(obj5);
        sb.append(obj6);
        sb.append(obj7);
        String string = sb.toString();
        sRecycleAll++;
        for (int i3 = 0; i3 < 10; i3++) {
            StringBuilder[] sbArr2 = sPool;
            if (sbArr2[i3] == null) {
                sbArr2[i3] = sb;
                sRecycleSuccess++;
                break;
            }
        }
        return string;
    }

    public static String toStr(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
        sObtainAll++;
        StringBuilder sb = null;
        for (int i2 = 0; i2 < 10; i2++) {
            StringBuilder[] sbArr = sPool;
            StringBuilder sb2 = sbArr[i2];
            if (sb2 != null) {
                sbArr[i2] = null;
                sb2.setLength(0);
                sObtainReuse++;
                sb = sb2;
            }
        }
        if (sb == null) {
            sb = new StringBuilder(512);
        }
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        sb.append(obj5);
        sb.append(obj6);
        sb.append(obj7);
        sb.append(obj8);
        String string = sb.toString();
        sRecycleAll++;
        for (int i3 = 0; i3 < 10; i3++) {
            StringBuilder[] sbArr2 = sPool;
            if (sbArr2[i3] == null) {
                sbArr2[i3] = sb;
                sRecycleSuccess++;
                break;
            }
        }
        return string;
    }

    public static String toStr(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object... objArr) {
        sObtainAll++;
        StringBuilder sb = null;
        for (int i2 = 0; i2 < 10; i2++) {
            StringBuilder[] sbArr = sPool;
            StringBuilder sb2 = sbArr[i2];
            if (sb2 != null) {
                sbArr[i2] = null;
                sb2.setLength(0);
                sObtainReuse++;
                sb = sb2;
            }
        }
        if (sb == null) {
            sb = new StringBuilder(512);
        }
        sb.append(obj);
        sb.append(obj2);
        sb.append(obj3);
        sb.append(obj4);
        sb.append(obj5);
        sb.append(obj6);
        sb.append(obj7);
        sb.append(obj8);
        if (objArr != null) {
            for (Object obj9 : objArr) {
                sb.append(obj9);
            }
        }
        String string = sb.toString();
        sRecycleAll++;
        for (int i3 = 0; i3 < 10; i3++) {
            StringBuilder[] sbArr2 = sPool;
            if (sbArr2[i3] == null) {
                sbArr2[i3] = sb;
                sRecycleSuccess++;
                break;
            }
        }
        return string;
    }
}
