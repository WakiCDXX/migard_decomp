package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.miui.powerkeeper.logsystem.log.logcatch.MemCache;
import com.miui.powerkeeper.logsystem.log.logcatch.MemCatchData;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SamplingCatchedLog extends BaseLog {
    boolean mCatchStarted;
    private MemCatchData mData;
    ArrayList<Data> mList;

    public class Data {
        long time;

        public Data() {
        }
    }

    protected SamplingCatchedLog(Context context) {
        super(context);
        this.mList = new ArrayList<>();
        this.mCatchStarted = false;
        this.mData = new MemCatchData(getInterfaceName());
    }

    private void catchStart() {
        this.mData.catchStart();
    }

    private void finish() {
        this.mData.catchEnd();
    }

    public final void addContent(String str) {
        addContent(str, true);
    }

    public void catchLog() {
        finish();
        this.mCatchStarted = false;
        MemCache.getInstance().cacheLog(this.mData);
        this.mData = new MemCatchData(getInterfaceName());
        this.mList.clear();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getLogCatchType() {
        return 1;
    }

    public final void addContent(String str, boolean z2) {
        if (!this.mCatchStarted) {
            this.mCatchStarted = true;
            catchStart();
        }
        if (z2) {
            this.mData.addContentLn(str);
        } else {
            this.mData.addContent(str);
        }
    }
}
