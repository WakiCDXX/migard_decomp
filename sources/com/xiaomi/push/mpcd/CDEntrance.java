package com.xiaomi.push.mpcd;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class CDEntrance {
    public static void start(Context context) {
        JobController.getInstance(context).schedulerJob();
    }
}
