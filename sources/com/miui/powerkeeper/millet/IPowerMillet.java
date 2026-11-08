package com.miui.powerkeeper.millet;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IPowerMillet extends IInterface {
    public static final String DESCRIPTOR = "com.miui.powerkeeper.millet.IPowerMillet";

    public static class _Parcel {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T> T readTypedObject(Parcel parcel, Parcelable.Creator<T> creator) {
            if (parcel.readInt() != 0) {
                return creator.createFromParcel(parcel);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void writeTypedObject(Parcel parcel, T t2, int i2) {
            if (t2 == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                t2.writeToParcel(parcel, i2);
            }
        }
    }

    void notifyBluetoothEvent(int i2, Bundle bundle) throws RemoteException;

    void showState() throws RemoteException;

    public static class Default implements IPowerMillet {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.miui.powerkeeper.millet.IPowerMillet
        public void showState() throws RemoteException {
        }

        @Override // com.miui.powerkeeper.millet.IPowerMillet
        public void notifyBluetoothEvent(int i2, Bundle bundle) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IPowerMillet {
        static final int TRANSACTION_notifyBluetoothEvent = 1;
        static final int TRANSACTION_showState = 2;

        private static class Proxy implements IPowerMillet {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPowerMillet.DESCRIPTOR;
            }

            @Override // com.miui.powerkeeper.millet.IPowerMillet
            public void notifyBluetoothEvent(int i2, Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPowerMillet.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    _Parcel.writeTypedObject(parcelObtain, bundle, 0);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.millet.IPowerMillet
            public void showState() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPowerMillet.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IPowerMillet.DESCRIPTOR);
        }

        public static IPowerMillet asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IPowerMillet.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IPowerMillet)) ? new Proxy(iBinder) : (IPowerMillet) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IPowerMillet.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IPowerMillet.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                notifyBluetoothEvent(parcel.readInt(), (Bundle) _Parcel.readTypedObject(parcel, Bundle.CREATOR));
            } else {
                if (i2 != 2) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                showState();
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }
}
