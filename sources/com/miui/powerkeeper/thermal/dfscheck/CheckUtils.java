package com.miui.powerkeeper.thermal.dfscheck;

import android.util.Log;
import com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class CheckUtils implements ThermalDfsInterface.ntcRegister, ThermalDfsInterface.deviceRegister {
    private static final String TAG = "powerkeeper.dfsutils";
    private static CheckUtils mCheckUtilsInstance = null;
    private static String mNtcAndDeviceHeadPath = "/sys/class/thermal";
    private static String mThermaldDevicesPath = "/vendor/etc/thermald-devices.conf";
    public int mTransError = -100000;
    public Long mLongTransError = -900000L;
    public int mLeveltransError = -1;
    ArrayList<String> mArrThermalNTC = new ArrayList<>(Arrays.asList("cam_therm0", "cam_therm1", "pa_therm0", "pa_therm1", "conn_therm", "slave_therm", "modem1_pa0", "modem1_pa1", "modem1_quiet", "modem1_wifi", "wireless_therm", "flash_therm", "charger_therm0", "charger_therm1", "wifi_therm", "cpu_therm", "xo-therm", "quiet_therm", "backlight_therm", "mtktsAP", "mtktsbtsmdpa", "mtktsbtsnrpa", "mtktsbattery", "mtktscpu"));
    private HashMap<String, String> mNtcMap = new HashMap<>();
    private HashMap<String, String> mDeviceMap = new HashMap<>();
    private HashMap<String, String> mCoolingDeviceMap = new HashMap<>();

    private CheckUtils() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v23 */
    /* JADX WARN: Type inference failed for: r2v24 */
    /* JADX WARN: Type inference failed for: r2v25 */
    /* JADX WARN: Type inference failed for: r2v26 */
    /* JADX WARN: Type inference failed for: r2v27 */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r2v8 */
    private void addDeviceMap() throws Throwable {
        StringBuilder sb;
        File file = new File(mThermaldDevicesPath);
        if (!file.exists()) {
            Log.e(TAG, "thermald devices is not found");
            return;
        }
        ?? r2 = 0;
        String strTrim = null;
        BufferedReader bufferedReader = null;
        BufferedReader bufferedReader2 = null;
        try {
            try {
                BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                loop0: while (true) {
                    boolean z2 = false;
                    while (true) {
                        try {
                            String line = bufferedReader3.readLine();
                            if (line == null) {
                                try {
                                    break loop0;
                                } catch (IOException e2) {
                                    e = e2;
                                    sb = new StringBuilder();
                                    sb.append("getMultiThermalIndex IOException");
                                    sb.append(e);
                                    Log.e(TAG, sb.toString());
                                    return;
                                }
                            }
                            if (line.contains("name:") && !line.contains("cooling_name:") && !z2) {
                                String[] strArrSplit = line.split(":");
                                strTrim = strTrim;
                                if (strArrSplit.length > 1) {
                                    strTrim = strArrSplit[1].trim();
                                }
                                z2 = true;
                            } else if (line.contains("path:") && z2) {
                                String[] strArrSplit2 = line.split(":");
                                if (strArrSplit2.length > 1) {
                                    addDeviceInMap(strTrim, strArrSplit2[1]);
                                    break;
                                }
                            } else if (line.contains("cooling_name:") && z2) {
                                String[] strArrSplit3 = line.split(":");
                                if (strArrSplit3.length > 1) {
                                    if (this.mCoolingDeviceMap.get(strArrSplit3[1]) != null) {
                                        addDeviceInMap(strTrim, this.mCoolingDeviceMap.get(strArrSplit3[1]));
                                        break;
                                    }
                                    break;
                                }
                            }
                        } catch (FileNotFoundException unused) {
                            bufferedReader = bufferedReader3;
                            Log.e(TAG, "open thermald devices is fail");
                            r2 = bufferedReader;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                    r2 = bufferedReader;
                                } catch (IOException e3) {
                                    e = e3;
                                    sb = new StringBuilder();
                                    sb.append("getMultiThermalIndex IOException");
                                    sb.append(e);
                                    Log.e(TAG, sb.toString());
                                    return;
                                }
                            }
                        } catch (IOException unused2) {
                            bufferedReader2 = bufferedReader3;
                            Log.e(TAG, "read thermald devices is fail");
                            r2 = bufferedReader2;
                            if (bufferedReader2 != null) {
                                try {
                                    bufferedReader2.close();
                                    r2 = bufferedReader2;
                                } catch (IOException e4) {
                                    e = e4;
                                    sb = new StringBuilder();
                                    sb.append("getMultiThermalIndex IOException");
                                    sb.append(e);
                                    Log.e(TAG, sb.toString());
                                    return;
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            r2 = bufferedReader3;
                            if (r2 != 0) {
                                try {
                                    r2.close();
                                } catch (IOException e5) {
                                    Log.e(TAG, "getMultiThermalIndex IOException" + e5);
                                }
                            }
                            throw th;
                        }
                    }
                }
                bufferedReader3.close();
                r2 = strTrim;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (FileNotFoundException unused3) {
        } catch (IOException unused4) {
        }
    }

    public static synchronized CheckUtils getCheckUtilsInstance() {
        try {
            if (mCheckUtilsInstance == null) {
                mCheckUtilsInstance = new CheckUtils();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mCheckUtilsInstance;
    }

    public static String getNtcAndDeviceHeadPath() {
        return mNtcAndDeviceHeadPath;
    }

    public void addDeviceInMap(String str, String str2) {
        synchronized (this.mDeviceMap) {
            this.mDeviceMap.put(str, str2);
        }
    }

    public HashMap<String, String> getDeviceMap() {
        return this.mDeviceMap;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v3, types: [java.io.BufferedReader] */
    public String getNtcAndDeviceName(String str) throws Throwable {
        Throwable th;
        String str2;
        FileNotFoundException e2;
        String str3 = "getMultiThermalIndex IOException";
        String line = null;
        line = null;
        BufferedReader bufferedReader = null;
        try {
            try {
                try {
                    File file = new File((String) str);
                    if (!file.exists()) {
                        return null;
                    }
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    try {
                        line = bufferedReader2.readLine();
                        bufferedReader2.close();
                        try {
                            bufferedReader2.close();
                        } catch (IOException e3) {
                            str3 = "getMultiThermalIndex IOException" + e3;
                            Log.e(TAG, str3);
                        }
                    } catch (FileNotFoundException e4) {
                        e2 = e4;
                    } catch (IOException e5) {
                        e = e5;
                        String str4 = line;
                        bufferedReader = bufferedReader2;
                        str2 = str4;
                        Log.e(TAG, "getNTCThermal IOException" + e);
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e6) {
                                str3 = "getMultiThermalIndex IOException" + e6;
                                Log.e(TAG, str3);
                            }
                        }
                        line = str2;
                    }
                    return line;
                } catch (Throwable th2) {
                    th = th2;
                    if (str != 0) {
                        try {
                            str.close();
                        } catch (IOException e7) {
                            Log.e(TAG, str3 + e7);
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException e8) {
                e2 = e8;
            } catch (IOException e9) {
                e = e9;
                str2 = null;
            }
            throw new RuntimeException(e2);
        } catch (Throwable th3) {
            String str5 = line;
            th = th3;
            str = str5;
        }
    }

    public HashMap<String, String> getNtcMap() {
        HashMap<String, String> map;
        synchronized (this.mNtcMap) {
            map = this.mNtcMap;
        }
        return map;
    }

    public Integer levelTransToInteger(String str, String str2) {
        String str3 = "Util:" + str;
        int i2 = this.mLeveltransError;
        if (str2 == null || str2.equals("")) {
            Log.e(str3, "can not tans " + str2 + " in " + str3 + "to int");
            return Integer.valueOf(i2);
        }
        try {
            return Integer.valueOf(Integer.parseInt(str2));
        } catch (NumberFormatException unused) {
            Log.e(str3, "can not tans " + str2 + " in " + str3 + "to int throw error");
            return Integer.valueOf(this.mLeveltransError);
        }
    }

    public boolean ntcMapIsEmpty() {
        boolean zIsEmpty;
        synchronized (this.mNtcMap) {
            zIsEmpty = this.mNtcMap.isEmpty();
        }
        return zIsEmpty;
    }

    public String printNtcAndDevice() {
        String str = "";
        synchronized (this.mNtcMap) {
            try {
                for (Map.Entry<String, String> entry : this.mNtcMap.entrySet()) {
                    str = str + entry.getKey() + ":" + entry.getValue() + ShellUtils.COMMAND_LINE_END;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        synchronized (this.mDeviceMap) {
            try {
                for (Map.Entry<String, String> entry2 : this.mDeviceMap.entrySet()) {
                    str = str + entry2.getKey() + ":" + entry2.getValue() + ShellUtils.COMMAND_LINE_END;
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
        return str;
    }

    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.deviceRegister
    public void registerDeviceInMap(String str, String str2) {
        synchronized (this.mDeviceMap) {
            try {
                if (!this.mDeviceMap.containsKey(str)) {
                    this.mDeviceMap.put(str, str2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerNtcAndDeviceMap() throws Throwable {
        if (!ntcMapIsEmpty()) {
            Log.d(TAG, "ntc map already find path");
            return;
        }
        Log.d(TAG, "add ntc from find");
        File file = new File(mNtcAndDeviceHeadPath);
        if (!file.exists()) {
            Log.e(TAG, "can not get thermal_message!");
            return;
        }
        File[] fileArrListFiles = file.listFiles();
        for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
            if (fileArrListFiles[i2].getName().contains("thermal_zone") && fileArrListFiles[i2].isDirectory()) {
                StringBuilder sb = new StringBuilder();
                sb.append(fileArrListFiles[i2].getPath());
                String str = File.separator;
                sb.append(str);
                sb.append("type");
                String ntcAndDeviceName = getNtcAndDeviceName(sb.toString());
                if (ntcAndDeviceName == null) {
                    Log.d(TAG, "can not get ntc name from " + fileArrListFiles[i2].isDirectory());
                } else if (this.mArrThermalNTC.contains(ntcAndDeviceName)) {
                    Log.d(TAG, "add ntc " + ntcAndDeviceName);
                    registerNtcInMap(ntcAndDeviceName, fileArrListFiles[i2].getPath() + str + "temp");
                }
            } else if (fileArrListFiles[i2].getName().contains("cooling_device") && fileArrListFiles[i2].isDirectory()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(fileArrListFiles[i2].getPath());
                String str2 = File.separator;
                sb2.append(str2);
                sb2.append("type");
                String ntcAndDeviceName2 = getNtcAndDeviceName(sb2.toString());
                if (ntcAndDeviceName2 == null) {
                    Log.d(TAG, "can not get ntc name from " + fileArrListFiles[i2].isDirectory());
                } else {
                    this.mCoolingDeviceMap.put(ntcAndDeviceName2, fileArrListFiles[i2].getPath() + str2 + "cur_state");
                }
            }
        }
        addDeviceMap();
    }

    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.ntcRegister
    public void registerNtcInMap(String str, String str2) {
        synchronized (this.mNtcMap) {
            try {
                if (!this.mNtcMap.containsKey(str)) {
                    this.mNtcMap.put(str, str2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public Boolean transToBoolean(String str, String str2) {
        if (str2 == null || str2.length() <= 0) {
            return null;
        }
        if (str2.equals("0") || str2.equals("false") || str2.equals("FALSE") || str2.equals("False")) {
            return Boolean.FALSE;
        }
        if (str2.equals("1") || str2.equals("true") || str2.equals("TRUE") || str2.equals("True")) {
            return Boolean.TRUE;
        }
        Log.e(str, "can not trans " + str2 + " int " + str + " to boolean");
        return null;
    }

    public Integer transToInteger(String str, String str2) {
        String str3 = "Util:" + str;
        int i2 = this.mTransError;
        if (str2 == null || str2.equals("")) {
            Log.e(str3, "can not tans " + str2 + " in " + str3 + "to int");
            return Integer.valueOf(i2);
        }
        try {
            return Integer.valueOf(Integer.parseInt(str2));
        } catch (NumberFormatException unused) {
            Log.e(str3, "can not tans " + str2 + " in " + str3 + "to int throw error");
            return Integer.valueOf(this.mTransError);
        }
    }

    public Long transToLong(String str, String str2) {
        String str3 = "Util:" + str;
        Long l2 = this.mLongTransError;
        if (str2 == null || str2.equals("")) {
            Log.e(str3, "can not tans " + str2 + " in " + str3 + "to long");
            return l2;
        }
        try {
            return Long.valueOf(Long.parseLong(str2));
        } catch (NumberFormatException unused) {
            Log.e(str3, "can not tans " + str2 + " in " + str3 + "to int throw error");
            return this.mLongTransError;
        }
    }

    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.deviceRegister
    public void unRegisterDeviceInMap(String str) {
        synchronized (this.mDeviceMap) {
            try {
                if (this.mDeviceMap.containsKey(str)) {
                    this.mDeviceMap.remove(str);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.ntcRegister
    public void unRegisterNtcInMap(String str) {
        synchronized (this.mNtcMap) {
            try {
                if (this.mNtcMap.containsKey(str)) {
                    this.mNtcMap.remove(str);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
