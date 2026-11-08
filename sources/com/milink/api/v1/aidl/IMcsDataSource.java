package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IMcsDataSource extends IInterface {
    public static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsDataSource";

    public static class Default implements IMcsDataSource {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.milink.api.v1.aidl.IMcsDataSource
        public String getNextPhoto(String str, boolean z2) throws RemoteException {
            return null;
        }

        @Override // com.milink.api.v1.aidl.IMcsDataSource
        public String getPrevPhoto(String str, boolean z2) throws RemoteException {
            return null;
        }
    }

    String getNextPhoto(String str, boolean z2) throws RemoteException;

    String getPrevPhoto(String str, boolean z2) throws RemoteException;

    public static abstract class Stub extends Binder implements IMcsDataSource {
        static final int TRANSACTION_getNextPhoto = 2;
        static final int TRANSACTION_getPrevPhoto = 1;

        private static class Proxy implements IMcsDataSource {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMcsDataSource.DESCRIPTOR;
            }

            @Override // com.milink.api.v1.aidl.IMcsDataSource
            public String getNextPhoto(String str, boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDataSource.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDataSource
            public String getPrevPhoto(String str, boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDataSource.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMcsDataSource.DESCRIPTOR);
        }

        public static IMcsDataSource asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMcsDataSource.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMcsDataSource)) ? new Proxy(iBinder) : (IMcsDataSource) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IMcsDataSource.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IMcsDataSource.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                String prevPhoto = getPrevPhoto(parcel.readString(), parcel.readInt() != 0);
                parcel2.writeNoException();
                parcel2.writeString(prevPhoto);
            } else {
                if (i2 != 2) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                String nextPhoto = getNextPhoto(parcel.readString(), parcel.readInt() != 0);
                parcel2.writeNoException();
                parcel2.writeString(nextPhoto);
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }
}
