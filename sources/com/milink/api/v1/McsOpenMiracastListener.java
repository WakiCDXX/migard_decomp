package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsOpenMiracastListener;

/* JADX INFO: loaded from: classes.dex */
public class McsOpenMiracastListener extends IMcsOpenMiracastListener.Stub {
    private c mOpenMiracastListener;

    public void setOpenMiracastListener(c cVar) {
    }

    @Override // com.milink.api.v1.aidl.IMcsOpenMiracastListener
    public void openSuccess(String str, String str2, String str3) throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsOpenMiracastListener
    public void openFailure(String str, String str2, String str3, String str4) throws RemoteException {
    }
}
