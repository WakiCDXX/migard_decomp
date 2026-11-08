package com.xiaomi.mipush.sdk.stat.db;

import android.content.Context;
import com.xiaomi.mipush.sdk.stat.db.base.DbManager;
import com.xiaomi.mipush.sdk.stat.util.FileUtil;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ScheduleQueryAndUploadJob extends MessageCountQueryJob {
    public ScheduleQueryAndUploadJob(String str, List<String> list, String str2, String[] strArr, String str3, String str4, String str5, int i2) {
        super(str, list, str2, strArr, str3, str4, str5, i2, "job to schedule upload jobs");
    }

    public static ScheduleQueryAndUploadJob getScheduleJob(String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("count(*)");
        return new ScheduleQueryAndUploadJob(str, arrayList, "status = ? or (status = ? and uploadTimestamp <= ?" + Constants.SEPARATOR_RIGHT_PARENTESIS, new String[]{String.valueOf(0), String.valueOf(1), String.valueOf(System.currentTimeMillis() - MessageInfoContract.TIMEOUT)}, null, null, null, 0);
    }

    @Override // com.xiaomi.mipush.sdk.stat.db.MessageCountQueryJob, com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseQueryJob
    public void notifyResult(Context context, List<Long> list) {
        int suitableLimit = FileUtil.getSuitableLimit(context);
        long jLongValue = list.get(0).longValue();
        long j2 = suitableLimit;
        if (jLongValue <= j2) {
            int i2 = (int) jLongValue;
            DbManager.getInstance(context).execDelay(MessageQueryJob.newInstance(getDataPath(), i2, i2), 0);
            return;
        }
        MessageQueryJob messageQueryJob = null;
        MessageQueryJob messageQueryJob2 = null;
        long j3 = j2;
        while (j3 < jLongValue) {
            MessageQueryJob messageQueryJobNewInstance = MessageQueryJob.newInstance(getDataPath(), suitableLimit, (int) jLongValue);
            if (messageQueryJob2 == null) {
                messageQueryJob2 = messageQueryJobNewInstance;
            }
            if (messageQueryJob != null) {
                messageQueryJob.append(messageQueryJobNewInstance);
            }
            j3 += j2;
            messageQueryJob = messageQueryJobNewInstance;
        }
        int i3 = (int) ((jLongValue - j3) + j2);
        MessageQueryJob messageQueryJobNewInstance2 = MessageQueryJob.newInstance(getDataPath(), i3, i3);
        if (messageQueryJob != null) {
            messageQueryJob.append(messageQueryJobNewInstance2);
        }
        DbManager.getInstance(context).exec(messageQueryJob2);
    }
}
