package com.xiaomi.mipush.sdk;

import android.text.TextUtils;

/* JADX INFO: compiled from: OperatePushHelper.java */
/* JADX INFO: loaded from: classes.dex */
class MessageBean {
    int count = 0;
    String messageId = "";

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MessageBean)) {
            return false;
        }
        MessageBean messageBean = (MessageBean) obj;
        return !TextUtils.isEmpty(messageBean.messageId) && messageBean.messageId.equals(this.messageId);
    }
}
