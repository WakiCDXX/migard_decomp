package com.xiaomi.smack;

import com.xiaomi.channel.commonutils.misc.BuildSettings;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ConnectionConfiguration implements Cloneable {
    public static final int CONNECT_STATUS_CONNECTED = 1;
    public static final int CONNECT_STATUS_CONNECTING = 0;
    public static final int CONNECT_STATUS_DISCONNECT = 2;
    public static final String PREF_NETWORK_DIAGNOSE = "network_diagnose";
    public static final String PREF_NETWORK_DIAG_RESULT = "logs_network_diag";
    private static final String TAG = "ConnectionConfiguration";
    public static final String XMPP_SERVER_CHINA_HOST_P = "cn.app.chat.xiaomi.net";
    public static final String XMPP_SERVER_HOST_ONEBOX = "10.38.162.35";
    public static final String XMPP_SERVER_HOST_P = "app.chat.xiaomi.net";
    public static final String XMPP_SERVER_HOST_SANDBOX = "sandbox.xmpush.xiaomi.com";
    public static String XMPP_SERVER_HOST_T = "wcc-ml-test10.bj";
    public static String xmppHost;
    private String connectionPoint;
    private String host;
    private HttpRequestProxy httpProxy;
    private int port;
    private String resource;
    private String serviceName;
    private String sid;
    private String token;
    private String username;
    private boolean debuggerEnabled = Connection.DEBUG_ENABLED;
    private boolean reconnectionAllowed = true;

    public ConnectionConfiguration(Map<String, Integer> map, int i2, String str, HttpRequestProxy httpRequestProxy) {
        init(map, i2, str, httpRequestProxy);
    }

    public static final String getXmppServerHost() {
        String str = xmppHost;
        if (str != null) {
            return str;
        }
        if (BuildSettings.IsSandBoxBuild()) {
            return XMPP_SERVER_HOST_SANDBOX;
        }
        return BuildSettings.IsOneBoxBuild() ? "10.38.162.35" : XMPP_SERVER_HOST_P;
    }

    private void init(Map<String, Integer> map, int i2, String str, HttpRequestProxy httpRequestProxy) {
        this.port = i2;
        this.serviceName = str;
        this.httpProxy = httpRequestProxy;
    }

    public static final void setXmppServerHost(String str) {
        if (BuildSettings.IsOneBoxBuild()) {
            return;
        }
        xmppHost = str;
    }

    public byte[] getConnectionBlob() {
        return null;
    }

    public String getConnectionPoint() {
        return this.connectionPoint;
    }

    public String getHost() {
        if (this.host == null) {
            this.host = getXmppServerHost();
        }
        return this.host;
    }

    public HttpRequestProxy getHttpRequestProxy() {
        return this.httpProxy;
    }

    public int getPort() {
        return this.port;
    }

    synchronized String getResource() {
        return this.resource;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    synchronized String getSid() {
        return this.sid;
    }

    synchronized String getToken() {
        return this.token;
    }

    public synchronized String getUsername() {
        return this.username;
    }

    public boolean isDebuggerEnabled() {
        return this.debuggerEnabled;
    }

    public boolean isReconnectionAllowed() {
        return this.reconnectionAllowed;
    }

    public void setConnectionPoint(String str) {
        this.connectionPoint = str;
    }

    public void setDebuggerEnabled(boolean z2) {
        this.debuggerEnabled = z2;
    }

    public void setHost(String str) {
        this.host = str;
    }

    public synchronized void setLoginInfo(String str, String str2, String str3, String str4) {
        this.username = str;
        this.sid = str2;
        this.token = str3;
        this.resource = str4;
    }

    public void setReconnectionAllowed(boolean z2) {
        this.reconnectionAllowed = z2;
    }

    public void setServiceName(String str) {
        this.serviceName = str;
    }

    public ConnectionConfiguration(Map<String, Integer> map, int i2, HttpRequestProxy httpRequestProxy) {
        init(map, i2, "", httpRequestProxy);
    }
}
