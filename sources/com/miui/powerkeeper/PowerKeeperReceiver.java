package com.miui.powerkeeper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;
import com.miui.powerkeeper.utils.PowerLog;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperReceiver extends BroadcastReceiver {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static boolean f277a = false;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static boolean f278b = false;

    public static void a(Context context) {
        f277a = true;
        Log.d(PowerKeeperManager.TAG, "initPowerKeeper");
        Intent intent = new Intent(context, (Class<?>) PowerKeeperBackgroundService.class);
        if (f278b) {
            f278b = false;
            intent.setAction("android.intent.action.BOOT_COMPLETED");
        }
        context.startServiceAsUser(intent, UserHandle.OWNER);
    }

    private void b(Context context) {
        if (!f277a) {
            Log.d(PowerKeeperManager.TAG, "start delayed for provision.");
            f278b = true;
        } else {
            Intent intent = new Intent(context, (Class<?>) PowerKeeperBackgroundService.class);
            intent.setAction("android.intent.action.BOOT_COMPLETED");
            context.startServiceAsUser(intent, UserHandle.OWNER);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            PowerLog.recordKey("main receive BOOT_COMPLETED");
            Log.d(PowerKeeperManager.TAG, "start");
            b(context);
            if (UserHandle.myUserId() == 0) {
                ScenarioCollectorUtils.getInstance().setFirstBootState(true);
                return;
            }
            return;
        }
        if ("android.intent.action.ACTION_SHUTDOWN".equals(action)) {
            Log.d(PowerKeeperManager.TAG, "shutdown");
            Intent intent2 = new Intent(context, (Class<?>) PowerKeeperBackgroundService.class);
            intent2.setAction(action);
            context.startServiceAsUser(intent2, UserHandle.OWNER);
        }
    }
}
