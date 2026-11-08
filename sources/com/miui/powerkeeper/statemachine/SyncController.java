package com.miui.powerkeeper.statemachine;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ServiceManager;
import android.util.Log;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.whetstone.IPowerKeeperPolicy;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class SyncController extends Handler {
    private static final String CLOUD_KEY_SYNC = "disable_sync";
    private static final String TAG = "PowerKeeper.Sync";
    private static SyncController sInstance;
    private final int MSG_UPDATE_CLOUD;
    private final int PARAM_TYPE_SYNC_RESTRICT;
    private final int PARAM_TYPE_SYNC_WHITELIST;
    private boolean changed;
    private Context mContext;

    private SyncController(Context context, Looper looper) {
        super(looper);
        this.MSG_UPDATE_CLOUD = 1;
        this.PARAM_TYPE_SYNC_WHITELIST = 1;
        this.PARAM_TYPE_SYNC_RESTRICT = 2;
        this.changed = false;
        Log.d(TAG, "sync controller created");
        this.mContext = context;
        String string = SimpleSettings.Misc.getString(context, CLOUD_KEY_SYNC, null);
        updateRestrictState(isCloudRestrict(string));
        setParam(1, getWhiteList(string));
        EventsAggregator.getInstance(context).registerPowerModeListener(this);
    }

    public static synchronized SyncController getInstance(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new SyncController(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private String getWhiteList(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new JSONObject(str).optString("whitelist", "");
            } catch (Exception unused) {
                PowerLog.d(TAG, "isCloudRestrict error");
            }
        }
        return "";
    }

    private boolean isCloudRestrict(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new JSONObject(str).optBoolean("fucSwitch", false);
            } catch (Exception unused) {
                PowerLog.d(TAG, "isCloudRestrict error");
            }
        }
        return false;
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        String string = SimpleSettings.Misc.getString(context, CLOUD_KEY_SYNC, "");
        String strOptString = jSONObject.optString(CLOUD_KEY_SYNC, "");
        if (string.equals(strOptString)) {
            return;
        }
        PowerLog.d(TAG, "onCloudFunctionUpdated newCloud=", strOptString);
        SimpleSettings.Misc.putString(context, CLOUD_KEY_SYNC, strOptString);
        SyncController syncController = sInstance;
        if (syncController != null) {
            boolean zIsCloudRestrict = syncController.isCloudRestrict(string);
            String whiteList = syncController.getWhiteList(string);
            boolean zIsCloudRestrict2 = syncController.isCloudRestrict(strOptString);
            String whiteList2 = syncController.getWhiteList(strOptString);
            Message messageObtainMessage = syncController.obtainMessage(1);
            if (zIsCloudRestrict2 == zIsCloudRestrict) {
                messageObtainMessage.arg1 = -1;
            } else {
                messageObtainMessage.arg1 = zIsCloudRestrict2 ? 1 : 0;
            }
            if (whiteList2.equals(whiteList)) {
                whiteList2 = null;
            }
            messageObtainMessage.obj = whiteList2;
            syncController.sendMessage(messageObtainMessage);
        }
    }

    private void setParam(int i2, String str) {
        try {
            Method declaredMethod = IPowerKeeperPolicy.class.getDeclaredMethod("setParam", Integer.TYPE, String.class, Bundle.class);
            if (declaredMethod == null) {
                PowerLog.d(TAG, "setParam return for no method");
                return;
            }
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            if (iWhetstoneActivityManagerAsInterface == null) {
                PowerLog.d(TAG, "setParam return for mWSProxy");
                return;
            }
            IPowerKeeperPolicy powerKeeperPolicy = iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy();
            if (powerKeeperPolicy == null) {
                PowerLog.d(TAG, "setParam return for null instance");
                return;
            }
            declaredMethod.invoke(powerKeeperPolicy, Integer.valueOf(i2), str, null);
            PowerLog.recordKey("setParam type=" + i2 + " value=" + str);
        } catch (Exception e2) {
            Log.d(TAG, "setParam excecption", e2);
        }
    }

    private void updateRestrictState(boolean z2) {
        int powerMode;
        boolean z3 = z2 && ((powerMode = EventsAggregator.getInstance(this.mContext).getPowerMode()) == 3 || powerMode == 4 || powerMode == 5);
        PowerLog.d(TAG, "updateConfigIfNeeded isNewRestrict=", Boolean.valueOf(z3), " isCloudRestrict=", Boolean.valueOf(z2));
        setParam(2, z3 ? "true" : "false");
    }

    public void destroy() {
        EventsAggregator.getInstance(this.mContext).unregisterPowerModeListener(this);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("SyncControllerDump");
        printWriter.println("config=" + SimpleSettings.Misc.getString(this.mContext, CLOUD_KEY_SYNC, ""));
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -22) {
            updateRestrictState(isCloudRestrict(SimpleSettings.Misc.getString(this.mContext, CLOUD_KEY_SYNC, null)));
            return;
        }
        if (i2 != 1) {
            return;
        }
        int i3 = message.arg1;
        if (i3 == 1) {
            updateRestrictState(true);
        } else if (i3 == 0) {
            updateRestrictState(false);
        }
        Object obj = message.obj;
        if (obj != null) {
            setParam(1, (String) obj);
        }
    }

    public void onScreenOffTimeout() {
        if (SimpleSettings.Misc.getBoolean(this.mContext, "screen_off_disable_sync", false) && ContentResolver.getMasterSyncAutomatically()) {
            ContentResolver.setMasterSyncAutomatically(false);
            this.changed = true;
            Log.d(TAG, "set sync to false");
        }
    }

    public void onScreenOn() {
        if (this.changed) {
            Log.d(TAG, "set sync back to true");
            ContentResolver.setMasterSyncAutomatically(true);
            this.changed = false;
        }
    }
}
