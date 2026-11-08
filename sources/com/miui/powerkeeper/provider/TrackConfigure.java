package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class TrackConfigure {
    public static final String TABLE = "TrackTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String ACTION = "action";
        public static final String CONFIGKEY = "config_key";
        public static final String EXTRA = "extra";
        public static final String ID = "_id";
        public static final String TIMEID = "timeId";
    }

    public static Uri getContentIdUri(long j2) {
        return Uri.withAppendedPath(CONTENT_URI, String.valueOf(j2));
    }
}
