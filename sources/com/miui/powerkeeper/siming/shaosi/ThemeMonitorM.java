package com.miui.powerkeeper.siming.shaosi;

import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.BrightnessInfo;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.ArrayMap;
import com.android.internal.display.BrightnessSynchronizer;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.batterylife.systeminfo.CpuStatus;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.event.EventMonitor;
import com.miui.powerkeeper.siming.SiUtils;
import com.miui.powerkeeper.siming.Subdivisions;
import com.miui.powerkeeper.siming.dasi.MIY;
import com.miui.powerkeeper.tracker.TrackerManager;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import com.xiaomi.mipush.sdk.stat.db.MessageInfoContract;
import com.xiaomi.push.extension.PluginConstants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.app.IFreeformCallback;
import miui.app.MiuiFreeFormManager;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class ThemeMonitorM extends M {
    private static final long DEFAULT_MINIMUM_INTERVAL_FAILED = 1200000;
    private static final long DEFAULT_MINIMUM_INTERVAL_SUCCESS = 7200000;
    private static final String LAUNCHER = "com.miui.home";
    public static final String ONE_TRACK_EVENT_THEME_MONITOR = "Theme_Power";
    private static final String TAG = "SM_S_ThemeMonitorN";
    public static final int THEME_TYPE_MAML = 1;
    private static final int THEME_TYPE_MAML_DYNAMIC = 2;
    private static final int THEME_TYPE_NORMAL = 0;
    private static int mStartId;
    private volatile boolean bInFreeformMode;
    private volatile boolean bMonitoringFreeform;
    private boolean bWaitingNewTheme;
    private volatile boolean bWindowStatusAbnormal;
    private final EventMonitor.EventCallback eventCallback;
    private final IFreeformCallback freeformCallback;
    private int mBrightCount;
    private final ArrayList<String> mCachedHistoryFinished;
    private boolean mCharging;
    private int mCount;
    private final ArrayList<CPUInfo> mCpuInfoList;
    private ThemeInfo mCurrentThemeInfo;
    private String mDrmFileDebug;
    private ForegroundInfo mFgInfo;
    private final FrameInfo mFrameInfo;
    private long mLastSampled;
    private long mMinInterval;
    private int mScreenOffTimeout;
    private long mStartGpuTime;
    private long mStartTime;
    private volatile boolean mStartTrack;
    private final ArrayList<ThemeInfo> mThemeInfoList;
    private int mTotalBrightness;
    private double mTotalCurrent;
    private boolean mTracking;
    private final HashMap<String, UploadInfo> mUploadMap;
    private final IWhetstoneActivityManager mWs;

    static class CPUInfo {
        public long endCpuTime;
        public int pid;
        public String pkgName;
        public long startCpuTime;

        CPUInfo(String str, int i2) {
            this.pkgName = str;
            this.pid = i2;
        }
    }

    static class FrameInfo {
        public static final String FRAME_LAUNCHER = "com.miui.home-Launcher";
        public static final String FRAME_WALLPAPER = "Wallpaper BBQ wrapper";
        int totalFrameForLauncher;
        int totalFrameForWallpaper;
        long totalTime = 0;

        FrameInfo() {
        }

        public static FrameInfo clone(FrameInfo frameInfo) {
            FrameInfo frameInfo2 = new FrameInfo();
            frameInfo2.totalFrameForWallpaper = frameInfo.totalFrameForWallpaper;
            frameInfo2.totalFrameForLauncher = frameInfo.totalFrameForLauncher;
            frameInfo2.totalTime = frameInfo.totalTime;
            return frameInfo2;
        }

        public void reset() {
            this.totalTime = 0L;
            this.totalFrameForLauncher = 0;
            this.totalFrameForWallpaper = 0;
        }

        public String toString() {
            if (this.totalTime == 0) {
                return "NULL";
            }
            return "FrameInfo(" + (this.totalTime / 1000) + "){" + FRAME_LAUNCHER + ":" + this.totalFrameForLauncher + "|" + ((((long) this.totalFrameForLauncher) * 1000) / this.totalTime) + ";" + FRAME_WALLPAPER + ":" + this.totalFrameForWallpaper + "|" + ((((long) this.totalFrameForWallpaper) * 1000) / this.totalTime) + "}";
        }
    }

    static class ThemeInfo {
        public int averageCurrent;
        public int bright;
        public long duration;
        public long gpuTime;
        public String hashCode;
        public String[] params;
        public int themeType;
        public long updateTime;
        public FrameInfo frameInfo = null;
        public ArrayList<CPUInfo> cpuInfos = new ArrayList<>();

        public ThemeInfo(String[] strArr) {
            this.params = new String[5];
            if (strArr.length != 5) {
                SiUtils.Log.w(ThemeMonitorM.TAG, "ThemeInfo is error.");
                return;
            }
            this.params = (String[]) Arrays.copyOf(strArr, strArr.length);
            this.hashCode = Integer.toHexString((strArr[0] + strArr[1] + strArr[2]).hashCode());
            if (!strArr[3].contains("true")) {
                this.themeType = 0;
                return;
            }
            this.themeType = 1;
            if (strArr[4].contains("true")) {
                this.themeType = 2;
            }
        }

        public String toString() {
            FrameInfo frameInfo = this.frameInfo;
            String string = frameInfo == null ? "NULL" : frameInfo.toString();
            StringBuilder sb = new StringBuilder();
            for (CPUInfo cPUInfo : this.cpuInfos) {
                if (cPUInfo.endCpuTime < cPUInfo.startCpuTime) {
                    SiUtils.Log.w(ThemeMonitorM.TAG, "Cpu time error.");
                } else {
                    sb.append(cPUInfo.pkgName);
                    sb.append(":");
                    sb.append(cPUInfo.endCpuTime - cPUInfo.startCpuTime);
                    sb.append(";");
                }
            }
            return "#" + this.hashCode + ": title=" + this.params[0] + ",onlineId=" + this.params[1] + ",localId=" + this.params[2] + ",updateTime=" + this.updateTime + ",themeType=" + this.themeType + ",averageCurrent=" + this.averageCurrent + ",duration=" + this.duration + ",gpuTime=" + this.gpuTime + ",cpuTime={" + ((Object) sb) + "},frameInfo={" + string + "}";
        }
    }

    static class UploadInfo {
        public String hashCode;
        public long lastUploadTime;
        public int uploadFailedTimes;
        public int uploadTimes;

        UploadInfo() {
        }

        public String toString() {
            return "#" + this.hashCode + ": lastUploadTime=" + this.lastUploadTime + ", uploadTimes=" + this.uploadTimes + ", uploadFailedTimes=" + this.uploadFailedTimes;
        }
    }

    protected ThemeMonitorM(Looper looper) {
        super(looper, 300);
        this.bWaitingNewTheme = true;
        this.mStartTrack = false;
        this.mTotalCurrent = 0.0d;
        this.mCount = 0;
        this.mTotalBrightness = 0;
        this.mBrightCount = 0;
        this.mTracking = false;
        this.mFrameInfo = new FrameInfo();
        this.mCpuInfoList = new ArrayList<>();
        this.mThemeInfoList = new ArrayList<>();
        this.mUploadMap = new HashMap<>();
        this.mCachedHistoryFinished = new ArrayList<>();
        this.bMonitoringFreeform = false;
        this.bInFreeformMode = false;
        this.bWindowStatusAbnormal = false;
        this.mMinInterval = DEFAULT_MINIMUM_INTERVAL_FAILED;
        this.mDrmFileDebug = "";
        this.mScreenOffTimeout = 0;
        this.freeformCallback = new IFreeformCallback.Stub() { // from class: com.miui.powerkeeper.siming.shaosi.ThemeMonitorM.1
            public void dispatchFreeFormStackModeChanged(int i2, MiuiFreeFormManager.MiuiFreeFormStackInfo miuiFreeFormStackInfo) throws RemoteException {
                SiUtils.Log.d(ThemeMonitorM.TAG, "dispatchFreeFormStackModeChanged i=" + i2);
                List allFreeFormStackInfosOnDisplay = MiuiFreeFormManager.getAllFreeFormStackInfosOnDisplay(0);
                SiUtils.Log.d(ThemeMonitorM.TAG, "dispatchFreeFormStackModeChanged stack size=" + allFreeFormStackInfosOnDisplay.size());
                if (allFreeFormStackInfosOnDisplay.isEmpty()) {
                    ThemeMonitorM.this.bInFreeformMode = false;
                    ThemeMonitorM.this.maybeStartTrack();
                } else if ((i2 == 3 || i2 == 5) && allFreeFormStackInfosOnDisplay.size() == 1) {
                    ThemeMonitorM.this.bInFreeformMode = false;
                    ThemeMonitorM.this.maybeStartTrack();
                } else {
                    ThemeMonitorM.this.bInFreeformMode = true;
                    ThemeMonitorM.this.stopTrack();
                }
            }
        };
        this.eventCallback = new EventMonitor.EventCallback() { // from class: com.miui.powerkeeper.siming.shaosi.b
            @Override // com.miui.powerkeeper.event.EventMonitor.EventCallback
            public final void notifyEvent(int i2, Bundle bundle) {
                this.f555a.lambda$new$1(i2, bundle);
            }
        };
        IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        this.mWs = iWhetstoneActivityManagerAsInterface;
        if (iWhetstoneActivityManagerAsInterface == null) {
            SiUtils.Log.w(TAG, "mWs interface error.");
        }
        if (mayDisable()) {
            return;
        }
        this.mLastSampled = System.currentTimeMillis();
        this.mHandler.sendEmptyMessage(0);
        this.mCharging = PowerKeeperManager.getInstance().isCharging();
        this.mScreenOffTimeout = getScreenOffTimeout();
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_off_timeout"), true, new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.siming.shaosi.ThemeMonitorM.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                ThemeMonitorM themeMonitorM = ThemeMonitorM.this;
                themeMonitorM.mScreenOffTimeout = themeMonitorM.getScreenOffTimeout();
                SiUtils.Log.w(ThemeMonitorM.TAG, "SCREEN_OFF_TIMEOUT changed, timeout=" + ThemeMonitorM.this.mScreenOffTimeout + "ms");
            }
        });
    }

    private void calculate() {
        int i2;
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mLastSampled = jCurrentTimeMillis;
        long j2 = jCurrentTimeMillis - this.mStartTime;
        if (this.bDebug) {
            this.mMinInterval = 10000L;
        } else {
            this.mMinInterval = DEFAULT_MINIMUM_INTERVAL_FAILED;
        }
        if (j2 < PluginConstants.TTS_REVERT_VOLUME_TIME) {
            SiUtils.Log.w(TAG, "calculate!! Time is too short. Discard.");
            return;
        }
        if (this.mCount == 0) {
            SiUtils.Log.w(TAG, "calculate!! Count is 0. Discard.");
            return;
        }
        log(TAG, "calculate.");
        ThemeInfo themeInfo = new ThemeInfo(this.mCurrentThemeInfo.params);
        if (this.mThemeInfoList.size() > 4) {
            this.mThemeInfoList.remove(0);
        }
        this.mThemeInfoList.add(themeInfo);
        themeInfo.updateTime = this.mCurrentThemeInfo.updateTime;
        themeInfo.averageCurrent = ((int) this.mTotalCurrent) / this.mCount;
        int i3 = this.mTotalBrightness;
        if (i3 != 0 && (i2 = this.mBrightCount) != 0) {
            themeInfo.bright = i3 / i2;
        }
        themeInfo.duration = j2;
        themeInfo.gpuTime = getGpuTimes() - this.mStartGpuTime;
        themeInfo.frameInfo = FrameInfo.clone(this.mFrameInfo);
        updateCpuTimes();
        themeInfo.cpuInfos.addAll(this.mCpuInfoList);
        this.mCpuInfoList.clear();
        maybeUpload(themeInfo);
    }

    private boolean canTrack() {
        ForegroundInfo foregroundInfo;
        if (this.mCurrentThemeInfo == null) {
            SiUtils.Log.d(TAG, "Can't track. mCurrentThemeInfo is null.");
            return false;
        }
        if (this.mCharging || (foregroundInfo = this.mFgInfo) == null || !foregroundInfo.mForegroundPackageName.equals(LAUNCHER)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Can't track. mCharging=");
            sb.append(this.mCharging);
            sb.append(", mFgInfo=");
            ForegroundInfo foregroundInfo2 = this.mFgInfo;
            sb.append(foregroundInfo2 == null ? "null" : foregroundInfo2.mForegroundPackageName);
            SiUtils.Log.d(TAG, sb.toString());
            return false;
        }
        if (this.bInFreeformMode) {
            SiUtils.Log.d(TAG, "Can't track. In FreeForm mode.");
            return false;
        }
        if (this.bWindowStatusAbnormal) {
            SiUtils.Log.d(TAG, "Can't track. Launcher is not on Top.");
            return false;
        }
        UploadInfo uploadInfo = this.mUploadMap.get(this.mCurrentThemeInfo.hashCode);
        if (uploadInfo == null || uploadInfo.uploadTimes < 3) {
            return true;
        }
        SiUtils.Log.d(TAG, "Can't track. Has upload 3 times for " + this.mCurrentThemeInfo.params[0]);
        return false;
    }

    private boolean canUpload(ThemeInfo themeInfo) {
        if (themeInfo.bright > 3000) {
            SiUtils.Log.d(TAG, "Ignore for bright. bright=" + themeInfo.bright);
            return false;
        }
        int powerModelCurrent = getPowerModelCurrent(themeInfo.themeType);
        SiUtils.Log.d(TAG, "mamlPower = " + powerModelCurrent + ", bright=" + themeInfo.bright);
        int i2 = themeInfo.themeType;
        if (i2 == 0) {
            return themeInfo.averageCurrent > powerModelCurrent;
        }
        if (i2 == 1) {
            return themeInfo.averageCurrent > powerModelCurrent;
        }
        long j2 = 0;
        for (CPUInfo cPUInfo : themeInfo.cpuInfos) {
            if (cPUInfo.pkgName.equals("com.miui.miwallpaper")) {
                j2 = cPUInfo.endCpuTime - cPUInfo.startCpuTime;
            }
        }
        return ((double) j2) > ((double) themeInfo.duration) * 0.6d || themeInfo.averageCurrent > powerModelCurrent;
    }

    private void fgInfoChanged(ForegroundInfo foregroundInfo) {
        log(TAG, "fgInfoChanged changed.");
        if (this.bWaitingNewTheme) {
            return;
        }
        ForegroundInfo foregroundInfo2 = this.mFgInfo;
        if (foregroundInfo2 != null && foregroundInfo2.mForegroundPackageName.equals(foregroundInfo.mForegroundPackageName)) {
            SiUtils.Log.d(TAG, "No change! Just return.");
            return;
        }
        ForegroundInfo foregroundInfo3 = this.mFgInfo;
        if (foregroundInfo3 != null && foregroundInfo3.mForegroundPackageName.equals(LAUNCHER)) {
            stopTrack();
            if (this.bMonitoringFreeform) {
                this.bMonitoringFreeform = false;
                SiUtils.Log.d(TAG, "stop monitoring Freeform");
                unregisterForEventCallback();
                MiuiFreeFormManager.unregisterFreeformCallback(this.freeformCallback);
            }
            this.mFgInfo = foregroundInfo;
            return;
        }
        if (!foregroundInfo.mForegroundPackageName.equals(LAUNCHER)) {
            this.mFgInfo = foregroundInfo;
            if (this.bMonitoringFreeform) {
                this.bMonitoringFreeform = false;
                SiUtils.Log.d(TAG, "stop monitoring Freeform");
                unregisterForEventCallback();
                MiuiFreeFormManager.unregisterFreeformCallback(this.freeformCallback);
                return;
            }
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - this.mLastSampled < this.mMinInterval) {
            SiUtils.Log.w(TAG, "Discard1!! Just sampled " + ((jCurrentTimeMillis - this.mLastSampled) / 60000) + " minutes ago.");
            return;
        }
        if (!this.bMonitoringFreeform) {
            this.bMonitoringFreeform = true;
            SiUtils.Log.d(TAG, "start monitoring Freeform");
            MiuiFreeFormManager.registerFreeformCallback(this.freeformCallback);
            this.bInFreeformMode = true ^ MiuiFreeFormManager.getAllFreeFormStackInfosOnDisplay(0).isEmpty();
            this.bWindowStatusAbnormal = false;
            registerForEventCallback(32);
        }
        this.mFgInfo = foregroundInfo;
        maybeStartTrack();
    }

    private long getCpuTimeForPid(int i2) {
        try {
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager != null) {
                return iWhetstoneActivityManager.getCpuTimeForPid(i2);
            }
            log(TAG, "Get CPU time for pid " + i2 + " failed.");
            return 0L;
        } catch (RemoteException unused) {
            SiUtils.Log.w(TAG, "getCpuTimeForPid RemoteException!");
            return 0L;
        }
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0040 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:14:0x0042  */
    private float getCurrentNow() {
        BatteryManager batteryManager;
        int intProperty;
        String sysNodeInfo = BLUtils.readSysNodeInfo("/sys/class/power_supply/battery/current_now");
        if (sysNodeInfo == null || sysNodeInfo.isEmpty()) {
            SiUtils.Log.d(TAG, "getCurrentNow current=" + sysNodeInfo);
            batteryManager = (BatteryManager) PowerKeeperApplication.b().getSystemService("batterymanager");
            if (batteryManager == null) {
                return 0.0f;
            }
            intProperty = batteryManager.getIntProperty(2);
        } else {
            try {
                intProperty = Integer.parseInt(sysNodeInfo);
            } catch (NumberFormatException unused) {
                SiUtils.Log.d(TAG, "getCurrentNow Integer.parseInt NumberFormatException!");
                SiUtils.Log.d(TAG, "getCurrentNow current=" + sysNodeInfo);
                batteryManager = (BatteryManager) PowerKeeperApplication.b().getSystemService("batterymanager");
                if (batteryManager == null) {
                    return 0.0f;
                }
                intProperty = batteryManager.getIntProperty(2);
            }
        }
        return intProperty / 1000.0f;
    }

    private long getGpuTimes() {
        long[] gpuTimeStats = CpuStatus.readGpuTimeStats();
        long j2 = 0;
        for (int i2 = 0; i2 < gpuTimeStats.length - 1; i2++) {
            j2 += gpuTimeStats[i2];
        }
        return j2;
    }

    private int getPowerModelCurrent(int i2) {
        return i2 == 0 ? 400 : 600;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getScreenOffTimeout() {
        try {
            return Settings.System.getInt(this.mContext.getContentResolver(), "screen_off_timeout");
        } catch (Settings.SettingNotFoundException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getThemeInfo() {
        try {
            ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = this.mContext.getContentResolver().acquireUnstableContentProviderClient(Uri.parse("content://com.android.thememanager.theme_provider"));
            if (contentProviderClientAcquireUnstableContentProviderClient == null) {
                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                    contentProviderClientAcquireUnstableContentProviderClient.close();
                    return;
                }
                return;
            }
            try {
                Bundle bundleCall = contentProviderClientAcquireUnstableContentProviderClient.call("getRuntimeFile", null, null);
                SiUtils.Log.d(TAG, "getThemeInfo result=" + bundleCall);
                if (bundleCall != null) {
                    String string = bundleCall.getString("lsr");
                    String string2 = bundleCall.getString("drmFile");
                    SiUtils.Log.d(TAG, "getThemeInfo str=" + string + ", drm=" + string2);
                    if (this.bDebug) {
                        this.mDrmFileDebug = string2;
                    }
                    updateThemeInfo(string2);
                }
                contentProviderClientAcquireUnstableContentProviderClient.close();
            } catch (Throwable th) {
                try {
                    contentProviderClientAcquireUnstableContentProviderClient.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e2) {
            SiUtils.Log.d(TAG, "getThemeInfo " + e2.getMessage());
        }
    }

    private boolean isThemeCheckFinished(ThemeInfo themeInfo) {
        if (this.mCachedHistoryFinished.contains(themeInfo.hashCode)) {
            return true;
        }
        String fromFile = readFromFile();
        if (fromFile == null || !fromFile.contains(themeInfo.hashCode)) {
            return false;
        }
        this.mCachedHistoryFinished.add(themeInfo.hashCode);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i2, Bundle bundle) {
        if (i2 != 32) {
            if (i2 == 64) {
                int i3 = bundle.getInt("motionEvent");
                SiUtils.Log.d(TAG, "focusWindowCallback RESOURCE_ID_MOTION_EVENT event=" + i3);
                if (i3 == 0) {
                    stopTrack(true);
                    return;
                } else {
                    if (i3 == 1) {
                        maybeStartTrack();
                        return;
                    }
                    return;
                }
            }
            return;
        }
        String string = bundle.getString("owningPackage");
        String string2 = bundle.getString("windowName");
        SiUtils.Log.d(TAG, "notifyEvent owningPackage=" + string + ", owningUid=" + bundle.getInt("owningUid") + ", windowName=" + string2 + ", windowType=" + bundle.getInt("windowType"));
        if (string2 == null) {
            return;
        }
        if (!string2.contains("LauncherOverlayWindow") && !string2.contains("NotificationShade") && !string2.contains("control_center")) {
            if (string2.contains("com.miui.home.launcher")) {
                SiUtils.Log.d(TAG, "maybeStartTrack for launcher is on TOP.");
                this.bWindowStatusAbnormal = false;
                maybeStartTrack();
                return;
            }
            return;
        }
        if (string2.contains("newhome")) {
            SiUtils.Log.d(TAG, "stopTrack for KAN-DIAN opened.");
        } else if (string2.contains("com.miui.personalassistant")) {
            SiUtils.Log.d(TAG, "stopTrack for Negative Screen opened.");
        } else if (string2.contains("control_center")) {
            SiUtils.Log.d(TAG, "stopTrack for Status Bar opened.");
        } else {
            SiUtils.Log.d(TAG, "stopTrack for Notification Bar opened.");
        }
        this.bWindowStatusAbnormal = true;
        stopTrack();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startTracking$0() {
        while (this.mTracking && this.mCount <= 60) {
            this.mTotalCurrent += (double) getCurrentNow();
            this.mCount++;
            BrightnessInfo brightnessInfo = this.mContext.getDisplay().getBrightnessInfo();
            if (brightnessInfo != null) {
                this.mTotalBrightness += BrightnessSynchronizer.brightnessFloatToInt(brightnessInfo.brightness);
                this.mBrightCount++;
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e2) {
                throw new RuntimeException(e2);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Track current finish. mCount=");
        sb.append(this.mCount);
        sb.append(", total=");
        sb.append(this.mTotalCurrent);
        sb.append(", avg=");
        int i2 = this.mCount;
        sb.append(i2 == 0 ? 0.0d : this.mTotalCurrent / ((double) i2));
        SiUtils.Log.w(TAG, sb.toString());
    }

    private boolean mayDisable() {
        boolean z2;
        boolean z3 = true;
        if (System.currentTimeMillis() - SharedPrefUtil.getInstance(this.mContext).getLongValue(Constants.FIRST_POWER_ON, 0L) > 93312000000L) {
            SiUtils.Log.d(TAG, "disable for device published too long.");
            z2 = true;
        } else {
            z2 = false;
        }
        if (z2 || this.mCurrentThemeInfo == null || System.currentTimeMillis() - this.mCurrentThemeInfo.updateTime <= 7776000000L) {
            z3 = z2;
        } else {
            SiUtils.Log.d(TAG, "disable for no update for theme too long.");
        }
        if (z3) {
            unregisterForEventCallback();
            MiuiFreeFormManager.unregisterFreeformCallback(this.freeformCallback);
            MIY.unregister(this.SI);
            Subdivisions.enable(this.SI, false);
            ShaoSi.getInstance().update(this.SI);
        }
        return z3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeStartTrack() {
        synchronized (this) {
            try {
                if (this.mScreenOffTimeout <= 20000) {
                    SiUtils.Log.d(TAG, "Discard for screen off timeout is too short.");
                    return;
                }
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (jCurrentTimeMillis - this.mLastSampled < this.mMinInterval) {
                    SiUtils.Log.w(TAG, "Discard2!! Just sampled " + ((jCurrentTimeMillis - this.mLastSampled) / 60000) + " minutes ago.");
                    return;
                }
                if (!this.mStartTrack && !this.mTracking) {
                    this.mStartTrack = true;
                    if (canTrack()) {
                        if (mStartId == Integer.MAX_VALUE) {
                            mStartId = 0;
                        }
                        mStartId++;
                        Message message = new Message();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putInt("startId", mStartId);
                        message.setData(bundle);
                        SiUtils.Log.d(TAG, "maybeStartTrack, start new task, startId=" + mStartId);
                        this.mHandler.sendMessageDelayed(message, 3000L);
                    } else {
                        this.mStartTrack = false;
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void maybeUpdateCIListener() {
        if (this.bWaitingNewTheme) {
            SiUtils.Log.d(TAG, "unregister SI.");
            MIY.unregister(this.SI, 2, 1, 4, 3);
        } else {
            SiUtils.Log.d(TAG, "register SI.");
            MIY.register(this.SI, 2, 1, 4, 3);
        }
    }

    private void maybeUpload(ThemeInfo themeInfo) {
        UploadInfo uploadInfo = this.mUploadMap.get(themeInfo.hashCode);
        if (uploadInfo == null) {
            uploadInfo = new UploadInfo();
            String str = themeInfo.hashCode;
            uploadInfo.hashCode = str;
            this.mUploadMap.put(str, uploadInfo);
        }
        log(TAG, "maybeUpload info=" + themeInfo);
        if (!canUpload(themeInfo)) {
            int i2 = uploadInfo.uploadFailedTimes + 1;
            uploadInfo.uploadFailedTimes = i2;
            if (i2 >= 10) {
                this.bWaitingNewTheme = true;
                maybeUpdateCIListener();
                unregisterForEventCallback();
                writeToFile(themeInfo);
            }
            SiUtils.Log.d(TAG, "upload to OneTrack FAILED. uploadInfo=" + uploadInfo);
            return;
        }
        uploadData(themeInfo);
        uploadInfo.lastUploadTime = System.currentTimeMillis();
        int i3 = uploadInfo.uploadTimes + 1;
        uploadInfo.uploadTimes = i3;
        uploadInfo.uploadFailedTimes = 0;
        if (i3 >= 3) {
            this.bWaitingNewTheme = true;
            maybeUpdateCIListener();
            unregisterForEventCallback();
            writeToFile(themeInfo);
        }
        SiUtils.Log.d(TAG, "upload to OneTrack SUCCESS. uploadInfo=" + uploadInfo);
        if (this.bDebug) {
            this.mMinInterval = MessageInfoContract.TIMEOUT;
        } else {
            this.mMinInterval = 7200000L;
        }
    }

    private void notifyCharging(boolean z2) {
        this.mCharging = z2;
        log(TAG, "charging mode changed mCharging=" + this.mCharging);
        if (!z2) {
            maybeStartTrack();
        } else {
            stopTrack();
            mayDisable();
        }
    }

    private String readFromFile() {
        return FileUtil.readFromFile(this.mContext.getFilesDir().getAbsolutePath() + "/theme_power_info");
    }

    private void registerForEventCallback(int i2) {
        if (this.bWaitingNewTheme) {
            return;
        }
        if ((i2 & 32) != 0) {
            SiUtils.Log.d(TAG, "registerForFocusWindow");
            EventMonitor.getInstance().registerCallback(32, this.eventCallback);
        }
        if ((i2 & 64) != 0) {
            SiUtils.Log.d(TAG, "registerForMotionEvent");
            EventMonitor.getInstance().registerCallback(64, this.eventCallback);
        }
    }

    private void registerThemeInfo() {
        SiUtils.Log.d(TAG, "registerThemeInfo 111");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.siming.shaosi.ThemeMonitorM.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == null) {
                    SiUtils.Log.d(ThemeMonitorM.TAG, "registerThemeInfo action is null");
                    return;
                }
                if (intent.getAction().equals("miui.intent.action.ACTION_THEME_CHANGED")) {
                    SiUtils.Log.d(ThemeMonitorM.TAG, "registerThemeInfo onChange1=" + intent);
                    ThemeMonitorM.this.getThemeInfo();
                    return;
                }
                if (intent.getAction().equals("android.intent.action.WALLPAPER_CHANGED")) {
                    SiUtils.Log.d(ThemeMonitorM.TAG, "registerThemeInfo onChange2=" + intent);
                    if (ThemeMonitorM.this.mCurrentThemeInfo == null) {
                        SiUtils.Log.d(ThemeMonitorM.TAG, "wallpaper changed, mCurrentThemeInfo is null, IGNORE.");
                        return;
                    }
                    if (System.currentTimeMillis() > ThemeMonitorM.this.mCurrentThemeInfo.updateTime) {
                        SiUtils.Log.d(ThemeMonitorM.TAG, "wallpaper changed, inactive current theme. time=" + System.currentTimeMillis());
                        ThemeMonitorM.this.mCurrentThemeInfo = null;
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("miui.intent.action.ACTION_THEME_CHANGED");
        intentFilter.addAction("android.intent.action.WALLPAPER_CHANGED");
        this.mContext.registerReceiver(broadcastReceiver, intentFilter, 2);
        getThemeInfo();
    }

    private void reset() {
        stopTrack();
        this.mCharging = PowerKeeperManager.getInstance().isCharging();
        this.mFgInfo = null;
    }

    private synchronized void startTracking(int i2) {
        log(TAG, "startTracking theme.");
        if (i2 != mStartId) {
            SiUtils.Log.w(TAG, "startTracking Discard!!! New task has been scheduled. startId=" + i2 + ", mStartId=" + mStartId);
            return;
        }
        if (!this.mStartTrack) {
            SiUtils.Log.w(TAG, "startTracking Discard!!! Maybe something happened during starting.");
            return;
        }
        if (this.mTracking) {
            SiUtils.Log.w(TAG, "startTracking Discard!!! Has been started.");
            return;
        }
        this.mTracking = true;
        registerForEventCallback(64);
        this.mCpuInfoList.clear();
        updateCpuTimes();
        this.mStartTime = System.currentTimeMillis();
        this.mStartGpuTime = getGpuTimes();
        this.mFrameInfo.reset();
        this.mTotalCurrent = 0.0d;
        this.mCount = 0;
        this.mTotalBrightness = 0;
        this.mBrightCount = 0;
        new Thread(new Runnable() { // from class: com.miui.powerkeeper.siming.shaosi.a
            @Override // java.lang.Runnable
            public final void run() {
                this.f554a.lambda$startTracking$0();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopTrack() {
        stopTrack(false);
    }

    private void unregisterForEventCallback() {
        unregisterForEventCallback(65535);
    }

    private void updateCpuTimes() {
        if (!this.mCpuInfoList.isEmpty()) {
            for (CPUInfo cPUInfo : this.mCpuInfoList) {
                cPUInfo.endCpuTime = getCpuTimeForPid(cPUInfo.pid);
            }
            return;
        }
        if (!this.mFgInfo.mForegroundPackageName.equals(LAUNCHER)) {
            SiUtils.Log.d(TAG, "Unsatisfied FG. mFgInfo=" + this.mFgInfo.mForegroundPackageName);
            return;
        }
        ForegroundInfo foregroundInfo = this.mFgInfo;
        CPUInfo cPUInfo2 = new CPUInfo(foregroundInfo.mForegroundPackageName, foregroundInfo.mForegroundPid);
        cPUInfo2.startCpuTime = getCpuTimeForPid(cPUInfo2.pid);
        this.mCpuInfoList.add(cPUInfo2);
        String str = new String[]{"com.miui.miwallpaper"}[0];
        List<Integer> pidByPackageName = PackageUtil.getPidByPackageName(str);
        if (pidByPackageName.isEmpty()) {
            return;
        }
        int iIntValue = pidByPackageName.get(0).intValue();
        CPUInfo cPUInfo3 = new CPUInfo(str, iIntValue);
        cPUInfo3.startCpuTime = getCpuTimeForPid(iIntValue);
        this.mCpuInfoList.add(cPUInfo3);
    }

    private void updateThemeInfo(String str) {
        if (str == null) {
            return;
        }
        String[] strArr = {"title", "onlineId", "localId", "isMaml", "isDynamicMaml"};
        String[] strArr2 = new String[5];
        Pattern patternCompile = Pattern.compile("=\\s*");
        for (int i2 = 0; i2 < 5; i2++) {
            Matcher matcher = Pattern.compile(strArr[i2] + "\\s*=\\s*[^,$]*").matcher(str);
            if (matcher.find()) {
                Matcher matcher2 = patternCompile.matcher(matcher.group());
                if (matcher2.find()) {
                    strArr2[i2] = matcher.group().substring(matcher2.end());
                    SiUtils.Log.d(TAG, "updateThemeInfo matched, " + strArr[i2] + "=" + strArr2[i2]);
                } else {
                    strArr2[i2] = "";
                }
            } else {
                strArr2[i2] = "";
                SiUtils.Log.w(TAG, "updateThemeInfo match failed for " + strArr[i2]);
            }
        }
        this.mCurrentThemeInfo = new ThemeInfo(strArr2);
        Matcher matcher3 = Pattern.compile("\\s1[78]\\d{11}\\s").matcher(str);
        if (matcher3.find()) {
            this.mCurrentThemeInfo.updateTime = Long.parseLong(matcher3.group().trim());
            SiUtils.Log.d(TAG, "updateThemeInfo match result=" + matcher3.group() + ", time=" + this.mCurrentThemeInfo.updateTime);
        }
        if (isThemeCheckFinished(this.mCurrentThemeInfo)) {
            log(TAG, "The theme has checked.");
            this.bWaitingNewTheme = true;
            unregisterForEventCallback();
        } else {
            UploadInfo uploadInfo = this.mUploadMap.get(this.mCurrentThemeInfo.hashCode);
            if (uploadInfo == null || uploadInfo.uploadTimes < 3 || uploadInfo.uploadFailedTimes < 10) {
                this.bWaitingNewTheme = false;
                reset();
            } else {
                this.bWaitingNewTheme = true;
                unregisterForEventCallback();
            }
        }
        maybeUpdateCIListener();
    }

    private void uploadData(ThemeInfo themeInfo) {
        ArrayMap arrayMap = new ArrayMap();
        arrayMap.put("theme_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis())));
        arrayMap.put("theme_name", themeInfo.params[0]);
        arrayMap.put("theme_category", Integer.valueOf(themeInfo.themeType));
        arrayMap.put("theme_type", 1);
        arrayMap.put("theme_current", Integer.valueOf(getPowerModelCurrent(themeInfo.themeType)));
        arrayMap.put("theme_current_actual", Integer.valueOf(themeInfo.averageCurrent));
        arrayMap.put("theme_product_id", themeInfo.params[1]);
        TrackerManager.getInstance(this.mContext).track(5, ONE_TRACK_EVENT_THEME_MONITOR, arrayMap);
    }

    private void writeToFile(ThemeInfo themeInfo) {
        FileUtil.appendFileString(this.mContext.getFilesDir().getAbsolutePath() + "/theme_power_info", "," + themeInfo.hashCode);
        this.mCachedHistoryFinished.add(themeInfo.hashCode);
        this.mUploadMap.remove(themeInfo.hashCode);
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S, com.miui.powerkeeper.siming.IDivision
    public void debug(boolean z2) {
        super.debug(z2);
        if (this.bDebug) {
            this.mMinInterval = 10000L;
        } else {
            this.mMinInterval = DEFAULT_MINIMUM_INTERVAL_FAILED;
        }
    }

    /* JADX WARN: Code duplicated, block: B:16:0x002a  */
    @Override // com.miui.powerkeeper.siming.shaosi.S, com.miui.powerkeeper.siming.IDivision
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr != null && strArr.length >= 1) {
            byte b2 = 0;
            String str = strArr[0];
            int iHashCode = str.hashCode();
            if (iHashCode != -1615291473) {
                if (iHashCode != 1511 || !str.equals("-t")) {
                    b2 = -1;
                }
            } else if (str.equals("--reset")) {
                b2 = 1;
            } else {
                b2 = -1;
            }
            if (b2 == 0) {
                printWriter.println("Test.");
                return;
            } else if (b2 == 1) {
                printWriter.println("reset.");
                this.mUploadMap.clear();
                return;
            }
        }
        printWriter.println("The drmFile is:" + this.mDrmFileDebug);
        if (this.mCount != 0) {
            printWriter.println("Average current:" + (this.mTotalCurrent / ((double) this.mCount)));
            printWriter.println("Sampling count:" + this.mCount);
            printWriter.println("Sampling duration:" + (System.currentTimeMillis() - this.mStartTime));
        }
        printWriter.println("mTracking=" + this.mStartTrack);
        printWriter.println("mCharging=" + this.mCharging);
        printWriter.println("mMinInterval=" + this.mMinInterval + com.xiaomi.push.mpcd.Constants.SEPARATOR_LEFT_PARENTESIS + ((this.mMinInterval - System.currentTimeMillis()) + this.mLastSampled) + com.xiaomi.push.mpcd.Constants.SEPARATOR_RIGHT_PARENTESIS);
        StringBuilder sb = new StringBuilder();
        sb.append("mLastSampled=");
        sb.append(this.mLastSampled);
        printWriter.println(sb.toString());
        printWriter.println("currentTheme=\n  " + this.mCurrentThemeInfo);
        printWriter.println("mThemeInfoList:");
        Iterator<ThemeInfo> it = this.mThemeInfoList.iterator();
        while (it.hasNext()) {
            printWriter.println("  " + it.next().toString());
        }
        printWriter.println("mUploadMap:");
        Iterator<UploadInfo> it2 = this.mUploadMap.values().iterator();
        while (it2.hasNext()) {
            printWriter.println("  " + it2.next().toString());
        }
        printWriter.println("mCachedHistoryFinished:");
        Iterator<String> it3 = this.mCachedHistoryFinished.iterator();
        while (it3.hasNext()) {
            printWriter.print("  " + it3.next());
        }
        printWriter.println("HistoryFile: " + readFromFile());
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S
    public void handleMessage(Message message) {
        SiUtils.Log.d(TAG, "handleMessage what=" + message.what);
        int i2 = message.what;
        if (i2 == 0) {
            registerThemeInfo();
        } else {
            if (i2 != 1) {
                return;
            }
            startTracking(message.getData().getInt("startId"));
        }
    }

    @Override // com.miui.powerkeeper.siming.shaosi.S, com.miui.powerkeeper.siming.IDivision
    public void notify(int i2, Bundle bundle) {
        log(TAG, "notify msgId=" + i2);
        if (this.mCurrentThemeInfo == null) {
            log(TAG, "Discard! mCurrentThemeInfo is null");
            return;
        }
        if (this.bWaitingNewTheme) {
            SiUtils.Log.d(TAG, "We have get enough info for " + this.mCurrentThemeInfo.params[0]);
            return;
        }
        if (i2 == 1) {
            stopTrack();
            return;
        }
        if (i2 == 2) {
            notifyCharging(bundle.getBoolean(MIY.BUNDLE_PARAM_CHARGING));
        } else if (i2 == 3) {
            maybeStartTrack();
        } else {
            if (i2 != 4) {
                return;
            }
            fgInfoChanged((ForegroundInfo) bundle.get(MIY.BUNDLE_PARAM_FG_INFO));
        }
    }

    private void stopTrack(boolean z2) {
        if (!z2) {
            unregisterForEventCallback(64);
        }
        synchronized (this) {
            try {
                if (this.mTracking) {
                    SiUtils.Log.d(TAG, "stopTrack! Calculate!");
                    calculate();
                } else {
                    SiUtils.Log.d(TAG, "stopTrack! Do nothing!");
                }
                this.mTracking = false;
                this.mStartTrack = false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void unregisterForEventCallback(int i2) {
        if ((i2 & 32) != 0) {
            SiUtils.Log.d(TAG, "unregisterForFocusWindow");
            EventMonitor.getInstance().unregisterCallback(32, this.eventCallback);
        }
        if ((i2 & 64) != 0) {
            SiUtils.Log.d(TAG, "unregisterForMotionEvent");
            EventMonitor.getInstance().unregisterCallback(64, this.eventCallback);
        }
    }
}
