package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.powerchecker.PowerCheckerCloudConfigure;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.ScenarioManager;
import com.miui.powerkeeper.thermal.setting.SettingDTO;
import com.miui.powerkeeper.thermal.setting.SettingProvider;
import com.miui.powerkeeper.utils.FileUtil;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import miui.process.ForegroundInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerSaveModeHandler extends Handler {
    public static final String CONTENT_KEY = "power_save_mode_new";
    private static final boolean DEBUG;
    private static final String TAG = "PowerSaveModeNew";
    private static PowerSaveModeHandler sInstance;
    private final int BATTERY_SAVE_LEVEL_0;
    private final int BATTERY_SAVE_LEVEL_1;
    private final int BATTERY_SAVE_LEVEL_2;
    private final int BATTERY_SAVE_LEVEL_3;
    private final int BATTERY_SAVE_LEVEL_ALL;
    private final int CLOSE_KWS_MOD;
    private final int DEFAULT_POWER_LEVEL;
    private final String[] DEFAULT_WHITE_SCENARIO_LIST;
    private final String KEY_SETTINGS_SUPERSAVE_MODE;
    private final int MONITOR_BATTERY;
    private final int MONITOR_PKG;
    private final int MONITOR_SCENARIO;
    private final int MSG_ID_BATTERY_STATE_CHANGE;
    private final int MSG_ID_CLOUD_UPDATE;
    private final int MSG_ID_SET_THERMAL_SETTING;
    private final int MSG_ID_SUPER_SAVE_MODE_CHANGE;
    private final String PATH_POWER_LEVEL;
    private final String PATH_POWER_SAVE_MODE;
    private final String PATH_SOC_LIMIT;
    private final int POWER_LEVEL_DISABLE;
    private final int POWER_SAVE_DISABLE;
    private final int POWER_SAVE_ENABLE;
    private final int POWER_SAVE_SUPER_ENABLE;
    private final String SOC_LIMIT_DISABLE;
    private boolean isSuperSaveMode;
    private PowerKeeperInterface.d mBatteryCallback;
    private final Map<Integer, SparseIntArray> mBatteryPowerLevel;
    private PowerKeeperInterface.m mBatteryStatusReceiver;
    private final Context mContext;
    private String mDefaultSocLimit;
    private boolean mFuncSwitch;
    private int mLastBattery;
    private String mLastFg;
    private String mLastScenario;
    private int mMonitorState;
    private final Map<String, Integer> mPkgPowerLevel;
    private final Map<String, String> mPkgSocLimit;
    private List<Integer> mPolicyIndexes;
    private Integer mPowerLevel;
    private Integer mPowerLevelCtrl;
    private int mPowerMode;
    private Integer mPowerModelCtrl;
    private ThermalScenarioObserver mScenarioObserver;
    private final Map<String, Integer> mScenarioPowerLevel;
    private final Map<String, String> mScenarioSocLimit;
    private String mSocLimit;
    private String mSocLimitCtrl;
    private ContentObserver mSuperSaveModeObserver;

    /* JADX INFO: Access modifiers changed from: private */
    static class ThermalScenarioObserver {
        private final Map<Integer, String> mScenarioNameMap = new HashMap(32);

        ThermalScenarioObserver() {
            SettingProvider.getInstance().registerCallback(new z(this));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onNewScenarioData(final SettingDTO settingDTO) {
            if (settingDTO == null || PowerSaveModeHandler.sInstance == null) {
                return;
            }
            PowerSaveModeHandler.sInstance.post(new Runnable() { // from class: com.miui.powerkeeper.statemachine.y
                @Override // java.lang.Runnable
                public final void run() {
                    this.f559a.lambda$onNewScenarioData$0(settingDTO);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX INFO: renamed from: setThermalSetting, reason: merged with bridge method [inline-methods] */
        public void lambda$onNewScenarioData$0(SettingDTO settingDTO) {
            this.mScenarioNameMap.clear();
            List<SettingDTO.ScenarioDTO> scenarios = settingDTO.getScenarios();
            if (scenarios != null) {
                for (SettingDTO.ScenarioDTO scenarioDTO : scenarios) {
                    this.mScenarioNameMap.put(Integer.valueOf(scenarioDTO.getID()), scenarioDTO.getName());
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void unregister() {
            SettingProvider.getInstance().unregisterCallback(new z(this));
        }

        String getCurrentName(int i2) {
            return this.mScenarioNameMap.getOrDefault(Integer.valueOf(i2), "");
        }
    }

    static {
        DEBUG = Log.isLoggable(TAG, 3) || Build.IS_DEBUGGABLE;
    }

    private PowerSaveModeHandler(final Context context, Looper looper) throws Throwable {
        super(looper);
        this.KEY_SETTINGS_SUPERSAVE_MODE = "power_supersave_mode_open";
        this.PATH_POWER_SAVE_MODE = "/sys/class/thermal/power_save/powersave_mode";
        this.PATH_POWER_LEVEL = "/sys/class/thermal/power_save/power_level";
        this.PATH_SOC_LIMIT = "/sys/powersave/limit_buf";
        this.CLOSE_KWS_MOD = 10;
        this.MSG_ID_CLOUD_UPDATE = 0;
        this.MSG_ID_SUPER_SAVE_MODE_CHANGE = 1;
        this.MSG_ID_BATTERY_STATE_CHANGE = 2;
        this.MSG_ID_SET_THERMAL_SETTING = 3;
        this.MONITOR_PKG = 1;
        this.MONITOR_SCENARIO = 2;
        this.MONITOR_BATTERY = 4;
        this.BATTERY_SAVE_LEVEL_0 = 1;
        this.BATTERY_SAVE_LEVEL_1 = 2;
        this.BATTERY_SAVE_LEVEL_2 = 4;
        this.BATTERY_SAVE_LEVEL_3 = 8;
        this.BATTERY_SAVE_LEVEL_ALL = 15;
        this.POWER_SAVE_DISABLE = -1;
        this.POWER_SAVE_ENABLE = 1;
        this.POWER_SAVE_SUPER_ENABLE = 2;
        this.POWER_LEVEL_DISABLE = -1;
        this.DEFAULT_POWER_LEVEL = 100111111;
        this.SOC_LIMIT_DISABLE = "\"\"";
        this.mPkgPowerLevel = new HashMap(5);
        this.mScenarioPowerLevel = new HashMap(5);
        this.mPkgSocLimit = new HashMap(5);
        this.mScenarioSocLimit = new HashMap(5);
        this.mBatteryPowerLevel = new HashMap(4);
        this.DEFAULT_WHITE_SCENARIO_LIST = new String[]{"lowtempcharge", "camera", "4k", "8k", "nolimits", "arvr", "huanji", "game", "pubg", "game2", "optiGame", "yuanshen", "xingtie", "cgame", "game+call", "game2+call", "optiGame+call", "yuanshen+call", "pubg+call", "xingtie+call"};
        this.mFuncSwitch = false;
        this.isSuperSaveMode = false;
        this.mDefaultSocLimit = null;
        this.mPowerMode = -1;
        this.mPowerModelCtrl = -1;
        this.mPowerLevelCtrl = -1;
        this.mPowerLevel = 100111111;
        this.mSocLimitCtrl = null;
        this.mSocLimit = null;
        this.mLastScenario = null;
        this.mLastFg = null;
        this.mLastBattery = -1;
        this.mMonitorState = -8;
        this.mPolicyIndexes = null;
        this.mScenarioObserver = null;
        this.mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.statemachine.PowerSaveModeHandler.3
            private int currentBattery = -1;

            @Override // com.miui.powerkeeper.PowerKeeperInterface.m
            public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
                int i8;
                if (i4 > 50) {
                    i8 = 8;
                } else if (i4 > 20) {
                    i8 = 4;
                } else {
                    i8 = i4 > 10 ? 2 : 1;
                }
                if (i8 != this.currentBattery) {
                    this.currentBattery = i8;
                    PowerSaveModeHandler.this.obtainMessage(2, Integer.valueOf(i8)).sendToTarget();
                }
            }
        };
        this.mContext = context;
        prepareCpuInfo();
        b.a.p().t(b.b.V, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.PowerSaveModeHandler.1
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                try {
                    Log.i(PowerSaveModeHandler.TAG, "Cloud-Observer onChange - power mode object is " + jSONObject);
                    if (jSONObject != null) {
                        String string = jSONObject.toString();
                        String string2 = SimpleSettings.Misc.getString(context, PowerSaveModeHandler.CONTENT_KEY, "");
                        Log.i(PowerSaveModeHandler.TAG, "power_save_mode_new newCloud is " + string + ", oldCloud is " + string2);
                        if (string.isEmpty() || string.equals(string2)) {
                            return;
                        }
                        SimpleSettings.Misc.putString(context, PowerSaveModeHandler.CONTENT_KEY, string);
                        PowerSaveModeHandler powerSaveModeHandler = PowerSaveModeHandler.sInstance;
                        if (powerSaveModeHandler != null) {
                            powerSaveModeHandler.sendEmptyMessage(0);
                        }
                    }
                } catch (Exception unused) {
                    Slog.e(PowerSaveModeHandler.TAG, "Cloud-Observer onChange err");
                }
            }
        });
        onUpdateConfig();
        this.isSuperSaveMode = Settings.System.getInt(context.getContentResolver(), "power_supersave_mode_open", 0) == 1;
        enableState(PowerKeeperManager.getInstance().getEventsAggregator().getPowerMode());
        onPowerModeChange(this.isSuperSaveMode ? 4 : this.mPowerMode);
    }

    private void ifShutByCloud(boolean z2) {
        if (this.mFuncSwitch == z2) {
            return;
        }
        if (z2) {
            PowerKeeperManager.getInstance().getEventsAggregator().registerPowerModeListener(this);
            this.mSuperSaveModeObserver = new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.PowerSaveModeHandler.2
                @Override // android.database.ContentObserver
                public void onChange(boolean z3) {
                    super.onChange(z3);
                    PowerSaveModeHandler.this.obtainMessage(1, Settings.System.getInt(PowerSaveModeHandler.this.mContext.getContentResolver(), "power_supersave_mode_open", 0), 0).sendToTarget();
                }
            };
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("power_supersave_mode_open"), false, this.mSuperSaveModeObserver);
            if (this.mScenarioObserver == null) {
                this.mScenarioObserver = new ThermalScenarioObserver();
            }
        } else {
            PowerKeeperManager.getInstance().getEventsAggregator().unregisterPowerModeListener(this);
            this.mContext.getContentResolver().unregisterContentObserver(this.mSuperSaveModeObserver);
            this.mSuperSaveModeObserver = null;
            ThermalScenarioObserver thermalScenarioObserver = this.mScenarioObserver;
            if (thermalScenarioObserver != null) {
                thermalScenarioObserver.unregister();
                this.mScenarioObserver = null;
            }
        }
        this.mFuncSwitch = z2;
        Slog.d(TAG, "PowerSaveModeNew funcSwitch=" + z2);
    }

    public static synchronized PowerSaveModeHandler init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new PowerSaveModeHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        b.a aVarP;
        if (!e.f.j() || (aVarP = b.a.p()) == null || aVarP.s(b.b.V)) {
            return;
        }
        String strOptString = jSONObject.optString(CONTENT_KEY, "");
        String string = SimpleSettings.Misc.getString(context, CONTENT_KEY, "");
        if (DEBUG) {
            Log.d(TAG, "power_save_mode_new newCloud is " + strOptString + ", oldCloud is " + string);
        }
        if (strOptString.isEmpty() || strOptString.equals(string)) {
            return;
        }
        SimpleSettings.Misc.putString(context, CONTENT_KEY, strOptString);
        PowerSaveModeHandler powerSaveModeHandler = sInstance;
        if (powerSaveModeHandler != null) {
            powerSaveModeHandler.sendEmptyMessage(0);
        }
    }

    private void onPowerLevelChange(int i2, String str, String str2, int i3) {
        int i4;
        if (i2 == -1) {
            if (this.mPowerModelCtrl.intValue() == i2 || !FileUtil.writeToFile("/sys/class/thermal/power_save/powersave_mode", -1)) {
                return;
            }
            this.mPowerModelCtrl = -1;
            Slog.d(TAG, "power mode update " + this.mPowerModelCtrl);
            if (this.mPowerLevelCtrl.intValue() != -1) {
                this.mPowerLevelCtrl = -1;
                FileUtil.writeToFile("/sys/class/thermal/power_save/power_level", -1);
                Slog.d(TAG, "power level update " + this.mPowerLevelCtrl);
                return;
            }
            return;
        }
        Integer num = this.mPowerLevel;
        num.intValue();
        Integer orDefault = this.mScenarioPowerLevel.getOrDefault(str, num);
        orDefault.intValue();
        int iIntValue = this.mPkgPowerLevel.getOrDefault(str2, orDefault).intValue();
        if (iIntValue > 0 && i3 != -1 && !this.mBatteryPowerLevel.isEmpty() && this.mBatteryPowerLevel.containsKey(Integer.valueOf(i3))) {
            SparseIntArray sparseIntArray = this.mBatteryPowerLevel.get(Integer.valueOf(i3));
            for (int i5 = 0; i5 < sparseIntArray.size(); i5++) {
                int iKeyAt = sparseIntArray.keyAt(i5);
                int iValueAt = sparseIntArray.valueAt(i5);
                if (iIntValue >= iKeyAt) {
                    int i6 = (iIntValue / iKeyAt) % 10;
                    i4 = i6 != iValueAt ? (iIntValue - (i6 * iKeyAt)) + (iValueAt * iKeyAt) : iIntValue;
                } else {
                    i4 = iIntValue + (iValueAt * iKeyAt);
                }
                iIntValue = Math.max(iIntValue, i4);
            }
        }
        if (iIntValue == -1) {
            i2 = -1;
        }
        if (this.mPowerModelCtrl.intValue() != i2 && FileUtil.writeToFile("/sys/class/thermal/power_save/powersave_mode", i2)) {
            this.mPowerModelCtrl = Integer.valueOf(i2);
            Slog.d(TAG, "power mode update " + this.mPowerModelCtrl);
        }
        if (this.mPowerLevelCtrl.intValue() != iIntValue) {
            this.mPowerLevelCtrl = Integer.valueOf(iIntValue);
            FileUtil.writeToFile("/sys/class/thermal/power_save/power_level", iIntValue);
            Slog.d(TAG, "power level update " + this.mPowerLevelCtrl);
        }
    }

    /* JADX WARN: Code duplicated, block: B:16:0x0026  */
    private void onPowerModeChange(int i2) throws Throwable {
        int i3;
        String str;
        if (!this.mFuncSwitch) {
            i3 = -1;
        } else if (i2 == 3) {
            i3 = (Settings.Secure.getInt(this.mContext.getContentResolver(), "power_center_xiaoai_voice", 0) == 1 ? 10 : 0) + 1;
        } else if (i2 == 4) {
            i3 = 2;
        } else {
            i3 = -1;
        }
        if (this.mDefaultSocLimit != null && (str = this.mSocLimit) != null) {
            if (i3 != -1) {
                String orDefault = this.mPkgSocLimit.getOrDefault(this.mLastFg, this.mScenarioSocLimit.getOrDefault(this.mLastScenario, str));
                if (!orDefault.equals(this.mSocLimitCtrl)) {
                    this.mSocLimitCtrl = orDefault;
                    FileUtil.writeToFile("/sys/powersave/limit_buf", orDefault);
                }
            } else if (!"\"\"".equals(this.mSocLimitCtrl)) {
                this.mSocLimitCtrl = "\"\"";
                FileUtil.writeToFile("/sys/powersave/limit_buf", "\"\"");
            }
        }
        onPowerLevelChange(i3, this.mLastScenario, this.mLastFg, this.mLastBattery);
    }

    private void onUpdateConfig() {
        Log.d(TAG, "enter updateConfig");
        this.mPkgPowerLevel.clear();
        this.mScenarioPowerLevel.clear();
        this.mPkgSocLimit.clear();
        this.mScenarioSocLimit.clear();
        this.mBatteryPowerLevel.clear();
        String string = SimpleSettings.Misc.getString(this.mContext, CONTENT_KEY, "");
        if (string.isEmpty()) {
            this.mPowerLevel = 100111111;
            this.mSocLimit = this.mDefaultSocLimit;
            ifShutByCloud(true);
            Slog.d(TAG, "Cloud-date:power_save_mode_new null");
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            boolean zOptBoolean = jSONObject.optBoolean("funcSwitch", true);
            if (zOptBoolean) {
                this.mPowerLevel = Integer.valueOf(jSONObject.optInt("power_level", 100111111));
                this.mSocLimit = parseSocLimit(jSONObject.optString("limit_param", ""));
                parseWhiteConfig(jSONObject.optJSONObject("white"));
            }
            ifShutByCloud(zOptBoolean);
        } catch (Exception e2) {
            Log.e(TAG, "update config failed" + e2);
        }
    }

    private String parseSocLimit(String str) {
        List<Integer> list = this.mPolicyIndexes;
        if (list == null || list.isEmpty()) {
            return "\"\"";
        }
        int i2 = 0;
        if (str == null || str.isEmpty()) {
            if (this.mDefaultSocLimit == null) {
                this.mDefaultSocLimit = LogType.LOG_TYPE_CPU_NAME + this.mPolicyIndexes.get(0) + " 0";
                for (int i3 = 1; i3 < this.mPolicyIndexes.size(); i3++) {
                    this.mDefaultSocLimit += " cpu" + this.mPolicyIndexes.get(i3) + " 70";
                }
                if (DEBUG) {
                    Log.d(TAG, "mDefaultSocLimit init: " + this.mDefaultSocLimit);
                }
            }
            return this.mDefaultSocLimit;
        }
        String[] strArrSplit = str.trim().split(",");
        String str2 = "";
        while (true) {
            int i4 = i2 + 1;
            if (i4 >= strArrSplit.length) {
                return str2.trim();
            }
            try {
                str2 = str2 + LogType.LOG_TYPE_CPU_NAME + Integer.parseInt(strArrSplit[i2]) + " " + Integer.parseInt(strArrSplit[i4]) + " ";
                i2 += 2;
            } catch (NumberFormatException unused) {
                return "\"\"";
            }
        }
    }

    private void parseWhiteConfig(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("pkg");
                if (jSONObjectOptJSONObject != null) {
                    JSONArray jSONArray = jSONObjectOptJSONObject.getJSONArray("power_level");
                    if (jSONArray != null) {
                        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                            String[] strArrSplit = jSONArray.getString(i2).split("#", 2);
                            this.mPkgPowerLevel.put(strArrSplit[0], Integer.valueOf(strArrSplit.length == 2 ? Integer.parseInt(strArrSplit[1]) : -1));
                        }
                    }
                    JSONArray jSONArray2 = jSONObjectOptJSONObject.getJSONArray("limit_param");
                    if (jSONArray2 != null) {
                        for (int i3 = 0; i3 < jSONArray2.length(); i3++) {
                            String[] strArrSplit2 = jSONArray2.getString(i3).split("#", 2);
                            this.mPkgSocLimit.put(strArrSplit2[0], strArrSplit2.length == 2 ? parseSocLimit(strArrSplit2[1]) : "\"\"");
                        }
                    }
                }
            } catch (NumberFormatException | JSONException unused) {
            }
            try {
                JSONObject jSONObjectOptJSONObject2 = jSONObject.optJSONObject("scenario");
                if (jSONObjectOptJSONObject2 != null) {
                    JSONArray jSONArray3 = jSONObjectOptJSONObject2.getJSONArray("power_level");
                    if (jSONArray3 != null) {
                        for (int i4 = 0; i4 < jSONArray3.length(); i4++) {
                            String[] strArrSplit3 = jSONArray3.getString(i4).split("#", 2);
                            this.mScenarioPowerLevel.put(strArrSplit3[0], Integer.valueOf(strArrSplit3.length == 2 ? Integer.parseInt(strArrSplit3[1]) : -1));
                        }
                    }
                    JSONArray jSONArray4 = jSONObjectOptJSONObject2.getJSONArray("limit_param");
                    if (jSONArray4 != null) {
                        for (int i5 = 0; i5 < jSONArray4.length(); i5++) {
                            String[] strArrSplit4 = jSONArray4.getString(i5).split("#", 2);
                            this.mScenarioSocLimit.put(strArrSplit4[0].toLowerCase(), strArrSplit4.length == 2 ? parseSocLimit(strArrSplit4[1]) : "\"\"");
                        }
                    }
                }
            } catch (NumberFormatException | JSONException unused2) {
            }
            try {
                JSONObject jSONObject2 = jSONObject.getJSONObject("battery");
                if (jSONObject2 != null) {
                    for (String str : jSONObject2.keySet()) {
                        JSONArray jSONArray5 = jSONObject2.getJSONArray(str);
                        SparseIntArray sparseIntArray = new SparseIntArray();
                        for (int i6 = 0; i6 < jSONArray5.length(); i6++) {
                            String[] strArrSplit5 = jSONArray5.getString(i6).split("#");
                            sparseIntArray.put(Integer.parseInt(strArrSplit5[0]), strArrSplit5.length == 2 ? Integer.parseInt(strArrSplit5[1]) % 10 : 0);
                        }
                        this.mBatteryPowerLevel.put(Integer.valueOf(Integer.parseInt(str, 2)), sparseIntArray);
                    }
                }
            } catch (NumberFormatException | JSONException unused3) {
            }
        }
        for (String str2 : this.DEFAULT_WHITE_SCENARIO_LIST) {
            if (!this.mScenarioSocLimit.containsKey(str2)) {
                this.mScenarioSocLimit.put(str2, "\"\"");
            }
        }
    }

    private void prepareCpuInfo() throws Throwable {
        if (!new File("/sys/powersave/limit_buf").exists()) {
            this.mPolicyIndexes = null;
            this.mDefaultSocLimit = null;
            return;
        }
        File file = new File("/sys/devices/system/cpu/cpufreq/");
        if (!file.exists()) {
            this.mPolicyIndexes = null;
            return;
        }
        String[] list = file.list();
        ArrayList arrayList = new ArrayList();
        int length = 0;
        for (String str : list) {
            if (str != null && str.startsWith(PowerCheckerCloudConfigure.POLICY_ITEM)) {
                try {
                    arrayList.add(Integer.valueOf(Integer.parseInt(str.substring(6))));
                    String fromFile = FileUtil.readFromFile("/sys/devices/system/cpu/cpufreq/" + str + "/affected_cpus");
                    if (fromFile != null) {
                        length += fromFile.trim().split("\\s+").length;
                    }
                } catch (NumberFormatException unused) {
                    Log.d(TAG, "parse policy NumberFormatException");
                }
            }
        }
        if (length <= 6 || arrayList.isEmpty()) {
            this.mPolicyIndexes = null;
            this.mDefaultSocLimit = null;
            return;
        }
        int i2 = length - 1;
        if (((Integer) Collections.max(arrayList)).intValue() < i2) {
            arrayList.add(Integer.valueOf(i2));
        }
        Collections.sort(arrayList, Collections.reverseOrder());
        this.mPolicyIndexes = arrayList;
        parseSocLimit(null);
    }

    public void dump(FileDescriptor fileDescriptor, final PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump PowerSaveModeNew enable:" + this.mFuncSwitch);
        if (this.mFuncSwitch) {
            printWriter.println(" powerLevel:" + this.mPowerLevel);
            if (DEBUG) {
                printWriter.println(" " + this.mPowerModelCtrl + "|" + this.mPowerLevelCtrl + "|" + this.mLastScenario + "|" + this.mLastFg + "|" + this.mLastBattery + "|" + this.mMonitorState);
                List<Integer> list = this.mPolicyIndexes;
                if (list != null) {
                    list.forEach(new Consumer() { // from class: com.miui.powerkeeper.statemachine.x
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            printWriter.print((Integer) obj);
                        }
                    });
                }
            }
        }
    }

    void enableState(int i2) {
        boolean z2 = i2 == 3 || i2 == 4;
        this.mPowerMode = i2;
        int size = this.mPkgPowerLevel.size() + this.mPkgSocLimit.size();
        int size2 = this.mScenarioPowerLevel.size() + this.mScenarioSocLimit.size();
        if (!z2 || (size == 0 && (this.mMonitorState & 1) != 0)) {
            PowerKeeperManager.getInstance().getEventsAggregator().unregisterForForegroundInfo(this);
            this.mLastFg = "";
            this.mMonitorState &= -2;
        } else if (size > 0 && (this.mMonitorState & 1) == 0) {
            ForegroundInfo appInfo = PowerKeeperManager.getInstance().getProcessObserver().getAppInfo();
            this.mLastFg = appInfo == null ? "" : appInfo.mForegroundPackageName;
            PowerKeeperManager.getInstance().getEventsAggregator().registerForForegroundInfo(this);
            this.mMonitorState |= 1;
        }
        if (!z2 || (size2 == 0 && (this.mMonitorState & 2) != 0)) {
            PowerKeeperManager.getInstance().getEventsAggregator().unregisterScenarioListener(this);
            this.mLastScenario = "";
            this.mMonitorState &= -3;
        } else if (size2 > 0 && (this.mMonitorState & 2) == 0) {
            PowerKeeperManager.getInstance().getEventsAggregator().registerScenarioListener(this);
            int currentScenarioName = ScenarioManager.getInstance().getCurrentScenarioName();
            ThermalScenarioObserver thermalScenarioObserver = this.mScenarioObserver;
            this.mLastScenario = thermalScenarioObserver != null ? thermalScenarioObserver.getCurrentName(currentScenarioName).toLowerCase() : "";
            this.mMonitorState |= 2;
        }
        if (!z2 || (this.mBatteryPowerLevel.isEmpty() && (this.mMonitorState & 4) != 0)) {
            if (this.mBatteryCallback != null) {
                PowerKeeperManager.getInstance().unregisterBatteryChangedListener(this.mBatteryCallback);
                this.mBatteryCallback = null;
            }
            this.mMonitorState &= -5;
            return;
        }
        if (this.mBatteryPowerLevel.isEmpty() || (this.mMonitorState & 4) != 0) {
            return;
        }
        this.mBatteryCallback = new PowerKeeperInterface.d(this, this.mBatteryStatusReceiver);
        PowerKeeperManager.getInstance().registerBatteryChangedListener(this.mBatteryCallback);
        this.mMonitorState |= 4;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        int i2 = message.what;
        if (i2 == 0) {
            onUpdateConfig();
            this.isSuperSaveMode = Settings.System.getInt(this.mContext.getContentResolver(), "power_supersave_mode_open", 0) == 1;
            int powerMode = this.mFuncSwitch ? PowerKeeperManager.getInstance().getEventsAggregator().getPowerMode() : 2;
            enableState(powerMode);
            onPowerModeChange(powerMode);
            return;
        }
        if (i2 == -32) {
            int iIntValue = ((Integer) message.obj).intValue();
            ThermalScenarioObserver thermalScenarioObserver = this.mScenarioObserver;
            if (thermalScenarioObserver != null) {
                String lowerCase = thermalScenarioObserver.getCurrentName(iIntValue).toLowerCase();
                String str = this.mLastScenario;
                if (str != null && str.equals(lowerCase)) {
                    return;
                } else {
                    this.mLastScenario = lowerCase;
                }
            }
        } else if (i2 == -22) {
            enableState(message.arg1);
        } else if (i2 == -9) {
            ForegroundInfo foregroundInfo = (ForegroundInfo) message.obj;
            if (foregroundInfo != null) {
                String str2 = foregroundInfo.mForegroundPackageName;
                String str3 = this.mLastFg;
                if (str3 != null && str3.equals(str2)) {
                    return;
                } else {
                    this.mLastFg = str2;
                }
            }
        } else if (i2 == 1) {
            this.isSuperSaveMode = message.arg1 == 1;
        } else if (i2 == 2) {
            this.mLastBattery = ((Integer) message.obj).intValue();
        } else if (i2 == 3) {
            ThermalScenarioObserver thermalScenarioObserver2 = this.mScenarioObserver;
            if (thermalScenarioObserver2 != null) {
                thermalScenarioObserver2.lambda$onNewScenarioData$0((SettingDTO) message.obj);
            }
            if (this.mScenarioPowerLevel.isEmpty()) {
                return;
            }
        }
        onPowerModeChange(this.isSuperSaveMode ? 4 : this.mPowerMode);
    }

    public void onDestroy() throws Throwable {
        if (this.mFuncSwitch) {
            ifShutByCloud(false);
            enableState(2);
            onPowerModeChange(2);
        }
    }
}
