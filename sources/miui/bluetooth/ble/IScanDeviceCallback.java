package miui.bluetooth.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IScanDeviceCallback extends IInterface {
    public static final String DESCRIPTOR = "miui.bluetooth.ble.IScanDeviceCallback";

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

    void onScanDevice(int i2, BluetoothDevice bluetoothDevice, int i3, byte[] bArr) throws RemoteException;

    public static abstract class Stub extends Binder implements IScanDeviceCallback {
        static final int TRANSACTION_onScanDevice = 1;

        private static class Proxy implements IScanDeviceCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IScanDeviceCallback.DESCRIPTOR;
            }

            @Override // miui.bluetooth.ble.IScanDeviceCallback
            public void onScanDevice(int i2, BluetoothDevice bluetoothDevice, int i3, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IScanDeviceCallback.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    _Parcel.writeTypedObject(parcelObtain, bluetoothDevice, 0);
                    parcelObtain.writeInt(i3);
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
            attachInterface(this, IScanDeviceCallback.DESCRIPTOR);
        }

        public static IScanDeviceCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IScanDeviceCallback.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IScanDeviceCallback)) ? new Proxy(iBinder) : (IScanDeviceCallback) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IScanDeviceCallback.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IScanDeviceCallback.DESCRIPTOR);
                return true;
            }
            if (i2 != 1) {
                return super.onTransact(i2, parcel, parcel2, i3);
            }
            onScanDevice(parcel.readInt(), (BluetoothDevice) _Parcel.readTypedObject(parcel, BluetoothDevice.CREATOR), parcel.readInt(), parcel.createByteArray());
            parcel2.writeNoException();
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IScanDeviceCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.bluetooth.ble.IScanDeviceCallback
        public void onScanDevice(int i2, BluetoothDevice bluetoothDevice, int i3, byte[] bArr) throws RemoteException {
        }
    }
}
