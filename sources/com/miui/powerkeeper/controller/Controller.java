package com.miui.powerkeeper.controller;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.miui.enterprise.ApplicationHelper;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.UserCheckManager;
import java.util.HashSet;
import java.util.Map;
import miui.enterprise.ApplicationHelperStub;

/* JADX INFO: loaded from: classes.dex */
public class Controller {
    protected static final boolean DEBUG;
    protected static final int MAX_HISTORY_ITEMS;
    private static final int MSG_EXIT_HANDLER = -84215046;
    public static final int POLICY_CONTROL_BY_APP_COUNT = 1;
    public static final int POLICY_CONTROL_BY_RULE_CHECKER = 0;
    private static final HashSet<String> mIgnoreApps;
    protected String TAG;
    protected boolean isEnabled;
    protected Context mContext;
    protected MyHandler mHandler;
    protected final HashSet<Integer> mIgnoreUids;
    protected final Object mLock;
    protected PowerKeeperInterface.d0 mPackageManagerCallback;
    private PowerKeeperInterface.x mPackageReceiver;
    protected PowerKeeperManager mPowerKeeperManager;
    protected SparseIntArray mUidPoliy;
    protected UserCheckManager mUserCheckManager;
    protected PowerKeeperInterface.g0 mUserStatusChangedCallback;
    private PowerKeeperInterface.b0 mUserStatusChangedListener;

    final class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            synchronized (Controller.this.mLock) {
                try {
                    if (message.what != Controller.MSG_EXIT_HANDLER) {
                        Controller.this.onHandleMessage(message);
                        return;
                    }
                    Controller controller = Controller.this;
                    if (controller.isEnabled) {
                        return;
                    }
                    controller.mHandler.removeCallbacksAndMessages(null);
                    Controller.this.mHandler = null;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    static {
        boolean z2 = Build.IS_DEBUGGABLE;
        DEBUG = z2;
        MAX_HISTORY_ITEMS = z2 ? 128 : 64;
        HashSet<String> hashSet = new HashSet<>();
        mIgnoreApps = hashSet;
        hashSet.add("com.android.cts.net.hostside.app2");
        hashSet.add("com.android.cts.net.hostside");
        hashSet.add("android.os.cts.batterysaving");
        hashSet.add("android.location.cts");
        hashSet.add("com.android.compatibility.common.deviceinfo");
        hashSet.add("android.content.cts");
        hashSet.add("com.android.preconditions.cts");
        hashSet.add("android.os.cts.batterysaving.app_target_api_25");
        hashSet.add("android.os.cts.batterysaving.app_target_api_current");
        hashSet.add("com.google.android.gts.location");
        hashSet.add("android.net.cts");
        hashSet.add("com.android.cts.verifier");
    }

    protected Controller(Context context, PowerKeeperManager powerKeeperManager) {
        this.TAG = "PowerKeeper.Controller";
        this.mLock = new Object();
        this.isEnabled = false;
        this.mUidPoliy = new SparseIntArray();
        this.mIgnoreUids = new HashSet<>();
        this.mPackageReceiver = new PowerKeeperInterface.x() { // from class: com.miui.powerkeeper.controller.Controller.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onPackageAdded(int i2, String str, int i3) {
                synchronized (Controller.this.mLock) {
                    try {
                        if (ApplicationHelper.shouldKeeAlive(Controller.this.mContext, str) || ApplicationHelperStub.getInstance().isKeepAlive(str)) {
                            Controller.this.mIgnoreUids.add(Integer.valueOf(i3));
                        }
                        if (Controller.mIgnoreApps.contains(str)) {
                            Controller.this.mIgnoreUids.add(Integer.valueOf(i3));
                        }
                        Controller.this.onPackageAdded(i2, str, i3);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onUidRemoved(int i2, int i3) {
                synchronized (Controller.this.mLock) {
                    try {
                        Controller controller = Controller.this;
                        if (!controller.isEnabled) {
                            if (Controller.DEBUG) {
                                Log.v(controller.TAG, "still disabled");
                            }
                        } else if (controller.mIgnoreUids.contains(Integer.valueOf(i3))) {
                            Controller.this.mIgnoreUids.remove(Integer.valueOf(i3));
                        } else {
                            Controller.this.onUidRemoved(i2, i3);
                            Controller.this.controllerUpdateForUidRemovedLocked(i3);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onRegistered() {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onUserReadyPackageAdded(int i2, Map<String, Integer> map) {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onPackageRemoved(int i2, String str, int i3) {
            }
        };
        this.mUserStatusChangedListener = new PowerKeeperInterface.b0() { // from class: com.miui.powerkeeper.controller.Controller.2
            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserEnableStatusChange(int i2, boolean z2) {
                synchronized (Controller.this.mLock) {
                    try {
                        Controller controller = Controller.this;
                        if (!controller.isEnabled) {
                            if (Controller.DEBUG) {
                                Log.v(controller.TAG, "still disabled");
                            }
                        } else {
                            controller.onUserEnableStatusChange(i2, z2);
                            if (!z2) {
                                Controller.this.controllerUpdateForUserRemovedLocked(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserForegroundStatusChange(int i2, boolean z2) {
                synchronized (Controller.this.mLock) {
                    Controller.this.onUserForegroundStatusChange(i2, z2);
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserRunningStatusChange(int i2, boolean z2) {
                synchronized (Controller.this.mLock) {
                    try {
                        Controller controller = Controller.this;
                        if (!controller.isEnabled) {
                            if (Controller.DEBUG) {
                                Log.v(controller.TAG, "still disabled");
                            }
                        } else {
                            controller.onUserRunningStatusChange(i2, z2);
                            if (!z2) {
                                Controller.this.controllerUpdateForUserRemovedLocked(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onXSpaceStatusChange(int i2, boolean z2) {
                synchronized (Controller.this.mLock) {
                    try {
                        Controller controller = Controller.this;
                        if (!controller.isEnabled) {
                            if (Controller.DEBUG) {
                                Log.v(controller.TAG, "still disabled");
                            }
                        } else {
                            controller.onXSpaceStatusChange(i2, z2);
                            if (!z2) {
                                Controller.this.controllerUpdateForUserRemovedLocked(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mContext = context;
        this.mPowerKeeperManager = powerKeeperManager;
        this.mUserCheckManager = powerKeeperManager.getUserCheckManager();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void controllerUpdateForUidRemovedLocked(int i2) {
        if (DEBUG) {
            Log.v(this.TAG, "controllerUpdateForUidRemovedLocked, uid=" + i2);
        }
        this.mUidPoliy.delete(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void controllerUpdateForUserRemovedLocked(int i2) {
        if (DEBUG) {
            Log.v(this.TAG, "controllerUpdateForUserRemovedLocked, userId=" + i2);
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        int size = this.mUidPoliy.size();
        for (int i3 = 0; i3 < size; i3++) {
            if (UserHandle.getUserId(this.mUidPoliy.keyAt(i3)) == i2) {
                sparseBooleanArray.put(this.mUidPoliy.keyAt(i3), true);
            }
        }
        for (int i4 = 0; i4 < sparseBooleanArray.size(); i4++) {
            this.mUidPoliy.delete(sparseBooleanArray.keyAt(i4));
        }
    }

    public void disable() {
        synchronized (this.mLock) {
            try {
                if (!this.isEnabled) {
                    if (DEBUG) {
                        Log.v(this.TAG, "still disabled");
                    }
                    return;
                }
                Log.i(this.TAG, "disable");
                this.isEnabled = false;
                onDisable();
                this.mPowerKeeperManager.unregisterPackageChangedListener(this.mPackageManagerCallback);
                this.mPackageManagerCallback.a();
                this.mPackageManagerCallback = null;
                this.mUserCheckManager.K(this.mUserStatusChangedCallback);
                this.mUserStatusChangedCallback.a();
                this.mUserStatusChangedCallback = null;
                this.mUidPoliy.clear();
                this.mHandler.sendMessage(Message.obtain(this.mHandler, MSG_EXIT_HANDLER));
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void enable() {
        synchronized (this.mLock) {
            try {
                if (this.isEnabled) {
                    if (DEBUG) {
                        Log.v(this.TAG, "already enabled");
                    }
                    return;
                }
                Log.i(this.TAG, "enable");
                this.isEnabled = true;
                if (this.mHandler == null) {
                    this.mHandler = new MyHandler(PowerKeeperManager.getThread().getLooper());
                }
                this.mUidPoliy.clear();
                this.mPackageManagerCallback = new PowerKeeperInterface.d0(this.mHandler, this.mPackageReceiver);
                this.mUserStatusChangedCallback = new PowerKeeperInterface.g0(this.mHandler, this.mUserStatusChangedListener);
                this.mPowerKeeperManager.registerPackageChangedListener(this.mPackageManagerCallback);
                this.mUserCheckManager.J(this.mUserStatusChangedCallback);
                onEnable();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected int[] getAllUids() {
        int[] iArrX = this.mUserCheckManager.x();
        SparseArray sparseArray = new SparseArray();
        int i2 = 0;
        for (int i3 : iArrX) {
            Map<String, Integer> packageUidsMap = this.mPowerKeeperManager.getPackageUidsMap(i3);
            sparseArray.put(i3, (Integer[]) packageUidsMap.values().toArray(new Integer[packageUidsMap.values().size()]));
        }
        if (this.mUserCheckManager.E()) {
            int iZ = this.mUserCheckManager.z();
            Map<String, Integer> packageUidsMap2 = this.mPowerKeeperManager.getPackageUidsMap(iZ);
            sparseArray.put(iZ, (Integer[]) packageUidsMap2.values().toArray(new Integer[packageUidsMap2.values().size()]));
        }
        int length = 0;
        for (int i4 = 0; i4 < sparseArray.size(); i4++) {
            length += ((Integer[]) sparseArray.valueAt(i4)).length;
        }
        int[] iArr = new int[length];
        int i5 = 0;
        while (i2 < sparseArray.size()) {
            Integer[] numArr = (Integer[]) sparseArray.valueAt(i2);
            int length2 = numArr.length + i5;
            for (int i6 = i5; i6 < length2; i6++) {
                iArr[i6] = numArr[i6 - i5].intValue();
            }
            i2++;
            i5 = length2;
        }
        return iArr;
    }

    public int getUidPolicy(int i2) {
        int i3;
        synchronized (this.mLock) {
            i3 = this.mUidPoliy.get(i2, 0);
        }
        return i3;
    }

    public void setUidPolicy(int i2, int i3) {
        synchronized (this.mLock) {
            try {
                if (!this.isEnabled) {
                    if (DEBUG) {
                        Log.v(this.TAG, "still disabled");
                    }
                } else {
                    if (this.mIgnoreUids.contains(Integer.valueOf(i2))) {
                        return;
                    }
                    if (i3 != this.mUidPoliy.get(i2)) {
                        if (i3 == 0) {
                            this.mUidPoliy.delete(i2);
                        } else {
                            this.mUidPoliy.put(i2, i3);
                        }
                        onSetUidPolicy(i2, i3);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void update() {
        synchronized (this.mLock) {
            try {
                if (this.isEnabled) {
                    onUpdate();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected Controller(Context context, PowerKeeperManager powerKeeperManager, String str) {
        this.TAG = "PowerKeeper.Controller";
        this.mLock = new Object();
        this.isEnabled = false;
        this.mUidPoliy = new SparseIntArray();
        this.mIgnoreUids = new HashSet<>();
        this.mPackageReceiver = new PowerKeeperInterface.x() { // from class: com.miui.powerkeeper.controller.Controller.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onPackageAdded(int i2, String str2, int i3) {
                synchronized (Controller.this.mLock) {
                    try {
                        if (ApplicationHelper.shouldKeeAlive(Controller.this.mContext, str2) || ApplicationHelperStub.getInstance().isKeepAlive(str2)) {
                            Controller.this.mIgnoreUids.add(Integer.valueOf(i3));
                        }
                        if (Controller.mIgnoreApps.contains(str2)) {
                            Controller.this.mIgnoreUids.add(Integer.valueOf(i3));
                        }
                        Controller.this.onPackageAdded(i2, str2, i3);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onUidRemoved(int i2, int i3) {
                synchronized (Controller.this.mLock) {
                    try {
                        Controller controller = Controller.this;
                        if (!controller.isEnabled) {
                            if (Controller.DEBUG) {
                                Log.v(controller.TAG, "still disabled");
                            }
                        } else if (controller.mIgnoreUids.contains(Integer.valueOf(i3))) {
                            Controller.this.mIgnoreUids.remove(Integer.valueOf(i3));
                        } else {
                            Controller.this.onUidRemoved(i2, i3);
                            Controller.this.controllerUpdateForUidRemovedLocked(i3);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onRegistered() {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onUserReadyPackageAdded(int i2, Map<String, Integer> map) {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onPackageRemoved(int i2, String str2, int i3) {
            }
        };
        this.mUserStatusChangedListener = new PowerKeeperInterface.b0() { // from class: com.miui.powerkeeper.controller.Controller.2
            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserEnableStatusChange(int i2, boolean z2) {
                synchronized (Controller.this.mLock) {
                    try {
                        Controller controller = Controller.this;
                        if (!controller.isEnabled) {
                            if (Controller.DEBUG) {
                                Log.v(controller.TAG, "still disabled");
                            }
                        } else {
                            controller.onUserEnableStatusChange(i2, z2);
                            if (!z2) {
                                Controller.this.controllerUpdateForUserRemovedLocked(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserForegroundStatusChange(int i2, boolean z2) {
                synchronized (Controller.this.mLock) {
                    Controller.this.onUserForegroundStatusChange(i2, z2);
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserRunningStatusChange(int i2, boolean z2) {
                synchronized (Controller.this.mLock) {
                    try {
                        Controller controller = Controller.this;
                        if (!controller.isEnabled) {
                            if (Controller.DEBUG) {
                                Log.v(controller.TAG, "still disabled");
                            }
                        } else {
                            controller.onUserRunningStatusChange(i2, z2);
                            if (!z2) {
                                Controller.this.controllerUpdateForUserRemovedLocked(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onXSpaceStatusChange(int i2, boolean z2) {
                synchronized (Controller.this.mLock) {
                    try {
                        Controller controller = Controller.this;
                        if (!controller.isEnabled) {
                            if (Controller.DEBUG) {
                                Log.v(controller.TAG, "still disabled");
                            }
                        } else {
                            controller.onXSpaceStatusChange(i2, z2);
                            if (!z2) {
                                Controller.this.controllerUpdateForUserRemovedLocked(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mContext = context;
        this.mPowerKeeperManager = powerKeeperManager;
        this.mUserCheckManager = powerKeeperManager.getUserCheckManager();
        this.TAG = str;
    }

    protected void onDisable() {
    }

    protected void onEnable() {
    }

    protected void onUpdate() {
    }

    public void restoreToDefaultState() {
    }

    protected void onHandleMessage(Message message) {
    }

    protected void onSetUidPolicy(int i2, int i3) {
    }

    protected void onUidRemoved(int i2, int i3) {
    }

    protected void onUserEnableStatusChange(int i2, boolean z2) {
    }

    protected void onUserForegroundStatusChange(int i2, boolean z2) {
    }

    protected void onUserRunningStatusChange(int i2, boolean z2) {
    }

    protected void onXSpaceStatusChange(int i2, boolean z2) {
    }

    protected void onPackageAdded(int i2, String str, int i3) {
    }
}
