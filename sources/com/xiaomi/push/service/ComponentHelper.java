package com.xiaomi.push.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ComponentHelper {
    public static boolean checkActivity(Context context, String str, String str2) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(str2);
            intent.setPackage(str);
            return packageManager.resolveActivity(intent, LogType.LOG_TYPE_SAMPLING_INTERRUPT) != null;
        } catch (Exception e2) {
            MyLog.w("checkActivity action: " + str2 + ", " + e2);
            return false;
        }
    }

    public static ComponentName checkAndGetComponent(Context context, Intent intent) {
        if (intent == null) {
            return null;
        }
        try {
            ResolveInfo resolveInfoResolveActivity = context.getPackageManager().resolveActivity(intent, LogType.LOG_TYPE_SAMPLING_INTERRUPT);
            if (resolveInfoResolveActivity != null) {
                return new ComponentName(resolveInfoResolveActivity.activityInfo.packageName, TextUtils.isEmpty(resolveInfoResolveActivity.activityInfo.targetActivity) ? resolveInfoResolveActivity.activityInfo.name : resolveInfoResolveActivity.activityInfo.targetActivity);
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public static boolean checkProvider(Context context, String str) {
        boolean z2 = false;
        try {
            List<ProviderInfo> listQueryContentProviders = context.getPackageManager().queryContentProviders((String) null, 0, 8);
            if (listQueryContentProviders != null && !listQueryContentProviders.isEmpty()) {
                for (ProviderInfo providerInfo : listQueryContentProviders) {
                    if (providerInfo.enabled && providerInfo.exported && providerInfo.authority.equals(str)) {
                        z2 = true;
                    }
                }
            }
        } catch (Exception e2) {
            MyLog.w("checkProvider " + e2);
        }
        return z2;
    }

    public static boolean checkService(Context context, String str) {
        try {
            ServiceInfo[] serviceInfoArr = context.getPackageManager().getPackageInfo(str, 4).services;
            if (serviceInfoArr == null) {
                return false;
            }
            for (ServiceInfo serviceInfo : serviceInfoArr) {
                if (serviceInfo.exported && serviceInfo.enabled && "com.xiaomi.mipush.sdk.PushMessageHandler".equals(serviceInfo.name) && !context.getPackageName().equals(serviceInfo.packageName)) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e2) {
            MyLog.w("checkService " + e2);
            return false;
        }
    }

    public static boolean checkActivity(Context context, ComponentName componentName) {
        try {
            new Intent().setComponent(componentName);
            context.getPackageManager().getActivityInfo(componentName, 128);
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean checkService(Context context, String str, String str2) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(str2);
            intent.setPackage(str);
            List<ResolveInfo> listQueryIntentServices = packageManager.queryIntentServices(intent, 32);
            return (listQueryIntentServices == null || listQueryIntentServices.isEmpty()) ? false : true;
        } catch (Exception e2) {
            MyLog.w("checkService action: " + str2 + ", " + e2);
            return false;
        }
    }
}
