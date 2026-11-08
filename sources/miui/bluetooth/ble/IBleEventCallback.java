package miui.bluetooth.ble;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IBleEventCallback extends IInterface {
    public static final String DESCRIPTOR = "miui.bluetooth.ble.IBleEventCallback";

    public static class Default implements IBleEventCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.bluetooth.ble.IBleEventCallback
        public boolean onEvent(String str, int i2, byte[] bArr) throws RemoteException {
            return false;
        }
    }

    boolean onEvent(String str, int i2, byte[] bArr) throws RemoteException;

    public static abstract class Stub extends Binder implements IBleEventCallback {
        static final int TRANSACTION_onEvent = 1;

        private static class Proxy implements IBleEventCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBleEventCallback.DESCRIPTOR;
            }

            @Override // miui.bluetooth.ble.IBleEventCallback
            public boolean onEvent(String str, int i2, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBleEventCallback.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IBleEventCallback.DESCRIPTOR);
        }

        public static IBleEventCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IBleEventCallback.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IBleEventCallback)) ? new Proxy(iBinder) : (IBleEventCallback) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IBleEventCallback.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IBleEventCallback.DESCRIPTOR);
                return true;
            }
            if (i2 != 1) {
                return super.onTransact(i2, parcel, parcel2, i3);
            }
            boolean zOnEvent = onEvent(parcel.readString(), parcel.readInt(), parcel.createByteArray());
            parcel2.writeNoException();
            parcel2.writeInt(zOnEvent ? 1 : 0);
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }
}
