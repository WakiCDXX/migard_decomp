package com.miui.powerkeeper;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.miui.powerkeeper.ui.AppStateInfo;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IPowerKeeper extends IInterface {
    public static final String DESCRIPTOR = "com.miui.powerkeeper.IPowerKeeper";

    public static class a {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T> T d(Parcel parcel, Parcelable.Creator<T> creator) {
            if (parcel.readInt() != 0) {
                return creator.createFromParcel(parcel);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void e(Parcel parcel, List<T> list, int i2) {
            if (list == null) {
                parcel.writeInt(-1);
                return;
            }
            int size = list.size();
            parcel.writeInt(size);
            for (int i3 = 0; i3 < size; i3++) {
                f(parcel, list.get(i3), i2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void f(Parcel parcel, T t2, int i2) {
            if (t2 == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                t2.writeToParcel(parcel, i2);
            }
        }
    }

    List<AppStateInfo> getHideModeAppInfoList() throws RemoteException;

    int getPowerSaveAppConfigure(Bundle bundle, Bundle bundle2) throws RemoteException;

    boolean isSupportVideoEnhancePkg(String str) throws RemoteException;

    void restrictAppQuick(int i2) throws RemoteException;

    int setPowerSaveAppConfigure(Bundle bundle) throws RemoteException;

    public static abstract class Stub extends Binder implements IPowerKeeper {
        static final int TRANSACTION_getHideModeAppInfoList = 4;
        static final int TRANSACTION_getPowerSaveAppConfigure = 3;
        static final int TRANSACTION_isSupportVideoEnhancePkg = 5;
        static final int TRANSACTION_restrictAppQuick = 1;
        static final int TRANSACTION_setPowerSaveAppConfigure = 2;

        private static class Proxy implements IPowerKeeper {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.miui.powerkeeper.IPowerKeeper
            public List<AppStateInfo> getHideModeAppInfoList() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPowerKeeper.DESCRIPTOR);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createTypedArrayList(AppStateInfo.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IPowerKeeper.DESCRIPTOR;
            }

            @Override // com.miui.powerkeeper.IPowerKeeper
            public int getPowerSaveAppConfigure(Bundle bundle, Bundle bundle2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPowerKeeper.DESCRIPTOR);
                    a.f(parcelObtain, bundle, 0);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    int i2 = parcelObtain2.readInt();
                    if (parcelObtain2.readInt() != 0) {
                        bundle2.readFromParcel(parcelObtain2);
                    }
                    return i2;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.IPowerKeeper
            public boolean isSupportVideoEnhancePkg(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPowerKeeper.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.IPowerKeeper
            public void restrictAppQuick(int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPowerKeeper.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.IPowerKeeper
            public int setPowerSaveAppConfigure(Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPowerKeeper.DESCRIPTOR);
                    a.f(parcelObtain, bundle, 0);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IPowerKeeper.DESCRIPTOR);
        }

        public static IPowerKeeper asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IPowerKeeper.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IPowerKeeper)) ? new Proxy(iBinder) : (IPowerKeeper) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IPowerKeeper.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IPowerKeeper.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                restrictAppQuick(parcel.readInt());
                parcel2.writeNoException();
            } else if (i2 == 2) {
                int powerSaveAppConfigure = setPowerSaveAppConfigure((Bundle) a.d(parcel, Bundle.CREATOR));
                parcel2.writeNoException();
                parcel2.writeInt(powerSaveAppConfigure);
            } else if (i2 == 3) {
                Bundle bundle = (Bundle) a.d(parcel, Bundle.CREATOR);
                Bundle bundle2 = new Bundle();
                int powerSaveAppConfigure2 = getPowerSaveAppConfigure(bundle, bundle2);
                parcel2.writeNoException();
                parcel2.writeInt(powerSaveAppConfigure2);
                a.f(parcel2, bundle2, 1);
            } else if (i2 == 4) {
                List<AppStateInfo> hideModeAppInfoList = getHideModeAppInfoList();
                parcel2.writeNoException();
                a.e(parcel2, hideModeAppInfoList, 1);
            } else {
                if (i2 != 5) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                boolean zIsSupportVideoEnhancePkg = isSupportVideoEnhancePkg(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(zIsSupportVideoEnhancePkg ? 1 : 0);
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IPowerKeeper {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.miui.powerkeeper.IPowerKeeper
        public List<AppStateInfo> getHideModeAppInfoList() throws RemoteException {
            return null;
        }

        @Override // com.miui.powerkeeper.IPowerKeeper
        public int getPowerSaveAppConfigure(Bundle bundle, Bundle bundle2) throws RemoteException {
            return 0;
        }

        @Override // com.miui.powerkeeper.IPowerKeeper
        public boolean isSupportVideoEnhancePkg(String str) throws RemoteException {
            return false;
        }

        @Override // com.miui.powerkeeper.IPowerKeeper
        public int setPowerSaveAppConfigure(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.miui.powerkeeper.IPowerKeeper
        public void restrictAppQuick(int i2) throws RemoteException {
        }
    }
}
