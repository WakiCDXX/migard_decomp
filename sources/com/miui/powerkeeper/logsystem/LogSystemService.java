package com.miui.powerkeeper.logsystem;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.logsystem.config.ConfigManager;
import com.miui.powerkeeper.logsystem.log.LogManager;
import com.miui.powerkeeper.logsystem.log.ShellCmdLogs;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class LogSystemService extends Service {
    public static final int MSG_INIT = 0;
    public static final int MSG_UPDATE_CONFIG = 1;
    public static final String TAG = "LS-LogSystemService";
    private PowerKeeperInterface.d mBatteryChangedCallback;
    ConfigManager mConfigManager;
    LogManager mLogManager;
    private PowerKeeperManager mPowerKeeperManager;
    boolean mLogSystemEnabled = false;
    int mLastBatteryLevel = 0;
    private PowerKeeperInterface.m mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.logsystem.LogSystemService.1
        @Override // com.miui.powerkeeper.PowerKeeperInterface.m
        public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
            Log.d(LogSystemService.TAG, "onBatteryChanged, mLastBatteryLevel = " + LogSystemService.this.mLastBatteryLevel + ", status = " + i2 + ", level = " + i4 + ", plug = " + i6 + ", scale = " + i7);
            if (i6 != 0) {
                return;
            }
            LogSystemService logSystemService = LogSystemService.this;
            if (logSystemService.mLastBatteryLevel - i4 == 1) {
                logSystemService.mLogManager.noteBatteryStatsChanged();
            }
            LogSystemService.this.mLastBatteryLevel = i4;
        }
    };
    MyHandler mHandler;
    private ContentObserver mCloudObserver = new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.logsystem.LogSystemService.2
        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            Log.d(LogSystemService.TAG, "mCloudObserver onChange.");
            ConfigManager configManager = LogSystemService.this.mConfigManager;
            if (configManager != null) {
                configManager.updateCloudConfig();
            }
            LogSystemService.this.mHandler.sendEmptyMessage(1);
        }
    };

    class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) throws Throwable {
            Log.d(LogSystemService.TAG, "handleMessage Msg ID:" + message.what);
            int i2 = message.what;
            if (i2 == 0) {
                LogSystemService.this.doInit();
                return;
            }
            if (i2 != 1) {
                return;
            }
            Log.d(LogSystemService.TAG, "handleMessage mLogSystemEnabled:" + LogSystemService.this.mLogSystemEnabled + ", localConfig: " + LogSystemService.this.mConfigManager.isFeatureEnabled());
            boolean zIsFeatureEnabled = LogSystemService.this.mConfigManager.isFeatureEnabled();
            LogSystemService logSystemService = LogSystemService.this;
            boolean z2 = logSystemService.mLogSystemEnabled;
            if (!z2 && zIsFeatureEnabled) {
                logSystemService.enable();
            } else {
                if (!z2 || zIsFeatureEnabled) {
                    return;
                }
                logSystemService.disable();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disable() {
        Log.d(TAG, "service disable.");
        this.mLogSystemEnabled = false;
        this.mPowerKeeperManager.unregisterBatteryChangedListener(this.mBatteryChangedCallback);
        PowerKeeperInterface.d dVar = this.mBatteryChangedCallback;
        if (dVar != null) {
            dVar.a();
            this.mBatteryChangedCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doInit() throws Throwable {
        ConfigManager configManager = ConfigManager.getInstance(this);
        this.mConfigManager = configManager;
        configManager.initLocalConfig();
        this.mConfigManager.updateCloudConfig();
        this.mLogManager = new LogManager(this);
        this.mHandler.sendEmptyMessage(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enable() {
        Log.d(TAG, "service enable.");
        this.mLogSystemEnabled = true;
        PowerKeeperInterface.d dVar = new PowerKeeperInterface.d(this.mHandler, this.mBatteryStatusReceiver);
        this.mBatteryChangedCallback = dVar;
        this.mPowerKeeperManager.registerBatteryChangedListener(dVar);
    }

    /* JADX WARN: Code duplicated, block: B:27:0x004d  */
    @Override // android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        ShellCmdLogs shellCmdLogs;
        printWriter.println("======== LogSystemService start (dumpsys activity service com.miui.powerkeeper/.logsystem.LogSystemService) ========");
        if (strArr != null && strArr.length > 0) {
            byte b2 = 0;
            String str = strArr[0];
            int iHashCode = str.hashCode();
            if (iHashCode != -71832145) {
                if (iHashCode != 1494) {
                    if (iHashCode != 1500) {
                        if (iHashCode == 1510 && str.equals("-s")) {
                            b2 = 3;
                        } else {
                            b2 = -1;
                        }
                    } else if (str.equals("-i")) {
                        b2 = 2;
                    } else {
                        b2 = -1;
                    }
                } else if (str.equals("-c")) {
                    b2 = 1;
                } else {
                    b2 = -1;
                }
            } else if (!str.equals("-config")) {
                b2 = -1;
            }
            if (b2 == 0 || b2 == 1) {
                printWriter.println("Cloud config: " + this.mConfigManager.getCloudConfigString());
                printWriter.println("Local config: " + this.mConfigManager.getLocalConfig());
                return;
            }
            if (b2 == 2) {
                this.mLogManager.dumpInterfaceLog(strArr);
                return;
            } else if (b2 == 3) {
                LogManager logManager = this.mLogManager;
                if (logManager == null || (shellCmdLogs = logManager.mShellCmdLogs) == null) {
                    return;
                }
                shellCmdLogs.dump(printWriter, strArr);
                return;
            }
        }
        printWriter.println("is Feature enabled: " + this.mLogSystemEnabled);
        LogManager logManager2 = this.mLogManager;
        if (logManager2 != null) {
            logManager2.dumpLog(printWriter);
        }
        printWriter.println("======== LogSystemService end ========");
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // android.app.Service
    public void onCreate() {
        Log.d(TAG, "service create.");
        this.mPowerKeeperManager = PowerKeeperManager.getInstance();
        MyHandler myHandler = new MyHandler(BackgroundThread.getHandler().getLooper());
        this.mHandler = myHandler;
        myHandler.sendEmptyMessage(0);
        getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor("log_system_config"), false, this.mCloudObserver);
        getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor("log_system_config_params"), false, this.mCloudObserver);
        getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor("log_system_config_params2"), false, this.mCloudObserver);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        disable();
        LogManager logManager = this.mLogManager;
        if (logManager != null) {
            logManager.destroy();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        return 2;
    }
}
