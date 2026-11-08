package com.xiaomi.smack;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: classes.dex */
public class ConnectionHelper {
    public static int asErrorCode(Throwable th) {
        boolean z2 = th instanceof XMPPException;
        if (z2) {
            XMPPException xMPPException = (XMPPException) th;
            if (xMPPException.getWrappedThrowable() != null) {
                th = xMPPException.getWrappedThrowable();
            }
        }
        String message = th.getMessage();
        if (th.getCause() != null) {
            message = th.getCause().getMessage();
        }
        if (th instanceof SocketTimeoutException) {
            return 105;
        }
        if (!(th instanceof SocketException)) {
            if (th instanceof UnknownHostException) {
                return 107;
            }
            if (z2) {
                return Connection.ERR_XMPP;
            }
            return 0;
        }
        if (message.indexOf("Network is unreachable") != -1) {
            return 102;
        }
        if (message.indexOf("Connection refused") != -1) {
            return 103;
        }
        if (message.indexOf("Connection timed out") != -1) {
            return 105;
        }
        if (message.endsWith("EACCES (Permission denied)")) {
            return 101;
        }
        if (message.indexOf("Connection reset by peer") != -1) {
            return 109;
        }
        if (message.indexOf("Broken pipe") != -1) {
            return 110;
        }
        if (message.indexOf("No route to host") != -1) {
            return 104;
        }
        if (message.endsWith("EINVAL (Invalid argument)")) {
            return 106;
        }
        return Connection.ERR_TCP_OTHER;
    }
}
