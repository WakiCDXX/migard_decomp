package com.miui.powerkeeper.controller;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.DeviceIdlePolicyHelper;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.provider.GlobalFeatureConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.provider.PowerKeeperConfigure;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.NetdExecutor;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class AppStandbyController extends Controller {
    public static final String ACTION_TX_POWER_CHANGERED = "com.android.phone.intent.action.CHANGE_TX_PWR";
    private static final int MSG_POLICY_CHANGE = 16;
    private static final int MSG_SCREEN_OFF_TIMEOUT = 17;
    private static final String TAG = "PowerKeeper.AppStandby";
    private static final int TYPE_GLOBAL = 1;
    private static INetworkManagementService mTXNMService;
    private static final UriMatcher sMatcher;
    private boolean TX_POWER_CHANGED;
    private PowerKeeperInterface.a mAlarmHappenCallback;
    private PowerKeeperInterface.i mAlarmHappenListener;
    private PowerKeeperInterface.l mAppIdleAppRuleChecker;
    private PowerKeeperInterface.c mAppRuleChangedCallback;
    private PowerKeeperInterface.k mAppRuleChangedListener;
    private boolean mCloudRequiresMiuiStandbyEnable;
    private int mCloudStandbyChainDelay;
    private boolean mCloudTxPower;
    private ContentObserver mContentObserver;
    private LocalLog mHistoryLog;
    private BroadcastReceiver mKeyReceiver;
    private Boolean mMiuiStandby;
    private PowerKeeperInterface.f0 mScreenChangedCallback;
    private long mScreenOffRealTime;
    private PowerKeeperInterface.z mScreenReceiver;
    private int mScreenTimeoutMs;
    private Method mSetParam;
    private TxPowerChangedObserver mTxPowerChangedObserver;
    private BroadcastReceiver mTxPowerReceiver;
    private SparseBooleanArray mUidState;
    private IWhetstoneActivityManager mWSProxy;

    private class TxPowerChangedObserver extends ContentObserver {
        public TxPowerChangedObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            if (Constant.FUNC_ENABLE_TXPOWER_CHANGED.equals(uri.getLastPathSegment())) {
                AppStandbyController.this.mCloudTxPower = true;
            } else {
                AppStandbyController.this.mCloudTxPower = false;
            }
        }
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, GlobalFeatureConfigure.TABLE, 1);
    }

    public AppStandbyController(Context context, PowerKeeperManager powerKeeperManager) {
        super(context, powerKeeperManager, TAG);
        this.TX_POWER_CHANGED = false;
        this.mCloudTxPower = true;
        this.mAppRuleChangedCallback = null;
        this.mMiuiStandby = null;
        this.mCloudStandbyChainDelay = -100;
        this.mWSProxy = null;
        this.mUidState = new SparseBooleanArray();
        this.mHistoryLog = new LocalLog(Controller.MAX_HISTORY_ITEMS);
        this.mAppRuleChangedListener = new PowerKeeperInterface.k() { // from class: com.miui.powerkeeper.controller.AppStandbyController.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.k
            public void onAppRuleChange(int i2, int i3) {
                synchronized (AppStandbyController.this.mLock) {
                    try {
                        AppStandbyController appStandbyController = AppStandbyController.this;
                        if (appStandbyController.isEnabled) {
                            if (!appStandbyController.mIgnoreUids.contains(Integer.valueOf(i2))) {
                                AppStandbyController.this.updateRuleForUidLocked(i2, i3);
                            }
                        } else {
                            if (Controller.DEBUG) {
                                Slog.v(AppStandbyController.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mScreenReceiver = new PowerKeeperInterface.z() { // from class: com.miui.powerkeeper.controller.AppStandbyController.2
            @Override // com.miui.powerkeeper.PowerKeeperInterface.z
            public void onRegistered(boolean z2) {
                if (!AppStandbyController.this.mCloudRequiresMiuiStandbyEnable || z2) {
                    return;
                }
                AppStandbyController.this.removeScreenOffTimeoutMessage();
                AppStandbyController.this.sendScreenOffTimeoutMessage();
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.z
            public void onScreenChanged(boolean z2) {
                if (!AppStandbyController.this.mCloudRequiresMiuiStandbyEnable || z2) {
                    return;
                }
                AppStandbyController.this.mHistoryLog.log(z2 ? "screen On" : "screen Off");
                AppStandbyController.this.removeScreenOffTimeoutMessage();
                AppStandbyController.this.sendScreenOffTimeoutMessage();
            }
        };
        this.mAlarmHappenListener = new PowerKeeperInterface.i() { // from class: com.miui.powerkeeper.controller.AppStandbyController.3
            @Override // com.miui.powerkeeper.PowerKeeperInterface.i
            public void onAlarmHappen(long j2) {
                AppStandbyController.this.handleAlarm(j2);
            }
        };
        this.mKeyReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.controller.AppStandbyController.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (AppStandbyController.this.mCloudRequiresMiuiStandbyEnable && "android.intent.action.USER_PRESENT".equals(action)) {
                    AppStandbyController.this.mHistoryLog.log("user present");
                    AppStandbyController.this.removeScreenOffTimeoutMessage();
                    AppStandbyController.this.setMiuiStandby(false);
                }
            }
        };
        this.mTxPowerReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.controller.AppStandbyController.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (AppStandbyController.ACTION_TX_POWER_CHANGERED.equals(intent.getAction())) {
                    boolean booleanExtra = intent.getBooleanExtra("enable", false);
                    if (AppStandbyController.this.mCloudTxPower) {
                        if (booleanExtra) {
                            AppStandbyController.this.TX_POWER_CHANGED = true;
                            Slog.d(AppStandbyController.TAG, "radio poor, network will be closed,TX_POWER_CHANGED =" + AppStandbyController.this.TX_POWER_CHANGED);
                            AppStandbyController.DisableTxPowerNetWork(true);
                            return;
                        }
                        AppStandbyController.this.TX_POWER_CHANGED = false;
                        Slog.d(AppStandbyController.TAG, "radio great, network will be open, TX_POWER_CHANGED =" + AppStandbyController.this.TX_POWER_CHANGED);
                        AppStandbyController.DisableTxPowerNetWork(false);
                    }
                }
            }
        };
    }

    public static void DisableTxPowerNetWork(boolean z2) {
        try {
            if (mTXNMService == null) {
                mTXNMService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
            }
            if (z2) {
                mTXNMService.setFirewallEnabled(true);
                Slog.d(TAG, "txpower---> setFirewallenable false, close network");
            } else {
                mTXNMService.setFirewallEnabled(false);
                Slog.d(TAG, "txpower---> setFirewallenable true, open network");
            }
        } catch (Exception e2) {
            Slog.e(TAG, "an exception occurred!!", e2);
        }
    }

    private boolean checkSetParamMethod() {
        if (this.mSetParam == null) {
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            this.mWSProxy = iWhetstoneActivityManagerAsInterface;
            if (iWhetstoneActivityManagerAsInterface != null) {
                try {
                    this.mSetParam = iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy().getClass().getMethod("setParam", Integer.TYPE, String.class, Bundle.class);
                } catch (Exception e2) {
                    Slog.d(TAG, "setParam method failed", e2);
                }
            }
        }
        return this.mSetParam != null;
    }

    private void configurePolicy() {
        int i2 = this.mCloudStandbyChainDelay;
        if (i2 < 0) {
            this.mCloudRequiresMiuiStandbyEnable = false;
            this.mScreenTimeoutMs = 0;
        } else {
            this.mCloudRequiresMiuiStandbyEnable = true;
            this.mScreenTimeoutMs = i2 * 1000;
        }
        Slog.d(TAG, "configurePolicy: miuiStandbyEnable: " + this.mCloudRequiresMiuiStandbyEnable + " screenTimeout: " + this.mScreenTimeoutMs);
        this.mHistoryLog.log("configurePolicy: miuiStandbyEnable: " + this.mCloudRequiresMiuiStandbyEnable + " screenTimeout: " + this.mScreenTimeoutMs);
    }

    private void disableNetdControl() {
        unRegisterScreenReceiver();
        unRegisterAlarmReceiver();
        unRegisterKeyReceiver();
        unRegisterCloudUpdateReceiver();
        unRegisterTxPowerReceiver();
        unregisterTxPowerChangedObserver();
        setMiuiStandby(true);
    }

    private void enableNetdControl() {
        this.mCloudStandbyChainDelay = GlobalFeatureConfigureHelper.getCloudStandbyChainDelay(this.mContext);
        configurePolicy();
        resetMiuiStandby();
        registerTxPowerReceiver();
        registerScreenReceiver();
        registerAlarmReceiver();
        registerKeyReceiver();
        registerCloudUpdateReceiver();
        registerTxPowerChangedObserver();
    }

    private int getUidRule(int i2) {
        return this.mAppIdleAppRuleChecker.getAppRule(i2);
    }

    private boolean getUidState(int i2) {
        return !DeviceIdlePolicyHelper.i(this.mContext).n(i2).booleanValue();
    }

    private SparseIntArray getUidsRule(int[] iArr) {
        return this.mAppIdleAppRuleChecker.getAppsRule(iArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAlarm(long j2) {
        if (this.mCloudRequiresMiuiStandbyEnable) {
            long j3 = this.mScreenOffRealTime;
            if (j3 > 0 && j3 <= j2) {
                if (Controller.DEBUG) {
                    Slog.d(TAG, "handleAlarm: elapsedRealtime = " + j2);
                }
                removeScreenOffTimeoutMessage();
                handleScreenOffTimeout();
            }
        }
    }

    private void handleScreenOffTimeout() {
        this.mScreenOffRealTime = 0L;
        if (this.mCloudRequiresMiuiStandbyEnable) {
            setMiuiStandby(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onContentChangeLocked(boolean z2, Uri uri) {
        if (Controller.DEBUG) {
            Slog.d(TAG, "onContentChangeLocked, " + uri.toSafeString());
        }
        if (sMatcher.match(uri) == 1 && startCloudUpdate(this.mContext)) {
            configurePolicy();
            this.mHandler.sendEmptyMessage(16);
        }
    }

    private void registerAlarmReceiver() {
        PowerKeeperInterface.a aVar = new PowerKeeperInterface.a(this.mHandler, this.mAlarmHappenListener);
        this.mAlarmHappenCallback = aVar;
        this.mPowerKeeperManager.registerAlarmListener(aVar);
    }

    private void registerCloudUpdateReceiver() {
        this.mContentObserver = new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.controller.AppStandbyController.6
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                if (uri == null) {
                    return;
                }
                AppStandbyController.this.onContentChangeLocked(z2, uri);
            }
        };
        this.mContext.getContentResolverForUser(UserHandle.OWNER).registerContentObserver(GlobalFeatureConfigure.CONTENT_URI, true, this.mContentObserver);
    }

    private void registerKeyReceiver() {
        this.mContext.registerReceiver(this.mKeyReceiver, new IntentFilter("android.intent.action.USER_PRESENT"), null, this.mHandler, 4);
    }

    private void registerScreenReceiver() {
        PowerKeeperInterface.f0 f0Var = new PowerKeeperInterface.f0(this.mHandler, this.mScreenReceiver);
        this.mScreenChangedCallback = f0Var;
        this.mPowerKeeperManager.registerScreenChangedListener(f0Var);
    }

    private void registerTxPowerChangedObserver() {
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.SYSTEM);
        this.mTxPowerChangedObserver = new TxPowerChangedObserver(this.mHandler);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(Constant.FUNC_ENABLE_TXPOWER_CHANGED), false, this.mTxPowerChangedObserver);
    }

    private void registerTxPowerReceiver() {
        this.mContext.registerReceiver(this.mTxPowerReceiver, new IntentFilter(ACTION_TX_POWER_CHANGERED), null, this.mHandler, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeScreenOffTimeoutMessage() {
        this.mScreenOffRealTime = 0L;
        this.mHandler.removeMessages(17);
    }

    private void resetMiuiStandby() {
        setMiuiStandby(false);
        removeScreenOffTimeoutMessage();
        if (!this.mCloudRequiresMiuiStandbyEnable) {
            setMiuiStandby(true);
        } else {
            if (this.mPowerKeeperManager.isScreenOn()) {
                return;
            }
            sendScreenOffTimeoutMessage();
        }
    }

    private void sendConnectivityActionToApp(int i2) {
        NetworkInfo activeNetworkInfo;
        if (this.mCloudRequiresMiuiStandbyEnable || (activeNetworkInfo = ((ConnectivityManager) this.mContext.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo()) == null || !activeNetworkInfo.isConnected()) {
            return;
        }
        Intent intent = new Intent("android.net.conn.CONNECTIVITY_CHANGE");
        intent.addFlags(1073741840);
        intent.putExtra("networkInfo", activeNetworkInfo);
        intent.putExtra("networkType", activeNetworkInfo.getType());
        String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i2);
        if (packagesForUid == null) {
            Slog.e(TAG, "cannot get packages for uid " + i2);
            return;
        }
        for (String str : packagesForUid) {
            intent.setPackage(str);
            this.mContext.sendBroadcastAsUser(intent, new UserHandle(UserHandle.getUserId(i2)));
            if (Controller.DEBUG) {
                Slog.d(TAG, "send broadcast android.net.conn.CONNECTIVITY_CHANGE to " + str);
            }
            this.mHistoryLog.log("send broadcast connectivity_change to " + str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendScreenOffTimeoutMessage() {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        int i2 = this.mScreenTimeoutMs;
        this.mScreenOffRealTime = jElapsedRealtime + ((long) i2);
        this.mHandler.sendEmptyMessageDelayed(17, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMiuiStandby(boolean z2) {
        if (z2) {
            Boolean bool = this.mMiuiStandby;
            Boolean bool2 = Boolean.TRUE;
            if (bool != bool2) {
                NetdExecutor.enableFirewallStandbyChain();
                this.mMiuiStandby = bool2;
                Slog.d(TAG, "enable standby chain");
                this.mHistoryLog.log("enable standby chain");
                return;
            }
        }
        if (z2) {
            return;
        }
        Boolean bool3 = this.mMiuiStandby;
        Boolean bool4 = Boolean.FALSE;
        if (bool3 != bool4) {
            NetdExecutor.disableFirewallStandbyChain();
            this.mMiuiStandby = bool4;
            Slog.d(TAG, "disable standby chain");
            this.mHistoryLog.log("disable standby chain");
        }
    }

    private void setUidState(int i2, boolean z2) {
        if (this.mUidState.indexOfKey(i2) < 0) {
            this.mUidState.put(i2, getUidState(i2));
        }
        if (z2 != this.mUidState.get(i2)) {
            this.mUidState.put(i2, z2);
            if (Controller.DEBUG) {
                Slog.d(TAG, "setUidState, uid = " + i2 + " allow = " + z2);
            }
            boolean z3 = !z2;
            DeviceIdlePolicyHelper.i(this.mContext).s(i2, z3);
            if (z3) {
                updateDisallowToastApps(4, i2);
            }
            if (z2) {
                updateDisallowToastApps(5, i2);
                sendConnectivityActionToApp(i2);
            }
            this.mHistoryLog.log("AppStandby: setUidState uid=" + i2 + " allow=" + z2);
        }
    }

    private boolean startCloudUpdate(Context context) {
        if (Controller.DEBUG) {
            Slog.i(TAG, "startCloudUpdate");
        }
        int cloudStandbyChainDelay = GlobalFeatureConfigureHelper.getCloudStandbyChainDelay(context);
        if (cloudStandbyChainDelay == this.mCloudStandbyChainDelay) {
            return false;
        }
        this.mCloudStandbyChainDelay = cloudStandbyChainDelay;
        return true;
    }

    private void unRegisterAlarmReceiver() {
        this.mPowerKeeperManager.unregisterAlarmListener(this.mAlarmHappenCallback);
        this.mAlarmHappenCallback.a();
        this.mAlarmHappenCallback = null;
    }

    private void unRegisterCloudUpdateReceiver() {
        this.mContext.getContentResolverForUser(UserHandle.OWNER).unregisterContentObserver(this.mContentObserver);
        this.mContentObserver = null;
    }

    private void unRegisterKeyReceiver() {
        this.mContext.unregisterReceiver(this.mKeyReceiver);
    }

    private void unRegisterScreenReceiver() {
        this.mPowerKeeperManager.unregisterScreenChangedListener(this.mScreenChangedCallback);
        this.mScreenChangedCallback.a();
        this.mScreenChangedCallback = null;
    }

    private void unRegisterTxPowerReceiver() {
        this.mContext.unregisterReceiver(this.mTxPowerReceiver);
    }

    private void unregisterTxPowerChangedObserver() {
        this.mContext.getContentResolverForUser(UserHandle.SYSTEM).unregisterContentObserver(this.mTxPowerChangedObserver);
        this.mCloudTxPower = false;
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
        if (UserHandle.isApp(i2)) {
            if (this.mUidPoliy.get(i2, 0) != 0) {
                return;
            }
            setUidState(i2, i3 != 1);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        synchronized (this.mLock) {
            indentingPrintWriter.println("#######dump##AppStandbyController#######");
            indentingPrintWriter.println("AppStandbyController operation history:");
            this.mHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
            indentingPrintWriter.println("end#######dump##AppStandbyController#######end");
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onDisable() {
        disableNetdControl();
        for (int i2 = 0; i2 < this.mUidState.size(); i2++) {
            setUidState(this.mUidState.keyAt(i2), true);
        }
        this.mUidState.clear();
        this.mAppIdleAppRuleChecker.unregisterAppRuleChangeListener(this.mAppRuleChangedCallback);
        this.mAppRuleChangedCallback.a();
        this.mAppRuleChangedCallback = null;
        this.mAppIdleAppRuleChecker = null;
        this.mHistoryLog.log("PowerKeeper.AppStandby: onDisable");
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onEnable() {
        this.mUidState.clear();
        this.mAppIdleAppRuleChecker = this.mPowerKeeperManager.getAppIdleAppRuleChecker();
        int[] allUids = getAllUids();
        for (int i2 : allUids) {
            this.mUidState.put(i2, getUidState(i2));
        }
        PowerKeeperInterface.c cVar = new PowerKeeperInterface.c(this.mHandler, this.mAppRuleChangedListener);
        this.mAppRuleChangedCallback = cVar;
        this.mAppIdleAppRuleChecker.registerAppRuleChangeListener(cVar);
        SparseIntArray uidsRule = getUidsRule(allUids);
        for (int i3 = 0; i3 < uidsRule.size(); i3++) {
            updateRuleForUidLocked(uidsRule.keyAt(i3), uidsRule.valueAt(i3));
        }
        DisableTxPowerNetWork(false);
        enableNetdControl();
        this.mHistoryLog.log("PowerKeeper.AppStandby: onEnable");
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onHandleMessage(Message message) {
        int i2 = message.what;
        if (i2 == 16) {
            resetMiuiStandby();
        } else {
            if (i2 != 17) {
                return;
            }
            handleScreenOffTimeout();
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

    public void updateDisallowToastApps(int i2, int i3) {
        try {
            if (checkSetParamMethod()) {
                this.mSetParam.invoke(this.mWSProxy.getPowerKeeperPolicy(), Integer.valueOf(i2), Integer.toString(i3), null);
            }
        } catch (Exception unused) {
            if (Controller.DEBUG) {
                Slog.d(TAG, "updateDisallowToastApps excecption");
            }
        }
    }

    protected void updateRuleForAllUids() {
        SparseIntArray uidsRule = getUidsRule(getAllUids());
        for (int i2 = 0; i2 < uidsRule.size(); i2++) {
            updateRuleForUidLocked(uidsRule.keyAt(i2), uidsRule.valueAt(i2));
        }
    }
}
