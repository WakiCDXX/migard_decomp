package com.miui.powerkeeper.batterylife;

import android.annotation.NonNull;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.LocalLog;
import com.miui.powerkeeper.utils.ShellUtils;
import com.miui.powerkeeper.utils.Utils;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.mpcd.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class BLUtils {
    public static final int BATTERY_LIFE_UNUSUAL_MIN = 3;
    public static final String BL_SP_NAME = "battery_life";
    public static final float BL_VERSION = 5.8f;
    public static final String CLOUD_ALARM_LIFE = "battery_life_alarm";
    public static final String CLOUD_BATTERY_LIFE = "battery_life_do_log";
    public static final String CLOUD_JOB_LIFE = "battery_life_job";
    public static final String CLOUD_PROC_CPU_TIME_IN_STATE = "proc_cpu_time_in_state";
    public static final String CLOUD_RESOURCE_BATTERY_LIFE = "resource_battery_life";
    public static final String CLOUD_SSRU_LIFE = "battery_life_ssru";
    public static final boolean FILE_CACHING_SUPPORTED = true;
    public static final int FLAG_HISTORYICAL = 1;
    public static final int FLAG_HISTORYICAL_TRACK = 2;
    public static final int FLAG_NEXT = 16;
    public static final int FLAG_NIGHTCURENT_ABNORMAL = 4;
    public static final int FLAG_NIGHTCURENT_DEFAULT = 16;
    public static final int FLAG_NIGHTCURENT_INIT = 1;
    public static final int FLAG_NIGHTCURENT_NODATA = 8;
    public static final int FLAG_NIGHTCURENT_NORMAL = 2;
    public static final int FLAG_NIGHTCURENT_NOSETTING = 32;
    public static final int FLAG_SCREEN_OFF_ACTIVE = 4;
    public static final float FLUCTUATION_ABNORMAL_BOUNDARY = 0.1f;
    public static final int HISTORY_AVERAGE_DRY_DEFAULT_DAYS = 7;
    public static final int HISTORY_BATTERY_DRY_MAX = 7;
    public static final int HISTORY_BATTERY_LIFE_MAX = 7;
    public static final int HISTORY_FLUCTUATION = 7;
    public static final int MIN_DURATION_TIME_SEC = 20;
    public static final int NIGHTCURRENT_ABNORMAL_BOUNDARY = 20;
    public static final int POWERMODE_TOTAL = 100;
    public static final int _4_HOURS = 14400;
    public static final LocalLog LOCAL_LOG = new LocalLog(64);
    public static int FEATURE_FLAG = Utils.isDevelopmentOrDebugVersion() ? 1 : 0;

    private BLUtils() {
    }

    public static Drawable getAppIconByUid(Context context, int i2) {
        if (context == null) {
            context = Utils.APP_CONTEXT;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationIcon(packageManager.getApplicationInfo(getPackageForUid(context, i2), 0));
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String getAppNameByPkg(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 0)).toString();
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
            return str;
        }
    }

    public static String getAppNameByUid(Context context, int i2) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(getPackageForUid(context, i2), 0)).toString();
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
            return String.valueOf(i2);
        }
    }

    public static String getPackageForUid(Context context, int i2) {
        if (i2 < 1000) {
            return i2 + "";
        }
        if (i2 == 1000) {
            return "system";
        }
        String[] packagesForUid = context.getPackageManager().getPackagesForUid(i2);
        if (packagesForUid != null && packagesForUid.length > 0) {
            return packagesForUid[0];
        }
        return i2 + "";
    }

    public static String getPowerModeName(int i2) {
        if (i2 == 1) {
            return "性能模式";
        }
        if (i2 != 2) {
            return i2 != 3 ? "其它" : "普通省电模式";
        }
        return "均衡模式";
    }

    public static String getPowerModeNameEng(int i2) {
        if (i2 == 1) {
            return "Performance";
        }
        if (i2 == 2) {
            return "Balance";
        }
        if (i2 != 3) {
            return i2 != 100 ? "Other" : "Total";
        }
        return "Save_battery";
    }

    public static String getWifiSSID(Context context) {
        WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        if (connectionInfo != null) {
            return connectionInfo.getSSID();
        }
        return null;
    }

    public static boolean isCurrentDevice(@NonNull String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.equals(Build.DEVICE);
    }

    public static boolean isFlagSupport(int i2) {
        return (i2 & FEATURE_FLAG) != 0;
    }

    static boolean isPowerModeSupport(int i2) {
        return i2 == 1 || i2 == 2 || i2 == 3;
    }

    static String listToString(List list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
        }
        return sb.toString();
    }

    public static String msToHour(long j2) {
        if (j2 <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(32);
        sb.append(Constants.SEPARATOR_LEFT_PARENTESIS);
        long j3 = j2 / 3600000;
        if (j3 > 0) {
            sb.append(j3);
            sb.append("h");
        }
        long j4 = (j2 / 60000) % 60;
        if (j4 > 0) {
            sb.append(j4);
            sb.append("m");
        }
        long j5 = (j2 / 1000) % 60;
        if (j5 > 0) {
            sb.append(j5);
            sb.append(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
        }
        long j6 = j2 % 1000;
        if (j6 > 0) {
            sb.append(j6);
            sb.append("ms");
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public static String msToHourNoParentheses(long j2) {
        if (j2 <= 0) {
            return "0s";
        }
        StringBuilder sb = new StringBuilder(32);
        long j3 = j2 / 3600000;
        if (j3 > 0) {
            sb.append(j3);
            sb.append("h");
        }
        long j4 = (j2 / 60000) % 60;
        if (j4 > 0) {
            sb.append(j4);
            sb.append("m");
        }
        long j5 = (j2 / 1000) % 60;
        if (j5 > 0) {
            sb.append(j5);
            sb.append(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
        }
        long j6 = j2 % 1000;
        if (j6 > 0) {
            sb.append(j6);
            sb.append("ms");
        }
        return sb.toString();
    }

    public static long numberStringSubtract(String str, String str2) {
        try {
            return Long.valueOf(str.trim()).longValue() - Long.valueOf(str2.trim()).longValue();
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return 0L;
        }
    }

    public static Object readFile(String str) {
        File file = new File(str);
        if (!file.exists()) {
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file.toString());
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                try {
                    Object object = objectInputStream.readObject();
                    objectInputStream.close();
                    fileInputStream.close();
                    return object;
                } catch (Throwable th) {
                    try {
                        objectInputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                try {
                    fileInputStream.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
                throw th3;
            }
        } catch (IOException | ClassNotFoundException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String readSysNodeInfo(String str) {
        File file = new File(str);
        if (!file.exists() || file.length() <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(str));
            boolean z2 = true;
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (z2) {
                        z2 = false;
                    } else {
                        sb.append(ShellUtils.COMMAND_LINE_END);
                    }
                    sb.append(line);
                } catch (Throwable th) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
            bufferedReader.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return sb.toString();
    }

    public static String readSysNodeInfoByWhetstone(String str) {
        try {
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            return iWhetstoneActivityManagerAsInterface == null ? "" : (String) IWhetstoneActivityManager.class.getDeclaredMethod("readSysNodeInfo", String.class).invoke(iWhetstoneActivityManagerAsInterface, str);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static void writeFile(String str, Serializable serializable) {
        File file = new File(str);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.toString());
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                try {
                    objectOutputStream.writeObject(serializable);
                    objectOutputStream.close();
                    fileOutputStream.close();
                } catch (Throwable th) {
                    try {
                        objectOutputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                try {
                    fileOutputStream.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
                throw th3;
            }
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }
}
