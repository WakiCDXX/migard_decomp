package miui.bluetooth.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IBluetoothMiBle extends IInterface {
    public static final String DESCRIPTOR = "miui.bluetooth.ble.IBluetoothMiBle";

    public static class _Parcel {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T> T readTypedObject(Parcel parcel, Parcelable.Creator<T> creator) {
            if (parcel.readInt() != 0) {
                return creator.createFromParcel(parcel);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void writeTypedList(Parcel parcel, List<T> list, int i2) {
            if (list == null) {
                parcel.writeInt(-1);
                return;
            }
            int size = list.size();
            parcel.writeInt(size);
            for (int i3 = 0; i3 < size; i3++) {
                writeTypedObject(parcel, list.get(i3), i2);
            }
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

    boolean authenticate(String str, ParcelUuid parcelUuid) throws RemoteException;

    boolean authorize(String str, ParcelUuid parcelUuid, String str2) throws RemoteException;

    void connect(String str, ParcelUuid parcelUuid) throws RemoteException;

    void disconnect(String str, ParcelUuid parcelUuid) throws RemoteException;

    byte[] encrypt(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException;

    List<BluetoothDevice> getConnectedDevices() throws RemoteException;

    byte[] getProperty(String str, ParcelUuid parcelUuid, int i2) throws RemoteException;

    int getRssi(String str, ParcelUuid parcelUuid) throws RemoteException;

    int getServiceVersion() throws RemoteException;

    boolean isConnected(String str) throws RemoteException;

    void registerClient(IBinder iBinder, String str, ParcelUuid parcelUuid, IBluetoothMiBleCallback iBluetoothMiBleCallback) throws RemoteException;

    boolean registerPropertyCallback(String str, ParcelUuid parcelUuid, int i2, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException;

    boolean setEncryptionKey(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException;

    boolean setProperty(String str, ParcelUuid parcelUuid, int i2, byte[] bArr) throws RemoteException;

    boolean setRssiThreshold(String str, ParcelUuid parcelUuid, int i2) throws RemoteException;

    boolean supportProperty(String str, int i2) throws RemoteException;

    void unregisterClient(IBinder iBinder, String str, ParcelUuid parcelUuid) throws RemoteException;

    boolean unregisterPropertyCallback(String str, ParcelUuid parcelUuid, int i2, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException;

    public static abstract class Stub extends Binder implements IBluetoothMiBle {
        static final int TRANSACTION_authenticate = 15;
        static final int TRANSACTION_authorize = 13;
        static final int TRANSACTION_connect = 4;
        static final int TRANSACTION_disconnect = 5;
        static final int TRANSACTION_encrypt = 17;
        static final int TRANSACTION_getConnectedDevices = 6;
        static final int TRANSACTION_getProperty = 12;
        static final int TRANSACTION_getRssi = 7;
        static final int TRANSACTION_getServiceVersion = 18;
        static final int TRANSACTION_isConnected = 3;
        static final int TRANSACTION_registerClient = 1;
        static final int TRANSACTION_registerPropertyCallback = 9;
        static final int TRANSACTION_setEncryptionKey = 16;
        static final int TRANSACTION_setProperty = 11;
        static final int TRANSACTION_setRssiThreshold = 14;
        static final int TRANSACTION_supportProperty = 8;
        static final int TRANSACTION_unregisterClient = 2;
        static final int TRANSACTION_unregisterPropertyCallback = 10;

        private static class Proxy implements IBluetoothMiBle {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean authenticate(String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean authorize(String str, ParcelUuid parcelUuid, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public void connect(String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public void disconnect(String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public byte[] encrypt(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createByteArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public List<BluetoothDevice> getConnectedDevices() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createTypedArrayList(BluetoothDevice.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IBluetoothMiBle.DESCRIPTOR;
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public byte[] getProperty(String str, ParcelUuid parcelUuid, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createByteArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public int getRssi(String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public int getServiceVersion() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    this.mRemote.transact(18, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean isConnected(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public void registerClient(IBinder iBinder, String str, ParcelUuid parcelUuid, IBluetoothMiBleCallback iBluetoothMiBleCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeStrongInterface(iBluetoothMiBleCallback);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean registerPropertyCallback(String str, ParcelUuid parcelUuid, int i2, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongInterface(iBluetoothMiBlePropertyCallback);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean setEncryptionKey(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(16, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean setProperty(String str, ParcelUuid parcelUuid, int i2, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean setRssiThreshold(String str, ParcelUuid parcelUuid, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean supportProperty(String str, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public void unregisterClient(IBinder iBinder, String str, ParcelUuid parcelUuid) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IBluetoothMiBle
            public boolean unregisterPropertyCallback(String str, ParcelUuid parcelUuid, int i2, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBluetoothMiBle.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongInterface(iBluetoothMiBlePropertyCallback);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IBluetoothMiBle.DESCRIPTOR);
        }

        public static IBluetoothMiBle asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IBluetoothMiBle.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IBluetoothMiBle)) ? new Proxy(iBinder) : (IBluetoothMiBle) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IBluetoothMiBle.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IBluetoothMiBle.DESCRIPTOR);
                return true;
            }
            switch (i2) {
                case 1:
                    registerClient(parcel.readStrongBinder(), parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), IBluetoothMiBleCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 2:
                    unregisterClient(parcel.readStrongBinder(), parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR));
                    parcel2.writeNoException();
                    return true;
                case 3:
                    boolean zIsConnected = isConnected(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsConnected ? 1 : 0);
                    return true;
                case 4:
                    connect(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    disconnect(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR));
                    parcel2.writeNoException();
                    return true;
                case 6:
                    List<BluetoothDevice> connectedDevices = getConnectedDevices();
                    parcel2.writeNoException();
                    _Parcel.writeTypedList(parcel2, connectedDevices, 1);
                    return true;
                case 7:
                    int rssi = getRssi(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR));
                    parcel2.writeNoException();
                    parcel2.writeInt(rssi);
                    return true;
                case 8:
                    boolean zSupportProperty = supportProperty(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zSupportProperty ? 1 : 0);
                    return true;
                case 9:
                    boolean zRegisterPropertyCallback = registerPropertyCallback(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.readInt(), IBluetoothMiBlePropertyCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(zRegisterPropertyCallback ? 1 : 0);
                    return true;
                case 10:
                    boolean zUnregisterPropertyCallback = unregisterPropertyCallback(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.readInt(), IBluetoothMiBlePropertyCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(zUnregisterPropertyCallback ? 1 : 0);
                    return true;
                case 11:
                    boolean property = setProperty(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.readInt(), parcel.createByteArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(property ? 1 : 0);
                    return true;
                case 12:
                    byte[] property2 = getProperty(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeByteArray(property2);
                    return true;
                case 13:
                    boolean zAuthorize = authorize(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zAuthorize ? 1 : 0);
                    return true;
                case 14:
                    boolean rssiThreshold = setRssiThreshold(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(rssiThreshold ? 1 : 0);
                    return true;
                case 15:
                    boolean zAuthenticate = authenticate(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR));
                    parcel2.writeNoException();
                    parcel2.writeInt(zAuthenticate ? 1 : 0);
                    return true;
                case 16:
                    boolean encryptionKey = setEncryptionKey(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.createByteArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(encryptionKey ? 1 : 0);
                    return true;
                case 17:
                    byte[] bArrEncrypt = encrypt(parcel.readString(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.createByteArray());
                    parcel2.writeNoException();
                    parcel2.writeByteArray(bArrEncrypt);
                    return true;
                case 18:
                    int serviceVersion = getServiceVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(serviceVersion);
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

    public static class Default implements IBluetoothMiBle {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean authenticate(String str, ParcelUuid parcelUuid) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean authorize(String str, ParcelUuid parcelUuid, String str2) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public byte[] encrypt(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public List<BluetoothDevice> getConnectedDevices() throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public byte[] getProperty(String str, ParcelUuid parcelUuid, int i2) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public int getRssi(String str, ParcelUuid parcelUuid) throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public int getServiceVersion() throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean isConnected(String str) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean registerPropertyCallback(String str, ParcelUuid parcelUuid, int i2, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean setEncryptionKey(String str, ParcelUuid parcelUuid, byte[] bArr) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean setProperty(String str, ParcelUuid parcelUuid, int i2, byte[] bArr) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean setRssiThreshold(String str, ParcelUuid parcelUuid, int i2) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean supportProperty(String str, int i2) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public boolean unregisterPropertyCallback(String str, ParcelUuid parcelUuid, int i2, IBluetoothMiBlePropertyCallback iBluetoothMiBlePropertyCallback) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public void connect(String str, ParcelUuid parcelUuid) throws RemoteException {
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public void disconnect(String str, ParcelUuid parcelUuid) throws RemoteException {
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public void unregisterClient(IBinder iBinder, String str, ParcelUuid parcelUuid) throws RemoteException {
        }

        @Override // miui.bluetooth.ble.IBluetoothMiBle
        public void registerClient(IBinder iBinder, String str, ParcelUuid parcelUuid, IBluetoothMiBleCallback iBluetoothMiBleCallback) throws RemoteException {
        }
    }
}
