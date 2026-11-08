package com.xiaomi.mipush.sdk.stat.db;

import android.content.Context;
import com.xiaomi.mipush.sdk.stat.db.base.BaseDbHelper;
import com.xiaomi.mipush.sdk.stat.db.base.BaseDbHelperFactory;

/* JADX INFO: loaded from: classes.dex */
public class MessageDbHelperFactory extends BaseDbHelperFactory {
    @Override // com.xiaomi.mipush.sdk.stat.db.base.BaseDbHelperFactory
    public BaseDbHelper getDbHelper(Context context, String str) {
        return MessageDbHelper.newInstance(context, str);
    }
}
