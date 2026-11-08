package com.miui.powerkeeper.statemachine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.PowerManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.controller.AlarmController;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.InternationalAdapter;
import com.miui.whetstone.AlarmPolicy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class SatelliteTelephonyController {
    public static final String ACTION_SATELLITE_STATE_CHANGE = "com.android.app.action.SATELLITE_STATE_CHANGE";
    private static final String CLOUD_BATTERY_LEVEL_FOR_PS = "batteryLevelForPowerSave";
    private static final String CLOUD_NFC_SWITCH = "nfcSwitch";
    private static final String CLOUD_POWER_SAVE_SWITCH = "powerSaveSwitch";
    private static final String CLOUD_RESTRICT_ALARM_SWITCH = "restrictAlarmSwitch";
    private static final String CLOUD_TOUCH_CONTROLLER_APPS = "touchControllerApps";
    private static final String CLOUD_TOUCH_CONTROLLER_SWITCH = "restrictTouchControllerSwitch";
    public static final String KEY_SATELLITE_STATE_CHANGE_IS_ENABLE = "is_enable";
    public static final String KEY_SATELLITE_STATE_CHANGE_PHONE_ID = "phone_id";
    private static final String KEY_SATELLITE_TELEPHONY_RESOURCE_STATE = "key_satellite_telephony_state";
    private static final String SatelliteTelephonyNameSpace = "st_name_space";
    private static final String TAG = "SatelliteTelephonyController";
    private static b.a mCloudObserver;
    private static volatile SatelliteTelephonyController sInstance;
    private final int SATELLITE_TELEPHONE_DEFAULT;
    private final int SATELLITE_TELEPHONE_MODE;
    private PowerKeeperInterface.d mBatteryChangedCallback;
    private int mBatteryLevelForPowerSave;
    private final ArraySet<String> mCloudTouchCtrlPkgs;
    private ContentResolver mContentResolver;
    private final Context mContext;
    private Handler mHandler;
    private boolean mIsSatelliteTelephonyEnabled;
    private final LocalLog mLocalLog;
    private final Object mLock;
    private boolean mNfcControllerSwitch;
    private final PowerManager mPowerManager;
    private boolean mPowerSaveControllerSwitch;
    private boolean mRestrictAlarmSwitch;
    private final SatelliteTelephonyControllerState mSatelliteTelephonyControllerState;
    private SettingsObserver mSettingsObserver;
    private boolean mTouchControllerSwitch;
    private static final boolean DEBUG = Build.isDebuggable();
    private static final String[] mRestrictPkgs = InternationalAdapter.mRestrictPkgs;

    class BatteryStatusListener implements PowerKeeperInterface.m {
        BatteryStatusListener() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.m
        public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
            if (i4 <= SatelliteTelephonyController.this.mBatteryLevelForPowerSave) {
                try {
                    if (SatelliteTelephonyController.this.mPowerSaveControllerSwitch && SatelliteTelephonyController.this.mIsSatelliteTelephonyEnabled && !SatelliteTelephonyController.this.mPowerManager.isPowerSaveMode()) {
                        boolean powerSaveModeEnabled = SatelliteTelephonyController.this.mPowerManager.setPowerSaveModeEnabled(true);
                        SatelliteTelephonyController.this.logWithsLocal("applySatelliteTelephonyMode savemode is " + powerSaveModeEnabled);
                    }
                } catch (SecurityException e2) {
                    Log.e(SatelliteTelephonyController.TAG, "SecurityException" + e2);
                }
            }
        }
    }

    private static class SatelliteTelephonyControllerState {
        static final int INDEX_NFC = 1;
        static final int INDEX_POWER_SAVE = 2;
        int previous;
        int restore;
        int restoreWarning;
        int stats;

        /* JADX INFO: Access modifiers changed from: private */
        public SatelliteTelephonyControllerState reset() {
            this.previous = 0;
            this.restore = 0;
            this.restoreWarning = 0;
            return this;
        }

        boolean getPreviousEnable(int i2) {
            return (this.previous & i2) == i2;
        }

        boolean needRestore(int i2) {
            return (this.restore & i2) == i2;
        }

        void setPreviousEnable(int i2, boolean z2) {
            if (z2) {
                this.previous |= i2;
                this.restore = i2 | this.restore;
            } else {
                int i3 = this.previous;
                int i4 = ~i2;
                this.previous = i3 & i4;
                this.restore = i4 & this.restore;
            }
        }

        void setRestore(int i2, boolean z2) {
            if (z2) {
                this.restore = i2 | this.restore;
            } else {
                this.restore = (~i2) & this.restore;
            }
        }

        void setRestoreWarning(int i2) {
            this.restoreWarning = i2 | this.restoreWarning;
        }

        private SatelliteTelephonyControllerState() {
            this.previous = 0;
            this.restore = 0;
            reset();
        }
    }

    private class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            if (uri == null) {
                return;
            }
            if (SatelliteTelephonyController.DEBUG) {
                Log.d(SatelliteTelephonyController.TAG, uri.toSafeString());
            }
            synchronized (SatelliteTelephonyController.this.mLock) {
                try {
                    if (uri.equals(Settings.System.getUriFor("satellite_state"))) {
                        SatelliteTelephonyController.this.mIsSatelliteTelephonyEnabled = Settings.System.getInt(PowerKeeperApplication.b().getContentResolver(), "satellite_state", 0) == 1;
                        SatelliteTelephonyController.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.statemachine.SatelliteTelephonyController.SettingsObserver.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (SatelliteTelephonyController.this.mIsSatelliteTelephonyEnabled) {
                                    SatelliteTelephonyController.this.applySatelliteTelephonyMode();
                                    SatelliteTelephonyController satelliteTelephonyController = SatelliteTelephonyController.this;
                                    satelliteTelephonyController.writeToDb(satelliteTelephonyController.mSatelliteTelephonyControllerState);
                                } else {
                                    SatelliteTelephonyController.this.restoreSatelliteTelephonyMode();
                                    SatelliteTelephonyController satelliteTelephonyController2 = SatelliteTelephonyController.this;
                                    satelliteTelephonyController2.writeToDb(satelliteTelephonyController2.mSatelliteTelephonyControllerState);
                                }
                            }
                        });
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private SatelliteTelephonyController() {
        this.mLocalLog = new LocalLog(DEBUG ? 128 : 64);
        this.SATELLITE_TELEPHONE_MODE = 1;
        this.SATELLITE_TELEPHONE_DEFAULT = 0;
        this.mNfcControllerSwitch = true;
        this.mRestrictAlarmSwitch = true;
        this.mIsSatelliteTelephonyEnabled = false;
        this.mCloudTouchCtrlPkgs = new ArraySet<>(10);
        logWithsLocal("SatelliteTelephonyController init");
        Context contextB = PowerKeeperApplication.b();
        this.mContext = contextB;
        contextB.getSharedPreferences(SatelliteTelephonyNameSpace, 0);
        mCloudObserver = b.a.p();
        SatelliteTelephonyControllerState satelliteTelephonyControllerState = new SatelliteTelephonyControllerState();
        this.mSatelliteTelephonyControllerState = satelliteTelephonyControllerState;
        registerCloudObserver();
        parseSatelliteTelephonyFromDb();
        this.mHandler = new Handler(PowerKeeperManager.getThread().getLooper());
        this.mLock = new Object();
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        ContentResolver contentResolverForUser = contextB.getContentResolverForUser(UserHandle.OWNER);
        this.mContentResolver = contentResolverForUser;
        contentResolverForUser.registerContentObserver(Settings.System.getUriFor("satellite_state"), false, this.mSettingsObserver, -2);
        this.mBatteryChangedCallback = new PowerKeeperInterface.d(this.mHandler, new BatteryStatusListener());
        this.mPowerManager = (PowerManager) contextB.getSystemService(NightStandbyRecord.KEY_POWER);
        PowerKeeperManager.getInstance().registerBatteryChangedListener(this.mBatteryChangedCallback);
        readFromDb(satelliteTelephonyControllerState);
        logWithsLocal("SatelliteTelephonyControllerState stats IS " + satelliteTelephonyControllerState.stats);
        if (satelliteTelephonyControllerState.stats == 1) {
            restoreSatelliteTelephonyMode();
            writeToDb(satelliteTelephonyControllerState.reset());
        }
    }

    public static SatelliteTelephonyController getInstance() {
        if (sInstance == null) {
            synchronized (SatelliteTelephonyController.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new SatelliteTelephonyController();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private boolean getNFCEnable() {
        try {
            NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
            return defaultAdapter != null && defaultAdapter.isEnabled();
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logWithsLocal(String str) {
        Slog.i(TAG, str);
        this.mLocalLog.log(str);
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        if (mCloudObserver == null) {
            Log.i(TAG, "onCloudFunctionUpdated failed");
        }
    }

    private void parseSatelliteTelephonyFromDb() {
        b.a aVar = mCloudObserver;
        int i2 = b.b.S;
        JSONObject jSONObjectL = aVar.l(i2);
        if (!mCloudObserver.s(i2) || jSONObjectL == null) {
            return;
        }
        this.mNfcControllerSwitch = jSONObjectL.optBoolean(CLOUD_NFC_SWITCH, true);
        this.mPowerSaveControllerSwitch = jSONObjectL.optBoolean(CLOUD_POWER_SAVE_SWITCH, false);
        this.mRestrictAlarmSwitch = jSONObjectL.optBoolean(CLOUD_RESTRICT_ALARM_SWITCH, true);
        this.mTouchControllerSwitch = jSONObjectL.optBoolean(CLOUD_TOUCH_CONTROLLER_SWITCH, true);
        this.mBatteryLevelForPowerSave = jSONObjectL.optInt(CLOUD_BATTERY_LEVEL_FOR_PS, 20);
        String strOptString = jSONObjectL.optString(CLOUD_TOUCH_CONTROLLER_APPS, null);
        if (strOptString == null || strOptString.isEmpty()) {
            this.mCloudTouchCtrlPkgs.clear();
        } else {
            Collections.addAll(this.mCloudTouchCtrlPkgs, strOptString.split(","));
        }
        Log.d(TAG, "parseSatelliteTelephonyFromDb mNfcControllerSwitch IS " + this.mNfcControllerSwitch + ", mPowerSaveControllerSwitch is " + this.mPowerSaveControllerSwitch + ", mRestrictAlarmSwitch is  " + this.mRestrictAlarmSwitch + ", mTouchControllerSwitch is " + this.mTouchControllerSwitch + ", appJsonStr is " + strOptString);
    }

    private void readFromDb(SatelliteTelephonyControllerState satelliteTelephonyControllerState) {
        try {
            String string = SimpleSettings.Misc.getString(this.mContext, KEY_SATELLITE_TELEPHONY_RESOURCE_STATE, "");
            if (!string.isEmpty()) {
                JSONObject jSONObject = new JSONObject(string);
                satelliteTelephonyControllerState.previous = jSONObject.optInt("previous", 0);
                satelliteTelephonyControllerState.restore = jSONObject.optInt("restore", 0);
                satelliteTelephonyControllerState.stats = jSONObject.optInt("stats", 0);
            }
        } catch (JSONException e2) {
            Slog.w(TAG, "readFromDb exception", e2);
        }
        logWithsLocal("readFromDb previous=0x" + Integer.toHexString(satelliteTelephonyControllerState.previous) + " restore=0x" + Integer.toHexString(satelliteTelephonyControllerState.restore) + " rebootTime=" + SimpleSettings.Misc.getString(this.mContext, KEY_SATELLITE_TELEPHONY_RESOURCE_STATE, ""));
    }

    private void registerCloudObserver() {
        mCloudObserver.t(b.b.S, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.SatelliteTelephonyController.1
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                if (jSONObject != null) {
                    SatelliteTelephonyController.this.mNfcControllerSwitch = jSONObject.optBoolean(SatelliteTelephonyController.CLOUD_NFC_SWITCH, true);
                    SatelliteTelephonyController.this.mPowerSaveControllerSwitch = jSONObject.optBoolean(SatelliteTelephonyController.CLOUD_POWER_SAVE_SWITCH, false);
                    SatelliteTelephonyController.this.mRestrictAlarmSwitch = jSONObject.optBoolean(SatelliteTelephonyController.CLOUD_RESTRICT_ALARM_SWITCH, true);
                    SatelliteTelephonyController.this.mTouchControllerSwitch = jSONObject.optBoolean(SatelliteTelephonyController.CLOUD_TOUCH_CONTROLLER_SWITCH, true);
                    SatelliteTelephonyController.this.mBatteryLevelForPowerSave = jSONObject.optInt(SatelliteTelephonyController.CLOUD_BATTERY_LEVEL_FOR_PS, 20);
                    String strOptString = jSONObject.optString(SatelliteTelephonyController.CLOUD_TOUCH_CONTROLLER_APPS, null);
                    if (strOptString == null || strOptString.isEmpty()) {
                        SatelliteTelephonyController.this.mCloudTouchCtrlPkgs.clear();
                    } else {
                        Collections.addAll(SatelliteTelephonyController.this.mCloudTouchCtrlPkgs, strOptString.split(","));
                    }
                    TouchController.updateCloudTouchCtrlPkgs(SatelliteTelephonyController.this.mCloudTouchCtrlPkgs);
                    SatelliteTelephonyController.this.logWithsLocal("Callback mNfcControllerSwitch IS " + SatelliteTelephonyController.this.mNfcControllerSwitch + ", mPowerSaveControllerSwitch is " + SatelliteTelephonyController.this.mPowerSaveControllerSwitch + ", mRestrictAlarmSwitch is  " + SatelliteTelephonyController.this.mRestrictAlarmSwitch + ", mTouchControllerSwitch is " + SatelliteTelephonyController.this.mTouchControllerSwitch + ", appJsonStr is " + strOptString);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreSatelliteTelephonyMode() {
        try {
            if (this.mSatelliteTelephonyControllerState.needRestore(1)) {
                boolean previousEnable = this.mSatelliteTelephonyControllerState.getPreviousEnable(1);
                logWithsLocal("restoreSatelliteTelephonyMode previous is " + previousEnable + ", getNFCEnable is " + getNFCEnable());
                if (previousEnable != getNFCEnable()) {
                    setNFCEnable(true);
                    logWithsLocal("restoreSatelliteTelephonyMode setNFCEnable is true");
                    this.mSatelliteTelephonyControllerState.setRestore(1, false);
                } else {
                    logWithsLocal("restoreSatelliteTelephonyMode setNFCEnabled:warning");
                    this.mSatelliteTelephonyControllerState.setRestoreWarning(1);
                }
            }
            logWithsLocal("restoreSatelliteTelephonyMode mPowerManager.isPowerSaveMode() is " + this.mPowerManager.isPowerSaveMode() + ", INDEX_POWER_SAVE is " + this.mSatelliteTelephonyControllerState.needRestore(2));
            if (this.mSatelliteTelephonyControllerState.needRestore(2) && this.mPowerManager.isPowerSaveMode()) {
                boolean previousEnable2 = this.mSatelliteTelephonyControllerState.getPreviousEnable(2);
                logWithsLocal("restoreSatelliteTelephonyMode previous is " + previousEnable2 + ", isPowerSaveMode is " + this.mPowerManager.isPowerSaveMode());
                if (previousEnable2 == this.mPowerManager.isPowerSaveMode()) {
                    this.mPowerManager.setPowerSaveModeEnabled(false);
                    logWithsLocal("restoreSatelliteTelephonyMode isPowerSaveMode is false");
                    this.mSatelliteTelephonyControllerState.setRestore(2, false);
                } else {
                    logWithsLocal("restoreSatelliteTelephonyMode setPowerSaveModeEnabled:warning");
                    this.mSatelliteTelephonyControllerState.setRestoreWarning(2);
                }
            }
            if (this.mRestrictAlarmSwitch) {
                restrictAlarm(false);
            }
            if (this.mTouchControllerSwitch) {
                logWithsLocal("restoreSatelliteTelephonyMode notifySatelliteTelephonyStatusChanged is " + this.mIsSatelliteTelephonyEnabled);
                TouchController.notifySatelliteTelephonyStatusChanged(this.mIsSatelliteTelephonyEnabled);
            }
            this.mSatelliteTelephonyControllerState.stats = 0;
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void restrictAlarm(boolean z2) {
        Map<String, Integer> packageUidsMap = PowerKeeperManager.getInstance().getPackageUidsMap(this.mContext.getUserId());
        int i2 = 0;
        String[] strArr = new String[0];
        AlarmController alarmController = PowerKeeperManager.getInstance().getAlarmController();
        ArrayList arrayList = new ArrayList();
        while (true) {
            String[] strArr2 = mRestrictPkgs;
            if (i2 >= strArr2.length) {
                alarmController.setAppsAlarmState(arrayList);
                return;
            }
            Integer num = packageUidsMap.get(strArr2[i2]);
            if (num != null) {
                arrayList.add(z2 ? new AlarmPolicy(num.intValue(), strArr) : new AlarmPolicy(num.intValue(), (String[]) null));
            }
            i2++;
        }
    }

    private void setNFCEnable(boolean z2) {
        if (z2 == getNFCEnable()) {
            return;
        }
        try {
            NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
            if (defaultAdapter == null) {
                return;
            }
            if (z2) {
                defaultAdapter.enable();
                logWithsLocal("quite satellite telephony open NFC");
            } else {
                this.mSatelliteTelephonyControllerState.setPreviousEnable(1, getNFCEnable());
                defaultAdapter.disable();
                logWithsLocal("entry satellite telephony close NFC");
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeToDb(SatelliteTelephonyControllerState satelliteTelephonyControllerState) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("previous", satelliteTelephonyControllerState.previous).put("restore", satelliteTelephonyControllerState.restore).put("stats", satelliteTelephonyControllerState.stats);
        } catch (JSONException e2) {
            Slog.w(TAG, "writeToDb exception", e2);
        }
        SimpleSettings.Misc.putString(this.mContext, KEY_SATELLITE_TELEPHONY_RESOURCE_STATE, jSONObject.toString());
    }

    public void applySatelliteTelephonyMode() {
        try {
            PowerManager powerManager = (PowerManager) this.mContext.getSystemService(NightStandbyRecord.KEY_POWER);
            BatteryManager batteryManager = (BatteryManager) PowerKeeperApplication.b().getSystemService("batterymanager");
            int intProperty = batteryManager != null ? batteryManager.getIntProperty(4) : 0;
            PowerManager.WakeLock wakeLockNewWakeLock = powerManager.newWakeLock(1, "SatelliteTelephonyController:changeSetting");
            wakeLockNewWakeLock.acquire(1000L);
            this.mSatelliteTelephonyControllerState.stats = 1;
            if (this.mNfcControllerSwitch) {
                logWithsLocal("applySatelliteTelephonyMode setNFCEnable controller");
                setNFCEnable(false);
            }
            if (this.mRestrictAlarmSwitch) {
                restrictAlarm(true);
                logWithsLocal("applySatelliteTelephonyMode restrictAlarm");
            }
            if (this.mPowerSaveControllerSwitch && this.mIsSatelliteTelephonyEnabled && !this.mPowerManager.isPowerSaveMode() && intProperty <= this.mBatteryLevelForPowerSave) {
                boolean powerSaveModeEnabled = this.mPowerManager.setPowerSaveModeEnabled(true);
                this.mSatelliteTelephonyControllerState.setPreviousEnable(2, powerSaveModeEnabled);
                logWithsLocal("applySatelliteTelephonyMode savemode is " + powerSaveModeEnabled);
            }
            if (this.mTouchControllerSwitch) {
                logWithsLocal("applySatelliteTelephonyMode touch controller is " + this.mIsSatelliteTelephonyEnabled);
                TouchController.notifySatelliteTelephonyStatusChanged(this.mIsSatelliteTelephonyEnabled);
            }
            if (wakeLockNewWakeLock.isHeld()) {
                wakeLockNewWakeLock.release();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void destroy() {
        try {
            if (this.mSatelliteTelephonyControllerState.stats == 1) {
                restoreSatelliteTelephonyMode();
                writeToDb(this.mSatelliteTelephonyControllerState.reset());
            }
            ContentResolver contentResolver = this.mContentResolver;
            if (contentResolver != null) {
                contentResolver.unregisterContentObserver(this.mSettingsObserver);
                this.mContentResolver = null;
            }
            if (this.mBatteryChangedCallback != null) {
                PowerKeeperManager.getInstance().unregisterBatteryChangedListener(this.mBatteryChangedCallback);
                this.mBatteryChangedCallback = null;
            }
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Failed to destroy satellite telephony controller", e2);
        } catch (Exception e3) {
            Log.e(TAG, "satellite telephony controller destroy error" + e3);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump of SatelliteTelephonyController:");
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
    }
}
