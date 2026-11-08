package com.miui.powerkeeper.thermal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IThermalScenarioListener extends IInterface {
    public static final String DESCRIPTOR = "com.miui.powerkeeper.thermal.IThermalScenarioListener";

    void onScenarioChanged(String str) throws RemoteException;

    void onTemperatureChanged(int i2) throws RemoteException;

    public static abstract class Stub extends Binder implements IThermalScenarioListener {
        static final int TRANSACTION_onScenarioChanged = 1;
        static final int TRANSACTION_onTemperatureChanged = 2;

        private static class Proxy implements IThermalScenarioListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IThermalScenarioListener.DESCRIPTOR;
            }

            @Override // com.miui.powerkeeper.thermal.IThermalScenarioListener
            public void onScenarioChanged(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IThermalScenarioListener.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.thermal.IThermalScenarioListener
            public void onTemperatureChanged(int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IThermalScenarioListener.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IThermalScenarioListener.DESCRIPTOR);
        }

        public static IThermalScenarioListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IThermalScenarioListener.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IThermalScenarioListener)) ? new Proxy(iBinder) : (IThermalScenarioListener) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IThermalScenarioListener.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IThermalScenarioListener.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                onScenarioChanged(parcel.readString());
            } else {
                if (i2 != 2) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                onTemperatureChanged(parcel.readInt());
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }

    public static class Default implements IThermalScenarioListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.miui.powerkeeper.thermal.IThermalScenarioListener
        public void onScenarioChanged(String str) throws RemoteException {
        }

        @Override // com.miui.powerkeeper.thermal.IThermalScenarioListener
        public void onTemperatureChanged(int i2) throws RemoteException {
        }
    }
}
