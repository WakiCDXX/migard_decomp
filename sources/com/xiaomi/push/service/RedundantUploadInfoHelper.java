package com.xiaomi.push.service;

import com.xiaomi.xmpush.thrift.PushMetaInfo;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class RedundantUploadInfoHelper {
    public static PushMetaInfo cleanRedundantInfoForAck(PushMetaInfo pushMetaInfo) {
        Map<String, String> map;
        if (pushMetaInfo != null && (map = pushMetaInfo.internal) != null) {
            map.remove("score_info");
        }
        return pushMetaInfo;
    }
}
