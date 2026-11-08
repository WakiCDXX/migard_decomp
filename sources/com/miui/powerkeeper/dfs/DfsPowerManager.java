package com.miui.powerkeeper.dfs;

import android.annotation.NonNull;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.TimeSpan;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.event.EventsAggregator;
import java.io.PrintWriter;
import java.util.Arrays;
import miui.process.ForegroundInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DfsPowerManager implements TimeSpan.a {
    private static final String ACTION_SCREEN_OFF_TIMEOUT = "action.powerkeeper.dfs.screenoff_timeout";
    public static final int DEFAULT_SCREEN_OFF_TIMEOUT = 180000;
    public static final int MSG_INIT = 0;
    public static final int MSG_TIME_SPAN_CHANGED = 1;
    public static final String TAG = "DFS-DfsPowerManager";
    private volatile boolean bNight;
    private boolean mCharging;
    private final CloudData mCloudData;
    private final Context mContext;
    private final BroadcastReceiver mDfsReceiver;
    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private volatile boolean mReceiverRegistered;
    private final StandbyManager mStandbyManager;
    private final UsageManager mUsageManager;
    private final UsageMonitor mUsageMonitor;
    private boolean mScreenOn = false;
    private volatile int mCurrentScene = 1;
    private volatile boolean mWorking = false;
    private int mScreenOffTimeoutMs = 180000;

    public DfsPowerManager(Context context) {
        HandlerThread handlerThread = new HandlerThread("DFS-Thread");
        this.mHandlerThread = handlerThread;
        this.mDfsReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.dfs.DfsPowerManager.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                Log.d(DfsPowerManager.TAG, "receive " + action);
                if (DfsPowerManager.ACTION_SCREEN_OFF_TIMEOUT.equals(action)) {
                    DfsPowerManager.this.startWorkSb();
                }
            }
        };
        this.mContext = context;
        CloudData cloudData = new CloudData();
        this.mCloudData = cloudData;
        this.mStandbyManager = new StandbyManager(context, cloudData);
        this.mUsageMonitor = UsageMonitor.getInstance(context);
        this.mCharging = PowerKeeperManager.getInstance().isCharging();
        this.bNight = TimeSpan.c();
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper()) { // from class: com.miui.powerkeeper.dfs.DfsPowerManager.2
            @Override // android.os.Handler
            public void handleMessage(@NonNull Message message) {
                DfsPowerManager.this.handleMessage(message);
            }
        };
        this.mHandler = handler;
        handler.sendEmptyMessage(0);
        this.mUsageManager = new UsageManager(context, cloudData, handlerThread.getLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -20) {
            UsageManager usageManager = this.mUsageManager;
            if (usageManager != null) {
                usageManager.notifyBatteryLevelChanged(message.arg1);
                return;
            }
            return;
        }
        if (i2 == -9) {
            UsageManager usageManager2 = this.mUsageManager;
            if (usageManager2 != null) {
                usageManager2.notifyForegroundInfoChanged((ForegroundInfo) message.obj);
                return;
            }
            return;
        }
        if (i2 == -8) {
            this.mCharging = ((Boolean) ((AsyncResult) message.obj).result).booleanValue();
            Log.d(TAG, "charging status changed: isCharging=" + this.mCharging);
            if (this.mCharging) {
                stopWorkSb();
            } else {
                scheduleStartWorkSb();
            }
            UsageManager usageManager3 = this.mUsageManager;
            if (usageManager3 != null) {
                usageManager3.notifyOnBattery(!this.mCharging);
                return;
            }
            return;
        }
        if (i2 == -7) {
            handleMsgUserPresent();
            return;
        }
        if (i2 == -5) {
            handleMsgScreenOff();
            UsageManager usageManager4 = this.mUsageManager;
            if (usageManager4 != null) {
                usageManager4.notifyScreenOff(true);
                return;
            }
            return;
        }
        if (i2 == -4) {
            handleMsgScreenOn();
            UsageManager usageManager5 = this.mUsageManager;
            if (usageManager5 != null) {
                usageManager5.notifyScreenOff(false);
                return;
            }
            return;
        }
        if (i2 == 0) {
            init();
            return;
        }
        if (i2 != 1) {
            return;
        }
        if (!this.bNight) {
            stopWorkSb();
        } else if (!this.mWorking) {
            scheduleStartWorkSb();
        } else {
            stopWorkSb();
            startWorkSb();
        }
    }

    private void handleMsgScreenOff() {
        synchronized (this) {
            this.mScreenOn = false;
        }
        if (this.mWorking) {
            return;
        }
        scheduleStartWorkSb();
    }

    private void handleMsgScreenOn() {
        synchronized (this) {
            this.mScreenOn = true;
        }
    }

    private void handleMsgUserPresent() {
        stopWorkSb();
    }

    private void init() {
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        eventsAggregator.registerForScreenOn(this.mHandler, null);
        eventsAggregator.registerForScreenOff(this.mHandler, null);
        eventsAggregator.registerForCharging(this.mHandler);
        eventsAggregator.registerForUserPresent(this.mHandler, null);
        eventsAggregator.registerForForegroundInfo(this.mHandler);
        if (DfsUtils.VERSION >= DfsUtils.VERSION_NEXT) {
            eventsAggregator.registerBatteryLevelListener(this.mHandler);
        }
        registerCloud();
        this.bNight = TimeSpan.c();
        if (this.bNight) {
            this.mCurrentScene = 2;
        }
        boolean zIsScreenOn = PowerKeeperManager.getInstance().isScreenOn();
        this.mScreenOn = zIsScreenOn;
        UsageManager usageManager = this.mUsageManager;
        if (usageManager != null) {
            usageManager.notifyScreenOff(!zIsScreenOn);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerCloud$0(JSONObject jSONObject) {
        Log.d(TAG, "Cloud onChanged.");
        updateCloudData(jSONObject);
    }

    private void registerCloud() {
        b.a aVarP = b.a.p();
        int i2 = b.b.O;
        updateCloudData(aVarP.l(i2));
        b.a.p().t(i2, new b.a.d() { // from class: com.miui.powerkeeper.dfs.a
            @Override // b.a.d
            public final void onChanged(JSONObject jSONObject) {
                this.f392a.lambda$registerCloud$0(jSONObject);
            }
        });
    }

    private synchronized void registerScreenOffTimeout() {
        if (!this.mReceiverRegistered) {
            Log.d(TAG, "register for ScreenOffTimeout");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_SCREEN_OFF_TIMEOUT);
            this.mContext.registerReceiver(this.mDfsReceiver, intentFilter, 4);
            this.mReceiverRegistered = true;
            setScreenOffTimeoutAlarm();
        }
    }

    private void scheduleStartWorkSb() {
        if (!DfsUtils.isSceneEnabled(this.mCurrentScene)) {
            Log.d(TAG, "The scene is not enabled. mCurrentScene=" + this.mCurrentScene);
            return;
        }
        if (!this.mScreenOn && !this.mWorking && !this.mReceiverRegistered && !this.mCharging) {
            if (DfsUtils.DEBUG) {
                startWorkSb();
                return;
            } else {
                registerScreenOffTimeout();
                return;
            }
        }
        Log.d(TAG, "scheduleStartWorkSb failed. mScreenOn=" + this.mScreenOn + ", mWorking=" + this.mWorking + ", mCharging=" + this.mCharging + ", mReceiverRegistered=" + this.mReceiverRegistered);
    }

    private void setScreenOffTimeoutAlarm() {
        ((AlarmManager) this.mContext.getSystemService("alarm")).setExact(3, SystemClock.elapsedRealtime() + ((long) this.mScreenOffTimeoutMs), PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_SCREEN_OFF_TIMEOUT), 67108864));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWorkSb() {
        synchronized (this) {
            try {
                if (!this.mScreenOn && !this.mWorking && !this.mCharging) {
                    if (BSHelper.getCurrentBatteryCap() == -1.0d) {
                        Slog.w(TAG, "startWork failed: Can't get the charge_counter. Please retry later.");
                        return;
                    } else {
                        this.mWorking = this.mStandbyManager.startWork(this.mCurrentScene);
                        return;
                    }
                }
                Log.d(TAG, "startWorkSb failed. mScreenOn=" + this.mScreenOn + ", mWorking=" + this.mWorking + ", mCharging=" + this.mCharging);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void stopWorkSb() {
        synchronized (this) {
            this.mStandbyManager.stopWork();
            this.mWorking = false;
            unRegisterScreenOffTimeout();
        }
    }

    private synchronized void unRegisterScreenOffTimeout() {
        try {
            if (this.mReceiverRegistered) {
                if (DfsUtils.DEBUG) {
                    Log.d(TAG, "unRegisterScreenOffTimeout");
                }
                this.mContext.unregisterReceiver(this.mDfsReceiver);
                this.mReceiverRegistered = false;
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private void updateCloudData(JSONObject jSONObject) {
        if (DfsUtils.DEBUG || jSONObject == null) {
            if (jSONObject == null) {
                Log.d(TAG, "Cloud config is empty");
            } else {
                Log.d(TAG, "Debug mode ignore cloud config.");
            }
            this.mCloudData.reset();
            return;
        }
        this.mCloudData.updateCloudConfig(jSONObject);
        if (jSONObject.has("screenOffTimeout")) {
            try {
                this.mScreenOffTimeoutMs = jSONObject.getInt("screenOffTimeout");
            } catch (JSONException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:47:0x009e  */
    public void dump(PrintWriter printWriter, String[] strArr) {
        byte b2;
        printWriter.println("Dump of DFS info for power:");
        printWriter.println("version=" + DfsUtils.VERSION);
        byte b3 = 0;
        String[] strArr2 = null;
        if (strArr != null && strArr.length > 0) {
            String str = strArr[0];
            int iHashCode = str.hashCode();
            if (iHashCode != 1497) {
                if (iHashCode != 1500) {
                    if (iHashCode != 1506) {
                        if (iHashCode != 1510) {
                            if (iHashCode != 1512) {
                                if (iHashCode != 487731628) {
                                    if (iHashCode != 1385203341) {
                                        if (iHashCode == 1396715842 && str.equals("-reset")) {
                                            b2 = 7;
                                        } else {
                                            b2 = -1;
                                        }
                                    } else if (str.equals("-event")) {
                                        b2 = 5;
                                    } else {
                                        b2 = -1;
                                    }
                                } else if (str.equals("-wakeup")) {
                                    b2 = 6;
                                } else {
                                    b2 = -1;
                                }
                            } else if (str.equals("-u")) {
                                b2 = 1;
                            } else {
                                b2 = -1;
                            }
                        } else if (str.equals("-s")) {
                            b2 = 0;
                        } else {
                            b2 = -1;
                        }
                    } else if (str.equals("-o")) {
                        b2 = 4;
                    } else {
                        b2 = -1;
                    }
                } else if (str.equals("-i")) {
                    b2 = 3;
                } else {
                    b2 = -1;
                }
            } else if (str.equals("-f")) {
                b2 = 2;
            } else {
                b2 = -1;
            }
            switch (b2) {
                case 0:
                    b3 = -2;
                    strArr2 = (String[]) Arrays.copyOfRange(strArr, 1, strArr.length);
                    break;
                case 1:
                    b3 = -3;
                    strArr2 = (String[]) Arrays.copyOfRange(strArr, 1, strArr.length);
                    break;
                case 2:
                    if (strArr.length == 2) {
                        this.mCloudData.factorBit = Float.parseFloat(strArr[1]);
                        printWriter.println("Set factorBit to " + this.mCloudData.factorBit);
                    }
                    strArr2 = (String[]) Arrays.copyOfRange(strArr, 1, strArr.length);
                    break;
                case 3:
                    inTimeSpan();
                    break;
                case 4:
                    outTimeSpan();
                    break;
                case 5:
                    if (strArr.length == 2) {
                        DfsUtils.debugEventId = Integer.parseInt(strArr[1]);
                        DfsUtils.debugWakeup = null;
                        printWriter.println("Set debug event to id=" + DfsUtils.debugEventId);
                    }
                    strArr2 = (String[]) Arrays.copyOfRange(strArr, 1, strArr.length);
                    break;
                case 6:
                    if (strArr.length == 2) {
                        DfsUtils.debugEventId = DfsUtils.EVENT_ID_WAKEUP_ALARM;
                        DfsUtils.debugWakeup = strArr[1];
                        printWriter.println("Set debug wakeup to " + DfsUtils.debugWakeup);
                    }
                    strArr2 = (String[]) Arrays.copyOfRange(strArr, 1, strArr.length);
                    break;
                case 7:
                    DfsUtils.debugEventId = 0;
                    DfsUtils.debugWakeup = null;
                    break;
                default:
                    strArr2 = (String[]) Arrays.copyOfRange(strArr, 1, strArr.length);
                    break;
            }
            return;
        }
        printWriter.println("mWorking=" + this.mWorking + ", bNight=" + this.bNight + ", mReceiverRegistered=" + this.mReceiverRegistered);
        StringBuilder sb = new StringBuilder();
        sb.append("mCurrentScene=");
        sb.append(this.mCurrentScene);
        printWriter.println(sb.toString());
        if (DfsUtils.DEBUG) {
            printWriter.println("debugEventId=" + DfsUtils.debugEventId + ", debugWakeup=" + DfsUtils.debugWakeup);
        }
        StandbyManager standbyManager = this.mStandbyManager;
        if (standbyManager != null && (b3 & 1) == 0) {
            standbyManager.dump(printWriter);
        }
        UsageManager usageManager = this.mUsageManager;
        if (usageManager == null || (b3 & 2) != 0) {
            return;
        }
        usageManager.dump(printWriter, strArr2);
    }

    public UsageMonitor getPowerUsageMonitor() {
        return this.mUsageMonitor;
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void inTimeSpan() {
        this.bNight = true;
        this.mCurrentScene = 2;
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void outTimeSpan() {
        this.bNight = false;
        this.mCurrentScene = 1;
        this.mHandler.sendEmptyMessage(1);
    }
}
