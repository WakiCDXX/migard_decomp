package com.miui.powerkeeper;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IWakelockManager extends IInterface {
    public static final String DESCRIPTOR = "com.miui.powerkeeper.IWakelockManager";

    public static class a {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T> T c(Parcel parcel, Parcelable.Creator<T> creator) {
            if (parcel.readInt() != 0) {
                return creator.createFromParcel(parcel);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void d(Parcel parcel, T t2, int i2) {
            if (t2 == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                t2.writeToParcel(parcel, i2);
            }
        }
    }

    void setWakelockBlock(boolean z2, ComponentName componentName, int i2, List<String> list) throws RemoteException;

    public static abstract class Stub extends Binder implements IWakelockManager {
        static final int TRANSACTION_setWakelockBlock = 1;

        private static class Proxy implements IWakelockManager {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWakelockManager.DESCRIPTOR;
            }

            @Override // com.miui.powerkeeper.IWakelockManager
            public void setWakelockBlock(boolean z2, ComponentName componentName, int i2, List<String> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IWakelockManager.DESCRIPTOR);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    a.d(parcelObtain, componentName, 0);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStringList(list);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IWakelockManager.DESCRIPTOR);
        }

        public static IWakelockManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IWakelockManager.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IWakelockManager)) ? new Proxy(iBinder) : (IWakelockManager) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IWakelockManager.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IWakelockManager.DESCRIPTOR);
                return true;
            }
            if (i2 != 1) {
                return super.onTransact(i2, parcel, parcel2, i3);
            }
            setWakelockBlock(parcel.readInt() != 0, (ComponentName) a.c(parcel, ComponentName.CREATOR), parcel.readInt(), parcel.createStringArrayList());
            parcel2.writeNoException();
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IWakelockManager {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.miui.powerkeeper.IWakelockManager
        public void setWakelockBlock(boolean z2, ComponentName componentName, int i2, List<String> list) throws RemoteException {
        }
    }
}
