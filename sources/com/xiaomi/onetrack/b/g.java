package com.xiaomi.onetrack.b;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class g extends SQLiteOpenHelper {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f1192a = "one_track_cloud";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f1193b = "events_cloud";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final String f1194c = "_id";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final String f1195d = "app_id";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final String f1196e = "cloud_data";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final String f1197f = "data_hash";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final String f1198g = "timestamp";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    static final String f1199h = "CREATE TABLE events_cloud (_id  INTEGER PRIMARY KEY AUTOINCREMENT,app_id TEXT,cloud_data TEXT,timestamp INTEGER,data_hash TEXT)";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static final String f1200i = "ConfigDatabaseHelper";

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final int f1201j = 1;

    public g(Context context) {
        super(context, f1192a, (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(f1199h);
        Log.d(f1200i, "onCreate: ");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
    }
}
