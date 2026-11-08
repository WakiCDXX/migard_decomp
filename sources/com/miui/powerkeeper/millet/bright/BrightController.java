package com.miui.powerkeeper.millet.bright;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.c;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.millet.FreezeBinder;
import com.miui.powerkeeper.millet.MilletConfig;
import com.miui.powerkeeper.millet.MilletHandler;
import com.miui.powerkeeper.millet.MilletPolicy;
import com.miui.powerkeeper.millet.MilletUidObserver;
import com.miui.powerkeeper.millet.MilletUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.process.ForegroundInfo;
import miui.process.ProcessManager;

/* JADX INFO: loaded from: classes.dex */
public class BrightController {
    public static boolean DEBUG = false;
    private static int DELAY_TIME = 0;
    private static String REASON_BRIGHT_MILLET = null;
    public static String REASON_BRIGHT_MILLET_END = null;
    private static String TAG = "BrightController";
    private FrozenAppController mCtrl;
    private Context mCtx;
    private Handler mHandler;
    private boolean isScreenOn = false;
    private int mCurForegroundUid = -1;
    private boolean mFreezeEnvAvail = false;
    private boolean isScenerioing = false;
    private boolean isListening = false;
    private int freezeTryCnt = 0;
    private List<BrightScenarioBase> mSceList = new ArrayList();
    private BrightScenarioBase mCurrentSce = null;
    private EventsAggregator eventsAggregator = null;
    private Map<Integer, String> mapUidPkg = new HashMap();
    private Handler mH = null;
    private boolean mPrepareFreeze = false;
    private boolean mIsFreezing = false;
    private boolean mNotificationShow = false;
    private List<Integer> mFreezeUids = new ArrayList();
    private int mPowerMode = 0;
    private boolean mBackuping = false;
    private Set<String> gameAppSet = new HashSet();
    private List<String> transparentApps = new ArrayList(Arrays.asList("com.android.quicksearchbox"));
    private final int FREEZE_REASON_SCENARIO = 1;
    private final int FREEZE_REASON_ANRCHANGE = 2;
    private final int FREEZE_REASON_NEWPROCESS = 3;
    private final int FREEZE_REASON_SCENARIO_END = 4;
    private final int FREEZE_REASON_TOFRONT = 5;
    private final int FREEZE_REASON_TOBACKGROUND = 6;
    private int MSG_WHAT_NOTIFICATION_BAR = 10;
    private CloudFunctionConfig.ICloudFunctionListener mFunctionListener = new CloudFunctionConfig.ICloudFunctionListener() { // from class: com.miui.powerkeeper.millet.bright.BrightController.2
        @Override // com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig.ICloudFunctionListener
        public void onCloudFunctionUpdated() {
            BrightController.this.onBrightFrozenChanged();
        }
    };
    private PowerKeeperInterface.x mPackageReceiver = new PowerKeeperInterface.x() { // from class: com.miui.powerkeeper.millet.bright.BrightController.3
        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onPackageAdded(int i2, String str, int i3) {
            if (MilletUtils.isAppUid(i3)) {
                if (str != null && (MilletUtils.backupPkg.equals(str) || MilletUtils.huanjiPkg.equals(str))) {
                    MilletUtils.updatePkgState(BrightController.this.mCtx, str, true);
                }
                Iterator it = BrightController.this.mSceList.iterator();
                while (it.hasNext()) {
                    ((BrightScenarioBase) it.next()).dealPackageAddRemove(str, true);
                }
                ((MilletHandler) BrightController.this.mCtrl.getScreenOffHandler()).getUidObserver().putIsSystem(i3, MilletUtils.isSystemApp(BrightController.this.mCtx, i3));
                if (BrightController.DEBUG) {
                    Log.d(BrightController.TAG, "add pkg: " + str + " uid=" + i3);
                }
                if (MilletPolicy.BRIGHT_WHITE_APPS.contains(str)) {
                    BrightController.this.mCtrl.getMilletPolicy().mBrightWhiteUids.add(Integer.valueOf(i3));
                }
                for (String str2 : MilletUtils.CTS_NAME) {
                    if (str != null && str.contains(str2) && !str.contains("ctsshim")) {
                        BrightController.this.mCtrl.setChangeFrozenStrategy(true, 16, false);
                        break;
                    }
                }
                if (MilletPolicy.MESSAGE_APPS.contains(str) && UserHandle.isApp(i3) && BrightController.this.mCtrl != null && BrightController.this.mCtrl.getMilletPolicy() != null) {
                    BrightController.this.mCtrl.getMilletPolicy().dealPacket(i3, true, BrightController.this.mCtrl.getMilletHandler());
                }
                BrightController.this.mCtrl.getMilletPolicy().checkImportantApps();
                BrightController.this.mCtrl.getMilletPolicy().updateSpecialBusiness();
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onPackageRemoved(int i2, String str, int i3) {
            if (MilletUtils.isAppUid(i3)) {
                Iterator it = BrightController.this.mSceList.iterator();
                while (it.hasNext()) {
                    ((BrightScenarioBase) it.next()).dealPackageAddRemove(str, false);
                }
                if (MilletPolicy.BRIGHT_WHITE_APPS.contains(str)) {
                    BrightController.this.mCtrl.getMilletPolicy().mBrightWhiteUids.remove(Integer.valueOf(i3));
                }
                if (BrightController.DEBUG) {
                    Log.d(BrightController.TAG, "remove pkg: " + str + " uid=" + i3);
                }
                MilletUidObserver.removePackage(str);
                BrightController.this.mCtrl.getMilletPolicy().checkImportantApps();
                BrightController.this.mCtrl.getMilletPolicy().updateSpecialBusiness();
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onRegistered() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onUidRemoved(int i2, int i3) {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onUserReadyPackageAdded(int i2, Map<String, Integer> map) {
        }
    };
    private int oldUid = -1;

    public class GameAppListBroadcastReceiver extends BroadcastReceiver {
        public String ACTION_JOYOSE_TOP_GAME_LIST = "intent.action.TOP_GAME_LIST";
        public String ACTION_TOP_GAME_LIST = "com.miui.securitycenter.intent.action.TOP_GAME_LIST";

        public GameAppListBroadcastReceiver() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(this.ACTION_JOYOSE_TOP_GAME_LIST);
            intentFilter.addAction(this.ACTION_TOP_GAME_LIST);
            BrightController.this.mCtx.registerReceiver(this, intentFilter, 2);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (this.ACTION_JOYOSE_TOP_GAME_LIST.equals(action) || this.ACTION_TOP_GAME_LIST.equals(action)) {
                String stringExtra = intent.getStringExtra("gameList");
                if (stringExtra == null || stringExtra.isEmpty()) {
                    Log.d(BrightController.TAG, "onReceive(), gameList is null!");
                    return;
                }
                String[] strArrSplit = stringExtra.split(",");
                for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                    String str = strArrSplit[i2];
                    if (str != null && !str.isEmpty()) {
                        BrightController.this.gameAppSet.add(strArrSplit[i2]);
                    }
                }
            }
        }
    }

    static {
        DEBUG = Log.isLoggable("BrightController", 3) || Build.IS_DEBUGGABLE;
        DELAY_TIME = MilletConfig.getScreenOnDelay();
        REASON_BRIGHT_MILLET = "BrightMillet";
        REASON_BRIGHT_MILLET_END = "BrightMilletEnd";
    }

    public BrightController(Context context, FrozenAppController frozenAppController) {
        this.mHandler = null;
        this.mCtrl = null;
        this.mCtx = context;
        this.mCtrl = frozenAppController;
        if (DEBUG) {
            Log.d(TAG, "BrightControll  init");
        }
        this.mHandler = new MilletBrightHandler(BackgroundThread.getHandler().getLooper(), context, frozenAppController);
        CloudFunctionConfig.getInstance().registerCloudFunctionConfig(this.mFunctionListener);
        this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.bright.BrightController.1
            @Override // java.lang.Runnable
            public void run() {
                PowerKeeperManager.getInstance().registerPackageChangedListener(new PowerKeeperInterface.d0(BrightController.this.mHandler, BrightController.this.mPackageReceiver));
                BrightController.this.eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
            }
        }, 4000L);
        new GameAppListBroadcastReceiver();
    }

    private void checkBackUp(String str) {
        if (!this.mBackuping && ("com.miui.backup".equals(str) || "com.miui.huanji".equals(str))) {
            this.mBackuping = true;
            Log.d(TAG, "backuping");
            this.mCtrl.setChangeFrozenStrategy(true, 2, false);
        } else if (this.mBackuping) {
            boolean zIsBackupActive = MilletUtils.isBackupActive(this.mCtx, true);
            this.mBackuping = zIsBackupActive;
            if (zIsBackupActive) {
                return;
            }
            Log.d(TAG, "mBackuping =" + this.mBackuping);
            this.mCtrl.setChangeFrozenStrategy(false, 2, false);
        }
    }

    private void checkCurrentActivity() {
        ForegroundInfo foregroundInfo = ProcessManager.getForegroundInfo();
        this.mCurForegroundUid = foregroundInfo.mForegroundUid;
        if (DEBUG) {
            Log.d(TAG, "checkCurrentActivity pkg: " + foregroundInfo.mForegroundPackageName + " isScreenOn=" + this.isScreenOn);
        }
        commonFreeze(-1);
    }

    private void checkListener() {
        if (DEBUG) {
            Log.d(TAG, "checkListener entry isListening= " + this.isListening);
        }
        if (this.mSceList.size() == 0) {
            initSceList();
        }
        new Handler().postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.bright.BrightController.5
            @Override // java.lang.Runnable
            public void run() {
                BrightController.this.configListener();
            }
        }, 4500L);
    }

    private boolean checkOtherFrozen(int i2) {
        return false;
    }

    private void commonFreeze(int i2) {
        if (this.isScreenOn && this.mFreezeEnvAvail) {
            if (!this.mPrepareFreeze) {
                if (this.mSceList.size() > 0) {
                    this.mCurrentSce = this.mSceList.get(0);
                }
                notifyEnableState(false, false);
                entryScenario();
                return;
            }
            if (i2 <= 0 || MilletUidObserver.isHomeUid(i2)) {
                return;
            }
            int i3 = MilletConfig.isGlobal ? MilletConfig.firstScreenOffDelayTime : DELAY_TIME;
            if (this.gameAppSet.contains(MilletUidObserver.getPkgNameByUid(i2))) {
                i3 = 30000;
            }
            frozenThaw(i2, true, i3, 6);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void configListener() {
        if (DEBUG) {
            Log.d(TAG, "configListener init");
        }
        checkCurrentActivity();
        if (this.isListening) {
            return;
        }
        startListen();
    }

    private void entryScenario() {
        if (this.mPrepareFreeze) {
            if (DEBUG) {
                Log.e(TAG, "mPrepareFreeze true again", new RuntimeException());
                return;
            }
            return;
        }
        this.mPrepareFreeze = true;
        List<Integer> freezingUids = getFreezingUids();
        if (DEBUG) {
            Log.d(TAG, " update ls = " + freezingUids.size() + " :" + freezingUids.toString());
        }
        if (freezingUids.size() > 0) {
            frozenThaw(freezingUids, true, MilletConfig.isGlobal ? MilletConfig.firstScreenOffDelayTime : DELAY_TIME, 1);
            this.mFreezeUids.clear();
            this.mFreezeUids.addAll(freezingUids);
        }
    }

    private void frozenThaw(final int i2, final boolean z2, int i3, final int i4) {
        if (this.mHandler == null || !MilletUtils.isAppUid(i2)) {
            return;
        }
        if (z2) {
            MilletUtils.setPkgData(i2, this.mHandler, i3);
            MilletUtils.checkBg(i2, 4500);
        }
        new Handler().postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.bright.BrightController.6
            @Override // java.lang.Runnable
            public void run() {
                BrightController.this.tryFreeze(i2, z2, i4);
                if (i4 == 1 && z2) {
                    BrightController.this.mIsFreezing = true;
                }
                BrightController.this.freezeTryCnt = 0;
            }
        }, 30, i3);
    }

    private List<Integer> getFreezingUids() {
        ArrayList arrayList = new ArrayList();
        BrightScenarioBase brightScenarioBase = this.mCurrentSce;
        if (brightScenarioBase == null) {
            return arrayList;
        }
        Map<Integer, String> allValidApps = brightScenarioBase.getAllValidApps();
        this.mapUidPkg = allValidApps;
        for (Map.Entry<Integer, String> entry : allValidApps.entrySet()) {
            if (!arrayList.contains(entry.getKey()) && this.mCurForegroundUid != entry.getKey().intValue() && this.mCurrentSce.canFreezePkg(MilletUidObserver.getPkgNameByUid(entry.getKey().intValue()), entry.getKey().intValue())) {
                arrayList.add(entry.getKey());
            }
        }
        return arrayList;
    }

    private void initSceList() {
        this.mSceList.add(new BrightScenarioBase(this.mCtrl.getMilletPolicy(), this.mCtx, true, this));
    }

    private void leaveScenario() {
        restoreState();
        Log.d(TAG, "leaveScenario entry");
        int[] frozenUidsPowerArray = FreezeBinder.getInstance().getFrozenUidsPowerArray();
        if (frozenUidsPowerArray.length > 0) {
            boolean z2 = this.mNotificationShow;
            String str = z2 ? "notif_bar" : "notInScene";
            if (!this.isScreenOn) {
                str = "screenoff";
            } else if (!this.mFreezeEnvAvail) {
                str = "notAvail";
            }
            if (!z2 && this.mCtrl.getFrozenCondition() != 41) {
                FreezeBinder.getInstance().thawAll(str, true);
            }
        }
        MilletUtils.notifyFreezeEvent(false, frozenUidsPowerArray);
    }

    private void notifyAvailableState() {
        Handler handler = this.mHandler;
        if (handler != null) {
            this.mHandler.sendMessage(handler.obtainMessage(26, this.mFreezeEnvAvail ? 1 : 0, 0));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v1, types: [android.os.Handler] */
    /* JADX WARN: Type inference failed for: r4v1, types: [int] */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v3 */
    private void notifyEnableState(boolean z2, boolean z3) {
        ?? r4;
        if (this.mHandler != null) {
            boolean z4 = !isInPerformanceMode();
            if (!z2) {
                r4 = z3;
                r4 = z4;
            }
            r4 = z3;
            this.mHandler.sendMessage(this.mHandler.obtainMessage(28, r4, 0));
        }
    }

    private boolean supportGame() {
        return SystemProperties.getBoolean("persist.sys.thrall.process.freezer_enable", false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryFreeze(int i2, boolean z2, int i3) {
        if (checkOtherFrozen(i2)) {
            return;
        }
        try {
            Handler handler = this.mHandler;
            if (handler != null) {
                Message messageObtainMessage = handler.obtainMessage(1, i2, z2 ? 1 : 0);
                if (i3 == 1) {
                    messageObtainMessage.obj = REASON_BRIGHT_MILLET;
                } else if (i3 == 3) {
                    messageObtainMessage.obj = "Bright new_process";
                } else if (i3 == 5) {
                    messageObtainMessage.obj = "tofront";
                } else if (i3 != 6) {
                    messageObtainMessage.obj = REASON_BRIGHT_MILLET_END;
                } else {
                    messageObtainMessage.obj = "tobg";
                }
                this.mHandler.sendMessage(messageObtainMessage);
            }
        } catch (Exception unused) {
            Log.d(TAG, "tryFreeze error monitor uid: " + i2);
        }
    }

    public boolean getBackupState() {
        return this.mBackuping;
    }

    public int getCurUid() {
        return this.mCurForegroundUid;
    }

    public FrozenAppController getFrzCtrl() {
        return this.mCtrl;
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public boolean getNotificationState() {
        return this.mNotificationShow;
    }

    public BrightScenarioBase getScenario() {
        return this.mCurrentSce;
    }

    public List<BrightScenarioBase> getScenarioList() {
        return this.mSceList;
    }

    public boolean isInPerformanceMode() {
        return false;
    }

    public void notifyForegroundChange(int i2, String str) {
        int i3;
        Handler handler;
        if (DEBUG) {
            Log.d(TAG, "foregroundChange uid=" + i2 + " pkg=" + str);
        }
        this.isScreenOn = true;
        this.mNotificationShow = false;
        this.oldUid = this.mCurForegroundUid;
        this.mCurForegroundUid = i2;
        if (!this.transparentApps.contains(str) || (handler = this.mHandler) == null) {
            MilletUtils.mOnWindowAppList.clear();
        } else {
            handler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.bright.BrightController.4
                @Override // java.lang.Runnable
                public void run() {
                    MilletUtils.updateAppsOnWindowStatus();
                    MilletUtils.mOnWindowAppList.add(Integer.valueOf(BrightController.this.oldUid));
                }
            }, 1000L);
        }
        Handler handler2 = this.mHandler;
        if (handler2 != null) {
            handler2.removeMessages(30);
        }
        frozenThaw(i2, false, 0, 5);
        checkBackUp(str);
        if (this.mBackuping || i2 == (i3 = this.oldUid)) {
            return;
        }
        commonFreeze(i3);
    }

    public void notifyFreezeEvent(boolean z2) {
        if (this.mFreezeUids.size() == 0) {
            return;
        }
        MilletUtils.notifyFreezeEvent(true, this.mFreezeUids.stream().mapToInt(new c()).toArray());
        this.mFreezeUids.clear();
    }

    public void onBrightFrozenChanged() {
        BrightMode.getInstance(this.mCtx).dealBrightCloudWhiteList();
        if (BrightMode.getInstance(this.mCtx).getBrightEnable()) {
            if (this.mSceList.size() == 0) {
                initSceList();
            }
            if (BrightMode.getInstance(this.mCtx).getScenarioApps().size() > 0) {
                Iterator<BrightScenarioBase> it = this.mSceList.iterator();
                while (it.hasNext()) {
                    it.next().reInit();
                }
            }
            if (this.eventsAggregator == null) {
                configListener();
            }
        }
    }

    public void restoreState() {
        this.mIsFreezing = false;
        this.mPrepareFreeze = false;
        BrightScenarioBase brightScenarioBase = this.mCurrentSce;
        if (brightScenarioBase != null) {
            brightScenarioBase.restoreCtrl();
        }
        notifyEnableState(true, false);
        this.mHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "restoreState end");
    }

    public void setFreezeAvail(boolean z2) {
        if (this.mFreezeEnvAvail == z2) {
            return;
        }
        if (this.mH == null) {
            this.mH = this.mCtrl.getMilletHandler();
        }
        if (DEBUG) {
            Log.d(TAG, "setFreezeAvail flag=" + z2);
        }
        this.mFreezeEnvAvail = z2;
        notifyAvailableState();
        this.mCurForegroundUid = ProcessManager.getForegroundInfo().mForegroundUid;
        if (z2) {
            notifyEnableState(false, false);
            checkListener();
        } else if (this.eventsAggregator != null) {
            updateState();
        }
    }

    public void setPowerMode(int i2) {
        this.mPowerMode = i2;
        updateState();
    }

    public void setScreenState(boolean z2) {
        if (this.isScreenOn == z2) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "setScreenState src=" + z2);
        }
        this.isScreenOn = z2;
    }

    public void showNotification(boolean z2) {
        if (this.mNotificationShow == z2 || !this.isListening) {
            this.mNotificationShow = z2;
        } else {
            this.mNotificationShow = z2;
        }
    }

    public void startListen() {
        if (this.isListening || this.eventsAggregator == null) {
            return;
        }
        this.isListening = true;
        if (DEBUG) {
            Log.d(TAG, "startListen");
        }
        this.eventsAggregator.registerForForegroundInfo(this.mHandler);
    }

    public void stopListen() {
        if (this.isListening) {
            this.isListening = false;
            if (DEBUG) {
                Log.d(TAG, "stopListen");
            }
            this.eventsAggregator.unregisterForForegroundInfo(this.mHandler);
        }
    }

    public void takeNewProcess(int i2, String str) {
        BrightScenarioBase brightScenarioBase;
        if (this.mCurForegroundUid == i2 || (brightScenarioBase = this.mCurrentSce) == null || !brightScenarioBase.canFreezePkg(str, i2)) {
            return;
        }
        boolean isSystemApp = MilletUidObserver.getIsSystemApp(i2);
        if (DEBUG) {
            Log.d(TAG, "takeNewProcess uid: " + i2 + " pkg:" + str + " issystem=" + isSystemApp);
        }
        if (isSystemApp || i2 == this.oldUid) {
            return;
        }
        frozenThaw(i2, true, 0, 3);
    }

    public void updateState() {
        if (this.mHandler == null || this.mCurrentSce == null) {
            return;
        }
        try {
            notifyEnableState(false, false);
            if (DEBUG) {
                Log.d(TAG, "updateState isAvai= " + this.mFreezeEnvAvail + " isScreenOn=" + this.isScreenOn + " isScenerioing = " + this.isScenerioing);
            }
            if (this.mFreezeEnvAvail) {
                return;
            }
            leaveScenario();
        } catch (Exception e2) {
            Log.e(TAG, "mCurrentSce =" + this.mCurrentSce, e2);
        }
    }

    private void frozenThaw(final List<Integer> list, final boolean z2, int i2, final int i3) {
        if (this.mHandler == null) {
            return;
        }
        if (z2) {
            for (Integer num : list) {
                MilletUtils.setPkgData(num.intValue(), this.mHandler, i2);
                MilletUtils.checkBg(num.intValue(), 4500);
            }
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.bright.BrightController.7
            @Override // java.lang.Runnable
            public void run() {
                for (int i4 = 0; i4 < list.size(); i4++) {
                    if (i3 == 1 && i4 == list.size() - 1) {
                        BrightController.this.tryFreeze(((Integer) list.get(i4)).intValue(), z2, 4);
                        break;
                    }
                    BrightController.this.tryFreeze(((Integer) list.get(i4)).intValue(), z2, i3);
                }
                if (i3 == 1 && z2) {
                    BrightController.this.mIsFreezing = true;
                }
                BrightController.this.freezeTryCnt = 0;
            }
        }, 30, i2);
    }
}
