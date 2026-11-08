package com.xiaomi.report;

import android.os.SystemClock;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.string.Base64Coder;
import com.xiaomi.push.service.PushClientsManager;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.push.thrift.ChannelStatsType;
import com.xiaomi.push.thrift.StatsEvent;
import com.xiaomi.push.thrift.StatsEvents;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.Hashtable;

/* JADX INFO: loaded from: classes.dex */
public class ConnReportHelper {
    private static final int DEFAULT_CHID = 0;
    private static final int MAX_KEY_VALUE = 16777215;
    private static final long STAT_PING_RTT_DURATION = 7200000;
    private static final int PING_RTT = ChannelStatsType.PING_RTT.getValue();
    private static long sLastPingRTT = 0;

    static class Holder {
        static Hashtable<Integer, Long> sTimeTracker = new Hashtable<>();

        Holder() {
        }
    }

    public static void connectFail(String str, Exception exc) {
        try {
            ConnReportAnalyser.TypeWraper typeWraperFromConnectionException = ConnReportAnalyser.fromConnectionException(exc);
            StatsEvent statsEventCreateStatsEvent = ConnReportHandler.getInstance().createStatsEvent();
            statsEventCreateStatsEvent.setType(typeWraperFromConnectionException.type.getValue());
            statsEventCreateStatsEvent.setAnnotation(typeWraperFromConnectionException.annotation);
            statsEventCreateStatsEvent.setHost(str);
            if (ConnReportHandler.getContext() != null && ConnReportHandler.getContext().pushService != null) {
                statsEventCreateStatsEvent.setSubvalue(Network.isConnectivity(ConnReportHandler.getContext().pushService) ? 1 : 0);
            }
            ConnReportHandler.getInstance().add(statsEventCreateStatsEvent);
        } catch (NullPointerException unused) {
        }
    }

    public static void connectionDown(String str, Exception exc) {
        try {
            ConnReportAnalyser.TypeWraper typeWraperFromDisconnectEx = ConnReportAnalyser.fromDisconnectEx(exc);
            StatsEvent statsEventCreateStatsEvent = ConnReportHandler.getInstance().createStatsEvent();
            statsEventCreateStatsEvent.setType(typeWraperFromDisconnectEx.type.getValue());
            statsEventCreateStatsEvent.setAnnotation(typeWraperFromDisconnectEx.annotation);
            statsEventCreateStatsEvent.setHost(str);
            if (ConnReportHandler.getContext() != null && ConnReportHandler.getContext().pushService != null) {
                statsEventCreateStatsEvent.setSubvalue(Network.isConnectivity(ConnReportHandler.getContext().pushService) ? 1 : 0);
            }
            ConnReportHandler.getInstance().add(statsEventCreateStatsEvent);
        } catch (NullPointerException unused) {
        }
    }

    public static void count(int i2) {
        StatsEvent statsEventCreateStatsEvent = ConnReportHandler.getInstance().createStatsEvent();
        statsEventCreateStatsEvent.setType(ChannelStatsType.CHANNEL_STATS_COUNTER.getValue());
        statsEventCreateStatsEvent.setSubvalue(i2);
        ConnReportHandler.getInstance().add(statsEventCreateStatsEvent);
    }

    public static void pingEnded() {
        trackEnd(0, PING_RTT, null, -1);
    }

    public static void pingStarted() {
        if (sLastPingRTT == 0 || SystemClock.elapsedRealtime() - sLastPingRTT > 7200000) {
            sLastPingRTT = SystemClock.elapsedRealtime();
            trackStart(0, PING_RTT);
        }
    }

    public static byte[] retriveStatsAsByte() {
        StatsEvents statsEventsRetriveStatsEvents = ConnReportHandler.getInstance().retriveStatsEvents();
        if (statsEventsRetriveStatsEvents != null) {
            return XmPushThriftSerializeUtils.convertThriftObjectToBytes(statsEventsRetriveStatsEvents);
        }
        return null;
    }

    public static String retriveStatsAsString() {
        byte[] bArrConvertThriftObjectToBytes;
        StatsEvents statsEventsRetriveStatsEvents = ConnReportHandler.getInstance().retriveStatsEvents();
        if (statsEventsRetriveStatsEvents == null || (bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(statsEventsRetriveStatsEvents)) == null) {
            return null;
        }
        return new String(Base64Coder.encode(bArrConvertThriftObjectToBytes));
    }

    public static void stats(int i2, int i3, int i4, String str, int i5) {
        StatsEvent statsEventCreateStatsEvent = ConnReportHandler.getInstance().createStatsEvent();
        statsEventCreateStatsEvent.setChid((byte) i2);
        statsEventCreateStatsEvent.setType(i3);
        statsEventCreateStatsEvent.setValue(i4);
        statsEventCreateStatsEvent.setHost(str);
        statsEventCreateStatsEvent.setSubvalue(i5);
        ConnReportHandler.getInstance().add(statsEventCreateStatsEvent);
    }

    public static void statsBind(XMPushService xMPushService, PushClientsManager.ClientLoginInfo clientLoginInfo) {
        new BindTracker(xMPushService, clientLoginInfo).track();
    }

    public static void statsGslb(String str, int i2, Exception exc) {
        StatsEvent statsEventCreateStatsEvent = ConnReportHandler.getInstance().createStatsEvent();
        if (ConnReportHandler.getContext() != null && ConnReportHandler.getContext().pushService != null) {
            statsEventCreateStatsEvent.setSubvalue(Network.isConnectivity(ConnReportHandler.getContext().pushService) ? 1 : 0);
        }
        if (i2 > 0) {
            statsEventCreateStatsEvent.setType(ChannelStatsType.GSLB_REQUEST_SUCCESS.getValue());
            statsEventCreateStatsEvent.setHost(str);
            statsEventCreateStatsEvent.setValue(i2);
            ConnReportHandler.getInstance().add(statsEventCreateStatsEvent);
            return;
        }
        try {
            ConnReportAnalyser.TypeWraper typeWraperFromGslbException = ConnReportAnalyser.fromGslbException(exc);
            statsEventCreateStatsEvent.setType(typeWraperFromGslbException.type.getValue());
            statsEventCreateStatsEvent.setAnnotation(typeWraperFromGslbException.annotation);
            statsEventCreateStatsEvent.setHost(str);
            ConnReportHandler.getInstance().add(statsEventCreateStatsEvent);
        } catch (NullPointerException unused) {
        }
    }

    public static synchronized void trackEnd(int i2, int i3, String str, int i4) {
        try {
            long jCurrentTimeMillis = System.currentTimeMillis();
            int i5 = (i2 << 24) | i3;
            if (Holder.sTimeTracker.containsKey(Integer.valueOf(i5))) {
                StatsEvent statsEventCreateStatsEvent = ConnReportHandler.getInstance().createStatsEvent();
                statsEventCreateStatsEvent.setType(i3);
                statsEventCreateStatsEvent.setValue((int) (jCurrentTimeMillis - Holder.sTimeTracker.get(Integer.valueOf(i5)).longValue()));
                statsEventCreateStatsEvent.setHost(str);
                if (i4 > -1) {
                    statsEventCreateStatsEvent.setSubvalue(i4);
                }
                ConnReportHandler.getInstance().add(statsEventCreateStatsEvent);
                Holder.sTimeTracker.remove(Integer.valueOf(i3));
            } else {
                MyLog.e("stats key not found");
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public static synchronized void trackStart(int i2, int i3) {
        try {
            if (i3 < MAX_KEY_VALUE) {
                Holder.sTimeTracker.put(Integer.valueOf((i2 << 24) | i3), Long.valueOf(System.currentTimeMillis()));
            } else {
                MyLog.e("stats key should less than 16777215");
            }
        } catch (Throwable th) {
            throw th;
        }
    }
}
