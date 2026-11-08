package com.miui.powerkeeper.logsystem.log.logcatch;

import android.content.Context;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class FileCatch {
    public static String FILE_NAME_SCENE_ID_LABEL = "s";
    public static String FILE_NAME_TIME_LABEL = "t";
    private static String LOG_PATH = "logsystem/";
    private static FileCatch gInstance;
    private Context mContext;
    private String mFile;

    private FileCatch(Context context) {
        this.mContext = context.getApplicationContext();
        initFile();
    }

    private String getFileRootPath() {
        return this.mContext.getFilesDir().getAbsolutePath() + "/";
    }

    public static synchronized FileCatch getInstance(Context context) {
        try {
            if (gInstance == null) {
                gInstance = new FileCatch(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return gInstance;
    }

    private void initFile() {
        String str = getFileRootPath() + LOG_PATH;
        File file = new File(str);
        if (file.exists() || file.mkdir()) {
            this.mFile = str;
        }
    }

    public String getFilePath(int i2, long j2, BaseLog baseLog) {
        String str = this.mFile + baseLog.getInterfaceName();
        if (baseLog.getLogCatchType() == 1) {
            str = this.mFile + MemCache.COMMON_LOG_FILE_NAME;
        }
        return str + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + FILE_NAME_TIME_LABEL + j2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + FILE_NAME_SCENE_ID_LABEL + i2;
    }
}
