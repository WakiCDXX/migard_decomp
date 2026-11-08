package com.miui.powerkeeper.customerpower;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerCompat;
import android.util.Log;
import com.xiaomi.onetrack.api.d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class CustomerPowerUtil {
    public static final int AID_ADB = 1011;
    public static final int AID_AUDIO = 1005;
    public static final int AID_AUDIOSERVER = 1041;
    public static final int AID_BLUETOOTH = 1002;
    public static final int AID_CAMERA = 1006;
    public static final int AID_CAMERASERVER = 1047;
    public static final int AID_CLAT = 1029;
    public static final int AID_COMPASS = 1008;
    public static final int AID_DBUS = 1038;
    public static final int AID_DEBUGGERD = 1045;
    public static final int AID_DHCP = 1014;
    public static final int AID_DNS = 1051;
    public static final int AID_DNS_TETHER = 1052;
    public static final int AID_DRM = 1019;
    public static final int AID_DRMRPC = 1026;
    public static final int AID_ESE = 1060;
    public static final int AID_FIREWALL = 1048;
    public static final int AID_GPS = 1021;
    public static final int AID_GRAPHICS = 1003;
    public static final int AID_INPUT = 1004;
    public static final int AID_INSTALL = 1012;
    public static final int AID_KEYSTORE = 1017;
    public static final int AID_LOG = 1007;
    public static final int AID_LOGD = 1036;
    public static final int AID_LOOP_RADIO = 1030;
    public static final int AID_MDNSR = 1020;
    public static final int AID_MEDIA = 1013;
    public static final int AID_MEDIA_AUDIO = 1055;
    public static final int AID_MEDIA_CODEC = 1046;
    public static final int AID_MEDIA_DRM = 1031;
    public static final int AID_MEDIA_EX = 1040;
    public static final int AID_MEDIA_IMAGE = 1057;
    public static final int AID_MEDIA_OBB = 1059;
    public static final int AID_MEDIA_RW = 1023;
    public static final int AID_MEDIA_VIDEO = 1056;
    public static final int AID_METRICSD = 1043;
    public static final int AID_METRICS_COLL = 1042;
    public static final int AID_MOUNT = 1009;
    public static final int AID_MTP = 1024;
    public static final int AID_NFC = 1027;
    public static final int AID_NVRAM = 1050;
    public static final int AID_OTA_UPDATE = 1061;
    public static final int AID_PACKAGE_INFO = 1032;
    public static final int AID_RADIO = 1001;
    public static final int AID_ROOT = 0;
    public static final int AID_SDCARD_ALL = 1035;
    public static final int AID_SDCARD_AV = 1034;
    public static final int AID_SDCARD_PICS = 1033;
    public static final int AID_SDCARD_R = 1028;
    public static final int AID_SDCARD_RW = 1015;
    public static final int AID_SHARED_RELRO = 1037;
    public static final int AID_SYSTEM = 1000;
    public static final int AID_TLSDATE = 1039;
    public static final int AID_TOMBSTONED = 1058;
    public static final int AID_TRUNKS = 1049;
    public static final int AID_UNUSED1 = 1022;
    public static final int AID_UNUSED2 = 1025;
    public static final int AID_USB = 1018;
    public static final int AID_VEHICLE_NETWORK = 1054;
    public static final int AID_VPN = 1016;
    public static final int AID_WEBSERV = 1044;
    public static final int AID_WEBVIEW_ZYGOTE = 1053;
    public static final int AID_WIFI = 1010;
    private static final String USER_HANDLE = "android.os.UserHandle";
    private static final String USER_HANDLE_EX = "miui.os.UserHandleEx";
    private static final int USER_NULL = -10000;
    private static final String TAG = CustomerPowerConstant.DEBUG + CustomerPowerUtil.class.getSimpleName();
    static final Comparator<TimerEntry> timerComparator = new Comparator<TimerEntry>() { // from class: com.miui.powerkeeper.customerpower.CustomerPowerUtil.1
        @Override // java.util.Comparator
        public int compare(TimerEntry timerEntry, TimerEntry timerEntry2) {
            long j2 = timerEntry.mTime;
            long j3 = timerEntry2.mTime;
            if (j2 < j3) {
                return 1;
            }
            return j2 > j3 ? -1 : 0;
        }
    };
    public static final String[] ANDROID_IDS_INFO = {"Android OS", "telephony", "bluetooth", "graphics", "input", "audio", "camera", "log", "compass", "mountd", "wifi", "adb", "install", "mediaserver", "dhcp", "sdcard_rw", "vpn", "keystore", "usb", "drm", "mdnsr", "gps", null, "media_rw", "mtp", null, "drmrpc", "nfc", "sdcard_r", "clat", "loop_radio", "mediadrm", "package_info", "sdcard_pics", "sdcard_av", "sdcard_all", "logd", "shared_relro", "dbus", "tlsdate", "mediaextractor", "audioserver", "metrics_collector", "metricsd", "webservd", "debuggerd", "mediacodec", "cameraserver", "firewalld", "trunksd", "nvram", d.P, "dns_tether", "webview_zygote", "vehicle_network", "media_audio", "media_vidio", "media_image", "tombstoned", "media_obb", "ese", "ota_update"};

    public static final class TimerEntry {
        public int mCount;
        public String mName;
        public long mTime;
        public int mUid;

        public TimerEntry(int i2, String str, long j2, int i3) {
            this.mUid = i2;
            this.mName = str;
            this.mTime = j2;
            this.mCount = i3;
        }
    }

    public static String formatDuration(long j2) {
        long j3;
        long j4;
        long j5;
        long j6;
        if (j2 <= 0) {
            return "";
        }
        if (j2 > 86400000) {
            j3 = j2 / 86400000;
            j2 -= 86400000 * j3;
        } else {
            j3 = 0;
        }
        if (j2 > 3600000) {
            j4 = j2 / 3600000;
            j2 -= 3600000 * j4;
        } else {
            j4 = 0;
        }
        if (j2 > 60000) {
            j5 = j2 / 60000;
            j2 -= 60000 * j5;
        } else {
            j5 = 0;
        }
        if (j2 > 1000) {
            j6 = j2 / 1000;
            j2 -= 1000 * j6;
        } else {
            j6 = 0;
        }
        if (j2 <= 0) {
            j2 = 0;
        }
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        if (j3 > 0) {
            sb.append(j3 + "d ");
        }
        if (j4 > 0) {
            sb.append(j4 + "h ");
        }
        if (j5 > 0) {
            sb.append(j5 + "m ");
        }
        if (j6 > 0) {
            sb.append(j6 + "s ");
        }
        if (j2 > 0) {
            sb.append(j2 + "ms ");
        }
        return sb.toString();
    }

    public static CharSequence getLabelByPackageName(Context context, String str) {
        ApplicationInfo applicationInfo;
        CharSequence charSequenceLoadLabel;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
            return (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null || (charSequenceLoadLabel = applicationInfo.loadLabel(packageManager)) == null) ? str : charSequenceLoadLabel;
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e(TAG, "getLabelByPackageName", e2);
        }
    }

    public static int getMaintenanceModeId() {
        try {
            try {
                Class<?> cls = Class.forName(USER_HANDLE);
                return cls.getField("MAINTENANCE_MODE_ID").getInt(cls);
            } catch (Exception e2) {
                e2.printStackTrace();
                return USER_NULL;
            }
        } catch (Exception unused) {
            Class<?> cls2 = Class.forName(USER_HANDLE_EX);
            return cls2.getField("MAINTENANCE_MODE_ID").getInt(cls2);
        }
    }

    public static String getPackageNameByUid(Context context, int i2) {
        String[] packagesForUid = context.getPackageManager().getPackagesForUid(i2);
        String str = (packagesForUid == null || packagesForUid.length <= 0) ? null : packagesForUid[0];
        if (i2 == 0) {
            return "Android System";
        }
        if (i2 >= 10000) {
            return str;
        }
        int i3 = i2 - 1000;
        if (i3 >= 0) {
            String[] strArr = ANDROID_IDS_INFO;
            if (i3 < strArr.length) {
                return strArr[i3];
            }
        }
        return i2 == 2950 ? "qti diag" : str;
    }

    public static String getTypeName(int i2) {
        if (i2 == 1) {
            return CustomerPowerConstant.BAD_SIGNAL;
        }
        if (i2 == 2) {
            return CustomerPowerConstant.HIGH_BTIGHT;
        }
        if (i2 == 5) {
            return CustomerPowerConstant.KERNEL_WAKELOCK;
        }
        if (i2 == 6) {
            return CustomerPowerConstant.KERNEL_WAKEUP;
        }
        if (i2 != 11) {
            return i2 != 12 ? "" : CustomerPowerConstant.APP_PARTIAL_WAKELOCK;
        }
        return CustomerPowerConstant.APP_BG_CPU;
    }

    public static int getUidByPackageName(Context context, String str) {
        return getUidByPackageName(context, str, context.getUserId());
    }

    public static boolean isMaintenanceModeEnable() {
        try {
            Class<?> cls = Class.forName(USER_HANDLE);
            Class[] clsArr = new Class[0];
            return ((Integer) cls.getDeclaredMethod("myUserId", null).invoke(cls, null)).intValue() == getMaintenanceModeId();
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static void sortTimerEntry(ArrayList<TimerEntry> arrayList) {
        Collections.sort(arrayList, timerComparator);
    }

    public static int getUidByPackageName(Context context, String str, int i2) {
        try {
            return PackageManagerCompat.getPackageUidAsUser(context.getPackageManager(), str, i2);
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        }
    }
}
