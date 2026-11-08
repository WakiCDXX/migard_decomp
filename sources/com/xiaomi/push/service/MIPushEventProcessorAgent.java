package com.xiaomi.push.service;

import android.content.Context;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class MIPushEventProcessorAgent {
    private static EventProcessorListener sEventProcessorListener;
    private static PullDownListener sListener;

    public interface EventProcessorListener {
        Map<String, String> fillParamsInACK(Context context, XmPushActionContainer xmPushActionContainer);

        void handleMessage(Context context, XmPushActionContainer xmPushActionContainer);

        void setAppNotificationPermission(Context context, XmPushActionContainer xmPushActionContainer, XmPushActionNotification xmPushActionNotification);

        boolean shouldHandleMessage(Context context, XmPushActionContainer xmPushActionContainer, boolean z2);
    }

    public interface PullDownListener {
        void clearMessage(XmPushActionContainer xmPushActionContainer);

        void clearMessage(String str);

        boolean handleReceiveMessage(XmPushActionContainer xmPushActionContainer);
    }

    public static void clearMessage(XmPushActionContainer xmPushActionContainer) {
        PullDownListener pullDownListener = sListener;
        if (pullDownListener == null || xmPushActionContainer == null) {
            MyLog.w("pepa clearMessage is null");
        } else {
            pullDownListener.clearMessage(xmPushActionContainer);
        }
    }

    public static Map<String, String> fillParamsInACK(Context context, XmPushActionContainer xmPushActionContainer) {
        EventProcessorListener eventProcessorListener = sEventProcessorListener;
        if (eventProcessorListener != null && xmPushActionContainer != null) {
            return eventProcessorListener.fillParamsInACK(context, xmPushActionContainer);
        }
        MyLog.w("pepa listener or container is null");
        return null;
    }

    public static void handleMessage(Context context, XmPushActionContainer xmPushActionContainer) {
        EventProcessorListener eventProcessorListener = sEventProcessorListener;
        if (eventProcessorListener == null || xmPushActionContainer == null) {
            MyLog.w("handle msg wrong");
        } else {
            eventProcessorListener.handleMessage(context, xmPushActionContainer);
        }
    }

    public static boolean handleReceiveMessage(XmPushActionContainer xmPushActionContainer) {
        PullDownListener pullDownListener = sListener;
        if (pullDownListener != null && xmPushActionContainer != null) {
            return pullDownListener.handleReceiveMessage(xmPushActionContainer);
        }
        MyLog.w("pepa handleReceiveMessage is null");
        return false;
    }

    public static void setAppNotificationPermission(Context context, XmPushActionContainer xmPushActionContainer, XmPushActionNotification xmPushActionNotification) {
        EventProcessorListener eventProcessorListener = sEventProcessorListener;
        if (eventProcessorListener == null) {
            MyLog.e("The Listener of EventProcessor must be set. Please check extension plugin initialization.");
        } else {
            eventProcessorListener.setAppNotificationPermission(context, xmPushActionContainer, xmPushActionNotification);
        }
    }

    public static void setEventProcessorListener(EventProcessorListener eventProcessorListener) {
        sEventProcessorListener = eventProcessorListener;
    }

    public static void setPullDownListener(PullDownListener pullDownListener) {
        sListener = pullDownListener;
    }

    public static boolean shouldHandleMessage(Context context, XmPushActionContainer xmPushActionContainer, boolean z2) {
        EventProcessorListener eventProcessorListener = sEventProcessorListener;
        if (eventProcessorListener != null && xmPushActionContainer != null) {
            return eventProcessorListener.shouldHandleMessage(context, xmPushActionContainer, z2);
        }
        MyLog.w("pepa judement listener or container is null");
        return false;
    }

    public static void clearMessage(String str) {
        PullDownListener pullDownListener = sListener;
        if (pullDownListener != null && str != null) {
            pullDownListener.clearMessage(str);
        } else {
            MyLog.w("pepa clearMessage is null");
        }
    }
}
