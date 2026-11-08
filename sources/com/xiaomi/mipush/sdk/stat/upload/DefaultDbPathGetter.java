package com.xiaomi.mipush.sdk.stat.upload;

import android.content.Context;
import com.xiaomi.mipush.sdk.stat.db.DataBaseConfig;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DefaultDbPathGetter implements IDbPathGetter {
    @Override // com.xiaomi.mipush.sdk.stat.upload.IDbPathGetter
    public String getPath(Context context, String str) {
        return context.getDatabasePath(DataBaseConfig.DATABASE_NAME).getParentFile().getAbsolutePath();
    }

    @Override // com.xiaomi.mipush.sdk.stat.upload.IDbPathGetter
    public List<String> getPathList(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(context.getDatabasePath(DataBaseConfig.DATABASE_NAME).getParentFile().getAbsolutePath());
        return arrayList;
    }
}
