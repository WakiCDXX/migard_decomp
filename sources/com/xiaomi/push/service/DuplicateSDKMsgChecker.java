package com.xiaomi.push.service;

import android.os.SystemClock;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class DuplicateSDKMsgChecker {
    private static final long MAX_HOLD_TIME_MILLISEC = 60000;
    private static final Map<String, Long> sMsgFromSdkMap = new HashMap();

    public static boolean isMsgDuplicated(byte[] bArr, String str) {
        boolean z2 = false;
        if (bArr != null && bArr.length > 0 && !TextUtils.isEmpty(str)) {
            String md5 = XMStringUtils.getMd5(bArr);
            if (!TextUtils.isEmpty(md5)) {
                Map<String, Long> map = sMsgFromSdkMap;
                synchronized (map) {
                    if (map.get(md5 + str) != null) {
                        z2 = true;
                    } else {
                        map.put(md5 + str, Long.valueOf(SystemClock.elapsedRealtime()));
                    }
                    removeTimeOutMsg();
                }
            }
        }
        return z2;
    }

    private static void removeTimeOutMsg() {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        Map<String, Long> map = sMsgFromSdkMap;
        ArrayList arrayList = new ArrayList(map.size());
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            if (jElapsedRealtime - entry.getValue().longValue() > MAX_HOLD_TIME_MILLISEC) {
                arrayList.add(entry.getKey());
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            sMsgFromSdkMap.remove((String) it.next());
        }
    }
}
