package com.xiaomi.push.tts;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.Region;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.extension.AppRunningUtils;
import com.xiaomi.push.extension.LogPools;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.AppRegionStorage;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class TtsUtils {
    private static final String KEY_FORMAT = "%s_%s";
    private static final String XIAO_AI_UI_APPEARANCE = "content://com.miui.voiceassist.xiaoai.manager.provider/ui/appearance_status";

    TtsUtils() {
    }

    public static String generateKey(String str, int i2) {
        return String.format(KEY_FORMAT, str, Integer.valueOf(i2));
    }

    public static int getErrorSceneCode(Context context, AudioManager audioManager) {
        int mode = getMode(audioManager);
        if (mode == 2 || mode == 3) {
            return PluginConstants.TTS_ERROR_CODE_USER_IN_CALL;
        }
        int ringerMode = getRingerMode(audioManager);
        if (isDndMode(context)) {
            return PluginConstants.TTS_ERROR_CODE_USER_IN_NOT_DISTURB;
        }
        if (ringerMode == 1 || ringerMode == 0) {
            return PluginConstants.TTS_ERROR_CODE_USER_IN_SILENT;
        }
        if (isTalkToXiaoAi(context, audioManager)) {
            return PluginConstants.TTS_ERROR_CODE_USER_TALK_TO_XA;
        }
        return audioManager.getStreamVolume(3) == 0 ? PluginConstants.TTS_ERROR_CODE_MUSIC_VOL_IS_0 : PluginConstants.TTS_ERROR_CODE_SUCCESS;
    }

    private static int getMode(AudioManager audioManager) {
        return audioManager.getMode();
    }

    private static int getRingerMode(AudioManager audioManager) {
        return audioManager.getRingerMode();
    }

    public static boolean isChinaRegion(Context context) {
        return Region.China.name().equals(AppRegionStorage.getInstance(context).getRegion());
    }

    public static boolean isDndMode(Context context) {
        Integer num = (Integer) JavaCalls.callMethod((NotificationManager) context.getSystemService("notification"), "getZenMode", new Object[0]);
        return num != null && num.intValue() == 1;
    }

    public static boolean isEngineVerSupport(Context context, String str, String str2) {
        int versionCode = AppInfoUtils.getVersionCode(context, str);
        int i2 = ToolUtils.parseInt(str2, -1);
        return i2 != -1 && versionCode >= i2;
    }

    public static boolean isTTSMessage(Object obj) {
        return MIPushNotificationHelper.MSG_BUSY_TYPE_TTS.equals(NotificationUtils.getMsgBusinessType(obj));
    }

    /* JADX WARN: Code duplicated, block: B:21:0x003f  */
    /* JADX WARN: Code duplicated, block: B:23:0x005c  */
    /* JADX WARN: Code duplicated, block: B:29:0x006d  */
    /* JADX WARN: Code duplicated, block: B:31:0x0085  */
    /* JADX WARN: Code duplicated, block: B:33:0x0087  */
    /* JADX WARN: Code duplicated, block: B:35:0x008d  */
    /* JADX WARN: Code duplicated, block: B:37:0x0097  */
    /* JADX WARN: Code duplicated, block: B:40:0x00a1  */
    /* JADX WARN: Code duplicated, block: B:53:0x00b1 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:54:? A[LOOP:0: B:38:0x009b->B:54:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:55:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Instruction removed from duplicated block: B:21:0x003f, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:29:0x006d, please report this as an issue */
    private static boolean isTalkToXiaoAi(Context context, AudioManager audioManager) throws Throwable {
        Cursor cursorQuery;
        int i2;
        List<ActivityManager.RunningAppProcessInfo> foregroundProcess;
        Iterator<ActivityManager.RunningAppProcessInfo> it;
        Cursor cursor = null;
        string = null;
        string = null;
        String string = null;
        try {
            cursorQuery = context.getContentResolver().query(Uri.parse(XIAO_AI_UI_APPEARANCE), null, null, null, null);
            if (cursorQuery != null) {
                try {
                    cursorQuery.moveToFirst();
                    string = cursorQuery.getString(cursorQuery.getColumnIndex("appearance_status"));
                } catch (Exception unused) {
                    if (cursorQuery != null) {
                    }
                    if (!TextUtils.isEmpty(string)) {
                        LogPools.TtsLog.d("XiaoAi ui status : " + string);
                        return "show".equalsIgnoreCase(string);
                    }
                    try {
                        i2 = Settings.Global.getInt(context.getContentResolver(), "lock_voiceassit_stream", -2);
                    } catch (Exception unused2) {
                        i2 = -2;
                    }
                    if (i2 != -2) {
                        LogPools.TtsLog.d("voice stream : " + i2);
                        if (i2 == 0) {
                            return true;
                        }
                        return false;
                    }
                    if (audioManager.isMusicActive()) {
                        foregroundProcess = AppRunningUtils.getForegroundProcess(context);
                        if (!ToolUtils.isEmptyList(foregroundProcess)) {
                            it = foregroundProcess.iterator();
                            while (it.hasNext()) {
                                if (PluginConstants.TTS_XIAOAI_PKGNAME.equalsIgnoreCase(it.next().processName)) {
                                    LogPools.TtsLog.d("cur name is xiaoai");
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (Exception unused3) {
            cursorQuery = null;
        } catch (Throwable th2) {
            th = th2;
        }
        if (!TextUtils.isEmpty(string)) {
            LogPools.TtsLog.d("XiaoAi ui status : " + string);
            return "show".equalsIgnoreCase(string);
        }
        i2 = Settings.Global.getInt(context.getContentResolver(), "lock_voiceassit_stream", -2);
        if (i2 != -2) {
            LogPools.TtsLog.d("voice stream : " + i2);
            if (i2 == 0) {
                return true;
            }
            return false;
        }
        if (audioManager.isMusicActive()) {
            foregroundProcess = AppRunningUtils.getForegroundProcess(context);
            if (!ToolUtils.isEmptyList(foregroundProcess)) {
                it = foregroundProcess.iterator();
                while (it.hasNext()) {
                    if (PluginConstants.TTS_XIAOAI_PKGNAME.equalsIgnoreCase(it.next().processName)) {
                        LogPools.TtsLog.d("cur name is xiaoai");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int percent(double d2, double d3) {
        if (d2 == 0.0d || d3 == 0.0d) {
            return 0;
        }
        return ToolUtils.parseInt(((DecimalFormat) NumberFormat.getPercentInstance()).format(d2 / d3).replace("%", ""), 0);
    }

    public static int volIndex(String str, int i2) {
        return (int) ((i2 * (ToolUtils.parseInt(str, 0) / 100.0f)) + 0.5f);
    }
}
