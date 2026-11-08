package com.miui.powerkeeper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import com.miui.powerkeeper.active.ActiveController;
import com.miui.powerkeeper.appcontrol.HideModeStateMachine;
import com.miui.powerkeeper.appcontrol.HideModeStateMachineConfig;
import com.miui.powerkeeper.appcontrol.HideModeStateMachineWrapper;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils;
import com.miui.powerkeeper.controller.ActiveStateController;
import com.miui.powerkeeper.controller.AlarmController;
import com.miui.powerkeeper.controller.AppClusterController;
import com.miui.powerkeeper.controller.AppStandbyController;
import com.miui.powerkeeper.controller.AudioDisguiseController;
import com.miui.powerkeeper.controller.BgIdleController;
import com.miui.powerkeeper.controller.BroadcastController;
import com.miui.powerkeeper.controller.DeviceIdleController;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.miui.powerkeeper.controller.KillProcessController;
import com.miui.powerkeeper.controller.SensorController;
import com.miui.powerkeeper.event.EventLogManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.feedbackcontrol.FeedbackControlService;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener;
import com.miui.powerkeeper.locationpolicy.LocationPolicyController;
import com.miui.powerkeeper.mipush.MiPushHelper;
import com.miui.powerkeeper.powerchecker.IPowerChecker;
import com.miui.powerkeeper.powerchecker.PowerCheckerService;
import com.miui.powerkeeper.provider.PowerKeeperConfigureManager;
import com.miui.powerkeeper.provider.PowerSaveConfigureManager;
import com.miui.powerkeeper.statemachine.DisplayFrameSetting;
import com.miui.powerkeeper.statemachine.PowerStateMachineProxy;
import com.miui.powerkeeper.ui.AppStateInfo;
import com.miui.powerkeeper.utils.AlarmControlManager;
import com.miui.powerkeeper.utils.BatteryStateObserver;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.DisplayFoldStateObserver;
import com.miui.powerkeeper.utils.FloatSceneObserver;
import com.miui.powerkeeper.utils.PowerKeeperPackageManager;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.ProcessObserver;
import com.miui.powerkeeper.utils.ScreenStateObserver;
import com.miui.powerkeeper.utils.SmartScenarioObserver;
import com.miui.powerkeeper.utils.Utils;
import com.miui.powerkeeper.utils.VpnTracker;
import com.miui.whetstone.WhetstoneActivityManager;
import f.m;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperManager extends IPowerKeeper.Stub {
    public static final String ACTION_ALARM = "miui.intent.action.powerkeeper_alarm";
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public static boolean JNI_LOADED = false;
    public static final String TAG = "PowerKeeperService";
    private static Handler sHandler;
    private static volatile PowerKeeperManager sInstance;
    private static HandlerThread sThread;
    private ActiveController mActiveController;
    private ActiveStateController mActiveStateController;
    private AlarmControlManager mAlarmControlManager;
    private AlarmController mAlarmController;
    private AppActiveChecker mAppActiveChecker;
    private AppClusterController mAppClusterController;
    private PowerKeeperInterface.l mAppIdleAppRuleChecker;
    private AppStandbyController mAppStandbyController;
    private AudioDisguiseController mAudioDisguiseController;
    private BatteryStateObserver mBatteryStateObserver;
    private BgIdleController mBgIdleController;
    private PowerKeeperInterface.l mBgIdleRuleChecker;
    private BroadcastController mBroadcastController;
    private Context mContext;
    private PowerKeeperInterface.l mDeviceIdleAppRuleChecker;
    private DeviceIdleController mDeviceIdleController;
    private DisplayFoldStateObserver mDisplayFoldStateObserver;
    private EventLogManager mEventLogManager;
    private EventsAggregator mEventsAggregator;
    private IFeedbackControl mFeedbackControlManager;
    private IFeedbackControlListener.Stub mFeedbackListener;
    private FloatSceneObserver mFloatSceneObserver;
    private ForegroundServiceManager mForegroundServiceManager;
    private FrozenAppController mFrozenAppController;
    private PowerKeeperInterface.l mFrozenAppRuleChecker;
    private HideModeStateMachine mHideModeStateMachine;
    private IMECheckManager mIMECheckManager;
    private PowerKeeperInterface.l mKillProcessAppRuleChecker;
    private KillProcessController mKillProcessController;
    private PowerKeeperInterface.l mLocationAppRuleChecker;
    private LocationPolicyController mLocationPolicyController;
    private e.e mModulesController;
    private OpRunInBackgroundManager mOpRunInBackgroundManager;
    private PowerKeeperPackageManager mPackageManager;
    private IPowerChecker mPowerCheckerManager;
    private PowerKeeperClientProxy mPowerKeeperClientProxy;
    private PowerKeeperConfigureManager mPowerKeeperConfigureManager;
    private PowerStateMachineProxy mPowerStateMachineProxy;
    private ProcessObserver mProcessObserver;
    private ScreenStateObserver mScreenStateObserver;
    private SensorController mSensorAppController;
    private PowerKeeperInterface.l mSensorAppRuleChecker;
    private SmartScenarioObserver mSmartScenarioObserver;
    private TimeScheduleManager mTimeScheduleManager;
    private UserCheckManager mUserCheckManager;
    private VpnTracker mVpnTracker;
    private WakelockInterfaceManager mWakelockInterfaceManager;
    private WakelockPolicyManager mWakelockPolicyManager;
    private WidgetCheckManager mWidgetCheckManager;
    public Object mSyncObject = new Object();
    private boolean isBootCompleted = false;
    private boolean mIsGestureBoost = false;
    private int mDelay = 10;
    private ServiceConnection feedbackConn = new b();
    private ServiceConnection powercheckerConn = new c();

    class a implements Runnable {
        a() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Utils.increaseStartTimes();
        }
    }

    class b implements ServiceConnection {
        b() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PowerKeeperManager.this.mFeedbackControlManager = IFeedbackControl.Stub.asInterface(iBinder);
            m mVar = (m) e.e.i(17);
            if (mVar == null || mVar.c() == null) {
                Log.d(PowerKeeperManager.TAG, "GmsObserver onCreate failed because gmsObserver init failed.");
            } else {
                mVar.c().onCreate(PowerKeeperManager.this.mFeedbackControlManager);
            }
            try {
                PowerKeeperManager powerKeeperManager = PowerKeeperManager.this;
                powerKeeperManager.mFeedbackListener = FeedBackManager.getInstance(powerKeeperManager.mContext);
                PowerKeeperManager.this.mFeedbackControlManager.registerFeedbackControlListener(PowerKeeperManager.this.mFeedbackListener);
            } catch (RemoteException e2) {
                Log.e(PowerKeeperManager.TAG, "registerFeedbackControlListener", e2);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            try {
                PowerKeeperManager.this.mFeedbackControlManager.unregisterFeedbackControlListener(PowerKeeperManager.this.mFeedbackListener);
            } catch (RemoteException e2) {
                Log.e(PowerKeeperManager.TAG, "unregisterFeedbackControlListener", e2);
            }
            PowerKeeperManager.this.mFeedbackListener = null;
            PowerKeeperManager.this.mFeedbackControlManager = null;
        }
    }

    class c implements ServiceConnection {
        c() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PowerKeeperManager.this.mPowerCheckerManager = IPowerChecker.Stub.asInterface(iBinder);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            PowerKeeperManager.this.mPowerCheckerManager = null;
        }
    }

    static {
        JNI_LOADED = false;
        if (Build.SUPPORTED_ABIS[0].startsWith("arm")) {
            System.loadLibrary("powerkeeper_jni");
            JNI_LOADED = true;
        }
    }

    private PowerKeeperManager(final Context context) {
        getHandler().post(new a());
        initDelayTime();
        this.mContext = context;
        this.mTimeScheduleManager = new TimeScheduleManager(this.mContext);
        this.mEventsAggregator = EventsAggregator.getInstance(this.mContext);
        UserCheckManager userCheckManagerV = UserCheckManager.v(this.mContext);
        this.mUserCheckManager = userCheckManagerV;
        this.mPackageManager = PowerKeeperPackageManager.getInstance(this.mContext, userCheckManagerV, this.mEventsAggregator);
        this.mForegroundServiceManager = ForegroundServiceManager.b(this.mContext);
        this.mProcessObserver = new ProcessObserver(this.mContext, this.mEventsAggregator);
        this.mScreenStateObserver = new ScreenStateObserver(this.mContext, this.mEventsAggregator);
        this.mDisplayFoldStateObserver = new DisplayFoldStateObserver(this.mEventsAggregator);
        this.mBatteryStateObserver = new BatteryStateObserver(this.mContext, this.mEventsAggregator);
        this.mSmartScenarioObserver = new SmartScenarioObserver(this.mEventsAggregator);
        this.mIMECheckManager = new IMECheckManager(this.mContext, this);
        this.mWidgetCheckManager = new WidgetCheckManager(this.mContext, this);
        this.mVpnTracker = new VpnTracker(this.mContext);
        this.mAlarmControlManager = new AlarmControlManager(this.mContext);
        this.mWakelockInterfaceManager = new WakelockInterfaceManager(this.mContext);
        this.mWakelockPolicyManager = new WakelockPolicyManager(this.mContext, this);
        this.mOpRunInBackgroundManager = new OpRunInBackgroundManager(this.mContext, this);
        this.mHideModeStateMachine = new HideModeStateMachine(this.mContext, this);
        Log.i(TAG, "init PowerKeeperManager start");
        if (SystemProperties.getInt(Constant.PROP_START_TIMES, 0) <= 1) {
            getHandler().postDelayed(new Runnable() { // from class: com.miui.powerkeeper.d
                @Override // java.lang.Runnable
                public final void run() {
                    this.f390a.lambda$new$0(context);
                }
            }, ((long) this.mDelay) * 1000);
        } else {
            Log.i(TAG, "init PowerKeeperManager");
            initOthers(context);
        }
        Utils.protectCurrentProcess(true);
        Log.i(TAG, "created");
    }

    private static void ensureThreadLocked() {
        if (sThread == null) {
            HandlerThread handlerThread = new HandlerThread(TAG, 0);
            sThread = handlerThread;
            handlerThread.start();
            sHandler = new Handler(sThread.getLooper());
        }
    }

    public static synchronized Handler getHandler() {
        ensureThreadLocked();
        return sHandler;
    }

    public static PowerKeeperManager getInstance() {
        if (sInstance == null) {
            synchronized (PowerKeeperManager.class) {
                try {
                    if (sInstance == null) {
                        if (!PowerKeeperApplication.d()) {
                            Log.d(TAG, "Kill self for provision not ready");
                            Process.killProcess(Process.myPid());
                        }
                        if (DEBUG) {
                            Log.d(TAG, Log.getStackTraceString(new Throwable()));
                        }
                        sInstance = new PowerKeeperManager(Utils.APP_CONTEXT);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public static synchronized HandlerThread getThread() {
        ensureThreadLocked();
        return sThread;
    }

    private void initDelayTime() {
        if (!DEBUG) {
            String str = Build.DEVICE;
            if (str.equals("muyu") || str.equals("uke")) {
                this.mDelay = 1200;
            }
        }
        int i2 = SystemProperties.getInt("persist.debug.memory.power", 0);
        if (i2 != 0) {
            String str2 = Build.DEVICE;
            if (str2.equals("dada") || str2.equals("haotian")) {
                this.mDelay = i2 * 60;
            }
        }
    }

    private void initOthers(Context context) {
        this.mAppActiveChecker = new AppActiveChecker(this.mContext, this);
        Bundle bundle = new Bundle();
        bundle.putBoolean(HideModeStateMachineConfig.KEY_REGARD_FOREGROUND_SERVICE, true);
        this.mKillProcessAppRuleChecker = new AppRuleCheckerRegardBackground(this.mContext, this, "KillProcessAppRuleChecker", bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_BACKGROUND, false);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_PRETASK, false);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_FOREGROUND_SERVICE, false);
        this.mLocationAppRuleChecker = new HideModeStateMachineWrapper(this.mContext, this, "location", HideModeStateMachineConfig.STATE_INACTIVE_LEVEL_1ST, bundle2);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_BACKGROUND, true);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_PRETASK, false);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_FOREGROUND_SERVICE, true);
        this.mFrozenAppRuleChecker = new HideModeStateMachineWrapper(this.mContext, this, "frozen", HideModeStateMachineConfig.STATE_INACTIVE_LEVEL_3RD, bundle2);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_BACKGROUND, true);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_PRETASK, false);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_FOREGROUND_SERVICE, false);
        this.mSensorAppRuleChecker = new HideModeStateMachineWrapper(this.mContext, this, HideModeStateMachineConfig.STRATEGY_SENSOR, HideModeStateMachineConfig.STATE_INACTIVE_LEVEL_1ST, bundle2);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_BACKGROUND, true);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_PRETASK, false);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_FOREGROUND_SERVICE, true);
        this.mBgIdleRuleChecker = new HideModeStateMachineWrapper(this.mContext, this, HideModeStateMachineConfig.STRATEGY_BKIDLE, HideModeStateMachineConfig.STATE_INACTIVE_LEVEL_2ND, bundle2);
        this.mActiveStateController = new ActiveStateController(this.mContext, this);
        this.mFrozenAppController = new FrozenAppController(this.mContext, this);
        this.mBroadcastController = new BroadcastController(this.mContext, this);
        this.mSensorAppController = new SensorController(this.mContext, this);
        this.mBgIdleController = new BgIdleController(this.mContext, this);
        this.mKillProcessController = new KillProcessController(this.mContext, this);
        this.mAppClusterController = new AppClusterController(this.mContext, this);
        this.mLocationPolicyController = new LocationPolicyController(this.mContext);
        Bundle bundle3 = new Bundle();
        bundle3.putBoolean("SET_WHITE_LIST", true);
        this.mDeviceIdleAppRuleChecker = new AppRuleChecker(this.mContext, this, "DeviceIdleAppRuleChecker", bundle3);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_BACKGROUND, true);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_PRETASK, false);
        bundle2.putBoolean(HideModeStateMachineConfig.KEY_REGARD_FOREGROUND_SERVICE, false);
        this.mAppIdleAppRuleChecker = new HideModeStateMachineWrapper(this.mContext, this, HideModeStateMachineConfig.STRATEGY_APPIDLE, HideModeStateMachineConfig.STATE_INACTIVE_LEVEL_1ST, bundle2);
        this.mDeviceIdleController = new DeviceIdleController(this.mContext, this);
        this.mAppStandbyController = new AppStandbyController(this.mContext, this);
        this.mAlarmController = new AlarmController(this.mContext, this);
        this.mAudioDisguiseController = new AudioDisguiseController(this.mContext, this);
        this.mFloatSceneObserver = new FloatSceneObserver(this.mContext, this.mEventsAggregator);
        this.mPowerStateMachineProxy = PowerStateMachineProxy.init();
        this.mActiveController = new ActiveController(this.mVpnTracker, this, this.mContext);
        this.mEventLogManager = new EventLogManager(this, context);
        this.mPowerKeeperConfigureManager = new PowerKeeperConfigureManager(this.mContext, this);
        this.mPowerKeeperClientProxy = new PowerKeeperClientProxy(this, context);
        parseLocalConfig();
        Intent intent = new Intent();
        intent.setClass(this.mContext, FeedbackControlService.class);
        this.mContext.bindService(intent, this.feedbackConn, 1);
        WhetstoneActivityManager.bindWhetstoneService(asBinder());
        if (SystemProperties.get("ro.miui.region", "unknown").equalsIgnoreCase("cn")) {
            MiPushHelper.init(this.mContext);
        }
        if (JNI_LOADED) {
            this.mIsGestureBoost = nativeIsGestureBoostDevice(Build.DEVICE);
        }
        this.mModulesController = e.e.h();
        this.mPowerKeeperConfigureManager.onBootCompleted();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Context context) {
        Log.i(TAG, "delay init PowerKeeperManager");
        initOthers(context);
    }

    private static native boolean nativeIsGestureBoostDevice(String str);

    private void parseLocalConfig() {
        Log.i(TAG, "parseLocalConfig");
        Intent intent = new Intent();
        intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.cloudcontrol.CloudUpdateReceiver");
        intent.setAction(Constants.INIT_PROFILE);
        this.mContext.sendBroadcast(intent);
    }

    public void bindPowerCheckerService() {
        Intent intent = new Intent();
        intent.setClass(this.mContext, PowerCheckerService.class);
        this.mContext.bindService(intent, this.powercheckerConn, 1);
    }

    /* JADX WARN: Code duplicated, block: B:32:0x0077  */
    @Override // android.os.Binder
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        ActiveController activeController;
        DeviceIdleController deviceIdleController;
        boolean z8 = false;
        if (strArr != null && strArr.length == 1) {
            if ("-cloudVersion".equalsIgnoreCase(strArr[0])) {
                printWriter.println(LocalUpdateUtils.getCloudVersion(this.mContext));
                return;
            } else if ("-updateCloud".equalsIgnoreCase(strArr[0])) {
                LocalUpdateUtils.waitAndDumpUpdateCloud(printWriter, this.mContext, sHandler);
                return;
            }
        }
        if (this.mPowerKeeperConfigureManager == null) {
            printWriter.println("PowerKeeperManager is waiting for init.");
            return;
        }
        if (strArr != null) {
            boolean z9 = true;
            z2 = false;
            boolean z10 = false;
            z3 = false;
            z4 = false;
            z5 = false;
            z6 = false;
            for (String str : strArr) {
                if ("hiddenMode".equalsIgnoreCase(str)) {
                    z9 = false;
                    z2 = true;
                } else if ("activeState".equalsIgnoreCase(str)) {
                    z9 = false;
                    z10 = true;
                } else if ("wakelockPolicy".equals(str)) {
                    z9 = false;
                    z5 = true;
                } else if ("BroadcastController".equals(str)) {
                    z9 = false;
                } else if ("SensorController".equals(str)) {
                    z9 = false;
                    z4 = true;
                } else if ("AppIdleController".equals(str)) {
                    z9 = false;
                    z3 = true;
                } else if ("ProcessObserver".equals(str)) {
                    z9 = false;
                    z6 = true;
                } else if ("perfengine".equals(str) || "gms".equals(str)) {
                    z9 = false;
                }
            }
            z7 = z9;
            z8 = z10;
        } else {
            z2 = false;
            z3 = false;
            z4 = false;
            z5 = false;
            z6 = false;
            z7 = true;
        }
        this.mEventLogManager.dump(fileDescriptor, printWriter, strArr);
        if (z7 || z8 || z2 || z3 || z4) {
            this.mPowerKeeperConfigureManager.dump(fileDescriptor, printWriter, strArr);
            this.mHideModeStateMachine.dump(fileDescriptor, printWriter, strArr);
            this.mAppActiveChecker.v(fileDescriptor, printWriter, strArr);
            this.mAppStandbyController.dump(fileDescriptor, printWriter, strArr);
            this.mKillProcessController.dump(fileDescriptor, printWriter, strArr);
            this.mSensorAppController.dump(fileDescriptor, printWriter, strArr);
            this.mBgIdleController.dump(fileDescriptor, printWriter, strArr);
            this.mFrozenAppController.dump(fileDescriptor, printWriter, strArr);
            this.mOpRunInBackgroundManager.i(fileDescriptor, printWriter, strArr);
            this.mAppClusterController.dump(fileDescriptor, printWriter, strArr);
        }
        if (z7 || z5) {
            this.mWakelockPolicyManager.c(fileDescriptor, printWriter, strArr);
        }
        if (z7 || z6) {
            this.mProcessObserver.dump(fileDescriptor, printWriter, strArr);
        }
        if (z7 && (deviceIdleController = this.mDeviceIdleController) != null) {
            deviceIdleController.dump(fileDescriptor, printWriter, strArr);
        }
        if (z7 && (activeController = this.mActiveController) != null) {
            activeController.dump(fileDescriptor, printWriter, strArr);
        }
        if (z7) {
            PowerKeeperPackageManager powerKeeperPackageManager = this.mPackageManager;
            if (powerKeeperPackageManager != null) {
                powerKeeperPackageManager.dump(fileDescriptor, printWriter, strArr);
            }
            PowerLog.dump(fileDescriptor, printWriter, strArr);
        }
    }

    public ActiveController getActiveController() {
        return this.mActiveController;
    }

    public ActiveStateController getActiveStateController() {
        return this.mActiveStateController;
    }

    public AlarmController getAlarmController() {
        return this.mAlarmController;
    }

    public AppActiveChecker getAppActiveChecker() {
        return this.mAppActiveChecker;
    }

    public boolean getAppActiveState(int i2) {
        AppActiveChecker appActiveChecker = this.mAppActiveChecker;
        if (appActiveChecker != null) {
            return appActiveChecker.y(i2);
        }
        return false;
    }

    public AppClusterController getAppClusterController() {
        return this.mAppClusterController;
    }

    public PowerKeeperInterface.l getAppIdleAppRuleChecker() {
        return this.mAppIdleAppRuleChecker;
    }

    public AppStandbyController getAppIdleController() {
        return this.mAppStandbyController;
    }

    public AudioDisguiseController getAudioDisguiseController() {
        return this.mAudioDisguiseController;
    }

    public int getBatteryCurLevel() {
        return this.mBatteryStateObserver.getBatteryCurLevel();
    }

    public int getBatteryCurTemp() {
        return this.mBatteryStateObserver.getBatteryCurTemp();
    }

    public BgIdleController getBgIdleController() {
        return this.mBgIdleController;
    }

    public PowerKeeperInterface.l getBgIdleRuleChecker() {
        return this.mBgIdleRuleChecker;
    }

    public BroadcastController getBroadcastController() {
        return this.mBroadcastController;
    }

    public int getCurrentIME(int i2) {
        return this.mIMECheckManager.h(i2);
    }

    public int[] getCurrentVpn() {
        return this.mVpnTracker.getCurrentVpn();
    }

    public Integer[] getCurrentWidgets(int i2) {
        return this.mWidgetCheckManager.e(i2);
    }

    public PowerKeeperInterface.l getDeviceIdleAppRuleChecker() {
        return this.mDeviceIdleAppRuleChecker;
    }

    public DeviceIdleController getDeviceIdleController() {
        return this.mDeviceIdleController;
    }

    public EventsAggregator getEventsAggregator() {
        return this.mEventsAggregator;
    }

    public IFeedbackControl getFeedbackControlManager() {
        return this.mFeedbackControlManager;
    }

    public FloatSceneObserver getFloatSceneObserver() {
        return this.mFloatSceneObserver;
    }

    public FrozenAppController getFrozenAppController() {
        return this.mFrozenAppController;
    }

    public PowerKeeperInterface.l getFrozenAppRuleChecker() {
        return this.mFrozenAppRuleChecker;
    }

    @Override // com.miui.powerkeeper.IPowerKeeper
    public List<AppStateInfo> getHideModeAppInfoList() throws RemoteException {
        return this.mAppActiveChecker.z();
    }

    public HideModeStateMachine getHideModeStateMachine() {
        return this.mHideModeStateMachine;
    }

    public boolean getIsGestureBoost() {
        return this.mIsGestureBoost;
    }

    public PowerKeeperInterface.l getKillProcessAppRuleChecker() {
        return this.mKillProcessAppRuleChecker;
    }

    public KillProcessController getKillProcessController() {
        return this.mKillProcessController;
    }

    public long getLastScreenOffTime() {
        return this.mScreenStateObserver.getLastScreenOffTime();
    }

    public PowerKeeperInterface.l getLocationAppRuleChecker() {
        return this.mLocationAppRuleChecker;
    }

    public Handler getMilletHandler() {
        return this.mFrozenAppController.getMilletHandler();
    }

    public OpRunInBackgroundManager getOpRunInBackgroundManager() {
        return this.mOpRunInBackgroundManager;
    }

    public Map<String, Integer> getPackageUidsMap(int i2) {
        return this.mPackageManager.getPackageUidsMap(i2);
    }

    public IPowerChecker getPowerCheckerManager() {
        return this.mPowerCheckerManager;
    }

    @Override // com.miui.powerkeeper.IPowerKeeper
    public int getPowerSaveAppConfigure(Bundle bundle, Bundle bundle2) {
        return PowerSaveConfigureManager.getInstance(this.mContext).getPowerSaveAppConfigure(bundle, bundle2);
    }

    public PowerStateMachineProxy getPowerStateMachineProxy() {
        return this.mPowerStateMachineProxy;
    }

    public ProcessObserver getProcessObserver() {
        return this.mProcessObserver;
    }

    public PowerKeeperInterface.l getSensorAppRuleChecker() {
        return this.mSensorAppRuleChecker;
    }

    public SensorController getSensorController() {
        return this.mSensorAppController;
    }

    public TimeScheduleManager getTimeScheduleManager() {
        return this.mTimeScheduleManager;
    }

    public UserCheckManager getUserCheckManager() {
        return this.mUserCheckManager;
    }

    public VpnTracker getVpnTracker() {
        return this.mVpnTracker;
    }

    public IWakelockManager getWakelockManagerInterface() {
        return this.mWakelockInterfaceManager.b();
    }

    public boolean isCharging() {
        return this.mBatteryStateObserver.isCharging();
    }

    public boolean isScreenOn() {
        return this.mScreenStateObserver.isScreenOn();
    }

    @Override // com.miui.powerkeeper.IPowerKeeper
    public boolean isSupportVideoEnhancePkg(String str) {
        DisplayFrameSetting dFSInstance = DisplayFrameSetting.getDFSInstance();
        if (dFSInstance != null) {
            return dFSInstance.isSupportVideoEnhancePkg(str);
        }
        return false;
    }

    public boolean isUidForeground(int i2) {
        return this.mProcessObserver.isUidForeground(i2);
    }

    public void notifyAMExecServiceEvent(int i2, boolean z2) {
        this.mFrozenAppController.notifyAMExecServiceEvent(i2, z2);
    }

    public void notifyEvent(int i2, Object obj) {
        FrozenAppController frozenAppController = this.mFrozenAppController;
        if (frozenAppController != null) {
            frozenAppController.notifyEvent(i2, obj);
        }
    }

    public void notifyWakeUpFrozenAppEvent(int i2) {
        this.mFrozenAppController.notifyWakeUpFrozenAppEvent(i2);
    }

    public void onAlarm(Intent intent) {
        Log.i(TAG, "onAlarm");
        this.mAlarmControlManager.onAlarm();
    }

    public void onBootCompleted() {
        Log.i(TAG, "onBootCompleted");
        synchronized (this.mSyncObject) {
            try {
                if (this.isBootCompleted) {
                    return;
                }
                this.isBootCompleted = true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void onCreate() {
        Log.i(TAG, "onCreate");
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (this.mModulesController == null) {
            return;
        }
        this.mEventLogManager.onDestory();
        synchronized (this.mSyncObject) {
            this.mLocationPolicyController.unregisterLocationObserver();
            try {
                this.mContext.unbindService(this.feedbackConn);
                if (DEBUG) {
                    Log.d(TAG, "unbind FeedbackControlService");
                }
            } catch (RuntimeException e2) {
                Log.e(TAG, "unbind FeedbackControlService", e2);
            }
        }
        if (e.e.k()) {
            e.e.h().f(3);
        }
    }

    public void registerAlarmListener(PowerKeeperInterface.a aVar) {
        this.mAlarmControlManager.registerAlarmHappenListener(aVar);
    }

    public void registerBatteryChangedListener(PowerKeeperInterface.d dVar) {
        this.mBatteryStateObserver.registerBatteryChangedListener(dVar);
    }

    public void registerForegroundActivitiesChangedListener(PowerKeeperInterface.f fVar) {
        this.mProcessObserver.registerForegroundActivitiesChangedListener(fVar);
    }

    public void registerIMEChangeListener(PowerKeeperInterface.w wVar) {
        this.mIMECheckManager.l(wVar);
    }

    public void registerPackageChangedListener(PowerKeeperInterface.d0 d0Var) {
        this.mPackageManager.registerPackageChangedListener(d0Var);
    }

    public void registerProcessDiedListener(PowerKeeperInterface.e0 e0Var) {
        this.mProcessObserver.registerProcessDiedListener(e0Var);
    }

    public void registerScreenChangedListener(PowerKeeperInterface.f0 f0Var) {
        this.mScreenStateObserver.registerScreenChangedListener(f0Var);
    }

    public boolean registerVpnStatusCallback(VpnTracker.StatusCallback statusCallback) {
        this.mVpnTracker.registerVpnStatusCallback(statusCallback);
        return true;
    }

    public void registerWidgetChangeListener(PowerKeeperInterface.h0 h0Var) {
        this.mWidgetCheckManager.g(h0Var);
    }

    @Override // com.miui.powerkeeper.IPowerKeeper
    public int setPowerSaveAppConfigure(Bundle bundle) {
        return PowerSaveConfigureManager.getInstance(this.mContext).setPowerSaveAppConfigure(bundle);
    }

    public void showNotification(boolean z2) {
        if (this.mFrozenAppController.getBrightCtrl() != null) {
            this.mFrozenAppController.getBrightCtrl().showNotification(z2);
        }
    }

    public void unbindPowerCheckerService() {
        try {
            this.mContext.unbindService(this.powercheckerConn);
            if (DEBUG) {
                Log.d(TAG, "unbind PowerCheckerService");
            }
        } catch (RuntimeException e2) {
            Log.e(TAG, "unbind PowerCheckerService", e2);
        }
    }

    public void unregisterAlarmListener(PowerKeeperInterface.a aVar) {
        this.mAlarmControlManager.unregisterAlarmHappenListener(aVar);
    }

    public void unregisterBatteryChangedListener(PowerKeeperInterface.d dVar) {
        this.mBatteryStateObserver.unregisterBatteryChangedListener(dVar);
    }

    public void unregisterForegroundActivitiesChangedListener(PowerKeeperInterface.f fVar) {
        this.mProcessObserver.unregisterForegroundActivitiesChangedListener(fVar);
    }

    public void unregisterIMEChangeListener(PowerKeeperInterface.w wVar) {
        this.mIMECheckManager.m(wVar);
    }

    public void unregisterPackageChangedListener(PowerKeeperInterface.d0 d0Var) {
        this.mPackageManager.unregisterPackageChangedListener(d0Var);
    }

    public void unregisterProcessDiedListener(PowerKeeperInterface.e0 e0Var) {
        this.mProcessObserver.unregisterProcessDiedListener(e0Var);
    }

    public void unregisterScreenChangedListener(PowerKeeperInterface.f0 f0Var) {
        this.mScreenStateObserver.unregisterScreenChangedListener(f0Var);
    }

    public boolean unregisterVpnStatusCallback(VpnTracker.StatusCallback statusCallback) {
        this.mVpnTracker.unregisterVpnStatusCallback(statusCallback);
        return true;
    }

    public void unregisterWidgetChangeListener(PowerKeeperInterface.h0 h0Var) {
        this.mWidgetCheckManager.h(h0Var);
    }

    @Override // com.miui.powerkeeper.IPowerKeeper
    public void restrictAppQuick(int i2) {
    }
}
