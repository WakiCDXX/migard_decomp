package com.miui.powerkeeper.controller;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class SensorController extends Controller {
    private static final boolean ALLOW_APP_USE_SENSORS = true;
    private static final boolean DEFAULT_RULE = true;
    private static final String DEFAULT_TYPE = "all";
    private static final String TAG = "PowerKeeper.Sensor";
    private PowerKeeperInterface.c mAppRuleChangedCallback;
    private PowerKeeperInterface.k mAppRuleChangedListener;
    private LocalLog mHistoryLog;
    private final Object mRulesLock;
    private boolean mSensorControlFlg;
    private IBinder mSensorService;
    private PowerKeeperInterface.l mSensorsAppRuleChecker;
    private SparseArray<String> mUidDisableType;
    private SparseBooleanArray mUidState;
    private List<String> ssruFunctionDevices;

    public SensorController(Context context, PowerKeeperManager powerKeeperManager) {
        super(context, powerKeeperManager, TAG);
        this.mRulesLock = new Object();
        this.mSensorControlFlg = false;
        this.mSensorService = null;
        this.mUidDisableType = new SparseArray<>();
        this.mUidState = new SparseBooleanArray();
        this.mHistoryLog = new LocalLog(Controller.MAX_HISTORY_ITEMS);
        this.ssruFunctionDevices = List.of("shennong", "houji", "haotian", "dada", "goku", "aurora");
        this.mAppRuleChangedListener = new PowerKeeperInterface.k() { // from class: com.miui.powerkeeper.controller.SensorController.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.k
            public void onAppRuleChange(int i2, int i3) {
                synchronized (SensorController.this.mLock) {
                    try {
                        SensorController sensorController = SensorController.this;
                        if (!sensorController.isEnabled) {
                            Slog.v(SensorController.TAG, "still disabled");
                        } else {
                            if (!sensorController.mIgnoreUids.contains(Integer.valueOf(i2))) {
                                SensorController.this.updateRuleForUidLocked(i2, i3);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        checkSensorService(true);
    }

    private boolean checkSensorService(boolean z2) {
        if (this.mSensorService != null && !z2) {
            return true;
        }
        IBinder service = ServiceManager.getService(LogType.LOG_TYPE_SENSOR_NAME);
        this.mSensorService = service;
        if (service != null) {
            return true;
        }
        Slog.e(TAG, "checkSensorService get sensorservice failed");
        return false;
    }

    private int getUidRule(int i2) {
        return this.mSensorsAppRuleChecker.getAppRule(i2);
    }

    private SparseIntArray getUidsRule(int[] iArr) {
        return this.mSensorsAppRuleChecker.getAppsRule(iArr);
    }

    private void setAppSensorsControlPolicy(int i2, boolean z2) {
        if (this.ssruFunctionDevices.contains(Build.DEVICE)) {
            if (Controller.DEBUG) {
                Slog.d(TAG, "Ban PowerKeeper SensorControl Function!!! Pass to Tare Control Sensor");
                return;
            }
            return;
        }
        synchronized (this.mRulesLock) {
            try {
                if (Controller.DEBUG) {
                    Slog.d(TAG, "setUidState, uid = " + i2 + " allow = " + z2);
                }
                if (true == z2) {
                    if (checkSensorService(false)) {
                        try {
                            this.mSensorService.dump(FileDescriptor.out, new String[]{"sensorsControl", "resume", Integer.toString(i2)});
                            this.mHistoryLog.log("sensorsControl resume uid=" + i2);
                        } catch (RemoteException e2) {
                            Slog.e(TAG, "setAppSensorsControlPolicy" + e2);
                        } catch (UnsupportedOperationException e3) {
                            Slog.e(TAG, "setAppSensorsControlPolicy" + e3);
                        }
                    }
                } else if (checkSensorService(false)) {
                    try {
                        this.mSensorService.dump(FileDescriptor.out, new String[]{"sensorsControl", "stop", Integer.toString(i2)});
                        this.mHistoryLog.log("sensorsControl stop uid=" + i2);
                    } catch (RemoteException e4) {
                        Slog.e(TAG, "setAppSensorsControlPolicy" + e4);
                    } catch (UnsupportedOperationException e5) {
                        Slog.e(TAG, "setAppSensorsControlPolicy" + e5);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void setAppSensorsControlType(int i2, String str) {
        synchronized (this.mRulesLock) {
            if (checkSensorService(false)) {
                try {
                    this.mSensorService.dump(FileDescriptor.out, new String[]{"sensorsControl", "update", Integer.toString(i2), str});
                } catch (RemoteException e2) {
                    Slog.e(TAG, "setAppSensorsControlType " + e2);
                } catch (UnsupportedOperationException e3) {
                    Slog.e(TAG, "setAppSensorsControlType " + e3);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setUidControlTypeLocked(int i2, String str) {
        if (UserHandle.isApp(i2)) {
            if (this.mUidDisableType.indexOfKey(i2) < 0 || !this.mUidDisableType.get(i2).equals(str)) {
                if (Controller.DEBUG) {
                    Slog.d(TAG, "update, uid = " + i2 + " type = " + str);
                }
                this.mUidDisableType.put(i2, str);
                boolean z2 = true;
                if (!this.mUidState.get(i2)) {
                    setUidState(i2, true);
                } else {
                    z2 = false;
                }
                setAppSensorsControlType(i2, str);
                if (z2) {
                    updateRuleForUidLocked(i2, getUidRule(i2));
                }
            }
        }
    }

    private void setUidState(int i2, boolean z2) {
        if (UserHandle.isApp(i2)) {
            if (this.mUidState.indexOfKey(i2) < 0 || z2 != this.mUidState.get(i2)) {
                if (Controller.DEBUG) {
                    Slog.d(TAG, "HideMode setUidState, uid = " + i2 + " allow = " + z2);
                }
                this.mUidState.put(i2, z2);
                setAppSensorsControlPolicy(i2, z2);
            }
        }
    }

    private void updateForUidRemovedLocked(int i2, int i3) {
        if (Controller.DEBUG) {
            Slog.v(TAG, "updateForUidRemovedLocked, userId=" + i2 + ", uid=" + i3);
        }
        this.mUidState.delete(i3);
        this.mUidDisableType.delete(i3);
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

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        synchronized (this.mRulesLock) {
            try {
                indentingPrintWriter.println("#######dump##SensorController#######");
                indentingPrintWriter.println("mSensorControlFlg = " + this.mSensorControlFlg);
                indentingPrintWriter.println("mSensorControlPolicy: true indicate allow to use");
                for (int i2 = 0; i2 < this.mUidState.size(); i2++) {
                    indentingPrintWriter.println("uid = " + this.mUidState.keyAt(i2) + " policy = " + this.mUidState.valueAt(i2));
                }
                indentingPrintWriter.println("SensorController operation history:");
                this.mHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.println("end#######dump##SensorController#######end");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onDisable() {
        synchronized (this.mRulesLock) {
            try {
                Slog.v(TAG, "SensorController onDisable ");
                for (int i2 = 0; i2 < this.mUidState.size(); i2++) {
                    int iKeyAt = this.mUidState.keyAt(i2);
                    setUidState(iKeyAt, true);
                    setUidControlTypeLocked(iKeyAt, "all");
                }
                this.mUidDisableType.clear();
                this.mUidState.clear();
                this.mSensorsAppRuleChecker.unregisterAppRuleChangeListener(this.mAppRuleChangedCallback);
                this.mAppRuleChangedCallback.a();
                this.mAppRuleChangedCallback = null;
                this.mSensorsAppRuleChecker = null;
                this.mSensorControlFlg = false;
                this.mHistoryLog.log("PowerKeeper.Sensor: onDisable");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onEnable() {
        synchronized (this.mRulesLock) {
            try {
                Slog.v(TAG, "SensorController enable ");
                this.mUidDisableType.clear();
                this.mUidState.clear();
                int[] allUids = getAllUids();
                for (int i2 = 0; i2 < allUids.length; i2++) {
                    setUidState(allUids[i2], true);
                    setUidControlTypeLocked(allUids[i2], "all");
                }
                this.mSensorsAppRuleChecker = this.mPowerKeeperManager.getSensorAppRuleChecker();
                PowerKeeperInterface.c cVar = new PowerKeeperInterface.c(this.mHandler, this.mAppRuleChangedListener);
                this.mAppRuleChangedCallback = cVar;
                this.mSensorsAppRuleChecker.registerAppRuleChangeListener(cVar);
                SparseIntArray uidsRule = getUidsRule(allUids);
                for (int i3 = 0; i3 < uidsRule.size(); i3++) {
                    updateRuleForUidLocked(uidsRule.keyAt(i3), uidsRule.valueAt(i3));
                }
                this.mSensorControlFlg = true;
                this.mHistoryLog.log("PowerKeeper.Sensor: onEnable");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void onSetUidDisableType(final int i2, final String str) {
        if (this.isEnabled) {
            this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.SensorController.2
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (SensorController.this.mRulesLock) {
                        try {
                            if (TextUtils.isEmpty(str)) {
                                SensorController.this.setUidControlTypeLocked(i2, "all");
                            } else {
                                SensorController.this.setUidControlTypeLocked(i2, str);
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            });
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

    public void setExternalPolicy(int i2, boolean z2) {
        boolean z3;
        boolean z4 = true;
        if (z2) {
            z3 = false;
        } else {
            setAppSensorsControlPolicy(i2, z2);
            z3 = true;
        }
        if (z2 && this.mUidState.indexOfKey(i2) >= 0 && this.mUidState.get(i2)) {
            setAppSensorsControlPolicy(i2, z2);
        } else if (this.mPowerKeeperManager.isUidForeground(i2) && this.mPowerKeeperManager.isScreenOn() && z2) {
            setAppSensorsControlPolicy(i2, z2);
            this.mUidState.put(i2, z2);
        } else {
            z4 = z3;
        }
        if (Controller.DEBUG && z4) {
            Slog.d("milletsen", "uid=" + i2 + " allowed=" + z2 + " get:" + this.mUidState.get(i2));
        }
    }
}
