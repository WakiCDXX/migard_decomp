package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;

/* JADX INFO: loaded from: classes.dex */
public class ChannelPushReportAgent {
    private static ReceiveAgent sReceiveAgent;
    private static ReportAgent sReportAgent;

    public interface ReceiveAgent {
        boolean receive(XmPushActionNotification xmPushActionNotification);
    }

    public interface ReportAgent {
        void report(XmPushActionNotification xmPushActionNotification);
    }

    public static boolean receive(XmPushActionNotification xmPushActionNotification) {
        if (sReceiveAgent == null || xmPushActionNotification == null) {
            MyLog.w("rc params is null, not cpra");
            return false;
        }
        if (MIUIUtils.isXMSF(SystemUtils.getContext())) {
            return sReceiveAgent.receive(xmPushActionNotification);
        }
        MyLog.w("rc app not permission to cpra");
        return false;
    }

    public static void report(XmPushActionNotification xmPushActionNotification) {
        if (sReportAgent == null || xmPushActionNotification == null) {
            MyLog.w("rp params is null, not cpra");
        } else if (MIUIUtils.isXMSF(SystemUtils.getContext())) {
            sReportAgent.report(xmPushActionNotification);
        } else {
            MyLog.w("rp app not permission to cpra");
        }
    }

    public static void setReceiveAgent(ReceiveAgent receiveAgent) {
        sReceiveAgent = receiveAgent;
    }

    public static void setReportAgent(ReportAgent reportAgent) {
        sReportAgent = reportAgent;
    }
}
