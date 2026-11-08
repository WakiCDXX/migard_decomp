package miui.content.res;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IThemeService extends IInterface {
    public static final String DESCRIPTOR = "miui.content.res.IThemeService";

    public static class Default implements IThemeService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // miui.content.res.IThemeService
        public boolean saveCustomizedIcon(String str, Bitmap bitmap) throws RemoteException {
            return false;
        }

        @Override // miui.content.res.IThemeService
        public boolean saveIcon(String str) throws RemoteException {
            return false;
        }

        @Override // miui.content.res.IThemeService
        public boolean saveLockWallpaper(String str) throws RemoteException {
            return false;
        }

        @Override // miui.content.res.IThemeService
        public boolean saveWallpaper(String str) throws RemoteException {
            return false;
        }
    }

    boolean saveCustomizedIcon(String str, Bitmap bitmap) throws RemoteException;

    boolean saveIcon(String str) throws RemoteException;

    boolean saveLockWallpaper(String str) throws RemoteException;

    boolean saveWallpaper(String str) throws RemoteException;

    public static abstract class Stub extends Binder implements IThemeService {
        static final int TRANSACTION_saveCustomizedIcon = 4;
        static final int TRANSACTION_saveIcon = 2;
        static final int TRANSACTION_saveLockWallpaper = 1;
        static final int TRANSACTION_saveWallpaper = 3;

        private static class Proxy implements IThemeService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IThemeService.DESCRIPTOR;
            }

            @Override // miui.content.res.IThemeService
            public boolean saveCustomizedIcon(String str, Bitmap bitmap) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IThemeService.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeTypedObject(bitmap, 0);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.content.res.IThemeService
            public boolean saveIcon(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IThemeService.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.content.res.IThemeService
            public boolean saveLockWallpaper(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IThemeService.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // miui.content.res.IThemeService
            public boolean saveWallpaper(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IThemeService.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IThemeService.DESCRIPTOR);
        }

        public static IThemeService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IThemeService.DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IThemeService)) ? new Proxy(iBinder) : (IThemeService) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i2, Parcel parcel, Parcel parcel2, int i3) throws RemoteException {
            if (i2 >= 1 && i2 <= 16777215) {
                parcel.enforceInterface(IThemeService.DESCRIPTOR);
            }
            if (i2 == 1598968902) {
                parcel2.writeString(IThemeService.DESCRIPTOR);
                return true;
            }
            if (i2 == 1) {
                boolean zSaveLockWallpaper = saveLockWallpaper(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(zSaveLockWallpaper ? 1 : 0);
            } else if (i2 == 2) {
                boolean zSaveIcon = saveIcon(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(zSaveIcon ? 1 : 0);
            } else if (i2 == 3) {
                boolean zSaveWallpaper = saveWallpaper(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(zSaveWallpaper ? 1 : 0);
            } else {
                if (i2 != 4) {
                    return super.onTransact(i2, parcel, parcel2, i3);
                }
                boolean zSaveCustomizedIcon = saveCustomizedIcon(parcel.readString(), (Bitmap) parcel.readTypedObject(Bitmap.CREATOR));
                parcel2.writeNoException();
                parcel2.writeInt(zSaveCustomizedIcon ? 1 : 0);
            }
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }
    }
}
