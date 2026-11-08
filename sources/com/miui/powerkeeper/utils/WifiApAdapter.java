package com.miui.powerkeeper.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import com.miui.powerkeeper.logsystem.config.LogType;

/* JADX INFO: loaded from: classes.dex */
public class WifiApAdapter {

    public interface SoftApCallback extends WifiManager.SoftApCallback {
    }

    private WifiApAdapter() {
    }

    public static void registerSoftApCallback(Context context, SoftApCallback softApCallback, Handler handler) {
        Log.e("WifiApAdapter", "registerSoftApCallback should not enter here");
    }

    public static void setWifiApEnabled(Context context, boolean z2) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME);
        if (z2) {
            connectivityManager.startTethering(0, false, new ConnectivityManager.OnStartTetheringCallback() { // from class: com.miui.powerkeeper.utils.WifiApAdapter.1
            });
        } else {
            connectivityManager.stopTethering(0);
        }
    }

    public static void unregisterSoftApCallback(Context context, SoftApCallback softApCallback) {
        Log.e("WifiApAdapter", "unregisterSoftApCallback should not enter here");
    }
}
