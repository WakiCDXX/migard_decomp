package com.xiaomi.mipush.sdk.stat.db;

import android.content.ContentValues;
import android.content.Context;
import com.xiaomi.mipush.sdk.stat.PushStatClientManager;
import com.xiaomi.mipush.sdk.stat.db.base.DbManager;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;

/* JADX INFO: loaded from: classes.dex */
public class MessageInsertJob extends DbManager.InsertJob {
    private String mDescription;

    public MessageInsertJob(String str, ContentValues contentValues, String str2) {
        super(str, contentValues);
        this.mDescription = str2;
    }

    public static MessageInsertJob buildInsertJob(Context context, String str, ClientUploadDataItem clientUploadDataItem) {
        byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(clientUploadDataItem);
        if (bArrConvertThriftObjectToBytes == null || bArrConvertThriftObjectToBytes.length <= 0) {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", (Integer) 0);
        contentValues.put("messageId", "");
        contentValues.put(MessageInfoContract.MessageEntry.COLUMN_NAME_MESSAGE_ITEM_ID, clientUploadDataItem.getId());
        contentValues.put(MessageInfoContract.MessageEntry.COLUMN_NAME_MESSAGE_ITEM, bArrConvertThriftObjectToBytes);
        contentValues.put("appId", PushStatClientManager.getInstance(context).getAppId());
        contentValues.put("packageName", PushStatClientManager.getInstance(context).getPackageName());
        contentValues.put(MessageInfoContract.MessageEntry.COLUMN_NAME_CREATE_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
        contentValues.put(MessageInfoContract.MessageEntry.COLUMN_NAME_UPLOAD_TIMESTAMP, (Integer) 0);
        return new MessageInsertJob(str, contentValues, "a job build to insert message to db");
    }

    @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.InsertJob, com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
    public String description() {
        return this.mDescription + "";
    }
}
