package com.xiaomi.push.stat;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class NormalReportEvent implements IReportEvent {
    public String mEventName;
    public Map<String, Object> mParamMap;

    public NormalReportEvent(String str, Map<String, Object> map) {
        this.mEventName = str;
        this.mParamMap = map;
    }

    @Override // com.xiaomi.push.stat.IReportEvent
    public String getEventName() {
        return this.mEventName;
    }

    @Override // com.xiaomi.push.stat.IReportEvent
    public Map<String, Object> getParamMap() {
        return this.mParamMap;
    }
}
