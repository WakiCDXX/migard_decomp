package com.miui.powerkeeper.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.android.collect.Lists;
import com.google.android.collect.Sets;
import com.miui.powerkeeper.millet.MilletConfig;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class CloudDatabaseHelper extends SQLiteHelper {
    private static final String DATABASE_NAME = "cloud_configure.db";
    private static final int DATABASE_VERSION = 34;
    private static final String TAG = CloudDatabaseHelper.class.getSimpleName();

    public CloudDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private boolean isHasTable(SQLiteDatabase sQLiteDatabase, String[] strArr) {
        Cursor cursorRawQuery = null;
        try {
            cursorRawQuery = sQLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            HashSet hashSetNewHashSet = Sets.newHashSet();
            while (cursorRawQuery.moveToNext()) {
                hashSetNewHashSet.add(cursorRawQuery.getString(0));
            }
            for (String str : strArr) {
                if (!hashSetNewHashSet.contains(str)) {
                    Log.e(TAG, "isHasTable return false for having no " + str);
                    cursorRawQuery.close();
                    return false;
                }
            }
            cursorRawQuery.close();
            return true;
        } catch (Exception unused) {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            return false;
        } catch (Throwable th) {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            throw th;
        }
    }

    private boolean isTableHasColumn(SQLiteDatabase sQLiteDatabase, String str, String[] strArr) {
        Cursor cursorRawQuery = null;
        try {
            cursorRawQuery = sQLiteDatabase.rawQuery("PRAGMA table_info(" + str + Constants.SEPARATOR_RIGHT_PARENTESIS, null);
            HashSet hashSetNewHashSet = Sets.newHashSet();
            while (cursorRawQuery.moveToNext()) {
                hashSetNewHashSet.add(cursorRawQuery.getString(1));
            }
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (!hashSetNewHashSet.contains(strArr[i2])) {
                    Log.i(TAG, "isTableHasColumn return false for have no tableName=" + str + " columnNameArray[i]= " + strArr[i2]);
                    cursorRawQuery.close();
                    return false;
                }
            }
            cursorRawQuery.close();
            return true;
        } catch (Exception unused) {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            return false;
        } catch (Throwable th) {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            throw th;
        }
    }

    private boolean isTableHasRaw(SQLiteDatabase sQLiteDatabase, String str, String str2, String str3) {
        Cursor cursorRawQuery = null;
        try {
            cursorRawQuery = sQLiteDatabase.rawQuery("SELECT * FROM " + str + " WHERE " + str2 + "='" + str3 + "'", null);
            if (cursorRawQuery.getCount() > 0) {
                cursorRawQuery.close();
                return true;
            }
        } catch (Exception unused) {
            if (cursorRawQuery != null) {
            }
            Log.e(TAG, "isTableHasRaw return false tableName=" + str + " column=" + str2 + " Value=" + str3);
            return false;
        } catch (Throwable th) {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            throw th;
        }
        cursorRawQuery.close();
        Log.e(TAG, "isTableHasRaw return false tableName=" + str + " column=" + str2 + " Value=" + str3);
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:109:0x033f  */
    /* JADX WARN: Code duplicated, block: B:122:0x03ab  */
    /* JADX WARN: Code duplicated, block: B:135:0x03fe  */
    /* JADX WARN: Code duplicated, block: B:148:0x0451  */
    /* JADX WARN: Code duplicated, block: B:161:0x04b0  */
    /* JADX WARN: Code duplicated, block: B:165:0x04f7 A[Catch: all -> 0x0510, Exception -> 0x0513, LOOP:1: B:163:0x04f1->B:165:0x04f7, LOOP_END, TryCatch #19 {Exception -> 0x0513, blocks: (B:162:0x04b3, B:163:0x04f1, B:165:0x04f7, B:170:0x0516), top: B:488:0x04b3, outer: #52 }] */
    /* JADX WARN: Code duplicated, block: B:178:0x0536  */
    /* JADX WARN: Code duplicated, block: B:190:0x0587  */
    /* JADX WARN: Code duplicated, block: B:203:0x0621  */
    /* JADX WARN: Code duplicated, block: B:216:0x067d  */
    /* JADX WARN: Code duplicated, block: B:229:0x06e7  */
    /* JADX WARN: Code duplicated, block: B:242:0x075c  */
    /* JADX WARN: Code duplicated, block: B:255:0x07a4  */
    /* JADX WARN: Code duplicated, block: B:258:0x07c4  */
    /* JADX WARN: Code duplicated, block: B:271:0x0818  */
    /* JADX WARN: Code duplicated, block: B:284:0x085d  */
    /* JADX WARN: Code duplicated, block: B:297:0x08b0  */
    /* JADX WARN: Code duplicated, block: B:300:0x08b6  */
    /* JADX WARN: Code duplicated, block: B:313:0x08fb  */
    /* JADX WARN: Code duplicated, block: B:316:0x0901  */
    /* JADX WARN: Code duplicated, block: B:329:0x096b  */
    /* JADX WARN: Code duplicated, block: B:342:0x09bf  */
    /* JADX WARN: Code duplicated, block: B:345:0x09c5  */
    /* JADX WARN: Code duplicated, block: B:358:0x0a19  */
    /* JADX WARN: Code duplicated, block: B:371:0x0a81  */
    /* JADX WARN: Code duplicated, block: B:384:0x0aee  */
    /* JADX WARN: Code duplicated, block: B:397:0x0b47  */
    /* JADX WARN: Code duplicated, block: B:410:0x0b9b  */
    /* JADX WARN: Code duplicated, block: B:424:0x0bdd  */
    /* JADX WARN: Code duplicated, block: B:437:0x0c2c  */
    /* JADX WARN: Code duplicated, block: B:438:0x0c48  */
    /* JADX WARN: Code duplicated, block: B:54:0x01ab  */
    /* JADX WARN: Code duplicated, block: B:57:0x01c9  */
    /* JADX WARN: Code duplicated, block: B:70:0x022f  */
    /* JADX WARN: Code duplicated, block: B:83:0x0295  */
    /* JADX WARN: Code duplicated, block: B:96:0x02e7  */
    /* JADX WARN: Instruction removed from duplicated block: B:165:0x04f7, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:255:0x07a4, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:437:0x0c2c, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:438:0x0c48, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:54:0x01ab, please report this as an issue */
    private void updateDatabase(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        int i4;
        String str;
        int i5;
        Iterator it;
        String str2 = TAG;
        Log.i(str2, "Upgrading database from version " + i2 + " to " + i3);
        if (i3 != DATABASE_VERSION) {
            Log.e(str2, "Illegal update request. Got " + i3 + ", expected " + DATABASE_VERSION);
            throw new IllegalArgumentException();
        }
        if (i2 > i3) {
            Log.e(str2, "Illegal update request: can't downgrade from " + i2 + " to " + i3 + ". Did you forget to wipe data?");
            throw new IllegalArgumentException();
        }
        if (i2 < 1) {
            sQLiteDatabase.beginTransaction();
            try {
                try {
                    Log.i(str2, "Upgrading database from version " + i2 + " to " + i3 + ", which will destroy all old data");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS GlobalFeatureTable");
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS cloudAppTable");
                    sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS GlobalFeatureTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,configureName TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, configureParam TEXT NOT NULL );");
                    sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS cloudAppTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,pkgName TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, bgData TEXT, bgDataDelayTime INTEGER, bgDataDelayCount INTEGER, bgDataMinDataKb INTEGER, bgDataMaxInactiveCount INTEGER, bgLocation TEXT, bgLocationDelayTime INTEGER );");
                    ArrayList arrayListNewArrayList = Lists.newArrayList();
                    arrayListNewArrayList.add("( 'featureStatus', 'false' );");
                    arrayListNewArrayList.add("( 'userConfigureStatus', 'true' );");
                    arrayListNewArrayList.add("( 'lastUpdated', '0' );");
                    arrayListNewArrayList.add("( 'bgDataDisableShortTime', '3' );");
                    arrayListNewArrayList.add("( 'bgDataDisableLongTime', '10' );");
                    arrayListNewArrayList.add("( 'bgLocationDisableShortTime', '3' );");
                    arrayListNewArrayList.add("( 'bgData', 'true' );");
                    arrayListNewArrayList.add("( 'bgDataDelayTime', '3' );");
                    arrayListNewArrayList.add("( 'bgDataDelayCount', '-1' );");
                    arrayListNewArrayList.add("( 'bgDataMinDataKb', '-1' );");
                    arrayListNewArrayList.add("( 'bgDataMaxInactiveCount', '-1' );");
                    arrayListNewArrayList.add("( 'bgLocation', 'true' );");
                    arrayListNewArrayList.add("( 'bgLocationDelayTime', '-1' );");
                    Iterator it2 = arrayListNewArrayList.iterator();
                    while (it2.hasNext()) {
                        sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES " + ((String) it2.next()));
                    }
                    arrayListNewArrayList.clear();
                    sQLiteDatabase.setTransactionSuccessful();
                    sQLiteDatabase.endTransaction();
                    i4 = 1;
                } catch (Throwable th) {
                    sQLiteDatabase.endTransaction();
                    throw th;
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
                sQLiteDatabase.endTransaction();
                i4 = i2;
            }
        } else {
            i4 = i2;
        }
        if (i4 == 1) {
            sQLiteDatabase.beginTransaction();
            try {
                try {
                    Log.i(TAG, "Upgrading database from version " + i4 + " to " + i3);
                    String str3 = "normal";
                    str = "' );";
                    try {
                        Cursor cursorQuery = sQLiteDatabase.query(GlobalFeatureConfigure.TABLE, null, "configureName = ?", new String[]{GlobalFeatureConfigure.USER_CONFIGURE_STATUS}, null, null, null);
                        if (cursorQuery != null && cursorQuery.moveToFirst() && cursorQuery.getString(cursorQuery.getColumnIndex("configureParam")).equals("false")) {
                            str3 = "disable";
                        }
                        String str4 = str3;
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        sQLiteDatabase.execSQL("INSERT OR REPLACE INTO GlobalFeatureTable ( configureName,configureParam ) VALUES " + ("( 'userConfigureStatus', '" + str4 + str));
                        sQLiteDatabase.setTransactionSuccessful();
                        sQLiteDatabase.endTransaction();
                        i5 = 2;
                    } catch (Exception e3) {
                        e = e3;
                        Log.e(TAG, e.getMessage());
                        sQLiteDatabase.endTransaction();
                        i5 = i4;
                    }
                } catch (Exception e4) {
                    e = e4;
                    str = "' );";
                }
                if (i5 == 2) {
                    Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                    i5 = 3;
                }
                if (i5 == 3) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb = new StringBuilder();
                            sb.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb.append("( 'deviceidleStatus', 'false' );");
                            sQLiteDatabase.execSQL(sb.toString());
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'noCoreSystemApps', '' );");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 4;
                        } catch (Exception e5) {
                            Log.e(TAG, e5.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th2) {
                        sQLiteDatabase.endTransaction();
                        throw th2;
                    }
                }
                if (i5 == 4) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb2.append("( 'miui_idle', 'false' );");
                            sQLiteDatabase.execSQL(sb2.toString());
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'miui_standby', 'true' );");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 5;
                        } catch (Exception e6) {
                            Log.e(TAG, e6.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th3) {
                        sQLiteDatabase.endTransaction();
                        throw th3;
                    }
                }
                if (i5 == 5) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb3.append("( 'musicApps', '' );");
                            sQLiteDatabase.execSQL(sb3.toString());
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 6;
                        } catch (Throwable th4) {
                            sQLiteDatabase.endTransaction();
                            throw th4;
                        }
                    } catch (Exception e7) {
                        Log.e(TAG, e7.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 6) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb4.append("( 'k_delay', '-2' );");
                            sQLiteDatabase.execSQL(sb4.toString());
                            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN k_delay INTEGER");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 7;
                        } catch (Throwable th5) {
                            sQLiteDatabase.endTransaction();
                            throw th5;
                        }
                    } catch (Exception e8) {
                        Log.e(TAG, e8.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 7) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb5 = new StringBuilder();
                            sb5.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb5.append("( 's_delay', '-2' );");
                            sQLiteDatabase.execSQL(sb5.toString());
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'SensorControlStatus', 'false' );");
                            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN s_delay INTEGER");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 8;
                        } catch (Exception e9) {
                            Log.e(TAG, e9.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th6) {
                        sQLiteDatabase.endTransaction();
                        throw th6;
                    }
                }
                if (i5 == 8) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb6 = new StringBuilder();
                            sb6.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb6.append("( 'levelUtimateSpecialApps', '' );");
                            sQLiteDatabase.execSQL(sb6.toString());
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 9;
                        } catch (Exception e10) {
                            Log.e(TAG, e10.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th7) {
                        sQLiteDatabase.endTransaction();
                        throw th7;
                    }
                }
                if (i5 == 9) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb7 = new StringBuilder();
                            sb7.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb7.append("( 'bleScanBlock', 'false' );");
                            sQLiteDatabase.execSQL(sb7.toString());
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 10;
                        } catch (Throwable th8) {
                            sQLiteDatabase.endTransaction();
                            throw th8;
                        }
                    } catch (Exception e11) {
                        Log.e(TAG, e11.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 10) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tempGlobalFeatureTable");
                            sQLiteDatabase.execSQL("ALTER TABLE GlobalFeatureTable RENAME TO tempGlobalFeatureTable");
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS GlobalFeatureTable");
                            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS GlobalFeatureTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER NOT NULL DEFAULT 0, configureName TEXT NOT NULL, configureParam TEXT NOT NULL, UNIQUE (userId, configureName) ON CONFLICT REPLACE );");
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) SELECT configureName, configureParam FROM tempGlobalFeatureTable");
                            sQLiteDatabase.execSQL("DROP TABLE tempGlobalFeatureTable");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 11;
                        } catch (Exception e12) {
                            Log.e(TAG, e12.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th9) {
                        sQLiteDatabase.endTransaction();
                        throw th9;
                    }
                }
                if (i5 == 11) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BCGlobalFeatureTable");
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BcCloudAppTable");
                            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS BCGlobalFeatureTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,configureName TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, configureParam TEXT NOT NULL );");
                            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS BcCloudAppTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,pkgName TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, bgBroadcastIds TEXT, except_pkg TEXT, b_delay INTEGER );");
                            ArrayList arrayListNewArrayList2 = Lists.newArrayList();
                            arrayListNewArrayList2.add("( 'g_broadcast_status', 'false' );");
                            arrayListNewArrayList2.add("( 'b_delay', '-2' );");
                            it = arrayListNewArrayList2.iterator();
                            while (it.hasNext()) {
                                sQLiteDatabase.execSQL("INSERT INTO BCGlobalFeatureTable ( configureName,configureParam ) VALUES " + ((String) it.next()));
                            }
                            arrayListNewArrayList2.clear();
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 12;
                        } catch (Exception e13) {
                            Log.e(TAG, e13.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th10) {
                        sQLiteDatabase.endTransaction();
                        throw th10;
                    }
                }
                if (i5 == 12) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb8 = new StringBuilder();
                            sb8.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb8.append("( 'bleScanParam', '' );");
                            sQLiteDatabase.execSQL(sb8.toString());
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 13;
                        } catch (Exception e14) {
                            Log.e(TAG, e14.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th11) {
                        sQLiteDatabase.endTransaction();
                        throw th11;
                    }
                }
                if (i5 == 13) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb9 = new StringBuilder();
                            sb9.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb9.append("( 'l_delay_hot', '-1' );");
                            sQLiteDatabase.execSQL(sb9.toString());
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'k_delay_hot', '-2' );");
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'hotFeedbackFeature', 'false' );");
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'networkFeedbackFeature', 'false' );");
                            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN l_delay_hot INTEGER");
                            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN k_delay_hot INTEGER");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 14;
                        } catch (Exception e15) {
                            Log.e(TAG, e15.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th12) {
                        sQLiteDatabase.endTransaction();
                        throw th12;
                    }
                }
                if (i5 == 14) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tempBroadcastManageGlobalFeatureTable");
                            sQLiteDatabase.execSQL("ALTER TABLE BcCloudAppTable RENAME TO tempBroadcastManageGlobalFeatureTable");
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BcCloudAppTable");
                            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS BcCloudAppTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,pkgName TEXT, bgBroadcastIds TEXT, except_pkg TEXT, b_delay INTEGER, UNIQUE (pkgName, bgBroadcastIds, except_pkg) ON CONFLICT REPLACE );");
                            sQLiteDatabase.execSQL("INSERT INTO BcCloudAppTable ( pkgName,bgBroadcastIds,except_pkg,b_delay ) SELECT pkgName, bgBroadcastIds, except_pkg, b_delay FROM tempBroadcastManageGlobalFeatureTable");
                            sQLiteDatabase.execSQL("DROP TABLE tempBroadcastManageGlobalFeatureTable");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 15;
                        } catch (Exception e16) {
                            Log.e(TAG, e16.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th13) {
                        sQLiteDatabase.endTransaction();
                        throw th13;
                    }
                }
                if (i5 == 15) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb10 = new StringBuilder();
                            sb10.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb10.append("( 'lightIdleStatus', 'false' );");
                            sQLiteDatabase.execSQL(sb10.toString());
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'dozeWhiteListApps', '' );");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 16;
                        } catch (Throwable th14) {
                            sQLiteDatabase.endTransaction();
                            throw th14;
                        }
                    } catch (Exception e17) {
                        Log.e(TAG, e17.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 16) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ComCloudTable");
                            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS ComCloudTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,fea_name TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, fea_value TEXT NOT NULL,fea_state TEXT NOT NULL );");
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb11 = new StringBuilder();
                            sb11.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb11.append("( 'i_delay', '-2' );");
                            sQLiteDatabase.execSQL(sb11.toString());
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'appIdleStatus', 'false' );");
                            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN i_delay INTEGER");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 17;
                        } catch (Throwable th15) {
                            sQLiteDatabase.endTransaction();
                            throw th15;
                        }
                    } catch (Exception e18) {
                        Log.e(TAG, e18.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 17) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ComCloudTable");
                            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS ComCloudTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,fea_name TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, fea_value TEXT NOT NULL,fea_state TEXT NOT NULL );");
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 18;
                        } catch (Exception e19) {
                            Log.e(TAG, e19.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th16) {
                        sQLiteDatabase.endTransaction();
                        throw th16;
                    }
                }
                if (i5 == 18) {
                    Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                    i5 = 19;
                }
                if (i5 == 19) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb12 = new StringBuilder();
                            sb12.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb12.append("( 'standbyChainDelay', '-2' );");
                            sQLiteDatabase.execSQL(sb12.toString());
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 20;
                        } catch (Exception e20) {
                            Log.e(TAG, e20.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th17) {
                        sQLiteDatabase.endTransaction();
                        throw th17;
                    }
                }
                if (i5 == 20) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN power_state_id INTEGER DEFAULT -1");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 21;
                        } catch (Throwable th18) {
                            sQLiteDatabase.endTransaction();
                            throw th18;
                        }
                    } catch (Exception e21) {
                        Log.e(TAG, e21.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 21) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BCGlobalFeatureTable");
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BcCloudAppTable");
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ComCloudTable");
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS TrackTable");
                            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS TrackTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,action TEXT, config_key TEXT, timeId INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE ,extra TEXT );");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 22;
                        } catch (Exception e22) {
                            Log.e(TAG, e22.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th19) {
                        sQLiteDatabase.endTransaction();
                        throw th19;
                    }
                }
                if (i5 == 22) {
                    i5++;
                }
                if (i5 == 23) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN s_disable_type INTEGER DEFAULT NULL");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 24;
                        } catch (Exception e23) {
                            Log.e(TAG, e23.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th20) {
                        sQLiteDatabase.endTransaction();
                        throw th20;
                    }
                }
                if (i5 == 24) {
                    i5++;
                }
                if (i5 == 25) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES " + ("( 'FrozenControlNewStatus', '" + MilletConfig.supportGlobalNewFeature() + str));
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 26;
                        } catch (Throwable th21) {
                            sQLiteDatabase.endTransaction();
                            throw th21;
                        }
                    } catch (Exception e24) {
                        Log.e(TAG, e24.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 26) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb13 = new StringBuilder();
                            sb13.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb13.append("( 'FrozenNewWhiteList', '');");
                            sQLiteDatabase.execSQL(sb13.toString());
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 27;
                        } catch (Throwable th22) {
                            sQLiteDatabase.endTransaction();
                            throw th22;
                        }
                    } catch (Exception e25) {
                        Log.e(TAG, e25.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 27) {
                    i5++;
                }
                if (i5 == 28) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb14 = new StringBuilder();
                            sb14.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb14.append("( 'alarmAlign', '');");
                            sQLiteDatabase.execSQL(sb14.toString());
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 29;
                        } catch (Exception e26) {
                            Log.e(TAG, e26.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th23) {
                        sQLiteDatabase.endTransaction();
                        throw th23;
                    }
                }
                if (i5 == 29) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb15 = new StringBuilder();
                            sb15.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb15.append("( 'bright_frozen', '');");
                            sQLiteDatabase.execSQL(sb15.toString());
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'screenoff_millet_mode', '');");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 30;
                        } catch (Exception e27) {
                            Log.e(TAG, e27.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th24) {
                        sQLiteDatabase.endTransaction();
                        throw th24;
                    }
                }
                if (i5 == 30) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb16 = new StringBuilder();
                            sb16.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb16.append("( 'k_policy', '896' );");
                            sQLiteDatabase.execSQL(sb16.toString());
                            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'killConfigs', '');");
                            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN k_policy INTEGER");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 31;
                        } catch (Throwable th25) {
                            sQLiteDatabase.endTransaction();
                            throw th25;
                        }
                    } catch (Exception e28) {
                        Log.e(TAG, e28.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 31) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb17 = new StringBuilder();
                            sb17.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb17.append("( 'ClusterStatus', 'false'  );");
                            sQLiteDatabase.execSQL(sb17.toString());
                            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN c_policy INTEGER");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 32;
                        } catch (Throwable th26) {
                            sQLiteDatabase.endTransaction();
                            throw th26;
                        }
                    } catch (Exception e29) {
                        Log.e(TAG, e29.getMessage());
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == 32) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb18 = new StringBuilder();
                            sb18.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb18.append("( 'launchRestrict', '');");
                            sQLiteDatabase.execSQL(sb18.toString());
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 33;
                        } catch (Exception e30) {
                            Log.e(TAG, e30.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Throwable th27) {
                        sQLiteDatabase.endTransaction();
                        throw th27;
                    }
                }
                if (i5 == 33) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ThermalInfo");
                        sQLiteDatabase.setTransactionSuccessful();
                        i5++;
                    } catch (Exception e31) {
                        Log.e(TAG, e31.getMessage());
                    } finally {
                        sQLiteDatabase.endTransaction();
                    }
                }
                if (i5 == DATABASE_VERSION) {
                    sQLiteDatabase.beginTransaction();
                    try {
                        try {
                            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
                            StringBuilder sb19 = new StringBuilder();
                            sb19.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
                            sb19.append("( 'aurogon_enable', '');");
                            sQLiteDatabase.execSQL(sb19.toString());
                            sQLiteDatabase.setTransactionSuccessful();
                            i5++;
                        } catch (Exception e32) {
                            Log.e(TAG, e32.getMessage());
                        }
                        sQLiteDatabase.endTransaction();
                    } catch (Throwable th28) {
                        sQLiteDatabase.endTransaction();
                        throw th28;
                    }
                }
                if (i3 != i5) {
                    Log.e(TAG, "Upgrade database to version " + i3 + " fails");
                    return;
                }
                Log.i(TAG, "Upgrade database to version " + i3 + " success");
            } catch (Throwable th29) {
                sQLiteDatabase.endTransaction();
                throw th29;
            }
        }
        str = "' );";
        i4 = i4;
        i5 = i4;
        if (i5 == 2) {
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            i5 = 3;
        }
        if (i5 == 3) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb20 = new StringBuilder();
            sb20.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb20.append("( 'deviceidleStatus', 'false' );");
            sQLiteDatabase.execSQL(sb20.toString());
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'noCoreSystemApps', '' );");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 4;
        }
        if (i5 == 4) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb21 = new StringBuilder();
            sb21.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb21.append("( 'miui_idle', 'false' );");
            sQLiteDatabase.execSQL(sb21.toString());
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'miui_standby', 'true' );");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 5;
        }
        if (i5 == 5) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb22 = new StringBuilder();
            sb22.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb22.append("( 'musicApps', '' );");
            sQLiteDatabase.execSQL(sb22.toString());
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 6;
        }
        if (i5 == 6) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb23 = new StringBuilder();
            sb23.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb23.append("( 'k_delay', '-2' );");
            sQLiteDatabase.execSQL(sb23.toString());
            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN k_delay INTEGER");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 7;
        }
        if (i5 == 7) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb24 = new StringBuilder();
            sb24.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb24.append("( 's_delay', '-2' );");
            sQLiteDatabase.execSQL(sb24.toString());
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'SensorControlStatus', 'false' );");
            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN s_delay INTEGER");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 8;
        }
        if (i5 == 8) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb25 = new StringBuilder();
            sb25.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb25.append("( 'levelUtimateSpecialApps', '' );");
            sQLiteDatabase.execSQL(sb25.toString());
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 9;
        }
        if (i5 == 9) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb26 = new StringBuilder();
            sb26.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb26.append("( 'bleScanBlock', 'false' );");
            sQLiteDatabase.execSQL(sb26.toString());
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 10;
        }
        if (i5 == 10) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tempGlobalFeatureTable");
            sQLiteDatabase.execSQL("ALTER TABLE GlobalFeatureTable RENAME TO tempGlobalFeatureTable");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS GlobalFeatureTable");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS GlobalFeatureTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER NOT NULL DEFAULT 0, configureName TEXT NOT NULL, configureParam TEXT NOT NULL, UNIQUE (userId, configureName) ON CONFLICT REPLACE );");
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) SELECT configureName, configureParam FROM tempGlobalFeatureTable");
            sQLiteDatabase.execSQL("DROP TABLE tempGlobalFeatureTable");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 11;
        }
        if (i5 == 11) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BCGlobalFeatureTable");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BcCloudAppTable");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS BCGlobalFeatureTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,configureName TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, configureParam TEXT NOT NULL );");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS BcCloudAppTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,pkgName TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, bgBroadcastIds TEXT, except_pkg TEXT, b_delay INTEGER );");
            ArrayList arrayListNewArrayList3 = Lists.newArrayList();
            arrayListNewArrayList3.add("( 'g_broadcast_status', 'false' );");
            arrayListNewArrayList3.add("( 'b_delay', '-2' );");
            it = arrayListNewArrayList3.iterator();
            while (it.hasNext()) {
                sQLiteDatabase.execSQL("INSERT INTO BCGlobalFeatureTable ( configureName,configureParam ) VALUES " + ((String) it.next()));
            }
            arrayListNewArrayList3.clear();
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 12;
        }
        if (i5 == 12) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb27 = new StringBuilder();
            sb27.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb27.append("( 'bleScanParam', '' );");
            sQLiteDatabase.execSQL(sb27.toString());
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 13;
        }
        if (i5 == 13) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb28 = new StringBuilder();
            sb28.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb28.append("( 'l_delay_hot', '-1' );");
            sQLiteDatabase.execSQL(sb28.toString());
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'k_delay_hot', '-2' );");
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'hotFeedbackFeature', 'false' );");
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'networkFeedbackFeature', 'false' );");
            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN l_delay_hot INTEGER");
            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN k_delay_hot INTEGER");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 14;
        }
        if (i5 == 14) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tempBroadcastManageGlobalFeatureTable");
            sQLiteDatabase.execSQL("ALTER TABLE BcCloudAppTable RENAME TO tempBroadcastManageGlobalFeatureTable");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BcCloudAppTable");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS BcCloudAppTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,pkgName TEXT, bgBroadcastIds TEXT, except_pkg TEXT, b_delay INTEGER, UNIQUE (pkgName, bgBroadcastIds, except_pkg) ON CONFLICT REPLACE );");
            sQLiteDatabase.execSQL("INSERT INTO BcCloudAppTable ( pkgName,bgBroadcastIds,except_pkg,b_delay ) SELECT pkgName, bgBroadcastIds, except_pkg, b_delay FROM tempBroadcastManageGlobalFeatureTable");
            sQLiteDatabase.execSQL("DROP TABLE tempBroadcastManageGlobalFeatureTable");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 15;
        }
        if (i5 == 15) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb110 = new StringBuilder();
            sb110.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb110.append("( 'lightIdleStatus', 'false' );");
            sQLiteDatabase.execSQL(sb110.toString());
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'dozeWhiteListApps', '' );");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 16;
        }
        if (i5 == 16) {
            sQLiteDatabase.beginTransaction();
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ComCloudTable");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS ComCloudTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,fea_name TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, fea_value TEXT NOT NULL,fea_state TEXT NOT NULL );");
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb111 = new StringBuilder();
            sb111.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb111.append("( 'i_delay', '-2' );");
            sQLiteDatabase.execSQL(sb111.toString());
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'appIdleStatus', 'false' );");
            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN i_delay INTEGER");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 17;
        }
        if (i5 == 17) {
            sQLiteDatabase.beginTransaction();
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ComCloudTable");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS ComCloudTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,fea_name TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, fea_value TEXT NOT NULL,fea_state TEXT NOT NULL );");
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 18;
        }
        if (i5 == 18) {
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            i5 = 19;
        }
        if (i5 == 19) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb112 = new StringBuilder();
            sb112.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb112.append("( 'standbyChainDelay', '-2' );");
            sQLiteDatabase.execSQL(sb112.toString());
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 20;
        }
        if (i5 == 20) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN power_state_id INTEGER DEFAULT -1");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 21;
        }
        if (i5 == 21) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BCGlobalFeatureTable");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS BcCloudAppTable");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ComCloudTable");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS TrackTable");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS TrackTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,action TEXT, config_key TEXT, timeId INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE ,extra TEXT );");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 22;
        }
        if (i5 == 22) {
            i5++;
        }
        if (i5 == 23) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN s_disable_type INTEGER DEFAULT NULL");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 24;
        }
        if (i5 == 24) {
            i5++;
        }
        if (i5 == 25) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES " + ("( 'FrozenControlNewStatus', '" + MilletConfig.supportGlobalNewFeature() + str));
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 26;
        }
        if (i5 == 26) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb113 = new StringBuilder();
            sb113.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb113.append("( 'FrozenNewWhiteList', '');");
            sQLiteDatabase.execSQL(sb113.toString());
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 27;
        }
        if (i5 == 27) {
            i5++;
        }
        if (i5 == 28) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb114 = new StringBuilder();
            sb114.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb114.append("( 'alarmAlign', '');");
            sQLiteDatabase.execSQL(sb114.toString());
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 29;
        }
        if (i5 == 29) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb115 = new StringBuilder();
            sb115.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb115.append("( 'bright_frozen', '');");
            sQLiteDatabase.execSQL(sb115.toString());
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'screenoff_millet_mode', '');");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 30;
        }
        if (i5 == 30) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb116 = new StringBuilder();
            sb116.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb116.append("( 'k_policy', '896' );");
            sQLiteDatabase.execSQL(sb116.toString());
            sQLiteDatabase.execSQL("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ( 'killConfigs', '');");
            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN k_policy INTEGER");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 31;
        }
        if (i5 == 31) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb117 = new StringBuilder();
            sb117.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb117.append("( 'ClusterStatus', 'false'  );");
            sQLiteDatabase.execSQL(sb117.toString());
            sQLiteDatabase.execSQL("ALTER TABLE cloudAppTable ADD COLUMN c_policy INTEGER");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 32;
        }
        if (i5 == 32) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb118 = new StringBuilder();
            sb118.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb118.append("( 'launchRestrict', '');");
            sQLiteDatabase.execSQL(sb118.toString());
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 33;
        }
        if (i5 == 33) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ThermalInfo");
            sQLiteDatabase.setTransactionSuccessful();
            i5++;
        }
        if (i5 == DATABASE_VERSION) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i3);
            StringBuilder sb119 = new StringBuilder();
            sb119.append("INSERT INTO GlobalFeatureTable ( configureName,configureParam ) VALUES ");
            sb119.append("( 'aurogon_enable', '');");
            sQLiteDatabase.execSQL(sb119.toString());
            sQLiteDatabase.setTransactionSuccessful();
            i5++;
            sQLiteDatabase.endTransaction();
        }
        if (i3 != i5) {
            Log.e(TAG, "Upgrade database to version " + i3 + " fails");
            return;
        }
        Log.i(TAG, "Upgrade database to version " + i3 + " success");
    }

    @Override // com.miui.powerkeeper.provider.SQLiteHelper
    public boolean isValidDatabase(SQLiteDatabase sQLiteDatabase) {
        boolean zIsHasTable;
        if (sQLiteDatabase.getVersion() == DATABASE_VERSION) {
            zIsHasTable = isHasTable(sQLiteDatabase, new String[]{CloudAppConfigure.TABLE, GlobalFeatureConfigure.TABLE});
            if (zIsHasTable) {
                zIsHasTable = isTableHasRaw(sQLiteDatabase, GlobalFeatureConfigure.TABLE, "configureName", GlobalFeatureConfigure.STANDBY_CHAIN_DELAY);
            }
            if (zIsHasTable) {
                zIsHasTable = isTableHasColumn(sQLiteDatabase, CloudAppConfigure.TABLE, new String[]{CloudAppConfigure.Columns.POWER_STATE_ID});
            }
        } else {
            zIsHasTable = false;
        }
        Log.i(TAG, "isValidDatabase: " + zIsHasTable);
        return zIsHasTable;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        updateDatabase(sQLiteDatabase, 0, DATABASE_VERSION);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        Log.e(TAG, "Downgrade database from version " + i2 + " to " + i3);
        updateDatabase(sQLiteDatabase, 0, i3);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        updateDatabase(sQLiteDatabase, i2, DATABASE_VERSION);
    }
}
