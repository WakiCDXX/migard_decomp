package com.xiaomi.push.notificationlisten;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.headsup.HeadsUpConstants;
import com.xiaomi.push.headsup.HeadsUpManager;
import com.xiaomi.push.headsup.HeadsUpNotification;
import com.xiaomi.push.headsup.HeadsUpNotificationListener;
import com.xiaomi.push.service.ButonClickRemoveNotificationHelper;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.notification.StatusBarNotificationWrapper;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class XMSFNotificationListener extends HeadsUpNotificationListener {
    public static final int LISTENER_FROM_HEADSUP = 1;
    public static final int LISTENER_FROM_SYSTEMUI = 0;
    private static volatile XMSFNotificationListener sInstance;
    private final Set<Callback> mCallbackSet = new HashSet();
    private final Context mContext;

    public interface Callback {
        void onNotificationPosted(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper, int i2);

        void onNotificationRemoved(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper, int i2, int i3);
    }

    private XMSFNotificationListener(Context context) {
        this.mContext = context.getApplicationContext();
        HeadsUpManager.getInstance(context).registerListener(-1, this);
    }

    private static int convertHeadsUpReasonToNotificationReason(int i2, HeadsUpNotification headsUpNotification) {
        switch (i2) {
            case 1:
                int i3 = headsUpNotification.clickStyleButtonIndex;
                if (i3 >= 0) {
                    if (i3 == 0) {
                        return PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_1;
                    }
                    if (i3 == 1) {
                        return PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_2;
                    }
                    if (i3 == 2) {
                        return PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_3;
                    }
                }
                return 1;
            case 2:
                return PushConstants.NOTIFICATION_REMOVED_REASON_HEADS_UP_REPLACE;
            case 3:
                return 2;
            case 4:
                return 19;
            case 5:
                return PushConstants.NOTIFICATION_REMOVED_REASON_HEADS_UP_KEYGUARD_LOCKED;
            case 6:
                return 8;
            case 7:
                return PushConstants.NOTIFICATION_REMOVED_REASON_HEADS_UP_UPDATE;
            default:
                return -1;
        }
    }

    public static XMSFNotificationListener getInstance(Context context) {
        if (sInstance == null) {
            synchronized (XMSFNotificationListener.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new XMSFNotificationListener(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public void addCallback(Callback callback) {
        if (callback != null) {
            this.mCallbackSet.add(callback);
        }
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        if (statusBarNotification != null) {
            StatusBarNotificationWrapper statusBarNotificationWrapper = new StatusBarNotificationWrapper(statusBarNotification.getId(), statusBarNotification.getNotification(), statusBarNotification.getPackageName(), String.valueOf(JavaCalls.callMethod(statusBarNotification, "getOpPkg", new Object[0])));
            Iterator<Callback> it = this.mCallbackSet.iterator();
            while (it.hasNext()) {
                it.next().onNotificationPosted(this.mContext, statusBarNotificationWrapper, 0);
            }
            if (statusBarNotification.getNotification() != null) {
                String string = statusBarNotification.getNotification().extras.getString("message_id", "");
                if (TextUtils.isEmpty(string)) {
                    return;
                }
                MyLog.w("notification: " + string + " is posted");
            }
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification, int i2) {
        if (statusBarNotification != null) {
            int iRedefineNotificationRemovedReasonAndFillActions = ButonClickRemoveNotificationHelper.redefineNotificationRemovedReasonAndFillActions(statusBarNotification, i2);
            StatusBarNotificationWrapper statusBarNotificationWrapper = new StatusBarNotificationWrapper(statusBarNotification.getId(), statusBarNotification.getNotification(), statusBarNotification.getPackageName(), String.valueOf(JavaCalls.callMethod(statusBarNotification, "getOpPkg", new Object[0])));
            Iterator<Callback> it = this.mCallbackSet.iterator();
            while (it.hasNext()) {
                it.next().onNotificationRemoved(this.mContext, statusBarNotificationWrapper, iRedefineNotificationRemovedReasonAndFillActions, 0);
            }
        }
    }

    public void removeCallback(Callback callback) {
        if (callback != null) {
            this.mCallbackSet.remove(callback);
        }
    }

    @Override // com.xiaomi.push.headsup.HeadsUpNotificationListener
    public HeadsUpConstants.ActionAfterRemoved onNotificationRemoved(HeadsUpNotification headsUpNotification, int i2) {
        if (headsUpNotification != null && headsUpNotification.actionAfterRemoved == HeadsUpConstants.ActionAfterRemoved.DidRemoved) {
            int iConvertHeadsUpReasonToNotificationReason = convertHeadsUpReasonToNotificationReason(i2, headsUpNotification);
            StatusBarNotificationWrapper statusBarNotificationWrapper = new StatusBarNotificationWrapper(headsUpNotification.id, headsUpNotification.f1747n, headsUpNotification.pkgName, PushConstants.PUSH_SERVICE_PACKAGE_NAME);
            Iterator<Callback> it = this.mCallbackSet.iterator();
            while (it.hasNext()) {
                it.next().onNotificationRemoved(this.mContext, statusBarNotificationWrapper, iConvertHeadsUpReasonToNotificationReason, 1);
            }
        }
        return HeadsUpConstants.ActionAfterRemoved.DidRemoved;
    }

    @Override // com.xiaomi.push.headsup.HeadsUpNotificationListener
    public void onNotificationPosted(HeadsUpNotification headsUpNotification) {
        if (headsUpNotification != null) {
            StatusBarNotificationWrapper statusBarNotificationWrapper = new StatusBarNotificationWrapper(headsUpNotification.id, headsUpNotification.f1747n, headsUpNotification.pkgName, PushConstants.PUSH_SERVICE_PACKAGE_NAME);
            Iterator<Callback> it = this.mCallbackSet.iterator();
            while (it.hasNext()) {
                it.next().onNotificationPosted(this.mContext, statusBarNotificationWrapper, 1);
            }
        }
    }
}
