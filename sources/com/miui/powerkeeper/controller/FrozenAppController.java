package com.miui.powerkeeper.controller;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.IWakelockManager;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.cloudcontrol.PowerKeeperCloudControlFeature;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.millet.FreezeBinder;
import com.miui.powerkeeper.millet.GreezeCallback;
import com.miui.powerkeeper.millet.MilletConfig;
import com.miui.powerkeeper.millet.MilletHandler;
import com.miui.powerkeeper.millet.MilletPolicy;
import com.miui.powerkeeper.millet.MilletUtils;
import com.miui.powerkeeper.millet.bright.BrightController;
import com.miui.powerkeeper.millet.bright.BrightMode;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.utils.CommonAdapter;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.PowerStateNativeProxy;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.PushConstants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class FrozenAppController extends Controller {
    private static final int APP_FREEZING = 3;
    private static final int APP_FROZEN = 1;
    private static final int APP_THAWED = 2;
    public static final int CMD_ADD_UID = 1;
    public static final int CMD_CLEAR_ALL = -2;
    public static final int CMD_DEL_UID = -1;
    private static final int EVENT_APP_CHECKER_SET_FROZEN = 18;
    private static final int EVENT_APP_CHECKER_SET_THAWED = 19;
    private static final int EVENT_APP_EXCUTING_SERVICE_END = 21;
    private static final int EVENT_APP_EXCUTING_SERVICE_START = 20;
    private static final int EVENT_APP_FROZEN_FAIL = 22;
    private static final int EVENT_APP_KILLING = 16;
    private static final int EVENT_APP_TO_FOREGROUND = 17;
    private static final int EVENT_BACKUP_ON = 28;
    private static final int EVENT_CHARGE = 30;
    private static final int EVENT_DISCHARGE = 29;
    private static final int EVENT_MILLET_FROZEN = 26;
    private static final int EVENT_MILLET_THAWED = 27;
    private static final int EVENT_SCEEN_OFF = 25;
    private static final int EVENT_SCEEN_ON = 24;
    private static final int EVENT_WAKE_UP_APP_SINGLE_EVENT = 23;
    public static final int FREEZE_CONDITION_ALL = 40;
    public static final int FREEZE_CONDITION_BACKUP = 2;
    public static final int FREEZE_CONDITION_CHARGING = 1;
    public static final int FREEZE_CONDITION_CLOUD = 32;
    public static final int FREEZE_CONDITION_CTS = 16;
    public static final int FREEZE_CONDITION_HANDSHAKE = 8;
    public static final int FREEZE_CONDITION_RESET = 4;
    private static final int FROZEN_FAIL_REASON_HAS_EXECTINGSERVICES = 1;
    private static final int FROZEN_FAIL_REASON_HAS_NOTIFICATION_SCEEN_ON = 4;
    private static final int FROZEN_FAIL_REASON_HAS_RECEIVEING_BROADCAST = 2;
    private static final int FROZEN_FAIL_REASON_HAS_RUNNING_PROCESSES = 5;
    private static final int FROZEN_FAIL_REASON_HAS_TOO_LOW_OOM_PROCESS = 3;
    private static final int FROZEN_FAIL_REASON_UID_HAS_NO_PIDS = 6;
    private static final int FROZEN_FAIL_REASON_UID_IS_ACTIVE = 7;
    private static final int FROZEN_FAIL_REASON_UID_IS_IN_FOREGROUND = 9;
    private static final int FROZEN_FAIL_REASON_UID_STATE_DONT_ALLOW = 8;
    private static final int FROZEN_OK = 0;
    private static final int PERCEPTIBLE_APP_ADJ = 200;
    private static final String TAG = "PowerKeeper.FrozenApp";
    private static final int UNKNOWN_ADJ = 1001;
    private final boolean IS_BLOCK_WAKEUP_DISABLED;
    private boolean isEnabled;
    private final SparseArray<AppStateFrozenControl> mAppFrozenControls;
    private PowerKeeperInterface.c mAppRuleChangedCallback;
    private PowerKeeperInterface.k mAppRuleChangedListener;
    private String mAudioRestrict;
    private PowerKeeperInterface.m mBatteryChangedReceiver;
    private BrightController mBrightCtrl;
    private b.a mCloudObserver;
    private Context mContext;
    private int mCurFreezeCondition;
    private Set<Integer> mDownloadPkgs;
    private PowerKeeperInterface.l mFrozenAppRuleChecker;
    private CloudFunctionConfig.ICloudFunctionListener mFunctionListener;
    private Handler mH;
    private Set<Integer> mHasAudioGps;
    private LocalLog mHistoryLog;
    private String mLaunchRestrict;
    private boolean mLoopSuccess;
    private boolean mMilletEnable;
    private MilletPolicy mMilletPolicy;
    private String mOldAurogonConfig;
    private String mOldLAudioRestrict;
    private PowerKeeperManager mPowerKeeperManager;
    private PowerKeeperInterface.e0 mProcessDiedCallback;
    private PowerKeeperInterface.y mProcessObserver;
    private final Object mRulesLock;
    private int mScreenOffMilletMode;
    private boolean mScreenOn;
    private final SparseBooleanArray mUidExecServiceState;
    private final SparseBooleanArray mUidState;
    private boolean mfrozenAppFunCtrlFlg;
    private String newCloudAurogonConfig;
    private int sHoldOnUid;
    private static final String[] mFailedReasonString = {"nofailed", "excutingServices", "receivBroadcast", "tooLowOom", "hasNotifitionSceenOn", "hasRunningProcess", "hasNoPids", "isActive", "notAllowFrozen", "appIsForeground"};
    private static boolean mKernelThawedFrozenEnabled = false;
    private static boolean mChangeStrategy = false;
    private static int mDelayScreenOff = MilletConfig.getScreenOffDelay();
    private static int mBatteryPlugged = -1;
    public static String newCloudScreenOffMillet = null;
    public static String newCloudLaunchRestrict = null;
    public static String newCloudBrightFrozen = null;
    public static String newCloudWhiteList = null;
    public static String newCloudFrozenNewStatus = null;
    public static String cloudVal = null;

    private static class FrozenUtil {
        private static final int PROC_COMBINE = 256;
        private static final int PROC_OUT_STRING = 4096;
        private static final int PROC_SPACE_TERM = 32;
        private static IWhetstoneActivityManager mWSProxy;
        private static final String[] mSytemPidStatData = new String[2];
        private static final int[] SYSTEM_PID_STAT_FORMAT = {288, 4128, 4128};

        private FrozenUtil() {
        }

        private static boolean checkWhetstoneAMProxy(boolean z2) {
            if (mWSProxy != null && !z2) {
                return true;
            }
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            mWSProxy = iWhetstoneActivityManagerAsInterface;
            return iWhetstoneActivityManagerAsInterface != null;
        }

        private static int getOomAdjByPid(int i2) {
            try {
                RunningProcessInfo runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i2);
                if (runningProcessInfoByPid != null) {
                    return runningProcessInfoByPid.mAdj;
                }
                return 1001;
            } catch (RemoteException e2) {
                Log.e(FrozenAppController.TAG, "getOomAdjByPid: ", e2);
                return -100;
            }
        }

        private static boolean getUidBroadcastStat(int i2) {
            if (true == checkWhetstoneAMProxy(false)) {
                try {
                    return mWSProxy.getProcessReceiverState(i2);
                } catch (RemoteException unused) {
                    Slog.e(FrozenAppController.TAG, "Occur RemoteException when IWhetstoneActivityManager");
                }
            }
            return false;
        }

        public static void init(Context context) {
            checkWhetstoneAMProxy(true);
        }

        public static boolean isExectingService(ArrayList<Integer> arrayList) {
            Iterator<Integer> it = arrayList.iterator();
            while (it.hasNext()) {
                if (isProcessExecutingServices(it.next().intValue())) {
                    return true;
                }
            }
            return false;
        }

        public static List<Integer> isHasForegroundService(ArrayList<Integer> arrayList) {
            return MilletUtils.getHasForegroundService(arrayList);
        }

        public static boolean isHasProcessOOMTooLow(ArrayList<Integer> arrayList) {
            Iterator<Integer> it = arrayList.iterator();
            while (it.hasNext()) {
                if (getOomAdjByPid(it.next().intValue()) <= 200) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isHasRunningStateProcess(ArrayList<Integer> arrayList) {
            Iterator<Integer> it = arrayList.iterator();
            while (it.hasNext()) {
                if (isProcessStateEqualRunning(it.next().intValue())) {
                    return true;
                }
            }
            return false;
        }

        private static boolean isProcessExecutingServices(int i2) {
            if (true == checkWhetstoneAMProxy(false)) {
                try {
                    return mWSProxy.isProcessExecutingServices(i2);
                } catch (RemoteException unused) {
                    Slog.e(FrozenAppController.TAG, "Occur RemoteException when IWhetstoneActivityManager");
                }
            }
            return false;
        }

        private static boolean isProcessStateEqualRunning(int i2) {
            String[] strArr = mSytemPidStatData;
            StringBuilder sb = new StringBuilder();
            sb.append("/proc/");
            sb.append(i2);
            sb.append("/stat");
            return Process.readProcFile(sb.toString(), SYSTEM_PID_STAT_FORMAT, strArr, null, null) && strArr[1].equals("R");
        }

        public static boolean isReceivingBroadcast(ArrayList<Integer> arrayList) {
            Iterator<Integer> it = arrayList.iterator();
            while (it.hasNext()) {
                if (getUidBroadcastStat(it.next().intValue())) {
                    return true;
                }
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void noticeFrozenEventToKernel(int i2, int[] iArr) {
            if (FrozenAppController.mKernelThawedFrozenEnabled) {
                if (FrozenAppController.mChangeStrategy) {
                    FreezeBinder.freezeUid(i2, "frozenAppControl");
                } else {
                    PowerStateNativeProxy.frozenApp(i2, iArr);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void noticeFrozenEventToWhetstone(int i2, int i3) {
            if (true == checkWhetstoneAMProxy(false)) {
                try {
                    mWSProxy.putUidFrozenState(i2, i3);
                } catch (RemoteException unused) {
                    Slog.e(FrozenAppController.TAG, "Occur RemoteException when IWhetstoneActivityManager");
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void noticeThawedEventToKernel(int i2) {
            int[] iArr = new int[0];
            if (FrozenAppController.mKernelThawedFrozenEnabled) {
                if (FrozenAppController.mChangeStrategy) {
                    FreezeBinder.thawUid(i2, "frozenAppControl");
                } else {
                    PowerStateNativeProxy.thawedApp(i2, iArr);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void noticeThawedEventToWhetstone(int i2, int i3) {
            if (true == checkWhetstoneAMProxy(false)) {
                try {
                    mWSProxy.putUidFrozenState(i2, i3);
                } catch (RemoteException unused) {
                    Slog.e(FrozenAppController.TAG, "Occur RemoteException when IWhetstoneActivityManager");
                }
            }
        }
    }

    private interface IAppControlActionMethod {
        void frozenAppLater(int i2);

        void frozenAppNow();

        void handleEvent(int i2);

        void thawedAppNow();
    }

    public FrozenAppController(Context context, PowerKeeperManager powerKeeperManager) {
        super(context, powerKeeperManager, TAG);
        String str = Build.DEVICE;
        this.IS_BLOCK_WAKEUP_DISABLED = str.equals("lancelot") || str.equals("galahad") || str.equals("shiva") || str.equals("dandelion") || str.equals("angelicain") || str.equals("angelica") || str.equals("angelican") || str.equals("cattail") || str.equals("merlin") || str.equals("merlinnfc");
        this.mRulesLock = new Object();
        this.isEnabled = false;
        this.mfrozenAppFunCtrlFlg = false;
        this.mMilletPolicy = null;
        this.mBrightCtrl = null;
        this.mHasAudioGps = new HashSet();
        this.mDownloadPkgs = new HashSet();
        this.mUidState = new SparseBooleanArray();
        this.mAppFrozenControls = new SparseArray<>();
        this.mUidExecServiceState = new SparseBooleanArray();
        this.mHistoryLog = new LocalLog(Controller.MAX_HISTORY_ITEMS);
        this.mProcessObserver = new PowerKeeperInterface.y() { // from class: com.miui.powerkeeper.controller.FrozenAppController.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.y
            public void onProcessDiedListener(int i2, int i3) {
                synchronized (FrozenAppController.this.mLock) {
                    synchronized (FrozenAppController.this.mRulesLock) {
                        if (FrozenAppController.this.isEnabled) {
                            FrozenAppController.this.mHasAudioGps.remove(Integer.valueOf(i3));
                            if (FrozenAppController.this.mMilletPolicy != null) {
                                FrozenAppController.this.mMilletPolicy.dealBluetoothUidDied(i2);
                            }
                            FrozenAppController.this.dispatchEvent(i2, 16);
                        }
                    }
                }
            }
        };
        this.mCurFreezeCondition = 0;
        this.mScreenOffMilletMode = -1;
        this.mCloudObserver = null;
        this.mH = null;
        this.mLoopSuccess = false;
        this.mLaunchRestrict = "";
        this.mOldLAudioRestrict = "";
        this.mAudioRestrict = "";
        this.mScreenOn = true;
        this.mMilletEnable = SystemProperties.getBoolean("persist.sys.powmillet.enable", false);
        this.mBatteryChangedReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.controller.FrozenAppController.2
            @Override // com.miui.powerkeeper.PowerKeeperInterface.m
            public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
                StringBuilder sb = new StringBuilder();
                sb.append("onBatteryChanged plug=");
                int i8 = i6 & 15;
                sb.append(i8 != 0);
                sb.append(" mBatteryPlugged=");
                sb.append(FrozenAppController.mBatteryPlugged);
                Log.d(FrozenAppController.TAG, sb.toString());
                if (FrozenAppController.mBatteryPlugged != i6) {
                    if (i8 == 0) {
                        FrozenAppController.this.FreezeSysAppWhenUnplug(MilletPolicy.mSystemBlackList);
                    } else {
                        FrozenAppController.this.ThawSysAppWhenCharge(MilletPolicy.mSystemBlackList);
                    }
                    FrozenAppController.mBatteryPlugged = i6;
                    if (FrozenAppController.this.mH == null || !FrozenAppController.this.mLoopSuccess || MilletConfig.supportCharging()) {
                        return;
                    }
                    FrozenAppController.this.mH.sendMessage(FrozenAppController.this.mH.obtainMessage(16, i8 == 0 ? 0 : 1, 0));
                }
            }
        };
        this.mFunctionListener = new CloudFunctionConfig.ICloudFunctionListener() { // from class: com.miui.powerkeeper.controller.FrozenAppController.3
            @Override // com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig.ICloudFunctionListener
            public void onCloudFunctionUpdated() {
                FrozenAppController.this.setChangeFrozenStrategy(FrozenAppController.this.getMilletModeResult(), 32, false);
                String cloudLaunchRestrict = FrozenAppController.newCloudLaunchRestrict;
                if (cloudLaunchRestrict == null) {
                    cloudLaunchRestrict = GlobalFeatureConfigureHelper.getCloudLaunchRestrict(FrozenAppController.this.mContext);
                }
                if (!cloudLaunchRestrict.equals(FrozenAppController.this.mLaunchRestrict)) {
                    FrozenAppController.this.mLaunchRestrict = cloudLaunchRestrict;
                    Log.d(FrozenAppController.TAG, "launch config=" + FrozenAppController.this.mLaunchRestrict + " ->" + cloudLaunchRestrict);
                    Settings.Secure.putString(FrozenAppController.this.mContext.getContentResolver(), "immobulus_mode_switch_restrict", cloudLaunchRestrict);
                }
                if (!FrozenAppController.this.mOldLAudioRestrict.equals(FrozenAppController.this.mAudioRestrict) && FrozenAppController.this.mAudioRestrict != null) {
                    FrozenAppController frozenAppController = FrozenAppController.this;
                    frozenAppController.mOldLAudioRestrict = frozenAppController.mAudioRestrict;
                    Log.d(FrozenAppController.TAG, "launch config=" + FrozenAppController.this.mOldLAudioRestrict + " ->" + FrozenAppController.this.mAudioRestrict);
                    Settings.Global.putString(FrozenAppController.this.mContext.getContentResolver(), "zeropkgs", FrozenAppController.this.mAudioRestrict);
                }
                if (!FrozenAppController.this.mOldAurogonConfig.equals(FrozenAppController.this.newCloudAurogonConfig)) {
                    String cloudAurogonEnable = FrozenAppController.this.newCloudAurogonConfig == null ? GlobalFeatureConfigureHelper.getCloudAurogonEnable(FrozenAppController.this.mContext) : FrozenAppController.this.newCloudAurogonConfig;
                    FrozenAppController.this.dealCloudAurogon(cloudAurogonEnable);
                    FrozenAppController.this.mOldAurogonConfig = cloudAurogonEnable;
                    Settings.Global.putString(FrozenAppController.this.mContext.getContentResolver(), "aurogon_enable", cloudAurogonEnable);
                }
                Log.d(FrozenAppController.TAG, "onCloudFunctionUpdated mLaunchRestrict=" + FrozenAppController.this.mLaunchRestrict + " newCloudLaunchRestrict=" + FrozenAppController.newCloudLaunchRestrict + " newCloudWhiteList=" + FrozenAppController.newCloudWhiteList + " newCloudFrozenNewStatus=" + FrozenAppController.newCloudFrozenNewStatus);
            }
        };
        this.mOldAurogonConfig = "";
        this.newCloudAurogonConfig = "";
        this.mAppRuleChangedListener = new PowerKeeperInterface.k() { // from class: com.miui.powerkeeper.controller.FrozenAppController.7
            @Override // com.miui.powerkeeper.PowerKeeperInterface.k
            public void onAppRuleChange(int i2, int i3) {
                synchronized (FrozenAppController.this.mRulesLock) {
                    try {
                        if (!FrozenAppController.mChangeStrategy) {
                            FrozenAppController.this.updateRuleForUidLocked(i2, i3);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mContext = context;
        this.mPowerKeeperManager = powerKeeperManager;
        this.mFrozenAppRuleChecker = powerKeeperManager.getFrozenAppRuleChecker();
        this.mfrozenAppFunCtrlFlg = false;
        this.isEnabled = false;
        FrozenUtil.init(context);
        MilletUtils.setHandler(BackgroundThread.getHandler());
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "zeropkgs");
        this.mOldLAudioRestrict = string;
        if (string == null) {
            this.mOldLAudioRestrict = "";
        }
        String string2 = Settings.Global.getString(this.mContext.getContentResolver(), "aurogon_enable");
        this.mOldAurogonConfig = string2;
        if (string2 == null || string2.length() == 0) {
            String cloudAurogonEnable = GlobalFeatureConfigureHelper.getCloudAurogonEnable(this.mContext);
            this.mOldAurogonConfig = cloudAurogonEnable;
            if (cloudAurogonEnable == null) {
                this.mOldAurogonConfig = "";
            }
        }
        Settings.Global.putString(this.mContext.getContentResolver(), "aurogon_enable", this.mOldAurogonConfig);
        dealCloudAurogon(this.mOldAurogonConfig);
        Slog.d(TAG, "mKernelThawedFrozenEnabled = " + mKernelThawedFrozenEnabled);
        if (this.mMilletEnable) {
            registerCloudObserver();
        }
        BackgroundThread.getHandler().postDelayed(new Runnable() { // from class: com.miui.powerkeeper.controller.FrozenAppController.4
            @Override // java.lang.Runnable
            public void run() {
                if (FrozenAppController.this.mMilletEnable) {
                    FrozenAppController frozenAppController = FrozenAppController.this;
                    String cloudLaunchRestrict = FrozenAppController.newCloudLaunchRestrict;
                    if (cloudLaunchRestrict == null) {
                        cloudLaunchRestrict = GlobalFeatureConfigureHelper.getCloudLaunchRestrict(frozenAppController.mContext);
                    }
                    frozenAppController.mLaunchRestrict = cloudLaunchRestrict;
                    FrozenAppController.newCloudWhiteList = GlobalFeatureConfigureHelper.getCloudFrozenNewWhiteList(FrozenAppController.this.mContext);
                    b.a aVar = FrozenAppController.this.mCloudObserver;
                    int i2 = b.b.f23e;
                    if (aVar.s(i2)) {
                        FrozenAppController frozenAppController2 = FrozenAppController.this;
                        frozenAppController2.updateCloudData(frozenAppController2.mCloudObserver.l(i2));
                    } else {
                        CloudFunctionConfig.getInstance().registerCloudFunctionConfig(FrozenAppController.this.mFunctionListener);
                    }
                    if (FrozenAppController.this.getMilletModeResult() && FrozenAppController.this.mH == null) {
                        FrozenAppController.this.initMillet();
                    }
                }
                if (FrozenAppController.this.mLaunchRestrict != null) {
                    Settings.Secure.putString(FrozenAppController.this.mContext.getContentResolver(), "immobulus_mode_switch_restrict", FrozenAppController.this.mLaunchRestrict);
                }
                Log.d(FrozenAppController.TAG, "FrozenAppController.Constructor mLaunchRestrict=" + FrozenAppController.this.mLaunchRestrict + " newCloudLaunchRestrict=" + FrozenAppController.newCloudLaunchRestrict + " newCloudWhiteList=" + FrozenAppController.newCloudWhiteList + " newCloudFrozenNewStatus=" + FrozenAppController.newCloudFrozenNewStatus + " mOldAurogonConfig=" + FrozenAppController.this.mOldAurogonConfig + " newCloudAurogonConfig=" + FrozenAppController.this.newCloudAurogonConfig);
            }
        }, PluginConstants.TTS_REMOVE_BEAN_TIME);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean appIsAllowToFrozen(int i2) {
        return !this.mUidState.get(i2, true);
    }

    public static int callNativeSet(int i2, int i3) {
        Log.d(TAG, "callNativeSet uid=" + i2 + " cmd=" + i3);
        if (PowerKeeperManager.JNI_LOADED) {
            return nativeSet(i2, i3);
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkCTS(int i2) {
        String packageNameByUid = PackageUtil.getPackageNameByUid(this.mContext, i2);
        for (String str : MilletUtils.CTS_NAME) {
            if (packageNameByUid != null && packageNameByUid.contains(str) && !packageNameByUid.contains("ctsshim")) {
                Log.d(TAG, "find name = " + packageNameByUid);
                setChangeFrozenStrategy(true, 16, false);
                return true;
            }
        }
        return false;
    }

    private static boolean checkCloudData(String str, String str2, JSONObject jSONObject) {
        if (jSONObject == null) {
            cloudVal = null;
        } else {
            try {
                cloudVal = jSONObject.getString(str);
            } catch (JSONException unused) {
                cloudVal = null;
            }
        }
        String str3 = cloudVal;
        if (str3 != null || str2 == null) {
            return (str3 == null || str3.equals(str2)) ? false : true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dealCloudAurogon(String str) {
        try {
            MilletConfig.dealCloudData(str);
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchEvent(final int i2, final int i3) {
        if (mChangeStrategy && this.mHandler == null) {
            this.mHandler = new Controller.MyHandler(PowerKeeperManager.getThread().getLooper());
        }
        Controller.MyHandler myHandler = this.mHandler;
        if (myHandler != null) {
            myHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.FrozenAppController.8
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (FrozenAppController.this.mRulesLock) {
                        try {
                            int i4 = i2;
                            if (-1 != i4) {
                                FrozenAppController.this.getAppControlStateLocked(i4).handleEvent(i3);
                            } else if (-1 == i4) {
                                try {
                                    Iterator<ActivityManager.RunningAppProcessInfo> it = ((ActivityManager) FrozenAppController.this.mContext.getSystemService("activity")).getRunningAppProcesses().iterator();
                                    while (it.hasNext()) {
                                        FrozenAppController.this.getAppControlStateLocked(it.next().uid).handleEvent(i3);
                                    }
                                } catch (Exception e2) {
                                    Slog.e(FrozenAppController.TAG, "getAppAllPids", e2);
                                }
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AppStateFrozenControl getAppControlStateLocked(int i2) {
        if (this.mAppFrozenControls.get(i2) == null) {
            this.mAppFrozenControls.put(i2, new AppStateFrozenControl(i2, 2));
        }
        return this.mAppFrozenControls.get(i2);
    }

    public static boolean getBatteryPlugged() {
        return (mBatteryPlugged & 15) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getMilletModeResult() {
        MilletPolicy milletPolicy;
        boolean enable = MilletConfig.getEnable(this.mContext);
        if (!enable) {
            return enable;
        }
        String cloudScreenOffMilletMode = newCloudScreenOffMillet;
        if (cloudScreenOffMilletMode == null) {
            cloudScreenOffMilletMode = GlobalFeatureConfigureHelper.getCloudScreenOffMilletMode(this.mContext);
        }
        int screenOffMilletMode = MilletUtils.getScreenOffMilletMode(cloudScreenOffMilletMode);
        boolean z2 = enable && (screenOffMilletMode == MilletUtils.CLOUD_WHITE_OPEN || screenOffMilletMode == MilletUtils.CLOUD_WHITE_BLACK_INVALID || screenOffMilletMode == MilletUtils.CLOUD_BLACK_OPEN);
        if (z2 && this.mH == null) {
            initMillet();
        }
        if (enable && (milletPolicy = this.mMilletPolicy) != null) {
            milletPolicy.setScreenOffMilletMode(screenOffMilletMode);
        }
        this.mScreenOffMilletMode = screenOffMilletMode;
        return z2;
    }

    private int getNotificationUid(StatusBarNotification statusBarNotification) {
        String packageName = statusBarNotification.getPackageName();
        int uid = statusBarNotification.getUid();
        if (!packageName.equalsIgnoreCase(PushConstants.PUSH_SERVICE_PACKAGE_NAME)) {
            return uid;
        }
        CharSequence targetPkg = statusBarNotification.getNotification().extraNotification.getTargetPkg();
        if (TextUtils.isEmpty(targetPkg)) {
            return uid;
        }
        return PackageUtil.getUidByPackageName(this.mContext, targetPkg.toString());
    }

    private int getUidRule(int i2) {
        return this.mFrozenAppRuleChecker.getAppRule(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SparseIntArray getUidsRule(int[] iArr) {
        return this.mFrozenAppRuleChecker.getAppsRule(iArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initMillet() {
        if (!FreezeBinder.getInstance().hasGreeze()) {
            Log.d(TAG, " has no greeze");
            return;
        }
        int iNativeInit = PowerKeeperManager.JNI_LOADED ? nativeInit() : -1;
        if (iNativeInit == -1 || iNativeInit == -2) {
            Log.d(TAG, "nativeInit error millet----: " + iNativeInit);
            return;
        }
        if (MilletUtils.hasForegroundField()) {
            if (TextUtils.equals(SystemProperties.get("ro.boot.factorybuild", "0"), "1")) {
                Log.d(TAG, "isFactory");
                return;
            }
            MilletUtils.setContext(this.mContext);
            this.mMilletPolicy = new MilletPolicy(this.mContext);
            MilletHandler milletHandler = new MilletHandler(BackgroundThread.getHandler().getLooper(), this, this.mMilletPolicy, this.mPowerKeeperManager, this.mContext);
            this.mH = milletHandler;
            this.mMilletPolicy.setHandler(milletHandler, BackgroundThread.get());
            EventsAggregator eventsAggregator = this.mPowerKeeperManager.getEventsAggregator();
            if (eventsAggregator != null) {
                eventsAggregator.registerAudioDisguise(this.mH);
            }
            Slog.d(TAG, "res register=" + FreezeBinder.getInstance().registerCallback(new GreezeCallback(this.mContext, this.mH)));
            this.mH.post(new Runnable() { // from class: com.miui.powerkeeper.controller.FrozenAppController.5
                @Override // java.lang.Runnable
                public void run() {
                    SparseIntArray uidsRule = FrozenAppController.this.getUidsRule(FrozenAppController.this.getAllUids());
                    boolean zCheckCTS = false;
                    for (int i2 = 0; i2 < uidsRule.size(); i2++) {
                        int iKeyAt = uidsRule.keyAt(i2);
                        AppStateFrozenControl appControlStateLocked = FrozenAppController.this.getAppControlStateLocked(iKeyAt);
                        if (appControlStateLocked == null) {
                            Log.d(FrozenAppController.TAG, "initMillet app=null uid=" + iKeyAt);
                        }
                        if (FrozenAppController.this.mH != null && appControlStateLocked != null) {
                            ((MilletHandler) FrozenAppController.this.mH).getUidObserver().putIsSystem(iKeyAt, appControlStateLocked.isSystemApp);
                        }
                        if (!zCheckCTS) {
                            zCheckCTS = FrozenAppController.this.checkCTS(iKeyAt);
                        }
                        FrozenAppController.this.mMilletPolicy.dealPacket(iKeyAt, true, FrozenAppController.this.mH);
                    }
                    if (FrozenAppController.this.mBrightCtrl == null) {
                        FrozenAppController.this.mBrightCtrl = new BrightController(FrozenAppController.this.mContext, FrozenAppController.this);
                    }
                    PowerManager powerManager = (PowerManager) FrozenAppController.this.mContext.getSystemService(PowerManager.class);
                    if (powerManager == null || powerManager.isInteractive() || FrozenAppController.this.mH == null) {
                        if (powerManager == null || !powerManager.isInteractive()) {
                            return;
                        }
                        FrozenAppController.this.mBrightCtrl.setScreenState(true);
                    } else {
                        FrozenAppController.this.mBrightCtrl.setScreenState(false);
                    }
                }
            });
            setChangeFrozenStrategy(getMilletModeResult(), 32, false);
            setChangeFrozenStrategy(false, 4, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isHasNotification(int i2) {
        StatusBarNotification[] statusBarNotification = CommonAdapter.getStatusBarNotification(this.mContext.getPackageName());
        if (statusBarNotification != null) {
            for (StatusBarNotification statusBarNotification2 : statusBarNotification) {
                if (statusBarNotification2 != null && getNotificationUid(statusBarNotification2) == i2) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isScreenOn() {
        return ((PowerManager) this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).isScreenOn();
    }

    private boolean isUidValidForRules(int i2) {
        return UserHandle.isApp(i2);
    }

    public static final native int nativeQuery(int i2);

    public static final native int nativeSet(int i2, int i3);

    private void registerCloudObserver() {
        b.a aVarP = b.a.p();
        this.mCloudObserver = aVarP;
        aVarP.t(b.b.f23e, new b.a.d() { // from class: com.miui.powerkeeper.controller.FrozenAppController.6
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                Log.d(FrozenAppController.TAG, "Cloud-Observer onChange - FREEZE");
                FrozenAppController.this.updateCloudData(jSONObject);
            }
        });
    }

    private void setUidState(int i2, boolean z2) {
        if (isUidValidForRules(i2)) {
            if (this.mUidState.indexOfKey(i2) < 0 || z2 != this.mUidState.get(i2)) {
                this.mUidState.put(i2, z2);
                if (Controller.DEBUG) {
                    Slog.d(TAG, "setUidState, uid = " + i2 + " allow = " + z2);
                }
                if (!mChangeStrategy || this.mScreenOn || !FreezeBinder.getInstance().isUidFrozen(i2)) {
                    if (z2) {
                        notifyAppCheckerSetThawedEvent(i2);
                    } else {
                        notifyAppCheckerSetFrozenEvent(i2);
                    }
                }
                this.mHistoryLog.log("FrozenApp(try): setUidState uid=" + i2 + " allow=" + z2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCloudData(JSONObject jSONObject) {
        if (checkCloudData("screenoff_millet_mode", newCloudScreenOffMillet, jSONObject)) {
            newCloudScreenOffMillet = cloudVal;
            this.mFunctionListener.onCloudFunctionUpdated();
        }
        if (checkCloudData("launch_restrict", newCloudLaunchRestrict, jSONObject)) {
            newCloudLaunchRestrict = cloudVal;
            this.mFunctionListener.onCloudFunctionUpdated();
        }
        if (checkCloudData("bright_millet", newCloudBrightFrozen, jSONObject)) {
            newCloudBrightFrozen = cloudVal;
            BrightController brightController = this.mBrightCtrl;
            if (brightController != null) {
                brightController.onBrightFrozenChanged();
            }
        }
        if (checkCloudData("frozenNew_whitelist", newCloudWhiteList, jSONObject)) {
            newCloudWhiteList = cloudVal;
            MilletPolicy milletPolicy = this.mMilletPolicy;
            if (milletPolicy != null) {
                milletPolicy.onCloudWhiteListChanged();
            }
        }
        if (checkCloudData("frozenNew", newCloudFrozenNewStatus, jSONObject)) {
            newCloudFrozenNewStatus = cloudVal;
            this.mFunctionListener.onCloudFunctionUpdated();
        }
        if (checkCloudData(PowerKeeperCloudControlFeature.AUDIO_RESTRICT, this.mOldLAudioRestrict, jSONObject)) {
            this.mAudioRestrict = cloudVal;
            this.mFunctionListener.onCloudFunctionUpdated();
        }
        if (checkCloudData("aurogon_enable", this.mOldAurogonConfig, jSONObject)) {
            this.newCloudAurogonConfig = cloudVal;
            this.mFunctionListener.onCloudFunctionUpdated();
        }
        Log.d(TAG, "updateNewCloudData newCloudScreenOffMillet: " + newCloudScreenOffMillet + " newCloudLaunchRestrict: " + newCloudLaunchRestrict + " newCloudBrightFrozen: " + newCloudBrightFrozen + " newCloudWhiteList: " + newCloudWhiteList + " newCloudFrozenNewStatus: " + newCloudFrozenNewStatus + " newCloudAudioRestrict: " + this.mAudioRestrict + " newCloudAurogonConfig=" + this.newCloudAurogonConfig);
    }

    private void updateForUidRemovedLocked(int i2, int i3) {
        if (Controller.DEBUG) {
            Slog.v(TAG, "updateForUidRemovedLocked, userId=" + i2 + ", uid=" + i3);
        }
        this.mUidState.delete(i3);
    }

    private void updateForUserRemovedLocked(int i2) {
        if (Controller.DEBUG) {
            Slog.v(TAG, "updateForUserRemovedLocked, userId=" + i2);
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        int size = this.mUidState.size();
        for (int i3 = 0; i3 < size; i3++) {
            if (UserHandle.getUserId(this.mUidState.keyAt(i3)) == i2) {
                sparseBooleanArray.put(this.mUidState.keyAt(i3), true);
            }
        }
        for (int i4 = 0; i4 < sparseBooleanArray.size(); i4++) {
            updateForUidRemovedLocked(i2, sparseBooleanArray.keyAt(i4));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRuleForUidLocked(int i2, int i3) {
        if (this.mUidPoliy.get(i2, 0) != 0) {
            return;
        }
        setUidState(i2, i3 != 1);
    }

    public void FreezeSysAppWhenUnplug(List<String> list) {
        Handler handler;
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, it.next());
            if (!MilletUtils.isTopApp(uidByPackageName) && !PowerKeeperManager.getInstance().isUidForeground(uidByPackageName) && getBrightCtrl().getCurUid() != uidByPackageName && (handler = this.mH) != null) {
                Message messageObtainMessage = handler.obtainMessage(1, uidByPackageName, 1);
                messageObtainMessage.obj = "MilletDischarge 29";
                this.mH.sendMessage(messageObtainMessage);
            }
        }
    }

    public void ThawSysAppWhenCharge(List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, it.next());
            Handler handler = this.mH;
            if (handler != null) {
                Message messageObtainMessage = handler.obtainMessage(1, uidByPackageName, 0);
                messageObtainMessage.obj = "thawedMillet 30";
                this.mH.sendMessage(messageObtainMessage);
            }
        }
    }

    public int callNativeQuery(int i2) {
        if (PowerKeeperManager.JNI_LOADED) {
            return nativeQuery(i2);
        }
        return -1;
    }

    public boolean checkBackuping(Context context, boolean z2) {
        if (!MilletUtils.isBackupActive(context, z2)) {
            setChangeFrozenStrategy(false, 2, false);
            return false;
        }
        Log.d(TAG, "backup is running");
        setChangeFrozenStrategy(true, 2, false);
        Handler handler = this.mH;
        handler.sendMessageDelayed(handler.obtainMessage(31), MilletUtils.BACKUP_INTERVAL);
        return true;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        synchronized (this.mRulesLock) {
            try {
                indentingPrintWriter.println("#######dump FrozenAppController#######");
                indentingPrintWriter.println("mfrozenAppFunCtrlFlg = " + this.mfrozenAppFunCtrlFlg);
                indentingPrintWriter.println("mKernelThawedFrozenEnabled = " + mKernelThawedFrozenEnabled);
                StringBuilder sb = new StringBuilder();
                sb.append("mChangeStrategy = ");
                sb.append(mChangeStrategy);
                sb.append(" mCurFreezeCondition=");
                sb.append(this.mCurFreezeCondition);
                sb.append(" cloudOffMillet=");
                Object objValueOf = newCloudFrozenNewStatus;
                if (objValueOf == null) {
                    objValueOf = Boolean.valueOf(GlobalFeatureConfigureHelper.getCloudFrozenNewConfigure(this.mContext));
                }
                sb.append(objValueOf);
                sb.append(" mMilleMode=");
                sb.append(this.mScreenOffMilletMode);
                sb.append(" newCloudAudioRestrict: ");
                sb.append(this.mAudioRestrict);
                sb.append(" newCloudWhiteList: ");
                sb.append(newCloudWhiteList);
                sb.append(" newCloudLaunchRestrict: ");
                sb.append(newCloudLaunchRestrict);
                sb.append(" mLaunchRestrict=");
                sb.append(this.mLaunchRestrict);
                sb.append(" mOldAurogonConfig:");
                sb.append(this.mOldAurogonConfig);
                sb.append(" newCloudAurogonConfig: ");
                sb.append(this.newCloudAurogonConfig);
                indentingPrintWriter.println(sb.toString());
                indentingPrintWriter.println("mPolicy=" + this.mMilletPolicy);
                if (this.mMilletPolicy != null) {
                    indentingPrintWriter.println("mImportantApps:" + this.mMilletPolicy.getImportantApps().toString());
                }
                indentingPrintWriter.println("mBrightMillet =" + BrightMode.getInstance(this.mContext).getBrightEnable() + "mBrightMode =" + BrightMode.getInstance(this.mContext).getBrightMode());
                indentingPrintWriter.println("notifications has as follows ");
                StatusBarNotification[] statusBarNotification = CommonAdapter.getStatusBarNotification(this.mContext.getPackageName());
                if (statusBarNotification != null) {
                    for (StatusBarNotification statusBarNotification2 : statusBarNotification) {
                        if (statusBarNotification2 != null) {
                            String packageName = statusBarNotification2.getPackageName();
                            CharSequence targetPkg = statusBarNotification2.getNotification().extraNotification.getTargetPkg();
                            if (!TextUtils.isEmpty(targetPkg)) {
                                packageName = targetPkg.toString();
                            }
                            indentingPrintWriter.println(" " + getNotificationUid(statusBarNotification2) + " " + packageName);
                        }
                    }
                }
                indentingPrintWriter.println("mAppFrozenControl:");
                for (int i2 = 0; i2 < this.mAppFrozenControls.size(); i2++) {
                    if (this.mAppFrozenControls.valueAt(i2) != null) {
                        indentingPrintWriter.println("index i " + i2 + " = " + this.mAppFrozenControls.valueAt(i2).toString() + "frozenFailedReason " + this.mAppFrozenControls.valueAt(i2).getFrozenFailedReasonString());
                    }
                }
                indentingPrintWriter.println("FronzenController operation history:");
                this.mHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.println("end#######dump FrozenAppController#######end");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public BrightController getBrightCtrl() {
        return this.mBrightCtrl;
    }

    public boolean getChangeFrozenStrategy() {
        return mChangeStrategy;
    }

    public int getFrozenCondition() {
        return this.mCurFreezeCondition;
    }

    public Handler getMilletHandler() {
        if (!this.mScreenOn) {
            return this.mH;
        }
        BrightController brightController = this.mBrightCtrl;
        if (brightController != null) {
            return brightController.getHandler();
        }
        return null;
    }

    public MilletPolicy getMilletPolicy() {
        return this.mMilletPolicy;
    }

    public PowerKeeperManager getPowerKeeperManager() {
        return this.mPowerKeeperManager;
    }

    public Handler getScreenOffHandler() {
        return this.mH;
    }

    public final native int nativeHandShake();

    public final native int nativeInit();

    public void notifyAMExecServiceEvent(int i2, boolean z2) {
        synchronized (this.mRulesLock) {
            try {
                if (this.isEnabled && UserHandle.isApp(i2)) {
                    if (z2) {
                        dispatchEvent(i2, 20);
                    } else {
                        dispatchEvent(i2, 21);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyAppCheckerSetFrozenEvent(int i2) {
        synchronized (this.mRulesLock) {
            try {
                if (this.isEnabled && UserHandle.isApp(i2)) {
                    dispatchEvent(i2, 18);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyAppCheckerSetThawedEvent(int i2) {
        synchronized (this.mRulesLock) {
            try {
                if (this.isEnabled && UserHandle.isApp(i2)) {
                    dispatchEvent(i2, 19);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyEvent(int i2, Object obj) {
        Handler handler;
        ComponentName componentName;
        synchronized (this.mRulesLock) {
            try {
                boolean z2 = this.isEnabled;
                if (z2 || mChangeStrategy) {
                    if (i2 == 11) {
                        this.mScreenOn = true;
                        dispatchEvent(-1, 24);
                    } else if (i2 == 12) {
                        this.mScreenOn = false;
                        Log.d(TAG, "SCREEN_OFF notify");
                        if (this.mH != null && mChangeStrategy) {
                            Set<String> marketDownloadNotification = MilletUtils.getMarketDownloadNotification(this.mContext);
                            this.mDownloadPkgs.clear();
                            Iterator<String> it = marketDownloadNotification.iterator();
                            while (it.hasNext()) {
                                this.mDownloadPkgs.add(Integer.valueOf(MilletUtils.getUidByPackageName(this.mContext, it.next())));
                            }
                            String holdOnPackage = MilletUtils.getHoldOnPackage();
                            if (!TextUtils.isEmpty(holdOnPackage)) {
                                this.sHoldOnUid = MilletUtils.getUidByPackageName(this.mContext, holdOnPackage);
                            }
                        }
                        dispatchEvent(-1, 25);
                    } else if (i2 == 100) {
                        if ((obj instanceof ComponentName) && z2 && (componentName = (ComponentName) obj) != null) {
                            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, componentName.getPackageName());
                            if (!UserHandle.isApp(uidByPackageName)) {
                                return;
                            } else {
                                dispatchEvent(uidByPackageName, 17);
                            }
                        }
                        if (mChangeStrategy && (handler = this.mH) != null) {
                            if (!MilletHandler.isScreenOn()) {
                                Log.w(TAG, "warning resend EVENT_ID_SCREEN_ON");
                                this.mScreenOn = true;
                                dispatchEvent(-1, 24);
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyMilletSetFrozenEvent(int i2) {
        synchronized (this.mRulesLock) {
            try {
                if (this.isEnabled && UserHandle.isApp(i2)) {
                    dispatchEvent(i2, 26);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyMilletSetThawedEvent(int i2) {
        synchronized (this.mRulesLock) {
            try {
                if (this.isEnabled && UserHandle.isApp(i2)) {
                    dispatchEvent(i2, 27);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyScreenState(boolean z2, boolean z3) {
        Handler handler;
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
        if (z2) {
            z3 = powerManager.isInteractive();
        }
        if (powerManager == null || (handler = this.mH) == null) {
            return;
        }
        handler.sendMessageDelayed(handler.obtainMessage(z3 ? 2 : 3, "retry"), 2000L);
    }

    public void notifyWakeUpFrozenAppEvent(int i2) {
        synchronized (this.mRulesLock) {
            try {
                if (this.isEnabled && UserHandle.isApp(i2)) {
                    dispatchEvent(i2, 23);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onDisable() {
        synchronized (this.mRulesLock) {
            try {
                if (!this.isEnabled) {
                    Slog.v(TAG, "still disabled");
                    return;
                }
                Slog.i(TAG, "disable");
                this.isEnabled = false;
                this.mUidState.clear();
                this.mHandler.removeCallbacksAndMessages(null);
                SparseIntArray uidsRule = getUidsRule(getAllUids());
                for (int i2 = 0; i2 < uidsRule.size(); i2++) {
                    int iKeyAt = uidsRule.keyAt(i2);
                    if (isUidValidForRules(iKeyAt)) {
                        dispatchEvent(iKeyAt, 19);
                    }
                }
                this.mFrozenAppRuleChecker.unregisterAppRuleChangeListener(this.mAppRuleChangedCallback);
                this.mAppRuleChangedCallback.a();
                this.mAppRuleChangedCallback = null;
                this.mFrozenAppRuleChecker = null;
                this.mfrozenAppFunCtrlFlg = false;
                this.mPowerKeeperManager.unregisterProcessDiedListener(this.mProcessDiedCallback);
                this.mProcessDiedCallback = null;
                this.mHistoryLog.log("PowerKeeper.FrozenApp: onDisable");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onEnable() {
        synchronized (this.mRulesLock) {
            try {
                if (this.isEnabled) {
                    Slog.v(TAG, "already enabled");
                    return;
                }
                Slog.i(TAG, "enable");
                this.isEnabled = true;
                this.mUidState.clear();
                this.mFrozenAppRuleChecker = this.mPowerKeeperManager.getFrozenAppRuleChecker();
                PowerKeeperInterface.c cVar = new PowerKeeperInterface.c(this.mHandler, this.mAppRuleChangedListener);
                this.mAppRuleChangedCallback = cVar;
                this.mFrozenAppRuleChecker.registerAppRuleChangeListener(cVar);
                SparseIntArray uidsRule = getUidsRule(getAllUids());
                for (int i2 = 0; i2 < uidsRule.size(); i2++) {
                    int iKeyAt = uidsRule.keyAt(i2);
                    updateRuleForUidLocked(iKeyAt, uidsRule.valueAt(i2));
                    getAppControlStateLocked(iKeyAt).init();
                }
                this.mfrozenAppFunCtrlFlg = true;
                this.mHandler.removeCallbacksAndMessages(null);
                PowerKeeperInterface.e0 e0Var = new PowerKeeperInterface.e0(this.mHandler, this.mProcessObserver);
                this.mProcessDiedCallback = e0Var;
                this.mPowerKeeperManager.registerProcessDiedListener(e0Var);
                this.mHistoryLog.log("PowerKeeper.FrozenApp: onEnable");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onSetUidPolicy(int i2, int i3) {
        if (i3 == 0) {
            updateRuleForUidLocked(i2, getUidRule(i2));
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUidRemoved(int i2, int i3) {
        updateForUidRemovedLocked(i2, i3);
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUserEnableStatusChange(int i2, boolean z2) {
        if (z2) {
            return;
        }
        updateForUserRemovedLocked(i2);
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUserRunningStatusChange(int i2, boolean z2) {
        if (z2) {
            return;
        }
        updateForUserRemovedLocked(i2);
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onXSpaceStatusChange(int i2, boolean z2) {
        if (z2) {
            return;
        }
        updateForUserRemovedLocked(i2);
    }

    public void setChangeFrozenStrategy(boolean z2, int i2, boolean z3) {
        boolean z4 = mChangeStrategy;
        if (z2) {
            this.mCurFreezeCondition |= i2;
        } else {
            this.mCurFreezeCondition &= ~i2;
        }
        Log.d(TAG, "setChangeFrozenStrategy cur=" + this.mCurFreezeCondition + " enable=" + z2 + " flag=" + i2 + " needR=" + z3);
        if (this.mCurFreezeCondition == 40) {
            mChangeStrategy = true;
        } else {
            mChangeStrategy = false;
        }
        if (z3) {
            this.mLoopSuccess = true;
            PowerKeeperManager.getInstance().registerBatteryChangedListener(new PowerKeeperInterface.d(null, this.mBatteryChangedReceiver));
        }
        boolean z5 = mChangeStrategy;
        if (!z5) {
            BrightController brightController = this.mBrightCtrl;
            if (brightController != null) {
                brightController.setFreezeAvail(false);
                return;
            }
            return;
        }
        if (z4 != z5) {
            PowerManager powerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
            if (powerManager != null && this.mH != null && !powerManager.isInteractive()) {
                notifyScreenState(false, false);
            }
            if (this.mBrightCtrl == null) {
                this.mBrightCtrl = new BrightController(this.mContext, this);
            }
            this.mBrightCtrl.setFreezeAvail(BrightMode.getInstance(this.mContext).getBrightEnable());
        }
    }

    public void takeNewProcess(int i2, String str) {
        BrightController brightController = this.mBrightCtrl;
        if (brightController != null) {
            brightController.takeNewProcess(i2, str);
        }
    }

    public void updateWakelockBlockedUid(int i2, boolean z2) {
        if (this.IS_BLOCK_WAKEUP_DISABLED || MilletUtils.VERSION_TWO) {
            return;
        }
        try {
            IWakelockManager wakelockManagerInterface = PowerKeeperManager.getInstance().getWakelockManagerInterface();
            if (wakelockManagerInterface == null) {
                Slog.d(TAG, "wakelockManagerInterface is null");
                return;
            }
            wakelockManagerInterface.setWakelockBlock(z2, new ComponentName(this.mContext, (Class<?>) FrozenAppController.class), i2, null);
            Slog.d(TAG, "updateWakelockBlockedUid, uid = " + i2 + ", isBlocked = " + z2);
        } catch (RemoteException unused) {
            Slog.e(TAG, "mWakelockManager work abnormal");
        }
    }

    private class AppStateFrozenControl implements IAppControlActionMethod {
        private static final int APP_STATE_FROZEN_FOR_WHETSTONE = 1;
        private static final int APP_STATE_THAWED_FOR_WHETSTONE = 0;
        private static final int DEFAULT_DELAY_TIME_WHEN_APP_KILLED_CHECK_AGAIN = 120000;
        private static final int DEFAULT_DELAY_TIME_WHEN_APP_THAWED_BY_SINGLE_EVENT = 30000;
        private static final int DEFAULT_DELAY_TIME_WHEN_FROZEN_BLOCKED = 60000;
        private static final int DEFAULT_POLICY_VALUE = -1;
        private static final int POLICY_LOCATION_WHEN_APP_IN_FROZEN = 4;
        public boolean isSystemApp;
        public boolean mIsForeground;
        public int mState;
        public int mUid;
        private int mFrozenFailedReason = 0;
        public int mHasNoPidsCnt = 0;
        private ArrayList<Integer> mPids = new ArrayList<>();
        public int mEvent = -1;

        public AppStateFrozenControl(int i2, int i3) {
            this.isSystemApp = true;
            this.mUid = i2;
            this.mState = i3;
            this.mIsForeground = FrozenAppController.this.mPowerKeeperManager.isUidForeground(this.mUid);
            this.isSystemApp = MilletUtils.isSystemApp(FrozenAppController.this.mContext, this.mUid);
        }

        private void frozenMillet() {
            boolean zIsAllowMilletFrozen = FrozenAppController.mChangeStrategy ? isAllowMilletFrozen() : false;
            if (this.mEvent == 25 && FrozenAppController.mChangeStrategy && zIsAllowMilletFrozen && FrozenAppController.this.mH != null) {
                Message messageObtainMessage = FrozenAppController.this.mH.obtainMessage(1, this.mUid, 1);
                messageObtainMessage.obj = "frozenMillet " + this.mEvent;
                FrozenAppController.this.mH.sendMessage(messageObtainMessage);
            }
        }

        private int getAppAllPids(ArrayList<Integer> arrayList) {
            arrayList.clear();
            try {
                Iterator it = ProcessManager.getRunningProcessInfoByUid(this.mUid).iterator();
                while (it.hasNext()) {
                    this.mPids.add(Integer.valueOf(((RunningProcessInfo) it.next()).mPid));
                }
            } catch (Exception e2) {
                Slog.e(FrozenAppController.TAG, "getAppAllPids", e2);
            }
            return arrayList.size();
        }

        private int getFrozenFailedReason() {
            return this.mFrozenFailedReason;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getFrozenFailedReasonString() {
            return FrozenAppController.mFailedReasonString[this.mFrozenFailedReason];
        }

        private void printEventAndState() {
            if (Controller.DEBUG) {
                Slog.d(FrozenAppController.TAG, ">>> event = " + this.mEvent + " >>> state = " + this.mState + " uid " + this.mUid);
            }
        }

        private void setFrozenFailedReason(int i2) {
            if (FrozenAppController.mFailedReasonString.length < i2 || i2 < 0) {
                return;
            }
            this.mFrozenFailedReason = i2;
        }

        private void thawedMillet() {
            if (FrozenAppController.mChangeStrategy && this.mEvent == 27 && FrozenAppController.this.mH != null) {
                Message messageObtainMessage = FrozenAppController.this.mH.obtainMessage(1, this.mUid, 0);
                messageObtainMessage.obj = "thawedMillet " + this.mEvent;
                FrozenAppController.this.mH.sendMessage(messageObtainMessage);
            }
        }

        public void blockAllWakelocks() {
            FrozenAppController.this.updateWakelockBlockedUid(this.mUid, true);
        }

        @Override // com.miui.powerkeeper.controller.FrozenAppController.IAppControlActionMethod
        public void frozenAppLater(int i2) {
            int i3;
            if (i2 < 0) {
                return;
            }
            if (FrozenAppController.mChangeStrategy && (i3 = this.mEvent) != 25 && i3 != 22) {
                Log.d(FrozenAppController.TAG, "frozenAppLater mEvent=" + this.mEvent);
                return;
            }
            this.mState = 3;
            Controller.MyHandler myHandler = FrozenAppController.this.mHandler;
            if (myHandler != null) {
                this.mState = 3;
                MilletUtils.setPkgData(this.mUid, myHandler, i2);
                if (FrozenAppController.this.mPowerKeeperManager.isUidForeground(this.mUid)) {
                    MilletUtils.checkBg(this.mUid, 4500);
                }
                FrozenAppController.this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.controller.FrozenAppController.AppStateFrozenControl.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AppStateFrozenControl appStateFrozenControl = AppStateFrozenControl.this;
                        if (appStateFrozenControl.mState == 3) {
                            appStateFrozenControl.frozenAppNow();
                        }
                    }
                }, i2);
                FrozenAppController.this.mHistoryLog.log("frozenAppLater: " + toString() + " " + i2);
            }
        }

        @Override // com.miui.powerkeeper.controller.FrozenAppController.IAppControlActionMethod
        public void frozenAppNow() {
            boolean z2 = FrozenAppController.mChangeStrategy && isAllowMilletFrozen();
            if (1 == this.mState) {
                return;
            }
            if (!FrozenAppController.mChangeStrategy && isAllowFrozenNow()) {
                blockAllWakelocks();
                noticeFrozenEventToOthers();
                this.mState = 1;
                FrozenAppController.this.mHistoryLog.log("FrozenApp: " + toString() + " success");
                return;
            }
            if (z2) {
                frozenMillet();
                this.mState = 1;
                FrozenAppController.this.mHistoryLog.log("FrozenNewApp: " + toString() + " success");
                return;
            }
            if (!FrozenAppController.mChangeStrategy || this.mEvent != 18 || !isAllowFrozenNow()) {
                this.mState = 3;
                handleEvent(22);
                return;
            }
            synchronized (FrozenAppController.this.mRulesLock) {
                try {
                    if (FrozenAppController.this.isEnabled) {
                        blockAllWakelocks();
                        this.mState = 1;
                        FrozenAppController.this.mHistoryLog.log("FrozenEventApp: " + toString() + " success");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.controller.FrozenAppController.IAppControlActionMethod
        public void handleEvent(int i2) {
            switch (i2) {
                case 16:
                    int i3 = this.mState;
                    if (1 == i3) {
                        this.mEvent = 16;
                        printEventAndState();
                        thawedAppNow();
                        frozenAppLater(DEFAULT_DELAY_TIME_WHEN_APP_KILLED_CHECK_AGAIN);
                    } else if (3 == i3) {
                        this.mEvent = 16;
                        printEventAndState();
                        thawedAppNow();
                    }
                    break;
                case 17:
                    if (2 != this.mState) {
                        this.mEvent = 17;
                        printEventAndState();
                        thawedAppNow();
                    }
                    break;
                case 18:
                    int i4 = this.mState;
                    if (2 == i4 || 3 == i4) {
                        this.mEvent = 18;
                        printEventAndState();
                        frozenAppNow();
                    }
                    break;
                case 19:
                    if (2 != this.mState) {
                        this.mEvent = 19;
                        printEventAndState();
                        thawedAppNow();
                    }
                    break;
                case 20:
                    if (2 != this.mState) {
                        this.mEvent = 20;
                        printEventAndState();
                        thawedAppNow();
                    }
                    FrozenAppController.this.mUidExecServiceState.put(this.mUid, true);
                    break;
                case 21:
                    if (2 == this.mState) {
                        if (20 == this.mEvent) {
                            printEventAndState();
                            frozenAppNow();
                        }
                        this.mEvent = 21;
                    }
                    FrozenAppController.this.mUidExecServiceState.put(this.mUid, false);
                    break;
                case 22:
                    if (3 == this.mState) {
                        this.mEvent = 22;
                        printEventAndState();
                        switch (getFrozenFailedReason()) {
                            case 1:
                            case 2:
                            case 3:
                            case 5:
                            case 7:
                                frozenAppLater(60000);
                                break;
                            case 4:
                                if (!FrozenAppController.this.isScreenOn()) {
                                    frozenAppLater(60000);
                                    break;
                                }
                                break;
                            case 6:
                                if (this.mHasNoPidsCnt <= 2) {
                                    frozenAppLater(60000);
                                } else {
                                    this.mHasNoPidsCnt = 0;
                                }
                                break;
                            case 8:
                            case 9:
                                this.mState = 2;
                                break;
                        }
                    }
                    break;
                case 23:
                    int i5 = this.mState;
                    if (3 == i5) {
                        this.mEvent = 23;
                        printEventAndState();
                        thawedAppNow();
                    } else if (1 == i5) {
                        this.mEvent = 23;
                        printEventAndState();
                        thawedAppNow();
                        frozenAppLater(30000);
                    }
                    break;
                case 24:
                    if (FrozenAppController.mChangeStrategy) {
                        this.mEvent = 24;
                    }
                    if (2 != this.mState) {
                        if (FrozenAppController.mChangeStrategy || FrozenAppController.this.isHasNotification(this.mUid)) {
                            if (!FrozenAppController.mChangeStrategy) {
                                this.mEvent = 24;
                            }
                            printEventAndState();
                            thawedAppNow();
                        }
                    }
                    break;
                case 25:
                    if (FrozenAppController.mChangeStrategy) {
                        this.mEvent = 25;
                        printEventAndState();
                        frozenAppLater(MilletConfig.isGlobal ? MilletConfig.firstScreenOffDelayTime : FrozenAppController.mDelayScreenOff);
                    } else if (2 == this.mState) {
                        int i6 = this.mEvent;
                        if (24 == i6 || (22 == i6 && 4 == getFrozenFailedReason())) {
                            this.mEvent = 25;
                            printEventAndState();
                            frozenAppLater(60000);
                        }
                    }
                    break;
                case 26:
                    this.mEvent = 26;
                    printEventAndState();
                    frozenMillet();
                    break;
                case 27:
                    this.mEvent = 27;
                    printEventAndState();
                    thawedMillet();
                    break;
            }
        }

        public boolean isAllowFrozenNow() {
            if (!FrozenAppController.this.appIsAllowToFrozen(this.mUid)) {
                setFrozenFailedReason(8);
                if (Controller.DEBUG) {
                    Slog.d(FrozenAppController.TAG, "FrozenAPP, not allow to frozen by policy");
                }
                return false;
            }
            if (FrozenAppController.this.mPowerKeeperManager.isUidForeground(this.mUid)) {
                setFrozenFailedReason(9);
                if (Controller.DEBUG) {
                    Slog.d(FrozenAppController.TAG, "FrozenAPP, is in foreground");
                }
                return false;
            }
            if (FrozenAppController.this.mPowerKeeperManager.getAppActiveState(this.mUid)) {
                if (Controller.DEBUG) {
                    Slog.v(FrozenAppController.TAG, "FrozenAPP, is active");
                }
                setFrozenFailedReason(7);
                return false;
            }
            if (getAppAllPids(this.mPids) == 0) {
                this.mHasNoPidsCnt++;
                setFrozenFailedReason(6);
                if (Controller.DEBUG) {
                    Slog.d(FrozenAppController.TAG, "FrozenAPP, has no pids");
                }
                return false;
            }
            if (FrozenUtil.isHasRunningStateProcess(this.mPids)) {
                if (Controller.DEBUG) {
                    Slog.d(FrozenAppController.TAG, "FrozenAPP, has running process");
                }
                setFrozenFailedReason(5);
                return false;
            }
            if (FrozenUtil.isExectingService(this.mPids)) {
                if (Controller.DEBUG) {
                    Slog.d(FrozenAppController.TAG, "FrozenAPP, has executing service");
                }
                setFrozenFailedReason(1);
                return false;
            }
            if (FrozenUtil.isReceivingBroadcast(this.mPids)) {
                if (Controller.DEBUG) {
                    Slog.d(FrozenAppController.TAG, "FrozenAPP, is receiving broadcast");
                }
                setFrozenFailedReason(2);
                return false;
            }
            if (FrozenUtil.isHasProcessOOMTooLow(this.mPids)) {
                if (Controller.DEBUG) {
                    Slog.d(FrozenAppController.TAG, "FrozenAPP, is HasProcessOOMTooLow");
                }
                setFrozenFailedReason(3);
                return false;
            }
            if (!FrozenAppController.this.isScreenOn() || !FrozenAppController.this.isHasNotification(this.mUid)) {
                setFrozenFailedReason(0);
                return true;
            }
            if (Controller.DEBUG) {
                Slog.v(FrozenAppController.TAG, "FrozenAPP, has notification");
            }
            setFrozenFailedReason(4);
            return false;
        }

        public boolean isAllowMilletFrozen() {
            if ((!FrozenAppController.this.mMilletPolicy.isAllowFreeze(this.mUid) && (!MilletConfig.getEnableFun() || (MilletConfig.getEnableFun() && !FrozenAppController.this.mMilletPolicy.isUidActive(this.mUid)))) || FrozenAppController.this.mDownloadPkgs.contains(Integer.valueOf(this.mUid))) {
                setFrozenFailedReason(8);
                return false;
            }
            if (getAppAllPids(this.mPids) == 0) {
                this.mHasNoPidsCnt++;
                setFrozenFailedReason(6);
                if (Controller.DEBUG) {
                    Slog.d(FrozenAppController.TAG, "FrozenAPP, has no pids");
                }
                return false;
            }
            if (FrozenAppController.this.sHoldOnUid != this.mUid) {
                setFrozenFailedReason(0);
                return true;
            }
            if (Controller.DEBUG) {
                Slog.d(FrozenAppController.TAG, "FrozenAPP, has sPackageHoldOn");
            }
            setFrozenFailedReason(8);
            return false;
        }

        public void noticeFrozenEventToOthers() {
            int[] iArr = new int[this.mPids.size()];
            for (int i2 = 0; i2 < this.mPids.size(); i2++) {
                iArr[i2] = this.mPids.get(i2).intValue();
            }
            FrozenUtil.noticeFrozenEventToWhetstone(this.mUid, 1);
            FrozenUtil.noticeFrozenEventToKernel(this.mUid, iArr);
        }

        public void noticeThawedEventToOthers() {
            FrozenUtil.noticeThawedEventToWhetstone(this.mUid, 0);
            FrozenUtil.noticeThawedEventToKernel(this.mUid);
        }

        public void resumeAllWakelocks() {
            FrozenAppController.this.updateWakelockBlockedUid(this.mUid, false);
        }

        @Override // com.miui.powerkeeper.controller.FrozenAppController.IAppControlActionMethod
        public void thawedAppNow() {
            this.mState = 2;
            if (!FrozenAppController.mChangeStrategy || this.mEvent == 19) {
                noticeThawedEventToOthers();
            } else {
                thawedMillet();
            }
            this.mIsForeground = FrozenAppController.this.mPowerKeeperManager.isUidForeground(this.mUid);
            FrozenAppController.this.mHistoryLog.log("thawedAppNow: " + toString());
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            sb.append(" uid: " + this.mUid);
            sb.append(" mState:" + this.mState);
            sb.append(" mEvent:" + this.mEvent);
            sb.append(" mIsForeground:" + this.mIsForeground);
            sb.append('}');
            return sb.toString();
        }

        public void init() {
        }
    }
}
