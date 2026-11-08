package com.miui.powerkeeper.appcontrol;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.AppActiveChecker;
import com.miui.powerkeeper.FeedBackManager;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.provider.AppActiveConfigure;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class HideModeStateMachine {
    private static final long DEFAULT_DELAY_MS_AFTER_BACKGROUND = 0;
    private static final int MSG_ALARM_HAPPENED = 3;
    private static final int MSG_COMMON_END = 255;
    private static final int MSG_COMMON_START = 0;
    private static final int MSG_HOT_CHANGED = 2;
    private static final int MSG_SCREEN_CHANGED = 1;
    private static final int MSG_STATE_END = 511;
    private static final int MSG_STATE_START = 256;
    private static final int MSG_SUB_STATE_DELAY_TIME = 513;
    private static final int MSG_SUB_STATE_END = 767;
    private static final int MSG_SUB_STATE_START = 512;
    private static final int MSG_UID_ACTIVE_CHANGED = 19;
    private static final int MSG_UID_FOREGROUND_CHANGED = 16;
    private static final int MSG_UID_FOREGROUND_PRETASK_CHANGED = 17;
    private static final int MSG_UID_FOREGROUND_SERVICE_CHANGED = 21;
    private static final int MSG_UID_REMOVED = 18;
    private static final int MSG_UID_RULE_CHANGED = 20;
    private static final int SUB_STATE_ACTIVE = 0;
    private static final int SUB_STATE_ACTIVE_TO_INACTIVE = 1;
    private static final int SUB_STATE_INACTIVE = 2;
    private static final String TAG = "HideModeStateMachine";
    private PowerKeeperInterface.a mAlarmHappenCallback;
    private PowerKeeperInterface.b mAppActiveChangedCallback;
    private AppActiveChecker mAppActiveChecker;
    private Context mContext;
    private PowerKeeperInterface.e mFeedBackCallback;
    private PowerKeeperInterface.f mForegroundActivitiesChangedCallback;
    private PowerKeeperInterface.g mForegroundPreTaskChangedCallback;
    private PowerKeeperInterface.h mForegroundServiceChangedCallback;
    private PowerKeeperInterface.d0 mPackageManagerCallback;
    private PowerKeeperManager mPowerKeeperManager;
    private PowerKeeperInterface.f0 mScreenChangedCallback;
    private volatile boolean mScreenOn;
    private PowerKeeperInterface.g0 mUserStatusChangedCallback;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final boolean DBG_POWER = Log.isLoggable("power.debug", 3);
    private final Object mLock = new Object();
    private volatile boolean mEnabled = false;
    private volatile boolean mHot = false;
    private SparseArray<HideModeAppInfo> mUidAppInfo = new SparseArray<>();
    private Map<String, List<PowerKeeperInterface.c>> mAppRuleChangedCallbacks = new HashMap();
    private PowerKeeperInterface.r mProcessObserver = new PowerKeeperInterface.r() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.3
        @Override // com.miui.powerkeeper.PowerKeeperInterface.r
        public void onForegroundActivitiesChanged(int i2, boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        HideModeStateMachine.this.updateForForegroundUidChangedLocked(i2, z2);
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.s mForegroundPreTaskObserver = new PowerKeeperInterface.s() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.4
        @Override // com.miui.powerkeeper.PowerKeeperInterface.s
        public void onForegroundPreTaskChanged(int i2, boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        HideModeStateMachine.this.updateForForegroundPreTaskUidChangedLocked(i2, z2);
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.t mForegroundServiceObserver = new PowerKeeperInterface.t() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.5
        @Override // com.miui.powerkeeper.PowerKeeperInterface.t
        public void onForegroundServiceChanged(int i2, boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        HideModeStateMachine.this.updateForForegroundServiceUidChangedLocked(i2, z2);
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.z mScreenReceiver = new PowerKeeperInterface.z() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.6
        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onRegistered(boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        if (HideModeStateMachine.this.mScreenOn != HideModeStateMachine.this.mPowerKeeperManager.isScreenOn()) {
                            HideModeStateMachine.this.updateForScreenChangedLocked();
                        }
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onScreenChanged(boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        if (HideModeStateMachine.this.mScreenOn != HideModeStateMachine.this.mPowerKeeperManager.isScreenOn()) {
                            HideModeStateMachine.this.updateForScreenChangedLocked();
                        }
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.x mPackageReceiver = new PowerKeeperInterface.x() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.7
        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onUidRemoved(int i2, int i3) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        HideModeStateMachine.this.updateForUidRemovedLocked(i3);
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
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
        public void onPackageAdded(int i2, String str, int i3) {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onPackageRemoved(int i2, String str, int i3) {
        }
    };
    private PowerKeeperInterface.i mAlarmHappenListener = new PowerKeeperInterface.i() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.8
        @Override // com.miui.powerkeeper.PowerKeeperInterface.i
        public void onAlarmHappen(long j2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (!HideModeStateMachine.this.mEnabled) {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                        return;
                    }
                    if (HideModeStateMachine.DEBUG) {
                        Log.d(HideModeStateMachine.TAG, "onAlarmHappen, elapsedRealtime = " + j2 + "ms");
                    }
                    HideModeStateMachine.this.updateForAlarmHappenedLocked(j2);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.j mAppActiveChangedListener = new PowerKeeperInterface.j() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.9
        @Override // com.miui.powerkeeper.PowerKeeperInterface.j
        public void onAppActiveChange(int i2, boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        HideModeStateMachine.this.updateForUidActiveChangedLocked(i2, z2);
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.b0 mUserStatusChangedListener = new PowerKeeperInterface.b0() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.10
        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserEnableStatusChange(int i2, boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        if (!z2) {
                            HideModeStateMachine.this.updateForUserRemovedLocked(i2);
                        }
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserForegroundStatusChange(int i2, boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserRunningStatusChange(int i2, boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        if (!z2) {
                            HideModeStateMachine.this.updateForUserRemovedLocked(i2);
                        }
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onXSpaceStatusChange(int i2, boolean z2) {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        if (!z2) {
                            HideModeStateMachine.this.updateForUserRemovedLocked(i2);
                        }
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.o mFeedBackBatteryListener = new PowerKeeperInterface.o() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.11
        @Override // com.miui.powerkeeper.PowerKeeperInterface.o
        public void batteryBecomeHeat() {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        HideModeStateMachine.this.updateForBatteryHeatChangedLocked(true);
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.o
        public void batteryCoolDown() {
            synchronized (HideModeStateMachine.this.mLock) {
                try {
                    if (HideModeStateMachine.this.mEnabled) {
                        HideModeStateMachine.this.updateForBatteryHeatChangedLocked(false);
                    } else {
                        if (HideModeStateMachine.DEBUG) {
                            Log.v(HideModeStateMachine.TAG, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private SmHandler mSmHandler = new SmHandler(PowerKeeperManager.getThread().getLooper());

    class ActiveHideModeState extends HideModeState {
        public ActiveHideModeState(String str) {
            super(str);
        }

        @Override // com.miui.powerkeeper.appcontrol.HideModeStateMachine.HideModeState
        public void startRun() {
            int uid = getUid();
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + uid + " State: " + this.mStateName + " |-> startRun");
            }
            if (HideModeStateMachine.this.isScreenOnLocked() && HideModeStateMachine.this.isUidForegroundLocked(uid)) {
                return;
            }
            this.mParentInfo.stateCompleteCallBack();
        }
    }

    class HideModeAppInfo {
        HideModeState mActiveHideModeState;
        HideModeState mCurrentHideModeState;
        HideModeState mInactiveFinalHideModeState;
        List<HideModeState> mStateList = Lists.newArrayList();
        Map<String, HideModeState> mStateMap = new ArrayMap();
        Map<String, HideModeSubFeature> mSubFeatureMap = new ArrayMap();
        int mUid;

        public HideModeAppInfo(int i2) {
            this.mUid = -1;
            this.mUid = i2;
            ActiveHideModeState activeHideModeState = HideModeStateMachine.this.new ActiveHideModeState(AppActiveConfigure.Columns.ACTIVE);
            this.mActiveHideModeState = activeHideModeState;
            activeHideModeState.setParentInfo(this);
            InactiveFinalHideModeState inactiveFinalHideModeState = HideModeStateMachine.this.new InactiveFinalHideModeState("inactive_final");
            this.mInactiveFinalHideModeState = inactiveFinalHideModeState;
            inactiveFinalHideModeState.setParentInfo(this);
            this.mStateList.add(this.mActiveHideModeState);
            this.mStateList.add(this.mInactiveFinalHideModeState);
            this.mCurrentHideModeState = this.mActiveHideModeState;
            initState();
            initSubFeature();
            this.mCurrentHideModeState.startRun();
        }

        private void addState(HideModeState hideModeState, HideModeState hideModeState2) {
            if (hideModeState == null) {
                hideModeState = this.mActiveHideModeState;
            }
            int iIndexOf = this.mStateList.indexOf(hideModeState);
            if (iIndexOf >= 0) {
                this.mStateList.add(iIndexOf + 1, hideModeState2);
                this.mStateMap.put(hideModeState2.getName(), hideModeState2);
                hideModeState2.setParentInfo(this);
            }
        }

        private void initState() {
            List<String> stateList = HideModeStateMachineConfig.getInstance().getStateList();
            HideModeState hideModeState = this.mActiveHideModeState;
            int i2 = 0;
            while (i2 < stateList.size()) {
                HideModeState hideModeState2 = HideModeStateMachine.this.new HideModeState(stateList.get(i2));
                addState(hideModeState, hideModeState2);
                i2++;
                hideModeState = hideModeState2;
            }
        }

        private void initSubFeature() {
            Map<String, HideModeStateMachineConfig.FeatureConfig> featureConfigMap = HideModeStateMachineConfig.getInstance().getFeatureConfigMap();
            for (String str : featureConfigMap.keySet()) {
                enableSubFeature(str, featureConfigMap.get(str).mEnabled);
            }
        }

        public void addSubFeatureToState(HideModeState hideModeState, HideModeSubFeature hideModeSubFeature) {
            hideModeState.addSubFeature(hideModeSubFeature);
            this.mSubFeatureMap.put(hideModeSubFeature.getSubFeatureName(), hideModeSubFeature);
            if (this.mStateList.indexOf(hideModeState) == this.mStateList.indexOf(this.mCurrentHideModeState)) {
                hideModeSubFeature.startRun();
                return;
            }
            if (this.mStateList.indexOf(hideModeState) >= this.mStateList.indexOf(this.mCurrentHideModeState)) {
                return;
            }
            this.mCurrentHideModeState.setActive();
            int iIndexOf = this.mStateList.indexOf(this.mCurrentHideModeState);
            while (true) {
                iIndexOf--;
                if (iIndexOf < this.mStateList.indexOf(hideModeState)) {
                    this.mCurrentHideModeState.startRun();
                    return;
                } else {
                    HideModeState hideModeState2 = this.mStateList.get(iIndexOf);
                    this.mCurrentHideModeState = hideModeState2;
                    hideModeState2.setActive();
                }
            }
        }

        public void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
            indentingPrintWriter.print("UID = " + this.mUid);
            this.mCurrentHideModeState.dump(indentingPrintWriter, strArr);
        }

        public void enableSubFeature(String str, boolean z2) {
            HideModeState hideModeState = this.mStateMap.get(HideModeStateMachineConfig.getInstance().getFeatureStateMap().get(str));
            if (hideModeState == null) {
                Log.e(HideModeStateMachine.TAG, str + " is not in any states");
                return;
            }
            if (z2 && !this.mSubFeatureMap.containsKey(str)) {
                HideModeStateMachineConfig.FeatureConfig featureConfig = HideModeStateMachineConfig.getInstance().getFeatureConfig(str);
                addSubFeatureToState(hideModeState, HideModeStateMachine.this.new HideModeSubFeature(featureConfig.mFeatureName, featureConfig.mFeatureOpt));
            } else {
                if (z2 || !this.mSubFeatureMap.containsKey(str)) {
                    return;
                }
                removeSubFeatureFromState(hideModeState, this.mSubFeatureMap.get(str));
            }
        }

        public int getRule(String str) {
            if (this.mSubFeatureMap.containsKey(str)) {
                return this.mSubFeatureMap.get(str).getSubFeatureRule();
            }
            return 0;
        }

        public void processMessage(Message message) {
            this.mCurrentHideModeState.processMessage(message);
            int i2 = message.what;
            if (i2 == 1) {
                if (message.arg1 == 0 && this.mCurrentHideModeState == this.mActiveHideModeState) {
                    HideModeState hideModeState = this.mStateList.get(1);
                    this.mCurrentHideModeState = hideModeState;
                    hideModeState.startRun();
                    return;
                }
                return;
            }
            if (i2 == 16) {
                if (message.arg1 <= 0) {
                    if (this.mCurrentHideModeState == this.mActiveHideModeState) {
                        HideModeState hideModeState2 = this.mStateList.get(1);
                        this.mCurrentHideModeState = hideModeState2;
                        hideModeState2.startRun();
                        return;
                    }
                    return;
                }
                this.mCurrentHideModeState.setForegroundActive();
                for (int iIndexOf = this.mStateList.indexOf(this.mCurrentHideModeState) - 1; iIndexOf >= 0; iIndexOf--) {
                    HideModeState hideModeState3 = this.mStateList.get(iIndexOf);
                    this.mCurrentHideModeState = hideModeState3;
                    hideModeState3.setForegroundActive();
                }
                return;
            }
            HideModeState hideModeState4 = null;
            if (i2 == 18) {
                this.mUid = -1;
                this.mInactiveFinalHideModeState = null;
                this.mActiveHideModeState = null;
                this.mCurrentHideModeState = null;
                this.mStateList.clear();
                this.mStateList = null;
                this.mStateMap.clear();
                this.mStateMap = null;
                this.mSubFeatureMap.clear();
                this.mSubFeatureMap = null;
                return;
            }
            if (i2 == 19 && message.arg1 > 0) {
                for (int i3 = 1; i3 < this.mStateList.indexOf(this.mCurrentHideModeState); i3++) {
                    HideModeState hideModeState5 = this.mStateList.get(i3);
                    Iterator<HideModeSubFeature> it = hideModeState5.mSubFeatureList.iterator();
                    while (it.hasNext()) {
                        if (it.next().getSubFeatureWorkPolicy() == 3) {
                            hideModeState4 = hideModeState5;
                            break;
                        }
                    }
                    if (hideModeState4 != null) {
                        break;
                    }
                }
                if (hideModeState4 != null) {
                    this.mCurrentHideModeState.setForegroundActive();
                    for (int iIndexOf2 = this.mStateList.indexOf(this.mCurrentHideModeState) - 1; iIndexOf2 >= this.mStateList.indexOf(hideModeState4); iIndexOf2--) {
                        HideModeState hideModeState6 = this.mStateList.get(iIndexOf2);
                        this.mCurrentHideModeState = hideModeState6;
                        hideModeState6.setForegroundActive();
                    }
                    this.mCurrentHideModeState.startRun();
                }
            }
        }

        public void removeSubFeatureFromState(HideModeState hideModeState, HideModeSubFeature hideModeSubFeature) {
            this.mSubFeatureMap.remove(hideModeSubFeature.getSubFeatureName());
            hideModeState.removeSubFeature(hideModeSubFeature);
            if (this.mStateList.indexOf(hideModeState) == this.mStateList.indexOf(this.mCurrentHideModeState)) {
                this.mCurrentHideModeState.startRun();
            }
        }

        public void setPolicy(String str, Bundle bundle) {
            if (!this.mSubFeatureMap.containsKey(str)) {
                return;
            }
            HideModeSubFeature hideModeSubFeature = this.mSubFeatureMap.get(str);
            if (!hideModeSubFeature.compareAndSetParam(bundle)) {
                return;
            }
            HideModeState hideModeState = hideModeSubFeature.mParentState;
            if (this.mStateList.indexOf(hideModeState) == this.mStateList.indexOf(this.mCurrentHideModeState)) {
                hideModeSubFeature.startRun();
                return;
            }
            if (this.mStateList.indexOf(hideModeState) >= this.mStateList.indexOf(this.mCurrentHideModeState)) {
                return;
            }
            this.mCurrentHideModeState.setActive();
            int iIndexOf = this.mStateList.indexOf(this.mCurrentHideModeState);
            while (true) {
                iIndexOf--;
                if (iIndexOf < this.mStateList.indexOf(hideModeState)) {
                    this.mCurrentHideModeState.startRun();
                    return;
                } else {
                    HideModeState hideModeState2 = this.mStateList.get(iIndexOf);
                    this.mCurrentHideModeState = hideModeState2;
                    hideModeState2.setActive();
                }
            }
        }

        public void stateCompleteCallBack() {
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + this.mUid + " State: " + this.mCurrentHideModeState.getName() + " |-> stateCompleteCallBack");
            }
            transitionToNextState();
        }

        public void transitionToNextState() {
            int iIndexOf = this.mStateList.indexOf(this.mCurrentHideModeState);
            if (iIndexOf < this.mStateList.size() - 1) {
                HideModeState hideModeState = this.mStateList.get(iIndexOf + 1);
                this.mCurrentHideModeState = hideModeState;
                hideModeState.startRun();
            }
        }
    }

    class HideModeState {
        protected String mStateName;
        protected HideModeAppInfo mParentInfo = null;
        protected List<HideModeSubFeature> mSubFeatureList = Lists.newArrayList();

        public HideModeState(String str) {
            this.mStateName = null;
            this.mStateName = str;
        }

        public void addSubFeature(HideModeSubFeature hideModeSubFeature) {
            if (this.mSubFeatureList.contains(hideModeSubFeature)) {
                return;
            }
            this.mSubFeatureList.add(hideModeSubFeature);
            hideModeSubFeature.setParentState(this);
        }

        public void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
            indentingPrintWriter.print(" state=" + this.mStateName);
            Iterator<HideModeSubFeature> it = this.mSubFeatureList.iterator();
            while (it.hasNext()) {
                it.next().dump(indentingPrintWriter, strArr);
            }
        }

        public String getName() {
            return this.mStateName;
        }

        public int getUid() {
            return this.mParentInfo.mUid;
        }

        public void processMessage(Message message) {
            Iterator<HideModeSubFeature> it = this.mSubFeatureList.iterator();
            while (it.hasNext()) {
                it.next().processMessage(message);
            }
            if (message.what != 18) {
                return;
            }
            this.mStateName = null;
            this.mParentInfo = null;
            this.mSubFeatureList.clear();
            this.mSubFeatureList = null;
        }

        public void removeSubFeature(HideModeSubFeature hideModeSubFeature) {
            if (this.mSubFeatureList.contains(hideModeSubFeature)) {
                Message messageObtainMessage = HideModeStateMachine.this.mSmHandler.obtainMessage(18);
                hideModeSubFeature.processMessage(messageObtainMessage);
                messageObtainMessage.recycle();
                this.mSubFeatureList.remove(hideModeSubFeature);
            }
        }

        public void setActive() {
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + this.mStateName + " |-> setActive");
            }
            Iterator<HideModeSubFeature> it = this.mSubFeatureList.iterator();
            while (it.hasNext()) {
                it.next().setSubStateActive();
            }
        }

        public void setForegroundActive() {
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + this.mStateName + " |-> setForegroundActive");
            }
            Iterator<HideModeSubFeature> it = this.mSubFeatureList.iterator();
            while (it.hasNext()) {
                it.next().setSubStateForegroundActive();
            }
        }

        public void setParentInfo(HideModeAppInfo hideModeAppInfo) {
            this.mParentInfo = hideModeAppInfo;
        }

        public void startRun() {
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + this.mStateName + " |-> startRun");
            }
            if (this.mSubFeatureList.size() == 0) {
                this.mParentInfo.stateCompleteCallBack();
                return;
            }
            Iterator<HideModeSubFeature> it = this.mSubFeatureList.iterator();
            while (it.hasNext()) {
                it.next().startRun();
            }
        }

        public void subFeatureRuleChangedCallback(HideModeSubFeature hideModeSubFeature, int i2) {
            if (i2 == 0) {
                return;
            }
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + this.mStateName + " Feature: " + hideModeSubFeature.getSubFeatureName() + " |-> subFeatureRuleChangedCallback, Rule = " + HideModeStateMachine.this.getRuleString(i2));
            }
            Iterator<HideModeSubFeature> it = this.mSubFeatureList.iterator();
            boolean z2 = true;
            while (it.hasNext()) {
                z2 &= it.next().getSubFeatureRule() == 1;
            }
            if (z2) {
                this.mParentInfo.stateCompleteCallBack();
            }
        }
    }

    class HideModeSubFeature {
        private HideModeSubFeatureWorkParam mSubFeatureHotParam;
        private String mSubFeatureName;
        private HideModeSubFeatureWorkParam mSubFeatureNormalParam;
        private boolean mSubFeatureRegardBackground;
        private boolean mSubFeatureRegardForegroundService;
        private boolean mSubFeatureRegardPreTask;
        private HideModeSubFeatureWorkParam mSubFeatureWorkParam;
        private HideModeState mParentState = null;
        private int mSubState = 0;
        private long mSubStateRealTime = 0;
        private int mSubFeatureRule = 0;

        HideModeSubFeature(String str, Bundle bundle) {
            this.mSubFeatureRegardPreTask = false;
            this.mSubFeatureRegardBackground = false;
            this.mSubFeatureRegardForegroundService = false;
            this.mSubFeatureName = str;
            HideModeSubFeatureWorkParam hideModeSubFeatureWorkParam = HideModeStateMachine.this.new HideModeSubFeatureWorkParam();
            this.mSubFeatureNormalParam = hideModeSubFeatureWorkParam;
            this.mSubFeatureWorkParam = hideModeSubFeatureWorkParam;
            this.mSubFeatureHotParam = null;
            if (bundle != null) {
                this.mSubFeatureRegardPreTask = bundle.getBoolean(HideModeStateMachineConfig.KEY_REGARD_PRETASK, false);
                this.mSubFeatureRegardBackground = bundle.getBoolean(HideModeStateMachineConfig.KEY_REGARD_BACKGROUND, false);
                this.mSubFeatureRegardForegroundService = bundle.getBoolean(HideModeStateMachineConfig.KEY_REGARD_FOREGROUND_SERVICE, false);
            }
        }

        private String getStateName() {
            return this.mParentState.mStateName;
        }

        private int getUid() {
            return this.mParentState.getUid();
        }

        private void handleActiveChanged(boolean z2) {
            if (this.mSubFeatureWorkParam.policy != 3 || this.mSubState == 0) {
                return;
            }
            if (z2) {
                this.mSubState = 1;
            } else {
                this.mSubState = 2;
            }
            updateRule();
        }

        private void handleAlarm(long j2) {
            if (this.mSubState == 1) {
                HideModeSubFeatureWorkParam hideModeSubFeatureWorkParam = this.mSubFeatureWorkParam;
                if (hideModeSubFeatureWorkParam.policy != 2 || hideModeSubFeatureWorkParam.delayMillis <= 0 || this.mSubStateRealTime > j2) {
                    return;
                }
                if (HideModeStateMachine.DBG_POWER) {
                    Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> handleAlarm, subState = " + this.mSubState + ", elapsedRealtime = " + j2);
                }
                HideModeStateMachine.this.mSmHandler.removeMessages(HideModeStateMachine.MSG_SUB_STATE_DELAY_TIME, this);
                handleDelayTimeout();
            }
        }

        private void handleDelayTimeout() {
            if (this.mSubState == 1) {
                HideModeSubFeatureWorkParam hideModeSubFeatureWorkParam = this.mSubFeatureWorkParam;
                if (hideModeSubFeatureWorkParam.policy != 2 || hideModeSubFeatureWorkParam.delayMillis <= 0) {
                    return;
                }
                this.mSubState = 2;
                updateRule();
            }
        }

        private void handleForegroundPreTaskChanged(boolean z2) {
            if (this.mSubFeatureRegardPreTask) {
                if (!HideModeStateMachine.this.isScreenOnLocked() || !z2) {
                    startRun();
                    return;
                }
                if (HideModeStateMachine.DBG_POWER) {
                    Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> handleForegroundPreTaskChanged, subState = " + this.mSubState);
                }
                setActive();
            }
        }

        private void handleForegroundServiceChanged(boolean z2) {
            if (this.mSubFeatureRegardForegroundService) {
                if (!z2) {
                    startRun();
                    return;
                }
                if (HideModeStateMachine.DBG_POWER) {
                    Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> handleForegroundServiceChanged, subState = " + this.mSubState);
                }
                setActive();
            }
        }

        private void handleHotChanged(boolean z2) {
            HideModeSubFeatureWorkParam hideModeSubFeatureWorkParam = this.mSubFeatureHotParam;
            if (hideModeSubFeatureWorkParam == null || this.mSubFeatureNormalParam.equals(hideModeSubFeatureWorkParam)) {
                return;
            }
            if (z2 && !this.mSubFeatureWorkParam.equals(this.mSubFeatureHotParam)) {
                this.mSubFeatureWorkParam = this.mSubFeatureHotParam;
                handleParamChanged();
            } else {
                if (z2 || this.mSubFeatureWorkParam.equals(this.mSubFeatureNormalParam)) {
                    return;
                }
                this.mSubFeatureWorkParam = this.mSubFeatureNormalParam;
                handleParamChanged();
            }
        }

        private void handleParamChanged() {
            HideModeStateMachine.this.mSmHandler.removeMessages(HideModeStateMachine.MSG_SUB_STATE_DELAY_TIME, this);
            if (this.mSubFeatureWorkParam.policy == 0) {
                setActive();
                return;
            }
            boolean zIsScreenOnLocked = HideModeStateMachine.this.isScreenOnLocked();
            int uid = getUid();
            int i2 = this.mSubFeatureWorkParam.policy;
            if ((zIsScreenOnLocked && (isUidInteractive(uid) || HideModeStateMachine.this.isUidForegroundLocked(uid))) || isUidFgServiceActive(uid)) {
                setActive();
                return;
            }
            if (i2 == 1) {
                this.mSubState = 2;
            } else if (i2 == 3) {
                if (HideModeStateMachine.this.isUidActiveLocked(uid)) {
                    this.mSubState = 1;
                } else {
                    this.mSubState = 2;
                }
            } else if (i2 == 2 && this.mSubState != 2) {
                long j2 = this.mSubFeatureWorkParam.delayMillis;
                if (j2 > 0) {
                    this.mSubState = 1;
                    this.mSubStateRealTime = SystemClock.elapsedRealtime() + j2;
                    HideModeStateMachine.this.mSmHandler.sendMessageDelayed(HideModeStateMachine.this.mSmHandler.obtainMessage(HideModeStateMachine.MSG_SUB_STATE_DELAY_TIME, this), j2);
                } else if (j2 != 0 && zIsScreenOnLocked) {
                    this.mSubState = 1;
                } else {
                    this.mSubState = 2;
                }
            }
            updateRule();
        }

        private void handleScreenOff() {
            if (this.mSubState == 1) {
                HideModeSubFeatureWorkParam hideModeSubFeatureWorkParam = this.mSubFeatureWorkParam;
                if (hideModeSubFeatureWorkParam.policy != 2 || hideModeSubFeatureWorkParam.delayMillis >= 0) {
                    return;
                }
                if (HideModeStateMachine.DBG_POWER) {
                    Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> handleScreenOff, subState = " + this.mSubState);
                }
                this.mSubState = 2;
            }
        }

        private void handleUidRemoved() {
            HideModeStateMachine.this.mSmHandler.removeMessages(HideModeStateMachine.MSG_SUB_STATE_DELAY_TIME, this);
            this.mSubFeatureName = null;
            this.mParentState = null;
            this.mSubState = 0;
            this.mSubStateRealTime = 0L;
            this.mSubFeatureRule = 0;
            this.mSubFeatureHotParam = null;
            this.mSubFeatureNormalParam = null;
            this.mSubFeatureWorkParam = null;
        }

        private boolean isUidFgServiceActive(int i2) {
            if (this.mSubFeatureRegardForegroundService) {
                return HideModeStateMachine.this.isUidForegroundServiceLocked(i2);
            }
            return false;
        }

        private boolean isUidInteractive(int i2) {
            if (this.mSubFeatureRegardPreTask) {
                return HideModeStateMachine.this.isUidForegroundPreTaskLocked(i2);
            }
            return false;
        }

        private void setActive() {
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> setActive");
            }
            HideModeStateMachine.this.mSmHandler.removeMessages(HideModeStateMachine.MSG_SUB_STATE_DELAY_TIME, this);
            this.mSubStateRealTime = 0L;
            this.mSubState = 0;
            updateRule();
        }

        private void updateRule() {
            updateRule(true, true);
        }

        public boolean compareAndSetParam(Bundle bundle) {
            long j2;
            HideModeSubFeatureWorkParam hideModeSubFeatureWorkParam;
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> compareAndSetParam, " + bundle);
            }
            boolean zCompareAndSetParam = true;
            int i2 = bundle.getInt("POLICY", 1);
            long j3 = -1;
            if (i2 == 2) {
                j2 = bundle.containsKey(HideModeStateMachineConfig.KEY_DELAY_MINUTE) ? bundle.getInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE) * 60000 : 0L;
                if (j2 < 0) {
                    j2 = -1;
                }
            } else {
                j2 = 0;
            }
            boolean zCompareAndSetParam2 = this.mSubFeatureNormalParam.compareAndSetParam(i2, j2);
            if (bundle.containsKey(HideModeStateMachineConfig.KEY_HOT_POLICY)) {
                int i3 = bundle.getInt(HideModeStateMachineConfig.KEY_HOT_POLICY);
                if (i3 == 2) {
                    long j4 = bundle.containsKey(HideModeStateMachineConfig.KEY_HOT_DELAY_MINUTE) ? bundle.getInt(HideModeStateMachineConfig.KEY_HOT_DELAY_MINUTE) * 60000 : 0L;
                    if (j4 >= 0) {
                        j3 = j4;
                    }
                } else {
                    j3 = 0;
                }
                HideModeSubFeatureWorkParam hideModeSubFeatureWorkParam2 = this.mSubFeatureHotParam;
                if (hideModeSubFeatureWorkParam2 == null) {
                    this.mSubFeatureHotParam = HideModeStateMachine.this.new HideModeSubFeatureWorkParam(i3, j3);
                } else {
                    zCompareAndSetParam = zCompareAndSetParam2 | hideModeSubFeatureWorkParam2.compareAndSetParam(i3, j3);
                }
            } else {
                zCompareAndSetParam = this.mSubFeatureHotParam == null ? zCompareAndSetParam2 : true;
                this.mSubFeatureHotParam = null;
            }
            if (!HideModeStateMachine.this.isBatteryHotLocked() || (hideModeSubFeatureWorkParam = this.mSubFeatureHotParam) == null) {
                this.mSubFeatureWorkParam = this.mSubFeatureNormalParam;
            } else {
                this.mSubFeatureWorkParam = hideModeSubFeatureWorkParam;
            }
            return zCompareAndSetParam;
        }

        public void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
            indentingPrintWriter.print("| subFeature=" + this.mSubFeatureName);
            indentingPrintWriter.print(" subState=" + HideModeStateMachine.this.getSubStateString(this.mSubState));
            indentingPrintWriter.print(" rule=" + HideModeStateMachine.this.getRuleString(this.mSubFeatureRule));
        }

        public String getSubFeatureName() {
            return this.mSubFeatureName;
        }

        public int getSubFeatureRule() {
            return this.mSubFeatureRule;
        }

        public int getSubFeatureWorkPolicy() {
            return this.mSubFeatureWorkParam.policy;
        }

        public void processMessage(Message message) {
            int i2 = message.what;
            if (i2 == 1) {
                if (message.arg1 == 0) {
                    handleScreenOff();
                }
                return;
            }
            if (i2 == 2) {
                handleHotChanged(message.arg1 > 0);
                return;
            }
            if (i2 == 3) {
                handleAlarm(((Long) message.obj).longValue());
                return;
            }
            if (i2 == 21) {
                handleForegroundServiceChanged(message.arg1 > 0);
                return;
            }
            if (i2 == HideModeStateMachine.MSG_SUB_STATE_DELAY_TIME) {
                handleDelayTimeout();
                return;
            }
            switch (i2) {
                case 17:
                    handleForegroundPreTaskChanged(message.arg1 > 0);
                    break;
                case 18:
                    handleUidRemoved();
                    break;
                case 19:
                    handleActiveChanged(message.arg1 > 0);
                    break;
            }
        }

        public void setParentState(HideModeState hideModeState) {
            this.mParentState = hideModeState;
        }

        public void setSubStateActive() {
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> setSubStateActive");
            }
            HideModeStateMachine.this.mSmHandler.removeMessages(HideModeStateMachine.MSG_SUB_STATE_DELAY_TIME, this);
            this.mSubStateRealTime = 0L;
            this.mSubState = 0;
            updateRule(false, true);
        }

        public void setSubStateForegroundActive() {
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> setSubStateForegroundActive");
            }
            HideModeStateMachine.this.mSmHandler.removeMessages(HideModeStateMachine.MSG_SUB_STATE_DELAY_TIME, this);
            this.mSubStateRealTime = 0L;
            if (this.mSubFeatureRegardBackground || this.mSubFeatureWorkParam.policy != 1) {
                this.mSubState = 0;
            } else {
                this.mSubState = 2;
            }
            updateRule(false, true);
        }

        public void startRun() {
            HideModeSubFeatureWorkParam hideModeSubFeatureWorkParam;
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> startRun");
            }
            if (!HideModeStateMachine.this.isBatteryHotLocked() || (hideModeSubFeatureWorkParam = this.mSubFeatureHotParam) == null) {
                this.mSubFeatureWorkParam = this.mSubFeatureNormalParam;
            } else {
                this.mSubFeatureWorkParam = hideModeSubFeatureWorkParam;
            }
            this.mSubStateRealTime = 0L;
            this.mSubState = 0;
            handleParamChanged();
        }

        private void updateRule(boolean z2, boolean z3) {
            int i2 = this.mSubState != 2 ? 0 : 1;
            if (i2 != this.mSubFeatureRule) {
                this.mSubFeatureRule = i2;
                if (HideModeStateMachine.DBG_POWER) {
                    Log.i(HideModeStateMachine.TAG, "Uid: " + getUid() + " State: " + getStateName() + " Feature: " + this.mSubFeatureName + " |-> updateRule, Rule = " + HideModeStateMachine.this.getRuleString(i2));
                }
                if (z3) {
                    HideModeStateMachine.this.mSmHandler.obtainMessage(20, getUid(), i2, this.mSubFeatureName).sendToTarget();
                }
                if (z2) {
                    this.mParentState.subFeatureRuleChangedCallback(this, i2);
                }
            }
        }
    }

    class InactiveFinalHideModeState extends HideModeState {
        public InactiveFinalHideModeState(String str) {
            super(str);
        }

        @Override // com.miui.powerkeeper.appcontrol.HideModeStateMachine.HideModeState
        public void startRun() {
            int uid = getUid();
            if (HideModeStateMachine.DBG_POWER) {
                Log.i(HideModeStateMachine.TAG, "Uid: " + uid + " State: " + this.mStateName + " |-> startRun");
            }
        }
    }

    private class SmHandler extends Handler {
        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 > 0 && i2 < HideModeStateMachine.MSG_COMMON_END) {
                if (i2 == 20) {
                    int i3 = message.arg1;
                    int i4 = message.arg2;
                    HideModeStateMachine.this.notifyAppRuleChangedListeners((String) message.obj, i3, i4);
                    return;
                }
                return;
            }
            if (i2 > 256 && i2 < HideModeStateMachine.MSG_STATE_END) {
                ((HideModeState) message.obj).processMessage(message);
            } else {
                if (i2 <= 512 || i2 >= HideModeStateMachine.MSG_SUB_STATE_END) {
                    return;
                }
                ((HideModeSubFeature) message.obj).processMessage(message);
            }
        }

        private SmHandler(Looper looper) {
            super(looper);
        }
    }

    public HideModeStateMachine(Context context, PowerKeeperManager powerKeeperManager) {
        this.mContext = context;
        this.mPowerKeeperManager = powerKeeperManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disable() {
        if (this.mEnabled) {
            synchronized (this.mLock) {
                try {
                    if (this.mEnabled) {
                        Log.i(TAG, "disable");
                        this.mEnabled = false;
                        this.mUidAppInfo.clear();
                        FeedBackManager.getInstance(this.mContext).unregisterFeedBackListener(this.mFeedBackCallback);
                        this.mPowerKeeperManager.getUserCheckManager().K(this.mUserStatusChangedCallback);
                        this.mAppActiveChecker.L(this.mAppActiveChangedCallback);
                        this.mPowerKeeperManager.unregisterAlarmListener(this.mAlarmHappenCallback);
                        this.mPowerKeeperManager.unregisterForegroundActivitiesChangedListener(this.mForegroundActivitiesChangedCallback);
                        this.mPowerKeeperManager.getProcessObserver().unregisterForegroundPreTaskChangedListener(this.mForegroundPreTaskChangedCallback);
                        this.mPowerKeeperManager.getProcessObserver().unregisterIForegroundServiceChangedRegister(this.mForegroundServiceChangedCallback);
                        this.mPowerKeeperManager.unregisterScreenChangedListener(this.mScreenChangedCallback);
                        this.mPowerKeeperManager.unregisterPackageChangedListener(this.mPackageManagerCallback);
                        this.mPackageManagerCallback.a();
                        this.mPackageManagerCallback = null;
                        this.mScreenChangedCallback.a();
                        this.mScreenChangedCallback = null;
                        this.mForegroundActivitiesChangedCallback.a();
                        this.mForegroundActivitiesChangedCallback = null;
                        this.mForegroundPreTaskChangedCallback.a();
                        this.mForegroundPreTaskChangedCallback = null;
                        this.mAlarmHappenCallback.a();
                        this.mAlarmHappenCallback = null;
                        this.mAppActiveChangedCallback.a();
                        this.mAppActiveChangedCallback = null;
                        this.mUserStatusChangedCallback.a();
                        this.mUserStatusChangedCallback = null;
                        this.mFeedBackCallback.a();
                        this.mFeedBackCallback = null;
                        this.mAppActiveChecker = null;
                        this.mSmHandler.removeCallbacksAndMessages(null);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enable() {
        if (this.mEnabled) {
            return;
        }
        synchronized (this.mLock) {
            try {
                if (this.mEnabled) {
                    return;
                }
                Log.i(TAG, "enable");
                this.mEnabled = true;
                this.mUidAppInfo.clear();
                this.mAppActiveChecker = this.mPowerKeeperManager.getAppActiveChecker();
                updateForScreenChangedLocked();
                updateForBatteryHeatChangedLocked(FeedBackManager.getInstance(this.mContext).isBatteryHeat());
                this.mPackageManagerCallback = new PowerKeeperInterface.d0(this.mSmHandler, this.mPackageReceiver);
                this.mScreenChangedCallback = new PowerKeeperInterface.f0(this.mSmHandler, this.mScreenReceiver);
                this.mForegroundActivitiesChangedCallback = new PowerKeeperInterface.f(this.mSmHandler, this.mProcessObserver);
                this.mForegroundPreTaskChangedCallback = new PowerKeeperInterface.g(this.mSmHandler, this.mForegroundPreTaskObserver);
                this.mForegroundServiceChangedCallback = new PowerKeeperInterface.h(this.mSmHandler, this.mForegroundServiceObserver);
                this.mAlarmHappenCallback = new PowerKeeperInterface.a(this.mSmHandler, this.mAlarmHappenListener);
                this.mAppActiveChangedCallback = new PowerKeeperInterface.b(this.mSmHandler, this.mAppActiveChangedListener);
                this.mUserStatusChangedCallback = new PowerKeeperInterface.g0(this.mSmHandler, this.mUserStatusChangedListener);
                this.mFeedBackCallback = new PowerKeeperInterface.e(this.mSmHandler, this.mFeedBackBatteryListener, null, null);
                this.mPowerKeeperManager.registerPackageChangedListener(this.mPackageManagerCallback);
                this.mPowerKeeperManager.registerScreenChangedListener(this.mScreenChangedCallback);
                this.mPowerKeeperManager.registerForegroundActivitiesChangedListener(this.mForegroundActivitiesChangedCallback);
                this.mPowerKeeperManager.getProcessObserver().registerForegroundPreTaskChangedListener(this.mForegroundPreTaskChangedCallback);
                this.mPowerKeeperManager.getProcessObserver().registerIForegroundServiceChangedRegister(this.mForegroundServiceChangedCallback);
                this.mPowerKeeperManager.registerAlarmListener(this.mAlarmHappenCallback);
                this.mAppActiveChecker.I(this.mAppActiveChangedCallback);
                this.mPowerKeeperManager.getUserCheckManager().J(this.mUserStatusChangedCallback);
                FeedBackManager.getInstance(this.mContext).registerFeedBackListener(this.mFeedBackCallback);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HideModeAppInfo getAndCreateIfNeedAppInfoLocked(int i2) {
        if (this.mUidAppInfo.get(i2) == null) {
            this.mUidAppInfo.put(i2, new HideModeAppInfo(i2));
        }
        return this.mUidAppInfo.get(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HideModeAppInfo getAppInfoLocked(int i2) {
        return this.mUidAppInfo.get(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getRuleString(int i2) {
        return i2 == 0 ? "allow" : "restrict";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getSubStateString(int i2) {
        if (i2 == 0) {
            return AppActiveConfigure.Columns.ACTIVE;
        }
        return i2 == 1 ? "active_to_inactive" : "inactive";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isBatteryHotLocked() {
        return this.mHot;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isScreenOnLocked() {
        return this.mScreenOn;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUidActiveLocked(int i2) {
        return this.mAppActiveChecker.y(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUidForegroundLocked(int i2) {
        return this.mPowerKeeperManager.isUidForeground(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUidForegroundPreTaskLocked(int i2) {
        return this.mPowerKeeperManager.getProcessObserver().isUidForegroundPreTask(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUidForegroundServiceLocked(int i2) {
        return this.mPowerKeeperManager.getProcessObserver().isUidForegroundService(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUidValidForRules(int i2) {
        return UserHandle.isApp(i2);
    }

    private boolean isUidValidForStateMachine(int i2) {
        return isUidValidForRules(i2) && getAppInfoLocked(i2) != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAppRuleChangedListeners(String str, final int i2, final int i3) {
        if (DEBUG) {
            Log.i(TAG, "Uid: " + i2 + " Feature: " + str + " Rule: " + getRuleString(i3) + " |-> notifyAppRuleChangedListeners");
        }
        synchronized (this.mAppRuleChangedCallbacks) {
            try {
                if (this.mAppRuleChangedCallbacks.containsKey(str)) {
                    for (final PowerKeeperInterface.c cVar : this.mAppRuleChangedCallbacks.get(str)) {
                        Handler handler = cVar.f249a;
                        if (handler == null) {
                            handler = BackgroundThread.getHandler();
                        }
                        handler.post(new Runnable() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.12
                            @Override // java.lang.Runnable
                            public void run() {
                                PowerKeeperInterface.k kVar = cVar.f250b;
                                if (kVar != null) {
                                    kVar.onAppRuleChange(i2, i3);
                                }
                            }
                        });
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForAlarmHappenedLocked(long j2) {
        Message messageObtainMessage = this.mSmHandler.obtainMessage(3, Long.valueOf(j2));
        int size = this.mUidAppInfo.size();
        for (int i2 = 0; i2 < size; i2++) {
            int iKeyAt = this.mUidAppInfo.keyAt(i2);
            if (isUidValidForRules(iKeyAt)) {
                getAppInfoLocked(iKeyAt).processMessage(messageObtainMessage);
            }
        }
        messageObtainMessage.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForBatteryHeatChangedLocked(boolean z2) {
        this.mHot = z2;
        if (DEBUG) {
            Log.d(TAG, "updateForBatteryHeatChangedLocked, mHot = " + this.mHot);
        }
        Message messageObtainMessage = this.mHot ? this.mSmHandler.obtainMessage(2, 1, 0) : this.mSmHandler.obtainMessage(2, 0, 0);
        int size = this.mUidAppInfo.size();
        for (int i2 = 0; i2 < size; i2++) {
            int iKeyAt = this.mUidAppInfo.keyAt(i2);
            if (isUidValidForRules(iKeyAt)) {
                getAppInfoLocked(iKeyAt).processMessage(messageObtainMessage);
            }
        }
        messageObtainMessage.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForForegroundPreTaskUidChangedLocked(int i2, boolean z2) {
        if (DEBUG) {
            Log.d(TAG, "updateForForegroundPreTaskUidChangedLocked: uid = " + i2 + " foreground = " + z2);
        }
        if (isUidValidForStateMachine(i2)) {
            HideModeAppInfo appInfoLocked = getAppInfoLocked(i2);
            if (this.mScreenOn) {
                Message messageObtainMessage = z2 ? this.mSmHandler.obtainMessage(17, 1, 0) : this.mSmHandler.obtainMessage(17, 0, 0);
                appInfoLocked.processMessage(messageObtainMessage);
                messageObtainMessage.recycle();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForForegroundServiceUidChangedLocked(int i2, boolean z2) {
        if (DEBUG) {
            Log.d(TAG, "updateForForegroundServiceUidChangedLocked: uid = " + i2 + " foreground = " + z2);
        }
        if (isUidValidForStateMachine(i2)) {
            HideModeAppInfo appInfoLocked = getAppInfoLocked(i2);
            Message messageObtainMessage = z2 ? this.mSmHandler.obtainMessage(21, 1, 0) : this.mSmHandler.obtainMessage(21, 0, 0);
            appInfoLocked.processMessage(messageObtainMessage);
            messageObtainMessage.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForForegroundUidChangedLocked(int i2, boolean z2) {
        if (DEBUG) {
            Log.d(TAG, "updateForForegroundUidChangedLocked: uid = " + i2 + " foreground = " + z2);
        }
        if (isUidValidForStateMachine(i2)) {
            HideModeAppInfo appInfoLocked = getAppInfoLocked(i2);
            if (this.mScreenOn) {
                Message messageObtainMessage = z2 ? this.mSmHandler.obtainMessage(16, 1, 0) : this.mSmHandler.obtainMessage(16, 0, 0);
                appInfoLocked.processMessage(messageObtainMessage);
                messageObtainMessage.recycle();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForScreenChangedLocked() {
        this.mScreenOn = this.mPowerKeeperManager.isScreenOn();
        if (DEBUG) {
            Log.d(TAG, "updateForScreenChangedLocked, mScreenOn = " + this.mScreenOn);
        }
        Message messageObtainMessage = this.mSmHandler.obtainMessage(1, 1, 0);
        Message messageObtainMessage2 = this.mSmHandler.obtainMessage(1, 0, 0);
        Message messageObtainMessage3 = this.mSmHandler.obtainMessage(16, 1, 0);
        int size = this.mUidAppInfo.size();
        for (int i2 = 0; i2 < size; i2++) {
            int iKeyAt = this.mUidAppInfo.keyAt(i2);
            if (isUidValidForRules(iKeyAt)) {
                HideModeAppInfo appInfoLocked = getAppInfoLocked(iKeyAt);
                if (this.mScreenOn) {
                    appInfoLocked.processMessage(messageObtainMessage);
                    if (isUidForegroundLocked(iKeyAt)) {
                        appInfoLocked.processMessage(messageObtainMessage3);
                    }
                } else {
                    appInfoLocked.processMessage(messageObtainMessage2);
                }
            }
        }
        messageObtainMessage.recycle();
        messageObtainMessage2.recycle();
        messageObtainMessage3.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForUidActiveChangedLocked(int i2, boolean z2) {
        if (isUidValidForStateMachine(i2)) {
            HideModeAppInfo appInfoLocked = getAppInfoLocked(i2);
            Message messageObtainMessage = z2 ? this.mSmHandler.obtainMessage(19, 1, 0) : this.mSmHandler.obtainMessage(19, 0, 0);
            appInfoLocked.processMessage(messageObtainMessage);
            messageObtainMessage.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForUidRemovedLocked(int i2) {
        if (DEBUG) {
            Log.v(TAG, "updateForUidRemovedLocked, uid=" + i2);
        }
        if (isUidValidForStateMachine(i2)) {
            HideModeAppInfo appInfoLocked = getAppInfoLocked(i2);
            Message messageObtainMessage = this.mSmHandler.obtainMessage(18);
            appInfoLocked.processMessage(messageObtainMessage);
            messageObtainMessage.recycle();
            this.mUidAppInfo.remove(i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForUserRemovedLocked(int i2) {
        if (DEBUG) {
            Log.v(TAG, "updateForUserRemovedLocked, userId=" + i2);
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        int size = this.mUidAppInfo.size();
        for (int i3 = 0; i3 < size; i3++) {
            if (UserHandle.getUserId(this.mUidAppInfo.keyAt(i3)) == i2) {
                sparseBooleanArray.put(this.mUidAppInfo.keyAt(i3), true);
            }
        }
        for (int i4 = 0; i4 < sparseBooleanArray.size(); i4++) {
            updateForUidRemovedLocked(sparseBooleanArray.keyAt(i4));
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.mLock) {
            try {
                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
                indentingPrintWriter.println("#######dump Hide Mode StateMachine#######");
                int size = this.mUidAppInfo.size();
                for (int i2 = 0; i2 < size; i2++) {
                    getAppInfoLocked(this.mUidAppInfo.keyAt(i2)).dump(indentingPrintWriter, strArr);
                    indentingPrintWriter.println();
                }
                indentingPrintWriter.println("end####dump Hide Mode StateMachine####end");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public int getUidRule(String str, int i2) {
        synchronized (this.mLock) {
            try {
                HideModeAppInfo appInfoLocked = getAppInfoLocked(i2);
                if (appInfoLocked == null) {
                    return 0;
                }
                return appInfoLocked.getRule(str);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public SparseIntArray getUidsRule(String str, int[] iArr) {
        SparseIntArray sparseIntArray;
        synchronized (this.mLock) {
            try {
                sparseIntArray = new SparseIntArray(iArr.length);
                for (int i2 : iArr) {
                    sparseIntArray.put(i2, getUidRule(str, i2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return sparseIntArray;
    }

    public void registerAppRuleChangeListener(String str, PowerKeeperInterface.c cVar) {
        synchronized (this.mAppRuleChangedCallbacks) {
            try {
                if (this.mAppRuleChangedCallbacks.containsKey(str)) {
                    List<PowerKeeperInterface.c> list = this.mAppRuleChangedCallbacks.get(str);
                    if (!list.contains(cVar)) {
                        list.add(cVar);
                    }
                } else {
                    ArrayList arrayListNewArrayList = Lists.newArrayList();
                    this.mAppRuleChangedCallbacks.put(str, arrayListNewArrayList);
                    arrayListNewArrayList.add(cVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void setFeatureEnable(final String str, final boolean z2) {
        synchronized (this.mLock) {
            this.mSmHandler.post(new Runnable() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (HideModeStateMachine.this.mLock) {
                        try {
                            Map<String, HideModeStateMachineConfig.FeatureConfig> featureConfigMap = HideModeStateMachineConfig.getInstance().getFeatureConfigMap();
                            if (featureConfigMap.containsKey(str)) {
                                if (featureConfigMap.get(str).mEnabled == z2) {
                                    return;
                                }
                                HideModeStateMachineConfig.FeatureConfig featureConfig = featureConfigMap.get(str);
                                boolean z3 = z2;
                                featureConfig.mEnabled = z3;
                                if (z3) {
                                    HideModeStateMachine.this.enable();
                                }
                                if (!z2) {
                                    Iterator<String> it = featureConfigMap.keySet().iterator();
                                    boolean z4 = false;
                                    while (it.hasNext()) {
                                        z4 |= featureConfigMap.get(it.next()).mEnabled;
                                    }
                                    if (!z4) {
                                        HideModeStateMachine.this.disable();
                                        return;
                                    }
                                }
                                if (!HideModeStateMachine.this.mEnabled) {
                                    Log.e(HideModeStateMachine.TAG, "still disabled");
                                    return;
                                }
                                int size = HideModeStateMachine.this.mUidAppInfo.size();
                                for (int i2 = 0; i2 < size; i2++) {
                                    int iKeyAt = HideModeStateMachine.this.mUidAppInfo.keyAt(i2);
                                    if (HideModeStateMachine.this.isUidValidForRules(iKeyAt)) {
                                        HideModeStateMachine.this.getAppInfoLocked(iKeyAt).enableSubFeature(str, z2);
                                    }
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

    public void setUidPolicy(final String str, final int i2, final Bundle bundle) {
        synchronized (this.mLock) {
            this.mSmHandler.post(new Runnable() { // from class: com.miui.powerkeeper.appcontrol.HideModeStateMachine.2
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (HideModeStateMachine.this.mLock) {
                        try {
                            if (HideModeStateMachine.this.mEnabled) {
                                HideModeStateMachine.this.getAndCreateIfNeedAppInfoLocked(i2).setPolicy(str, bundle);
                            } else {
                                if (HideModeStateMachine.DEBUG) {
                                    Log.v(HideModeStateMachine.TAG, "still disabled");
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

    public void unregisterAppRuleChangeListener(String str, PowerKeeperInterface.c cVar) {
        synchronized (this.mAppRuleChangedCallbacks) {
            try {
                if (this.mAppRuleChangedCallbacks.containsKey(str)) {
                    List<PowerKeeperInterface.c> list = this.mAppRuleChangedCallbacks.get(str);
                    if (list.contains(cVar)) {
                        list.remove(cVar);
                    }
                    if (list.size() == 0) {
                        this.mAppRuleChangedCallbacks.remove(str);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    class HideModeSubFeatureWorkParam {
        long delayMillis;
        int policy;

        HideModeSubFeatureWorkParam() {
            setParam(0, 0L);
        }

        boolean compareAndSetParam(int i2, long j2) {
            boolean z2;
            if (this.policy != i2) {
                this.policy = i2;
                z2 = true;
            } else {
                z2 = false;
            }
            if (this.delayMillis == j2) {
                return z2;
            }
            this.delayMillis = j2;
            return true;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof HideModeSubFeatureWorkParam)) {
                return false;
            }
            HideModeSubFeatureWorkParam hideModeSubFeatureWorkParam = (HideModeSubFeatureWorkParam) obj;
            return this.policy == hideModeSubFeatureWorkParam.policy && this.delayMillis == hideModeSubFeatureWorkParam.delayMillis;
        }

        HideModeSubFeatureWorkParam getParam() {
            return HideModeStateMachine.this.new HideModeSubFeatureWorkParam(this.policy, this.delayMillis);
        }

        void setParam(int i2, long j2) {
            this.policy = i2;
            this.delayMillis = j2;
        }

        HideModeSubFeatureWorkParam(int i2, long j2) {
            setParam(i2, j2);
        }
    }
}
