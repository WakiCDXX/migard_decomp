package com.xiaomi.push.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import com.xiaomi.channel.commonutils.android.DataCryptUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.string.Base64Coder;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionRegistrationResult;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class FCMChannelHelper {
    public static byte[] decryptContainer(byte[] bArr, String str) {
        if (TextUtils.isEmpty(str)) {
            MyLog.w("secret is empty, return null");
            return null;
        }
        try {
            return DataCryptUtils.mipushDecrypt(Base64Coder.decode(str), bArr);
        } catch (Exception e2) {
            MyLog.e("dencryption error. ", e2);
            return null;
        }
    }

    public static byte[] encryptContainer(byte[] bArr, String str) {
        if (TextUtils.isEmpty(str)) {
            MyLog.w("secret is empty, return null");
            return null;
        }
        try {
            return DataCryptUtils.mipushEncrypt(Base64Coder.decode(str), bArr);
        } catch (Exception e2) {
            MyLog.e("encryption error. ", e2);
            return null;
        }
    }

    public static XmPushActionRegistrationResult getRegistrationResponse(XmPushActionContainer xmPushActionContainer) {
        byte[] pushAction = xmPushActionContainer.getPushAction();
        XmPushActionRegistrationResult xmPushActionRegistrationResult = new XmPushActionRegistrationResult();
        try {
            XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionRegistrationResult, pushAction);
            return xmPushActionRegistrationResult;
        } catch (TException unused) {
            return null;
        }
    }

    static void handleFcmMessage(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String stringExtra = intent.getStringExtra(PushConstants.EXTRA_FCM_CONTAINER_BUFFER_BASE64);
        String stringExtra2 = intent.getStringExtra(PushConstants.MIPUSH_EXTRA_APP_PACKAGE);
        if (TextUtils.isEmpty(stringExtra) || TextUtils.isEmpty(stringExtra2)) {
            return;
        }
        try {
            byte[] bArrDecryptContainer = decryptContainer(Base64.decode(stringExtra, 2), context.getSharedPreferences(PushServiceConstants.PREF_APPS_SCRT, 0).getString(stringExtra2, null));
            if (bArrDecryptContainer != null) {
                MIPushNotificationHelper.notifyPushMessage(context, MIPushEventProcessor.buildContainer(bArrDecryptContainer), bArrDecryptContainer);
            } else {
                MyLog.w("notify fcm notification error ：dencrypt failed");
            }
        } catch (Throwable th) {
            MyLog.e("notify fcm notification error ", th);
        }
    }

    public static void saveAppRegistrationSecret(Context context, String str, String str2) {
        if (context == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        context.getSharedPreferences(PushServiceConstants.PREF_APPS_SCRT, 0).edit().putString(str, str2).apply();
    }
}
