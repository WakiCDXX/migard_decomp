package com.miui.powerkeeper.millet;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerCompat;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.view.Display;
import android.view.WindowManager;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.provider.PowerKeeperConfigure;
import com.miui.powerkeeper.provider.UserConfigure;
import com.miui.powerkeeper.provider.UserConfigureHelper;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import com.miui.powerkeeper.utils.PackageUtil;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.PushConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.os.Build;
import miui.system.Manifest;
import miui.util.PlayerActions;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class MilletPolicy implements PowerKeeperInterface.a0 {
    private static final String SETTINGS_SYSTEM_SPECIAL_BUSINESS = "settings_special_business_app";
    public static String TAG = "MilletPolicy";
    private static final int TYPE_USER_APP_ID = 1;
    private static final UriMatcher sMatcher;
    private AlarmManager alarm;
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private Display mDisplay;
    private SettingsObserver mSettingsObserver;
    private PackageManager pm;
    public static List<String> mSystemBlackList = new ArrayList(Arrays.asList("com.miui.gallery", PlayerActions.BROADCAST_PREFIX, "com.android.vending", "com.android.contacts", PushConstants.XIAOMI_BROWSER_PACKAGE_NAME, "com.miui.cloudservice", "com.miui.micloudsync", "com.android.quicksearchbox", "com.google.android.gms", "com.miui.hybrid", "com.android.thememanager", "com.xiaomi.misettings", "com.miui.fm"));
    public static List<String> mDataWhiteList = new ArrayList(Arrays.asList("com.xiaomi.mibrain.speech", PluginConstants.TTS_XIAOAI_PKGNAME, "com.xiaomi.account", "com.android.soundrecorder", "com.miui.newhome", "com.miui.notes", "com.xiaomi.aiasst.service", "com.miui.themestore", "com.miui.fliphome", "com.miui.calculator", "com.android.fileexplorer", "com.mi.android.globalFileexplorer", "com.miui.mediaeditor", "com.tencent.wework", "com.xiaomi.wearable", "com.xiaomi.migameservice", "com.android.thememanager", "com.miui.miservice", "com.mipay.wallet", "com.android.providers.downloads.ui", "com.android.stk", "com.google.android.wearable.app.cn", "com.xiaomi.miralink", "com.baidu.carlife.xiaomi", "com.miui.vpnsdkmanager", "com.miui.miwallpaper", "com.potin.systest", "com.potin.systestlow", "com.potintest.systestp", "com.potintest.systestlow"));
    private static List<String> mMiDataWhiteList = new ArrayList(Arrays.asList("com.android.deskclock", "com.android.calendar", "com.miui.securitycenter", "com.miui.virtualsim", PushConstants.PUSH_SERVICE_PACKAGE_NAME));
    public static List<String> musicApp = new ArrayList(Arrays.asList("com.ximalaya.ting.android", "fm.qingting.qtradio", "com.kugou.android", "com.netease.cloudmusic", "com.tencent.qqmusic", "fm.xiami.main", "cn.toside.music.mobile", "cn.wenyu.bodian"));
    private static List<String> whiteApps = new ArrayList(Arrays.asList("com.google.android.gms", "com.xiaomi.market", "com.miui.hybrid", PlayerActions.BROADCAST_PREFIX, "com.miui.systemAdSolution", "com.miui.weather2", "com.google.android.ext.services"));
    private static String ACTION_BLUETOOTH_NOTIFY_APP_DEVICE_FOUND = "android.miuibluetooth.action.notifyappdevicefound";
    public static List<String> MESSAGE_APPS = new ArrayList(Arrays.asList("com.tencent.mobileqq", "com.tencent.mm", "com.alibaba.android.rimet", "com.ss.android.lark", "com.ss.android.lark.kami", "com.tencent.tim"));
    private static List<String> SYSTEM_BLACK = new ArrayList(Arrays.asList("com.xiaomi.market"));
    public static List<String> BRIGHT_WHITE_APPS = new ArrayList(Arrays.asList("com.xiaomi.smarthome", "com.miui.gallery"));
    public static Set<Integer> SYNC_UIDS = new HashSet();
    private static String ACTION_PKG_ALARM = "com.miui.powerkeeper.PKG_ALARM";
    private static String ACTION_FROZEN_DELAY_ALARM = "com.miui.powerkeeper.FROZEN_DELAY_ALRM";
    private static int PKG_BINDER_TIMEOUT = 5000;
    private static String KEY_NO_RESTRICT_APP = "MILLET_NO_RESTRICT_APP";
    private static boolean mExtremeMode = false;
    private List<String> pkgWhiteList = new ArrayList();
    private List<String> pkgBlackList = new ArrayList();
    private List<String> pkgGrayList = new ArrayList();
    private Set<String> mUseDataWhiteList = new HashSet();
    private Set<String> mUseSystemBlackList = new HashSet();
    private Set<String> mCloudWhiteList = new HashSet();
    private int uidWallpaper = -1;
    private int uidVpnActive = -1;
    private BluetoothEvent mBluetoothEvent = null;
    private long pkgFirstTime = 0;
    private Handler mHandler = null;
    private Set<Integer> delayList = new HashSet();
    private Set<String> mNoRestrictSet = new HashSet();
    private int mOldScreenState = -1;
    private int mMilletMode = MilletUtils.CLOUD_WHITE_BLACK_INVALID;
    private int mDeviceOwner = -1;
    private Set<Integer> mAccessibilityUids = new HashSet();
    private SparseLongArray mThawUidTime = new SparseLongArray();
    private SparseIntArray mUserIMEUid = new SparseIntArray();
    private List<Integer> mImportantApps = new ArrayList();
    public Set<Integer> mBrightWhiteUids = new HashSet();
    private PowerKeeperInterface.v mIMEChangedListener = new PowerKeeperInterface.v() { // from class: com.miui.powerkeeper.millet.MilletPolicy.1
        @Override // com.miui.powerkeeper.PowerKeeperInterface.v
        public void onIMEChange(int i2, final int i3) {
            MilletPolicy.this.mUserIMEUid.put(i2, i3);
            Slog.d(MilletPolicy.TAG, "ime userId=" + i2 + " uid =" + i3);
            if (MilletPolicy.this.mHandler != null) {
                MilletPolicy.this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletPolicy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (FreezeBinder.getInstance().isUidFrozen(i3)) {
                            FreezeBinder.thawUid(i3, "ime change");
                        }
                    }
                }, 200L);
            } else if (FreezeBinder.getInstance().isUidFrozen(i3)) {
                FreezeBinder.thawUid(i3, "ime change.");
            }
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.millet.MilletPolicy.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(MilletPolicy.TAG, "onReceive " + action);
            if (MilletPolicy.ACTION_PKG_ALARM.equals(action)) {
                MilletPolicy.this.pkgFirstTime = 0L;
                Log.d(MilletPolicy.TAG, "onReceive send size=" + MilletPolicy.this.delayList.size());
                if (MilletPolicy.this.mHandler != null) {
                    MilletPolicy.this.mHandler.removeMessages(12);
                }
                MilletPolicy.this.thawedDelayList(0L);
                return;
            }
            if ("com.miui.powerkeeper_sleep_changed".equals(action)) {
                MilletPolicy.this.dealSleepMode(intent.getIntExtra(YellowPageContract.Permission.STATE, -1));
                return;
            }
            if (!MilletPolicy.ACTION_BLUETOOTH_NOTIFY_APP_DEVICE_FOUND.equals(action)) {
                if (!"android.os.action.DEVICE_IDLE_MODE_CHANGED".equals(action) && !"android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED".equals(action)) {
                    if ("android.intent.action.WALLPAPER_CHANGED".equals(action)) {
                        MilletPolicy.this.checkWallpaper();
                        return;
                    } else {
                        if ("android.intent.action.USER_PRESENT".equals(action)) {
                            MilletPolicy.this.mHandler.sendEmptyMessage(33);
                            return;
                        }
                        return;
                    }
                }
                PowerManager powerManager = (PowerManager) MilletPolicy.this.mContext.getSystemService(NightStandbyRecord.KEY_POWER);
                if (MilletPolicy.this.mHandler == null || powerManager == null) {
                    return;
                }
                if (powerManager.isDeviceIdleMode() || powerManager.isLightDeviceIdleMode()) {
                    MilletPolicy.this.mHandler.sendEmptyMessage(25);
                    return;
                }
                return;
            }
            String stringExtra = intent.getStringExtra("thirty_apps_start");
            int intExtra = intent.getIntExtra("event", -1);
            Log.d(MilletPolicy.TAG, "found blue name=" + stringExtra + " event=" + intExtra);
            if (stringExtra == null || stringExtra.isEmpty()) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("thirty_apps_start", stringExtra);
            bundle.putInt("event", intExtra);
            bundle.putString("profile", "y");
            Message messageObtain = Message.obtain();
            messageObtain.what = 23;
            messageObtain.setData(bundle);
            if (MilletPolicy.this.mHandler != null) {
                MilletPolicy.this.mHandler.sendMessage(messageObtain);
            }
        }
    };
    private BroadcastReceiver mReceiverScreen = new BroadcastReceiver() { // from class: com.miui.powerkeeper.millet.MilletPolicy.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d(MilletPolicy.TAG, "onReceive " + intent.getAction());
            if (MilletPolicy.this.mHandler == null) {
                Log.d(MilletPolicy.TAG, "mReceiverScreen mHandler == null");
            } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                MilletPolicy.this.mHandler.sendEmptyMessage(3);
            }
        }
    };
    int mLastPlayUid = -1;
    long mLastAudioStopTime = 0;
    private CloudFunctionConfig.ICloudFunctionListener mFunctionListener = new CloudFunctionConfig.ICloudFunctionListener() { // from class: com.miui.powerkeeper.millet.MilletPolicy.4
        @Override // com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig.ICloudFunctionListener
        public void onCloudFunctionUpdated() {
            MilletPolicy.this.onCloudWhiteListChanged();
        }
    };
    private String PC_SECURITY_CENTER_EXTREME_MODE = "pc_security_center_extreme_mode_enter";
    private List<Integer> mSpecialBusiness = new ArrayList();

    private class MyDisplayListener implements DisplayManager.DisplayListener {
        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i2) {
            int state = MilletPolicy.this.mDisplay.getState();
            if (MilletHandler.DEBUG && (state == 1 || state == 2)) {
                Slog.d(MilletPolicy.TAG, "onDisplayChanged new=" + state);
            }
            if (MilletPolicy.this.mHandler == null || state == MilletPolicy.this.mOldScreenState) {
                return;
            }
            MilletPolicy.this.mOldScreenState = state;
            if (state == 1) {
                MilletPolicy.this.checkImportantApps();
                MilletPolicy.this.mHandler.sendEmptyMessageDelayed(3, 1000L);
            } else if (state == 2) {
                MilletPolicy.this.mHandler.removeMessages(3);
                MilletPolicy.this.mHandler.sendEmptyMessage(2);
            }
        }

        private MyDisplayListener() {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i2) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i2) {
        }
    }

    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            if (uri.equals(Settings.Secure.getUriFor(MilletPolicy.this.PC_SECURITY_CENTER_EXTREME_MODE))) {
                MilletPolicy.mExtremeMode = Settings.Secure.getInt(MilletPolicy.this.mContext.getContentResolver(), MilletPolicy.this.PC_SECURITY_CENTER_EXTREME_MODE, -1) == 1;
                Log.d(MilletPolicy.TAG, "mExtremeMode =" + MilletPolicy.mExtremeMode);
                return;
            }
            if (uri.equals(Settings.Secure.getUriFor("enabled_accessibility_services"))) {
                MilletPolicy.this.updateAccessUids();
            } else if (MilletPolicy.sMatcher.match(uri) == 1) {
                MilletPolicy.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletPolicy.SettingsObserver.1
                    @Override // java.lang.Runnable
                    public void run() {
                        MilletPolicy.this.dealNoRestrictApp();
                    }
                });
            } else if (uri.equals(Settings.System.getUriFor(MilletPolicy.SETTINGS_SYSTEM_SPECIAL_BUSINESS))) {
                MilletPolicy.this.updateSpecialBusiness();
            }
        }
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "userTable/#", 1);
    }

    public MilletPolicy(Context context) {
        this.pm = null;
        this.alarm = null;
        this.mContext = context;
        this.alarm = (AlarmManager) context.getSystemService("alarm");
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService("window");
        this.pm = this.mContext.getPackageManager();
        this.mDisplay = windowManager.getDefaultDisplay();
        if (MilletConfig.isGlobal) {
            mSystemBlackList.remove("com.google.android.gms");
            whiteApps.remove("com.google.android.gms");
            whiteApps.remove("com.google.android.ext.services");
        }
        if (Build.IS_STABLE_VERSION) {
            mSystemBlackList.removeAll(SYSTEM_BLACK);
        }
        checkMessageApp();
        checkImportantApps();
        checkWallpaper();
        this.mUseDataWhiteList.addAll(mDataWhiteList);
        this.mUseDataWhiteList.addAll(mMiDataWhiteList);
        this.mUseSystemBlackList.addAll(mSystemBlackList);
        getExternalApps(this.mContext);
        dealCloudWhiteList();
        updateSpecialBusiness();
        CloudFunctionConfig.getInstance().registerCloudFunctionConfig(this.mFunctionListener);
        dealNoRestrictApp();
    }

    private void addList(List<Integer> list, List<String> list2) {
        for (String str : list2) {
            try {
                int packageUidAsUser = PackageManagerCompat.getPackageUidAsUser(this.pm, str, this.mContext.getUserId());
                if (MilletUtils.isAppUid(packageUidAsUser)) {
                    list.add(Integer.valueOf(packageUidAsUser));
                }
            } catch (PackageManager.NameNotFoundException unused) {
            } catch (Exception e2) {
                Log.e(TAG, "addList name=" + str, e2);
            }
        }
    }

    private void checkMessageApp() {
        Log.d(TAG, "checkMessage d=" + android.os.Build.DEVICE);
        mDataWhiteList.removeAll(MESSAGE_APPS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkWallpaper() {
        try {
            String wallpaperName = MilletUtils.getWallpaperName(this.mContext);
            if (wallpaperName != null) {
                int packageUidAsUser = PackageManagerCompat.getPackageUidAsUser(this.pm, wallpaperName, this.mContext.getUserId());
                if (packageUidAsUser != this.uidWallpaper) {
                    Log.d(TAG, "new wallpaper uid: " + packageUidAsUser);
                    this.uidWallpaper = packageUidAsUser;
                }
            } else {
                this.uidWallpaper = -1;
            }
        } catch (Exception e2) {
            Log.e(TAG, "checkWallpaper err", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dealSleepMode(int i2) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(mDataWhiteList);
        if (i2 == 1) {
            this.mUseDataWhiteList.removeAll(mDataWhiteList);
            dealSleepModeWhiteList(mDataWhiteList, true);
        } else {
            if (i2 != 2) {
                return;
            }
            this.mUseDataWhiteList.addAll(mDataWhiteList);
            dealSleepModeWhiteList(mDataWhiteList, false);
        }
        Iterator<Integer> it = MilletUtils.getUidsForNames(this.mContext, arrayList).iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            if (MilletUtils.isAppUid(iIntValue)) {
                Message messageObtain = Message.obtain();
                messageObtain.obj = i2 == 1 ? "sleep entry" : "sleep exit";
                messageObtain.arg1 = iIntValue;
                messageObtain.arg2 = i2 == 1 ? 1 : 0;
                messageObtain.what = 1;
                this.mHandler.sendMessage(messageObtain);
            }
        }
    }

    private void dealSleepModeWhiteList(List<String> list, boolean z2) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(MESSAGE_APPS);
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "key_open_earthquake_warning", 0, 0) == 0) {
            arrayList.add(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
            if (z2) {
                this.mUseDataWhiteList.remove(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
            } else {
                this.mUseDataWhiteList.add(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, (String) it.next());
            if (this.mHandler != null && uidByPackageName != -1 && MilletUtils.isAppUid(uidByPackageName)) {
                Message messageObtain = Message.obtain();
                messageObtain.obj = z2 ? "sleep entry" : "sleep exit";
                messageObtain.arg1 = uidByPackageName;
                messageObtain.arg2 = z2 ? 1 : 0;
                messageObtain.what = 1;
                this.mHandler.sendMessage(messageObtain);
            }
        }
    }

    private void getExternalApps(Context context) {
        Iterator<String> it = BRIGHT_WHITE_APPS.iterator();
        while (it.hasNext()) {
            int uidByPackageName = PackageUtil.getUidByPackageName(context, it.next());
            if (MilletUtils.isAppUid(uidByPackageName)) {
                this.mBrightWhiteUids.add(Integer.valueOf(uidByPackageName));
            }
        }
        String string = Settings.System.getString(context.getContentResolver(), "millet_black");
        if (string != null && !string.isEmpty()) {
            for (String str : string.split(";")) {
                Log.d(TAG, "getExternalApps black:" + str);
                if (!this.mUseSystemBlackList.contains(str)) {
                    this.mUseSystemBlackList.add(str);
                }
                this.mUseDataWhiteList.remove(str);
            }
        }
        String string2 = Settings.System.getString(context.getContentResolver(), "millet_white");
        if (string2 == null || string2.isEmpty()) {
            return;
        }
        for (String str2 : string2.split(";")) {
            Log.d(TAG, "getExternalApps white:" + str2);
            if (!this.mUseDataWhiteList.contains(str2)) {
                this.mUseDataWhiteList.add(str2);
            }
            this.mUseSystemBlackList.remove(str2);
        }
    }

    private int getImeUid(int i2) {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "default_input_method", i2);
        if (stringForUser == null) {
            return -1;
        }
        int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, stringForUser.split("/")[0]);
        if (UserHandle.isApp(uidByPackageName)) {
            return uidByPackageName;
        }
        return -1;
    }

    private boolean isAllowAutoStart(String str, int i2) {
        if (!Build.IS_STABLE_VERSION) {
            return false;
        }
        AppOpsManager appOpsManager = this.mAppOpsManager;
        if (appOpsManager != null) {
            return appOpsManager.checkOpNoThrow(10008, i2, str) == 0;
        }
        Log.d(TAG, "isAllowAutoStart err pkg=" + str + " uid=" + i2);
        return true;
    }

    private boolean isAllowWallpaper(int i2, String str) {
        if (this.mOldScreenState != 2) {
            int i3 = Settings.Secure.getInt(this.mContext.getContentResolver(), "doze_always_on", 0);
            if (str != null && str.startsWith("com.miui.mi") && i3 == 1) {
                return false;
            }
        } else if (i2 == this.uidWallpaper) {
            return false;
        }
        return true;
    }

    public static boolean isExtremeMode() {
        return mExtremeMode;
    }

    private boolean isGoogleApp(int i2) {
        String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i2);
        if (pkgNameByUid == null || !pkgNameByUid.startsWith("com.google.") || pkgNameByUid.contains("quicksearch")) {
            return pkgNameByUid != null && "com.android.vending".equals(pkgNameByUid);
        }
        return true;
    }

    private boolean isImeUid(int i2) {
        for (int i3 = 0; i3 < this.mUserIMEUid.size(); i3++) {
            if (i2 == this.mUserIMEUid.valueAt(i3)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInPkgList(int i2, boolean z2) {
        String[] packagesForUid = MilletUtils.getPackagesForUid(i2);
        List<String> list = z2 ? this.pkgWhiteList : this.pkgBlackList;
        if (packagesForUid == null || packagesForUid.length <= 0) {
            Log.d(TAG, "isInPkgList uid= " + i2 + " not found pkgname");
        } else {
            for (String str : packagesForUid) {
                for (String str2 : list) {
                    if (str.equals(str2)) {
                        String str3 = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("isInPkgList uid= ");
                        sb.append(i2);
                        sb.append(" name=");
                        sb.append(str2);
                        sb.append(" in ");
                        sb.append(z2 ? "whitelist" : "blacklist");
                        Log.d(str3, sb.toString());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isIncludeDataWhite(int i2) {
        String[] packagesForUid = MilletUtils.getPackagesForUid(i2);
        if (packagesForUid == null || packagesForUid.length == 0) {
            Log.d(TAG, "isAllowFrozenPackage names= null uid=" + i2);
            return false;
        }
        for (String str : packagesForUid) {
            if (this.mUseDataWhiteList.contains(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean isIncludeSystemBlack(int i2) {
        String[] packagesForUid = MilletUtils.getPackagesForUid(i2);
        if (packagesForUid == null || packagesForUid.length == 0) {
            Log.d(TAG, "isAllowFrozenPackage names= null uid=" + i2);
            return false;
        }
        for (String str : packagesForUid) {
            if (this.mUseSystemBlackList.contains(str)) {
                return true;
            }
        }
        return false;
    }

    private void setAlarm(String str, long j2) {
        long jElapsedRealtime = SystemClock.elapsedRealtime() + j2;
        Intent intent = new Intent();
        intent.setAction(str);
        this.alarm.setExactAndAllowWhileIdle(2, jElapsedRealtime, PendingIntent.getBroadcast(this.mContext, 0, intent, 201326592));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void thawedDelayList(long j2) {
        if (this.delayList.size() == 0 || this.mHandler == null) {
            return;
        }
        Message messageObtain = Message.obtain();
        messageObtain.what = 12;
        this.mHandler.sendMessageDelayed(messageObtain, j2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAccessUids() {
        this.mAccessibilityUids.clear();
        Iterator<String> it = MilletUtils.getEnabeledAccessibilityService(this.mContext).iterator();
        while (it.hasNext()) {
            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, it.next());
            if (UserHandle.isApp(uidByPackageName)) {
                this.mAccessibilityUids.add(Integer.valueOf(uidByPackageName));
            }
        }
    }

    public void cancelAlarm(String str) {
        Intent intent = new Intent();
        intent.setAction(str);
        ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(this.mContext, 0, intent, 201326592));
    }

    public void checkImportantApps() {
        this.mImportantApps.clear();
        for (int i2 : FreezeBinder.getInstance().getImportantApps()) {
            this.mImportantApps.add(Integer.valueOf(i2));
        }
    }

    public boolean checkIntervalThawTime(int i2) {
        if (this.mThawUidTime.indexOfKey(i2) <= 0 || SystemClock.uptimeMillis() - this.mThawUidTime.get(i2) > 3000) {
            return false;
        }
        Log.d(TAG, "checkIntervalThawTime uid=" + i2);
        return true;
    }

    public void checkSleepMode() {
        if (this.mUseDataWhiteList.contains("com.miui.virtualsim")) {
            return;
        }
        Log.d(TAG, "maybe receive no broadcast");
        dealSleepMode(2);
    }

    public void dealBluetoothEvent(Bundle bundle) {
        this.mBluetoothEvent.dealBluetoothEvent(bundle);
    }

    public void dealBluetoothUidDied(int i2) {
        this.mBluetoothEvent.dealBluetoothUidDied(i2);
    }

    public void dealCloudWhiteList() {
        String cloudFrozenNewWhiteList = FrozenAppController.newCloudWhiteList;
        if (cloudFrozenNewWhiteList == null) {
            cloudFrozenNewWhiteList = GlobalFeatureConfigureHelper.getCloudFrozenNewWhiteList(this.mContext);
        }
        Log.d(TAG, "dealCloudWhiteList curDealConfig=" + cloudFrozenNewWhiteList);
        if (cloudFrozenNewWhiteList == null || cloudFrozenNewWhiteList.isEmpty()) {
            return;
        }
        for (String str : cloudFrozenNewWhiteList.split(";")) {
            if (str.length() > 5) {
                this.mUseDataWhiteList.add(str);
                this.mCloudWhiteList.add(str);
            }
        }
    }

    public void dealFindDeviceOpen(Context context) {
        if (MilletUtils.isFindDeviceOpen(context)) {
            mDataWhiteList.add(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
            this.mUseDataWhiteList.add(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        } else {
            mDataWhiteList.remove(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
            this.mUseDataWhiteList.remove(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        }
    }

    public void dealNoRestrictApp() {
        List<String> noRestrictApps = UserConfigureHelper.getNoRestrictApps(this.mContext);
        if (noRestrictApps == null || noRestrictApps.size() == 0) {
            Iterator<String> it = this.mNoRestrictSet.iterator();
            while (it.hasNext()) {
                try {
                    String next = it.next();
                    this.mUseDataWhiteList.remove(next);
                    it.remove();
                    Log.d(TAG, "del noRestr:" + next);
                } catch (Exception unused) {
                }
            }
            Settings.System.putString(this.mContext.getContentResolver(), KEY_NO_RESTRICT_APP, this.mNoRestrictSet.toString().substring(1, this.mNoRestrictSet.toString().length() - 1));
            return;
        }
        HashSet hashSet = new HashSet();
        hashSet.addAll(noRestrictApps);
        if (hashSet.equals(this.mNoRestrictSet)) {
            return;
        }
        if (this.mNoRestrictSet.size() > 0) {
            Iterator<String> it2 = this.mNoRestrictSet.iterator();
            while (it2.hasNext()) {
                String next2 = it2.next();
                if (!noRestrictApps.contains(next2)) {
                    this.mUseDataWhiteList.remove(next2);
                    it2.remove();
                    Log.d(TAG, "del noRestr:" + next2);
                }
            }
        }
        if (noRestrictApps.size() > 0) {
            for (String str : noRestrictApps) {
                if (!this.mNoRestrictSet.contains(str)) {
                    this.mUseDataWhiteList.add(str);
                    this.mNoRestrictSet.add(str);
                    Log.d(TAG, "add noRestr:" + str);
                }
            }
        }
        Context context = this.mContext;
        if (context != null) {
            Settings.System.putString(context.getContentResolver(), KEY_NO_RESTRICT_APP, this.mNoRestrictSet.toString().substring(1, this.mNoRestrictSet.toString().length() - 1));
        }
    }

    public int getCurrentUserId() {
        try {
            UserInfo currentUser = ActivityManager.getService().getCurrentUser();
            if (currentUser != null) {
                return currentUser.id;
            }
            return 0;
        } catch (Exception unused) {
            Log.d(TAG, "getCurrentUserId error");
            return 0;
        }
    }

    public Set<Integer> getDelayUids() {
        return this.delayList;
    }

    public List<Integer> getImportantApps() {
        return this.mImportantApps;
    }

    public List<Integer> getReportList() {
        ArrayList arrayList = new ArrayList();
        addList(arrayList, this.pkgWhiteList);
        addList(arrayList, this.pkgGrayList);
        return arrayList;
    }

    public List<Integer> getSpecialBusiness() {
        return this.mSpecialBusiness;
    }

    public SparseLongArray getThawTime() {
        return this.mThawUidTime;
    }

    public int getWallpaper() {
        return this.uidWallpaper;
    }

    public boolean isAllowFreeze(int i2) {
        String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i2);
        if (this.mMilletMode == MilletUtils.CLOUD_BLACK_OPEN) {
            if (MilletUtils.getCloudBlackApps().contains(pkgNameByUid)) {
                Log.d(TAG, "black uid = " + i2);
                return true;
            }
            Log.d(TAG, "black open mode uid = " + i2);
            return false;
        }
        if (MilletUtils.isCtsPackage(i2, pkgNameByUid, this.mContext) || this.mNoRestrictSet.contains(pkgNameByUid)) {
            return false;
        }
        if (!MilletUtils.isAppUid(i2) || i2 == this.uidVpnActive || isImeUid(i2)) {
            if (MilletUtils.isAppUid(i2)) {
                Log.d(TAG, "ime uid =" + i2);
            }
            return false;
        }
        if (this.mImportantApps.contains(Integer.valueOf(i2)) || !isAllowWallpaper(i2, pkgNameByUid)) {
            return false;
        }
        BluetoothEvent bluetoothEvent = this.mBluetoothEvent;
        if (bluetoothEvent != null && bluetoothEvent.isBluetoothActive(i2)) {
            Log.d(TAG, "bluetooth uid =" + i2);
            return false;
        }
        if (this.mDeviceOwner == i2 || this.mAccessibilityUids.contains(Integer.valueOf(i2))) {
            Log.d(TAG, "deviceownOrAcces uid=" + i2 + " name=" + pkgNameByUid + " own=" + this.mDeviceOwner);
            return false;
        }
        if (isNotAllowFrozenMusic(i2)) {
            return false;
        }
        if (SYNC_UIDS.contains(Integer.valueOf(i2))) {
            Log.d(TAG, "syncing uid=" + i2);
            return false;
        }
        if (MilletUtils.getDownload(i2, pkgNameByUid)) {
            Log.d(TAG, "download uid =" + i2);
            return false;
        }
        if (isIncludeDataWhite(i2) || this.mCloudWhiteList.contains(pkgNameByUid)) {
            if (this.mCloudWhiteList.contains(pkgNameByUid)) {
                Log.d(TAG, "white uid =" + i2);
            }
            return false;
        }
        boolean isSystemApp = MilletUidObserver.getIsSystemApp(i2);
        if (isBrightWhite(i2) && this.mOldScreenState == 2) {
            return false;
        }
        if (isIncludeSystemBlack(i2)) {
            if (FrozenAppController.getBatteryPlugged()) {
                return false;
            }
            if (!MilletConfig.isGlobal) {
                return true;
            }
        }
        if (MilletConfig.isGlobal) {
            if (pkgNameByUid != null && isGoogleApp(i2)) {
                return false;
            }
            if (isAllowAutoStart(pkgNameByUid, i2)) {
                if (!isSystemApp) {
                    Log.d(TAG, "isAllowFreeze autostart uid=" + i2 + " pkgName=" + pkgNameByUid);
                }
                return false;
            }
        }
        return !isSystemApp;
    }

    public boolean isAllowSync(int i2, String str) {
        return MilletUtils.isSystemApp(this.mContext, i2);
    }

    public boolean isBrightWhite(int i2) {
        return this.mBrightWhiteUids.contains(Integer.valueOf(i2)) || isGoogleApp(i2);
    }

    public boolean isCycleCheck(int i2) {
        if (isIncludeDataWhite(i2) || this.mAccessibilityUids.contains(Integer.valueOf(i2))) {
            return false;
        }
        String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i2);
        return ((pkgNameByUid != null && this.mNoRestrictSet.contains(pkgNameByUid)) || this.mDeviceOwner == i2 || SYNC_UIDS.contains(Integer.valueOf(i2))) ? false : true;
    }

    public boolean isGameUid(int i2, Context context) {
        String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i2);
        if (pkgNameByUid != null) {
            return pkgNameByUid.contains(".miHoYo.") || pkgNameByUid.contains("t.lolm") || pkgNameByUid.contains("t.jkchess") || pkgNameByUid.contains("t.tmgp.");
        }
        return false;
    }

    public boolean isInWhiteList(int i2) {
        return isIncludeDataWhite(i2) || this.mCloudWhiteList.contains(MilletUidObserver.getPkgNameByUid(i2));
    }

    public boolean isNotAllowFrozenMusic(int i2) {
        Context context = this.mContext;
        if (context == null) {
            Log.d(TAG, "isNot ctx null");
            return true;
        }
        int[] pidsForUid = MilletUtils.getPidsForUid(i2, context);
        if (pidsForUid.length == 0) {
            Log.d(TAG, "pids 0");
            return true;
        }
        ArrayList arrayList = new ArrayList();
        for (int i3 : pidsForUid) {
            arrayList.add(Integer.valueOf(i3));
        }
        boolean zIsUidForegroundService = PowerKeeperManager.getInstance().getProcessObserver().isUidForegroundService(i2);
        String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i2);
        if (isUidActive(i2)) {
            return true;
        }
        if (!zIsUidForegroundService || pkgNameByUid == null || musicApp.contains(pkgNameByUid)) {
            return false;
        }
        if (this.mOldScreenState == 1 && this.mSpecialBusiness.contains(Integer.valueOf(i2))) {
            return false;
        }
        if (MilletConfig.getRestrictFg(pkgNameByUid)) {
            Log.d(TAG, "fg uid=" + i2);
            return false;
        }
        Log.d(TAG, "HasForegroundService pkgName=" + pkgNameByUid + " uid:" + i2);
        return true;
    }

    public boolean isUidActive(int i2) {
        if (PowerKeeperManager.getInstance().getActiveController().getActiveUids(24).contains(Integer.valueOf(i2))) {
            Log.d(TAG, "active uid = " + i2 + " type =" + PowerKeeperManager.getInstance().getActiveController().getAppActiveType(i2));
            return true;
        }
        if (MilletUtils.isUidActive(i2, 2)) {
            Log.d(TAG, "active gps uid =" + i2);
            return true;
        }
        List<Integer> activeUids = PowerKeeperManager.getInstance().getActiveController().getActiveUids(3);
        boolean zIsUidActive = MilletUtils.isUidActive(i2, 1);
        if (!activeUids.contains(Integer.valueOf(i2)) && !zIsUidActive) {
            if (this.mLastPlayUid != i2) {
                return false;
            }
            long jCurrentTimeMillis = System.currentTimeMillis() - this.mLastAudioStopTime;
            this.mLastAudioStopTime = System.currentTimeMillis();
            if (jCurrentTimeMillis <= PluginConstants.TTS_REMOVE_BEAN_TIME) {
                return false;
            }
            Log.d(TAG, "maybe change song,set audio state still active, uid =" + i2);
            this.mLastPlayUid = -1;
            return true;
        }
        if (!zIsUidActive || !activeUids.contains(Integer.valueOf(i2))) {
            Log.d(TAG, "uid =" + i2 + " ret =" + zIsUidActive + " contains:" + activeUids.contains(Integer.valueOf(i2)));
        }
        this.mLastPlayUid = i2;
        Log.d(TAG, "active audio uid=" + i2);
        return true;
    }

    public void onCloudWhiteListChanged() {
        this.mUseDataWhiteList.removeAll(this.mCloudWhiteList);
        this.mCloudWhiteList.clear();
        dealCloudWhiteList();
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.a0
    public void onSleepMode(boolean z2) {
        Log.d(TAG, "onSleepMode start=" + z2);
        dealSleepMode(z2 ? 1 : 2);
    }

    public void registerEvent() {
        IntentFilter intentFilter = new IntentFilter(ACTION_PKG_ALARM);
        intentFilter.addAction("android.intent.action.WALLPAPER_CHANGED");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        this.mContext.registerReceiver(this.mReceiver, intentFilter, 2);
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter(ACTION_BLUETOOTH_NOTIFY_APP_DEVICE_FOUND), Manifest.permission.USE_INTERNAL_GENERAL_API, null, 2);
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        PowerKeeperManager.getInstance().registerIMEChangeListener(new PowerKeeperInterface.w(this.mHandler, this.mIMEChangedListener));
        this.mUserIMEUid.put(0, getImeUid(0));
        if (getCurrentUserId() > 0) {
            this.mUserIMEUid.put(getCurrentUserId(), getImeUid(getCurrentUserId()));
        }
        Slog.d(TAG, "currentIme=" + this.mUserIMEUid.get(getCurrentUserId()) + " userId:" + getCurrentUserId());
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("enabled_accessibility_services"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor(this.PC_SECURITY_CENTER_EXTREME_MODE), false, this.mSettingsObserver, -2);
        contentResolver.registerContentObserver(Settings.System.getUriFor(SETTINGS_SYSTEM_SPECIAL_BUSINESS), false, this.mSettingsObserver, -2);
        contentResolver.registerContentObserver(UserConfigure.CONTENT_URI, true, this.mSettingsObserver);
    }

    public void removeBluetoothUid(int i2) {
        this.mBluetoothEvent.removeBluetoothUid(i2);
    }

    public void resetDelayList() {
        cancelAlarm(ACTION_PKG_ALARM);
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(12);
        }
        this.delayList.clear();
        this.pkgFirstTime = 0L;
    }

    public void setHandler(Handler handler, HandlerThread handlerThread) {
        this.mHandler = handler;
        ((DisplayManager) this.mContext.getSystemService("display")).registerDisplayListener(new MyDisplayListener(), this.mHandler);
        this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletPolicy.6
            @Override // java.lang.Runnable
            public void run() {
                SleepModeControllerNew sleepModeControllerNew = SleepModeControllerNew.getInstance();
                if (sleepModeControllerNew != null) {
                    sleepModeControllerNew.registerSleepListener(MilletPolicy.this);
                }
                MilletPolicy milletPolicy = MilletPolicy.this;
                milletPolicy.mDeviceOwner = MilletUtils.getDeviceOwner(milletPolicy.mContext);
                MilletPolicy.this.updateAccessUids();
            }
        }, PluginConstants.TTS_REMOVE_BEAN_TIME);
        this.mBluetoothEvent = BluetoothEvent.getInstance(this.mContext, this.mHandler);
    }

    public Bundle setList(boolean z2, String str, boolean z3) {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<String> list = z2 ? this.pkgWhiteList : this.pkgGrayList;
        Bundle bundle = new Bundle();
        if (z3) {
            if (list.contains(str)) {
                return null;
            }
            list.add(str);
        } else {
            if (!list.contains(str)) {
                return null;
            }
            list.remove(str);
        }
        try {
            int packageUidAsUser = PackageManagerCompat.getPackageUidAsUser(packageManager, str, this.mContext.getUserId());
            if (!MilletUtils.isAppUid(packageUidAsUser)) {
                return null;
            }
            bundle.putInt("uid", packageUidAsUser);
            bundle.putInt("add", z3 ? 1 : -1);
            return bundle;
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        } catch (Exception e2) {
            Log.e(TAG, "setList name=" + str, e2);
            return null;
        }
    }

    public void setScreenOffMilletMode(int i2) {
        this.mMilletMode = i2;
        Log.d(TAG, "setScreenOffMilletMode mode=" + i2);
    }

    public void updateSpecialBusiness() {
        String string = Settings.System.getString(this.mContext.getContentResolver(), SETTINGS_SYSTEM_SPECIAL_BUSINESS);
        this.mSpecialBusiness.clear();
        if (string == null) {
            return;
        }
        for (String str : string.split(",")) {
            try {
                int packageUidAsUser = PackageManagerCompat.getPackageUidAsUser(this.pm, str, this.mContext.getUserId());
                if (MilletUtils.isAppUid(packageUidAsUser)) {
                    this.mSpecialBusiness.add(Integer.valueOf(packageUidAsUser));
                }
            } catch (Exception unused) {
            }
        }
        Log.d(TAG, "special busi:" + this.mSpecialBusiness.toString());
    }

    public void dealWhiteApp(boolean z2, Set<Integer> set) {
    }

    public void dealPacket(int i2, boolean z2, Handler handler) {
    }
}
