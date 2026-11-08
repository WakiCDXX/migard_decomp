package com.xiaomi.tinyData;

import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface TinyDataUploader {
    boolean checkCanUpload(ClientUploadDataItem clientUploadDataItem, String str);

    void upload(List<ClientUploadDataItem> list, String str, String str2);
}
