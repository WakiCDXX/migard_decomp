package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.PushConstants;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import miui.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class ManifestChecker {

    public static class IllegalManifestException extends RuntimeException {
        private static final long serialVersionUID = 1;

        public IllegalManifestException(String str) {
            super(str);
        }
    }

    public static class ServiceCheckInfo {
        public boolean enabled;
        public boolean exported;
        public String permission;
        public String serviceName;

        public ServiceCheckInfo(String str, boolean z2, boolean z3, String str2) {
            this.serviceName = str;
            this.enabled = z2;
            this.exported = z3;
            this.permission = str2;
        }
    }

    public static void asynCheckManifest(final Context context) {
        new Thread(new Runnable() { // from class: com.xiaomi.mipush.sdk.ManifestChecker.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 4612);
                    ManifestChecker.checkReceivers(context);
                    ManifestChecker.checkServices(context, packageInfo);
                    ManifestChecker.checkPermissions(context, packageInfo);
                } catch (Throwable th) {
                    Log.e("ManifestChecker", "", th);
                }
            }
        }).start();
    }

    private static void checkAssembleReceiver(Context context, String str, String str2) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        Intent intent = new Intent(str);
        intent.setPackage(packageName);
        Iterator<ResolveInfo> it = packageManager.queryBroadcastReceivers(intent, DateUtils.FORMAT_ABBREV_TIME).iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            ActivityInfo activityInfo = it.next().activityInfo;
            z2 = (activityInfo == null || TextUtils.isEmpty(activityInfo.name) || !activityInfo.name.equals(str2)) ? false : true;
            if (z2) {
                break;
            }
        }
        if (!z2) {
            throw new IllegalManifestException(String.format("<receiver android:name=\"%1$s\" .../> is missing or disabled in AndroidManifest.", str2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkPermissions(Context context, PackageInfo packageInfo) {
        HashSet hashSet = new HashSet();
        String str = context.getPackageName() + ".permission.MIPUSH_RECEIVE";
        hashSet.addAll(Arrays.asList("android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", str, "android.permission.ACCESS_WIFI_STATE", "android.permission.VIBRATE"));
        PermissionInfo[] permissionInfoArr = packageInfo.permissions;
        if (permissionInfoArr != null) {
            for (PermissionInfo permissionInfo : permissionInfoArr) {
                if (str.equals(permissionInfo.name)) {
                    String[] strArr = packageInfo.requestedPermissions;
                    if (strArr != null) {
                        for (String str2 : strArr) {
                            if (!TextUtils.isEmpty(str2) && hashSet.contains(str2)) {
                                hashSet.remove(str2);
                                if (hashSet.isEmpty()) {
                                    break;
                                }
                            }
                        }
                    }
                    if (!hashSet.isEmpty()) {
                        throw new IllegalManifestException(String.format("<uses-permission android:name=\"%1$s\"/> is missing in AndroidManifest.", hashSet.iterator().next()));
                    }
                    return;
                }
            }
        }
        throw new IllegalManifestException(String.format("<permission android:name=\"%1$s\" .../> is undefined in AndroidManifest.", str));
    }

    private static void checkReceiverInfo(ActivityInfo activityInfo, Boolean[] boolArr) {
        if (boolArr[0].booleanValue() != activityInfo.enabled) {
            throw new IllegalManifestException(String.format("<receiver android:name=\"%1$s\" .../> in AndroidManifest had the wrong enabled attribute, which should be android:enabled=%2$b.", activityInfo.name, boolArr[0]));
        }
        if (boolArr[1].booleanValue() != activityInfo.exported) {
            throw new IllegalManifestException(String.format("<receiver android:name=\"%1$s\" .../> in AndroidManifest had the wrong exported attribute, which should be android:exported=%2$b.", activityInfo.name, boolArr[1]));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:45:0x009d A[EDGE_INSN: B:45:0x009d->B:31:0x009d BREAK  A[LOOP:0: B:16:0x0069->B:50:0x0069], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:51:0x0069 A[SYNTHETIC] */
    public static void checkReceivers(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        Intent intent = new Intent(PushConstants.ACTION_PING_TIMER);
        intent.setPackage(packageName);
        try {
            ActivityInfo activityInfoFindReceiverInfo = findReceiverInfo(packageManager, intent, SystemUtils.loadClass(context, PushConstants.PING_RECEIVER_CLASS_NAME_JAR));
            if (MiPushClient.shouldUseMIUIPush(context)) {
                if (activityInfoFindReceiverInfo != null) {
                    checkReceiverInfo(activityInfoFindReceiverInfo, new Boolean[]{Boolean.TRUE, Boolean.FALSE});
                }
            } else {
                if (activityInfoFindReceiverInfo == null) {
                    throw new IllegalManifestException(String.format("<receiver android:name=\"%1$s\" .../> is missing or disabled in AndroidManifest.", PushConstants.PING_RECEIVER_CLASS_NAME_JAR));
                }
                checkReceiverInfo(activityInfoFindReceiverInfo, new Boolean[]{Boolean.TRUE, Boolean.FALSE});
            }
        } catch (ClassNotFoundException e2) {
            MyLog.e(e2);
        }
        Intent intent2 = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
        intent2.setPackage(packageName);
        Iterator<ResolveInfo> it = packageManager.queryBroadcastReceivers(intent2, DateUtils.FORMAT_ABBREV_TIME).iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            ActivityInfo activityInfo = it.next().activityInfo;
            if (activityInfo != null) {
                try {
                    if (!TextUtils.isEmpty(activityInfo.name) && PushMessageReceiver.class.isAssignableFrom(SystemUtils.loadClass(context, activityInfo.name)) && activityInfo.enabled) {
                        z2 = true;
                    }
                    if (z2) {
                        break;
                    }
                } catch (ClassNotFoundException e3) {
                    MyLog.e(e3);
                }
            }
            z2 = false;
            if (z2) {
                break;
                break;
            }
        }
        if (!z2) {
            throw new IllegalManifestException("Receiver: none of the subclasses of PushMessageReceiver is enabled or defined.");
        }
        if (MiPushClient.getOpenHmsPush(context)) {
            checkAssembleReceiver(context, PushConstants.HMS_PUSH_ACTION_NEW_MESSAGE, PushConstants.HMS_PUSH_RECEIVER_CLASS_NAME);
            checkAssembleReceiver(context, PushConstants.HMS_OLD_PUSH_ACTION_NEW_MESSAGE, PushConstants.HMS_PUSH_OLD_RECEIVER_CLASS_NAME);
        }
        if (MiPushClient.getOpenVIVOPush(context)) {
            checkAssembleReceiver(context, PushConstants.VIVO_PUSH_MESSAGE_RECEIVER_ACTION_NAME, PushConstants.VIVO_PUSH_MESSAGE_RECEIVER_CLASS_NAME);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkServices(Context context, PackageInfo packageInfo) {
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();
        map2.put(PushMessageHandler.class.getCanonicalName(), new ServiceCheckInfo(PushMessageHandler.class.getCanonicalName(), true, true, ""));
        map2.put(MessageHandleService.class.getCanonicalName(), new ServiceCheckInfo(MessageHandleService.class.getCanonicalName(), true, false, ""));
        if (!MiPushClient.shouldUseMIUIPush(context) || containAnyService(packageInfo, new String[]{PushConstants.XM_SERVICE_CLASS_NAME_JAR, PushConstants.PUSH_SERVICE_CLASS_NAME_JAR})) {
            map2.put(PushConstants.XM_SERVICE_CLASS_NAME_JAR, new ServiceCheckInfo(PushConstants.XM_SERVICE_CLASS_NAME_JAR, true, false, "android.permission.BIND_JOB_SERVICE"));
            map2.put(PushConstants.PUSH_SERVICE_CLASS_NAME_JAR, new ServiceCheckInfo(PushConstants.PUSH_SERVICE_CLASS_NAME_JAR, true, false, ""));
        }
        if (MiPushClient.getOpenFCMPush(context)) {
            map2.put(PushConstants.FCM_PUSH_INSTANCE_ID_SERVICE_NAME, new ServiceCheckInfo(PushConstants.FCM_PUSH_INSTANCE_ID_SERVICE_NAME, true, false, ""));
            map2.put(PushConstants.FCM_PUSH_MESSAGE_SERVICE_NAME, new ServiceCheckInfo(PushConstants.FCM_PUSH_MESSAGE_SERVICE_NAME, true, false, ""));
        }
        if (MiPushClient.getOpenOPPOPush(context)) {
            map2.put(PushConstants.OPPO_PUSH_MESSAGE_SERVICE_NAME, new ServiceCheckInfo(PushConstants.OPPO_PUSH_MESSAGE_SERVICE_NAME, true, true, "com.coloros.mcs.permission.SEND_MCS_MESSAGE"));
        }
        ServiceInfo[] serviceInfoArr = packageInfo.services;
        if (serviceInfoArr != null) {
            for (ServiceInfo serviceInfo : serviceInfoArr) {
                if (!TextUtils.isEmpty(serviceInfo.name) && map2.containsKey(serviceInfo.name)) {
                    ServiceCheckInfo serviceCheckInfo = (ServiceCheckInfo) map2.remove(serviceInfo.name);
                    boolean z2 = serviceCheckInfo.enabled;
                    boolean z3 = serviceCheckInfo.exported;
                    String str = serviceCheckInfo.permission;
                    if (z2 != serviceInfo.enabled) {
                        throw new IllegalManifestException(String.format("<service android:name=\"%1$s\" .../> in AndroidManifest had the wrong enabled attribute, which should be android:enabled=%2$b.", serviceInfo.name, Boolean.valueOf(z2)));
                    }
                    if (z3 != serviceInfo.exported) {
                        throw new IllegalManifestException(String.format("<service android:name=\"%1$s\" .../> in AndroidManifest had the wrong exported attribute, which should be android:exported=%2$b.", serviceInfo.name, Boolean.valueOf(z3)));
                    }
                    if (!TextUtils.isEmpty(str) && !TextUtils.equals(str, serviceInfo.permission)) {
                        throw new IllegalManifestException(String.format("<service android:name=\"%1$s\" .../> in AndroidManifest had the wrong permission attribute, which should be android:permission=\"%2$s\".", serviceInfo.name, str));
                    }
                    map.put(serviceInfo.name, serviceInfo.processName);
                    if (map2.isEmpty()) {
                        break;
                    }
                }
            }
        }
        if (!map2.isEmpty()) {
            throw new IllegalManifestException(String.format("<service android:name=\"%1$s\" .../> is missing or disabled in AndroidManifest.", map2.keySet().iterator().next()));
        }
        if (!TextUtils.equals((CharSequence) map.get(PushMessageHandler.class.getCanonicalName()), (CharSequence) map.get(MessageHandleService.class.getCanonicalName()))) {
            throw new IllegalManifestException(String.format("\"%1$s\" and \"%2$s\" must be running in the same process.", PushMessageHandler.class.getCanonicalName(), MessageHandleService.class.getCanonicalName()));
        }
        if (map.containsKey(PushConstants.XM_SERVICE_CLASS_NAME_JAR) && map.containsKey(PushConstants.PUSH_SERVICE_CLASS_NAME_JAR) && !TextUtils.equals((CharSequence) map.get(PushConstants.XM_SERVICE_CLASS_NAME_JAR), (CharSequence) map.get(PushConstants.PUSH_SERVICE_CLASS_NAME_JAR))) {
            throw new IllegalManifestException(String.format("\"%1$s\" and \"%2$s\" must be running in the same process.", PushConstants.XM_SERVICE_CLASS_NAME_JAR, PushConstants.PUSH_SERVICE_CLASS_NAME_JAR));
        }
    }

    private static boolean containAnyService(PackageInfo packageInfo, String[] strArr) {
        for (ServiceInfo serviceInfo : packageInfo.services) {
            if (containString(strArr, serviceInfo.name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containString(String[] strArr, String str) {
        if (strArr != null && str != null) {
            for (String str2 : strArr) {
                if (TextUtils.equals(str2, str)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ActivityInfo findReceiverInfo(PackageManager packageManager, Intent intent, Class<?> cls) {
        Iterator<ResolveInfo> it = packageManager.queryBroadcastReceivers(intent, DateUtils.FORMAT_ABBREV_TIME).iterator();
        while (it.hasNext()) {
            ActivityInfo activityInfo = it.next().activityInfo;
            if (activityInfo != null && cls.getCanonicalName().equals(activityInfo.name)) {
                return activityInfo;
            }
        }
        return null;
    }
}
