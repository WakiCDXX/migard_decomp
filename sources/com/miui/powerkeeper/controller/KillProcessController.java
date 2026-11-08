package com.miui.powerkeeper.controller;

import android.content.Context;
import android.miui.AppOpsUtils;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.WindowManagerGlobal;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.Utils;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;

/* JADX INFO: loaded from: classes.dex */
public class KillProcessController extends Controller {
    private static final String TAG = "PowerKeeper.KillControl";
    private final int[] mAppActiveCounts;
    private PowerKeeperInterface.c mAppRuleChangedCallback;
    private PowerKeeperInterface.k mAppRuleChangedListener;
    private int mDefaultAutoStartPolicy;
    private LocalLog mHistoryLog;
    private long mInitTime;
    private PowerKeeperInterface.l mKillProcessAppRuleChecker;
    private int mPowerMode;
    private Method mSetUidAutoStartState;
    private final SparseBooleanArray mUidAutoStartControlState;
    private final SparseIntArray mUidAutoStartPolicy;
    private final SparseArray<LinkedHashMap<Integer, Long>> mUserKillUidArray;
    private IWhetstoneActivityManager mWSProxy;

    public KillProcessController(Context context, PowerKeeperManager powerKeeperManager) {
        super(context, powerKeeperManager, TAG);
        this.mHistoryLog = new LocalLog(Controller.MAX_HISTORY_ITEMS);
        this.mAppActiveCounts = new int[]{-1, -1, 20, 5, 3, 3};
        this.mDefaultAutoStartPolicy = 56;
        this.mUidAutoStartPolicy = new SparseIntArray();
        this.mUidAutoStartControlState = new SparseBooleanArray();
        this.mUserKillUidArray = new SparseArray<>();
        this.mAppRuleChangedListener = new PowerKeeperInterface.k() { // from class: com.miui.powerkeeper.controller.KillProcessController.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.k
            public void onAppRuleChange(int i2, int i3) {
                synchronized (KillProcessController.this.mLock) {
                    try {
                        KillProcessController killProcessController = KillProcessController.this;
                        if (killProcessController.isEnabled) {
                            if (!killProcessController.mIgnoreUids.contains(Integer.valueOf(i2))) {
                                KillProcessController.this.updateRuleForUidLocked(i2, i3);
                            }
                        } else {
                            if (Controller.DEBUG) {
                                Slog.v(KillProcessController.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
    }

    private boolean addKillUidLocked(int i2) {
        if (this.mAppActiveCounts[this.mPowerMode] <= 0) {
            return false;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - this.mInitTime < 60000) {
            return false;
        }
        LinkedHashMap<Integer, Long> userKillUidMap = getUserKillUidMap(UserHandle.getUserId(i2));
        while (userKillUidMap.size() >= this.mAppActiveCounts[this.mPowerMode]) {
            Map.Entry entryEldest = userKillUidMap.eldest();
            if (entryEldest != null) {
                Integer num = (Integer) entryEldest.getKey();
                boolean zCheckIfAutoStartApp = checkIfAutoStartApp(num.intValue());
                if (zCheckIfAutoStartApp) {
                    if (((1 << this.mPowerMode) & this.mUidAutoStartPolicy.get(num.intValue(), this.mDefaultAutoStartPolicy)) != 0) {
                        disableUidAutoStartLocked(num.intValue());
                    }
                }
                userKillUidMap.remove(num);
                if (!zCheckIfAutoStartApp || this.mUidAutoStartControlState.get(num.intValue())) {
                    Log.i(TAG, "power mode limit count kill uid is " + num);
                    setUidState(num.intValue(), false);
                } else {
                    Log.d(TAG, "skip killing auto start apps uid=" + num);
                }
            }
        }
        userKillUidMap.put(Integer.valueOf(i2), Long.valueOf(jCurrentTimeMillis));
        return true;
    }

    private boolean checkAutoStartMethod() {
        if (this.mSetUidAutoStartState == null) {
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            this.mWSProxy = iWhetstoneActivityManagerAsInterface;
            if (iWhetstoneActivityManagerAsInterface != null) {
                try {
                    this.mSetUidAutoStartState = iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy().getClass().getMethod("setUidAutoStartState", Integer.TYPE, Boolean.TYPE);
                } catch (Exception e2) {
                    Log.d(TAG, "set uid auto start state failed", e2);
                }
            }
        }
        return this.mSetUidAutoStartState != null;
    }

    private boolean checkIfAutoStartApp(int i2) {
        return AppOpsUtils.getApplicationAutoStart(this.mContext.getApplicationContext(), PackageUtil.getPackageNameByUid(this.mContext, i2), i2) == 0;
    }

    private void disableUidAutoStartLocked(int i2) {
        if (this.mUidAutoStartControlState.get(i2)) {
            return;
        }
        if (updateAutoStartStateLocked(i2, false)) {
            this.mUidAutoStartControlState.put(i2, true);
        } else if (Controller.DEBUG) {
            Log.d(TAG, "update uid policy to whetstone failed");
        }
    }

    private void enableUidAutoStartLocked(int i2) {
        if (this.mUidAutoStartControlState.get(i2)) {
            if (updateAutoStartStateLocked(i2, true)) {
                this.mUidAutoStartControlState.delete(i2);
            } else if (Controller.DEBUG) {
                Log.d(TAG, "update uid policy to whetstone failed");
            }
        }
    }

    private int getUidRule(int i2) {
        return this.mKillProcessAppRuleChecker.getAppRule(i2);
    }

    private SparseIntArray getUidsRule(int[] iArr) {
        return this.mKillProcessAppRuleChecker.getAppsRule(iArr);
    }

    private LinkedHashMap<Integer, Long> getUserKillUidMap(int i2) {
        if (this.mUserKillUidArray.get(i2) == null) {
            this.mUserKillUidArray.put(i2, new LinkedHashMap<>());
        }
        return this.mUserKillUidArray.get(i2);
    }

    private boolean removeKillUidLocked(int i2) {
        enableUidAutoStartLocked(i2);
        LinkedHashMap<Integer, Long> userKillUidMap = getUserKillUidMap(UserHandle.getUserId(i2));
        if (!userKillUidMap.containsKey(Integer.valueOf(i2))) {
            return false;
        }
        userKillUidMap.remove(Integer.valueOf(i2));
        return true;
    }

    private void setUidState(int i2, boolean z2) {
        int userId = UserHandle.getUserId(i2);
        if (Controller.DEBUG) {
            Slog.d(TAG, "setUidState, uid = " + i2 + " allow = " + z2);
        }
        try {
            int i3 = this.mUidPoliy.get(i2, 0);
            if (z2) {
                this.mHistoryLog.log("allow uid=" + i2 + " running policy=" + i3);
                return;
            }
            Slog.i(TAG, "calling ProcessManager killApplicationAlways, uid = " + i2);
            String packageNameByUid = PackageUtil.getPackageNameByUid(Utils.APP_CONTEXT, i2);
            if (ProcessManager.isLockedApplication(packageNameByUid, 0)) {
                return;
            }
            try {
                if (WindowManagerGlobal.getWindowManagerService().checkAppOnWindowsStatus(i2)) {
                    Slog.i(TAG, "calling ProcessManager killApplicationAlways, visible not kill uid = " + i2);
                    return;
                }
            } catch (Exception e2) {
                Slog.e(TAG, "error : " + e2);
            }
            if (this.mPowerKeeperManager.getCurrentIME(userId) == i2) {
                return;
            }
            ProcessManager.kill(new ProcessConfig(13, packageNameByUid, i2));
            this.mHistoryLog.log("stop uid=" + i2 + " policy=" + i3);
        } catch (Exception e3) {
            Slog.e(TAG, "setUidState", e3);
        }
    }

    private boolean updateAutoStartStateLocked(int i2, boolean z2) {
        try {
            if (checkAutoStartMethod()) {
                return ((Boolean) this.mSetUidAutoStartState.invoke(this.mWSProxy.getPowerKeeperPolicy(), Integer.valueOf(i2), Boolean.valueOf(z2))).booleanValue();
            }
            return false;
        } catch (Exception unused) {
            Log.e(TAG, "IWhetstoneActivityManager work abnormal");
            return false;
        }
    }

    private void updateCloudConfigureLocked() {
        String[] strArrSplit;
        String cloudBgKillConfig = GlobalFeatureConfigureHelper.getCloudBgKillConfig(this.mContext);
        if (TextUtils.isEmpty(cloudBgKillConfig) || (strArrSplit = cloudBgKillConfig.split(";")) == null || strArrSplit.length != 2) {
            return;
        }
        String[] strArrSplit2 = strArrSplit[0].split(",");
        for (int i2 = 0; i2 < strArrSplit2.length; i2++) {
            this.mAppActiveCounts[i2] = Integer.parseInt(strArrSplit2[i2]);
        }
        if (TextUtils.isEmpty(strArrSplit[1])) {
            return;
        }
        this.mDefaultAutoStartPolicy = Integer.parseInt(strArrSplit[1]);
    }

    private void updateKillUidLocked(int i2, boolean z2) {
        if (!z2) {
            addKillUidLocked(i2);
        } else if (removeKillUidLocked(i2)) {
            setUidState(i2, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRuleForUidLocked(int i2, int i3) {
        int i4 = this.mUidPoliy.get(i2, 0);
        if (i4 == 0 || i4 == 1) {
            boolean z2 = getUidRule(i2) != 1;
            if (i4 == 1) {
                updateKillUidLocked(i2, z2);
            } else {
                removeKillUidLocked(i2);
                setUidState(i2, z2);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        synchronized (this.mLock) {
            try {
                indentingPrintWriter.println("#######dump##KillProcessController#######");
                indentingPrintWriter.println("KillProcessController operation history:");
                this.mHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.println("mPowerMode:" + this.mPowerMode + " mAppActiveCounts:" + Arrays.toString(this.mAppActiveCounts));
                indentingPrintWriter.println("Pending kill uids");
                indentingPrintWriter.increaseIndent();
                for (int i2 = 0; i2 < this.mUserKillUidArray.size(); i2++) {
                    LinkedHashMap<Integer, Long> linkedHashMapValueAt = this.mUserKillUidArray.valueAt(i2);
                    indentingPrintWriter.println("userId = " + this.mUserKillUidArray.keyAt(i2) + ":");
                    indentingPrintWriter.increaseIndent();
                    for (Map.Entry<Integer, Long> entry : linkedHashMapValueAt.entrySet()) {
                        indentingPrintWriter.println("uid = " + entry.getKey() + " inactiveTime:" + Utils.timestamp2String(entry.getValue().longValue()));
                    }
                    indentingPrintWriter.decreaseIndent();
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("Auto start disabled uids:");
                indentingPrintWriter.increaseIndent();
                for (int i3 = 0; i3 < this.mUidAutoStartControlState.size(); i3++) {
                    indentingPrintWriter.print(this.mUidAutoStartControlState.keyAt(i3) + " ");
                }
                indentingPrintWriter.println();
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("end#######dump##KillProcessController#######end");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onDisable() {
        this.mKillProcessAppRuleChecker.unregisterAppRuleChangeListener(this.mAppRuleChangedCallback);
        this.mAppRuleChangedCallback.a();
        this.mAppRuleChangedCallback = null;
        this.mKillProcessAppRuleChecker = null;
        this.mHistoryLog.log("PowerKeeper.KillControl: onDisable");
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onEnable() {
        this.mKillProcessAppRuleChecker = this.mPowerKeeperManager.getKillProcessAppRuleChecker();
        PowerKeeperInterface.c cVar = new PowerKeeperInterface.c(this.mHandler, this.mAppRuleChangedListener);
        this.mAppRuleChangedCallback = cVar;
        this.mKillProcessAppRuleChecker.registerAppRuleChangeListener(cVar);
        this.mPowerMode = this.mPowerKeeperManager.getEventsAggregator().getPowerMode();
        this.mInitTime = System.currentTimeMillis();
        SparseIntArray uidsRule = getUidsRule(getAllUids());
        for (int i2 = 0; i2 < uidsRule.size(); i2++) {
            updateRuleForUidLocked(uidsRule.keyAt(i2), uidsRule.valueAt(i2));
        }
        this.mHistoryLog.log("PowerKeeper.KillControl: onEnable");
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onSetUidPolicy(int i2, int i3) {
        if (i3 == 0 || i3 == 1) {
            updateRuleForUidLocked(i2, getUidRule(i2));
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUpdate() {
        updateCloudConfigureLocked();
        int powerMode = this.mPowerKeeperManager.getEventsAggregator().getPowerMode();
        this.mInitTime = System.currentTimeMillis();
        if (powerMode != this.mPowerMode) {
            this.mPowerMode = powerMode;
            for (int size = this.mUidAutoStartControlState.size() - 1; size >= 0; size--) {
                int iKeyAt = this.mUidAutoStartControlState.keyAt(size);
                if ((this.mUidAutoStartPolicy.get(iKeyAt, this.mDefaultAutoStartPolicy) & (1 << this.mPowerMode)) == 0) {
                    enableUidAutoStartLocked(iKeyAt);
                }
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    public void setUidPolicy(int i2, int i3) {
        int i4;
        synchronized (this.mLock) {
            try {
                if (this.isEnabled && !this.mIgnoreUids.contains(Integer.valueOf(i2)) && (i4 = (i3 & 4080) >> 4) != this.mUidAutoStartPolicy.get(i2, this.mDefaultAutoStartPolicy)) {
                    if (this.mDefaultAutoStartPolicy == i4) {
                        this.mUidAutoStartPolicy.delete(i2);
                    } else {
                        this.mUidAutoStartPolicy.put(i2, i4);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        super.setUidPolicy(i2, i3 & 15);
    }
}
