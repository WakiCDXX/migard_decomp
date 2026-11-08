package com.xiaomi.channel.commonutils.android;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
public class ReceiverHelper {
    private static final Object TASK_LOCK = new Object();
    private static volatile Handler sReceiverHandler;
    private static volatile Handler sTaskHandler;

    public static void disableStaticReceiver(Context context, Class<?> cls) {
        if (context == null || cls == null) {
            return;
        }
        realDisableStaticReceiver(context, new ComponentName(context, cls));
    }

    private static Handler getHandler() {
        if (sReceiverHandler == null) {
            synchronized (ReceiverHelper.class) {
                try {
                    if (sReceiverHandler == null) {
                        HandlerThread handlerThread = new HandlerThread("handle_receiver");
                        handlerThread.start();
                        sReceiverHandler = new Handler(handlerThread.getLooper());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sReceiverHandler;
    }

    public static Handler getTaskHandler() {
        if (sTaskHandler == null) {
            synchronized (TASK_LOCK) {
                try {
                    if (sTaskHandler == null) {
                        HandlerThread handlerThread = new HandlerThread("receiver_task");
                        handlerThread.start();
                        sTaskHandler = new Handler(handlerThread.getLooper());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sTaskHandler;
    }

    private static void realDisableStaticReceiver(final Context context, final ComponentName componentName) {
        getHandler().post(new Runnable() { // from class: com.xiaomi.channel.commonutils.android.ReceiverHelper.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PackageManager packageManager = context.getPackageManager();
                    if (packageManager.getComponentEnabledSetting(componentName) != 2) {
                        packageManager.setComponentEnabledSetting(componentName, 2, 1);
                    }
                } catch (Throwable th) {
                    MyLog.w("close static register of network status receiver failed:" + th);
                }
            }
        });
    }

    public static Intent registerReceiver(Context context, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        return registerReceiver(context, broadcastReceiver, intentFilter, 2);
    }

    public static void disableStaticReceiver(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return;
        }
        realDisableStaticReceiver(context, new ComponentName(context, str));
    }

    public static Intent registerReceiver(Context context, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, int i2) {
        return registerReceiver(context, broadcastReceiver, intentFilter, (String) null, i2);
    }

    public static Intent registerReceiver(Context context, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, String str, int i2) {
        return registerReceiver(context, broadcastReceiver, intentFilter, str, getHandler(), i2);
    }

    public static Intent registerReceiver(Context context, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, String str, Handler handler) {
        return registerReceiver(context, broadcastReceiver, intentFilter, str, handler, 2);
    }

    public static Intent registerReceiver(Context context, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, String str, Handler handler, int i2) {
        if (context == null || broadcastReceiver == null || intentFilter == null) {
            return null;
        }
        return context.registerReceiver(broadcastReceiver, intentFilter, str, handler, i2);
    }
}
