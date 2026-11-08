package com.miui.powerkeeper.statemachine;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LocalLog;
import android.util.Log;
import com.miui.powerkeeper.utils.AssetUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ExtremeScenarioAwareHandler extends Handler {
    private static final String TAG = "ExtremeScenarioAware";
    private static ExtremeScenarioAwareHandler sInstance;
    private final String ACTION_POWER_MODE_LEVEL;
    private final String CONFIG_FILE_NAME;
    private final int COOKIE_STATUS_EXTREME_COLD_MODE;
    private final int COOKIE_STATUS_EXTREME_ENDURANCE_MODE;
    private final int COOKIE_STATUS_NORMAL;
    private final int COOKIE_STATUS_NORMAL_DISABLED;
    private final int COOKIE_STATUS_SAVE_MODE;
    private final int COOKIE_STATUS_SUPER_SAVE_MODE;
    private final boolean DBG_CTR;
    private final String EXTREME_ENDURANCE_MODE;
    private final String LOCAL_CONFIG_FILE_PATH;
    private final int MSG_CHECK_STATUS;
    private final int MSG_CLOUD_UPDATE;
    private final int MSG_INIT;
    private final String PERMISSION_POWER_MODE_LEVEL;
    private final int POWER_LEVEL_NORMAL;
    private final int POWER_LEVEL_POWER_SAVE;
    private final int POWER_LEVEL_SUPER_POWER_SAVE;
    private final String POWER_SAVE_MODE;
    private final String SUPER_POWER_SAVE_MODE;
    private b.a mCloudObserver;
    private Context mContext;
    private ExtremeScenarioConfig mESConfig;
    private int mExtremeColdStatus;
    private int mExtremeEnduranceModeStatus;
    private LocalLog mLocalLog;
    private volatile int mPowerLevel;
    private volatile int mPowerLevelReason;
    private int mPowerSaveModeStatus;
    private int mSuperPowerSaveModeStatus;

    private class ExtremeScenarioConfig {
        int configType;
        List<Boolean> modeDistingFunList;
        boolean notifyStatusFun;

        private void reset() {
            this.configType = 0;
            this.notifyStatusFun = false;
            this.modeDistingFunList.clear();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("configType = " + this.configType);
            sb.append(", notifyStatusFun = " + this.notifyStatusFun);
            sb.append(", modeDistingFunList = " + this.modeDistingFunList.toString());
            return sb.toString();
        }

        /* JADX WARN: Code duplicated, block: B:16:0x003a  */
        public void updateConfig(Context context) {
            String strOptString;
            String[] strArrSplit;
            JSONObject jSONObjectL;
            try {
                reset();
                if (ExtremeScenarioAwareHandler.this.mCloudObserver != null) {
                    b.a aVar = ExtremeScenarioAwareHandler.this.mCloudObserver;
                    int i2 = b.b.R;
                    if (!aVar.s(i2) || (jSONObjectL = ExtremeScenarioAwareHandler.this.mCloudObserver.l(i2)) == null) {
                        strOptString = null;
                    } else {
                        this.configType = 1;
                        strOptString = jSONObjectL.optString("extreme_aware_group", null);
                    }
                } else {
                    strOptString = null;
                }
                if (strOptString == null && AssetUtils.doesAssetExist(context, ExtremeScenarioAwareHandler.this.LOCAL_CONFIG_FILE_PATH, MiawareBcasHandler.CONFIG_FILE_NAME)) {
                    String strFromAssetFile = AssetUtils.getStrFromAssetFile(context, ExtremeScenarioAwareHandler.this.LOCAL_CONFIG_FILE_PATH + "/" + MiawareBcasHandler.CONFIG_FILE_NAME);
                    if (!TextUtils.isEmpty(strFromAssetFile)) {
                        String str = new String(Base64.decode(strFromAssetFile, 0));
                        if (!TextUtils.isEmpty(str)) {
                            this.configType = 2;
                            strOptString = new JSONObject(str).optString("extreme_aware_group", null);
                        }
                    }
                }
                if (strOptString == null) {
                    String strFromAssetFile2 = AssetUtils.getStrFromAssetFile(context, MiawareBcasHandler.CONFIG_FILE_NAME);
                    if (!TextUtils.isEmpty(strFromAssetFile2)) {
                        String str2 = new String(Base64.decode(strFromAssetFile2, 0));
                        if (!TextUtils.isEmpty(str2)) {
                            this.configType = 3;
                            strOptString = new JSONObject(str2).optString("extreme_aware_group", null);
                        }
                    }
                }
                if (strOptString == null || strOptString.isEmpty()) {
                    return;
                }
                JSONObject jSONObject = new JSONObject(strOptString);
                this.notifyStatusFun = jSONObject.optBoolean("notify_fun", false);
                String strOptString2 = jSONObject.optString("enable_status", "");
                if (TextUtils.isEmpty(strOptString2) || (strArrSplit = strOptString2.split(":")) == null) {
                    return;
                }
                for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                    String str3 = strArrSplit[i3];
                    this.modeDistingFunList.add(Boolean.valueOf(str3 != null && str3.equals("1")));
                }
            } catch (JSONException e2) {
                reset();
                Log.e(ExtremeScenarioAwareHandler.TAG, "updateConfig e= " + e2);
            } catch (Exception e3) {
                reset();
                Log.e(ExtremeScenarioAwareHandler.TAG, "updateConfig e= ", e3);
            }
        }

        private ExtremeScenarioConfig() {
            this.configType = 0;
            this.notifyStatusFun = false;
            this.modeDistingFunList = new ArrayList(6);
        }
    }

    public ExtremeScenarioAwareHandler(Context context, Looper looper) {
        super(looper);
        this.DBG_CTR = Log.isLoggable("power.etsa", 3) || Build.IS_DEBUGGABLE;
        this.mLocalLog = new LocalLog(64);
        this.ACTION_POWER_MODE_LEVEL = "hyper.os.POWER_MODE_LEVEL";
        this.PERMISSION_POWER_MODE_LEVEL = "hyper.permission.POWER_MODE_LEVEL";
        this.POWER_SAVE_MODE = "POWER_SAVE_MODE_OPEN";
        this.SUPER_POWER_SAVE_MODE = "power_supersave_mode_open";
        this.EXTREME_ENDURANCE_MODE = "pc_security_center_extreme_mode_enter";
        this.LOCAL_CONFIG_FILE_PATH = "overlay/" + Build.DEVICE;
        this.CONFIG_FILE_NAME = MiawareBcasHandler.CONFIG_FILE_NAME;
        this.POWER_LEVEL_NORMAL = 0;
        this.POWER_LEVEL_POWER_SAVE = 1;
        this.POWER_LEVEL_SUPER_POWER_SAVE = 2;
        this.COOKIE_STATUS_NORMAL_DISABLED = -2;
        this.COOKIE_STATUS_NORMAL = -1;
        this.COOKIE_STATUS_SAVE_MODE = 0;
        this.COOKIE_STATUS_SUPER_SAVE_MODE = 1;
        this.COOKIE_STATUS_EXTREME_ENDURANCE_MODE = 2;
        this.COOKIE_STATUS_EXTREME_COLD_MODE = 3;
        this.MSG_INIT = 1;
        this.MSG_CHECK_STATUS = 2;
        this.MSG_CLOUD_UPDATE = 3;
        this.mPowerSaveModeStatus = 0;
        this.mSuperPowerSaveModeStatus = 0;
        this.mExtremeEnduranceModeStatus = 0;
        this.mExtremeColdStatus = 0;
        this.mPowerLevel = -1;
        this.mPowerLevelReason = -1;
        this.mESConfig = new ExtremeScenarioConfig();
        writeLocalLog("ExtremeScenarioAwareHandler init");
        this.mContext = context;
        this.mCloudObserver = b.a.p();
        sendEmptyMessage(1);
    }

    private void checkStatus(boolean z2) {
        try {
            this.mPowerLevelReason = -1;
            int i2 = 0;
            if (this.mESConfig.notifyStatusFun) {
                if (this.mSuperPowerSaveModeStatus != 0 && isDistingSupport(1)) {
                    this.mPowerLevelReason = 1;
                } else if (this.mExtremeEnduranceModeStatus == 0 || !isDistingSupport(2)) {
                    if (this.mPowerSaveModeStatus != 0 && isDistingSupport(0)) {
                        this.mPowerLevelReason = 0;
                    } else if (this.mExtremeColdStatus != 0 && isDistingSupport(3)) {
                        this.mPowerLevelReason = 3;
                    }
                    i2 = 1;
                } else {
                    this.mPowerLevelReason = 2;
                }
                i2 = 2;
            } else {
                this.mPowerLevelReason = -2;
            }
            if (!z2 && i2 == this.mPowerLevel) {
                if (this.DBG_CTR) {
                    Log.d(TAG, "ignore notifyPowerLevel for the same level, powerLevel =" + i2);
                    return;
                }
                return;
            }
            notifyPowerLevelChange(i2);
            PowerScheduleController.notifyUpdatePowerModelLevel(i2);
        } catch (Exception e2) {
            Log.e(TAG, "checkStatus e " + e2);
        }
    }

    private void ecaInit() {
        this.mESConfig.updateConfig(this.mContext);
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.SYSTEM);
        contentResolverForUser.registerContentObserver(Settings.System.getUriFor("POWER_SAVE_MODE_OPEN"), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.ExtremeScenarioAwareHandler.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                ExtremeScenarioAwareHandler.this.updateSaveModeStatus(false);
            }
        });
        contentResolverForUser.registerContentObserver(Settings.System.getUriFor("power_supersave_mode_open"), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.ExtremeScenarioAwareHandler.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                ExtremeScenarioAwareHandler.this.updateSaveModeStatus(false);
            }
        });
        contentResolverForUser.registerContentObserver(Settings.Secure.getUriFor("pc_security_center_extreme_mode_enter"), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.ExtremeScenarioAwareHandler.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                ExtremeScenarioAwareHandler.this.updateSaveModeStatus(false);
            }
        });
        updateSaveModeStatus(true);
    }

    public static ExtremeScenarioAwareHandler getInstance() {
        return sInstance;
    }

    public static synchronized ExtremeScenarioAwareHandler init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new ExtremeScenarioAwareHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private boolean isDistingSupport(int i2) {
        if (i2 < this.mESConfig.modeDistingFunList.size()) {
            return this.mESConfig.modeDistingFunList.get(i2).booleanValue();
        }
        return true;
    }

    public static void notifyCloudUpdate() {
        if (sInstance != null) {
            Log.d(TAG, "notifyCloudUpdate");
            ExtremeScenarioAwareHandler extremeScenarioAwareHandler = sInstance;
            Objects.requireNonNull(extremeScenarioAwareHandler);
            extremeScenarioAwareHandler.sendMessage(extremeScenarioAwareHandler.obtainMessage(3));
        }
    }

    private void notifyPowerLevelChange(int i2) {
        try {
            writeLocalLog("notifyPowerLevelChange: " + i2 + ", reason: " + this.mPowerLevelReason);
            Intent intent = new Intent("hyper.os.POWER_MODE_LEVEL");
            intent.putExtra("power_level", i2);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "hyper.permission.POWER_MODE_LEVEL");
            Settings.System.putInt(this.mContext.getContentResolver(), "power_mode_level", i2);
            this.mPowerLevel = i2;
        } catch (Exception e2) {
            Log.e(TAG, "notifyPowerLevelChange e " + e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSaveModeStatus(boolean z2) {
        int i2 = Settings.System.getInt(this.mContext.getContentResolver(), "POWER_SAVE_MODE_OPEN", 0) != 0 ? 1 : 0;
        int i3 = Settings.System.getInt(this.mContext.getContentResolver(), "power_supersave_mode_open", 0) != 0 ? 1 : 0;
        int i4 = Settings.Secure.getInt(this.mContext.getContentResolver(), "pc_security_center_extreme_mode_enter", 0) == 1 ? 1 : 0;
        if (!z2 && i2 == this.mPowerSaveModeStatus && i3 == this.mSuperPowerSaveModeStatus && i4 == this.mExtremeEnduranceModeStatus) {
            return;
        }
        this.mPowerSaveModeStatus = i2;
        this.mSuperPowerSaveModeStatus = i3;
        this.mExtremeEnduranceModeStatus = i4;
        if (z2) {
            writeLocalLog("power level init");
            checkStatus(true);
        } else {
            if (hasMessages(2)) {
                removeMessages(2);
            }
            sendMessage(obtainMessage(2));
        }
    }

    private void writeLocalLog(String str) {
        if (this.DBG_CTR) {
            Log.i(TAG, str);
        }
        this.mLocalLog.log(str);
    }

    public void destroy() {
        Log.w(TAG, "destroy");
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("esaconfig mPowerLevel = " + this.mPowerLevel);
        printWriter.println("esaconfig mPowerLevelReason = " + this.mPowerLevelReason);
        printWriter.println("esaconfig mPowerSaveModeStatus = " + this.mPowerSaveModeStatus);
        printWriter.println("esaconfig mSuperPowerSaveModeStatus = " + this.mSuperPowerSaveModeStatus);
        printWriter.println("esaconfig mExtremeEnduranceModeStatus = " + this.mExtremeEnduranceModeStatus);
        printWriter.println("esaconfig mExtremeColdStatus = " + this.mExtremeColdStatus);
        printWriter.println("esaconfig mESConfig: " + this.mESConfig.toString());
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == 1) {
            writeLocalLog("MSG_INIT");
            ecaInit();
            return;
        }
        if (i2 == 2) {
            if (this.DBG_CTR) {
                Log.d(TAG, "MSG_CHECK_STATUS");
            }
            checkStatus(false);
        } else {
            if (i2 != 3) {
                return;
            }
            writeLocalLog("MSG_CLOUD_UPDATE");
            ExtremeScenarioConfig extremeScenarioConfig = this.mESConfig;
            if (extremeScenarioConfig != null) {
                extremeScenarioConfig.updateConfig(this.mContext);
                updateSaveModeStatus(false);
            }
        }
    }
}
