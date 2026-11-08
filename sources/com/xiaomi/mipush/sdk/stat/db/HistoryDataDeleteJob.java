package com.xiaomi.mipush.sdk.stat.db;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.mipush.sdk.stat.PushStatClientManager;
import com.xiaomi.mipush.sdk.stat.db.base.DbManager;
import com.xiaomi.mipush.sdk.stat.util.FileUtil;
import com.xiaomi.onetrack.api.e;

/* JADX INFO: loaded from: classes.dex */
public class HistoryDataDeleteJob extends MessageDeleteJob {
    public HistoryDataDeleteJob(String str, String str2, String[] strArr, String str3) {
        super(str, str2, strArr, str3);
    }

    public static HistoryDataDeleteJob deleteHistoryJob(Context context, String str, int i2) {
        MyLog.i("delete  messages when db size is too bigger");
        String tableName = DbManager.getInstance(context).getTableName(str);
        if (TextUtils.isEmpty(tableName)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("rowDataId in (select ");
        sb.append("rowDataId from " + tableName);
        sb.append(" order by createTimeStamp asc");
        sb.append(" limit ?)");
        return new HistoryDataDeleteJob(str, sb.toString(), new String[]{String.valueOf(i2)}, "a job build to delete history message");
    }

    private void setLimit(long j2) {
        String[] strArr = this.mWhereValues;
        if (strArr == null || strArr.length <= 0) {
            return;
        }
        strArr[0] = String.valueOf(j2);
    }

    @Override // com.xiaomi.mipush.sdk.stat.db.MessageDeleteJob, com.xiaomi.mipush.sdk.stat.db.base.DbManager.DeleteJob, com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
    public String description() {
        return this.mDescription + " " + this.mWhereValues[0];
    }

    @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob, com.xiaomi.mipush.sdk.stat.db.base.ISerialSchedule
    public void input(Context context, Object obj) {
        if (obj instanceof Long) {
            long jLongValue = ((Long) obj).longValue();
            long fileSize = FileUtil.getFileSize(getDataPath());
            long j2 = DataBaseConfig.MAX_DB_SIZE;
            if (fileSize <= j2) {
                MyLog.i("db size is suitable");
                return;
            }
            long j3 = (long) ((((fileSize - j2) * 1.2d) / j2) * jLongValue);
            setLimit(j3);
            PushStatClientManager.getInstance(context).record("begin delete " + j3 + "noUpload messages , because db size is " + fileSize + e.f1070a);
            super.input(context, obj);
        }
    }
}
