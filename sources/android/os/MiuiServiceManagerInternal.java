package android.os;

import android.util.Log;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class MiuiServiceManagerInternal extends IMiuiServiceManager.Stub {
    public static final String SERVICE_NAME = "miui.os.servicemanager";
    private static final String TAG = "MiuiServiceManagerInternal";
    private HashMap<String, IBinder> map = new HashMap<>();

    @Override // android.os.IMiuiServiceManager
    public void addService(String str, IBinder iBinder) {
        if (str != null && iBinder != null) {
            this.map.put(str, iBinder);
            return;
        }
        Log.e(TAG, "add service parameter error, service name: " + str);
    }

    @Override // android.os.IMiuiServiceManager
    public IBinder getService(String str) {
        if (str != null) {
            return this.map.get(str);
        }
        Log.e(TAG, "get service name is null");
        return null;
    }

    @Override // android.os.IMiuiServiceManager
    public String[] listServices() {
        return (String[]) this.map.keySet().toArray();
    }
}
