package com.xiaomi.mipush.sdk.stat.util;

import android.content.Context;
import android.system.Os;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.mipush.sdk.stat.db.DataBaseConfig;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.xmpush.thrift.ConfigKey;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class FileUtil {
    public static long getFileSize(String str) {
        try {
            if (new File(str).exists()) {
                return Os.stat(str).st_size;
            }
            return 0L;
        } catch (Exception e2) {
            MyLog.e(e2);
            return 0L;
        }
    }

    public static int getSuitableLimit(Context context) {
        int intValue = OnlineConfig.getInstance(context).getIntValue(ConfigKey.StatDataProcessFrequency.getValue(), DataBaseConfig.DEFAULT_NUM);
        long jFreeMemory = Runtime.getRuntime().freeMemory() >> 20;
        if (jFreeMemory <= 0) {
            jFreeMemory = 1;
        }
        return (int) (jFreeMemory * ((long) intValue));
    }
}
