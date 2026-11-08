package com.xiaomi.push.service;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.network.Fallback;
import com.xiaomi.network.HostFilter;
import com.xiaomi.network.HostManager;
import com.xiaomi.push.protobuf.ChannelConfig;
import com.xiaomi.push.protobuf.ChannelMessage;
import com.xiaomi.push.thrift.ChannelStatsType;
import com.xiaomi.report.ConnReportHandler;
import com.xiaomi.report.ConnReportHelper;
import com.xiaomi.smack.Connection;
import com.xiaomi.smack.util.StringUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class PushHostManagerFactory extends ServiceConfig.Listener implements HostManager.HostManagerFactory {
    private static final long MIN_BUCKET_FETCH_DURATION = 3600000;
    private static final String V2 = "bucket_v2";
    private static final String VERSION = "2.2";
    private long lastFetchTime;
    private XMPushService pushService;

    static class GslbHttpGet implements HostManager.HttpGet {
        private static final String KEY_DROID_VERSION = "osver";
        private static final String KEY_MI = "mi";
        private static final String KEY_OS = "os";
        private static final String KEY_SDK_VERSION = "sdkver";

        GslbHttpGet() {
        }

        @Override // com.xiaomi.network.HostManager.HttpGet
        public String doGet(String str) throws IOException {
            Uri.Builder builderBuildUpon = Uri.parse(str).buildUpon();
            builderBuildUpon.appendQueryParameter(KEY_SDK_VERSION, String.valueOf(48));
            builderBuildUpon.appendQueryParameter(KEY_DROID_VERSION, String.valueOf(Build.VERSION.SDK_INT));
            builderBuildUpon.appendQueryParameter(KEY_OS, StringUtils.escapeForXML(Build.MODEL + ":" + Build.VERSION.INCREMENTAL));
            builderBuildUpon.appendQueryParameter(KEY_MI, String.valueOf(SystemUtils.getMIUIType()));
            String string = builderBuildUpon.toString();
            MyLog.v("fetch bucket from : " + string);
            URL url = new URL(string);
            int port = url.getPort() == -1 ? 80 : url.getPort();
            try {
                long jCurrentTimeMillis = System.currentTimeMillis();
                String strDownloadXml = Network.downloadXml(SystemUtils.getContext(), url);
                ConnReportHelper.statsGslb(url.getHost() + ":" + port, (int) (System.currentTimeMillis() - jCurrentTimeMillis), null);
                return strDownloadXml;
            } catch (IOException e2) {
                ConnReportHelper.statsGslb(url.getHost() + ":" + port, -1, e2);
                throw e2;
            }
        }
    }

    static class PushHostManager extends HostManager {
        protected PushHostManager(Context context, HostFilter hostFilter, HostManager.HttpGet httpGet, String str) {
            super(context, hostFilter, httpGet, str);
        }

        @Override // com.xiaomi.network.HostManager
        protected String getRemoteFallbackJSON(ArrayList<String> arrayList, String str, String str2, boolean z2) throws IOException {
            try {
                if (ConnReportHandler.getInstance().isAllowStats()) {
                    str2 = ServiceConfig.getDeviceUUID();
                }
                return super.getRemoteFallbackJSON(arrayList, str, str2, z2);
            } catch (IOException e2) {
                ConnReportHelper.stats(0, ChannelStatsType.GSLB_ERR.getValue(), 1, null, Network.isConnectivity(HostManager.sAppContext) ? 1 : 0);
                throw e2;
            }
        }
    }

    PushHostManagerFactory(XMPushService xMPushService) {
        this.pushService = xMPushService;
    }

    public static void init(XMPushService xMPushService) {
        PushHostManagerFactory pushHostManagerFactory = new PushHostManagerFactory(xMPushService);
        ServiceConfig.getInstance().addListener(pushHostManagerFactory);
        synchronized (HostManager.class) {
            HostManager.setHostManagerFactory(pushHostManagerFactory);
            HostManager.init(xMPushService, null, new GslbHttpGet(), "0", "push", VERSION);
        }
    }

    @Override // com.xiaomi.network.HostManager.HostManagerFactory
    public HostManager createHostManager(Context context, HostFilter hostFilter, HostManager.HttpGet httpGet, String str) {
        return new PushHostManager(context, hostFilter, httpGet, str);
    }

    @Override // com.xiaomi.push.service.ServiceConfig.Listener
    public void onConfigMsgReceive(ChannelMessage.PushServiceConfigMsg pushServiceConfigMsg) {
        Fallback fallbacksByHost;
        if (pushServiceConfigMsg.hasFetchBucket() && pushServiceConfigMsg.getFetchBucket() && System.currentTimeMillis() - this.lastFetchTime > 3600000) {
            MyLog.w("fetch bucket :" + pushServiceConfigMsg.getFetchBucket());
            this.lastFetchTime = System.currentTimeMillis();
            HostManager hostManager = HostManager.getInstance();
            hostManager.clear();
            hostManager.refreshFallbacks();
            Connection currentConnection = this.pushService.getCurrentConnection();
            if (currentConnection == null || (fallbacksByHost = hostManager.getFallbacksByHost(currentConnection.getConfiguration().getHost())) == null) {
                return;
            }
            ArrayList<String> hosts = fallbacksByHost.getHosts();
            Iterator<String> it = hosts.iterator();
            while (it.hasNext()) {
                if (it.next().equals(currentConnection.getHost())) {
                    return;
                }
            }
            if (hosts.isEmpty()) {
                return;
            }
            MyLog.w("bucket changed, force reconnect");
            this.pushService.disconnect(0, null);
            this.pushService.scheduleConnect(false);
        }
    }

    @Override // com.xiaomi.push.service.ServiceConfig.Listener
    public void onConfigChange(ChannelConfig.PushServiceConfig pushServiceConfig) {
    }
}
