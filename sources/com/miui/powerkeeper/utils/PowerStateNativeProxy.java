package com.miui.powerkeeper.utils;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import com.miui.powerkeeper.statemachine.PowerStateMachine;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class PowerStateNativeProxy {
    private static final int MAX_RETRY = 5;
    private static final int POWER_SAVE_FEATURE_ENABLE = 1;
    private static final int POWER_STATE_APP_FROZEN = 7;
    private static final int POWER_STATE_APP_THAWED = 8;
    private static final int POWER_STATE_GET_AVG_CURRENT = 5;
    private static final int POWER_STATE_GET_TIME_IN_STATE = 6;
    private static final int POWER_STATE_MODE_START = 2;
    private static final int POWER_STATE_TRIGGER_ACTION = 3;
    private static final int POWER_STATE_UPDATE_CONFIG = 4;
    private static final String TAG = "PowerStateNativeProxy";
    private static Method sCheckService = null;
    private static IBinder sNativeService = null;
    private static final String sNativeServiceName = "miui.whetstone.power";
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
                sNativeService = (IBinder) method.invoke(null, "miui.whetstone.power");
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

    public static boolean frozenApp(int i2, int[] iArr) {
        if (!checkNativeService()) {
            return false;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken("miui.whetstone.power");
                parcelObtain.writeInt(i2);
                parcelObtain.writeInt(iArr.length);
                for (int i3 : iArr) {
                    parcelObtain.writeInt(i3);
                }
                sNativeService.transact(7, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                boolean z2 = parcelObtain2.readInt() == 0;
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return z2;
            } catch (RemoteException e2) {
                Log.e(TAG, "application_focused RemoteException:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                    parcelObtain2.recycle();
                }
                return false;
            }
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public static int getAvgCurrent(int i2) {
        if (!checkNativeService()) {
            return -1;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken("miui.whetstone.power");
                parcelObtain.writeInt(i2);
                sNativeService.transact(5, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                int i3 = parcelObtain2.readInt();
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return i3;
            } catch (RemoteException e2) {
                Log.e(TAG, "power_state_get_avg_current RemoteException:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                    parcelObtain2.recycle();
                }
                return -1;
            }
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public static int getTimeInState(int i2) {
        if (!checkNativeService()) {
            return -1;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken("miui.whetstone.power");
                parcelObtain.writeInt(i2);
                sNativeService.transact(6, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                int i3 = parcelObtain2.readInt();
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return i3;
            } catch (RemoteException e2) {
                Log.e(TAG, "power_state_time_in_state RemoteException:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                    parcelObtain2.recycle();
                }
                return -1;
            }
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public static boolean modeStart(String str, int i2) {
        if (!checkNativeService()) {
            return false;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken("miui.whetstone.power");
                parcelObtain.writeString(str);
                parcelObtain.writeInt(i2);
                sNativeService.transact(2, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                boolean z2 = parcelObtain2.readInt() == 0;
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return z2;
            } catch (RemoteException e2) {
                Log.e(TAG, "power_state_mode_start RemoteException:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                    parcelObtain2.recycle();
                }
                return false;
            }
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public static boolean nativeFeatureEnable(int i2) {
        if (!checkNativeService()) {
            return false;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken("miui.whetstone.power");
                parcelObtain.writeInt(i2);
                sNativeService.transact(1, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                boolean z2 = parcelObtain2.readInt() == 0;
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return z2;
            } catch (RemoteException e2) {
                Log.e(TAG, "power_save_feature_enable RemoteException:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                    parcelObtain2.recycle();
                }
                return false;
            }
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public static boolean thawedApp(int i2, int[] iArr) {
        if (!checkNativeService()) {
            return false;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken("miui.whetstone.power");
                parcelObtain.writeInt(i2);
                parcelObtain.writeInt(iArr.length);
                for (int i3 : iArr) {
                    parcelObtain.writeInt(i3);
                }
                sNativeService.transact(8, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                boolean z2 = parcelObtain2.readInt() == 0;
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return z2;
            } catch (RemoteException e2) {
                Log.e(TAG, "setKLODumpFiles RemoteException:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                    parcelObtain2.recycle();
                }
                return false;
            }
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public static boolean triggerEvent(String str, int i2) {
        if (!checkNativeService()) {
            return false;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken("miui.whetstone.power");
                parcelObtain.writeString(str);
                parcelObtain.writeInt(i2);
                sNativeService.transact(3, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                boolean z2 = parcelObtain2.readInt() == 0;
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return z2;
            } catch (RemoteException e2) {
                Log.e(TAG, "power_state_trigger_action RemoteException:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                    parcelObtain2.recycle();
                }
                return false;
            }
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public static boolean updatePowerStateConfig(int i2, PowerStateMachine.PowerState powerState) {
        if (!checkNativeService()) {
            return false;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken("miui.whetstone.power");
                parcelObtain.writeInt(i2);
                powerState.toMcdParcel(parcelObtain);
                sNativeService.transact(4, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                boolean z2 = parcelObtain2.readInt() == 0;
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return z2;
            } catch (RemoteException e2) {
                Log.e(TAG, "power_state_update_config RemoteException:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                    parcelObtain2.recycle();
                }
                return false;
            }
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }
}
