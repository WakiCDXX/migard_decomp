package com.miui.powerkeeper.provider;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.perfengine.PerfEngineManager;
import com.miui.powerkeeper.perfengine.g;
import com.miui.powerkeeper.statemachine.DisplayFrameSetting;
import com.miui.powerkeeper.utils.IrisServiceUtils;
import com.miui.powerkeeper.utils.PowerLog;
import e.e;
import java.util.HashSet;
import miui.util.FeatureParser;

/* JADX INFO: loaded from: classes.dex */
public class HomeGestureStatusProvider {
    private static final String KEY_STATUS = "status";
    public static final String METHOD_NOTIFY_POWERKEEPER_GESTURE = "notifyPowerKeeperGesture";
    public static final String VALUE_GESTURE_END = "gesture_end";
    public static final String VALUE_GESTURE_START = "gesture_start";
    public static final String VALUE_TO_APP = "to_app";
    public static final String VALUE_TO_HOME = "to_home";
    public static final String VALUE_TO_ONE_HAND = "to_one_hand";
    public static final String VALUE_TO_RECENT = "to_recent";
    private static HashSet<String> sMethods;
    private Context mContext;
    private boolean mIsMTKDevice;
    private int mCheckCount = 5;
    private boolean mIsDeviceSupportGestureBoost = false;
    private int mGestureStartPerflockID = -1;

    static {
        HashSet<String> hashSet = new HashSet<>();
        sMethods = hashSet;
        hashSet.add(METHOD_NOTIFY_POWERKEEPER_GESTURE);
    }

    HomeGestureStatusProvider(Context context) {
        this.mIsMTKDevice = false;
        this.mContext = context;
        this.mIsMTKDevice = FeatureParser.getBoolean("is_mediatek", false);
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Bundle bundle2 = new Bundle();
        if (METHOD_NOTIFY_POWERKEEPER_GESTURE.equals(str)) {
            String string = bundle.getString("status");
            int i2 = this.mCheckCount;
            if (i2 > 0) {
                this.mCheckCount = i2 - 1;
                if (PowerKeeperManager.getInstance() != null) {
                    this.mIsDeviceSupportGestureBoost = PowerKeeperManager.getInstance().getIsGestureBoost();
                }
            }
            if (PowerLog.V) {
                PowerLog.v("HomeGestureStatusProvider", "call status:" + string + ", isMTK:" + this.mIsMTKDevice + ", isGesBoost:" + this.mIsDeviceSupportGestureBoost);
            }
            if (VALUE_GESTURE_START.equals(string)) {
                if (this.mIsDeviceSupportGestureBoost && !this.mIsMTKDevice && e.o(4) && PerfEngineManager.d() != null) {
                    String strC = PerfEngineManager.d().c();
                    if (!TextUtils.isEmpty(strC) && !strC.equals("com.miui.home")) {
                        this.mGestureStartPerflockID = g.c().b(600);
                    } else if (PowerLog.V) {
                        PowerLog.v("HomeGestureStatusProvider", "No boost, FG is home");
                    }
                }
                DisplayFrameSetting.notifyGesture(VALUE_GESTURE_START);
            } else if (VALUE_TO_APP.equals(string)) {
                if (this.mIsDeviceSupportGestureBoost && !this.mIsMTKDevice) {
                    g.c().a(true, 400);
                    if (this.mGestureStartPerflockID > 0) {
                        g.c().h(this.mGestureStartPerflockID);
                        this.mGestureStartPerflockID = -1;
                    }
                }
                DisplayFrameSetting.notifyGesture(VALUE_TO_APP);
            } else if (VALUE_TO_HOME.equals(string)) {
                if (this.mIsDeviceSupportGestureBoost && !this.mIsMTKDevice) {
                    g.c().a(true, 400);
                    if (this.mGestureStartPerflockID > 0) {
                        g.c().h(this.mGestureStartPerflockID);
                        this.mGestureStartPerflockID = -1;
                    }
                }
            } else if (VALUE_TO_RECENT.equals(string)) {
                if (this.mIsDeviceSupportGestureBoost && !this.mIsMTKDevice) {
                    g.c().a(true, 400);
                    if (this.mGestureStartPerflockID > 0) {
                        g.c().h(this.mGestureStartPerflockID);
                        this.mGestureStartPerflockID = -1;
                    }
                }
            } else if (VALUE_TO_ONE_HAND.equals(string)) {
                DisplayFrameSetting.notifyGesture(VALUE_TO_ONE_HAND);
            } else if (VALUE_GESTURE_END.equals(string)) {
                DisplayFrameSetting.notifyGesture(VALUE_GESTURE_END);
            }
            if (IrisServiceUtils.isDualDPUSupportedNew()) {
                IrisServiceUtils.getInstance(this.mContext).notifyUIServiceGestureChange(string);
            }
        }
        return bundle2;
    }

    public boolean checkMethod(String str) {
        return sMethods.contains(str);
    }
}
