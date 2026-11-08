package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IMcsDelegate extends IInterface {
    public static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsDelegate";

    void onConnected() throws RemoteException;

    void onConnectedFailed() throws RemoteException;

    void onDisconnected() throws RemoteException;

    void onLoading() throws RemoteException;

    void onNextAudio(boolean z2) throws RemoteException;

    void onPaused() throws RemoteException;

    void onPlaying() throws RemoteException;

    void onPrevAudio(boolean z2) throws RemoteException;

    void onStopped() throws RemoteException;

    void onVolume(int i2) throws RemoteException;

    public static class Default implements IMcsDelegate {
        @Override // android.os.IInterface
        public IBinder asBinder() {
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
    }

    public static abstract class Stub extends Binder implements IMcsDelegate {
        static final int TRANSACTION_onConnected = 1;
        static final int TRANSACTION_onConnectedFailed = 2;
        static final int TRANSACTION_onDisconnected = 3;
        static final int TRANSACTION_onLoading = 4;
        static final int TRANSACTION_onNextAudio = 9;
        static final int TRANSACTION_onPaused = 7;
        static final int TRANSACTION_onPlaying = 5;
        static final int TRANSACTION_onPrevAudio = 10;
        static final int TRANSACTION_onStopped = 6;
        static final int TRANSACTION_onVolume = 8;

        private static class Proxy implements IMcsDelegate {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMcsDelegate.DESCRIPTOR;
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onConnected() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onConnectedFailed() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onDisconnected() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onLoading() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onNextAudio(boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onPaused() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onPlaying() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onPrevAudio(boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onStopped() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDelegate
            public void onVolume(int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDelegate.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMcsDelegate.DESCRIPTOR);
        }

        public static IMcsDelegate asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMcsDelegate.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMcsDelegate)) ? new Proxy(iBinder) : (IMcsDelegate) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IMcsDelegate.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IMcsDelegate.DESCRIPTOR);
                return true;
            }
            switch (i2) {
                case 1:
                    onConnected();
                    parcel2.writeNoException();
                    return true;
                case 2:
                    onConnectedFailed();
                    parcel2.writeNoException();
                    return true;
                case 3:
                    onDisconnected();
                    parcel2.writeNoException();
                    return true;
                case 4:
                    onLoading();
                    parcel2.writeNoException();
                    return true;
                case 5:
                    onPlaying();
                    parcel2.writeNoException();
                    return true;
                case 6:
                    onStopped();
                    parcel2.writeNoException();
                    return true;
                case 7:
                    onPaused();
                    parcel2.writeNoException();
                    return true;
                case 8:
                    onVolume(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 9:
                    onNextAudio(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 10:
                    onPrevAudio(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i2, parcel, parcel2, i3);
            }
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }
}
