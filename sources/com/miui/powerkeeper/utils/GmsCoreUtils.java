package com.miui.powerkeeper.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GmsCoreUtils {
    private static final List<String> GOOGLE_APPS;
    private static final String[] GOOGLE_APPS_ARRAY = {"com.google.android.gm", "com.google.android.apps.magazines", "com.google.android.apps.books", "com.google.android.apps.plus", "com.google.android.videos", "com.google.android.apps.docs", "com.google.android.youtube", "com.google.android.play.games", "com.google.android.apps.photos", "com.google.android.talk", "com.google.android.music", "com.google.android.apps.maps"};
    private static final List<String> GOOGLE_OTHERS;
    private static final String TAG = "GmsCoreUtils";

    static {
        ArrayList arrayList = new ArrayList();
        GOOGLE_APPS = arrayList;
        arrayList.add("com.android.vending");
        arrayList.add("com.google.android.gms");
        arrayList.add("com.google.android.gsf");
        ArrayList arrayList2 = new ArrayList();
        GOOGLE_OTHERS = arrayList2;
        arrayList2.add("com.google.android.syncadapters.contacts");
        arrayList2.add("com.google.android.backuptransport");
        arrayList2.add("com.google.android.onetimeinitializer");
        arrayList2.add("com.google.android.partnersetup");
        arrayList2.add("com.google.android.configupdater");
        arrayList2.add("com.google.android.ext.services");
        arrayList2.add("com.google.android.ext.shared");
        arrayList2.add("com.google.android.printservice.recommendation");
    }

    public static boolean isGmsCoreApp(String str) {
        return GOOGLE_APPS.contains(str);
    }

    public static boolean isInstalledGoogleApps(Context context) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        ArrayList arrayList = new ArrayList();
        if (installedPackages != null && !installedPackages.isEmpty()) {
            for (PackageInfo packageInfo : installedPackages) {
                if (packageInfo != null) {
                    arrayList.add(packageInfo.packageName);
                }
            }
        }
        if (context.getPackageManager().getLaunchIntentForPackage("com.android.vending") != null) {
            return true;
        }
        for (String str : GOOGLE_APPS_ARRAY) {
            if (arrayList.contains(str)) {
                return true;
            }
        }
        return false;
    }

    private static void killGmsCoreProcess(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        Iterator<String> it = GOOGLE_APPS.iterator();
        while (it.hasNext()) {
            activityManager.forceStopPackage(it.next());
        }
        for (String str : GOOGLE_OTHERS) {
            if (context.getPackageManager().isPackageAvailable(str)) {
                activityManager.forceStopPackage(str);
            }
        }
    }

    public static void setGmsCoreState(Context context, int i2) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            Log.e(TAG, "PackageManager is null!");
            return;
        }
        try {
            Iterator<String> it = GOOGLE_APPS.iterator();
            while (it.hasNext()) {
                packageManager.setApplicationEnabledSetting(it.next(), i2, 1);
            }
            for (String str : GOOGLE_OTHERS) {
                if (packageManager.isPackageAvailable(str)) {
                    packageManager.setApplicationEnabledSetting(str, i2, 1);
                }
            }
            if (2 == i2) {
                killGmsCoreProcess(context);
            }
        } catch (IllegalArgumentException e2) {
            Log.e(TAG, "googleApps don't exist! ", e2);
        }
    }
}
