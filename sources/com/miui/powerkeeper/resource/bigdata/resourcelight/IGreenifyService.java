package com.miui.powerkeeper.resource.bigdata.resourcelight;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IGreenifyService extends IInterface {
    public static final String DESCRIPTOR = "com.miui.powerkeeper.resource.bigdata.resourcelight.IGreenifyService";

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

    void triggerGreenifyEngineFromDetailsWakeUp(Bundle bundle) throws RemoteException;

    void triggerGreenifyEngineFromSuspend(Bundle bundle) throws RemoteException;

    public static abstract class Stub extends Binder implements IGreenifyService {
        static final int TRANSACTION_triggerGreenifyEngineFromDetailsWakeUp = 2;
        static final int TRANSACTION_triggerGreenifyEngineFromSuspend = 1;

        private static class Proxy implements IGreenifyService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGreenifyService.DESCRIPTOR;
            }

            @Override // com.miui.powerkeeper.resource.bigdata.resourcelight.IGreenifyService
            public void triggerGreenifyEngineFromDetailsWakeUp(Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IGreenifyService.DESCRIPTOR);
                    _Parcel.writeTypedObject(parcelObtain, bundle, 0);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.resource.bigdata.resourcelight.IGreenifyService
            public void triggerGreenifyEngineFromSuspend(Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IGreenifyService.DESCRIPTOR);
                    _Parcel.writeTypedObject(parcelObtain, bundle, 0);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IGreenifyService.DESCRIPTOR);
        }

        public static IGreenifyService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IGreenifyService.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IGreenifyService)) ? new Proxy(iBinder) : (IGreenifyService) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IGreenifyService.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IGreenifyService.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                triggerGreenifyEngineFromSuspend((Bundle) _Parcel.readTypedObject(parcel, Bundle.CREATOR));
            } else {
                if (i2 != 2) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                triggerGreenifyEngineFromDetailsWakeUp((Bundle) _Parcel.readTypedObject(parcel, Bundle.CREATOR));
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IGreenifyService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.miui.powerkeeper.resource.bigdata.resourcelight.IGreenifyService
        public void triggerGreenifyEngineFromDetailsWakeUp(Bundle bundle) throws RemoteException {
        }

        @Override // com.miui.powerkeeper.resource.bigdata.resourcelight.IGreenifyService
        public void triggerGreenifyEngineFromSuspend(Bundle bundle) throws RemoteException {
        }
    }
}
