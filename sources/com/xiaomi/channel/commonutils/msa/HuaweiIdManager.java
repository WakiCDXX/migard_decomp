package com.xiaomi.channel.commonutils.msa;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
class HuaweiIdManager implements IdManager {
    private static final int OAID_SUPPORT_VERSION = 20602000;
    private static final String SERVICE_ACTION = "com.uodis.opendevice.OPENIDS_SERVICE";
    private static final String SERVICE_PACKAGE_NAME = "com.huawei.hwid";
    private static final String SP_NAME_AAID = "aaid";
    private static final int STATE_BINDING = 1;
    private static final int STATE_END = 2;
    private static final int STATE_NONE = 0;
    private static final int TIME_WAIT_LOCK = 3000;
    private static boolean sIsSupport;
    private Context mContext;
    private ServiceConnection mServiceConnection;
    private volatile int mState = 0;
    private volatile String mOaid = null;
    private volatile boolean mIsOaidLimited = false;
    private volatile String mAaid = null;
    private final Object mLockObj = new Object();

    private static class ServiceRemote {
        static final String DESCRIPTOR = "com.uodis.opendevice.aidl.OpenDeviceIdentifierService";
        static final int TRANSACTION_getOaid = 1;
        static final int TRANSACTION_isOaidTrackLimited = 2;

        private ServiceRemote() {
        }

        static String getOaid(IBinder iBinder) throws RemoteException {
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                parcelObtain.writeInterfaceToken(DESCRIPTOR);
                iBinder.transact(1, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                return parcelObtain2.readString();
            } finally {
                parcelObtain2.recycle();
                parcelObtain.recycle();
            }
        }

        static boolean isOaidTrackLimited(IBinder iBinder) throws RemoteException {
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                parcelObtain.writeInterfaceToken(DESCRIPTOR);
                iBinder.transact(2, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                return parcelObtain2.readInt() != 0;
            } finally {
                parcelObtain2.recycle();
                parcelObtain.recycle();
            }
        }
    }

    public HuaweiIdManager(Context context) {
        this.mContext = context;
        bindService();
    }

    private void bindService() {
        boolean zBindService;
        this.mServiceConnection = new IdentifierServiceConnection();
        Intent intent = new Intent(SERVICE_ACTION);
        intent.setPackage("com.huawei.hwid");
        try {
            zBindService = this.mContext.bindService(intent, this.mServiceConnection, 1);
        } catch (Exception unused) {
            zBindService = false;
        }
        this.mState = zBindService ? 1 : 2;
    }

    private static String getAaidFromSp(Context context) {
        String string;
        String str = null;
        try {
            String string2 = context.createDeviceProtectedStorageContext().getSharedPreferences("aaid", 0).getString("aaid", null);
            if (string2 != null) {
                return string2;
            }
            try {
                string = context.getSharedPreferences("aaid", 0).getString("aaid", null);
            } catch (Exception unused) {
                str = string2;
                string = str;
            }
            return string == null ? "" : string;
        } catch (Exception unused2) {
        }
    }

    public static boolean isHuaweiPhone(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.huawei.hwid", 128);
            boolean z2 = (packageInfo.applicationInfo.flags & 1) != 0;
            sIsSupport = packageInfo.versionCode >= OAID_SUPPORT_VERSION;
            return z2;
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindService() {
        ServiceConnection serviceConnection = this.mServiceConnection;
        if (serviceConnection != null) {
            try {
                this.mContext.unbindService(serviceConnection);
            } catch (Exception unused) {
            }
        }
    }

    private void waitIfNeed(String str) {
        if (this.mState != 1 || Looper.myLooper() == Looper.getMainLooper()) {
            return;
        }
        synchronized (this.mLockObj) {
            try {
                MyLog.w("huawei's " + str + " wait...");
                this.mLockObj.wait(3000L);
            } catch (Exception unused) {
            }
        }
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getAAID() {
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getOAID() {
        waitIfNeed("getOAID");
        return this.mOaid;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getUDID() {
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public String getVAID() {
        return null;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public boolean isAllowOAID() {
        waitIfNeed("isAllowOAID");
        return !this.mIsOaidLimited;
    }

    @Override // com.xiaomi.channel.commonutils.msa.IdManager
    public boolean isSupported() {
        return sIsSupport;
    }

    private class IdentifierServiceConnection implements ServiceConnection {
        private IdentifierServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, final IBinder iBinder) {
            new Thread(new Runnable() { // from class: com.xiaomi.channel.commonutils.msa.HuaweiIdManager.IdentifierServiceConnection.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        HuaweiIdManager.this.mOaid = ServiceRemote.getOaid(iBinder);
                        HuaweiIdManager.this.mIsOaidLimited = ServiceRemote.isOaidTrackLimited(iBinder);
                        HuaweiIdManager.this.unbindService();
                        HuaweiIdManager.this.mState = 2;
                        synchronized (HuaweiIdManager.this.mLockObj) {
                            try {
                                HuaweiIdManager.this.mLockObj.notifyAll();
                            } catch (Exception unused) {
                            }
                        }
                    } catch (Exception unused2) {
                        HuaweiIdManager.this.unbindService();
                        HuaweiIdManager.this.mState = 2;
                        synchronized (HuaweiIdManager.this.mLockObj) {
                            try {
                                HuaweiIdManager.this.mLockObj.notifyAll();
                            } catch (Exception unused3) {
                            }
                        }
                    } catch (Throwable th) {
                        HuaweiIdManager.this.unbindService();
                        HuaweiIdManager.this.mState = 2;
                        synchronized (HuaweiIdManager.this.mLockObj) {
                            try {
                                HuaweiIdManager.this.mLockObj.notifyAll();
                            } catch (Exception unused4) {
                            }
                            throw th;
                        }
                    }
                }
            }).start();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }
}
