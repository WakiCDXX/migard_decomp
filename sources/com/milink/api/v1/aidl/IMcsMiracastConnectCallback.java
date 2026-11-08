package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IMcsMiracastConnectCallback extends IInterface {
    public static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsMiracastConnectCallback";

    void onConnectFail(String str) throws RemoteException;

    void onConnectSuccess(String str) throws RemoteException;

    void onConnecting(String str) throws RemoteException;

    void onResult(int i2, String str, String str2) throws RemoteException;

    public static abstract class Stub extends Binder implements IMcsMiracastConnectCallback {
        static final int TRANSACTION_onConnectFail = 2;
        static final int TRANSACTION_onConnectSuccess = 1;
        static final int TRANSACTION_onConnecting = 3;
        static final int TRANSACTION_onResult = 4;

        private static class Proxy implements IMcsMiracastConnectCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMcsMiracastConnectCallback.DESCRIPTOR;
            }

            @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
            public void onConnectFail(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsMiracastConnectCallback.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
            public void onConnectSuccess(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsMiracastConnectCallback.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
            public void onConnecting(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsMiracastConnectCallback.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
            public void onResult(int i2, String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsMiracastConnectCallback.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMcsMiracastConnectCallback.DESCRIPTOR);
        }

        public static IMcsMiracastConnectCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMcsMiracastConnectCallback.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMcsMiracastConnectCallback)) ? new Proxy(iBinder) : (IMcsMiracastConnectCallback) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IMcsMiracastConnectCallback.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IMcsMiracastConnectCallback.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                onConnectSuccess(parcel.readString());
                parcel2.writeNoException();
            } else if (i2 == 2) {
                onConnectFail(parcel.readString());
                parcel2.writeNoException();
            } else if (i2 == 3) {
                onConnecting(parcel.readString());
                parcel2.writeNoException();
            } else {
                if (i2 != 4) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                onResult(parcel.readInt(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IMcsMiracastConnectCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
        public void onConnectFail(String str) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
        public void onConnectSuccess(String str) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
        public void onConnecting(String str) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcsMiracastConnectCallback
        public void onResult(int i2, String str, String str2) throws RemoteException {
        }
    }
}
