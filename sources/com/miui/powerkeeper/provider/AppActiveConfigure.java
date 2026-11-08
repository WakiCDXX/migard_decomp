package com.miui.powerkeeper.provider;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class AppActiveConfigure {
    public static final String ACTIVE_DEFAULT = "true";
    public static final String ACTIVE_FALSE = "false";
    public static final String ACTIVE_TRUE = "true";
    public static final int PROPERTY_IME = 8;
    public static final int PROPERTY_NAVIGATING = 16;
    public static final int PROPERTY_NETWORK = 32;
    public static final int PROPERTY_SYSTEM = 1;
    public static final int PROPERTY_USER_NO_RESTRICT = 2;
    public static final int PROPERTY_VPN = 4;
    public static final String TABLE = "appActiveTable";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public interface Columns {
        public static final String ACTIVE = "active";
        public static final String ID = "_id";
        public static final String PROPERTY = "property";
        public static final String UID = "uid";
    }
}
