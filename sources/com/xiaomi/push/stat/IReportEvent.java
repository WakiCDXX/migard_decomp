package com.xiaomi.push.stat;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public interface IReportEvent {
    String getEventName();

    Map<String, Object> getParamMap();
}
