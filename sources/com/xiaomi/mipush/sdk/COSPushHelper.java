package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
public class COSPushHelper {
    private static long mLastTime = 0;
    private static volatile boolean mNeedRegister = false;

    public static void convertMessage(Intent intent) {
        AssemblePushHelper.convertMessage(intent);
    }

    public static void doInNetworkChange(Context context) {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (getNeedRegister()) {
            long j2 = mLastTime;
            if (j2 <= 0 || j2 + Constants.ASSEMBLE_PUSH_NETWORK_INTERVAL <= jElapsedRealtime) {
                mLastTime = jElapsedRealtime;
                registerCOSAssemblePush(context);
            }
        }
    }

    public static boolean getNeedRegister() {
        return mNeedRegister;
    }

    public static boolean hasNetwork(Context context) {
        return AssemblePushHelper.hasNetwork(context);
    }

    public static void registerCOSAssemblePush(Context context) {
        AbstractPushManager manager = AssemblePushCollectionsManager.getInstance(context).getManager(AssemblePush.ASSEMBLE_PUSH_COS);
        if (manager != null) {
            MyLog.w("ASSEMBLE_PUSH :  register cos when network change!");
            manager.register();
        }
    }

    public static synchronized void setNeedRegister(boolean z2) {
        mNeedRegister = z2;
    }

    public static void uploadToken(Context context, String str) {
        AssemblePushHelper.uploadToken(context, AssemblePush.ASSEMBLE_PUSH_COS, str);
    }

    public static void onNotificationMessageCome(Context context, String str) {
    }

    public static void onPassThoughMessageCome(Context context, String str) {
    }
}
