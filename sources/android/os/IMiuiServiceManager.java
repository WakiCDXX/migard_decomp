package android.os;

/* JADX INFO: loaded from: classes.dex */
public interface IMiuiServiceManager extends IInterface {
    public static final String DESCRIPTOR = "android.os.IMiuiServiceManager";

    void addService(String str, IBinder iBinder) throws RemoteException;

    IBinder getService(String str) throws RemoteException;

    String[] listServices() throws RemoteException;

    public static abstract class Stub extends Binder implements IMiuiServiceManager {
        static final int TRANSACTION_addService = 2;
        static final int TRANSACTION_getService = 1;
        static final int TRANSACTION_listServices = 3;

        private static class Proxy implements IMiuiServiceManager {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IMiuiServiceManager
            public void addService(String str, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiuiServiceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMiuiServiceManager.DESCRIPTOR;
            }

            @Override // android.os.IMiuiServiceManager
            public IBinder getService(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiuiServiceManager.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readStrongBinder();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.IMiuiServiceManager
            public String[] listServices() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiuiServiceManager.DESCRIPTOR);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createStringArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMiuiServiceManager.DESCRIPTOR);
        }

        public static IMiuiServiceManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMiuiServiceManager.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMiuiServiceManager)) ? new Proxy(iBinder) : (IMiuiServiceManager) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IMiuiServiceManager.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IMiuiServiceManager.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                IBinder service = getService(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeStrongBinder(service);
            } else if (i2 == 2) {
                addService(parcel.readString(), parcel.readStrongBinder());
                parcel2.writeNoException();
            } else {
                if (i2 != 3) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                String[] strArrListServices = listServices();
                parcel2.writeNoException();
                parcel2.writeStringArray(strArrListServices);
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IMiuiServiceManager {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.os.IMiuiServiceManager
        public IBinder getService(String str) throws RemoteException {
            return null;
        }

        @Override // android.os.IMiuiServiceManager
        public String[] listServices() throws RemoteException {
            return null;
        }

        @Override // android.os.IMiuiServiceManager
        public void addService(String str, IBinder iBinder) throws RemoteException {
        }
    }
}
