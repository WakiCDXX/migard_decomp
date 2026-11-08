package com.miui.powerkeeper.controller;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.whetstone.BroadcastPolicy;
import com.miui.whetstone.server.IWhetstoneActivityManager;

/* JADX INFO: loaded from: classes.dex */
public class BroadcastController extends Controller {
    private static final String TAG = "BroadcastController";
    private IWhetstoneActivityManager mWSProxy;

    public BroadcastController(Context context, PowerKeeperManager powerKeeperManager) {
        super(context, powerKeeperManager, TAG);
        this.mWSProxy = null;
        checkWhetstoneAMProxy(true);
    }

    public boolean checkWhetstoneAMProxy(boolean z2) {
        if (this.mWSProxy != null && !z2) {
            return true;
        }
        IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        this.mWSProxy = iWhetstoneActivityManagerAsInterface;
        return iWhetstoneActivityManagerAsInterface != null;
    }

    public void setBroadcastControlStat(int i2, boolean z2) {
        if (checkWhetstoneAMProxy(false)) {
            try {
                this.mWSProxy.getPowerKeeperPolicy().setAppBroadcastControlStat(i2, z2);
            } catch (RemoteException e2) {
                Slog.e(TAG, "setBroadcastControlStat", e2);
            }
        }
    }

    public void setBroadcastRestrict(int i2, String[] strArr) {
        BroadcastPolicy[] broadcastPolicyArr = new BroadcastPolicy[1];
        if (strArr == null) {
            broadcastPolicyArr[0] = new BroadcastPolicy(i2, new String[]{"a.jack.bc.1"});
        } else {
            broadcastPolicyArr[0] = new BroadcastPolicy(i2, strArr);
        }
        if (checkWhetstoneAMProxy(false)) {
            try {
                this.mWSProxy.getPowerKeeperPolicy().setBroadcastPolicy(broadcastPolicyArr, false);
            } catch (RemoteException e2) {
                Slog.e(TAG, "getBroadcastControlStat", e2);
            }
        }
    }
}
