package com.miui.powerkeeper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/* JADX INFO: loaded from: classes.dex */
public class WakelockInterfaceManager {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Context f321a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private IWakelockManager f322b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private ServiceConnection f323c = new a();

    class a implements ServiceConnection {
        a() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WakelockInterfaceManager.this.f322b = IWakelockManager.Stub.asInterface(iBinder);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            WakelockInterfaceManager.this.f322b = null;
        }
    }

    public WakelockInterfaceManager(Context context) {
        this.f321a = context;
        Intent intent = new Intent();
        intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.WakelockManagerService");
        this.f321a.bindService(intent, this.f323c, 1);
    }

    public IWakelockManager b() {
        return this.f322b;
    }
}
