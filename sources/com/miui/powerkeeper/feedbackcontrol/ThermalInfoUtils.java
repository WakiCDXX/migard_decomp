package com.miui.powerkeeper.feedbackcontrol;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.WindowManager;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.NetUtils;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import miui.process.ForegroundInfo;
import miui.process.ProcessManager;
import miui.provider.Weather;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ThermalInfoUtils {
    public static final int DEFAULT_SHELL_CMD_TIMEOUT = 5;
    private static final String LEARNGING_CAPACITY_PATH = "/sys/class/power_supply/bms/charge_full";
    private static final String TAG = "ThermalInfoUtils";
    private static final String THERM_PATH = "sys/class/thermal/thermal_zone";
    private static final int TOP_CPU_COUNT = 10;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static boolean sIsTestMode = isTestMode();
    public static Map<String, Object> sMultiThermalIndex = new ArrayMap();

    private ThermalInfoUtils() {
    }

    public static int calculateNetSpeed(long j2, long j3) {
        long j4 = j2 / 1000;
        long j5 = j3 / 1048576;
        if (j4 > 0) {
            return (int) (j5 / j4);
        }
        return 0;
    }

    public static String createFileName(String str) {
        return Build.PRODUCT + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + Build.MODEL + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + Build.ID + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + str + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + System.currentTimeMillis();
    }

    public static void execShellCommand(String str, String str2) {
        execShellCommand(str, str2, 5);
    }

    public static String getAppVersionName(String str, Context context) {
        String str2 = "null";
        try {
            String str3 = context.getPackageManager().getPackageInfo(str, 0).versionName;
            try {
                return (TextUtils.isEmpty(str3) || str3.length() <= 0) ? "null" : str3;
            } catch (PackageManager.NameNotFoundException e2) {
                e = e2;
                str2 = str3;
                e.printStackTrace();
                return str2;
            }
        } catch (PackageManager.NameNotFoundException e3) {
            e = e3;
        }
    }

    public static int getBatteryCurrentNow(Context context) {
        return ((BatteryManager) context.getSystemService("batterymanager")).getIntProperty(2) / 1000;
    }

    /* JADX WARN: Code duplicated, block: B:45:0x0089 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static long getBoardThermalTemp() throws Throwable {
        Throwable th;
        BufferedReader bufferedReader;
        Exception e2;
        StringBuilder sb;
        IOException e3;
        BufferedReader bufferedReader2 = null;
        long j2 = 0;
        try {
            try {
                File file = new File("sys/class/thermal/thermal_message/board_sensor_temp");
                if (file.exists()) {
                    StringBuilder sb2 = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    try {
                        for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                            sb2.append(line);
                        }
                        j2 = Integer.parseInt(sb2.toString());
                        bufferedReader2 = bufferedReader;
                    } catch (Exception e4) {
                        e2 = e4;
                        Log.e(TAG, "getBoardThermalTemp" + e2);
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e5) {
                                e3 = e5;
                                sb = new StringBuilder();
                                sb.append("getBoardThermalTemp");
                                sb.append(e3);
                                Log.e(TAG, sb.toString());
                            }
                        }
                    }
                }
                if (bufferedReader2 != null) {
                    try {
                        bufferedReader2.close();
                    } catch (IOException e6) {
                        e3 = e6;
                        sb = new StringBuilder();
                        sb.append("getBoardThermalTemp");
                        sb.append(e3);
                        Log.e(TAG, sb.toString());
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                if (0 != 0) {
                    try {
                        bufferedReader2.close();
                    } catch (IOException e7) {
                        Log.e(TAG, "getBoardThermalTemp" + e7);
                    }
                }
                throw th;
            }
        } catch (Exception e8) {
            bufferedReader = null;
            e2 = e8;
        } catch (Throwable th3) {
            th = th3;
            if (0 != 0) {
                bufferedReader2.close();
            }
            throw th;
        }
        return j2;
    }

    /* JADX WARN: Code duplicated, block: B:47:0x00a2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static int getCurrentCapacity() throws Throwable {
        Throwable th;
        BufferedReader bufferedReader;
        Exception e2;
        StringBuilder sb;
        Exception e3;
        BufferedReader bufferedReader2 = null;
        int i2 = -1;
        try {
            try {
                File file = new File("/sys/class/power_supply/battery/charge_counter");
                if (file.exists()) {
                    StringBuilder sb2 = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    try {
                        for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                            sb2.append(line);
                        }
                        i2 = Integer.parseInt(sb2.toString());
                        if (DEBUG) {
                            Log.d(TAG, "capacity is " + i2);
                        }
                        bufferedReader2 = bufferedReader;
                    } catch (Exception e4) {
                        e2 = e4;
                        Log.e(TAG, "getCurrentCapacity Exception" + e2);
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e5) {
                                e3 = e5;
                                sb = new StringBuilder();
                                sb.append("getCurrentCapacity IOException");
                                sb.append(e3);
                                Log.e(TAG, sb.toString());
                            }
                        }
                    }
                }
                if (bufferedReader2 != null) {
                    try {
                        bufferedReader2.close();
                    } catch (Exception e6) {
                        e3 = e6;
                        sb = new StringBuilder();
                        sb.append("getCurrentCapacity IOException");
                        sb.append(e3);
                        Log.e(TAG, sb.toString());
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                if (0 != 0) {
                    try {
                        bufferedReader2.close();
                    } catch (Exception e7) {
                        Log.e(TAG, "getCurrentCapacity IOException" + e7);
                    }
                }
                throw th;
            }
        } catch (Exception e8) {
            bufferedReader = null;
            e2 = e8;
        } catch (Throwable th3) {
            th = th3;
            if (0 != 0) {
                bufferedReader2.close();
            }
            throw th;
        }
        return i2;
    }

    public static String getForegroundPkgs() {
        try {
            ForegroundInfo foregroundInfo = ProcessManager.getForegroundInfo();
            return foregroundInfo != null ? foregroundInfo.mForegroundPackageName : "";
        } catch (Exception e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static String getHardwareId() {
        try {
            return SystemProperties.get("ro.boot.hwversion", "");
        } catch (Exception e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static Double getLearningCapacity() throws Throwable {
        Double d2;
        File file = new File(LEARNGING_CAPACITY_PATH);
        if (!file.exists()) {
            Log.d(TAG, "no file /sys/class/power_supply/bms/charge_full");
            return Double.valueOf(0.0d);
        }
        BufferedReader bufferedReader = null;
        Double dValueOf = null;
        bufferedReader = null;
        try {
            try {
                BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file));
                while (true) {
                    try {
                        String line = bufferedReader2.readLine();
                        if (line == null) {
                            bufferedReader2.close();
                            try {
                                bufferedReader2.close();
                                return dValueOf;
                            } catch (IOException e2) {
                                Log.e(TAG, "IOException" + e2);
                                return dValueOf;
                            }
                        }
                        if (DEBUG) {
                            Log.d(TAG, "Read learning capacity = " + line);
                        }
                        dValueOf = Double.valueOf(Double.valueOf(line).doubleValue() / 1000.0d);
                    } catch (IOException e3) {
                        e = e3;
                        d2 = dValueOf;
                        bufferedReader = bufferedReader2;
                        e.printStackTrace();
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e4) {
                                Log.e(TAG, "IOException" + e4);
                            }
                        }
                        return d2;
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e5) {
                                Log.e(TAG, "IOException" + e5);
                            }
                        }
                        throw th;
                    }
                }
            } catch (IOException e6) {
                e = e6;
                d2 = null;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static int getModemSignal(Context context) {
        try {
            return ((TelephonyManager) context.getSystemService("phone")).getSignalStrength().getGsmSignalStrength();
        } catch (Exception unused) {
            return 99;
        }
    }

    private static void getMultiThermalIndex() throws Throwable {
        Throwable th;
        IOException iOException;
        StringBuilder sb;
        if (sMultiThermalIndex.isEmpty()) {
            String[] strArr = {"cpu-1-6", "cpu-1-7", "cpu-1-6-usr", "cpu-1-7-usr", "cpu-0-0-usr", "cpu-0-1-usr", "cpu-0-2-usr", "cpu-0-3-usr", "cpu-0-4-usr", "cpu-0-5-usr", "cpu-1-0-usr", "cpu-1-1-usr", "cpu-1-2-usr", "cpu-1-3-usr", "cam_therm0", "cam_therm1", "pa_therm0", "pa_therm1", "laser_therm0", "conn_therm", "slave_therm", "modem1_pa0", "modem1_pa1", "modem1_xo", "modem1_quiet", "modem1_wifi", "wireless_therm", "flash_therm", "charger_therm0", "charger_therm1", "wifi_therm", "battery", "cpu_therm", "xo_therm", "quiet_therm", "backlight_therm"};
            BufferedReader bufferedReader = null;
            int i2 = 0;
            while (i2 < getThermalZoneNums()) {
                try {
                    try {
                        File file = new File(THERM_PATH + i2 + "/type");
                        if (!file.exists()) {
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                    return;
                                } catch (IOException e2) {
                                    Log.e(TAG, "getMultiThermalIndex IOException" + e2);
                                    return;
                                }
                            }
                            return;
                        }
                        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        try {
                            String line = bufferedReader2.readLine();
                            if (ArrayUtils.contains(strArr, line)) {
                                sMultiThermalIndex.put(line, Integer.valueOf(i2));
                            }
                            bufferedReader2.close();
                            i2++;
                            bufferedReader = bufferedReader2;
                        } catch (FileNotFoundException e3) {
                            e = e3;
                            bufferedReader = bufferedReader2;
                        } catch (IOException e4) {
                            e = e4;
                            bufferedReader = bufferedReader2;
                            Log.e(TAG, "getMultiThermalIndex IOException" + e);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                    return;
                                } catch (IOException e5) {
                                    iOException = e5;
                                    sb = new StringBuilder();
                                    sb.append("getMultiThermalIndex IOException");
                                    sb.append(iOException);
                                    Log.e(TAG, sb.toString());
                                }
                            }
                            return;
                        } catch (Throwable th2) {
                            th = th2;
                            bufferedReader = bufferedReader2;
                            if (bufferedReader == null) {
                                throw th;
                            }
                            try {
                                bufferedReader.close();
                                throw th;
                            } catch (IOException e6) {
                                Log.e(TAG, "getMultiThermalIndex IOException" + e6);
                                throw th;
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                    }
                } catch (FileNotFoundException e7) {
                    e = e7;
                } catch (IOException e8) {
                    e = e8;
                }
                Log.e(TAG, "getMultiThermalIndex FileNotFoundException" + e);
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                        return;
                    } catch (IOException e9) {
                        iOException = e9;
                        sb = new StringBuilder();
                        sb.append("getMultiThermalIndex IOException");
                        sb.append(iOException);
                        Log.e(TAG, sb.toString());
                    }
                }
                return;
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e10) {
                    iOException = e10;
                    sb = new StringBuilder();
                    sb.append("getMultiThermalIndex IOException");
                    sb.append(iOException);
                    Log.e(TAG, sb.toString());
                }
            }
        }
    }

    public static List<Map<String, Object>> getMultiThermalTemp() throws Throwable {
        ArrayList arrayList = new ArrayList();
        if (sMultiThermalIndex.isEmpty()) {
            getMultiThermalIndex();
        }
        for (Map.Entry<String, Object> entry : sMultiThermalIndex.entrySet()) {
            ArrayMap arrayMap = new ArrayMap();
            arrayMap.put("ntc", entry.getKey());
            arrayMap.put("temp", Integer.valueOf(readMultiThermalTemp(Integer.parseInt(entry.getValue().toString()))));
            arrayList.add(arrayMap);
        }
        return arrayList;
    }

    public static int getNetType(Context context) {
        if (getNetworkType(context) == 1) {
            return NetUtils.isWifi6(context) ? 3 : 2;
        }
        if (getNetworkType(context) != 0) {
            return -1;
        }
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo();
            if (activeNetworkInfo == null || activeNetworkInfo.getType() != 0 || !activeNetworkInfo.isConnected()) {
                return -1;
            }
            int networkType = ((TelephonyManager) context.getSystemService("phone")).getNetworkType();
            if (networkType == 20) {
                return 1;
            }
            return networkType == 13 ? 0 : -1;
        } catch (Exception e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    public static int getNetworkType(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.getType();
            }
            return -1;
        } catch (Exception e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    public static int getPlugType(Context context) {
        try {
            Intent intentRegisterReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 2);
            if (intentRegisterReceiver != null) {
                return intentRegisterReceiver.getIntExtra("plugged", 0);
            }
            if (DEBUG) {
                Log.d(TAG, "getBatteryPluggedType null");
            }
            return 0;
        } catch (Exception e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    public static JSONObject getProcessList() {
        JSONObject jSONObject = new JSONObject();
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (ThermalManager.ProcessInfo processInfo : getTopProcessInfoByCpuRate(10)) {
                if (processInfo.mPercent != -1) {
                    ArrayMap arrayMap = new ArrayMap();
                    arrayMap.put("process", processInfo.mPName);
                    arrayMap.put(LogType.LOG_TYPE_CPU_NAME, Integer.valueOf(processInfo.mPercent));
                    arrayList.add(arrayMap);
                    arrayList2.add(processInfo.lineToString());
                }
            }
            jSONObject.put("total", arrayList);
            jSONObject.put(Weather.AlertInfo.DETAIL, arrayList2);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return jSONObject;
    }

    public static int getRefreshRate(Context context) {
        try {
            return (int) ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRefreshRate();
        } catch (Exception e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    public static String getResolution(Context context) {
        try {
            WindowManager windowManager = (WindowManager) context.getSystemService("window");
            Point point = new Point();
            windowManager.getDefaultDisplay().getRealSize(point);
            return point.y + "×" + point.x;
        } catch (Exception e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static int getRingMode(Context context) {
        return ((AudioManager) context.getSystemService("audio")).getRingerMode();
    }

    public static int getScreenBrightness(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), "screen_brightness");
        } catch (Exception e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    private static int getThermalZoneNums() {
        for (int i2 = 0; i2 < 100; i2++) {
            File file = new File(THERM_PATH + i2);
            if (!file.exists() && !file.isDirectory()) {
                return i2;
            }
        }
        return 0;
    }

    public static ThermalManager.ProcessInfo[] getTopProcess(int i2) throws Throwable {
        ThermalManager.ProcessInfo[] processInfoArr = new ThermalManager.ProcessInfo[i2];
        try {
            List<ThermalManager.ProcessInfo> topProcessInfoByCpuRate = getTopProcessInfoByCpuRate(i2);
            for (int i3 = 0; i3 < topProcessInfoByCpuRate.size(); i3++) {
                processInfoArr[i3] = topProcessInfoByCpuRate.get(i3);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return processInfoArr;
    }

    public static List<ThermalManager.ProcessInfo> getTopProcessInfoByCpuRate(int i2) throws Throwable {
        List<String> allProcessInfoFromCpuInfo = Utils.getAllProcessInfoFromCpuInfo();
        ArrayList arrayList = new ArrayList();
        arrayList.clear();
        for (String str : allProcessInfoFromCpuInfo) {
            if (str != null && ThermalManager.ProcessInfo.isValidProInfoLine(str)) {
                arrayList.add(new ThermalManager.ProcessInfo(str));
            }
        }
        return arrayList.size() <= i2 ? arrayList : arrayList.subList(0, i2);
    }

    public static int getVolumeForApp(Context context) {
        try {
            return ((AudioManager) context.getSystemService("audio")).getStreamVolume(3);
        } catch (Exception e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    public static String getWifiApStatus(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            Class[] clsArr = new Class[0];
            return ((Integer) wifiManager.getClass().getDeclaredMethod("getWifiApState", null).invoke(wifiManager, null)).intValue() == ((Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED").get(wifiManager)).intValue() ? SleepModeControllerNew.SLEEP_MODE_ON : "off";
        } catch (Exception e2) {
            e2.printStackTrace();
            return "off";
        }
    }

    public static int getWifiSignal(Context context) {
        try {
            return ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getRssi();
        } catch (Exception e2) {
            e2.printStackTrace();
            return -127;
        }
    }

    public static boolean isEmptyString(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isTestMode() {
        try {
            return SystemProperties.getBoolean("persist.sys.thermalinfo.test", false);
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean isTorchState(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), ScenarioCollectorUtils.KEY_TORCH_STATE, 0) != 0;
    }

    public static boolean isUnReleased() {
        return SystemProperties.getBoolean("ro.mi.development", false);
    }

    private static int readMultiThermalTemp(int i2) throws Throwable {
        StringBuilder sb;
        int i3 = 0;
        if (i2 < 0) {
            return 0;
        }
        BufferedReader bufferedReader = null;
        try {
            try {
                File file = new File(THERM_PATH + i2 + "/temp");
                if (file.exists()) {
                    StringBuilder sb2 = new StringBuilder();
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    try {
                        for (String line = bufferedReader2.readLine(); line != null; line = bufferedReader2.readLine()) {
                            sb2.append(line);
                        }
                        i3 = Integer.parseInt(sb2.toString());
                        if (sIsTestMode) {
                            Log.d(TAG, "thermal sensor" + i2 + " temp is " + i3);
                        }
                        bufferedReader = bufferedReader2;
                    } catch (FileNotFoundException e2) {
                        e = e2;
                        bufferedReader = bufferedReader2;
                        Log.e(TAG, "readMultiThermalTemp FileNotFoundException" + e);
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e3) {
                                e = e3;
                                sb = new StringBuilder();
                                sb.append("readMultiThermalTemp IOException");
                                sb.append(e);
                                Log.e(TAG, sb.toString());
                            }
                        }
                    } catch (IOException e4) {
                        e = e4;
                        bufferedReader = bufferedReader2;
                        Log.e(TAG, "readMultiThermalTemp IOException" + e);
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e5) {
                                e = e5;
                                sb = new StringBuilder();
                                sb.append("readMultiThermalTemp IOException");
                                sb.append(e);
                                Log.e(TAG, sb.toString());
                            }
                        }
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e6) {
                                Log.e(TAG, "readMultiThermalTemp IOException" + e6);
                            }
                        }
                        throw th;
                    }
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e7) {
                        e = e7;
                        sb = new StringBuilder();
                        sb.append("readMultiThermalTemp IOException");
                        sb.append(e);
                        Log.e(TAG, sb.toString());
                    }
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (FileNotFoundException e8) {
            e = e8;
        } catch (IOException e9) {
            e = e9;
        }
        return i3 / 1000;
    }

    public static void execShellCommand(String str, String str2, int i2) {
        if (isEmptyString(str) || isEmptyString(str2)) {
            return;
        }
        Process processExec = null;
        try {
            try {
                processExec = Runtime.getRuntime().exec(str);
                processExec.waitFor(i2, TimeUnit.SECONDS);
                FileUtil.copyToFile(processExec.getInputStream(), new File(str2));
            } catch (Exception e2) {
                Log.e(TAG, "// Exception from " + str + " : " + e2.getMessage());
                if (processExec == null) {
                    return;
                }
            }
            processExec.destroy();
        } catch (Throwable th) {
            if (processExec != null) {
                processExec.destroy();
            }
            throw th;
        }
    }
}
