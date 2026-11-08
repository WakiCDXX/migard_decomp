package com.miui.powerkeeper.statemachine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.miui.powerkeeper.DeviceIdlePolicyHelper;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.TimeSpan;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class ForceDozeController implements TimeSpan.a {
    private static final int MAXLOG = 50;
    private static final String TAG = "PowerKeeper.Doze";
    private Context mContext;
    private RandomDelay mRandomDelay;
    PendingIntent mTimeTickSender;
    private boolean mForceIdle = false;
    private boolean mBlackAppApplied = false;
    private boolean mRegistered = false;
    private boolean mScreenOffTimeout = false;
    private LocalLog mLog = new LocalLog(50);
    private BroadcastReceiver mKeyReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.statemachine.ForceDozeController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ForceDozeController.this.mScreenOffTimeout = false;
            if (Utils.isCharging(ForceDozeController.this.mContext) || "android.intent.action.KEYCODE_POWER_UP".equals(action) || "android.intent.action.USER_PRESENT".equals(action)) {
                ForceDozeController.this.mLog.log("receive " + action);
                ForceDozeController.this.mRandomDelay.stop();
                synchronized (ForceDozeController.class) {
                    try {
                        if (ForceDozeController.this.mForceIdle) {
                            ForceDozeController.this.exitForceIdle();
                            ForceDozeController.this.logIdleEnd("key or user present");
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }
    };
    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.statemachine.ForceDozeController.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                ForceDozeController.this.mScreenOffTimeout = false;
                if (ForceDozeController.this.mForceIdle) {
                    ForceDozeController.this.updateTime();
                }
            }
        }
    };

    private class RandomDelay {
        private static final String ACTION_RANDOM_DELAY = "miui.action.powerkeeper.randomdelay";
        private static final int DELAY_TIME_RANGE = 3600000;
        private AlarmManager mAlarmManager;
        private PendingIntent mPendingIntent;
        private BroadcastReceiver mReviver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.statemachine.ForceDozeController.RandomDelay.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                ForceDozeController.this.exitNightAction();
            }
        };

        public RandomDelay() {
            this.mAlarmManager = (AlarmManager) ForceDozeController.this.mContext.getSystemService("alarm");
            ForceDozeController.this.mContext.registerReceiver(this.mReviver, new IntentFilter(ACTION_RANDOM_DELAY), 2);
            this.mPendingIntent = PendingIntent.getBroadcastAsUser(ForceDozeController.this.mContext, 0, new Intent(ACTION_RANDOM_DELAY), 201326592, UserHandle.ALL);
        }

        public void destroy() {
            ForceDozeController.this.mContext.unregisterReceiver(this.mReviver);
        }

        public void start() {
            this.mAlarmManager.set(1, System.currentTimeMillis() + ((long) new Random().nextInt(3600000)), this.mPendingIntent);
        }

        public void stop() {
            this.mAlarmManager.cancel(this.mPendingIntent);
        }
    }

    public ForceDozeController(Context context) {
        Log.d(TAG, "create ForceDozeController");
        this.mContext = context;
        this.mRandomDelay = new RandomDelay();
        registerScreenReceiver();
        DeviceIdlePolicyHelper.i(this.mContext).g();
        PowerKeeperManager.getInstance().getTimeScheduleManager().e(0, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exitForceIdle() {
        if (this.mForceIdle) {
            updateTime();
            unregisterPowerkeyReceiver();
            Log.d(TAG, "exit force idle");
            restoreWhiteListAppsIfQuitForceIdle();
            DeviceIdlePolicyHelper.i(this.mContext).g();
            this.mForceIdle = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exitNightAction() {
        boolean z2;
        try {
            z2 = Boolean.parseBoolean(CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, "true"));
        } catch (Exception e2) {
            Log.e(TAG, "parse inNightOnly error: " + e2.getMessage());
            z2 = true;
        }
        synchronized (ForceDozeController.class) {
            if (z2) {
                try {
                    if (this.mForceIdle) {
                        exitForceIdle();
                        logIdleEnd("out time span");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private void forceToIdle() {
        if (this.mForceIdle) {
            return;
        }
        Log.d(TAG, "force to idle");
        removeWhiteListAppsIfEnterForceIdle();
        DeviceIdlePolicyHelper.i(this.mContext).h();
        this.mForceIdle = true;
        registerPowerkeyReceiver();
        logIdleStart();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logIdleEnd(String str) {
        this.mLog.log("force-doze end: " + str);
    }

    private void logIdleStart() {
        this.mLog.log("force-doze start");
    }

    private void registerPowerkeyReceiver() {
        if (this.mRegistered) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.KEYCODE_POWER_UP");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        this.mContext.registerReceiver(this.mKeyReceiver, intentFilter, 2);
        this.mRegistered = true;
    }

    private void registerScreenReceiver() {
        this.mContext.registerReceiver(this.mScreenReceiver, new IntentFilter("android.intent.action.SCREEN_ON"), 2);
    }

    private void removeWhiteListAppsIfEnterForceIdle() {
        String[] strArrSplit;
        if (this.mBlackAppApplied) {
            return;
        }
        try {
            String functionParameters2 = CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, "");
            if (functionParameters2.equals("") || (strArrSplit = functionParameters2.split(",")) == null) {
                return;
            }
            for (String str : strArrSplit) {
                DeviceIdlePolicyHelper.i(this.mContext).o(str);
            }
            this.mBlackAppApplied = true;
        } catch (Exception e2) {
            Log.e(TAG, "remove white apps fail: ", e2);
        }
    }

    private void restoreWhiteListAppsIfQuitForceIdle() {
        String[] strArrSplit;
        if (this.mBlackAppApplied) {
            try {
                String functionParameters2 = CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, "");
                if (functionParameters2.equals("") || (strArrSplit = functionParameters2.split(",")) == null) {
                    return;
                }
                SparseBooleanArray uidStates = PowerKeeperManager.getInstance().getDeviceIdleController().getUidStates();
                for (String str : strArrSplit) {
                    int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, str);
                    if (uidStates.get(uidByPackageName)) {
                        DeviceIdlePolicyHelper.i(this.mContext).d(uidByPackageName);
                    }
                }
                this.mBlackAppApplied = false;
            } catch (Exception e2) {
                Log.e(TAG, "restore white apps fail: ", e2);
            }
        }
    }

    private void unRegisterScreenReceiver() {
        this.mContext.unregisterReceiver(this.mScreenReceiver);
    }

    private void unregisterPowerkeyReceiver() {
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this.mKeyReceiver);
            this.mRegistered = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTime() {
        PendingIntent broadcastAsUser = PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent("android.intent.action.TIME_TICK").addFlags(1342177280), 67108864, UserHandle.ALL);
        this.mTimeTickSender = broadcastAsUser;
        try {
            broadcastAsUser.send();
        } catch (PendingIntent.CanceledException unused) {
        }
    }

    public void destroy() {
        unRegisterScreenReceiver();
        unregisterPowerkeyReceiver();
        restoreWhiteListAppsIfQuitForceIdle();
        DeviceIdlePolicyHelper.i(this.mContext).g();
        PowerKeeperManager.getInstance().getTimeScheduleManager().g(0, this);
        this.mRandomDelay.destroy();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("\ndump ForceDozeController");
        boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, false);
        sb.append("\nfunc enabled = " + z2);
        if (z2) {
            sb.append("\nmForceIdle = ");
            sb.append(this.mForceIdle);
            sb.append("\nconfig black list:\n");
            sb.append(CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, "none"));
            sb.append("\nmRegistered = ");
            sb.append(this.mRegistered);
            sb.append("\nidle history:\n");
            printWriter.println(sb.toString());
            this.mLog.dump(fileDescriptor, printWriter, strArr);
        }
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void inTimeSpan() {
        boolean z2;
        Log.d(TAG, "enter night");
        if (SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, false)) {
            try {
                z2 = Boolean.parseBoolean(CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, "true"));
            } catch (Exception e2) {
                Log.e(TAG, "parse inNightOnly error: " + e2.getMessage());
                z2 = true;
            }
            if (z2 && this.mScreenOffTimeout) {
                onScreenOffTimeout();
            }
        }
    }

    public void onScreenOffTimeout() {
        boolean z2 = true;
        this.mScreenOffTimeout = true;
        if (Utils.isCharging(this.mContext)) {
            Log.d(TAG, "charging, nothing to be done");
            return;
        }
        if (((PowerManager) this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).isScreenOn()) {
            Log.d(TAG, "screen on, not enter idle");
            return;
        }
        if (SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, false)) {
            try {
                z2 = Boolean.parseBoolean(CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, "true"));
            } catch (Exception e2) {
                Log.e(TAG, "parse inNightOnly error: " + e2.getMessage());
            }
            Log.d(TAG, "night only = " + z2 + ", is night: " + TimeSpan.c() + ", inForceIdle:" + this.mForceIdle);
            if (!z2 || TimeSpan.c()) {
                forceToIdle();
            }
        }
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void outTimeSpan() {
        Log.d(TAG, "exit night");
        this.mRandomDelay.start();
    }
}
