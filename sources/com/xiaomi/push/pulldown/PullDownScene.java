package com.xiaomi.push.pulldown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.channel.commonutils.android.ReceiverHelper;
import com.xiaomi.channel.commonutils.android.SafeRunnable;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class PullDownScene {
    private static final String KEYCODE_EXTERNAL = "miui.intent.action.KEYCODE_EXTERNAL";
    private static final String KEYCODE_POWER_UP = "android.intent.action.KEYCODE_POWER_UP";
    private static final String PERMISSION_BROADCAST = "android.permission.STATUS_BAR_SERVICE";
    private static final String STATUSBAR_EXPANDED = "com.xiaomi.action.RESPONSE_STATUS_BAR_PANEL_STATUS";
    private static final String TAG = "PullDownScene";
    private OnTriggerPullDownCallback mCallback;
    private Context sContext;
    private volatile boolean mListened = false;
    private volatile long powerUpTime = -1;
    private volatile long touchExternalTime = -1;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private StatusBarExpandedReceiver mStatusBarExpandedReceiver = new StatusBarExpandedReceiver();
    private PowerUpReceiver mPowerUpReceiver = new PowerUpReceiver();

    public interface OnTriggerPullDownCallback {
        void onTrigger();
    }

    class PowerUpReceiver extends BroadcastReceiver {
        PowerUpReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            action.hashCode();
            if (action.equals(PullDownScene.KEYCODE_EXTERNAL)) {
                PullDownScene.this.touchExternalTime = System.currentTimeMillis();
            } else if (action.equals(PullDownScene.KEYCODE_POWER_UP)) {
                PullDownScene.this.powerUpTime = System.currentTimeMillis();
            }
        }
    }

    class StatusBarExpandedReceiver extends BroadcastReceiver {
        StatusBarExpandedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean zIsKeyguardLocked = ToolUtils.isKeyguardLocked(PullDownScene.this.sContext);
            PullDownLog.w("PullDownScene receive status bar, is keyguard: " + zIsKeyguardLocked);
            if (zIsKeyguardLocked) {
                return;
            }
            PullDownScene.this.singleThreadExecutor.execute(new SafeRunnable("receiveStatusBar") { // from class: com.xiaomi.push.pulldown.PullDownScene.StatusBarExpandedReceiver.1
                @Override // com.xiaomi.channel.commonutils.android.SafeRunnable
                public void safeRun() {
                    if (PullDownScene.this.touchExternalTime == -1) {
                        try {
                            Thread.sleep(10L);
                        } catch (InterruptedException e2) {
                            PullDownLog.e(PullDownScene.TAG + e2);
                        }
                    }
                    if (PullDownScene.this.touchExternalTime != -1 && PullDownScene.this.powerUpTime == -1) {
                        try {
                            Thread.sleep(1500L);
                            if (PullDownScene.this.powerUpTime == -1) {
                                PullDownLog.w("PullDownScene notifyPushMessage no powerup");
                                PullDownScene.this.notifyMessage();
                            } else {
                                PullDownLog.e("PullDownScene wrong event");
                            }
                        } catch (InterruptedException e3) {
                            PullDownLog.e(PullDownScene.TAG + e3);
                        }
                    }
                    if (PullDownScene.this.touchExternalTime == -1) {
                        PullDownLog.w("PullDownScene notifyPushMessage no external");
                        PullDownScene.this.notifyMessage();
                    }
                    PullDownScene.this.touchExternalTime = -1L;
                    PullDownScene.this.powerUpTime = -1L;
                    if (PullDownSupport.subscribePullDownBroadcast(PullDownScene.this.sContext)) {
                        PullDownLog.d("PullDownScene subscribePullDownBroadcast succ");
                    } else {
                        PullDownLog.d("PullDownScene subscribePullDownBroadcast failed");
                    }
                }
            });
        }
    }

    public PullDownScene(Context context, OnTriggerPullDownCallback onTriggerPullDownCallback) {
        this.sContext = context;
        this.mCallback = onTriggerPullDownCallback;
    }

    private void initBroadcast() {
        MyLog.w("PullDownScene initBroadcast");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STATUSBAR_EXPANDED);
        ReceiverHelper.registerReceiver(this.sContext, this.mStatusBarExpandedReceiver, intentFilter, PERMISSION_BROADCAST, (Handler) null);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(KEYCODE_POWER_UP);
        intentFilter2.addAction(KEYCODE_EXTERNAL);
        ReceiverHelper.registerReceiver(this.sContext, this.mPowerUpReceiver, intentFilter2, (String) null, (Handler) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyMessage() {
        if (DeviceInfo.isSameSpace()) {
            trigger();
        }
    }

    protected void listen() {
        if (!PullDownSupport.subscribePullDownBroadcast(this.sContext) || this.mListened) {
            return;
        }
        PullDownLog.w("PullDownScene listen");
        try {
            initBroadcast();
            this.mListened = true;
        } catch (Exception unused) {
            PullDownLog.w("PullDownSceneinit broadcast failed");
        }
    }

    protected void trigger() {
        OnTriggerPullDownCallback onTriggerPullDownCallback = this.mCallback;
        if (onTriggerPullDownCallback != null) {
            onTriggerPullDownCallback.onTrigger();
        }
    }

    protected void unlisten() {
        if (this.mListened) {
            try {
                this.sContext.unregisterReceiver(this.mStatusBarExpandedReceiver);
            } catch (Exception e2) {
                PullDownLog.e("PullDownSceneunregister statusbarexpanded_receiver error:" + e2);
            }
            try {
                this.sContext.unregisterReceiver(this.mPowerUpReceiver);
            } catch (Exception e3) {
                PullDownLog.e("PullDownSceneunregister powerup_receiver error" + e3);
            }
        }
        this.mListened = false;
    }
}
