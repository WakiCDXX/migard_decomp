package com.miui.powerkeeper.utils;

import android.os.Process;
import android.util.Slog;
import java.lang.reflect.Method;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class ProcessUtils {
    private static final String KILL_CAMERA_PROCESSES_BY_NAMES = "kill_camera_processes_by_names";
    private static final String KILL_PROCESSES_BY_NAMES = "kill_processes_by_names";
    private static final String TAG = "ProcessUtils";

    public static int killCameraProcess(String[] strArr) {
        if (strArr == null) {
            return -1;
        }
        String str = null;
        for (int i2 = 0; i2 < strArr.length; i2++) {
            String str2 = strArr[i2];
            if (str2 != null && !str2.isEmpty()) {
                str = str == null ? strArr[i2] : str + '#' + strArr[i2];
            }
        }
        if (str != null) {
            Slog.w(TAG, "killCameraProcess pids=" + str);
            return OctVmNativeProxy.sudebug_camera_command_execute(new String[]{"sudebug_camera", KILL_CAMERA_PROCESSES_BY_NAMES, str});
        }
        Slog.w(TAG, "killCameraProcess failed " + Arrays.toString(strArr));
        return -1;
    }

    public static int killProcess(int i2) {
        if (i2 <= 0) {
            Slog.w(TAG, "killProcess failed for pid=" + i2);
            return -1;
        }
        String string = Integer.toString(i2);
        Slog.w(TAG, "killProcess pids=" + string);
        return OctVmNativeProxy.sudebug_command_execute(new String[]{"sudebug", "kill_process_by_pid", string});
    }

    public static boolean setSchedAffinity(int i2, int[] iArr) {
        Method methodTryFindMethodBestMatch = miui.util.ReflectionUtils.tryFindMethodBestMatch(Process.class, "setSchedAffinity", new Object[]{Integer.valueOf(i2), iArr});
        if (methodTryFindMethodBestMatch == null) {
            return false;
        }
        try {
            methodTryFindMethodBestMatch.invoke(Process.class, Integer.valueOf(i2), iArr);
            return true;
        } catch (Exception e2) {
            e2.printStackTrace();
            return true;
        }
    }

    public static int killProcess(String[] strArr) {
        if (strArr == null) {
            return -1;
        }
        String str = null;
        for (int i2 = 0; i2 < strArr.length; i2++) {
            String str2 = strArr[i2];
            if (str2 != null && !str2.isEmpty()) {
                str = str == null ? strArr[i2] : str + '#' + strArr[i2];
            }
        }
        if (str != null) {
            Slog.w(TAG, "killProcess pids=" + str);
            return OctVmNativeProxy.sudebug_command_execute(new String[]{"sudebug", KILL_PROCESSES_BY_NAMES, str});
        }
        Slog.w(TAG, "killProcess failed " + Arrays.toString(strArr));
        return -1;
    }
}
