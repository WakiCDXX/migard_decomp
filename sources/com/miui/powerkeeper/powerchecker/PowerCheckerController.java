package com.miui.powerkeeper.powerchecker;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.provider.PowerCheckerConfigure;
import com.miui.powerkeeper.provider.PowerCheckerConfigureHelper;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerController {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String KEY_PARAM_LEVEL = "level";
    private static final String KEY_PARAM_PKG = "package";
    private static final String KEY_PARAM_RULE = "rule";
    private static final String KEY_PARAM_TIME = "time";
    private static final String KEY_PARAM_TYPE = "type";
    private static final String KEY_PARAM_UID = "uid";
    private static final String KEY_PARAM_VERSION = "version";
    private static final int MSG_ACTION = 0;
    private static final int MSG_END = 2;
    private static final int MSG_REMOVE = 3;
    private static final int MSG_START = 1;
    private static String TAG = "PowerChecker.Controller";
    private PowerCheckerDatabaseCrud databaseCrud;
    private Context mContext;
    private Handler mHandler;
    private PowerCheckerProcessNotifier mNotifier;
    private PowerCheckerPolicy mPolicy;
    private PowerCheckerNotificationManager mPowerCheckerNotificationManager;
    private PowerCheckerProcessStats mProcessStats;
    private IWindowManager mWindowManager;
    public ArrayMap<String, String> mCpuInfoArrayMap = new ArrayMap<>();
    private final String ACTION_USER_CLEAR_NOTIFICATION = "powerchecker.user_clear_notification";
    private boolean mSystemAnomaly = false;
    private ArrayMap<String, PowerCheckerAppInfo> mPowerCheckerAppInfo = new ArrayMap<>();
    private List<PowerCheckerNotifier.KWLPowerExceedInfo> mPowerCheckerPlatformInfo = Lists.newArrayList();
    private List<PowerCheckerNotifier.SubSystemPowerExceedInfo> mPowerCheckerSubSystemInfo = Lists.newArrayList();
    private PowerKeeperManager mPowerKeeperManager = PowerKeeperManager.getInstance();

    private class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            boolean zOptBoolean;
            int i2 = message.what;
            if (i2 == 1) {
                PowerCheckerController.this.mSystemAnomaly = false;
                PowerCheckerController.this.mPowerCheckerNotificationManager.clearNotifyInfoList();
                PowerCheckerController.this.mProcessStats.setAppStatsInfoFlag();
                PowerCheckerController.this.mProcessStats.setSystemStatsInfoFlag();
                return;
            }
            if (i2 != 2) {
                return;
            }
            boolean z2 = SystemProperties.getBoolean("persist.sys.spc.powerkill.newpolicy.enable", false);
            b.a aVarP = b.a.p();
            int i3 = b.b.I;
            JSONObject jSONObjectL = aVarP.l(i3);
            if (b.a.p().s(i3)) {
                zOptBoolean = jSONObjectL.optBoolean("screen_on_witch", false);
                if (PowerCheckerController.DEBUG) {
                    Log.d(PowerCheckerController.TAG, "screen_on_witch is " + zOptBoolean);
                }
            } else {
                zOptBoolean = false;
            }
            PowerCheckerController.this.processExceedInfo();
            PowerCheckerController.this.mPowerCheckerNotificationManager.updateNotification();
            PowerCheckerController.this.mProcessStats.clearAppStatsInfoFlag();
            PowerCheckerController.this.mProcessStats.clearSystemStatsInfoFlag();
            if (PowerCheckerController.this.mSystemAnomaly) {
                PowerCheckerController.this.mSystemAnomaly = false;
                if (PowerCheckerController.this.mPolicy.getSystemAnomalyAction() == 2 && (!z2 || zOptBoolean)) {
                    PowerCheckerController.this.clearApp();
                }
            }
            PowerCheckerController.this.mPowerCheckerAppInfo.clear();
            PowerCheckerController.this.mPowerCheckerPlatformInfo.clear();
            PowerCheckerController.this.mPowerCheckerSubSystemInfo.clear();
        }
    }

    public static class PowerCheckerAppExceedInfo {
        private PowerCheckerNotifier.AppPowerExceedInfo mExceedInfo;
        private PowerCheckerPolicy.PolicyResult mPolicyResult;

        public PowerCheckerAppExceedInfo(PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo) {
            this.mExceedInfo = appPowerExceedInfo;
        }

        public PowerCheckerNotifier.AppPowerExceedInfo getAppPowerExceedInfo() {
            return this.mExceedInfo;
        }

        public PowerCheckerPolicy.PolicyResult getPolicyResult() {
            return this.mPolicyResult;
        }

        public void setPolicyResult(PowerCheckerPolicy.PolicyResult policyResult) {
            this.mPolicyResult = policyResult;
        }

        public String toString() {
            return "AppExceedInfo: type=" + this.mExceedInfo.type + ", uid=" + this.mExceedInfo.uid + ", level=" + this.mExceedInfo.priority + ", policy=" + this.mPolicyResult;
        }
    }

    public static class PowerCheckerAppInfo {
        private List<PowerCheckerAppExceedInfo> mAppExceedInfoList = Lists.newArrayList();
        private String mPkg;
        private int mUid;

        public PowerCheckerAppInfo(int i2, String str) {
            this.mUid = i2;
            this.mPkg = str;
        }

        public void addAppPowerExceedInfo(PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo) {
            this.mAppExceedInfoList.add(new PowerCheckerAppExceedInfo(appPowerExceedInfo));
        }

        public List<PowerCheckerAppExceedInfo> getAppExceedInfoList() {
            return this.mAppExceedInfoList;
        }

        public String getPkg() {
            return this.mPkg;
        }

        public int getUid() {
            return this.mUid;
        }
    }

    public PowerCheckerController(Context context, Looper looper, PowerCheckerPolicy powerCheckerPolicy, PowerCheckerProcessStats powerCheckerProcessStats, PowerCheckerProcessNotifier powerCheckerProcessNotifier) {
        this.mContext = context;
        this.mHandler = new InternalHandler(looper);
        this.mPolicy = powerCheckerPolicy;
        this.mProcessStats = powerCheckerProcessStats;
        this.mNotifier = powerCheckerProcessNotifier;
        this.mPowerCheckerNotificationManager = new PowerCheckerNotificationManager(context);
        this.databaseCrud = PowerCheckerDatabaseCrud.getInstance(this.mContext);
    }

    private boolean autoKillApp(int i2, String str) {
        if (str == null) {
            return false;
        }
        Slog.w(TAG, "autoKillApp, calling ProcessManager uid = " + i2 + ", pkg = " + str);
        return ProcessManager.kill(new ProcessConfig(11, str, i2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearApp() {
        ProcessConfig processConfig = new ProcessConfig(16);
        processConfig.setWhiteList(this.mPolicy.getActivePackages());
        ProcessManager.kill(processConfig);
    }

    private IWindowManager getWindowManagerService() {
        if (this.mWindowManager == null) {
            this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
        }
        return this.mWindowManager;
    }

    private void notifyApp(int i2, int i3, String str, Bundle bundle) {
        Log.d(TAG, "notifyApp, uid = " + i3 + ", pkg = " + str);
        this.mPowerCheckerNotificationManager.addNotificationInfo(i2, i3, str);
    }

    private void notifyAppTobroadcast(int i2, int i3, String str, JSONObject jSONObject) {
        Log.d(TAG, "notifyAppTobroadcast, uid = " + i3 + ", pkg = " + str);
        this.mPowerCheckerNotificationManager.addNotificationInfos(i2, i3, str, jSONObject);
    }

    /* JADX WARN: Code duplicated, block: B:55:0x00b4 A[Catch: IOException -> 0x00b8, TRY_ENTER, TryCatch #0 {IOException -> 0x00b8, blocks: (B:55:0x00b4, B:59:0x00bc, B:61:0x00c1, B:66:0x00d7, B:68:0x00dc, B:70:0x00e1), top: B:83:0x000e }] */
    /* JADX WARN: Code duplicated, block: B:59:0x00bc A[Catch: IOException -> 0x00b8, TryCatch #0 {IOException -> 0x00b8, blocks: (B:55:0x00b4, B:59:0x00bc, B:61:0x00c1, B:66:0x00d7, B:68:0x00dc, B:70:0x00e1), top: B:83:0x000e }] */
    /* JADX WARN: Code duplicated, block: B:61:0x00c1 A[Catch: IOException -> 0x00b8, TRY_LEAVE, TryCatch #0 {IOException -> 0x00b8, blocks: (B:55:0x00b4, B:59:0x00bc, B:61:0x00c1, B:66:0x00d7, B:68:0x00dc, B:70:0x00e1), top: B:83:0x000e }] */
    /* JADX WARN: Code duplicated, block: B:77:0x00ef A[Catch: IOException -> 0x00eb, TryCatch #6 {IOException -> 0x00eb, blocks: (B:73:0x00e7, B:77:0x00ef, B:79:0x00f4), top: B:84:0x00e7 }] */
    /* JADX WARN: Code duplicated, block: B:79:0x00f4 A[Catch: IOException -> 0x00eb, TRY_LEAVE, TryCatch #6 {IOException -> 0x00eb, blocks: (B:73:0x00e7, B:77:0x00ef, B:79:0x00f4), top: B:84:0x00e7 }] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x0075: MOVE (r2 I:??[OBJECT, ARRAY]) = (r8 I:??[OBJECT, ARRAY]) (LINE:118), block:B:23:0x0075 */
    private String procName2Pid(String str) throws Throwable {
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader;
        FileInputStream fileInputStream;
        BufferedReader bufferedReader2;
        BufferedReader bufferedReader3 = null;
        try {
            try {
                try {
                    for (File file : new File("/proc/").listFiles()) {
                        if (file.isDirectory()) {
                            File file2 = new File("/proc/" + file.getName() + "/cmdline");
                            if (file2.exists()) {
                                fileInputStream = new FileInputStream(file2);
                                try {
                                    inputStreamReader = new InputStreamReader(fileInputStream);
                                    try {
                                        bufferedReader2 = new BufferedReader(inputStreamReader);
                                        try {
                                            String line = bufferedReader2.readLine();
                                            if (line != null && str.equals(line.trim())) {
                                                String name = file.getName();
                                                try {
                                                    bufferedReader2.close();
                                                    inputStreamReader.close();
                                                    fileInputStream.close();
                                                } catch (IOException e2) {
                                                    e2.printStackTrace();
                                                    Log.e(TAG, "packageName2Pid io final exception", e2);
                                                }
                                                return name;
                                            }
                                            bufferedReader2.close();
                                            inputStreamReader.close();
                                            fileInputStream.close();
                                        } catch (FileNotFoundException e3) {
                                            e = e3;
                                        } catch (IOException e4) {
                                            e = e4;
                                            Log.e(TAG, "packageName2Pid io exception", e);
                                            if (bufferedReader2 != 0) {
                                                bufferedReader2.close();
                                            }
                                            if (inputStreamReader != null) {
                                                inputStreamReader.close();
                                            }
                                            if (fileInputStream != null) {
                                                fileInputStream.close();
                                            }
                                            return null;
                                        }
                                    } catch (FileNotFoundException e5) {
                                        e = e5;
                                        bufferedReader2 = 0;
                                    } catch (IOException e6) {
                                        e = e6;
                                        bufferedReader2 = 0;
                                    } catch (Throwable th) {
                                        th = th;
                                        if (bufferedReader3 != null) {
                                            try {
                                                bufferedReader3.close();
                                                if (inputStreamReader != null) {
                                                    inputStreamReader.close();
                                                }
                                                if (fileInputStream != null) {
                                                    fileInputStream.close();
                                                }
                                            } catch (IOException e7) {
                                                e7.printStackTrace();
                                                Log.e(TAG, "packageName2Pid io final exception", e7);
                                                throw th;
                                            }
                                        } else {
                                            if (inputStreamReader != null) {
                                                inputStreamReader.close();
                                            }
                                            if (fileInputStream != null) {
                                                fileInputStream.close();
                                            }
                                        }
                                        throw th;
                                    }
                                } catch (FileNotFoundException e8) {
                                    e = e8;
                                    inputStreamReader = null;
                                    bufferedReader2 = inputStreamReader;
                                } catch (IOException e9) {
                                    e = e9;
                                    inputStreamReader = null;
                                    bufferedReader2 = inputStreamReader;
                                    Log.e(TAG, "packageName2Pid io exception", e);
                                    if (bufferedReader2 != 0) {
                                        bufferedReader2.close();
                                    }
                                    if (inputStreamReader != null) {
                                        inputStreamReader.close();
                                    }
                                    if (fileInputStream != null) {
                                        fileInputStream.close();
                                    }
                                    return null;
                                } catch (Throwable th2) {
                                    th = th2;
                                    inputStreamReader = null;
                                }
                                Log.e(TAG, "packageName2Pid file not found", e);
                                if (bufferedReader2 != 0) {
                                    bufferedReader2.close();
                                }
                                if (inputStreamReader != null) {
                                    inputStreamReader.close();
                                }
                                if (fileInputStream != null) {
                                    fileInputStream.close();
                                }
                                return null;
                            }
                            continue;
                        }
                    }
                } catch (IOException e10) {
                    e10.printStackTrace();
                    Log.e(TAG, "packageName2Pid io final exception", e10);
                }
            } catch (FileNotFoundException e11) {
                e = e11;
                fileInputStream = null;
                inputStreamReader = null;
            } catch (IOException e12) {
                e = e12;
                fileInputStream = null;
                inputStreamReader = null;
            } catch (Throwable th3) {
                th = th3;
                fileInputStream = null;
                inputStreamReader = null;
            }
            return null;
        } catch (Throwable th4) {
            th = th4;
            bufferedReader3 = bufferedReader;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processExceedInfo() {
        boolean z2;
        int i2;
        boolean z3;
        int i3;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo;
        PowerCheckerPolicy.PolicyResult policyResult;
        boolean zAutoKillApp;
        ContentValues contentValues;
        int i4;
        boolean zCheckAppOnWindowsStatus;
        boolean zIsScreenOn;
        f.c cVar;
        boolean z4 = false;
        int i5 = 0;
        while (true) {
            int i6 = 100;
            if (i5 >= this.mPowerCheckerAppInfo.size()) {
                break;
            }
            PowerCheckerAppInfo powerCheckerAppInfoValueAt = this.mPowerCheckerAppInfo.valueAt(i5);
            boolean z5 = SystemProperties.getBoolean("persist.sys.spc.powerkill.newpolicy.enable", z4);
            b.a aVarP = b.a.p();
            int i7 = b.b.I;
            JSONObject jSONObjectL = aVarP.l(i7);
            if (b.a.p().s(i7)) {
                boolean zOptBoolean = jSONObjectL.optBoolean("screen_on_witch", z4);
                if (DEBUG) {
                    Log.d(TAG, "screen_on_witch is " + zOptBoolean);
                }
                z2 = zOptBoolean;
            } else {
                z2 = z4;
            }
            if (UserHandle.isApp(powerCheckerAppInfoValueAt.mUid)) {
                this.mPolicy.getAppRule(powerCheckerAppInfoValueAt);
                int uid = powerCheckerAppInfoValueAt.getUid();
                String pkg = powerCheckerAppInfoValueAt.getPkg();
                for (PowerCheckerAppExceedInfo powerCheckerAppExceedInfo : powerCheckerAppInfoValueAt.getAppExceedInfoList()) {
                    Log.i(TAG, "getAppRule, " + powerCheckerAppExceedInfo + ", milletEnable is " + z5);
                    LogCatcher logCatcher = LogCatcher.getInstance();
                    StringBuilder sb = new StringBuilder();
                    sb.append("getAppRule, ");
                    sb.append(powerCheckerAppExceedInfo);
                    logCatcher.debugLog(sb.toString());
                    PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo2 = powerCheckerAppExceedInfo.getAppPowerExceedInfo();
                    PowerCheckerPolicy.PolicyResult policyResult2 = powerCheckerAppExceedInfo.getPolicyResult();
                    int i8 = appPowerExceedInfo2.type;
                    int i9 = appPowerExceedInfo2.priority;
                    int i10 = policyResult2.mActionRule;
                    String str = "package";
                    String str2 = "level";
                    int i11 = i5;
                    try {
                        if (i10 >= i6 || i10 == 0) {
                            z5 = z5;
                            i3 = i8;
                            appPowerExceedInfo = appPowerExceedInfo2;
                            policyResult = policyResult2;
                            updateAppIgnoreStats(uid, pkg, i10, new PowerCheckerProcessStats.HistoryInfo(i9, i10, false), i3);
                        } else if (i10 == 1) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("type", i8);
                            bundle.putInt("uid", uid);
                            bundle.putInt("level", i9);
                            bundle.putString("package", pkg);
                            notifyApp(i8, uid, pkg, bundle);
                            updateAppNotifyStats(uid, pkg, new PowerCheckerProcessStats.HistoryInfo(i9, i10, false), i8);
                            z5 = z5;
                            i3 = i8;
                            policyResult = policyResult2;
                            appPowerExceedInfo = appPowerExceedInfo2;
                        } else {
                            if (i10 == 2) {
                                if (z5 && i8 == 4 && !z2) {
                                    i4 = 4;
                                } else {
                                    if (i8 == 4 && (cVar = (f.c) e.e.i(1073741826)) != null && cVar.c() != null && cVar.c().getPowerUsageMonitor() != null) {
                                        Log.i(TAG, "notifyCpuAbnomal uid is " + uid + ", processName=" + appPowerExceedInfo2.proc);
                                        cVar.c().getPowerUsageMonitor().notifyCpuAbnormal(uid);
                                    }
                                    try {
                                        zCheckAppOnWindowsStatus = getWindowManagerService().checkAppOnWindowsStatus(uid);
                                        try {
                                            zIsScreenOn = this.mPowerKeeperManager.isScreenOn();
                                        } catch (Exception e2) {
                                            e = e2;
                                            Log.e(TAG, "error : " + e);
                                            zCheckAppOnWindowsStatus = zCheckAppOnWindowsStatus;
                                            zIsScreenOn = false;
                                        }
                                    } catch (Exception e3) {
                                        e = e3;
                                        zCheckAppOnWindowsStatus = false;
                                    }
                                    if (zCheckAppOnWindowsStatus && zIsScreenOn) {
                                        Log.w(TAG, "can not kill reason isVisible = " + zCheckAppOnWindowsStatus + " screenOn = " + zIsScreenOn);
                                        zAutoKillApp = false;
                                    } else {
                                        zAutoKillApp = autoKillApp(uid, pkg);
                                        updateAppProcessStats(uid, pkg, new PowerCheckerProcessStats.HistoryInfo(i9, i10, zAutoKillApp), i8);
                                    }
                                    i9 = i9;
                                    i3 = i8;
                                    uid = uid;
                                    str = "package";
                                    pkg = pkg;
                                    str2 = "level";
                                    policyResult = policyResult2;
                                    z2 = z2;
                                    appPowerExceedInfo = appPowerExceedInfo2;
                                }
                                contentValues = new ContentValues();
                                contentValues.put("uid", Integer.valueOf(uid));
                                contentValues.put("package_name", pkg);
                                contentValues.put("type", Integer.valueOf(i3));
                                contentValues.put(PowerCheckerConfigure.Columns.ACTION, Integer.valueOf(i10));
                                contentValues.put("priority", Integer.valueOf(i9));
                                contentValues.put("package_version", appPowerExceedInfo.packageVersion);
                                contentValues.put("record_time", Long.valueOf(System.currentTimeMillis()));
                                if ((i10 != 2 || i10 == 104 || i10 == 102) && this.mPolicy.isFlagChecked(i9, i3)) {
                                    contentValues.put("flag", (Integer) 1);
                                } else {
                                    contentValues.put("flag", (Integer) 0);
                                }
                                this.databaseCrud.insert(PowerCheckerConfigure.TABLE, contentValues);
                                processNotify(i3, policyResult, appPowerExceedInfo, zAutoKillApp);
                                int i12 = uid;
                                this.mProcessStats.getAppStatsInfo(i12, pkg).setFlag(i3, true);
                                JSONObject jSONObject = new JSONObject();
                                jSONObject.put("uid", i12);
                                jSONObject.put(str, pkg);
                                jSONObject.put("type", i3);
                                jSONObject.put(KEY_PARAM_RULE, i10);
                                jSONObject.put(str2, i9);
                                jSONObject.put("version", appPowerExceedInfo.packageVersion);
                                jSONObject.put("time", String.valueOf(System.currentTimeMillis()));
                                notifyAppTobroadcast(i3, i12, pkg, jSONObject);
                                uid = i12;
                                pkg = pkg;
                                i5 = i11;
                                z2 = z2;
                                z5 = z5;
                                i6 = 100;
                            } else {
                                i4 = 4;
                            }
                            if (i10 == i4) {
                                updateAppRecordStats(uid, pkg, new PowerCheckerProcessStats.HistoryInfo(i9, i10, false), i8);
                            } else {
                                Log.w(TAG, "can not find which rule to do");
                            }
                            i3 = i8;
                            policyResult = policyResult2;
                            appPowerExceedInfo = appPowerExceedInfo2;
                        }
                        jSONObject.put("uid", i12);
                        jSONObject.put(str, pkg);
                        jSONObject.put("type", i3);
                        jSONObject.put(KEY_PARAM_RULE, i10);
                        jSONObject.put(str2, i9);
                        jSONObject.put("version", appPowerExceedInfo.packageVersion);
                        jSONObject.put("time", String.valueOf(System.currentTimeMillis()));
                    } catch (JSONException e4) {
                        e4.printStackTrace();
                    }
                    zAutoKillApp = false;
                    contentValues = new ContentValues();
                    contentValues.put("uid", Integer.valueOf(uid));
                    contentValues.put("package_name", pkg);
                    contentValues.put("type", Integer.valueOf(i3));
                    contentValues.put(PowerCheckerConfigure.Columns.ACTION, Integer.valueOf(i10));
                    contentValues.put("priority", Integer.valueOf(i9));
                    contentValues.put("package_version", appPowerExceedInfo.packageVersion);
                    contentValues.put("record_time", Long.valueOf(System.currentTimeMillis()));
                    if (i10 != 2) {
                        contentValues.put("flag", (Integer) 1);
                    } else {
                        contentValues.put("flag", (Integer) 1);
                    }
                    this.databaseCrud.insert(PowerCheckerConfigure.TABLE, contentValues);
                    processNotify(i3, policyResult, appPowerExceedInfo, zAutoKillApp);
                    int i13 = uid;
                    this.mProcessStats.getAppStatsInfo(i13, pkg).setFlag(i3, true);
                    JSONObject jSONObject2 = new JSONObject();
                    notifyAppTobroadcast(i3, i13, pkg, jSONObject2);
                    uid = i13;
                    pkg = pkg;
                    i5 = i11;
                    z2 = z2;
                    z5 = z5;
                    i6 = 100;
                }
                i2 = i5;
            } else {
                i2 = i5;
                this.mPolicy.getSystemRule(powerCheckerAppInfoValueAt);
                int uid2 = powerCheckerAppInfoValueAt.getUid();
                String pkg2 = powerCheckerAppInfoValueAt.getPkg();
                for (PowerCheckerAppExceedInfo powerCheckerAppExceedInfo2 : powerCheckerAppInfoValueAt.getAppExceedInfoList()) {
                    Log.d(TAG, "getSystemRule, " + powerCheckerAppExceedInfo2);
                    LogCatcher.getInstance().debugLog("getSystemRule, " + powerCheckerAppExceedInfo2);
                    PowerCheckerNotifier.PowerExceedInfo appPowerExceedInfo3 = powerCheckerAppExceedInfo2.getAppPowerExceedInfo();
                    PowerCheckerPolicy.PolicyResult policyResult3 = powerCheckerAppExceedInfo2.getPolicyResult();
                    int i14 = appPowerExceedInfo3.type;
                    int i15 = appPowerExceedInfo3.priority;
                    int i16 = policyResult3.mActionRule;
                    if (i16 >= 100 || i16 == 0) {
                        z3 = false;
                        updateSystemIgnoreStats(uid2, pkg2, i16, new PowerCheckerProcessStats.HistoryInfo(i15, i16, false), i14);
                    } else if (i16 == 4) {
                        z3 = false;
                        updateSystemRecordStats(uid2, pkg2, new PowerCheckerProcessStats.HistoryInfo(i15, i16, false), i14);
                    } else {
                        z3 = false;
                        if (i16 == 2) {
                            this.mSystemAnomaly = true;
                            updateSystemProcessStats(uid2, pkg2, new PowerCheckerProcessStats.HistoryInfo(i15, i16, false), i14);
                        } else {
                            Log.w(TAG, "can not find system which rule to do");
                        }
                    }
                    processNotify(i14, policyResult3, appPowerExceedInfo3, z3);
                    this.mProcessStats.getSystemStatsInfo(uid2, pkg2).setFlag(i14, true);
                }
            }
            i5 = i2 + 1;
            z4 = false;
        }
        for (PowerCheckerNotifier.KWLPowerExceedInfo kWLPowerExceedInfo : this.mPowerCheckerPlatformInfo) {
            PowerCheckerUtils.TimerEntry timerEntry = kWLPowerExceedInfo.ktimer;
            int i17 = kWLPowerExceedInfo.priority;
            String str3 = timerEntry != null ? timerEntry.mName : "kernel_wakelocks";
            PowerCheckerPolicy.PolicyResult kwlRule = this.mPolicy.getKwlRule(kWLPowerExceedInfo);
            int i18 = kwlRule.mActionRule;
            Log.d(TAG, "getKwlRule, type = 0, level = " + i17 + ", rule = " + i18);
            if (i18 >= 100 || i18 == 0) {
                updateKwlIgnoreStats(str3, i18);
                processNotify(0, kwlRule, kWLPowerExceedInfo, false);
            } else if (i18 == 4) {
                updateKwlProcessStats(str3);
                processNotify(0, kwlRule, kWLPowerExceedInfo, false);
            }
        }
        for (PowerCheckerNotifier.SubSystemPowerExceedInfo subSystemPowerExceedInfo : this.mPowerCheckerSubSystemInfo) {
            int i19 = subSystemPowerExceedInfo.priority;
            PowerCheckerPolicy.PolicyResult subSystemRule = this.mPolicy.getSubSystemRule(7, subSystemPowerExceedInfo);
            int i20 = subSystemRule.mActionRule;
            Log.d(TAG, "getSubSystemRule, type = 7, level = " + i19 + ", rule = " + i20);
            if (i20 >= 100 || i20 == 0) {
                updateSubSystemIgnoreStats(i20);
                processNotify(7, subSystemRule, subSystemPowerExceedInfo, false);
            } else if (i20 == 4) {
                updateSubSystemProcessStats();
                processNotify(7, subSystemRule, subSystemPowerExceedInfo, false);
            }
        }
    }

    private void processNotify(int i2, PowerCheckerPolicy.PolicyResult policyResult, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, boolean z2) {
        switch (i2) {
            case 0:
                this.mNotifier.notifyPlatformWakelockExceedLimit(policyResult, powerExceedInfo, z2);
                break;
            case 1:
                this.mNotifier.notifyUidPartialWakelockExceedLimit(policyResult, powerExceedInfo, z2);
                break;
            case 2:
                this.mNotifier.notifyUidWifiScanExceedLimit(policyResult, powerExceedInfo, z2);
                break;
            case 3:
                this.mNotifier.notifyUidBgGPSTimeExceedLimit(policyResult, powerExceedInfo, z2);
                break;
            case 4:
                this.mNotifier.notifyUidCPUTimeExceedLimit(policyResult, powerExceedInfo, z2);
                break;
            case 5:
                this.mNotifier.notifyUidWakeupsExceedLimit(policyResult, powerExceedInfo, z2);
                break;
            case 6:
                this.mNotifier.notifyUidMobileRadioActiveExceedLimit(policyResult, powerExceedInfo, z2);
                break;
            case 7:
                this.mNotifier.notifySubSystemExcessivePower(policyResult, powerExceedInfo, z2);
                break;
        }
    }

    private void removeNotification(int i2, int i3) {
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 3, i2, i3));
    }

    private void updateAppIgnoreStats(int i2, String str, int i3, PowerCheckerProcessStats.HistoryInfo historyInfo, int i4) {
        this.mProcessStats.updateAppIgnoreStats(i2, str, i3, historyInfo, i4);
    }

    private void updateAppNotifyStats(int i2, String str, PowerCheckerProcessStats.HistoryInfo historyInfo, int i3) {
        this.mProcessStats.updateAppNotifyStats(i2, str, SystemClock.elapsedRealtime(), historyInfo, i3);
    }

    private void updateAppProcessStats(int i2, String str, PowerCheckerProcessStats.HistoryInfo historyInfo, int i3) {
        this.mProcessStats.updateAppProcessStats(i2, str, SystemClock.elapsedRealtime(), historyInfo, i3);
    }

    private void updateAppRecordStats(int i2, String str, PowerCheckerProcessStats.HistoryInfo historyInfo, int i3) {
        this.mProcessStats.updateAppRecordStats(i2, str, SystemClock.elapsedRealtime(), historyInfo, i3);
    }

    private void updateKwlIgnoreStats(String str, int i2) {
        this.mProcessStats.updateKwlIgnoreStats(str, i2);
    }

    private void updateKwlProcessStats(String str) {
        this.mProcessStats.updateKwlProcessStats(str, SystemClock.elapsedRealtime());
    }

    private void updateSubSystemIgnoreStats(int i2) {
        this.mProcessStats.updateSubSystemIgnoreStats(i2);
    }

    private void updateSubSystemProcessStats() {
        this.mProcessStats.updateSubSystemProcessStats(SystemClock.elapsedRealtime());
    }

    private void updateSystemIgnoreStats(int i2, String str, int i3, PowerCheckerProcessStats.HistoryInfo historyInfo, int i4) {
        this.mProcessStats.updateSystemIgnoreStats(i2, str, i3, historyInfo, i4);
    }

    private void updateSystemProcessStats(int i2, String str, PowerCheckerProcessStats.HistoryInfo historyInfo, int i3) {
        this.mProcessStats.updateSystemProcessStats(i2, str, SystemClock.elapsedRealtime(), historyInfo, i3);
    }

    private void updateSystemRecordStats(int i2, String str, PowerCheckerProcessStats.HistoryInfo historyInfo, int i3) {
        this.mProcessStats.updateSystemRecordStats(i2, str, SystemClock.elapsedRealtime(), historyInfo, i3);
    }

    public void dispatchEvent(PowerCheckerNotifier.PowerExceedInfo powerExceedInfo) {
        String packageNameByProcessName;
        Log.d(TAG, "dispatchEvent,  " + powerExceedInfo.toString());
        if (PowerCheckerManager.isValidType(powerExceedInfo.type)) {
            int i2 = powerExceedInfo.type;
            if (i2 == 0) {
                this.mPowerCheckerPlatformInfo.add((PowerCheckerNotifier.KWLPowerExceedInfo) powerExceedInfo);
                return;
            }
            if (i2 == 7) {
                this.mPowerCheckerSubSystemInfo.add((PowerCheckerNotifier.SubSystemPowerExceedInfo) powerExceedInfo);
                return;
            }
            PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
            int i3 = appPowerExceedInfo.uid;
            if (UserHandle.isApp(i3)) {
                if (i2 == 5) {
                    packageNameByProcessName = appPowerExceedInfo.pkg;
                } else {
                    packageNameByProcessName = i2 == 4 ? PackageUtil.getPackageNameByProcessName(appPowerExceedInfo.proc) : PackageUtil.getPackageNameByUid(this.mContext, i3);
                }
                String str = i3 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + packageNameByProcessName;
                if (this.mPowerCheckerAppInfo.containsKey(str)) {
                    this.mPowerCheckerAppInfo.get(str).addAppPowerExceedInfo(appPowerExceedInfo);
                    return;
                }
                PowerCheckerAppInfo powerCheckerAppInfo = new PowerCheckerAppInfo(i3, packageNameByProcessName);
                powerCheckerAppInfo.addAppPowerExceedInfo(appPowerExceedInfo);
                this.mPowerCheckerAppInfo.put(str, powerCheckerAppInfo);
                return;
            }
            if (i2 == 4) {
                String packageNameByProcessName2 = PackageUtil.getPackageNameByProcessName(appPowerExceedInfo.proc);
                String str2 = i3 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + packageNameByProcessName2;
                if (this.mPowerCheckerAppInfo.containsKey(str2)) {
                    this.mPowerCheckerAppInfo.get(str2).addAppPowerExceedInfo(appPowerExceedInfo);
                    return;
                }
                PowerCheckerAppInfo powerCheckerAppInfo2 = new PowerCheckerAppInfo(i3, packageNameByProcessName2);
                powerCheckerAppInfo2.addAppPowerExceedInfo(appPowerExceedInfo);
                this.mPowerCheckerAppInfo.put(str2, powerCheckerAppInfo2);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        printWriter.println("PowerCheckerController dump heavy info");
        printWriter.println("---------------------------------------");
        printWriter.println("heavy info: ");
        ArrayMap<String, String> arrayMap = this.mCpuInfoArrayMap;
        if (arrayMap != null && arrayMap.size() >= 0) {
            for (int i2 = 0; i2 < this.mCpuInfoArrayMap.size(); i2++) {
                printWriter.println(this.mCpuInfoArrayMap.keyAt(i2).toString());
                printWriter.println(this.mCpuInfoArrayMap.valueAt(i2).toString());
            }
        }
        printWriter.println();
        printWriter.println("PowerCheckerController dump result info");
        List<PowerCheckerConfigureHelper> listCreateFromTable = PowerCheckerConfigureHelper.createFromTable(this.mContext);
        int size = listCreateFromTable.size();
        if (size > 0) {
            for (int i3 = 0; i3 < size; i3++) {
                printWriter.println(listCreateFromTable.get(i3).getId() + " | " + listCreateFromTable.get(i3).getUid() + " | " + listCreateFromTable.get(i3).getPackageName() + " | " + listCreateFromTable.get(i3).getType() + " | " + listCreateFromTable.get(i3).getAction() + " | " + listCreateFromTable.get(i3).getPriority() + " | " + listCreateFromTable.get(i3).getVersion() + " | " + listCreateFromTable.get(i3).getFlag() + " | " + simpleDateFormat.format(new Date(listCreateFromTable.get(i3).getRecordTime())));
            }
        }
        printWriter.println("---------------------------------------");
        this.mPowerCheckerNotificationManager.dump(fileDescriptor, printWriter, strArr);
    }

    /* JADX WARN: Code duplicated, block: B:42:0x00cf A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:43:0x00d1 A[Catch: IOException -> 0x00cd, TRY_LEAVE, TryCatch #6 {IOException -> 0x00cd, blocks: (B:39:0x00c9, B:43:0x00d1), top: B:50:0x00c9 }] */
    /* JADX WARN: Code duplicated, block: B:50:0x00c9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public String getBacktrace(String str) throws Throwable {
        InputStreamReader inputStreamReader;
        Throwable th;
        BufferedReader bufferedReader;
        IOException e2;
        String strProcName2Pid = procName2Pid(str);
        Log.d(TAG, "getBacktrace: pid = " + strProcName2Pid);
        if (strProcName2Pid == null) {
            return null;
        }
        String str2 = "debuggerd -b " + strProcName2Pid;
        StringBuilder sb = new StringBuilder(128);
        try {
            try {
                inputStreamReader = new InputStreamReader(Runtime.getRuntime().exec(str2).getInputStream());
                try {
                    bufferedReader = new BufferedReader(inputStreamReader);
                    try {
                        try {
                            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                                sb.append(line + ShellUtils.COMMAND_LINE_END);
                            }
                            inputStreamReader.close();
                            bufferedReader.close();
                        } catch (IOException e3) {
                            e2 = e3;
                            Log.e(TAG, "getBacktrace io exception", e2);
                            if (inputStreamReader != null) {
                                inputStreamReader.close();
                            }
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (inputStreamReader != null) {
                            try {
                                inputStreamReader.close();
                                if (bufferedReader != null) {
                                    bufferedReader.close();
                                }
                            } catch (IOException e4) {
                                Log.e(TAG, "getBacktrace io finally exception", e4);
                                throw th;
                            }
                        } else if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                        throw th;
                    }
                } catch (IOException e5) {
                    e2 = e5;
                    bufferedReader = null;
                } catch (Throwable th3) {
                    th = th3;
                    bufferedReader = null;
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    } else if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    throw th;
                }
            } catch (IOException e6) {
                Log.e(TAG, "getBacktrace io finally exception", e6);
            }
        } catch (IOException e7) {
            inputStreamReader = null;
            e2 = e7;
            bufferedReader = null;
        } catch (Throwable th4) {
            inputStreamReader = null;
            th = th4;
            bufferedReader = null;
        }
        if (DEBUG) {
            Log.d(TAG, "debuggerd result: " + sb.toString());
        }
        return sb.toString();
    }

    public void notifyPowerCheckerComplete() {
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 2));
    }

    public void notifyPowerCheckerStart() {
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 1));
    }

    public void notifyProcessDied(int i2, int i3) {
        if (UserHandle.isApp(i2)) {
            removeNotification(i2, i3);
        }
    }

    void processResult(int i2, int i3, String str, int i4) {
        this.mNotifier.updatePowerExceedRecordAction(i2, i3, str, i4);
    }

    public void updateBackgroundNetworkActiveUids() {
        this.mPolicy.updateBackgroundNetworkActiveUids();
    }
}
