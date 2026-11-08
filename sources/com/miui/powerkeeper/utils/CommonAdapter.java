package com.miui.powerkeeper.utils;

import android.app.usage.IUsageStatsManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.Network;
import android.net.VpnManager;
import android.os.IDeviceIdleController;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.IDockedStackListener;
import android.view.IWindowManager;
import com.android.internal.net.VpnConfig;
import com.miui.powerkeeper.batterylife.TrackData;
import com.miui.whetstone.WhetstoneActivityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.util.ITouchFeature;

/* JADX INFO: loaded from: classes.dex */
public class CommonAdapter {
    private static final String TAG = "PowerKeeper.CommonAdapter";
    public static final String UNKNOWN_SSID = "<unknown ssid>";

    private CommonAdapter() {
    }

    public static void addPowerSaveWhitelistApps(IDeviceIdleController iDeviceIdleController, String[] strArr) throws RemoteException {
        iDeviceIdleController.addPowerSaveWhitelistApps(Arrays.asList(strArr));
    }

    public static List getAppOpsManager() {
        new ArrayList();
        List appOpsManager = WhetstoneActivityManager.getAppOpsManager();
        Log.i(TAG, "WhetstoneActivityManager.setProperty appOpsList is " + appOpsManager);
        return appOpsManager;
    }

    public static int getNetId(Network network) {
        return network.getNetId();
    }

    public static StatusBarNotification[] getStatusBarNotification(String str) {
        return WhetstoneActivityManager.getStatusBarNotification(str);
    }

    public static VpnConfig getVpnConfig(Context context, int i2) {
        return ((VpnManager) context.getSystemService(VpnManager.class)).getVpnConfig(i2);
    }

    public static boolean hasNavigationBar(IWindowManager iWindowManager, Context context) {
        try {
            return iWindowManager.hasNavigationBar(context.getDisplayId());
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static boolean isAppInactive(IUsageStatsManager iUsageStatsManager, String str, int i2, String str2) throws RemoteException {
        return iUsageStatsManager.isAppInactive(str, i2, str2);
    }

    public static boolean isDataEnabled(TelephonyManager telephonyManager) {
        return telephonyManager.isDataEnabled();
    }

    public static boolean isGpsEnabled(Context context) {
        return ((LocationManager) context.getSystemService("location")).isProviderEnabled("gps");
    }

    public static void removePowerSaveWhitelistApps(IDeviceIdleController iDeviceIdleController, String[] strArr) throws RemoteException {
        for (String str : strArr) {
            iDeviceIdleController.removePowerSaveWhitelistApp(str);
        }
    }

    public static void setDataEnabled(TelephonyManager telephonyManager, boolean z2) {
        telephonyManager.setDataEnabled(z2);
    }

    public static void setDeviceIdle(Context context, String str, String[] strArr, long[] jArr) {
        for (int i2 = 0; i2 < strArr.length; i2++) {
            Log.i(TAG, "WhetstoneActivityManager.setProperty " + strArr[i2] + "=" + jArr[i2]);
            WhetstoneActivityManager.setProperty(TrackData.DI_TRACK_NAME, strArr[i2], String.valueOf(jArr[i2]), false);
        }
    }

    public static void setGpsEnabled(Context context, boolean z2) {
        ((LocationManager) context.getSystemService("location")).setLocationEnabledForUser(z2, UserHandle.SYSTEM);
    }

    public static void setTouch(ITouchFeature iTouchFeature, int i2) {
        if (iTouchFeature != null) {
            iTouchFeature.setTouchMode(0, 13, i2);
        }
    }

    public static void setTouchPower(ITouchFeature iTouchFeature, int i2) {
        iTouchFeature.setTouchMode(0, 21, i2);
    }

    public static void registerDockedStackListener(IDockedStackListener iDockedStackListener) {
    }
}
