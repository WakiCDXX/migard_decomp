package miui.greeze;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IGreezeCallback extends IInterface {
    public static final String DESCRIPTOR = "miui.greeze.IGreezeCallback";

    void reportBinderState(int i2, int i3, int i4, int i5, long j2) throws RemoteException;

    void reportBinderTrans(int i2, int i3, int i4, int i5, int i6, boolean z2, long j2, long j3) throws RemoteException;

    void reportNet(int i2, long j2) throws RemoteException;

    void reportSignal(int i2, int i3, long j2) throws RemoteException;

    void serviceReady(boolean z2) throws RemoteException;

    void thawedByOther(int i2, int i3, int i4) throws RemoteException;

    public static abstract class Stub extends Binder implements IGreezeCallback {
        static final int TRANSACTION_reportBinderState = 4;
        static final int TRANSACTION_reportBinderTrans = 3;
        static final int TRANSACTION_reportNet = 2;
        static final int TRANSACTION_reportSignal = 1;
        static final int TRANSACTION_serviceReady = 5;
        static final int TRANSACTION_thawedByOther = 6;

        private static class Proxy implements IGreezeCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGreezeCallback.DESCRIPTOR;
            }

            @Override // miui.greeze.IGreezeCallback
            public void reportBinderState(int i2, int i3, int i4, int i5, long j2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IGreezeCallback.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeInt(i4);
                    parcelObtain.writeInt(i5);
                    parcelObtain.writeLong(j2);
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.greeze.IGreezeCallback
            public void reportBinderTrans(int i2, int i3, int i4, int i5, int i6, boolean z2, long j2, long j3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IGreezeCallback.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeInt(i4);
                    parcelObtain.writeInt(i5);
                    parcelObtain.writeInt(i6);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    parcelObtain.writeLong(j2);
                    parcelObtain.writeLong(j3);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.greeze.IGreezeCallback
            public void reportNet(int i2, long j2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IGreezeCallback.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeLong(j2);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.greeze.IGreezeCallback
            public void reportSignal(int i2, int i3, long j2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IGreezeCallback.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeLong(j2);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.greeze.IGreezeCallback
            public void serviceReady(boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IGreezeCallback.DESCRIPTOR);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.greeze.IGreezeCallback
            public void thawedByOther(int i2, int i3, int i4) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IGreezeCallback.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeInt(i4);
                    this.mRemote.transact(6, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IGreezeCallback.DESCRIPTOR);
        }

        public static IGreezeCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IGreezeCallback.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IGreezeCallback)) ? new Proxy(iBinder) : (IGreezeCallback) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IGreezeCallback.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IGreezeCallback.DESCRIPTOR);
                return true;
            }
            switch (i2) {
                case 1:
                    reportSignal(parcel.readInt(), parcel.readInt(), parcel.readLong());
                    return true;
                case 2:
                    reportNet(parcel.readInt(), parcel.readLong());
                    return true;
                case 3:
                    reportBinderTrans(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0, parcel.readLong(), parcel.readLong());
                    return true;
                case 4:
                    reportBinderState(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readLong());
                    return true;
                case 5:
                    serviceReady(parcel.readInt() != 0);
                    return true;
                case 6:
                    thawedByOther(parcel.readInt(), parcel.readInt(), parcel.readInt());
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

    public static class Default implements IGreezeCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.greeze.IGreezeCallback
        public void serviceReady(boolean z2) throws RemoteException {
        }

        @Override // miui.greeze.IGreezeCallback
        public void reportNet(int i2, long j2) throws RemoteException {
        }

        @Override // miui.greeze.IGreezeCallback
        public void reportBinderTrans(int i2, int i3, int i4, int i5, int i6, boolean z2, long j2, long j3) throws RemoteException {
        }

        @Override // miui.greeze.IGreezeCallback
        public void reportSignal(int i2, int i3, long j2) throws RemoteException {
        }

        @Override // miui.greeze.IGreezeCallback
        public void thawedByOther(int i2, int i3, int i4) throws RemoteException {
        }

        @Override // miui.greeze.IGreezeCallback
        public void reportBinderState(int i2, int i3, int i4, int i5, long j2) throws RemoteException {
        }
    }
}
