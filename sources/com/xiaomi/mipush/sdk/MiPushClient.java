package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.PreferenceUtils;
import com.xiaomi.channel.commonutils.android.ReceiverHelper;
import com.xiaomi.channel.commonutils.android.SharedPrefsCompat;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.msa.MsaIdManager;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.clientreport.data.Config;
import com.xiaomi.clientreport.manager.ClientReportClient;
import com.xiaomi.clientreport.manager.ClientReportLogicManager;
import com.xiaomi.push.crash.SdkCrashManager;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.mpcd.CDActionProviderHolder;
import com.xiaomi.push.mpcd.CDEntrance;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.push.service.PacketHelper;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.clientReport.MIPushEventDataProcessor;
import com.xiaomi.push.service.clientReport.MIPushPerfDataProcessor;
import com.xiaomi.push.service.clientReport.PushClientReportHelper;
import com.xiaomi.push.service.receivers.NetworkStatusReceiver;
import com.xiaomi.push.service.xmpush.Command;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import com.xiaomi.xmpush.thrift.ConfigKey;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.RegistrationReason;
import com.xiaomi.xmpush.thrift.XmPushActionCommand;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushActionRegistration;
import com.xiaomi.xmpush.thrift.XmPushActionSubscription;
import com.xiaomi.xmpush.thrift.XmPushActionUnRegistration;
import com.xiaomi.xmpush.thrift.XmPushActionUnSubscription;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public abstract class MiPushClient {
    private static final int CD_DELAY = 10;
    private static final int CHECK_VERSION_DELAY = 5;
    public static final String COMMAND_REGISTER = "register";
    public static final String COMMAND_SET_ACCEPT_TIME = "accept-time";
    public static final String COMMAND_SET_ACCOUNT = "set-account";
    public static final String COMMAND_SET_ALIAS = "set-alias";
    public static final String COMMAND_SUBSCRIBE_TOPIC = "subscribe-topic";
    public static final String COMMAND_UNREGISTER = "unregister";
    public static final String COMMAND_UNSET_ACCOUNT = "unset-account";
    public static final String COMMAND_UNSET_ALIAS = "unset-alias";
    public static final String COMMAND_UNSUBSCRIBE_TOPIC = "unsubscibe-topic";
    private static final String DEFAULT_ACCEPT_TIME = "00:00-23:59";
    private static final String LAST_PULL_NOTIFICATION = "last_pull_notification";
    private static final String LAST_REG_REQUEST = "last_reg_request";
    private static final String PREFIX_ACCOUNT = "account_";
    private static final String PREFIX_ALIAS = "alias_";
    private static final String PREFIX_TOPIC = "topic_";
    public static final String PREF_EXTRA = "mipush_extra";
    private static final String TOPIC_ALL = "**ALL**";
    private static Context sContext;
    private static long sCurMsgId = System.currentTimeMillis();

    public static class CodeResult {
        private long resultCode = -1;

        public long getResultCode() {
            return this.resultCode;
        }

        protected void setResultCode(long j2) {
            this.resultCode = j2;
        }
    }

    public interface ICallbackResult<R> {
        void onResult(R r2);
    }

    @Deprecated
    public static abstract class MiPushClientCallback {
        private String category;

        protected String getCategory() {
            return this.category;
        }

        public void onReceiveMessage(MiPushMessage miPushMessage) {
        }

        protected void setCategory(String str) {
            this.category = str;
        }

        public void onReceiveMessage(String str, String str2, String str3, boolean z2) {
        }

        public void onInitializeResult(long j2, String str, String str2) {
        }

        public void onSubscribeResult(long j2, String str, String str2) {
        }

        public void onUnsubscribeResult(long j2, String str, String str2) {
        }

        public void onCommandResult(String str, long j2, String str2, List<String> list) {
        }
    }

    public static class TokenResult {
        private String token = null;
        private long resultCode = -1;

        public long getResultCode() {
            return this.resultCode;
        }

        public String getToken() {
            return this.token;
        }

        protected void setResultCode(long j2) {
            this.resultCode = j2;
        }

        protected void setToken(String str) {
            this.token = str;
        }
    }

    public interface UPSRegisterCallBack extends ICallbackResult<TokenResult> {
    }

    public interface UPSTurnCallBack extends ICallbackResult<CodeResult> {
    }

    public interface UPSUnRegisterCallBack extends ICallbackResult<TokenResult> {
    }

    private static boolean acceptTimeSet(Context context, String str, String str2) {
        return TextUtils.equals(getAcceptTime(context), str + "," + str2);
    }

    public static long accountSetTime(Context context, String str) {
        return context.getSharedPreferences("mipush_extra", 0).getLong(PREFIX_ACCOUNT + str, -1L);
    }

    static synchronized void addAcceptTime(Context context, String str, String str2) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("mipush_extra", 0).edit();
        editorEdit.putString(Constants.EXTRA_KEY_ACCEPT_TIME, str + "," + str2);
        SharedPrefsCompat.apply(editorEdit);
    }

    static synchronized void addAccount(Context context, String str) {
        context.getSharedPreferences("mipush_extra", 0).edit().putLong(PREFIX_ACCOUNT + str, System.currentTimeMillis()).commit();
    }

    static synchronized void addAlias(Context context, String str) {
        context.getSharedPreferences("mipush_extra", 0).edit().putLong(PREFIX_ALIAS + str, System.currentTimeMillis()).commit();
    }

    private static void addPullNotificationTime(Context context) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("mipush_extra", 0).edit();
        editorEdit.putLong(LAST_PULL_NOTIFICATION, System.currentTimeMillis());
        SharedPrefsCompat.apply(editorEdit);
    }

    private static void addRegRequestTime(Context context) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("mipush_extra", 0).edit();
        editorEdit.putLong(LAST_REG_REQUEST, System.currentTimeMillis());
        SharedPrefsCompat.apply(editorEdit);
    }

    static synchronized void addTopic(Context context, String str) {
        context.getSharedPreferences("mipush_extra", 0).edit().putLong(PREFIX_TOPIC + str, System.currentTimeMillis()).commit();
    }

    public static long aliasSetTime(Context context, String str) {
        return context.getSharedPreferences("mipush_extra", 0).getLong(PREFIX_ALIAS + str, -1L);
    }

    public static void awakeApps(final Context context, final String[] strArr) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.mipush.sdk.MiPushClient.4
            @Override // java.lang.Runnable
            public void run() {
                PackageInfo packageInfo;
                try {
                    for (String str : strArr) {
                        if (!TextUtils.isEmpty(str) && (packageInfo = context.getPackageManager().getPackageInfo(str, 4)) != null) {
                            MiPushClient.awakePushServiceByPackageInfo(context, packageInfo);
                        }
                    }
                } catch (Throwable th) {
                    MyLog.e(th);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void awakePushServiceByPackageInfo(Context context, PackageInfo packageInfo) {
        ServiceInfo[] serviceInfoArr = packageInfo.services;
        if (serviceInfoArr != null) {
            for (ServiceInfo serviceInfo : serviceInfoArr) {
                if (serviceInfo.exported && serviceInfo.enabled && "com.xiaomi.mipush.sdk.PushMessageHandler".equals(serviceInfo.name) && !context.getPackageName().equals(serviceInfo.packageName)) {
                    try {
                        Thread.sleep(((long) ((Math.random() * 2.0d) + 1.0d)) * 1000);
                        Intent intent = new Intent();
                        intent.setClassName(serviceInfo.packageName, serviceInfo.name);
                        intent.setAction(PushConstants.ACTION_WAKEUP);
                        intent.putExtra(PushConstants.ACTION_WAKER_PKGNAME, context.getPackageName());
                        PushMessageHandler.addJob(context, intent);
                        return;
                    } catch (Throwable unused) {
                        return;
                    }
                }
            }
        }
    }

    private static void checkNotNull(Object obj, Object obj2) {
    }

    protected static void clearExtras(Context context) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("mipush_extra", 0).edit();
        editorEdit.clear();
        editorEdit.commit();
    }

    private static void clearExtrasForInitialize(Context context) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("mipush_extra", 0).edit();
        Iterator<String> it = getAllAlias(context).iterator();
        while (it.hasNext()) {
            editorEdit.remove(PREFIX_ALIAS + it.next());
        }
        Iterator<String> it2 = getAllUserAccount(context).iterator();
        while (it2.hasNext()) {
            editorEdit.remove(PREFIX_ACCOUNT + it2.next());
        }
        Iterator<String> it3 = getAllTopic(context).iterator();
        while (it3.hasNext()) {
            editorEdit.remove(PREFIX_TOPIC + it3.next());
        }
        editorEdit.remove(Constants.EXTRA_KEY_ACCEPT_TIME);
        editorEdit.commit();
    }

    public static void clearLocalNotificationType(Context context) {
        PushServiceClient.getInstance(context).clearLocalNotificationType();
    }

    public static void clearNotification(Context context, int i2) {
        PushServiceClient.getInstance(context).clearNotification(i2);
    }

    public static void disablePush(Context context) {
        PushServiceClient.getInstance(context).sendPushEnableDisableMessage(true);
    }

    public static void enablePush(Context context) {
        PushServiceClient.getInstance(context).sendPushEnableDisableMessage(false);
    }

    protected static String getAcceptTime(Context context) {
        return context.getSharedPreferences("mipush_extra", 0).getString(Constants.EXTRA_KEY_ACCEPT_TIME, DEFAULT_ACCEPT_TIME);
    }

    public static List<String> getAllAlias(Context context) {
        ArrayList arrayList = new ArrayList();
        for (String str : context.getSharedPreferences("mipush_extra", 0).getAll().keySet()) {
            if (str.startsWith(PREFIX_ALIAS)) {
                arrayList.add(str.substring(6));
            }
        }
        return arrayList;
    }

    public static List<String> getAllTopic(Context context) {
        ArrayList arrayList = new ArrayList();
        for (String str : context.getSharedPreferences("mipush_extra", 0).getAll().keySet()) {
            if (str.startsWith(PREFIX_TOPIC) && !str.contains(TOPIC_ALL)) {
                arrayList.add(str.substring(6));
            }
        }
        return arrayList;
    }

    public static List<String> getAllUserAccount(Context context) {
        ArrayList arrayList = new ArrayList();
        for (String str : context.getSharedPreferences("mipush_extra", 0).getAll().keySet()) {
            if (str.startsWith(PREFIX_ACCOUNT)) {
                arrayList.add(str.substring(8));
            }
        }
        return arrayList;
    }

    public static String getAppRegion(Context context) {
        if (AppInfoHolder.getInstance(context).isRegistrationValid()) {
            return AppInfoHolder.getInstance(context).getAppRegion();
        }
        return null;
    }

    private static boolean getDefaultSwitch() {
        return MIUIUtils.isNotMIUI();
    }

    protected static boolean getOpenFCMPush(Context context) {
        checkNotNull((Object) context, "context");
        return AssemblePushCollectionsManager.getInstance(context).getUserSwitch(AssemblePush.ASSEMBLE_PUSH_FCM);
    }

    protected static boolean getOpenHmsPush(Context context) {
        checkNotNull((Object) context, "context");
        return AssemblePushCollectionsManager.getInstance(context).getUserSwitch(AssemblePush.ASSEMBLE_PUSH_HUAWEI);
    }

    protected static boolean getOpenOPPOPush(Context context) {
        checkNotNull((Object) context, "context");
        return AssemblePushCollectionsManager.getInstance(context).getUserSwitch(AssemblePush.ASSEMBLE_PUSH_COS);
    }

    protected static boolean getOpenVIVOPush(Context context) {
        return AssemblePushCollectionsManager.getInstance(context).getUserSwitch(AssemblePush.ASSEMBLE_PUSH_FTOS);
    }

    public static String getRegId(Context context) {
        if (AppInfoHolder.getInstance(context).isRegistrationValid()) {
            return AppInfoHolder.getInstance(context).getRegID();
        }
        return null;
    }

    private static void initEventPerfLogic(final Context context) {
        PushClientReportHelper.setUploader(new PushClientReportHelper.Uploader() { // from class: com.xiaomi.mipush.sdk.MiPushClient.5
            @Override // com.xiaomi.push.service.clientReport.PushClientReportHelper.Uploader
            public void uploader(Context context2, ClientUploadDataItem clientUploadDataItem) {
                MiTinyDataClient.upload(context2, clientUploadDataItem);
            }
        });
        Config config = PushClientReportHelper.getConfig(context);
        ClientReportLogicManager.getInstance(context).prepareInit(PushConstants.PUSH_VERSION_NAME);
        ClientReportClient.init(context, config, new MIPushEventDataProcessor(context), new MIPushPerfDataProcessor(context));
        ActivityLifecycleCallbacksForCR.forceAttachApplication(context);
        ClientReportHelper.sendConfigInfo(context, config);
        OnlineConfig.getInstance(context).addOCUpdateCallbacks(new OnlineConfig.OCUpdateCallback(100, "perf event job update") { // from class: com.xiaomi.mipush.sdk.MiPushClient.6
            @Override // com.xiaomi.push.service.OnlineConfig.OCUpdateCallback
            protected void onCallback() {
                PushClientReportHelper.checkConfigChange(context);
            }
        });
    }

    @Deprecated
    public static void initialize(Context context, String str, String str2, MiPushClientCallback miPushClientCallback) {
        initialize(context, str, str2, miPushClientCallback, null, null);
    }

    private static void operateSyncAction(Context context) {
        if (OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(sContext).getSyncStatus(RetryType.DISABLE_PUSH))) {
            disablePush(sContext);
        }
        if (OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(sContext).getSyncStatus(RetryType.ENABLE_PUSH))) {
            enablePush(sContext);
        }
        OperatePushHelper operatePushHelper = OperatePushHelper.getInstance(sContext);
        RetryType retryType = RetryType.UPLOAD_HUAWEI_TOKEN;
        if (OperatePushHelper.SYNCING.equals(operatePushHelper.getSyncStatus(retryType))) {
            PushServiceClient.getInstance(sContext).sendAssemblePushTokenCommon(null, retryType, AssemblePush.ASSEMBLE_PUSH_HUAWEI, AssemblePushUtils.REASON_SYNC_TOKEN_INIT);
        }
        if (OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(sContext).getSyncStatus(RetryType.UPLOAD_FCM_TOKEN))) {
            syncAssembleFCMPushToken(sContext);
        }
        OperatePushHelper operatePushHelper2 = OperatePushHelper.getInstance(sContext);
        RetryType retryType2 = RetryType.UPLOAD_COS_TOKEN;
        if (OperatePushHelper.SYNCING.equals(operatePushHelper2.getSyncStatus(retryType2))) {
            PushServiceClient.getInstance(sContext).sendAssemblePushTokenCommon(null, retryType2, AssemblePush.ASSEMBLE_PUSH_COS, AssemblePushUtils.REASON_SYNC_TOKEN_INIT);
        }
        OperatePushHelper operatePushHelper3 = OperatePushHelper.getInstance(sContext);
        RetryType retryType3 = RetryType.UPLOAD_FTOS_TOKEN;
        if (OperatePushHelper.SYNCING.equals(operatePushHelper3.getSyncStatus(retryType3))) {
            PushServiceClient.getInstance(context).sendAssemblePushTokenCommon(null, retryType3, AssemblePush.ASSEMBLE_PUSH_FTOS, AssemblePushUtils.REASON_SYNC_TOKEN_INIT);
        }
    }

    public static void pausePush(Context context, String str) {
        setAcceptTime(context, 0, 0, 0, 0, str);
    }

    static void reInitialize(Context context, RegistrationReason registrationReason) {
        MyLog.persist("re-register reason: " + registrationReason);
        String strGenerateRandomString = XMStringUtils.generateRandomString(6);
        String appID = AppInfoHolder.getInstance(context).getAppID();
        String appToken = AppInfoHolder.getInstance(context).getAppToken();
        AppInfoHolder.getInstance(context).clear();
        clearExtrasForInitialize(context);
        clearNotification(context);
        AppInfoHolder.getInstance(context).setEnvType(Constants.getEnvType());
        AppInfoHolder.getInstance(context).putAppIDAndToken(appID, appToken, strGenerateRandomString);
        XmPushActionRegistration xmPushActionRegistration = new XmPushActionRegistration();
        xmPushActionRegistration.setId(PacketHelper.generatePacketIdForRegistration());
        xmPushActionRegistration.setAppId(appID);
        xmPushActionRegistration.setToken(appToken);
        xmPushActionRegistration.setDeviceId(strGenerateRandomString);
        xmPushActionRegistration.setPackageName(context.getPackageName());
        xmPushActionRegistration.setAppVersion(AppInfoUtils.getVersionName(context, context.getPackageName()));
        xmPushActionRegistration.setAppVersionCode(AppInfoUtils.getVersionCode(context, context.getPackageName()));
        xmPushActionRegistration.setPushSdkVersionName(PushConstants.PUSH_VERSION_NAME);
        xmPushActionRegistration.setPushSdkVersionCode(PushConstants.PUSH_VERSION_CODE);
        xmPushActionRegistration.setReason(registrationReason);
        int spaceId = DeviceInfo.getSpaceId();
        if (spaceId >= 0) {
            xmPushActionRegistration.setSpaceId(spaceId);
        }
        PushServiceClient.getInstance(context).register(xmPushActionRegistration, false);
    }

    @Deprecated
    public static void registerCrashHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }

    private static void registerNetworkReceiver(Context context) {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            intentFilter.addCategory("android.intent.category.DEFAULT");
            ReceiverHelper.registerReceiver(context.getApplicationContext(), new NetworkStatusReceiver(null), intentFilter, 2);
        } catch (Throwable th) {
            MyLog.w("dynamic register network status receiver failed:" + th);
        }
        Network.addNetworkCallBack(sContext);
    }

    public static void registerPush(Context context, String str, String str2) {
        registerPush(context, str, str2, new PushConfiguration());
    }

    public static void registerToken(Context context, String str, String str2, String str3, UPSRegisterCallBack uPSRegisterCallBack) {
        registerPush(context, str, str2, new PushConfiguration(), null, uPSRegisterCallBack);
    }

    static synchronized void removeAcceptTime(Context context) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("mipush_extra", 0).edit();
        editorEdit.remove(Constants.EXTRA_KEY_ACCEPT_TIME);
        SharedPrefsCompat.apply(editorEdit);
    }

    static synchronized void removeAccount(Context context, String str) {
        context.getSharedPreferences("mipush_extra", 0).edit().remove(PREFIX_ACCOUNT + str).commit();
    }

    static synchronized void removeAlias(Context context, String str) {
        context.getSharedPreferences("mipush_extra", 0).edit().remove(PREFIX_ALIAS + str).commit();
    }

    static synchronized void removeAllAccounts(Context context) {
        Iterator<String> it = getAllUserAccount(context).iterator();
        while (it.hasNext()) {
            removeAccount(context, it.next());
        }
    }

    static synchronized void removeAllAliases(Context context) {
        Iterator<String> it = getAllAlias(context).iterator();
        while (it.hasNext()) {
            removeAlias(context, it.next());
        }
    }

    static synchronized void removeAllTopics(Context context) {
        Iterator<String> it = getAllTopic(context).iterator();
        while (it.hasNext()) {
            removeTopic(context, it.next());
        }
    }

    static synchronized void removeTopic(Context context, String str) {
        context.getSharedPreferences("mipush_extra", 0).edit().remove(PREFIX_TOPIC + str).commit();
    }

    public static void removeWindow(Context context) {
        PushServiceClient.getInstance(context).clearHeadsUpNotification();
    }

    public static void reportAppRunInBackground(Context context, boolean z2) {
        if (AppInfoHolder.getInstance(context).checkAppInfo()) {
            NotificationType notificationType = z2 ? NotificationType.APP_SLEEP : NotificationType.APP_WAKEUP;
            XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
            xmPushActionNotification.setAppId(AppInfoHolder.getInstance(context).getAppID());
            xmPushActionNotification.setType(notificationType.value);
            xmPushActionNotification.setPackageName(context.getPackageName());
            xmPushActionNotification.setId(PacketHelper.generatePacketID());
            xmPushActionNotification.setRequireAck(false);
            PushServiceClient.getInstance(context).sendMessage(xmPushActionNotification, ActionType.Notification, false, null, false);
        }
    }

    static void reportIgnoreRegMessageClicked(Context context, String str, PushMetaInfo pushMetaInfo, String str2, String str3) {
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        if (TextUtils.isEmpty(str3)) {
            MyLog.e("do not report clicked message");
            return;
        }
        xmPushActionNotification.setAppId(str3);
        xmPushActionNotification.setType("bar:click");
        xmPushActionNotification.setId(str);
        xmPushActionNotification.setRequireAck(false);
        PushServiceClient.getInstance(context).sendMessage(xmPushActionNotification, ActionType.Notification, false, true, pushMetaInfo, true, str2, str3);
    }

    @Deprecated
    public static void reportMessageClicked(Context context, String str) {
        reportMessageClicked(context, str, null, null);
    }

    public static void resumePush(Context context, String str) {
        setAcceptTime(context, 0, 0, 23, 59, str);
    }

    private static void scheduleDataCollectionJobs(Context context) {
        if (OnlineConfig.getInstance(sContext).getBooleanValue(ConfigKey.DataCollectionSwitch.getValue(), getDefaultSwitch())) {
            CDActionProviderHolder.getInstance().setCDActionProvider(new CDActionProviderImpl(context));
            ScheduledJobManager.getInstance(sContext).addOneShootJob(new Runnable() { // from class: com.xiaomi.mipush.sdk.MiPushClient.2
                @Override // java.lang.Runnable
                public void run() {
                    CDEntrance.start(MiPushClient.sContext);
                }
            }, 10);
        }
    }

    private static void scheduleOcVersionCheckJob() {
        ScheduledJobManager.getInstance(sContext).addRepeatJob(new OcVersionCheckJob(sContext), OnlineConfig.getInstance(sContext).getIntValue(ConfigKey.OcVersionCheckFrequency.getValue(), 86400), 5);
    }

    public static void setAcceptTime(Context context, int i2, int i3, int i4, int i5, String str) {
        if (i2 < 0 || i2 >= 24 || i4 < 0 || i4 >= 24 || i3 < 0 || i3 >= 60 || i5 < 0 || i5 >= 60) {
            throw new IllegalArgumentException("the input parameter is not valid.");
        }
        long rawOffset = ((TimeZone.getTimeZone("GMT+08").getRawOffset() - TimeZone.getDefault().getRawOffset()) / 1000) / 60;
        long j2 = ((((long) ((i2 * 60) + i3)) + rawOffset) + 1440) % 1440;
        long j3 = ((((long) ((i4 * 60) + i5)) + rawOffset) + 1440) % 1440;
        ArrayList arrayList = new ArrayList();
        arrayList.add(String.format("%1$02d:%2$02d", Long.valueOf(j2 / 60), Long.valueOf(j2 % 60)));
        arrayList.add(String.format("%1$02d:%2$02d", Long.valueOf(j3 / 60), Long.valueOf(j3 % 60)));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(String.format("%1$02d:%2$02d", Integer.valueOf(i2), Integer.valueOf(i3)));
        arrayList2.add(String.format("%1$02d:%2$02d", Integer.valueOf(i4), Integer.valueOf(i5)));
        if (!acceptTimeSet(context, (String) arrayList.get(0), (String) arrayList.get(1))) {
            setCommand(context, Command.COMMAND_SET_ACCEPT_TIME.value, (ArrayList<String>) arrayList, str);
        } else if (1 == PushMessageHelper.getPushMode(context)) {
            PushMessageHandler.onCommandResult(context, str, Command.COMMAND_SET_ACCEPT_TIME.value, 0L, null, arrayList2);
        } else {
            PushMessageHelper.sendCommandMessageBroadcast(context, PushMessageHelper.generateCommandMessage(Command.COMMAND_SET_ACCEPT_TIME.value, arrayList2, 0L, null, null, null));
        }
    }

    public static void setAlias(Context context, String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        setCommand(context, Command.COMMAND_SET_ALIAS.value, str, str2);
    }

    protected static void setCommand(Context context, String str, String str2, String str3) {
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(str2)) {
            arrayList.add(str2);
        }
        Command command = Command.COMMAND_SET_ALIAS;
        if (command.value.equalsIgnoreCase(str) && Math.abs(System.currentTimeMillis() - aliasSetTime(context, str2)) < 86400000) {
            if (1 == PushMessageHelper.getPushMode(context)) {
                PushMessageHandler.onCommandResult(context, str3, str, 0L, null, arrayList);
                return;
            } else {
                PushMessageHelper.sendCommandMessageBroadcast(context, PushMessageHelper.generateCommandMessage(command.value, arrayList, 0L, null, str3, null));
                return;
            }
        }
        if (Command.COMMAND_UNSET_ALIAS.value.equalsIgnoreCase(str) && aliasSetTime(context, str2) < 0) {
            MyLog.w("Don't cancel alias for " + XMStringUtils.obfuscateString(arrayList.toString(), 3) + " is unseted");
            return;
        }
        Command command2 = Command.COMMAND_SET_ACCOUNT;
        if (command2.value.equalsIgnoreCase(str) && Math.abs(System.currentTimeMillis() - accountSetTime(context, str2)) < 3600000) {
            if (1 == PushMessageHelper.getPushMode(context)) {
                PushMessageHandler.onCommandResult(context, str3, str, 0L, null, arrayList);
                return;
            } else {
                PushMessageHelper.sendCommandMessageBroadcast(context, PushMessageHelper.generateCommandMessage(command2.value, arrayList, 0L, null, str3, null));
                return;
            }
        }
        if (!Command.COMMAND_UNSET_ACCOUNT.value.equalsIgnoreCase(str) || accountSetTime(context, str2) >= 0) {
            setCommand(context, str, (ArrayList<String>) arrayList, str3);
            return;
        }
        MyLog.w("Don't cancel account for " + XMStringUtils.obfuscateString(arrayList.toString(), 3) + " is unseted");
    }

    public static void setLocalNotificationType(Context context, int i2) {
        PushServiceClient.getInstance(context).setLocalNotificationType(i2);
    }

    public static void setUserAccount(Context context, String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        setCommand(context, Command.COMMAND_SET_ACCOUNT.value, str, str2);
    }

    private static boolean shouldPullNotification(Context context) {
        return Math.abs(System.currentTimeMillis() - context.getSharedPreferences("mipush_extra", 0).getLong(LAST_PULL_NOTIFICATION, -1L)) > Constants.ASSEMBLE_PUSH_NETWORK_INTERVAL;
    }

    private static boolean shouldSendRegRequest(Context context) {
        return Math.abs(System.currentTimeMillis() - context.getSharedPreferences("mipush_extra", 0).getLong(LAST_REG_REQUEST, -1L)) > PluginConstants.TTS_REMOVE_BEAN_TIME;
    }

    public static boolean shouldUseMIUIPush(Context context) {
        return PushServiceClient.getInstance(context).shouldUseMIUIPush();
    }

    public static void subscribe(Context context, String str, String str2) {
        if (TextUtils.isEmpty(AppInfoHolder.getInstance(context).getAppID()) || TextUtils.isEmpty(str)) {
            return;
        }
        if (Math.abs(System.currentTimeMillis() - topicSubscribedTime(context, str)) <= 86400000) {
            if (1 == PushMessageHelper.getPushMode(context)) {
                PushMessageHandler.onSubscribeResult(context, str2, 0L, null, str);
                return;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(str);
            PushMessageHelper.sendCommandMessageBroadcast(context, PushMessageHelper.generateCommandMessage(Command.COMMAND_SUBSCRIBE_TOPIC.value, arrayList, 0L, null, null, null));
            return;
        }
        XmPushActionSubscription xmPushActionSubscription = new XmPushActionSubscription();
        String strGeneratePacketID = PacketHelper.generatePacketID();
        xmPushActionSubscription.setId(strGeneratePacketID);
        xmPushActionSubscription.setAppId(AppInfoHolder.getInstance(context).getAppID());
        xmPushActionSubscription.setTopic(str);
        xmPushActionSubscription.setPackageName(context.getPackageName());
        xmPushActionSubscription.setCategory(str2);
        MyLog.persist("cmd:" + Command.COMMAND_SUBSCRIBE_TOPIC + ", " + strGeneratePacketID);
        PushServiceClient.getInstance(context).sendMessage(xmPushActionSubscription, ActionType.Subscription, null);
    }

    public static void syncAssembleFCMPushToken(Context context) {
        PushServiceClient.getInstance(context).sendAssemblePushTokenCommon(null, RetryType.UPLOAD_FCM_TOKEN, AssemblePush.ASSEMBLE_PUSH_FCM, "");
    }

    public static long topicSubscribedTime(Context context, String str) {
        return context.getSharedPreferences("mipush_extra", 0).getLong(PREFIX_TOPIC + str, -1L);
    }

    public static void turnOffPush(Context context, UPSTurnCallBack uPSTurnCallBack) {
        disablePush(context);
        if (uPSTurnCallBack != null) {
            CodeResult codeResult = new CodeResult();
            codeResult.setResultCode(0L);
            codeResult.getResultCode();
            uPSTurnCallBack.onResult(codeResult);
        }
    }

    public static void turnOnPush(Context context, UPSTurnCallBack uPSTurnCallBack) {
        enablePush(context);
        if (uPSTurnCallBack != null) {
            CodeResult codeResult = new CodeResult();
            codeResult.setResultCode(0L);
            codeResult.getResultCode();
            uPSTurnCallBack.onResult(codeResult);
        }
    }

    public static void unRegisterToken(Context context, UPSUnRegisterCallBack uPSUnRegisterCallBack) {
        unregisterPush(context);
        if (uPSUnRegisterCallBack != null) {
            TokenResult tokenResult = new TokenResult();
            tokenResult.setToken(null);
            tokenResult.getToken();
            tokenResult.setResultCode(0L);
            tokenResult.getResultCode();
            uPSUnRegisterCallBack.onResult(tokenResult);
        }
    }

    public static void unregisterPush(Context context) {
        AssemblePushHelper.unregisterAssemblePush(context);
        OnlineConfig.getInstance(context).clearCallbacks();
        if (AppInfoHolder.getInstance(context).checkAppInfo()) {
            XmPushActionUnRegistration xmPushActionUnRegistration = new XmPushActionUnRegistration();
            xmPushActionUnRegistration.setId(PacketHelper.generatePacketID());
            xmPushActionUnRegistration.setAppId(AppInfoHolder.getInstance(context).getAppID());
            xmPushActionUnRegistration.setRegId(AppInfoHolder.getInstance(context).getRegID());
            xmPushActionUnRegistration.setToken(AppInfoHolder.getInstance(context).getAppToken());
            xmPushActionUnRegistration.setPackageName(context.getPackageName());
            PushServiceClient.getInstance(context).unregister(xmPushActionUnRegistration);
            PushMessageHandler.removeAllPushCallbackClass();
            PushMessageHandler.removeAllUPSCallback();
            AppInfoHolder.getInstance(context).invalidate();
            clearLocalNotificationType(context);
            clearNotification(context);
            clearExtras(context);
        }
    }

    public static void unsetAlias(Context context, String str, String str2) {
        setCommand(context, Command.COMMAND_UNSET_ALIAS.value, str, str2);
    }

    public static void unsetUserAccount(Context context, String str, String str2) {
        setCommand(context, Command.COMMAND_UNSET_ACCOUNT.value, str, str2);
    }

    public static void unsubscribe(Context context, String str, String str2) {
        if (AppInfoHolder.getInstance(context).checkAppInfo()) {
            if (topicSubscribedTime(context, str) < 0) {
                MyLog.w("Don't cancel subscribe for " + XMStringUtils.obfuscateString(str, 3) + " is unsubscribed");
                return;
            }
            XmPushActionUnSubscription xmPushActionUnSubscription = new XmPushActionUnSubscription();
            String strGeneratePacketID = PacketHelper.generatePacketID();
            xmPushActionUnSubscription.setId(strGeneratePacketID);
            xmPushActionUnSubscription.setAppId(AppInfoHolder.getInstance(context).getAppID());
            xmPushActionUnSubscription.setTopic(str);
            xmPushActionUnSubscription.setPackageName(context.getPackageName());
            xmPushActionUnSubscription.setCategory(str2);
            MyLog.persist("cmd:" + Command.COMMAND_UNSUBSCRIBE_TOPIC + ", " + strGeneratePacketID);
            PushServiceClient.getInstance(context).sendMessage(xmPushActionUnSubscription, ActionType.UnSubscription, null);
        }
    }

    private static void updateImeiOrOaid() {
        new Thread(new Runnable() { // from class: com.xiaomi.mipush.sdk.MiPushClient.3
            @Override // java.lang.Runnable
            public void run() {
                if (MIUIUtils.isGlobalRegion()) {
                    return;
                }
                if (DeviceInfo.blockingGetIMEI(MiPushClient.sContext) != null || MsaIdManager.getInstance(MiPushClient.sContext).isSupported()) {
                    XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
                    xmPushActionNotification.setAppId(AppInfoHolder.getInstance(MiPushClient.sContext).getAppID());
                    xmPushActionNotification.setType(NotificationType.ClientInfoUpdate.value);
                    xmPushActionNotification.setId(PacketHelper.generatePacketID());
                    xmPushActionNotification.setExtra(new HashMap());
                    String strBlockingGetIMEI = DeviceInfo.blockingGetIMEI(MiPushClient.sContext);
                    String str = "";
                    if (!TextUtils.isEmpty(strBlockingGetIMEI)) {
                        str = "" + XMStringUtils.getMd5Digest(strBlockingGetIMEI);
                    }
                    String strBlockingGetSubIMEISMd5 = DeviceInfo.blockingGetSubIMEISMd5(MiPushClient.sContext);
                    if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(strBlockingGetSubIMEISMd5)) {
                        str = str + "," + strBlockingGetSubIMEISMd5;
                    }
                    if (!TextUtils.isEmpty(str)) {
                        xmPushActionNotification.getExtra().put(Constants.EXTRA_KEY_IMEI_MD5, str);
                    }
                    MsaIdManager.getInstance(MiPushClient.sContext).fillData(xmPushActionNotification.getExtra());
                    int spaceId = DeviceInfo.getSpaceId();
                    if (spaceId >= 0) {
                        xmPushActionNotification.getExtra().put("space_id", Integer.toString(spaceId));
                    }
                    PushServiceClient.getInstance(MiPushClient.sContext).sendMessage(xmPushActionNotification, ActionType.Notification, false, null);
                }
            }
        }).start();
    }

    private static void checkNotNull(Object obj, String str) {
        if (obj != null) {
            return;
        }
        throw new IllegalArgumentException("param " + str + " is not nullable");
    }

    public static void clearNotification(Context context, String str, String str2) {
        PushServiceClient.getInstance(context).clearNotification(str, str2);
    }

    protected static boolean getOpenVIVOPush() {
        return AssemblePushCollectionsManager.getInstance(sContext).getUserSwitch(AssemblePush.ASSEMBLE_PUSH_FTOS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void initialize(Context context, String str, String str2, MiPushClientCallback miPushClientCallback, String str3, ICallbackResult iCallbackResult) {
        try {
            MyLog.init(context.getApplicationContext());
            MyLog.persist("sdk_version = 5_7_8-C");
            MsaIdManager.getInstance(context).init();
            SdkCrashManager.listenCrash(context);
            if (miPushClientCallback != null) {
                PushMessageHandler.addPushCallbackClass(miPushClientCallback);
            }
            if (iCallbackResult != null) {
                PushMessageHandler.addUPSCallback(iCallbackResult);
            }
            if (SystemUtils.isDebuggable(sContext)) {
                ManifestChecker.asynCheckManifest(sContext);
            }
            boolean z2 = AppInfoHolder.getInstance(sContext).getEnvType() != Constants.getEnvType();
            if (!z2 && !shouldSendRegRequest(sContext)) {
                PushServiceClient.getInstance(sContext).awakePushService();
                MyLog.w("Could not send  register message within 5s repeatly .");
                return;
            }
            if (z2 || !AppInfoHolder.getInstance(sContext).isRegistrationValid(str, str2) || AppInfoHolder.getInstance(sContext).invalidated()) {
                String strGenerateRandomString = XMStringUtils.generateRandomString(6);
                AppInfoHolder.getInstance(sContext).clear();
                AppInfoHolder.getInstance(sContext).setEnvType(Constants.getEnvType());
                AppInfoHolder.getInstance(sContext).putAppIDAndToken(str, str2, strGenerateRandomString);
                MiTinyDataClient.MiTinyDataClientImp.getInstance().processPendingList(MiTinyDataClient.PENDING_REASON_APPID);
                clearExtras(sContext);
                clearNotification(context);
                XmPushActionRegistration xmPushActionRegistration = new XmPushActionRegistration();
                xmPushActionRegistration.setId(PacketHelper.generatePacketIdForRegistration());
                xmPushActionRegistration.setAppId(str);
                xmPushActionRegistration.setToken(str2);
                xmPushActionRegistration.setPackageName(sContext.getPackageName());
                xmPushActionRegistration.setDeviceId(strGenerateRandomString);
                Context context2 = sContext;
                xmPushActionRegistration.setAppVersion(AppInfoUtils.getVersionName(context2, context2.getPackageName()));
                Context context3 = sContext;
                xmPushActionRegistration.setAppVersionCode(AppInfoUtils.getVersionCode(context3, context3.getPackageName()));
                xmPushActionRegistration.setPushSdkVersionName(PushConstants.PUSH_VERSION_NAME);
                xmPushActionRegistration.setPushSdkVersionCode(PushConstants.PUSH_VERSION_CODE);
                xmPushActionRegistration.setReason(RegistrationReason.Init);
                if (!TextUtils.isEmpty(str3)) {
                    xmPushActionRegistration.setAliasName(str3);
                }
                if (!MIUIUtils.isGlobalRegion()) {
                    String strQuicklyGetIMEI = DeviceInfo.quicklyGetIMEI(sContext);
                    if (!TextUtils.isEmpty(strQuicklyGetIMEI)) {
                        xmPushActionRegistration.setImeiMd5(XMStringUtils.getMd5Digest(strQuicklyGetIMEI) + "," + DeviceInfo.quicklyGetSubIMEISMd5(sContext));
                    }
                }
                int spaceId = DeviceInfo.getSpaceId();
                if (spaceId >= 0) {
                    xmPushActionRegistration.setSpaceId(spaceId);
                }
                PushServiceClient.getInstance(sContext).register(xmPushActionRegistration, z2);
                sContext.getSharedPreferences("mipush_extra", 4).getBoolean(PushConstants.SP_KEY_MIPUSH_REGISTED, true);
            } else {
                if (1 == PushMessageHelper.getPushMode(sContext)) {
                    checkNotNull((Object) miPushClientCallback, "callback");
                    miPushClientCallback.onInitializeResult(0L, null, AppInfoHolder.getInstance(sContext).getRegID());
                } else {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(AppInfoHolder.getInstance(sContext).getRegID());
                    PushMessageHelper.sendCommandMessageBroadcast(sContext, PushMessageHelper.generateCommandMessage(Command.COMMAND_REGISTER.value, arrayList, 0L, null, null, null));
                }
                PushServiceClient.getInstance(sContext).awakePushService();
                if (AppInfoHolder.getInstance(sContext).checkVersionNameChanged()) {
                    XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
                    xmPushActionNotification.setAppId(AppInfoHolder.getInstance(sContext).getAppID());
                    xmPushActionNotification.setType(NotificationType.ClientInfoUpdate.value);
                    xmPushActionNotification.setId(PacketHelper.generatePacketID());
                    HashMap map = new HashMap();
                    xmPushActionNotification.extra = map;
                    Context context4 = sContext;
                    map.put(Constants.EXTRA_KEY_APP_VERSION, AppInfoUtils.getVersionName(context4, context4.getPackageName()));
                    Map<String, String> map2 = xmPushActionNotification.extra;
                    Context context5 = sContext;
                    map2.put(Constants.EXTRA_KEY_APP_VERSION_CODE, Integer.toString(AppInfoUtils.getVersionCode(context5, context5.getPackageName())));
                    xmPushActionNotification.extra.put(PushConstants.KEY_PUSH_SDK_VERSION_NAME, PushConstants.PUSH_VERSION_NAME);
                    xmPushActionNotification.extra.put(PushConstants.KEY_PUSH_SDK_VERSION_CODE, Integer.toString(PushConstants.PUSH_VERSION_CODE));
                    String regResource = AppInfoHolder.getInstance(sContext).getRegResource();
                    if (!TextUtils.isEmpty(regResource)) {
                        xmPushActionNotification.extra.put("deviceid", regResource);
                    }
                    PushServiceClient.getInstance(sContext).sendMessage(xmPushActionNotification, ActionType.Notification, false, null);
                    PushServiceClient.getInstance(sContext).sendAssemblePushToken(sContext);
                }
                if (!PreferenceUtils.getSettingBoolean(sContext, "update_devId", false)) {
                    updateImeiOrOaid();
                    PreferenceUtils.setSettingBoolean(sContext, "update_devId", true);
                }
                if (shouldUseMIUIPush(sContext) && shouldPullNotification(sContext)) {
                    XmPushActionNotification xmPushActionNotification2 = new XmPushActionNotification();
                    xmPushActionNotification2.setAppId(AppInfoHolder.getInstance(sContext).getAppID());
                    xmPushActionNotification2.setType(NotificationType.PullOfflineMessage.value);
                    xmPushActionNotification2.setId(PacketHelper.generatePacketID());
                    xmPushActionNotification2.setRequireAck(false);
                    PushServiceClient.getInstance(sContext).sendMessage(xmPushActionNotification2, ActionType.Notification, false, null, false);
                    addPullNotificationTime(sContext);
                }
            }
            addRegRequestTime(sContext);
            scheduleOcVersionCheckJob();
            scheduleDataCollectionJobs(sContext);
            initEventPerfLogic(sContext);
            SyncInfoHelper.tryToSyncInfo(sContext);
            if (!sContext.getPackageName().equals(PushConstants.PUSH_SERVICE_PACKAGE_NAME)) {
                if (Logger.getUserLogger() != null) {
                    Logger.setLogger(sContext, Logger.getUserLogger());
                }
                MyLog.setLogLevel(2);
            }
            operateSyncAction(context);
        } catch (Throwable th) {
            MyLog.e(th);
        }
    }

    public static void registerPush(Context context, String str, String str2, String str3) {
        registerPush(context, str, str2, new PushConfiguration(), str3, null);
    }

    public static void reportMessageClicked(Context context, MiPushMessage miPushMessage) {
        PushMetaInfo pushMetaInfo = new PushMetaInfo();
        pushMetaInfo.setId(miPushMessage.getMessageId());
        pushMetaInfo.setTopic(miPushMessage.getTopic());
        pushMetaInfo.setDescription(miPushMessage.getDescription());
        pushMetaInfo.setTitle(miPushMessage.getTitle());
        pushMetaInfo.setNotifyId(miPushMessage.getNotifyId());
        pushMetaInfo.setNotifyType(miPushMessage.getNotifyType());
        pushMetaInfo.setPassThrough(miPushMessage.getPassThrough());
        pushMetaInfo.setExtra(miPushMessage.getExtra());
        reportMessageClicked(context, miPushMessage.getMessageId(), pushMetaInfo, null);
    }

    public static void clearNotification(Context context) {
        PushServiceClient.getInstance(context).clearNotification(-1);
    }

    public static void registerPush(Context context, String str, String str2, PushConfiguration pushConfiguration) {
        registerPush(context, str, str2, pushConfiguration, null, null);
    }

    private static void registerPush(Context context, final String str, final String str2, PushConfiguration pushConfiguration, final String str3, final ICallbackResult iCallbackResult) {
        checkNotNull((Object) context, "context");
        checkNotNull((Object) str, "appID");
        checkNotNull((Object) str2, "appToken");
        Context applicationContext = context.getApplicationContext();
        sContext = applicationContext;
        if (applicationContext == null) {
            sContext = context;
        }
        Context context2 = sContext;
        SystemUtils.initialize(context2);
        if (!NetworkStatusReceiver.isRegister()) {
            registerNetworkReceiver(sContext);
        }
        AssemblePushCollectionsManager.getInstance(sContext).setConfiguration(pushConfiguration);
        ScheduledJobManager.getInstance(context2).addOneShootJob(new Runnable() { // from class: com.xiaomi.mipush.sdk.MiPushClient.1
            @Override // java.lang.Runnable
            public void run() {
                MiPushClient.initialize(MiPushClient.sContext, str, str2, null, str3, iCallbackResult);
            }
        });
    }

    static void reportMessageClicked(Context context, String str, PushMetaInfo pushMetaInfo, String str2) {
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        if (TextUtils.isEmpty(str2)) {
            if (AppInfoHolder.getInstance(context).checkAppInfo()) {
                xmPushActionNotification.setAppId(AppInfoHolder.getInstance(context).getAppID());
            } else {
                MyLog.e("do not report clicked message");
                return;
            }
        } else {
            xmPushActionNotification.setAppId(str2);
        }
        xmPushActionNotification.setType("bar:click");
        xmPushActionNotification.setId(str);
        xmPushActionNotification.setRequireAck(false);
        PushServiceClient.getInstance(context).sendMessage(xmPushActionNotification, ActionType.Notification, false, pushMetaInfo);
    }

    protected static void setCommand(Context context, String str, ArrayList<String> arrayList, String str2) {
        if (TextUtils.isEmpty(AppInfoHolder.getInstance(context).getAppID())) {
            return;
        }
        XmPushActionCommand xmPushActionCommand = new XmPushActionCommand();
        String strGeneratePacketID = PacketHelper.generatePacketID();
        xmPushActionCommand.setId(strGeneratePacketID);
        xmPushActionCommand.setAppId(AppInfoHolder.getInstance(context).getAppID());
        xmPushActionCommand.setCmdName(str);
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            xmPushActionCommand.addToCmdArgs(it.next());
        }
        xmPushActionCommand.setCategory(str2);
        xmPushActionCommand.setPackageName(context.getPackageName());
        MyLog.persist("cmd:" + str + ", " + strGeneratePacketID);
        PushServiceClient.getInstance(context).sendMessage(xmPushActionCommand, ActionType.Command, null);
    }

    @Deprecated
    static void setAwakeServiceEnabled(boolean z2) {
    }

    @Deprecated
    public static void syncAssembleCOSPushToken(Context context) {
    }

    @Deprecated
    public static void syncAssembleFTOSPushToken(Context context) {
    }

    @Deprecated
    public static void syncAssemblePushToken(Context context) {
    }
}
