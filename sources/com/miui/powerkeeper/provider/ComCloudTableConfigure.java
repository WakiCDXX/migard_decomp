package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class ComCloudTableConfigure {
    public static final String TABLE = "ComCloudTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String FEATURE_NAME = "fea_name";
        public static final String FEATURE_STATE = "fea_state";
        public static final String FEATURE_VALUE = "fea_value";
        public static final String ID = "_id";
    }

    public interface Method {
        public static final String METHOD_DELETE = "ComCloudTabledelete";
        public static final String METHOD_INSERT = "ComCloudTableinsert";
        public static final String METHOD_OVERRIDE = "ComCloudTableoverride";
        public static final String METHOD_QUERY = "ComCloudTablequery";
        public static final String METHOD_UPDATE = "ComCloudTableupdate";
    }
}
