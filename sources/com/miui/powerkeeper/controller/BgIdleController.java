package com.miui.powerkeeper.controller;

import android.content.Context;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.utils.PackageUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BgIdleController extends Controller {
    private static final boolean ACT_SET_IDLE = true;
    private static final String TAG = "PowerKeeper.AppBgIdle";
    private PowerKeeperInterface.c mAppRuleChangedCallback;
    private PowerKeeperInterface.k mAppRuleChangedListener;
    private PowerKeeperInterface.l mBkGroundIdleChecker;
    private LocalLog mHistoryLog;
    private PowerKeeperManager mPowerKeeperManager;
    private final Object mRulesLock;
    private final SparseBooleanArray mUidState;

    public BgIdleController(Context context, PowerKeeperManager powerKeeperManager) {
        super(context, powerKeeperManager, TAG);
        this.mRulesLock = new Object();
        this.mUidState = new SparseBooleanArray();
        this.mHistoryLog = new LocalLog(Controller.MAX_HISTORY_ITEMS);
        this.mAppRuleChangedListener = new PowerKeeperInterface.k() { // from class: com.miui.powerkeeper.controller.BgIdleController.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.k
            public void onAppRuleChange(int i2, int i3) {
                synchronized (BgIdleController.this.mLock) {
                    try {
                        BgIdleController bgIdleController = BgIdleController.this;
                        if (!bgIdleController.isEnabled) {
                            Slog.v(BgIdleController.TAG, "still disabled");
                        } else {
                            if (!bgIdleController.mIgnoreUids.contains(Integer.valueOf(i2))) {
                                BgIdleController.this.updateRuleForUidLocked(i2, i3);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mPowerKeeperManager = powerKeeperManager;
    }

    private int getUidRule(int i2) {
        return this.mBkGroundIdleChecker.getAppRule(i2);
    }

    private boolean getUidState(int i2, String str) {
        if (!UserHandle.isApp(i2) || str == null) {
            return false;
        }
        return this.mPowerKeeperManager.getOpRunInBackgroundManager().f(i2, str);
    }

    private SparseIntArray getUidsRule(int[] iArr) {
        return this.mBkGroundIdleChecker.getAppsRule(iArr);
    }

    private void setBkGroundIdleControlPolicy(int i2, String str, boolean z2) {
        synchronized (this.mRulesLock) {
            this.mPowerKeeperManager.getOpRunInBackgroundManager().k(TAG, i2, str, z2);
        }
    }

    private void setUidState(int i2, boolean z2) {
        if (UserHandle.isApp(i2)) {
            String packageNameByUid = PackageUtil.getPackageNameByUid(this.mContext, i2);
            List<String> activePkgList = PowerKeeperManager.getInstance().getActiveController().getActivePkgList(this.mContext, 3);
            if (activePkgList != null && activePkgList.contains(packageNameByUid)) {
                this.mUidState.put(i2, true);
            }
            if (packageNameByUid == null) {
                Slog.w(TAG, "getPackageNameByUid uid = " + i2 + "pkg is null");
                return;
            }
            if (this.mUidState.indexOfKey(i2) < 0) {
                this.mUidState.put(i2, getUidState(i2, packageNameByUid));
            }
            if (z2 != this.mUidState.get(i2)) {
                this.mUidState.put(i2, z2);
                Slog.d(TAG, "setUidState, uid = " + i2 + " allow = " + z2);
                setBkGroundIdleControlPolicy(i2, packageNameByUid, z2);
                this.mHistoryLog.log("AppBgIdle: setUidState uid=" + i2 + " allow=" + z2);
            }
        }
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
        setUidState(i2, i3 == 1);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        synchronized (this.mRulesLock) {
            try {
                indentingPrintWriter.println("#######dump##BkGroundIdleController#######");
                indentingPrintWriter.println("BkGroundIdleControlPolicy: ");
                for (int i2 = 0; i2 < this.mUidState.size(); i2++) {
                    if (this.mUidState.valueAt(i2)) {
                        indentingPrintWriter.println("uid = " + this.mUidState.keyAt(i2) + " policy = " + this.mUidState.valueAt(i2));
                    }
                }
                indentingPrintWriter.println("BkGroundIdleController operation history:");
                this.mHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.println("end#######dump##BkGroundIdleController#######end");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onDisable() {
        synchronized (this.mRulesLock) {
            try {
                Slog.v(TAG, "onDisable ");
                for (int i2 = 0; i2 < this.mUidState.size(); i2++) {
                    setUidState(this.mUidState.keyAt(i2), false);
                }
                this.mUidState.clear();
                this.mBkGroundIdleChecker.unregisterAppRuleChangeListener(this.mAppRuleChangedCallback);
                this.mAppRuleChangedCallback.a();
                this.mAppRuleChangedCallback = null;
                this.mBkGroundIdleChecker = null;
                this.mPowerKeeperManager.getOpRunInBackgroundManager().l(TAG, false);
                this.mHistoryLog.log("PowerKeeper.AppBgIdle: onDisable");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onEnable() {
        synchronized (this.mRulesLock) {
            try {
                Slog.v(TAG, "enable ");
                this.mUidState.clear();
                this.mBkGroundIdleChecker = this.mPowerKeeperManager.getBgIdleRuleChecker();
                int[] allUids = getAllUids();
                for (int i2 : allUids) {
                    this.mUidState.put(i2, getUidState(i2, PackageUtil.getPackageNameByUid(this.mContext, i2)));
                }
                PowerKeeperInterface.c cVar = new PowerKeeperInterface.c(this.mHandler, this.mAppRuleChangedListener);
                this.mAppRuleChangedCallback = cVar;
                this.mBkGroundIdleChecker.registerAppRuleChangeListener(cVar);
                this.mPowerKeeperManager.getOpRunInBackgroundManager().l(TAG, true);
                SparseIntArray uidsRule = getUidsRule(allUids);
                for (int i3 = 0; i3 < uidsRule.size(); i3++) {
                    updateRuleForUidLocked(uidsRule.keyAt(i3), uidsRule.valueAt(i3));
                }
                this.mHistoryLog.log("PowerKeeper.AppBgIdle: onEnable");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onSetUidPolicy(int i2, int i3) {
        synchronized (this.mRulesLock) {
            if (i3 == 0) {
                try {
                    updateRuleForUidLocked(i2, getUidRule(i2));
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUidRemoved(int i2, int i3) {
        synchronized (this.mRulesLock) {
            updateForUidRemovedLocked(i2, i3);
        }
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

    @Override // com.miui.powerkeeper.controller.Controller
    public void restoreToDefaultState() {
        synchronized (this.mRulesLock) {
            try {
                this.mPowerKeeperManager.getOpRunInBackgroundManager().l(TAG, true);
                for (int i2 : getAllUids()) {
                    if (UserHandle.isApp(i2)) {
                        setUidState(i2, false);
                    }
                }
                this.mHistoryLog.log("PowerKeeper.AppBgIdle: restoreToDefaultState");
                this.mPowerKeeperManager.getOpRunInBackgroundManager().l(TAG, false);
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
