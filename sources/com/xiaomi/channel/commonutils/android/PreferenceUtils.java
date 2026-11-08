package com.xiaomi.channel.commonutils.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public abstract class PreferenceUtils {
    public static void clearPreference(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.clear();
        editorEdit.commit();
    }

    public static void dumpDefaultPreference(Context context) {
        checkProcess(context);
        dumpPreference(PreferenceManager.getDefaultSharedPreferences(context), "default preference:");
    }

    private static void dumpPreference(SharedPreferences sharedPreferences, String str) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str);
        stringBuffer.append(ShellUtils.COMMAND_LINE_END);
        Map<String, ?> all = sharedPreferences.getAll();
        for (String str2 : all.keySet()) {
            stringBuffer.append(str2);
            stringBuffer.append(":");
            stringBuffer.append(all.get(str2));
            stringBuffer.append(ShellUtils.COMMAND_LINE_END);
        }
        MyLog.w(stringBuffer.toString());
    }

    public static boolean getSettingBoolean(Context context, String str, boolean z2) {
        checkProcess(context);
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(str, z2);
    }

    public static float getSettingFloat(Context context, String str, float f2) {
        checkProcess(context);
        return PreferenceManager.getDefaultSharedPreferences(context).getFloat(str, f2);
    }

    public static int getSettingInt(Context context, String str, int i2) {
        checkProcess(context);
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(str, i2);
    }

    public static long getSettingLong(Context context, String str, long j2) {
        checkProcess(context);
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(str, j2);
    }

    public static String getSettingString(Context context, String str, String str2) {
        checkProcess(context);
        return PreferenceManager.getDefaultSharedPreferences(context).getString(str, str2);
    }

    public static boolean hasKey(Context context, String str) {
        checkProcess(context);
        return PreferenceManager.getDefaultSharedPreferences(context).contains(str);
    }

    public static void increaseSettingInt(Context context, String str) {
        checkProcess(context);
        increaseSettingInt(PreferenceManager.getDefaultSharedPreferences(context), str);
    }

    public static void increaseSettingLong(SharedPreferences sharedPreferences, String str, long j2) {
        sharedPreferences.edit().putLong(str, sharedPreferences.getLong(str, 0L) + j2).commit();
    }

    public static void putNotNullExtra(Map<String, String> map, String str, String str2) {
        if (map == null || str == null || str2 == null) {
            return;
        }
        map.put(str, str2);
    }

    public static void removePreference(Context context, String str) {
        checkProcess(context);
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(str).commit();
    }

    public static void setSettingBoolean(Context context, String str, boolean z2) {
        checkProcess(context);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(str, z2).commit();
    }

    public static void setSettingFloat(Context context, String str, float f2) {
        checkProcess(context);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat(str, f2).commit();
    }

    public static void setSettingInt(Context context, String str, int i2) {
        checkProcess(context);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(str, i2).commit();
    }

    public static void setSettingLong(Context context, String str, long j2) {
        try {
            checkProcess(context);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(str, j2).commit();
        } catch (Exception e2) {
            MyLog.e(e2);
        }
    }

    public static void setSettingString(Context context, String str, String str2) {
        checkProcess(context);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(str, str2).commit();
    }

    public static void dumpDefaultPreference(Context context, String str) {
        dumpPreference(context.getSharedPreferences(str, 0), str);
    }

    public static void increaseSettingInt(SharedPreferences sharedPreferences, String str) {
        sharedPreferences.edit().putInt(str, sharedPreferences.getInt(str, 0) + 1).commit();
    }

    public static void setSettingInt(SharedPreferences sharedPreferences, String str, int i2) {
        sharedPreferences.edit().putInt(str, i2).commit();
    }

    public static void increaseSettingInt(SharedPreferences sharedPreferences, String str, int i2) {
        sharedPreferences.edit().putInt(str, sharedPreferences.getInt(str, 0) + i2).commit();
    }

    public static void checkProcess(Context context) {
    }
}
