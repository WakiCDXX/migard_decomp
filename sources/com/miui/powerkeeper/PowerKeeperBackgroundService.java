package com.miui.powerkeeper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.event.EventMonitor;
import com.miui.powerkeeper.resource.bigdata.resourcelight.PowerOnetrackManager;
import com.miui.powerkeeper.siming.SiMing;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperBackgroundService extends Service {
    @Override // android.app.Service
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (UserHandle.myUserId() != 0) {
            return;
        }
        if (strArr != null && strArr.length > 0 && strArr[0].equals("-si")) {
            SiMing.getInstance().dump(fileDescriptor, printWriter, (String[]) Arrays.copyOfRange(strArr, 1, strArr.length));
            return;
        }
        if (strArr != null && strArr.length > 0 && strArr[0].equals("-em")) {
            EventMonitor.getInstance().dump(fileDescriptor, printWriter, (String[]) Arrays.copyOfRange(strArr, 1, strArr.length));
            return;
        }
        if (e.e.k()) {
            if (strArr != null && strArr.length > 0 && (strArr[0].equals("-m") || strArr[0].equals("-bm"))) {
                e.e.h().g(fileDescriptor, printWriter, strArr);
                return;
            }
            e.e.h().g(fileDescriptor, printWriter, strArr);
        }
        PowerKeeperManager.getInstance().dump(fileDescriptor, printWriter, strArr);
        EventMonitor.getInstance().dump(fileDescriptor, printWriter, null);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (UserHandle.myUserId() != 0) {
            return null;
        }
        Log.i("PowerKeeperBackgroundService", "onBind()");
        return PowerKeeperManager.getInstance().asBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return;
        }
        Log.i("PowerKeeperBackgroundService", "onCreate()");
        SharedPrefUtil.getInstance(this).save(Constants.IS_FIRST_POWER_ON, !SharedPrefUtil.getInstance(this).contains(Constants.IS_FIRST_POWER_ON));
        PowerKeeperManager.getInstance().onCreate();
        PowerOnetrackManager.getInstance().onCreate();
        if (SystemProperties.get("sys.boot_completed").equals("1")) {
            PowerKeeperManager.getInstance().onBootCompleted();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (UserHandle.myUserId() != 0) {
            super.onDestroy();
            return;
        }
        PowerKeeperManager.getInstance().onDestroy();
        Log.i("PowerKeeperBackgroundService", "onDestroy()");
        super.onDestroy();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return 1;
        }
        Log.i("PowerKeeperBackgroundService", "onStartCommand()");
        if (intent == null || intent.getAction() == null) {
            if (SystemProperties.get("sys.boot_completed").equals("1")) {
                PowerKeeperManager.getInstance().onBootCompleted();
            }
        } else if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {
            Log.i("PowerKeeperBackgroundService", "onStartCommand() intent.getAction()==android.intent.action.BOOT_COMPLETED");
            Settings.System.putInt(getContentResolver(), "powerkeep_earthquake_warning_version", 1);
            PowerKeeperManager.getInstance().onBootCompleted();
        } else if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")) {
            PowerKeeperManager.getInstance().onDestroy();
        } else if (intent.getAction().equals(PowerKeeperManager.ACTION_ALARM)) {
            PowerKeeperManager.getInstance().onAlarm(intent);
        }
        return 1;
    }
}
