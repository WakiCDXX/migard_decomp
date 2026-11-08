package com.xiaomi.push.extension;

import android.app.ActivityManager;
import android.content.Context;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AppRunningUtils {
    public static Map<String, String> divideAppStatus(Context context, String str) {
        List<ActivityManager.RunningAppProcessInfo> processInfos = getProcessInfos(context);
        if (ToolUtils.isEmptyList(processInfos)) {
            return null;
        }
        String[] strArrSplit = str.split(",");
        HashMap map = new HashMap();
        for (String str2 : strArrSplit) {
            String[] strArrSplit2 = str2.split(Constants.WAVE_SEPARATOR);
            if (strArrSplit2.length >= 2) {
                map.put(strArrSplit2[1], strArrSplit2[0]);
            }
        }
        ToolUtils.StringMap stringMap = new ToolUtils.StringMap(Constants.WAVE_SEPARATOR, ",");
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processInfos) {
            if (map.containsKey(runningAppProcessInfo.processName)) {
                stringMap.put((String) map.get(runningAppProcessInfo.processName), String.valueOf(runningAppProcessInfo.importance));
                map.remove(runningAppProcessInfo.processName);
            }
        }
        HashMap map2 = new HashMap();
        if (stringMap.toString().length() > 0) {
            map2.put("alive", stringMap.toString());
            if (map.size() > 0) {
                ToolUtils.StringMap stringMap2 = new ToolUtils.StringMap("", ",");
                Iterator it = map.keySet().iterator();
                while (it.hasNext()) {
                    stringMap2.put((String) map.get((String) it.next()), "");
                }
                map2.put("notAlive", stringMap2.toString());
            }
        } else {
            map2.put("alive", "");
        }
        return map2;
    }

    public static List<ActivityManager.RunningAppProcessInfo> getForegroundProcess(Context context) {
        List<ActivityManager.RunningAppProcessInfo> processInfos;
        if (context == null || (processInfos = getProcessInfos(context)) == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processInfos) {
            if (runningAppProcessInfo.importance == 100) {
                Integer num = (Integer) JavaCalls.getField(runningAppProcessInfo, "processState");
                Integer num2 = (Integer) JavaCalls.getField(runningAppProcessInfo, "flags");
                if (num != null && num2 != null && num.intValue() == 2 && (num2.intValue() & 4) != 0) {
                    arrayList.add(runningAppProcessInfo);
                }
            }
        }
        return arrayList;
    }

    private static List<ActivityManager.RunningAppProcessInfo> getProcessInfos(Context context) {
        try {
            return ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        } catch (Throwable th) {
            MyLog.w("get infos error: " + th);
            return null;
        }
    }

    public static String getProcessNameByPid(Context context, int i2) {
        List<ActivityManager.RunningAppProcessInfo> processInfos;
        if (context == null || (processInfos = getProcessInfos(context)) == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processInfos) {
            if (runningAppProcessInfo.pid == i2) {
                return runningAppProcessInfo.processName;
            }
        }
        return null;
    }

    public static boolean isAppRunning(Context context, String str) {
        List<ActivityManager.RunningAppProcessInfo> processInfos = getProcessInfos(context);
        if (processInfos == null) {
            return false;
        }
        Iterator<ActivityManager.RunningAppProcessInfo> it = processInfos.iterator();
        while (it.hasNext()) {
            if (Arrays.asList(it.next().pkgList).contains(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isApplicationForeground(Context context, String str) {
        List<ActivityManager.RunningAppProcessInfo> processInfos = getProcessInfos(context);
        if (processInfos == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processInfos) {
            if (runningAppProcessInfo.importance == 100 && Arrays.asList(runningAppProcessInfo.pkgList).contains(str)) {
                return true;
            }
        }
        return false;
    }
}
