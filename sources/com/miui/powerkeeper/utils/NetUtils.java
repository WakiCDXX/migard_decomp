package com.miui.powerkeeper.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import com.miui.powerkeeper.logsystem.config.LogType;

/* JADX INFO: loaded from: classes.dex */
public class NetUtils {
    private static final String TAG = "NetUtils";

    public static boolean getMobileDataEnabled(Context context, String str) {
        return ((TelephonyManager) context.getSystemService("phone")).getDataEnabled();
    }

    public static NetworkStats getNetworkStats() {
        return null;
    }

    public static boolean isMobileConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == 0;
    }

    public static boolean isMobileWifi(Context context) {
        String bssid;
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if (wifiManager == null) {
            return false;
        }
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo != null) {
            String ipAddress = Formatter.formatIpAddress(dhcpInfo.gateway);
            if ("192.168.43.1".equals(ipAddress) || "172.20.10.1".equals(ipAddress)) {
                return true;
            }
        }
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo == null || (bssid = connectionInfo.getBSSID()) == null || bssid == "") {
            return false;
        }
        return ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).isActiveNetworkMetered();
    }

    public static boolean isNetConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isWifi6(Context context) {
        try {
            return ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getWifiStandard() == 6;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean isWifiConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == 1;
    }

    public static void setMobileDataEnabled(Context context, boolean z2, String str) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (z2) {
            telephonyManager.enableDataConnectivity();
        } else {
            telephonyManager.disableDataConnectivity();
        }
    }
}
