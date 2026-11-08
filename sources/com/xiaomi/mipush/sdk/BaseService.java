package com.xiaomi.mipush.sdk;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseService extends Service {
    private TimeoutHandler mHandler;

    public static class TimeoutHandler extends Handler {
        private static final long TIME_OUT = 1000;
        private static final int TIME_OUT_KILL_SELF = 1001;
        private WeakReference<BaseService> mWRService;

        public TimeoutHandler(WeakReference<BaseService> weakReference) {
            this.mWRService = weakReference;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            WeakReference<BaseService> weakReference;
            BaseService baseService;
            if (message.what != 1001 || (weakReference = this.mWRService) == null || (baseService = weakReference.get()) == null) {
                return;
            }
            MyLog.v("TimeoutHandler " + baseService.toString() + " kill self");
            if (!baseService.hasJob()) {
                baseService.stopSelf();
            } else {
                MyLog.v("TimeoutHandler has job");
                sendEmptyMessageDelayed(1001, 1000L);
            }
        }

        public void reSendTimeoutMessage() {
            if (hasMessages(1001)) {
                removeMessages(1001);
            }
            sendEmptyMessageDelayed(1001, 1000L);
        }
    }

    protected abstract boolean hasJob();

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int i2) {
        super.onStart(intent, i2);
        if (this.mHandler == null) {
            this.mHandler = new TimeoutHandler(new WeakReference(this));
        }
        this.mHandler.reSendTimeoutMessage();
    }
}
