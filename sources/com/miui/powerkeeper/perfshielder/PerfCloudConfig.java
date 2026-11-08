package com.miui.powerkeeper.perfshielder;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public abstract class PerfCloudConfig {
    public String mBucketName;

    public PerfCloudConfig(String str) {
        this.mBucketName = str;
    }

    public abstract void parseResult(Context context, String str, String str2, String str3);

    public abstract void updateResult(Context context, String str, String str2);
}
