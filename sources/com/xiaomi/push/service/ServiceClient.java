package com.xiaomi.push.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.BuildSettings;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.push.service.profile.MessageProfiling;
import com.xiaomi.smack.packet.CommonPacketExtension;
import com.xiaomi.smack.packet.IQ;
import com.xiaomi.smack.packet.Presence;
import com.xiaomi.smack.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;

/* JADX INFO: loaded from: classes.dex */
public class ServiceClient {
    private static final int MAX_PENDING_MESSAGES_SIZE = 50;
    private static final int MIN_MIUI_PUSH_VERSION = 104;
    private static final int MIN_MIUI_PUSH_VERSION_JAR = 106;
    private static ServiceClient sInstance;
    private static String sSession;
    private Messenger mClientMessenger;
    private Context mContext;
    private boolean mIsMiuiPushServiceEnabled;
    private static String prefix = StringUtils.randomString(5) + Constants.ACCEPT_TIME_SEPARATOR_SERVER;
    private static long sPacketCount = 0;
    private Messenger mMessenger = null;
    private List<Message> pendingMessages = new ArrayList();
    private boolean isConnectingService = false;

    private ServiceClient(Context context) {
        this.mIsMiuiPushServiceEnabled = false;
        this.mContext = context.getApplicationContext();
        if (serviceInstalled()) {
            MyLog.v("use miui push service");
            this.mIsMiuiPushServiceEnabled = true;
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
                this.mContext.bindService(intent, new ServiceConnection() { // from class: com.xiaomi.push.service.ServiceClient.1
                    @Override // android.content.ServiceConnection
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        synchronized (ServiceClient.this) {
                            ServiceClient.this.mClientMessenger = new Messenger(iBinder);
                            ServiceClient.this.isConnectingService = false;
                            Iterator it = ServiceClient.this.pendingMessages.iterator();
                            while (it.hasNext()) {
                                try {
                                    ServiceClient.this.mClientMessenger.send((Message) it.next());
                                } catch (RemoteException e2) {
                                    MyLog.e(e2);
                                }
                            }
                            ServiceClient.this.pendingMessages.clear();
                        }
                    }

                    @Override // android.content.ServiceConnection
                    public void onServiceDisconnected(ComponentName componentName) {
                        ServiceClient.this.mClientMessenger = null;
                        ServiceClient.this.isConnectingService = false;
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

    private Intent createServiceIntent() {
        if (!isMiuiPushServiceEnabled()) {
            Intent intent = new Intent(this.mContext, (Class<?>) XMPushService.class);
            intent.putExtra(PushConstants.EXTRA_PACKAGE_NAME, this.mContext.getPackageName());
            enableMyPushService();
            return intent;
        }
        Intent intent2 = new Intent();
        intent2.setPackage(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        intent2.setClassName(PushConstants.PUSH_SERVICE_PACKAGE_NAME, getPushServiceName());
        intent2.putExtra(PushConstants.EXTRA_PACKAGE_NAME, this.mContext.getPackageName());
        disableMyPushService();
        return intent2;
    }

    private void disableMyPushService() {
        this.mContext.getPackageManager().setComponentEnabledSetting(new ComponentName(this.mContext, (Class<?>) XMPushService.class), 2, 1);
    }

    private void enableMyPushService() {
        this.mContext.getPackageManager().setComponentEnabledSetting(new ComponentName(this.mContext, (Class<?>) XMPushService.class), 1, 1);
    }

    public static ServiceClient getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ServiceClient(context);
        }
        return sInstance;
    }

    private String getPushServiceName() {
        try {
            return this.mContext.getPackageManager().getPackageInfo(PushConstants.PUSH_SERVICE_PACKAGE_NAME, 4).versionCode >= 106 ? PushConstants.PUSH_SERVICE_CLASS_NAME_JAR : PushConstants.PUSH_SERVICE_CLASS_NAME;
        } catch (Exception unused) {
            return PushConstants.PUSH_SERVICE_CLASS_NAME;
        }
    }

    public static String getSession() {
        return sSession;
    }

    @Deprecated
    private String joinAttributes(List<NameValuePair> list) {
        return joinAttributes(translate(list));
    }

    private Message parseToMessage(Intent intent) {
        Message messageObtain = Message.obtain();
        messageObtain.what = 17;
        messageObtain.obj = intent;
        return messageObtain;
    }

    @Deprecated
    private void putOpenParamsIntoIntent(Intent intent, String str, String str2, String str3, String str4, String str5, boolean z2, List<NameValuePair> list, List<NameValuePair> list2) {
        putOpenParamsIntoIntent(intent, str, str2, str3, str4, str5, z2, translate(list), translate(list2));
    }

    private boolean serviceInstalled() {
        if (BuildSettings.IsTestBuild) {
            return false;
        }
        try {
            PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(PushConstants.PUSH_SERVICE_PACKAGE_NAME, 4);
            return packageInfo != null && packageInfo.versionCode >= 104;
        } catch (Exception unused) {
            return false;
        }
    }

    public static void setSession(String str) {
        sSession = str;
    }

    private Map<String, String> translate(List<NameValuePair> list) {
        HashMap map = new HashMap();
        if (list != null && list.size() > 0) {
            for (NameValuePair nameValuePair : list) {
                if (nameValuePair != null) {
                    map.put(nameValuePair.getName(), nameValuePair.getValue());
                }
            }
        }
        return map;
    }

    public boolean batchSendMessage(com.xiaomi.smack.packet.Message[] messageArr, boolean z2) {
        if (!Network.hasNetwork(this.mContext)) {
            return false;
        }
        Intent intentCreateServiceIntent = createServiceIntent();
        int length = messageArr.length;
        Bundle[] bundleArr = new Bundle[length];
        for (int i2 = 0; i2 < messageArr.length; i2++) {
            String prefString = MessageProfiling.getPrefString();
            if (!TextUtils.isEmpty(prefString)) {
                CommonPacketExtension commonPacketExtension = new CommonPacketExtension("pf", (String) null, (String[]) null, (String[]) null);
                CommonPacketExtension commonPacketExtension2 = new CommonPacketExtension("sent", (String) null, (String[]) null, (String[]) null);
                commonPacketExtension2.setText(prefString);
                commonPacketExtension.appendChild(commonPacketExtension2);
                messageArr[i2].addExtension(commonPacketExtension);
            }
            MyLog.v("SEND:" + messageArr[i2].toXML());
            bundleArr[i2] = messageArr[i2].toBundle();
        }
        if (length <= 0) {
            return false;
        }
        intentCreateServiceIntent.setAction(PushConstants.ACTION_BATCH_SEND_MESSAGE);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_SESSION, sSession);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKETS, bundleArr);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_ENCYPT, z2);
        return startServiceSafely(intentCreateServiceIntent);
    }

    public void checkAlive() {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushServiceConstants.ACTION_CHECK_ALIVE);
        startServiceSafely(intentCreateServiceIntent);
    }

    public boolean closeChannel() {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.ACTION_CLOSE_CHANNEL);
        return startServiceSafely(intentCreateServiceIntent);
    }

    @Deprecated
    public boolean forceReconnection(String str, String str2, String str3, String str4, String str5, boolean z2, List<NameValuePair> list, List<NameValuePair> list2) {
        return forceReconnection(str, str2, str3, str4, str5, z2, translate(list), translate(list2));
    }

    public boolean isMiuiPushServiceEnabled() {
        return this.mIsMiuiPushServiceEnabled;
    }

    public boolean notifyMessage(Bundle bundle, String str, String str2) {
        if (bundle == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            MyLog.w("Failed to notify message: bundle|userId|chid may be empty");
            return false;
        }
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.ACTION_APP_NOTIFY_MSG);
        intentCreateServiceIntent.putExtras(bundle);
        MyLog.persist("notify: chid=" + str2 + " bundle:" + bundle);
        return startServiceSafely(intentCreateServiceIntent);
    }

    @Deprecated
    public int openChannel(String str, String str2, String str3, String str4, String str5, boolean z2, List<NameValuePair> list, List<NameValuePair> list2) {
        return openChannel(str, str2, str3, str4, str5, translate(list), translate(list2), z2);
    }

    @Deprecated
    public void resetConnection(String str, String str2, String str3, String str4, String str5, boolean z2, List<NameValuePair> list, List<NameValuePair> list2) {
        resetConnection(str, str2, str3, str4, str5, z2, translate(list), translate(list2));
    }

    public boolean sendIQ(IQ iq) {
        if (!Network.hasNetwork(this.mContext)) {
            return false;
        }
        Intent intentCreateServiceIntent = createServiceIntent();
        Bundle bundle = iq.toBundle();
        if (bundle == null) {
            return false;
        }
        MyLog.v("SEND:" + iq.toXML());
        intentCreateServiceIntent.setAction(PushConstants.ACTION_SEND_IQ);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_SESSION, sSession);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKET, bundle);
        return startServiceSafely(intentCreateServiceIntent);
    }

    public boolean sendMessage(com.xiaomi.smack.packet.Message message, boolean z2) {
        if (!Network.hasNetwork(this.mContext)) {
            return false;
        }
        Intent intentCreateServiceIntent = createServiceIntent();
        String prefString = MessageProfiling.getPrefString();
        if (!TextUtils.isEmpty(prefString)) {
            CommonPacketExtension commonPacketExtension = new CommonPacketExtension("pf", (String) null, (String[]) null, (String[]) null);
            CommonPacketExtension commonPacketExtension2 = new CommonPacketExtension("sent", (String) null, (String[]) null, (String[]) null);
            commonPacketExtension2.setText(prefString);
            commonPacketExtension.appendChild(commonPacketExtension2);
            message.addExtension(commonPacketExtension);
        }
        Bundle bundle = message.toBundle();
        if (bundle == null) {
            return false;
        }
        MyLog.v("SEND:" + message.toXML());
        intentCreateServiceIntent.setAction(PushConstants.ACTION_SEND_MESSAGE);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_SESSION, sSession);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKET, bundle);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_ENCYPT, z2);
        return startServiceSafely(intentCreateServiceIntent);
    }

    public boolean sendPresence(Presence presence) {
        if (!Network.hasNetwork(this.mContext)) {
            return false;
        }
        Intent intentCreateServiceIntent = createServiceIntent();
        Bundle bundle = presence.toBundle();
        if (bundle == null) {
            return false;
        }
        MyLog.v("SEND:" + presence.toXML());
        intentCreateServiceIntent.setAction(PushConstants.ACTION_SEND_PRESENCE);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_SESSION, sSession);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKET, bundle);
        return startServiceSafely(intentCreateServiceIntent);
    }

    public void setMessenger(Messenger messenger) {
        this.mMessenger = messenger;
    }

    public boolean startServiceSafely(Intent intent) {
        try {
            if (MIUIUtils.isMIUI()) {
                this.mContext.startService(intent);
                return true;
            }
            bindServiceSafely(intent);
            return true;
        } catch (Exception e2) {
            MyLog.e(e2);
            return false;
        }
    }

    @Deprecated
    public void updateChannelInfo(String str, List<NameValuePair> list, List<NameValuePair> list2) {
        updateChannelInfo(str, translate(list), translate(list2));
    }

    private String joinAttributes(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        int i2 = 1;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
            if (i2 < map.size()) {
                sb.append(",");
            }
            i2++;
        }
        return sb.toString();
    }

    private void putOpenParamsIntoIntent(Intent intent, String str, String str2, String str3, String str4, String str5, boolean z2, Map<String, String> map, Map<String, String> map2) {
        intent.putExtra(PushConstants.EXTRA_USER_ID, str);
        intent.putExtra(PushConstants.EXTRA_CHANNEL_ID, str2);
        intent.putExtra(PushConstants.EXTRA_TOKEN, str3);
        intent.putExtra(PushConstants.EXTRA_SECURITY, str5);
        intent.putExtra(PushConstants.EXTRA_AUTH_METHOD, str4);
        intent.putExtra(PushConstants.EXTRA_KICK, z2);
        intent.putExtra(PushConstants.EXTRA_SESSION, sSession);
        intent.putExtra(PushConstants.EXTRA_MESSENGER, this.mMessenger);
        if (map != null && map.size() > 0) {
            String strJoinAttributes = joinAttributes(map);
            if (!TextUtils.isEmpty(strJoinAttributes)) {
                intent.putExtra(PushConstants.EXTRA_CLIENT_ATTR, strJoinAttributes);
            }
        }
        if (map2 == null || map2.size() <= 0) {
            return;
        }
        String strJoinAttributes2 = joinAttributes(map2);
        if (TextUtils.isEmpty(strJoinAttributes2)) {
            return;
        }
        intent.putExtra(PushConstants.EXTRA_CLOUD_ATTR, strJoinAttributes2);
    }

    public boolean forceReconnection(String str, String str2, String str3, String str4, String str5, boolean z2, Map<String, String> map, Map<String, String> map2) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.ACTION_FORCE_RECONNECT);
        putOpenParamsIntoIntent(intentCreateServiceIntent, str, str2, str3, str4, str5, z2, map, map2);
        return startServiceSafely(intentCreateServiceIntent);
    }

    public int openChannel(String str, String str2, String str3, String str4, String str5, Map<String, String> map, Map<String, String> map2, boolean z2) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.ACTION_OPEN_CHANNEL);
        putOpenParamsIntoIntent(intentCreateServiceIntent, str, str2, str3, str4, str5, z2, map, map2);
        startServiceSafely(intentCreateServiceIntent);
        return 0;
    }

    public void resetConnection(String str, String str2, String str3, String str4, String str5, boolean z2, Map<String, String> map, Map<String, String> map2) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.ACTION_RESET_CONNECTION);
        putOpenParamsIntoIntent(intentCreateServiceIntent, str, str2, str3, str4, str5, z2, map, map2);
        startServiceSafely(intentCreateServiceIntent);
    }

    public void updateChannelInfo(String str, Map<String, String> map, Map<String, String> map2) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.ACTION_UPDATE_CHANNEL_INFO);
        if (map != null) {
            String strJoinAttributes = joinAttributes(map);
            if (!TextUtils.isEmpty(strJoinAttributes)) {
                intentCreateServiceIntent.putExtra(PushConstants.EXTRA_CLIENT_ATTR, strJoinAttributes);
            }
        }
        if (map2 != null) {
            String strJoinAttributes2 = joinAttributes(map2);
            if (!TextUtils.isEmpty(strJoinAttributes2)) {
                intentCreateServiceIntent.putExtra(PushConstants.EXTRA_CLOUD_ATTR, strJoinAttributes2);
            }
        }
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_CHANNEL_ID, str);
        startServiceSafely(intentCreateServiceIntent);
    }

    public boolean closeChannel(String str) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.ACTION_CLOSE_CHANNEL);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_CHANNEL_ID, str);
        return startServiceSafely(intentCreateServiceIntent);
    }

    public boolean closeChannel(String str, String str2) {
        Intent intentCreateServiceIntent = createServiceIntent();
        intentCreateServiceIntent.setAction(PushConstants.ACTION_CLOSE_CHANNEL);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_CHANNEL_ID, str);
        intentCreateServiceIntent.putExtra(PushConstants.EXTRA_USER_ID, str2);
        return startServiceSafely(intentCreateServiceIntent);
    }

    public boolean sendMessage(byte[] bArr, String str, String str2) {
        String strSubstring;
        if (Network.hasNetwork(this.mContext) && bArr != null && !TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            Intent intentCreateServiceIntent = createServiceIntent();
            intentCreateServiceIntent.setAction(PushConstants.ACTION_SEND_MESSAGE);
            intentCreateServiceIntent.putExtra(PushConstants.EXTRA_SESSION, sSession);
            intentCreateServiceIntent.putExtra(PushConstants.EXTRA_RAW_PACKET, bArr);
            int iIndexOf = str.indexOf("@");
            String strSubstring2 = null;
            String strSubstring3 = iIndexOf != -1 ? str.substring(0, iIndexOf) : null;
            int iLastIndexOf = str.lastIndexOf("/");
            if (iLastIndexOf != -1) {
                strSubstring2 = str.substring(iIndexOf + 1, iLastIndexOf);
                strSubstring = str.substring(iLastIndexOf + 1);
            } else {
                strSubstring = null;
            }
            intentCreateServiceIntent.putExtra(PushConstants.EXTRA_USER_ID, strSubstring3);
            intentCreateServiceIntent.putExtra(PushConstants.EXTRA_USER_SERVER, strSubstring2);
            intentCreateServiceIntent.putExtra(PushConstants.EXTRA_USER_RES, strSubstring);
            StringBuilder sb = new StringBuilder();
            sb.append(prefix);
            long j2 = sPacketCount;
            sPacketCount = 1 + j2;
            sb.append(j2);
            String string = sb.toString();
            intentCreateServiceIntent.putExtra(PushConstants.EXTRA_PACKET_ID, string);
            intentCreateServiceIntent.putExtra(PushConstants.EXTRA_CHID, str2);
            MyLog.persist("SEND: chid=" + str2 + ", packetId=" + string);
            return startServiceSafely(intentCreateServiceIntent);
        }
        MyLog.w("Failed to send message: message|userId|chid may be empty, or the network is unavailable.");
        return false;
    }
}
