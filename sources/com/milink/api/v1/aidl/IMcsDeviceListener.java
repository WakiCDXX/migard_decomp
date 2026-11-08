package com.milink.api.v1.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IMcsDeviceListener extends IInterface {
    public static final String DESCRIPTOR = "com.milink.api.v1.aidl.IMcsDeviceListener";

    void onDeviceFound(String str, String str2, String str3) throws RemoteException;

    void onDeviceFound2(String str, String str2, String str3, String str4, String str5, String str6) throws RemoteException;

    void onDeviceLost(String str) throws RemoteException;

    public static abstract class Stub extends Binder implements IMcsDeviceListener {
        static final int TRANSACTION_onDeviceFound = 1;
        static final int TRANSACTION_onDeviceFound2 = 2;
        static final int TRANSACTION_onDeviceLost = 3;

        private static class Proxy implements IMcsDeviceListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMcsDeviceListener.DESCRIPTOR;
            }

            @Override // com.milink.api.v1.aidl.IMcsDeviceListener
            public void onDeviceFound(String str, String str2, String str3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDeviceListener.DESCRIPTOR);
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

            @Override // com.milink.api.v1.aidl.IMcsDeviceListener
            public void onDeviceFound2(String str, String str2, String str3, String str4, String str5, String str6) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDeviceListener.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeString(str3);
                    parcelObtain.writeString(str4);
                    parcelObtain.writeString(str5);
                    parcelObtain.writeString(str6);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.milink.api.v1.aidl.IMcsDeviceListener
            public void onDeviceLost(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMcsDeviceListener.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMcsDeviceListener.DESCRIPTOR);
        }

        public static IMcsDeviceListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMcsDeviceListener.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMcsDeviceListener)) ? new Proxy(iBinder) : (IMcsDeviceListener) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IMcsDeviceListener.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IMcsDeviceListener.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                onDeviceFound(parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
            } else if (i2 == 2) {
                onDeviceFound2(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString());
                parcel2.writeNoException();
            } else {
                if (i2 != 3) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                onDeviceLost(parcel.readString());
                parcel2.writeNoException();
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IMcsDeviceListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.milink.api.v1.aidl.IMcsDeviceListener
        public void onDeviceLost(String str) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcsDeviceListener
        public void onDeviceFound(String str, String str2, String str3) throws RemoteException {
        }

        @Override // com.milink.api.v1.aidl.IMcsDeviceListener
        public void onDeviceFound2(String str, String str2, String str3, String str4, String str5, String str6) throws RemoteException {
        }
    }
}
