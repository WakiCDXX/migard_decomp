package com.xiaomi.channelcollection;

import org.json.JSONArray;

/* JADX INFO: loaded from: classes.dex */
public class JSONArrayBytesCount extends JSONArray implements CountBytes {
    private int bytes = 2;

    @Override // com.xiaomi.channelcollection.CountBytes
    public int getBytesCount() {
        return this.bytes + (length() - 1);
    }

    @Override // org.json.JSONArray
    public JSONArray put(Object obj) {
        if (obj instanceof CountBytes) {
            this.bytes += ((CountBytes) obj).getBytesCount();
        }
        return super.put(obj);
    }
}
