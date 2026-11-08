package com.miui.powerkeeper.statemachine;

import android.app.ActivityTaskManager;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.IDockedStackListener;
import android.view.IWindowManager;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.active.ActiveController;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.cloudcontrol.CloudUpdateHideMode;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.cloudcontrol.PowerKeeperCloudControlApp;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.provider.CloudAppConfigure;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.provider.UserConfigureHelper;
import com.miui.powerkeeper.thermal.ThermalSwitchHandler;
import com.miui.powerkeeper.utils.AssetUtils;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.DanmuScenarioUtils;
import com.miui.powerkeeper.utils.IrisServiceUtils;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.PowerStateNativeProxy;
import com.miui.powerkeeper.utils.ProcessObserver;
import com.miui.powerkeeper.utils.ProcessUtils;
import com.miui.powerkeeper.utils.SensorServiceLogRecord;
import com.miui.powerkeeper.utils.Utils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import miui.process.ForegroundInfo;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerStateMachine extends Handler implements ProcessObserver.ForegroundListener {
    private static final String ACTIVITYTASKMANAGER = "android.app.ActivityTaskManager";
    private static final String CAMERA_APP = "com.android.camera";
    private static final int CLEAN_POLICY_BY_NORMAL = 1;
    private static final int CLEAN_POLICY_BY_PRIORITY = 2;
    private static final boolean DBG_SM = Log.isLoggable("power.sm", 3);
    private static final int DEFAULT_KILL_PRIORITY = 906;
    public static final boolean DEFAULT_OFF_DEVICES;
    private static final String METHOD_GETSERVICE = "getService";
    private static final String SOURCE_PROFILE_CONF = "profile_sample.conf";
    public static final String TAG = "PowerStateMachine";
    private static final int TYPE_APP_LIST = 1;
    private static final String USELESS_USER_PROFILE_PATH = "/data/data/com.miui.powerkeeper/files/profile.conf";
    private static boolean mIsNewClearUnactiveCloud;
    private static String mUnactiveApps;
    private final String ACTION_NAME_THERMAL;
    private SparseArray<PowerState> mAllPowerState;
    private ArrayMap<String, Integer> mAppStateMap;
    private DisplayCabcHandler mCabcHandler;
    private CallStateReceiver mCallStateReceiver;
    private CastingHandler mCastingHandler;
    private b.a mCloudObserver;
    private ContentObserver mContentObserver;
    private Context mContext;
    private PowerState mCurrentState;
    private DebugLabelSetting mDebugLabelSetting;
    private DisplayDfpsHandler mDfpsHandler;
    private DisplayFrameSetting mDisplayFrameSetting;
    private FKVideoReceiver mFKVideoReceiver;
    private ComponentName mForegroundComponentName;
    private boolean mIsFullScreen;
    private boolean mIsMultiScreen;
    private boolean mIsMultiScreenMinimized;
    private boolean mIsSplitScreen;
    private boolean mIsSwitchViaMcd;
    private Object mPowerStateLock;
    private ProcessObserver mProcessObserver;
    private boolean mScreenOn;
    private SyncController mSyncController;
    private final TaskStackListener mTaskStackListener;
    private ThermalSwitchHandler mThermalHandler;
    private ThermalIECHandler mThermalIECHandler;
    private TouchController mTouchController;

    private class DockedStackListener extends IDockedStackListener.Stub {
        public void onDockedStackExistsChanged(boolean z2) throws RemoteException {
            if (PowerStateMachine.DBG_SM) {
                Log.d(PowerStateMachine.TAG, "onDockedStackExistsChanged exists=" + z2);
            }
            PowerStateMachine.this.sendMessage(PowerStateMachine.this.obtainMessage(1002, z2 ? 1 : 0, -1));
        }

        public void onDockedStackMinimizedChanged(boolean z2, long j2) throws RemoteException {
            if (PowerStateMachine.DBG_SM) {
                Log.d(PowerStateMachine.TAG, "onDockedStackMinimizedChanged minimized=" + z2);
            }
            PowerStateMachine.this.sendMessage(PowerStateMachine.this.obtainMessage(1003, z2 ? 1 : 0, -1));
        }

        private DockedStackListener() {
        }

        public void onDockedStackMinimizedChanged(boolean z2, long j2, boolean z3) {
            if (PowerStateMachine.DBG_SM) {
                Log.d(PowerStateMachine.TAG, "onDockedStackMinimizedChanged minimized=" + z2);
            }
            PowerStateMachine.this.sendMessage(PowerStateMachine.this.obtainMessage(1003, z2 ? 1 : 0, -1));
        }

        public void onDividerVisibilityChanged(boolean z2) throws RemoteException {
        }

        public void onDockSideChanged(int i2) throws RemoteException {
        }

        public void onAdjustedForImeChanged(boolean z2, long j2) throws RemoteException {
        }
    }

    class EventAction {
        private Pair<String, String>[] action;
        private String name;

        EventAction(String str, List<Pair<String, String>> list) {
            this.name = str;
            Pair<String, String>[] pairArr = new Pair[list.size()];
            this.action = pairArr;
            this.action = (Pair[]) list.toArray(pairArr);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof EventAction)) {
                return false;
            }
            EventAction eventAction = (EventAction) obj;
            if (!this.name.equals(eventAction.name) || this.action.length != eventAction.action.length) {
                return false;
            }
            int i2 = 0;
            while (true) {
                Pair<String, String>[] pairArr = this.action;
                if (i2 >= pairArr.length) {
                    return true;
                }
                if (!pairArr[i2].equals(eventAction.action[i2])) {
                    return false;
                }
                i2++;
            }
        }
    }

    public class PowerState {
        public static final String KEY_ACTION_NAME = "action_name";
        public static final String KEY_ACTION_PARAM = "action_param";
        public static final String KEY_PACKAGE_NAME = "package_name";
        public static final String KEY_TYPE = "type";
        public static final int OPERATION_TYPE_REMOVE = 1;
        public static final int OPERATION_TYPE_UPDATE = 2;
        public static final int POWER_MODE_4K_VIDEO_END = 109;
        public static final int POWER_MODE_4K_VIDEO_START = 108;
        public static final int POWER_MODE_8K_VIDEO_START = 201;
        public static final int POWER_MODE_CUSTOM_DEFINE = 1;
        public static final int POWER_MODE_DEFAULT = 0;
        public static final int POWER_MODE_GAME = 9;
        public static final int POWER_MODE_GAME2 = 16;
        public static final int POWER_MODE_GAME2_MOVIE = 107;
        public static final int POWER_MODE_GAME_PUBG = 13;
        public static final int POWER_MODE_GAME_PUBG_MOVIE = 106;
        public static final int POWER_MODE_HOME = 3;
        public static final int POWER_MODE_IDLE = 1;
        public static final int POWER_MODE_INCALL = 4;
        public static final int POWER_MODE_KEYGUARD = 2;
        public static final int POWER_MODE_SYSTEM_DEFINE = 0;
        protected EventAction[] mEventsActions = new EventAction[0];
        protected final int mId;
        private String[] mMimeApps;
        protected final String mName;

        public PowerState(int i2, String str) {
            this.mId = i2;
            this.mName = str;
        }

        public boolean hasAction(String str, String str2) {
            if (this.mEventsActions != null && str != null && str2 != null) {
                int i2 = 0;
                while (true) {
                    EventAction[] eventActionArr = this.mEventsActions;
                    if (i2 >= eventActionArr.length) {
                        break;
                    }
                    if (eventActionArr[i2].action != null) {
                        for (Pair pair : this.mEventsActions[i2].action) {
                            if (str.equals(pair.first) && str2.equals(pair.second)) {
                                return true;
                            }
                        }
                    }
                    i2++;
                }
            }
            return false;
        }

        public boolean hasEvent(String str) {
            if (str != null && !str.isEmpty()) {
                int i2 = 0;
                while (true) {
                    EventAction[] eventActionArr = this.mEventsActions;
                    if (i2 >= eventActionArr.length) {
                        break;
                    }
                    if (str.equals(eventActionArr[i2].name)) {
                        return true;
                    }
                    i2++;
                }
            }
            return false;
        }

        public boolean needUpdateToMcd(PowerState powerState) {
            if (powerState == null || !this.mName.equals(powerState.mName) || this.mEventsActions.length != powerState.mEventsActions.length) {
                return true;
            }
            int i2 = 0;
            while (true) {
                EventAction[] eventActionArr = this.mEventsActions;
                if (i2 >= eventActionArr.length) {
                    return false;
                }
                if (!eventActionArr[i2].equals(powerState.mEventsActions[i2])) {
                    return true;
                }
                i2++;
            }
        }

        public void toMcdParcel(Parcel parcel) {
            parcel.writeInt(this.mId);
            parcel.writeString(this.mName);
            int length = this.mEventsActions.length;
            parcel.writeInt(length);
            for (int i2 = 0; i2 < length; i2++) {
                parcel.writeString(this.mEventsActions[i2].name);
                int length2 = this.mEventsActions[i2].action.length;
                parcel.writeInt(length2);
                for (int i3 = 0; i3 < length2; i3++) {
                    parcel.writeString((String) this.mEventsActions[i2].action[i3].first);
                    parcel.writeString((String) this.mEventsActions[i2].action[i3].second);
                }
            }
        }

        public String toString() {
            return "{id=" + this.mId + " name=" + this.mName + '}';
        }
    }

    public static class TimeStats {
        int mErrorTimes;
        int mTimes;
        int mTotalSec;

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append("{times=");
            sb.append(this.mTimes);
            sb.append(" totalSec=");
            sb.append(this.mTotalSec);
            sb.append(" errorTimes=");
            sb.append(this.mErrorTimes);
            sb.append('}');
            return sb.toString();
        }
    }

    static {
        String str = Build.DEVICE;
        DEFAULT_OFF_DEVICES = str.equalsIgnoreCase("begonia") || str.equalsIgnoreCase("begoniain") || str.equalsIgnoreCase("cezanne");
        mIsNewClearUnactiveCloud = false;
        mUnactiveApps = null;
    }

    PowerStateMachine(String str, Context context, PowerKeeperManager powerKeeperManager) {
        super(PowerStateMachineService.getThread().getLooper());
        this.ACTION_NAME_THERMAL = "set_thermal_config";
        this.mPowerStateLock = new Object();
        this.mAllPowerState = new SparseArray<>(12);
        this.mAppStateMap = new ArrayMap<>();
        this.mIsMultiScreen = false;
        this.mIsMultiScreenMinimized = false;
        this.mIsSplitScreen = false;
        this.mTaskStackListener = new TaskStackListener() { // from class: com.miui.powerkeeper.statemachine.PowerStateMachine.2
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
            public void onTaskStackChanged() {
                boolean zIsInSplitScreenMode = PowerStateMachine.this.isInSplitScreenMode();
                if (PowerStateMachine.DBG_SM) {
                    Log.i(PowerStateMachine.TAG, "onTaskStackChanged, splitScreenMode: " + zIsInSplitScreenMode);
                }
                PowerStateMachine.this.sendMessage(PowerStateMachine.this.obtainMessage(4019, zIsInSplitScreenMode ? 1 : 0, -1));
            }
        };
        this.mContext = context;
        sendEmptyMessage(1);
    }

    private void clearAppWhenScreenOffTimeOut() {
        b.a aVar = this.mCloudObserver;
        int i2 = b.b.f37s;
        JSONObject jSONObjectL = aVar.l(i2);
        String functionParameters2 = (!this.mCloudObserver.s(i2) || jSONObjectL == null) ? CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_SCREEN_OFF_CLEAN_APP, null) : jSONObjectL.optString(CloudUpdateHideMode.PARAM2, null);
        String separatedStringAt = Utils.getSeparatedStringAt(functionParameters2, ',', 1);
        int i3 = Utils.toInt(Utils.getSeparatedStringAt(functionParameters2, ',', 3), 2);
        List<String> activePkgList = PowerKeeperManager.getInstance().getActiveController().getActivePkgList(this.mContext, 15);
        if (activePkgList == null) {
            activePkgList = UserConfigureHelper.getNoRestrictApps(this.mContext);
        } else {
            List<String> noRestrictApps = UserConfigureHelper.getNoRestrictApps(this.mContext);
            if (noRestrictApps != null) {
                for (String str : noRestrictApps) {
                    if (!activePkgList.contains(str)) {
                        activePkgList.add(str);
                    }
                }
            }
        }
        if (functionParameters2 == null || functionParameters2.isEmpty()) {
            if (activePkgList == null) {
                activePkgList = new ArrayList<>(1);
            }
            if (!activePkgList.contains("com.sankuai.meituan.meituanwaimaibusiness")) {
                activePkgList.add("com.sankuai.meituan.meituanwaimaibusiness");
            }
        } else {
            String separatedStringAt2 = Utils.getSeparatedStringAt(functionParameters2, ',', 2);
            if (separatedStringAt2 != null) {
                String[] strArrSplit = separatedStringAt2.split(";");
                for (int i4 = 0; i4 < strArrSplit.length; i4++) {
                    String str2 = strArrSplit[i4];
                    if (str2 != null && !str2.isEmpty()) {
                        if (activePkgList == null) {
                            activePkgList = new ArrayList<>(strArrSplit.length);
                        }
                        if (!activePkgList.contains(strArrSplit[i4])) {
                            activePkgList.add(strArrSplit[i4]);
                        }
                    }
                }
            }
        }
        if (DBG_SM) {
            Log.d(TAG, "clearAppWhenScreenOffTimeOut priority=" + separatedStringAt + " whitelist=" + activePkgList + " policy=" + i3);
        }
        if ((i3 & 2) == 2) {
            ProcessConfig processConfig = new ProcessConfig(17);
            processConfig.setPriority(convertToValidPriority(separatedStringAt));
            if (activePkgList != null) {
                processConfig.setWhiteList(activePkgList);
            }
            try {
                ProcessManager.kill(processConfig);
            } catch (Exception e2) {
                Log.e(TAG, "clearAppWhenScreenOffTimeOut by priority", e2);
            }
            PowerLog.recordKey("AutoLockOffCleanByPriority");
        }
        if ((i3 & 1) == 1) {
            ProcessConfig processConfig2 = new ProcessConfig(15);
            if (activePkgList != null) {
                processConfig2.setWhiteList(activePkgList);
            }
            try {
                ProcessManager.kill(processConfig2);
            } catch (Exception e3) {
                Log.e(TAG, "clearAppWhenScreenOffTimeOut by normal", e3);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("AutoLockOffClean ");
            sb.append(activePkgList == null ? "" : activePkgList.toString());
            PowerLog.recordKey(sb.toString());
        }
        clearUnactiveApps(this.mContext);
    }

    private void clearAppWhenScreenOffTimeOutInNight() {
        ProcessConfig processConfig = new ProcessConfig(15);
        List<String> activePkgList = PowerKeeperManager.getInstance().getActiveController().getActivePkgList(this.mContext, 3);
        if (activePkgList != null) {
            processConfig.setWhiteList(activePkgList);
        }
        b.a aVar = this.mCloudObserver;
        int i2 = b.b.f38t;
        JSONObject jSONObjectL = aVar.l(i2);
        String functionParameters2 = (!this.mCloudObserver.s(i2) || jSONObjectL == null) ? CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_NIGHT_CLEAN_PROCESS, "") : jSONObjectL.optString(CloudUpdateHideMode.PARAM2, "");
        StringBuilder sb = new StringBuilder();
        sb.append("AutoLockOffClean in night ");
        sb.append(activePkgList != null ? activePkgList.toString() : "");
        PowerLog.recordKey(sb.toString());
        if (DBG_SM) {
            Log.d(TAG, "clearAppWhenScreenOffTimeOutInNight whiteList=" + activePkgList + " processes=" + functionParameters2);
        }
        try {
            ProcessManager.kill(processConfig);
            ProcessUtils.killProcess(functionParameters2.split(","));
        } catch (Exception e2) {
            Log.e(TAG, "clearAppWhenScreenOffTimeOutInNight", e2);
        }
        clearUnactiveApps(this.mContext);
    }

    public static void clearUnactiveApps(Context context) {
        if (mUnactiveApps == null) {
            b.a aVarP = b.a.p();
            int i2 = b.b.f39u;
            JSONObject jSONObjectL = aVarP.l(i2);
            if (!aVarP.s(i2) || jSONObjectL == null) {
                mUnactiveApps = SimpleSettings.Misc.getString(context, "clear_unactive_apps", "");
                if (DBG_SM) {
                    Log.d(TAG, "clearUnactiveApps old cloud configured ID_CLEAR_UNACTIVE_APPS: " + mUnactiveApps);
                }
                mIsNewClearUnactiveCloud = false;
            } else {
                mUnactiveApps = jSONObjectL.toString();
                if (DBG_SM) {
                    Log.d(TAG, "clearUnactiveApps new cloud configured ID_CLEAR_UNACTIVE_APPS: " + mUnactiveApps);
                }
                mIsNewClearUnactiveCloud = true;
            }
        }
        String str = mUnactiveApps;
        if (str.isEmpty()) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.optBoolean("fucSwitch", false)) {
                String strOptString = jSONObject.optString("pkgs", "");
                if (strOptString.isEmpty()) {
                    return;
                }
                String[] strArrSplit = strOptString.split(",");
                ActiveController activeController = PowerKeeperManager.getInstance().getActiveController();
                SparseIntArray unActiveTime = activeController.getUnActiveTime();
                for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                    try {
                        boolean z2 = DBG_SM;
                        if (z2) {
                            Log.d(TAG, "clearUnactiveApps pkg=" + strArrSplit[i3]);
                        }
                        List runningProcessInfoByPackageName = ProcessManager.getRunningProcessInfoByPackageName(strArrSplit[i3]);
                        if (runningProcessInfoByPackageName != null && !runningProcessInfoByPackageName.isEmpty()) {
                            int i4 = ((RunningProcessInfo) runningProcessInfoByPackageName.get(0)).mUid;
                            int i5 = unActiveTime.get(i4, -1);
                            if (z2) {
                                Log.d(TAG, "clearUnactiveApps unactiveTime=" + i5);
                            }
                            if (i5 == 0) {
                                PowerLog.recordKey("clearUnactiveApps: ignore for audio " + strArrSplit[i3]);
                            } else if (i5 > 0 && (SystemClock.elapsedRealtime() / 1000) - ((long) i5) < 180) {
                                PowerLog.recordKey("clearUnactiveApps: ignore for just now audio " + strArrSplit[i3]);
                            } else if ((activeController.getAppActiveType(i4) & 4) != 0) {
                                PowerLog.recordKey("clearUnactiveApps: ignore for gps " + strArrSplit[i3]);
                            } else if (!ProcessManager.isLockedApplication(strArrSplit[i3], 0)) {
                                ProcessConfig processConfig = new ProcessConfig(13, strArrSplit[i3], i4);
                                if (z2) {
                                    Log.d(TAG, "clearUnactiveApps: " + strArrSplit[i3]);
                                }
                                PowerLog.recordKey("clearUnactiveApps: " + strArrSplit[i3]);
                                ProcessManager.kill(processConfig);
                            } else if (z2) {
                                Log.d(TAG, "clearUnactiveApps isLockedApplication");
                            }
                        }
                    } catch (RemoteException e2) {
                        Log.e(TAG, "clearUnactiveApps", e2);
                    }
                }
            }
        } catch (JSONException e3) {
            Log.e(TAG, "clearUnactiveApps", e3);
        }
    }

    private int convertToValidPriority(String str) {
        if (str == null || str.isEmpty()) {
            return DEFAULT_KILL_PRIORITY;
        }
        try {
            int i2 = Integer.parseInt(str);
            if (i2 >= 300) {
                return i2;
            }
            return 300;
        } catch (Exception unused) {
            return DEFAULT_KILL_PRIORITY;
        }
    }

    private String ensureConfig() {
        String string = SimpleSettings.Misc.getString(Utils.APP_CONTEXT, "power_group", null);
        if (string == null) {
            try {
                new File(USELESS_USER_PROFILE_PATH).delete();
            } catch (Exception e2) {
                Log.i(TAG, "ensureConfig", e2);
            }
            try {
                string = new JSONObject(AssetUtils.getStrFromAssetFile(Utils.APP_CONTEXT, SOURCE_PROFILE_CONF)).optString("power_group", "");
            } catch (JSONException e3) {
                Log.i(TAG, "ensureConfig", e3);
                string = "";
            }
            Context context = Utils.APP_CONTEXT;
            SimpleSettings.Misc.putString(context, "power_group", string);
            SimpleSettings.Misc.putString(context, Constants.LAST_DATA_MD5, "");
        }
        return string;
    }

    private boolean enterState(PowerState powerState) {
        PowerState powerState2;
        int i2;
        boolean z2 = false;
        if (powerState != null && (powerState2 = this.mCurrentState) != null && (i2 = powerState2.mId) != powerState.mId) {
            z2 = i2 != 4;
            if (z2) {
                updateCurrentState(powerState);
            }
        }
        return z2;
    }

    private boolean exitAndRestoreState(PowerState powerState) {
        if (powerState == null) {
            return false;
        }
        if (powerState.mId != 4) {
            return true;
        }
        processScreenCase(this.mScreenOn);
        return true;
    }

    private PowerState getForgroundAppState() {
        ForegroundInfo appInfo;
        PowerState appState = ((!this.mIsMultiScreen || this.mIsMultiScreenMinimized) && (appInfo = this.mProcessObserver.getAppInfo()) != null) ? getAppState(appInfo.mForegroundPackageName) : null;
        return appState == null ? getState(0) : appState;
    }

    private boolean getKillAppEnable() {
        b.a aVar = this.mCloudObserver;
        boolean zOptBoolean = false;
        if (aVar != null) {
            int i2 = b.b.I;
            JSONObject jSONObjectL = aVar.l(i2);
            if (this.mCloudObserver.s(i2) && jSONObjectL != null) {
                zOptBoolean = jSONObjectL.optBoolean("screen_on_witch", false);
                if (Constant.DEBUG) {
                    Log.d(TAG, "getKillAppEnable screen_on_witch is " + zOptBoolean);
                }
            }
        }
        return zOptBoolean;
    }

    private boolean getNightKillAppEnable() {
        b.a aVar = this.mCloudObserver;
        boolean zOptBoolean = false;
        if (aVar != null) {
            int i2 = b.b.I;
            JSONObject jSONObjectL = aVar.l(i2);
            if (this.mCloudObserver.s(i2)) {
                zOptBoolean = jSONObjectL.optBoolean("night_switch", false);
                if (DBG_SM) {
                    Log.d(TAG, "PowerStateMachine nightCleanEnable is " + zOptBoolean);
                }
            }
        }
        return zOptBoolean;
    }

    private PowerState getState(int i2) {
        PowerState powerState;
        synchronized (this.mPowerStateLock) {
            powerState = this.mAllPowerState.get(i2);
        }
        return powerState;
    }

    private void init() {
        this.mCloudObserver = b.a.p();
        registerCloudObserver();
        registerTaskStackListener();
        Looper looper = PowerStateMachineService.getThread().getLooper();
        this.mScreenOn = PowerKeeperManager.getInstance().isScreenOn();
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        this.mCallStateReceiver = new CallStateReceiver(this.mContext, eventsAggregator);
        this.mFKVideoReceiver = new FKVideoReceiver(this.mContext, eventsAggregator);
        this.mDisplayFrameSetting = DisplayFrameSetting.init(this.mContext, looper);
        this.mTouchController = TouchController.init(this.mContext, looper);
        this.mDebugLabelSetting = DebugLabelSetting.init(this.mContext, looper);
        this.mThermalIECHandler = ThermalIECHandler.init(this.mContext, looper);
        this.mCastingHandler = CastingHandler.init(this.mContext, looper);
        if (IrisServiceUtils.isDualDPUSupported()) {
            IrisServiceUtils.getInstance(this.mContext).onCreate();
        }
        if (DanmuScenarioUtils.isDanmuSupported()) {
            DanmuScenarioUtils.getInstance(this.mContext).onCreate();
        }
        if (e.e.l(new e.e.a() { // from class: com.miui.powerkeeper.statemachine.b0
            @Override // e.e.a
            public final void a() {
                this.f557a.initOtherModules();
            }
        })) {
            initOtherModules();
        }
        updateAppState();
        updateStateMachine();
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.OWNER);
        this.mContentObserver = new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.PowerStateMachine.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                String string = uri.toString();
                Log.i(PowerStateMachine.TAG, "onChange uri=" + string);
                if (string.contains("power_group")) {
                    PowerStateMachine.this.sendEmptyMessage(4012);
                    return;
                }
                if (string.contains(CloudAppConfigure.CONTENT_URI.toString())) {
                    PowerStateMachine powerStateMachine = PowerStateMachine.this;
                    powerStateMachine.sendMessage(powerStateMachine.obtainMessage(4014, 1, -1));
                } else if (string.contains(SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME) || string.contains(SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES)) {
                    PowerStateMachine.this.sendEmptyMessage(4014);
                }
            }
        };
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor("power_group"), false, this.mContentObserver);
        contentResolverForUser.registerContentObserver(CloudAppConfigure.CONTENT_URI, true, this.mContentObserver);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME), false, this.mContentObserver);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES), false, this.mContentObserver);
        registerDockedStackListener();
        ProcessObserver processObserver = PowerKeeperManager.getInstance().getProcessObserver();
        this.mProcessObserver = processObserver;
        processObserver.setForegroundListener(this);
        PowerKeeperManager.getInstance().getEventsAggregator().registerForUserPresent(this, null);
    }

    private void initInitialState() {
        CallStateReceiver callStateReceiver;
        PowerState state = getState(4);
        if (state == null || (callStateReceiver = this.mCallStateReceiver) == null || !callStateReceiver.isInCall()) {
            updateCurrentState(getState(0));
        } else {
            updateCurrentState(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initOtherModules() {
        f.b0 b0Var = (f.b0) e.e.i(16);
        if (b0Var != null) {
            this.mSyncController = b0Var.c();
        }
        f.f fVar = (f.f) e.e.i(10);
        if (fVar != null) {
            this.mDfpsHandler = fVar.c();
        }
        f.e eVar = (f.e) e.e.i(11);
        if (eVar != null) {
            this.mCabcHandler = eVar.c();
        }
        this.mIsSwitchViaMcd = isSwitchViaMcd();
    }

    private boolean isGameModeApp(int i2) {
        return i2 == 9 || i2 == 13 || i2 == 16 || i2 == 106 || i2 == 107;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInSplitScreenMode() {
        try {
            Class[] clsArr = new Class[0];
            Object objInvoke = Class.forName(ACTIVITYTASKMANAGER).getMethod(METHOD_GETSERVICE, null).invoke(null, null);
            Class[] clsArr2 = new Class[0];
            return ((Boolean) objInvoke.getClass().getMethod("isInSplitScreenWindowingMode", null).invoke(objInvoke, null)).booleanValue();
        } catch (Exception e2) {
            Log.e(TAG, "getStackInfo exception : " + e2);
            return false;
        }
    }

    private boolean isSwitchViaMcd() {
        return false;
    }

    public static void onCloudUpdate(Context context, JSONObject jSONObject) {
        if (mIsNewClearUnactiveCloud) {
            return;
        }
        String strOptString = jSONObject.optString("clear_unactive_apps", "");
        if (DBG_SM) {
            Log.d(TAG, "onCloudUpdate newCloud=" + strOptString);
        }
        if (strOptString.equals(SimpleSettings.Misc.getString(context, "clear_unactive_apps", ""))) {
            return;
        }
        SimpleSettings.Misc.putString(context, "clear_unactive_apps", strOptString);
    }

    private EventAction[] parsePowerStateActions(JSONObject jSONObject) {
        JSONArray jSONArrayOptJSONArray;
        try {
            JSONArray jSONArrayOptJSONArray2 = jSONObject.optJSONArray("event_actions");
            if (jSONArrayOptJSONArray2 == null) {
                return new EventAction[0];
            }
            int length = jSONArrayOptJSONArray2.length();
            ArrayList arrayList = new ArrayList(length);
            for (int i2 = 0; i2 < length; i2++) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray2.optJSONObject(i2);
                if (jSONObjectOptJSONObject != null) {
                    String strOptString = jSONObjectOptJSONObject.optString("event_name", "");
                    if (!strOptString.isEmpty() && (jSONArrayOptJSONArray = jSONObjectOptJSONObject.optJSONArray(PowerKeeperCloudControlApp.ACTION_LIST)) != null) {
                        int length2 = jSONArrayOptJSONArray.length();
                        ArrayList arrayList2 = new ArrayList(length2);
                        for (int i3 = 0; i3 < length2; i3++) {
                            JSONObject jSONObjectOptJSONObject2 = jSONArrayOptJSONArray.optJSONObject(i3);
                            if (jSONObjectOptJSONObject2 != null) {
                                String strOptString2 = jSONObjectOptJSONObject2.optString(PowerState.KEY_ACTION_NAME, "");
                                if (!strOptString2.isEmpty() && (this.mThermalHandler == null || !strOptString2.equals("set_thermal_config"))) {
                                    String strOptString3 = jSONObjectOptJSONObject2.optString(PowerState.KEY_ACTION_PARAM, "");
                                    if (!strOptString3.isEmpty()) {
                                        arrayList2.add(new Pair(strOptString2, strOptString3));
                                    }
                                }
                            }
                        }
                        if (!arrayList2.isEmpty()) {
                            arrayList.add(new EventAction(strOptString, arrayList2));
                        }
                    }
                }
            }
            EventAction[] eventActionArr = new EventAction[arrayList.size()];
            arrayList.toArray(eventActionArr);
            return eventActionArr;
        } catch (Exception e2) {
            Log.e(TAG, "parsePowerStateActions exception", e2);
            return new EventAction[0];
        }
    }

    private String[] parsePowerStateMimeList(JSONObject jSONObject) {
        String str;
        try {
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("mime_list");
            if (jSONArrayOptJSONArray == null) {
                Log.i(TAG, "no mine list");
                return null;
            }
            int length = jSONArrayOptJSONArray.length();
            ArrayList arrayList = null;
            for (int i2 = 0; i2 < length; i2++) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i2);
                if (jSONObjectOptJSONObject != null) {
                    String strOptString = jSONObjectOptJSONObject.optString("type", "");
                    if (!strOptString.isEmpty()) {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setDataAndType(Uri.parse("file://"), strOptString);
                        for (ResolveInfo resolveInfo : this.mContext.getPackageManager().queryIntentActivities(intent, LogType.LOG_TYPE_SAMPLING_INTERRUPT)) {
                            String str2 = resolveInfo.activityInfo.applicationInfo.processName;
                            if (str2 != null && !str2.isEmpty() && (str = resolveInfo.activityInfo.packageName) != null) {
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                    arrayList.add(resolveInfo.activityInfo.packageName);
                                } else if (!arrayList.contains(str)) {
                                    arrayList.add(resolveInfo.activityInfo.packageName);
                                }
                            }
                        }
                    }
                }
            }
            if (arrayList == null) {
                return null;
            }
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        } catch (Exception e2) {
            Log.e(TAG, "updateMimeList exception", e2);
            return null;
        }
    }

    private boolean processScreenCase(boolean z2) {
        PowerState forgroundAppState = z2 ? getForgroundAppState() : getState(1);
        if (forgroundAppState == null) {
            return false;
        }
        updateCurrentState(forgroundAppState);
        if (z2 && this.mIsFullScreen) {
            triggerEvent(forgroundAppState, "full_screen_enter");
            return true;
        }
        triggerEvent(forgroundAppState, "full_screen_exit");
        return true;
    }

    private void registerCloudObserver() {
        this.mCloudObserver.t(b.b.f39u, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.PowerStateMachine.3
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                if (jSONObject == null) {
                    if (PowerStateMachine.DBG_SM) {
                        Log.d(PowerStateMachine.TAG, "Cloud-Observer onChange old ID_CLEAR_UNACTIVE_APPS");
                    }
                    PowerStateMachine.mUnactiveApps = null;
                    PowerStateMachine.mIsNewClearUnactiveCloud = false;
                    return;
                }
                if (PowerStateMachine.DBG_SM) {
                    Log.d(PowerStateMachine.TAG, "Cloud-Observer onChange ID_CLEAR_UNACTIVE_APPS: " + jSONObject.toString());
                }
                PowerStateMachine.mUnactiveApps = jSONObject.toString();
                PowerStateMachine.mIsNewClearUnactiveCloud = true;
            }
        });
        this.mCloudObserver.t(b.b.f35q, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.PowerStateMachine.4
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                try {
                    Log.d(PowerStateMachine.TAG, "Cloud-Observer onChange ID_DYNAMIC_TURBO_POWER");
                    MiawareBcasHandler.notifyUpdateBcasCloud(jSONObject);
                    DynamicTurboPowerHandler.notifyUpdateNewCloud(jSONObject);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    private void registerDockedStackListener() {
        IWindowManager iWindowManagerAsInterface = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        if (iWindowManagerAsInterface == null) {
            Log.e(TAG, "registerDockedStackListener windowManager is null");
            return;
        }
        try {
            Method declaredMethod = IWindowManager.class.getDeclaredMethod("registerDockedStackListener", IDockedStackListener.class);
            if (declaredMethod == null) {
                Log.e(TAG, "registerDockedStackListener method is null");
            } else {
                declaredMethod.invoke(iWindowManagerAsInterface, new DockedStackListener());
            }
        } catch (Exception e2) {
            Log.e(TAG, "registerDockedStackListener Failed", e2);
        }
    }

    private void triggerEvent(PowerState powerState, String str) {
        if (powerState == null || !powerState.hasEvent(str)) {
            return;
        }
        if (DBG_SM) {
            Log.d(TAG, "triggerEvent:" + str + " in state:" + powerState.mName);
        }
        if (this.mIsSwitchViaMcd) {
            PowerStateNativeProxy.triggerEvent(str, powerState.mId);
        }
    }

    /* JADX WARN: Code duplicated, block: B:51:0x00e4 A[Catch: all -> 0x003b, TryCatch #1 {all -> 0x003b, blocks: (B:4:0x0003, B:6:0x0018, B:7:0x0028, B:9:0x002b, B:17:0x005d, B:18:0x0060, B:44:0x00d2, B:49:0x00e0, B:51:0x00e4, B:52:0x0100, B:55:0x0104, B:56:0x0107, B:14:0x003f, B:20:0x0062, B:21:0x006e, B:23:0x0074, B:26:0x0080, B:28:0x0084, B:33:0x0091, B:35:0x0095, B:37:0x009b, B:39:0x00a5, B:41:0x00a9, B:42:0x00c4, B:46:0x00d6), top: B:61:0x0003, inners: #2 }] */
    /* JADX WARN: Instruction removed from duplicated block: B:51:0x00e4, please report this as an issue */
    private void updateAppState() {
        String[] strArrSplit;
        synchronized (this.mPowerStateLock) {
            try {
                this.mAppStateMap.erase();
                boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME, !DEFAULT_OFF_DEVICES);
                Cursor cursorQuery = null;
                if (z2) {
                    strArrSplit = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES, "").split(",");
                    for (String str : strArrSplit) {
                        this.mAppStateMap.put(str, 16);
                    }
                } else {
                    strArrSplit = null;
                }
                try {
                    try {
                        cursorQuery = this.mContext.getContentResolverForUser(UserHandle.OWNER).query(CloudAppConfigure.CONTENT_URI, new String[]{"pkgName", CloudAppConfigure.Columns.POWER_STATE_ID}, "power_state_id>=0", null, null);
                        if (cursorQuery == null) {
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return;
                        }
                        int columnIndex = cursorQuery.getColumnIndex("pkgName");
                        int columnIndex2 = cursorQuery.getColumnIndex(CloudAppConfigure.Columns.POWER_STATE_ID);
                        while (cursorQuery.moveToNext()) {
                            int i2 = cursorQuery.getInt(columnIndex2);
                            if (!isGameModeApp(i2) || z2) {
                                if (!DEFAULT_OFF_DEVICES || !isGameModeApp(i2) || ArrayUtils.contains(strArrSplit, cursorQuery.getString(columnIndex))) {
                                    this.mAppStateMap.put(cursorQuery.getString(columnIndex), Integer.valueOf(i2));
                                } else if (DBG_SM) {
                                    Log.e(TAG, "updateAppState ignore optimize for " + cursorQuery.getString(columnIndex));
                                }
                            } else if (DBG_SM) {
                                Log.e(TAG, "updateAppState ignore optimize for switch off");
                            }
                        }
                    } catch (Exception e2) {
                        Log.e(TAG, "updateAppState", e2);
                        if (0 != 0) {
                        }
                        if (DBG_SM) {
                            Log.e(TAG, "updateAppState mAppStateMap=" + this.mAppStateMap.toString());
                        }
                    }
                    cursorQuery.close();
                    if (DBG_SM) {
                        Log.e(TAG, "updateAppState mAppStateMap=" + this.mAppStateMap.toString());
                    }
                } catch (Throwable th) {
                    if (0 != 0) {
                        cursorQuery.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    private void updateCurrentState(PowerState powerState) {
        if (powerState == null) {
            return;
        }
        this.mCurrentState = powerState;
        if (DBG_SM) {
            Log.d(TAG, "Enter power state id=" + this.mCurrentState.mId + " name=" + this.mCurrentState.mName);
        }
        if (this.mIsSwitchViaMcd) {
            PowerState powerState2 = this.mCurrentState;
            PowerStateNativeProxy.modeStart(powerState2.mName, powerState2.mId);
        }
    }

    private void updateStateMachine() {
        JSONArray jSONArray;
        String strEnsureConfig = ensureConfig();
        if (strEnsureConfig.isEmpty()) {
            Log.e(TAG, "updateStateMachine error for power state db is empty");
            return;
        }
        try {
            jSONArray = new JSONArray(strEnsureConfig);
        } catch (JSONException e2) {
            Log.e(TAG, "updateStateMachine", e2);
            jSONArray = null;
        }
        int length = jSONArray == null ? 0 : jSONArray.length();
        if (length <= 0) {
            Log.e(TAG, "updateStateMachine error for power state config");
            return;
        }
        ArrayList<PowerState> arrayList = new ArrayList(20);
        ArrayList<PowerState> arrayList2 = new ArrayList(20);
        synchronized (this.mPowerStateLock) {
            try {
                SparseArray<PowerState> sparseArray = this.mAllPowerState;
                this.mAllPowerState = new SparseArray<>(length);
                for (int i2 = 0; i2 < length; i2++) {
                    try {
                        JSONObject jSONObject = jSONArray.getJSONObject(i2);
                        int i3 = jSONObject.getInt("group_id");
                        if (i3 < 0) {
                            Log.e(TAG, "JSON config missed id");
                        } else {
                            String strOptString = jSONObject.optString("group_name", "");
                            if (strOptString.isEmpty()) {
                                Log.e(TAG, "JSON config missed name");
                            } else {
                                PowerState powerState = new PowerState(i3, strOptString);
                                this.mAllPowerState.put(i3, powerState);
                                powerState.mEventsActions = parsePowerStateActions(jSONObject);
                                powerState.mMimeApps = parsePowerStateMimeList(jSONObject);
                                if (powerState.needUpdateToMcd(sparseArray.get(powerState.mId))) {
                                    arrayList.add(powerState);
                                }
                            }
                        }
                    } catch (JSONException e3) {
                        Log.e(TAG, "updateStateMachine exception", e3);
                    }
                }
                for (int i4 = 0; i4 < sparseArray.size(); i4++) {
                    if (this.mAllPowerState.get(sparseArray.keyAt(i4)) == null) {
                        arrayList2.add(sparseArray.valueAt(i4));
                    }
                }
                sparseArray.clear();
            } catch (Throwable th) {
                throw th;
            }
        }
        for (PowerState powerState2 : arrayList) {
            Log.i(TAG, "updateStateMachine id=" + powerState2.mId);
            if (this.mIsSwitchViaMcd) {
                PowerStateNativeProxy.updatePowerStateConfig(2, powerState2);
            }
        }
        for (PowerState powerState3 : arrayList2) {
            Log.i(TAG, "updateStateMachine remove id=" + powerState3.mId);
            if (this.mIsSwitchViaMcd) {
                PowerStateNativeProxy.updatePowerStateConfig(1, powerState3);
            }
        }
        initInitialState();
    }

    public void destroy() {
        if (this.mContentObserver != null) {
            this.mContext.getContentResolverForUser(UserHandle.OWNER).unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
        removeCallbacksAndMessages(null);
        synchronized (this.mPowerStateLock) {
            this.mAllPowerState.clear();
        }
        if (e.e.k()) {
            e.e.h().f(16);
        }
        CallStateReceiver callStateReceiver = this.mCallStateReceiver;
        if (callStateReceiver != null) {
            this.mContext.unregisterReceiver(callStateReceiver);
            this.mCallStateReceiver = null;
        }
        FKVideoReceiver fKVideoReceiver = this.mFKVideoReceiver;
        if (fKVideoReceiver != null) {
            this.mContext.unregisterReceiver(fKVideoReceiver);
            this.mFKVideoReceiver = null;
        }
        this.mProcessObserver.setForegroundListener(null);
        PowerKeeperManager.getInstance().getEventsAggregator().unregisterForUserPresent(this);
        DisplayFrameSetting displayFrameSetting = this.mDisplayFrameSetting;
        if (displayFrameSetting != null) {
            displayFrameSetting.destroy();
        }
        TouchController touchController = this.mTouchController;
        if (touchController != null) {
            touchController.destroy();
        }
        DebugLabelSetting debugLabelSetting = this.mDebugLabelSetting;
        if (debugLabelSetting != null) {
            debugLabelSetting.destroy();
        }
        ThermalIECHandler thermalIECHandler = this.mThermalIECHandler;
        if (thermalIECHandler != null) {
            thermalIECHandler.destroy();
        }
        CastingHandler castingHandler = this.mCastingHandler;
        if (castingHandler != null) {
            castingHandler.destroy();
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("--------------PowerStateMachine--------------");
        printWriter.println(" mScreenOn=" + this.mScreenOn + " mForegroundComponentName=" + this.mForegroundComponentName + " mIsFullScreen=" + this.mIsFullScreen);
        StringBuilder sb = new StringBuilder();
        sb.append(" mIsNewClearUnactiveCloud=");
        sb.append(mIsNewClearUnactiveCloud);
        sb.append(" mUnactiveApps=");
        sb.append(mUnactiveApps);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder(512);
        sb2.append(" mIsSwitchViaMcd=");
        sb2.append(this.mIsSwitchViaMcd);
        sb2.append("mIsMultiScreen=");
        sb2.append(this.mIsMultiScreen);
        sb2.append(" mIsMinimized=");
        sb2.append(this.mIsMultiScreenMinimized);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("current powerState:");
        PowerState powerState = this.mCurrentState;
        sb3.append(powerState == null ? "null" : powerState.mName);
        printWriter.println(sb3.toString());
        printWriter.println("PowerState Statistics:");
        if (this.mIsSwitchViaMcd) {
            synchronized (this.mPowerStateLock) {
                try {
                    int size = this.mAllPowerState.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        PowerState powerStateValueAt = this.mAllPowerState.valueAt(i2);
                        printWriter.println(powerStateValueAt.mName + ":[average current: " + PowerStateNativeProxy.getAvgCurrent(powerStateValueAt.mId) + "mA, time in state: " + (PowerStateNativeProxy.getTimeInState(powerStateValueAt.mId) / 60) + "min]");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        SensorServiceLogRecord.dumpSensorHistoryLog(printWriter, new File(this.mContext.getCacheDir().getPath() + "/" + SensorServiceLogRecord.FILE_NAME));
        if (this.mDisplayFrameSetting != null) {
            printWriter.println("DisplayFrameSetting enabled");
            this.mDisplayFrameSetting.dump(fileDescriptor, printWriter, strArr);
        } else {
            printWriter.println("DisplayFrameSetting disabled");
        }
        if (this.mTouchController != null) {
            printWriter.println("mTouchController enabled");
            this.mTouchController.dump(fileDescriptor, printWriter, strArr);
        } else {
            printWriter.println("mTouchController disabled");
        }
        if (this.mDebugLabelSetting != null) {
            printWriter.println("DebugLabelSetting enabled");
            this.mDebugLabelSetting.dump(fileDescriptor, printWriter, strArr);
        } else {
            printWriter.println("DebugLabelSetting disabled");
        }
        if (IrisServiceUtils.isDualDPUSupported()) {
            IrisServiceUtils.getInstance(this.mContext).dump(fileDescriptor, printWriter, strArr);
        }
    }

    public PowerState getAppState(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        synchronized (this.mPowerStateLock) {
            try {
                Integer num = this.mAppStateMap.get(str);
                if (num != null && num.intValue() >= 0) {
                    return this.mAllPowerState.get(num.intValue(), null);
                }
                int size = this.mAllPowerState.size();
                for (int i2 = 0; i2 < size; i2++) {
                    PowerState powerStateValueAt = this.mAllPowerState.valueAt(i2);
                    if (powerStateValueAt.mMimeApps != null) {
                        for (int i3 = 0; i3 < powerStateValueAt.mMimeApps.length; i3++) {
                            if (powerStateValueAt.mMimeApps[i3].equals(str)) {
                                return powerStateValueAt;
                            }
                        }
                    }
                }
                return null;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public PowerState getPrimaryState(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        synchronized (this.mPowerStateLock) {
            try {
                Integer num = this.mAppStateMap.get(str);
                if (num != null && num.intValue() >= 0) {
                    return this.mAllPowerState.get(num.intValue(), null);
                }
                return null;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        SyncController syncController;
        ForegroundInfo appInfo;
        boolean z2 = SystemProperties.getBoolean("persist.sys.spc.powerkill.newpolicy.enable", false);
        int i2 = message.what;
        if (i2 == -7) {
            if (DBG_SM) {
                Log.d(TAG, "EVENT_ID_USER_PRESENT appInfo=" + this.mProcessObserver.getAppInfo());
            }
            DisplayCabcHandler displayCabcHandler = this.mCabcHandler;
            if (displayCabcHandler != null) {
                displayCabcHandler.onForegroundChanged(this.mProcessObserver.getAppInfo(), this.mIsSplitScreen, true);
            }
            enterState(getForgroundAppState());
        }
        if (i2 == 1) {
            init();
            return;
        }
        if (i2 == 4012) {
            updateStateMachine();
            return;
        }
        if (i2 == 4014) {
            updateAppState();
            enterState(getForgroundAppState());
            return;
        }
        if (i2 == 4000) {
            enterState(getState(4));
            return;
        }
        if (i2 == 4001) {
            PowerState powerState = this.mCurrentState;
            if (powerState == null || powerState.mId != 4) {
                return;
            }
            exitAndRestoreState(powerState);
            return;
        }
        switch (i2) {
            case 11:
                this.mScreenOn = true;
                enterState(getState(2));
                SyncController syncController2 = this.mSyncController;
                if (syncController2 != null) {
                    syncController2.onScreenOn();
                }
                break;
            case 12:
                this.mScreenOn = false;
                enterState(getState(1));
                break;
            case 13:
                if (!this.mScreenOn && (syncController = this.mSyncController) != null) {
                    syncController.onScreenOffTimeout();
                }
                break;
            case 14:
                Log.i(TAG, "CLEAN IN TIME OUT milletEnable is " + z2);
                if (!this.mScreenOn) {
                    if (!z2 || getKillAppEnable()) {
                        clearAppWhenScreenOffTimeOut();
                    }
                }
                break;
            case 15:
                Log.i(TAG, "CLEAN IN NIGHT milletEnable is " + z2);
                if (!this.mScreenOn) {
                    if (!z2 || getNightKillAppEnable()) {
                        clearAppWhenScreenOffTimeOutInNight();
                        SensorServiceLogRecord.cacheSensorHistoryLog(new File(this.mContext.getCacheDir().getPath() + "/" + SensorServiceLogRecord.FILE_NAME));
                    }
                }
                break;
            default:
                switch (i2) {
                    case 100:
                        if (DBG_SM) {
                            Log.d(TAG, "EVENT_ID_FOREGROUND appInfo=" + this.mProcessObserver.getAppInfo());
                        }
                        ForegroundInfo appInfo2 = this.mProcessObserver.getAppInfo();
                        DisplayDfpsHandler displayDfpsHandler = this.mDfpsHandler;
                        if (displayDfpsHandler != null) {
                            displayDfpsHandler.onForegroundChanged(appInfo2);
                        }
                        DisplayCabcHandler displayCabcHandler2 = this.mCabcHandler;
                        if (displayCabcHandler2 != null) {
                            displayCabcHandler2.onForegroundChanged(appInfo2, this.mIsSplitScreen);
                        }
                        enterState(getForgroundAppState());
                        break;
                    case 101:
                        if (this.mDisplayFrameSetting != null) {
                            String str = (String) message.obj;
                            if (!TextUtils.isEmpty(str)) {
                                if (PowerLog.V) {
                                    PowerLog.v(TAG, "EVENT_ID_AM_STOP:" + str);
                                }
                                ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(str);
                                if (componentNameUnflattenFromString != null) {
                                    TextUtils.isEmpty(componentNameUnflattenFromString.getPackageName());
                                }
                            }
                        }
                        break;
                    case 102:
                        break;
                    default:
                        switch (i2) {
                            case 1000:
                                this.mIsFullScreen = true;
                                DisplayCabcHandler displayCabcHandler3 = this.mCabcHandler;
                                if (displayCabcHandler3 != null) {
                                    displayCabcHandler3.onFullScreenEnter();
                                }
                                DisplayFrameSetting displayFrameSetting = this.mDisplayFrameSetting;
                                if (displayFrameSetting != null) {
                                    displayFrameSetting.onFullScreenChange(true);
                                }
                                triggerEvent(this.mCurrentState, "full_screen_enter");
                                break;
                            case 1001:
                                this.mIsFullScreen = false;
                                DisplayCabcHandler displayCabcHandler4 = this.mCabcHandler;
                                if (displayCabcHandler4 != null) {
                                    displayCabcHandler4.onFullScreenExit();
                                }
                                DisplayFrameSetting displayFrameSetting2 = this.mDisplayFrameSetting;
                                if (displayFrameSetting2 != null) {
                                    displayFrameSetting2.onFullScreenChange(false);
                                }
                                triggerEvent(this.mCurrentState, "full_screen_exit");
                                break;
                            case 1002:
                                this.mIsMultiScreen = message.arg1 == 1;
                                if (DBG_SM) {
                                    Log.d(TAG, "EVENT_ID_SCREEN_MULTI_SCREEN_CHANGE mIsMultiScreen=" + this.mIsMultiScreen);
                                }
                                String str2 = null;
                                if (!this.mIsMultiScreen) {
                                    ForegroundInfo appInfo3 = PowerKeeperManager.getInstance().getProcessObserver().getAppInfo();
                                    if (appInfo3 != null) {
                                        str2 = appInfo3.mMultiWindowForegroundPackageName;
                                    }
                                } else if (this.mIsMultiScreenMinimized && (appInfo = PowerKeeperManager.getInstance().getProcessObserver().getAppInfo()) != null) {
                                    str2 = appInfo.mForegroundPackageName;
                                }
                                PowerState appState = getAppState(str2);
                                if (appState == null) {
                                    appState = getState(0);
                                }
                                enterState(appState);
                                break;
                            case 1003:
                                this.mIsMultiScreenMinimized = message.arg1 == 1;
                                if (DBG_SM) {
                                    Log.d(TAG, "EVENT_ID_MULTI_SCREEN_MINIMIZED mIsMultiScreenMinimized=" + this.mIsMultiScreenMinimized);
                                }
                                if (this.mIsMultiScreen) {
                                    enterState(getForgroundAppState());
                                    break;
                                }
                                break;
                            default:
                                switch (i2) {
                                    case 4016:
                                        ForegroundInfo appInfo4 = this.mProcessObserver.getAppInfo();
                                        if (appInfo4 != null && appInfo4.mForegroundPackageName.equals(CAMERA_APP)) {
                                            enterState(getState(108));
                                            break;
                                        }
                                        break;
                                    case 4017:
                                        ForegroundInfo appInfo5 = this.mProcessObserver.getAppInfo();
                                        if (appInfo5 != null && appInfo5.mForegroundPackageName.equals(CAMERA_APP)) {
                                            enterState(getState(109));
                                            break;
                                        }
                                        break;
                                    case 4018:
                                        ForegroundInfo appInfo6 = this.mProcessObserver.getAppInfo();
                                        if (appInfo6 != null && appInfo6.mForegroundPackageName.equals(CAMERA_APP)) {
                                            enterState(getState(201));
                                            break;
                                        }
                                        break;
                                    case 4019:
                                        this.mIsSplitScreen = message.arg1 == 1;
                                        if (DBG_SM) {
                                            Log.d(TAG, "EVENT_ID_SPLITSCREEN isInSplitScreenMode =" + this.mIsSplitScreen);
                                        }
                                        DisplayCabcHandler displayCabcHandler5 = this.mCabcHandler;
                                        if (displayCabcHandler5 != null) {
                                            displayCabcHandler5.onForegroundChanged(this.mProcessObserver.getAppInfo(), this.mIsSplitScreen);
                                        }
                                        PowerScheduleController.notifySplitScreenStatusChange(this.mIsSplitScreen);
                                        break;
                                    default:
                                        if (DBG_SM) {
                                            Log.d(TAG, "should not go here");
                                        }
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
        }
    }

    @Override // com.miui.powerkeeper.utils.ProcessObserver.ForegroundListener
    public void onForegroundChanged() {
        sendEmptyMessage(100);
    }

    public void registerTaskStackListener() {
        try {
            ActivityTaskManager.getService().registerTaskStackListener(this.mTaskStackListener);
        } catch (Exception unused) {
            Log.w(TAG, "Faild to call registerTaskStackListener");
        }
    }
}
