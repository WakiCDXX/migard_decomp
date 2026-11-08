package com.xiaomi.push.service.clientReport;

import android.content.Context;
import com.xiaomi.clientreport.processor.DefaultEventProcessor;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MIPushEventDataProcessor extends DefaultEventProcessor {
    public MIPushEventDataProcessor(Context context) {
        super(context);
    }

    @Override // com.xiaomi.clientreport.processor.DefaultEventProcessor, com.xiaomi.clientreport.processor.IDataSend
    public void send(List<String> list) {
        PushClientReportHelper.sendData(this.mContext, list);
    }
}
