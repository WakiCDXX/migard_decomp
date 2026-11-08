package com.miui.powerkeeper.mipush;

import android.content.Context;
import android.util.Log;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

/* JADX INFO: loaded from: classes.dex */
public class MiPushHelper {
    private static final String APP_ID = "2882303761517597680";
    private static final String APP_KEY = "5921759750680";
    private static final String TAG = "PowerKeeperMiPushHelper";

    public static void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null.");
        }
        MiPushClient.registerPush(context, APP_ID, APP_KEY);
        Logger.setLogger(context, new LoggerInterface() { // from class: com.miui.powerkeeper.mipush.MiPushHelper.1
            @Override // com.xiaomi.channel.commonutils.logger.LoggerInterface
            public void log(String str, Throwable th) {
                if (str == null || th == null) {
                    return;
                }
                Log.d(MiPushHelper.TAG, str, th);
            }

            @Override // com.xiaomi.channel.commonutils.logger.LoggerInterface
            public void log(String str) {
                if (str != null) {
                    Log.d(MiPushHelper.TAG, str);
                }
            }

            @Override // com.xiaomi.channel.commonutils.logger.LoggerInterface
            public void setTag(String str) {
            }
        });
        Logger.disablePushFileLog(context);
    }
}
