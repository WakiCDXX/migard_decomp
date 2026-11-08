package com.miui.powerkeeper.utils;

import android.content.Context;
import android.os.ServiceManager;
import android.util.Log;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.whetstone.IPowerKeeperPolicy;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import miui.util.FeatureParser;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DimController {
    public static final String CLOUD_DIM_CONFIG = "dim_config";
    private static boolean SUPPPORT_CLOUD_DIM = FeatureParser.getBoolean("support_cloud_dim", false);
    private static final String TAG = "PowerKeeper.Dim";
    private static String mDimConfig = "";

    public static void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("DimControllerDump");
        printWriter.println("config=" + mDimConfig);
    }

    public static boolean isSupportDevice() {
        return SUPPPORT_CLOUD_DIM;
    }

    public static void notifyDimConfigChanged(String str) {
        if (!isSupportDevice() || str.isEmpty()) {
            return;
        }
        try {
            Method declaredMethod = IPowerKeeperPolicy.class.getDeclaredMethod("notifyCloudDimChanged", String.class);
            if (declaredMethod == null) {
                PowerLog.d(TAG, "notifyCloudDimChanged return for no method");
                return;
            }
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            if (iWhetstoneActivityManagerAsInterface == null) {
                PowerLog.d(TAG, "notifyCloudDimChanged return for mWSProxy");
                return;
            }
            IPowerKeeperPolicy powerKeeperPolicy = iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy();
            if (powerKeeperPolicy == null) {
                PowerLog.d(TAG, "notifyCloudDimChanged return for null instance");
            } else {
                declaredMethod.invoke(powerKeeperPolicy, str);
            }
        } catch (Exception e2) {
            Log.d(TAG, "notifyCloudDimChanged excecption", e2);
        }
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        String string = SimpleSettings.Misc.getString(context, CLOUD_DIM_CONFIG, "");
        String strOptString = jSONObject.optString(CLOUD_DIM_CONFIG, "");
        if (string.equals(strOptString)) {
            return;
        }
        PowerLog.d(TAG, "onCloudFunctionUpdated newCloud=", strOptString);
        SimpleSettings.Misc.putString(context, CLOUD_DIM_CONFIG, strOptString);
        mDimConfig = strOptString;
        notifyDimConfigChanged(strOptString);
    }
}
