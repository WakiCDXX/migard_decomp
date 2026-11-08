package com.miui.powerkeeper.provider.ThermalDB;

import android.net.Uri;
import com.miui.powerkeeper.provider.PowerKeeperConfigure;

/* JADX INFO: loaded from: classes.dex */
public class ThermalInfo {
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ThermalInfo ( _id INTEGER PRIMARY KEY,info TEXT, time INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE,extra TEXT, type TEXT );";
    public static final String TABLE = "ThermalInfo";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, TABLE);

    public static class Columns {
        public static final String EXTRA = "extra";
        public static final String ID = "_id";
        public static final String INFO = "info";
        public static final String TIME = "time";
        public static final String TYPE = "type";

        private Columns() {
        }
    }

    private ThermalInfo() {
    }
}
