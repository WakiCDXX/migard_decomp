package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.Tools;

/* JADX INFO: loaded from: classes.dex */
public class Timestamp extends LogCollector {
    private final String TAG;

    public Timestamp(Context context) {
        super(context);
        this.TAG = "TimestampCollector";
        this.mLog.d("TimestampCollector", "create");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
        this.mLog.d("TimestampCollector", "collect");
        this.mCache.insert(Tools.getTimestamp() + ":  ");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("TimestampCollector", "logout");
    }
}
