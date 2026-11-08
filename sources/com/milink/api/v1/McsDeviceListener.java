package com.milink.api.v1;

import android.os.Handler;
import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDeviceListener;

/* JADX INFO: loaded from: classes.dex */
public class McsDeviceListener extends IMcsDeviceListener.Stub {
    private f mDelegate;
    private a mDeviceListener;
    private Handler mHandler = new Handler();

    static /* synthetic */ f access$000(McsDeviceListener mcsDeviceListener) {
        mcsDeviceListener.getClass();
        return null;
    }

    static /* synthetic */ a access$100(McsDeviceListener mcsDeviceListener) {
        mcsDeviceListener.getClass();
        return null;
    }

    @Override // com.milink.api.v1.aidl.IMcsDeviceListener
    public void onDeviceLost(String str) throws RemoteException {
    }

    public void setDelegate(f fVar) {
    }

    public void setDeviceListener(a aVar) {
    }

    @Override // com.milink.api.v1.aidl.IMcsDeviceListener
    public void onDeviceFound(String str, String str2, String str3) throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDeviceListener
    public void onDeviceFound2(String str, String str2, String str3, String str4, String str5, String str6) throws RemoteException {
    }
}
