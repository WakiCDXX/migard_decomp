package com.ot.pubsub.util;

import android.text.TextUtils;
import android.util.LruCache;

/* JADX INFO: loaded from: classes.dex */
final class h extends LruCache<String, g.a> {
    h(int i2) {
        super(i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.util.LruCache
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int sizeOf(String str, g.a aVar) {
        if (aVar == null || TextUtils.isEmpty(aVar.f717a)) {
            return 0;
        }
        return aVar.f717a.length();
    }
}
