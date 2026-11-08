package com.xiaomi.report;

import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.stats.Stats;
import com.xiaomi.push.protobuf.ChannelMessage;
import com.xiaomi.push.service.ServiceConfig;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.push.thrift.ChannelStatsType;
import com.xiaomi.push.thrift.StatsEvent;
import com.xiaomi.push.thrift.StatsEvents;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.XmPushTBinaryProtocol;
import org.apache.thrift.protocol.d;
import org.apache.thrift.transport.b;

/* JADX INFO: loaded from: classes.dex */
public class ConnReportHandler {
    private static final int MAX_DURATION = 604800000;
    private static final int MIN_STATS_PING_SIZE = 750;
    private ConnReportContext context;
    private int duration;
    private long startTime;
    private String uuid;
    private boolean allowStatsUpload = false;
    private Stats statsContainer = Stats.instance();

    static class Holder {
        static final ConnReportHandler S_CONN_REPORT_HANDLER = new ConnReportHandler();

        Holder() {
        }
    }

    private void dump() {
        MyLog.w("[Stats] dump stats:");
        Iterator<Stats.Item> it = this.statsContainer.getStats().iterator();
        while (it.hasNext()) {
            Object obj = it.next().obj;
            if (obj instanceof StatsEvent) {
                StatsEvent statsEvent = (StatsEvent) obj;
                MyLog.w("[Stats] type=" + statsEvent.type + ", host=" + statsEvent.host + ", val=" + statsEvent.value + ", subval=" + statsEvent.subvalue);
            }
        }
    }

    private StatsEvent from(Stats.Item item) {
        if (item.key == 0) {
            Object obj = item.obj;
            if (obj instanceof StatsEvent) {
                return (StatsEvent) obj;
            }
            return null;
        }
        StatsEvent statsEventCreateStatsEvent = createStatsEvent();
        statsEventCreateStatsEvent.setType(ChannelStatsType.CHANNEL_STATS_COUNTER.getValue());
        statsEventCreateStatsEvent.setSubvalue(item.key);
        statsEventCreateStatsEvent.setAnnotation(item.annotation);
        return statsEventCreateStatsEvent;
    }

    public static ConnReportContext getContext() {
        ConnReportContext connReportContext;
        ConnReportHandler connReportHandler = Holder.S_CONN_REPORT_HANDLER;
        synchronized (connReportHandler) {
            connReportContext = connReportHandler.context;
        }
        return connReportContext;
    }

    public static ConnReportHandler getInstance() {
        return Holder.S_CONN_REPORT_HANDLER;
    }

    private void internalAdd(int i2, int i3, int i4, String str, String str2, long j2) {
        StatsEvent statsEvent = new StatsEvent();
        statsEvent.chid = (byte) i2;
        statsEvent.type = i3;
        statsEvent.value = i4;
        statsEvent.connpt = str2;
        statsEvent.host = str;
        statsEvent.time = ((int) System.currentTimeMillis()) / 1000;
        this.statsContainer.stat(statsEvent);
        MyLog.v(String.format(Locale.US, "add stats: chid = %s, type =%d, value = %d, connpt = %s", Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), str2));
    }

    private void stopStatsIfNeed() {
        if (!this.allowStatsUpload || System.currentTimeMillis() - this.startTime <= this.duration) {
            return;
        }
        this.allowStatsUpload = false;
        this.startTime = 0L;
    }

    public synchronized void add(int i2, int i3, int i4, String str) {
        if (this.uuid == null) {
            MyLog.v(String.format(Locale.US, "ConnReportHandler.add() Should initialized before add", new Object[0]));
            return;
        }
        String activeConnPoint = Network.getActiveConnPoint(this.context.pushService);
        if (!TextUtils.isEmpty(activeConnPoint)) {
            internalAdd(i2, i3, i4, str, activeConnPoint, System.currentTimeMillis());
        }
    }

    synchronized StatsEvent createStatsEvent() {
        StatsEvent statsEvent;
        statsEvent = new StatsEvent();
        statsEvent.setConnpt(Network.getActiveConnPoint(this.context.pushService));
        statsEvent.chid = (byte) 0;
        statsEvent.value = 1;
        statsEvent.setTime((int) (System.currentTimeMillis() / 1000));
        return statsEvent;
    }

    public synchronized void init(XMPushService xMPushService) {
        this.context = new ConnReportContext(xMPushService);
        this.uuid = "";
        ServiceConfig.getInstance().addListener(new ServiceConfig.Listener() { // from class: com.xiaomi.report.ConnReportHandler.1
            @Override // com.xiaomi.push.service.ServiceConfig.Listener
            public void onConfigMsgReceive(ChannelMessage.PushServiceConfigMsg pushServiceConfigMsg) {
                if (pushServiceConfigMsg.hasDots()) {
                    ConnReportHandler.getInstance().setDuration(pushServiceConfigMsg.getDots());
                }
            }
        });
    }

    public boolean isAllowStats() {
        return this.allowStatsUpload;
    }

    synchronized StatsEvents retriveStatsEvents() {
        StatsEvents statsEventsRetriveStatsEvents;
        if (shouldSendStatsNow()) {
            statsEventsRetriveStatsEvents = retriveStatsEvents(!Network.isWIFIConnected(this.context.pushService) ? 375 : MIN_STATS_PING_SIZE);
        } else {
            statsEventsRetriveStatsEvents = null;
        }
        return statsEventsRetriveStatsEvents;
    }

    public void setDuration(int i2) {
        if (i2 > 0) {
            int i3 = i2 * 1000;
            if (i3 > 604800000) {
                i3 = 604800000;
            }
            if (this.duration == i3 && this.allowStatsUpload) {
                return;
            }
            this.allowStatsUpload = true;
            this.startTime = System.currentTimeMillis();
            this.duration = i3;
            MyLog.v("enable dot duration = " + i3 + " start = " + this.startTime);
        }
    }

    boolean shouldSendStatsNow() {
        stopStatsIfNeed();
        return this.allowStatsUpload && this.statsContainer.getCount() > 0;
    }

    private StatsEvents retriveStatsEvents(int i2) {
        ArrayList arrayList = new ArrayList();
        StatsEvents statsEvents = new StatsEvents(this.uuid, arrayList);
        if (!Network.isWIFIConnected(this.context.pushService)) {
            statsEvents.setOperator(DeviceInfo.getSimOperatorName(this.context.pushService));
        }
        b bVar = new b(i2);
        d dVarA = new XmPushTBinaryProtocol.Factory().a(bVar);
        try {
            statsEvents.write(dVarA);
        } catch (TException unused) {
        }
        LinkedList<Stats.Item> stats = this.statsContainer.getStats();
        while (stats.size() > 0) {
            try {
                StatsEvent statsEventFrom = from(stats.getLast());
                if (statsEventFrom != null) {
                    statsEventFrom.write(dVarA);
                }
                if (bVar.h() > i2) {
                    break;
                }
                if (statsEventFrom != null) {
                    arrayList.add(statsEventFrom);
                }
                stats.removeLast();
            } catch (NoSuchElementException | TException unused2) {
            }
        }
        return statsEvents;
    }

    synchronized void add(StatsEvent statsEvent) {
        this.statsContainer.stat(statsEvent);
    }
}
