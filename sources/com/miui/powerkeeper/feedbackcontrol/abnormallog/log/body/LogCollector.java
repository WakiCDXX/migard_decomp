package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.storage.Cache;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.LogPrinter;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.PublicSpace;

/* JADX INFO: loaded from: classes.dex */
public abstract class LogCollector {
    protected final Context mContext;
    protected final Cache mCache = Cache.getCache();
    protected final LogPrinter mLog = LogPrinter.getLogPrinter();
    protected final PublicSpace mPublicSpace = PublicSpace.getInstance();

    public LogCollector(Context context) {
        this.mContext = context;
    }

    public abstract void collect();

    public abstract void logout();
}
