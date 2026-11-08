package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsScanListCallback;

/* JADX INFO: loaded from: classes.dex */
public class McsScanListCallback extends IMcsScanListCallback.Stub {
    private d mCallback;

    public void setCallback(d dVar) {
    }

    @Override // com.milink.api.v1.aidl.IMcsScanListCallback
    public void onConnectFail(String str, String str2) throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsScanListCallback
    public void onConnectSuccess(String str, String str2) throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsScanListCallback
    public void onSelectDevice(String str, String str2, String str3) throws RemoteException {
    }
}
