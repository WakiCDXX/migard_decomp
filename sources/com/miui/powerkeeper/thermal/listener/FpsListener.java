package com.miui.powerkeeper.thermal.listener;

import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.whetstone.IVideoFpsCallBack;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import com.xiaomi.mipush.sdk.MiPushClient;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class FpsListener extends ElementListener {
    private static final int MSG_REPORT_FPS_VALUE = 1000;
    private Set<String> mHighFpsPkgSet;
    private final IVideoFpsCallBack mVideoFpsCallBack;
    private IWhetstoneActivityManager mWs;
    private boolean mHighFpsState = false;
    private int mEnterHighFpsValue = 60;

    public FpsListener() {
        HashSet hashSet = new HashSet(2);
        this.mHighFpsPkgSet = hashSet;
        hashSet.add("com.youku.phone");
        this.mHighFpsPkgSet.add("tv.danmaku.bili");
        this.mVideoFpsCallBack = new IVideoFpsCallBack.Stub() { // from class: com.miui.powerkeeper.thermal.listener.FpsListener.1
            public void onVideoFpsChange(int i2, int i3) {
                Message messageObtainMessage = FpsListener.this.mElementHandler.obtainMessage(1000);
                messageObtainMessage.arg1 = i2;
                messageObtainMessage.arg2 = i3;
                messageObtainMessage.sendToTarget();
            }
        };
    }

    private void registerVideoFpsCallBack() {
        try {
            Log.i("ElementListener", MiPushClient.COMMAND_REGISTER);
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                Log.e("ElementListener", "mWs = null");
            } else {
                iWhetstoneActivityManager.registVideoFpsCallBack(this.mVideoFpsCallBack);
            }
        } catch (RemoteException unused) {
            Log.e("ElementListener", "IWhetstoneActivityManager work abnormal");
        }
    }

    private void unRegisterVideoFpsCallBack() {
        try {
            Log.i("ElementListener", MiPushClient.COMMAND_UNREGISTER);
            IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
            if (iWhetstoneActivityManager == null) {
                Log.e("ElementListener", "mWs = null");
            } else {
                iWhetstoneActivityManager.unRegistVideoFpsCallBack(this.mVideoFpsCallBack);
            }
        } catch (RemoteException unused) {
            Log.e("ElementListener", "IWhetstoneActivityManager work abnormal");
        }
    }

    private void updateFpsState(int i2, int i3) {
        String[] packagesForUid = AutoRegister.getContext().getPackageManager().getPackagesForUid(i2);
        if (packagesForUid == null || packagesForUid.length <= 0) {
            return;
        }
        Log.i("ElementListener", "onVideoFpsChange uid = " + i2 + " pkg = " + packagesForUid[0] + " fps = " + i3);
        if (!this.mHighFpsPkgSet.contains(packagesForUid[0]) || this.mEnterHighFpsValue == i3) {
            return;
        }
        this.mEnterHighFpsValue = i3;
        boolean z2 = i3 >= 120;
        this.mHighFpsState = z2;
        modifyCurrentState(z2 ? 1 : 99);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        super.dump(printWriter);
        printWriter.print("\tmEnterHighFpsValue: " + this.mEnterHighFpsValue);
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 != 1000) {
            return;
        }
        updateFpsState(i3, i4);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        this.mWs = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        registerVideoFpsCallBack();
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 99;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.listener.IListener
    public void uninstall() {
        unRegisterVideoFpsCallBack();
        super.uninstall();
    }
}
