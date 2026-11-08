package com.xiaomi.push.providers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
public class TrafficDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "traffic.db";
    private static int DATABASE_VERSION = 1;
    public static final String TRAFFIC_TABLE = "traffic";
    public static final Object DataBaseLock = new Object();
    private static final String ZERO_BASED_LONG = " LONG DEFAULT 0 ";
    private static final String ZERO_BASED_INTEGER = " INT DEFAULT -1 ";
    private static final String[] TRAFFIC_Columns = {"package_name", "TEXT", TrafficProvider.TrafficColumns.MESSAGE_TS, ZERO_BASED_LONG, TrafficProvider.TrafficColumns.BYTES, ZERO_BASED_LONG, TrafficProvider.TrafficColumns.NETWORK_TYPE, ZERO_BASED_INTEGER, TrafficProvider.TrafficColumns.RCV, ZERO_BASED_INTEGER, "imsi", "TEXT"};

    public TrafficDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
    }

    private void createTrafficTable(SQLiteDatabase sQLiteDatabase) {
        StringBuilder sb = new StringBuilder("CREATE TABLE traffic(_id INTEGER  PRIMARY KEY ,");
        int i2 = 0;
        while (true) {
            String[] strArr = TRAFFIC_Columns;
            if (i2 >= strArr.length - 1) {
                sb.append(");");
                sQLiteDatabase.execSQL(sb.toString());
                return;
            }
            if (i2 != 0) {
                sb.append(",");
            }
            sb.append(strArr[i2]);
            sb.append(" ");
            sb.append(strArr[i2 + 1]);
            i2 += 2;
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        synchronized (DataBaseLock) {
            try {
                createTrafficTable(sQLiteDatabase);
            } catch (SQLException e2) {
                MyLog.e(e2);
            }
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
    }
}
