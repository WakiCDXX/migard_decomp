package com.miui.powerkeeper.feedbackcontrol;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.statemachine.PowerStateMachineProxy;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.ThermalStoreUtils;
import e.e;
import f.n;
import f.o;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import miui.process.ForegroundInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class FeedbackControlService extends Service {
    public static final int MSG_BACKLIGHT_BECOME_HEAT = 9;
    public static final int MSG_BACKLIGHT_COOL_DOWN = 10;
    public static final int MSG_BATTERY_BECOME_HEAT = 0;
    public static final int MSG_BATTERY_COOL_DOWN = 1;
    public static final int MSG_CHARGE_STATE_POLLING = 12;
    public static final int MSG_FORGROUND_INFO_CHANGE = 13;
    public static final int MSG_GOOGLE_NETWORK_CONNECTED = 4;
    public static final int MSG_GOOGLE_NETWORK_DISCONNECTED = 5;
    public static final int MSG_INTERNET_DISCONNECTED = 2;
    public static final int MSG_INTERNET_RECONNECTED = 3;
    public static final int MSG_OPTIMIZE_GAME_LIST_UPDATE = 15;
    public static final int MSG_SHOW_KILL_PKG_TOAST = 14;
    public static final int MSG_TEMP_STATE_POLLING = 11;
    public static final int MSG_THERMALGROUP_GAME_LIST_UPDATE = 16;
    public static final int MSG_THERMAL_KILL_APP_SCAN = 8;
    public static final int MSG_WIFI_DATE_SETTING_DISABLE = 6;
    public static final int MSG_WIFI_DATE_SETTING_ENABLE = 7;
    private static final String TAG = "PowerKeeper.Feedback";
    private ReachabilityManager mReachabilityManager;
    private ThermalManager mThermalManager;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final CountDownLatch initLatch = new CountDownLatch(1);
    private final int BUILD_VERSION_CODES_S = 31;
    private final Object mLock = new Object();
    private final ArrayList<IFeedbackControlListener> mListeners = new ArrayList<>();
    private HashMap<String, Boolean> optimizeGameMap = new HashMap<>();
    private HashMap<String, Boolean> thermalGroupGameMap = new HashMap<>();
    private int mFeatureConfig = 1;
    private boolean need2RefreshOptimizeGame = true;
    private boolean need2RefreshThermalGroupGame = true;
    private FeedbackControlHandler mHandler;
    private final ContentObserver optimizeContentObserver = new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.feedbackcontrol.FeedbackControlService.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            FeedbackControlService.this.mHandler.sendEmptyMessage(15);
        }
    };
    private final ContentObserver thermalGroupContentObserver = new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.feedbackcontrol.FeedbackControlService.2
        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            FeedbackControlService.this.mHandler.sendEmptyMessage(16);
        }
    };

    /* JADX INFO: renamed from: com.miui.powerkeeper.feedbackcontrol.FeedbackControlService$3, reason: invalid class name */
    class AnonymousClass3 implements Runnable {
        AnonymousClass3() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0() {
            FeedbackControlService.this.init();
        }

        @Override // java.lang.Runnable
        public void run() {
            if (e.l(new e.a() { // from class: com.miui.powerkeeper.feedbackcontrol.b
                @Override // e.e.a
                public final void a() {
                    this.f402a.lambda$run$0();
                }
            })) {
                FeedbackControlService.this.init();
            }
        }
    }

    private class FeedbackControlHandler extends Handler {
        public FeedbackControlHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == -35) {
                int[] iArr = (int[]) message.obj;
                if (FeedbackControlService.this.mThermalManager == null || iArr.length != 3) {
                    return;
                }
                FeedbackControlService.this.mThermalManager.setIECBatteryThresholds(iArr[0], iArr[1], iArr[2]);
            }
            if (i2 == -28) {
                int iIntValue = ((Integer) message.obj).intValue();
                if (FeedbackControlService.this.mThermalManager != null) {
                    FeedbackControlService.this.mThermalManager.setIECState(iIntValue);
                    return;
                }
                return;
            }
            if (i2 == -9) {
                ForegroundInfo foregroundInfo = (ForegroundInfo) message.obj;
                if (FeedbackControlService.this.mThermalManager != null) {
                    FeedbackControlService.this.mThermalManager.onForegroundPkgChanged(foregroundInfo);
                    return;
                }
                return;
            }
            if (i2 == 11) {
                if (FeedbackControlService.this.mThermalManager != null) {
                    FeedbackControlService.this.mThermalManager.controlSystemRes();
                    return;
                }
                return;
            }
            if (i2 == 12) {
                if (FeedbackControlService.this.mThermalManager != null) {
                    FeedbackControlService.this.mThermalManager.checkBatteryCharge();
                    return;
                }
                return;
            }
            switch (i2) {
                case 0:
                    FeedbackControlService.this.dispatchFeedbackEvent(0);
                    break;
                case 1:
                    FeedbackControlService.this.dispatchFeedbackEvent(1);
                    break;
                case 2:
                    FeedbackControlService.this.dispatchFeedbackEvent(2);
                    break;
                case 3:
                    FeedbackControlService.this.dispatchFeedbackEvent(3);
                    break;
                case 4:
                    FeedbackControlService.this.dispatchFeedbackEvent(4);
                    break;
                case 5:
                    FeedbackControlService.this.dispatchFeedbackEvent(5);
                    break;
                case 6:
                    FeedbackControlService.this.dispatchFeedbackEvent(6);
                    break;
                case 7:
                    FeedbackControlService.this.dispatchFeedbackEvent(7);
                    break;
                case 8:
                    if (FeedbackControlService.this.mThermalManager != null) {
                        FeedbackControlService.this.mThermalManager.checkAndKillAbnormalProcesses();
                    }
                    break;
                default:
                    switch (i2) {
                        case 14:
                            if (FeedbackControlService.this.mThermalManager != null) {
                                FeedbackControlService.this.mThermalManager.showKillPkgToast();
                            }
                            break;
                        case 15:
                            FeedbackControlService.this.need2RefreshOptimizeGame = true;
                            break;
                        case 16:
                            FeedbackControlService.this.need2RefreshThermalGroupGame = true;
                            break;
                    }
                    break;
            }
        }
    }

    class MyBinderService extends IFeedbackControl.Stub {
        MyBinderService() {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public int getControlFeature() {
            int i2;
            synchronized (FeedbackControlService.this.mLock) {
                i2 = FeedbackControlService.this.mFeatureConfig;
            }
            return i2;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public int getOptimizeVersion() {
            int i2;
            if (ArrayUtils.contains(new String[]{"sagit", "cactus", "cereus", "pine", "jason", "daisy", "sakura", "sakura_india", "tissot", "wayne", "jasmine", "platina", "chiron", "sirius", "nitrogen", "ysl", "whyred", "tulip", "lavender", "violet", "lotus"}, Build.DEVICE)) {
                i2 = getThermalSupported() ? 1 : 0;
            } else {
                i2 = 2;
            }
            if (Constant.DBG_SM) {
                Log.d(FeedbackControlService.TAG, "getOptimizeVersion version=" + i2);
            }
            return i2;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public boolean getThermalSupported() {
            return ThermalStoreUtils.warmControlModeSupported(FeedbackControlService.this);
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public Bundle getThermalThresholds() {
            if (FeedbackControlService.this.mThermalManager != null) {
                return FeedbackControlService.this.mThermalManager.getThermalThresholds();
            }
            return null;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public boolean isEnableOptimizeGame() {
            boolean z2 = SimpleSettings.Misc.getBoolean(FeedbackControlService.this, SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME, SimpleSettings.Misc.getBoolean(FeedbackControlService.this, ScenarioConfig.KEY_NEED_CLOUD_OPTIMIZE, false));
            if (Constant.DBG_SM) {
                Log.e(FeedbackControlService.TAG, "isEnableOptimizeGame ret=" + z2);
            }
            return z2;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public boolean isOptimizeGame(String str) {
            if (FeedbackControlService.this.need2RefreshOptimizeGame) {
                FeedbackControlService.this.need2RefreshOptimizeGame = false;
                FeedbackControlService.this.optimizeGameMap.clear();
                String string = SimpleSettings.Misc.getString(FeedbackControlService.this, SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES, "");
                if (string != null && !"".equals(string)) {
                    for (String str2 : string.split(",")) {
                        FeedbackControlService.this.optimizeGameMap.put(str2, Boolean.TRUE);
                    }
                }
            }
            if (FeedbackControlService.this.need2RefreshThermalGroupGame) {
                FeedbackControlService.this.need2RefreshThermalGroupGame = false;
                FeedbackControlService.this.thermalGroupGameMap.clear();
                try {
                    JSONObject jSONObject = new JSONObject(SimpleSettings.Misc.getString(FeedbackControlService.this, "thermal_group", ""));
                    String strOptString = jSONObject.optString("game", "");
                    if (!"".equals(strOptString)) {
                        for (String str3 : strOptString.split(",")) {
                            FeedbackControlService.this.thermalGroupGameMap.put(str3, Boolean.TRUE);
                        }
                    }
                    String strOptString2 = jSONObject.optString("pubg", "");
                    if (!"".equals(strOptString2)) {
                        for (String str4 : strOptString2.split(",")) {
                            FeedbackControlService.this.thermalGroupGameMap.put(str4, Boolean.TRUE);
                        }
                    }
                    String strOptString3 = jSONObject.optString("game2", "");
                    if (!"".equals(strOptString3)) {
                        for (String str5 : strOptString3.split(",")) {
                            FeedbackControlService.this.thermalGroupGameMap.put(str5, Boolean.TRUE);
                        }
                    }
                    String strOptString4 = jSONObject.optString("yuanshen", "");
                    if (!"".equals(strOptString4)) {
                        for (String str6 : strOptString4.split(",")) {
                            FeedbackControlService.this.thermalGroupGameMap.put(str6, Boolean.TRUE);
                        }
                    }
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
            return FeedbackControlService.this.optimizeGameMap.containsKey(str) || FeedbackControlService.this.thermalGroupGameMap.containsKey(str);
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void registerFeedbackControlListener(IFeedbackControlListener iFeedbackControlListener) {
            synchronized (FeedbackControlService.this.mLock) {
                try {
                    Iterator it = FeedbackControlService.this.mListeners.iterator();
                    while (it.hasNext()) {
                        if (((IFeedbackControlListener) it.next()).asBinder() == iFeedbackControlListener.asBinder()) {
                        }
                    }
                    FeedbackControlService.this.mListeners.add(iFeedbackControlListener);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void restoreThermalMode() throws Throwable {
            ThermalStoreUtils.restoreThermalMode(FeedbackControlService.this);
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void setControlFeature(int i2) {
            synchronized (FeedbackControlService.this.mLock) {
                if (i2 != FeedbackControlService.this.mFeatureConfig) {
                    if (FeedbackControlService.this.mThermalManager == null || FeedbackControlService.this.mReachabilityManager == null) {
                        try {
                            if (!FeedbackControlService.initLatch.await(20L, TimeUnit.SECONDS)) {
                                Log.d(FeedbackControlService.TAG, "ModulesManager init timeout!");
                            }
                        } catch (InterruptedException e2) {
                            throw new RuntimeException(e2);
                        }
                    }
                    FeedbackControlService.this.mFeatureConfig = i2;
                    if (FeedbackControlService.this.mThermalManager != null) {
                        FeedbackControlService.this.mThermalManager.enableMonitor((i2 & 1) != 0);
                    }
                    if (FeedbackControlService.this.mReachabilityManager != null) {
                        FeedbackControlService.this.mReachabilityManager.enableMonitor(i2);
                    }
                }
            }
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void setEnableOptimizeGame(boolean z2) {
            if (Constant.DBG_SM) {
                Log.d(FeedbackControlService.TAG, "setEnableOptimizeGame " + z2);
            }
            SimpleSettings.Misc.putBoolean(FeedbackControlService.this, SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME, z2);
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void setOptimizeGameNames(String[] strArr) {
            if (Constant.DBG_SM) {
                Log.d(FeedbackControlService.TAG, "setOptimizeGameNames " + Arrays.toString(strArr));
            }
            StringBuilder sb = null;
            if (strArr != null) {
                for (int i2 = 0; i2 < strArr.length; i2++) {
                    String str = strArr[i2];
                    if (str != null && !str.isEmpty()) {
                        if (sb == null) {
                            sb = new StringBuilder(512);
                            sb.append(strArr[i2]);
                        } else {
                            sb.append(',');
                            sb.append(strArr[i2]);
                        }
                    }
                }
            }
            SimpleSettings.Misc.putString(FeedbackControlService.this, SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES, sb == null ? "" : sb.toString());
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void setThermalMode(int i2, String str) throws Throwable {
            if (PowerStateMachineProxy.isGameModeApp(str)) {
                return;
            }
            ThermalStoreUtils.storeOthernessThermalMode(FeedbackControlService.this, i2);
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void unregisterFeedbackControlListener(IFeedbackControlListener iFeedbackControlListener) {
            synchronized (FeedbackControlService.this.mLock) {
                try {
                    Iterator it = FeedbackControlService.this.mListeners.iterator();
                    while (it.hasNext()) {
                        if (((IFeedbackControlListener) it.next()).asBinder() == iFeedbackControlListener.asBinder()) {
                            FeedbackControlService.this.mListeners.remove(iFeedbackControlListener);
                            break;
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchFeedbackEvent(int i2) {
        synchronized (this.mLock) {
            try {
                int size = this.mListeners.size();
                for (int i3 = 0; i3 < size; i3++) {
                    IFeedbackControlListener iFeedbackControlListener = this.mListeners.get(i3);
                    switch (i2) {
                        case 0:
                            iFeedbackControlListener.batteryBecomeHeat();
                            continue;
                            break;
                        case 1:
                            iFeedbackControlListener.batteryCoolDown();
                            continue;
                            break;
                        case 2:
                            iFeedbackControlListener.networkDisconnectFromInternet();
                            continue;
                            break;
                        case 3:
                            iFeedbackControlListener.networkReconnectToInternet();
                            continue;
                            break;
                        case 4:
                            iFeedbackControlListener.googleNetworkConnected();
                            continue;
                            break;
                        case 5:
                            iFeedbackControlListener.googleNetworkDisconnect();
                            continue;
                            break;
                        case 6:
                            iFeedbackControlListener.dataWifiSettingToDisable();
                            continue;
                            break;
                        case 7:
                            try {
                                iFeedbackControlListener.dataWifiSettingToEnable();
                                continue;
                            } catch (RemoteException e2) {
                                Log.e(TAG, "dispatchFeedbackEvent to remote exception", e2);
                            }
                            break;
                        default:
                            continue;
                            break;
                    }
                    throw th;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void init() {
        n nVar = (n) e.i(1073741827);
        if (nVar != null) {
            this.mThermalManager = nVar.c(this.mHandler, (this.mFeatureConfig & 1) != 0);
            initContentObserver();
            this.mReachabilityManager = new ReachabilityManager(this, this.mHandler, 0);
        }
        o oVar = (o) e.i(1073741828);
        if (oVar != null) {
            oVar.c();
        }
        initLatch.countDown();
    }

    private void initContentObserver() {
        this.mHandler.sendEmptyMessage(15);
        this.mHandler.sendEmptyMessage(16);
        getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES), false, this.optimizeContentObserver);
        getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor("thermal_group"), false, this.thermalGroupContentObserver);
    }

    @Override // android.app.Service
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (UserHandle.myUserId() != 0) {
            return;
        }
        printWriter.println("ThermalManager:");
        ThermalManager thermalManager = this.mThermalManager;
        if (thermalManager != null) {
            thermalManager.dump(fileDescriptor, printWriter, strArr);
        }
        ReachabilityManager reachabilityManager = this.mReachabilityManager;
        if (reachabilityManager != null) {
            reachabilityManager.dump(fileDescriptor, printWriter, strArr);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (UserHandle.myUserId() != 0) {
            return null;
        }
        return new MyBinderService().asBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return;
        }
        HandlerThread handlerThread = new HandlerThread("FCS");
        handlerThread.start();
        FeedbackControlHandler feedbackControlHandler = new FeedbackControlHandler(handlerThread.getLooper());
        this.mHandler = feedbackControlHandler;
        feedbackControlHandler.post(new AnonymousClass3());
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (UserHandle.myUserId() != 0) {
            super.onDestroy();
            return;
        }
        ThermalManager thermalManager = this.mThermalManager;
        if (thermalManager != null) {
            thermalManager.enableMonitor(false);
        }
        ReachabilityManager reachabilityManager = this.mReachabilityManager;
        if (reachabilityManager != null) {
            reachabilityManager.enableMonitor(0);
        }
        getContentResolver().unregisterContentObserver(this.optimizeContentObserver);
        getContentResolver().unregisterContentObserver(this.thermalGroupContentObserver);
    }
}
