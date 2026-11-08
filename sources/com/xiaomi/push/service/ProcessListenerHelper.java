package com.xiaomi.push.service;

import android.content.Context;
import com.xiaomi.push.scene.ScenePushManager;

/* JADX INFO: loaded from: classes.dex */
public class ProcessListenerHelper {
    public static void onForegroundActivitiesChanged(Context context, int i2, int i3, boolean z2) {
        ScenePushManager.onForegroundStateChanged(context, i2, i3, z2);
    }

    public static void onProcessDied(Context context, int i2, int i3) {
        ScenePushManager.onForegroundStateChanged(context, i2, i3, false);
    }
}
