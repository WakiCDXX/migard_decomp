package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class CustomerPowerCheckerConfigure {
    public static final String TABLE = "CustomerPowerRecordTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String DETAIL_NAME = "detail_name";
        public static final String ID = "_id";
        public static final String RECORD_TIME = "record_time";
        public static final String TYPE = "type";
        public static final String TYPE_NAME = "type_name";
    }
}
