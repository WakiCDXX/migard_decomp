package com.miui.powerkeeper.thermalcollector.source;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import miui.provider.ExtraCalendarContracts;

/* JADX INFO: loaded from: classes.dex */
public class CollectorConfig {
    public static final String CLOUD_COMPOUND_SCENARIOS_BACKGROUND_INFO = "backgroundInfo";
    public static final String CLOUD_COMPOUND_SCENARIOS_CALL_INFO = "callInfo";
    public static final String CLOUD_COMPOUND_SCENARIOS_FLOATWINDOW_INFO = "floatwindowInfo";
    public static final String CLOUD_COMPOUND_SCENARIOS_FOREGROUND_INFO = "foregroundInfo";
    public static final String CLOUD_COMPOUND_SCENARIOS_SCREEN_INFO = "screenInfo";
    public static final String CLOUD_COMPOUND_SCENARIOS_UPLOAD_ID = "uploadId";
    public static final String CLOUD_COMPOUND_SCENARIOS_UPLOAD_SCENARIOS = "uploadScenarios";
    public static final String CLOUD_THERMAL_COLLECTOR = "thermal_collector";
    public static final String CLOUD_THERMAL_COLLECTOR_VERSION = "thermal_collector_version";
    public static final String CLOUD_THERMAL_COMPOUND_SCENARIOS = "thermal_collector_compound_scenarios";
    public static final int COLLECTION_TIME_LIMIT = 60;
    public static final String COLLECTOR_AMBIENT = "CollectorAmbient";
    public static final String COLLECTOR_AUDIO = "CollectorAudio";
    public static final String COLLECTOR_BLUETOOTH = "CollectorBluetooth";
    public static final String COLLECTOR_BOARD = "CollectorBoard";
    public static final String COLLECTOR_CAMERA_STATE = "CollectorCameraState";
    public static final String COLLECTOR_COMPOUND_SCENARIO = "CollectorCompoundScenario";
    public static final String COLLECTOR_DISPLAY = "CollectorDisplay";
    public static final String COLLECTOR_FLASH = "CollectorFlash";
    public static final String COLLECTOR_FOREGROUND_END = "CollectorForegroundEnd";
    public static final String COLLECTOR_FOREGROUND_START = "CollectorForegroundStart";
    public static final String COLLECTOR_HEAD = "CollectorHead";
    public static final String COLLECTOR_MICRO_PHONE_STATE = "CollectorMicroPhoneState";
    public static final String COLLECTOR_MISC = "CollectorMisc";
    public static final String COLLECTOR_NET = "CollectorNet";
    public static final String COLLECTOR_OLD_THERMAL = "CollectorOldThermal";
    public static final String COLLECTOR_POPUP_WINDOW = "CollectorPopUpWindow";
    public static final String COLLECTOR_SCENARIO = "CollectorScenario";
    public static final String COLLECTOR_SETTING = "CollectorSetting";
    public static final String COLLECTOR_THERMAL = "CollectorThermal";
    public static final String COLLECTOR_THERMAL_PLUS = "CollectorThermalPlus";
    public static final int COMPOUND_SCENARIOS_FIRST_VERIFY_FLAG = 1;
    public static final int COMPOUND_SCENARIOS_RESET_FLAG = 0;
    public static final int COMPOUND_SCENARIOS_SECOND_VERIFY_FLAG = 2;
    public static final int COMPOUND_SCENARIOS_THIRD_VERIFY_FLAG = 3;
    public static final String EVENT_APPS = "apps";
    public static final String EVENT_GEN_APPS_AMBIENT_TEMP = "genAppsAmbientTemp";
    public static final String EVENT_GEN_APPS_COMPOUND_SCENARIO = "genAppsCompoundScenario";
    public static final String EVENT_GEN_APPS_FLASH_TEMP = "genAppsFlashTemp";
    public static final String EVENT_GEN_APPS_POPUP_WINDOW = "genAppsPopupWindow";
    public static final String EVENT_TOTAL = "total";
    public static final String FLASH_STATE_PATH = "/sys/class/thermal/thermal_message/flash_state";
    public static final String FLASH_TEMP_FILE = "/sys/class/thermal/thermal_zone%d/temp";
    public static final String FLOAT_WINDOW_PACKAGE_NAME = "packageName";
    public static final String FLOAT_WINDOW_WECHAT_VIDEOCHAT = "wechatVideo";
    public static final String FLOAT_WINDOW_WECHAT_VOICECHAT = "wechatVoice";
    public static final String KEY_PROCESS_TRIGGER_ID = "process_trigger_id";
    public static final String KEY_PROCESS_TRIGGER_PARAMS = "process_trigger_params";
    public static final String RULE_APPS_FOREGROUND = "apps_foreground";
    public static final String RULE_GEN_APPS_AMBIENT = "genApps_ambient";
    public static final String RULE_GEN_APPS_BROAD = "genApps_broad";
    public static final String RULE_GEN_APPS_FLASH = "genApps_flash";
    public static final String RULE_GEN_APPS_FOREGROUND = "genApps_foreground";
    public static final String RULE_THERMAL_SCENARIO_TIME = "thermal_scenario_time";
    public static final String RULE_TOTAL_BATTERY = "total_battery";
    public static final String TRIGGER_AMBIENT_TEMP = "AmbientTempTrigger";
    public static final String TRIGGER_BATTERY = "BatteryTrigger";
    public static final String TRIGGER_BROAD_TEMP = "BroadTempTrigger";
    public static final String TRIGGER_FLASH_TEMP = "FlashTempTrigger";
    public static final String TRIGGER_FOREGROUND = "ForegroundTrigger";
    public static final String VALUE_PROCESS_AMBIENT_TRIGGER_ID = "1";
    public static final String VALUE_PROCESS_BOARD_TRIGGER_ID = "0";
    public static final String VALUE_PROCESS_CAMERA_TRIGGER_ID = "5";
    public static final String VALUE_PROCESS_COMPOUND_SCENARIO_TRIGGER_ID = "4";
    public static final String VALUE_PROCESS_FLASH_TRIGGER_ID = "2";
    public static final String VALUE_PROCESS_MICRO_PHONE_TRIGGER_ID = "6";
    public static final String VALUE_PROCESS_POPUP_WINDOW_TRIGGER_ID = "3";
    public static final String VALUE_PROCESS_UNDEFINED_COMPOUND_SCENARIO_TRIGGER_ID = "7";
    public static final String EVENT_GEN_APPS = "genApps";
    public static final String EVENT_THERMAL_SCENARIO = "thermal_scenario";
    public static final String[] DEFAULT_EVENTS = {EVENT_GEN_APPS, "apps", "total", EVENT_THERMAL_SCENARIO};
    public static final int[] COMPOUND_SCENARIO_IDS = {ExtraCalendarContracts.CALENDAR_ACCESS_LEVEL_LOCAL, 701, 702, 800, 801, 802};
    public static final Set<String> QCOM_NTC_NAMES = new HashSet(Arrays.asList("cpu-1-6", "cpu-1-7", "cpu-1-6-usr", "cpu-1-7-usr", "cpu-0-0-usr", "cpu-0-1-usr", "cpu-0-2-usr", "cpu-0-3-usr", "cpu-0-4-usr", "cpu-0-5-usr", "cpu-1-0-usr", "cpu-1-1-usr", "cpu-1-2-usr", "cpu-1-3-usr", "cam_therm0", "cam_therm1", "pa_therm0", "pa_therm1", "laser_therm0", "conn_therm", "slave_therm", "modem1_pa0", "modem1_pa1", "modem1_xo", "modem1_quiet", "modem1_wifi", "wireless_therm", "flash_therm", "charger_therm0", "charger_therm1", "wifi_therm", "battery", "cpu_therm", "xo_therm", "quiet_therm", "backlight_therm", "cpu-2-2-0", "cpu-2-2-1", "cpu-1-0-0", "cpu-1-1-0", "isp_therm", "tt_therm", "nsphvx-1"));
    public static final Set<String> MTK_NTC_NAMES = new HashSet(Arrays.asList("mtktsAP", "wifi_therm", "mtktsbtsmdpa", "mtktsbattery", "mtktscharger", "mtktsbtsnrpa", "mtktscpu", "mtktspa", "mtktspmic", "quiet_therm", "charger_therm", "flash_therm", "soc_max", "cpu_little1", "cpu_bigbig1", "cpu_big6", "cpu_bigbig2", "cpu_little2", "cpu_little3", "cpu_little4", "cpu_big1", "battery", "cpu_big2", "cpu_big3", "cpu_big4", "cpu_big5", "cpu-big-core7-1", "cpu-big-core7-2"));
    public static final Gson sGson = new Gson();

    private CollectorConfig() {
    }
}
