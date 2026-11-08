package com.miui.powerkeeper.thermalcollector.source;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;

/* JADX INFO: loaded from: classes.dex */
public class CollectorUtils {
    public static boolean getNightMode(Context context) {
        int i2 = context.getResources().getConfiguration().uiMode;
        context.getResources().getConfiguration();
        return i2 == 32;
    }

    public static int getScreenBrightnessMode(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), "screen_brightness_mode");
        } catch (Exception e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    public static String getWifiApStatus(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            wifiManager.getWifiState();
            Class[] clsArr = new Class[0];
            return ((Integer) wifiManager.getClass().getDeclaredMethod("getWifiApState", null).invoke(wifiManager, null)).intValue() == ((Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED").get(wifiManager)).intValue() ? SleepModeControllerNew.SLEEP_MODE_ON : "off";
        } catch (Exception e2) {
            e2.printStackTrace();
            return "e";
        }
    }
}
