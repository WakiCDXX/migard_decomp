package com.miui.powerkeeper.perfengine;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.statemachine.NoiseCpuHandler;
import com.miui.powerkeeper.utils.PowerLog;

/* JADX INFO: loaded from: classes.dex */
public class MtkBoost {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static volatile boolean f403a = false;

    public static void a(Context context, String str) {
        if (f403a) {
            Log.w("PeGameController_MtkBoost", "perfByJoyose acquire return, already boost");
            return;
        }
        if (TextUtils.isEmpty(str)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_MtkBoost", "perfByJoyose cmd is empty");
            }
        } else {
            Intent intent = new Intent(NoiseCpuHandler.ACTION_MTK_PERF_LOCK_ACQUIRE);
            intent.putExtra("cmd", str);
            intent.setPackage("com.xiaomi.joyose");
            context.sendBroadcast(intent);
            Log.w("PeGameController_MtkBoost", "perfByJoyose perfLockAcquire");
            f403a = true;
        }
    }

    public static void b(Context context, boolean z2) {
        if (!z2) {
            if (!f403a) {
                Log.w("PeGameController_MtkBoost", "perfByJoyose release return, already no boost");
                return;
            }
            Intent intent = new Intent(NoiseCpuHandler.ACTION_MTK_PERF_LOCK_RELEASE_HANDLER);
            intent.setPackage("com.xiaomi.joyose");
            context.sendBroadcast(intent);
            Log.w("PeGameController_MtkBoost", "perfByJoyose perfLockRelease");
            f403a = false;
            return;
        }
        if (f403a) {
            Log.w("PeGameController_MtkBoost", "perfByJoyose acquire return, already boost");
            return;
        }
        String str = Build.DEVICE;
        if (TextUtils.isEmpty(str)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_MtkBoost", "perfByJoyose deviceName is empty");
                return;
            }
            return;
        }
        String strNativeGetCMD = !TextUtils.isEmpty(PeGameController.j0) ? PeGameController.j0 : nativeGetCMD(str);
        if (TextUtils.isEmpty(strNativeGetCMD)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_MtkBoost", "perfByJoyose cmd is empty return");
                return;
            }
            return;
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController_MtkBoost", "perfByJoyose native cmd:" + strNativeGetCMD);
        }
        Intent intent2 = new Intent(NoiseCpuHandler.ACTION_MTK_PERF_LOCK_ACQUIRE);
        intent2.putExtra("cmd", strNativeGetCMD);
        intent2.setPackage("com.xiaomi.joyose");
        context.sendBroadcast(intent2);
        Log.w("PeGameController_MtkBoost", "perfByJoyose perfLockAcquire");
        f403a = true;
    }

    private static native String nativeGetCMD(String str);
}
