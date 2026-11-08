package miui.bluetooth.ble;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.RemoteException;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public interface IMiBleDeviceManager extends IInterface {
    public static final String DESCRIPTOR = "miui.bluetooth.ble.IMiBleDeviceManager";

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

    boolean deleteSettings(String str) throws RemoteException;

    List<String> getBoundDevices() throws RemoteException;

    Map getDeviceSettings(String str) throws RemoteException;

    int getDeviceType(String str) throws RemoteException;

    String getRegisterAppForBleEvent(String str, int i2) throws RemoteException;

    ScanResult getScanResult(String str) throws RemoteException;

    int getServiceVersion() throws RemoteException;

    int getSettingInteger(String str, String str2) throws RemoteException;

    String getSettingString(String str, String str2) throws RemoteException;

    boolean isRegisted(String str) throws RemoteException;

    boolean registerAppForBleEvent(String str, int i2) throws RemoteException;

    boolean registerBleEventListener(String str, int i2, IBleEventCallback iBleEventCallback) throws RemoteException;

    boolean registerSysNotificationService(String str, String str2, ISystemNotificationCallback iSystemNotificationCallback) throws RemoteException;

    boolean setSettingInteger(String str, String str2, int i2) throws RemoteException;

    boolean setSettingString(String str, String str2, String str3) throws RemoteException;

    void setSysNotificationCallback(String str, ISystemNotificationCallback iSystemNotificationCallback) throws RemoteException;

    boolean setToken(String str, byte[] bArr) throws RemoteException;

    boolean startScanDevice(IBinder iBinder, ParcelUuid parcelUuid, int i2, IScanDeviceCallback iScanDeviceCallback) throws RemoteException;

    void stopScanDevice(ParcelUuid parcelUuid) throws RemoteException;

    boolean unRegisterSysNotificationService(String str) throws RemoteException;

    boolean unregisterAppForBleEvent(String str, int i2) throws RemoteException;

    boolean unregisterBleEventListener(String str, int i2, IBleEventCallback iBleEventCallback) throws RemoteException;

    public static abstract class Stub extends Binder implements IMiBleDeviceManager {
        static final int TRANSACTION_deleteSettings = 6;
        static final int TRANSACTION_getBoundDevices = 13;
        static final int TRANSACTION_getDeviceSettings = 5;
        static final int TRANSACTION_getDeviceType = 7;
        static final int TRANSACTION_getRegisterAppForBleEvent = 16;
        static final int TRANSACTION_getScanResult = 17;
        static final int TRANSACTION_getServiceVersion = 12;
        static final int TRANSACTION_getSettingInteger = 4;
        static final int TRANSACTION_getSettingString = 2;
        static final int TRANSACTION_isRegisted = 22;
        static final int TRANSACTION_registerAppForBleEvent = 14;
        static final int TRANSACTION_registerBleEventListener = 10;
        static final int TRANSACTION_registerSysNotificationService = 19;
        static final int TRANSACTION_setSettingInteger = 3;
        static final int TRANSACTION_setSettingString = 1;
        static final int TRANSACTION_setSysNotificationCallback = 21;
        static final int TRANSACTION_setToken = 18;
        static final int TRANSACTION_startScanDevice = 8;
        static final int TRANSACTION_stopScanDevice = 9;
        static final int TRANSACTION_unRegisterSysNotificationService = 20;
        static final int TRANSACTION_unregisterAppForBleEvent = 15;
        static final int TRANSACTION_unregisterBleEventListener = 11;

        private static class Proxy implements IMiBleDeviceManager {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean deleteSettings(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public List<String> getBoundDevices() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArrayList();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public Map getDeviceSettings(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public int getDeviceType(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IMiBleDeviceManager.DESCRIPTOR;
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public String getRegisterAppForBleEvent(String str, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(16, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public ScanResult getScanResult(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (ScanResult) _Parcel.readTypedObject(parcelObtain2, ScanResult.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public int getServiceVersion() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public int getSettingInteger(String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public String getSettingString(String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean isRegisted(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(22, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean registerAppForBleEvent(String str, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean registerBleEventListener(String str, int i2, IBleEventCallback iBleEventCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongInterface(iBleEventCallback);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean registerSysNotificationService(String str, String str2, ISystemNotificationCallback iSystemNotificationCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeStrongInterface(iSystemNotificationCallback);
                    this.mRemote.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean setSettingInteger(String str, String str2, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean setSettingString(String str, String str2, String str3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeString(str3);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public void setSysNotificationCallback(String str, ISystemNotificationCallback iSystemNotificationCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeStrongInterface(iSystemNotificationCallback);
                    this.mRemote.transact(21, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean setToken(String str, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(18, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean startScanDevice(IBinder iBinder, ParcelUuid parcelUuid, int i2, IScanDeviceCallback iScanDeviceCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongInterface(iScanDeviceCallback);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public void stopScanDevice(ParcelUuid parcelUuid) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    _Parcel.writeTypedObject(parcelObtain, parcelUuid, 0);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean unRegisterSysNotificationService(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(20, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean unregisterAppForBleEvent(String str, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.bluetooth.ble.IMiBleDeviceManager
            public boolean unregisterBleEventListener(String str, int i2, IBleEventCallback iBleEventCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiBleDeviceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongInterface(iBleEventCallback);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMiBleDeviceManager.DESCRIPTOR);
        }

        public static IMiBleDeviceManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMiBleDeviceManager.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMiBleDeviceManager)) ? new Proxy(iBinder) : (IMiBleDeviceManager) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IMiBleDeviceManager.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IMiBleDeviceManager.DESCRIPTOR);
                return true;
            }
            switch (i2) {
                case 1:
                    boolean settingString = setSettingString(parcel.readString(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(settingString ? 1 : 0);
                    return true;
                case 2:
                    String settingString2 = getSettingString(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeString(settingString2);
                    return true;
                case 3:
                    boolean settingInteger = setSettingInteger(parcel.readString(), parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(settingInteger ? 1 : 0);
                    return true;
                case 4:
                    int settingInteger2 = getSettingInteger(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(settingInteger2);
                    return true;
                case 5:
                    Map deviceSettings = getDeviceSettings(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeMap(deviceSettings);
                    return true;
                case 6:
                    boolean zDeleteSettings = deleteSettings(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zDeleteSettings ? 1 : 0);
                    return true;
                case 7:
                    int deviceType = getDeviceType(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(deviceType);
                    return true;
                case 8:
                    boolean zStartScanDevice = startScanDevice(parcel.readStrongBinder(), (ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR), parcel.readInt(), IScanDeviceCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(zStartScanDevice ? 1 : 0);
                    return true;
                case 9:
                    stopScanDevice((ParcelUuid) _Parcel.readTypedObject(parcel, ParcelUuid.CREATOR));
                    parcel2.writeNoException();
                    return true;
                case 10:
                    boolean zRegisterBleEventListener = registerBleEventListener(parcel.readString(), parcel.readInt(), IBleEventCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(zRegisterBleEventListener ? 1 : 0);
                    return true;
                case 11:
                    boolean zUnregisterBleEventListener = unregisterBleEventListener(parcel.readString(), parcel.readInt(), IBleEventCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(zUnregisterBleEventListener ? 1 : 0);
                    return true;
                case 12:
                    int serviceVersion = getServiceVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(serviceVersion);
                    return true;
                case 13:
                    List<String> boundDevices = getBoundDevices();
                    parcel2.writeNoException();
                    parcel2.writeStringList(boundDevices);
                    return true;
                case 14:
                    boolean zRegisterAppForBleEvent = registerAppForBleEvent(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zRegisterAppForBleEvent ? 1 : 0);
                    return true;
                case 15:
                    boolean zUnregisterAppForBleEvent = unregisterAppForBleEvent(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zUnregisterAppForBleEvent ? 1 : 0);
                    return true;
                case 16:
                    String registerAppForBleEvent = getRegisterAppForBleEvent(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeString(registerAppForBleEvent);
                    return true;
                case 17:
                    ScanResult scanResult = getScanResult(parcel.readString());
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, scanResult, 1);
                    return true;
                case 18:
                    boolean token = setToken(parcel.readString(), parcel.createByteArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(token ? 1 : 0);
                    return true;
                case 19:
                    boolean zRegisterSysNotificationService = registerSysNotificationService(parcel.readString(), parcel.readString(), ISystemNotificationCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(zRegisterSysNotificationService ? 1 : 0);
                    return true;
                case 20:
                    boolean zUnRegisterSysNotificationService = unRegisterSysNotificationService(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zUnRegisterSysNotificationService ? 1 : 0);
                    return true;
                case 21:
                    setSysNotificationCallback(parcel.readString(), ISystemNotificationCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 22:
                    boolean zIsRegisted = isRegisted(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsRegisted ? 1 : 0);
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

    public static class Default implements IMiBleDeviceManager {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean deleteSettings(String str) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public List<String> getBoundDevices() throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public Map getDeviceSettings(String str) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public int getDeviceType(String str) throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public String getRegisterAppForBleEvent(String str, int i2) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public ScanResult getScanResult(String str) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public int getServiceVersion() throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public int getSettingInteger(String str, String str2) throws RemoteException {
            return 0;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public String getSettingString(String str, String str2) throws RemoteException {
            return null;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean isRegisted(String str) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean registerAppForBleEvent(String str, int i2) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean registerBleEventListener(String str, int i2, IBleEventCallback iBleEventCallback) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean registerSysNotificationService(String str, String str2, ISystemNotificationCallback iSystemNotificationCallback) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean setSettingInteger(String str, String str2, int i2) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean setSettingString(String str, String str2, String str3) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean setToken(String str, byte[] bArr) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean startScanDevice(IBinder iBinder, ParcelUuid parcelUuid, int i2, IScanDeviceCallback iScanDeviceCallback) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean unRegisterSysNotificationService(String str) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean unregisterAppForBleEvent(String str, int i2) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public boolean unregisterBleEventListener(String str, int i2, IBleEventCallback iBleEventCallback) throws RemoteException {
            return false;
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public void stopScanDevice(ParcelUuid parcelUuid) throws RemoteException {
        }

        @Override // miui.bluetooth.ble.IMiBleDeviceManager
        public void setSysNotificationCallback(String str, ISystemNotificationCallback iSystemNotificationCallback) throws RemoteException {
        }
    }
}
