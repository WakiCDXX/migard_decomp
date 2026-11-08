package com.xiaomi.push.stat;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class Reporter implements IReporter {
    private IReporter mReporter;

    private static class InstanceHolder {
        private static Reporter sInstance = new Reporter();

        private InstanceHolder() {
        }
    }

    public static Reporter getInstance() {
        return InstanceHolder.sInstance;
    }

    public void init(IReporter iReporter) {
        this.mReporter = iReporter;
    }

    @Override // com.xiaomi.push.stat.IReporter
    public void trackEvent(IReportEvent iReportEvent) {
        IReporter iReporter = this.mReporter;
        if (iReporter != null) {
            iReporter.trackEvent(iReportEvent);
        }
    }

    public void trackRDEvent(String str, Object obj) {
        if (this.mReporter != null) {
            HashMap map = new HashMap();
            map.put("key", str);
            map.put("package", obj != null ? String.valueOf(obj) : "");
            this.mReporter.trackEvent("rd_event", map);
        }
    }

    private Reporter() {
    }

    @Override // com.xiaomi.push.stat.IReporter
    public void trackEvent(String str, Map<String, Object> map) {
        IReporter iReporter = this.mReporter;
        if (iReporter != null) {
            iReporter.trackEvent(str, map);
        }
    }
}
