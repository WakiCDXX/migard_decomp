package com.miui.powerkeeper.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.ProcessUtils;

/* JADX INFO: loaded from: classes.dex */
public class KillCameraReceiver extends BroadcastReceiver {
    private static final String TAG = KillCameraReceiver.class.getSimpleName();
    private static KillCameraReceiver sInstance;
    private final String[] mAllowableSet = {"cameraserver", "android.hardware.camera.provider@2.4-service", "android.hardware.camera.provider@2.4-service_64", "vendor.qti.camera.provider@2.7-service_64", "vendor.qti.camera.provider-service_64"};

    public static void register(Context context, Handler handler) {
        synchronized (KillCameraReceiver.class) {
            try {
                if (sInstance == null) {
                    sInstance = new KillCameraReceiver();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        context.registerReceiver(sInstance, new IntentFilter("com.android.camera.action.KILL_CAMERA_SERVICE"), "com.miui.powerkeeper.permission.KILL_CAMERA", handler, 2);
    }

    public static void unregister(Context context) {
        synchronized (KillCameraReceiver.class) {
            try {
                KillCameraReceiver killCameraReceiver = sInstance;
                if (killCameraReceiver != null) {
                    context.unregisterReceiver(killCameraReceiver);
                    sInstance = null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.android.camera.action.KILL_CAMERA_SERVICE".equals(intent.getAction())) {
            if (Constant.DEBUG) {
                Slog.d(TAG, "receive ACTION_KILL_CAMERA_SERVICE");
            }
            if (SystemClock.elapsedRealtime() > intent.getLongExtra("time", 0L)) {
                Slog.w(TAG, "Ignore killing camera intent");
                return;
            }
            String[] stringArrayExtra = intent.getStringArrayExtra("process_name");
            for (int i2 = 0; stringArrayExtra != null && i2 < stringArrayExtra.length; i2++) {
                if (!ArrayUtils.contains(this.mAllowableSet, stringArrayExtra[i2])) {
                    Slog.w(TAG, "Ignore killing camera process:" + stringArrayExtra[i2]);
                    stringArrayExtra[i2] = null;
                }
            }
            if (intent.getBooleanExtra("dump_backtrace", false)) {
                ProcessUtils.killCameraProcess(stringArrayExtra);
            } else {
                ProcessUtils.killProcess(stringArrayExtra);
            }
        }
    }
}
