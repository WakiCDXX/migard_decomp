package miui.bluetooth.ble;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IBluetoothMiBlePropertyCallback extends IInterface {
    public static final String DESCRIPTOR = "miui.bluetooth.ble.IBluetoothMiBlePropertyCallback";

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

    void notifyProperty(ParcelUuid parcelUuid, int i2, byte[] bArr) throws RemoteException;

    public static abstract class Stub extends Binder implements IBluetoothMiBlePropertyCallback {
        static final int TRANSACTION_notifyProperty = 1;

        private static class Proxy implements IBluetoothMiBlePropertyCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBluetoothMiBlePropertyCallback.DESCRIPTOR;
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBlePropertyCallback
            public void notifyProperty(ParcelUuid parcelUuid, int i2, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBlePropertyCallback.DESCRIPTOR);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IBluetoothMiBlePropertyCallback.DESCRIPTOR);
        }

        public static IBluetoothMiBlePropertyCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IBluetoothMiBlePropertyCallback.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IBluetoothMiBlePropertyCallback)) ? new Proxy(iBinder) : (IBluetoothMiBlePropertyCallback) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IBluetoothMiBlePropertyCallback.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IBluetoothMiBlePropertyCallback.DESCRIPTOR);
                return true;
            }
            if (i2 != 1) {
                return super.onTransact(i2, parcel, parcel2, i3);
            }
            notifyProperty((ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.readInt(), parcel.createByteArray());
            parcel2.writeNoException();
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IBluetoothMiBlePropertyCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBlePropertyCallback
        public void notifyProperty(ParcelUuid parcelUuid, int i2, byte[] bArr) throws RemoteException {
        }
    }
}
