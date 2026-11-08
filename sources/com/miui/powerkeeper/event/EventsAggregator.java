package com.miui.powerkeeper.event;

import android.annotation.Nullable;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RegistrantList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import android.view.IDockedStackListener;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.provider.GlobalFeatureConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.statemachine.CallStateReceiver;
import com.miui.powerkeeper.utils.CommonAdapter;
import com.miui.powerkeeper.utils.FloatSceneObserver;
import com.miui.powerkeeper.utils.PowerKeyPressObserver;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.ProcessObserver;
import com.miui.powerkeeper.utils.UserPresentObserver;
import com.xiaomi.mirror.IMirrorManager;
import com.xiaomi.mirror.MirrorManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import miui.process.ForegroundInfo;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class EventsAggregator {
    public static final int COMMON_MSG_5_MINS_ALARM_HAPPENED = -13;
    public static final int COMMON_MSG_ACTIVE_STATE_CHANGE = -14;
    public static final int COMMON_MSG_AMBIENT_TEMP_CHANGE = -38;
    public static final int COMMON_MSG_AUDIO_DISGUISE_CHANGE = -27;
    public static final int COMMON_MSG_BATTERY_HEAT_STATE_CAHNGE = -16;
    public static final int COMMON_MSG_BATTERY_LEVEL_CHANGE = -20;
    public static final int COMMON_MSG_BOARD_TEMP_CHANGE = -37;
    public static final int COMMON_MSG_BRIGHT_FREEZE_CHANGED = -24;
    public static final int COMMON_MSG_CALL_STATE_CHANGE = -12;
    public static final int COMMON_MSG_CALL_STATE_OFFHOOK_CHANGE = -36;
    public static final int COMMON_MSG_CHARGING_STATE_CAHNGE = -8;
    public static final int COMMON_MSG_CLOUD_UPDATED = -17;
    public static final int COMMON_MSG_CURRENT_ACTIVITY_CHANGE = -29;
    public static final int COMMON_MSG_FG_COMPONENT_CHANGE = -33;
    public static final int COMMON_MSG_FLASH_STATE_CHANGE = -40;
    public static final int COMMON_MSG_FLASH_TEMP_CHANGE = -39;
    public static final int COMMON_MSG_FLOAT_SCENE_EVENT = -31;
    public static final int COMMON_MSG_FOREGROUND_ACTIVITY_CHANGE = -10;
    public static final int COMMON_MSG_FOREGROUND_INFO_CHANGE = -9;
    public static final int COMMON_MSG_FOREGROUND_PRETASK = -23;
    public static final int COMMON_MSG_FOREGROUND_SERVICE_CHANGE = -11;
    public static final int COMMON_MSG_IECSTATE_BATTERY_THRESHOLD_CHANGE = -35;
    public static final int COMMON_MSG_IECSTATE_CHANGE = -28;
    public static final int COMMON_MSG_IME_CHANGE = -18;
    public static final int COMMON_MSG_INPUT_INFO_CHANGE = -25;
    public static final int COMMON_MSG_PACKAGE_ADDED = -1;
    public static final int COMMON_MSG_PACKAGE_REMOVED = -2;
    public static final int COMMON_MSG_POWER_KEY_PRESSED = -6;
    public static final int COMMON_MSG_POWER_MODE_CHANGED = -22;
    public static final int COMMON_MSG_PROCESS_DIED = -21;
    public static final int COMMON_MSG_SCENARIO_CHANGE = -32;
    public static final int COMMON_MSG_SCREEN_OFF = -5;
    public static final int COMMON_MSG_SCREEN_ON = -4;
    public static final int COMMON_MSG_SLEEP_MODE_STATE_CHANGE = -19;
    public static final int COMMON_MSG_SMART_POWER_SCENARIO_ID_CHANGE = -34;
    public static final int COMMON_MSG_TOUCH_EVENT = -30;
    public static final int COMMON_MSG_UID_REMOVED = -3;
    public static final int COMMON_MSG_USER_PRESENT = -7;
    public static final int COMMON_MSG_USER_STATUS_CAHNGE = -15;
    public static final int MAX_POWER_MODE = 6;
    public static final int MSG_BASE = 0;
    public static final int POWER_MODE_BALANCE = 2;
    public static final int POWER_MODE_DISABLE = 0;
    public static final int POWER_MODE_NORMAL_SAVE = 3;
    public static final int POWER_MODE_PERFORMANCE = 1;
    public static final int POWER_MODE_SLEEP = 5;
    public static final int POWER_MODE_ULTIMATE_SAVE = 4;
    public static final int STATE_4K_VIDEO_START = 1;
    public static final int STATE_8K_VIDEO_START = 2;
    public static final int STATE_VIDEO_END = 3;
    public static final int STATE_VIDEO_START = 4;
    private static final String TAG = "Power.Events";
    private static volatile EventsAggregator sInstance;
    private CallStateReceiver mCallStateReceiver;
    private Context mContext;
    private PowerKeyPressObserver mKeyPressObserver;
    private ProcessObserver mProcessObserver;
    private UserPresentObserver mUserPresentObserver;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final boolean DBG_EVENTS = Log.isLoggable("power.events", 3);
    private boolean mIsFolded = true;
    private final Object mLock = new Object();
    private RegistrantList mScreenOnRegistrants = new RegistrantList();
    private RegistrantList mScreenOffRegistrants = new RegistrantList();
    private RegistrantList mPowerKeyPressedRegistrants = new RegistrantList();
    private RegistrantList mUserPresentRegistrants = new RegistrantList();
    private RegistrantList mChargingRegistrants = new RegistrantList();
    private RegistrantList mDockedStackRegistrants = new RegistrantList();
    private RegistrantList mDisplayFoldRegistrants = new RegistrantList();
    private ArrayList<EventRegistrant> mVideoRecordStateRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mForegroundInfoRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mInputInfoRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mForegroundActivityRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mForegroundServiceRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mPreTaskRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mCallStateRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mPkgExistentStateRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mAlarmHappenRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mActiveRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mUserStatusRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mBatteryHeatRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mPkgAddedRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mPkgRemovedRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mUidRemovedRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mCloudUpdateRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mImeChangedRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mSleepModeStateRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mBatteryLevelRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mProcessDiedRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mPowerModeRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mFreezeRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mAudioDisguiseRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mIECStateRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mScenarioRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mTouchObserverResgistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mCurrentActivityRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mFloatSceneObserverRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mFgComponentObserverRegistrants = new ArrayList<>(4);
    private ArrayList<EventRegistrant> mSmartPowerScenarioIdRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mIECStateBatteryRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mCallStateOffHookRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mBoardTempRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mAmbientTempRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mFlashTempRegistrants = new ArrayList<>(2);
    private ArrayList<EventRegistrant> mFlashStateRegistrants = new ArrayList<>(2);
    private int mPowerMode = -1;
    private WeakReference<ComponentName> mCurrentComponentName = null;
    private int mCurrentUserId = 0;

    public static class EventResult {
        public static final int FLAG_FALSE = 0;
        public static final int FLAG_SPECTIAL = 2;
        public static final int FLAG_TRUE = 1;
        public int mFlag;
        public Object mObj;

        public EventResult(int i2, Object obj) {
            this.mFlag = i2;
            this.mObj = obj;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("{mFlag=");
            sb.append(this.mFlag);
            sb.append(",mObj=");
            sb.append(this.mObj);
            sb.append('}');
            return sb.toString();
        }
    }

    private EventsAggregator(Context context) {
        this.mContext = context;
        this.mUserPresentObserver = new UserPresentObserver(context, this);
        this.mKeyPressObserver = new PowerKeyPressObserver(context, this);
        monitorDockedStack();
        monitorInputInfo();
    }

    public static EventsAggregator getInstance(Context context) {
        if (context != null && sInstance == null) {
            synchronized (EventsAggregator.class) {
                try {
                    if (sInstance == null) {
                        PowerLog.recordKey("init EventsAggregator context=" + context);
                        sInstance = new EventsAggregator(context.getApplicationContext());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private void monitorDockedStack() {
        try {
            CommonAdapter.registerDockedStackListener(new IDockedStackListener.Stub() { // from class: com.miui.powerkeeper.event.EventsAggregator.2
                public void onAdjustedForImeChanged(boolean z2, long j2) throws RemoteException {
                    if (EventsAggregator.DBG_EVENTS) {
                        Slog.d(EventsAggregator.TAG, "onAdjustedForImeChanged adjustedForIme=" + z2 + " animDuration=" + j2);
                    }
                }

                public void onDividerVisibilityChanged(boolean z2) throws RemoteException {
                    if (EventsAggregator.DBG_EVENTS) {
                        Slog.d(EventsAggregator.TAG, "onDividerVisibilityChanged visible=" + z2);
                    }
                }

                public void onDockSideChanged(int i2) throws RemoteException {
                    if (EventsAggregator.DBG_EVENTS) {
                        Slog.d(EventsAggregator.TAG, "onDockSideChanged newDockSide=" + i2);
                    }
                }

                public void onDockedStackExistsChanged(boolean z2) throws RemoteException {
                    if (EventsAggregator.DBG_EVENTS) {
                        Slog.d(EventsAggregator.TAG, "onDockedStackExistsChanged exists=" + z2);
                    }
                    EventsAggregator.this.mDockedStackRegistrants.notifyResult(Boolean.valueOf(z2));
                }

                public void onDockedStackMinimizedChanged(boolean z2, long j2, boolean z3) {
                    if (EventsAggregator.DBG_EVENTS) {
                        Slog.d(EventsAggregator.TAG, "onDockedStackMinimizedChanged minimized=" + z2 + " animDuration=" + j2 + " isHomeStackResizable=" + z3);
                    }
                }
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void monitorInputInfo() {
        MirrorManager.get(this.mContext).regAcceptInputCallback(new IMirrorManager.AcceptInputCallback() { // from class: com.miui.powerkeeper.event.EventsAggregator.1
            public void onAcceptInputStatus(int i2, int i3) {
                EventsAggregator.this.notifyForInputInfo(i2, i3);
            }
        });
    }

    private void removeRegistrants(ArrayList<EventRegistrant> arrayList, Handler handler) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            Handler handler2 = arrayList.get(size).getHandler();
            if (handler2 == null || handler2 == handler) {
                arrayList.remove(size);
            }
        }
    }

    public void dispose() {
        this.mScreenOnRegistrants.removeCleared();
        this.mScreenOnRegistrants = null;
        this.mScreenOffRegistrants.removeCleared();
        this.mScreenOffRegistrants = null;
        this.mPowerKeyPressedRegistrants.removeCleared();
        this.mPowerKeyPressedRegistrants = null;
        this.mUserPresentRegistrants.removeCleared();
        this.mUserPresentRegistrants = null;
    }

    public Context getAppContext() {
        return this.mContext;
    }

    public ComponentName getCurrentComponentName() {
        WeakReference<ComponentName> weakReference = this.mCurrentComponentName;
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }

    public int getCurrentUserId() {
        return this.mCurrentUserId;
    }

    public boolean getFoldedStatus() {
        return this.mIsFolded;
    }

    @Nullable
    public ForegroundInfo getForegroundInfo() {
        ProcessObserver processObserver = this.mProcessObserver;
        if (processObserver != null) {
            return processObserver.getAppInfo();
        }
        Log.e(TAG, "instance of processObserver is null");
        return null;
    }

    public int getPowerMode() {
        if (this.mPowerMode == -1) {
            this.mPowerMode = toPowerMode(GlobalFeatureConfigureHelper.getUserConfigure(this.mContext));
        }
        return this.mPowerMode;
    }

    public boolean isFoldAbleDevice() {
        String[] strArr = {"cetus", "argo", "zizhan", "babylon", "ruyi", "goku"};
        String str = Build.DEVICE;
        if (ArrayUtils.contains(strArr, str)) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("in");
        return ArrayUtils.contains(strArr, sb.toString());
    }

    public boolean isInCall() {
        CallStateReceiver callStateReceiver = this.mCallStateReceiver;
        if (callStateReceiver == null) {
            return false;
        }
        return callStateReceiver.isInCall();
    }

    public boolean isOffHookInCall() {
        CallStateReceiver callStateReceiver = this.mCallStateReceiver;
        if (callStateReceiver == null) {
            return false;
        }
        return callStateReceiver.isOffHookInCall();
    }

    public void notifyActiveListeners(int i2, boolean z2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyActiveListeners uid=" + i2 + " isActive=" + z2);
        }
        synchronized (this.mActiveRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mActiveRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, z2 ? 1 : 0);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyAlarmHappenListeners(long j2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyAlarmHappenListeners elapsedRealtime=" + j2);
        }
        synchronized (this.mAlarmHappenRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mAlarmHappenRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Long.valueOf(j2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyAudioDisguise(int i2, int i3) {
        synchronized (this.mAudioDisguiseRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mAudioDisguiseRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, i3);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyBatteryHeatListeners(boolean z2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyBatteryHeatListeners isHeat=" + z2);
        }
        synchronized (this.mBatteryHeatRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mBatteryHeatRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Boolean.valueOf(z2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyBatteryLevelListeners(int i2, int i3, Object obj) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyBatteryLevelListeners newLevel=" + i2 + " old=" + i3 + " other=" + obj);
        }
        synchronized (this.mBatteryLevelRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mBatteryLevelRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, i3, obj);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyCloudUpdateListeners() {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyCloudUpdateListeners");
        }
        synchronized (this.mCloudUpdateRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mCloudUpdateRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(null);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyCurrentActivityChangeListeners(String str) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyCurrentActivityChangeListeners currentActivity=" + str);
        }
        synchronized (this.mCurrentActivityRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mCurrentActivityRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(str);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyDisplayFoldChanged(boolean z2) {
        this.mIsFolded = z2;
        this.mDisplayFoldRegistrants.notifyResult(Boolean.valueOf(z2));
    }

    public void notifyForAmbientTemperature(int i2) {
        synchronized (this.mAmbientTempRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mAmbientTempRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Integer.valueOf(i2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForBoardTemperature(int i2) {
        synchronized (this.mBoardTempRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mBoardTempRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Integer.valueOf(i2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForCallState(boolean z2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForCallState isInCall=" + z2);
        }
        synchronized (this.mCallStateRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mCallStateRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Boolean.valueOf(z2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForCharging(boolean z2) {
        this.mChargingRegistrants.notifyRegistrants(new AsyncResult((Object) null, Boolean.valueOf(z2), (Throwable) null));
    }

    public void notifyForFlashStateChanged(boolean z2) {
        synchronized (this.mFlashStateRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mFlashStateRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Boolean.valueOf(z2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForFlashTemperature(int i2) {
        synchronized (this.mFlashTempRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mFlashTempRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Integer.valueOf(i2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForFloatSceneEvent(FloatSceneObserver.FloatWindowState floatWindowState) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForFloatSceneStateEvent: state=" + floatWindowState);
        }
        synchronized (this.mFloatSceneObserverRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mFloatSceneObserverRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(floatWindowState);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForForegroundInfo(ForegroundInfo foregroundInfo) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForForegroundInfo appInfo=" + foregroundInfo);
        }
        synchronized (this.mForegroundInfoRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mForegroundInfoRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(foregroundInfo);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForFreeze(Bundle bundle) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForFreeze: " + bundle.getChar("freeze"));
        }
        synchronized (this.mFreezeRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mFreezeRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(bundle);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForInputInfo(int i2, int i3) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForInputInfo appInfo=" + i2);
        }
        synchronized (this.mInputInfoRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mInputInfoRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, i3);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForOffHookCallState(boolean z2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForOffHookCallState isInOffHookCall=" + z2);
        }
        synchronized (this.mCallStateOffHookRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mCallStateOffHookRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Boolean.valueOf(z2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForPkgExistentState(EventResult eventResult) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForPkgExistentState result=" + eventResult);
        }
        synchronized (this.mPkgExistentStateRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mPkgExistentStateRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(eventResult);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForPowerKeyPressed() {
        if (DEBUG) {
            Slog.d(TAG, "power key pressed");
        }
        this.mPowerKeyPressedRegistrants.notifyRegistrants();
    }

    public void notifyForTouchEvent(int i2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForTouchIdleStateChange mode=" + i2);
        }
        synchronized (this.mTouchObserverResgistrants) {
            try {
                Iterator<EventRegistrant> it = this.mTouchObserverResgistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Integer.valueOf(i2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForUserPresent() {
        if (DEBUG) {
            Slog.d(TAG, "user present");
        }
        this.mUserPresentRegistrants.notifyRegistrants();
    }

    public void notifyForVideoRecordState(int i2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForVideoRecordState videoState=" + i2);
        }
        synchronized (this.mVideoRecordStateRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mVideoRecordStateRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Integer.valueOf(i2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForegroundActivityChangeListeners(int i2, boolean z2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForegroundActivityChangeListeners uid=" + i2 + " isForeground=" + z2);
        }
        synchronized (this.mForegroundActivityRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mForegroundActivityRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, z2 ? 1 : 0);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForegroundComponentChange(ComponentName componentName, Integer num) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForegroundComponentChange: name=" + componentName + ", user=" + num);
        }
        this.mCurrentComponentName = new WeakReference<>(componentName);
        this.mCurrentUserId = num.intValue();
        synchronized (this.mFgComponentObserverRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mFgComponentObserverRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(num.intValue(), 0, componentName);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForegroundPreTaskChangeListeners(int i2, boolean z2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForegroundPreTaskChangeListeners uid=" + i2 + " isForeground=" + z2);
        }
        synchronized (this.mPreTaskRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mPreTaskRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, z2 ? 1 : 0);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyForegroundServiceChangeListeners(int i2, boolean z2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyForegroundServiceChangeListeners uid=" + i2 + " isForeground=" + z2);
        }
        synchronized (this.mForegroundServiceRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mForegroundServiceRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, z2 ? 1 : 0);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyIECStateBatteryChanged(Object obj) {
        synchronized (this.mIECStateBatteryRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mIECStateBatteryRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(obj);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyIECStateChanged(int i2) {
        synchronized (this.mIECStateRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mIECStateRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(Integer.valueOf(i2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyImeChangedListeners(int i2, int i3) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyImeChangedListeners uid=" + i3);
        }
        synchronized (this.mImeChangedRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mImeChangedRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, i3);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyPackageAddedListeners(int i2, int i3, String str) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyPackageAddedListeners uid=" + i3 + " pkg=" + str);
        }
        synchronized (this.mPkgAddedRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mPkgAddedRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, i3, str);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyPackageRemovedListeners(int i2, int i3, String str) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyPackageRemovedListeners uid=" + i3 + " pkg=" + str);
        }
        synchronized (this.mPkgRemovedRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mPkgRemovedRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, i3, str);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyPowerModeListeners(String str) {
        this.mPowerMode = toPowerMode(str);
        Log.i(TAG, "notifyPowerModeListeners, newMode:" + str + ", mPowerMode:" + this.mPowerMode);
        StringBuilder sb = new StringBuilder();
        sb.append("enter mode=");
        sb.append(this.mPowerMode);
        PowerLog.recordKey(sb.toString());
        synchronized (this.mPowerModeRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mPowerModeRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(this.mPowerMode, -1);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyProcessDiedListeners(int i2, int i3) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyProcessDiedListeners uid=" + i2 + " pid=" + i3);
        }
        synchronized (this.mProcessDiedRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mProcessDiedRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, i3);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyScenarioChanged(Object obj) {
        synchronized (this.mScenarioRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mScenarioRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(obj);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyScreenStateChanged(boolean z2) {
        if (DEBUG) {
            Slog.d(TAG, "screen on: " + z2);
        }
        if (z2) {
            this.mScreenOnRegistrants.notifyRegistrants();
        } else {
            this.mScreenOffRegistrants.notifyRegistrants();
        }
    }

    public void notifySleepStateListeners(boolean z2, String str) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifySleepStateListeners isEnter=" + z2);
        }
        Log.d("SleepMode", "notifySleepStateListeners isEnter=" + z2 + ", reason=" + str);
        StringBuilder sb = new StringBuilder();
        sb.append("notifySleepStateListeners listenerSize=");
        sb.append(this.mSleepModeStateRegistrants.size());
        Log.d("SleepMode", sb.toString());
        synchronized (this.mSleepModeStateRegistrants) {
            try {
                for (EventRegistrant eventRegistrant : this.mSleepModeStateRegistrants) {
                    Log.d("SleepMode", "notifySleepStateListeners handler=" + eventRegistrant.getHandler());
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(YellowPageContract.Permission.STATE, z2);
                    bundle.putString("reason", str);
                    eventRegistrant.notifyResult(bundle);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifySmartPowerScenarioIdChanged(Object obj) {
        synchronized (this.mSmartPowerScenarioIdRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mSmartPowerScenarioIdRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(obj);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyUidRemovedListeners(int i2, int i3) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyUidRemovedListeners uid=" + i3);
        }
        synchronized (this.mUidRemovedRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mUidRemovedRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, i3);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void notifyUserStatusListeners(int i2, boolean z2) {
        if (DBG_EVENTS) {
            Slog.d(TAG, "notifyUserStatusListeners userId=" + i2 + " enable=" + z2);
        }
        synchronized (this.mUserStatusRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mUserStatusRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, z2 ? 1 : 0);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerActiveListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -14);
        synchronized (this.mActiveRegistrants) {
            removeRegistrants(this.mActiveRegistrants, handler);
            this.mActiveRegistrants.add(eventRegistrant);
        }
    }

    public void registerAlarmHappenListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -13);
        synchronized (this.mAlarmHappenRegistrants) {
            removeRegistrants(this.mAlarmHappenRegistrants, handler);
            this.mAlarmHappenRegistrants.add(eventRegistrant);
        }
    }

    public void registerAudioDisguise(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -27);
        synchronized (this.mAudioDisguiseRegistrants) {
            removeRegistrants(this.mAudioDisguiseRegistrants, handler);
            this.mAudioDisguiseRegistrants.add(eventRegistrant);
        }
    }

    public void registerBatteryHeatListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -16);
        synchronized (this.mBatteryHeatRegistrants) {
            removeRegistrants(this.mBatteryHeatRegistrants, handler);
            this.mBatteryHeatRegistrants.add(eventRegistrant);
        }
    }

    public void registerBatteryLevelListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -20);
        synchronized (this.mBatteryLevelRegistrants) {
            removeRegistrants(this.mBatteryLevelRegistrants, handler);
            this.mBatteryLevelRegistrants.add(eventRegistrant);
        }
    }

    public void registerCloudUpdateListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -17);
        synchronized (this.mCloudUpdateRegistrants) {
            removeRegistrants(this.mCloudUpdateRegistrants, handler);
            this.mCloudUpdateRegistrants.add(eventRegistrant);
        }
    }

    public void registerCurrentActivityChangeListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -29);
        synchronized (this.mCurrentActivityRegistrants) {
            removeRegistrants(this.mCurrentActivityRegistrants, handler);
            this.mCurrentActivityRegistrants.add(eventRegistrant);
        }
    }

    public void registerForAmbientTemperature(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -38);
        synchronized (this.mAmbientTempRegistrants) {
            removeRegistrants(this.mAmbientTempRegistrants, handler);
            this.mAmbientTempRegistrants.add(eventRegistrant);
        }
    }

    public void registerForBoardTemperature(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -37);
        synchronized (this.mBoardTempRegistrants) {
            removeRegistrants(this.mBoardTempRegistrants, handler);
            this.mBoardTempRegistrants.add(eventRegistrant);
        }
    }

    public void registerForCallState(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -12);
        synchronized (this.mCallStateRegistrants) {
            removeRegistrants(this.mCallStateRegistrants, handler);
            this.mCallStateRegistrants.add(eventRegistrant);
        }
    }

    public void registerForCharging(Handler handler) {
        this.mChargingRegistrants.addUnique(handler, -8, (Object) null);
    }

    public void registerForDisplayFold(Handler handler, int i2) {
        if (DBG_EVENTS) {
            Slog.i(TAG, "registerForDisplayFold h = " + handler);
        }
        if (isFoldAbleDevice()) {
            this.mDisplayFoldRegistrants.addUnique(handler, i2, (Object) null);
        }
    }

    public void registerForDockedStack(Handler handler, int i2) {
        if (DBG_EVENTS) {
            Slog.i(TAG, "registerForDockedStack h=" + handler);
        }
        this.mDockedStackRegistrants.addUnique(handler, i2, (Object) null);
    }

    public void registerForFlashStateChanged(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -40);
        synchronized (this.mFlashStateRegistrants) {
            removeRegistrants(this.mFlashStateRegistrants, handler);
            this.mFlashStateRegistrants.add(eventRegistrant);
        }
    }

    public void registerForFlashTemperature(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -39);
        synchronized (this.mFlashTempRegistrants) {
            removeRegistrants(this.mFlashTempRegistrants, handler);
            this.mFlashTempRegistrants.add(eventRegistrant);
        }
    }

    public void registerForFloatSceneEvent(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -31);
        synchronized (this.mFloatSceneObserverRegistrants) {
            removeRegistrants(this.mFloatSceneObserverRegistrants, handler);
            this.mFloatSceneObserverRegistrants.add(eventRegistrant);
        }
    }

    public void registerForForegroundInfo(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -9);
        synchronized (this.mForegroundInfoRegistrants) {
            removeRegistrants(this.mForegroundInfoRegistrants, handler);
            this.mForegroundInfoRegistrants.add(eventRegistrant);
        }
    }

    public void registerForFreezeState(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -24);
        synchronized (this.mFreezeRegistrants) {
            removeRegistrants(this.mFreezeRegistrants, handler);
            this.mFreezeRegistrants.add(eventRegistrant);
        }
    }

    public void registerForInputInfo(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -25);
        synchronized (this.mInputInfoRegistrants) {
            removeRegistrants(this.mInputInfoRegistrants, handler);
            this.mInputInfoRegistrants.add(eventRegistrant);
        }
    }

    public void registerForOffHookCallState(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -36);
        synchronized (this.mCallStateOffHookRegistrants) {
            removeRegistrants(this.mCallStateOffHookRegistrants, handler);
            this.mCallStateOffHookRegistrants.add(eventRegistrant);
        }
    }

    public void registerForPkgExistentState(Handler handler, int i2) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, i2);
        synchronized (this.mPkgExistentStateRegistrants) {
            removeRegistrants(this.mPkgExistentStateRegistrants, handler);
            this.mPkgExistentStateRegistrants.add(eventRegistrant);
        }
    }

    public void registerForPowerKeyPressed(Handler handler, Object obj) {
        synchronized (this.mLock) {
            try {
                if (this.mPowerKeyPressedRegistrants.size() == 0) {
                    this.mKeyPressObserver.start();
                }
                this.mPowerKeyPressedRegistrants.addUnique(handler, -6, obj);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerForScreenOff(Handler handler, Object obj) {
        if (DEBUG) {
            Slog.d(TAG, "registerforScreenOff");
        }
        this.mScreenOffRegistrants.addUnique(handler, -5, obj);
    }

    public void registerForScreenOn(Handler handler, Object obj) {
        this.mScreenOnRegistrants.addUnique(handler, -4, obj);
    }

    public void registerForTouchEvent(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -30);
        synchronized (this.mTouchObserverResgistrants) {
            removeRegistrants(this.mTouchObserverResgistrants, handler);
            this.mTouchObserverResgistrants.add(eventRegistrant);
        }
    }

    public void registerForUserPresent(Handler handler, Object obj) {
        synchronized (this.mLock) {
            try {
                if (this.mUserPresentRegistrants.size() == 0) {
                    this.mUserPresentObserver.start();
                }
                this.mUserPresentRegistrants.addUnique(handler, -7, obj);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerForVideoRecordState(Handler handler, int i2) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, i2);
        synchronized (this.mVideoRecordStateRegistrants) {
            removeRegistrants(this.mVideoRecordStateRegistrants, handler);
            this.mVideoRecordStateRegistrants.add(eventRegistrant);
        }
    }

    public void registerForegroundActivityChangeListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -10);
        synchronized (this.mForegroundActivityRegistrants) {
            removeRegistrants(this.mForegroundActivityRegistrants, handler);
            this.mForegroundActivityRegistrants.add(eventRegistrant);
        }
    }

    public void registerForegroundComponentChange(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -33);
        synchronized (this.mFgComponentObserverRegistrants) {
            removeRegistrants(this.mFgComponentObserverRegistrants, handler);
            this.mFgComponentObserverRegistrants.add(eventRegistrant);
        }
    }

    public void registerForegroundPreTaskChangeListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -11);
        synchronized (this.mPreTaskRegistrants) {
            removeRegistrants(this.mPreTaskRegistrants, handler);
            this.mPreTaskRegistrants.add(eventRegistrant);
        }
    }

    public void registerForegroundServiceChangeListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -11);
        synchronized (this.mForegroundServiceRegistrants) {
            removeRegistrants(this.mForegroundServiceRegistrants, handler);
            this.mForegroundServiceRegistrants.add(eventRegistrant);
        }
    }

    public void registerIECStateBatteryChanged(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -35);
        synchronized (this.mIECStateBatteryRegistrants) {
            removeRegistrants(this.mIECStateBatteryRegistrants, handler);
            this.mIECStateBatteryRegistrants.add(eventRegistrant);
        }
    }

    public void registerIECStateChanged(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -28);
        synchronized (this.mIECStateRegistrants) {
            removeRegistrants(this.mIECStateRegistrants, handler);
            this.mIECStateRegistrants.add(eventRegistrant);
        }
    }

    public void registerImeChangedListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -18);
        synchronized (this.mImeChangedRegistrants) {
            removeRegistrants(this.mImeChangedRegistrants, handler);
            this.mImeChangedRegistrants.add(eventRegistrant);
        }
    }

    public void registerPackageAddedListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -1);
        synchronized (this.mPkgAddedRegistrants) {
            removeRegistrants(this.mPkgAddedRegistrants, handler);
            this.mPkgAddedRegistrants.add(eventRegistrant);
        }
    }

    public void registerPackageRemovedListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -2);
        synchronized (this.mPkgRemovedRegistrants) {
            removeRegistrants(this.mPkgRemovedRegistrants, handler);
            this.mPkgRemovedRegistrants.add(eventRegistrant);
        }
    }

    public void registerPowerModeListener(Handler handler) {
        this.mPowerMode = getPowerMode();
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -22);
        synchronized (this.mPowerModeRegistrants) {
            removeRegistrants(this.mPowerModeRegistrants, handler);
            this.mPowerModeRegistrants.add(eventRegistrant);
            eventRegistrant.notifyResult(this.mPowerMode, -1);
        }
    }

    public void registerProcessDiedListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -21);
        synchronized (this.mProcessDiedRegistrants) {
            removeRegistrants(this.mProcessDiedRegistrants, handler);
            this.mProcessDiedRegistrants.add(eventRegistrant);
        }
    }

    public void registerScenarioListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -32);
        synchronized (this.mScenarioRegistrants) {
            removeRegistrants(this.mScenarioRegistrants, handler);
            this.mScenarioRegistrants.add(eventRegistrant);
        }
    }

    public void registerSleepStateListener(Handler handler) {
        Log.d("SleepMode", "notifySleepStateListeners handler=" + handler);
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -19);
        synchronized (this.mSleepModeStateRegistrants) {
            removeRegistrants(this.mSleepModeStateRegistrants, handler);
            this.mSleepModeStateRegistrants.add(eventRegistrant);
        }
    }

    public void registerSmartPowerScenarioIdChanged(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -34);
        synchronized (this.mSmartPowerScenarioIdRegistrants) {
            removeRegistrants(this.mSmartPowerScenarioIdRegistrants, handler);
            this.mSmartPowerScenarioIdRegistrants.add(eventRegistrant);
        }
    }

    public void registerUidRemovedListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -3);
        synchronized (this.mUidRemovedRegistrants) {
            removeRegistrants(this.mUidRemovedRegistrants, handler);
            this.mUidRemovedRegistrants.add(eventRegistrant);
        }
    }

    public void registerUserStatusListener(Handler handler) {
        EventRegistrant eventRegistrant = new EventRegistrant(handler, -15);
        synchronized (this.mUserStatusRegistrants) {
            removeRegistrants(this.mUserStatusRegistrants, handler);
            this.mUserStatusRegistrants.add(eventRegistrant);
        }
    }

    public void setObserver(Object obj) {
        if (obj instanceof ProcessObserver) {
            this.mProcessObserver = (ProcessObserver) obj;
            return;
        }
        if (obj instanceof CallStateReceiver) {
            this.mCallStateReceiver = (CallStateReceiver) obj;
            return;
        }
        Slog.w(TAG, "setObserver ignore observer=" + obj);
    }

    public int toPowerMode(String str) {
        if (str.equals("performance")) {
            return 1;
        }
        if (str.equals(GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_ULTIMATE)) {
            return 3;
        }
        if (str.equals(GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_ULTIMATE_EXTRA)) {
            return 4;
        }
        if (str.equals(GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_SLEEP)) {
            return 5;
        }
        return str.equals("disable") ? 0 : 2;
    }

    public void unregisterActiveListener(Handler handler) {
        synchronized (this.mActiveRegistrants) {
            removeRegistrants(this.mActiveRegistrants, handler);
        }
    }

    public void unregisterAlarmHappenListener(Handler handler) {
        synchronized (this.mAlarmHappenRegistrants) {
            removeRegistrants(this.mAlarmHappenRegistrants, handler);
        }
    }

    public void unregisterAudioDisguise(Handler handler) {
        synchronized (this.mAudioDisguiseRegistrants) {
            removeRegistrants(this.mAudioDisguiseRegistrants, handler);
        }
    }

    public void unregisterBatteryHeatListener(Handler handler) {
        synchronized (this.mBatteryHeatRegistrants) {
            removeRegistrants(this.mBatteryHeatRegistrants, handler);
        }
    }

    public void unregisterBatteryLevelListener(Handler handler) {
        synchronized (this.mBatteryLevelRegistrants) {
            removeRegistrants(this.mBatteryLevelRegistrants, handler);
        }
    }

    public void unregisterCloudUpdateListener(Handler handler) {
        synchronized (this.mCloudUpdateRegistrants) {
            removeRegistrants(this.mCloudUpdateRegistrants, handler);
        }
    }

    public void unregisterCurrentActivityChangeListener(Handler handler) {
        synchronized (this.mCurrentActivityRegistrants) {
            removeRegistrants(this.mCurrentActivityRegistrants, handler);
        }
    }

    public void unregisterForAmbientTemperature(Handler handler) {
        synchronized (this.mAmbientTempRegistrants) {
            removeRegistrants(this.mAmbientTempRegistrants, handler);
        }
    }

    public void unregisterForBoardTemperature(Handler handler) {
        synchronized (this.mBoardTempRegistrants) {
            removeRegistrants(this.mBoardTempRegistrants, handler);
        }
    }

    public void unregisterForCallState(Handler handler) {
        synchronized (this.mCallStateRegistrants) {
            removeRegistrants(this.mCallStateRegistrants, handler);
        }
    }

    public void unregisterForCharging(Handler handler) {
        this.mChargingRegistrants.remove(handler);
    }

    public void unregisterForDisplayFold(Handler handler) {
        if (DBG_EVENTS) {
            Slog.i(TAG, "unregisterForDisplayFold h = " + handler);
        }
        if (isFoldAbleDevice()) {
            this.mDisplayFoldRegistrants.remove(handler);
        }
    }

    public void unregisterForDockedStack(Handler handler) {
        if (DBG_EVENTS) {
            Slog.i(TAG, "unregisterForDockedStack h=" + handler);
        }
        this.mDockedStackRegistrants.remove(handler);
    }

    public void unregisterForFlashStateChanged(Handler handler) {
        synchronized (this.mFlashStateRegistrants) {
            removeRegistrants(this.mFlashStateRegistrants, handler);
        }
    }

    public void unregisterForFlashTemperature(Handler handler) {
        synchronized (this.mFlashTempRegistrants) {
            removeRegistrants(this.mFlashTempRegistrants, handler);
        }
    }

    public void unregisterForFloatSceneEvent(Handler handler) {
        synchronized (this.mFloatSceneObserverRegistrants) {
            removeRegistrants(this.mFloatSceneObserverRegistrants, handler);
        }
    }

    public void unregisterForForegroundInfo(Handler handler) {
        synchronized (this.mForegroundInfoRegistrants) {
            removeRegistrants(this.mForegroundInfoRegistrants, handler);
        }
    }

    public void unregisterForFreezeState(Handler handler) {
        synchronized (this.mFreezeRegistrants) {
            removeRegistrants(this.mFreezeRegistrants, handler);
        }
    }

    public void unregisterForInputInfo(Handler handler) {
        synchronized (this.mInputInfoRegistrants) {
            removeRegistrants(this.mInputInfoRegistrants, handler);
        }
    }

    public void unregisterForOffHookCallState(Handler handler) {
        synchronized (this.mCallStateOffHookRegistrants) {
            removeRegistrants(this.mCallStateOffHookRegistrants, handler);
        }
    }

    public void unregisterForPkgExistentState(Handler handler) {
        synchronized (this.mPkgExistentStateRegistrants) {
            removeRegistrants(this.mPkgExistentStateRegistrants, handler);
        }
    }

    public void unregisterForPowerKeyPressed(Handler handler) {
        synchronized (this.mLock) {
            try {
                this.mPowerKeyPressedRegistrants.remove(handler);
                if (this.mPowerKeyPressedRegistrants.size() == 0) {
                    this.mKeyPressObserver.stop();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterForScreenOff(Handler handler) {
        if (DEBUG) {
            Slog.d(TAG, "unregisterforScreenOff");
        }
        this.mScreenOffRegistrants.remove(handler);
    }

    public void unregisterForScreenOn(Handler handler) {
        this.mScreenOnRegistrants.remove(handler);
    }

    public void unregisterForTouchEvent(Handler handler) {
        synchronized (this.mTouchObserverResgistrants) {
            removeRegistrants(this.mTouchObserverResgistrants, handler);
        }
    }

    public void unregisterForUserPresent(Handler handler) {
        synchronized (this.mLock) {
            try {
                this.mUserPresentRegistrants.remove(handler);
                if (this.mUserPresentRegistrants.size() == 0) {
                    this.mUserPresentObserver.stop();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterForVideoRecordState(Handler handler) {
        synchronized (this.mVideoRecordStateRegistrants) {
            removeRegistrants(this.mVideoRecordStateRegistrants, handler);
        }
    }

    public void unregisterForegroundActivityChangeListener(Handler handler) {
        synchronized (this.mForegroundActivityRegistrants) {
            removeRegistrants(this.mForegroundActivityRegistrants, handler);
        }
    }

    public void unregisterForegroundComponentChange(Handler handler) {
        synchronized (this.mFgComponentObserverRegistrants) {
            removeRegistrants(this.mFgComponentObserverRegistrants, handler);
        }
    }

    public void unregisterForegroundPreTaskChangeListener(Handler handler) {
        synchronized (this.mPreTaskRegistrants) {
            removeRegistrants(this.mPreTaskRegistrants, handler);
        }
    }

    public void unregisterForegroundServiceChangeListener(Handler handler) {
        synchronized (this.mForegroundServiceRegistrants) {
            removeRegistrants(this.mForegroundServiceRegistrants, handler);
        }
    }

    public void unregisterIECStateBatteryChanged(Handler handler) {
        synchronized (this.mIECStateBatteryRegistrants) {
            removeRegistrants(this.mIECStateBatteryRegistrants, handler);
        }
    }

    public void unregisterIECStateChanged(Handler handler) {
        synchronized (this.mIECStateRegistrants) {
            removeRegistrants(this.mIECStateRegistrants, handler);
        }
    }

    public void unregisterImeChangedListener(Handler handler) {
        synchronized (this.mImeChangedRegistrants) {
            removeRegistrants(this.mImeChangedRegistrants, handler);
        }
    }

    public void unregisterPackageAddedListener(Handler handler) {
        synchronized (this.mPkgAddedRegistrants) {
            removeRegistrants(this.mPkgAddedRegistrants, handler);
        }
    }

    public void unregisterPackageRemovedListener(Handler handler) {
        synchronized (this.mPkgRemovedRegistrants) {
            removeRegistrants(this.mPkgRemovedRegistrants, handler);
        }
    }

    public void unregisterPowerModeListener(Handler handler) {
        synchronized (this.mPowerModeRegistrants) {
            removeRegistrants(this.mPowerModeRegistrants, handler);
        }
    }

    public void unregisterProcessDiedListener(Handler handler) {
        synchronized (this.mProcessDiedRegistrants) {
            removeRegistrants(this.mProcessDiedRegistrants, handler);
        }
    }

    public void unregisterScenarioListener(Handler handler) {
        synchronized (this.mScenarioRegistrants) {
            removeRegistrants(this.mScenarioRegistrants, handler);
        }
    }

    public void unregisterSleepStateListener(Handler handler) {
        synchronized (this.mSleepModeStateRegistrants) {
            removeRegistrants(this.mSleepModeStateRegistrants, handler);
        }
    }

    public void unregisterSmartPowerScenarioIdChanged(Handler handler) {
        synchronized (this.mSmartPowerScenarioIdRegistrants) {
            removeRegistrants(this.mSmartPowerScenarioIdRegistrants, handler);
        }
    }

    public void unregisterUidRemovedListener(Handler handler) {
        synchronized (this.mUidRemovedRegistrants) {
            removeRegistrants(this.mUidRemovedRegistrants, handler);
        }
    }

    public void unregisterUserStatusListener(Handler handler) {
        synchronized (this.mUserStatusRegistrants) {
            removeRegistrants(this.mUserStatusRegistrants, handler);
        }
    }

    class EventRegistrant {
        WeakReference<Handler> refH;
        int what;

        EventRegistrant(Handler handler, int i2) {
            this.refH = new WeakReference<>(handler);
            this.what = i2;
        }

        Handler getHandler() {
            WeakReference<Handler> weakReference = this.refH;
            if (weakReference == null) {
                return null;
            }
            return weakReference.get();
        }

        void notifyResult(Object obj) {
            WeakReference<Handler> weakReference = this.refH;
            if (weakReference != null) {
                Handler handler = weakReference.get();
                if (handler == null) {
                    this.refH = null;
                } else {
                    handler.sendMessageAtTime(Message.obtain(handler, this.what, obj), SystemClock.uptimeMillis());
                }
            }
        }

        void notifyResult(int i2, int i3) {
            WeakReference<Handler> weakReference = this.refH;
            if (weakReference != null) {
                Handler handler = weakReference.get();
                if (handler == null) {
                    this.refH = null;
                } else {
                    handler.sendMessageAtTime(Message.obtain(handler, this.what, i2, i3), SystemClock.uptimeMillis());
                }
            }
        }

        void notifyResult(int i2, int i3, Object obj) {
            WeakReference<Handler> weakReference = this.refH;
            if (weakReference != null) {
                Handler handler = weakReference.get();
                if (handler == null) {
                    this.refH = null;
                } else {
                    handler.sendMessageAtTime(Message.obtain(handler, this.what, i2, i3, obj), SystemClock.uptimeMillis());
                }
            }
        }
    }

    public void notifyAudioDisguise(int i2, int i3, int[] iArr) {
        synchronized (this.mAudioDisguiseRegistrants) {
            try {
                Iterator<EventRegistrant> it = this.mAudioDisguiseRegistrants.iterator();
                while (it.hasNext()) {
                    it.next().notifyResult(i2, i3, iArr);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
