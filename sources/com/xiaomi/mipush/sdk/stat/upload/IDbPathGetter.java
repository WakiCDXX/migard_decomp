package com.xiaomi.mipush.sdk.stat.upload;

import android.content.Context;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IDbPathGetter {
    String getPath(Context context, String str);

    List<String> getPathList(Context context);
}
