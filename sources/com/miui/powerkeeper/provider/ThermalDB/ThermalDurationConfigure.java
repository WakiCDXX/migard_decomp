package com.miui.powerkeeper.provider.ThermalDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import com.miui.powerkeeper.provider.PowerKeeperConfigure;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class ThermalDurationConfigure {
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, "thermal_duration");
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS thermal_duration ( _id INTEGER PRIMARY KEY AUTOINCREMENT, column0 INTEGER, column1 INTEGER, column2 INTEGER, column3 TEXT, column4 REAL );";
    public static final String TABLE = "thermal_duration";
    private long mDate;
    private long mDuration;
    private boolean mInterrupt;
    private int mRange;
    private String mScenario;
    private String mVersion;

    public class Columns {
        public static final String DATE = "column0";
        public static final String DURATION = "column2";
        public static final String ID = "_id";
        public static final String INTERRUPT = "column3";
        public static final String RANGE = "column1";
        public static final String SCENARIO = "column5";
        public static final String VERSION = "column4";

        public Columns() {
        }
    }

    public ThermalDurationConfigure() {
    }

    public static void cleanStaleData(final Context context, final Calendar calendar) {
        try {
            Cursor cursorQuery = context.getContentResolver().query(CONTENT_URI, null, null, null);
            HashSet hashSet = new HashSet();
            while (cursorQuery.moveToNext()) {
                hashSet.add(Long.valueOf(cursorQuery.getLong(1)));
            }
            final Calendar calendar2 = Calendar.getInstance();
            hashSet.stream().filter(new Predicate() { // from class: com.miui.powerkeeper.provider.ThermalDB.a
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ThermalDurationConfigure.lambda$cleanStaleData$0(calendar2, calendar, (Long) obj);
                }
            }).forEach(new Consumer() { // from class: com.miui.powerkeeper.provider.ThermalDB.b
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ThermalDurationConfigure.lambda$cleanStaleData$1(context, (Long) obj);
                }
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static List<ThermalDurationConfigure> getList(Context context) {
        ArrayList arrayList = new ArrayList();
        try {
            Cursor cursorQuery = context.getContentResolver().query(CONTENT_URI, null, null, null);
            while (cursorQuery.moveToNext()) {
                ThermalDurationConfigure thermalDurationConfigure = new ThermalDurationConfigure();
                thermalDurationConfigure.mDate = cursorQuery.getLong(1);
                thermalDurationConfigure.mRange = cursorQuery.getInt(2);
                thermalDurationConfigure.mDuration = cursorQuery.getInt(3);
                thermalDurationConfigure.mInterrupt = Boolean.parseBoolean(cursorQuery.getString(4));
                thermalDurationConfigure.mVersion = cursorQuery.getString(5);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$cleanStaleData$0(Calendar calendar, Calendar calendar2, Long l2) {
        calendar.setTimeInMillis(l2.longValue());
        return calendar.before(calendar2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$cleanStaleData$1(Context context, Long l2) {
        context.getContentResolver().delete(CONTENT_URI, DatabaseUtils.concatenateWhere("column0 = '" + l2 + "'", null), null);
    }

    public long getDate() {
        return this.mDate;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public boolean getInterrupt() {
        return this.mInterrupt;
    }

    public int getRange() {
        return this.mRange;
    }

    public String getScenario() {
        return this.mScenario;
    }

    public String getVersion() {
        return this.mVersion;
    }

    public void insertToDB(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.DATE, Long.valueOf(this.mDate));
        contentValues.put(Columns.RANGE, Integer.valueOf(this.mRange));
        contentValues.put(Columns.DURATION, Long.valueOf(this.mDuration));
        contentValues.put(Columns.INTERRUPT, Boolean.valueOf(this.mInterrupt));
        contentValues.put(Columns.VERSION, this.mVersion);
        contentValues.put(Columns.SCENARIO, this.mScenario);
        context.getContentResolver().insert(CONTENT_URI, contentValues);
    }

    public ThermalDurationConfigure(long j2, int i2, long j3, boolean z2, float f2, String str) {
        this.mDate = j2;
        this.mRange = i2;
        this.mDuration = j3;
        this.mInterrupt = z2;
        this.mVersion = String.valueOf(f2);
        this.mScenario = str;
    }
}
