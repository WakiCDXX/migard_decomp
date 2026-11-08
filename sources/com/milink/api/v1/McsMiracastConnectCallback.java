package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsMiracastConnectCallback;

/* JADX INFO: loaded from: classes.dex */
public class McsMiracastConnectCallback extends IMcsMiracastConnectCallback.Stub {
    private b mCallback;

    @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
    public void onConnectFail(String str) throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
    public void onConnectSuccess(String str) throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
    public void onConnecting(String str) throws RemoteException {
    }

    public void setCallback(b bVar) {
    }

    @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
    public void onResult(int i2, String str, String str2) throws RemoteException {
    }
}
