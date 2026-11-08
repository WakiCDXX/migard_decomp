package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerConfigure {
    public static final String TABLE = "abnormalTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String ACTION = "paction";
        public static final String FLAG = "flag";
        public static final String ID = "_id";
        public static final String PACKAGE_NAME = "package_name";
        public static final String PRIORITY = "priority";
        public static final String RECORD_TIME = "record_time";
        public static final String TYPE = "type";
        public static final String UID = "uid";
        public static final String VERSION = "package_version";
    }
}
