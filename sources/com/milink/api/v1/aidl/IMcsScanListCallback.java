package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IMcsScanListCallback extends IInterface {
    public static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsScanListCallback";

    void onConnectFail(String str, String str2) throws RemoteException;

    void onConnectSuccess(String str, String str2) throws RemoteException;

    void onSelectDevice(String str, String str2, String str3) throws RemoteException;

    public static abstract class Stub extends Binder implements IMcsScanListCallback {
        static final int TRANSACTION_onConnectFail = 3;
        static final int TRANSACTION_onConnectSuccess = 2;
        static final int TRANSACTION_onSelectDevice = 1;

        private static class Proxy implements IMcsScanListCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMcsScanListCallback.DESCRIPTOR;
            }

            @Override // com.milink.api.v1.aidl.IMcsScanListCallback
            public void onConnectFail(String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsScanListCallback.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsScanListCallback
            public void onConnectSuccess(String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsScanListCallback.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsScanListCallback
            public void onSelectDevice(String str, String str2, String str3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsScanListCallback.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeString(str3);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMcsScanListCallback.DESCRIPTOR);
        }

        public static IMcsScanListCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMcsScanListCallback.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMcsScanListCallback)) ? new Proxy(iBinder) : (IMcsScanListCallback) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IMcsScanListCallback.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IMcsScanListCallback.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                onSelectDevice(parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
            } else if (i2 == 2) {
                onConnectSuccess(parcel.readString(), parcel.readString());
                parcel2.writeNoException();
            } else {
                if (i2 != 3) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                onConnectFail(parcel.readString(), parcel.readString());
                parcel2.writeNoException();
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IMcsScanListCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
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
}
