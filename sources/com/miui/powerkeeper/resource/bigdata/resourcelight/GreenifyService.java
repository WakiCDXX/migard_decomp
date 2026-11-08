package com.miui.powerkeeper.resource.bigdata.resourcelight;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class GreenifyService extends Service {
    public static final int MSG_APP_WAKEUP = 0;
    public static final int MSG_WAKEUP = 1;
    static final String TAG = "GreenifyService";
    private AbnormalDetector mAbnormalDetector;
    private boolean mEnabled;
    private Handler mHandler;
    private final Object mLock = new Object();
    private WakeupAnalysis mWakeupAnalysis;

    private class GreenifyCheckerHandler extends Handler {
        public GreenifyCheckerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == 0) {
                if (GreenifyService.this.mWakeupAnalysis != null) {
                    GreenifyService.this.mWakeupAnalysis.LogDetailsWakeupData((Bundle) message.obj);
                }
            } else if (i2 == 1 && GreenifyService.this.mWakeupAnalysis != null) {
                GreenifyService.this.mWakeupAnalysis.LogWakeupData((Bundle) message.obj);
            }
        }
    }

    private class MyBinderService extends IGreenifyService.Stub {
        @Override // com.miui.powerkeeper.resource.bigdata.resourcelight.IGreenifyService
        public void triggerGreenifyEngineFromDetailsWakeUp(Bundle bundle) {
            GreenifyService.this.LogDetailsWakeUpData(bundle);
        }

        @Override // com.miui.powerkeeper.resource.bigdata.resourcelight.IGreenifyService
        public void triggerGreenifyEngineFromSuspend(Bundle bundle) {
            GreenifyService.this.LogWakeupData(bundle);
        }

        private MyBinderService() {
        }
    }

    private void dumpService(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.mLock) {
            printWriter.println("GreenifyService dump start >>>>>>");
        }
        this.mWakeupAnalysis.dump(printWriter, strArr);
        printWriter.println();
    }

    private void initializeComponents() {
        this.mWakeupAnalysis = new WakeupAnalysis(this);
        AbnormalDetector abnormalDetector = new AbnormalDetector(this);
        this.mAbnormalDetector = abnormalDetector;
        abnormalDetector.init();
    }

    public void LogDetailsWakeUpData(Bundle bundle) {
        if (this.mEnabled) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(0, bundle));
        }
    }

    public void LogWakeupData(Bundle bundle) {
        if (this.mEnabled) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, bundle));
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return new MyBinderService().asBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        Log.d(TAG, "onCreate");
        initializeComponents();
        WakeupAnalysis wakeupAnalysis = this.mWakeupAnalysis;
        if (wakeupAnalysis != null) {
            wakeupAnalysis.onEnable();
        }
        this.mWakeupAnalysis.postinit();
        this.mHandler = new GreenifyCheckerHandler(BackgroundThread.getHandler().getLooper());
        this.mEnabled = true;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.mEnabled = false;
        WakeupAnalysis wakeupAnalysis = this.mWakeupAnalysis;
        if (wakeupAnalysis != null) {
            wakeupAnalysis.onDisable();
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        this.mWakeupAnalysis = null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        return 1;
    }

    @Override // android.app.Service
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
