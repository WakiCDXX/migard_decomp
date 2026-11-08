package android.os;

import android.util.Log;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class MiuiServiceManager {
    private static final String DEPRECATED_MSG = "MiuiServiceManager has been deprecated";
    private static final String TAG = "MiuiServiceManager";
    private static HashMap<String, IBinder> sCache = new HashMap<>();
    private static IMiuiServiceManager sMiuiServiceManager;

    @Deprecated
    public static void addService(String str, IBinder iBinder) {
        try {
            getIMiuiServiceManager().addService(str, iBinder);
        } catch (RemoteException e2) {
            Log.e(TAG, "error in miui addService", e2);
        } catch (NullPointerException e3) {
            Log.w(TAG, DEPRECATED_MSG, e3);
        }
    }

    private static IMiuiServiceManager getIMiuiServiceManager() {
        IMiuiServiceManager iMiuiServiceManager = sMiuiServiceManager;
        if (iMiuiServiceManager != null) {
            return iMiuiServiceManager;
        }
        IMiuiServiceManager iMiuiServiceManagerAsInterface = IMiuiServiceManager.Stub.asInterface(ServiceManager.getService(MiuiServiceManagerInternal.SERVICE_NAME));
        sMiuiServiceManager = iMiuiServiceManagerAsInterface;
        return iMiuiServiceManagerAsInterface;
    }

    @Deprecated
    public static IBinder getService(String str) {
        try {
            IBinder iBinder = sCache.get(str);
            return iBinder != null ? iBinder : getIMiuiServiceManager().getService(str);
        } catch (RemoteException e2) {
            Log.e(TAG, "error in miui getService", e2);
            return null;
        } catch (NullPointerException e3) {
            Log.w(TAG, DEPRECATED_MSG, e3);
            return null;
        }
    }

    @Deprecated
    public static String[] listServices() {
        try {
            return getIMiuiServiceManager().listServices();
        } catch (RemoteException e2) {
            Log.e(TAG, "error in miui listServices", e2);
            return new String[0];
        } catch (NullPointerException e3) {
            Log.w(TAG, DEPRECATED_MSG, e3);
            return new String[0];
        }
    }
}
