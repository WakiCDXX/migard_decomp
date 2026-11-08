package com.miui.powerkeeper.utils;

import android.os.HwBinder;
import android.os.HwParcel;
import android.os.IBinder;
import android.os.IHwBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes.dex */
public class TouchFeatureUtil {
    private static final String AIDL_NAME_V1 = "vendor.xiaomi.hw.touchfeature.ITouchFeature";
    private static final int AIDL_V1_GETMODEWHITELIST = 11;
    private static final int AIDL_V1_SET_MODE_VALUE = 9;
    private static final String DEFAULT = "default";
    private static final int GET_MODE_WHITELIST = 10;
    private static volatile TouchFeatureUtil INSTANCE = null;
    private static final String SERVICE_AIDL_NAME_V1 = "vendor.xiaomi.hw.touchfeature.ITouchFeature/default";
    private static final String SERVICE_NAME_V1 = "vendor.xiaomi.hardware.touchfeature@1.0::ITouchFeature";
    private static final String SERVICE_NAME_V2 = "vendor.xiaomi.hw.touchfeature@1.0::ITouchFeature";
    public static final int SERVICE_VERSION_CODE_NONE = 0;
    public static final int SERVICE_VERSION_CODE_V1 = 1;
    public static final int SERVICE_VERSION_CODE_V2 = 2;
    public static final int SERVICE_VERSION_CODE_V3 = 3;
    private static final int SET_MODE_VALUE = 1;
    private static final String TAG = "TouchFeatureUtil";
    private int mServiceVersion;
    private int mTouchFeatureProperties = SystemProperties.getInt("ro.vendor.touchfeature.type", 0);

    private TouchFeatureUtil() {
        this.mServiceVersion = 0;
        this.mServiceVersion = getSupportTouchFeatureVersion();
    }

    public static TouchFeatureUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (TouchFeatureUtil.class) {
                try {
                    if (INSTANCE == null) {
                        INSTANCE = new TouchFeatureUtil();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return INSTANCE;
    }

    public String getModeWhitelist(int i2, int[] iArr) {
        int supportTouchFeatureVersion = getSupportTouchFeatureVersion();
        if (supportTouchFeatureVersion == 2) {
            HwParcel hwParcel = new HwParcel();
            try {
                try {
                    IHwBinder service = HwBinder.getService(SERVICE_NAME_V2, "default");
                    if (service != null) {
                        HwParcel hwParcel2 = new HwParcel();
                        hwParcel2.writeInterfaceToken(SERVICE_NAME_V2);
                        hwParcel2.writeInt32(i2);
                        hwParcel2.writeInt32Vector((ArrayList) Arrays.stream(iArr).boxed().collect(Collectors.toCollection(new Supplier() { // from class: com.miui.powerkeeper.utils.d
                            @Override // java.util.function.Supplier
                            public final Object get() {
                                return new ArrayList();
                            }
                        })));
                        service.transact(10, hwParcel2, hwParcel, 0);
                        hwParcel.verifySuccess();
                        hwParcel2.releaseTemporaryStorage();
                        String string = hwParcel.readString();
                        hwParcel.release();
                        return string;
                    }
                } catch (Throwable th) {
                    hwParcel.release();
                    throw th;
                }
            } catch (RemoteException e2) {
                Slog.e(TAG, "transact failed. " + e2);
            } catch (NoSuchElementException e3) {
                Slog.e(TAG, e3.toString());
            }
            hwParcel.release();
            Slog.e(TAG, "getModeWhitelist failed.");
            return "NULL";
        }
        if (supportTouchFeatureVersion != 3) {
            return "NULL";
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                IBinder service2 = ServiceManager.getService(SERVICE_AIDL_NAME_V1);
                if (service2 == null) {
                    Slog.e(TAG, "[JAVA] can't get vendor.xiaomi.hw.touchfeature.ITouchFeature/default service!");
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                    return "NULL";
                }
                parcelObtain.writeInterfaceToken(AIDL_NAME_V1);
                parcelObtain.writeInt(i2);
                parcelObtain.writeIntArray(iArr);
                service2.transact(11, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                String string2 = parcelObtain2.readString();
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return string2;
            } catch (RemoteException e4) {
                Slog.e(TAG, "[JAVA] transact failed. " + e4);
                parcelObtain.recycle();
                parcelObtain2.recycle();
                Slog.e(TAG, "[JAVA] getModeWhitelist failed.");
                return "NULL";
            }
        } catch (Throwable th2) {
            parcelObtain.recycle();
            parcelObtain2.recycle();
            throw th2;
        }
    }

    public int getSupportTouchFeatureVersion() {
        if (this.mServiceVersion != 0) {
            Slog.i(TAG, "current device and process support version:" + this.mServiceVersion);
            return this.mServiceVersion;
        }
        if (ServiceManager.getService(SERVICE_AIDL_NAME_V1) != null) {
            Slog.i(TAG, "current device and process support AIDL v1 service");
            return 3;
        }
        try {
            if (HwBinder.getService(SERVICE_NAME_V2, "default") != null) {
                Slog.i(TAG, "current device and process support v2 service");
                return 2;
            }
        } catch (RemoteException e2) {
            e2.printStackTrace();
        } catch (NoSuchElementException e3) {
            Slog.e(TAG, e3.toString());
        }
        try {
            if (HwBinder.getService(SERVICE_NAME_V1, "default") != null) {
                Slog.i(TAG, "current device and process support v1 service");
                return 1;
            }
        } catch (RemoteException e4) {
            e4.printStackTrace();
        } catch (NoSuchElementException e5) {
            Slog.e(TAG, e5.toString());
        }
        Slog.e(TAG, "current device and process not support, v1/v2 HIDL service and v1 AIDL service not found");
        return 0;
    }

    public boolean setTouchMode(int i2, int i3) {
        HwParcel hwParcel = new HwParcel();
        try {
            try {
                IHwBinder service = HwBinder.getService(SERVICE_NAME_V1, "default");
                if (service != null) {
                    HwParcel hwParcel2 = new HwParcel();
                    hwParcel2.writeInterfaceToken(SERVICE_NAME_V1);
                    hwParcel2.writeInt32(i2);
                    hwParcel2.writeInt32(i3);
                    service.transact(1, hwParcel2, hwParcel, 0);
                    hwParcel.verifySuccess();
                    hwParcel2.releaseTemporaryStorage();
                    int int32 = hwParcel.readInt32();
                    if (int32 == 0) {
                        hwParcel.release();
                        return true;
                    }
                    Slog.e(TAG, "setTouchMode failed. ret = " + int32);
                    hwParcel.release();
                    return false;
                }
            } catch (RemoteException e2) {
                Slog.e(TAG, "transact failed. " + e2);
            } catch (NoSuchElementException e3) {
                Slog.e(TAG, e3.toString());
            }
            hwParcel.release();
            Slog.e(TAG, "setTouchMode failed.");
            return false;
        } catch (Throwable th) {
            hwParcel.release();
            throw th;
        }
    }

    public boolean setTouchMode(int i2, int i3, int i4) {
        int supportTouchFeatureVersion = getSupportTouchFeatureVersion();
        if (supportTouchFeatureVersion == 1) {
            return setTouchMode(i3, i4);
        }
        if (supportTouchFeatureVersion == 2) {
            HwParcel hwParcel = new HwParcel();
            try {
                try {
                    IHwBinder service = HwBinder.getService(SERVICE_NAME_V2, "default");
                    if (service != null) {
                        HwParcel hwParcel2 = new HwParcel();
                        hwParcel2.writeInterfaceToken(SERVICE_NAME_V2);
                        hwParcel2.writeInt32(i2);
                        hwParcel2.writeInt32(i3);
                        hwParcel2.writeInt32(i4);
                        service.transact(1, hwParcel2, hwParcel, 0);
                        hwParcel.verifySuccess();
                        hwParcel2.releaseTemporaryStorage();
                        int int32 = hwParcel.readInt32();
                        if (int32 == 0) {
                            hwParcel.release();
                            return true;
                        }
                        Slog.e(TAG, "setTouchMode failed. ret = " + int32);
                        hwParcel.release();
                        return false;
                    }
                } catch (Throwable th) {
                    hwParcel.release();
                    throw th;
                }
            } catch (RemoteException e2) {
                Slog.e(TAG, "transact failed. " + e2);
            } catch (NoSuchElementException e3) {
                Slog.e(TAG, e3.toString());
            }
            hwParcel.release();
            Slog.e(TAG, "setTouchMode failed.");
            return false;
        }
        if (supportTouchFeatureVersion != 3) {
            return false;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                IBinder service2 = ServiceManager.getService(SERVICE_AIDL_NAME_V1);
                if (service2 == null) {
                    Slog.e(TAG, "[JAVA] can't get vendor.xiaomi.hw.touchfeature.ITouchFeature/default service!");
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                    return false;
                }
                parcelObtain.writeInterfaceToken(AIDL_NAME_V1);
                parcelObtain.writeInt(i2);
                parcelObtain.writeInt(i3);
                parcelObtain.writeInt(i4);
                service2.transact(9, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                int i5 = parcelObtain2.readInt();
                if (i5 != 0) {
                    Slog.e(TAG, "[JAVA] setTouchMode failed. ret = " + i5);
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                    return false;
                }
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return true;
            } catch (RemoteException e4) {
                Slog.e(TAG, "[JAVA] transact failed. " + e4);
                parcelObtain.recycle();
                parcelObtain2.recycle();
                Slog.e(TAG, "[JAVA] setTouchMode failed.");
                return false;
            }
        } catch (Throwable th2) {
            parcelObtain.recycle();
            parcelObtain2.recycle();
            throw th2;
        }
    }
}
