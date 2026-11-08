package com.miui.powerkeeper.statemachine;

import android.annotation.Nullable;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import miui.process.ForegroundInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CpuDdrHandler extends Handler {
    private static final String CLOUD_SYSTEM_PARAMETER_TUNING = "system_parameter_tuning";
    public static final int DELAY_TIME = 10000;
    private static final String TAG = "CpuDdrHandler";
    private static CpuDdrHandler sInstance;
    private final String ACTION_CAMERA_STATE;
    private final boolean DBG_CPU;
    private final String EXTRA_CAMERA_STATE;
    private final String EXTRA_PKG;
    private final String EXTRA_UID;
    private final int FLAG_CLOSE_CAMERA;
    private final int FLAG_PULL_UP_CAMERA;
    private final int MSG_CAMERA_STATE_CHANGED;
    private final int MSG_CONFIG_UPDATE;
    private boolean mCameraDelay;
    private int[] mClass0Args;
    private String[] mClass0PkgName;
    private int[] mClass1Args;
    private String[] mClass1PkgName;
    private Context mContext;
    private ForegroundInfo mForegroundInfo;
    private boolean mIsCameraOn;
    private boolean mIsFeatureOn;
    private LocalLog mLocalLog;
    private boolean mNeedRelease;
    private BroadcastReceiver mReceiver;

    private CpuDdrHandler(Context context, Looper looper) {
        super(looper);
        this.DBG_CPU = Log.isLoggable("power.cpu", 3);
        this.MSG_CONFIG_UPDATE = 2;
        this.MSG_CAMERA_STATE_CHANGED = 3;
        this.FLAG_PULL_UP_CAMERA = 1;
        this.FLAG_CLOSE_CAMERA = 2;
        this.ACTION_CAMERA_STATE = ScenarioConfig.ACTION_CAMERA_STATE;
        this.EXTRA_CAMERA_STATE = "camera_state";
        this.EXTRA_PKG = "package_name";
        this.EXTRA_UID = "application_uid";
        this.mForegroundInfo = null;
        this.mLocalLog = new LocalLog(128);
        this.mIsFeatureOn = false;
        this.mNeedRelease = false;
        this.mIsCameraOn = false;
        this.mCameraDelay = false;
        this.mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.statemachine.CpuDdrHandler.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent != null && ScenarioConfig.ACTION_CAMERA_STATE.equals(intent.getAction())) {
                    int intExtra = intent.getIntExtra("application_uid", -1);
                    int intExtra2 = intent.getIntExtra("camera_state", -1);
                    String stringExtra = intent.getStringExtra("package_name");
                    CpuDdrHandler.this.writeLocalLog("onReceive camera uid=" + intExtra + " state=" + intExtra2 + " pkg=" + stringExtra);
                    CpuDdrHandler.this.sendMessage(CpuDdrHandler.this.obtainMessage(3, intExtra2, intExtra, stringExtra));
                }
            }
        };
        Log.i(TAG, "CpuDdrHandler constructive");
        this.mContext = context;
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            this.mForegroundInfo = eventsAggregator.getForegroundInfo();
            eventsAggregator.registerForForegroundInfo(this);
        }
        this.mContext.getContentResolverForUser(UserHandle.SYSTEM).registerContentObserver(SimpleSettings.Misc.getUriFor(CLOUD_SYSTEM_PARAMETER_TUNING), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.CpuDdrHandler.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                CpuDdrHandler cpuDdrHandler = CpuDdrHandler.this;
                cpuDdrHandler.sendMessage(cpuDdrHandler.obtainMessage(2, uri));
            }
        });
        updateConfig();
        registerReceiver();
    }

    private int[] callArgs(String str) {
        if (!str.startsWith("0x")) {
            Log.d(TAG, "error num:" + str + ",return");
            return null;
        }
        String[] strArrSplit = str.split(",");
        int length = strArrSplit.length;
        if (length % 2 != 0) {
            Log.d(TAG, "error num:" + str + ",return");
            return null;
        }
        int[] iArr = new int[length];
        for (int i2 = 0; i2 < length; i2++) {
            if (i2 % 2 == 0) {
                String str2 = strArrSplit[i2];
                iArr[i2] = Integer.parseInt(str2.substring(2, str2.length()), 16);
            } else {
                iArr[i2] = Integer.parseInt(strArrSplit[i2]);
            }
        }
        return iArr;
    }

    public static synchronized CpuDdrHandler getInstance() {
        return sInstance;
    }

    public static synchronized CpuDdrHandler init(Context context, Looper looper) {
        if (!isSupportDevice()) {
            Log.i(TAG, "CpuDdrHandler init failed");
            return null;
        }
        if (sInstance == null) {
            sInstance = new CpuDdrHandler(context, looper);
        }
        return sInstance;
    }

    private static boolean isSupportDevice() {
        return ArrayUtils.contains(new String[]{"venus", "star", "mars", "haydn", "renoir"}, Build.DEVICE);
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        if (isSupportDevice()) {
            String strOptString = jSONObject.optString(CLOUD_SYSTEM_PARAMETER_TUNING, "");
            if (strOptString.equals(SimpleSettings.Misc.getString(context, CLOUD_SYSTEM_PARAMETER_TUNING, ""))) {
                return;
            }
            SimpleSettings.Misc.putString(context, CLOUD_SYSTEM_PARAMETER_TUNING, strOptString);
        }
    }

    private void registerReceiver() {
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter(ScenarioConfig.ACTION_CAMERA_STATE), 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void systemNocDDRLLCTuning(@Nullable ForegroundInfo foregroundInfo) {
        int[] iArr;
        if (foregroundInfo == null) {
            Log.e(TAG, "error for foregroundInfo is null");
            return;
        }
        if (ArrayUtils.contains(this.mClass1PkgName, foregroundInfo.mForegroundPackageName)) {
            iArr = this.mClass1Args;
        } else {
            iArr = ArrayUtils.contains(this.mClass0PkgName, foregroundInfo.mForegroundPackageName) ? this.mClass0Args : null;
        }
        if (!this.mIsFeatureOn || iArr == null) {
            if (this.mNeedRelease) {
                writeLocalLog("systemNocDDRLLCTuning perfLockRelease");
                com.miui.powerkeeper.perfengine.g.c().f();
                this.mNeedRelease = false;
                return;
            }
            return;
        }
        if (this.mIsCameraOn || this.mCameraDelay) {
            return;
        }
        writeLocalLog("systemNocDDRLLCTuning perfLockAcquire");
        com.miui.powerkeeper.perfengine.g.c().e(com.miui.powerkeeper.perfengine.g.f531k, iArr);
        this.mNeedRelease = true;
    }

    private void updateConfig() {
        String string = SimpleSettings.Misc.getString(this.mContext, CLOUD_SYSTEM_PARAMETER_TUNING, "");
        if (string.isEmpty()) {
            if (this.DBG_CPU) {
                Log.d(TAG, "updateConfig empty cloud");
                return;
            }
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            this.mIsFeatureOn = jSONObject.optBoolean("fucSwitch", false);
            this.mClass0PkgName = jSONObject.optString("class0", "").split(",");
            this.mClass0Args = callArgs(jSONObject.optString("class0Args", ""));
            this.mClass1PkgName = jSONObject.optString("class1", "").split(",");
            this.mClass1Args = callArgs(jSONObject.optString("class1Args", ""));
        } catch (JSONException e2) {
            Log.e(TAG, "updateConfig error", e2);
        }
        if (this.DBG_CPU) {
            Log.i(TAG, "updateConfig newCloud=" + string + " mIsFeatureOn=" + this.mIsFeatureOn + " mClass0PkgName=" + Arrays.toString(this.mClass0PkgName) + " mClass0Args = " + Arrays.toString(this.mClass0Args) + " mClass1PkgName=" + Arrays.toString(this.mClass1PkgName) + " mClass1Args = " + Arrays.toString(this.mClass1Args));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeLocalLog(String str) {
        Log.i(TAG, str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump CpuDdrHandler mIsFeatureOn=" + this.mIsFeatureOn + " mClass0PkgName=" + Arrays.toString(this.mClass0PkgName) + " mClass0Args = " + Arrays.toString(this.mClass0Args) + " mClass1PkgName=" + Arrays.toString(this.mClass1PkgName) + " mClass1Args = " + Arrays.toString(this.mClass1Args));
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -9) {
            this.mForegroundInfo = (ForegroundInfo) message.obj;
            if (this.DBG_CPU) {
                Log.d(TAG, "MSG_FORGROUND_INFO_CHANGE mIsFeatureOn=" + this.mIsFeatureOn + " mClass0PkgName=" + Arrays.toString(this.mClass0PkgName) + " mClass0Args = " + Arrays.toString(this.mClass0Args) + " mClass1PkgName=" + Arrays.toString(this.mClass1PkgName) + " mClass1Args = " + Arrays.toString(this.mClass1Args) + " fg=" + this.mForegroundInfo);
            }
            systemNocDDRLLCTuning(this.mForegroundInfo);
            return;
        }
        if (i2 == 2) {
            if (this.DBG_CPU) {
                Log.d(TAG, "MSG_CONFIG_UPDATE " + message.obj);
            }
            updateConfig();
            return;
        }
        if (i2 != 3) {
            Log.w(TAG, "warning for access here");
            return;
        }
        int i3 = message.arg1;
        String str = (String) message.obj;
        writeLocalLog("MSG_CAMERA_STATE_CHANGED cameraState=" + i3);
        if (i3 == 1) {
            this.mIsCameraOn = true;
            if (this.mNeedRelease) {
                writeLocalLog("mIsCameraOn=true systemNocDDRLLCTuning perfLockRelease");
                com.miui.powerkeeper.perfengine.g.c().f();
                this.mNeedRelease = false;
                return;
            }
            return;
        }
        if (i3 == 2) {
            this.mIsCameraOn = false;
            if ("com.android.camera".equals(str)) {
                this.mCameraDelay = true;
            }
            Runnable runnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.CpuDdrHandler.2
                @Override // java.lang.Runnable
                public void run() {
                    CpuDdrHandler.this.mCameraDelay = false;
                    CpuDdrHandler cpuDdrHandler = CpuDdrHandler.this;
                    cpuDdrHandler.systemNocDDRLLCTuning(cpuDdrHandler.mForegroundInfo);
                }
            };
            removeCallbacks(runnable);
            postDelayed(runnable, 10000L);
        }
    }
}
