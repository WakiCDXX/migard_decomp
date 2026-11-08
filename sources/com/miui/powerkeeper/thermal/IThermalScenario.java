package com.miui.powerkeeper.thermal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IThermalScenario extends IInterface {
    public static final String DESCRIPTOR = "com.miui.powerkeeper.thermal.IThermalScenario";

    int isInCastingWithCamera() throws RemoteException;

    void registerListener(int[] iArr, IThermalScenarioListener iThermalScenarioListener) throws RemoteException;

    void unregisterListener(IThermalScenarioListener iThermalScenarioListener) throws RemoteException;

    public static abstract class Stub extends Binder implements IThermalScenario {
        static final int TRANSACTION_isInCastingWithCamera = 1;
        static final int TRANSACTION_registerListener = 2;
        static final int TRANSACTION_unregisterListener = 3;

        private static class Proxy implements IThermalScenario {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IThermalScenario.DESCRIPTOR;
            }

            @Override // com.miui.powerkeeper.thermal.IThermalScenario
            public int isInCastingWithCamera() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IThermalScenario.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.thermal.IThermalScenario
            public void registerListener(int[] iArr, IThermalScenarioListener iThermalScenarioListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IThermalScenario.DESCRIPTOR);
                    parcelObtain.writeIntArray(iArr);
                    parcelObtain.writeStrongInterface(iThermalScenarioListener);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.thermal.IThermalScenario
            public void unregisterListener(IThermalScenarioListener iThermalScenarioListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IThermalScenario.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iThermalScenarioListener);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IThermalScenario.DESCRIPTOR);
        }

        public static IThermalScenario asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IThermalScenario.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IThermalScenario)) ? new Proxy(iBinder) : (IThermalScenario) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IThermalScenario.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IThermalScenario.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                int iIsInCastingWithCamera = isInCastingWithCamera();
                parcel2.writeNoException();
                parcel2.writeInt(iIsInCastingWithCamera);
            } else if (i2 == 2) {
                registerListener(parcel.createIntArray(), IThermalScenarioListener.Stub.asInterface(parcel.readStrongBinder()));
            } else {
                if (i2 != 3) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                unregisterListener(IThermalScenarioListener.Stub.asInterface(parcel.readStrongBinder()));
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IThermalScenario {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.miui.powerkeeper.thermal.IThermalScenario
        public int isInCastingWithCamera() throws RemoteException {
            return 0;
        }

        @Override // com.miui.powerkeeper.thermal.IThermalScenario
        public void unregisterListener(IThermalScenarioListener iThermalScenarioListener) throws RemoteException {
        }

        @Override // com.miui.powerkeeper.thermal.IThermalScenario
        public void registerListener(int[] iArr, IThermalScenarioListener iThermalScenarioListener) throws RemoteException {
        }
    }
}
