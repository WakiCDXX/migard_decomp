package com.xiaomi.push.mpcd;

import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;

/* JADX INFO: loaded from: classes.dex */
public interface CDActionProvider {
    String getRegSecret();

    void uploadNotification(XmPushActionNotification xmPushActionNotification, ActionType actionType, PushMetaInfo pushMetaInfo);
}
