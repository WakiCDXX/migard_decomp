package com.xiaomi.mipush.sdk;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.channel.commonutils.string.MD5;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.push.clientreport.PerfMessageHelper;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.ChannelPushReportAgent;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.push.service.PacketHelper;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.PushProvision;
import com.xiaomi.push.service.clientReport.PushClientReportManager;
import com.xiaomi.push.service.clientReport.ReportConstants;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.BootModeType;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import com.xiaomi.xmpush.thrift.ConfigKey;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushActionRegistration;
import com.xiaomi.xmpush.thrift.XmPushActionUnRegistration;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.thrift.TBase;

/* JADX INFO: loaded from: classes.dex */
public class PushServiceClient {
    private static final int MAX_PENDING_MESSAGES_SIZE = 50;
    private static final int MIN_MIUI_PUSH_BIND_SERVICE_VERSION = 108;
    private static final int MIN_MIUI_PUSH_VERSION = 105;
    private static final int MIN_MIUI_PUSH_VERSION_JAR = 106;
    private static final int REQUEST_CACHE_SIZE = 10;
    private static boolean isBind = false;
    private static final boolean isForHybridFrame = false;
    private static PushServiceClient sInstance;
    private static final ArrayList<BufferedRequest> sPendingRequest = new ArrayList<>();
    private Handler handler;
    private Messenger mClientMessenger;
    private Context mContext;
    private boolean mIsMiuiPushServiceEnabled;
    private long mLastRegRequestTime;
    private List<Message> pendingMessages = new ArrayList();
    private boolean isConnectingService = false;
    private String mPushServiceClassName = null;
    private Intent registerTask = null;
    private Integer mDeviceProvisioned = null;
    private String mSession = null;

    /* JADX INFO: renamed from: com.xiaomi.mipush.sdk.PushServiceClient$5, reason: invalid class name */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$mipush$sdk$RetryType;

        static {
            int[] iArr = new int[RetryType.values().length];
            $SwitchMap$com$xiaomi$mipush$sdk$RetryType = iArr;
            try {
                iArr[RetryType.DISABLE_PUSH.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$RetryType[RetryType.ENABLE_PUSH.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$RetryType[RetryType.UPLOAD_HUAWEI_TOKEN.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$RetryType[RetryType.UPLOAD_FCM_TOKEN.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$RetryType[RetryType.UPLOAD_COS_TOKEN.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$xiaomi$mipush$sdk$RetryType[RetryType.UPLOAD_FTOS_TOKEN.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    static class BufferedRequest<T extends TBase<T, ?>> {
        ActionType actionType;
        boolean encrypt;
        T message;

        BufferedRequest() {
        }
    }

    private PushServiceClient(Context context) {
        this.mIsMiuiPushServiceEnabled = false;
        this.handler = null;
        this.mContext = context.getApplicationContext();
        this.mIsMiuiPushServiceEnabled = serviceInstalled();
        isBind = useBind();
        this.handler = new Handler(Looper.getMainLooper()) { // from class: com.xiaomi.mipush.sdk.PushServiceClient.1
            @Override // android.os.Handler
            public void dispatchMessage(Message message) {
                if (message.what != 19) {
                    return;
                }
                String str = (String) message.obj;
                int i2 = message.arg1;
                synchronized (OperatePushHelper.class) {
                    try {
                        if (OperatePushHelper.getInstance(PushServiceClient.this.mContext).isMessageOperating(str)) {
                            if (OperatePushHelper.getInstance(PushServiceClient.this.mContext).getRetryCount(str) < 10) {
                                String string = message.getData() != null ? message.getData().getString(AssemblePushUtils.KEY_SYNC_TOKEN_REASON) : "";
                                RetryType retryType = RetryType.DISABLE_PUSH;
                                if (retryType.ordinal() == i2 && OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(PushServiceClient.this.mContext).getSyncStatus(retryType))) {
                                    PushServiceClient.this.retryPolicy(str, retryType, true, null);
                                } else {
                                    RetryType retryType2 = RetryType.ENABLE_PUSH;
                                    if (retryType2.ordinal() == i2 && OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(PushServiceClient.this.mContext).getSyncStatus(retryType2))) {
                                        PushServiceClient.this.retryPolicy(str, retryType2, true, null);
                                    } else {
                                        RetryType retryType3 = RetryType.UPLOAD_HUAWEI_TOKEN;
                                        if (retryType3.ordinal() == i2 && OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(PushServiceClient.this.mContext).getSyncStatus(retryType3))) {
                                            HashMap<String, String> assemblePushExtra = AssemblePushHelper.getAssemblePushExtra(PushServiceClient.this.mContext, AssemblePush.ASSEMBLE_PUSH_HUAWEI);
                                            assemblePushExtra.put(AssemblePushUtils.KEY_SYNC_TOKEN_REASON, string);
                                            PushServiceClient.this.retryPolicy(str, retryType3, false, assemblePushExtra);
                                        } else {
                                            RetryType retryType4 = RetryType.UPLOAD_FCM_TOKEN;
                                            if (retryType4.ordinal() == i2 && OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(PushServiceClient.this.mContext).getSyncStatus(retryType4))) {
                                                PushServiceClient pushServiceClient = PushServiceClient.this;
                                                pushServiceClient.retryPolicy(str, retryType4, false, AssemblePushHelper.getAssemblePushExtra(pushServiceClient.mContext, AssemblePush.ASSEMBLE_PUSH_FCM));
                                            } else {
                                                RetryType retryType5 = RetryType.UPLOAD_COS_TOKEN;
                                                if (retryType5.ordinal() == i2 && OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(PushServiceClient.this.mContext).getSyncStatus(retryType5))) {
                                                    HashMap<String, String> assemblePushExtra2 = AssemblePushHelper.getAssemblePushExtra(PushServiceClient.this.mContext, AssemblePush.ASSEMBLE_PUSH_COS);
                                                    assemblePushExtra2.put(AssemblePushUtils.KEY_SYNC_TOKEN_REASON, string);
                                                    PushServiceClient.this.retryPolicy(str, retryType5, false, assemblePushExtra2);
                                                } else {
                                                    RetryType retryType6 = RetryType.UPLOAD_FTOS_TOKEN;
                                                    if (retryType6.ordinal() == i2 && OperatePushHelper.SYNCING.equals(OperatePushHelper.getInstance(PushServiceClient.this.mContext).getSyncStatus(retryType6))) {
                                                        HashMap<String, String> assemblePushExtra3 = AssemblePushHelper.getAssemblePushExtra(PushServiceClient.this.mContext, AssemblePush.ASSEMBLE_PUSH_FTOS);
                                                        assemblePushExtra3.put(AssemblePushUtils.KEY_SYNC_TOKEN_REASON, string);
                                                        PushServiceClient.this.retryPolicy(str, retryType6, false, assemblePushExtra3);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                OperatePushHelper.getInstance(PushServiceClient.this.mContext).increaseRetryCount(str);
                            } else {
                                OperatePushHelper.getInstance(PushServiceClient.this.mContext).removeOperateMessage(str);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        if (MIUIUtils.isXMSF(context)) {
            ChannelPushReportAgent.setReportAgent(new ChannelPushReportAgent.ReportAgent() { // from class: com.xiaomi.mipush.sdk.PushServiceClient.2
                @Override // com.xiaomi.push.service.ChannelPushReportAgent.ReportAgent
                public void report(XmPushActionNotification xmPushActionNotification) {
                    if (TextUtils.isEmpty(xmPushActionNotification.getAppId()) || TextUtils.isEmpty(xmPushActionNotification.getPackageName())) {
                        MyLog.w("cpra no fill ainfo, fill it by default.");
                        xmPushActionNotification.setAppId(AppInfoHolder.getInstance(PushServiceClient.this.mContext).getAppID());
                        xmPushActionNotification.setPackageName(PushServiceClient.this.mContext.getPackageName());
                    }
                    PushServiceClient.this.sendMessage(xmPushActionNotification, ActionType.Notification, false, null);
                }
            });
        }
        Intent intentCreateGlobalServiceIntent = createGlobalServiceIntent();
        if (intentCreateGlobalServiceIntent != null) {
            startServiceSafely(intentCreateGlobalServiceIntent);
        }
    }

    private synchronized void bindServiceSafely(Intent intent) {
        try {
            if (this.isConnectingService) {
                Message toMessage = parseToMessage(intent);
                if (this.pendingMessages.size() >= 50) {
                    this.pendingMessages.remove(0);
                }
                this.pendingMessages.add(toMessage);
                return;
            }
            if (this.mClientMessenger == null) {
                this.mContext.bindService(intent, new ServiceConnection() { // from class: com.xiaomi.mipush.sdk.PushServiceClient.4
                    @Override // android.content.ServiceConnection
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        synchronized (PushServiceClient.this) {
                            PushServiceClient.this.mClientMessenger = new Messenger(iBinder);
                            PushServiceClient.this.isConnectingService = false;
                            Iterator it = PushServiceClient.this.pendingMessages.iterator();
                            while (it.hasNext()) {
                                try {
                                    PushServiceClient.this.mClientMessenger.send((Message) it.next());
                                } catch (RemoteException e2) {
                                    MyLog.e(e2);
                                }
                            }
                            PushServiceClient.this.pendingMessages.clear();
                        }
                    }

                    @Override // android.content.ServiceConnection
                    public void onServiceDisconnected(ComponentName componentName) {
                        PushServiceClient.this.mClientMessenger = null;
                        PushServiceClient.this.isConnectingService = false;
                    }
                }, 1);
                this.isConnectingService = true;
                this.pendingMessages.clear();
                this.pendingMessages.add(parseToMessage(intent));
            } else {
                try {
                    this.mClientMessenger.send(parseToMessage(intent));
                } catch (RemoteException unused) {
                    this.mClientMessenger = null;
                    this.isConnectingService = false;
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private void callService(Intent intent) {
        OnlineConfig onlineConfig = OnlineConfig.getInstance(this.mContext);
        int value = ConfigKey.ServiceBootMode.getValue();
        BootModeType bootModeType = BootModeType.START;
        int intValue = onlineConfig.getIntValue(value, bootModeType.getValue());
        int serviceBootMode = getServiceBootMode();
        BootModeType bootModeType2 = BootModeType.BIND;
        boolean z2 = intValue == bootModeType2.getValue() && isBind;
        int value2 = z2 ? bootModeType2.getValue() : bootModeType.getValue();
        if (value2 != serviceBootMode) {
            sendServiceBootMode(value2);
        }
        if (z2) {
            bindServiceSafely(intent);
        } else {
            startServiceSafely(intent);
        }
    }

    private Intent createGlobalServiceIntent() {
        if (!PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(this.mContext.getPackageName())) {
            return createGlobalServiceIntentForApp();
        }
        MyLog.v("pushChannel xmsf create own channel");
        return createMyPushChannelIntent();
    }

    private Intent createGlobalServiceIntentForApp() {
        if (shouldUseMIUIPush()) {
            MyLog.v("pushChannel app start miui china channel");
            return createMIUIPushChannelIntent();
        }
        MyLog.v("pushChannel app start  own channel");
        return createMyPushChannelIntent();
    }

    private Intent createMIUIPushChannelIntent() {
        Intent intent = new Intent();
        String packageName = this.mContext.getPackageName();
        intent.setPackage(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        intent.setClassName(PushConstants.PUSH_SERVICE_PACKAGE_NAME, getPushServiceName());
        intent.putExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE, packageName);
        disableMyPushService();
        return intent;
    }

    private Intent createMyPushChannelIntent() {
        Intent intent = new Intent();
        String packageName = this.mContext.getPackageName();
        enableMyPushService();
        intent.setComponent(new ComponentName(this.mContext, PushConstants.PUSH_SERVICE_CLASS_NAME_JAR));
        intent.putExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE, packageName);
        return intent;
    }

    private Intent createServiceIntent() {
        return (!shouldUseMIUIPush() || PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(this.mContext.getPackageName())) ? createMyPushChannelIntent() : createMIUIPushChannelIntent();
    }

    private void disableMyPushService() {
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            ComponentName componentName = new ComponentName(this.mContext, PushConstants.PUSH_SERVICE_CLASS_NAME_JAR);
            if (packageManager.getComponentEnabledSetting(componentName) == 2) {
                return;
            }
            packageManager.setComponentEnabledSetting(componentName, 2, 1);
        } catch (Throwable unused) {
        }
    }

    private void enableMyPushService() {
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            ComponentName componentName = new ComponentName(this.mContext, PushConstants.PUSH_SERVICE_CLASS_NAME_JAR);
            if (packageManager.getComponentEnabledSetting(componentName) == 1) {
                return;
            }
            packageManager.setComponentEnabledSetting(componentName, 1, 1);
        } catch (Throwable unused) {
        }
    }

    public static synchronized PushServiceClient getInstance(Context context) {
        try {
            if (sInstance == null) {
                sInstance = new PushServiceClient(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private String getPushServiceName() {
        String str = this.mPushServiceClassName;
        if (str != null) {
            return str;
        }
        try {
            if (this.mContext.getPackageManager().getPackageInfo(PushConstants.PUSH_SERVICE_PACKAGE_NAME, 4).versionCode >= 106) {
                this.mPushServiceClassName = PushConstants.PUSH_SERVICE_CLASS_NAME_JAR;
                return PushConstants.PUSH_SERVICE_CLASS_NAME_JAR;
            }
        } catch (Exception unused) {
        }
        this.mPushServiceClassName = PushConstants.PUSH_SERVICE_CLASS_NAME;
        return PushConstants.PUSH_SERVICE_CLASS_NAME;
    }

    private synchronized int getServiceBootMode() {
        return this.mContext.getSharedPreferences("mipush_extra", 0).getInt(Constants.EXTRA_KEY_BOOT_SERVICE_MODE, -1);
    }

    private boolean isAutoTry() {
        String packageName = this.mContext.getPackageName();
        return packageName.contains(OneTrack.Param.MIUI) || packageName.contains("xiaomi") || (this.mContext.getApplicationInfo().flags & 1) != 0;
    }

    private Message parseToMessage(Intent intent) {
        Message messageObtain = Message.obtain();
        messageObtain.what = 17;
        messageObtain.obj = intent;
        return messageObtain;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void retryPolicy(String str, RetryType retryType, boolean z2, HashMap<String, String> map) {
        XmPushActionNotification xmPushActionNotification;
        String strGeneratePacketID = str;
        if (AppInfoHolder.getInstance(this.mContext).checkAppInfo() && Network.hasNetwork(this.mContext)) {
            XmPushActionNotification xmPushActionNotification2 = new XmPushActionNotification();
            xmPushActionNotification2.setRequireAck(true);
            Intent intentCreateServiceIntent = createServiceIntent();
            if (TextUtils.isEmpty(str)) {
                strGeneratePacketID = PacketHelper.generatePacketID();
                xmPushActionNotification2.setId(strGeneratePacketID);
                xmPushActionNotification = z2 ? new XmPushActionNotification(strGeneratePacketID, true) : null;
                synchronized (OperatePushHelper.class) {
                    OperatePushHelper.getInstance(this.mContext).resetOperateMessage(strGeneratePacketID);
                }
            } else {
                xmPushActionNotification2.setId(strGeneratePacketID);
                xmPushActionNotification = z2 ? new XmPushActionNotification(strGeneratePacketID, true) : null;
            }
            switch (AnonymousClass5.$SwitchMap$com$xiaomi$mipush$sdk$RetryType[retryType.ordinal()]) {
                case 1:
                    NotificationType notificationType = NotificationType.DisablePushMessage;
                    xmPushActionNotification2.setType(notificationType.value);
                    xmPushActionNotification.setType(notificationType.value);
                    if (map != null) {
                        xmPushActionNotification2.setExtra(map);
                        xmPushActionNotification.setExtra(map);
                    }
                    intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_DISABLE_PUSH_MESSAGE);
                    break;
                case 2:
                    NotificationType notificationType2 = NotificationType.EnablePushMessage;
                    xmPushActionNotification2.setType(notificationType2.value);
                    xmPushActionNotification.setType(notificationType2.value);
                    if (map != null) {
                        xmPushActionNotification2.setExtra(map);
                        xmPushActionNotification.setExtra(map);
                    }
                    intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_ENABLE_PUSH_MESSAGE);
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                    xmPushActionNotification2.setType(NotificationType.ThirdPartyRegUpdate.value);
                    if (map != null) {
                        xmPushActionNotification2.setExtra(map);
                    }
                    break;
            }
            MyLog.persist("type:" + retryType + ", " + strGeneratePacketID);
            xmPushActionNotification2.setAppId(AppInfoHolder.getInstance(this.mContext).getAppID());
            xmPushActionNotification2.setPackageName(this.mContext.getPackageName());
            ActionType actionType = ActionType.Notification;
            sendMessage(xmPushActionNotification2, actionType, false, null);
            if (z2) {
                xmPushActionNotification.setAppId(AppInfoHolder.getInstance(this.mContext).getAppID());
                xmPushActionNotification.setPackageName(this.mContext.getPackageName());
                Context context = this.mContext;
                byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(PushContainerHelper.generateRequestContainer(context, xmPushActionNotification, actionType, false, context.getPackageName(), AppInfoHolder.getInstance(this.mContext).getAppID()));
                if (bArrConvertThriftObjectToBytes != null) {
                    PerfMessageHelper.collectPerfData(this.mContext.getPackageName(), this.mContext, xmPushActionNotification, actionType, bArrConvertThriftObjectToBytes.length);
                    intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArrConvertThriftObjectToBytes);
                    intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_MESSAGE_CACHE, true);
                    intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_APP_ID, AppInfoHolder.getInstance(this.mContext).getAppID());
                    intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_APP_TOKEN, AppInfoHolder.getInstance(this.mContext).getAppToken());
                    callService(intentCreateServiceIntent);
                }
            }
            Message messageObtain = Message.obtain();
            messageObtain.what = 19;
            int iOrdinal = retryType.ordinal();
            messageObtain.obj = strGeneratePacketID;
            messageObtain.arg1 = iOrdinal;
            if (map != null && map.get(AssemblePushUtils.KEY_SYNC_TOKEN_REASON) != null) {
                Bundle bundle = new Bundle();
                bundle.putString(AssemblePushUtils.KEY_SYNC_TOKEN_REASON, map.get(AssemblePushUtils.KEY_SYNC_TOKEN_REASON));
                messageObtain.setData(bundle);
            }
            this.handler.sendMessageDelayed(messageObtain, PluginConstants.TTS_REMOVE_BEAN_TIME);
        }
    }

    private void saveRegRequestTimeNow() {
        this.mLastRegRequestTime = SystemClock.elapsedRealtime();
    }

    private synchronized void saveServiceBootMode(int i2) {
        this.mContext.getSharedPreferences("mipush_extra", 0).edit().putInt(Constants.EXTRA_KEY_BOOT_SERVICE_MODE, i2).commit();
    }

    private boolean serviceInstalled() {
        try {
            PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(PushConstants.PUSH_SERVICE_PACKAGE_NAME, 4);
            return packageInfo != null && packageInfo.versionCode >= 105;
        } catch (Throwable unused) {
            return false;
        }
    }

    private void startServiceSafely(Intent intent) {
        try {
            if (MIUIUtils.isMIUI()) {
                this.mContext.startService(intent);
            } else {
                bindServiceSafely(intent);
            }
        } catch (Exception e2) {
            MyLog.e(e2);
        }
    }

    private boolean useBind() {
        if (shouldUseMIUIPush()) {
            try {
                return this.mContext.getPackageManager().getPackageInfo(PushConstants.PUSH_SERVICE_PACKAGE_NAME, 4).versionCode >= 108;
            } catch (Exception unused) {
            }
        }
        return true;
    }

    public <T extends TBase<T, ?>> void addPendRequest(T t2, ActionType actionType, boolean z2) {
        BufferedRequest bufferedRequest = new BufferedRequest();
        bufferedRequest.message = t2;
        bufferedRequest.actionType = actionType;
        bufferedRequest.encrypt = z2;
        ArrayList<BufferedRequest> arrayList = sPendingRequest;
        synchronized (arrayList) {
            try {
                arrayList.add(bufferedRequest);
                if (arrayList.size() > 10) {
                    arrayList.remove(0);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void awakePushService() {
        startServiceSafely(createServiceIntent());
    }

    public void clearHeadsUpNotification() {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_CLEAR_HEADSUPNOTIFICATION);
        Application application = (Application) JavaCalls.callStaticMethod("android.app.ActivityThread", "currentApplication", new Object[0]);
        String packageName = (application == null || application.getApplicationContext() == null) ? null : application.getApplicationContext().getPackageName();
        String packageName2 = this.mContext.getPackageName();
        if (TextUtils.isEmpty(packageName) || packageName.equals(packageName2)) {
            packageName = packageName2;
        } else {
            MyLog.w("application package name: " + packageName + ", not equals context package name: " + packageName2);
        }
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKAGE_NAME, packageName);
        callService(intentCreateServiceIntent);
    }

    public void clearLocalNotificationType() {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_SET_NOTIFICATION_TYPE);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKAGE_NAME, this.mContext.getPackageName());
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_SIG, MD5.MD5_16(this.mContext.getPackageName()));
        callService(intentCreateServiceIntent);
    }

    public void clearNotification(int i2) {
        clearNotification(i2, 0);
    }

    public final void closePush() {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_DISABLE_PUSH);
        callService(intentCreateServiceIntent);
    }

    public long getLastRegRequestTime() {
        return this.mLastRegRequestTime;
    }

    public boolean isProvisioned() {
        if (!shouldUseMIUIPush() || !isAutoTry()) {
            return true;
        }
        if (this.mDeviceProvisioned == null) {
            Integer numValueOf = Integer.valueOf(PushProvision.getInstance(this.mContext).getProvisioned());
            this.mDeviceProvisioned = numValueOf;
            if (numValueOf.intValue() == 0) {
                this.mContext.getContentResolver().registerContentObserver(PushProvision.getInstance(this.mContext).getProvisionedUri(), false, new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.xiaomi.mipush.sdk.PushServiceClient.3
                    @Override // android.database.ContentObserver
                    public void onChange(boolean z2) {
                        PushServiceClient pushServiceClient = PushServiceClient.this;
                        pushServiceClient.mDeviceProvisioned = Integer.valueOf(PushProvision.getInstance(pushServiceClient.mContext).getProvisioned());
                        if (PushServiceClient.this.mDeviceProvisioned.intValue() != 0) {
                            PushServiceClient.this.mContext.getContentResolver().unregisterContentObserver(this);
                            if (Network.hasNetwork(PushServiceClient.this.mContext)) {
                                PushServiceClient.this.processRegisterTask();
                            }
                        }
                    }
                });
            }
        }
        return this.mDeviceProvisioned.intValue() != 0;
    }

    public void processPendRequest() {
        ArrayList<BufferedRequest> arrayList = sPendingRequest;
        synchronized (arrayList) {
            boolean z2 = Thread.currentThread() == Looper.getMainLooper().getThread();
            for (BufferedRequest bufferedRequest : arrayList) {
                sendMessage(bufferedRequest.message, bufferedRequest.actionType, bufferedRequest.encrypt, false, null, true);
                if (!z2) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException unused) {
                    }
                }
            }
            sPendingRequest.clear();
        }
    }

    public void processRegisterTask() {
        if (this.registerTask != null) {
            saveRegRequestTimeNow();
            callService(this.registerTask);
            this.registerTask = null;
        }
    }

    public final void register(XmPushActionRegistration xmPushActionRegistration, boolean z2) {
        PushClientReportManager.getInstance(this.mContext.getApplicationContext()).reportEvent(this.mContext.getPackageName(), ReportConstants.REGISTER_EVENT_CHAIN_INTERFACE_ID, xmPushActionRegistration.getId(), ReportConstants.REGISTER_TYPE_CONSTRUCT_MSG, null);
        this.registerTask = null;
        AppInfoHolder.getInstance(this.mContext).appRegRequestId = xmPushActionRegistration.getId();
        Intent intentCreateServiceIntent = createServiceIntent();
        byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(PushContainerHelper.generateRequestContainer(this.mContext, xmPushActionRegistration, ActionType.Registration));
        if (bArrConvertThriftObjectToBytes == null) {
            MyLog.w("register fail, because msgBytes is null.");
            return;
        }
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_REGISTER_APP);
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_APP_ID, AppInfoHolder.getInstance(this.mContext).getAppID());
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArrConvertThriftObjectToBytes);
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_SESSION, this.mSession);
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_ENV_CHANAGE, z2);
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_ENV_TYPE, AppInfoHolder.getInstance(this.mContext).getEnvType());
        if (!Network.hasNetwork(this.mContext) || !isProvisioned()) {
            this.registerTask = intentCreateServiceIntent;
        } else {
            saveRegRequestTimeNow();
            callService(intentCreateServiceIntent);
        }
    }

    void send3rdPushHint(int i2, String str) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_THIRDPARTY_HINT);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_THIRDPARTY_HINT_LEVEL, i2);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_THIRDPARTY_HINT_DESC, str);
        startServiceSafely(intentCreateServiceIntent);
    }

    public void sendAssemblePushToken(Context context) {
        if (MIUIUtils.isMIUI()) {
            return;
        }
        PhoneBrand phoneBrand = AssemblePushUtils.getPhoneBrand(context);
        if (PhoneBrand.HUAWEI.equals(phoneBrand)) {
            sendAssemblePushTokenCommon(null, RetryType.UPLOAD_HUAWEI_TOKEN, AssemblePush.ASSEMBLE_PUSH_HUAWEI, "update");
        }
        if (PhoneBrand.OPPO.equals(phoneBrand)) {
            sendAssemblePushTokenCommon(null, RetryType.UPLOAD_COS_TOKEN, AssemblePush.ASSEMBLE_PUSH_COS, "update");
        }
        if (PhoneBrand.VIVO.equals(phoneBrand)) {
            sendAssemblePushTokenCommon(null, RetryType.UPLOAD_FTOS_TOKEN, AssemblePush.ASSEMBLE_PUSH_FTOS, "update");
        }
    }

    public final void sendAssemblePushTokenCommon(String str, RetryType retryType, AssemblePush assemblePush, String str2) {
        OperatePushHelper.getInstance(this.mContext).putSyncStatus(retryType, OperatePushHelper.SYNCING);
        HashMap<String, String> assemblePushExtra = AssemblePushHelper.getAssemblePushExtra(this.mContext, assemblePush);
        assemblePushExtra.put(AssemblePushUtils.KEY_SYNC_TOKEN_REASON, str2);
        retryPolicy(str, retryType, false, assemblePushExtra);
    }

    void sendDataCommon(Intent intent) {
        intent.fillIn(createServiceIntent(), 24);
        callService(intent);
    }

    public final <T extends TBase<T, ?>> void sendMessage(T t2, ActionType actionType, PushMetaInfo pushMetaInfo) {
        sendMessage(t2, actionType, !actionType.equals(ActionType.Registration), pushMetaInfo);
    }

    public final void sendPushEnableDisableMessage(boolean z2) {
        sendPushEnableDisableMessage(z2, null);
    }

    public boolean sendServiceBootMode(int i2) {
        if (!AppInfoHolder.getInstance(this.mContext).checkAppInfo()) {
            return false;
        }
        saveServiceBootMode(i2);
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        xmPushActionNotification.setId(PacketHelper.generatePacketID());
        xmPushActionNotification.setAppId(AppInfoHolder.getInstance(this.mContext).getAppID());
        xmPushActionNotification.setPackageName(this.mContext.getPackageName());
        xmPushActionNotification.setType(NotificationType.ClientABTest.value);
        HashMap map = new HashMap();
        xmPushActionNotification.extra = map;
        map.put("boot_mode", i2 + "");
        getInstance(this.mContext).sendMessage(xmPushActionNotification, ActionType.Notification, false, null);
        return true;
    }

    public final void sendTinyData(ClientUploadDataItem clientUploadDataItem) {
        Intent intentCreateServiceIntent = createServiceIntent();
        byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(clientUploadDataItem);
        if (bArrConvertThriftObjectToBytes == null) {
            MyLog.w("send TinyData failed, because tinyDataBytes is null.");
            return;
        }
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_SEND_TINYDATA);
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArrConvertThriftObjectToBytes);
        startServiceSafely(intentCreateServiceIntent);
    }

    public void setLocalNotificationType(int i2) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_SET_NOTIFICATION_TYPE);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKAGE_NAME, this.mContext.getPackageName());
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_NOTIFY_TYPE, i2);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_SIG, MD5.MD5_16(this.mContext.getPackageName() + i2));
        callService(intentCreateServiceIntent);
    }

    public boolean shouldUseMIUIPush() {
        return this.mIsMiuiPushServiceEnabled && 1 == AppInfoHolder.getInstance(this.mContext).getEnvType();
    }

    public final void unregister(XmPushActionUnRegistration xmPushActionUnRegistration) {
        byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(PushContainerHelper.generateRequestContainer(this.mContext, xmPushActionUnRegistration, ActionType.UnRegistration));
        if (bArrConvertThriftObjectToBytes == null) {
            MyLog.w("unregister fail, because msgBytes is null.");
            return;
        }
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_UNREGISTER_APP);
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_APP_ID, AppInfoHolder.getInstance(this.mContext).getAppID());
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArrConvertThriftObjectToBytes);
        callService(intentCreateServiceIntent);
    }

    void clearNotification(int i2, int i3) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_CLEAR_NOTIFICATION);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKAGE_NAME, this.mContext.getPackageName());
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_NOTIFY_ID, i2);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_CLICKED_BUTTON, i3);
        callService(intentCreateServiceIntent);
    }

    public final <T extends TBase<T, ?>> void sendMessage(T t2, ActionType actionType, boolean z2, PushMetaInfo pushMetaInfo, boolean z3) {
        sendMessage(t2, actionType, z2, true, pushMetaInfo, z3);
    }

    public final void sendPushEnableDisableMessage(boolean z2, String str) {
        if (z2) {
            OperatePushHelper operatePushHelper = OperatePushHelper.getInstance(this.mContext);
            RetryType retryType = RetryType.DISABLE_PUSH;
            operatePushHelper.putSyncStatus(retryType, OperatePushHelper.SYNCING);
            OperatePushHelper.getInstance(this.mContext).putSyncStatus(RetryType.ENABLE_PUSH, "");
            retryPolicy(str, retryType, true, null);
            return;
        }
        OperatePushHelper operatePushHelper2 = OperatePushHelper.getInstance(this.mContext);
        RetryType retryType2 = RetryType.ENABLE_PUSH;
        operatePushHelper2.putSyncStatus(retryType2, OperatePushHelper.SYNCING);
        OperatePushHelper.getInstance(this.mContext).putSyncStatus(RetryType.DISABLE_PUSH, "");
        retryPolicy(str, retryType2, true, null);
    }

    public final <T extends TBase<T, ?>> void sendMessage(T t2, ActionType actionType, boolean z2, PushMetaInfo pushMetaInfo) {
        sendMessage(t2, actionType, z2, true, pushMetaInfo, true);
    }

    public final <T extends TBase<T, ?>> void sendMessage(T t2, ActionType actionType, boolean z2, boolean z3, PushMetaInfo pushMetaInfo, boolean z4) {
        sendMessage(t2, actionType, z2, z3, pushMetaInfo, z4, this.mContext.getPackageName(), AppInfoHolder.getInstance(this.mContext).getAppID());
    }

    public final <T extends TBase<T, ?>> void sendMessage(T t2, ActionType actionType, boolean z2, boolean z3, PushMetaInfo pushMetaInfo, boolean z4, String str, String str2) {
        sendMessage(t2, actionType, z2, z3, pushMetaInfo, z4, str, str2, true);
    }

    public void clearNotification(String str, String str2) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_CLEAR_NOTIFICATION);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKAGE_NAME, this.mContext.getPackageName());
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_NOTIFY_TITLE, str);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_NOTIFY_DESCRIPTION, str2);
        callService(intentCreateServiceIntent);
    }

    public final <T extends TBase<T, ?>> void sendMessage(T t2, ActionType actionType, boolean z2, boolean z3, PushMetaInfo pushMetaInfo, boolean z4, String str, String str2, boolean z5) {
        sendMessage(t2, actionType, z2, z3, pushMetaInfo, z4, str, str2, z5, true);
    }

    public final <T extends TBase<T, ?>> void sendMessage(T t2, ActionType actionType, boolean z2, boolean z3, PushMetaInfo pushMetaInfo, boolean z4, String str, String str2, boolean z5, boolean z6) {
        XmPushActionContainer xmPushActionContainerConstructResponseContainer;
        if (z6 && !AppInfoHolder.getInstance(this.mContext).isRegistrationValid()) {
            if (z3) {
                addPendRequest(t2, actionType, z2);
                return;
            } else {
                MyLog.w("drop the message before initialization.");
                return;
            }
        }
        if (z5) {
            xmPushActionContainerConstructResponseContainer = PushContainerHelper.generateRequestContainer(this.mContext, t2, actionType, z2, str, str2);
        } else {
            xmPushActionContainerConstructResponseContainer = PushContainerHelper.constructResponseContainer(this.mContext, t2, actionType, z2, str, str2);
        }
        if (pushMetaInfo != null) {
            xmPushActionContainerConstructResponseContainer.setMetaInfo(pushMetaInfo);
        }
        byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionContainerConstructResponseContainer);
        if (bArrConvertThriftObjectToBytes == null) {
            MyLog.w("send message fail, because msgBytes is null.");
            return;
        }
        PerfMessageHelper.collectPerfData(this.mContext.getPackageName(), this.mContext, t2, actionType, bArrConvertThriftObjectToBytes.length);
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.MIPUSH_ACTION_SEND_MESSAGE);
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArrConvertThriftObjectToBytes);
        intentCreateServiceIntent.putExtra(PushConstants.MIPUSH_EXTRA_MESSAGE_CACHE, z4);
        callService(intentCreateServiceIntent);
    }
}
