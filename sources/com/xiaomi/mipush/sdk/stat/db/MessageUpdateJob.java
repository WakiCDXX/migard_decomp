package com.xiaomi.mipush.sdk.stat.db;

import android.content.ContentValues;
import com.xiaomi.mipush.sdk.stat.db.base.DbManager;
import com.xiaomi.mipush.sdk.stat.upload.UploadDataHelper;

/* JADX INFO: loaded from: classes.dex */
public class MessageUpdateJob extends DbManager.UpdateJob {
    private String mDescription;

    public MessageUpdateJob(String str, String str2, String[] strArr, ContentValues contentValues, String str3) {
        super(str, str2, strArr, contentValues);
        this.mDescription = str3;
    }

    public static MessageUpdateJob updateItemStatusAfterAck(String str, String str2, boolean z2) {
        String[] strArr = {str2};
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", Integer.valueOf(z2 ? 2 : 0));
        return new MessageUpdateJob(str, "messageId = ?", strArr, contentValues, "a job build to update message status after receive ack");
    }

    public static MessageUpdateJob updateMessageId(String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        String rowId = UploadDataHelper.getRowId(str2);
        sb.append("rowDataId = ?");
        String[] strArr = {rowId};
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageInfoContract.MessageEntry.COLUMN_NAME_UPLOAD_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
        contentValues.put("status", (Integer) 1);
        contentValues.put("messageId", str3);
        contentValues.put(MessageInfoContract.MessageEntry.COLUMN_NAME_MESSAGE_ITEM_ID, str2);
        return new MessageUpdateJob(str, sb.toString(), strArr, contentValues, "a job build to update message upload time and messageId");
    }

    @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.UpdateJob, com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
    public String description() {
        return this.mDescription + "";
    }
}
