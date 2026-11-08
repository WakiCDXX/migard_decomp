package com.xiaomi.push.pulldown;

import com.xiaomi.xmpush.thrift.XmPushActionContainer;

/* JADX INFO: loaded from: classes.dex */
public class PullDownTask {
    private static final String TAG = "PullDownTask";
    public static OnTaskChecked sOnTaskChecked;
    public static OnTaskExpired sOnTaskExpired;

    public interface OnTaskChecked {
        void onTaskCheckExpired();
    }

    public interface OnTaskExpired {
        void onTaskExpired(XmPushActionContainer xmPushActionContainer);
    }

    public static void onTaskCheckExpired() {
        OnTaskChecked onTaskChecked = sOnTaskChecked;
        if (onTaskChecked == null) {
            PullDownLog.w("PullDownTaskonTaskCheckExpired sOnTaskChecked is null");
        } else {
            onTaskChecked.onTaskCheckExpired();
        }
    }

    public static void onTaskExpired(XmPushActionContainer xmPushActionContainer) {
        OnTaskExpired onTaskExpired = sOnTaskExpired;
        if (onTaskExpired == null) {
            PullDownLog.w("PullDownTaskonTaskExpired sOnTaskExpired is null");
        } else {
            onTaskExpired.onTaskExpired(xmPushActionContainer);
        }
    }

    public static void setsOnTaskChecked(OnTaskChecked onTaskChecked) {
        sOnTaskChecked = onTaskChecked;
    }

    public static void setsOnTaskExpired(OnTaskExpired onTaskExpired) {
        sOnTaskExpired = onTaskExpired;
    }
}
