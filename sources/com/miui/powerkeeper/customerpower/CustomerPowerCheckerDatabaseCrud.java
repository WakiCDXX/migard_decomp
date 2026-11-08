package com.miui.powerkeeper.customerpower;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.miui.powerkeeper.provider.CustomerPowerCheckerConfigure;
import com.miui.powerkeeper.provider.CustomerPowerCheckerDatabaseHelper;
import com.miui.powerkeeper.utils.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class CustomerPowerCheckerDatabaseCrud {
    static final String TAG = "CustomerPowerCheckerDatabaseCrud";
    private static CustomerPowerCheckerDatabaseCrud pcDatabaseCrud;
    private CustomerPowerCheckerDatabaseHelper helper;
    private int mCustomerPowerCheckOccurInterval = 3;

    private CustomerPowerCheckerDatabaseCrud(Context context) {
        this.helper = CustomerPowerCheckerDatabaseHelper.getInstance(context);
    }

    public static CustomerPowerCheckerDatabaseCrud getInstance(Context context) {
        if (pcDatabaseCrud == null) {
            pcDatabaseCrud = new CustomerPowerCheckerDatabaseCrud(context);
        }
        return pcDatabaseCrud;
    }

    public void copyDbToMaintenance(String str, String str2) {
        File file = new File(str);
        File file2 = new File(str2);
        FileUtil.copyFile(file, file2);
        if (file2.exists()) {
            file2.setReadable(true, false);
            file2.setWritable(true, false);
        }
        Log.d(TAG, "Copy database to maintenance mode");
    }

    public synchronized int deleteByDate(String str, int i2) {
        SQLiteDatabase writableDatabase = this.helper.getWritableDatabase();
        String str2 = " DELETE FROM " + str + " WHERE record_time < " + (System.currentTimeMillis() - (((long) i2) * 86400000));
        try {
            writableDatabase.execSQL(str2);
            writableDatabase.close();
            SQLiteDatabase sQLiteDatabaseOpenOrCreateDatabase = null;
            try {
                try {
                    if (new File("/data/local/maintenance_mode/customerpowerchecker.db").exists()) {
                        sQLiteDatabaseOpenOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase("/data/local/maintenance_mode/customerpowerchecker.db", (SQLiteDatabase.CursorFactory) null);
                        sQLiteDatabaseOpenOrCreateDatabase.execSQL(str2);
                    }
                    if (sQLiteDatabaseOpenOrCreateDatabase != null) {
                        sQLiteDatabaseOpenOrCreateDatabase.close();
                    }
                } catch (Exception unused) {
                    Log.d(TAG, "data/local/maintenance_mode/customerpowerchecker.db can not open");
                    if (sQLiteDatabaseOpenOrCreateDatabase != null) {
                    }
                }
            } catch (Throwable th) {
                if (sQLiteDatabaseOpenOrCreateDatabase != null) {
                    sQLiteDatabaseOpenOrCreateDatabase.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            throw th2;
        }
        return 0;
    }

    public ArrayList<CustomerPowerResultBean> filterQuery(ArrayList<CustomerPowerResultBean> arrayList) {
        String strQueryUtil;
        String strQueryUtil2;
        ArrayList<CustomerPowerResultBean> arrayList2 = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap map3 = new HashMap();
        HashMap map4 = new HashMap();
        HashMap map5 = new HashMap();
        HashMap map6 = new HashMap();
        for (CustomerPowerResultBean customerPowerResultBean : arrayList) {
            int type = customerPowerResultBean.getType();
            if (customerPowerResultBean.getDetailName() != null) {
                String[] strArrSplit = customerPowerResultBean.getDetailName().split(",");
                int i2 = 0;
                if (type != 11) {
                    if (type == 12 && strArrSplit.length > 0) {
                        int length = strArrSplit.length;
                        while (i2 < length) {
                            String str = strArrSplit[i2];
                            if (map2.containsKey(str)) {
                                map2.put(str, Integer.valueOf(map2.get(str).intValue() + 1));
                            } else {
                                map2.put(str, 1);
                            }
                            i2++;
                        }
                    }
                } else if (strArrSplit.length > 0) {
                    int length2 = strArrSplit.length;
                    while (i2 < length2) {
                        String str2 = strArrSplit[i2];
                        if (map.containsKey(str2)) {
                            map.put(str2, Integer.valueOf(map.get(str2).intValue() + 1));
                        } else {
                            map.put(str2, 1);
                        }
                        i2++;
                    }
                }
            } else if (type != 1) {
                if (type != 2) {
                    if (type != 5) {
                        if (type == 6) {
                            if (map5.containsKey("kernelWakeup")) {
                                map5.put("kernelWakeup", Integer.valueOf(((Integer) map5.get("kernelWakeup")).intValue() + 1));
                            } else {
                                map5.put("kernelWakeup", 1);
                            }
                        }
                    } else if (map4.containsKey("kernelWakelock")) {
                        map4.put("kernelWakelock", Integer.valueOf(((Integer) map4.get("kernelWakelock")).intValue() + 1));
                    } else {
                        map4.put("kernelWakelock", 1);
                    }
                } else if (map3.containsKey("highBrightness")) {
                    map3.put("highBrightness", Integer.valueOf(((Integer) map3.get("highBrightness")).intValue() + 1));
                } else {
                    map3.put("highBrightness", 1);
                }
            } else if (map6.containsKey("badSignal")) {
                map6.put("badSignal", Integer.valueOf(((Integer) map6.get("badSignal")).intValue() + 1));
            } else {
                map6.put("badSignal", 1);
            }
        }
        if (map.size() > 0 && (strQueryUtil2 = queryUtil(map, 11)) != "") {
            arrayList2.add(new CustomerPowerResultBean(11, CustomerPowerConstant.APP_BG_CPU, strQueryUtil2, 0L, 0));
        }
        if (map2.size() > 0 && (strQueryUtil = queryUtil(map2, 12)) != "") {
            arrayList2.add(new CustomerPowerResultBean(12, CustomerPowerConstant.APP_PARTIAL_WAKELOCK, strQueryUtil, 0L, 0));
        }
        if (map3.size() > 0 && ((Integer) map3.get("highBrightness")).intValue() >= this.mCustomerPowerCheckOccurInterval) {
            arrayList2.add(new CustomerPowerResultBean(2, CustomerPowerConstant.HIGH_BTIGHT, null, 0L, 0));
        }
        if (map4.size() > 0 && ((Integer) map4.get("kernelWakelock")).intValue() >= this.mCustomerPowerCheckOccurInterval) {
            arrayList2.add(new CustomerPowerResultBean(5, CustomerPowerConstant.KERNEL_WAKELOCK, null, 0L, 0));
        }
        if (map5.size() > 0 && ((Integer) map5.get("kernelWakeup")).intValue() >= this.mCustomerPowerCheckOccurInterval) {
            arrayList2.add(new CustomerPowerResultBean(6, CustomerPowerConstant.KERNEL_WAKEUP, null, 0L, 0));
        }
        if (map6.size() > 0 && ((Integer) map6.get("badSignal")).intValue() >= this.mCustomerPowerCheckOccurInterval) {
            arrayList2.add(new CustomerPowerResultBean(1, CustomerPowerConstant.BAD_SIGNAL, null, 0L, 0));
        }
        return arrayList2;
    }

    public synchronized long insert(String str, ContentValues contentValues) {
        long jInsert;
        SQLiteDatabase writableDatabase = this.helper.getWritableDatabase();
        SQLiteDatabase sQLiteDatabase = null;
        try {
            jInsert = writableDatabase.insert(str, null, contentValues);
            try {
                writableDatabase.close();
                try {
                    if (new File("/data/local/maintenance_mode/customerpowerchecker.db").exists()) {
                        SQLiteDatabase sQLiteDatabaseOpenOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase("/data/local/maintenance_mode/customerpowerchecker.db", (SQLiteDatabase.CursorFactory) null);
                        try {
                            sQLiteDatabaseOpenOrCreateDatabase.insert(str, null, contentValues);
                            sQLiteDatabase = sQLiteDatabaseOpenOrCreateDatabase;
                        } catch (Exception unused) {
                            sQLiteDatabase = sQLiteDatabaseOpenOrCreateDatabase;
                            Log.d(TAG, "data/local/maintenance_mode/customerpowerchecker.db can not open");
                            if (sQLiteDatabase != null) {
                            }
                            return jInsert;
                        } catch (Throwable th) {
                            th = th;
                            sQLiteDatabase = sQLiteDatabaseOpenOrCreateDatabase;
                            if (sQLiteDatabase != null) {
                                sQLiteDatabase.close();
                            }
                            throw th;
                        }
                    } else {
                        copyDbToMaintenance("/data/data/com.miui.powerkeeper/databases/customerpowerchecker.db", "/data/local/maintenance_mode/customerpowerchecker.db");
                    }
                    if (sQLiteDatabase != null) {
                        sQLiteDatabase.close();
                    }
                } catch (Exception unused2) {
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Throwable th3) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            throw th3;
        }
        return jInsert;
    }

    public ArrayList<CustomerPowerResultBean> query(String str, int i2) {
        new ArrayList();
        ArrayList<CustomerPowerResultBean> arrayList = new ArrayList<>();
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (i2 == 0) {
            return filterQuery(queryAllByWeek(str, jCurrentTimeMillis));
        }
        if (i2 != 1) {
            return arrayList;
        }
        for (int i3 = 0; i3 < 24; i3++) {
            arrayList = filterQuery(queryAllByWeek(str, jCurrentTimeMillis));
            if (arrayList.size() != 0) {
                return arrayList;
            }
            jCurrentTimeMillis -= 604800000;
        }
        return arrayList;
    }

    /* JADX WARN: Code duplicated, block: B:29:0x00c4  */
    /* JADX WARN: Code duplicated, block: B:31:0x00c9  */
    /* JADX WARN: Multi-variable type inference failed */
    public ArrayList<CustomerPowerResultBean> queryAllByWeek(String str, long j2) throws Throwable {
        SQLiteDatabase sQLiteDatabase;
        SQLiteDatabase writableDatabase;
        ArrayList<CustomerPowerResultBean> arrayList = new ArrayList<>();
        new ArrayList();
        String str2 = " SELECT * FROM " + str + " WHERE record_time >= " + (j2 - 604800000) + " and record_time <= " + j2;
        Cursor cursorRawQuery = null;
        try {
            try {
                if (!CustomerPowerUtil.isMaintenanceModeEnable()) {
                    writableDatabase = this.helper.getWritableDatabase();
                } else {
                    if (!new File("/data/local/maintenance_mode/customerpowerchecker.db").exists()) {
                        Log.d(TAG, "maintenance mode databases is not exists");
                        return new ArrayList<>();
                    }
                    writableDatabase = SQLiteDatabase.openOrCreateDatabase("/data/local/maintenance_mode/customerpowerchecker.db", (SQLiteDatabase.CursorFactory) null);
                }
                try {
                    cursorRawQuery = writableDatabase.rawQuery(str2, null);
                    while (cursorRawQuery.moveToNext()) {
                        arrayList.add(new CustomerPowerResultBean(cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("type")), cursorRawQuery.getString(cursorRawQuery.getColumnIndex(CustomerPowerCheckerConfigure.Columns.TYPE_NAME)), cursorRawQuery.getString(cursorRawQuery.getColumnIndex(CustomerPowerCheckerConfigure.Columns.DETAIL_NAME)), 0L, 0));
                    }
                    cursorRawQuery.close();
                } catch (Exception unused) {
                    Log.d(TAG, "customerpowerchecker.db can not open");
                    if (cursorRawQuery != null) {
                        cursorRawQuery.close();
                    }
                    if (writableDatabase != null) {
                    }
                    return arrayList;
                }
            } catch (Throwable th) {
                sQLiteDatabase = this;
                th = th;
                if (0 != 0) {
                    cursorRawQuery.close();
                }
                if (sQLiteDatabase != 0) {
                    sQLiteDatabase.close();
                }
                throw th;
            }
        } catch (Exception unused2) {
            writableDatabase = null;
        } catch (Throwable th2) {
            th = th2;
            sQLiteDatabase = 0;
            if (0 != 0) {
                cursorRawQuery.close();
            }
            if (sQLiteDatabase != 0) {
                sQLiteDatabase.close();
            }
            throw th;
        }
        writableDatabase.close();
        return arrayList;
    }

    public String queryUtil(HashMap<String, Integer> map, int i2) {
        String str;
        if (i2 == 11) {
            str = "";
            for (String str2 : map.keySet()) {
                if (map.get(str2).intValue() >= this.mCustomerPowerCheckOccurInterval) {
                    str = "".equals(str) ? str + str2 : str + "," + str2;
                }
            }
        } else {
            if (i2 != 12) {
                return "";
            }
            str = "";
            for (String str3 : map.keySet()) {
                if (map.get(str3).intValue() >= this.mCustomerPowerCheckOccurInterval) {
                    str = "".equals(str) ? str + str3 : str + "," + str3;
                }
            }
        }
        return str;
    }
}
