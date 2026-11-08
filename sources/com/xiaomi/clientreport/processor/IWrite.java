package com.xiaomi.clientreport.processor;

import com.xiaomi.clientreport.data.BaseClientReport;

/* JADX INFO: loaded from: classes.dex */
public interface IWrite {
    void preProcess(BaseClientReport baseClientReport);

    void process();

    void write(BaseClientReport[] baseClientReportArr);
}
