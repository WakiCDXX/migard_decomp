package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.onetrack.api.d;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class ClickToStartActivityReporter {
    private static final int START_ACTIVITY_RESULT_BUSINESS_WITHOUT_EFFECT = 2;
    private static final int START_ACTIVITY_RESULT_CONTENT_EMPTY = 4;
    private static final int START_ACTIVITY_RESULT_DECRYPT_FAILED = 3;
    private static final int START_ACTIVITY_RESULT_MESSAGE_NOT_ENCRYPT = 1;
    private static final int START_ACTIVITY_RESULT_REGIST_INFO_EMPTY = 6;
    private static final int START_ACTIVITY_RESULT_REGIST_INFO_INCORRECT = 5;
    private static final int START_ACTIVITY_RESULT_REGIST_INFO_INVALID = 7;
    private static final int START_ACTIVITY_RESULT_SUCCESS = 0;
    private static volatile ClickToStartActivityReporter sInstance;
    private final Context mContext;

    private ClickToStartActivityReporter(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static void businessWithoutEffect(Context context, XmPushActionContainer xmPushActionContainer, boolean z2) {
        getInstance(context).reportClickToStartActivityResult(xmPushActionContainer, 2, z2);
    }

    public static void dencryptMessageFailed(Context context, XmPushActionContainer xmPushActionContainer, boolean z2) {
        getInstance(context).reportClickToStartActivityResult(xmPushActionContainer, 3, z2);
    }

    public static void failedWithMessageNotEncrypt(Context context, XmPushActionContainer xmPushActionContainer, boolean z2) {
        getInstance(context).reportClickToStartActivityResult(xmPushActionContainer, 1, z2);
    }

    private static ClickToStartActivityReporter getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ClickToStartActivityReporter.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new ClickToStartActivityReporter(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public static void messageContentEmpty(Context context, XmPushActionContainer xmPushActionContainer, boolean z2) {
        getInstance(context).reportClickToStartActivityResult(xmPushActionContainer, 4, z2);
    }

    public static void registerInfoIncorrect(Context context, XmPushActionContainer xmPushActionContainer, boolean z2) {
        AppInfoHolder appInfoHolder = AppInfoHolder.getInstance(context);
        if (TextUtils.isEmpty(appInfoHolder.getRegID()) || TextUtils.isEmpty(appInfoHolder.getRegSecret())) {
            getInstance(context).reportClickToStartActivityResult(xmPushActionContainer, 6, z2);
        } else if (appInfoHolder.invalidated()) {
            getInstance(context).reportClickToStartActivityResult(xmPushActionContainer, 7, z2);
        } else {
            getInstance(context).reportClickToStartActivityResult(xmPushActionContainer, 5, z2);
        }
    }

    private void reportClickToStartActivityResult(XmPushActionContainer xmPushActionContainer, int i2, boolean z2) {
        if (MIUIUtils.isXMSF(this.mContext) || !MIUIUtils.isMIUI() || xmPushActionContainer == null || xmPushActionContainer.action != ActionType.SendMessage || xmPushActionContainer.getMetaInfo() == null || !z2) {
            return;
        }
        MyLog.w("click to start activity result:" + String.valueOf(i2));
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification(xmPushActionContainer.getMetaInfo().getId(), false);
        xmPushActionNotification.setType(NotificationType.SDK_START_ACTIVITY.value);
        xmPushActionNotification.setAppId(xmPushActionContainer.getAppid());
        xmPushActionNotification.setPackageName(xmPushActionContainer.packageName);
        HashMap map = new HashMap();
        xmPushActionNotification.extra = map;
        map.put(d.L, String.valueOf(i2));
        PushServiceClient.getInstance(this.mContext).sendMessage(xmPushActionNotification, ActionType.Notification, false, false, null, true, xmPushActionContainer.packageName, xmPushActionContainer.appid, true, false);
    }

    public static void startActivity(Context context, XmPushActionContainer xmPushActionContainer) {
        getInstance(context).reportClickToStartActivityResult(xmPushActionContainer, 0, true);
    }
}
