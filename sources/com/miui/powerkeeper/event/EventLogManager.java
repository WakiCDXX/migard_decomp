package com.miui.powerkeeper.event;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import b.a;
import b.b;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.TimeSpan;
import com.miui.powerkeeper.batterylife.BatteryLifeChecker;
import com.miui.powerkeeper.batterylife.ProcAudioPowerManager;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.cloudcontrol.CloudUpdateHideMode;
import com.miui.powerkeeper.perfengine.PerfEngineManager;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.statemachine.PowerStateMachineProxy;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.Utils;
import com.miui.whetstone.IAudioResourceCallBack;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import e.e;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class EventLogManager implements TimeSpan.a, CloudFunctionConfig.ICloudFunctionListener {
    public static final int AM_RESTART_ACTIVITY_AI = 30090;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final int DISABLE_FLAG_HIDE_IN_FULLSCREEN = 256;
    private static final String SCREEN_OFF_CLEAN_APP_TIMER_ACTION = "com.miui.powerkeeper.clean_app";
    private static final String SCREEN_OFF_FORCE_IDLE_TIMER_ACTION = "com.miui.powerkeeper.force_idle";
    private static final String SCREEN_OFF_NIGHT_CLEAN_TIMER_ACTION = "com.miui.powerkeeper.night_clean";
    private static final long SECOND_TO_MILLISECOND = 1000;
    private static final String TAG = "PowerKeeper.Event";
    private AlarmManager mAlarmManager;
    private int mCleanAppDelaySecond;
    private a mCloudObserver;
    private Context mContext;
    private int mForceIdleDelaySecond;
    private boolean mIsAudioNotifyFunctionOn;
    private boolean mIsCleanAppFunctionOn;
    private boolean mIsForceIdleFunctionOn;
    private boolean mIsNightCleanFunctionOn;
    private int mNightCleanDelaySecond;
    private PowerKeeperManager mPowerManager;
    private PowerStateMachineProxy mPowerStateMachineProxy;
    private long mPtr;
    private IWhetstoneActivityManager mWs;
    private String mForegroundPackageName = null;
    private boolean mScreenOn = false;
    private boolean mFullScreen = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.event.EventLogManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (EventLogManager.DEBUG) {
                Log.d(EventLogManager.TAG, "receive: " + action);
            }
            if (EventLogManager.SCREEN_OFF_FORCE_IDLE_TIMER_ACTION.equals(action)) {
                EventLogManager.this.mPowerStateMachineProxy.sendEvent(13, null);
            } else if (EventLogManager.SCREEN_OFF_CLEAN_APP_TIMER_ACTION.equals(action)) {
                EventLogManager.this.mPowerStateMachineProxy.sendEvent(14, null);
            } else if (EventLogManager.SCREEN_OFF_NIGHT_CLEAN_TIMER_ACTION.equals(action)) {
                EventLogManager.this.mPowerStateMachineProxy.sendEvent(15, null);
            }
        }
    };
    private IAudioResourceCallBack mAudioResourceCallBack = new IAudioResourceCallBack.Stub() { // from class: com.miui.powerkeeper.event.EventLogManager.5
        public void noteMuteAudioInNeed(int i2, int i3, int i4, int i5) {
            Log.i(EventLogManager.TAG, "noteMuteAudioInNeed uid is " + i2 + ", sessionid is " + i4 + ", status is " + i5);
            EventLogManager.this.mPowerManager.getActiveController().onDisguiseChecked(i3, i2, i4, i5);
        }

        public void noteStartAudioInNeed(int i2, int i3, int i4, int i5, int i6) {
            ProcAudioPowerManager procAudioPowerManager;
            Log.i(EventLogManager.TAG, "noteStartAudioInNeed uid is " + i2 + ", sessionid is " + i4 + ", port is " + i5 + ", type is " + i6);
            if (1 == i6) {
                EventLogManager.this.mPowerManager.getActiveController().onRecordStatusChanged(i3, i2, 1);
                return;
            }
            EventLogManager.this.mPowerManager.getActiveController().onAudioStatusChanged(i3, i2, 1, i4);
            BatteryLifeChecker batteryLifeChecker = BatteryLifeChecker.getInstance();
            if (batteryLifeChecker == null || (procAudioPowerManager = batteryLifeChecker.getProcAudioPowerManager()) == null) {
                return;
            }
            procAudioPowerManager.noteStartAudioInNeed(i2, i3, i4, i5, i6);
        }

        public void noteStopAudioInNeed(int i2, int i3, int i4, int i5, int i6) {
            ProcAudioPowerManager procAudioPowerManager;
            Log.i(EventLogManager.TAG, "noteStopAudioInNeed uid is " + i2 + ", sessionid is " + i4 + ", port is " + i5 + ", type is " + i6);
            if (1 == i6) {
                EventLogManager.this.mPowerManager.getActiveController().onRecordStatusChanged(i3, i2, 2);
                return;
            }
            EventLogManager.this.mPowerManager.getActiveController().onAudioStatusChanged(i3, i2, 2, i4);
            BatteryLifeChecker batteryLifeChecker = BatteryLifeChecker.getInstance();
            if (batteryLifeChecker == null || (procAudioPowerManager = batteryLifeChecker.getProcAudioPowerManager()) == null) {
                return;
            }
            procAudioPowerManager.noteStopAudioInNeed(i2, i3, i4, i5, i6);
        }
    };

    public EventLogManager(PowerKeeperManager powerKeeperManager, Context context) {
        this.mPtr = 0L;
        this.mPowerManager = powerKeeperManager;
        this.mPowerStateMachineProxy = powerKeeperManager.getPowerStateMachineProxy();
        this.mContext = context;
        if (PowerKeeperManager.JNI_LOADED) {
            long jNativeInit = nativeInit(this);
            this.mPtr = jNativeInit;
            nativeStart(jNativeInit);
        }
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        IntentFilter intentFilter = new IntentFilter(SCREEN_OFF_FORCE_IDLE_TIMER_ACTION);
        intentFilter.addAction(SCREEN_OFF_CLEAN_APP_TIMER_ACTION);
        intentFilter.addAction(SCREEN_OFF_NIGHT_CLEAN_TIMER_ACTION);
        this.mContext.registerReceiver(this.mReceiver, intentFilter, 2);
        powerKeeperManager.getTimeScheduleManager().e(0, this);
        this.mWs = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        this.mCloudObserver = a.p();
        registerCloudObserver();
        onCloudFunctionUpdated();
        CloudFunctionConfig.getInstance().registerCloudFunctionConfig(this);
    }

    private void cancelAlarm(String str) {
        try {
            this.mAlarmManager.cancel(PendingIntent.getBroadcast(this.mContext, 0, new Intent(str), 201326592));
        } catch (Exception e2) {
            Log.e(TAG, "Exception" + e2);
        }
    }

    private void dispatchEvent(EventLog eventLog) {
        String string;
        String string2;
        String string3;
        switch (eventLog.getTag()) {
            case EventLogTags.CPU /* 2721 */:
                notifyCpuStatsUpdate();
                break;
            case EventLogTags.NOTIFICATION_PANEL_REVEALED /* 27500 */:
                PowerKeeperManager powerKeeperManager = this.mPowerManager;
                if (powerKeeperManager != null) {
                    powerKeeperManager.showNotification(true);
                }
                break;
            case EventLogTags.NOTIFICATION_PANEL_HIDDEN /* 27501 */:
                PowerKeeperManager powerKeeperManager2 = this.mPowerManager;
                if (powerKeeperManager2 != null) {
                    powerKeeperManager2.showNotification(false);
                }
                break;
            case EventLogTags.AM_CREATE_ACTIVITY /* 30005 */:
                String string4 = eventLog.getString(3);
                if (string4 != null) {
                    ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(string4);
                    if (componentNameUnflattenFromString == null || !e.o(4)) {
                        Log.e(TAG, "invliad componentName " + string4);
                    } else if (PerfEngineManager.d() != null) {
                        PerfEngineManager.d().g().g(componentNameUnflattenFromString);
                    }
                } else {
                    Log.e(TAG, "oops, parse AM_CREATE_ACTIVITY parameter error");
                }
                break;
            case EventLogTags.AM_RESTART_ACTIVITY /* 30006 */:
                String string5 = eventLog.getString(3);
                Integer integer = eventLog.getInteger(0);
                Integer integer2 = eventLog.getInteger(0);
                Integer integer3 = eventLog.getInteger(1);
                if (integer == null || string5 == null || integer3 == null) {
                    Log.e(TAG, "oops, parse AM_RESTART_ACTIVITY parameter error");
                    break;
                } else {
                    ComponentName componentNameUnflattenFromString2 = ComponentName.unflattenFromString(string5);
                    if (componentNameUnflattenFromString2 == null) {
                        Log.e(TAG, "invliad componentName " + string5);
                        break;
                    } else {
                        notifyForegroundCompomentChanged(componentNameUnflattenFromString2, integer2);
                        if (e.o(4) && PerfEngineManager.d() != null) {
                            PerfEngineManager.d().g().h(componentNameUnflattenFromString2, integer.intValue(), integer3.intValue());
                            break;
                        }
                    }
                }
                break;
            case EventLogTags.AM_RESUME_ACTIVITY /* 30007 */:
                String string6 = eventLog.getString(3);
                Integer integer4 = eventLog.getInteger(0);
                Integer integer5 = eventLog.getInteger(0);
                Integer integer6 = eventLog.getInteger(1);
                if (integer4 == null || string6 == null || integer6 == null) {
                    Log.e(TAG, "oops, parse AM_RESUME_ACTIVITY parameter error");
                    break;
                } else {
                    ComponentName componentNameUnflattenFromString3 = ComponentName.unflattenFromString(string6);
                    if (componentNameUnflattenFromString3 == null) {
                        Log.e(TAG, "invliad componentName " + string6);
                        break;
                    } else {
                        notifyForegroundCompomentChanged(componentNameUnflattenFromString3, integer5);
                        if (e.o(4) && PerfEngineManager.d() != null) {
                            PerfEngineManager.d().g().i(componentNameUnflattenFromString3, integer4.intValue(), integer6.intValue());
                            break;
                        }
                    }
                }
                break;
            case EventLogTags.AM_ACTIVITY_LAUNCH_TIME /* 30009 */:
                String string7 = eventLog.getString(2);
                Long l2 = eventLog.getLong(3);
                Long l3 = eventLog.getLong(4);
                if (string7 != null && l2 != null && l3 != null) {
                    notifyActivityLaunchTime(string7, l2.longValue(), l3.longValue());
                    break;
                }
                break;
            case EventLogTags.AM_PROC_DIED /* 30011 */:
                Integer integer7 = eventLog.getInteger(1);
                String string8 = eventLog.getString(2);
                if (integer7 != null) {
                    notifyAMProcDied(integer7.intValue(), string8);
                }
                break;
            case EventLogTags.AM_PROC_START /* 30014 */:
                Integer integer8 = eventLog.getInteger(0);
                Integer integer9 = eventLog.getInteger(1);
                Integer integer10 = eventLog.getInteger(2);
                String string9 = eventLog.getString(3);
                String string10 = eventLog.getString(4);
                String string11 = eventLog.getString(5);
                if (integer8 == null || integer9 == null || string10 == null || integer10 == null || string9 == null) {
                    Log.e(TAG, "oops, parse am proc start parameter error");
                } else if (!string10.equalsIgnoreCase("AI")) {
                    notifyAMProcStart(integer8.intValue(), integer9.intValue(), integer10.intValue(), string9, (string11 == null || string11.length() == 0) ? new ComponentName(string9, "defaultClass") : ComponentName.unflattenFromString(string11), string10);
                } else {
                    notifyAMProcStart(integer8.intValue(), integer9.intValue(), integer10.intValue(), string9, ComponentName.unflattenFromString(string9 + "/.AI"), string10);
                }
                break;
            case EventLogTags.AM_KILL /* 30023 */:
                Integer integer11 = eventLog.getInteger(1);
                String string12 = eventLog.getString(2);
                eventLog.getInteger(3);
                String string13 = eventLog.getString(4);
                if (integer11 != null) {
                    if (string13 == null) {
                        string13 = "";
                    }
                    notifyActiveKilled(integer11.intValue(), string12, string13);
                }
                break;
            case EventLogTags.AM_CREATE_SERVICE /* 30030 */:
                if (this.mPowerManager == null) {
                    Log.e(TAG, "AM_CREATE_SERVICE error");
                } else {
                    Integer integer12 = eventLog.getInteger(3);
                    if (integer12 != null) {
                        this.mPowerManager.notifyWakeUpFrozenAppEvent(integer12.intValue());
                    }
                }
                break;
            case EventLogTags.AM_CRASH /* 30039 */:
                Integer integer13 = eventLog.getInteger(0);
                String string14 = eventLog.getString(1);
                if (integer13 == null || string14 == null) {
                    Log.e(TAG, "oops, parse AM_CRASH parameter error");
                } else {
                    notifyAMCrash(integer13.intValue(), string14);
                }
                break;
            case EventLogTags.AM_STOP_ACTIVITY /* 30048 */:
                String string15 = eventLog.getString(2);
                if (string15 != null) {
                    notifyActivityStop(string15);
                }
                break;
            case EventLogTags.AM_ON_RESTART_CALLED /* 30058 */:
                String string16 = eventLog.getString(1);
                String string17 = eventLog.getString(2);
                if (string16 != null && string17 != null) {
                    notifyAMOnRestartCalled(string16, string17);
                    break;
                }
                break;
            case EventLogTags.AM_START_SERVICE /* 30070 */:
                if (this.mPowerManager == null) {
                    Log.e(TAG, "AM_START_SERVICE error");
                } else {
                    Integer integer14 = eventLog.getInteger(2);
                    if (integer14 != null) {
                        this.mPowerManager.notifyWakeUpFrozenAppEvent(integer14.intValue());
                    }
                }
                break;
            case EventLogTags.AM_BIND_SERVICE /* 30071 */:
                if (this.mPowerManager == null) {
                    Log.e(TAG, "AM_BIND_SERVICE error");
                } else {
                    Integer integer15 = eventLog.getInteger(2);
                    if (integer15 != null) {
                        this.mPowerManager.notifyWakeUpFrozenAppEvent(integer15.intValue());
                    }
                }
                break;
            case EventLogTags.AM_GET_PROVIDER /* 30073 */:
                if (this.mPowerManager == null) {
                    Log.e(TAG, "AM_GET_PROVIDER error");
                } else {
                    Integer integer16 = eventLog.getInteger(2);
                    if (integer16 != null) {
                        this.mPowerManager.notifyWakeUpFrozenAppEvent(integer16.intValue());
                    }
                }
                break;
            case EventLogTags.AM_EXECUTING_SERVICE_START /* 30080 */:
                if (this.mPowerManager == null) {
                    Log.e(TAG, "AM_EXECUTING_SERVICE_START error");
                } else {
                    Integer integer17 = eventLog.getInteger(0);
                    if (integer17 != null) {
                        this.mPowerManager.notifyAMExecServiceEvent(integer17.intValue(), true);
                    }
                }
                break;
            case EventLogTags.AM_EXECUTING_SERVICE_END /* 30081 */:
                if (this.mPowerManager == null) {
                    Log.e(TAG, "AM_EXECUTING_SERVICE_END error");
                } else {
                    Integer integer18 = eventLog.getInteger(0);
                    if (integer18 != null) {
                        this.mPowerManager.notifyAMExecServiceEvent(integer18.intValue(), false);
                    }
                }
                break;
            case EventLogTags.EVENT_STATUSBAR_STATE /* 30099 */:
                Integer integer19 = eventLog.getInteger(0);
                if (integer19 == null) {
                    Log.e(TAG, "EVENT_STATUSBAR_STATE error");
                } else {
                    notifyFullScreen(integer19.intValue());
                }
                break;
            case EventLogTags.AUDIO_STATUS_CHANGE /* 30200 */:
                if ((this.mWs == null || !this.mIsAudioNotifyFunctionOn) && (string = eventLog.getString(0)) != null) {
                    String[] strArrSplit = string.split("&");
                    if (strArrSplit != null && strArrSplit.length >= 3) {
                        int i2 = Integer.parseInt(strArrSplit[0]);
                        int i3 = Integer.parseInt(strArrSplit[1]);
                        int i4 = Integer.parseInt(strArrSplit[2]);
                        if (strArrSplit.length == 3 && !this.mPowerManager.getAudioDisguiseController().getNewAudioActiveControl()) {
                            this.mPowerManager.getActiveController().onAudioStatusChanged(i2, i3, i4, 0);
                        } else if (strArrSplit.length > 3) {
                            this.mPowerManager.getActiveController().onAudioStatusChanged(i2, i3, i4, Integer.parseInt(strArrSplit[3]));
                        }
                    } else {
                        Log.e(TAG, "invalid AUDIO_STATUS_CHANGE");
                    }
                }
                break;
            case EventLogTags.RECORD_STATUS_CHANGE /* 30201 */:
                if ((this.mWs == null || !this.mIsAudioNotifyFunctionOn) && (string2 = eventLog.getString(0)) != null) {
                    String[] strArrSplit2 = string2.split("&");
                    if (strArrSplit2 != null && strArrSplit2.length >= 3) {
                        this.mPowerManager.getActiveController().onRecordStatusChanged(Integer.parseInt(strArrSplit2[0]), Integer.parseInt(strArrSplit2[1]), Integer.parseInt(strArrSplit2[2]));
                    } else {
                        Log.e(TAG, "invalid RECORD_STATUS_CHANGE");
                    }
                }
                break;
            case EventLogTags.AUDIO_CONTENT_NULL /* 30203 */:
                if ((this.mWs == null || !this.mIsAudioNotifyFunctionOn) && (string3 = eventLog.getString(0)) != null) {
                    String[] strArrSplit3 = string3.split("&");
                    boolean newAudioActiveControl = this.mPowerManager.getAudioDisguiseController().getNewAudioActiveControl();
                    if (strArrSplit3 != null && ((!newAudioActiveControl || strArrSplit3.length >= 4) && (newAudioActiveControl || strArrSplit3.length >= 2))) {
                        int i5 = Integer.parseInt(strArrSplit3[0]);
                        int i6 = Integer.parseInt(strArrSplit3[1]);
                        if (!newAudioActiveControl) {
                            this.mPowerManager.getActiveController().onDisguiseChecked(i5, i6);
                        } else {
                            int i7 = Integer.parseInt(strArrSplit3[2]);
                            int i8 = Integer.parseInt(strArrSplit3[3]);
                            Log.i(TAG, "AUDIO_CONTENT_NULL pid is " + i5 + ", uid is " + i6 + ", ssionId is " + i7 + ", status is " + i8);
                            this.mPowerManager.getActiveController().onDisguiseChecked(i5, i6, i7, i8);
                        }
                    } else {
                        Log.e(TAG, "invalid AUDIO_CONTENT_NULL");
                    }
                }
                break;
            case EventLogTags.RESOURCE_EXCEPTION_REPORT /* 37266 */:
                Log.e(TAG, "RESOURCE_EXCEPTION_HAPPEN");
                String string18 = eventLog.getString(0);
                if (string18 != null) {
                    String[] strArrSplit4 = string18.split("&");
                    if (strArrSplit4 != null && strArrSplit4.length >= 3) {
                        Integer.parseInt(strArrSplit4[0]);
                        String str = strArrSplit4[1];
                        String str2 = strArrSplit4[2];
                    } else {
                        Log.e(TAG, "invalid VIDEO_STATUS_CHANGE");
                    }
                }
                break;
            case EventLogTags.SCREEN_TOGGLED /* 70000 */:
                Integer integer20 = eventLog.getInteger(0);
                if (integer20 == null) {
                    Log.e(TAG, "SCREEN_TOGGLED error");
                } else {
                    notifyScreenChange(integer20);
                    notifyScreenToggle(integer20.intValue());
                }
                break;
            case EventLogTags.EVENT_OCTVM_READY /* 1990300 */:
                this.mPowerStateMachineProxy.sendEvent(4013, null);
                break;
        }
    }

    private boolean getKillAppEnable() {
        a aVar = this.mCloudObserver;
        boolean zOptBoolean = false;
        if (aVar != null) {
            int i2 = b.I;
            JSONObject jSONObjectL = aVar.l(i2);
            if (this.mCloudObserver.s(i2) && jSONObjectL != null) {
                zOptBoolean = jSONObjectL.optBoolean("screen_on_witch", false);
                if (DEBUG) {
                    Log.d(TAG, "getKillAppEnable screen_on_witch is " + zOptBoolean);
                }
            }
        }
        return zOptBoolean;
    }

    private boolean getNightKillAppEnable() {
        int i2;
        JSONObject jSONObjectL;
        a aVar = this.mCloudObserver;
        boolean zOptBoolean = false;
        if (aVar != null && (jSONObjectL = aVar.l((i2 = b.I))) != null && this.mCloudObserver.s(i2)) {
            zOptBoolean = jSONObjectL.optBoolean("night_switch", false);
            if (DEBUG) {
                Log.d(TAG, "EventLogManager nightCleanEnable is " + zOptBoolean);
            }
        }
        return zOptBoolean;
    }

    private static native void nativeFinalize(EventLogManager eventLogManager, long j2);

    private static native long nativeInit(EventLogManager eventLogManager);

    private static native void nativeStart(long j2);

    private void notifyAMOnRestartCalled(String str, String str2) {
        this.mPowerStateMachineProxy.sendEvent(102, str + "#" + str2);
    }

    private void notifyAMProcStart(int i2, int i3, int i4, String str, ComponentName componentName, String str2) {
        if (UnionPowerConfig.f657b) {
            l.a.l(i2, i4, i3, str, componentName, str2);
        }
    }

    private void notifyActivityStop(String str) {
        this.mPowerStateMachineProxy.sendEvent(101, str);
    }

    private void notifyCpuStatsUpdate() {
        if (PowerLog.V) {
            PowerLog.v(TAG, "notifyCpuStatsUpdate");
        }
        try {
            if (this.mPowerManager.getPowerCheckerManager() != null) {
                this.mPowerManager.getPowerCheckerManager().triggerPowerChecker();
            }
        } catch (Exception e2) {
            Log.e(TAG, "notifyCpuStatsUpdate: triggerPowerChecker failed.", e2);
        }
    }

    private void notifyForegroundCompomentChanged(ComponentName componentName, Integer num) {
        if (PowerLog.V) {
            PowerLog.v(TAG, "notifyForegroundCompomentChanged " + componentName);
        }
        this.mPowerManager.notifyEvent(100, componentName);
        this.mPowerManager.getEventsAggregator().notifyCurrentActivityChangeListeners(componentName.getClassName());
        this.mPowerManager.getEventsAggregator().notifyForegroundComponentChange(componentName, num);
    }

    private void notifyForegroundPackageChanged(String str) {
        String str2 = this.mForegroundPackageName;
        if (str2 == null || !str2.equals(str)) {
            this.mForegroundPackageName = str;
            if (PowerLog.V) {
                PowerLog.v(TAG, "notifyForegroundPackageChanged " + str);
            }
        }
    }

    private void notifyFullScreen(int i2) {
        boolean z2 = this.mFullScreen;
        if (!z2 && (i2 & 256) != 0) {
            if (PowerLog.V) {
                PowerLog.v(TAG, "notifyEnterFullScreen");
            }
            this.mFullScreen = true;
            this.mPowerStateMachineProxy.sendEvent(1000, null);
            return;
        }
        if (z2 && (i2 & 256) == 0) {
            if (PowerLog.V) {
                PowerLog.v(TAG, "notifyExitFullScreen");
            }
            this.mFullScreen = false;
            this.mPowerStateMachineProxy.sendEvent(1001, null);
        }
    }

    private void notifyScreenChange(Integer num) {
        boolean z2 = SystemProperties.getBoolean("persist.sys.spc.powerkill.newpolicy.enable", false);
        if (PowerLog.V) {
            PowerLog.v(TAG, "notifyScreenChange " + num + ", milletEnable is " + z2);
        }
        if (!this.mScreenOn && num.intValue() == 1) {
            if (this.mIsForceIdleFunctionOn) {
                cancelAlarm(SCREEN_OFF_FORCE_IDLE_TIMER_ACTION);
            }
            if (this.mIsCleanAppFunctionOn) {
                cancelAlarm(SCREEN_OFF_CLEAN_APP_TIMER_ACTION);
            }
            if (this.mIsNightCleanFunctionOn && TimeSpan.c()) {
                cancelAlarm(SCREEN_OFF_NIGHT_CLEAN_TIMER_ACTION);
            }
            this.mScreenOn = true;
            this.mPowerStateMachineProxy.sendEvent(11, null);
            if (SystemProperties.getBoolean("persist.sys.powmillet.enable", false) || SystemProperties.getBoolean("persist.sys.powmillet_stable.enable", false)) {
                return;
            }
            this.mPowerManager.notifyEvent(11, null);
            return;
        }
        if (this.mScreenOn && num.intValue() == 0) {
            this.mScreenOn = false;
            this.mPowerStateMachineProxy.sendEvent(12, null);
            if (!SystemProperties.getBoolean("persist.sys.powmillet.enable", false) && !SystemProperties.getBoolean("persist.sys.powmillet_stable.enable", false)) {
                this.mPowerManager.notifyEvent(12, null);
            }
            if (this.mIsForceIdleFunctionOn) {
                setAlarm(this.mForceIdleDelaySecond, SCREEN_OFF_FORCE_IDLE_TIMER_ACTION);
            }
            if (this.mIsCleanAppFunctionOn && (!z2 || getKillAppEnable())) {
                setAlarm(this.mCleanAppDelaySecond, SCREEN_OFF_CLEAN_APP_TIMER_ACTION);
            }
            if (this.mIsNightCleanFunctionOn && TimeSpan.c()) {
                if (!z2 || getNightKillAppEnable()) {
                    setAlarm(this.mNightCleanDelaySecond, SCREEN_OFF_NIGHT_CLEAN_TIMER_ACTION);
                }
            }
        }
    }

    private void notifyScreenToggle(int i2) {
        if (!e.o(4) || PerfEngineManager.d() == null) {
            return;
        }
        PerfEngineManager.d().g().j(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registAudioResourceCallback() {
        Log.i(TAG, "registAudioResourceCallback");
        try {
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                return;
            }
            iWhetstoneActivityManager.registAudioResourceCallback(this.mAudioResourceCallBack);
        } catch (RemoteException unused) {
            Log.e(TAG, "registAudioResourceCallback work abnormal");
        }
    }

    private void registerCloudObserver() {
        this.mCloudObserver.t(b.f37s, new a.d() { // from class: com.miui.powerkeeper.event.EventLogManager.2
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                if (jSONObject == null) {
                    if (EventLogManager.DEBUG) {
                        Log.d(EventLogManager.TAG, "Cloud-Observer onChange old ID_SCREEN_OFF_CLEAN_APP");
                    }
                    EventLogManager eventLogManager = EventLogManager.this;
                    eventLogManager.mIsCleanAppFunctionOn = SimpleSettings.Misc.getBoolean(eventLogManager.mContext, Constant.FUNC_SCREEN_OFF_CLEAN_APP, false);
                    EventLogManager.this.mCleanAppDelaySecond = Utils.toInt(Utils.getSeparatedStringAt(CloudFunctionConfig.getFunctionParameters2(EventLogManager.this.mContext, Constant.FUNC_SCREEN_OFF_CLEAN_APP, null), ',', 0), 1200);
                    return;
                }
                if (EventLogManager.DEBUG) {
                    Log.d(EventLogManager.TAG, "Cloud-Observer onChange ID_SCREEN_OFF_CLEAN_APP:" + jSONObject.toString());
                }
                EventLogManager.this.mIsCleanAppFunctionOn = jSONObject.optBoolean("fucSwitch", false);
                EventLogManager.this.mCleanAppDelaySecond = Utils.toInt(Utils.getSeparatedStringAt(jSONObject.optString(CloudUpdateHideMode.PARAM2, null), ',', 0), 1200);
            }
        });
        this.mCloudObserver.t(b.f38t, new a.d() { // from class: com.miui.powerkeeper.event.EventLogManager.3
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                if (jSONObject == null) {
                    if (EventLogManager.DEBUG) {
                        Log.d(EventLogManager.TAG, "Cloud-Observer onChange old ID_NIGHT_CLEAN_PROCESS");
                    }
                    EventLogManager eventLogManager = EventLogManager.this;
                    eventLogManager.mIsNightCleanFunctionOn = SimpleSettings.Misc.getBoolean(eventLogManager.mContext, Constant.FUNC_NIGHT_CLEAN_PROCESS, false);
                    EventLogManager.this.mNightCleanDelaySecond = Utils.toInt(CloudFunctionConfig.getFunctionParameters(EventLogManager.this.mContext, Constant.FUNC_NIGHT_CLEAN_PROCESS, null), 1200);
                    return;
                }
                if (EventLogManager.DEBUG) {
                    Log.d(EventLogManager.TAG, "Cloud-Observer onChange ID_NIGHT_CLEAN_PROCESS:" + jSONObject.toString());
                }
                EventLogManager.this.mIsNightCleanFunctionOn = jSONObject.optBoolean("fucSwitch", false);
                EventLogManager.this.mNightCleanDelaySecond = Utils.toInt(jSONObject.optString("param", null), 1200);
            }
        });
        this.mCloudObserver.t(b.M, new a.d() { // from class: com.miui.powerkeeper.event.EventLogManager.4
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                boolean zOptBoolean;
                if (jSONObject != null) {
                    if (EventLogManager.DEBUG) {
                        Log.d(EventLogManager.TAG, "Cloud-Observer onChange ID_EVENT_NOTIFY_CONTROL:" + jSONObject.toString());
                    }
                    zOptBoolean = jSONObject.optBoolean(SimpleSettingKeys.KEY_EVENT_NOTIFY_CONTROL_AUDIO, true);
                } else {
                    if (EventLogManager.DEBUG) {
                        Log.d(EventLogManager.TAG, "Cloud-Observer onChange local ID_EVENT_NOTIFY_CONTROL");
                    }
                    zOptBoolean = SimpleSettings.Misc.getBoolean(EventLogManager.this.mContext, SimpleSettingKeys.KEY_EVENT_NOTIFY_CONTROL_AUDIO_SWITCH, true);
                }
                if (zOptBoolean != EventLogManager.this.mIsAudioNotifyFunctionOn) {
                    if (zOptBoolean) {
                        EventLogManager.this.registAudioResourceCallback();
                    } else {
                        EventLogManager.this.unRegistAudioResourceCallback();
                    }
                    EventLogManager.this.mIsAudioNotifyFunctionOn = zOptBoolean;
                }
            }
        });
    }

    private void setAlarm(int i2, String str) {
        try {
            this.mAlarmManager.setExact(3, SystemClock.elapsedRealtime() + (((long) i2) * 1000), PendingIntent.getBroadcast(this.mContext, 0, new Intent(str), 201326592));
        } catch (Exception e2) {
            Log.e(TAG, "Exception" + e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unRegistAudioResourceCallback() {
        Log.i(TAG, "unRegistAudioResourceCallback");
        try {
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                return;
            }
            iWhetstoneActivityManager.unRegistAudioResourceCallback(this.mAudioResourceCallBack);
        } catch (RemoteException unused) {
            Log.e(TAG, "unRegistAudioResourceCallback work abnormal");
        }
    }

    public boolean filterOut(int i2, int i3) {
        if (i3 == 2721) {
            return false;
        }
        return ((i3 >= 30001 && i3 <= 30081) || i3 == 70000 || i3 == 30099 || i3 == 27500 || i3 == 27501 || i3 >= 1000000 || i3 == 37266) ? false : true;
    }

    protected void finalize() {
        try {
            if (PowerKeeperManager.JNI_LOADED) {
                nativeFinalize(this, this.mPtr);
            }
            super.finalize();
        } catch (Throwable unused) {
        }
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void inTimeSpan() {
        boolean z2 = SystemProperties.getBoolean("persist.sys.spc.powerkill.newpolicy.enable", false);
        if (this.mScreenOn || !this.mIsNightCleanFunctionOn) {
            return;
        }
        if (!z2 || getNightKillAppEnable()) {
            setAlarm(this.mNightCleanDelaySecond, SCREEN_OFF_NIGHT_CLEAN_TIMER_ACTION);
        }
    }

    @Override // com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig.ICloudFunctionListener
    public void onCloudFunctionUpdated() {
        boolean zOptBoolean;
        this.mIsForceIdleFunctionOn = SimpleSettings.Misc.getBoolean(this.mContext, "screen_off_disable_sync", false) || SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, false);
        this.mForceIdleDelaySecond = Utils.toInt(CloudFunctionConfig.getFunctionParameters(this.mContext, "screen_off_disable_sync", null), 1380);
        a aVar = this.mCloudObserver;
        int i2 = b.f37s;
        JSONObject jSONObjectL = aVar.l(i2);
        if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
            if (DEBUG) {
                Log.d(TAG, "new old configured ID_SCREEN_OFF_CLEAN_APP");
            }
            this.mIsCleanAppFunctionOn = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_SCREEN_OFF_CLEAN_APP, false);
            this.mCleanAppDelaySecond = Utils.toInt(Utils.getSeparatedStringAt(CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_SCREEN_OFF_CLEAN_APP, null), ',', 0), 1200);
        } else {
            if (DEBUG) {
                Log.d(TAG, "new cloud configured ID_SCREEN_OFF_CLEAN_APP");
            }
            this.mIsCleanAppFunctionOn = jSONObjectL.optBoolean("fucSwitch", false);
            this.mCleanAppDelaySecond = Utils.toInt(Utils.getSeparatedStringAt(jSONObjectL.optString(CloudUpdateHideMode.PARAM2, null), ',', 0), 1200);
        }
        a aVar2 = this.mCloudObserver;
        int i3 = b.f38t;
        JSONObject jSONObjectL2 = aVar2.l(i3);
        if (!this.mCloudObserver.s(i3) || jSONObjectL2 == null) {
            if (DEBUG) {
                Log.d(TAG, "new old configured ID_NIGHT_CLEAN_PROCESS");
            }
            this.mIsNightCleanFunctionOn = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_NIGHT_CLEAN_PROCESS, false);
            this.mNightCleanDelaySecond = Utils.toInt(CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_NIGHT_CLEAN_PROCESS, null), 1200);
        } else {
            if (DEBUG) {
                Log.d(TAG, "new cloud configured ID_NIGHT_CLEAN_PROCESS");
            }
            this.mIsNightCleanFunctionOn = jSONObjectL2.optBoolean("fucSwitch", false);
            this.mNightCleanDelaySecond = Utils.toInt(jSONObjectL2.optString("param", null), 1200);
        }
        a aVar3 = this.mCloudObserver;
        int i4 = b.M;
        JSONObject jSONObjectL3 = aVar3.l(i4);
        if (!this.mCloudObserver.s(i4) || jSONObjectL3 == null) {
            if (DEBUG) {
                Log.d(TAG, "local configured ID_EVENT_NOTIFY_CONTROL");
            }
            zOptBoolean = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_EVENT_NOTIFY_CONTROL_AUDIO_SWITCH, true);
        } else {
            if (DEBUG) {
                Log.d(TAG, "new cloud configured ID_EVENT_NOTIFY_CONTROL");
            }
            zOptBoolean = jSONObjectL3.optBoolean(SimpleSettingKeys.KEY_EVENT_NOTIFY_CONTROL_AUDIO, true);
        }
        if (zOptBoolean != this.mIsAudioNotifyFunctionOn) {
            if (zOptBoolean) {
                registAudioResourceCallback();
            } else {
                unRegistAudioResourceCallback();
            }
            this.mIsAudioNotifyFunctionOn = zOptBoolean;
        }
        if (DEBUG) {
            Log.d(TAG, "onCloudFunctionUpdated mIsForceIdleFunctionOn" + this.mIsForceIdleFunctionOn + " mForceIdleDelaySecond=" + this.mForceIdleDelaySecond + " mIsCleanAppFunctionOn=" + this.mIsCleanAppFunctionOn + " mCleanAppDelaySecond=" + this.mCleanAppDelaySecond + " mIsNightCleanFunctionOn=" + this.mIsNightCleanFunctionOn + " mNightCleanDelaySecond=" + this.mNightCleanDelaySecond + " mIsAudioNotifyFunctionOn=" + this.mIsAudioNotifyFunctionOn);
        }
    }

    public void onDestory() {
        if (this.mIsAudioNotifyFunctionOn) {
            unRegistAudioResourceCallback();
        }
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void outTimeSpan() {
        cancelAlarm(SCREEN_OFF_NIGHT_CLEAN_TIMER_ACTION);
    }

    public void sendEventLog(String str, int i2) {
        try {
            dispatchEvent(new EventLog(str, i2));
        } catch (Exception e2) {
            Log.e(TAG, "sendEventLog: dispatch event failed.", e2);
        }
    }

    private void notifyAMCrash(int i2, String str) {
    }

    private void notifyAMProcDied(int i2, String str) {
    }

    private void notifyActiveKilled(int i2, String str, String str2) {
    }

    private void notifyActivityLaunchTime(String str, long j2, long j3) {
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
