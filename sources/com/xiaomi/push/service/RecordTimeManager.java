package com.xiaomi.push.service;

import android.os.SystemClock;
import android.text.TextUtils;
import com.xiaomi.push.service.clientReport.ReportConstants;
import com.xiaomi.push.stat.NormalReportEvent;
import com.xiaomi.push.stat.Reporter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes.dex */
public class RecordTimeManager {
    public static final int DELAY_CLEAR_TIME = 15000;
    private static final int MAX_LIVE_TIME = 10000;
    private final ConcurrentHashMap<String, TimeRecord> timeRecordMap = new ConcurrentHashMap<>();

    public static class ClearJob extends XMPushService.Job {
        public ClearJob() {
            super(17);
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public String getDesc() {
            return "RecordTimeManager clear";
        }

        @Override // com.xiaomi.push.service.XMPushService.Job
        public void process() {
            RecordTimeManager.get().clear();
        }
    }

    private static class SingletonHolder {
        private static final RecordTimeManager INSTANCE = new RecordTimeManager();

        private SingletonHolder() {
        }
    }

    private static class TimeRecord {
        long appReceiveTime;
        long receiveTime;
        long sendTime;
        long xmsfVer;

        private TimeRecord() {
        }

        public long getBroadcastTime() {
            long j2 = this.appReceiveTime;
            long j3 = this.sendTime;
            if (j2 > j3) {
                return j2 - j3;
            }
            return 0L;
        }

        public long getHandleTime() {
            long j2 = this.sendTime;
            long j3 = this.receiveTime;
            if (j2 > j3) {
                return j2 - j3;
            }
            return 0L;
        }
    }

    public static RecordTimeManager get() {
        return SingletonHolder.INSTANCE;
    }

    private void uploadData(String str, TimeRecord timeRecord) {
        if (TextUtils.isEmpty(str) || timeRecord == null) {
            return;
        }
        HashMap map = new HashMap();
        map.put("xmsfVC", Long.valueOf(timeRecord.xmsfVer));
        map.put("packetId", str);
        map.put("pTime", Long.valueOf(timeRecord.getHandleTime()));
        map.put("bTime", Long.valueOf(timeRecord.getBroadcastTime()));
        Reporter.getInstance().trackEvent(new NormalReportEvent(ReportConstants.EVENT_MSG_PROCESS_TIME, map));
    }

    public void clear() {
        if (this.timeRecordMap.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<String, TimeRecord>> it = this.timeRecordMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, TimeRecord> next = it.next();
            if (next == null || next.getValue() == null) {
                it.remove();
            } else {
                TimeRecord value = next.getValue();
                if (Math.abs(SystemClock.elapsedRealtime() - value.receiveTime) > 10000) {
                    uploadData(next.getKey(), value);
                    it.remove();
                }
            }
        }
    }

    public void recordAppReceiveTime(String str, long j2) {
        TimeRecord timeRecordRemove = this.timeRecordMap.remove(str);
        if (timeRecordRemove != null) {
            timeRecordRemove.appReceiveTime = j2;
            uploadData(str, timeRecordRemove);
        }
    }

    public void recordMsgHandleTime(String str, long j2) {
        TimeRecord timeRecord = this.timeRecordMap.get(str);
        if (timeRecord != null) {
            timeRecord.sendTime = j2;
        }
    }

    public void recordMsgReceiveTime(String str, long j2, long j3) {
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.xmsfVer = j3;
        timeRecord.receiveTime = j2;
        this.timeRecordMap.put(str, timeRecord);
    }
}
