package com.xiaomi.push.service.profile;

import android.util.Pair;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes.dex */
public class MessageProfiling {
    private static Vector<Pair<String, Long>> sSentPerfDatas = new Vector<>();
    private static ConcurrentHashMap<String, Long> sSendingMessages = new ConcurrentHashMap<>();

    public static String getPrefString() {
        StringBuilder sb = new StringBuilder();
        synchronized (sSentPerfDatas) {
            for (int i2 = 0; i2 < sSentPerfDatas.size(); i2++) {
                try {
                    Pair<String, Long> pairElementAt = sSentPerfDatas.elementAt(i2);
                    sb.append((String) pairElementAt.first);
                    sb.append(":");
                    sb.append(pairElementAt.second);
                    if (i2 < sSentPerfDatas.size() - 1) {
                        sb.append(";");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            sSentPerfDatas.clear();
        }
        return sb.toString();
    }

    public static void onReceiveSentAck(String str, String str2) {
        if (sSendingMessages.containsKey(str)) {
            sSentPerfDatas.add(new Pair<>(str2, Long.valueOf(System.currentTimeMillis() - sSendingMessages.get(str).longValue())));
            sSendingMessages.remove(str);
        }
    }

    public static void onSendingMessage(String str) {
        sSendingMessages.put(str, Long.valueOf(System.currentTimeMillis()));
    }
}
