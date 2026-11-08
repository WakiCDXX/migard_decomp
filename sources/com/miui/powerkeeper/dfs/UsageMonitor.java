package com.miui.powerkeeper.dfs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class UsageMonitor {
    public static final String TAG = "DFS-UsageMonitor";
    public static final int TYPE_AUDIO = 0;
    public static final int TYPE_CPU = 1;
    public static final int TYPE_VPN = 2;
    public static final int TYPE_WIFI_AP = 3;
    private static UsageMonitor sInstance;
    private final ArrayList<UsageCallback> mCallbacks = new ArrayList<>();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.dfs.UsageMonitor.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("wifi_state", 0);
                boolean z2 = intExtra == 13 || intExtra == 12;
                if (z2 != UsageMonitor.this.mWifiApEnabled) {
                    Log.d(UsageMonitor.TAG, "wifiApChanged, enabled=" + z2);
                    UsageMonitor.this.mWifiApEnabled = z2;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(YellowPageContract.Permission.STATE, z2);
                    UsageMonitor.this.notifyUsageCallback(3, bundle);
                }
            }
        }
    };
    private boolean mWifiApEnabled;

    interface UsageCallback {
        void used(int i2, Bundle bundle);
    }

    private UsageMonitor(Context context) {
        registerReceiver(context);
        this.mWifiApEnabled = ((WifiManager) context.getSystemService(WifiManager.class)).isWifiApEnabled();
    }

    public static synchronized UsageMonitor getInstance(Context context) {
        try {
            if (sInstance == null) {
                sInstance = new UsageMonitor(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUsageCallback(int i2, Bundle bundle) {
        Iterator<UsageCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().used(i2, bundle);
        }
    }

    private void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        context.registerReceiver(this.mReceiver, intentFilter, 2);
    }

    public void notifyCpuAbnormal(int i2) {
        Log.d(TAG, "Cpu Abnormal, uid=" + i2);
        Bundle bundle = new Bundle();
        bundle.putInt("uid", i2);
        notifyUsageCallback(1, bundle);
    }

    public void notifyVpnConnect(int i2, int i3) {
        Log.d(TAG, "Vpn Connected, uid=" + i2);
        Bundle bundle = new Bundle();
        bundle.putInt("status", i3);
        notifyUsageCallback(2, bundle);
    }

    public void registerUsageCallback(UsageCallback usageCallback) {
        this.mCallbacks.add(usageCallback);
    }

    public void unregisterUsageCallback(UsageCallback usageCallback) {
        this.mCallbacks.remove(usageCallback);
    }

    public void notifyAudioDisguise(int i2) {
    }
}
