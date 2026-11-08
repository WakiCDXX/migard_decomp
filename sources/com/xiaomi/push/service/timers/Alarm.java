package com.xiaomi.push.service.timers;

import android.content.Context;
import android.content.pm.ServiceInfo;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.mpcd.Constants;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.XMJobService;

/* JADX INFO: loaded from: classes.dex */
public final class Alarm {
    public static final int HYBRID_ALARM = 2;
    public static final int SYSTEM_ALARM = 0;
    private static final String XMSERVICE_PERMISSION = "android.permission.BIND_JOB_SERVICE";
    private static IAlarm sAlarmInstance;
    private static final String XMSERVICE = XMJobService.class.getCanonicalName();
    private static int sLevel = 0;

    interface IAlarm {
        boolean isAlive();

        void registerPing(boolean z2);

        void stop();
    }

    public static synchronized void changePolicy(Context context, int i2) {
        try {
            int i3 = sLevel;
            if (!PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName())) {
                if (i2 == 2) {
                    sLevel = 2;
                } else {
                    sLevel = 0;
                }
            }
            int i4 = sLevel;
            if (i3 != i4 && i4 == 2) {
                stop();
                sAlarmInstance = new HybridTimer(context);
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:15:0x0046  */
    public static void initialize(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(applicationContext.getPackageName())) {
            sAlarmInstance = new AlarmManagerTimer(applicationContext);
            return;
        }
        int i2 = 0;
        try {
            ServiceInfo[] serviceInfoArr = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 4).services;
            if (serviceInfoArr != null) {
                int length = serviceInfoArr.length;
                int i3 = 0;
                while (true) {
                    if (i2 < length) {
                        try {
                            ServiceInfo serviceInfo = serviceInfoArr[i2];
                            if (XMSERVICE_PERMISSION.equals(serviceInfo.permission)) {
                                String str = XMSERVICE;
                                if (str.equals(serviceInfo.name)) {
                                    i3 = 1;
                                } else {
                                    try {
                                        if (str.equals(SystemUtils.loadClass(applicationContext, serviceInfo.name).getSuperclass().getCanonicalName())) {
                                            i3 = 1;
                                        }
                                    } catch (Exception unused) {
                                    }
                                }
                                if (i3 == 1) {
                                }
                            }
                            if (XMSERVICE.equals(serviceInfo.name) && XMSERVICE_PERMISSION.equals(serviceInfo.permission)) {
                                i2 = 1;
                                break;
                            }
                            i2++;
                        } catch (Exception e2) {
                            e = e2;
                            i2 = i3;
                            MyLog.w("check service err : " + e.getMessage());
                        }
                    }
                    i2 = i3;
                    break;
                }
            }
        } catch (Exception e3) {
            e = e3;
        }
        if (i2 != 0 || !SystemUtils.isDebuggable(applicationContext)) {
            sAlarmInstance = new AlarmManagerTimer(applicationContext);
            return;
        }
        throw new RuntimeException("Should export service: " + XMSERVICE + " with permission " + XMSERVICE_PERMISSION + " in AndroidManifest.xml file");
    }

    public static synchronized boolean isAlive() {
        IAlarm iAlarm = sAlarmInstance;
        if (iAlarm == null) {
            return false;
        }
        return iAlarm.isAlive();
    }

    public static synchronized void registerPing(boolean z2) {
        if (sAlarmInstance == null) {
            MyLog.w("timer is not initialized");
            return;
        }
        MyLog.w("[Alarm] register alarm. (" + z2 + Constants.SEPARATOR_RIGHT_PARENTESIS);
        sAlarmInstance.registerPing(z2);
    }

    public static synchronized void stop() {
        if (sAlarmInstance == null) {
            return;
        }
        MyLog.w("[Alarm] stop alarm.");
        sAlarmInstance.stop();
    }
}
