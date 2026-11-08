package com.miui.powerkeeper.dfs;

import android.os.SystemProperties;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.util.Comparator;
import miui.provider.MiCloudSmsCmd;

/* JADX INFO: loaded from: classes.dex */
public class DfsUtils {
    public static final int EVENT_ID_AOD = 903111101;
    public static final int EVENT_ID_APP_USAGE = 903112000;
    public static final int EVENT_ID_DOZE = 903111412;
    public static final int EVENT_ID_KERNEL_WAKELOCK = 903111402;
    public static final int EVENT_ID_LOG = 903111201;
    public static final int EVENT_ID_OTHER = 903111501;
    public static final int EVENT_ID_PARTIAL_WAKELOCK = 903111401;
    public static final int EVENT_ID_PHONE_SIGNAL_POOR = 903111413;
    public static final int EVENT_ID_SUBCLASS = 903111601;
    public static final int EVENT_ID_SUBCLASS_APP_USAGE = 903112001;
    public static final int EVENT_ID_WAKEUP_ALARM = 903111311;
    public static final int EVENT_ID_WAKEUP_BT = 903111321;
    public static final int EVENT_ID_WAKEUP_CAMERA = 903111323;
    public static final int EVENT_ID_WAKEUP_MODEM = 903111328;
    public static final int EVENT_ID_WAKEUP_POWER = 903111399;
    public static final int EVENT_ID_WAKEUP_SENSOR = 903111330;
    public static final int EVENT_ID_WAKEUP_TP = 903111322;
    public static final int EVENT_ID_WAKEUP_WIFI = 903111325;
    public static final int EVENT_ID_WIFI_SIGNAL_POOR = 903111414;
    public static final long HOUR_TO_MILLIS = 3600000;
    public static final int LOG_TYPE_ALARM_FLAG = 20;
    public static final int LOG_TYPE_BATTERY_FLAG = 0;
    public static final int LOG_TYPE_BT_FLAG = 3;
    public static final int LOG_TYPE_CAMERA_FLAG = 11;
    public static final int LOG_TYPE_CPU_FLAG = 17;
    public static final int LOG_TYPE_DEVICE_IDLE_FLAG = 16;
    public static final int LOG_TYPE_EVENT_LOG_FLAG = 14;
    public static final int LOG_TYPE_JOB_FLAG = 21;
    public static final int LOG_TYPE_KERNEL_FLAG = 7;
    public static final int LOG_TYPE_MAIN_LOG_FLAG = 12;
    public static final int LOG_TYPE_MODEM_FLAG = 5;
    public static final int LOG_TYPE_NETWORK_FLAG = 6;
    public static final int LOG_TYPE_NFC_FLAG = 19;
    public static final int LOG_TYPE_POWER_FLAG = 1;
    public static final int LOG_TYPE_RADIO_LOG_FLAG = 13;
    public static final int LOG_TYPE_SENSOR_FLAG = 4;
    public static final int LOG_TYPE_SETTINGS_FLAG = 18;
    public static final int LOG_TYPE_SUB_SYSTEM_FLAG = 15;
    public static final int LOG_TYPE_TETHER_FLAG = 9;
    public static final int LOG_TYPE_TP_FLAG = 10;
    public static final int LOG_TYPE_VPN_FLAG = 8;
    public static final int LOG_TYPE_WIFI_FLAG = 2;
    public static final int SCENE_DEFAULT = 0;
    public static final int SCENE_DEFAULT_SB = 1;
    public static final int SCENE_NIGHT_SB = 2;
    public static final long SECOND_TO_MICROS = 1000000;
    public static final String TAG = "DFS-DfsUtils";
    public static final int TOTAL_LOG_TYPES = 22;
    public static float VERSION = 5.1f;
    public static float VERSION_NEXT = 6.0f;
    public static int WHICH = 2;
    public static final boolean DEBUG = SystemProperties.getBoolean("persist.debug.power.dfs", false);
    public static final boolean DEBUG_INTERNAL = SystemProperties.getBoolean("persist.debug.power.dfs_i", false);
    public static final String[] LOG_TYPE_STRINGS = {RegisterAppChannelInfoCollectionJob.JSON_KEY_APP_ID, "b", "c", "d", "e", "f", "g", "h", "i", "j", MiCloudSmsCmd.TYPE_LOCK, "l", "m", MiCloudSmsCmd.TYPE_NOISE, "o", "p", "q", "r", RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH, RegisterAppChannelInfoCollectionJob.JSON_KEY_CHANNEL_TYPE, "u", "v"};
    public static int debugEventId = 0;
    public static String debugWakeup = null;
    public static final Comparator<TimerEntry> countComparator = new Comparator() { // from class: com.miui.powerkeeper.dfs.b
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return DfsUtils.lambda$static$0((DfsUtils.TimerEntry) obj, (DfsUtils.TimerEntry) obj2);
        }
    };
    public static final Comparator<TimerEntry> timerComparator = new Comparator() { // from class: com.miui.powerkeeper.dfs.c
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return DfsUtils.lambda$static$1((DfsUtils.TimerEntry) obj, (DfsUtils.TimerEntry) obj2);
        }
    };

    public static int addLogBit(int i2, int i3) {
        return i2 <= 31 ? i3 | (1 << i2) : i3;
    }

    public static String eventIdToString(int i2) {
        switch (i2) {
            case EVENT_ID_AOD /* 903111101 */:
                return "AOD";
            case EVENT_ID_LOG /* 903111201 */:
                return "Log";
            case EVENT_ID_WAKEUP_ALARM /* 903111311 */:
                return "alarm";
            case EVENT_ID_WAKEUP_BT /* 903111321 */:
                return "BT";
            case EVENT_ID_WAKEUP_TP /* 903111322 */:
                return "TP";
            case EVENT_ID_WAKEUP_CAMERA /* 903111323 */:
                return "Camera";
            case EVENT_ID_WAKEUP_WIFI /* 903111325 */:
                return "Wi-Fi";
            case EVENT_ID_WAKEUP_MODEM /* 903111328 */:
                return "Modem";
            case EVENT_ID_WAKEUP_SENSOR /* 903111330 */:
                return "Sensor";
            case EVENT_ID_PARTIAL_WAKELOCK /* 903111401 */:
                return "partial wakelock";
            case EVENT_ID_KERNEL_WAKELOCK /* 903111402 */:
                return "kernel wakelock";
            case EVENT_ID_DOZE /* 903111412 */:
                return "doze";
            case EVENT_ID_PHONE_SIGNAL_POOR /* 903111413 */:
                return "phone signal poor";
            case EVENT_ID_WIFI_SIGNAL_POOR /* 903111414 */:
                return "wifi signal poor";
            default:
                return "other";
        }
    }

    public static String getPackageForUid(int i2) {
        if (i2 < 1000) {
            return i2 + "";
        }
        if (i2 == 1000) {
            return "Android";
        }
        String[] packagesForUid = PowerKeeperApplication.b().getPackageManager().getPackagesForUid(i2);
        if (packagesForUid != null && packagesForUid.length > 0) {
            return packagesForUid[0];
        }
        return i2 + "";
    }

    public static boolean isSceneEnabled(int i2) {
        return i2 == 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$0(TimerEntry timerEntry, TimerEntry timerEntry2) {
        return Long.compare(timerEntry2.count, timerEntry.count);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$1(TimerEntry timerEntry, TimerEntry timerEntry2) {
        return Long.compare(timerEntry2.timeUs, timerEntry.timeUs);
    }

    public static String logFlagsToString(int i2) {
        StringBuilder sb = new StringBuilder();
        if (LOG_TYPE_STRINGS.length != 22) {
            return "";
        }
        for (int i3 = 0; i3 < 22; i3++) {
            if (((i2 >> i3) & 1) != 0) {
                sb.append(LOG_TYPE_STRINGS[i3]);
            }
        }
        return sb.toString();
    }

    public static int priorityToEventId(int i2) {
        switch (i2) {
            case 0:
                return EVENT_ID_WAKEUP_ALARM;
            case 1:
                return EVENT_ID_PARTIAL_WAKELOCK;
            case 2:
                return EVENT_ID_KERNEL_WAKELOCK;
            case 3:
                return EVENT_ID_PHONE_SIGNAL_POOR;
            case 4:
                return EVENT_ID_WIFI_SIGNAL_POOR;
            case 5:
                return EVENT_ID_LOG;
            case 6:
                return EVENT_ID_AOD;
            case 7:
                return EVENT_ID_DOZE;
            default:
                return EVENT_ID_OTHER;
        }
    }

    public static final class TimerEntry {
        int count;
        int id;
        String name;
        long timeUs;

        TimerEntry() {
            this.name = "";
            this.timeUs = 0L;
            this.count = 0;
            this.id = -1;
        }

        TimerEntry(String str, long j2, int i2) {
            this(str, j2, i2, 0);
        }

        TimerEntry(String str, long j2, int i2, int i3) {
            this.name = str;
            this.timeUs = j2;
            this.count = i2;
            this.id = i3;
        }
    }
}
