package com.miui.powerkeeper.logsystem.log.logcatch;

import android.util.Log;
import com.miui.powerkeeper.utils.FileUtil;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class MemCache {
    static String COMMON_LOG_FILE_NAME = "commonlog";
    public static final int LOG_CATCH_TYPE_CATCH = 1;
    public static final int LOG_CATCH_TYPE_NO_CATCH = 0;
    public static final String TAG = "MemCache";
    private static MemCache mInstance;
    private ArrayList<MemCatchData> mList = new ArrayList<>();

    private MemCache() {
        reset();
    }

    public static synchronized MemCache getInstance() {
        try {
            if (mInstance == null) {
                mInstance = new MemCache();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mInstance;
    }

    public void cacheLog(MemCatchData memCatchData) {
        if (memCatchData.getList().size() == 0) {
            Log.d(TAG, "cacheLog failed, The MemCatchData is null.");
        } else {
            this.mList.add(memCatchData);
        }
    }

    public void commit(String str) throws Throwable {
        if (this.mList.size() == 0) {
            Log.d(TAG, "commit failed, The List is null.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<MemCatchData> it = this.mList.iterator();
        while (it.hasNext()) {
            Iterator<String> it2 = it.next().getList().iterator();
            while (it2.hasNext()) {
                sb.append(it2.next());
            }
        }
        FileUtil.writeToFile(str, sb.toString());
        reset();
    }

    public void reset() {
        this.mList.clear();
    }
}
