package com.miui.powerkeeper.feedbackcontrol;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IFeedbackControl extends IInterface {
    public static final String DESCRIPTOR = "com.miui.powerkeeper.feedbackcontrol.IFeedbackControl";

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

    int getControlFeature() throws RemoteException;

    int getOptimizeVersion() throws RemoteException;

    boolean getThermalSupported() throws RemoteException;

    Bundle getThermalThresholds() throws RemoteException;

    boolean isEnableOptimizeGame() throws RemoteException;

    boolean isOptimizeGame(String str) throws RemoteException;

    void registerFeedbackControlListener(IFeedbackControlListener iFeedbackControlListener) throws RemoteException;

    void restoreThermalMode() throws RemoteException;

    void setControlFeature(int i2) throws RemoteException;

    void setEnableOptimizeGame(boolean z2) throws RemoteException;

    void setOptimizeGameNames(String[] strArr) throws RemoteException;

    void setThermalMode(int i2, String str) throws RemoteException;

    void unregisterFeedbackControlListener(IFeedbackControlListener iFeedbackControlListener) throws RemoteException;

    public static class Default implements IFeedbackControl {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public int getControlFeature() throws RemoteException {
            return 0;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public int getOptimizeVersion() throws RemoteException {
            return 0;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public boolean getThermalSupported() throws RemoteException {
            return false;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public Bundle getThermalThresholds() throws RemoteException {
            return null;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public boolean isEnableOptimizeGame() throws RemoteException {
            return false;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public boolean isOptimizeGame(String str) throws RemoteException {
            return false;
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void restoreThermalMode() throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void registerFeedbackControlListener(IFeedbackControlListener iFeedbackControlListener) throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void setControlFeature(int i2) throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void setEnableOptimizeGame(boolean z2) throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void setOptimizeGameNames(String[] strArr) throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void unregisterFeedbackControlListener(IFeedbackControlListener iFeedbackControlListener) throws RemoteException {
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
        public void setThermalMode(int i2, String str) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IFeedbackControl {
        static final int TRANSACTION_getControlFeature = 2;
        static final int TRANSACTION_getOptimizeVersion = 12;
        static final int TRANSACTION_getThermalSupported = 6;
        static final int TRANSACTION_getThermalThresholds = 5;
        static final int TRANSACTION_isEnableOptimizeGame = 9;
        static final int TRANSACTION_isOptimizeGame = 13;
        static final int TRANSACTION_registerFeedbackControlListener = 3;
        static final int TRANSACTION_restoreThermalMode = 8;
        static final int TRANSACTION_setControlFeature = 1;
        static final int TRANSACTION_setEnableOptimizeGame = 10;
        static final int TRANSACTION_setOptimizeGameNames = 11;
        static final int TRANSACTION_setThermalMode = 7;
        static final int TRANSACTION_unregisterFeedbackControlListener = 4;

        private static class Proxy implements IFeedbackControl {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public int getControlFeature() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IFeedbackControl.DESCRIPTOR;
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public int getOptimizeVersion() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public boolean getThermalSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public Bundle getThermalThresholds() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (Bundle) _Parcel.readTypedObject(parcelObtain2, Bundle.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public boolean isEnableOptimizeGame() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public boolean isOptimizeGame(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public void registerFeedbackControlListener(IFeedbackControlListener iFeedbackControlListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iFeedbackControlListener);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public void restoreThermalMode() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public void setControlFeature(int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public void setEnableOptimizeGame(boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public void setOptimizeGameNames(String[] strArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    parcelObtain.writeStringArray(strArr);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public void setThermalMode(int i2, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControl
            public void unregisterFeedbackControlListener(IFeedbackControlListener iFeedbackControlListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IFeedbackControl.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iFeedbackControlListener);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IFeedbackControl.DESCRIPTOR);
        }

        public static IFeedbackControl asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IFeedbackControl.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IFeedbackControl)) ? new Proxy(iBinder) : (IFeedbackControl) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IFeedbackControl.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IFeedbackControl.DESCRIPTOR);
                return true;
            }
            switch (i2) {
                case 1:
                    setControlFeature(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 2:
                    int controlFeature = getControlFeature();
                    parcel2.writeNoException();
                    parcel2.writeInt(controlFeature);
                    return true;
                case 3:
                    registerFeedbackControlListener(IFeedbackControlListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 4:
                    unregisterFeedbackControlListener(IFeedbackControlListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    Bundle thermalThresholds = getThermalThresholds();
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, thermalThresholds, 1);
                    return true;
                case 6:
                    boolean thermalSupported = getThermalSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(thermalSupported ? 1 : 0);
                    return true;
                case 7:
                    setThermalMode(parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 8:
                    restoreThermalMode();
                    parcel2.writeNoException();
                    return true;
                case 9:
                    boolean zIsEnableOptimizeGame = isEnableOptimizeGame();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsEnableOptimizeGame ? 1 : 0);
                    return true;
                case 10:
                    setEnableOptimizeGame(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 11:
                    setOptimizeGameNames(parcel.createStringArray());
                    parcel2.writeNoException();
                    return true;
                case 12:
                    int optimizeVersion = getOptimizeVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(optimizeVersion);
                    return true;
                case 13:
                    boolean zIsOptimizeGame = isOptimizeGame(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsOptimizeGame ? 1 : 0);
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
