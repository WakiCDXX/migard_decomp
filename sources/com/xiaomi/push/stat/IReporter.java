package com.xiaomi.push.stat;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public interface IReporter {
    void trackEvent(IReportEvent iReportEvent);

    void trackEvent(String str, Map<String, Object> map);
}
