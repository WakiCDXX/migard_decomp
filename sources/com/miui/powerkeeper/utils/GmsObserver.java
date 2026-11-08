package com.miui.powerkeeper.utils;

import android.app.backup.IBackupManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Log;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.FeedBackManager;
import com.miui.powerkeeper.IWakelockManager;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.provider.UserConfigure;
import com.miui.powerkeeper.provider.UserConfigureHelper;
import com.miui.whetstone.AlarmPolicy;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.os.Build;
import miui.provider.ExtraContacts;

/* JADX INFO: loaded from: classes.dex */
public class GmsObserver {
    private static final String GMS_BACKUP_PKG = "com.google.android.backuptransport";
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private static final int MSG_UPDATE_CLOUD = 0;
    private static final int MSG_UPDATE_GMS_STATE = 1;
    private static final String TAG = "PowerKeeper.GMS";
    private static final boolean debug = true;
    private IBackupManager mBackupManager;
    private boolean mBackupSet;
    private boolean mBackupStatusChanged;
    private CloudFunctionConfig mCloudFunctionConfig;
    private Context mContext;
    private boolean mEnableGms;
    private PowerKeeperInterface.e mFeedBackCallback;
    private IFeedbackControl mFeedBackControl;
    private boolean mGmsAppEnabled;
    private boolean mGmsBlocked;
    private boolean mGmsControlEnabled;
    private GmsControlObserver mGmsControlObserver;
    private boolean mGmsInstalled;
    private int mGmsUid;
    private UserConfigureHelper mGmsUserConfigure;
    private boolean mLastGoogleReachable;
    private boolean mLastGoogleReachableUsed;
    private final Set<String> mGmsApps = new HashSet();
    private final String[] mGmsCoreApps = {"com.google.android.gms", "com.google.android.gsf", "com.google.android.gsf.login", "com.android.vending"};
    private boolean mGmsControlInited = false;
    private boolean defaultState = !Build.IS_INTERNATIONAL_BUILD;
    private final List<StatusCallback> mCallbacks = new ArrayList();
    private LocalLog mReachabilityHistoryLog = new LocalLog(50);
    private LocalLog mWakelockBlockHistoryLog = new LocalLog(256);
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.utils.GmsObserver.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int intExtra;
            if (GmsObserver.this.mEnableGms) {
                String action = intent.getAction();
                Log.d(GmsObserver.TAG, "receive: " + action);
                String schemeSpecificPart = null;
                if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                    try {
                        schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                    } catch (Exception unused) {
                    }
                    if (GmsObserver.this.isGmsApp(schemeSpecificPart)) {
                        Log.d(GmsObserver.TAG, schemeSpecificPart + " installed");
                        if (!GmsObserver.this.mGmsInstalled) {
                            GmsObserver.this.updateGmsInstalled(true);
                            return;
                        } else {
                            if (GmsObserver.this.mGmsAppEnabled) {
                                return;
                            }
                            GmsObserver.this.updateGmsEnabled(true);
                            return;
                        }
                    }
                    return;
                }
                if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                    try {
                        schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                        intExtra = intent.getIntExtra("android.intent.extra.UID", -1);
                    } catch (Exception unused2) {
                        intExtra = -1;
                    }
                    if (GmsObserver.this.isGmsApp(schemeSpecificPart)) {
                        Log.d(GmsObserver.TAG, schemeSpecificPart + " uninstalled");
                        if (intExtra != -1) {
                            GmsObserver.this.updateWakelockBlockedUid(intExtra, false);
                        }
                        if (!GmsObserver.this.isGmsAppInstalled()) {
                            GmsObserver.this.updateGmsInstalled(false);
                            return;
                        } else {
                            if (GmsObserver.this.isGmsCoreAppEnabled()) {
                                return;
                            }
                            GmsObserver.this.updateGmsEnabled(false);
                            return;
                        }
                    }
                    return;
                }
                if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                    Uri data = intent.getData();
                    schemeSpecificPart = data != null ? data.getSchemeSpecificPart() : null;
                    Log.d(GmsObserver.TAG, "pkgName: " + schemeSpecificPart);
                    String[] strArr = GmsObserver.this.mGmsCoreApps;
                    int length = strArr.length;
                    for (int i2 = 0; i2 < length; i2++) {
                        if (strArr[i2].equals(schemeSpecificPart)) {
                            if (GmsObserver.this.isGmsCoreAppEnabled()) {
                                GmsObserver.this.updateGmsEnabled(true);
                                return;
                            } else {
                                GmsObserver.this.updateGmsEnabled(false);
                                return;
                            }
                        }
                    }
                }
            }
        }
    };
    private Handler mHandler = new Handler(PowerKeeperManager.getThread().getLooper()) { // from class: com.miui.powerkeeper.utils.GmsObserver.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == 0) {
                GmsObserver.this.onCloudUpdated();
            } else {
                if (i2 != 1) {
                    return;
                }
                GmsObserver.this.updateGmsState(((Boolean) message.obj).booleanValue());
            }
        }
    };
    private CloudFunctionConfig.ICloudFunctionListener mFunctionListener = new CloudFunctionConfig.ICloudFunctionListener() { // from class: com.miui.powerkeeper.utils.GmsObserver.3
        @Override // com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig.ICloudFunctionListener
        public void onCloudFunctionUpdated() {
            GmsObserver.this.mHandler.sendMessage(GmsObserver.this.mHandler.obtainMessage(0));
        }
    };
    private PowerKeeperInterface.p mFeedBackGoogleNetworkListener = new PowerKeeperInterface.p() { // from class: com.miui.powerkeeper.utils.GmsObserver.5
        @Override // com.miui.powerkeeper.PowerKeeperInterface.p
        public void googleNetworkConnected() {
            GmsObserver.this.onGoogleReachabilityChanged(true);
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.p
        public void googleNetworkDisconnect() {
            GmsObserver.this.onGoogleReachabilityChanged(false);
        }
    };

    private class GmsControlObserver extends ContentObserver {
        public GmsControlObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            if (uri.toString().contains(UserConfigure.CONTENT_URI.toString())) {
                boolean zIsGmsControlEnabled = GmsObserver.this.isGmsControlEnabled();
                if (GmsObserver.this.mGmsUserConfigure.getId() == -1 || zIsGmsControlEnabled == GmsObserver.this.mGmsControlEnabled) {
                    return;
                }
                GmsObserver.this.mGmsControlEnabled = zIsGmsControlEnabled;
                if (!zIsGmsControlEnabled && GmsObserver.this.mGmsBlocked) {
                    GmsObserver.this.updateGmsState(false);
                } else {
                    if (!GmsObserver.this.mLastGoogleReachableUsed || GmsObserver.this.mLastGoogleReachable) {
                        return;
                    }
                    GmsObserver.this.updateGmsState(true);
                }
            }
        }
    }

    public interface StatusCallback {
        void onGmsStatusChanged(int i2, boolean z2);
    }

    public GmsObserver(Context context) {
        Log.d(TAG, "gms observer created");
        this.mContext = context;
        this.mGmsUid = PackageUtil.getUidByPackageName(context, "com.google.android.gms");
        this.mGmsBlocked = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkReady() {
        if (!this.mEnableGms || this.mFeedBackControl == null) {
            return;
        }
        registerPackageReceiver();
        this.mGmsInstalled = isGmsAppInstalled();
        this.mGmsAppEnabled = isGmsCoreAppEnabled();
        initGmsControl();
        if (this.mGmsInstalled && this.mGmsAppEnabled) {
            startGetFeedback();
        }
    }

    private void disableGms() {
        updateGoogleReletivesWakelock(false);
        updateGmsState(false);
        updateGoogleBackup(true);
        stopGetFeedback();
        unRegisterPackageReceiver();
        if (this.mGmsControlObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mGmsControlObserver);
        }
        this.mGmsControlInited = false;
    }

    private void enableGms() {
        checkReady();
    }

    private String enabledSettingToString(int i2) {
        if (i2 != 1) {
            return i2 != 2 ? "unknown" : "disabled";
        }
        return "enabled";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void init() {
        this.mBackupStatusChanged = false;
        this.mBackupSet = false;
        this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService(ExtraContacts.Calls.BACKUP_PARAM));
        this.mLastGoogleReachable = false;
        this.mLastGoogleReachableUsed = false;
        boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_GMS_CONTROL, this.defaultState);
        this.mEnableGms = z2;
        updateGmsApps(z2);
        Log.d(TAG, "init: gms control enabled: " + this.mEnableGms);
        this.mFeedBackCallback = new PowerKeeperInterface.e(null, null, null, this.mFeedBackGoogleNetworkListener);
        FeedBackManager.getInstance(this.mContext).registerFeedBackListener(this.mFeedBackCallback);
    }

    private void initGmsControl() {
        if (this.mEnableGms && !this.mGmsControlInited && this.mGmsInstalled) {
            this.mGmsUid = PackageUtil.getUidByPackageName(this.mContext, "com.google.android.gms");
            this.mGmsControlObserver = new GmsControlObserver(this.mHandler);
            this.mGmsControlEnabled = isGmsControlEnabled();
            this.mContext.getContentResolver().registerContentObserver(UserConfigure.CONTENT_URI, true, this.mGmsControlObserver);
            int i2 = this.mGmsUid;
            if (i2 != -1) {
                NetdExecutor.initGmsChain(NetdExecutor.CHAIN_GMS_WALL, i2, "REJECT");
                this.mGmsControlInited = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isGmsApp(String str) {
        if (str == null) {
            return false;
        }
        synchronized (this.mGmsApps) {
            try {
                return this.mGmsApps.contains(str);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isGmsAppInstalled() {
        for (String str : this.mGmsCoreApps) {
            if (PackageUtil.checkPackageInstalled(this.mContext, str)) {
                return true;
            }
        }
        synchronized (this.mGmsApps) {
            try {
                Iterator<String> it = this.mGmsApps.iterator();
                while (it.hasNext()) {
                    if (PackageUtil.checkPackageInstalled(this.mContext, it.next())) {
                        return true;
                    }
                }
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isGmsControlEnabled() {
        UserConfigureHelper userConfigureHelperByPkg = UserConfigureHelper.getUserConfigureHelperByPkg(this.mContext, "com.android.vending");
        this.mGmsUserConfigure = userConfigureHelperByPkg;
        return !userConfigureHelperByPkg.getBgControl().equals(UserConfigure.BG_CONTROL_NO_RESTRICT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isGmsCoreAppEnabled() {
        for (String str : this.mGmsCoreApps) {
            if (PackageUtil.checkPackageEnabled(this.mContext, str)) {
                return true;
            }
        }
        return false;
    }

    private void registerPackageReceiver() {
        if (this.mEnableGms) {
            Log.d(TAG, "register package receiver");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addDataScheme("package");
            this.mContext.registerReceiver(this.mReceiver, intentFilter, 4);
        }
    }

    private void restoreGoogleBackup() {
        IBackupManager iBackupManager;
        if (this.mBackupStatusChanged || (iBackupManager = this.mBackupManager) == null) {
            return;
        }
        try {
            boolean zIsBackupEnabled = iBackupManager.isBackupEnabled();
            this.mBackupManager.setBackupEnabled(!zIsBackupEnabled);
            StringBuilder sb = new StringBuilder();
            sb.append("set google backup enabled = ");
            sb.append(!zIsBackupEnabled);
            Log.d(TAG, sb.toString());
            this.mBackupStatusChanged = false;
            this.mBackupSet = false;
        } catch (RemoteException unused) {
            Log.d(TAG, "restore backup option failed");
        }
    }

    private void setGmsBackupEnableState(int i2) {
        IPackageManager iPackageManagerAsInterface = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        if (iPackageManagerAsInterface == null) {
            Log.d("TAG", "get PackageManger failed");
            return;
        }
        ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(GMS_BACKUP_PKG);
        try {
            if (componentNameUnflattenFromString == null) {
                iPackageManagerAsInterface.setApplicationEnabledSetting(GMS_BACKUP_PKG, i2, 0, 0, "GmsObserver:" + Process.myUid());
                Log.d(TAG, "Package " + GMS_BACKUP_PKG + " new state: " + enabledSettingToString(iPackageManagerAsInterface.getApplicationEnabledSetting(GMS_BACKUP_PKG, 0)));
            } else {
                iPackageManagerAsInterface.setComponentEnabledSetting(componentNameUnflattenFromString, i2, 0, 0, this.mContext.getOpPackageName());
                Log.d(TAG, "Component " + componentNameUnflattenFromString.toShortString() + " new state: " + enabledSettingToString(iPackageManagerAsInterface.getComponentEnabledSetting(componentNameUnflattenFromString, 0)));
            }
        } catch (RemoteException | IllegalArgumentException unused) {
        }
    }

    private void setGoogleBackup(boolean z2) {
        IBackupManager iBackupManager;
        if (this.mBackupSet || (iBackupManager = this.mBackupManager) == null) {
            return;
        }
        try {
            if (iBackupManager.isBackupEnabled() != z2) {
                this.mBackupManager.setBackupEnabled(z2);
                this.mBackupStatusChanged = true;
                this.mBackupSet = true;
                Log.d(TAG, "set google backup enabled = " + z2);
            }
        } catch (RemoteException unused) {
            Log.d(TAG, "set backup option failed");
        }
    }

    private void startGetFeedback() {
        if (this.mFeedBackControl != null) {
            Log.d(TAG, "request feedback for gms");
            try {
                IFeedbackControl iFeedbackControl = this.mFeedBackControl;
                iFeedbackControl.setControlFeature(iFeedbackControl.getControlFeature() | 512);
            } catch (RemoteException e2) {
                Log.e(TAG, "error: " + e2);
            }
        }
    }

    private void stopGetFeedback() {
        if (this.mFeedBackControl != null) {
            Log.d(TAG, "stop feedback for gms");
            try {
                IFeedbackControl iFeedbackControl = this.mFeedBackControl;
                iFeedbackControl.setControlFeature(iFeedbackControl.getControlFeature() & (-513));
            } catch (RemoteException e2) {
                Log.e(TAG, "error: " + e2);
            }
        }
    }

    private void unRegisterPackageReceiver() {
        Log.d(TAG, "un-register package receiver");
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    private void updateGmsAlarm(boolean z2) {
        Map<String, Integer> packageUidsMap = PowerKeeperManager.getInstance().getPackageUidsMap(this.mContext.getUserId());
        ArrayList arrayList = new ArrayList();
        String[] strArr = z2 ? new String[0] : null;
        synchronized (this.mGmsApps) {
            try {
                Iterator<String> it = this.mGmsApps.iterator();
                while (it.hasNext()) {
                    Integer num = packageUidsMap.get(it.next());
                    if (num != null) {
                        arrayList.add(new AlarmPolicy(num.intValue(), strArr));
                        arrayList.add(new AlarmPolicy(UserHandle.getUid(10, num.intValue()), strArr));
                        arrayList.add(new AlarmPolicy(UserHandle.getUid(DeviceInfo.DUAL_SPACE_ID, num.intValue()), strArr));
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        PowerKeeperManager.getInstance().getAlarmController().setAppsAlarmState(arrayList);
    }

    private void updateGmsApps(boolean z2) {
        if (z2) {
            String functionParameters = CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_GMS_CONTROL, "");
            Log.d(TAG, "params updated");
            try {
                synchronized (this.mGmsApps) {
                    try {
                        this.mGmsApps.clear();
                        if (TextUtils.isEmpty(functionParameters)) {
                            this.mGmsApps.addAll(Arrays.asList(this.mGmsCoreApps));
                        } else {
                            this.mGmsApps.addAll(Arrays.asList(functionParameters.split(",")));
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, "error to parse para:" + functionParameters + ": " + e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGmsEnabled(boolean z2) {
        Log.d(TAG, "update GMS enabled:" + z2);
        if (this.mGmsAppEnabled == z2) {
            return;
        }
        this.mGmsAppEnabled = z2;
        if (!z2) {
            stopGetFeedback();
        } else if (this.mEnableGms) {
            startGetFeedback();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGmsInstalled(boolean z2) {
        Log.d(TAG, "GMS installed:" + z2);
        this.mGmsInstalled = z2;
        if (z2) {
            checkReady();
        } else {
            stopGetFeedback();
        }
    }

    private void updateGmsNetWork(boolean z2) {
        NetdExecutor.setGmsChainState(NetdExecutor.CHAIN_GMS_WALL, z2);
        NetdExecutor.setGmsDnsBlockerState(this.mGmsUid, z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGmsState(boolean z2) {
        if (this.mGmsControlInited) {
            if (this.mGmsControlEnabled || !z2) {
                this.mGmsBlocked = z2;
                updateGmsNetWork(z2);
                updateGmsAlarm(z2);
                synchronized (this.mCallbacks) {
                    try {
                        Iterator<StatusCallback> it = this.mCallbacks.iterator();
                        while (it.hasNext()) {
                            it.next().onGmsStatusChanged(this.mGmsUid, z2);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }
    }

    private void updateGoogleBackup(boolean z2) {
        if (z2) {
            setGmsBackupEnableState(1);
            restoreGoogleBackup();
        } else {
            setGmsBackupEnableState(2);
            setGoogleBackup(false);
        }
    }

    private void updateGoogleReletivesWakelock(boolean z2) {
        Log.d(TAG, "updateGoogleReletivesWakelock: isBlocked = " + z2);
        synchronized (this.mGmsApps) {
            try {
                for (String str : this.mGmsApps) {
                    int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, str);
                    if (uidByPackageName != -1) {
                        Log.d(TAG, "uid = " + uidByPackageName + " for " + str);
                        updateWakelockBlockedUid(uidByPackageName, z2);
                        int uid = UserHandle.getUid(10, uidByPackageName);
                        Log.d(TAG, "secondUid = " + uid + " for " + str);
                        updateWakelockBlockedUid(uid, z2);
                        int uid2 = UserHandle.getUid(DeviceInfo.DUAL_SPACE_ID, uidByPackageName);
                        Log.d(TAG, "spaceXUid = " + uid2 + " for " + str);
                        updateWakelockBlockedUid(uid2, z2);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void updateGoogleSync(boolean z2) {
        boolean z3 = Build.IS_INTERNATIONAL_BUILD;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWakelockBlockedUid(int i2, boolean z2) {
        try {
            IWakelockManager wakelockManagerInterface = PowerKeeperManager.getInstance().getWakelockManagerInterface();
            if (wakelockManagerInterface == null) {
                Log.d(TAG, "wakelockManagerInterface is null");
                return;
            }
            wakelockManagerInterface.setWakelockBlock(z2, new ComponentName(this.mContext, (Class<?>) GmsObserver.class), i2, null);
            Log.d(TAG, "updateWakelockBlockedUid, uid = " + i2 + ", isBlocked = " + z2);
            this.mWakelockBlockHistoryLog.log("PowerKeeper.GMS: uid = " + i2 + ", isBlocked = " + z2);
        } catch (RemoteException unused) {
            Log.e(TAG, "mWakelockManager work abnormal");
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("#######dump GMS Observer#######");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Gms observer is enabled: " + this.mEnableGms);
        indentingPrintWriter.println("Gms application control enabled: " + this.mGmsControlEnabled);
        indentingPrintWriter.println("Google Reachability History:");
        indentingPrintWriter.increaseIndent();
        this.mReachabilityHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Gms wakelock block History:");
        indentingPrintWriter.increaseIndent();
        this.mWakelockBlockHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Gms Apps List from Cloud:");
        indentingPrintWriter.increaseIndent();
        synchronized (this.mGmsApps) {
            try {
                Iterator<String> it = this.mGmsApps.iterator();
                while (it.hasNext()) {
                    indentingPrintWriter.println(it.next().replace(ShellUtils.COMMAND_LINE_END, ""));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("end#######dump GMS Observer#######end");
    }

    public void onCloudUpdated() {
        boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_GMS_CONTROL, this.defaultState);
        Log.d(TAG, "gms control enabled: " + z2);
        updateGmsApps(z2);
        if (this.mEnableGms == z2) {
            Log.d(TAG, "gms control not change");
            return;
        }
        this.mEnableGms = z2;
        if (z2) {
            enableGms();
        } else {
            disableGms();
        }
    }

    public void onCreate(IFeedbackControl iFeedbackControl) {
        this.mFeedBackControl = iFeedbackControl;
        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.GmsObserver.4
            @Override // java.lang.Runnable
            public void run() {
                GmsObserver.this.init();
                GmsObserver.this.checkReady();
                GmsObserver.this.mCloudFunctionConfig = CloudFunctionConfig.getInstance();
                GmsObserver.this.mCloudFunctionConfig.registerCloudFunctionConfig(GmsObserver.this.mFunctionListener);
            }
        });
    }

    public void onGoogleReachabilityChanged(boolean z2) {
        Log.d(TAG, "onGoogleReachabilityChanged :" + z2);
        if (this.mLastGoogleReachableUsed && this.mLastGoogleReachable == z2) {
            return;
        }
        this.mLastGoogleReachableUsed = true;
        this.mLastGoogleReachable = z2;
        boolean z3 = !z2;
        this.mReachabilityHistoryLog.log("PowerKeeper.GMS: reach --> " + z2);
        updateGoogleReletivesWakelock(z3);
        Message.obtain(this.mHandler, 1, Boolean.valueOf(z3)).sendToTarget();
        updateGoogleSync(z2);
        updateGoogleBackup(z2);
    }

    public void registerGmsStatusCallback(StatusCallback statusCallback) {
        if (statusCallback != null) {
            synchronized (this.mCallbacks) {
                try {
                    if (this.mCallbacks.contains(statusCallback)) {
                        Log.d(TAG, "gms StatusCallback already registered");
                        return;
                    }
                    this.mCallbacks.add(statusCallback);
                    int i2 = this.mGmsUid;
                    if (i2 == -1) {
                        return;
                    }
                    if (this.mGmsBlocked) {
                        statusCallback.onGmsStatusChanged(i2, true);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public void unregisterGmsStatusCallback(StatusCallback statusCallback) {
        if (statusCallback != null) {
            synchronized (this.mCallbacks) {
                this.mCallbacks.remove(statusCallback);
                statusCallback.onGmsStatusChanged(this.mGmsUid, false);
            }
        }
    }
}
