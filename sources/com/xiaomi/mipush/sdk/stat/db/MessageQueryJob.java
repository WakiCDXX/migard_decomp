package com.xiaomi.mipush.sdk.stat.db;

import android.content.Context;
import android.database.Cursor;
import com.xiaomi.mipush.sdk.stat.PushStatClientManager;
import com.xiaomi.mipush.sdk.stat.db.base.DbManager;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MessageQueryJob extends DbManager.BaseQueryJob<MessageInfoContract.MessageModel> {
    private int mCount;
    private String mDescription;

    public MessageQueryJob(String str, List<String> list, String str2, String[] strArr, String str3, String str4, String str5, int i2, int i3, String str6) {
        super(str, list, str2, strArr, str3, str4, str5, i2);
        this.mDescription = str6;
        this.mCount = i3;
    }

    public static MessageQueryJob newInstance(String str, int i2, int i3) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(MessageInfoContract.MessageEntry.COLUMN_NAME_ID);
        arrayList.add("messageId");
        arrayList.add(MessageInfoContract.MessageEntry.COLUMN_NAME_MESSAGE_ITEM_ID);
        arrayList.add(MessageInfoContract.MessageEntry.COLUMN_NAME_MESSAGE_ITEM);
        arrayList.add(MessageInfoContract.MessageEntry.COLUMN_NAME_CREATE_TIMESTAMP);
        arrayList.add(MessageInfoContract.MessageEntry.COLUMN_NAME_UPLOAD_TIMESTAMP);
        arrayList.add("appId");
        arrayList.add("packageName");
        arrayList.add("status");
        return new MessageQueryJob(str, arrayList, "status = ? or (status = ? and uploadTimestamp <= ?" + Constants.SEPARATOR_RIGHT_PARENTESIS, new String[]{String.valueOf(0), String.valueOf(1), String.valueOf(System.currentTimeMillis() - MessageInfoContract.TIMEOUT)}, null, null, "createTimeStamp asc", i2, i3, "a job build to query db");
    }

    @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
    public String description() {
        return this.mDescription + "  limit = " + getLimit();
    }

    @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob, com.xiaomi.mipush.sdk.stat.db.base.ISerialSchedule
    public void input(Context context, Object obj) {
        if (obj == null || !(obj instanceof int[])) {
            return;
        }
        int[] iArr = (int[]) obj;
        int i2 = iArr[0];
        int i3 = iArr[1];
        if (i2 <= 0 || i3 <= 0) {
            return;
        }
        setLimit(i2);
        this.mCount = i3;
        super.input(context, obj);
    }

    @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseQueryJob
    public void notifyResult(Context context, List<MessageInfoContract.MessageModel> list) {
        PushStatClientManager.getInstance(context).send(getDataPath(), list);
    }

    @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob, com.xiaomi.mipush.sdk.stat.db.base.ISerialSchedule
    public Object output() {
        int limit = getLimit();
        int i2 = this.mCount - limit;
        return (limit <= 0 || i2 <= 0) ? super.output() : new int[]{limit, i2};
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseQueryJob
    public MessageInfoContract.MessageModel processOneData(Context context, Cursor cursor) {
        if (cursor != null) {
            return MessageInfoContract.MessageModel.build(cursor);
        }
        return null;
    }
}
