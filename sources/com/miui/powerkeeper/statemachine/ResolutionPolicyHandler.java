package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.os.Bundle;
import android.os.ServiceManager;
import android.util.Log;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.whetstone.IPowerKeeperPolicy;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.lang.reflect.Method;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ResolutionPolicyHandler {
    private static final String CLOUD_RESOLUTION_POLICY = "resolution_policy";

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        String strOptString = jSONObject.optString(CLOUD_RESOLUTION_POLICY, "");
        if (strOptString.equals(SimpleSettings.Misc.getString(context, CLOUD_RESOLUTION_POLICY, ""))) {
            return;
        }
        SimpleSettings.Misc.putString(context, CLOUD_RESOLUTION_POLICY, strOptString);
        updateResolutionIfNeeded(strOptString, context);
    }

    public static void updateResolutionIfNeeded(String str, Context context) {
        if (str == null) {
            str = SimpleSettings.Misc.getString(context, CLOUD_RESOLUTION_POLICY, "");
        }
        try {
            Method declaredMethod = IPowerKeeperPolicy.class.getDeclaredMethod("setParam", Integer.TYPE, String.class, Bundle.class);
            if (declaredMethod == null) {
                Log.d("ResolutionPolicyHandler", "updateResolutionIfNeeded return for setParam");
                return;
            }
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            if (iWhetstoneActivityManagerAsInterface == null) {
                Log.d("ResolutionPolicyHandler", "updateResolutionIfNeeded return for mWSProxy");
                return;
            }
            IPowerKeeperPolicy powerKeeperPolicy = iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy();
            if (powerKeeperPolicy == null) {
                Log.d("ResolutionPolicyHandler", "updateResolutionIfNeeded return for null instance");
                return;
            }
            Log.d("ResolutionPolicyHandler", "updateResolutionIfNeeded call setParam config=" + str);
            declaredMethod.invoke(powerKeeperPolicy, 0, str, null);
        } catch (Exception e2) {
            Log.d("ResolutionPolicyHandler", "updateResolutionIfNeeded excecption", e2);
        }
    }
}
