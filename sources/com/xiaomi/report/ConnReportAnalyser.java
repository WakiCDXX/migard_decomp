package com.xiaomi.report;

import com.xiaomi.push.thrift.ChannelStatsType;
import com.xiaomi.smack.ConnectionHelper;
import com.xiaomi.smack.XMPPException;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: classes.dex */
final class ConnReportAnalyser {

    static class TypeWraper {
        String annotation;
        ChannelStatsType type;

        TypeWraper() {
        }
    }

    ConnReportAnalyser() {
    }

    private static void checkNull(Exception exc) {
        exc.getClass();
    }

    static TypeWraper fromBind(Exception exc) {
        checkNull(exc);
        boolean z2 = exc instanceof XMPPException;
        Throwable wrappedThrowable = exc;
        if (z2) {
            XMPPException xMPPException = (XMPPException) exc;
            if (xMPPException.getWrappedThrowable() != null) {
                wrappedThrowable = exc;
                wrappedThrowable = xMPPException.getWrappedThrowable();
            }
        }
        wrappedThrowable = exc;
        TypeWraper typeWraper = new TypeWraper();
        String message = wrappedThrowable.getMessage();
        if (wrappedThrowable.getCause() != null) {
            message = wrappedThrowable.getCause().getMessage();
        }
        int iAsErrorCode = ConnectionHelper.asErrorCode(wrappedThrowable);
        String str = wrappedThrowable.getClass().getSimpleName() + ":" + message;
        if (iAsErrorCode == 105) {
            typeWraper.type = ChannelStatsType.BIND_TCP_READ_TIMEOUT;
        } else if (iAsErrorCode == 199) {
            typeWraper.type = ChannelStatsType.BIND_TCP_ERR;
        } else if (iAsErrorCode == 499) {
            typeWraper.type = ChannelStatsType.BIND_BOSH_ERR;
            if (message.startsWith("Terminal binding condition encountered: item-not-found")) {
                typeWraper.type = ChannelStatsType.BIND_BOSH_ITEM_NOT_FOUND;
            }
        } else if (iAsErrorCode == 109) {
            typeWraper.type = ChannelStatsType.BIND_TCP_CONNRESET;
        } else if (iAsErrorCode != 110) {
            typeWraper.type = ChannelStatsType.BIND_XMPP_ERR;
        } else {
            typeWraper.type = ChannelStatsType.BIND_TCP_BROKEN_PIPE;
        }
        ChannelStatsType channelStatsType = typeWraper.type;
        if (channelStatsType == ChannelStatsType.BIND_TCP_ERR || channelStatsType == ChannelStatsType.BIND_XMPP_ERR || channelStatsType == ChannelStatsType.BIND_BOSH_ERR) {
            typeWraper.annotation = str;
        }
        return typeWraper;
    }

    static TypeWraper fromConnectionException(Exception exc) {
        Throwable cause;
        checkNull(exc);
        boolean z2 = exc instanceof XMPPException;
        Throwable wrappedThrowable = exc;
        if (z2) {
            XMPPException xMPPException = (XMPPException) exc;
            if (xMPPException.getWrappedThrowable() != null) {
                wrappedThrowable = exc;
                wrappedThrowable = xMPPException.getWrappedThrowable();
            }
        }
        wrappedThrowable = exc;
        TypeWraper typeWraper = new TypeWraper();
        String message = wrappedThrowable.getMessage();
        if (wrappedThrowable.getCause() != null) {
            message = wrappedThrowable.getCause().getMessage();
        }
        int iAsErrorCode = ConnectionHelper.asErrorCode(wrappedThrowable);
        String str = wrappedThrowable.getClass().getSimpleName() + ":" + message;
        if (iAsErrorCode != 0) {
            ChannelStatsType channelStatsTypeFindByValue = ChannelStatsType.findByValue(ChannelStatsType.CONN_SUCCESS.getValue() + iAsErrorCode);
            typeWraper.type = channelStatsTypeFindByValue;
            if (channelStatsTypeFindByValue == ChannelStatsType.CONN_BOSH_ERR && (cause = wrappedThrowable.getCause()) != null && (cause instanceof UnknownHostException)) {
                typeWraper.type = ChannelStatsType.CONN_BOSH_UNKNOWNHOST;
            }
        } else {
            typeWraper.type = ChannelStatsType.CONN_XMPP_ERR;
        }
        ChannelStatsType channelStatsType = typeWraper.type;
        if (channelStatsType == ChannelStatsType.CONN_TCP_ERR_OTHER || channelStatsType == ChannelStatsType.CONN_XMPP_ERR || channelStatsType == ChannelStatsType.CONN_BOSH_ERR) {
            typeWraper.annotation = str;
        }
        return typeWraper;
    }

    static TypeWraper fromDisconnectEx(Exception exc) {
        checkNull(exc);
        boolean z2 = exc instanceof XMPPException;
        Throwable wrappedThrowable = exc;
        if (z2) {
            XMPPException xMPPException = (XMPPException) exc;
            if (xMPPException.getWrappedThrowable() != null) {
                wrappedThrowable = exc;
                wrappedThrowable = xMPPException.getWrappedThrowable();
            }
        }
        wrappedThrowable = exc;
        TypeWraper typeWraper = new TypeWraper();
        String message = wrappedThrowable.getMessage();
        int iAsErrorCode = ConnectionHelper.asErrorCode(wrappedThrowable);
        String str = wrappedThrowable.getClass().getSimpleName() + ":" + message;
        if (iAsErrorCode == 105) {
            typeWraper.type = ChannelStatsType.CHANNEL_TCP_READTIMEOUT;
        } else if (iAsErrorCode == 199) {
            typeWraper.type = ChannelStatsType.CHANNEL_TCP_ERR;
        } else if (iAsErrorCode == 499) {
            typeWraper.type = ChannelStatsType.CHANNEL_BOSH_EXCEPTION;
            if (message.startsWith("Terminal binding condition encountered: item-not-found")) {
                typeWraper.type = ChannelStatsType.CHANNEL_BOSH_ITEMNOTFIND;
            }
        } else if (iAsErrorCode == 109) {
            typeWraper.type = ChannelStatsType.CHANNEL_TCP_CONNRESET;
        } else if (iAsErrorCode != 110) {
            typeWraper.type = ChannelStatsType.CHANNEL_XMPPEXCEPTION;
        } else {
            typeWraper.type = ChannelStatsType.CHANNEL_TCP_BROKEN_PIPE;
        }
        ChannelStatsType channelStatsType = typeWraper.type;
        if (channelStatsType == ChannelStatsType.CHANNEL_TCP_ERR || channelStatsType == ChannelStatsType.CHANNEL_XMPPEXCEPTION || channelStatsType == ChannelStatsType.CHANNEL_BOSH_EXCEPTION) {
            typeWraper.annotation = str;
        }
        return typeWraper;
    }

    static TypeWraper fromGslbException(Exception exc) {
        checkNull(exc);
        boolean z2 = exc instanceof XMPPException;
        Throwable wrappedThrowable = exc;
        if (z2) {
            XMPPException xMPPException = (XMPPException) exc;
            if (xMPPException.getWrappedThrowable() != null) {
                wrappedThrowable = exc;
                wrappedThrowable = xMPPException.getWrappedThrowable();
            }
        }
        wrappedThrowable = exc;
        TypeWraper typeWraper = new TypeWraper();
        String message = wrappedThrowable.getMessage();
        if (wrappedThrowable.getCause() != null) {
            message = wrappedThrowable.getCause().getMessage();
        }
        String str = wrappedThrowable.getClass().getSimpleName() + ":" + message;
        int iAsErrorCode = ConnectionHelper.asErrorCode(wrappedThrowable);
        if (iAsErrorCode != 0) {
            typeWraper.type = ChannelStatsType.findByValue(ChannelStatsType.GSLB_REQUEST_SUCCESS.getValue() + iAsErrorCode);
        }
        if (typeWraper.type == null) {
            typeWraper.type = ChannelStatsType.GSLB_TCP_ERR_OTHER;
        }
        if (typeWraper.type == ChannelStatsType.GSLB_TCP_ERR_OTHER) {
            typeWraper.annotation = str;
        }
        return typeWraper;
    }
}
