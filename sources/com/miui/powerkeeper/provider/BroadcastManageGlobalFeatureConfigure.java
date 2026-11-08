package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class BroadcastManageGlobalFeatureConfigure {
    public static final String BC_DEFAULT = "false";
    public static final String BC_DISABLE = "false";
    public static final String BC_ENABLE = "true";
    public static final String BROADCAST_DELAY = "b_delay";
    public static final String BROADCAST_STATUS = "g_broadcast_status";
    public static final String LAST_UPDATED = "lastUpdated";
    public static final String TABLE = "BCGlobalFeatureTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String CONFIGURE_NAME = "configureName";
        public static final String CONFIGURE_PARAM = "configureParam";
        public static final String ID = "_id";
    }

    public interface Method {
        public static final String METHOD_QUERY = "BCGlobalFeatureTablequery";
        public static final String METHOD_UPDATE = "BCGlobalFeatureTableupdate";
    }
}
