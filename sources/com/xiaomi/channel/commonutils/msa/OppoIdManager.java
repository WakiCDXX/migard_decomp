package com.xiaomi.channel.commonutils.msa;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.security.MessageDigest;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
class OppoIdManager implements IdManager {
    private static final int OAID_SUPPORT_VERSION = 1;
    private static final String SERVICE_ACTION = "action.com.heytap.openid.OPEN_ID_SERVICE";
    private static final String SERVICE_CLASS = "com.heytap.openid.IdentifyService";
    private static final String SERVICE_PACKAGE_NAME = "com.heytap.openid";
    private static final int STATE_BINDING = 1;
    private static final int STATE_END = 2;
    private static final int STATE_NONE = 0;
    private static final int TIME_WAIT_LOCK = 3000;
    private static boolean sIsSupport;
    private Context mContext;
    private ServiceConnection mServiceConnection;
    private volatile int mState = 0;
    private volatile IdData mIdData = null;
    private final Object mLockObj = new Object();

    private class IdData {
        String aaid;
        String oaid;
        String udid;
        String vaid;

        private IdData() {
            this.udid = null;
            this.oaid = null;
            this.vaid = null;
            this.aaid = null;
        }
    }

    private static class ServiceRemote {
        static final String DESCRIPTOR = "com.heytap.openid.IOpenID";
        static final int TRANSACTION_getOpenId = 1;

        private ServiceRemote() {
        }

        static String getOpenId(IBinder iBinder, String str, String str2, String str3) throws RemoteException {
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                parcelObtain.writeInterfaceToken(DESCRIPTOR);
                parcelObtain.writeString(str);
                parcelObtain.writeString(str2);
                parcelObtain.writeString(str3);
                iBinder.transact(1, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                return parcelObtain2.readString();
            } finally {
                parcelObtain2.recycle();
                parcelObtain.recycle();
            }
        }
    }

    public OppoIdManager(Context context) {
        this.mContext = context;
        bindService();
    }

    private void bindService() {
        boolean zBindService;
        this.mServiceConnection = new IdentifierServiceConnection();
        Intent intent = new Intent();
        intent.setClassName(SERVICE_PACKAGE_NAME, SERVICE_CLASS);
        intent.setAction(SERVICE_ACTION);
        try {
            zBindService = this.mContext.bindService(intent, this.mServiceConnection, 1);
        } catch (Exception unused) {
            zBindService = false;
        }
        this.mState = zBindService ? 1 : 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getAppSignaturesSha1() {
        try {
            Signature[] signatureArr = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 64).signatures;
            MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.SHA1);
            StringBuilder sb = new StringBuilder();
            for (byte b2 : messageDigest.digest(signatureArr[0].toByteArray())) {
                sb.append(Integer.toHexString((b2 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) | 256).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception unused) {
            return "";
        }
    }

    public static boolean isOppoPhone(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(SERVICE_PACKAGE_NAME, 128);
            if (packageInfo != null) {
                long longVersionCode = packageInfo.getLongVersionCode();
                boolean z2 = (packageInfo.applicationInfo.flags & 1) != 0;
                sIsSupport = longVersionCode >= 1;
                if (z2) {
                    return true;
                }
            }
        } catch (Exception unused) {
        }
        return false;
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
                MyLog.w("oppo's " + str + " wait...");
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
        if (this.mIdData == null) {
            return null;
        }
        return this.mIdData.oaid;
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
        return true;
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
            if (OppoIdManager.this.mIdData != null) {
                return;
            }
            new Thread(new Runnable() { // from class: com.xiaomi.channel.commonutils.msa.OppoIdManager.IdentifierServiceConnection.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        String packageName = OppoIdManager.this.mContext.getPackageName();
                        String appSignaturesSha1 = OppoIdManager.this.getAppSignaturesSha1();
                        IdData idData = new IdData();
                        idData.oaid = ServiceRemote.getOpenId(iBinder, packageName, appSignaturesSha1, "OUID");
                        OppoIdManager.this.mIdData = idData;
                        OppoIdManager.this.unbindService();
                        OppoIdManager.this.mState = 2;
                        synchronized (OppoIdManager.this.mLockObj) {
                            try {
                                OppoIdManager.this.mLockObj.notifyAll();
                            } catch (Exception unused) {
                            }
                        }
                    } catch (Exception unused2) {
                        OppoIdManager.this.unbindService();
                        OppoIdManager.this.mState = 2;
                        synchronized (OppoIdManager.this.mLockObj) {
                            try {
                                OppoIdManager.this.mLockObj.notifyAll();
                            } catch (Exception unused3) {
                            }
                        }
                    } catch (Throwable th) {
                        OppoIdManager.this.unbindService();
                        OppoIdManager.this.mState = 2;
                        synchronized (OppoIdManager.this.mLockObj) {
                            try {
                                OppoIdManager.this.mLockObj.notifyAll();
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
