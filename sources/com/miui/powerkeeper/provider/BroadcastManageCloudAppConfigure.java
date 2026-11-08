package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class BroadcastManageCloudAppConfigure {
    public static final int BG_DELAY_DEFAULT = -2;
    public static final String TABLE = "BcCloudAppTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String BG_BROADCAST_IDS = "bgBroadcastIds";
        public static final String BG_DELAY = "b_delay";
        public static final String BG_EXCEPT_PKGS = "except_pkg";
        public static final String ID = "_id";
        public static final String PACKAGE_NAME = "pkgName";
    }

    public interface Method {
        public static final String METHOD_DELETE = "BcCloudAppTabledelete";
        public static final String METHOD_INSERT = "BcCloudAppTableinsert";
        public static final String METHOD_OVERRIDE = "BcCloudAppTableoverride";
        public static final String METHOD_QUERY = "BcCloudAppTablequery";
        public static final String METHOD_UPDATE = "BcCloudAppTableupdate";
    }
}
