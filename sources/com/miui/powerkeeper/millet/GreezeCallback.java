package com.miui.powerkeeper.millet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.millet.bright.BrightMode;
import miui.greeze.IGreezeCallback;

/* JADX INFO: loaded from: classes.dex */
public class GreezeCallback extends IGreezeCallback.Stub {
    private static final String TAG = "GreezeCallback";
    private boolean DEBUG = Log.isLoggable(TAG, 3);
    private Context mContext;
    private Handler mHandler;

    public GreezeCallback(Context context, Handler handler) {
        this.mHandler = null;
        Log.d(TAG, "init!!");
        this.mContext = context;
        this.mHandler = handler;
    }

    private void sendMsg(Message message) {
        MilletHandler milletHandler = (MilletHandler) this.mHandler;
        if (!MilletHandler.isScreenOn()) {
            milletHandler.sendMessage(message);
            return;
        }
        MilletPolicy milletPolicy = milletHandler.getFrozenCtrl().getMilletPolicy();
        if (milletHandler.getFrozenCtrl().getBrightCtrl() == null || milletPolicy == null || !BrightMode.getInstance(this.mContext).getBrightEnable()) {
            return;
        }
        milletHandler.getFrozenCtrl().getBrightCtrl().getHandler().sendMessage(message);
    }

    private void sendQueryMsg(Message message, int i2) {
        MilletHandler milletHandler = (MilletHandler) this.mHandler;
        if (milletHandler.isInQueryState(i2)) {
            milletHandler.sendMessage(message);
        } else {
            if (milletHandler.getFrozenCtrl().getBrightCtrl() == null || !BrightMode.getInstance(this.mContext).getBrightEnable()) {
                return;
            }
            milletHandler.getFrozenCtrl().getBrightCtrl().getHandler().sendMessage(message);
        }
    }

    @Override // miui.greeze.IGreezeCallback
    public void reportBinderState(int i2, int i3, int i4, int i5, long j2) {
        Message messageObtain = Message.obtain();
        if (this.DEBUG) {
            Log.d(TAG, "st reason=" + i5 + " uid=" + i2);
        }
        messageObtain.what = 5;
        Bundle bundle = new Bundle();
        bundle.putInt("uid", i2);
        bundle.putInt("reason", i5);
        bundle.putInt("pid", i3);
        messageObtain.setData(bundle);
        sendQueryMsg(messageObtain, i2);
    }

    @Override // miui.greeze.IGreezeCallback
    public void reportBinderTrans(int i2, int i3, int i4, int i5, int i6, boolean z2, long j2, long j3) {
        Message messageObtain = Message.obtain();
        if (this.DEBUG) {
            Slog.d(TAG, "callerPid=" + i5 + " cu=" + i4 + " tu=" + i2 + " oneway=" + z2 + " tp=" + i3 + " b=" + j3);
        }
        messageObtain.what = 6;
        Bundle bundle = new Bundle();
        bundle.putInt("uid", i2);
        bundle.putInt("caller_pid", i5);
        bundle.putInt("caller_uid", i4);
        bundle.putInt("dst_pid", i3);
        bundle.putChar("tf_oneway", z2 ? 'T' : 'F');
        bundle.putInt("buffer", (int) j3);
        messageObtain.setData(bundle);
        sendMsg(messageObtain);
    }

    @Override // miui.greeze.IGreezeCallback
    public void reportNet(int i2, long j2) {
        if (this.DEBUG) {
            Log.d(TAG, "reportNet uid=" + i2);
        }
        Message messageObtain = Message.obtain();
        messageObtain.what = 8;
        Bundle bundle = new Bundle();
        bundle.putInt("uid", i2);
        messageObtain.setData(bundle);
        sendMsg(messageObtain);
    }

    @Override // miui.greeze.IGreezeCallback
    public void reportSignal(int i2, int i3, long j2) {
        if (this.DEBUG) {
            Log.d(TAG, "reportSignal uid=" + i2 + " pid=" + i3);
        }
    }

    @Override // miui.greeze.IGreezeCallback
    public void serviceReady(boolean z2) {
        Log.d(TAG, "serviceReady ready=" + z2);
        Message messageObtain = Message.obtain();
        messageObtain.what = 17;
        Bundle bundle = new Bundle();
        bundle.putChar("ready", z2 ? 'T' : 'F');
        messageObtain.setData(bundle);
        this.mHandler.sendMessage(messageObtain);
    }

    @Override // miui.greeze.IGreezeCallback
    public void thawedByOther(int i2, int i3, int i4) {
        if (this.DEBUG) {
            Log.d(TAG, "thawedByOther uid=" + i2 + " pid=" + i3);
        }
        Message messageObtain = Message.obtain();
        messageObtain.what = 18;
        Bundle bundle = new Bundle();
        bundle.putInt("uid", i2);
        bundle.putInt("pid", i3);
        bundle.putInt("module", i4);
        messageObtain.setData(bundle);
        sendMsg(messageObtain);
    }
}
