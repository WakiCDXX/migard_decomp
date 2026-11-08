package com.miui.powerkeeper.millet;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.INotificationManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.net.VpnConfig;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.utils.CommonAdapter;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.PowerKeeperPackageManager;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.clientReport.ReportConstants;
import java.io.FileDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.app.backup.BackupManager;
import miui.process.ActiveUidInfo;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;

/* JADX INFO: loaded from: classes.dex */
public class MilletUtils {
    private static final boolean ALLOW_APP_USE_SENSORS = true;
    public static int BACKUP_INTERVAL = 120000;
    private static boolean DEBUG = false;
    private static String TAG = "MilletUtils";
    public static String backupPkg = null;
    static int backupUid = 0;
    private static int bgDataThreshold = 0;
    private static long checkBgMarketTime = 0;
    private static int dataFgThreshold = 0;
    public static String huanjiPkg = null;
    static int huanjiUid = 0;
    private static boolean isBackuping = false;
    private static HandlerThread mHT;
    private static long queryBackupTime;
    private static Context sCtx;
    private static SparseLongArray txBytes;
    public static String[] CTS_NAME = {"android.net.cts", "com.android.cts.verifier", "android.cts"};
    private static Set<String> mCloudBlackModeApps = new HashSet();
    private static Set<Integer> Downloadapp = new HashSet();
    public static int CLOUD_WHITE_OPEN = 1;
    public static int CLOUD_WHITE_CLOSE = 2;
    public static int CLOUD_BLACK_OPEN = 3;
    public static int CLOUD_BLACK_CLOSE = 4;
    public static int CLOUD_WHITE_BLACK_INVALID = 5;
    public static int CLOUD_WHITE_BLACK_ERROR = 6;
    public static boolean VERSION_TWO = true;
    public static List<String> mDownloadWhiteList = new ArrayList(Arrays.asList("com.baidu.searchbox", "com.baidu.browser.apps", "com.UCMobile", "com.tencent.mtt", "com.baidu.searchbox.lite", "com.quark.browser", "com.microsoft.emmx", "com.mmbox.xbrowser.pro", "com.ucmobile.lite", "com.qihoo.browser", "com.qihoo.contents", "sogou.mobile.explorer", "mark.via", "com.browser2345", PushConstants.XIAOMI_BROWSER_PACKAGE_NAME, "com.youku.phone", "com.cmcc.cmvideo", "com.hunantv.imgo.activity", "com.netease.dwrg.mi", "com.tencent.lolm", "com.tencent.jkchess", "com.netease.party.mi", "com.tencent.tmgp.speedmobile", "com.netease.tom.mi", "com.bairimeng.dmmdzz.mi", "com.tencent.tmgp.sgame", "com.cn21.ecloud", "com.aligames.star.sgzhxdl.mi", "com.greeze.testapp", "com.wandoujia.phoenix2"));
    private static final Object mRulesLock = new Object();
    private static IBinder mSensorService = null;
    private static boolean initFlagSensor = false;
    public static IWindowManager mWindowManager = null;
    public static Set<Integer> mOnWindowAppList = new HashSet();
    private static String pkgMedia = "com.android.providers.downloads";
    private static SparseIntArray notifiRecords = new SparseIntArray();
    private static Handler mHandler = null;

    static {
        boolean z2 = true;
        if (!Log.isLoggable(TAG, 3) && !Build.IS_DEBUGGABLE) {
            z2 = false;
        }
        DEBUG = z2;
        txBytes = new SparseLongArray();
        dataFgThreshold = 80;
        bgDataThreshold = SystemProperties.getInt("persist.sys.millet.traffic", 800);
        checkBgMarketTime = 0L;
        backupUid = -1;
        huanjiUid = -1;
        backupPkg = "com.miui.backup";
        huanjiPkg = "com.miui.huanji";
    }

    public static void checkBg(int i2, int i3) {
        HashSet hashSet = new HashSet();
        hashSet.add(Integer.valueOf(i2));
        checkBg(hashSet, i3);
    }

    public static boolean checkHuanji(Context context) {
        if (backupUid == -1) {
            int uidByPackageName = getUidByPackageName(context, backupPkg);
            backupUid = uidByPackageName;
            if (uidByPackageName == -1) {
                backupUid = -2;
            }
        }
        if (huanjiUid == -1) {
            int uidByPackageName2 = getUidByPackageName(context, huanjiPkg);
            huanjiUid = uidByPackageName2;
            if (uidByPackageName2 == -1) {
                huanjiUid = -2;
            }
        }
        boolean zIsUidForeground = UserHandle.isApp(backupUid) ? PowerKeeperManager.getInstance().getProcessObserver().isUidForeground(backupUid) : false;
        if (UserHandle.isApp(huanjiUid)) {
            return zIsUidForeground || PowerKeeperManager.getInstance().getProcessObserver().isUidForeground(huanjiUid);
        }
        return zIsUidForeground;
    }

    private static boolean checkSensorService() {
        if (mSensorService == null && !initFlagSensor) {
            IBinder service = ServiceManager.getService(LogType.LOG_TYPE_SENSOR_NAME);
            mSensorService = service;
            initFlagSensor = true;
            if (service == null) {
                Slog.e(TAG, "checkSensorService get sensorservice failed");
                return false;
            }
        }
        return true;
    }

    public static int getActiveVpnUid(Context context) {
        int uidByPackageName;
        try {
            VpnConfig vpnConfig = CommonAdapter.getVpnConfig(context, UserHandle.myUserId());
            uidByPackageName = vpnConfig != null ? getUidByPackageName(context, vpnConfig.user) : -1;
        } catch (Exception e2) {
            Log.e(TAG, "getActiveVpnUid err", e2);
        }
        if (uidByPackageName != -1) {
            Log.d(TAG, "active vpn name:" + uidByPackageName);
        }
        return uidByPackageName;
    }

    public static Set<String> getCloudBlackApps() {
        return mCloudBlackModeApps;
    }

    private static int getCloudStateDevice(String str, int i2, int i3) {
        try {
            for (String str2 : str.split(":")[1].split(",")) {
                if (str2.startsWith(Build.DEVICE)) {
                    String[] strArrSplit = str2.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER)[1].split("/");
                    if (MilletConfig.isGlobal) {
                        return "true".equals(strArrSplit[1]) ? i2 : i3;
                    }
                    return "true".equals(strArrSplit[0]) ? i2 : i3;
                }
            }
            Log.d(TAG, "parse error:" + str);
        } catch (Exception e2) {
            Log.e(TAG, "getCloudStateDevice pair", e2);
        }
        return CLOUD_WHITE_BLACK_ERROR;
    }

    public static int getDeviceOwner(Context context) {
        String deviceOwner;
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (devicePolicyManager == null || (deviceOwner = devicePolicyManager.getDeviceOwner()) == null) {
            return -1;
        }
        return PackageUtil.getUidByPackageName(context, deviceOwner);
    }

    public static boolean getDownload(int i2, String str) {
        return notifiRecords.get(i2, -1) == 1 || notifiRecords.get(i2, -1) == 2;
    }

    public static Set<String> getEnabeledAccessibilityService(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "enabled_accessibility_services");
        if (string == null) {
            return new HashSet();
        }
        HashSet hashSet = new HashSet();
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(simpleStringSplitter.next());
            if (componentNameUnflattenFromString != null) {
                hashSet.add(componentNameUnflattenFromString.getPackageName());
            }
        }
        return hashSet;
    }

    public static List<Integer> getHasForegroundService(ArrayList<Integer> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        Class<?> cls = new RunningProcessInfo().getClass();
        for (Integer num : arrayList) {
            int iIntValue = num.intValue();
            try {
                Field field = cls.getField("mLocationForeground");
                RunningProcessInfo runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(iIntValue);
                boolean zBooleanValue = ((Boolean) field.get(runningProcessInfoByPid)).booleanValue();
                if (runningProcessInfoByPid != null && zBooleanValue) {
                    Log.d(TAG, "pid=" + iIntValue + " isHasForegroundService=" + zBooleanValue);
                    arrayList2.add(num);
                }
            } catch (Exception e2) {
                Log.e(TAG, "pid=" + iIntValue + " isHasForegroundService: ", e2);
            }
        }
        return arrayList2;
    }

    public static String getHoldOnPackage() {
        String string;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken("android.app.IActivityManager");
            ServiceManager.getService("activity").transact(Class.forName("android.os.MiuiBinderTransaction$IActivityManager").getDeclaredField("TRANSACT_ID_GET_PACKAGE_HOLD_ON").getInt(null), parcelObtain, parcelObtain2, 0);
            parcelObtain2.readException();
            string = parcelObtain2.readString();
        } catch (Exception unused) {
            string = "";
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
        if (string != null && !string.isEmpty()) {
            Log.d(TAG, "getHoldOnPackage name=" + string);
        }
        return string;
    }

    public static String getImeName(Context context) {
        String[] strArrSplit;
        String str;
        String string = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
        if (string == null || string.length() <= 4 || (str = (strArrSplit = string.split("/"))[0]) == null || str.length() <= 4) {
            return null;
        }
        return strArrSplit[0];
    }

    public static Set<String> getMarketDownloadNotification(Context context) {
        List<StatusBarNotification> list;
        HashSet hashSet = new HashSet();
        try {
            ParceledListSlice appActiveNotifications = INotificationManager.Stub.asInterface(ServiceManager.getService("notification")).getAppActiveNotifications(pkgMedia, UserHandle.getUserId(getUidByPackageName(context, pkgMedia)));
            if (appActiveNotifications != null && (list = appActiveNotifications.getList()) != null && !list.isEmpty()) {
                for (StatusBarNotification statusBarNotification : list) {
                    String tag = statusBarNotification.getTag();
                    if (tag != null && tag.contains(":")) {
                        for (String str : tag.split(":")) {
                            if (!TextUtils.isEmpty(str) && str.contains(com.xiaomi.push.mpcd.Constants.DOT_SEPARATOR) && str.length() > 6) {
                                tag = str;
                                break;
                            }
                        }
                    }
                    if (DEBUG) {
                        Log.d(TAG, "noti tag=" + tag + " pkg=" + statusBarNotification.getOpPkg());
                    }
                    if (tag != null) {
                        hashSet.add(tag);
                    }
                }
                return hashSet;
            }
            return hashSet;
        } catch (Exception e2) {
            Log.e(TAG, "getMarketDownloadNotification", e2);
        }
    }

    public static String[] getPackagesForUid(int i2) {
        try {
            return AppGlobals.getPackageManager().getPackagesForUid(i2);
        } catch (Exception unused) {
            Log.d(TAG, "getPackagesForUid error uid = " + i2);
            return null;
        }
    }

    public static int[] getPidsForUid(int i2, Context context) {
        List arrayList = new ArrayList();
        int[] iArr = new int[0];
        try {
            arrayList = ProcessManager.getRunningProcessInfoByUid(i2);
        } catch (Exception e2) {
            Log.e(TAG, "getAppAllPids", e2);
        }
        if (arrayList == null || arrayList.size() == 0) {
            return iArr;
        }
        int[] iArr2 = new int[arrayList.size()];
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            iArr2[i3] = ((RunningProcessInfo) arrayList.get(i3)).mPid;
        }
        return iArr2;
    }

    public static String[] getRestrictActions() {
        return new String[]{"android.intent.action.BATTERY_CHANGED", "android.net.wifi.STATE_CHANGE", "android.intent.action.DROPBOX_ENTRY_ADDED", "android.net.wifi.RSSI_CHANGED", "android.net.wifi.supplicant.STATE_CHANGE"};
    }

    public static Map<Integer, String> getRunningApp(Context context) {
        HashMap map = new HashMap();
        try {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses()) {
                if (runningAppProcessInfo.processName != null) {
                    map.put(Integer.valueOf(runningAppProcessInfo.uid), runningAppProcessInfo.processName);
                }
            }
        } catch (Exception e2) {
            Log.e(TAG, "getRunningApp", e2);
        }
        return map;
    }

    public static int getScreenOffMilletMode(String str) {
        int cloudStateDevice = CLOUD_WHITE_BLACK_INVALID;
        if (str != null && !str.isEmpty() && str.length() >= 5) {
            for (String str2 : str.split(";")) {
                if (str2.startsWith("whitedevices:") && str2.contains(Build.DEVICE)) {
                    cloudStateDevice = getCloudStateDevice(str2, CLOUD_WHITE_OPEN, CLOUD_WHITE_CLOSE);
                } else if ((cloudStateDevice == CLOUD_WHITE_BLACK_INVALID || cloudStateDevice == CLOUD_WHITE_BLACK_ERROR || cloudStateDevice == CLOUD_WHITE_CLOSE) && str2.startsWith("blackdevices:") && str2.contains(Build.DEVICE)) {
                    cloudStateDevice = getCloudStateDevice(str2, CLOUD_BLACK_OPEN, CLOUD_BLACK_CLOSE);
                } else if (str2.startsWith("blackapps")) {
                    mCloudBlackModeApps.clear();
                    for (String str3 : str2.split(":")[1].split(",")) {
                        mCloudBlackModeApps.add(str3);
                    }
                }
            }
            Log.d(TAG, "millemode: " + cloudStateDevice + " blackapps:" + mCloudBlackModeApps.toString());
        }
        return cloudStateDevice;
    }

    public static int getUidByPackageName(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).uid;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.i(TAG, "not find packageName :" + str);
            return -1;
        }
    }

    public static int getUidForPid(int i2, Context context) {
        try {
            RunningProcessInfo runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i2);
            if (runningProcessInfoByPid != null) {
                return runningProcessInfoByPid.mUid;
            }
            return -1;
        } catch (Exception e2) {
            Log.e(TAG, "getUidForPid", e2);
            return -1;
        }
    }

    public static Set<Integer> getUidsForNames(Context context, List<String> list) {
        HashSet hashSet = new HashSet();
        try {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses()) {
                if (!runningAppProcessInfo.processName.isEmpty() && runningAppProcessInfo.uid > 0 && list.contains(runningAppProcessInfo.processName)) {
                    hashSet.add(Integer.valueOf(runningAppProcessInfo.uid));
                }
            }
        } catch (Exception e2) {
            Log.e(TAG, "getUidForNames", e2);
        }
        return hashSet;
    }

    public static String getWallpaperName(Context context) {
        WallpaperInfo wallpaperInfo = ((WallpaperManager) context.getSystemService("wallpaper")).getWallpaperInfo();
        if (wallpaperInfo != null) {
            return wallpaperInfo.getPackageName();
        }
        ComponentName defaultWallpaperComponent = WallpaperManager.getDefaultWallpaperComponent(context);
        if (defaultWallpaperComponent != null) {
            return defaultWallpaperComponent.getPackageName();
        }
        return null;
    }

    public static IWindowManager getWindowManagerService() {
        if (mWindowManager == null) {
            mWindowManager = WindowManagerGlobal.getWindowManagerService();
        }
        return mWindowManager;
    }

    public static boolean hasDownloadNotification(String str, int i2) {
        ParceledListSlice appActiveNotifications;
        List list;
        try {
            INotificationManager iNotificationManagerAsInterface = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
            if (iNotificationManagerAsInterface != null && (appActiveNotifications = iNotificationManagerAsInterface.getAppActiveNotifications(str, UserHandle.getUserId(i2))) != null && (list = appActiveNotifications.getList()) != null) {
                list.isEmpty();
            }
            return false;
        } catch (Exception e2) {
            Log.e(TAG, "hasDownloadNotification", e2);
            return false;
        }
    }

    public static boolean hasForegroundField() {
        try {
            new RunningProcessInfo().getClass().getField("mLocationForeground");
            return true;
        } catch (NoSuchFieldException unused) {
            Log.d(TAG, "no foregroundservices field----");
            return false;
        }
    }

    public static boolean isAppUid(int i2) {
        return i2 >= 10000 && UserHandle.isApp(i2) && i2 <= 19999;
    }

    public static boolean isBackupActive(Context context, boolean z2) {
        BackupManager backupManager = BackupManager.getBackupManager(context);
        if (SystemClock.uptimeMillis() - queryBackupTime <= BACKUP_INTERVAL && !z2) {
            return isBackuping;
        }
        queryBackupTime = SystemClock.uptimeMillis();
        try {
            boolean z3 = !backupManager.isServiceIdle();
            isBackuping = z3;
            return z3;
        } catch (Exception e2) {
            Log.e(TAG, "Could not access BackupManager: " + e2.toString());
            return false;
        }
    }

    public static boolean isCtsPackage(int i2, String str, Context context) {
        if (!SystemProperties.getBoolean("persist.sys.miui_optimization", true)) {
            return true;
        }
        if (TextUtils.isEmpty(str)) {
            str = MilletUidObserver.getPkgNameByUid(i2);
        }
        if (str != null) {
            return (str.contains("android.") && str.contains("cts")) || str.contains(".gts.");
        }
        return false;
    }

    public static boolean isFindDeviceOpen(Context context) {
        return false;
    }

    public static boolean isInstalledCtsApps() {
        ArraySet<String> installedThirdPartApps = PowerKeeperPackageManager.getInstalledThirdPartApps();
        if (installedThirdPartApps != null && !installedThirdPartApps.isEmpty()) {
            for (String str : installedThirdPartApps) {
                for (String str2 : CTS_NAME) {
                    if (str != null && str.contains(str2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isSystemApp(Context context, int i2) {
        try {
            if (!isAppUid(i2)) {
                return true;
            }
            PackageManager packageManager = context.getPackageManager();
            String[] packagesForUid = getPackagesForUid(i2);
            if (packagesForUid == null) {
                Log.d(TAG, "isSystemApp uid =" + i2 + " info null");
                return true;
            }
            for (String str : packagesForUid) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
                if (applicationInfo != null) {
                    int i3 = applicationInfo.flags;
                    boolean z2 = (i3 & 1) != 0 || (i3 & 128) != 0 || applicationInfo.isVendor() || applicationInfo.isProduct();
                    if (MilletConfig.supportSystemApp(str)) {
                        return false;
                    }
                    if (z2) {
                        return true;
                    }
                } else {
                    Log.d(TAG, "isSystemApp uid=" + i2 + " info null");
                }
            }
            return false;
        } catch (Exception e2) {
            Log.e(TAG, "isSystemApp ", e2);
            return true;
        }
    }

    public static boolean isTopApp(int i2) {
        boolean z2 = PowerKeeperManager.getInstance().getProcessObserver().getUidState(i2) == 2;
        if (z2) {
            Log.d(TAG, "isTopApp uid = " + i2);
        }
        return z2;
    }

    public static boolean isUidActive(int i2, int i3) {
        try {
            List activeUidInfo = ProcessManager.getActiveUidInfo(i3);
            if (activeUidInfo == null) {
                return false;
            }
            Iterator it = activeUidInfo.iterator();
            while (it.hasNext()) {
                if (((ActiveUidInfo) it.next()).uid == i2) {
                    return true;
                }
            }
        } catch (Exception unused) {
            Log.e(TAG, "Failed to get active uid: " + i2 + " type=" + i3);
        }
        return false;
    }

    public static boolean isWebviewSandboxUid(int i2) {
        int uidState = PowerKeeperManager.getInstance().getProcessObserver().getUidState(i2);
        boolean zIsIsolated = Process.isIsolated(i2);
        if (uidState != 3 || !zIsIsolated) {
            return false;
        }
        Log.d(TAG, "isWebviewSandboxUid uid = " + i2);
        return true;
    }

    public static boolean isWidget(int i2) {
        return false;
    }

    public static void notifyFreezeEvent(boolean z2, int[] iArr) {
        Bundle bundle = new Bundle();
        bundle.putChar("freeze", z2 ? 'T' : 'F');
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i2 : iArr) {
            arrayList.add(Integer.valueOf(i2));
        }
        bundle.putIntegerArrayList("uids", arrayList);
        PowerKeeperManager.getInstance().getEventsAggregator().notifyForFreeze(bundle);
    }

    public static void setAppSensorsControlPolicy(int i2, boolean z2) {
        synchronized (mRulesLock) {
            Slog.d(TAG, "setUidState, uid = " + i2 + " allow = " + z2);
            if (true == z2) {
                if (checkSensorService()) {
                    try {
                        mSensorService.dump(FileDescriptor.out, new String[]{"sensorsControl", "resume", Integer.toString(i2)});
                    } catch (RemoteException e2) {
                        Slog.e(TAG, "setAppSensorsControlPolicy" + e2);
                    } catch (UnsupportedOperationException e3) {
                        Slog.e(TAG, "setAppSensorsControlPolicy" + e3);
                    }
                }
            } else if (checkSensorService()) {
                try {
                    mSensorService.dump(FileDescriptor.out, new String[]{"sensorsControl", "stop", Integer.toString(i2)});
                } catch (RemoteException e4) {
                    Slog.e(TAG, "setAppSensorsControlPolicy" + e4);
                } catch (UnsupportedOperationException e5) {
                    Slog.e(TAG, "setAppSensorsControlPolicy" + e5);
                }
            }
            throw th;
        }
    }

    public static void setContext(Context context) {
        sCtx = context;
    }

    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

    public static void setPkgData(final int i2, final Handler handler, int i3) {
        if (MilletPolicy.isExtremeMode()) {
            return;
        }
        if (MilletPolicy.MESSAGE_APPS.contains(MilletUidObserver.getPkgNameByUid(i2))) {
            handler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletUtils.2
                @Override // java.lang.Runnable
                public void run() {
                    if (handler.hasMessages(11, Integer.valueOf(i2))) {
                        return;
                    }
                    Message messageObtainMessage = handler.obtainMessage(11, i2, 1);
                    messageObtainMessage.obj = Integer.valueOf(i2);
                    handler.sendMessage(messageObtainMessage);
                }
            }, i3 - 5000);
        }
    }

    public static void updateAppsOnWindowStatus() {
        try {
            List appsOnWindowsStatus = getWindowManagerService().getAppsOnWindowsStatus();
            if (appsOnWindowsStatus != null) {
                mOnWindowAppList.clear();
                Iterator it = appsOnWindowsStatus.iterator();
                while (it.hasNext()) {
                    int i2 = Integer.parseInt((String) it.next());
                    if (isAppUid(i2)) {
                        mOnWindowAppList.add(Integer.valueOf(i2));
                    }
                }
            }
        } catch (Exception unused) {
        }
    }

    public static void updatePkgState(Context context, String str, boolean z2) {
        if (backupPkg.equals(str)) {
            backupUid = z2 ? getUidByPackageName(context, backupPkg) : -2;
        } else if (huanjiPkg.equals(str)) {
            huanjiUid = z2 ? getUidByPackageName(context, huanjiPkg) : -2;
        }
    }

    public static void checkBg(Set<Integer> set, final int i2) {
        if (mHandler == null) {
            return;
        }
        final HashSet hashSet = new HashSet();
        long jUptimeMillis = SystemClock.uptimeMillis();
        if (jUptimeMillis - checkBgMarketTime > 4000) {
            checkBgMarketTime = jUptimeMillis;
            Iterator<String> it = getMarketDownloadNotification(sCtx).iterator();
            while (it.hasNext()) {
                int uidByPackageName = getUidByPackageName(sCtx, it.next());
                if (isAppUid(uidByPackageName)) {
                    hashSet.add(Integer.valueOf(uidByPackageName));
                }
            }
        }
        synchronized (Downloadapp) {
            try {
                for (Integer num : set) {
                    num.intValue();
                    if (!Downloadapp.contains(num) && !Downloadapp.contains(num)) {
                        Downloadapp.add(num);
                        hashSet.add(num);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        if (hashSet.size() == 0) {
            return;
        }
        Iterator it2 = hashSet.iterator();
        while (it2.hasNext()) {
            int iIntValue = ((Integer) it2.next()).intValue();
            txBytes.put(iIntValue, TrafficStats.getUidRxBytes(iIntValue) + TrafficStats.getUidTxBytes(iIntValue));
        }
        final long jUptimeMillis2 = SystemClock.uptimeMillis();
        mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletUtils.1
            @Override // java.lang.Runnable
            public void run() {
                for (Integer num2 : hashSet) {
                    int iIntValue2 = num2.intValue();
                    String pkgNameByUid = MilletUidObserver.getPkgNameByUid(iIntValue2);
                    HashSet hashSet2 = new HashSet();
                    long jUptimeMillis3 = SystemClock.uptimeMillis() - jUptimeMillis2;
                    long uidRxBytes = (TrafficStats.getUidRxBytes(iIntValue2) + TrafficStats.getUidTxBytes(iIntValue2)) - MilletUtils.txBytes.get(iIntValue2);
                    int i3 = (int) ((1000 * uidRxBytes) / (1024 * jUptimeMillis3));
                    if (MilletUtils.DEBUG) {
                        Log.d(MilletUtils.TAG, "checkDownload uid =" + iIntValue2 + " per=" + i3 + " delTime=" + jUptimeMillis3 + " delByte=" + uidRxBytes + " get=" + MilletUtils.notifiRecords.get(iIntValue2, -1));
                    }
                    boolean zIsUidForegroundService = (jUptimeMillis3 <= 0 || i3 <= MilletUtils.dataFgThreshold) ? false : PowerKeeperManager.getInstance().getProcessObserver().isUidForegroundService(iIntValue2);
                    if (jUptimeMillis3 <= 0 || i3 <= MilletUtils.dataFgThreshold || (!zIsUidForegroundService && (i3 <= MilletUtils.bgDataThreshold || !MilletUtils.mDownloadWhiteList.contains(pkgNameByUid)))) {
                        if (MilletUtils.notifiRecords.get(iIntValue2, -1) == 2) {
                            MilletUtils.notifiRecords.put(iIntValue2, 0);
                        }
                        if (MilletUtils.notifiRecords.get(iIntValue2, -1) == 1) {
                            MilletUtils.notifiRecords.put(iIntValue2, 2);
                            hashSet2.add(num2);
                        }
                    } else {
                        MilletUtils.notifiRecords.put(iIntValue2, 1);
                        hashSet2.add(num2);
                    }
                    synchronized (MilletUtils.Downloadapp) {
                        MilletUtils.Downloadapp.remove(num2);
                    }
                    if (hashSet2.size() > 0 && !MilletUtils.isTopApp(iIntValue2)) {
                        int i4 = i2;
                        if (i4 < 5000) {
                            i4 = ReportConstants.REGISTER_TYPE;
                        }
                        MilletUtils.checkBg(hashSet2, i4);
                    }
                }
            }
        }, i2);
    }

    public static Set<Integer> getUidForPid(int[] iArr, Context context) {
        HashSet hashSet = new HashSet();
        try {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses()) {
                for (int i2 : iArr) {
                    if (runningAppProcessInfo.pid == i2) {
                        hashSet.add(Integer.valueOf(runningAppProcessInfo.uid));
                        break;
                    }
                }
            }
        } catch (Exception e2) {
            Log.e(TAG, "getUidForPid", e2);
        }
        return hashSet;
    }
}
