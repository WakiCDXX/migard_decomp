package com.xiaomi.push.service;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.smack.XMPPException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class MIPushClientManager {
    private static final int MAX_PENDING_MESSAGE = 50;
    private static final Map<String, byte[]> pendingRegisterationRequests = new HashMap();
    private static ArrayList<Pair<String, byte[]>> pendingMessages = new ArrayList<>();

    public static void addPendingMessages(String str, byte[] bArr) {
        synchronized (pendingMessages) {
            try {
                pendingMessages.add(new Pair<>(str, bArr));
                if (pendingMessages.size() > 50) {
                    pendingMessages.remove(0);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void notifyError(Context context, String str, byte[] bArr, int i2, String str2) {
        Intent intent = new Intent(PushConstants.MIPUSH_ACTION_ERROR);
        intent.setPackage(str);
        intent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArr);
        intent.putExtra(PushConstants.MIPUSH_EXTRA_ERROR_CODE, i2);
        intent.putExtra(PushConstants.MIPUSH_EXTRA_ERROR_MSG, str2);
        context.sendBroadcast(intent, MIPushHelper.getReceiverPermission(str));
    }

    public static void notifyRegisterError(Context context, int i2, String str) {
        Map<String, byte[]> map = pendingRegisterationRequests;
        synchronized (map) {
            try {
                for (String str2 : map.keySet()) {
                    MyLog.w("notify registration error. " + str2);
                    notifyError(context, str2, pendingRegisterationRequests.get(str2), i2, str);
                }
                pendingRegisterationRequests.clear();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void pendingRegisterApp(String str, byte[] bArr) {
        Map<String, byte[]> map = pendingRegisterationRequests;
        synchronized (map) {
            MyLog.w("pending registration request. " + str);
            map.put(str, bArr);
        }
    }

    public static void processPendingMessages(XMPushService xMPushService) {
        ArrayList<Pair<String, byte[]>> arrayList;
        try {
            synchronized (pendingMessages) {
                arrayList = pendingMessages;
                pendingMessages = new ArrayList<>();
            }
            boolean zIsUIThread = ToolUtils.isUIThread();
            for (Pair<String, byte[]> pair : arrayList) {
                MIPushHelper.sendPacket(xMPushService, (String) pair.first, (byte[]) pair.second);
                if (!zIsUIThread) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException unused) {
                    }
                }
            }
        } catch (XMPPException e2) {
            MyLog.e("meet error when process pending message. " + e2);
            xMPushService.disconnect(10, e2);
        }
    }

    public static void processPendingRegistrationRequest(XMPushService xMPushService, boolean z2) {
        try {
            Map<String, byte[]> map = pendingRegisterationRequests;
            synchronized (map) {
                try {
                    for (String str : map.keySet()) {
                        MyLog.w("processing pending registration request. " + str);
                        MIPushHelper.sendPacket(xMPushService, str, pendingRegisterationRequests.get(str));
                        if (z2 && !ToolUtils.isUIThread()) {
                            try {
                                Thread.sleep(200L);
                            } catch (Exception unused) {
                            }
                        }
                    }
                    pendingRegisterationRequests.clear();
                } catch (Throwable th) {
                    throw th;
                }
            }
        } catch (XMPPException e2) {
            MyLog.e("fail to deal with pending register request. " + e2);
            xMPushService.disconnect(10, e2);
        }
    }
}
