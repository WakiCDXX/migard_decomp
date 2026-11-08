package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class CloudAppConfigure {
    public static final String BG_DATA_DISABLE = "false";
    public static final String BG_DATA_ENABLE = "true";
    public static final int BG_IDLE_DELAY_DEFAULT = -2;
    public static final int BG_KILL_CONFIG_DEFAULT = 896;
    public static final int BG_KILL_DELAY_DEFAULT = -2;
    public static final int BG_LOCATION_DELAY_DEFAULT = -2;
    public static final int BG_LOCATION_DELAY_WITH_NETWORK = -1;
    public static final String BG_LOCATION_DISABLE = "false";
    public static final String BG_LOCATION_ENABLE = "true";
    public static final int BG_SENSOR_DELAY_DEFAULT = -2;
    public static final String TABLE = "cloudAppTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String BG_CLUSTER_POLICY = "c_policy";
        public static final String BG_DATA = "bgData";
        public static final String BG_DATA_DELAY_COUNT = "bgDataDelayCount";
        public static final String BG_DATA_DELAY_TIME = "bgDataDelayTime";
        public static final String BG_DATA_MAX_INACTIVE_COUNT = "bgDataMaxInactiveCount";
        public static final String BG_DATA_MIN_DATA_KB = "bgDataMinDataKb";
        public static final String BG_IDLE_DELAY = "i_delay";
        public static final String BG_KILL_DELAY = "k_delay";
        public static final String BG_KILL_DELAY_HOT = "k_delay_hot";
        public static final String BG_KILL_POLICY = "k_policy";
        public static final String BG_LOCATION = "bgLocation";
        public static final String BG_LOCATION_DELAY_HOT = "l_delay_hot";
        public static final String BG_LOCATION_DELAY_TIME = "bgLocationDelayTime";
        public static final String BG_SENSOR_DELAY = "s_delay";
        public static final String BG_SENSOR_DISABLE_TYPE = "s_disable_type";
        public static final String ID = "_id";
        public static final String PACKAGE_NAME = "pkgName";
        public static final String POWER_STATE_ID = "power_state_id";
    }

    public interface Method {
        public static final String METHOD_OVERRIDE = "cloudAppTableoverride";
    }
}
