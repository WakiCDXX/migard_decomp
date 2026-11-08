package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDataSource;

/* JADX INFO: loaded from: classes.dex */
public class McsDataSource extends IMcsDataSource.Stub {
    e mDataSource;

    @Override // com.milink.api.v1.aidl.IMcsDataSource
    public String getNextPhoto(String str, boolean z2) throws RemoteException {
        return null;
    }

    @Override // com.milink.api.v1.aidl.IMcsDataSource
    public String getPrevPhoto(String str, boolean z2) throws RemoteException {
        return null;
    }

    public void setDataSource(e eVar) {
    }
}
