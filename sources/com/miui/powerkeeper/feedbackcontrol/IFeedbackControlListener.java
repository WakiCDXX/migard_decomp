package com.miui.powerkeeper.feedbackcontrol;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IFeedbackControlListener extends IInterface {
    public static final String DESCRIPTOR = "com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener";

    void batteryBecomeHeat() throws RemoteException;

    void batteryCoolDown() throws RemoteException;

    void dataWifiSettingToDisable() throws RemoteException;

    void dataWifiSettingToEnable() throws RemoteException;

    void googleNetworkConnected() throws RemoteException;

    void googleNetworkDisconnect() throws RemoteException;

    void networkDisconnectFromInternet() throws RemoteException;

    void networkReconnectToInternet() throws RemoteException;

    public static class Default implements IFeedbackControlListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
        public void batteryBecomeHeat() throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
        public void batteryCoolDown() throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
        public void dataWifiSettingToDisable() throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
        public void dataWifiSettingToEnable() throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
        public void googleNetworkConnected() throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
        public void googleNetworkDisconnect() throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
        public void networkDisconnectFromInternet() throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
        public void networkReconnectToInternet() throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IFeedbackControlListener {
        static final int TRANSACTION_batteryBecomeHeat = 1;
        static final int TRANSACTION_batteryCoolDown = 2;
        static final int TRANSACTION_dataWifiSettingToDisable = 7;
        static final int TRANSACTION_dataWifiSettingToEnable = 8;
        static final int TRANSACTION_googleNetworkConnected = 5;
        static final int TRANSACTION_googleNetworkDisconnect = 6;
        static final int TRANSACTION_networkDisconnectFromInternet = 3;
        static final int TRANSACTION_networkReconnectToInternet = 4;

        private static class Proxy implements IFeedbackControlListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
            public void batteryBecomeHeat() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControlListener.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
            public void batteryCoolDown() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControlListener.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
            public void dataWifiSettingToDisable() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControlListener.DESCRIPTOR);
                    this.mRemote.transact(7, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
            public void dataWifiSettingToEnable() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControlListener.DESCRIPTOR);
                    this.mRemote.transact(8, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IFeedbackControlListener.DESCRIPTOR;
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
            public void googleNetworkConnected() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControlListener.DESCRIPTOR);
                    this.mRemote.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
            public void googleNetworkDisconnect() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControlListener.DESCRIPTOR);
                    this.mRemote.transact(6, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
            public void networkDisconnectFromInternet() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControlListener.DESCRIPTOR);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
            public void networkReconnectToInternet() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControlListener.DESCRIPTOR);
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IFeedbackControlListener.DESCRIPTOR);
        }

        public static IFeedbackControlListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IFeedbackControlListener.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IFeedbackControlListener)) ? new Proxy(iBinder) : (IFeedbackControlListener) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IFeedbackControlListener.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IFeedbackControlListener.DESCRIPTOR);
                return true;
            }
            switch (i2) {
                case 1:
                    batteryBecomeHeat();
                    return true;
                case 2:
                    batteryCoolDown();
                    return true;
                case 3:
                    networkDisconnectFromInternet();
                    return true;
                case 4:
                    networkReconnectToInternet();
                    return true;
                case 5:
                    googleNetworkConnected();
                    return true;
                case 6:
                    googleNetworkDisconnect();
                    return true;
                case 7:
                    dataWifiSettingToDisable();
                    return true;
                case 8:
                    dataWifiSettingToEnable();
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
