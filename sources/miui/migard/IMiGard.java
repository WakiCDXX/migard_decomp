package miui.migard;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IMiGard extends IInterface {
    public static final String DESCRIPTOR = "miui.migard.IMiGard";

    void addGameCleanUserProtectList(List<String> list, boolean z2) throws RemoteException;

    void configCompactorWhiteList(List<String> list) throws RemoteException;

    void configGameList(List<String> list) throws RemoteException;

    void configGameMemoryCleaner(String str, String str2) throws RemoteException;

    void configKillerWhiteList(List<String> list) throws RemoteException;

    void configPowerWhiteList(String str) throws RemoteException;

    void configTrace(String str) throws RemoteException;

    void dumpTrace(boolean z2) throws RemoteException;

    void reclaimBackgroundMemory() throws RemoteException;

    void removeGameCleanUserProtectList(List<String> list) throws RemoteException;

    void startDefaultTrace(boolean z2) throws RemoteException;

    void startTrace(String[] strArr, boolean z2) throws RemoteException;

    void stopTrace(boolean z2) throws RemoteException;

    public static class Default implements IMiGard {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.migard.IMiGard
        public void reclaimBackgroundMemory() throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void configCompactorWhiteList(List<String> list) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void configGameList(List<String> list) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void configKillerWhiteList(List<String> list) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void configPowerWhiteList(String str) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void configTrace(String str) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void dumpTrace(boolean z2) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void removeGameCleanUserProtectList(List<String> list) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void startDefaultTrace(boolean z2) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void stopTrace(boolean z2) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void addGameCleanUserProtectList(List<String> list, boolean z2) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void configGameMemoryCleaner(String str, String str2) throws RemoteException {
        }

        @Override // miui.migard.IMiGard
        public void startTrace(String[] strArr, boolean z2) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IMiGard {
        static final int TRANSACTION_addGameCleanUserProtectList = 6;
        static final int TRANSACTION_configCompactorWhiteList = 11;
        static final int TRANSACTION_configGameList = 9;
        static final int TRANSACTION_configGameMemoryCleaner = 12;
        static final int TRANSACTION_configKillerWhiteList = 10;
        static final int TRANSACTION_configPowerWhiteList = 8;
        static final int TRANSACTION_configTrace = 5;
        static final int TRANSACTION_dumpTrace = 4;
        static final int TRANSACTION_reclaimBackgroundMemory = 13;
        static final int TRANSACTION_removeGameCleanUserProtectList = 7;
        static final int TRANSACTION_startDefaultTrace = 1;
        static final int TRANSACTION_startTrace = 2;
        static final int TRANSACTION_stopTrace = 3;

        private static class Proxy implements IMiGard {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // miui.migard.IMiGard
            public void addGameCleanUserProtectList(List<String> list, boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeStringList(list);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(6, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // miui.migard.IMiGard
            public void configCompactorWhiteList(List<String> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeStringList(list);
                    this.mRemote.transact(11, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void configGameList(List<String> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeStringList(list);
                    this.mRemote.transact(9, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void configGameMemoryCleaner(String str, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    this.mRemote.transact(12, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void configKillerWhiteList(List<String> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeStringList(list);
                    this.mRemote.transact(10, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void configPowerWhiteList(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(8, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void configTrace(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void dumpTrace(boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IMiGard.DESCRIPTOR;
            }

            @Override // miui.migard.IMiGard
            public void reclaimBackgroundMemory() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    this.mRemote.transact(13, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void removeGameCleanUserProtectList(List<String> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeStringList(list);
                    this.mRemote.transact(7, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void startDefaultTrace(boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void startTrace(String[] strArr, boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeStringArray(strArr);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.migard.IMiGard
            public void stopTrace(boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMiGard.DESCRIPTOR);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMiGard.DESCRIPTOR);
        }

        public static IMiGard asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMiGard.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IMiGard)) ? new Proxy(iBinder) : (IMiGard) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IMiGard.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IMiGard.DESCRIPTOR);
                return true;
            }
            switch (i2) {
                case 1:
                    startDefaultTrace(parcel.readInt() != 0);
                    return true;
                case 2:
                    startTrace(parcel.createStringArray(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 3:
                    stopTrace(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 4:
                    dumpTrace(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 5:
                    configTrace(parcel.readString());
                    return true;
                case 6:
                    addGameCleanUserProtectList(parcel.createStringArrayList(), parcel.readInt() != 0);
                    return true;
                case 7:
                    removeGameCleanUserProtectList(parcel.createStringArrayList());
                    return true;
                case 8:
                    configPowerWhiteList(parcel.readString());
                    return true;
                case 9:
                    configGameList(parcel.createStringArrayList());
                    return true;
                case 10:
                    configKillerWhiteList(parcel.createStringArrayList());
                    return true;
                case 11:
                    configCompactorWhiteList(parcel.createStringArrayList());
                    return true;
                case 12:
                    configGameMemoryCleaner(parcel.readString(), parcel.readString());
                    return true;
                case 13:
                    reclaimBackgroundMemory();
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
}
