package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IMcsOpenMiracastListener extends IInterface {
    public static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsOpenMiracastListener";

    void openFailure(String str, String str2, String str3, String str4) throws RemoteException;

    void openSuccess(String str, String str2, String str3) throws RemoteException;

    public static abstract class Stub extends Binder implements IMcsOpenMiracastListener {
        static final int TRANSACTION_openFailure = 2;
        static final int TRANSACTION_openSuccess = 1;

        private static class Proxy implements IMcsOpenMiracastListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMcsOpenMiracastListener.DESCRIPTOR;
            }

            @Override // com.milink.api.v1.aidl.IMcsOpenMiracastListener
            public void openFailure(String str, String str2, String str3, String str4) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsOpenMiracastListener.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeString(str3);
                    parcelObtain.writeString(str4);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsOpenMiracastListener
            public void openSuccess(String str, String str2, String str3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsOpenMiracastListener.DESCRIPTOR);
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
            attachInterface(this, IMcsOpenMiracastListener.DESCRIPTOR);
        }

        public static IMcsOpenMiracastListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMcsOpenMiracastListener.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMcsOpenMiracastListener)) ? new Proxy(iBinder) : (IMcsOpenMiracastListener) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IMcsOpenMiracastListener.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IMcsOpenMiracastListener.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                openSuccess(parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
            } else {
                if (i2 != 2) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                openFailure(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IMcsOpenMiracastListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.milink.api.v1.aidl.IMcsOpenMiracastListener
        public void openSuccess(String str, String str2, String str3) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcsOpenMiracastListener
        public void openFailure(String str, String str2, String str3, String str4) throws RemoteException {
        }
    }
}
