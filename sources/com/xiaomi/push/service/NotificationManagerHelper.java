package com.xiaomi.push.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.xmpush.thrift.ConfigKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes.dex */
public class NotificationManagerHelper {
    public static final String DEFAULT_ID = "default";
    private static final String NEW_FORMAT_PREFIX = "mipush|%s|%s";
    private static final String OLD_FORMAT_PREFIX = "mipush_%s_%s";
    private static final String TAG = "NMHelper";
    private static final String XMSF_FAKE_CONDITION_PROVIDER_PATH = "xmsf_fake_condition_provider_path";
    private static Context sAppContext;
    private static WeakHashMap<Integer, NotificationManagerHelper> sCacheInstance = new WeakHashMap<>();
    private static boolean sIsSupportFwk;
    private static Object sNMS;
    private String mDefaultAppChannelId;
    private String mTargetPkg;

    private NotificationManagerHelper(String str) {
        this.mTargetPkg = str;
    }

    public static String convertToOldAppChannelId(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String channelId = formatChannelId(NEW_FORMAT_PREFIX, str2, "");
        return str.startsWith(channelId) ? formatChannelId(OLD_FORMAT_PREFIX, str2, str.replace(channelId, "")) : str;
    }

    private static String formatChannelId(String str, String str2, String str3) {
        return TextUtils.isEmpty(str) ? "" : String.format(str, str2, str3);
    }

    public static NotificationManagerHelper from(Context context, String str) {
        initStaticFields(context);
        int iHashCode = str.hashCode();
        NotificationManagerHelper notificationManagerHelper = sCacheInstance.get(Integer.valueOf(iHashCode));
        if (notificationManagerHelper != null) {
            return notificationManagerHelper;
        }
        NotificationManagerHelper notificationManagerHelper2 = new NotificationManagerHelper(str);
        sCacheInstance.put(Integer.valueOf(iHashCode), notificationManagerHelper2);
        return notificationManagerHelper2;
    }

    private StatusBarNotification[] getAllNotifications() {
        if (!MIUIUtils.isXMSF(getAppContext())) {
            return null;
        }
        try {
            Object objCallMethod = JavaCalls.callMethod(sNMS, "getActiveNotifications", getAppContext().getPackageName());
            if (objCallMethod instanceof StatusBarNotification[]) {
                return (StatusBarNotification[]) objCallMethod;
            }
            return null;
        } catch (Throwable th) {
            outLog("getAllNotifications error " + th);
            return null;
        }
    }

    private static <T> T getListFromParceledListSlice(Object obj) {
        if (obj != null) {
            try {
                Class[] clsArr = new Class[0];
                return (T) obj.getClass().getMethod("getList", null).invoke(obj, null);
            } catch (Exception unused) {
            }
        }
        return null;
    }

    private String getMipushChannelId(String str) {
        return formatChannelId(isSupportFwk() ? NEW_FORMAT_PREFIX : OLD_FORMAT_PREFIX, this.mTargetPkg, str);
    }

    private static NotificationManager getNm() {
        return (NotificationManager) sAppContext.getSystemService("notification");
    }

    private static int getPkgUid(String str) {
        try {
            return sAppContext.getPackageManager().getPackageUid(str, 0);
        } catch (Exception unused) {
            return -1;
        }
    }

    private static void initStaticFields(Context context) {
        if (sAppContext == null) {
            sAppContext = context.getApplicationContext();
            NotificationManager nm = getNm();
            Boolean bool = (Boolean) JavaCalls.callMethod(nm, "isSystemConditionProviderEnabled", XMSF_FAKE_CONDITION_PROVIDER_PATH);
            outLog("fwk is support.init:" + bool);
            boolean zBooleanValue = bool != null ? bool.booleanValue() : false;
            sIsSupportFwk = zBooleanValue;
            if (zBooleanValue) {
                sNMS = JavaCalls.callMethod(nm, "getService", new Object[0]);
            }
        }
    }

    public static boolean isRomSupportNotificationBelongToApp(Context context) {
        initStaticFields(context);
        return isSupportFwk();
    }

    private static boolean isSupportFwk() {
        if (MIUIUtils.isMIUI() && OnlineConfig.getInstance(sAppContext).getBooleanValue(ConfigKey.NotificationBelongToAppSwitch.getValue(), true)) {
            return sIsSupportFwk;
        }
        return false;
    }

    private static Object newParceledListSlice(List list) throws Exception {
        return Class.forName("android.content.pm.ParceledListSlice").getConstructor(List.class).newInstance(list);
    }

    static void outLog(String str) {
        MyLog.w("NMHelper:" + str);
    }

    public void cancel(int i2) {
        String str = this.mTargetPkg;
        try {
            if (isSupportFwk()) {
                JavaCalls.callMethodOrThrow(sNMS, "cancelNotificationWithTag", str, getAppContext().getPackageName(), null, Integer.valueOf(i2), Integer.valueOf(DeviceInfo.getSpaceId()));
                outLog("cancel succ:" + i2);
            } else {
                getNm().cancel(i2);
            }
        } catch (Exception e2) {
            outLog("cancel error" + e2);
        }
    }

    @TargetApi(26)
    void createNotificationChannel(NotificationChannel notificationChannel) {
        String str = this.mTargetPkg;
        try {
            if (isSupportFwk()) {
                int pkgUid = getPkgUid(str);
                if (pkgUid != -1) {
                    JavaCalls.callMethodOrThrow(sNMS, "createNotificationChannelsForPackage", str, Integer.valueOf(pkgUid), newParceledListSlice(Arrays.asList(notificationChannel)));
                }
            } else {
                getNm().createNotificationChannel(notificationChannel);
            }
        } catch (Exception e2) {
            outLog("createNotificationChannel error" + e2);
        }
    }

    @TargetApi(26)
    void createNotificationChannelGroup(NotificationChannelGroup notificationChannelGroup) {
        String str = this.mTargetPkg;
        try {
            if (isSupportFwk()) {
                int pkgUid = getPkgUid(str);
                if (pkgUid != -1) {
                    JavaCalls.callMethodOrThrow(sNMS, "updateNotificationChannelGroupForPackage", str, Integer.valueOf(pkgUid), notificationChannelGroup);
                }
            } else {
                getNm().createNotificationChannelGroup(notificationChannelGroup);
            }
        } catch (Exception e2) {
            outLog("createNotificationChannelGroup error" + e2);
        }
    }

    @TargetApi(26)
    void deleteNotificationChannel(String str) {
        getNm().deleteNotificationChannel(str);
    }

    @TargetApi(26)
    void deleteNotificationChannelGroup(String str) {
        getNm().deleteNotificationChannelGroup(str);
    }

    public List<StatusBarNotification> getActiveNotifications() {
        String str = this.mTargetPkg;
        NotificationManager nm = getNm();
        List<StatusBarNotification> list = null;
        try {
            if (isSupportFwk()) {
                int spaceId = DeviceInfo.getSpaceId();
                if (spaceId != -1) {
                    list = (List) getListFromParceledListSlice(JavaCalls.callMethod(sNMS, "getAppActiveNotifications", str, Integer.valueOf(spaceId)));
                }
            } else {
                StatusBarNotification[] activeNotifications = nm.getActiveNotifications();
                if (activeNotifications != null && activeNotifications.length > 0) {
                    ArrayList arrayList = new ArrayList();
                    try {
                        for (StatusBarNotification statusBarNotification : activeNotifications) {
                            if (str.equals(NotificationUtils.getTargetPackage(statusBarNotification.getNotification()))) {
                                arrayList.add(statusBarNotification);
                            }
                        }
                        list = arrayList;
                    } catch (Throwable th) {
                        th = th;
                        list = arrayList;
                        outLog("getActiveNotifications error " + th);
                    }
                }
            }
        } catch (Throwable th2) {
            th = th2;
        }
        return list;
    }

    public String getAppChannelId(String str) {
        if (TextUtils.isEmpty(str)) {
            return getDefaultAppChannelId();
        }
        return MIUIUtils.isXMSF(getAppContext()) ? getMipushChannelId(str) : str;
    }

    public Context getAppContext() {
        return sAppContext;
    }

    String getDefaultAppChannelId() {
        if (TextUtils.isEmpty(this.mDefaultAppChannelId)) {
            this.mDefaultAppChannelId = getMipushChannelId("default");
        }
        return this.mDefaultAppChannelId;
    }

    String getGroupSummaryChannelId(String str, String str2) {
        return isSupportFwk() ? str : str2;
    }

    @TargetApi(26)
    public NotificationChannel getNotificationChannel(String str) {
        NotificationChannel notificationChannel = null;
        try {
            if (isSupportFwk()) {
                List<NotificationChannel> notificationChannels = getNotificationChannels();
                if (notificationChannels != null) {
                    for (NotificationChannel notificationChannel2 : notificationChannels) {
                        if (str.equals(notificationChannel2.getId())) {
                            notificationChannel = notificationChannel2;
                            break;
                        }
                    }
                }
            } else {
                notificationChannel = getNm().getNotificationChannel(str);
            }
        } catch (Exception e2) {
            outLog("getNotificationChannel error" + e2);
        }
        return notificationChannel;
    }

    @TargetApi(26)
    NotificationChannelGroup getNotificationChannelGroup(String str) {
        NotificationChannelGroup notificationChannelGroup;
        String str2 = this.mTargetPkg;
        NotificationManager nm = getNm();
        try {
            if (isSupportFwk()) {
                int pkgUid = getPkgUid(str2);
                if (pkgUid == -1) {
                    return null;
                }
                notificationChannelGroup = (NotificationChannelGroup) JavaCalls.callMethod(sNMS, "getNotificationChannelGroupForPackage", str, str2, Integer.valueOf(pkgUid));
            } else {
                notificationChannelGroup = nm.getNotificationChannelGroup(str);
            }
            return notificationChannelGroup;
        } catch (Exception e2) {
            outLog("getNotificationChannel error" + e2);
            return null;
        }
    }

    @TargetApi(26)
    List<NotificationChannelGroup> getNotificationChannelGroups() {
        try {
            if (isSupportFwk()) {
                return null;
            }
            return getNm().getNotificationChannelGroups();
        } catch (Exception e2) {
            outLog("getNotificationChannelGroups error " + e2);
            return null;
        }
    }

    /* JADX WARN: Unreachable blocks removed: 2, instructions: 2 */
    @TargetApi(26)
    public List<NotificationChannel> getNotificationChannels() {
        String str;
        String str2 = this.mTargetPkg;
        List<NotificationChannel> notificationChannels = null;
        try {
            if (isSupportFwk()) {
                int pkgUid = getPkgUid(str2);
                if (pkgUid != -1) {
                    List<NotificationChannel> list = (List) getListFromParceledListSlice(JavaCalls.callMethod(sNMS, "getNotificationChannelsForPackage", str2, Integer.valueOf(pkgUid), Boolean.FALSE));
                    str = NEW_FORMAT_PREFIX;
                    notificationChannels = list;
                } else {
                    str = null;
                }
            } else {
                notificationChannels = getNm().getNotificationChannels();
                str = OLD_FORMAT_PREFIX;
            }
            if (!MIUIUtils.isMIUI() || notificationChannels == null) {
                return notificationChannels;
            }
            ArrayList arrayList = new ArrayList();
            String channelId = formatChannelId(str, str2, "");
            for (NotificationChannel notificationChannel : notificationChannels) {
                if (notificationChannel.getId().startsWith(channelId)) {
                    arrayList.add(notificationChannel);
                }
            }
            return arrayList;
        } catch (Exception e2) {
            outLog("getNotificationChannels error " + e2);
            return notificationChannels;
        }
    }

    public String getTargetPkg() {
        return this.mTargetPkg;
    }

    public boolean isThisAppChannel(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.startsWith(getMipushChannelId(""));
    }

    public void notify(int i2, Notification notification) {
        String str = this.mTargetPkg;
        NotificationManager nm = getNm();
        try {
            if (isSupportFwk()) {
                notification.extras.putString("xmsf_target_package", str);
                nm.notifyAsPackage(str, null, i2, notification);
            } else {
                nm.notify(i2, notification);
            }
        } catch (Exception unused) {
        }
    }

    public String toString() {
        return "NotificationManagerHelper{" + this.mTargetPkg + "}";
    }

    void updateNotificationChannel(NotificationChannel notificationChannel, boolean z2) {
        String str = this.mTargetPkg;
        try {
            if (z2) {
                int pkgUid = getPkgUid(str);
                if (pkgUid != -1) {
                    JavaCalls.callMethodOrThrow(sNMS, "updateNotificationChannelForPackage", str, Integer.valueOf(pkgUid), notificationChannel);
                }
            } else {
                createNotificationChannel(notificationChannel);
            }
        } catch (Exception e2) {
            outLog("updateNotificationChannel error " + e2);
        }
    }
}
