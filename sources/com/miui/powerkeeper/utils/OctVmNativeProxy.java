package com.miui.powerkeeper.utils;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class OctVmNativeProxy {
    private static final int APPLICATION_FOCUSED = 5;
    private static final int APPLICATION_INACTIVE = 6;
    private static final int APPLICATION_STARTED_BG = 7;
    private static final int GET_PID_BY_NAME = 9;
    private static final int MAX_RETRY = 5;
    private static final int NET_DNS_DISABLE = 2;
    private static final int NET_DNS_ENABLE = 1;
    private static final int NET_DNS_RELOAD = 3;
    private static final int SET_MEMORY_MODE = 4;
    private static final int SUDEBUG_CAMERA_COMMAND_EXECUTE = 11;
    private static final int SUDEBUG_COMMAND_EXECUTE = 8;
    private static final String TAG = "OctVmNativeProxy";
    private static Method sCheckService = null;
    private static IBinder sNativeService = null;
    private static final String sNativeServiceName = "miui.whetstone.mcd";
    private static Class<?> sServiceManager;

    private static boolean checkNativeService() {
        Class<?> cls;
        try {
            if (sServiceManager == null) {
                sServiceManager = Class.forName("android.os.ServiceManager");
            }
            if (sCheckService == null && (cls = sServiceManager) != null) {
                sCheckService = cls.getDeclaredMethod("checkService", String.class);
            }
            Method method = sCheckService;
            if (method != null) {
                sNativeService = (IBinder) method.invoke(null, sNativeServiceName);
            }
        } catch (ClassNotFoundException e2) {
            Log.e(TAG, "checkNativeService ClassNotFoundException:" + e2);
        } catch (IllegalAccessException e3) {
            Log.e(TAG, "checkNativeService IllegalAccessException:" + e3);
        } catch (NoSuchMethodException e4) {
            Log.e(TAG, "checkNativeService NoSuchMethodException:" + e4);
        } catch (InvocationTargetException e5) {
            Log.e(TAG, "checkNativeService InvocationTargetException:" + e5);
        }
        return sNativeService != null;
    }

    public static Bundle get_pid_by_name(String[] strArr) {
        Bundle bundle = new Bundle();
        if (strArr != null && strArr.length != 0 && checkNativeService()) {
            try {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                parcelObtain.writeInterfaceToken(sNativeServiceName);
                parcelObtain.writeInt(strArr.length);
                for (String str : strArr) {
                    parcelObtain.writeString(str);
                }
                sNativeService.transact(9, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                int i2 = parcelObtain2.readInt();
                for (int i3 = 0; i3 < i2; i3++) {
                    bundle.putString(parcelObtain2.readString(), parcelObtain2.readString());
                }
                parcelObtain.recycle();
                parcelObtain2.recycle();
            } catch (Exception e2) {
                Log.e(TAG, "get_pid_by_name RemoteException:" + e2);
            }
        }
        return bundle;
    }

    public static int sudebug_camera_command_execute(String[] strArr) {
        if (!checkNativeService()) {
            return -1;
        }
        try {
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            parcelObtain.writeInterfaceToken(sNativeServiceName);
            parcelObtain.writeInt(strArr.length);
            for (String str : strArr) {
                parcelObtain.writeString(str);
            }
            sNativeService.transact(11, parcelObtain, parcelObtain2, 0);
            parcelObtain2.readException();
            int i2 = parcelObtain2.readInt();
            parcelObtain.recycle();
            parcelObtain2.recycle();
            return i2;
        } catch (RemoteException e2) {
            Log.e(TAG, "setKLODumpFiles RemoteException:" + e2);
            return -1;
        }
    }

    public static int sudebug_command_execute(String[] strArr) {
        if (!checkNativeService()) {
            return -1;
        }
        try {
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            parcelObtain.writeInterfaceToken(sNativeServiceName);
            parcelObtain.writeInt(strArr.length);
            for (String str : strArr) {
                parcelObtain.writeString(str);
            }
            sNativeService.transact(8, parcelObtain, parcelObtain2, 0);
            parcelObtain2.readException();
            int i2 = parcelObtain2.readInt();
            parcelObtain.recycle();
            parcelObtain2.recycle();
            return i2;
        } catch (RemoteException e2) {
            Log.e(TAG, "setKLODumpFiles RemoteException:" + e2);
            return -1;
        }
    }
}
