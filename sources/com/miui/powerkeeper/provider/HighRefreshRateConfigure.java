package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class HighRefreshRateConfigure {
    public static final String TABLE = "highRefreshRateTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String ID = "_id";
        public static final String PACKAGE_NAME = "package_name";
    }
}
