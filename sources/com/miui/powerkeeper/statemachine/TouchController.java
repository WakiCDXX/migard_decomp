package com.miui.powerkeeper.statemachine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.TouchFeatureUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import miui.process.ForegroundInfo;
import miui.util.ITouchFeature;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class TouchController extends Handler {
    private static final String CLOUD_DYNAMIC_RATE_PKG = "touchControllerApps";
    private static final String CONFIG_VERSION = "version";
    private static final String DYNAMIC_RATE = "DynamicRate";
    private static final int EVENT_ID_INIT = 0;
    private static final int EVENT_ID_TP_ACTIVE_TIME_UPDATE = 1;
    private static final String FUN_SWITCH = "DEFAULT";
    private static final int MSG_CHEKC_FG_CONDITION = 3;
    private static final int MSG_CLOUD_TOUCH_PKGS_UPDATE = 2;
    private static final String PKG_WHITELIST = "APP";
    private static final String PRODUCT = "product";
    public static final String TAG = "TouchController";
    private static final int TOUCH_ID = 0;
    private static final int TOUCH_MODE_APPS = 1085;
    private static final int TOUCH_MODE_TP_ACTIVE = 13;
    private static ArraySet<String> mCloudTouchCtrlPkgs = new ArraySet<>(10);
    private static boolean mIsSatelliteTelephonyEnabled = false;
    static ITouchFeature mTouchFeature = ITouchFeature.getInstance();
    static TouchFeatureUtil mTouchFeatureUtil = TouchFeatureUtil.getInstance();
    private static TouchController sInstance;
    private final boolean DBG_TOUCHMODE;
    private b.a mCloudObserver;
    private ContentObserver mContentObserver;
    private Context mContext;
    private ForegroundInfo mCurrentFgInfo;
    private String mDynamicRateDevice;
    private boolean mDynamicRateSwitch;
    private String mDynamicRateVersion;
    private boolean mIsTouchModeAppSwitch;
    private int mLastTPActiveTime;
    private LocalLog mLocalLog;
    private boolean mTPActiveSettingEnabled;
    private ArrayList<TPActiveTime> mTPActiveTimeList;
    private ArraySet<String> mTouchModeApps;

    public class TPActiveTime {
        int mActiveTime;
        int mGroupId;

        public TPActiveTime() {
        }
    }

    private TouchController(Context context, Looper looper) {
        super(looper);
        this.mLastTPActiveTime = -1;
        this.mTPActiveTimeList = new ArrayList<>();
        this.mTouchModeApps = new ArraySet<>(10);
        this.mDynamicRateSwitch = false;
        this.mDynamicRateVersion = "";
        this.mDynamicRateDevice = "";
        this.mCurrentFgInfo = null;
        this.mIsTouchModeAppSwitch = true;
        this.mLocalLog = new LocalLog(32);
        this.DBG_TOUCHMODE = Log.isLoggable("power.touchmode", 3);
        this.mTPActiveSettingEnabled = false;
        if (Constant.DEBUG) {
            Log.d(TAG, "TouchController created");
        }
        this.mContext = context;
        this.mCloudObserver = b.a.p();
        parseTouchModeAppFromTp();
        sendMessage(obtainMessage(2, null));
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.registerForForegroundInfo(this);
            this.mCurrentFgInfo = eventsAggregator.getForegroundInfo();
        }
        sendMessage(obtainMessage(3));
    }

    public static synchronized TouchController init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new TouchController(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v3, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v7 */
    /* JADX WARN: Type inference failed for: r1v9 */
    /* JADX WARN: Type inference failed for: r3v1, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r5v2, types: [com.miui.powerkeeper.utils.TouchFeatureUtil] */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private void notifyFGChange() {
        ArraySet<String> arraySet;
        ?? r1;
        ArraySet<String> arraySet2;
        boolean zContains;
        ArraySet<String> arraySet3;
        ForegroundInfo foregroundInfo = this.mCurrentFgInfo;
        if (foregroundInfo == null || foregroundInfo.mForegroundPackageName == null) {
            writeLocalLog("ignore switch touch mode for currentPkg is null");
            return;
        }
        if ((!this.mDynamicRateSwitch || (arraySet3 = this.mTouchModeApps) == null || arraySet3.size() <= 0) && ((arraySet = mCloudTouchCtrlPkgs) == null || arraySet.size() <= 0)) {
            return;
        }
        String str = this.mCurrentFgInfo.mForegroundPackageName;
        ArraySet<String> arraySet4 = this.mTouchModeApps;
        if (arraySet4 != null) {
            zContains = arraySet4.contains(str);
        } else {
            r1 = 0;
        }
        if (r1 == 0 && (arraySet2 = mCloudTouchCtrlPkgs) != null) {
            if (arraySet2.contains(str) && mIsSatelliteTelephonyEnabled) {
                r1 = zContains;
                r1 = 1;
            } else {
                r1 = zContains;
                r1 = zContains;
                r1 = 0;
            }
        }
        r1 = zContains;
        r1 = zContains;
        if (r1 != this.mIsTouchModeAppSwitch) {
            this.mIsTouchModeAppSwitch = r1;
            try {
                writeLocalLog("onForegroundChanged for touch mode value=" + r1 + "， pkg = " + str);
                mTouchFeatureUtil.setTouchMode(0, TOUCH_MODE_APPS, r1);
            } catch (Exception unused) {
                Log.e(TAG, "setTouchMode work abnormal");
            }
        }
    }

    public static void notifySatelliteTelephonyStatusChanged(boolean z2) {
        TouchController touchController = sInstance;
        if (touchController == null || mIsSatelliteTelephonyEnabled == z2) {
            return;
        }
        mIsSatelliteTelephonyEnabled = z2;
        if (touchController.hasMessages(3)) {
            touchController.removeMessages(3);
        }
        touchController.sendMessage(touchController.obtainMessage(3));
    }

    private void parseTouchModeAppFromDb(String str) {
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i2 = b.b.S;
            JSONObject jSONObjectL = aVar.l(i2);
            if (this.mCloudObserver.s(i2) && jSONObjectL != null) {
                str = jSONObjectL.optString(CLOUD_DYNAMIC_RATE_PKG, null);
            }
        }
        mCloudTouchCtrlPkgs.clear();
        if (str == null || str.isEmpty()) {
            return;
        }
        String[] strArrSplit = str.split(",");
        for (String str2 : strArrSplit) {
            mCloudTouchCtrlPkgs.add(str2);
        }
    }

    private void parseTouchModeAppFromTp() {
        String modeWhitelist;
        this.mTouchModeApps.clear();
        String strOptString = null;
        try {
            modeWhitelist = mTouchFeatureUtil.getModeWhitelist(0, new int[]{TOUCH_MODE_APPS});
        } catch (Exception unused) {
            Log.e(TAG, "getTouchModeWhiteList work abnormal");
            modeWhitelist = null;
        }
        if (modeWhitelist != null && !modeWhitelist.isEmpty()) {
            try {
                String strOptString2 = new JSONObject(modeWhitelist).optString(DYNAMIC_RATE, null);
                if (strOptString2 == null || strOptString2.isEmpty()) {
                    writeLocalLog("parseTouchModeAppFromTp ignore, config is null");
                    return;
                }
                JSONObject jSONObject = new JSONObject(strOptString2);
                String strOptString3 = jSONObject.optString(PRODUCT, "");
                this.mDynamicRateDevice = strOptString3;
                if (strOptString3 != null && !strOptString3.isEmpty() && Build.DEVICE.equals(this.mDynamicRateDevice)) {
                    boolean zOptBoolean = jSONObject.optBoolean(FUN_SWITCH, false);
                    this.mDynamicRateSwitch = zOptBoolean;
                    if (!zOptBoolean) {
                        if (Constant.DEBUG) {
                            Log.d(TAG, "parseTouchModeAppFromTp ignore, config switch: " + this.mDynamicRateSwitch);
                            return;
                        }
                        return;
                    }
                    this.mDynamicRateVersion = jSONObject.optString("version", "");
                    strOptString = jSONObject.optString(PKG_WHITELIST, null);
                    writeLocalLog("parseTouchModeAppFromTp, pkgs = " + strOptString + ", version = " + this.mDynamicRateVersion);
                    if (strOptString != null && !strOptString.isEmpty()) {
                        for (String str : strOptString.split(",")) {
                            this.mTouchModeApps.add(str);
                        }
                    }
                }
                if (Constant.DEBUG) {
                    Log.d(TAG, "parseTouchModeAppFromTp ignore, config device: " + this.mDynamicRateDevice);
                    return;
                }
                return;
            } catch (JSONException e2) {
                Log.e(TAG, "parseTouchModeAppFromTp e=" + e2);
            }
        }
        writeLocalLog("parseTouchModeAppFromTp mTouchModeApps = " + this.mTouchModeApps + " appJsonStr = " + modeWhitelist);
    }

    private void resetTPActiveTime() {
        int i2 = this.mLastTPActiveTime;
        if (i2 != -1) {
            mTouchFeature.setTouchMode(13, i2);
            this.mLastTPActiveTime = -1;
            if (Constant.DEBUG) {
                Log.d(TAG, "after reset currentTime=" + mTouchFeature.getTouchModeCurValue(13));
            }
        }
    }

    private void setTPActiveTime(TPActiveTime tPActiveTime) {
        if (this.mLastTPActiveTime == -1) {
            this.mLastTPActiveTime = mTouchFeature.getTouchModeDefValue(13);
        }
        int touchModeMaxValue = mTouchFeature.getTouchModeMaxValue(13);
        int touchModeMinValue = mTouchFeature.getTouchModeMinValue(13);
        int i2 = tPActiveTime.mActiveTime;
        if (i2 > touchModeMaxValue) {
            i2 = touchModeMaxValue;
        } else if (i2 < touchModeMinValue) {
            i2 = touchModeMinValue;
        }
        if (!mTouchFeature.setTouchMode(13, i2)) {
            this.mLastTPActiveTime = -1;
        }
        if (Constant.DEBUG) {
            Log.d(TAG, "defaultTime=" + this.mLastTPActiveTime + ", maxTime=" + touchModeMaxValue + ", minTime=" + touchModeMinValue + ", after set currentTime=" + mTouchFeature.getTouchModeCurValue(13));
        }
    }

    private void updateCloudData() {
        JSONArray jSONArray = null;
        String string = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_TP_ACTIVE_DEVICES, null);
        if (Constant.DEBUG) {
            Log.d(TAG, "updateCloudData device=" + string);
        }
        if (string == null || string.length() == 0) {
            return;
        }
        for (String str : string.split(",")) {
            if (str.equals(Build.DEVICE)) {
                this.mTPActiveSettingEnabled = true;
                if (Constant.DEBUG) {
                    Log.d(TAG, "updateCloudData device " + str + " is supported.");
                }
            }
        }
        if (this.mTPActiveSettingEnabled) {
            String string2 = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_TP_ACTIVE_TIME, null);
            if (Constant.DEBUG) {
                Log.d(TAG, "updateCloudData value=" + string2);
            }
            this.mTPActiveTimeList.clear();
            if (string2 == null || string2.length() == 0) {
                return;
            }
            try {
                jSONArray = new JSONArray(string2);
            } catch (JSONException e2) {
                Log.e(TAG, "updateCloudData", e2);
            }
            if (jSONArray == null || jSONArray.length() == 0) {
                return;
            }
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                TPActiveTime tPActiveTime = new TPActiveTime();
                try {
                    JSONObject jSONObject = jSONArray.getJSONObject(i2);
                    int iOptInt = jSONObject.optInt("time", -1);
                    tPActiveTime.mActiveTime = iOptInt;
                    if (iOptInt < 0) {
                        Log.e(TAG, "JSON config incorrect time");
                    } else {
                        int iOptInt2 = jSONObject.optInt("power_group_id", -1);
                        tPActiveTime.mGroupId = iOptInt2;
                        if (iOptInt2 < 0) {
                            Log.e(TAG, "JSON config incorrect group id");
                        } else {
                            this.mTPActiveTimeList.add(tPActiveTime);
                        }
                    }
                } catch (JSONException e3) {
                    Log.e(TAG, "updateCloudData exception", e3);
                }
            }
        }
    }

    public static void updateCloudTouchCtrlPkgs(ArraySet<String> arraySet) {
        mCloudTouchCtrlPkgs = arraySet;
    }

    private void writeLocalLog(String str) {
        if (this.DBG_TOUCHMODE) {
            Log.i(TAG, str);
        }
        this.mLocalLog.log(str);
    }

    void destroy() {
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.unregisterForForegroundInfo(this);
        }
        removeCallbacksAndMessages(null);
    }

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("tpconfig mDynamicRateDevice = " + this.mDynamicRateDevice + ", mDynamicRateSwitch = " + this.mDynamicRateSwitch);
        printWriter.println("tpconfig mTouchModeApps = " + this.mTouchModeApps + ", version = " + this.mDynamicRateVersion);
        StringBuilder sb = new StringBuilder();
        sb.append("tpconfig mCloudTouchCtrlPkgs = ");
        sb.append(mCloudTouchCtrlPkgs);
        printWriter.println(sb.toString());
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        super.handleMessage(message);
        int i2 = message.what;
        if (i2 == -9) {
            this.mCurrentFgInfo = (ForegroundInfo) message.obj;
            notifyFGChange();
        } else if (i2 == 2) {
            if (Constant.DEBUG) {
                Log.d(TAG, "MSG_CLOUD_TOUCH_PKGS_UPDATE");
            }
            parseTouchModeAppFromDb((String) message.obj);
        } else {
            if (i2 != 3) {
                return;
            }
            if (Constant.DEBUG) {
                Log.d(TAG, "MSG_CHEKC_FG_CONDITION");
            }
            notifyFGChange();
        }
    }

    void setOrResetTPActiveTime(PowerStateMachine.PowerState powerState) {
        if (this.mTPActiveSettingEnabled) {
            for (TPActiveTime tPActiveTime : this.mTPActiveTimeList) {
                if (tPActiveTime.mGroupId == powerState.mId) {
                    setTPActiveTime(tPActiveTime);
                    return;
                }
            }
            resetTPActiveTime();
        }
    }

    private void init() {
        updateCloudData();
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.OWNER);
        this.mContentObserver = new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.TouchController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                String string = uri.toString();
                if (Constant.DEBUG) {
                    Log.i(TouchController.TAG, "onChange uri=" + string);
                }
                if (string.contains(SimpleSettingKeys.KEY_TP_ACTIVE_DEVICES) || string.contains(SimpleSettingKeys.KEY_TP_ACTIVE_TIME)) {
                    TouchController.this.sendEmptyMessage(1);
                }
            }
        };
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_TP_ACTIVE_DEVICES), false, this.mContentObserver);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_TP_ACTIVE_TIME), false, this.mContentObserver);
    }
}
