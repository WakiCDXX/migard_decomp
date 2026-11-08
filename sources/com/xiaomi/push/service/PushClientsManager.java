package com.xiaomi.push.service;

import android.content.Context;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.slim.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public class PushClientsManager {
    private static PushClientsManager sInstance;
    private ConcurrentHashMap<String, HashMap<String, ClientLoginInfo>> mActiveClients = new ConcurrentHashMap<>();
    private List<ClientChangeListener> clientChangeListeners = new ArrayList();

    public interface ClientChangeListener {
        void onChange();
    }

    public enum ClientStatus {
        unbind,
        binding,
        binded
    }

    private PushClientsManager() {
    }

    public static synchronized PushClientsManager getInstance() {
        try {
            if (sInstance == null) {
                sInstance = new PushClientsManager();
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private String getSmtpLocalPart(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int iIndexOf = str.indexOf("@");
        return iIndexOf > 0 ? str.substring(0, iIndexOf) : str;
    }

    public synchronized void addActiveClient(ClientLoginInfo clientLoginInfo) {
        try {
            HashMap<String, ClientLoginInfo> map = this.mActiveClients.get(clientLoginInfo.chid);
            if (map == null) {
                map = new HashMap<>();
                this.mActiveClients.put(clientLoginInfo.chid, map);
            }
            map.put(getSmtpLocalPart(clientLoginInfo.userId), clientLoginInfo);
            MyLog.w("add active client. " + clientLoginInfo.pkgName);
            Iterator<ClientChangeListener> it = this.clientChangeListeners.iterator();
            while (it.hasNext()) {
                it.next().onChange();
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized void addClientChangeListener(ClientChangeListener clientChangeListener) {
        this.clientChangeListeners.add(clientChangeListener);
    }

    public synchronized void deactivateAllClientByChid(String str) {
        try {
            HashMap<String, ClientLoginInfo> map = this.mActiveClients.get(str);
            if (map != null) {
                Iterator<ClientLoginInfo> it = map.values().iterator();
                while (it.hasNext()) {
                    it.next().unwatch();
                }
                map.clear();
                this.mActiveClients.remove(str);
            }
            Iterator<ClientChangeListener> it2 = this.clientChangeListeners.iterator();
            while (it2.hasNext()) {
                it2.next().onChange();
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized void deactivateClient(String str, String str2) {
        try {
            HashMap<String, ClientLoginInfo> map = this.mActiveClients.get(str);
            if (map != null) {
                ClientLoginInfo clientLoginInfo = map.get(getSmtpLocalPart(str2));
                if (clientLoginInfo != null) {
                    clientLoginInfo.unwatch();
                }
                map.remove(getSmtpLocalPart(str2));
                if (map.isEmpty()) {
                    this.mActiveClients.remove(str);
                }
            }
            Iterator<ClientChangeListener> it = this.clientChangeListeners.iterator();
            while (it.hasNext()) {
                it.next().onChange();
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized int getActiveClientCount() {
        return this.mActiveClients.size();
    }

    public synchronized Collection<ClientLoginInfo> getAllClientLoginInfoByChid(String str) {
        if (this.mActiveClients.containsKey(str)) {
            return ((HashMap) this.mActiveClients.get(str).clone()).values();
        }
        return new ArrayList();
    }

    public synchronized ArrayList<ClientLoginInfo> getAllClients() {
        ArrayList<ClientLoginInfo> arrayList;
        arrayList = new ArrayList<>();
        Iterator<HashMap<String, ClientLoginInfo>> it = this.mActiveClients.values().iterator();
        while (it.hasNext()) {
            arrayList.addAll(it.next().values());
        }
        return arrayList;
    }

    public synchronized ClientLoginInfo getClientLoginInfoByChidAndUserId(String str, String str2) {
        HashMap<String, ClientLoginInfo> map = this.mActiveClients.get(str);
        if (map == null) {
            return null;
        }
        return map.get(getSmtpLocalPart(str2));
    }

    public synchronized void notifyConnectionFailed(Context context) {
        Iterator<HashMap<String, ClientLoginInfo>> it = this.mActiveClients.values().iterator();
        while (it.hasNext()) {
            Iterator<ClientLoginInfo> it2 = it.next().values().iterator();
            while (it2.hasNext()) {
                it2.next().setStatus(ClientStatus.unbind, 1, 3, null, null);
            }
        }
    }

    public synchronized List<String> queryChannelIdByPackage(String str) {
        ArrayList arrayList;
        arrayList = new ArrayList();
        Iterator<HashMap<String, ClientLoginInfo>> it = this.mActiveClients.values().iterator();
        while (it.hasNext()) {
            for (ClientLoginInfo clientLoginInfo : it.next().values()) {
                if (str.equals(clientLoginInfo.pkgName)) {
                    arrayList.add(clientLoginInfo.chid);
                }
            }
        }
        return arrayList;
    }

    public synchronized void removeActiveClients() {
        try {
            Iterator<ClientLoginInfo> it = getAllClients().iterator();
            while (it.hasNext()) {
                it.next().unwatch();
            }
            this.mActiveClients.clear();
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized void removeAllClientChangeListeners() {
        this.clientChangeListeners.clear();
    }

    public synchronized void removeClientChangeListener(ClientChangeListener clientChangeListener) {
        this.clientChangeListeners.remove(clientChangeListener);
    }

    public synchronized void resetAllClients(Context context, int i2) {
        Iterator<HashMap<String, ClientLoginInfo>> it = this.mActiveClients.values().iterator();
        while (it.hasNext()) {
            Iterator<ClientLoginInfo> it2 = it.next().values().iterator();
            while (it2.hasNext()) {
                it2.next().setStatus(ClientStatus.unbind, 2, i2, null, null);
            }
        }
    }

    public static class ClientLoginInfo {
        public static final int TYPE_CHANNEL_CLOSE = 2;
        public static final int TYPE_CHANNEL_NO_NOTIFY = 0;
        public static final int TYPE_CHANNEL_OPEN_RESULT = 1;
        public static final int TYPE_CHANNEL_SERVER_KICK = 3;
        public String authMethod;
        public String chid;
        public String clientExtra;
        public String cloudExtra;
        public Context context;
        public boolean kick;
        public ClientEventDispatcher mClientEventDispatcher;
        private XMPushService mPushService;
        Messenger peer;
        public String pkgName;
        public String security;
        public String session;
        public String token;
        public String userId;
        ClientStatus status = ClientStatus.unbind;
        private int currentRetrys = 0;
        private final CopyOnWriteArrayList<ClientStatusListener> statusChangeListeners = new CopyOnWriteArrayList<>();
        ClientStatus notifiedStatus = null;
        private boolean hasPeerSupport = false;
        private XMPushService.BindTimeoutJob timeOutJob = new XMPushService.BindTimeoutJob(this);
        IBinder.DeathRecipient peerWatcher = null;
        final NotifyClientJob notifyClientJob = new NotifyClientJob();

        public interface ClientStatusListener {
            void onChange(ClientStatus clientStatus, ClientStatus clientStatus2, int i2);
        }

        class NotifyClientJob extends XMPushService.Job {
            String errorType;
            int notifyType;
            int reason;
            String reasonMessage;

            public NotifyClientJob() {
                super(0);
            }

            public XMPushService.Job build(int i2, int i3, String str, String str2) {
                this.notifyType = i2;
                this.reason = i3;
                this.errorType = str2;
                this.reasonMessage = str;
                return this;
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "notify job";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                if (ClientLoginInfo.this.shouldNotifyClient(this.notifyType, this.reason, this.errorType)) {
                    ClientLoginInfo.this.notifyClientStatus(this.notifyType, this.reason, this.reasonMessage, this.errorType);
                    return;
                }
                MyLog.i(" ignore notify client :" + ClientLoginInfo.this.chid);
            }
        }

        class PeerWatcher implements IBinder.DeathRecipient {
            final ClientLoginInfo info;
            final Messenger peer;

            PeerWatcher(ClientLoginInfo clientLoginInfo, Messenger messenger) {
                this.info = clientLoginInfo;
                this.peer = messenger;
            }

            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                MyLog.i("peer died, chid = " + this.info.chid);
                int i2 = 0;
                ClientLoginInfo.this.mPushService.executeJobDelayed(new XMPushService.Job(i2) { // from class: com.xiaomi.push.service.PushClientsManager.ClientLoginInfo.PeerWatcher.1
                    @Override // com.xiaomi.push.service.XMPushService.Job
                    public String getDesc() {
                        return "clear peer job";
                    }

                    @Override // com.xiaomi.push.service.XMPushService.Job
                    public void process() {
                        PeerWatcher peerWatcher = PeerWatcher.this;
                        if (peerWatcher.peer == peerWatcher.info.peer) {
                            MyLog.i("clean peer, chid = " + PeerWatcher.this.info.chid);
                            PeerWatcher.this.info.peer = null;
                        }
                    }
                }, 0L);
                if ("9".equals(this.info.chid) && PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(ClientLoginInfo.this.mPushService.getPackageName())) {
                    ClientLoginInfo.this.mPushService.executeJobDelayed(new XMPushService.Job(i2) { // from class: com.xiaomi.push.service.PushClientsManager.ClientLoginInfo.PeerWatcher.2
                        @Override // com.xiaomi.push.service.XMPushService.Job
                        public String getDesc() {
                            return "check peer job";
                        }

                        @Override // com.xiaomi.push.service.XMPushService.Job
                        public void process() {
                            PushClientsManager pushClientsManager = PushClientsManager.getInstance();
                            ClientLoginInfo clientLoginInfo = PeerWatcher.this.info;
                            if (pushClientsManager.getClientLoginInfoByChidAndUserId(clientLoginInfo.chid, clientLoginInfo.userId).peer == null) {
                                XMPushService xMPushService = ClientLoginInfo.this.mPushService;
                                ClientLoginInfo clientLoginInfo2 = PeerWatcher.this.info;
                                xMPushService.closeChannel(clientLoginInfo2.chid, clientLoginInfo2.userId, 2, null, null);
                            }
                        }
                    }, 60000L);
                }
            }
        }

        public ClientLoginInfo() {
        }

        public static String getResource(String str) {
            int iLastIndexOf;
            return (TextUtils.isEmpty(str) || (iLastIndexOf = str.lastIndexOf("/")) == -1) ? "" : str.substring(iLastIndexOf + 1);
        }

        private boolean isSpeicalErr(int i2, int i3, String str) {
            if (i2 == 1) {
                return (this.status == ClientStatus.binded || !this.mPushService.isConnected() || i3 == 21 || (i3 == 7 && "wait".equals(str))) ? false : true;
            }
            if (i2 == 2) {
                return this.mPushService.isConnected();
            }
            if (i2 != 3) {
                return false;
            }
            return !"wait".equals(str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyClientStatus(int i2, int i3, String str, String str2) {
            ClientStatus clientStatus = this.status;
            this.notifiedStatus = clientStatus;
            if (i2 == 2) {
                this.mClientEventDispatcher.notifyChannelClosed(this.context, this, i3);
                return;
            }
            if (i2 == 3) {
                this.mClientEventDispatcher.notifyKickedByServer(this.context, this, str2, str);
                return;
            }
            if (i2 == 1) {
                boolean z2 = clientStatus == ClientStatus.binded;
                if (!z2 && "wait".equals(str2)) {
                    this.currentRetrys++;
                } else if (z2) {
                    this.currentRetrys = 0;
                    if (this.peer != null) {
                        try {
                            this.peer.send(Message.obtain(null, 16, this.mPushService.messenger));
                        } catch (RemoteException unused) {
                        }
                    }
                }
                this.mClientEventDispatcher.notifyChannelOpenResult(this.mPushService, this, z2, i3, str);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean shouldNotifyClient(int i2, int i3, String str) {
            boolean z2;
            ClientStatus clientStatus = this.notifiedStatus;
            if (clientStatus == null || !(z2 = this.hasPeerSupport)) {
                return true;
            }
            if (clientStatus == this.status) {
                MyLog.i(" status recovered, don't notify client:" + this.chid);
                return false;
            }
            if (this.peer == null || !z2) {
                MyLog.i("peer died, ignore notify " + this.chid);
                return false;
            }
            MyLog.i("Peer alive notify status to client:" + this.chid);
            return true;
        }

        public void addClientStatusListener(ClientStatusListener clientStatusListener) {
            this.statusChangeListeners.add(clientStatusListener);
        }

        public String getDesc(int i2) {
            if (i2 == 1) {
                return "OPEN";
            }
            if (i2 != 2) {
                return i2 != 3 ? "unknown" : Blob.CMD_KICK;
            }
            return Blob.CMD_CLOSE;
        }

        public long getNextRetryInterval() {
            return (((long) ((Math.random() * 20.0d) - 10.0d)) + ((long) ((this.currentRetrys + 1) * 15))) * 1000;
        }

        public void removeClientStatusListener(ClientStatusListener clientStatusListener) {
            this.statusChangeListeners.remove(clientStatusListener);
        }

        public void setStatus(ClientStatus clientStatus, int i2, int i3, String str, String str2) {
            boolean z2;
            for (ClientStatusListener clientStatusListener : this.statusChangeListeners) {
                if (clientStatusListener != null) {
                    clientStatusListener.onChange(this.status, clientStatus, i3);
                }
            }
            ClientStatus clientStatus2 = this.status;
            if (clientStatus2 != clientStatus) {
                MyLog.w(String.format("update the client %7$s status. %1$s->%2$s %3$s %4$s %5$s %6$s", clientStatus2, clientStatus, getDesc(i2), PushConstants.getErrorDesc(i3), str, str2, this.chid));
                this.status = clientStatus;
            }
            if (this.mClientEventDispatcher == null) {
                MyLog.e("status changed while the client dispatcher is missing");
                return;
            }
            if (clientStatus == ClientStatus.binding) {
                return;
            }
            int i4 = (this.notifiedStatus == null || !(z2 = this.hasPeerSupport)) ? 0 : (this.peer == null || !z2) ? 10100 : 1000;
            this.mPushService.removeJobs(this.notifyClientJob);
            if (isSpeicalErr(i2, i3, str2)) {
                notifyClientStatus(i2, i3, str, str2);
            } else {
                this.mPushService.executeJobDelayed(this.notifyClientJob.build(i2, i3, str, str2), i4);
            }
        }

        void unwatch() {
            try {
                Messenger messenger = this.peer;
                if (messenger != null && this.peerWatcher != null) {
                    messenger.getBinder().unlinkToDeath(this.peerWatcher, 0);
                }
            } catch (Exception unused) {
            }
            this.notifiedStatus = null;
        }

        void watch(Messenger messenger) {
            unwatch();
            try {
                if (messenger != null) {
                    this.peer = messenger;
                    this.hasPeerSupport = true;
                    this.peerWatcher = new PeerWatcher(this, messenger);
                    messenger.getBinder().linkToDeath(this.peerWatcher, 0);
                } else {
                    MyLog.i("peer linked with old sdk chid = " + this.chid);
                }
            } catch (Exception e2) {
                MyLog.i("peer linkToDeath err: " + e2.getMessage());
                this.peer = null;
                this.hasPeerSupport = false;
            }
        }

        public ClientLoginInfo(XMPushService xMPushService) {
            this.mPushService = xMPushService;
            addClientStatusListener(new ClientStatusListener() { // from class: com.xiaomi.push.service.PushClientsManager.ClientLoginInfo.1
                @Override // com.xiaomi.push.service.PushClientsManager.ClientLoginInfo.ClientStatusListener
                public void onChange(ClientStatus clientStatus, ClientStatus clientStatus2, int i2) {
                    if (clientStatus2 == ClientStatus.binding) {
                        ClientLoginInfo.this.mPushService.executeJobDelayed(ClientLoginInfo.this.timeOutJob, 60000L);
                    } else {
                        ClientLoginInfo.this.mPushService.removeJobs(ClientLoginInfo.this.timeOutJob);
                    }
                }
            });
        }
    }
}
