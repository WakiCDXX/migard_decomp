package com.miui.powerkeeper.thermal.listener;

import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.miui.whetstone.IDanmakuCallBack;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class DanmuListener extends ElementListener {
    private static final int ICON_STATUS_NOT_SURE = 3;
    private static final int ICON_STATUS_OFF = 2;
    private static final int ICON_STATUS_ON = 1;
    private static final int MSG_REPORT_DANMAKU_STATUS_CHANGED = 1000;
    private String TAG = "DanmuListener";
    private final IDanmakuCallBack mDanmakuCallback = new IDanmakuCallBack.Stub() { // from class: com.miui.powerkeeper.thermal.listener.DanmuListener.1
        public void onDanmakuInfoChanged(int i2) {
            Log.i(DanmuListener.this.TAG, "onDanmakuInfoChanged = " + i2);
            Message messageObtainMessage = DanmuListener.this.mElementHandler.obtainMessage(1000);
            messageObtainMessage.arg1 = i2;
            messageObtainMessage.sendToTarget();
        }
    };
    private int mState;
    private IWhetstoneActivityManager mWs;

    private void registerDanmakuCallBack() {
        try {
            Log.i(this.TAG, "registerDanmakuCallBack");
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                Log.e(this.TAG, "mWs = null");
            } else {
                iWhetstoneActivityManager.registerDanmakuCallBack(this.mDanmakuCallback);
            }
        } catch (RemoteException unused) {
            Log.e(this.TAG, "registerDanmakuCallBack failed");
        }
    }

    private void unRegisterDanmakuCallBack() {
        try {
            Log.i(this.TAG, "unRegisterDanmakuCallBack");
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                Log.e(this.TAG, "mWs = null");
            } else {
                iWhetstoneActivityManager.unRegisterDanmakuCallBack(this.mDanmakuCallback);
            }
        } catch (RemoteException unused) {
            Log.e(this.TAG, "unregisterDanmakuCallBack failed");
        }
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        super.dump(printWriter);
        printWriter.print("\tDankamuState: " + this.mState);
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 == 1000 && i3 != this.mState) {
            this.mState = i3;
            Log.i(this.TAG, "onDanmuStatusChanged mEnable=" + this.mState);
            modifyCurrentState(i3 != 1 ? 99 : 1);
        }
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        this.mWs = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        registerDanmakuCallBack();
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    int initCurrentState() {
        return 99;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.listener.IListener
    public void uninstall() {
        super.uninstall();
        unRegisterDanmakuCallBack();
    }
}
