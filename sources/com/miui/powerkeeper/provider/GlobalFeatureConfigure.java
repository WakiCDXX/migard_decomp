package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class GlobalFeatureConfigure {
    public static final String ALARM_ALIGN = "alarmAlign";
    public static final String APP_IDLE_DEFAULT = "true";
    public static final String APP_IDLE_DISABLE = "false";
    public static final String APP_IDLE_ENABLE = "true";
    public static final String AUROGON_ENABLE = "aurogon_enable";
    public static final String BG_CLUSTER_POLICY = "c_policy";
    public static final String BG_DATA = "bgData";
    public static final String BG_DATA_DEFAULT = "true";
    public static final String BG_DATA_DELAY_COUNT = "bgDataDelayCount";
    public static final String BG_DATA_DELAY_COUNT_DEFAULT = "-1";
    public static final String BG_DATA_DELAY_TIME = "bgDataDelayTime";
    public static final String BG_DATA_DELAY_TIME_DEFAULT = "3";
    public static final String BG_DATA_DISABLE = "false";
    public static final String BG_DATA_DISABLE_LONG_TIME = "bgDataDisableLongTime";
    public static final String BG_DATA_DISABLE_LONG_TIME_DEFAULT = "10";
    public static final String BG_DATA_DISABLE_SHORT_TIME = "bgDataDisableShortTime";
    public static final String BG_DATA_DISABLE_SHORT_TIME_DEFAULT = "3";
    public static final String BG_DATA_ENABLE = "true";
    public static final String BG_DATA_MAX_INACTIVE_COUNT = "bgDataMaxInactiveCount";
    public static final String BG_DATA_MAX_INACTIVE_COUNT_DEFAULT = "-1";
    public static final String BG_DATA_MIN_DATA_KB = "bgDataMinDataKb";
    public static final String BG_DATA_MIN_DATA_KB_DEFAULT = "-1";
    public static final String BG_IDLE_DEFAULT = "false";
    public static final String BG_IDLE_DELAY = "i_delay";
    public static final String BG_IDLE_DISABLE = "false";
    public static final String BG_IDLE_ENABLE = "true";
    public static final String BG_IDLE_STATUS = "appIdleStatus";
    public static final String BG_KILL_DELAY = "k_delay";
    public static final String BG_KILL_DELAY_HOT = "k_delay_hot";
    public static final String BG_KILL_POLICY = "k_policy";
    public static final String BG_LOCATION = "bgLocation";
    public static final String BG_LOCATION_DEFAULT = "true";
    public static final String BG_LOCATION_DELAY_HOT = "l_delay_hot";
    public static final String BG_LOCATION_DELAY_TIME = "bgLocationDelayTime";
    public static final String BG_LOCATION_DELAY_TIME_DEFAULT = "-1";
    public static final String BG_LOCATION_DELAY_WITH_NETWORK = "-1";
    public static final String BG_LOCATION_DISABLE = "false";
    public static final String BG_LOCATION_DISABLE_SHORT_TIME = "bgLocationDisableShortTime";
    public static final String BG_LOCATION_DISABLE_SHORT_TIME_DEFAULT = "3";
    public static final String BG_LOCATION_ENABLE = "true";
    public static final String BLE_SCAN_BLOCK_DEFAULT = "false";
    public static final String BLE_SCAN_BLOCK_DISABLE = "false";
    public static final String BLE_SCAN_BLOCK_ENABLE = "true";
    public static final String BLE_SCAN_BLOCK_PARAM = "bleScanParam";
    public static final String BLE_SCAN_BLOCK_STATUS = "bleScanBlock";
    public static final String BRIGHT_FROZEN = "bright_frozen";
    public static final String CLUSTER_DEFAULT = "false";
    public static final String CLUSTER_DISABLE = "false";
    public static final String CLUSTER_ENABLE = "true";
    public static final String CLUSTER_STATUS = "ClusterStatus";
    public static final String DEVICEIDLE_DEFAULT = "false";
    public static final String DEVICEIDLE_DISABLE = "false";
    public static final String DEVICEIDLE_ENABLE = "true";
    public static final String DEVICEIDLE_STATUS = "deviceidleStatus";
    public static final String DOZE_WHITELIST_APPS = "dozeWhiteListApps";
    public static final String FEATURE_DEFAULT = "false";
    public static final String FEATURE_DISABLE = "false";
    public static final String FEATURE_ENABLE = "true";
    public static final String FEATURE_STATUS = "featureStatus";
    public static final String FROZEN_DEFAULT = "false";
    public static final String FROZEN_DISABLE = "false";
    public static final String FROZEN_ENABLE = "true";
    public static final String FROZEN_NEW_STATUS = "FrozenControlNewStatus";
    public static final String FROZEN_NEW_WHITELIST = "FrozenNewWhiteList";
    public static final String FROZEN_STATUS = "FrozenControlStatus";
    public static final String HOT_FEEDBACK_FEATURE = "hotFeedbackFeature";
    public static final String HOT_FEEDBACK_FEATURE_DISABLE = "false";
    public static final String HOT_FEEDBACK_FEATURE_ENABLE = "true";
    public static final String KILL_CONFIGS = "killConfigs";
    public static final String LAST_UPDATED = "lastUpdated";
    public static final String LAUNCH_RESTRICT = "launchRestrict";
    public static final String LEVEL_ULTIMATE_SPECIAL_APPS = "levelUtimateSpecialApps";
    public static final String LIGHTIDLE_DEFAULT = "false";
    public static final String LIGHTIDLE_DISABLE = "false";
    public static final String LIGHTIDLE_ENABLE = "true";
    public static final String LIGHTIDLE_STATUE = "lightIdleStatus";

    @Deprecated
    public static final String MIUI_IDLE_STATUS = "miui_idle";
    public static final String MIUI_STANDBY_STATUS = "miui_standby";
    public static final String MUSIC_APPS = "musicApps";
    public static final String NETWORK_FEEDBACK_FEATURE = "networkFeedbackFeature";
    public static final String NETWORK_FEEDBACK_FEATURE_DISABLE = "false";
    public static final String NETWORK_FEEDBACK_FEATURE_ENABLE = "true";
    public static final String NO_CORE_SYSTEM_APPS = "noCoreSystemApps";
    public static final String SCREENOFF_MILLET_MODE = "screenoff_millet_mode";
    public static final String SENSORS_STATUS = "SensorControlStatus";
    public static final String SENSOR_DEFAULT = "false";
    public static final String SENSOR_DELAY = "s_delay";
    public static final String SENSOR_DISABLE = "false";
    public static final String SENSOR_DISABLE_TYPE = "s_disable_type";
    public static final String SENSOR_ENABLE = "true";
    public static final String STANDBY_CHAIN_DELAY = "standbyChainDelay";
    public static final String USER_CONFIGURE_DEFAULT = "enhance";
    public static final String USER_CONFIGURE_DISABLE = "disable";
    public static final String USER_CONFIGURE_LEVEL_ENHANCE = "enhance";

    @Deprecated
    public static final String USER_CONFIGURE_LEVEL_NORMAL = "normal";
    public static final String USER_CONFIGURE_LEVEL_PERFORMANCE = "performance";
    public static final String USER_CONFIGURE_LEVEL_SLEEP = "sleep";
    public static final String USER_CONFIGURE_LEVEL_ULTIMATE = "ultimate";
    public static final String USER_CONFIGURE_LEVEL_ULTIMATE_EXTRA = "ultimate_extra";
    public static final String USER_CONFIGURE_STATUS = "userConfigureStatus";
    public static final String USER_ID = "userId";
    public static final String TABLE = "GlobalFeatureTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String CONFIGURE_NAME = "configureName";
        public static final String CONFIGURE_PARAM = "configureParam";
        public static final String ID = "_id";
        public static final String USER_ID = "userId";
    }

    public interface Method {
        public static final String METHOD_DELETE = "GlobalFeatureTabledelete";
        public static final String METHOD_INSERT = "GlobalFeatureTableinsert";
        public static final String METHOD_QUERY = "GlobalFeatureTablequery";
        public static final String METHOD_UPDATE = "GlobalFeatureTableupdate";
    }
}
