package com.miui.powerkeeper.customerpower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class CustomerPowerReceiver extends BroadcastReceiver {
    private static final String CP_PERMISSION = "com.miui.powerkeeper.permission.CUSTOMER_POWER";
    private static final String TAG = CustomerPowerConstant.TAG + CustomerPowerReceiver.class.getSimpleName();
    private static CustomerPowerReceiver sInstance;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onReceive$0(Context context, Intent intent) {
        CustomerPowerCheck customerPowerCheck = new CustomerPowerCheck(context);
        if (CustomerPowerConstant.ACTION_CUSTOMER_POWER.equals(intent.getAction())) {
            customerPowerCheck.outputFinalResult(null, 0);
        } else if (CustomerPowerConstant.ACTION_FACTORY_CUSTOMER_POWER.equals(intent.getAction())) {
            customerPowerCheck.outputFinalResult(null, 1);
        }
    }

    public static void register(Context context) {
        synchronized (CustomerPowerReceiver.class) {
            try {
                if (sInstance == null) {
                    sInstance = new CustomerPowerReceiver();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        context.registerReceiver(sInstance, new IntentFilter(CustomerPowerConstant.ACTION_CUSTOMER_POWER), 2);
        context.registerReceiver(sInstance, new IntentFilter(CustomerPowerConstant.ACTION_FACTORY_CUSTOMER_POWER), 2);
    }

    public static void unregister(Context context) {
        synchronized (CustomerPowerReceiver.class) {
            try {
                CustomerPowerReceiver customerPowerReceiver = sInstance;
                if (customerPowerReceiver != null) {
                    context.unregisterReceiver(customerPowerReceiver);
                    sInstance = null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null) {
            Log.d(TAG, "customer power info intent is null");
            return;
        }
        Log.d(TAG, "customer power info intent action is " + intent.getAction());
        new Thread(new Runnable() { // from class: com.miui.powerkeeper.customerpower.a
            @Override // java.lang.Runnable
            public final void run() {
                CustomerPowerReceiver.lambda$onReceive$0(context, intent);
            }
        }).start();
    }
}
