package com.miui.powerkeeper.statemachine;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.powerchecker.TxPowerChecker;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class PowerStateMachineService extends Service {
    public static final String DISABLE_MACHINE = "disable_machine";
    public static final String ENABLE_MACHINE = "enable_machine";
    private static final String TAG = "PowerStateMachine";
    private static HandlerThread sThread;
    private PowerStateMachine mPowerStateMachine;
    private SatelliteTelephonyController mSatelliteTelephonyController;
    private SleepModeControllerNew mSleepModeControllerNew;
    private TxPowerChecker mTxPowerChecker;
    private VolteSettingController mVolteSettingController;

    private static void ensureThreadLocked() {
        if (sThread == null) {
            HandlerThread handlerThread = new HandlerThread("PowerStateMachine", 0);
            sThread = handlerThread;
            handlerThread.start();
        }
    }

    public static synchronized HandlerThread getThread() {
        ensureThreadLocked();
        return sThread;
    }

    @Override // android.app.Service
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        String str2;
        if (UserHandle.myUserId() != 0) {
            return;
        }
        if (strArr != null) {
            if (strArr.length == 1 && (str2 = strArr[0]) != null && str2.equals("-fpsconfig") && Build.IS_DEBUGGABLE) {
                ArrayMap<String, Integer> pkgFpsConfig = DisplayFrameSetting.getPkgFpsConfig();
                printWriter.println("fpsconfig size=" + pkgFpsConfig.size() + " " + pkgFpsConfig);
                return;
            }
            if (strArr.length >= 1 && (str = strArr[0]) != null && str.equals("-sleep") && Build.IS_DEBUGGABLE) {
                SleepModeControllerNew sleepModeControllerNew = SleepModeControllerNew.getInstance();
                this.mSleepModeControllerNew = sleepModeControllerNew;
                if (sleepModeControllerNew != null) {
                    sleepModeControllerNew.dump(fileDescriptor, printWriter, (String[]) Arrays.copyOfRange(strArr, 1, strArr.length));
                    return;
                } else {
                    printWriter.println("Sleep mode is not support.");
                    return;
                }
            }
        }
        PowerStateMachine powerStateMachine = this.mPowerStateMachine;
        if (powerStateMachine != null) {
            powerStateMachine.dump(fileDescriptor, printWriter, strArr);
        }
        TxPowerChecker txPowerChecker = this.mTxPowerChecker;
        if (txPowerChecker != null) {
            txPowerChecker.dump(fileDescriptor, printWriter, strArr);
        }
        VolteSettingController volteSettingController = this.mVolteSettingController;
        if (volteSettingController != null) {
            volteSettingController.dump(fileDescriptor, printWriter, strArr);
        }
        SleepModeControllerNew sleepModeControllerNew2 = SleepModeControllerNew.getInstance();
        this.mSleepModeControllerNew = sleepModeControllerNew2;
        if (sleepModeControllerNew2 != null) {
            sleepModeControllerNew2.dump(fileDescriptor, printWriter, strArr);
        }
        SatelliteTelephonyController satelliteTelephonyController = this.mSatelliteTelephonyController;
        if (satelliteTelephonyController != null) {
            satelliteTelephonyController.dump(fileDescriptor, printWriter, strArr);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return;
        }
        Log.i("PowerStateMachine", "onCreate()");
        this.mVolteSettingController = new VolteSettingController(this);
        PowerKeeperManager.getInstance().getTimeScheduleManager().e(0, this.mVolteSettingController);
        this.mTxPowerChecker = new TxPowerChecker(this);
        this.mSatelliteTelephonyController = SatelliteTelephonyController.getInstance();
        this.mSleepModeControllerNew = SleepModeControllerNew.getInstance();
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (UserHandle.myUserId() != 0) {
            super.onDestroy();
            return;
        }
        Log.i("PowerStateMachine", "onDestroy()");
        super.onDestroy();
        PowerStateMachineProxy.getInstance().setPowerStateMachine(null);
        PowerKeeperManager.getInstance().getTimeScheduleManager().g(0, this.mVolteSettingController);
        PowerStateMachine powerStateMachine = this.mPowerStateMachine;
        if (powerStateMachine != null) {
            powerStateMachine.destroy();
        }
        TxPowerChecker txPowerChecker = this.mTxPowerChecker;
        if (txPowerChecker != null) {
            txPowerChecker.destroy();
        }
        VolteSettingController volteSettingController = this.mVolteSettingController;
        if (volteSettingController != null) {
            volteSettingController.destroy();
        }
        SleepModeControllerNew sleepModeControllerNew = this.mSleepModeControllerNew;
        if (sleepModeControllerNew != null) {
            sleepModeControllerNew.dispose();
        }
        SatelliteTelephonyController satelliteTelephonyController = this.mSatelliteTelephonyController;
        if (satelliteTelephonyController != null) {
            satelliteTelephonyController.destroy();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return 2;
        }
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ENABLE_MACHINE)) {
                if (this.mPowerStateMachine == null) {
                    Settings.System.putInt(getContentResolver(), "speed_mode", 0);
                    this.mPowerStateMachine = new PowerStateMachine("PowerStateMachine", this, PowerKeeperManager.getInstance());
                    PowerStateMachineProxy.getInstance().setPowerStateMachine(this.mPowerStateMachine);
                }
            } else if (intent.getAction().equals(DISABLE_MACHINE) && this.mPowerStateMachine != null) {
                PowerStateMachineProxy.getInstance().setPowerStateMachine(null);
                this.mPowerStateMachine.destroy();
                this.mPowerStateMachine = null;
            }
        }
        return 2;
    }
}
