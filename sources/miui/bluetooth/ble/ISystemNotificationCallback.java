package miui.bluetooth.ble;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ISystemNotificationCallback extends IInterface {
    public static final String DESCRIPTOR = "miui.bluetooth.ble.ISystemNotificationCallback";

    void onSystemNotification(String str, int i2, byte[] bArr) throws RemoteException;

    public static abstract class Stub extends Binder implements ISystemNotificationCallback {
        static final int TRANSACTION_onSystemNotification = 1;

        private static class Proxy implements ISystemNotificationCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISystemNotificationCallback.DESCRIPTOR;
            }

            @Override // miui.bluetooth.ble.ISystemNotificationCallback
            public void onSystemNotification(String str, int i2, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemNotificationCallback.DESCRIPTOR);
                    parcelObtain.writeString(str);
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
            attachInterface(this, ISystemNotificationCallback.DESCRIPTOR);
        }

        public static ISystemNotificationCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(ISystemNotificationCallback.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ISystemNotificationCallback)) ? new Proxy(iBinder) : (ISystemNotificationCallback) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(ISystemNotificationCallback.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(ISystemNotificationCallback.DESCRIPTOR);
                return true;
            }
            if (i2 != 1) {
                return super.onTransact(i2, parcel, parcel2, i3);
            }
            onSystemNotification(parcel.readString(), parcel.readInt(), parcel.createByteArray());
            parcel2.writeNoException();
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements ISystemNotificationCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.bluetooth.ble.ISystemNotificationCallback
        public void onSystemNotification(String str, int i2, byte[] bArr) throws RemoteException {
        }
    }
}
