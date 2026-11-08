package com.xiaomi.push.clientreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;

/* JADX INFO: loaded from: classes.dex */
public class PowerStatsSP {
    private static final String KEY_ANDROID_VC = "android_vc";
    private static final String KEY_CURRENT_SCREEN_STATE = "current_screen_state";
    private static final String KEY_CURRENT_SCREEN_STATE_START_TIME = "current_screen_state_start_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_SCREEN_OFF_DOWN_COUNT = "off_down_count";
    private static final String KEY_SCREEN_OFF_DURATION = "off_duration";
    private static final String KEY_SCREEN_OFF_PING_COUNT = "off_ping_count";
    private static final String KEY_SCREEN_OFF_PONG_COUNT = "off_pong_count";
    private static final String KEY_SCREEN_OFF_UP_COUNT = "off_up_count";
    private static final String KEY_SCREEN_ON_DOWN_COUNT = "on_down_count";
    private static final String KEY_SCREEN_ON_DURATION = "on_duration";
    private static final String KEY_SCREEN_ON_PING_COUNT = "on_ping_count";
    private static final String KEY_SCREEN_ON_PONG_COUNT = "on_pong_count";
    private static final String KEY_SCREEN_ON_UP_COUNT = "on_up_count";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_XMSF_VC = "xmsf_vc";
    private static final int MAX_COUNT = 1073741823;
    private static final int MS_FOR_24H = 86400000;
    private static boolean SHOULD_CHECK_VC = true;
    private static final String SP_FILE_NAME = "sp_power_stats";
    private static final int VALUE_SCREEN_OFF = 0;
    private static final int VALUE_SCREEN_ON = 1;
    private static int XMSF_VC;

    private static PowerStatsModel buildModel(Context context) {
        SharedPreferences sp = getSp(context);
        PowerStatsModel powerStatsModel = new PowerStatsModel();
        powerStatsModel.setOffUpCount(sp.getInt(KEY_SCREEN_OFF_UP_COUNT, 0));
        powerStatsModel.setOffDownCount(sp.getInt(KEY_SCREEN_OFF_DOWN_COUNT, 0));
        powerStatsModel.setOffPingCount(sp.getInt(KEY_SCREEN_OFF_PING_COUNT, 0));
        powerStatsModel.setOffPongCount(sp.getInt(KEY_SCREEN_OFF_PONG_COUNT, 0));
        powerStatsModel.setOffDuration(sp.getLong(KEY_SCREEN_OFF_DURATION, 0L));
        powerStatsModel.setOnUpCount(sp.getInt(KEY_SCREEN_ON_UP_COUNT, 0));
        powerStatsModel.setOnDownCount(sp.getInt(KEY_SCREEN_ON_DOWN_COUNT, 0));
        powerStatsModel.setOnPingCount(sp.getInt(KEY_SCREEN_ON_PING_COUNT, 0));
        powerStatsModel.setOnPongCount(sp.getInt(KEY_SCREEN_ON_PONG_COUNT, 0));
        powerStatsModel.setOnDuration(sp.getLong(KEY_SCREEN_ON_DURATION, 0L));
        powerStatsModel.setStartTime(sp.getLong(KEY_START_TIME, 0L));
        powerStatsModel.setEndTime(sp.getLong(KEY_END_TIME, 0L));
        powerStatsModel.setXmsfVc(sp.getInt("xmsf_vc", 0));
        powerStatsModel.setAndroidVc(sp.getInt(KEY_ANDROID_VC, 0));
        return powerStatsModel;
    }

    private static void checkToUpload(Context context, long j2, long j3, int i2, int i3) {
        if (j2 > 0) {
            if (isVcChanged(context) || i2 >= MAX_COUNT || j3 - j2 >= 86400000) {
                getSp(context).edit().putLong(KEY_END_TIME, j3).apply();
                upload(context, j3, i3);
            }
        }
    }

    private static int getScreenState(boolean z2) {
        return z2 ? 1 : 0;
    }

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(SP_FILE_NAME, 0);
    }

    private static int getXmsfVersionCode(Context context) {
        if (XMSF_VC <= 0) {
            XMSF_VC = MIUIUtils.getXmsfVersionCode(context);
        }
        return XMSF_VC;
    }

    private static boolean isVcChanged(Context context) {
        boolean z2 = false;
        if (SHOULD_CHECK_VC) {
            SHOULD_CHECK_VC = false;
            SharedPreferences sp = getSp(context);
            int i2 = sp.getInt("xmsf_vc", 0);
            int i3 = sp.getInt(KEY_ANDROID_VC, 0);
            if (i2 != 0 && i3 != 0 && (i2 != getXmsfVersionCode(context) || i3 != Build.VERSION.SDK_INT)) {
                z2 = true;
            }
        }
        PowerStatsHelper.log("isVcChanged = " + z2);
        return z2;
    }

    public static void onPing(final Context context, final long j2, final boolean z2) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.clientreport.PowerStatsSP.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PowerStatsSP.recordPing(context, j2, z2);
                } catch (Exception e2) {
                    MyLog.w("PowerStatsSP onPing exception: " + e2.getMessage());
                }
            }
        });
    }

    public static void onPong(final Context context, final long j2, final boolean z2) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.clientreport.PowerStatsSP.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PowerStatsSP.recordPong(context, j2, z2);
                } catch (Exception e2) {
                    MyLog.w("PowerStatsSP onPong exception: " + e2.getMessage());
                }
            }
        });
    }

    public static void onReceiveMsg(final Context context, final long j2, final boolean z2) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.clientreport.PowerStatsSP.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PowerStatsSP.recordReceiveMsg(context, j2, z2);
                } catch (Exception e2) {
                    MyLog.w("PowerStatsSP onReceiveMsg exception: " + e2.getMessage());
                }
            }
        });
    }

    public static void onScreenOff(final Context context, final long j2) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.clientreport.PowerStatsSP.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PowerStatsSP.recordScreenOff(context, j2);
                } catch (Exception e2) {
                    MyLog.w("PowerStatsSP onScreenOff exception: " + e2.getMessage());
                }
            }
        });
    }

    public static void onScreenOn(final Context context, final long j2) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.clientreport.PowerStatsSP.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PowerStatsSP.recordScreenOn(context, j2);
                } catch (Exception e2) {
                    MyLog.w("PowerStatsSP onScreenOn exception: " + e2.getMessage());
                }
            }
        });
    }

    public static void onSendMsg(final Context context, final long j2, final boolean z2) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.clientreport.PowerStatsSP.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PowerStatsSP.recordSendMsg(context, j2, z2);
                } catch (Exception e2) {
                    MyLog.w("PowerStatsSP onSendMsg exception: " + e2.getMessage());
                }
            }
        });
    }

    private static void recordInit(Context context, SharedPreferences sharedPreferences, long j2, int i2) {
        PowerStatsHelper.log("recordInit");
        sharedPreferences.edit().putLong(KEY_START_TIME, j2).putInt(KEY_CURRENT_SCREEN_STATE, i2).putLong(KEY_CURRENT_SCREEN_STATE_START_TIME, j2).putInt("xmsf_vc", getXmsfVersionCode(context)).putInt(KEY_ANDROID_VC, Build.VERSION.SDK_INT).apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void recordPing(Context context, long j2, boolean z2) {
        int i2;
        try {
            PowerStatsHelper.log("recordPing start");
            int screenState = getScreenState(z2);
            SharedPreferences sp = getSp(context);
            long j3 = sp.getLong(KEY_START_TIME, 0L);
            if (j3 <= 0) {
                recordInit(context, sp, j2, screenState);
            }
            if (screenState == 1) {
                i2 = sp.getInt(KEY_SCREEN_ON_PING_COUNT, 0) + 1;
                sp.edit().putInt(KEY_SCREEN_ON_PING_COUNT, i2).apply();
            } else {
                i2 = sp.getInt(KEY_SCREEN_OFF_PING_COUNT, 0) + 1;
                sp.edit().putInt(KEY_SCREEN_OFF_PING_COUNT, i2).apply();
            }
            checkToUpload(context, j3, j2, i2, screenState);
            PowerStatsHelper.log("recordPing complete");
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void recordPong(Context context, long j2, boolean z2) {
        int i2;
        try {
            PowerStatsHelper.log("recordPong start");
            int screenState = getScreenState(z2);
            SharedPreferences sp = getSp(context);
            long j3 = sp.getLong(KEY_START_TIME, 0L);
            if (j3 <= 0) {
                recordInit(context, sp, j2, screenState);
            }
            if (screenState == 1) {
                i2 = sp.getInt(KEY_SCREEN_ON_PONG_COUNT, 0) + 1;
                sp.edit().putInt(KEY_SCREEN_ON_PONG_COUNT, i2).apply();
            } else {
                i2 = sp.getInt(KEY_SCREEN_OFF_PONG_COUNT, 0) + 1;
                sp.edit().putInt(KEY_SCREEN_OFF_PONG_COUNT, i2).apply();
            }
            checkToUpload(context, j3, j2, i2, screenState);
            PowerStatsHelper.log("recordPong complete");
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void recordReceiveMsg(Context context, long j2, boolean z2) {
        int i2;
        try {
            PowerStatsHelper.log("recordReceiveMsg start");
            int screenState = getScreenState(z2);
            SharedPreferences sp = getSp(context);
            long j3 = sp.getLong(KEY_START_TIME, 0L);
            if (j3 <= 0) {
                recordInit(context, sp, j2, screenState);
            }
            if (screenState == 1) {
                i2 = sp.getInt(KEY_SCREEN_ON_DOWN_COUNT, 0) + 1;
                sp.edit().putInt(KEY_SCREEN_ON_DOWN_COUNT, i2).apply();
            } else {
                i2 = sp.getInt(KEY_SCREEN_OFF_DOWN_COUNT, 0) + 1;
                sp.edit().putInt(KEY_SCREEN_OFF_DOWN_COUNT, i2).apply();
            }
            checkToUpload(context, j3, j2, i2, screenState);
            PowerStatsHelper.log("recordReceiveMsg complete");
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void recordScreenOff(Context context, long j2) {
        try {
            PowerStatsHelper.log("recordScreenOff start");
            SharedPreferences sp = getSp(context);
            long j3 = sp.getLong(KEY_START_TIME, 0L);
            if (j3 <= 0) {
                recordInit(context, sp, j2, 0);
            } else {
                long j4 = sp.getLong(KEY_CURRENT_SCREEN_STATE_START_TIME, 0L);
                long j5 = sp.getLong(KEY_SCREEN_ON_DURATION, 0L);
                long j6 = j2 - j4;
                if (j6 > 0) {
                    j5 += j6;
                }
                sp.edit().putInt(KEY_CURRENT_SCREEN_STATE, 0).putLong(KEY_CURRENT_SCREEN_STATE_START_TIME, j2).putLong(KEY_SCREEN_ON_DURATION, j5).apply();
                checkToUpload(context, j3, j2, 0, 1);
            }
            PowerStatsHelper.log("recordScreenOff complete");
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void recordScreenOn(Context context, long j2) {
        try {
            PowerStatsHelper.log("recordScreenOn start");
            SharedPreferences sp = getSp(context);
            long j3 = sp.getLong(KEY_START_TIME, 0L);
            if (j3 <= 0) {
                recordInit(context, sp, j2, 1);
            } else {
                long j4 = sp.getLong(KEY_CURRENT_SCREEN_STATE_START_TIME, 0L);
                long j5 = sp.getLong(KEY_SCREEN_OFF_DURATION, 0L);
                long j6 = j2 - j4;
                if (j6 > 0) {
                    j5 += j6;
                }
                sp.edit().putInt(KEY_CURRENT_SCREEN_STATE, 1).putLong(KEY_CURRENT_SCREEN_STATE_START_TIME, j2).putLong(KEY_SCREEN_OFF_DURATION, j5).apply();
                checkToUpload(context, j3, j2, 0, 1);
            }
            PowerStatsHelper.log("recordScreenOn complete");
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void recordSendMsg(Context context, long j2, boolean z2) {
        int i2;
        try {
            PowerStatsHelper.log("recordSendMsg start");
            int screenState = getScreenState(z2);
            SharedPreferences sp = getSp(context);
            long j3 = sp.getLong(KEY_START_TIME, 0L);
            if (j3 <= 0) {
                recordInit(context, sp, j2, screenState);
            }
            if (screenState == 1) {
                i2 = sp.getInt(KEY_SCREEN_ON_UP_COUNT, 0) + 1;
                sp.edit().putInt(KEY_SCREEN_ON_UP_COUNT, i2).apply();
            } else {
                i2 = sp.getInt(KEY_SCREEN_OFF_UP_COUNT, 0) + 1;
                sp.edit().putInt(KEY_SCREEN_OFF_UP_COUNT, i2).apply();
            }
            checkToUpload(context, j3, j2, i2, screenState);
            PowerStatsHelper.log("recordSendMsg complete");
        } catch (Throwable th) {
            throw th;
        }
    }

    private static void reset(Context context, long j2, int i2) {
        PowerStatsHelper.log("reset");
        getSp(context).edit().clear().putLong(KEY_START_TIME, j2).putInt(KEY_CURRENT_SCREEN_STATE, i2).putLong(KEY_CURRENT_SCREEN_STATE_START_TIME, j2).putInt("xmsf_vc", getXmsfVersionCode(context)).putInt(KEY_ANDROID_VC, Build.VERSION.SDK_INT).apply();
    }

    private static void upload(Context context, long j2, int i2) {
        PowerStatsHelper.log("upload");
        new PowerStatsReporter().upload(context, buildModel(context));
        reset(context, j2, i2);
    }
}
