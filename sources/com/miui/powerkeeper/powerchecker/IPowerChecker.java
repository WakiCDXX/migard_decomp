package com.miui.powerkeeper.powerchecker;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IPowerChecker extends IInterface {
    public static final String DESCRIPTOR = "com.miui.powerkeeper.powerchecker.IPowerChecker";

    void triggerPowerChecker() throws RemoteException;

    public static class Default implements IPowerChecker {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.miui.powerkeeper.powerchecker.IPowerChecker
        public void triggerPowerChecker() throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IPowerChecker {
        static final int TRANSACTION_triggerPowerChecker = 1;

        private static class Proxy implements IPowerChecker {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPowerChecker.DESCRIPTOR;
            }

            @Override // com.miui.powerkeeper.powerchecker.IPowerChecker
            public void triggerPowerChecker() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPowerChecker.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IPowerChecker.DESCRIPTOR);
        }

        public static IPowerChecker asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IPowerChecker.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IPowerChecker)) ? new Proxy(iBinder) : (IPowerChecker) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IPowerChecker.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IPowerChecker.DESCRIPTOR);
                return true;
            }
            if (i2 != 1) {
                return super.onTransact(i2, parcel, parcel2, i3);
            }
            triggerPowerChecker();
            parcel2.writeNoException();
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }
}
