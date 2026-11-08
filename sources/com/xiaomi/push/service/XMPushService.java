package com.xiaomi.push.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ReceiverHelper;
import com.xiaomi.channel.commonutils.android.Region;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.LogTag;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.BuildSettings;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.misc.ThreadUtils;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.network.NetworkInfoWrapper;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.channel.commonutils.string.MD5;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.clientreport.data.Config;
import com.xiaomi.clientreport.util.ClientReportUtil;
import com.xiaomi.mipush.sdk.stat.db.MessageInfoContract;
import com.xiaomi.network.HostManager;
import com.xiaomi.push.clientreport.DisconnectStatsHelper;
import com.xiaomi.push.clientreport.PowerStatsHelper;
import com.xiaomi.push.crash.SdkCrashManager;
import com.xiaomi.push.log.LogUploader;
import com.xiaomi.push.protobuf.ChannelMessage;
import com.xiaomi.push.service.clientReport.PushClientReportHelper;
import com.xiaomi.push.service.clientReport.PushClientReportManager;
import com.xiaomi.push.service.clientReport.ReportConstants;
import com.xiaomi.push.service.timers.Alarm;
import com.xiaomi.report.ConnReportHandler;
import com.xiaomi.report.ConnReportHelper;
import com.xiaomi.slim.Blob;
import com.xiaomi.slim.SlimConnection;
import com.xiaomi.smack.Connection;
import com.xiaomi.smack.ConnectionConfiguration;
import com.xiaomi.smack.ConnectionListener;
import com.xiaomi.smack.PacketListener;
import com.xiaomi.smack.SmackConfiguration;
import com.xiaomi.smack.XMPPException;
import com.xiaomi.smack.filter.PacketFilter;
import com.xiaomi.smack.packet.IQ;
import com.xiaomi.smack.packet.Message;
import com.xiaomi.smack.packet.Packet;
import com.xiaomi.smack.packet.Presence;
import com.xiaomi.smack.util.TrafficUtils;
import com.xiaomi.tinyData.TinyDataCacheProcessor;
import com.xiaomi.tinyData.TinyDataManager;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import com.xiaomi.xmpush.thrift.ConfigKey;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionRegistration;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class XMPushService extends Service implements ConnectionListener {
    public static final String ACTION_CONNECTIVITY_INFO = "com.xiaomi.channel.CONNECTIVITY_INFO";
    public static final String ACTION_MILIAO_PUSH_STARTED = "com.xiaomi.channel.PUSH_STARTED";
    public static final int CHECK_ALIVE_INTERVAL = 30000;
    private static final String CHID_MILIAO = "1";
    public static final int CONNECTING_TIMEOUT = 15000;
    private static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String DIGEST_EXTRA_KEY = "digest";
    private static final String KEEP_SHORT_HB_ACTION = "com.xiaomi.xmsf.USE_INTELLIGENT_HB";
    private static final String KEEP_SHORT_HB_PERMISSION = "com.xiaomi.xmsf.permission.INTELLIGENT_HB";
    public static final int LOGIN_TIMEOUT = 30000;
    public static final String MSG_DATA_KEY_HOST = "msg_data_hots";
    private static final String PACKAGE_NAME_MILIAO = "com.xiaomi.channel";
    private static final String REQUIRED_WIFI_DIGEST_PERMISSION = "miui.net.wifi.permission.ACCESS_WIFI_DIGEST_INFO";
    private static final String SUPER_POWER_MODE = "power_supersave_mode_open";
    private static final String TAG = "XMPushService";
    public static final int TIMER_RESET_CONNECTION = 30000;
    private static final String WIFI_DIGEST_ACTION = "miui.net.wifi.DIGEST_INFORMATION_CHANGED";
    private static final boolean XMPPCONNECTION_DEBUG_ENABLED = true;
    private static boolean sUseDynamicBroadcast = false;
    private ConnectionConfiguration connConfig;
    private AlarmReceiver mAlarmReceiver;
    private ClientEventDispatcher mClientEventDispatcher;
    private ConnectionChangeReceiver mConnectionChangeReceiver;
    private Connection mCurrentConnection;
    private KeepShortHbReceiver mKeepShortHbReceiver;
    private Object mNetworkCallback;
    private ReconnectionManager mReconnManager;
    private ScreenStateReceiver mScreenStateReceiver;
    private SlimConnection mSlimConnection;
    private ContentObserver mSuperPowerModeObserver;
    private WifiChangeReceiver mWifiChangeReceiver;
    private boolean mTriggerWifiBroadcast = false;
    private int mFalldownStart = 0;
    private int mFalldownEnd = 0;
    private long lastAlive = 0;
    protected Class mJobClazz = XMJobService.class;
    private int sXmsfVersionCode = -1;
    private PacketSync mPacketSync = null;
    private JobScheduler mJobController = null;
    Messenger messenger = null;
    private Collection<NetworkListener> networkListeners = Collections.synchronizedCollection(new ArrayList());
    private ArrayList<PingCallBack> pingCallBacks = new ArrayList<>();
    private PacketListener mPacketListener = new PacketListener() { // from class: com.xiaomi.push.service.XMPushService.1
        @Override // com.xiaomi.smack.PacketListener
        public void process(Blob blob) {
            if (ChannelConstants.isIOTDCMStatTime(blob)) {
                RecordTimeManager.get().recordMsgReceiveTime(blob.getPacketID(), SystemClock.elapsedRealtime(), XMPushService.this.getXmsfVersionCode());
            }
            XMPushService xMPushService = XMPushService.this;
            xMPushService.executeJob(xMPushService.new BlobReceiveJob(blob));
        }

        @Override // com.xiaomi.smack.PacketListener
        public void processPacket(Packet packet) {
            XMPushService xMPushService = XMPushService.this;
            xMPushService.executeJob(xMPushService.new PacketReceiveJob(packet));
        }
    };

    private class AlarmReceiver extends BroadcastReceiver {
        private final Object alarmLock;

        private AlarmReceiver() {
            this.alarmLock = new Object();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyFromWait() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                MyLog.e("[Alarm] Cannot perform lock.notifyAll in the UI thread!");
                return;
            }
            synchronized (this.alarmLock) {
                try {
                    this.alarmLock.notifyAll();
                } catch (Exception e2) {
                    MyLog.w("[Alarm] notify lock. " + e2);
                }
            }
        }

        private void waitForTimeout(long j2) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                MyLog.e("[Alarm] Cannot perform lock.wait in the UI thread!");
                return;
            }
            synchronized (this.alarmLock) {
                try {
                    this.alarmLock.wait(j2);
                } catch (InterruptedException e2) {
                    MyLog.w("[Alarm] interrupt from waiting state. " + e2);
                }
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            MyLog.v("[Alarm] heartbeat alarm has been triggered.");
            if (!PushConstants.ACTION_PING_TIMER.equals(intent.getAction())) {
                MyLog.w("[Alarm] cancel the old ping timer");
                Alarm.stop();
                return;
            }
            if (TextUtils.equals(context.getPackageName(), intent.getPackage())) {
                MyLog.v("[Alarm] Ping XMChannelService on timer");
                try {
                    Intent intent2 = new Intent(context, (Class<?>) XMPushService.class);
                    intent2.putExtra("time_stamp", System.currentTimeMillis());
                    intent2.setAction(PushServiceConstants.ACTION_TIMER);
                    ServiceClient.getInstance(context).startServiceSafely(intent2);
                    waitForTimeout(3000L);
                    MyLog.w("[Alarm] heartbeat alarm finish in " + (System.currentTimeMillis() - jCurrentTimeMillis));
                } catch (Throwable unused) {
                }
            }
        }
    }

    class BindJob extends Job {
        PushClientsManager.ClientLoginInfo mLoginInfo;

        public BindJob(PushClientsManager.ClientLoginInfo clientLoginInfo) {
            super(9);
            this.mLoginInfo = clientLoginInfo;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "bind the client. " + this.mLoginInfo.chid;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            try {
                if (XMPushService.this.isConnected()) {
                    PushClientsManager pushClientsManager = PushClientsManager.getInstance();
                    PushClientsManager.ClientLoginInfo clientLoginInfo = this.mLoginInfo;
                    PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = pushClientsManager.getClientLoginInfoByChidAndUserId(clientLoginInfo.chid, clientLoginInfo.userId);
                    if (clientLoginInfoByChidAndUserId == null) {
                        MyLog.w("ignore bind because the channel " + this.mLoginInfo.chid + " is removed ");
                    } else if (clientLoginInfoByChidAndUserId.status == PushClientsManager.ClientStatus.unbind) {
                        clientLoginInfoByChidAndUserId.setStatus(PushClientsManager.ClientStatus.binding, 0, 0, null, null);
                        XMPushService.this.mCurrentConnection.bind(clientLoginInfoByChidAndUserId);
                        ConnReportHelper.statsBind(XMPushService.this, clientLoginInfoByChidAndUserId);
                    } else {
                        MyLog.w("trying duplicate bind, ingore! " + clientLoginInfoByChidAndUserId.status);
                    }
                } else {
                    MyLog.e("trying bind while the connection is not created, quit!");
                }
            } catch (Exception e2) {
                MyLog.e("Meet error when trying to bind. " + e2);
                XMPushService.this.disconnect(10, e2);
            } catch (Throwable unused) {
            }
        }
    }

    static class BindTimeoutJob extends Job {
        public static final int BIND_TIMEOUT = 60000;
        private final PushClientsManager.ClientLoginInfo mLoginInfo;

        public BindTimeoutJob(PushClientsManager.ClientLoginInfo clientLoginInfo) {
            super(12);
            this.mLoginInfo = clientLoginInfo;
        }

        public boolean equals(Object obj) {
            if (obj instanceof BindTimeoutJob) {
                return TextUtils.equals(((BindTimeoutJob) obj).mLoginInfo.chid, this.mLoginInfo.chid);
            }
            return false;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "bind time out. chid=" + this.mLoginInfo.chid;
        }

        public int hashCode() {
            return this.mLoginInfo.chid.hashCode();
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            this.mLoginInfo.setStatus(PushClientsManager.ClientStatus.unbind, 1, 21, null, null);
        }
    }

    class BlobReceiveJob extends Job {
        private Blob mBlob;

        public BlobReceiveJob(Blob blob) {
            super(8);
            this.mBlob = blob;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "receive a message.";
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            XMPushService.this.mPacketSync.onBlobReceive(this.mBlob);
            if (ChannelConstants.isIOTDCMStatTime(this.mBlob)) {
                XMPushService.this.executeJobDelayed(new RecordTimeManager.ClearJob(), 15000L);
            }
        }
    }

    public class ConnectJob extends Job {
        ConnectJob() {
            super(1);
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "do reconnect..";
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            if (XMPushService.this.shouldReconnect()) {
                XMPushService xMPushService = XMPushService.this;
                if (xMPushService.waitForNetValidated(xMPushService.getApplicationContext())) {
                    XMPushService.this.connect();
                    return;
                }
            }
            MyLog.w("should not connect. quit the job.");
        }
    }

    class ConnectionChangeReceiver extends BroadcastReceiver {
        ConnectionChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Network.onNetworkStatusReceive();
            XMPushService.this.onStart(intent, 1);
        }
    }

    public class DisconnectJob extends Job {

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public Exception f1748e;
        public int reason;

        DisconnectJob(int i2, Exception exc) {
            super(2);
            this.reason = i2;
            this.f1748e = exc;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "disconnect the connection.";
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            XMPushService.this.disconnect(this.reason, this.f1748e);
        }
    }

    class InitJob extends Job {
        InitJob() {
            super(65535);
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "Init Job";
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            XMPushService.this.postOnCreate();
        }
    }

    class IntentJob extends Job {
        private Intent mIntent;

        public IntentJob(Intent intent) {
            super(15);
            this.mIntent = intent;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "Handle intent action = " + this.mIntent.getAction();
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            XMPushService.this.handleIntent(this.mIntent);
        }
    }

    public static abstract class Job extends JobScheduler.Job {
        public static final int TYPE_BIND_TIMEOUT = 12;
        public static final int TYPE_BIND_UNBIND = 9;
        public static final int TYPE_CLEAR_ACCOUNT_CACHE = 14;
        public static final int TYPE_CONNECT = 1;
        public static final int TYPE_CONNECTING_TIMEOUT = 10;
        public static final int TYPE_DISCONNECT = 2;
        public static final int TYPE_HANDLE_INTENT = 15;
        public static final int TYPE_INIT = 65535;
        public static final int TYPE_MAX = 16;
        public static final int TYPE_MIN = 1;
        public static final int TYPE_NOTYPE_JOB = 0;
        public static final int TYPE_PING_TIMEOUT = 13;
        public static final int TYPE_PREPARE_MIPUSH_ACCOUNT = 11;
        public static final int TYPE_QUIT = 5;
        public static final int TYPE_RECEIVE_CHALLENGE = 7;
        public static final int TYPE_RECEIVE_MSG = 8;
        public static final int TYPE_RECEIVE_TIMEOUT = 6;
        public static final int TYPE_RECORD_TIME_TASK_CLEAR = 17;
        public static final int TYPE_RESET_CONNECT = 3;
        public static final int TYPE_SEND_MSG = 4;

        public Job(int i2) {
            super(i2);
        }

        public abstract String getDesc();

        public abstract void process();

        @Override // java.lang.Runnable
        public void run() {
            int i2 = this.type;
            if (i2 != 4 && i2 != 8) {
                MyLog.w(LogTag.TAG_JOB, getDesc());
            }
            process();
        }
    }

    class KeepShortHbReceiver extends BroadcastReceiver {
        KeepShortHbReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MyLog.w("[HB] hold short heartbeat, " + MIUIUtils.intentToString(intent));
            if (intent == null || intent.getExtras() == null) {
                return;
            }
            XMPushService.this.onStart(intent, 1);
        }
    }

    class KillJob extends Job {
        public KillJob() {
            super(5);
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "ask the job queue to quit";
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            XMPushService.this.mJobController.quit();
        }
    }

    public class MessageChannel extends Binder {
        public MessageChannel() {
        }

        public void sendMessage(Message message) {
            MyLog.w("drop the message in MessageChannel: " + message.toXML());
        }
    }

    class PacketReceiveJob extends Job {
        private Packet mPacket;

        public PacketReceiveJob(Packet packet) {
            super(8);
            this.mPacket = packet;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "receive a message.";
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            XMPushService.this.mPacketSync.onPacketReceive(this.mPacket);
        }
    }

    public interface PingCallBack {
        void pingFollowUpAction();
    }

    class ReBindJob extends Job {
        PushClientsManager.ClientLoginInfo mLoginInfo;

        public ReBindJob(PushClientsManager.ClientLoginInfo clientLoginInfo) {
            super(4);
            this.mLoginInfo = clientLoginInfo;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "rebind the client. " + this.mLoginInfo.chid;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            try {
                this.mLoginInfo.setStatus(PushClientsManager.ClientStatus.unbind, 1, 16, null, null);
                Connection connection = XMPushService.this.mCurrentConnection;
                PushClientsManager.ClientLoginInfo clientLoginInfo = this.mLoginInfo;
                connection.unbind(clientLoginInfo.chid, clientLoginInfo.userId);
                XMPushService xMPushService = XMPushService.this;
                xMPushService.executeJobDelayed(xMPushService.new BindJob(this.mLoginInfo), 300L);
            } catch (XMPPException e2) {
                MyLog.e(e2);
                XMPushService.this.disconnect(10, e2);
            }
        }
    }

    class ResetConnectionJob extends Job {
        ResetConnectionJob() {
            super(3);
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "reset the connection.";
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            XMPushService.this.disconnect(11, null);
            if (XMPushService.this.shouldReconnect()) {
                XMPushService xMPushService = XMPushService.this;
                if (xMPushService.waitForNetValidated(xMPushService.getApplicationContext())) {
                    XMPushService.this.connect();
                }
            }
        }
    }

    class ScreenStateReceiver extends BroadcastReceiver {
        ScreenStateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            XMPushService.this.onStart(intent, 1);
        }
    }

    class UnbindJob extends Job {
        String kickType;
        PushClientsManager.ClientLoginInfo mLoginInfo;
        int mNotifyType;
        String reason;

        public UnbindJob(PushClientsManager.ClientLoginInfo clientLoginInfo, int i2, String str, String str2) {
            super(9);
            this.mLoginInfo = clientLoginInfo;
            this.mNotifyType = i2;
            this.kickType = str;
            this.reason = str2;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "unbind the channel. " + this.mLoginInfo.chid;
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            if (this.mLoginInfo.status != PushClientsManager.ClientStatus.unbind && XMPushService.this.mCurrentConnection != null) {
                try {
                    Connection connection = XMPushService.this.mCurrentConnection;
                    PushClientsManager.ClientLoginInfo clientLoginInfo = this.mLoginInfo;
                    connection.unbind(clientLoginInfo.chid, clientLoginInfo.userId);
                } catch (XMPPException e2) {
                    MyLog.e(e2);
                    XMPushService.this.disconnect(10, e2);
                }
            }
            this.mLoginInfo.setStatus(PushClientsManager.ClientStatus.unbind, this.mNotifyType, 0, this.reason, this.kickType);
        }
    }

    class WifiChangeReceiver extends BroadcastReceiver {
        WifiChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!XMPushService.this.mTriggerWifiBroadcast) {
                XMPushService.this.mTriggerWifiBroadcast = true;
            }
            XMPushService.this.onStart(intent, 1);
        }
    }

    private static void addReservedHost(String str) {
        if (Region.China.name().equals(str)) {
            HostManager.addReservedHost(ConnectionConfiguration.XMPP_SERVER_CHINA_HOST_P, ConnectionConfiguration.XMPP_SERVER_CHINA_HOST_P);
            HostManager.addReservedHost(ConnectionConfiguration.XMPP_SERVER_CHINA_HOST_P, "111.13.141.211:443");
            HostManager.addReservedHost(ConnectionConfiguration.XMPP_SERVER_CHINA_HOST_P, "39.156.81.172:443");
            HostManager.addReservedHost(ConnectionConfiguration.XMPP_SERVER_CHINA_HOST_P, "111.202.1.250:443");
            HostManager.addReservedHost(ConnectionConfiguration.XMPP_SERVER_CHINA_HOST_P, "123.125.102.213:443");
            HostManager.addReservedHost(HostManager.RESOLVER_HOST, "111.13.142.153:443");
            HostManager.addReservedHost(HostManager.RESOLVER_HOST, "111.202.1.252:443");
        }
    }

    private void broadcastNetworkAvailable(boolean z2) {
        try {
            if (SystemUtils.isBootCompleted()) {
                if (!z2) {
                    if (MIUIUtils.isXMSF(this)) {
                        Intent intent = new Intent("miui.intent.action.NETWORK_BLOCKED");
                        intent.addFlags(LogType.LOG_TYPE_F_SAMPLING_CURRENT);
                        sendBroadcast(intent);
                        return;
                    }
                    return;
                }
                if (MIUIUtils.isXMSF(this)) {
                    Intent intent2 = new Intent("miui.intent.action.NETWORK_CONNECTED");
                    intent2.addFlags(LogType.LOG_TYPE_F_SAMPLING_CURRENT);
                    sendBroadcast(intent2);
                }
                for (NetworkListener networkListener : (NetworkListener[]) this.networkListeners.toArray(new NetworkListener[0])) {
                    networkListener.onNetwrokAvaible();
                }
            }
        } catch (Exception e2) {
            MyLog.e(e2);
        }
    }

    private boolean canOpenForegroundService() {
        if (TextUtils.equals(getPackageName(), PushConstants.PUSH_SERVICE_PACKAGE_NAME)) {
            return false;
        }
        return OnlineConfig.getInstance(this).getBooleanValue(ConfigKey.ForegroundServiceSwitch.getValue(), false);
    }

    private void checkAlive(boolean z2) {
        this.lastAlive = SystemClock.elapsedRealtime();
        if (!isConnected()) {
            scheduleConnect(true);
        } else if (Network.hasNetwork(this)) {
            executeJobNow(new PingJob(z2));
        } else {
            executeJobNow(new DisconnectJob(17, null));
            scheduleConnect(true);
        }
    }

    private String checkAndUpdateCountryCode(AppRegionStorage appRegionStorage) {
        if (appRegionStorage == null || !TextUtils.isEmpty(appRegionStorage.getCountryCode()) || TextUtils.isEmpty(appRegionStorage.getRegion())) {
            MyLog.i("no need to check country code");
            return null;
        }
        String miuiCountryCode = PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName()) ? getMiuiCountryCode() : MIUIUtils.getCountryCode();
        if (TextUtils.isEmpty(miuiCountryCode)) {
            MyLog.i("check no country code");
            return null;
        }
        String strName = MIUIUtils.getRegion(miuiCountryCode).name();
        if (TextUtils.equals(strName, appRegionStorage.getRegion())) {
            MyLog.w("current country code: " + miuiCountryCode);
            return miuiCountryCode;
        }
        MyLog.w("not update country code, because not equals " + strName);
        return null;
    }

    private void clearPingCallbacks() {
        synchronized (this.pingCallBacks) {
            this.pingCallBacks.clear();
        }
    }

    private void closeAllChannelByChid(String str, int i2) {
        Collection<PushClientsManager.ClientLoginInfo> allClientLoginInfoByChid = PushClientsManager.getInstance().getAllClientLoginInfoByChid(str);
        if (allClientLoginInfoByChid != null) {
            for (PushClientsManager.ClientLoginInfo clientLoginInfo : allClientLoginInfoByChid) {
                if (clientLoginInfo != null) {
                    executeJob(new UnbindJob(clientLoginInfo, i2, null, null));
                }
            }
        }
        PushClientsManager.getInstance().deactivateAllClientByChid(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connect() {
        Connection connection = this.mCurrentConnection;
        if (connection != null && connection.isConnecting()) {
            MyLog.e("try to connect while connecting.");
            return;
        }
        Connection connection2 = this.mCurrentConnection;
        if (connection2 != null && connection2.isConnected()) {
            MyLog.e("try to connect while is connected.");
            return;
        }
        this.connConfig.setConnectionPoint(Network.getActiveConnPoint(this));
        connectBySlim();
        if (this.mCurrentConnection == null) {
            PushClientsManager.getInstance().notifyConnectionFailed(this);
            broadcastNetworkAvailable(false);
        }
    }

    private void connectBySlim() {
        try {
            this.mSlimConnection.addPacketListener(this.mPacketListener, new PacketFilter() { // from class: com.xiaomi.push.service.XMPushService.13
                @Override // com.xiaomi.smack.filter.PacketFilter
                public boolean accept(Packet packet) {
                    return true;
                }
            });
            this.mSlimConnection.connect();
            this.mCurrentConnection = this.mSlimConnection;
        } catch (XMPPException e2) {
            MyLog.e("fail to create Slim connection", e2);
            this.mSlimConnection.disconnect(3, e2);
        }
    }

    private String ensureCountryCodeAvaible() {
        String miuiCountryCode;
        ThreadUtils.checkNotUIThread();
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        Object obj = new Object();
        int i2 = 0;
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName())) {
            PushProvision pushProvision = PushProvision.getInstance(this);
            String miuiCountryCode2 = null;
            while (true) {
                if (!TextUtils.isEmpty(miuiCountryCode2) && pushProvision.getProvisioned() != 0) {
                    miuiCountryCode = getMiuiCountryCode();
                    break;
                }
                if (TextUtils.isEmpty(miuiCountryCode2)) {
                    miuiCountryCode2 = getMiuiCountryCode();
                }
                try {
                    synchronized (obj) {
                        if (i2 < 30) {
                            try {
                                obj.wait(1000L);
                            } catch (Throwable th) {
                                throw th;
                            }
                        } else {
                            obj.wait(MessageInfoContract.TIMEOUT);
                        }
                    }
                } catch (InterruptedException unused) {
                }
                i2++;
            }
        } else {
            miuiCountryCode = "CN";
        }
        MyLog.w("wait coutrycode :" + miuiCountryCode + " cost = " + (SystemClock.elapsedRealtime() - jElapsedRealtime) + " , count = " + i2);
        return miuiCountryCode;
    }

    private void executeJobNow(Job job) {
        this.mJobController.executeJobNow(job);
    }

    private int[] getFalldownTimeRange() {
        String[] strArrSplit;
        String stringValue = OnlineConfig.getInstance(getApplicationContext()).getStringValue(ConfigKey.FallDownTimeRange.getValue(), "");
        if (!TextUtils.isEmpty(stringValue) && (strArrSplit = stringValue.split(",")) != null && strArrSplit.length >= 2) {
            int[] iArr = new int[2];
            try {
                iArr[0] = Integer.valueOf(strArrSplit[0]).intValue();
                int iIntValue = Integer.valueOf(strArrSplit[1]).intValue();
                iArr[1] = iIntValue;
                int i2 = iArr[0];
                if (i2 >= 0 && i2 <= 23 && iIntValue >= 0 && iIntValue <= 23 && i2 != iIntValue) {
                    return iArr;
                }
            } catch (NumberFormatException e2) {
                MyLog.e("parse falldown time range failure: " + e2);
            }
        }
        return null;
    }

    private String getMiuiCountryCode() {
        String property = MIUIUtils.getProperty("ro.miui.region");
        return TextUtils.isEmpty(property) ? MIUIUtils.getProperty("ro.product.locale.region") : property;
    }

    @TargetApi(11)
    public static Notification getPushServiceNotification(Context context) {
        Intent intent = new Intent(context, (Class<?>) XMPushService.class);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(context.getApplicationInfo().icon);
        builder.setContentTitle("Push Service");
        builder.setContentText("Push Service");
        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, LogType.LOG_TYPE_SAMPLING_CPUFREQ));
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleIntent(Intent intent) {
        int i2;
        String strMD5_16;
        int intExtra;
        SharedPreferences sharedPreferences;
        PushClientsManager pushClientsManager = PushClientsManager.getInstance();
        boolean z2 = true;
        if (PushConstants.ACTION_OPEN_CHANNEL.equalsIgnoreCase(intent.getAction()) || PushConstants.ACTION_FORCE_RECONNECT.equalsIgnoreCase(intent.getAction())) {
            String stringExtra = intent.getStringExtra(PushConstants.EXTRA_CHANNEL_ID);
            if (TextUtils.isEmpty(intent.getStringExtra(PushConstants.EXTRA_SECURITY))) {
                MyLog.w("security is empty. ignore.");
                return;
            }
            if (TextUtils.isEmpty(stringExtra)) {
                MyLog.e("channel id is empty, do nothing!");
                return;
            }
            boolean zShouldRebind = shouldRebind(stringExtra, intent);
            PushClientsManager.ClientLoginInfo clientLoginInfoUpdatePushClient = updatePushClient(stringExtra, intent);
            if (!Network.isConnectivity(this)) {
                this.mClientEventDispatcher.notifyChannelOpenResult(this, clientLoginInfoUpdatePushClient, false, 2, null);
                return;
            }
            if (!isConnected()) {
                scheduleConnect(true);
                return;
            }
            PushClientsManager.ClientStatus clientStatus = clientLoginInfoUpdatePushClient.status;
            if (clientStatus == PushClientsManager.ClientStatus.unbind) {
                executeJobNow(new BindJob(clientLoginInfoUpdatePushClient));
                return;
            }
            if (zShouldRebind) {
                executeJobNow(new ReBindJob(clientLoginInfoUpdatePushClient));
                return;
            } else if (clientStatus == PushClientsManager.ClientStatus.binding) {
                MyLog.w(String.format("the client is binding. %1$s %2$s.", clientLoginInfoUpdatePushClient.chid, PushClientsManager.ClientLoginInfo.getResource(clientLoginInfoUpdatePushClient.userId)));
                return;
            } else {
                if (clientStatus == PushClientsManager.ClientStatus.binded) {
                    this.mClientEventDispatcher.notifyChannelOpenResult(this, clientLoginInfoUpdatePushClient, true, 0, null);
                    return;
                }
                return;
            }
        }
        if (PushConstants.ACTION_CLOSE_CHANNEL.equalsIgnoreCase(intent.getAction())) {
            String stringExtra2 = intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME);
            String stringExtra3 = intent.getStringExtra(PushConstants.EXTRA_CHANNEL_ID);
            String stringExtra4 = intent.getStringExtra(PushConstants.EXTRA_USER_ID);
            MyLog.w("Service called close channel chid = " + stringExtra3 + " res = " + PushClientsManager.ClientLoginInfo.getResource(stringExtra4));
            if (TextUtils.isEmpty(stringExtra3)) {
                Iterator<String> it = pushClientsManager.queryChannelIdByPackage(stringExtra2).iterator();
                while (it.hasNext()) {
                    closeAllChannelByChid(it.next(), 2);
                }
                return;
            } else if (TextUtils.isEmpty(stringExtra4)) {
                closeAllChannelByChid(stringExtra3, 2);
                return;
            } else {
                closeChannel(stringExtra3, stringExtra4, 2, null, null);
                return;
            }
        }
        if (PushConstants.ACTION_SEND_MESSAGE.equalsIgnoreCase(intent.getAction())) {
            sendMessage(intent);
            return;
        }
        if (PushConstants.ACTION_BATCH_SEND_MESSAGE.equalsIgnoreCase(intent.getAction())) {
            sendMessages(intent);
            return;
        }
        if (PushConstants.ACTION_SEND_IQ.equalsIgnoreCase(intent.getAction())) {
            Packet packetPreparePacket = preparePacket(new IQ(intent.getBundleExtra(PushConstants.EXTRA_PACKET)), intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME), intent.getStringExtra(PushConstants.EXTRA_SESSION));
            if (packetPreparePacket != null) {
                executeJobNow(new SendMessageJob(this, Blob.from(packetPreparePacket, pushClientsManager.getClientLoginInfoByChidAndUserId(packetPreparePacket.getChannelId(), packetPreparePacket.getFrom()).security)));
                return;
            }
            return;
        }
        if (PushConstants.ACTION_SEND_PRESENCE.equalsIgnoreCase(intent.getAction())) {
            Packet packetPreparePacket2 = preparePacket(new Presence(intent.getBundleExtra(PushConstants.EXTRA_PACKET)), intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME), intent.getStringExtra(PushConstants.EXTRA_SESSION));
            if (packetPreparePacket2 != null) {
                executeJobNow(new SendMessageJob(this, Blob.from(packetPreparePacket2, pushClientsManager.getClientLoginInfoByChidAndUserId(packetPreparePacket2.getChannelId(), packetPreparePacket2.getFrom()).security)));
                return;
            }
            return;
        }
        if (PushConstants.ACTION_RESET_CONNECTION.equals(intent.getAction())) {
            String stringExtra5 = intent.getStringExtra(PushConstants.EXTRA_CHANNEL_ID);
            String stringExtra6 = intent.getStringExtra(PushConstants.EXTRA_USER_ID);
            if (stringExtra5 != null) {
                MyLog.w("request reset connection from chid = " + stringExtra5);
                PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(stringExtra5, stringExtra6);
                if (clientLoginInfoByChidAndUserId != null && clientLoginInfoByChidAndUserId.security.equals(intent.getStringExtra(PushConstants.EXTRA_SECURITY)) && clientLoginInfoByChidAndUserId.status == PushClientsManager.ClientStatus.binded) {
                    Connection currentConnection = getCurrentConnection();
                    if (currentConnection == null || !currentConnection.isReadAlive(SystemClock.elapsedRealtime() - 15000)) {
                        executeJobNow(new ResetConnectionJob());
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        String string = null;
        string = null;
        clientLoginInfoByChidAndUserId = null;
        PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId2 = null;
        boolean z3 = false;
        if (PushConstants.ACTION_UPDATE_CHANNEL_INFO.equals(intent.getAction())) {
            String stringExtra7 = intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME);
            List<String> listQueryChannelIdByPackage = pushClientsManager.queryChannelIdByPackage(stringExtra7);
            if (listQueryChannelIdByPackage.isEmpty()) {
                MyLog.w("open channel should be called first before update info, pkg=" + stringExtra7);
                return;
            }
            String stringExtra8 = intent.getStringExtra(PushConstants.EXTRA_CHANNEL_ID);
            String stringExtra9 = intent.getStringExtra(PushConstants.EXTRA_USER_ID);
            if (TextUtils.isEmpty(stringExtra8)) {
                stringExtra8 = listQueryChannelIdByPackage.get(0);
            }
            if (TextUtils.isEmpty(stringExtra9)) {
                Collection<PushClientsManager.ClientLoginInfo> allClientLoginInfoByChid = pushClientsManager.getAllClientLoginInfoByChid(stringExtra8);
                if (allClientLoginInfoByChid != null && !allClientLoginInfoByChid.isEmpty()) {
                    clientLoginInfoByChidAndUserId2 = allClientLoginInfoByChid.iterator().next();
                }
            } else {
                clientLoginInfoByChidAndUserId2 = pushClientsManager.getClientLoginInfoByChidAndUserId(stringExtra8, stringExtra9);
            }
            if (clientLoginInfoByChidAndUserId2 != null) {
                if (intent.hasExtra(PushConstants.EXTRA_CLIENT_ATTR)) {
                    clientLoginInfoByChidAndUserId2.clientExtra = intent.getStringExtra(PushConstants.EXTRA_CLIENT_ATTR);
                }
                if (intent.hasExtra(PushConstants.EXTRA_CLOUD_ATTR)) {
                    clientLoginInfoByChidAndUserId2.cloudExtra = intent.getStringExtra(PushConstants.EXTRA_CLOUD_ATTR);
                    return;
                }
                return;
            }
            return;
        }
        if ("android.intent.action.SCREEN_ON".equals(intent.getAction()) || "android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
            if (!"android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                if ("android.intent.action.SCREEN_OFF".equals(intent.getAction()) && shouldFalldown() && Alarm.isAlive()) {
                    MyLog.w("enter falldown mode, stop alarm.");
                    Alarm.stop();
                    return;
                }
                return;
            }
            if (shouldFalldown()) {
                return;
            }
            MyLog.w("exit falldown mode, activate alarm.");
            updateAlarmTimer();
            if (isConnected() || isConnecting()) {
                return;
            }
            scheduleConnect(true);
            return;
        }
        if (PushConstants.MIPUSH_ACTION_REGISTER_APP.equals(intent.getAction())) {
            if (PushProvision.getInstance(getApplicationContext()).checkProvisioned() && PushProvision.getInstance(getApplicationContext()).getProvisioned() == 0) {
                MyLog.w("register without being provisioned. " + intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE));
                return;
            }
            final byte[] byteArrayExtra = intent.getByteArrayExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD);
            final String stringExtra10 = intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE);
            boolean booleanExtra = intent.getBooleanExtra(PushConstants.MIPUSH_EXTRA_ENV_CHANAGE, false);
            final int intExtra2 = intent.getIntExtra(PushConstants.MIPUSH_EXTRA_ENV_TYPE, 1);
            MIPushAppInfo.getInstance(this).removeUnRegisteredPkg(stringExtra10);
            if (!booleanExtra || PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName())) {
                registerForMiPushApp(byteArrayExtra, stringExtra10);
                return;
            } else {
                executeJobNow(new Job(14) { // from class: com.xiaomi.push.service.XMPushService.10
                    @Override // com.xiaomi.push.service.XMPushService.Job
                    public String getDesc() {
                        return "clear account cache.";
                    }

                    @Override // com.xiaomi.push.service.XMPushService.Job
                    public void process() {
                        MIPushAccountUtils.clearAccount(XMPushService.this);
                        PushClientsManager.getInstance().deactivateAllClientByChid("5");
                        BuildSettings.setEnvType(intExtra2);
                        XMPushService.this.connConfig.setHost(ConnectionConfiguration.getXmppServerHost());
                        MyLog.w("clear account and start registration. " + stringExtra10);
                        XMPushService.this.registerForMiPushApp(byteArrayExtra, stringExtra10);
                    }
                });
                return;
            }
        }
        if (PushConstants.MIPUSH_ACTION_SEND_MESSAGE.equals(intent.getAction()) || PushConstants.MIPUSH_ACTION_UNREGISTER_APP.equals(intent.getAction())) {
            String stringExtra11 = intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE);
            byte[] byteArrayExtra2 = intent.getByteArrayExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD);
            boolean booleanExtra2 = intent.getBooleanExtra(PushConstants.MIPUSH_EXTRA_MESSAGE_CACHE, true);
            if (DuplicateSDKMsgChecker.isMsgDuplicated(byteArrayExtra2, stringExtra11)) {
                MyLog.w("duplicate msg from: " + String.valueOf(stringExtra11));
                return;
            }
            if (PushConstants.MIPUSH_ACTION_UNREGISTER_APP.equals(intent.getAction())) {
                MIPushAppInfo.getInstance(this).addUnRegisteredPkg(stringExtra11);
                if (MIUIUtils.isXMSF(getApplicationContext())) {
                    MIPushEventProcessorAgent.clearMessage(stringExtra11);
                }
            }
            sendMessage(stringExtra11, byteArrayExtra2, booleanExtra2);
            return;
        }
        if (PushServiceConstants.ACTION_UNINSTALL.equals(intent.getAction())) {
            String stringExtra12 = intent.getStringExtra(PushServiceConstants.EXTRA_UNINSTALL_PKG_NAME);
            if (stringExtra12 == null || TextUtils.isEmpty(stringExtra12.trim())) {
                return;
            }
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(stringExtra12, 0);
                if (packageInfo == null || packageInfo.applicationInfo == null || !DeviceInfo.isDualAppUninstalled(this, packageInfo.packageName)) {
                    z2 = false;
                } else {
                    MyLog.w("dual space's app uninstalled " + stringExtra12);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            if (PACKAGE_NAME_MILIAO.equals(stringExtra12) && !PushClientsManager.getInstance().getAllClientLoginInfoByChid("1").isEmpty() && z2) {
                closeAllChannelByChid("1", 0);
                MyLog.w("close the miliao channel as the app is uninstalled.");
                return;
            }
            SharedPreferences sharedPreferences2 = getSharedPreferences(PushServiceConstants.PREF_KEY_REGISTERED_PKGS, 0);
            String string2 = sharedPreferences2.getString(stringExtra12, null);
            if (TextUtils.isEmpty(string2) || !z2) {
                return;
            }
            SharedPreferences.Editor editorEdit = sharedPreferences2.edit();
            editorEdit.remove(stringExtra12);
            editorEdit.commit();
            if (MIPushNotificationHelper.hasLocalNotifyType(this, stringExtra12)) {
                MIPushNotificationHelper.clearLocalNotifyType(this, stringExtra12);
            }
            MIPushNotificationHelper.clearNotification(this, stringExtra12);
            NotificationChannelHelper.onPackageDataCleanedOrUninstall(getApplicationContext(), stringExtra12);
            if (!isConnected() || string2 == null) {
                return;
            }
            try {
                MIPushHelper.sendPacket(this, MIPushHelper.contructAppAbsentMessage(stringExtra12, string2));
                MyLog.w("uninstall " + stringExtra12 + " msg sent");
                return;
            } catch (XMPPException e2) {
                MyLog.e("Fail to send Message: " + e2.getMessage());
                disconnect(10, e2);
                return;
            }
        }
        if (PushServiceConstants.ACTION_PACKAGE_DATA_CLEARED.equals(intent.getAction())) {
            String stringExtra13 = intent.getStringExtra(PushServiceConstants.EXTRA_DATA_CLEARED_PKG_NAME);
            if (TextUtils.isEmpty(stringExtra13)) {
                return;
            }
            try {
                sharedPreferences = getSharedPreferences(PushServiceConstants.PREF_KEY_REGISTERED_PKGS, 0);
                if (sharedPreferences != null) {
                    try {
                        string = sharedPreferences.getString(stringExtra13, null);
                    } catch (Throwable th) {
                        th = th;
                        MyLog.w("Fail to get sp or appId : " + th);
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                sharedPreferences = null;
            }
            if (!TextUtils.isEmpty(string)) {
                SharedPreferences.Editor editorEdit2 = sharedPreferences.edit();
                editorEdit2.remove(stringExtra13);
                editorEdit2.commit();
                if (MIPushNotificationHelper.hasLocalNotifyType(this, stringExtra13)) {
                    MIPushNotificationHelper.clearLocalNotifyType(this, stringExtra13);
                }
                MIPushNotificationHelper.clearNotification(this, stringExtra13);
                sendMessage(stringExtra13, XmPushThriftSerializeUtils.convertThriftObjectToBytes(MIPushHelper.contructAppDataCleared(stringExtra13, string)), true);
            }
            NotificationChannelHelper.onPackageDataCleanedOrUninstall(this, stringExtra13);
            if (MIUIUtils.isXMSF(getApplicationContext())) {
                MIPushEventProcessorAgent.clearMessage(stringExtra13);
                return;
            }
            return;
        }
        if (PushConstants.MIPUSH_ACTION_CLEAR_NOTIFICATION.equals(intent.getAction())) {
            String stringExtra14 = intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME);
            int intExtra3 = intent.getIntExtra(PushConstants.EXTRA_NOTIFY_ID, -2);
            if (TextUtils.isEmpty(stringExtra14)) {
                return;
            }
            if (intExtra3 >= -1) {
                MIPushNotificationHelper.clearNotification(this, stringExtra14, intExtra3, intent.getIntExtra(PushConstants.EXTRA_CLICKED_BUTTON, -1));
                return;
            } else {
                MIPushNotificationHelper.clearNotification(this, stringExtra14, intent.getStringExtra(PushConstants.EXTRA_NOTIFY_TITLE), intent.getStringExtra(PushConstants.EXTRA_NOTIFY_DESCRIPTION));
                return;
            }
        }
        if (PushConstants.MIPUSH_ACTION_CLEAR_HEADSUPNOTIFICATION.equals(intent.getAction())) {
            String stringExtra15 = intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME);
            if (TextUtils.isEmpty(stringExtra15)) {
                return;
            }
            MIPushNotificationHelper.clearHeadsUpNotification(this, stringExtra15);
            return;
        }
        if (PushConstants.MIPUSH_ACTION_SET_NOTIFICATION_TYPE.equals(intent.getAction())) {
            String stringExtra16 = intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME);
            String stringExtra17 = intent.getStringExtra(PushConstants.EXTRA_SIG);
            if (intent.hasExtra(PushConstants.EXTRA_NOTIFY_TYPE)) {
                intExtra = intent.getIntExtra(PushConstants.EXTRA_NOTIFY_TYPE, 0);
                strMD5_16 = MD5.MD5_16(stringExtra16 + intExtra);
            } else {
                strMD5_16 = MD5.MD5_16(stringExtra16);
                intExtra = 0;
                z3 = true;
            }
            if (TextUtils.isEmpty(stringExtra16) || !TextUtils.equals(stringExtra17, strMD5_16)) {
                MyLog.e("invalid notification for " + stringExtra16);
                return;
            }
            if (z3) {
                MIPushNotificationHelper.clearLocalNotifyType(this, stringExtra16);
                return;
            } else {
                MIPushNotificationHelper.setLocalNotifyType(this, stringExtra16, intExtra);
                return;
            }
        }
        if (PushConstants.MIPUSH_ACTION_DISABLE_PUSH.equals(intent.getAction())) {
            String stringExtra18 = intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE);
            if (!TextUtils.isEmpty(stringExtra18)) {
                MIPushAppInfo.getInstance(this).addDisablePushPkg(stringExtra18);
            }
            if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName())) {
                return;
            }
            disconnect(19, null);
            updateAlarmTimer();
            stopSelf();
            return;
        }
        if (PushConstants.MIPUSH_ACTION_DISABLE_PUSH_MESSAGE.equals(intent.getAction()) || PushConstants.MIPUSH_ACTION_ENABLE_PUSH_MESSAGE.equals(intent.getAction())) {
            String stringExtra19 = intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE);
            byte[] byteArrayExtra3 = intent.getByteArrayExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD);
            String stringExtra20 = intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_ID);
            String stringExtra21 = intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_TOKEN);
            if (PushConstants.MIPUSH_ACTION_DISABLE_PUSH_MESSAGE.equals(intent.getAction())) {
                MIPushAppInfo.getInstance(this).addDisablePushPkgCache(stringExtra19);
            }
            if (PushConstants.MIPUSH_ACTION_ENABLE_PUSH_MESSAGE.equals(intent.getAction())) {
                MIPushAppInfo.getInstance(this).removeDisablePushPkg(stringExtra19);
                MIPushAppInfo.getInstance(this).removeDisablePushPkgCache(stringExtra19);
            }
            if (byteArrayExtra3 == null) {
                MIPushClientManager.notifyError(this, stringExtra19, byteArrayExtra3, 70000003, "null payload");
                return;
            }
            MIPushClientManager.addPendingMessages(stringExtra19, byteArrayExtra3);
            executeJob(new MIPushAppRegisterJob(this, stringExtra19, stringExtra20, stringExtra21, byteArrayExtra3));
            if (PushConstants.MIPUSH_ACTION_ENABLE_PUSH_MESSAGE.equals(intent.getAction())) {
                if (this.mConnectionChangeReceiver == null) {
                    this.mConnectionChangeReceiver = new ConnectionChangeReceiver();
                    ReceiverHelper.registerReceiver(this, this.mConnectionChangeReceiver, new IntentFilter(CONNECTIVITY_ACTION), (String) null, (Handler) null);
                }
                if (this.mNetworkCallback == null) {
                    this.mNetworkCallback = Network.addNetworkCallBack(this);
                    return;
                }
                return;
            }
            return;
        }
        if (PushConstants.MIPUSH_ACTION_SEND_TINYDATA.equals(intent.getAction())) {
            String stringExtra22 = intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE);
            byte[] byteArrayExtra4 = intent.getByteArrayExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD);
            ClientUploadDataItem clientUploadDataItem = new ClientUploadDataItem();
            try {
                XmPushThriftSerializeUtils.convertByteArrayToThriftObject(clientUploadDataItem, byteArrayExtra4);
                TinyDataManager.getInstance(this).upload(clientUploadDataItem, stringExtra22);
                return;
            } catch (TException e3) {
                MyLog.e(e3);
                return;
            }
        }
        if (PushServiceConstants.ACTION_TIMER.equalsIgnoreCase(intent.getAction())) {
            MyLog.w("[Alarm] Service called on timer");
            if (!shouldFalldown()) {
                Alarm.registerPing(false);
                if (shouldCheckAlive()) {
                    checkAlive(false);
                }
            } else if (Alarm.isAlive()) {
                MyLog.w("enter falldown mode, stop alarm");
                Alarm.stop();
            }
            AlarmReceiver alarmReceiver = this.mAlarmReceiver;
            if (alarmReceiver != null) {
                alarmReceiver.notifyFromWait();
                return;
            }
            return;
        }
        if (PushServiceConstants.ACTION_CHECK_ALIVE.equalsIgnoreCase(intent.getAction())) {
            MyLog.w("Service called on check alive.");
            if (shouldCheckAlive()) {
                checkAlive(false);
                return;
            }
            return;
        }
        if (PushConstants.MIPUSH_ACTION_THIRDPARTY_HINT.equals(intent.getAction())) {
            MyLog.w("on thirdpart push :" + intent.getStringExtra(PushConstants.EXTRA_THIRDPARTY_HINT_DESC));
            Alarm.changePolicy(this, intent.getIntExtra(PushConstants.EXTRA_THIRDPARTY_HINT_LEVEL, 0));
            return;
        }
        if (CONNECTIVITY_ACTION.equals(intent.getAction())) {
            networkChanged();
            return;
        }
        if (WIFI_DIGEST_ACTION.equals(intent.getAction())) {
            wifiChanged(intent);
            return;
        }
        if (KEEP_SHORT_HB_ACTION.equals(intent.getAction())) {
            if (intent.getExtras() == null || (i2 = intent.getExtras().getInt("effectivePeriod", 0)) <= 0 || i2 > 604800) {
                return;
            }
            HeartbeatHolder.getInstance(getApplicationContext()).updateShortHbEffectivePeriod(i2);
            return;
        }
        if (!PushConstants.ACTION_CLIENT_REPORT_CONFIG.equals(intent.getAction())) {
            if (PushConstants.ACTION_HANDLE_FCM_MSG.equals(intent.getAction())) {
                FCMChannelHelper.handleFcmMessage(getApplicationContext(), intent);
                return;
            } else {
                if (PushConstants.ACTION_APP_NOTIFY_MSG.equals(intent.getAction())) {
                    String stringExtra23 = intent.getStringExtra(PushConstants.EXTRA_DOWNWARD_PACKET_ID);
                    if (TextUtils.isEmpty(stringExtra23)) {
                        return;
                    }
                    RecordTimeManager.get().recordAppReceiveTime(stringExtra23, intent.getLongExtra(PushConstants.EXTRA_APP_REV_TIME, 0L));
                    return;
                }
                return;
            }
        }
        boolean booleanExtra3 = intent.getBooleanExtra(PushConstants.EXTRA_CR_EVENT_SWITCH, false);
        long longExtra = intent.getLongExtra(PushConstants.EXTRA_CR_EVENT_FREQUENCY, 86400L);
        boolean booleanExtra4 = intent.getBooleanExtra(PushConstants.EXTRA_CR_PREF_SWITCH, false);
        long longExtra2 = intent.getLongExtra(PushConstants.EXTRA_CR_PREF_FREQUENCY, 86400L);
        boolean booleanExtra5 = intent.getBooleanExtra(PushConstants.EXTRA_CR_EVENT_ENCRYPTED, true);
        long longExtra3 = intent.getLongExtra(PushConstants.EXTRA_CR_MAX_FILE_SIZE, 1048576L);
        Config configBuild = Config.getBuilder().setEventUploadSwitchOpen(booleanExtra3).setEventUploadFrequency(longExtra).setPerfUploadSwitchOpen(booleanExtra4).setPerfUploadFrequency(longExtra2).setAESKey(ClientReportUtil.getEventKeyWithDefault(getApplicationContext())).setEventEncrypted(booleanExtra5).setMaxFileLength(longExtra3).build(getApplicationContext());
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName()) || longExtra <= 0 || longExtra2 <= 0 || longExtra3 <= 0) {
            return;
        }
        PushClientReportHelper.initEventPerfLogic(getApplicationContext(), configBuild);
    }

    private boolean isInFalldownTimeRange() {
        int iIntValue = Integer.valueOf(String.format("%tH", new Date())).intValue();
        int i2 = this.mFalldownStart;
        int i3 = this.mFalldownEnd;
        if (i2 > i3) {
            if (iIntValue >= i2 || iIntValue < i3) {
                return true;
            }
        } else if (i2 < i3 && iIntValue >= i2 && iIntValue < i3) {
            return true;
        }
        return false;
    }

    private boolean isPushEnabled() {
        boolean zEquals;
        String packageName = getPackageName();
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(packageName)) {
            MyLog.w("current sdk expect region is cn");
            zEquals = Region.China.name().equals(AppRegionStorage.getInstance(getApplicationContext()).getRegion());
        } else {
            zEquals = !MIPushAppInfo.getInstance(this).isPushDisabled(packageName);
        }
        boolean z2 = zEquals;
        if (!z2) {
            MyLog.w(TAG, "-->isPushEnabled(): isEnabled=", Boolean.valueOf(z2), ", package=", packageName, ", region=", AppRegionStorage.getInstance(getApplicationContext()).getRegion());
        }
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSuperPowerModeEnable() {
        return PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName()) && Settings.System.getInt(getContentResolver(), SUPER_POWER_MODE, 0) == 1;
    }

    public static boolean isUseDynamicBroadcast() {
        return sUseDynamicBroadcast;
    }

    private void networkChanged() {
        NetworkInfoWrapper networkInfo = Network.getNetworkInfo();
        HeartbeatHolder.getInstance(getApplicationContext()).onNetworkChanged(networkInfo);
        if (networkInfo != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("network changed,");
            sb.append("[type: " + networkInfo.getTypeName() + "[" + networkInfo.getSubtypeName() + "], state: " + networkInfo.getState() + "/" + networkInfo.getDetailedState());
            MyLog.w(TAG, sb.toString());
            NetworkInfo.State state = networkInfo.getState();
            if (state == NetworkInfo.State.SUSPENDED || state == NetworkInfo.State.UNKNOWN) {
                return;
            }
        } else {
            MyLog.w(TAG, "network changed, no active network");
        }
        if (ConnReportHandler.getContext() != null) {
            ConnReportHandler.getContext().statsChannelIfNeed();
        }
        TrafficUtils.notifyNetworkChanage(this);
        this.mSlimConnection.clearCachedStatus();
        if (Network.hasNetwork(this)) {
            if (isConnected() && shouldCheckAlive()) {
                checkAlive(false);
            }
            if (!isConnected() && !isConnecting()) {
                this.mJobController.removeJobs(1);
                executeJob(new ConnectJob());
            }
            LogUploader.getInstance(this).checkUpload();
        } else {
            executeJob(new DisconnectJob(2, null));
        }
        updateAlarmTimer();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:22:0x00a9  */
    /* JADX WARN: Code duplicated, block: B:25:0x00b7  */
    /* JADX WARN: Code duplicated, block: B:28:0x00d4 A[Catch: Exception -> 0x00da, TRY_LEAVE, TryCatch #0 {Exception -> 0x00da, blocks: (B:26:0x00ce, B:28:0x00d4), top: B:36:0x00ce }] */
    /* JADX WARN: Code duplicated, block: B:34:0x00e8  */
    /* JADX WARN: Code duplicated, block: B:38:? A[RETURN, SYNTHETIC] */
    public void postOnCreate() {
        String str;
        HostManager.getInstance().deleteOldHostFallbacksFile();
        HeartbeatHolder.getInstance(getApplicationContext()).init();
        AppRegionStorage appRegionStorage = AppRegionStorage.getInstance(getApplicationContext());
        String region = appRegionStorage.getRegion();
        MyLog.w(TAG, "region of cache is " + region);
        String strName = "";
        if (TextUtils.isEmpty(region)) {
            String strEnsureCountryCodeAvaible = ensureCountryCodeAvaible();
            str = strEnsureCountryCodeAvaible;
            region = MIUIUtils.getRegion(strEnsureCountryCodeAvaible).name();
        } else {
            str = "";
        }
        String str2 = "CN";
        try {
            if (TextUtils.isEmpty(region) || !Region.China.name().equals(region)) {
                if (TextUtils.isEmpty(region)) {
                    region = Region.China.name();
                } else {
                    if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName())) {
                        str2 = "";
                    } else {
                        strName = Region.China.name();
                    }
                    appRegionStorage.setRegion(strName, true);
                    appRegionStorage.setCountryCode(str2, true);
                    region = strName;
                }
                MyLog.w(TAG, "after check, appRegion is ", region, ", countryCode=", str);
                if (Region.China.name().equals(region)) {
                    ConnectionConfiguration.setXmppServerHost(ConnectionConfiguration.XMPP_SERVER_CHINA_HOST_P);
                }
                addReservedHost(region);
                if (isPushEnabled()) {
                    MyLog.w(TAG, "-->postOnCreate(): try trigger connect now");
                    final Job job = new Job(11) { // from class: com.xiaomi.push.service.XMPushService.8
                        @Override // com.xiaomi.push.service.XMPushService.Job
                        public String getDesc() {
                            return "prepare the mi push account.";
                        }

                        @Override // com.xiaomi.push.service.XMPushService.Job
                        public void process() {
                            MIPushHelper.prepareMIPushAccount(XMPushService.this);
                            if (Network.hasNetwork(XMPushService.this)) {
                                XMPushService.this.scheduleConnect(true);
                            }
                        }
                    };
                    executeJob(job);
                    MIPushAccountUtils.setAccountChangeListener(new MIPushAccountUtils.PushAccountChangeListener() { // from class: com.xiaomi.push.service.XMPushService.9
                        @Override // com.xiaomi.push.service.MIPushAccountUtils.PushAccountChangeListener
                        public void onChange() {
                            XMPushService.this.executeJob(job);
                        }
                    });
                }
                if (SystemUtils.isBootCompleted()) {
                    this.mClientEventDispatcher.notifyServiceStarted(this);
                }
                if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName())) {
                    AppInfoUtils.setNotificationsEnabledForPackage(this, getApplicationInfo(), true);
                }
            }
            appRegionStorage.setRegion(region, true);
            appRegionStorage.setCountryCode("CN", true);
            if (SystemUtils.isBootCompleted()) {
                this.mClientEventDispatcher.notifyServiceStarted(this);
            }
        } catch (Exception e2) {
            MyLog.e(e2);
        }
        str = str2;
        MyLog.w(TAG, "after check, appRegion is ", region, ", countryCode=", str);
        if (Region.China.name().equals(region)) {
            ConnectionConfiguration.setXmppServerHost(ConnectionConfiguration.XMPP_SERVER_CHINA_HOST_P);
        }
        addReservedHost(region);
        if (isPushEnabled()) {
            MyLog.w(TAG, "-->postOnCreate(): try trigger connect now");
            final Job job2 = new Job(11) { // from class: com.xiaomi.push.service.XMPushService.8
                @Override // com.xiaomi.push.service.XMPushService.Job
                public String getDesc() {
                    return "prepare the mi push account.";
                }

                @Override // com.xiaomi.push.service.XMPushService.Job
                public void process() {
                    MIPushHelper.prepareMIPushAccount(XMPushService.this);
                    if (Network.hasNetwork(XMPushService.this)) {
                        XMPushService.this.scheduleConnect(true);
                    }
                }
            };
            executeJob(job2);
            MIPushAccountUtils.setAccountChangeListener(new MIPushAccountUtils.PushAccountChangeListener() { // from class: com.xiaomi.push.service.XMPushService.9
                @Override // com.xiaomi.push.service.MIPushAccountUtils.PushAccountChangeListener
                public void onChange() {
                    XMPushService.this.executeJob(job2);
                }
            });
        }
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName())) {
            AppInfoUtils.setNotificationsEnabledForPackage(this, getApplicationInfo(), true);
        }
    }

    private Packet preparePacket(Packet packet, String str, String str2) {
        PushClientsManager pushClientsManager = PushClientsManager.getInstance();
        List<String> listQueryChannelIdByPackage = pushClientsManager.queryChannelIdByPackage(str);
        if (listQueryChannelIdByPackage.isEmpty()) {
            MyLog.w("open channel should be called first before sending a packet, pkg=" + str);
            return null;
        }
        packet.setPackageName(str);
        String channelId = packet.getChannelId();
        if (TextUtils.isEmpty(channelId)) {
            channelId = listQueryChannelIdByPackage.get(0);
            packet.setChannelId(channelId);
        }
        PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = pushClientsManager.getClientLoginInfoByChidAndUserId(channelId, packet.getFrom());
        if (!isConnected()) {
            MyLog.w("drop a packet as the channel is not connected, chid=" + channelId);
            return null;
        }
        if (clientLoginInfoByChidAndUserId == null || clientLoginInfoByChidAndUserId.status != PushClientsManager.ClientStatus.binded) {
            MyLog.w("drop a packet as the channel is not opened, chid=" + channelId);
            return null;
        }
        if (TextUtils.equals(str2, clientLoginInfoByChidAndUserId.session)) {
            return packet;
        }
        MyLog.w("invalid session. " + str2);
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:21:0x00b8  */
    private void sendMessage(Intent intent) {
        long j2;
        Blob blobFrom;
        String stringExtra = intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME);
        String stringExtra2 = intent.getStringExtra(PushConstants.EXTRA_SESSION);
        Bundle bundleExtra = intent.getBundleExtra(PushConstants.EXTRA_PACKET);
        PushClientsManager pushClientsManager = PushClientsManager.getInstance();
        if (bundleExtra != null) {
            Message message = (Message) preparePacket(new Message(bundleExtra), stringExtra, stringExtra2);
            if (message == null) {
                return;
            } else {
                blobFrom = Blob.from(message, pushClientsManager.getClientLoginInfoByChidAndUserId(message.getChannelId(), message.getFrom()).security);
            }
        } else {
            byte[] byteArrayExtra = intent.getByteArrayExtra(PushConstants.EXTRA_RAW_PACKET);
            if (byteArrayExtra != null) {
                try {
                    j2 = Long.parseLong(intent.getStringExtra(PushConstants.EXTRA_USER_ID));
                } catch (NumberFormatException unused) {
                    j2 = 0;
                }
                String stringExtra3 = intent.getStringExtra(PushConstants.EXTRA_USER_SERVER);
                String stringExtra4 = intent.getStringExtra(PushConstants.EXTRA_USER_RES);
                String stringExtra5 = intent.getStringExtra(PushConstants.EXTRA_CHID);
                PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = pushClientsManager.getClientLoginInfoByChidAndUserId(stringExtra5, String.valueOf(j2));
                if (clientLoginInfoByChidAndUserId != null) {
                    Blob blob = new Blob();
                    try {
                        blob.setChannelId(Integer.parseInt(stringExtra5));
                    } catch (NumberFormatException unused2) {
                    }
                    blob.setCmd(Blob.CMD_SECMSG, null);
                    if (TextUtils.isEmpty(stringExtra3)) {
                        stringExtra3 = "xiaomi.com";
                    }
                    blob.setFrom(j2, stringExtra3, stringExtra4);
                    blob.setPacketID(intent.getStringExtra(PushConstants.EXTRA_PACKET_ID));
                    blob.setPayload(byteArrayExtra, clientLoginInfoByChidAndUserId.security);
                    MyLog.w("send a message: chid=" + stringExtra5 + ", packetId=" + intent.getStringExtra(PushConstants.EXTRA_PACKET_ID));
                    blobFrom = blob;
                } else {
                    blobFrom = null;
                }
            } else {
                blobFrom = null;
            }
        }
        if (blobFrom != null) {
            executeJobNow(new SendMessageJob(this, blobFrom));
        }
    }

    private void sendMessages(Intent intent) {
        String stringExtra = intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME);
        String stringExtra2 = intent.getStringExtra(PushConstants.EXTRA_SESSION);
        Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra(PushConstants.EXTRA_PACKETS);
        int length = parcelableArrayExtra.length;
        Message[] messageArr = new Message[length];
        intent.getBooleanExtra(PushConstants.EXTRA_ENCYPT, true);
        for (int i2 = 0; i2 < parcelableArrayExtra.length; i2++) {
            Message message = new Message((Bundle) parcelableArrayExtra[i2]);
            messageArr[i2] = message;
            Message message2 = (Message) preparePacket(message, stringExtra, stringExtra2);
            messageArr[i2] = message2;
            if (message2 == null) {
                return;
            }
        }
        PushClientsManager pushClientsManager = PushClientsManager.getInstance();
        Blob[] blobArr = new Blob[length];
        for (int i3 = 0; i3 < length; i3++) {
            Message message3 = messageArr[i3];
            blobArr[i3] = Blob.from(message3, pushClientsManager.getClientLoginInfoByChidAndUserId(message3.getChannelId(), message3.getFrom()).security);
        }
        executeJobNow(new BatchSendMessageJob(this, blobArr));
    }

    private boolean shouldCheckAlive() {
        if (SystemClock.elapsedRealtime() - this.lastAlive < MessageInfoContract.TIMEOUT) {
            return false;
        }
        return Network.isConnected(this);
    }

    private boolean shouldFalldown() {
        return getApplicationContext().getPackageName().equals(PushConstants.PUSH_SERVICE_PACKAGE_NAME) && isInFalldownTimeRange() && !DeviceInfo.isScreenOn(this) && !DeviceInfo.isCharging(getApplicationContext());
    }

    private boolean shouldRebind(String str, Intent intent) {
        PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(str, intent.getStringExtra(PushConstants.EXTRA_USER_ID));
        boolean z2 = false;
        if (clientLoginInfoByChidAndUserId == null || str == null) {
            return false;
        }
        String stringExtra = intent.getStringExtra(PushConstants.EXTRA_SESSION);
        String stringExtra2 = intent.getStringExtra(PushConstants.EXTRA_SECURITY);
        if (!TextUtils.isEmpty(clientLoginInfoByChidAndUserId.session) && !TextUtils.equals(stringExtra, clientLoginInfoByChidAndUserId.session)) {
            MyLog.w("session changed. old session=" + clientLoginInfoByChidAndUserId.session + ", new session=" + stringExtra + " chid = " + str);
            z2 = true;
        }
        if (stringExtra2.equals(clientLoginInfoByChidAndUserId.security)) {
            return z2;
        }
        MyLog.w("security changed. chid = " + str + " sechash = " + MD5.MD5_32(stringExtra2));
        return true;
    }

    private void unregisterReceiverSafely(BroadcastReceiver broadcastReceiver) {
        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (IllegalArgumentException e2) {
                MyLog.e(e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarmTimer() {
        if (!shouldReconnect()) {
            Alarm.stop();
        } else {
            if (Alarm.isAlive()) {
                return;
            }
            Alarm.registerPing(true);
        }
    }

    private PushClientsManager.ClientLoginInfo updatePushClient(String str, Intent intent) {
        PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(str, intent.getStringExtra(PushConstants.EXTRA_USER_ID));
        if (clientLoginInfoByChidAndUserId == null) {
            clientLoginInfoByChidAndUserId = new PushClientsManager.ClientLoginInfo(this);
        }
        clientLoginInfoByChidAndUserId.chid = intent.getStringExtra(PushConstants.EXTRA_CHANNEL_ID);
        clientLoginInfoByChidAndUserId.userId = intent.getStringExtra(PushConstants.EXTRA_USER_ID);
        clientLoginInfoByChidAndUserId.token = intent.getStringExtra(PushConstants.EXTRA_TOKEN);
        clientLoginInfoByChidAndUserId.pkgName = intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME);
        clientLoginInfoByChidAndUserId.clientExtra = intent.getStringExtra(PushConstants.EXTRA_CLIENT_ATTR);
        clientLoginInfoByChidAndUserId.cloudExtra = intent.getStringExtra(PushConstants.EXTRA_CLOUD_ATTR);
        clientLoginInfoByChidAndUserId.kick = intent.getBooleanExtra(PushConstants.EXTRA_KICK, false);
        clientLoginInfoByChidAndUserId.security = intent.getStringExtra(PushConstants.EXTRA_SECURITY);
        clientLoginInfoByChidAndUserId.session = intent.getStringExtra(PushConstants.EXTRA_SESSION);
        clientLoginInfoByChidAndUserId.authMethod = intent.getStringExtra(PushConstants.EXTRA_AUTH_METHOD);
        clientLoginInfoByChidAndUserId.mClientEventDispatcher = this.mClientEventDispatcher;
        clientLoginInfoByChidAndUserId.watch((Messenger) intent.getParcelableExtra(PushConstants.EXTRA_MESSENGER));
        clientLoginInfoByChidAndUserId.context = getApplicationContext();
        PushClientsManager.getInstance().addActiveClient(clientLoginInfoByChidAndUserId);
        return clientLoginInfoByChidAndUserId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean waitForNetValidated(Context context) {
        try {
            ThreadUtils.checkNotUIThread();
            for (int i2 = 100; i2 > 0; i2--) {
                if (Network.isConnectivity(context)) {
                    MyLog.w("network connectivity ok.");
                    return true;
                }
                try {
                    Thread.sleep(100L);
                } catch (Exception unused) {
                }
            }
            return false;
        } catch (Exception unused2) {
            return true;
        }
    }

    private void wifiChanged(Intent intent) {
        Bundle extras;
        if (intent == null || (extras = intent.getExtras()) == null) {
            return;
        }
        String string = extras.getString(DIGEST_EXTRA_KEY);
        HeartbeatHolder.getInstance(getApplicationContext()).onWifiChanged(string);
        DisconnectStatsHelper.onWifiChanged(this, string);
    }

    public void addPingCallBack(PingCallBack pingCallBack) {
        synchronized (this.pingCallBacks) {
            this.pingCallBacks.add(pingCallBack);
        }
    }

    public void batchSendPacket(Blob[] blobArr) throws XMPPException {
        Connection connection = this.mCurrentConnection;
        if (connection == null) {
            throw new XMPPException("try send msg while connection is null.");
        }
        connection.batchSend(blobArr);
    }

    public void closeChannel(String str, String str2, int i2, String str3, String str4) {
        PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(str, str2);
        if (clientLoginInfoByChidAndUserId != null) {
            executeJob(new UnbindJob(clientLoginInfoByChidAndUserId, i2, str4, str3));
        }
        PushClientsManager.getInstance().deactivateClient(str, str2);
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void connectionClosed(Connection connection, int i2, Exception exc) {
        ConnReportHandler.getContext().connectionClosed(connection, i2, exc);
        if (shouldFalldown()) {
            return;
        }
        scheduleConnect(false);
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void connectionStarted(Connection connection) {
        MyLog.v("begin to connect...");
        ConnReportHandler.getContext().connectionStarted(connection);
    }

    public ClientEventDispatcher createClientEventDispatcher() {
        return new ClientEventDispatcher();
    }

    public void disconnect(int i2, Exception exc) {
        StringBuilder sb = new StringBuilder();
        sb.append("disconnect ");
        sb.append(hashCode());
        sb.append(", ");
        Connection connection = this.mCurrentConnection;
        sb.append(connection == null ? null : Integer.valueOf(connection.hashCode()));
        MyLog.w(sb.toString());
        Connection connection2 = this.mCurrentConnection;
        if (connection2 != null) {
            connection2.disconnect(i2, exc);
            this.mCurrentConnection = null;
        }
        removeJobs(7);
        removeJobs(4);
        PushClientsManager.getInstance().resetAllClients(this, i2);
    }

    public void executeJob(Job job) {
        executeJobDelayed(job, 0L);
    }

    public void executeJobDelayed(Job job, long j2) {
        try {
            this.mJobController.executeJobDelayed(job, j2);
        } catch (IllegalStateException e2) {
            MyLog.w("can't execute job err = " + e2.getMessage());
        }
    }

    public ClientEventDispatcher getClientEventDispatcher() {
        return this.mClientEventDispatcher;
    }

    public Connection getCurrentConnection() {
        return this.mCurrentConnection;
    }

    public int getXmsfVersionCode() {
        if (this.sXmsfVersionCode < 0) {
            this.sXmsfVersionCode = AppInfoUtils.getVersionCode(this, PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        }
        return this.sXmsfVersionCode;
    }

    public boolean hasJob(int i2) {
        return this.mJobController.hasJob(i2);
    }

    public boolean isConnectAllowed() {
        return shouldReconnect();
    }

    public boolean isConnected() {
        Connection connection = this.mCurrentConnection;
        return connection != null && connection.isConnected();
    }

    public boolean isConnecting() {
        Connection connection = this.mCurrentConnection;
        return connection != null && connection.isConnecting();
    }

    public boolean isPushDisabled() {
        try {
            Class<?> clsLoadClass = SystemUtils.loadClass(this, "miui.os.Build");
            return clsLoadClass.getField("IS_CM_CUSTOMIZATION_TEST").getBoolean(null) || clsLoadClass.getField("IS_CU_CUSTOMIZATION_TEST").getBoolean(null) || clsLoadClass.getField("IS_CT_CUSTOMIZATION_TEST").getBoolean(null);
        } catch (Throwable unused) {
            return false;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.messenger.getBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        String[] strArrSplit;
        super.onCreate();
        MyLog.init(getApplicationContext());
        SystemUtils.initialize(this);
        MIPushAccount mIPushAccount = MIPushAccountUtils.getMIPushAccount(this);
        if (mIPushAccount != null) {
            BuildSettings.setEnvType(mIPushAccount.envType);
        }
        if (MIUIUtils.isXMSF(getApplicationContext())) {
            HandlerThread handlerThread = new HandlerThread("hb-alarm");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper());
            this.mAlarmReceiver = new AlarmReceiver();
            ReceiverHelper.registerReceiver(this, this.mAlarmReceiver, new IntentFilter(PushConstants.ACTION_PING_TIMER), "com.xiaomi.xmsf.permission.MIPUSH_RECEIVE", handler, 4);
            sUseDynamicBroadcast = true;
            handler.post(new Runnable() { // from class: com.xiaomi.push.service.XMPushService.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        PackageManager packageManager = XMPushService.this.getApplicationContext().getPackageManager();
                        ComponentName componentName = new ComponentName(XMPushService.this.getApplicationContext(), PushConstants.PING_RECEIVER_CLASS_NAME_JAR);
                        if (packageManager.getComponentEnabledSetting(componentName) != 2) {
                            packageManager.setComponentEnabledSetting(componentName, 2, 1);
                        }
                    } catch (Throwable th) {
                        MyLog.w("[Alarm] disable ping receiver may be failure. " + th);
                    }
                }
            });
        }
        this.messenger = new Messenger(new Handler() { // from class: com.xiaomi.push.service.XMPushService.3
            @Override // android.os.Handler
            public void handleMessage(android.os.Message message) {
                super.handleMessage(message);
                if (message != null) {
                    try {
                        int i2 = message.what;
                        if (i2 == 17) {
                            Object obj = message.obj;
                            if (obj != null) {
                                XMPushService.this.onStart((Intent) obj, 1);
                            }
                        } else if (i2 == 18) {
                            android.os.Message messageObtain = android.os.Message.obtain((Handler) null, 0);
                            messageObtain.what = 18;
                            Bundle bundle = new Bundle();
                            bundle.putString(PushConstants.MESSAGE_KEY_XMSF_REGION, AppRegionStorage.getInstance(XMPushService.this.getApplicationContext()).getRegion());
                            messageObtain.setData(bundle);
                            message.replyTo.send(messageObtain);
                        }
                    } catch (Throwable unused) {
                    }
                }
            }
        });
        PushHostManagerFactory.init(this);
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(null, PushServiceConstants.XMPP_SERVER_PORT, "xiaomi.com", null) { // from class: com.xiaomi.push.service.XMPushService.4
            @Override // com.xiaomi.smack.ConnectionConfiguration
            public byte[] getConnectionBlob() {
                try {
                    ChannelMessage.PushServiceConfigMsg pushServiceConfigMsg = new ChannelMessage.PushServiceConfigMsg();
                    pushServiceConfigMsg.setClientVersion(ServiceConfig.getInstance().getConfigVersion());
                    return pushServiceConfigMsg.toByteArray();
                } catch (Exception e2) {
                    MyLog.w("getOBBString err: " + e2.toString());
                    return null;
                }
            }
        };
        this.connConfig = connectionConfiguration;
        connectionConfiguration.setDebuggerEnabled(true);
        this.mSlimConnection = new SlimConnection(this, this.connConfig);
        this.mClientEventDispatcher = createClientEventDispatcher();
        Alarm.initialize(this);
        this.mSlimConnection.addConnectionListener(this);
        this.mPacketSync = new PacketSync(this);
        this.mReconnManager = new ReconnectionManager(this);
        new CommonPacketExtensionProvider().register();
        ConnReportHandler.getInstance().init(this);
        this.mJobController = new JobScheduler("Connection Controller Thread");
        PushClientsManager pushClientsManager = PushClientsManager.getInstance();
        pushClientsManager.removeAllClientChangeListeners();
        pushClientsManager.addClientChangeListener(new PushClientsManager.ClientChangeListener() { // from class: com.xiaomi.push.service.XMPushService.5
            @Override // com.xiaomi.push.service.PushClientsManager.ClientChangeListener
            public void onChange() {
                XMPushService.this.updateAlarmTimer();
                if (PushClientsManager.getInstance().getActiveClientCount() <= 0) {
                    XMPushService xMPushService = XMPushService.this;
                    xMPushService.executeJob(xMPushService.new DisconnectJob(12, null));
                }
            }
        });
        if (canOpenForegroundService()) {
            enableForegroundService();
        }
        TinyDataManager.getInstance(this).addUploader(new LongConnUploader(this), TinyDataManager.UPLOADER_PUSH_CHANNEL);
        addPingCallBack(new TinyDataCacheProcessor(this));
        addPingCallBack(new UploadNotificationDatas(this));
        if (MIUIUtils.isXMSF(this)) {
            addPingCallBack(new PingPongCallbackAgent());
            if (DeviceInfo.isMainSpace()) {
                addPingCallBack(new PingCallBack() { // from class: com.xiaomi.push.service.XMPushService.6
                    @Override // com.xiaomi.push.service.XMPushService.PingCallBack
                    public void pingFollowUpAction() {
                        RegisterAppChannelInfoCollectionJob.uploadAppChannelInfo(XMPushService.this.getApplicationContext());
                    }
                });
            }
        }
        executeJob(new InitJob());
        this.networkListeners.add(Sync.getInstance(this));
        if (isPushEnabled()) {
            this.mConnectionChangeReceiver = new ConnectionChangeReceiver();
            ReceiverHelper.registerReceiver(this, this.mConnectionChangeReceiver, new IntentFilter(CONNECTIVITY_ACTION), (String) null, (Handler) null);
            this.mNetworkCallback = Network.addNetworkCallBack(this);
        }
        if (MIUIUtils.isXMSF(getApplicationContext())) {
            this.mWifiChangeReceiver = new WifiChangeReceiver();
            ReceiverHelper.registerReceiver(this, this.mWifiChangeReceiver, new IntentFilter(WIFI_DIGEST_ACTION), REQUIRED_WIFI_DIGEST_PERMISSION, null, 2);
            KeepShortHbReceiver keepShortHbReceiver = new KeepShortHbReceiver();
            this.mKeepShortHbReceiver = keepShortHbReceiver;
            ReceiverHelper.registerReceiver(this, keepShortHbReceiver, new IntentFilter(KEEP_SHORT_HB_ACTION), KEEP_SHORT_HB_PERMISSION, null, 2);
        }
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName())) {
            Uri uriFor = Settings.System.getUriFor(SUPER_POWER_MODE);
            if (uriFor != null) {
                this.mSuperPowerModeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.xiaomi.push.service.XMPushService.7
                    @Override // android.database.ContentObserver
                    public void onChange(boolean z2) {
                        super.onChange(z2);
                        boolean zIsSuperPowerModeEnable = XMPushService.this.isSuperPowerModeEnable();
                        MyLog.w("SuperPowerMode:" + zIsSuperPowerModeEnable);
                        XMPushService.this.updateAlarmTimer();
                        if (!zIsSuperPowerModeEnable) {
                            XMPushService.this.scheduleConnect(true);
                        } else {
                            XMPushService xMPushService = XMPushService.this;
                            xMPushService.executeJob(xMPushService.new DisconnectJob(24, null));
                        }
                    }
                };
                try {
                    getContentResolver().registerContentObserver(uriFor, false, this.mSuperPowerModeObserver);
                } catch (Throwable th) {
                    MyLog.e("register super-power-mode observer err:" + th.getMessage());
                }
            }
            int[] falldownTimeRange = getFalldownTimeRange();
            if (falldownTimeRange != null) {
                this.mScreenStateReceiver = new ScreenStateReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.SCREEN_ON");
                intentFilter.addAction("android.intent.action.SCREEN_OFF");
                ReceiverHelper.registerReceiver(this, this.mScreenStateReceiver, intentFilter, (String) null, (Handler) null);
                this.mFalldownStart = falldownTimeRange[0];
                this.mFalldownEnd = falldownTimeRange[1];
                MyLog.w("falldown initialized: " + this.mFalldownStart + "," + this.mFalldownEnd);
            }
        }
        DisconnectStatsHelper.startStats(this, this.mSlimConnection);
        PowerStatsHelper.startStats(this, this.mSlimConnection);
        String str = "";
        if (mIPushAccount != null) {
            try {
                if (!TextUtils.isEmpty(mIPushAccount.account) && (strArrSplit = mIPushAccount.account.split("@")) != null && strArrSplit.length > 0) {
                    str = strArrSplit[0];
                }
            } catch (Exception unused) {
            }
        }
        SdkCrashManager.listenCrash(this);
        MyLog.persist("XMPushService created. pid=" + Process.myPid() + ", uid=" + Process.myUid() + ", vc=" + AppInfoUtils.getVersionCode(getApplicationContext(), getPackageName()) + ", uuid=" + str);
    }

    @Override // android.app.Service
    public void onDestroy() {
        ConnectionChangeReceiver connectionChangeReceiver = this.mConnectionChangeReceiver;
        if (connectionChangeReceiver != null) {
            unregisterReceiverSafely(connectionChangeReceiver);
            this.mConnectionChangeReceiver = null;
        }
        Object obj = this.mNetworkCallback;
        if (obj != null) {
            Network.removeCallback(this, obj);
            this.mNetworkCallback = null;
        }
        WifiChangeReceiver wifiChangeReceiver = this.mWifiChangeReceiver;
        if (wifiChangeReceiver != null) {
            unregisterReceiverSafely(wifiChangeReceiver);
            this.mWifiChangeReceiver = null;
        }
        KeepShortHbReceiver keepShortHbReceiver = this.mKeepShortHbReceiver;
        if (keepShortHbReceiver != null) {
            unregisterReceiverSafely(keepShortHbReceiver);
            this.mKeepShortHbReceiver = null;
        }
        ScreenStateReceiver screenStateReceiver = this.mScreenStateReceiver;
        if (screenStateReceiver != null) {
            unregisterReceiverSafely(screenStateReceiver);
            this.mScreenStateReceiver = null;
        }
        AlarmReceiver alarmReceiver = this.mAlarmReceiver;
        if (alarmReceiver != null) {
            unregisterReceiverSafely(alarmReceiver);
            this.mAlarmReceiver = null;
        }
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(getPackageName()) && this.mSuperPowerModeObserver != null) {
            try {
                getContentResolver().unregisterContentObserver(this.mSuperPowerModeObserver);
            } catch (Throwable th) {
                MyLog.e("unregister super-power-mode err:" + th.getMessage());
            }
        }
        this.networkListeners.clear();
        this.mJobController.removeAllJobs();
        executeJob(new Job(2) { // from class: com.xiaomi.push.service.XMPushService.12
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "disconnect for service destroy.";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                if (XMPushService.this.mCurrentConnection != null) {
                    XMPushService.this.mCurrentConnection.disconnect(15, null);
                    XMPushService.this.mCurrentConnection = null;
                }
            }
        });
        executeJob(new KillJob());
        PushClientsManager.getInstance().removeAllClientChangeListeners();
        PushClientsManager.getInstance().resetAllClients(this, 15);
        PushClientsManager.getInstance().removeActiveClients();
        this.mSlimConnection.removeConnectionListener(this);
        ServiceConfig.getInstance().clear();
        Alarm.stop();
        clearPingCallbacks();
        DisconnectStatsHelper.stopStats(this, this.mSlimConnection);
        PowerStatsHelper.stopStats(this, this.mSlimConnection);
        super.onDestroy();
        MyLog.w("Service destroyed");
    }

    void onPong() {
        HeartbeatHolder.getInstance(getApplicationContext()).onPong();
        Iterator it = new ArrayList(this.pingCallBacks).iterator();
        while (it.hasNext()) {
            ((PingCallBack) it.next()).pingFollowUpAction();
        }
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int i2) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (intent == null) {
            MyLog.e("onStart() with intent NULL");
        } else {
            try {
                String stringExtra = intent.getStringExtra(PushConstants.EXTRA_CHANNEL_ID);
                String stringExtra2 = intent.getStringExtra(PushConstants.EXTRA_PACKAGE_NAME);
                String stringExtra3 = intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE);
                if (CONNECTIVITY_ACTION.equals(intent.getAction()) || WIFI_DIGEST_ACTION.equals(intent.getAction())) {
                    MyLog.w(TAG, String.format("onStart() with intent.Action = %s, chid = %s, pkg = %s|%s, intent = %s", intent.getAction(), stringExtra, stringExtra2, stringExtra3, MIUIUtils.intentToString(intent)));
                } else {
                    MyLog.w(TAG, String.format("onStart() with intent.Action = %s, chid = %s, pkg = %s|%s", intent.getAction(), stringExtra, stringExtra2, stringExtra3));
                }
            } catch (Throwable th) {
                MyLog.e("onStart() cause error: " + th.getMessage());
                return;
            }
        }
        if (intent != null && intent.getAction() != null) {
            if (PushServiceConstants.ACTION_TIMER.equalsIgnoreCase(intent.getAction()) || PushServiceConstants.ACTION_CHECK_ALIVE.equalsIgnoreCase(intent.getAction())) {
                if (this.mJobController.isBlocked()) {
                    MyLog.e("ERROR, the job controller is blocked.");
                    PushClientsManager.getInstance().resetAllClients(this, 14);
                    stopSelf();
                } else {
                    executeJob(new IntentJob(intent));
                }
            } else if (!PushServiceConstants.ACTION_NETWORK_STATUS_CHANGED.equalsIgnoreCase(intent.getAction())) {
                executeJob(new IntentJob(intent));
            }
        }
        long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
        if (jCurrentTimeMillis2 > 50) {
            MyLog.v("[Prefs] spend " + jCurrentTimeMillis2 + " ms, too more times.");
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        onStart(intent, i3);
        return 1;
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void reconnectionFailed(Connection connection, Exception exc) {
        ConnReportHandler.getContext().reconnectionFailed(connection, exc);
        broadcastNetworkAvailable(false);
        if (shouldFalldown()) {
            return;
        }
        scheduleConnect(false);
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void reconnectionSuccessful(Connection connection) {
        ConnReportHandler.getContext().reconnectionSuccessful(connection);
        broadcastNetworkAvailable(true);
        this.mReconnManager.onConnectSucceeded();
        if (!Alarm.isAlive() && !shouldFalldown()) {
            MyLog.w("reconnection successful, reactivate alarm.");
            Alarm.registerPing(true);
        }
        Iterator<PushClientsManager.ClientLoginInfo> it = PushClientsManager.getInstance().getAllClients().iterator();
        while (it.hasNext()) {
            executeJob(new BindJob(it.next()));
        }
        if (this.mTriggerWifiBroadcast || !MIUIUtils.isXMSF(getApplicationContext())) {
            return;
        }
        ScheduledJobManager.getInstance(getApplicationContext()).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.service.XMPushService.15
            @Override // java.lang.Runnable
            public void run() {
                XMPushService.this.mTriggerWifiBroadcast = true;
                try {
                    MyLog.w("try to trigger the wifi digest broadcast.");
                    Object systemService = XMPushService.this.getApplicationContext().getSystemService("MiuiWifiService");
                    if (systemService != null) {
                        JavaCalls.callMethodOrThrow(systemService, "sendCurrentWifiDigestInfo", new Object[0]);
                    }
                } catch (Throwable unused) {
                }
            }
        });
    }

    public void registerForMiPushApp(byte[] bArr, String str) {
        if (bArr == null) {
            MIPushClientManager.notifyError(this, str, bArr, 70000003, "null payload");
            MyLog.w("register request without payload");
            return;
        }
        XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
        try {
            XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer, bArr);
            if (xmPushActionContainer.action == ActionType.Registration) {
                XmPushActionRegistration xmPushActionRegistration = new XmPushActionRegistration();
                try {
                    XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionRegistration, xmPushActionContainer.getPushAction());
                    executeJob(new MIPushAppRegisterJob(this, xmPushActionContainer.getPackageName(), xmPushActionRegistration.getAppId(), xmPushActionRegistration.getToken(), bArr));
                    PushClientReportManager.getInstance(getApplicationContext()).reportEvent(xmPushActionContainer.getPackageName(), ReportConstants.REGISTER_EVENT_CHAIN_INTERFACE_ID, xmPushActionRegistration.getId(), ReportConstants.REGISTER_TYPE_SEND_TO_SERVER, null);
                } catch (TException e2) {
                    MyLog.e("app register error. " + e2);
                    MIPushClientManager.notifyError(this, str, bArr, 70000003, " data action error.");
                }
            } else {
                MIPushClientManager.notifyError(this, str, bArr, 70000003, " registration action required.");
                MyLog.w("register request with invalid payload");
            }
        } catch (TException e3) {
            MyLog.e("app register fail. " + e3);
            MIPushClientManager.notifyError(this, str, bArr, 70000003, " data container error.");
        }
    }

    public void removeJobs(int i2) {
        this.mJobController.removeJobs(i2);
    }

    public void removePingCallBack(PingCallBack pingCallBack) {
        synchronized (this.pingCallBacks) {
            this.pingCallBacks.remove(pingCallBack);
        }
    }

    public void scheduleConnect(boolean z2) {
        this.mReconnManager.tryReconnect(z2);
    }

    public void scheduleRebindChannel(PushClientsManager.ClientLoginInfo clientLoginInfo) {
        if (clientLoginInfo != null) {
            long nextRetryInterval = clientLoginInfo.getNextRetryInterval();
            MyLog.w("schedule rebind job in " + (nextRetryInterval / 1000));
            executeJobDelayed(new BindJob(clientLoginInfo), nextRetryInterval);
        }
    }

    public void sendPacket(Blob blob) throws XMPPException {
        Connection connection = this.mCurrentConnection;
        if (connection == null) {
            throw new XMPPException("try send msg while connection is null.");
        }
        connection.send(blob);
    }

    void sendPongIfNeed() {
        if (SystemClock.elapsedRealtime() - this.lastAlive >= SmackConfiguration.getCheckAliveInterval() && Network.isConnected(this)) {
            checkAlive(true);
        }
    }

    public void setConnectingTimeout() {
        executeJobDelayed(new Job(10) { // from class: com.xiaomi.push.service.XMPushService.14
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "disconnect because of connecting timeout";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                if (XMPushService.this.isConnecting()) {
                    XMPushService.this.disconnect(18, null);
                }
            }
        }, 15000L);
    }

    public boolean shouldReconnect() {
        boolean zHasNetwork = Network.hasNetwork(this);
        boolean z2 = false;
        boolean z3 = PushClientsManager.getInstance().getActiveClientCount() > 0;
        boolean z4 = !isPushDisabled();
        boolean zIsPushEnabled = isPushEnabled();
        boolean z5 = !isSuperPowerModeEnable();
        if (zHasNetwork && z3 && z4 && zIsPushEnabled && z5) {
            z2 = true;
        }
        if (!z2) {
            MyLog.persist(String.format("not conn, net=%s;cnt=%s;!dis=%s;enb=%s;!spm=%s;", Boolean.valueOf(zHasNetwork), Boolean.valueOf(z3), Boolean.valueOf(z4), Boolean.valueOf(zIsPushEnabled), Boolean.valueOf(z5)));
        }
        return z2;
    }

    class PingJob extends Job {
        boolean isPong;

        public PingJob() {
            super(4);
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "send ping..";
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            if (XMPushService.this.isConnected()) {
                try {
                    if (!this.isPong) {
                        ConnReportHelper.pingStarted();
                    }
                    XMPushService.this.mCurrentConnection.ping(this.isPong);
                } catch (XMPPException e2) {
                    MyLog.e(e2);
                    XMPushService.this.disconnect(10, e2);
                }
            }
        }

        public PingJob(boolean z2) {
            super(4);
            this.isPong = z2;
        }
    }

    public boolean hasJob(Job job) {
        return this.mJobController.hasJob(job.type, job);
    }

    public void removeJobs(Job job) {
        this.mJobController.removeJobs(job.type, job);
    }

    public void batchSendPacket(Packet[] packetArr) throws XMPPException {
        Connection connection = this.mCurrentConnection;
        if (connection != null) {
            connection.batchSendPacket(packetArr);
            return;
        }
        throw new XMPPException("try send msg while connection is null.");
    }

    public void sendPacket(Packet packet) throws XMPPException {
        Connection connection = this.mCurrentConnection;
        if (connection != null) {
            connection.sendPacket(packet);
            return;
        }
        throw new XMPPException("try send msg while connection is null.");
    }

    void sendMessage(final String str, final byte[] bArr, boolean z2) {
        Collection<PushClientsManager.ClientLoginInfo> allClientLoginInfoByChid = PushClientsManager.getInstance().getAllClientLoginInfoByChid("5");
        if (allClientLoginInfoByChid.isEmpty()) {
            if (z2) {
                MIPushClientManager.addPendingMessages(str, bArr);
            }
        } else if (allClientLoginInfoByChid.iterator().next().status == PushClientsManager.ClientStatus.binded) {
            executeJob(new Job(4) { // from class: com.xiaomi.push.service.XMPushService.11
                @Override // com.xiaomi.push.service.XMPushService.Job
                public String getDesc() {
                    return "send mi push message";
                }

                @Override // com.xiaomi.push.service.XMPushService.Job
                public void process() {
                    try {
                        MIPushHelper.sendPacket(XMPushService.this, str, bArr);
                    } catch (XMPPException e2) {
                        MyLog.e(e2);
                        XMPushService.this.disconnect(10, e2);
                    }
                }
            });
        } else if (z2) {
            MIPushClientManager.addPendingMessages(str, bArr);
        }
    }

    private void enableForegroundService() {
    }
}
