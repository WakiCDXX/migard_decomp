package miui.process;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IAnrStateChangedListener extends IInterface {
    public static final int BROADCAST_ANR_TYPE = 4;
    public static final String DESCRIPTOR = "miui.process.IAnrStateChangedListener";
    public static final int ENTER_ANR_WAITING_STATE = 1;
    public static final int LEAVE_ANR_WAITING_STATE = 2;
    public static final int PROC_START_ANR_TYPE = 16;
    public static final int PROVIDER_ANR_TYPE = 8;
    public static final int SERVICE_ANR_TYPE = 2;
    public static final int SERVICE_START_ANR_TYPE = 1;

    void onAnrHappened(int i2, int i3, int i4) throws RemoteException;

    void onAnrStateChanged(int i2, int i3, int i4) throws RemoteException;

    public static abstract class Stub extends Binder implements IAnrStateChangedListener {
        static final int TRANSACTION_onAnrHappened = 1;
        static final int TRANSACTION_onAnrStateChanged = 2;

        private static class Proxy implements IAnrStateChangedListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAnrStateChangedListener.DESCRIPTOR;
            }

            @Override // miui.process.IAnrStateChangedListener
            public void onAnrHappened(int i2, int i3, int i4) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IAnrStateChangedListener.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeInt(i4);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.process.IAnrStateChangedListener
            public void onAnrStateChanged(int i2, int i3, int i4) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IAnrStateChangedListener.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeInt(i4);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IAnrStateChangedListener.DESCRIPTOR);
        }

        public static IAnrStateChangedListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IAnrStateChangedListener.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IAnrStateChangedListener)) ? new Proxy(iBinder) : (IAnrStateChangedListener) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IAnrStateChangedListener.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IAnrStateChangedListener.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                onAnrHappened(parcel.readInt(), parcel.readInt(), parcel.readInt());
            } else {
                if (i2 != 2) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                onAnrStateChanged(parcel.readInt(), parcel.readInt(), parcel.readInt());
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IAnrStateChangedListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.process.IAnrStateChangedListener
        public void onAnrHappened(int i2, int i3, int i4) throws RemoteException {
        }

        @Override // miui.process.IAnrStateChangedListener
        public void onAnrStateChanged(int i2, int i3, int i4) throws RemoteException {
        }
    }
}
