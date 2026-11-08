package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.LocalLog;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.utils.AssetUtils;
import com.miui.powerkeeper.utils.FileUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import miui.util.FeatureParser;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MiawareBcasHandler extends Handler {
    public static final String BCAS_TEMP_MODE_CHANGE = "com.miui.powerkeeper.TEMP_MODE_CHANGE";
    public static final String CONFIG_FILE_NAME = "local.config";
    private static final String DEFAULT_VAL = "9";
    public static final String INTL_LOCAL_CONFIG_FILE_PATH;
    public static final String LOCAL_CONFIG_FILE_PATH;
    public static final String TAG = "MiawareBcasHandler";
    private static MiawareBcasHandler sInstance;
    private final boolean DBG_CONTROL;
    private final String LOW_TEMPERATURE_EXTREME_ENDUREANCE_SUPPORT;
    private final String LOW_TEMPERATURE_EXTREME_ENDUREANCE_TEMP_MODE_VALUE;
    private final int MSG_BASE;
    private final int MSG_CHECK_STATS;
    private final int MSG_INIT;
    private final int MSG_UPDATE_CLOUD;
    private final String PC_LOW_TEMPERATURE_EXTREME_ENDUREANCE_SWITCH;
    private final String TEMP_AWARE_PATH_MTK;
    private final String TEMP_AWARE_PATH_QCOM;
    private PowerKeeperInterface.d mBatteryChangedCallback;
    private PowerKeeperInterface.m mBatteryStatusReceiver;
    private b.a mCloudObserver;
    private int mConfigType;
    private Context mContext;
    private BatteryStatus mCurBatteryStatus;
    private BcasConfig mCurBcasConfig;
    private String mCurSendAwareVal;
    private String mCurTempAwareVal;
    private String[] mDefConfigDeviceArr;
    private boolean mIsInitDone;
    private boolean mIsMTKDevice;
    private LocalLog mLocalLog;
    private final Object mLock;
    private int mLowTempEnduranceSupport;
    private int mLowTempEnduranceSwitch;
    private int mLowTempEnduranceTempModeValue;
    private PowerKeeperManager mPower;
    private int mSendTempMode;
    private String mTempAwarePath;
    private String[] mThermalAwareFileDeviceArr;

    private class BatteryStatus {
        private String mAwareNodeVal;
        private int mBatteryLevel;
        private boolean mBatteryPlugged;
        private int mBatteryTemp;
        private int mLastBatteryTemp;
        private int mTempMode;

        public BatteryStatus() {
            this.mBatteryLevel = MiawareBcasHandler.this.mPower.getBatteryCurLevel();
            int batteryCurTemp = MiawareBcasHandler.this.mPower.getBatteryCurTemp();
            this.mBatteryTemp = batteryCurTemp;
            this.mLastBatteryTemp = batteryCurTemp;
            this.mBatteryPlugged = MiawareBcasHandler.this.mPower.isCharging();
            this.mTempMode = 0;
            this.mAwareNodeVal = "9";
        }

        private int isInGrayscaleIntreval(int i2, BcasConfig bcasConfig) {
            for (int size = bcasConfig.mUpTempModeThreshold.size() - 1; size >= 0; size--) {
                if (i2 <= bcasConfig.mUpTempModeThreshold.get(size).intValue() && i2 >= bcasConfig.mDownTempModeThreshold.get(size).intValue()) {
                    return size;
                }
            }
            return -1;
        }

        private boolean updateTempMode(BcasConfig bcasConfig) {
            if (!bcasConfig.mBcasEnabled || !bcasConfig.mIsValid) {
                Log.e(MiawareBcasHandler.TAG, "updateTempMode error for config is not valid");
                this.mTempMode = 0;
                return false;
            }
            if (bcasConfig.mTempModeFixed) {
                this.mTempMode = 0;
                MiawareBcasHandler.this.writeLocalLog("temp mode is 0 for not distinguish temp mode");
                return true;
            }
            boolean z2 = MiawareBcasHandler.this.DBG_CONTROL ? SystemProperties.getBoolean("debug.power.bcas.ui", false) : false;
            if (MiawareBcasHandler.this.mLowTempEnduranceSwitch == 0 && !z2) {
                this.mTempMode = 0;
                MiawareBcasHandler.this.writeLocalLog("temp mode is 0 for user disabled, mLowTempEnduranceSwitch = " + MiawareBcasHandler.this.mLowTempEnduranceSwitch + ", debugUserSwitch = " + z2);
                return true;
            }
            int i2 = this.mBatteryTemp;
            if (i2 != this.mLastBatteryTemp) {
                int iIsInGrayscaleIntreval = isInGrayscaleIntreval(i2, bcasConfig);
                int iIsInGrayscaleIntreval2 = isInGrayscaleIntreval(this.mLastBatteryTemp, bcasConfig);
                if (iIsInGrayscaleIntreval != -1 && iIsInGrayscaleIntreval2 == iIsInGrayscaleIntreval) {
                    MiawareBcasHandler.this.writeLocalLog("ignore temp mode change for temp in the same grayscale interval, mBatteryTemp = " + this.mBatteryTemp + ", mTempMode = " + this.mTempMode);
                    return true;
                }
                if (iIsInGrayscaleIntreval2 != iIsInGrayscaleIntreval) {
                    MiawareBcasHandler.this.writeLocalLog("grayscale interval change, curTempGrayState = " + iIsInGrayscaleIntreval + ", lastTempGrayState = " + iIsInGrayscaleIntreval2);
                }
            }
            List<Integer> list = (this.mBatteryTemp <= this.mLastBatteryTemp || bcasConfig.mUpTempModeThreshold.size() != bcasConfig.mTempModeThreshold.size()) ? (this.mBatteryTemp >= this.mLastBatteryTemp || bcasConfig.mDownTempModeThreshold.size() != bcasConfig.mTempModeThreshold.size()) ? bcasConfig.mTempModeThreshold : bcasConfig.mDownTempModeThreshold : bcasConfig.mUpTempModeThreshold;
            if (list != null) {
                for (int i3 = 0; i3 < list.size(); i3++) {
                    if (this.mBatteryTemp >= list.get(i3).intValue()) {
                        this.mTempMode = i3;
                        return true;
                    }
                }
                this.mTempMode = list.size();
            }
            return true;
        }

        public boolean getBatteryPlugged() {
            return this.mBatteryPlugged;
        }

        public int getTempMode() {
            return this.mTempMode;
        }

        public void reset() {
            updateBatteryStatus();
            this.mTempMode = 0;
            this.mAwareNodeVal = "9";
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("  mBatteryLevel = " + this.mBatteryLevel);
            sb.append(", mBatteryTemp = " + this.mBatteryTemp);
            sb.append(", mLastBatteryTemp = " + this.mLastBatteryTemp);
            sb.append(", mBatteryPlugged = " + this.mBatteryPlugged);
            sb.append(", mTempMode = " + this.mTempMode);
            sb.append(", mAwareNodeVal = " + this.mAwareNodeVal);
            return sb.toString();
        }

        public boolean updateAwareNodeVal(BcasConfig bcasConfig) {
            try {
                if (bcasConfig.mBcasEnabled && bcasConfig.mIsValid && updateTempMode(bcasConfig)) {
                    int[] iArr = bcasConfig.mBatteryLevelModeThreshold.get(Integer.valueOf(this.mTempMode));
                    if (iArr != null && iArr.length + 1 == bcasConfig.mAwareNodeVal.size()) {
                        for (int i2 = 0; i2 < iArr.length; i2++) {
                            if (this.mBatteryLevel > iArr[i2]) {
                                this.mAwareNodeVal = bcasConfig.mAwareNodeVal.get(i2);
                                return true;
                            }
                        }
                        List<String> list = bcasConfig.mAwareNodeVal;
                        this.mAwareNodeVal = list.get(list.size() - 1);
                        return true;
                    }
                    Log.e(MiawareBcasHandler.TAG, "updateAwareNodeVal error for not valid config");
                    reset();
                    return false;
                }
                Log.e(MiawareBcasHandler.TAG, "updateAwareNodeVal error for config is not valid");
                reset();
                return false;
            } catch (Exception e2) {
                Log.e(MiawareBcasHandler.TAG, "updateAwareNodeVal e= ", e2);
                reset();
                return false;
            }
        }

        public void updateBatteryStatus() {
            if (MiawareBcasHandler.this.mPower != null) {
                this.mBatteryLevel = MiawareBcasHandler.this.mPower.getBatteryCurLevel();
                int batteryCurTemp = MiawareBcasHandler.this.mPower.getBatteryCurTemp();
                this.mBatteryTemp = batteryCurTemp;
                this.mLastBatteryTemp = batteryCurTemp;
                this.mBatteryPlugged = MiawareBcasHandler.this.mPower.isCharging();
            }
        }
    }

    private class BcasConfig {
        int mTempAwareEnabledTempModeThreshold = 0;
        List<String> mAwareNodeVal = new ArrayList();
        List<Integer> mTempModeThreshold = new ArrayList();
        List<Integer> mUpTempModeThreshold = new ArrayList();
        List<Integer> mDownTempModeThreshold = new ArrayList();
        ArrayMap<Integer, int[]> mBatteryLevelModeThreshold = new ArrayMap<>();
        boolean mBcasEnabled = false;
        int mTempModeNum = -1;
        int mAwareModeNum = -1;
        boolean mTempModeFixed = false;
        boolean mIsValid = false;
        int mPowerSaveTempModeValue = 3;

        public BcasConfig() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void reset() {
            this.mTempModeThreshold.clear();
            this.mUpTempModeThreshold.clear();
            this.mDownTempModeThreshold.clear();
            this.mAwareNodeVal.clear();
            this.mBatteryLevelModeThreshold.clear();
            this.mBcasEnabled = false;
            this.mTempModeNum = -1;
            this.mAwareModeNum = -1;
            this.mTempModeFixed = false;
            this.mIsValid = false;
            this.mPowerSaveTempModeValue = 3;
            this.mTempAwareEnabledTempModeThreshold = 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\nbcasconfig    mAwareNodeVal = " + this.mAwareNodeVal.toString() + ", \n");
            sb.append("bcasconfig    mTempModeThreshold = " + this.mTempModeThreshold.toString() + ", \n");
            sb.append("bcasconfig    mUpTempModeThreshold = " + this.mUpTempModeThreshold.toString() + ", \n");
            sb.append("bcasconfig    mDownTempModeThreshold = " + this.mDownTempModeThreshold.toString() + ", \n");
            sb.append("bcasconfig    mBatteryLevelModeThreshold = " + this.mBatteryLevelModeThreshold.toString() + ", \n");
            sb.append("bcasconfig    mBcasEnabled = " + this.mBcasEnabled + ", \n");
            sb.append("bcasconfig    mTempModeNum = " + this.mTempModeNum + ", \n");
            sb.append("bcasconfig    mAwareModeNum = " + this.mAwareModeNum + ", \n");
            sb.append("bcasconfig    mTempModeFixed = " + this.mTempModeFixed + ", \n");
            sb.append("bcasconfig    mPowerSaveTempModeValue = " + this.mPowerSaveTempModeValue + ", \n");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("bcasconfig    mTempAwareEnabledTempModeThreshold = ");
            sb2.append(this.mTempAwareEnabledTempModeThreshold);
            sb.append(sb2.toString());
            return sb.toString();
        }

        /* JADX WARN: Code duplicated, block: B:15:0x0041  */
        public boolean updateConfig(Context context) {
            String strOptString;
            JSONObject jSONObjectL;
            try {
                reset();
                if (MiawareBcasHandler.this.mCloudObserver != null) {
                    b.a aVar = MiawareBcasHandler.this.mCloudObserver;
                    int i2 = b.b.f35q;
                    if (!aVar.s(i2) || (jSONObjectL = MiawareBcasHandler.this.mCloudObserver.l(i2)) == null) {
                        strOptString = null;
                    } else {
                        MiawareBcasHandler.this.mConfigType = 0;
                        strOptString = jSONObjectL.optString("bcas_group", null);
                    }
                } else {
                    strOptString = null;
                }
                if (MiawareBcasHandler.this.mDefConfigDeviceArr != null && ArrayUtils.contains(MiawareBcasHandler.this.mDefConfigDeviceArr, Build.DEVICE)) {
                    MiawareBcasHandler.this.writeLocalLog("skip device unique config, just use common default config for debug");
                } else if (strOptString == null) {
                    if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
                        String str = MiawareBcasHandler.INTL_LOCAL_CONFIG_FILE_PATH;
                        if (AssetUtils.doesAssetExist(context, str, MiawareBcasHandler.CONFIG_FILE_NAME)) {
                            String strFromAssetFile = AssetUtils.getStrFromAssetFile(context, str + "/" + MiawareBcasHandler.CONFIG_FILE_NAME);
                            if (!TextUtils.isEmpty(strFromAssetFile)) {
                                String str2 = new String(Base64.decode(strFromAssetFile, 0));
                                if (!TextUtils.isEmpty(str2)) {
                                    MiawareBcasHandler.this.mConfigType = 1;
                                    strOptString = new JSONObject(str2).optString("bcas_group", null);
                                }
                            }
                        }
                    }
                    if (strOptString == null) {
                        String str3 = MiawareBcasHandler.LOCAL_CONFIG_FILE_PATH;
                        if (AssetUtils.doesAssetExist(context, str3, MiawareBcasHandler.CONFIG_FILE_NAME)) {
                            String strFromAssetFile2 = AssetUtils.getStrFromAssetFile(context, str3 + "/" + MiawareBcasHandler.CONFIG_FILE_NAME);
                            if (!TextUtils.isEmpty(strFromAssetFile2)) {
                                String str4 = new String(Base64.decode(strFromAssetFile2, 0));
                                if (!TextUtils.isEmpty(str4)) {
                                    MiawareBcasHandler.this.mConfigType = 2;
                                    strOptString = new JSONObject(str4).optString("bcas_group", null);
                                }
                            }
                        }
                    }
                }
                if (strOptString == null) {
                    String strFromAssetFile3 = AssetUtils.getStrFromAssetFile(context, MiawareBcasHandler.CONFIG_FILE_NAME);
                    if (!TextUtils.isEmpty(strFromAssetFile3)) {
                        String str5 = new String(Base64.decode(strFromAssetFile3, 0));
                        if (!TextUtils.isEmpty(str5)) {
                            MiawareBcasHandler.this.mConfigType = 3;
                            strOptString = new JSONObject(str5).optString("bcas_group", null);
                        }
                    }
                }
                if (strOptString == null || strOptString.isEmpty()) {
                    Log.e(MiawareBcasHandler.TAG, "reset configType, lastConfigType = " + MiawareBcasHandler.this.mConfigType);
                    MiawareBcasHandler.this.mConfigType = -1;
                } else {
                    JSONObject jSONObject = new JSONObject(strOptString);
                    boolean zOptBoolean = jSONObject.optBoolean("bcasSwitch", false);
                    this.mBcasEnabled = zOptBoolean;
                    if (zOptBoolean) {
                        String strOptString2 = jSONObject.optString("mode_temp", "");
                        String strOptString3 = jSONObject.optString("mode_temp_gap", "");
                        String strOptString4 = jSONObject.optString("mode_battery_level", "");
                        String strOptString5 = jSONObject.optString("temp_aware_val", "");
                        this.mPowerSaveTempModeValue = jSONObject.optInt("power_save_temp_mode_value", 4);
                        this.mTempAwareEnabledTempModeThreshold = jSONObject.optInt("temp_aware_enabled_temp_mode", 0);
                        if (!TextUtils.isEmpty(strOptString2) && !TextUtils.isEmpty(strOptString4) && !TextUtils.isEmpty(strOptString5) && !TextUtils.isEmpty(strOptString3)) {
                            String[] strArrSplit = strOptString2.split(":");
                            if (strArrSplit.length == 1 && Integer.valueOf(strArrSplit[0]).intValue() == 1000) {
                                this.mTempModeFixed = true;
                                this.mTempModeThreshold.clear();
                                this.mUpTempModeThreshold.clear();
                                this.mDownTempModeThreshold.clear();
                            } else {
                                for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                                    this.mTempModeThreshold.add(Integer.valueOf(strArrSplit[i3]));
                                    if (i3 > 0 && this.mTempModeThreshold.get(i3).intValue() > this.mTempModeThreshold.get(i3 - 1).intValue()) {
                                        Log.d(MiawareBcasHandler.TAG, "updateConfig error for modeTemp is not valid");
                                        this.mTempModeThreshold.clear();
                                        return false;
                                    }
                                }
                            }
                            String[] strArrSplit2 = strOptString3.split(":");
                            if (!this.mTempModeFixed && strArrSplit2.length == 2) {
                                String[] strArrSplit3 = strArrSplit2[0].split(",");
                                String[] strArrSplit4 = strArrSplit2[1].split(",");
                                if (strArrSplit3.length == strArrSplit4.length && strArrSplit3.length == this.mTempModeThreshold.size()) {
                                    for (int i4 = 0; i4 < this.mTempModeThreshold.size(); i4++) {
                                        this.mUpTempModeThreshold.add(Integer.valueOf(this.mTempModeThreshold.get(i4).intValue() + Integer.valueOf(strArrSplit3[i4]).intValue()));
                                        this.mDownTempModeThreshold.add(Integer.valueOf(this.mTempModeThreshold.get(i4).intValue() + Integer.valueOf(strArrSplit4[i4]).intValue()));
                                    }
                                    for (int i5 = 0; i5 < this.mUpTempModeThreshold.size(); i5++) {
                                        if (this.mUpTempModeThreshold.get(i5).intValue() < this.mTempModeThreshold.get(i5).intValue() || this.mTempModeThreshold.get(i5).intValue() < this.mDownTempModeThreshold.get(i5).intValue() || ((i5 >= this.mUpTempModeThreshold.size() - 1 || this.mDownTempModeThreshold.get(i5).intValue() <= this.mUpTempModeThreshold.get(i5 + 1).intValue()) && i5 != this.mUpTempModeThreshold.size() - 1)) {
                                            Log.d(MiawareBcasHandler.TAG, "updateConfig error for modeTempGap is not valid");
                                            this.mUpTempModeThreshold.clear();
                                            this.mDownTempModeThreshold.clear();
                                            return false;
                                        }
                                    }
                                }
                                Log.d(MiawareBcasHandler.TAG, "updateConfig error for modeTempGapStr is not valid, modeTempGapStr = " + strOptString3);
                                return false;
                            }
                            String[] strArrSplit5 = strOptString5.split(":");
                            for (String str6 : strArrSplit5) {
                                this.mAwareNodeVal.add(str6);
                            }
                            this.mTempModeNum = this.mTempModeThreshold.size() + 1;
                            this.mAwareModeNum = this.mAwareNodeVal.size();
                            Log.d(MiawareBcasHandler.TAG, "updateConfig modeBatteryLevelStr = " + strOptString4);
                            String[] strArrSplit6 = strOptString4.split(":");
                            if (strArrSplit6.length != this.mTempModeNum) {
                                Log.d(MiawareBcasHandler.TAG, "updateConfig error mTempModeNum is not valid");
                                return false;
                            }
                            for (int i6 = 0; i6 < strArrSplit6.length; i6++) {
                                String[] strArrSplit7 = strArrSplit6[i6].split(",");
                                if (strArrSplit7.length + 1 != this.mAwareModeNum) {
                                    Log.d(MiawareBcasHandler.TAG, "updateConfig error for mAwareModeNum is not valid," + i6 + " mAwareModeNum = " + this.mAwareModeNum + ", batteryLevelArrayNum = " + strArrSplit7.length);
                                    return false;
                                }
                                int length = strArrSplit7.length;
                                int[] iArr = new int[length];
                                for (int i7 = 0; i7 < strArrSplit7.length; i7++) {
                                    if (i7 > 0 && Integer.valueOf(strArrSplit7[i7]).intValue() > iArr[i7 - 1]) {
                                        Log.d(MiawareBcasHandler.TAG, "updateConfig error for mode battery level is not valid," + i7 + " last = " + iArr[length - 1] + ", cur = " + strArrSplit7[i7]);
                                        return false;
                                    }
                                    iArr[i7] = Integer.valueOf(strArrSplit7[i7]).intValue();
                                }
                                this.mBatteryLevelModeThreshold.put(Integer.valueOf(i6), iArr);
                            }
                            this.mIsValid = true;
                            return true;
                        }
                    }
                }
                return false;
            } catch (JSONException e2) {
                Log.e(MiawareBcasHandler.TAG, "updateConfig e= " + e2);
                return false;
            } catch (Exception e3) {
                Log.e(MiawareBcasHandler.TAG, "updateConfig e= ", e3);
                return false;
            }
        }
    }

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("overlay/");
        String str = Build.DEVICE;
        sb.append(str);
        LOCAL_CONFIG_FILE_PATH = sb.toString();
        INTL_LOCAL_CONFIG_FILE_PATH = "overlay/" + str + "_intl";
    }

    private MiawareBcasHandler(Context context, Looper looper) {
        super(looper);
        this.mLock = new Object();
        this.TEMP_AWARE_PATH_QCOM = "/sys/class/power_debug/temp_aware";
        this.TEMP_AWARE_PATH_MTK = "/sys/class/thermal/thermal_message/temp_aware";
        this.LOW_TEMPERATURE_EXTREME_ENDUREANCE_SUPPORT = "low_temperature_extreme_endurance_support";
        this.PC_LOW_TEMPERATURE_EXTREME_ENDUREANCE_SWITCH = "pc_low_temperature_extreme_endurance_switch";
        this.LOW_TEMPERATURE_EXTREME_ENDUREANCE_TEMP_MODE_VALUE = "low_temperature_extreme_endurance_temp_mode_value";
        this.DBG_CONTROL = Log.isLoggable("power.bcas", 3);
        this.MSG_BASE = 0;
        this.MSG_CHECK_STATS = 1;
        this.MSG_UPDATE_CLOUD = 2;
        this.MSG_INIT = 3;
        this.mConfigType = -1;
        this.mTempAwarePath = null;
        this.mCurTempAwareVal = "10";
        this.mCurSendAwareVal = "10";
        this.mIsMTKDevice = false;
        this.mIsInitDone = false;
        this.mLowTempEnduranceSwitch = 0;
        this.mLowTempEnduranceSupport = 0;
        this.mLowTempEnduranceTempModeValue = 3;
        this.mDefConfigDeviceArr = new String[]{"xuanyuan", "dijun"};
        this.mThermalAwareFileDeviceArr = new String[]{"dijun", "jinghu"};
        this.mLocalLog = new LocalLog(256);
        this.mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.statemachine.MiawareBcasHandler.2
            @Override // com.miui.powerkeeper.PowerKeeperInterface.m
            public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
                if (MiawareBcasHandler.this.mCurBatteryStatus == null || MiawareBcasHandler.this.mCurBcasConfig == null || !MiawareBcasHandler.this.mCurBcasConfig.mIsValid || !MiawareBcasHandler.this.mCurBcasConfig.mBcasEnabled) {
                    return;
                }
                if (MiawareBcasHandler.this.mCurBatteryStatus.mBatteryLevel == i4 && ((MiawareBcasHandler.this.mConfigType == 3 || MiawareBcasHandler.this.mCurBatteryStatus.mBatteryTemp == i5) && MiawareBcasHandler.this.mCurBatteryStatus.mBatteryPlugged == MiawareBcasHandler.this.mPower.isCharging())) {
                    return;
                }
                int i8 = MiawareBcasHandler.this.mCurBatteryStatus.mBatteryPlugged != MiawareBcasHandler.this.mPower.isCharging() ? 1 : 0;
                if (MiawareBcasHandler.this.DBG_CONTROL || Build.IS_DEBUGGABLE) {
                    Log.d(MiawareBcasHandler.TAG, "onBatteryChanged plug = " + i6 + ", status = " + i2 + ", level = " + i4 + ", temp = " + i5 + ", isChragingStateChange = " + i8);
                }
                MiawareBcasHandler.this.mCurBatteryStatus.mBatteryLevel = i4;
                if (i5 != MiawareBcasHandler.this.mCurBatteryStatus.mBatteryTemp) {
                    MiawareBcasHandler.this.mCurBatteryStatus.mLastBatteryTemp = MiawareBcasHandler.this.mCurBatteryStatus.mBatteryTemp;
                    MiawareBcasHandler.this.mCurBatteryStatus.mBatteryTemp = i5;
                }
                MiawareBcasHandler.this.mCurBatteryStatus.mBatteryPlugged = MiawareBcasHandler.this.mPower.isCharging();
                if (MiawareBcasHandler.this.hasMessages(1)) {
                    MiawareBcasHandler.this.removeMessages(1);
                }
                MiawareBcasHandler miawareBcasHandler = MiawareBcasHandler.this;
                miawareBcasHandler.sendMessage(miawareBcasHandler.obtainMessage(1, i8, -1, Boolean.FALSE));
            }
        };
        this.mContext = context;
        this.mPower = PowerKeeperManager.getInstance();
        this.mSendTempMode = -1;
        updateTempAwarePath();
        this.mCloudObserver = b.a.p();
        this.mCurBatteryStatus = new BatteryStatus();
        this.mCurBcasConfig = new BcasConfig();
        sendEmptyMessage(3);
    }

    private void bcasInit() {
        PowerKeeperInterface.d dVar = new PowerKeeperInterface.d(this, this.mBatteryStatusReceiver);
        this.mBatteryChangedCallback = dVar;
        this.mPower.registerBatteryChangedListener(dVar);
        if (!this.mCurBcasConfig.updateConfig(this.mContext)) {
            writeLocalLog("mCurBcasConfig = " + this.mCurBcasConfig.toString());
            this.mCurBcasConfig.reset();
        }
        BcasConfig bcasConfig = this.mCurBcasConfig;
        if (bcasConfig.mBcasEnabled && bcasConfig.mIsValid && !bcasConfig.mTempModeFixed) {
            this.mLowTempEnduranceSupport = 1;
            this.mLowTempEnduranceTempModeValue = bcasConfig.mPowerSaveTempModeValue;
            Settings.System.putInt(this.mContext.getContentResolver(), "low_temperature_extreme_endurance_temp_mode_value", this.mLowTempEnduranceTempModeValue);
            Settings.System.putInt(this.mContext.getContentResolver(), "low_temperature_extreme_endurance_support", 1);
        } else {
            this.mLowTempEnduranceSupport = 0;
            Settings.System.putInt(this.mContext.getContentResolver(), "low_temperature_extreme_endurance_support", 0);
        }
        this.mLowTempEnduranceSwitch = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "pc_low_temperature_extreme_endurance_switch", 0, 0);
        this.mContext.getContentResolverForUser(UserHandle.SYSTEM).registerContentObserver(Settings.Secure.getUriFor("pc_low_temperature_extreme_endurance_switch"), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.MiawareBcasHandler.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                int i2 = MiawareBcasHandler.this.mLowTempEnduranceSwitch;
                MiawareBcasHandler miawareBcasHandler = MiawareBcasHandler.this;
                miawareBcasHandler.mLowTempEnduranceSwitch = Settings.Secure.getIntForUser(miawareBcasHandler.mContext.getContentResolver(), "pc_low_temperature_extreme_endurance_switch", 0, 0);
                if (i2 == MiawareBcasHandler.this.mLowTempEnduranceSwitch || MiawareBcasHandler.this.mCurBatteryStatus == null || MiawareBcasHandler.this.mCurBcasConfig == null || !MiawareBcasHandler.this.mCurBcasConfig.mIsValid || !MiawareBcasHandler.this.mCurBcasConfig.mBcasEnabled) {
                    return;
                }
                MiawareBcasHandler.this.mCurBatteryStatus.updateBatteryStatus();
                MiawareBcasHandler miawareBcasHandler2 = MiawareBcasHandler.this;
                miawareBcasHandler2.sendMessage(miawareBcasHandler2.obtainMessage(1, -1, -1, Boolean.FALSE));
            }
        });
        this.mIsInitDone = true;
        sendMessage(obtainMessage(1, 0, -1, Boolean.TRUE));
    }

    private void checkBatteryStatus(boolean z2, int i2) {
        if (this.mIsInitDone) {
            this.mCurBatteryStatus.updateAwareNodeVal(this.mCurBcasConfig);
            if (z2 || i2 == 1 || this.mCurBatteryStatus.getTempMode() != this.mSendTempMode || !this.mCurSendAwareVal.equals(this.mCurBatteryStatus.mAwareNodeVal)) {
                sendStateInfoChange(BCAS_TEMP_MODE_CHANGE, this.mCurBatteryStatus.getTempMode(), Integer.valueOf(this.mCurBatteryStatus.mAwareNodeVal).intValue(), this.mCurBatteryStatus.getBatteryPlugged());
                if (this.mCurBatteryStatus.getTempMode() != this.mSendTempMode) {
                    PowerScheduleController.notifyUpdateTempMode(this.mCurBatteryStatus.getTempMode());
                }
                this.mSendTempMode = this.mCurBatteryStatus.getTempMode();
                this.mCurSendAwareVal = this.mCurBatteryStatus.mAwareNodeVal;
            }
            if (this.mConfigType != 3) {
                updateTempAwareFile(this.mTempAwarePath, this.mCurBatteryStatus.mAwareNodeVal, this.mCurBatteryStatus.getTempMode());
            }
        }
    }

    public static int getCurBcasTempMode() {
        MiawareBcasHandler miawareBcasHandler = sInstance;
        if (miawareBcasHandler != null && miawareBcasHandler.mIsInitDone) {
            BcasConfig bcasConfig = miawareBcasHandler.mCurBcasConfig;
            if (bcasConfig.mIsValid && !bcasConfig.mTempModeFixed && miawareBcasHandler.mLowTempEnduranceSwitch != 0) {
                Log.d(TAG, "support curBcasTempMode = " + sInstance.mCurBatteryStatus.getTempMode());
                return sInstance.mCurBatteryStatus.getTempMode();
            }
        }
        Log.d(TAG, "not support getCurBcasTempMode rerurn -1");
        return -1;
    }

    public static MiawareBcasHandler getInstance() {
        return sInstance;
    }

    public static synchronized MiawareBcasHandler init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new MiawareBcasHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public static void notifyUpdateBcasCloud(JSONObject jSONObject) {
        MiawareBcasHandler miawareBcasHandler = sInstance;
        if (miawareBcasHandler == null || !miawareBcasHandler.mIsInitDone) {
            return;
        }
        Log.i(TAG, "Cloud-Observer onChange - DY object is " + jSONObject);
        MiawareBcasHandler miawareBcasHandler2 = sInstance;
        Objects.requireNonNull(miawareBcasHandler2);
        if (miawareBcasHandler2.hasMessages(2)) {
            MiawareBcasHandler miawareBcasHandler3 = sInstance;
            Objects.requireNonNull(miawareBcasHandler3);
            miawareBcasHandler3.removeMessages(2);
        }
        MiawareBcasHandler miawareBcasHandler4 = sInstance;
        Objects.requireNonNull(miawareBcasHandler4);
        miawareBcasHandler4.sendEmptyMessage(2);
    }

    private void sendStateInfoChange(String str, int i2, int i3, boolean z2) {
        writeLocalLog("Delivery_status(" + z2 + "): bcas_mode=" + i2 + ",bcas_l=" + i3 + ",b_t=" + this.mCurBatteryStatus.mBatteryTemp + ",b_l=" + this.mCurBatteryStatus.mBatteryLevel);
        Intent intent = new Intent(str);
        if (BCAS_TEMP_MODE_CHANGE.equals(str)) {
            intent.putExtra("is_charging", z2);
            intent.putExtra("temp_mode", i2);
            intent.putExtra("bcas_level", i3);
        }
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void updateBcasCloud() {
        if (this.mIsInitDone) {
            if (!this.mCurBcasConfig.mBcasEnabled) {
                this.mCurBatteryStatus.updateBatteryStatus();
            }
            if (!this.mCurBcasConfig.updateConfig(this.mContext)) {
                writeLocalLog("mCurBcasConfig = " + this.mCurBcasConfig.toString());
                this.mCurBcasConfig.reset();
            }
            BcasConfig bcasConfig = this.mCurBcasConfig;
            if (bcasConfig.mBcasEnabled && bcasConfig.mIsValid) {
                int i2 = this.mLowTempEnduranceTempModeValue;
                int i3 = bcasConfig.mPowerSaveTempModeValue;
                if (i2 != i3) {
                    this.mLowTempEnduranceTempModeValue = i3;
                    Settings.System.putInt(this.mContext.getContentResolver(), "low_temperature_extreme_endurance_temp_mode_value", this.mLowTempEnduranceTempModeValue);
                }
            }
            BcasConfig bcasConfig2 = this.mCurBcasConfig;
            if (bcasConfig2.mBcasEnabled && bcasConfig2.mIsValid && !bcasConfig2.mTempModeFixed) {
                if (this.mLowTempEnduranceSupport == 0) {
                    this.mLowTempEnduranceSupport = 1;
                    Settings.System.putInt(this.mContext.getContentResolver(), "low_temperature_extreme_endurance_support", 1);
                }
            } else if (this.mLowTempEnduranceSupport == 1) {
                this.mLowTempEnduranceSupport = 0;
                Settings.System.putInt(this.mContext.getContentResolver(), "low_temperature_extreme_endurance_support", 0);
            }
            sendMessage(obtainMessage(1, -1, -1, Boolean.FALSE));
        }
    }

    /* JADX WARN: Code duplicated, block: B:16:0x001a A[Catch: all -> 0x000e, TryCatch #0 {all -> 0x000e, blocks: (B:5:0x0005, B:7:0x0009, B:17:0x0021, B:19:0x0029, B:21:0x002d, B:24:0x0047, B:23:0x0031, B:26:0x0049, B:28:0x004f, B:30:0x0081, B:29:0x006b, B:12:0x0010, B:14:0x0016, B:16:0x001a), top: B:34:0x0005 }] */
    private void updateTempAwareFile(String str, String str2, int i2) {
        synchronized (this.mLock) {
            if (i2 != 0) {
                try {
                    BcasConfig bcasConfig = this.mCurBcasConfig;
                    if (bcasConfig != null && i2 <= bcasConfig.mTempAwareEnabledTempModeThreshold) {
                        str2 = "9";
                        if (this.DBG_CONTROL || Build.IS_DEBUGGABLE) {
                            Log.d(TAG, "change aware val for temp mode zero");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                str2 = "9";
                if (this.DBG_CONTROL) {
                    Log.d(TAG, "change aware val for temp mode zero");
                } else {
                    Log.d(TAG, "change aware val for temp mode zero");
                }
            }
            if (this.mCurTempAwareVal.equals(str2)) {
                if (this.DBG_CONTROL || Build.IS_DEBUGGABLE) {
                    Log.d(TAG, "ignore for the same value: " + str2);
                }
                return;
            }
            if (FileUtil.writeToFile(str, str2)) {
                this.mCurTempAwareVal = str2;
                writeLocalLog("write temp aware " + str2);
            } else {
                Log.e(TAG, "write " + str + " error");
            }
        }
    }

    private void updateTempAwarePath() {
        String[] strArr = this.mThermalAwareFileDeviceArr;
        if (strArr != null && ArrayUtils.contains(strArr, Build.DEVICE)) {
            this.mTempAwarePath = "/sys/class/thermal/thermal_message/temp_aware";
            return;
        }
        boolean z2 = FeatureParser.getBoolean("is_mediatek", false);
        this.mIsMTKDevice = z2;
        if (z2) {
            this.mTempAwarePath = "/sys/class/thermal/thermal_message/temp_aware";
            return;
        }
        this.mTempAwarePath = "/sys/class/power_debug/temp_aware";
        writeLocalLog("temp wawre path: " + this.mTempAwarePath);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeLocalLog(String str) {
        if (this.DBG_CONTROL) {
            Log.i(TAG, str);
        }
        this.mLocalLog.log(TAG + str);
    }

    public void destroy() {
        Log.w(TAG, "destroy");
        PowerKeeperManager powerKeeperManager = this.mPower;
        if (powerKeeperManager != null) {
            powerKeeperManager.unregisterBatteryChangedListener(this.mBatteryChangedCallback);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("bcasconfig mConfigType = " + this.mConfigType);
        printWriter.println("bcasconfig mCurBatteryStatus: " + this.mCurBatteryStatus.toString());
        printWriter.println("bcasconfig mCurBcasConfig: " + this.mCurBcasConfig.toString());
        printWriter.println("bcasconfig mTempAwarePath = " + this.mTempAwarePath);
        printWriter.println("bcasconfig mCurSendAwareVal = " + this.mCurSendAwareVal);
        printWriter.println("bcasconfig mCurTempAwareVal = " + this.mCurTempAwareVal);
        printWriter.println("bcasconfig mLowTempEnduranceSupport = " + this.mLowTempEnduranceSupport);
        printWriter.println("bcasconfig mLowTempEnduranceSwitch = " + this.mLowTempEnduranceSwitch);
        printWriter.println("bcasconfig mLowTempEnduranceTempModeValue = " + this.mLowTempEnduranceTempModeValue);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 != 1) {
            if (i2 == 2) {
                writeLocalLog("MSG_UPDATE_CLOUD");
                updateBcasCloud();
                return;
            } else {
                if (i2 != 3) {
                    return;
                }
                writeLocalLog("MSG_INIT");
                bcasInit();
                return;
            }
        }
        boolean zBooleanValue = ((Boolean) message.obj).booleanValue();
        int i3 = message.arg1;
        if (this.DBG_CONTROL || Build.IS_DEBUGGABLE) {
            Log.d(TAG, "MSG_CHECK_STATS isInit = " + zBooleanValue + ", isChragingStateChange = " + i3);
        }
        checkBatteryStatus(zBooleanValue, i3);
    }
}
