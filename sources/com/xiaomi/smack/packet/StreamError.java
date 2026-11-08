package com.xiaomi.smack.packet;

import com.xiaomi.push.mpcd.Constants;

/* JADX INFO: loaded from: classes.dex */
public class StreamError {
    private String code;

    public StreamError(String str) {
        this.code = str;
    }

    public String getCode() {
        return this.code;
    }

    public String toString() {
        return "stream:error (" + this.code + Constants.SEPARATOR_RIGHT_PARENTESIS;
    }
}
