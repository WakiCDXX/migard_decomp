package com.miui.powerkeeper.millet;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.miui.powerkeeper.controller.FrozenAppController;

/* JADX INFO: loaded from: classes.dex */
public class PowerMilletService extends IPowerMillet.Stub {
    public static final String SERVICE_NAME = "miui.powerkeeper.PowerMillet";
    private static final String TAG = "PowerMilletService";
    private Context mContext;
    private FrozenAppController mFac;

    public PowerMilletService(Context context, FrozenAppController frozenAppController) {
        this.mFac = null;
        Log.d(TAG, "init!!");
        this.mContext = context;
        this.mFac = frozenAppController;
    }

    @Override // com.miui.powerkeeper.millet.IPowerMillet
    public void notifyBluetoothEvent(int i2, Bundle bundle) {
        Log.d(TAG, "notifyBluetoothEvent mFac=" + this.mFac + " uid:" + Binder.getCallingUid());
        if (this.mFac == null || Binder.getCallingUid() != 1002) {
            return;
        }
        Log.d(TAG, "notifyBluetoothEvent event=" + i2);
        Message messageObtain = Message.obtain();
        messageObtain.what = 23;
        bundle.putString("profile", "y");
        messageObtain.setData(bundle);
        if (this.mFac.getMilletHandler() != null) {
            this.mFac.getMilletHandler().sendMessage(messageObtain);
        }
    }

    @Override // com.miui.powerkeeper.millet.IPowerMillet
    public void showState() {
        Log.d(TAG, "showState");
    }
}
