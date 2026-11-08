package com.miui.powerkeeper.statemachine;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Base64;
import android.util.LocalLog;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.utils.AssetUtils;
import com.xiaomi.push.mpcd.Constants;
import com.xiaomi.push.service.MIPushNotificationHelper;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import miui.process.ForegroundInfo;
import miui.process.IActivityChangeListener;
import miui.process.ProcessManager;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerScheduleController extends Handler {
    private static final String FILE_POWER_SCHELED = "/data/system/whetstone/power_scheled_data";
    private static final String TAG = "PowerScheduleController";
    private static final String VALUE_OFF = "OFF";
    private static final String VALUE_ON = "ON";
    private static int mCurPowerModeLevel;
    private static int mCurTempMode;
    private static PowerScheduleController sInstance;
    private final String CONFIG_FILE_NAME;
    private final boolean DBG_PSC;
    private final String DEFAULT_SCENE_ID;
    private final String LOCAL_CONFIG_FILE_PATH;
    private final int MSG_ACTIVITY_CHANGE;
    private final int MSG_CHECK_STATUS;
    private final int MSG_CLOUD_UPDATE;
    private final int MSG_INIT;
    private Object boostFramework;
    private Method mAcquireFunc;
    private IActivityChangeListener.Stub mActListener;
    private String mCasting;
    private b.a mCloudObserver;
    private Context mContext;
    private volatile String mCurrentActivity;
    private volatile String mCurrentFgPkg;
    private volatile String mCurrentSceneId;
    private int mCurrentSceneIdReason;
    private EventsAggregator mEventsAggregator;
    private String mFloatState;
    private boolean mIsInitDone;
    private boolean mIsPreloadDone;
    private volatile boolean mIsReleased;
    private volatile boolean mIsScreenOn;
    private String mIsSplitScreen;
    private LocalLog mLocalLog;
    private final Object mLock;
    private PerfConfig mPerfConfig;
    private PowerKeeperManager mPowerKeeperManager;
    private Method mReleaseFunc;
    private Class<?> sPerfClass;

    private class PerfConfig {
        private int configType;
        public ArrayMap<String, String> mNodeConfig;
        public List<String> mSceneActivityList;
        public ArrayMap<String, SceneConfig> mSceneConfigArr;
        public int mScenePowerModeLevelThreshold;
        public int mSceneTempModeThreshold;
        public boolean mScheduleFun;

        private String getValidCmd(String str) {
            String[] strArrSplit;
            if (TextUtils.isEmpty(str) || this.mNodeConfig.size() == 0 || (strArrSplit = str.split("#")) == null || strArrSplit.length != 2) {
                return "";
            }
            String str2 = this.mNodeConfig.get(strArrSplit[0]);
            String str3 = strArrSplit[1];
            if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3)) {
                return "";
            }
            return str2 + "#" + str3;
        }

        private void parseNodeConfig(String str) {
            String[] strArrSplit;
            String[] strArrSplit2;
            try {
                if (PowerScheduleController.this.DBG_PSC) {
                    PowerScheduleController.this.writeLocalLog("parseNodeConfig configStr = " + str);
                }
                if (TextUtils.isEmpty(str) || (strArrSplit = str.split(";")) == null) {
                    return;
                }
                for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                    if (!TextUtils.isEmpty(strArrSplit[i2]) && (strArrSplit2 = strArrSplit[i2].split(":")) != null && strArrSplit2.length == 2) {
                        String str2 = strArrSplit2[0];
                        String str3 = strArrSplit2[1];
                        if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str3)) {
                            this.mNodeConfig.put(str2, str3);
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e(PowerScheduleController.TAG, "updatePowerConfig e= ", e2);
                this.mNodeConfig.clear();
            }
        }

        private void parseSceneConfig(String str) throws Throwable {
            String[] strArrSplit;
            boolean z2 = false;
            try {
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    String strOptString = jSONObject.optString("scene_id", "");
                    if (!TextUtils.isEmpty(strOptString) && (strArrSplit = strOptString.split(";")) != null) {
                        boolean z3 = false;
                        for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                            try {
                                if (!TextUtils.isEmpty(strArrSplit[i2]) && strArrSplit[i2].length() == 5) {
                                    if (!parseSceneIdConfig(jSONObject.optString(strArrSplit[i2], "10000"), strArrSplit[i2])) {
                                        break;
                                    } else if (i2 == strArrSplit.length - 1) {
                                        z3 = true;
                                    }
                                }
                            } catch (Throwable th) {
                                th = th;
                                z2 = z3;
                                if (!z2) {
                                    PowerScheduleController.this.writeLocalLog("parseSceneConfig reset ");
                                    reset();
                                }
                                throw th;
                            }
                        }
                        z2 = z3;
                    }
                    if (z2) {
                        return;
                    }
                } catch (JSONException e2) {
                    Log.e(PowerScheduleController.TAG, "updatePowerConfig e= " + e2);
                } catch (Exception e3) {
                    Log.e(PowerScheduleController.TAG, "updatePowerConfig e= ", e3);
                }
                PowerScheduleController.this.writeLocalLog("parseSceneConfig reset ");
                reset();
            } catch (Throwable th2) {
                th = th2;
            }
        }

        private boolean parseSceneIdConfig(String str, String str2) {
            String[] strArrSplit;
            boolean z2 = false;
            try {
                try {
                    if (!TextUtils.isEmpty(str) && !"10000".equals(str2)) {
                        JSONObject jSONObject = new JSONObject(str);
                        String strOptString = jSONObject.optString(MIPushNotificationHelper.MSG_BUSI_TYPE_SCENE, "");
                        String strOptString2 = jSONObject.optString("scene_activity", "");
                        String strOptString3 = jSONObject.optString("cmd_start", "");
                        String strOptString4 = jSONObject.optString("cmd_recovery", "");
                        String strOptString5 = jSONObject.optString("perf_config", "");
                        PowerScheduleController.this.writeLocalLog("parseSceneIdConfig sceneAppsStr = " + strOptString + ", cmdStartStr = " + strOptString3 + ", cmdRecoveryStr = " + strOptString4);
                        if (!TextUtils.isEmpty(strOptString) || !TextUtils.isEmpty(strOptString2)) {
                            if ((!TextUtils.isEmpty(strOptString3) && !TextUtils.isEmpty(strOptString4)) || !TextUtils.isEmpty(strOptString5)) {
                                String[] strArrSplit2 = null;
                                String[] strArrSplit3 = !TextUtils.isEmpty(strOptString) ? strOptString.split(",") : null;
                                String[] strArrSplit4 = !TextUtils.isEmpty(strOptString2) ? strOptString2.split(",") : null;
                                if (TextUtils.isEmpty(strOptString3) || TextUtils.isEmpty(strOptString4)) {
                                    strArrSplit = null;
                                } else {
                                    strArrSplit2 = strOptString3.split(";");
                                    strArrSplit = strOptString4.split(";");
                                }
                                if ((strArrSplit3 != null || strArrSplit4 != null) && ((strArrSplit2 != null && strArrSplit != null) || !TextUtils.isEmpty(strOptString5))) {
                                    SceneConfig sceneConfig = PowerScheduleController.this.new SceneConfig(str2);
                                    if (strArrSplit3 != null) {
                                        for (int i2 = 0; i2 < strArrSplit3.length; i2++) {
                                            if (!TextUtils.isEmpty(strArrSplit3[i2])) {
                                                sceneConfig.sceneApps.add(strArrSplit3[i2]);
                                            }
                                        }
                                    }
                                    if (strArrSplit4 != null) {
                                        for (int i3 = 0; i3 < strArrSplit4.length; i3++) {
                                            if (!TextUtils.isEmpty(strArrSplit4[i3])) {
                                                sceneConfig.sceneActivities.add(strArrSplit4[i3]);
                                                this.mSceneActivityList.add(strArrSplit4[i3]);
                                            }
                                        }
                                    }
                                    if (strArrSplit2 != null && strArrSplit != null) {
                                        for (String str3 : strArrSplit2) {
                                            String validCmd = getValidCmd(str3);
                                            if (!TextUtils.isEmpty(validCmd)) {
                                                sceneConfig.cmdStart.add(validCmd);
                                            }
                                        }
                                        for (String str4 : strArrSplit) {
                                            String validCmd2 = getValidCmd(str4);
                                            if (!TextUtils.isEmpty(validCmd2)) {
                                                sceneConfig.cmdRecovery.add(validCmd2);
                                            }
                                        }
                                    }
                                    if (!TextUtils.isEmpty(strOptString5)) {
                                        sceneConfig.perfVal = strOptString5;
                                    }
                                    PowerScheduleController.this.writeLocalLog("parseSceneIdConfig sceneConfig = " + sceneConfig.toString());
                                    if ((sceneConfig.sceneApps.size() != 0 || sceneConfig.sceneActivities.size() != 0) && sceneConfig.cmdStart.size() != 0 && sceneConfig.cmdRecovery.size() != 0) {
                                        this.mSceneConfigArr.put(sceneConfig.sceneId, sceneConfig);
                                        z2 = true;
                                    }
                                }
                            }
                        }
                    }
                    if (!z2) {
                        reset();
                    }
                } catch (JSONException e2) {
                    Log.e(PowerScheduleController.TAG, "updatePowerConfig e= " + e2);
                } catch (Exception e3) {
                    Log.e(PowerScheduleController.TAG, "updatePowerConfig e= ", e3);
                }
                return z2;
            } catch (Throwable th) {
                reset();
                throw th;
            }
        }

        public void reset() {
            ArrayMap<String, SceneConfig> arrayMap = this.mSceneConfigArr;
            if (arrayMap != null) {
                Iterator<String> it = arrayMap.keySet().iterator();
                while (it.hasNext()) {
                    SceneConfig sceneConfig = this.mSceneConfigArr.get(it.next());
                    if (sceneConfig != null && sceneConfig.cmdStatus == 1) {
                        sceneConfig.runCmd(0);
                    }
                }
                this.mSceneConfigArr.clear();
            }
            this.mSceneActivityList.clear();
            this.mSceneTempModeThreshold = -1;
            this.mScenePowerModeLevelThreshold = -1;
            this.mScheduleFun = false;
            this.mNodeConfig.clear();
            if (!PowerScheduleController.this.mIsPreloadDone || PowerScheduleController.this.mIsReleased) {
                return;
            }
            PowerScheduleController.this.perfLockRelease();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(" mScheduleFun = " + this.mScheduleFun);
            sb.append("， mConfigType = " + this.configType);
            sb.append("， mSceneTempModeThreshold = " + this.mSceneTempModeThreshold);
            sb.append("， mScenePowerModeLevelThreshold = " + this.mScenePowerModeLevelThreshold);
            sb.append(", mSceneConfigArr: " + this.mSceneConfigArr.toString());
            if (PowerScheduleController.this.DBG_PSC) {
                sb.append(", mNodeConfig: " + this.mNodeConfig.toString());
                sb.append(", mSceneActivityList: " + this.mSceneActivityList.toString());
            }
            return sb.toString();
        }

        /* JADX WARN: Code duplicated, block: B:18:0x0043  */
        public void updateConfig(Context context) {
            String strOptString;
            String strOptString2;
            JSONObject jSONObjectL;
            try {
                try {
                    reset();
                    if (PowerScheduleController.this.mCloudObserver != null) {
                        b.a aVar = PowerScheduleController.this.mCloudObserver;
                        int i2 = b.b.R;
                        if (!aVar.s(i2) || (jSONObjectL = PowerScheduleController.this.mCloudObserver.l(i2)) == null) {
                            strOptString = null;
                            strOptString2 = null;
                        } else {
                            this.configType = 1;
                            strOptString2 = jSONObjectL.optString("schedule_group", null);
                            strOptString = jSONObjectL.optString("node_group", null);
                        }
                    } else {
                        strOptString = null;
                        strOptString2 = null;
                    }
                    if ((strOptString2 == null || strOptString == null) && AssetUtils.doesAssetExist(context, PowerScheduleController.this.LOCAL_CONFIG_FILE_PATH, MiawareBcasHandler.CONFIG_FILE_NAME)) {
                        String strFromAssetFile = AssetUtils.getStrFromAssetFile(context, PowerScheduleController.this.LOCAL_CONFIG_FILE_PATH + "/" + MiawareBcasHandler.CONFIG_FILE_NAME);
                        if (!TextUtils.isEmpty(strFromAssetFile)) {
                            String str = new String(Base64.decode(strFromAssetFile, 0));
                            if (!TextUtils.isEmpty(str)) {
                                if (strOptString2 == null) {
                                    this.configType = 2;
                                    strOptString2 = new JSONObject(str).optString("schedule_group", null);
                                }
                                if (strOptString == null) {
                                    strOptString = new JSONObject(str).optString("node_group", null);
                                }
                            }
                        }
                    }
                    if (strOptString2 == null || strOptString == null) {
                        String strFromAssetFile2 = AssetUtils.getStrFromAssetFile(context, MiawareBcasHandler.CONFIG_FILE_NAME);
                        if (!TextUtils.isEmpty(strFromAssetFile2)) {
                            String str2 = new String(Base64.decode(strFromAssetFile2, 0));
                            if (!TextUtils.isEmpty(str2)) {
                                if (strOptString2 == null) {
                                    this.configType = 3;
                                    strOptString2 = new JSONObject(str2).optString("schedule_group", null);
                                }
                                if (strOptString == null) {
                                    strOptString = new JSONObject(str2).optString("node_group", null);
                                }
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(strOptString)) {
                        parseNodeConfig(strOptString);
                    }
                    if (!TextUtils.isEmpty(strOptString2)) {
                        JSONObject jSONObject = new JSONObject(strOptString2);
                        this.mScheduleFun = jSONObject.optBoolean("schedule_fun", false);
                        this.mSceneTempModeThreshold = jSONObject.optInt("enable_temp_mode_thre", -1);
                        this.mScenePowerModeLevelThreshold = jSONObject.optInt("enable_power_mode_level_thre", -1);
                        if (this.mScheduleFun) {
                            String strOptString3 = jSONObject.optString("scene_config", "");
                            PowerScheduleController.this.writeLocalLog("updateConfig sceneConfigStr = " + strOptString3);
                            if (!TextUtils.isEmpty(strOptString3)) {
                                parseSceneConfig(strOptString3);
                                return;
                            }
                        }
                    }
                } catch (JSONException e2) {
                    Log.e(PowerScheduleController.TAG, "updateConfig e= " + e2);
                } catch (Exception e3) {
                    Log.e(PowerScheduleController.TAG, "updateConfig e= ", e3);
                }
                reset();
            } catch (Throwable th) {
                reset();
                throw th;
            }
        }

        private PerfConfig() {
            this.mScheduleFun = false;
            this.mNodeConfig = new ArrayMap<>();
            this.mSceneConfigArr = new ArrayMap<>();
            this.mSceneActivityList = new ArrayList();
            this.mSceneTempModeThreshold = -1;
            this.mScenePowerModeLevelThreshold = -1;
            this.configType = 0;
        }
    }

    private class SceneConfig {
        public String sceneId;
        public ArraySet<String> sceneApps = new ArraySet<>(5);
        public ArraySet<String> sceneActivities = new ArraySet<>(5);
        public int cmdStatus = 0;
        public List<String> cmdStart = new ArrayList(10);
        public List<String> cmdRecovery = new ArrayList(10);
        public String perfVal = "";

        public SceneConfig(String str) {
            this.sceneId = "10000";
            this.sceneId = str;
        }

        private boolean runRootCommand(String str, List<String> list) {
            String str2;
            String str3;
            boolean z2 = false;
            if (list == null || list.size() == 0) {
                Log.e(PowerScheduleController.TAG, "runRootCommand error for cmds not valid");
                return false;
            }
            synchronized (PowerScheduleController.this.mLock) {
                FileOutputStream fileOutputStream = null;
                try {
                    try {
                        File file = new File(str);
                        for (int i2 = 10; file.exists() && i2 > 0; i2--) {
                            Thread.sleep(100L);
                            Log.w(PowerScheduleController.TAG, "runRootCommand wait for file delete");
                        }
                        if (file.exists()) {
                            PowerScheduleController.this.writeLocalLog("del file " + str);
                            if (!file.delete()) {
                                Log.e(PowerScheduleController.TAG, "del file " + str + " error");
                            }
                        }
                        if (file.createNewFile()) {
                            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                            try {
                                if (list.size() != 0) {
                                    Iterator<String> it = list.iterator();
                                    while (it.hasNext()) {
                                        fileOutputStream2.write((it.next() + Constants.ENDLINE).getBytes());
                                    }
                                }
                                SystemProperties.set("mcd.extra.params", "sudebug sched " + str);
                                SystemProperties.set("ctl.start", "mcd_init");
                                z2 = true;
                                fileOutputStream = fileOutputStream2;
                            } catch (InterruptedException e2) {
                                fileOutputStream = fileOutputStream2;
                                e = e2;
                                Log.e(PowerScheduleController.TAG, "runRootCommand InterruptedException error  " + e);
                                if (fileOutputStream != null) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (IOException e3) {
                                        str2 = PowerScheduleController.TAG;
                                        str3 = "runRootCommand IOException error  " + e3;
                                        Log.e(str2, str3);
                                    }
                                }
                            } catch (Exception e4) {
                                fileOutputStream = fileOutputStream2;
                                e = e4;
                                Log.e(PowerScheduleController.TAG, "runRootCommand error  " + e);
                                if (fileOutputStream != null) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (IOException e5) {
                                        str2 = PowerScheduleController.TAG;
                                        str3 = "runRootCommand IOException error  " + e5;
                                        Log.e(str2, str3);
                                    }
                                }
                            } catch (Throwable th) {
                                fileOutputStream = fileOutputStream2;
                                th = th;
                                if (fileOutputStream != null) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (IOException e6) {
                                        Log.e(PowerScheduleController.TAG, "runRootCommand IOException error  " + e6);
                                    }
                                }
                                throw th;
                            }
                        }
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e7) {
                                str2 = PowerScheduleController.TAG;
                                str3 = "runRootCommand IOException error  " + e7;
                                Log.e(str2, str3);
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (InterruptedException e8) {
                    e = e8;
                } catch (Exception e9) {
                    e = e9;
                }
            }
            return z2;
        }

        public void runCmd(int i2) {
            try {
                if (this.cmdStatus == i2) {
                    PowerScheduleController.this.writeLocalLog("ignore runCmd for the same status = " + i2 + ", sceneId = " + this.sceneId);
                } else {
                    PowerScheduleController.this.writeLocalLog("runCmd for status " + i2 + ", sceneId = " + this.sceneId);
                    if (i2 == 0) {
                        runRootCommand(PowerScheduleController.FILE_POWER_SCHELED, this.cmdRecovery);
                        this.cmdStatus = 0;
                    } else if (i2 == 1) {
                        runRootCommand(PowerScheduleController.FILE_POWER_SCHELED, this.cmdStart);
                        this.cmdStatus = 1;
                    }
                }
            } catch (Exception e2) {
                Log.e(PowerScheduleController.TAG, "runCmd error  " + e2);
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(" sceneId: " + this.sceneId);
            sb.append(",cmdStatus: " + this.cmdStatus);
            sb.append(",sceneApps: " + this.sceneApps.toString());
            sb.append(",sceneActivities: " + this.sceneActivities.toString());
            sb.append(",cmdStart: " + this.cmdStart.toString());
            sb.append(",cmdRecovery: " + this.cmdRecovery.toString());
            sb.append(",perfVal: " + this.perfVal);
            return sb.toString();
        }
    }

    public PowerScheduleController(Context context, Looper looper) {
        super(looper);
        this.DBG_PSC = Log.isLoggable("power.psctrl", 3) || Build.IS_DEBUGGABLE;
        this.mLocalLog = new LocalLog(64);
        this.mLock = new Object();
        this.MSG_INIT = 1;
        this.MSG_ACTIVITY_CHANGE = 2;
        this.MSG_CLOUD_UPDATE = 3;
        this.MSG_CHECK_STATUS = 4;
        this.LOCAL_CONFIG_FILE_PATH = "overlay/" + Build.DEVICE;
        this.CONFIG_FILE_NAME = MiawareBcasHandler.CONFIG_FILE_NAME;
        this.DEFAULT_SCENE_ID = "10000";
        this.mCurrentFgPkg = "";
        this.mCurrentActivity = "";
        this.mCurrentSceneId = "10000";
        this.mIsScreenOn = false;
        this.sPerfClass = null;
        this.boostFramework = null;
        this.mAcquireFunc = null;
        this.mReleaseFunc = null;
        this.mIsPreloadDone = false;
        this.mIsReleased = true;
        this.mCurrentSceneIdReason = -1;
        this.mEventsAggregator = null;
        this.mPerfConfig = new PerfConfig();
        this.mIsInitDone = false;
        this.mCasting = VALUE_OFF;
        this.mFloatState = VALUE_OFF;
        this.mIsSplitScreen = VALUE_OFF;
        this.mActListener = new IActivityChangeListener.Stub() { // from class: com.miui.powerkeeper.statemachine.PowerScheduleController.1
            public void onActivityChanged(ComponentName componentName, ComponentName componentName2) {
                String className = componentName2.getClassName();
                String packageName = componentName2.getPackageName();
                if (className == null || packageName == null || className.equals(PowerScheduleController.this.mCurrentActivity)) {
                    return;
                }
                PowerScheduleController.this.mCurrentActivity = className;
                PowerScheduleController.this.sendEmptyMessage(2);
            }
        };
        writeLocalLog("PowerScheduleController init");
        this.mContext = context;
        this.mCloudObserver = b.a.p();
        sendEmptyMessage(1);
    }

    /* JADX WARN: Code duplicated, block: B:106:0x020b A[Catch: Exception -> 0x001c, TryCatch #0 {Exception -> 0x001c, blocks: (B:3:0x0005, B:7:0x000f, B:9:0x0015, B:65:0x0154, B:67:0x015c, B:69:0x0160, B:71:0x0166, B:74:0x018c, B:76:0x0194, B:78:0x01a2, B:80:0x01a6, B:82:0x01ae, B:83:0x01b1, B:85:0x01b5, B:87:0x01b9, B:88:0x01bc, B:117:0x0234, B:89:0x01c6, B:91:0x01ce, B:93:0x01dc, B:95:0x01e0, B:97:0x01e8, B:98:0x01eb, B:100:0x01ef, B:102:0x01f3, B:103:0x01f6, B:104:0x01ff, B:106:0x020b, B:108:0x0213, B:110:0x0217, B:111:0x021c, B:113:0x0220, B:115:0x0228, B:116:0x022b, B:12:0x001f, B:14:0x0025, B:16:0x002c, B:17:0x0033, B:19:0x003b, B:21:0x0042, B:22:0x0049, B:24:0x004d, B:26:0x0054, B:27:0x005b, B:29:0x0063, B:31:0x006a, B:32:0x0082, B:34:0x0088, B:36:0x008f, B:37:0x00a7, B:39:0x00af, B:41:0x00b7, B:44:0x00c0, B:45:0x00cc, B:47:0x00d2, B:49:0x00e4, B:51:0x00ee, B:53:0x00f4, B:55:0x00fa, B:56:0x00fe, B:58:0x0104, B:60:0x0116, B:62:0x0120, B:63:0x0126), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:134:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:67:0x015c A[Catch: Exception -> 0x001c, TryCatch #0 {Exception -> 0x001c, blocks: (B:3:0x0005, B:7:0x000f, B:9:0x0015, B:65:0x0154, B:67:0x015c, B:69:0x0160, B:71:0x0166, B:74:0x018c, B:76:0x0194, B:78:0x01a2, B:80:0x01a6, B:82:0x01ae, B:83:0x01b1, B:85:0x01b5, B:87:0x01b9, B:88:0x01bc, B:117:0x0234, B:89:0x01c6, B:91:0x01ce, B:93:0x01dc, B:95:0x01e0, B:97:0x01e8, B:98:0x01eb, B:100:0x01ef, B:102:0x01f3, B:103:0x01f6, B:104:0x01ff, B:106:0x020b, B:108:0x0213, B:110:0x0217, B:111:0x021c, B:113:0x0220, B:115:0x0228, B:116:0x022b, B:12:0x001f, B:14:0x0025, B:16:0x002c, B:17:0x0033, B:19:0x003b, B:21:0x0042, B:22:0x0049, B:24:0x004d, B:26:0x0054, B:27:0x005b, B:29:0x0063, B:31:0x006a, B:32:0x0082, B:34:0x0088, B:36:0x008f, B:37:0x00a7, B:39:0x00af, B:41:0x00b7, B:44:0x00c0, B:45:0x00cc, B:47:0x00d2, B:49:0x00e4, B:51:0x00ee, B:53:0x00f4, B:55:0x00fa, B:56:0x00fe, B:58:0x0104, B:60:0x0116, B:62:0x0120, B:63:0x0126), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:69:0x0160 A[Catch: Exception -> 0x001c, TryCatch #0 {Exception -> 0x001c, blocks: (B:3:0x0005, B:7:0x000f, B:9:0x0015, B:65:0x0154, B:67:0x015c, B:69:0x0160, B:71:0x0166, B:74:0x018c, B:76:0x0194, B:78:0x01a2, B:80:0x01a6, B:82:0x01ae, B:83:0x01b1, B:85:0x01b5, B:87:0x01b9, B:88:0x01bc, B:117:0x0234, B:89:0x01c6, B:91:0x01ce, B:93:0x01dc, B:95:0x01e0, B:97:0x01e8, B:98:0x01eb, B:100:0x01ef, B:102:0x01f3, B:103:0x01f6, B:104:0x01ff, B:106:0x020b, B:108:0x0213, B:110:0x0217, B:111:0x021c, B:113:0x0220, B:115:0x0228, B:116:0x022b, B:12:0x001f, B:14:0x0025, B:16:0x002c, B:17:0x0033, B:19:0x003b, B:21:0x0042, B:22:0x0049, B:24:0x004d, B:26:0x0054, B:27:0x005b, B:29:0x0063, B:31:0x006a, B:32:0x0082, B:34:0x0088, B:36:0x008f, B:37:0x00a7, B:39:0x00af, B:41:0x00b7, B:44:0x00c0, B:45:0x00cc, B:47:0x00d2, B:49:0x00e4, B:51:0x00ee, B:53:0x00f4, B:55:0x00fa, B:56:0x00fe, B:58:0x0104, B:60:0x0116, B:62:0x0120, B:63:0x0126), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:71:0x0166 A[Catch: Exception -> 0x001c, TRY_LEAVE, TryCatch #0 {Exception -> 0x001c, blocks: (B:3:0x0005, B:7:0x000f, B:9:0x0015, B:65:0x0154, B:67:0x015c, B:69:0x0160, B:71:0x0166, B:74:0x018c, B:76:0x0194, B:78:0x01a2, B:80:0x01a6, B:82:0x01ae, B:83:0x01b1, B:85:0x01b5, B:87:0x01b9, B:88:0x01bc, B:117:0x0234, B:89:0x01c6, B:91:0x01ce, B:93:0x01dc, B:95:0x01e0, B:97:0x01e8, B:98:0x01eb, B:100:0x01ef, B:102:0x01f3, B:103:0x01f6, B:104:0x01ff, B:106:0x020b, B:108:0x0213, B:110:0x0217, B:111:0x021c, B:113:0x0220, B:115:0x0228, B:116:0x022b, B:12:0x001f, B:14:0x0025, B:16:0x002c, B:17:0x0033, B:19:0x003b, B:21:0x0042, B:22:0x0049, B:24:0x004d, B:26:0x0054, B:27:0x005b, B:29:0x0063, B:31:0x006a, B:32:0x0082, B:34:0x0088, B:36:0x008f, B:37:0x00a7, B:39:0x00af, B:41:0x00b7, B:44:0x00c0, B:45:0x00cc, B:47:0x00d2, B:49:0x00e4, B:51:0x00ee, B:53:0x00f4, B:55:0x00fa, B:56:0x00fe, B:58:0x0104, B:60:0x0116, B:62:0x0120, B:63:0x0126), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:74:0x018c A[Catch: Exception -> 0x001c, TRY_ENTER, TryCatch #0 {Exception -> 0x001c, blocks: (B:3:0x0005, B:7:0x000f, B:9:0x0015, B:65:0x0154, B:67:0x015c, B:69:0x0160, B:71:0x0166, B:74:0x018c, B:76:0x0194, B:78:0x01a2, B:80:0x01a6, B:82:0x01ae, B:83:0x01b1, B:85:0x01b5, B:87:0x01b9, B:88:0x01bc, B:117:0x0234, B:89:0x01c6, B:91:0x01ce, B:93:0x01dc, B:95:0x01e0, B:97:0x01e8, B:98:0x01eb, B:100:0x01ef, B:102:0x01f3, B:103:0x01f6, B:104:0x01ff, B:106:0x020b, B:108:0x0213, B:110:0x0217, B:111:0x021c, B:113:0x0220, B:115:0x0228, B:116:0x022b, B:12:0x001f, B:14:0x0025, B:16:0x002c, B:17:0x0033, B:19:0x003b, B:21:0x0042, B:22:0x0049, B:24:0x004d, B:26:0x0054, B:27:0x005b, B:29:0x0063, B:31:0x006a, B:32:0x0082, B:34:0x0088, B:36:0x008f, B:37:0x00a7, B:39:0x00af, B:41:0x00b7, B:44:0x00c0, B:45:0x00cc, B:47:0x00d2, B:49:0x00e4, B:51:0x00ee, B:53:0x00f4, B:55:0x00fa, B:56:0x00fe, B:58:0x0104, B:60:0x0116, B:62:0x0120, B:63:0x0126), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:89:0x01c6 A[Catch: Exception -> 0x001c, TryCatch #0 {Exception -> 0x001c, blocks: (B:3:0x0005, B:7:0x000f, B:9:0x0015, B:65:0x0154, B:67:0x015c, B:69:0x0160, B:71:0x0166, B:74:0x018c, B:76:0x0194, B:78:0x01a2, B:80:0x01a6, B:82:0x01ae, B:83:0x01b1, B:85:0x01b5, B:87:0x01b9, B:88:0x01bc, B:117:0x0234, B:89:0x01c6, B:91:0x01ce, B:93:0x01dc, B:95:0x01e0, B:97:0x01e8, B:98:0x01eb, B:100:0x01ef, B:102:0x01f3, B:103:0x01f6, B:104:0x01ff, B:106:0x020b, B:108:0x0213, B:110:0x0217, B:111:0x021c, B:113:0x0220, B:115:0x0228, B:116:0x022b, B:12:0x001f, B:14:0x0025, B:16:0x002c, B:17:0x0033, B:19:0x003b, B:21:0x0042, B:22:0x0049, B:24:0x004d, B:26:0x0054, B:27:0x005b, B:29:0x0063, B:31:0x006a, B:32:0x0082, B:34:0x0088, B:36:0x008f, B:37:0x00a7, B:39:0x00af, B:41:0x00b7, B:44:0x00c0, B:45:0x00cc, B:47:0x00d2, B:49:0x00e4, B:51:0x00ee, B:53:0x00f4, B:55:0x00fa, B:56:0x00fe, B:58:0x0104, B:60:0x0116, B:62:0x0120, B:63:0x0126), top: B:121:0x0005 }] */
    /* JADX WARN: Instruction removed from duplicated block: B:71:0x0166, please report this as an issue */
    private void checkPerfStatus() {
        String str;
        SceneConfig sceneConfig;
        SceneConfig sceneConfig2;
        SceneConfig sceneConfig3;
        try {
            this.mCurrentSceneIdReason = 0;
            if (this.mIsInitDone) {
                PerfConfig perfConfig = this.mPerfConfig;
                if (!perfConfig.mScheduleFun) {
                    this.mCurrentSceneIdReason = -2;
                    if (this.DBG_PSC) {
                        Log.d(TAG, "reset sceneId for disable");
                    }
                } else if (perfConfig.mSceneConfigArr.size() == 0) {
                    this.mCurrentSceneIdReason = -3;
                    if (this.DBG_PSC) {
                        Log.d(TAG, "reset sceneId for scene config not support");
                    }
                } else if (this.mIsScreenOn) {
                    int i2 = mCurPowerModeLevel;
                    PerfConfig perfConfig2 = this.mPerfConfig;
                    if (i2 < perfConfig2.mScenePowerModeLevelThreshold) {
                        this.mCurrentSceneIdReason = -5;
                        if (this.DBG_PSC) {
                            Log.d(TAG, "reset sceneId for power mode level disabled, mCurPowerModeLevel = " + mCurPowerModeLevel);
                        }
                    } else {
                        if (mCurTempMode >= perfConfig2.mSceneTempModeThreshold) {
                            if (!VALUE_ON.equals(this.mIsSplitScreen) && !VALUE_ON.equals(this.mCasting) && !VALUE_ON.equals(this.mFloatState)) {
                                Set<String> setKeySet = this.mPerfConfig.mSceneConfigArr.keySet();
                                Iterator<String> it = setKeySet.iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        str = "10000";
                                        break;
                                    }
                                    SceneConfig sceneConfig4 = this.mPerfConfig.mSceneConfigArr.get(it.next());
                                    if (sceneConfig4 != null && sceneConfig4.sceneActivities.contains(this.mCurrentActivity)) {
                                        this.mCurrentSceneIdReason = 1;
                                        str = sceneConfig4.sceneId;
                                        break;
                                    }
                                }
                                if ("10000".equals(str)) {
                                    Iterator<String> it2 = setKeySet.iterator();
                                    while (it2.hasNext()) {
                                        SceneConfig sceneConfig5 = this.mPerfConfig.mSceneConfigArr.get(it2.next());
                                        if (sceneConfig5 != null && sceneConfig5.sceneApps.contains(this.mCurrentFgPkg)) {
                                            this.mCurrentSceneIdReason = 2;
                                            str = sceneConfig5.sceneId;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                this.mCurrentSceneIdReason = -7;
                                writeLocalLog("reset sceneId for composite scene, mIsSplitScreen = " + this.mIsSplitScreen + ", mCasting = " + this.mCasting + ", mFloatState = " + this.mFloatState);
                            }
                            if (this.mCurrentSceneId.equals(str)) {
                                if (this.DBG_PSC) {
                                    Log.d(TAG, "ignore update cmd for same scene id");
                                    return;
                                }
                                return;
                            }
                            writeLocalLog("newSceneId = " + str + ", mCurrentSceneId = " + this.mCurrentSceneId);
                            if ("10000".equals(str)) {
                                if (!"10000".equals(this.mCurrentSceneId) && (sceneConfig2 = this.mPerfConfig.mSceneConfigArr.get(this.mCurrentSceneId)) != null) {
                                    if (sceneConfig2.cmdStatus != 0 && sceneConfig2.cmdRecovery.size() > 0) {
                                        sceneConfig2.runCmd(0);
                                    }
                                    if (this.mIsPreloadDone && !this.mIsReleased) {
                                        perfLockRelease();
                                    }
                                    Settings.System.putString(this.mContext.getContentResolver(), "schedule_scene", "10000");
                                }
                                sceneConfig = this.mPerfConfig.mSceneConfigArr.get(str);
                                if (sceneConfig != null) {
                                    if (!TextUtils.isEmpty(sceneConfig.perfVal) && this.mIsReleased) {
                                        perfLockAcquire(sceneConfig.perfVal);
                                    }
                                    if (sceneConfig.cmdStatus != 1 && sceneConfig.cmdStart.size() > 0) {
                                        sceneConfig.runCmd(1);
                                    }
                                    Settings.System.putString(this.mContext.getContentResolver(), "schedule_scene", str);
                                }
                            } else if (!"10000".equals(this.mCurrentSceneId) && (sceneConfig3 = this.mPerfConfig.mSceneConfigArr.get(this.mCurrentSceneId)) != null) {
                                if (sceneConfig3.cmdStatus != 0 && sceneConfig3.cmdRecovery.size() > 0) {
                                    sceneConfig3.runCmd(0);
                                }
                                if (this.mIsPreloadDone && !this.mIsReleased) {
                                    perfLockRelease();
                                }
                                Settings.System.putString(this.mContext.getContentResolver(), "schedule_scene", "10000");
                            }
                            this.mCurrentSceneId = str;
                        }
                        this.mCurrentSceneIdReason = -6;
                        if (this.DBG_PSC) {
                            Log.d(TAG, "reset sceneId for temp mode disabled, mCurTempMode = " + mCurTempMode);
                        }
                    }
                } else {
                    this.mCurrentSceneIdReason = -4;
                    if (this.DBG_PSC) {
                        Log.d(TAG, "reset sceneId for screen off");
                    }
                }
            } else {
                this.mCurrentSceneIdReason = -1;
                if (this.DBG_PSC) {
                    Log.d(TAG, "reset sceneId for init not complete");
                }
            }
            str = "10000";
            if (this.mCurrentSceneId.equals(str)) {
                if (this.DBG_PSC) {
                    Log.d(TAG, "ignore update cmd for same scene id");
                    return;
                }
                return;
            }
            writeLocalLog("newSceneId = " + str + ", mCurrentSceneId = " + this.mCurrentSceneId);
            if ("10000".equals(str)) {
                if (!"10000".equals(this.mCurrentSceneId)) {
                    if (sceneConfig2.cmdStatus != 0) {
                        sceneConfig2.runCmd(0);
                    }
                    if (this.mIsPreloadDone) {
                        perfLockRelease();
                    }
                    Settings.System.putString(this.mContext.getContentResolver(), "schedule_scene", "10000");
                }
                sceneConfig = this.mPerfConfig.mSceneConfigArr.get(str);
                if (sceneConfig != null) {
                    if (!TextUtils.isEmpty(sceneConfig.perfVal)) {
                        perfLockAcquire(sceneConfig.perfVal);
                    }
                    if (sceneConfig.cmdStatus != 1) {
                        sceneConfig.runCmd(1);
                    }
                    Settings.System.putString(this.mContext.getContentResolver(), "schedule_scene", str);
                }
            } else if (!"10000".equals(this.mCurrentSceneId)) {
                if (sceneConfig3.cmdStatus != 0) {
                    sceneConfig3.runCmd(0);
                }
                if (this.mIsPreloadDone) {
                    perfLockRelease();
                }
                Settings.System.putString(this.mContext.getContentResolver(), "schedule_scene", "10000");
            }
            this.mCurrentSceneId = str;
        } catch (Exception e2) {
            Log.e(TAG, "checkPerfStatus error " + e2);
        }
    }

    public static synchronized PowerScheduleController init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new PowerScheduleController(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public static void notifyCloudUpdate() {
        if (sInstance != null) {
            Log.d(TAG, "notifyCloudUpdate");
            PowerScheduleController powerScheduleController = sInstance;
            Objects.requireNonNull(powerScheduleController);
            powerScheduleController.sendMessage(powerScheduleController.obtainMessage(3));
        }
    }

    public static void notifySplitScreenStatusChange(boolean z2) {
        PowerScheduleController powerScheduleController = sInstance;
        if (powerScheduleController == null || !powerScheduleController.mIsInitDone) {
            return;
        }
        String str = z2 ? VALUE_ON : VALUE_OFF;
        if (str.equals(powerScheduleController.mIsSplitScreen)) {
            return;
        }
        Log.d(TAG, "notifySplitScreenStatusChange isSplit = " + str);
        PowerScheduleController powerScheduleController2 = sInstance;
        powerScheduleController2.mIsSplitScreen = str;
        Objects.requireNonNull(powerScheduleController2);
        if (powerScheduleController2.hasMessages(4)) {
            PowerScheduleController powerScheduleController3 = sInstance;
            Objects.requireNonNull(powerScheduleController3);
            powerScheduleController3.removeMessages(4);
        }
        PowerScheduleController powerScheduleController4 = sInstance;
        Objects.requireNonNull(powerScheduleController4);
        powerScheduleController4.sendMessageDelayed(powerScheduleController4.obtainMessage(4), 1200L);
    }

    private void notifyStatusUpdate() {
        if (hasMessages(4)) {
            removeMessages(4);
        }
        sendMessageDelayed(obtainMessage(4), 1200L);
    }

    public static void notifyUpdatePowerModelLevel(int i2) {
        Log.d(TAG, "notifyUpdatePowerModelLevel  powerModeLevel = " + i2);
        mCurPowerModeLevel = i2;
        PowerScheduleController powerScheduleController = sInstance;
        if (powerScheduleController == null || !powerScheduleController.mIsInitDone || powerScheduleController.mPerfConfig.mScenePowerModeLevelThreshold == -1) {
            return;
        }
        Objects.requireNonNull(powerScheduleController);
        if (powerScheduleController.hasMessages(4)) {
            PowerScheduleController powerScheduleController2 = sInstance;
            Objects.requireNonNull(powerScheduleController2);
            powerScheduleController2.removeMessages(4);
        }
        PowerScheduleController powerScheduleController3 = sInstance;
        Objects.requireNonNull(powerScheduleController3);
        powerScheduleController3.sendMessageDelayed(powerScheduleController3.obtainMessage(4), 1200L);
    }

    public static void notifyUpdateTempMode(int i2) {
        Log.d(TAG, "notifyUpdateTempMode + tempMode = " + i2);
        mCurTempMode = i2;
        PowerScheduleController powerScheduleController = sInstance;
        if (powerScheduleController == null || !powerScheduleController.mIsInitDone || powerScheduleController.mPerfConfig.mSceneTempModeThreshold == -1) {
            return;
        }
        Objects.requireNonNull(powerScheduleController);
        if (powerScheduleController.hasMessages(4)) {
            PowerScheduleController powerScheduleController2 = sInstance;
            Objects.requireNonNull(powerScheduleController2);
            powerScheduleController2.removeMessages(4);
        }
        PowerScheduleController powerScheduleController3 = sInstance;
        Objects.requireNonNull(powerScheduleController3);
        powerScheduleController3.sendEmptyMessage(4);
    }

    private void onSmartScenarioChange(long j2) {
        long j3 = ScenarioConfig.INTERACTIVE_ACTION_TYPE_CASTING & j2;
        String str = VALUE_ON;
        String str2 = j3 != 0 ? VALUE_ON : VALUE_OFF;
        if ((4 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WECHAT & j2) == 0 && (32 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_DINGTALK & j2) == 0 && (128 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WHATSAPP & j2) == 0 && (64 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WEWORK & j2) == 0 && (256 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_LARK & j2) == 0 && (8 & j2) == 0 && (j2 & ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_QQ) == 0) {
            str = VALUE_OFF;
        }
        if (str2.equals(this.mCasting) && str.equals(this.mFloatState)) {
            return;
        }
        this.mCasting = str2;
        this.mFloatState = str;
        writeLocalLog("mFloatState = " + this.mFloatState + " mCasting = " + this.mCasting);
        PowerScheduleController powerScheduleController = sInstance;
        Objects.requireNonNull(powerScheduleController);
        if (powerScheduleController.hasMessages(4)) {
            PowerScheduleController powerScheduleController2 = sInstance;
            Objects.requireNonNull(powerScheduleController2);
            powerScheduleController2.removeMessages(4);
        }
        PowerScheduleController powerScheduleController3 = sInstance;
        Objects.requireNonNull(powerScheduleController3);
        powerScheduleController3.sendMessageDelayed(powerScheduleController3.obtainMessage(4), 1200L);
    }

    private synchronized void perfLockAcquire(String str) {
        if (TextUtils.isEmpty(str)) {
            Log.i(TAG, "perfLockAcquire ignore for mCurPerfCmd is not valid");
            return;
        }
        if (!this.mIsPreloadDone) {
            Log.i(TAG, "perfLockAcquire preloadPerfLock is not completed");
            return;
        }
        if (!this.mIsReleased) {
            Log.i(TAG, "perfLockAcquire ignore for not release");
            return;
        }
        try {
            String[] strArrSplit = str.split(",");
            if (strArrSplit != null && strArrSplit.length % 2 == 0) {
                int[] iArr = new int[strArrSplit.length];
                for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                    iArr[i2] = Integer.parseInt(strArrSplit[i2], 16);
                }
                if (this.mAcquireFunc != null) {
                    writeLocalLog("perfLockAcquire invoke run cmd = " + str);
                    this.mAcquireFunc.invoke(this.boostFramework, Integer.valueOf(com.miui.powerkeeper.perfengine.g.f531k), iArr);
                    this.mIsReleased = false;
                }
                return;
            }
            Log.e(TAG, "perfLockAcquire error for cmd is not valid, cmd = " + str);
        } catch (IllegalAccessException e2) {
            Log.e(TAG, "perfLockAcquire illegal access e:" + e2.getMessage());
        } catch (Exception e3) {
            Log.e(TAG, "perfLockAcquire e:" + e3.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void perfLockRelease() {
        try {
            if (!this.mIsPreloadDone) {
                writeLocalLog("perfLockRelease preloadPerfLock is not completed");
                return;
            }
            if (this.mIsReleased) {
                writeLocalLog("perfLockRelease ignore for is released");
                return;
            }
            try {
                if (this.mReleaseFunc != null) {
                    writeLocalLog("perfLockRelease invoke run");
                    this.mReleaseFunc.invoke(this.boostFramework, null);
                    this.mIsReleased = true;
                }
            } catch (IllegalAccessException e2) {
                Log.e(TAG, "perfLockRelease illegal access e:" + e2.getMessage());
            } catch (Exception e3) {
                Log.e(TAG, "perfLockRelease e:" + e3.getMessage());
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private void preloadPerfLock() {
        try {
            Class<?> cls = Class.forName("android.util.BoostFramework");
            this.sPerfClass = cls;
            Class[] clsArr = new Class[0];
            Constructor<?> constructor = cls.getConstructor(null);
            if (constructor != null) {
                this.boostFramework = constructor.newInstance(null);
            }
            this.mAcquireFunc = this.sPerfClass.getMethod("perfLockAcquire", Integer.TYPE, int[].class);
            Class[] clsArr2 = new Class[0];
            this.mReleaseFunc = this.sPerfClass.getMethod("perfLockRelease", null);
            this.mIsPreloadDone = true;
        } catch (Exception unused) {
            Log.e(TAG, "preload class android.util.BoostFramework failed");
        }
    }

    private void registerActivityChangeListener() {
        if (this.mActListener == null || this.mPerfConfig.mSceneActivityList.size() <= 0) {
            return;
        }
        writeLocalLog("registerActivityChangeListener");
        ProcessManager.unregisterActivityChanageListener(this.mActListener);
        ProcessManager.registerActivityChangeListener((List) null, this.mPerfConfig.mSceneActivityList, this.mActListener);
    }

    private void scheduleInit() {
        this.mPerfConfig.updateConfig(this.mContext);
        PowerKeeperManager powerKeeperManager = PowerKeeperManager.getInstance();
        this.mPowerKeeperManager = powerKeeperManager;
        this.mIsScreenOn = powerKeeperManager.isScreenOn();
        EventsAggregator eventsAggregator = this.mPowerKeeperManager.getEventsAggregator();
        this.mEventsAggregator = eventsAggregator;
        if (eventsAggregator != null) {
            ForegroundInfo foregroundInfo = eventsAggregator.getForegroundInfo();
            this.mCurrentFgPkg = foregroundInfo != null ? foregroundInfo.mForegroundPackageName : "";
            this.mEventsAggregator.registerForForegroundInfo(this);
            this.mEventsAggregator.registerForUserPresent(this, null);
            this.mEventsAggregator.registerForScreenOff(this, null);
            this.mEventsAggregator.registerSmartPowerScenarioIdChanged(this);
        }
        registerActivityChangeListener();
        String string = Settings.System.getString(this.mContext.getContentResolver(), "schedule_scene");
        if (TextUtils.isEmpty(string)) {
            string = "10000";
        }
        this.mCurrentSceneId = string;
        preloadPerfLock();
        this.mIsInitDone = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeLocalLog(String str) {
        if (this.DBG_PSC) {
            Log.i(TAG, str);
        }
        this.mLocalLog.log(str);
    }

    public void destroy() {
        Log.w(TAG, "destroy");
        IActivityChangeListener.Stub stub = this.mActListener;
        if (stub != null) {
            ProcessManager.unregisterActivityChanageListener(stub);
        }
        EventsAggregator eventsAggregator = this.mEventsAggregator;
        if (eventsAggregator != null) {
            eventsAggregator.unregisterForForegroundInfo(this);
            this.mEventsAggregator.unregisterForUserPresent(this);
            this.mEventsAggregator.unregisterForScreenOff(this);
            this.mEventsAggregator.unregisterSmartPowerScenarioIdChanged(this);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("psconfig mCurTempMode = " + mCurTempMode);
        printWriter.println("psconfig mCurrentFgPkg = " + this.mCurrentFgPkg);
        printWriter.println("psconfig mCurrentActivity = " + this.mCurrentActivity);
        printWriter.println("psconfig mCurrentSceneId = " + this.mCurrentSceneId);
        printWriter.println("psconfig mCurrentSceneIdReason = " + this.mCurrentSceneIdReason);
        printWriter.println("psconfig mIsScreenOn = " + this.mIsScreenOn);
        printWriter.println("psconfig mPerfConfig: " + this.mPerfConfig.toString());
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        String str;
        int i2 = message.what;
        if (i2 == -34) {
            onSmartScenarioChange(((Long) message.obj).longValue());
            return;
        }
        if (i2 == -9) {
            if (this.DBG_PSC) {
                Log.d(TAG, "MSG_FOREGROUND_INFO_CHANGE");
            }
            ForegroundInfo foregroundInfo = (ForegroundInfo) message.obj;
            if (foregroundInfo == null || (str = foregroundInfo.mForegroundPackageName) == null) {
                this.mCurrentFgPkg = "";
                Log.e(TAG, "appInfo is error");
            } else {
                this.mCurrentFgPkg = str;
            }
            notifyStatusUpdate();
            return;
        }
        if (i2 == -7) {
            writeLocalLog("MSG_USER_PRESENT");
            this.mIsScreenOn = true;
            notifyStatusUpdate();
            return;
        }
        if (i2 == -5) {
            writeLocalLog("MSG_SCREEN_OFF");
            this.mIsScreenOn = false;
            notifyStatusUpdate();
            return;
        }
        if (i2 == 1) {
            writeLocalLog("MSG_INIT");
            scheduleInit();
            return;
        }
        if (i2 == 2) {
            if (this.DBG_PSC) {
                Log.d(TAG, "MSG_ACTIVITY_CHANGEF");
            }
            notifyStatusUpdate();
            return;
        }
        if (i2 != 3) {
            if (i2 != 4) {
                return;
            }
            if (this.DBG_PSC) {
                Log.d(TAG, "MSG_CHECK_STATUS");
            }
            checkPerfStatus();
            return;
        }
        writeLocalLog("MSG_CLOUD_UPDATE");
        PerfConfig perfConfig = this.mPerfConfig;
        if (perfConfig != null) {
            perfConfig.updateConfig(this.mContext);
            registerActivityChangeListener();
            if (this.mIsInitDone) {
                checkPerfStatus();
            }
        }
    }
}
