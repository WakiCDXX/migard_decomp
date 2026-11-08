package com.xiaomi.push.service;

import android.content.SharedPreferences;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public class MiPushMessageDuplicate {
    private static final int MAX_MSG_CACHE_COUNT = 25;
    private static Object lock = new Object();
    private static Map<String, Queue<String>> mCachedMsgIds = new HashMap();

    public static boolean isDuplicateMessage(XMPushService xMPushService, String str, String str2) {
        synchronized (lock) {
            try {
                SharedPreferences sharedPreferences = xMPushService.getSharedPreferences("push_message_ids", 0);
                Queue<String> queue = mCachedMsgIds.get(str);
                if (queue == null) {
                    String[] strArrSplit = sharedPreferences.getString(str, "").split(",");
                    LinkedList linkedList = new LinkedList();
                    for (String str3 : strArrSplit) {
                        linkedList.add(str3);
                    }
                    mCachedMsgIds.put(str, linkedList);
                    queue = linkedList;
                }
                if (queue.contains(str2)) {
                    return true;
                }
                queue.add(str2);
                if (queue.size() > 25) {
                    queue.poll();
                }
                String strJoin = XMStringUtils.join(queue, ",");
                SharedPreferences.Editor editorEdit = sharedPreferences.edit();
                editorEdit.putString(str, strJoin);
                editorEdit.commit();
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
