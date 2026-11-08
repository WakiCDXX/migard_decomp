package com.xiaomi.push.clientreport;

import android.content.Context;
import android.content.Intent;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.smack.Connection;

/* JADX INFO: loaded from: classes.dex */
public class PowerStatsHelper {
    public static final String TAG = "Push-PowerStats";
    private static DataReceiveListener receiveListener;
    private static DataSendListener sendListener;

    public static void handleScreenChanged(Context context, Intent intent) {
        log("handleScreenChanged");
        if (shouldSampling(context) && intent != null) {
            if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                PowerStatsSP.onScreenOn(context, System.currentTimeMillis());
            } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                PowerStatsSP.onScreenOff(context, System.currentTimeMillis());
            }
        }
    }

    public static boolean isScreenOn(Context context) {
        return DeviceInfo.isScreenOn(context);
    }

    static void log(String str) {
        ConnectionQualityStatsHelper.log(TAG, str);
    }

    public static void onPing(Context context) {
        log("onPing");
        if (shouldSampling(context)) {
            PowerStatsSP.onPing(context, System.currentTimeMillis(), isScreenOn(context));
        }
    }

    public static void onPong(Context context) {
        log("onPong");
        if (shouldSampling(context)) {
            PowerStatsSP.onPong(context, System.currentTimeMillis(), isScreenOn(context));
        }
    }

    public static void onReceiveMsg(Context context) {
        log("onReceiveMsg");
        if (shouldSampling(context)) {
            PowerStatsSP.onReceiveMsg(context, System.currentTimeMillis(), isScreenOn(context));
        }
    }

    public static void onSendMsg(Context context) {
        log("onSendMsg");
        if (shouldSampling(context)) {
            PowerStatsSP.onSendMsg(context, System.currentTimeMillis(), isScreenOn(context));
        }
    }

    private static boolean shouldSampling(Context context) {
        return ConnectionQualityStatsHelper.shouldSampling(context);
    }

    public static void startStats(Context context, Connection connection) {
        if (shouldSampling(context)) {
            if (receiveListener == null) {
                receiveListener = new DataReceiveListener(context);
            }
            if (sendListener == null) {
                sendListener = new DataSendListener(context);
            }
            DataReceiveListener dataReceiveListener = receiveListener;
            connection.addPacketListener(dataReceiveListener, dataReceiveListener);
            DataSendListener dataSendListener = sendListener;
            connection.addPacketSendingListener(dataSendListener, dataSendListener);
            log("startStats");
        }
    }

    public static void stopStats(Context context, Connection connection) {
        DataReceiveListener dataReceiveListener = receiveListener;
        if (dataReceiveListener != null) {
            connection.removePacketListener(dataReceiveListener);
            receiveListener = null;
        }
        DataSendListener dataSendListener = sendListener;
        if (dataSendListener != null) {
            connection.removePacketSendingListener(dataSendListener);
            sendListener = null;
        }
        log("stopStats");
    }
}
