package com.xiaomi.xmpush.thrift;

import android.app.NotificationChannel;
import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.MobileStatusUtils;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationManagerHelper;
import g0.c;
import g0.d;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.XmPushTBinaryProtocol;

/* JADX INFO: loaded from: classes.dex */
public class XmPushThriftSerializeUtils {
    public static final String CHANNEL_STATUS = "cs";
    private static final int MASK_DEVICE_CHARGING = 4;
    private static final int MASK_DEVICE_N_BELONG_TO_APP = 16;
    private static final int MASK_DEVICE_N_CHANNEL_CLOSE = 64;
    private static final int MASK_DEVICE_N_CHANNEL_OPEN = 32;
    private static final int MASK_DEVICE_SCREEN_LOCKED = 8;
    private static final int MASK_GEO_PASS = 1;
    private static final int MASK_GEO_RECEIVE = 4;
    private static final int MASK_GEO_SHOW = 2;
    private static final int MASK_TYPE_SHIELD = 16;

    public static <T extends TBase<T, ?>> void convertByteArrayToThriftObject(T t2, byte[] bArr) throws TException {
        if (bArr == null) {
            throw new TException("the message byte is empty.");
        }
        new c(new XmPushTBinaryProtocol.Factory(true, true, bArr.length)).a(t2, bArr);
    }

    public static <T extends TBase<T, ?>> byte[] convertThriftObjectToBytes(T t2) {
        if (t2 == null) {
            return null;
        }
        try {
            return new d(new TBinaryProtocol.Factory()).a(t2);
        } catch (TException e2) {
            MyLog.e("convertThriftObjectToBytes catch TException.", e2);
            return null;
        }
    }

    private static int getChannelStatus(Context context, String str, String str2) {
        NotificationManagerHelper notificationManagerHelperFrom;
        NotificationChannel notificationChannel;
        if (context == null || TextUtils.isEmpty(str) || (notificationManagerHelperFrom = NotificationManagerHelper.from(context, str)) == null || (notificationChannel = notificationManagerHelperFrom.getNotificationChannel(notificationManagerHelperFrom.getAppChannelId(str2))) == null) {
            return 0;
        }
        return notificationChannel.getImportance() != 0 ? 32 : 64;
    }

    public static short getDeviceStatus(Context context, XmPushActionContainer xmPushActionContainer) {
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        return getDeviceStatus(context, xmPushActionContainer.packageName, (metaInfo == null || metaInfo.getExtra() == null) ? null : metaInfo.getExtra().get(MIPushNotificationHelper.NOTIFICATION_CHANNEL_ID));
    }

    public static short getGeoMsgStatus(boolean z2, boolean z3, boolean z4) {
        return (short) ((z2 ? 4 : 0) + (z3 ? 2 : 0) + (z4 ? 1 : 0));
    }

    public static short getDeviceStatus(Context context, String str) {
        return getDeviceStatus(context, str, null);
    }

    public static short getDeviceStatus(Context context, String str, String str2) {
        return (short) (AppInfoUtils.getAppNotificationOp(context, str, false).getValue() + (MobileStatusUtils.isCharging(context) ? 4 : 0) + (MobileStatusUtils.isScreenLocked(context) ? 8 : 0) + (NotificationManagerHelper.isRomSupportNotificationBelongToApp(context) ? 16 : 0) + getChannelStatus(context, str, str2));
    }
}
