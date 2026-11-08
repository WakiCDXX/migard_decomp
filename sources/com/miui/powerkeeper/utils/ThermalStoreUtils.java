package com.miui.powerkeeper.utils;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.cloudcontrol.HttpUtils;
import com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.ThermalSwitchHandler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ThermalStoreUtils {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String DEFAULT_CONFIG_FILE_PATH = "/data/thermal/";
    private static final String DEFAULT_GLOBAL_MODE_FILE_PATH = "/data/vendor/thermal/";
    private static final String PROP_THERMAL_CONFIG = "persist.sys.thermal.config";
    private static final String PROP_THERMAL_FILE_PATH = "sys.thermal.data.path";
    private static final String PROP_THERMAL_FILE_PATH_NEW = "vendor.sys.thermal.data.path";
    private static final String TAG = "ThermalStoreUtils";
    private static final String THERMAL_COMMINICATE_CONFIG = "/sys/class/thermal/thermal_message/sconfig";
    public static final int THERMAL_DEFALUT_INT = 0;
    public static final String THERMAL_DEFALUT_STRING = "default";
    public static final String THERMAL_DEFALUT_ZERO_STRING = "0";
    public static final int THERMAL_PERFARMANCE_INT = 1;
    public static final String THERMAL_PERFARMANCE_ONE_STRING = "1";
    public static final String THERMAL_PERFARMANCE_STRING = "performance";

    private static String checkSlip(String str, String str2) throws IllegalArgumentException {
        try {
            if (!new File(str2, str).getCanonicalPath().startsWith(str2)) {
                throw new IllegalArgumentException("New file is outside of the parent dir: " + str);
            }
        } catch (IOException e2) {
            Log.i("checkSlip", "exception = " + e2.toString());
        }
        return str;
    }

    public static boolean checkThermalConfigReady() {
        return FileUtil.checkConfigReady(TAG, getThermalConfigFilePath());
    }

    /* JADX INFO: Removed unreachable split cross block B:35:0x007b */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v12 */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v3, types: [java.io.Writer] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v9 */
    public static void comunicateWithNative(int i2, boolean z2) throws Throwable {
        if (z2) {
            ThermalSwitchHandler.getInstance().onPerformanceEnabled(i2);
            SystemProperties.set(PROP_THERMAL_CONFIG, Integer.toString(i2));
            storeThermalGlobalMode(i2);
        }
        if (!FileUtil.checkFileReady(TAG, THERMAL_COMMINICATE_CONFIG)) {
            socketCommunicate(i2);
            return;
        }
        ?? r1 = 0;
        FileWriter fileWriter = null;
        r1 = 0;
        try {
            try {
                try {
                    FileWriter fileWriter2 = new FileWriter(THERMAL_COMMINICATE_CONFIG, false);
                    try {
                        fileWriter2.write(Integer.toString(i2));
                        StringBuilder sb = new StringBuilder();
                        String str = "fileWrite:";
                        sb.append("fileWrite:");
                        sb.append(i2);
                        Log.e(TAG, sb.toString());
                        fileWriter2.close();
                        r1 = str;
                    } catch (Exception e2) {
                        e = e2;
                        fileWriter = fileWriter2;
                        Log.e(TAG, "thermalComunicateException:" + e);
                        r1 = fileWriter;
                        if (fileWriter != null) {
                            fileWriter.close();
                            r1 = fileWriter;
                        }
                    } catch (Throwable th) {
                        th = th;
                        r1 = fileWriter2;
                        if (r1 != 0) {
                            try {
                                r1.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e4) {
                e = e4;
            }
        } catch (IOException e5) {
            e5.printStackTrace();
        }
    }

    /* JADX WARN: Code duplicated, block: B:66:0x0094 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:73:0x008a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:85:? A[SYNTHETIC] */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x003d, code lost:
    
        r2 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x003e, code lost:
    
        android.util.Log.e(com.miui.powerkeeper.utils.ThermalStoreUtils.TAG, "confirmZipPath File IOException", r2);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean confirmPath(java.lang.String r8) throws java.lang.Throwable {
        /*
            java.lang.String r0 = "confirmZipPath Zip IOException"
            java.lang.String r1 = "confirmZipPath File IOException"
            java.io.File r2 = new java.io.File
            r2.<init>(r8)
            boolean r2 = r2.exists()
            r3 = 0
            java.lang.String r4 = "ThermalStoreUtils"
            if (r2 != 0) goto L18
            java.lang.String r8 = "file is not exist!"
            android.util.Log.i(r4, r8)
            return r3
        L18:
            r2 = 0
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6e
            r5.<init>(r8)     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6e
            java.util.zip.ZipInputStream r8 = new java.util.zip.ZipInputStream     // Catch: java.lang.Throwable -> L5f java.lang.Exception -> L64
            java.lang.String r6 = "GBK"
            java.nio.charset.Charset r6 = java.nio.charset.Charset.forName(r6)     // Catch: java.lang.Throwable -> L5f java.lang.Exception -> L64
            r8.<init>(r5, r6)     // Catch: java.lang.Throwable -> L5f java.lang.Exception -> L64
        L29:
            java.util.zip.ZipEntry r2 = r8.getNextEntry()     // Catch: java.lang.Throwable -> L4a java.lang.Exception -> L4c
            if (r2 == 0) goto L4e
            java.lang.String r6 = getConfigFileSavePath(r3)     // Catch: java.lang.Throwable -> L4a java.lang.Exception -> L4c
            boolean r2 = confirmPerPath(r2, r6)     // Catch: java.lang.Throwable -> L4a java.lang.Exception -> L4c
            if (r2 != 0) goto L29
            r5.close()     // Catch: java.io.IOException -> L3d
            goto L41
        L3d:
            r2 = move-exception
            android.util.Log.e(r4, r1, r2)
        L41:
            r8.close()     // Catch: java.io.IOException -> L45
            goto L49
        L45:
            r8 = move-exception
            android.util.Log.e(r4, r0, r8)
        L49:
            return r3
        L4a:
            r2 = move-exception
            goto L88
        L4c:
            r2 = move-exception
            goto L72
        L4e:
            r5.close()     // Catch: java.io.IOException -> L52
            goto L56
        L52:
            r2 = move-exception
            android.util.Log.e(r4, r1, r2)
        L56:
            r8.close()     // Catch: java.io.IOException -> L5a
            goto L86
        L5a:
            r8 = move-exception
            android.util.Log.e(r4, r0, r8)
            goto L86
        L5f:
            r8 = move-exception
            r7 = r2
            r2 = r8
            r8 = r7
            goto L88
        L64:
            r8 = move-exception
            r7 = r2
            r2 = r8
            r8 = r7
            goto L72
        L69:
            r8 = move-exception
            r5 = r2
            r2 = r8
            r8 = r5
            goto L88
        L6e:
            r8 = move-exception
            r5 = r2
            r2 = r8
            r8 = r5
        L72:
            java.lang.String r3 = "confirmZipPath Exception"
            android.util.Log.e(r4, r3, r2)     // Catch: java.lang.Throwable -> L4a
            if (r5 == 0) goto L81
            r5.close()     // Catch: java.io.IOException -> L7d
            goto L81
        L7d:
            r2 = move-exception
            android.util.Log.e(r4, r1, r2)
        L81:
            if (r8 == 0) goto L86
            r8.close()     // Catch: java.io.IOException -> L5a
        L86:
            r8 = 1
            return r8
        L88:
            if (r5 == 0) goto L92
            r5.close()     // Catch: java.io.IOException -> L8e
            goto L92
        L8e:
            r3 = move-exception
            android.util.Log.e(r4, r1, r3)
        L92:
            if (r8 == 0) goto L9c
            r8.close()     // Catch: java.io.IOException -> L98
            goto L9c
        L98:
            r8 = move-exception
            android.util.Log.e(r4, r0, r8)
        L9c:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powerkeeper.utils.ThermalStoreUtils.confirmPath(java.lang.String):boolean");
    }

    private static boolean confirmPerPath(ZipEntry zipEntry, String str) {
        if (!zipEntry.isDirectory()) {
            String strSubstring = checkSlip(zipEntry.getName(), str).substring(zipEntry.getName().lastIndexOf(47) + 1);
            if (!"".equals(strSubstring) || strSubstring.length() <= 255 || strSubstring.lastIndexOf(".xml") == strSubstring.length() - 4 || strSubstring.lastIndexOf(".conf") == strSubstring.length() - 5) {
                return true;
            }
        } else if ("config/".equals(zipEntry.getName().substring(0, zipEntry.getName().lastIndexOf(47) + 1))) {
            Log.i(TAG, "dir is config!");
            return true;
        }
        Log.i(TAG, "dir or file name is illegal!");
        return false;
    }

    public static void downLoadConfigureDefault(Context context, boolean z2) throws Throwable {
        String strHttpGet;
        boolean z3;
        Log.i(TAG, "downLoadConfigureDefault");
        if (LocalUpdateUtils.useNewCloudControl()) {
            strHttpGet = HttpUtils.httpGet(Constants.CLOUD_NEW_SERVER + Constants.CLOUD_NEW_SERVER_UUID + LocalUpdateUtils.getUUID(context) + Constants.CLOUD_NEW_SERVER_UPID + LocalUpdateUtils.getUPID(context, Constants.CLOUD_THERMAL_CONFIG_PATH_UPID, z2) + Constants.CLOUD_NEW_SERVER_REGION + Constants.REGION_NAME_THERMAL + Constants.CLOUD_NEW_SERVER_BUCKET + Constants.BUCKET_NAME_THERMAL_CONFIG_PATH + Constants.CLOUD_NEW_SERVER_TYPE + Device.getInstance().getType(context), Constants.CLOUD_THERMAL_NEW_SID);
            if (strHttpGet == null || strHttpGet.isEmpty()) {
                Log.d(TAG, "downLoadConfigureDefault result is empty");
                return;
            }
            if (DEBUG) {
                Log.d(TAG, "downLoadConfigureDefault result=" + strHttpGet);
            }
            try {
                int i2 = new JSONObject(strHttpGet).getInt(Constants.CLOUD_NEW_SERVER_KEY_CODE);
                switch (i2) {
                    case 200:
                        Log.d(TAG, "hit profile");
                        break;
                    case 201:
                        Log.d(TAG, "none profile match the type");
                        break;
                    case 202:
                        Log.d(TAG, "gray test, not hit");
                        break;
                    case 203:
                        Log.d(TAG, "no update profile");
                        break;
                    default:
                        Log.d(TAG, "unknown");
                        break;
                }
                if (i2 != 200) {
                    Log.i(TAG, "no new clould configuration, returnCode = " + i2);
                    return;
                }
            } catch (Exception e2) {
                Log.e(TAG, "downLoadConfigureDefault", e2);
            }
            z3 = true;
        } else {
            strHttpGet = HttpUtils.httpGet(Constants.CLOUD_THERMAL_CONFIG_FILE_PATH + Device.getInstance().getType(context), Constants.CLOUD_THERMAL_SID);
            if (strHttpGet == null || strHttpGet.isEmpty()) {
                Log.d(TAG, "get no data for thermal : " + strHttpGet);
                return;
            }
            if (DEBUG) {
                Log.d(TAG, "hashResult : " + strHttpGet);
            }
            z3 = false;
        }
        try {
            String string = z3 ? new JSONObject(new JSONObject(strHttpGet).getString("profile")).getString(Constants.BUCKET_NAME_THERMAL_CONFIG_PATH) : new JSONObject(strHttpGet).getString("data");
            String string2 = new JSONObject(string).getString("exloc");
            String string3 = new JSONObject(string).getString("enable");
            SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_THERMAL_CLOUD_SWITCH, string3);
            if ("0".equals(string3)) {
                SimpleSettings.Misc.putString(context, SimpleSettingKeys.THERMAL_CONFIG_TAG, "default");
                if (checkThermalConfigReady()) {
                    comunicateWithNative(0, true);
                }
            }
            Log.i(TAG, "remoteData: " + string + "remoteFileId" + string2);
            String string4 = SimpleSettings.Misc.getString(context, SimpleSettingKeys.KEY_THERMAL_PERFORMANCE_LAST_UPDATE_FILEID, "");
            if (checkThermalConfigReady() && string4.equalsIgnoreCase(string2) && !z2) {
                Log.i(TAG, "No need to get remote data: " + string4);
                return;
            }
            String str = Constants.MARKET_SERVER + string2;
            boolean z4 = DEBUG;
            if (z4) {
                Log.i(TAG, "contentUrl: " + str);
            }
            boolean zLoadFile = HttpUtils.loadFile(str, getConfigFileSavePath(false));
            if (z4) {
                Log.i(TAG, "isLoadSucess: " + zLoadFile);
            }
            if (zLoadFile) {
                SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_THERMAL_PERFORMANCE_LAST_UPDATE_FILEID, string2);
                Log.i(TAG, "local_saved_hash: " + SimpleSettings.Misc.getString(context, SimpleSettingKeys.KEY_THERMAL_PERFORMANCE_LAST_UPDATE_FILEID, ""));
                SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_THERMAL_PERFORMANCE_LAST_UPDATE_TIME, Utils.getCurrentFormatTime());
                unZipConfig(str);
            }
        } catch (Exception e3) {
            Log.d(TAG, "startUpdate", e3);
        }
    }

    public static String getConfigFileSavePath(boolean z2) {
        String str = SystemProperties.get(PROP_THERMAL_FILE_PATH_NEW, SystemProperties.get(PROP_THERMAL_FILE_PATH, DEFAULT_CONFIG_FILE_PATH));
        return z2 ? (String) str.subSequence(0, str.length() - 1) : str;
    }

    public static String getGlobalModeFilePath() {
        return SystemProperties.get(PROP_THERMAL_FILE_PATH_NEW, SystemProperties.get(PROP_THERMAL_FILE_PATH, DEFAULT_GLOBAL_MODE_FILE_PATH)).concat("thermal-global-mode");
    }

    public static String getThermalConfigFilePath() {
        return SystemProperties.get(PROP_THERMAL_FILE_PATH_NEW, SystemProperties.get(PROP_THERMAL_FILE_PATH, DEFAULT_CONFIG_FILE_PATH)).concat("config/");
    }

    public static void restoreThermalMode(Context context) throws Throwable {
        if (SimpleSettings.Misc.getString(context, SimpleSettingKeys.THERMAL_CONFIG_TAG, "default").equals("performance")) {
            comunicateWithNative(1, false);
        } else {
            comunicateWithNative(0, false);
        }
    }

    /* JADX WARN: Code duplicated, block: B:40:0x0085 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:47:0x008e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:56:? A[SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v2, types: [android.net.LocalSocket, java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r1v3, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r1v6 */
    public static void socketCommunicate(int i2) throws Throwable {
        LocalSocket localSocket;
        Log.i(TAG, "socket:" + i2);
        ?? outputStream = 0;
        outputStream = 0;
        try {
            try {
                localSocket = new LocalSocket();
                try {
                    localSocket.connect(new LocalSocketAddress("thermal-switch-socket", LocalSocketAddress.Namespace.RESERVED));
                    outputStream = localSocket.getOutputStream();
                    if (outputStream != 0) {
                        outputStream.write(String.valueOf(i2).getBytes());
                        outputStream.flush();
                        Log.i(TAG, "socket connect successful!");
                    }
                    if (outputStream != 0) {
                        try {
                            outputStream.close();
                        } catch (IOException unused) {
                            Log.i(TAG, "exception when close socket");
                        }
                    }
                } catch (IOException e2) {
                    e = e2;
                    Log.i(TAG, "exception = " + e.toString());
                    if (outputStream != 0) {
                        try {
                            outputStream.close();
                        } catch (IOException unused2) {
                            Log.i(TAG, "exception when close socket");
                        }
                    }
                    if (localSocket == null) {
                        return;
                    }
                }
            } catch (Throwable th) {
                th = th;
                if (0 != 0) {
                    try {
                        outputStream.close();
                    } catch (IOException unused3) {
                        Log.i(TAG, "exception when close socket");
                    }
                }
                if (0 != 0) {
                    throw th;
                }
                try {
                    outputStream.close();
                    throw th;
                } catch (IOException unused4) {
                    Log.i(TAG, "exception when close socket");
                    throw th;
                }
            }
        } catch (IOException e3) {
            e = e3;
            localSocket = null;
        } catch (Throwable th2) {
            th = th2;
            if (0 != 0) {
                outputStream.close();
            }
            if (0 != 0) {
                throw th;
            }
            outputStream.close();
            throw th;
        }
        try {
            localSocket.close();
        } catch (IOException unused5) {
            Log.i(TAG, "exception when close socket");
        }
    }

    public static void storeOthernessThermalMode(Context context, int i2) throws Throwable {
        if (SimpleSettings.Misc.getString(context, SimpleSettingKeys.THERMAL_CONFIG_TAG, "default").equals(i2 == 1 ? "performance" : "default")) {
            return;
        }
        comunicateWithNative(i2, false);
    }

    private static void storeThermalGlobalMode(int i2) throws Throwable {
        String globalModeFilePath = getGlobalModeFilePath();
        if (!FileUtil.checkFileReady(TAG, globalModeFilePath)) {
            return;
        }
        FileWriter fileWriter = null;
        try {
            try {
                try {
                    FileWriter fileWriter2 = new FileWriter(globalModeFilePath, false);
                    try {
                        fileWriter2.write(Integer.toString(i2));
                        Log.e(TAG, "fileWrite:" + i2);
                        fileWriter2.close();
                    } catch (Exception e2) {
                        e = e2;
                        fileWriter = fileWriter2;
                        Log.e(TAG, "storeThermalGlobalModeException:" + e);
                        if (fileWriter == null) {
                        } else {
                            fileWriter.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        fileWriter = fileWriter2;
                        if (fileWriter != null) {
                            try {
                                fileWriter.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Exception e4) {
                    e = e4;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e5) {
            e5.printStackTrace();
        }
    }

    public static void unZipConfig(String str) {
        try {
            String name = new File(new URL(str).getFile()).getName();
            if (!confirmPath(getConfigFileSavePath(false) + name)) {
                FileUtil.deleteFile(getConfigFileSavePath(false) + name);
                return;
            }
            FileUtil.unZipFile(getConfigFileSavePath(false) + name, getConfigFileSavePath(true));
            Log.i(TAG, "nowzipfilename: " + name);
            FileUtil.deleteFile(getConfigFileSavePath(false) + name);
        } catch (MalformedURLException e2) {
            Log.i("unZipConfig", "exception = " + e2.toString());
        }
    }

    public static boolean warmControlModeSupported(Context context) {
        String string = SimpleSettings.Misc.getString(context, SimpleSettingKeys.KEY_THERMAL_CLOUD_SWITCH, "");
        if (!"0".equals(string) && UserHandle.myUserId() == 0) {
            return "1".equals(string) && checkThermalConfigReady();
        }
        Log.i(TAG, "uiEnabled:" + string + ",UserHandle.myUserId():" + UserHandle.myUserId() + ",UserHandle.USER_OWNER:0");
        return false;
    }
}
