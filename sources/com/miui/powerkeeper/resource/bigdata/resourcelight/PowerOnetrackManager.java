package com.miui.powerkeeper.resource.bigdata.resourcelight;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import com.miui.powerkeeper.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PowerOnetrackManager {
    protected static final boolean DEBUG = Log.isLoggable("onetrack.data", 3);
    public static final String TAG = "PowerOnetrackManager";
    private static PowerOnetrackManager sInstance;
    private Context mContext;
    private IGreenifyService mGreenifyServiceManager;
    private List<String> mSupportDeviceList = new ArrayList(Arrays.asList("liuqin", "pipa", "yuechu", "yudi", "xun", "sheng", "ruyi", "muyu", "uke"));
    private boolean mIsSupport = false;
    private ServiceConnection GreenifyServiceConn = new ServiceConnection() { // from class: com.miui.powerkeeper.resource.bigdata.resourcelight.PowerOnetrackManager.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PowerOnetrackManager.this.mGreenifyServiceManager = IGreenifyService.Stub.asInterface(iBinder);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            PowerOnetrackManager.this.mGreenifyServiceManager = null;
        }
    };

    private PowerOnetrackManager(Context context) {
        this.mContext = context;
    }

    public static synchronized PowerOnetrackManager getInstance() {
        try {
            if (sInstance == null) {
                if (DEBUG) {
                    Log.d(TAG, Log.getStackTraceString(new Throwable()));
                }
                sInstance = new PowerOnetrackManager(Utils.APP_CONTEXT);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public IGreenifyService getGreenifyServiceManager() {
        boolean zContains = this.mSupportDeviceList.contains(Build.DEVICE);
        this.mIsSupport = zContains;
        if (zContains) {
            return this.mGreenifyServiceManager;
        }
        return null;
    }

    public void onCreate() {
        if (DEBUG) {
            Log.i(TAG, "onCreate");
        }
        boolean zContains = this.mSupportDeviceList.contains(Build.DEVICE);
        this.mIsSupport = zContains;
        if (zContains) {
            Intent intent = new Intent();
            intent.setClass(this.mContext, GreenifyService.class);
            this.mContext.bindService(intent, this.GreenifyServiceConn, 1);
        }
    }
}
