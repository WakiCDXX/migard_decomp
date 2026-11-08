package com.milink.api.v1;

import android.os.Handler;
import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDelegate;

/* JADX INFO: loaded from: classes.dex */
public class McsDelegate extends IMcsDelegate.Stub {
    private f mDelegate;
    private Handler mHandler = new Handler();

    static /* synthetic */ f access$000(McsDelegate mcsDelegate) {
        mcsDelegate.getClass();
        return null;
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onConnected() throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onConnectedFailed() throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onDisconnected() throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onLoading() throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onPaused() throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onPlaying() throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onStopped() throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onNextAudio(boolean z2) throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onPrevAudio(boolean z2) throws RemoteException {
    }

    @Override // com.milink.api.v1.aidl.IMcsDelegate
    public void onVolume(int i2) throws RemoteException {
    }

    public void setDelegate(f fVar) {
    }
}
