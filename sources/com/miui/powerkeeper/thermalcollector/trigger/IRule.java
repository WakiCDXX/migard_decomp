package com.miui.powerkeeper.thermalcollector.trigger;

import android.os.Message;

/* JADX INFO: loaded from: classes.dex */
public interface IRule {
    String getName();

    boolean isTriggerRule(Message message);
}
