package com.miui.powerkeeper.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import miui.os.Build;
import miui.provider.ExtraSettings;
import miui.provider.MiCloudSmsCmd;
import miui.provider.Weather;
import miui.util.HashUtils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public abstract class Utils {
    public static final String SERVICE_NAME_POWER = "miui.whetstone.power";
    private static final String TAG = "Utils";
    private static final String URL_PRIVACY_POLICY = "http://www.miui.com/res/doc/privacy.html?region=%s&lang=%s";

    @Deprecated
    public static final Context APP_CONTEXT = PowerKeeperApplication.b();
    private static final int[] SYSTEM_CPU_FORMAT = {288, 8224, 8224, 8224, 8224, 8224, 8224, 8224};
    private static final int[] PROCESS_STATS_FORMAT = {32, 544, 32, 32, 32, 32, 32, 32, 32, 8224, 32, 8224, 32, 8224, 8224};
    static final Set<String> EU = new HashSet(Arrays.asList("AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", "DE", "EL", "GR", "HU", "IS", "IE", "IT", "LV", "LI", "LT", "LU", "MT", "NL", "NO", "PL", "PT", "RO", "SK", "SI", "ES", "SE", "UK", "GB"));

    public static String MD5(String str) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] bytes = str.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
            messageDigest.update(bytes);
            byte[] bArrDigest = messageDigest.digest();
            char[] cArr2 = new char[bArrDigest.length * 2];
            int i2 = 0;
            for (byte b2 : bArrDigest) {
                int i3 = i2 + 1;
                cArr2[i2] = cArr[(b2 >>> 4) & 15];
                i2 += 2;
                cArr2[i3] = cArr[b2 & 15];
            }
            return new String(cArr2);
        } catch (Exception e2) {
            Log.e(TAG, "Generate md5 value failed." + e2.getMessage());
            return "";
        }
    }

    static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception unused) {
            }
        }
    }

    public static String genPrivacyUlr() {
        return String.format(URL_PRIVACY_POLICY, Build.getRegion(), Locale.getDefault().toString());
    }

    /* JADX WARN: Code duplicated, block: B:65:0x0085 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:74:0x008f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static List<String> getAllProcessInfoFromCpuInfo() throws Throwable {
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        IOException e2;
        ArrayList arrayList = new ArrayList();
        try {
            Process processExec = Runtime.getRuntime().exec("dumpsys cpuinfo");
            BufferedReader bufferedReader2 = null;
            try {
                inputStreamReader = new InputStreamReader(processExec.getInputStream());
                try {
                    bufferedReader = new BufferedReader(inputStreamReader);
                    while (true) {
                        try {
                            try {
                                String line = bufferedReader.readLine();
                                if (line == null) {
                                    try {
                                        break;
                                    } catch (IOException e3) {
                                        Log.e(TAG, "close file stream error", e3);
                                    }
                                } else if (line.contains("%") && !line.contains("iowait") && !line.contains("    ")) {
                                    arrayList.add(line);
                                }
                            } catch (IOException e4) {
                                e2 = e4;
                                Log.e(TAG, "io error when read stream", e2);
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e5) {
                                        Log.e(TAG, "close file stream error", e5);
                                    }
                                }
                                if (inputStreamReader != null) {
                                    try {
                                        inputStreamReader.close();
                                    } catch (IOException e6) {
                                        e = e6;
                                        Log.e(TAG, "close file stream error", e);
                                    }
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader2 = bufferedReader;
                            if (bufferedReader2 != null) {
                                try {
                                    bufferedReader2.close();
                                } catch (IOException e7) {
                                    Log.e(TAG, "close file stream error", e7);
                                }
                            }
                            if (inputStreamReader != null) {
                                try {
                                    inputStreamReader.close();
                                } catch (IOException e8) {
                                    Log.e(TAG, "close file stream error", e8);
                                }
                            }
                            processExec.destroy();
                            throw th;
                        }
                    }
                    bufferedReader.close();
                    try {
                        inputStreamReader.close();
                    } catch (IOException e9) {
                        e = e9;
                        Log.e(TAG, "close file stream error", e);
                    }
                } catch (IOException e10) {
                    bufferedReader = null;
                    e2 = e10;
                } catch (Throwable th2) {
                    th = th2;
                    if (bufferedReader2 != null) {
                        bufferedReader2.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                    processExec.destroy();
                    throw th;
                }
            } catch (IOException e11) {
                bufferedReader = null;
                e2 = e11;
                inputStreamReader = null;
            } catch (Throwable th3) {
                th = th3;
                inputStreamReader = null;
            }
            processExec.destroy();
            return arrayList;
        } catch (IOException e12) {
            Log.e(TAG, "can't exec the cmd ", e12);
            return arrayList;
        }
    }

    public static int getArrayMaxMember(int[] iArr, int i2) {
        if (iArr != null && iArr.length > 0) {
            i2 = iArr[0];
            for (int i3 = 1; i3 < iArr.length; i3++) {
                int i4 = iArr[i3];
                if (i4 > i2) {
                    i2 = i4;
                }
            }
        }
        return i2;
    }

    public static int getBatteryVoltage(Context context) {
        Intent intentRegisterReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4);
        if (intentRegisterReceiver == null) {
            return 0;
        }
        return intentRegisterReceiver.getIntExtra("voltage", 0);
    }

    public static List<Integer> getBlueToothType(Context context) {
        ArrayList arrayList = new ArrayList();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService("bluetooth");
        return bluetoothManager != null ? (List) bluetoothManager.getConnectedDevices(7).stream().map(new Function() { // from class: com.miui.powerkeeper.utils.e
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((BluetoothDevice) obj).getType());
            }
        }).collect(Collectors.toList()) : arrayList;
    }

    public static long getCpuTime() {
        long[] jArr = new long[7];
        if (!Process.readProcFile("/proc/stat", SYSTEM_CPU_FORMAT, null, jArr, null)) {
            return -1L;
        }
        long j2 = jArr[0];
        long j3 = jArr[1];
        long j4 = jArr[2];
        long j5 = jArr[3];
        long j6 = jArr[4];
        return j2 + j3 + j4 + j5 + j6 + jArr[5] + jArr[6];
    }

    public static int getCurBatteryLevel(Context context) {
        Intent intentRegisterReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4);
        if (intentRegisterReceiver == null) {
            return 0;
        }
        return intentRegisterReceiver.getIntExtra("level", 0);
    }

    public static int getCurTemp(Context context) {
        Intent intentRegisterReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4);
        if (intentRegisterReceiver == null) {
            return 0;
        }
        return intentRegisterReceiver.getIntExtra(Weather.WeatherBaseColumns.TEMPERATURE, 0);
    }

    public static String getCurrentFormatTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public static String getFileMD5(File file) {
        String absolutePath = file.getAbsolutePath();
        try {
            absolutePath = absolutePath + Files.readAttributes(file.toPath(), (Class<BasicFileAttributes>) BasicFileAttributes.class, new LinkOption[0]).lastModifiedTime().toString();
        } catch (Exception e2) {
            Log.e(TAG, "Generate file md5 value failed." + e2.getMessage());
        }
        return MD5(absolutePath);
    }

    public static int getInt(String str, int i2) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Class cls2 = Integer.TYPE;
            return ((Integer) ReflectionUtils.callStaticObjectMethod(cls, cls2, "getInt", new Class[]{String.class, cls2}, str, Integer.valueOf(i2))).intValue();
        } catch (Exception e2) {
            Log.e(TAG, "SystemPropertiesUtils getInt: ", e2);
            return 0;
        }
    }

    public static String getJsonFromAssert(Context context, String str) throws Throwable {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            try {
                try {
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(context.getAssets().open(str)));
                    while (true) {
                        try {
                            String line = bufferedReader2.readLine();
                            if (line == null) {
                                break;
                            }
                            sb.append(line);
                        } catch (IOException e2) {
                            e = e2;
                            bufferedReader = bufferedReader2;
                            e.printStackTrace();
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                            return sb.toString();
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader = bufferedReader2;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e3) {
                                    e3.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    }
                    bufferedReader2.close();
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException e4) {
                e = e4;
            }
        } catch (IOException e5) {
            e5.printStackTrace();
        }
        return sb.toString();
    }

    public static String getLastCharacters(String str, int i2) {
        if (str == null) {
            return "null";
        }
        int length = str.length();
        return length <= i2 ? str : str.substring(length - i2, length);
    }

    public static long getProcessCpuTime(int i2) {
        long[] jArr = new long[4];
        if (Process.readProcFile("/proc/" + i2 + "/stat", PROCESS_STATS_FORMAT, null, jArr, null)) {
            return jArr[2] + jArr[3];
        }
        return -1L;
    }

    static String getRegion() {
        return SystemProperties.get("ro.miui.region", "unknown");
    }

    public static int getScreenOffPowerConsumed() {
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        if (batteryStats == null) {
            return -1;
        }
        try {
            return batteryStats.getDischargeAmountScreenOffSinceCharge();
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
            return 0;
        }
    }

    public static String getSeparatedStringAt(String str, char c2, int i2) {
        if (str == null || i2 < 0) {
            return null;
        }
        int length = str.length();
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < length; i5++) {
            if (str.charAt(i5) == c2) {
                i3++;
                if (i3 != i2) {
                    if (i3 == i2 + 1) {
                        length = i5;
                        break;
                    }
                } else {
                    i4 = i5 + 1;
                }
            }
        }
        if (i2 > i3) {
            return null;
        }
        return str.substring(i4, length);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v11 */
    /* JADX WARN: Type inference failed for: r4v12 */
    /* JADX WARN: Type inference failed for: r4v13 */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v4, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7, types: [int] */
    /* JADX WARN: Type inference failed for: r4v8 */
    /* JADX WARN: Type inference failed for: r4v9 */
    public static String getTopNProcessInfoFromCpuInfo(int i2) throws Throwable {
        InputStreamReader inputStreamReader;
        String[] strArr = new String[i2];
        try {
            Process processExec = Runtime.getRuntime().exec("dumpsys cpuinfo");
            ?? r4 = 0;
            r4 = 0;
            r4 = 0;
            r4 = 0;
            try {
                try {
                    inputStreamReader = new InputStreamReader(processExec.getInputStream());
                    try {
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        r4 = 0;
                        while (true) {
                            try {
                                String line = bufferedReader.readLine();
                                if (line == null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e2) {
                                        Log.e(TAG, "close file stream error", e2);
                                    }
                                    try {
                                        inputStreamReader.close();
                                        break;
                                    } catch (IOException e3) {
                                        e = e3;
                                        Log.e(TAG, "close file stream error", e);
                                    }
                                } else if (line.contains("%") && !line.contains("iowait") && !line.contains("    ") && !line.contains("   +")) {
                                    if (r4 >= i2) {
                                        String string = Arrays.toString(strArr);
                                        try {
                                            bufferedReader.close();
                                        } catch (IOException e4) {
                                            Log.e(TAG, "close file stream error", e4);
                                        }
                                        try {
                                            inputStreamReader.close();
                                        } catch (IOException e5) {
                                            Log.e(TAG, "close file stream error", e5);
                                        }
                                        processExec.destroy();
                                        return string;
                                    }
                                    strArr[r4] = line.trim();
                                    r4++;
                                }
                            } catch (IOException e6) {
                                e = e6;
                                r4 = bufferedReader;
                                Log.e(TAG, "io error when read stream", e);
                                if (r4 != 0) {
                                    try {
                                        r4.close();
                                    } catch (IOException e7) {
                                        Log.e(TAG, "close file stream error", e7);
                                    }
                                }
                                if (inputStreamReader != null) {
                                    try {
                                        inputStreamReader.close();
                                    } catch (IOException e8) {
                                        e = e8;
                                        Log.e(TAG, "close file stream error", e);
                                    }
                                }
                            } catch (Throwable th) {
                                th = th;
                                r4 = bufferedReader;
                                if (r4 != 0) {
                                    try {
                                        r4.close();
                                    } catch (IOException e9) {
                                        Log.e(TAG, "close file stream error", e9);
                                    }
                                }
                                if (inputStreamReader != null) {
                                    try {
                                        inputStreamReader.close();
                                    } catch (IOException e10) {
                                        Log.e(TAG, "close file stream error", e10);
                                    }
                                }
                                processExec.destroy();
                                throw th;
                            }
                        }
                    } catch (IOException e11) {
                        e = e11;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException e12) {
                e = e12;
                inputStreamReader = null;
            } catch (Throwable th3) {
                th = th3;
                inputStreamReader = null;
            }
            processExec.destroy();
            return Arrays.toString(strArr);
        } catch (IOException e13) {
            Log.e(TAG, "can't exec the cmd ", e13);
            return Arrays.toString(strArr);
        }
    }

    public static String getVersionName(Context context, String str) {
        try {
            if (!PackageUtil.checkPackageInstalled(context, str)) {
                Log.i(TAG, "getVersionName not installed");
                return "";
            }
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 0);
            Log.i(TAG, "packageInfo.versionName:" + packageInfo.versionName);
            return packageInfo.versionName;
        } catch (Exception e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static void increaseStartTimes() {
        int i2 = SystemProperties.getInt(Constant.PROP_START_TIMES, 0) + 1;
        SystemProperties.set(Constant.PROP_START_TIMES, Integer.toString(i2));
        if (i2 > 1) {
            Slog.w(TAG, "increaseStartTimes current=" + i2);
        }
    }

    public static boolean isBelongToDeviceSeries(String str) {
        if (str == null) {
            return false;
        }
        String str2 = android.os.Build.DEVICE;
        if (str2.equals(str)) {
            return true;
        }
        if (str2.equals(str + "in")) {
            return true;
        }
        if (str2.equals(str + "pro")) {
            return true;
        }
        if (str2.equals(str + "inpro")) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(MiCloudSmsCmd.TYPE_NOISE);
        return str2.equals(sb.toString());
    }

    public static boolean isBlueToothConnectedOrConnecting(Context context) {
        int connectionState;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled() && ((connectionState = defaultAdapter.getConnectionState()) == 2 || connectionState == 1)) {
            return true;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService("bluetooth");
        if (bluetoothManager == null) {
            return false;
        }
        List<BluetoothDevice> connectedDevices = bluetoothManager.getConnectedDevices(7);
        if (connectedDevices == null || connectedDevices.size() <= 0) {
            return (bluetoothManager.getConnectedDevices(8) == null || connectedDevices == null || connectedDevices.size() <= 0) ? false : true;
        }
        return true;
    }

    public static boolean isCharging(Context context) {
        Intent intentRegisterReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4);
        if (intentRegisterReceiver == null) {
            return false;
        }
        int intExtra = intentRegisterReceiver.getIntExtra("status", -1);
        if (intExtra == 2) {
            return true;
        }
        return intExtra == 5 && (intentRegisterReceiver.getIntExtra("plugged", -1) & 15) != 0;
    }

    public static boolean isCloudControlAllowed(Context context) {
        return SimpleSettings.Misc.getBoolean(context, SimpleSettingKeys.KEY_UPLOAD_LOG, !Build.IS_INTERNATIONAL_BUILD);
    }

    public static boolean isContainedWithSeparator(String str, String str2, char c2) {
        int length;
        int i2;
        if (str == null || str2 == null || (length = str2.length()) == 0) {
            return false;
        }
        int length2 = str.length();
        int iIndexOf = str.indexOf(str2, 0);
        while (iIndexOf >= 0 && iIndexOf <= length2 - length) {
            if ((iIndexOf == 0 || str.charAt(iIndexOf - 1) == c2) && ((i2 = iIndexOf + length) == length2 || str.charAt(i2) == c2)) {
                return true;
            }
            iIndexOf = str.indexOf(str2, iIndexOf + 1);
        }
        return false;
    }

    public static boolean isDeepSleepModeCloudEnabled(Context context) {
        return SimpleSettings.Misc.getBooleanForUser(context, Constant.FUNC_DEEP_SLEEP_MODE_CLOUD, true, 0);
    }

    public static boolean isDevelopmentOrDebugVersion() {
        return SystemProperties.getBoolean("ro.mi.development", false) || Build.IS_DEBUGGABLE;
    }

    public static boolean isExtendedTablet() {
        return isTablet() || isFoldDevice();
    }

    private static boolean isFoldDevice() {
        return getInt("persist.sys.muiltdisplay_type", 0) == 2;
    }

    private static boolean isFoldExternalScreen(Context context) {
        Configuration configuration;
        return context != null && (configuration = context.getResources().getConfiguration()) != null && isFoldDevice() && (configuration.screenLayout & 15) == 2;
    }

    public static boolean isInEURegion() {
        String region = getRegion();
        if (TextUtils.isEmpty(region) || TextUtils.equals(region, "unknown")) {
            return true;
        }
        return EU.contains(region.toUpperCase());
    }

    public static boolean isNfcSupported(Context context) {
        return NfcAdapter.getDefaultAdapter(context) != null;
    }

    public static boolean isNumber(String str) {
        if (str == null || "".equals(str.trim())) {
            return false;
        }
        return str.matches("^[0-9]*$");
    }

    public static boolean isPrivacyEnable(Context context) {
        return MiuiSettings.Privacy.isEnabled(context, context.getPackageName());
    }

    public static boolean isSleepModeEnabled(Context context) {
        return SimpleSettings.Misc.getStringForUser(context, Constant.FUNC_SLEEP_MODE_USER, SleepModeControllerNew.SLEEP_MODE_ON, 0).equals(SleepModeControllerNew.SLEEP_MODE_ON);
    }

    public static boolean isSleepModeViewVisable(boolean z2) {
        if (!z2) {
            Log.d("power.sleep", "it don't check region");
            return true;
        }
        if (!Build.IS_INTERNATIONAL_BUILD) {
            Log.d("power.sleep", "cn devices will return ture !");
            return true;
        }
        if (Build.checkRegion("IN") || "IN".equalsIgnoreCase(SystemProperties.get("ro.miui.build.region"))) {
            Log.d("power.sleep", "indian device will support sleep mode by default!!! ");
            return true;
        }
        if (isBelongToDeviceSeries("camellia")) {
            Log.d("power.sleep", "it is Camellia device!!! ");
            return true;
        }
        Log.d("power.sleep", "international don't support sleep mode!!!");
        return false;
    }

    public static boolean isSystemServiceExists(String str) {
        if (str == null || ServiceManager.getService(str) == null) {
            if (!PowerLog.V) {
                return false;
            }
            PowerLog.v(TAG, "isSystemServiceExists return false");
            return false;
        }
        if (!PowerLog.V) {
            return true;
        }
        PowerLog.v(TAG, "isSystemServiceExists return true");
        return true;
    }

    private static boolean isTablet() {
        return Build.IS_TABLET;
    }

    public static boolean isTabletSpitModel(Activity activity) {
        Intent intent;
        return (activity == null || isFoldExternalScreen(activity.getApplicationContext()) || (intent = activity.getIntent()) == null || !isExtendedTablet() || (((Integer) ReflectionUtils.callObjectMethodCall(TAG, intent, Integer.TYPE, "getMiuiFlags", null, new Object[0])).intValue() & 4) == 0) ? false : true;
    }

    public static boolean isUserAllowed(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), ExtraSettings.Secure.UPLOAD_LOG, 0) > 0;
    }

    public static boolean isUserExperienceAndPrivacyAllowed(Context context) {
        return SimpleSettings.Misc.getBoolean(context, SimpleSettingKeys.KEY_UPLOAD_LOG, Build.IS_INTERNATIONAL_BUILD ^ true) && Settings.Secure.getInt(context.getContentResolver(), ExtraSettings.Secure.UPLOAD_LOG, 0) > 0 && MiuiSettings.Privacy.isEnabled(context, context.getPackageName());
    }

    public static boolean parseRemoteResultToFile(String str, String str2) {
        try {
            return FileUtil.writeToFile(str, new JSONObject(new JSONObject(str2).getString("data")).toString(2));
        } catch (Exception e2) {
            Log.e(TAG, "parseRemoteResultToFile", e2);
            return false;
        }
    }

    public static boolean pkgHasIcon(Context context, String str) {
        return context.getPackageManager().getLaunchIntentForPackage(str) != null;
    }

    public static void protectCurrentProcess(boolean z2) {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        Class<?> clsLoadClassNoThrow = ReflectionUtils.loadClassNoThrow(systemClassLoader, "miui.process.ProcessManager");
        if (clsLoadClassNoThrow != null) {
            ReflectionUtils.callStaticMethod(clsLoadClassNoThrow, "protectCurrentProcess", Boolean.valueOf(z2));
            return;
        }
        Class<?> clsLoadClassNoThrow2 = ReflectionUtils.loadClassNoThrow(systemClassLoader, "com.miui.whetstone.WhetstoneActivityManager");
        if (clsLoadClassNoThrow2 != null) {
            if (z2) {
                ReflectionUtils.callStaticMethod(clsLoadClassNoThrow2, "promoteApplicationLevel", 2);
            } else {
                ReflectionUtils.callStaticMethod(clsLoadClassNoThrow2, "releaseApplicationPromoteLevel", new Object[0]);
            }
        }
    }

    public static String timestamp2String(long j2) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss").format(new Date(j2));
    }

    public static int toInt(String str, int i2) {
        if (str != null) {
            try {
                if (!str.isEmpty()) {
                    return Integer.parseInt(str);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return i2;
    }

    public static String toString(String[] strArr) {
        if (strArr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            sb.append(str);
            sb.append(" ");
        }
        return sb.toString();
    }
}
