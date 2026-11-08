package com.miui.powerkeeper.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.android.collect.Maps;
import com.google.android.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class UserDatabaseHelper extends SQLiteHelper {
    private static final String DATABASE_NAME = "user_configure.db";
    private static final int DATABASE_VERSION = 6;
    private static final String TAG = UserDatabaseHelper.class.getSimpleName();

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 6);
    }

    /* JADX WARN: Code duplicated, block: B:101:0x031f  */
    /* JADX WARN: Code duplicated, block: B:104:0x0324  */
    /* JADX WARN: Code duplicated, block: B:105:0x033e  */
    /* JADX WARN: Code duplicated, block: B:57:0x0213  */
    /* JADX WARN: Code duplicated, block: B:72:0x026e  */
    /* JADX WARN: Code duplicated, block: B:75:0x0273  */
    /* JADX WARN: Code duplicated, block: B:77:0x0293  */
    /* JADX WARN: Code duplicated, block: B:80:0x029c  */
    /* JADX WARN: Code duplicated, block: B:91:0x02dc  */
    /* JADX WARN: Instruction removed from duplicated block: B:104:0x0324, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:105:0x033e, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:75:0x0273, please report this as an issue */
    private void updateDatabase(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        int i4;
        int i5;
        int i6;
        int i7;
        String str;
        String str2 = TAG;
        Log.i(str2, "Upgrading database from version " + i2 + " to " + i3);
        if (i3 != 6) {
            Log.e(str2, "Illegal update request. Got " + i3 + ", expected 6");
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
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS userTable");
                    sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS userTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,pkgName TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, lastConfigured INTEGER NOT NULL, miuiSuggest TEXT NOT NULL DEFAULT 'enable', bgData TEXT NOT NULL DEFAULT 'disableShort', bgLocation TEXT NOT NULL DEFAULT 'disableShort' );");
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
                    Log.i(TAG, "Upgrading database from version " + i4 + " to " + i3 + ", which will destroy all old data");
                    try {
                        Cursor cursorQuery = sQLiteDatabase.query(UserConfigure.TABLE, null, null, null, null, null, null);
                        HashMap mapNewHashMap = Maps.newHashMap();
                        HashMap mapNewHashMap2 = Maps.newHashMap();
                        HashMap mapNewHashMap3 = Maps.newHashMap();
                        int columnIndex = cursorQuery.getColumnIndex("pkgName");
                        int columnIndex2 = cursorQuery.getColumnIndex("miuiSuggest");
                        int columnIndex3 = cursorQuery.getColumnIndex("bgData");
                        int columnIndex4 = cursorQuery.getColumnIndex("bgLocation");
                        while (cursorQuery.moveToNext()) {
                            String string = cursorQuery.getString(columnIndex);
                            String string2 = cursorQuery.getString(columnIndex2);
                            int i8 = columnIndex;
                            String string3 = cursorQuery.getString(columnIndex3);
                            int i9 = columnIndex2;
                            String string4 = cursorQuery.getString(columnIndex4);
                            int i10 = columnIndex3;
                            Log.i(TAG, "pkg = " + string + " miuiSuggest = " + string2 + " bgData = " + string3 + " bgLocation = " + string4);
                            mapNewHashMap.put(string, string2);
                            mapNewHashMap2.put(string, string3);
                            mapNewHashMap3.put(string, string4);
                            columnIndex = i8;
                            columnIndex2 = i9;
                            columnIndex3 = i10;
                            columnIndex4 = columnIndex4;
                        }
                        cursorQuery.close();
                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS userTable");
                        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS userTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT,pkgName TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, lastConfigured INTEGER, bgControl TEXT NOT NULL DEFAULT 'miuiAuto', bgLocation TEXT );");
                        for (String str3 : mapNewHashMap.keySet()) {
                            if (((String) mapNewHashMap.get(str3)).equals("enable")) {
                                sQLiteDatabase.execSQL("INSERT INTO userTable ( pkgName,bgControl ) VALUES ( '" + str3 + "', 'miuiAuto' );");
                            } else if (!((String) mapNewHashMap2.get(str3)).equals("enable")) {
                                sQLiteDatabase.execSQL("INSERT INTO userTable ( pkgName,bgControl ) VALUES ( '" + str3 + "', 'restrictBg' );");
                            } else if (((String) mapNewHashMap3.get(str3)).equals("enable")) {
                                sQLiteDatabase.execSQL("INSERT INTO userTable ( pkgName,bgControl,bgLocation ) VALUES ( '" + str3 + "', 'noRestrict', 'enable' );");
                            } else {
                                sQLiteDatabase.execSQL("INSERT INTO userTable ( pkgName,bgControl,bgLocation ) VALUES ( '" + str3 + "', 'noRestrict', 'disable' );");
                            }
                        }
                        sQLiteDatabase.setTransactionSuccessful();
                        sQLiteDatabase.endTransaction();
                        i5 = 2;
                    } catch (Exception e3) {
                        e = e3;
                        Log.e(TAG, e.getMessage());
                        sQLiteDatabase.endTransaction();
                        i5 = i4;
                    }
                } catch (Throwable th2) {
                    sQLiteDatabase.endTransaction();
                    throw th2;
                }
            } catch (Exception e4) {
                e = e4;
            }
            if (i5 == 2) {
                sQLiteDatabase.beginTransaction();
                try {
                    try {
                        String str4 = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Upgrading database from version ");
                        sb.append(i5);
                        sb.append(" to ");
                        i6 = i3;
                        try {
                            sb.append(i6);
                            Log.i(str4, sb.toString());
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tempUserTable");
                            sQLiteDatabase.execSQL("ALTER TABLE userTable RENAME TO tempUserTable");
                            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS userTable");
                            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS userTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER NOT NULL DEFAULT 0, pkgName TEXT NOT NULL, lastConfigured INTEGER, bgControl TEXT NOT NULL DEFAULT 'miuiAuto', bgLocation TEXT, UNIQUE (userId, pkgName) ON CONFLICT REPLACE );");
                            sQLiteDatabase.execSQL("INSERT INTO userTable ( pkgName,lastConfigured,bgControl,bgLocation ) SELECT pkgName, lastConfigured, bgControl, bgLocation FROM tempUserTable");
                            sQLiteDatabase.execSQL("DROP TABLE tempUserTable");
                            sQLiteDatabase.setTransactionSuccessful();
                            sQLiteDatabase.endTransaction();
                            i5 = 3;
                        } catch (Exception e5) {
                            e = e5;
                            Log.e(TAG, e.getMessage());
                            sQLiteDatabase.endTransaction();
                        }
                    } catch (Exception e6) {
                        e = e6;
                        i6 = i3;
                    }
                } catch (Throwable th3) {
                    sQLiteDatabase.endTransaction();
                    throw th3;
                }
            } else {
                i6 = i3;
            }
            if (i5 == 3) {
                str = TAG;
                Log.i(str, "Upgrading database from version " + i5 + " to " + i6);
                if (version3Valid(sQLiteDatabase)) {
                    Log.i(str, "versionChecking pass");
                    i5 = 4;
                }
            }
            if (i5 == 4) {
                sQLiteDatabase.beginTransaction();
                try {
                    try {
                        Log.i(TAG, "Upgrading database from version " + i5 + " to " + i6);
                        sQLiteDatabase.execSQL("ALTER TABLE userTable ADD COLUMN bgDelayMin INTEGER");
                        sQLiteDatabase.setTransactionSuccessful();
                        sQLiteDatabase.endTransaction();
                        i5 = 5;
                    } catch (Throwable th4) {
                        sQLiteDatabase.endTransaction();
                        throw th4;
                    }
                } catch (Exception e7) {
                    Log.e(TAG, e7.getMessage());
                    sQLiteDatabase.endTransaction();
                }
            }
            if (i5 == 5) {
                sQLiteDatabase.beginTransaction();
                try {
                    try {
                        Log.i(TAG, "Upgrading database from version " + i5 + " to " + i6);
                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS misc");
                        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS misc ( _id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER NOT NULL DEFAULT 0, name TEXT NOT NULL, value TEXT, UNIQUE (userId, name) ON CONFLICT REPLACE );");
                        sQLiteDatabase.setTransactionSuccessful();
                        sQLiteDatabase.endTransaction();
                        i7 = 6;
                    } catch (Exception e8) {
                        Log.e(TAG, e8.getMessage());
                        sQLiteDatabase.endTransaction();
                        i7 = i5;
                        if (i6 != i7) {
                            Log.e(TAG, "Upgrade database to version " + i6 + " fails");
                            return;
                        }
                        Log.i(TAG, "Upgrade database to version " + i6 + " success");
                    }
                } catch (Throwable th5) {
                    sQLiteDatabase.endTransaction();
                    throw th5;
                }
            } else {
                i7 = i5;
            }
            if (i6 != i7) {
                Log.e(TAG, "Upgrade database to version " + i6 + " fails");
                return;
            }
            Log.i(TAG, "Upgrade database to version " + i6 + " success");
        }
        i4 = i4;
        i5 = i4;
        if (i5 == 2) {
            sQLiteDatabase.beginTransaction();
            String str5 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Upgrading database from version ");
            sb2.append(i5);
            sb2.append(" to ");
            i6 = i3;
            sb2.append(i6);
            Log.i(str5, sb2.toString());
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tempUserTable");
            sQLiteDatabase.execSQL("ALTER TABLE userTable RENAME TO tempUserTable");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS userTable");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS userTable ( _id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER NOT NULL DEFAULT 0, pkgName TEXT NOT NULL, lastConfigured INTEGER, bgControl TEXT NOT NULL DEFAULT 'miuiAuto', bgLocation TEXT, UNIQUE (userId, pkgName) ON CONFLICT REPLACE );");
            sQLiteDatabase.execSQL("INSERT INTO userTable ( pkgName,lastConfigured,bgControl,bgLocation ) SELECT pkgName, lastConfigured, bgControl, bgLocation FROM tempUserTable");
            sQLiteDatabase.execSQL("DROP TABLE tempUserTable");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 3;
        } else {
            i6 = i3;
        }
        if (i5 == 3) {
            str = TAG;
            Log.i(str, "Upgrading database from version " + i5 + " to " + i6);
            if (version3Valid(sQLiteDatabase)) {
                Log.i(str, "versionChecking pass");
                i5 = 4;
            }
        }
        if (i5 == 4) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i6);
            sQLiteDatabase.execSQL("ALTER TABLE userTable ADD COLUMN bgDelayMin INTEGER");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i5 = 5;
        }
        if (i5 == 5) {
            sQLiteDatabase.beginTransaction();
            Log.i(TAG, "Upgrading database from version " + i5 + " to " + i6);
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS misc");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS misc ( _id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER NOT NULL DEFAULT 0, name TEXT NOT NULL, value TEXT, UNIQUE (userId, name) ON CONFLICT REPLACE );");
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
            i7 = 6;
        } else {
            i7 = i5;
        }
        if (i6 != i7) {
            Log.e(TAG, "Upgrade database to version " + i6 + " fails");
            return;
        }
        Log.i(TAG, "Upgrade database to version " + i6 + " success");
    }

    private boolean version3Valid(SQLiteDatabase sQLiteDatabase) throws Throwable {
        Cursor cursorRawQuery;
        Throwable th;
        Cursor cursorRawQuery2;
        Throwable th2;
        try {
            cursorRawQuery = sQLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            try {
                HashSet hashSetNewHashSet = Sets.newHashSet();
                while (cursorRawQuery.moveToNext()) {
                    hashSetNewHashSet.add(cursorRawQuery.getString(0));
                }
                cursorRawQuery.close();
                Log.i(TAG, "All tables: " + hashSetNewHashSet);
                boolean zContains = hashSetNewHashSet.contains(UserConfigure.TABLE);
                if (zContains) {
                    try {
                        cursorRawQuery2 = sQLiteDatabase.rawQuery("PRAGMA table_info(userTable)", null);
                        try {
                            HashSet hashSetNewHashSet2 = Sets.newHashSet();
                            while (cursorRawQuery2.moveToNext()) {
                                hashSetNewHashSet2.add(cursorRawQuery2.getString(1));
                            }
                            cursorRawQuery2.close();
                            Log.i(TAG, "userTable All columns: " + hashSetNewHashSet2);
                            zContains = hashSetNewHashSet2.contains("userId");
                        } catch (Throwable th3) {
                            th2 = th3;
                            if (cursorRawQuery2 == null) {
                                throw th2;
                            }
                            cursorRawQuery2.close();
                            throw th2;
                        }
                    } catch (Throwable th4) {
                        cursorRawQuery2 = null;
                        th2 = th4;
                    }
                }
                Log.i(TAG, "version3Valid: " + zContains);
                return zContains;
            } catch (Throwable th5) {
                th = th5;
                if (cursorRawQuery == null) {
                    throw th;
                }
                cursorRawQuery.close();
                throw th;
            }
        } catch (Throwable th6) {
            cursorRawQuery = null;
            th = th6;
        }
    }

    private boolean version4Valid(SQLiteDatabase sQLiteDatabase) throws Throwable {
        boolean zVersion3Valid = version3Valid(sQLiteDatabase);
        Log.i(TAG, "version4Valid: " + zVersion3Valid);
        return zVersion3Valid;
    }

    private boolean version5Valid(SQLiteDatabase sQLiteDatabase) throws Throwable {
        Cursor cursorRawQuery;
        Throwable th;
        Cursor cursorRawQuery2;
        Throwable th2;
        try {
            cursorRawQuery = sQLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            try {
                HashSet hashSetNewHashSet = Sets.newHashSet();
                while (cursorRawQuery.moveToNext()) {
                    hashSetNewHashSet.add(cursorRawQuery.getString(0));
                }
                cursorRawQuery.close();
                Log.i(TAG, "All tables: " + hashSetNewHashSet);
                boolean zContains = hashSetNewHashSet.contains(UserConfigure.TABLE);
                if (zContains) {
                    try {
                        cursorRawQuery2 = sQLiteDatabase.rawQuery("PRAGMA table_info(userTable)", null);
                        try {
                            HashSet hashSetNewHashSet2 = Sets.newHashSet();
                            while (cursorRawQuery2.moveToNext()) {
                                hashSetNewHashSet2.add(cursorRawQuery2.getString(1));
                            }
                            cursorRawQuery2.close();
                            Log.i(TAG, "userTable All columns: " + hashSetNewHashSet2);
                            zContains = hashSetNewHashSet2.contains(UserConfigure.Columns.BG_DELAY_MIN);
                        } catch (Throwable th3) {
                            th2 = th3;
                            if (cursorRawQuery2 == null) {
                                throw th2;
                            }
                            cursorRawQuery2.close();
                            throw th2;
                        }
                    } catch (Throwable th4) {
                        cursorRawQuery2 = null;
                        th2 = th4;
                    }
                }
                Log.i(TAG, "version5Valid: " + zContains);
                return zContains;
            } catch (Throwable th5) {
                th = th5;
                if (cursorRawQuery == null) {
                    throw th;
                }
                cursorRawQuery.close();
                throw th;
            }
        } catch (Throwable th6) {
            cursorRawQuery = null;
            th = th6;
        }
    }

    private boolean version6Valid(SQLiteDatabase sQLiteDatabase) throws Throwable {
        Cursor cursorRawQuery;
        Throwable th;
        boolean zContains;
        Cursor cursorRawQuery2;
        Throwable th2;
        try {
            cursorRawQuery = sQLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            try {
                HashSet hashSetNewHashSet = Sets.newHashSet();
                while (true) {
                    zContains = false;
                    if (!cursorRawQuery.moveToNext()) {
                        break;
                    }
                    hashSetNewHashSet.add(cursorRawQuery.getString(0));
                }
                cursorRawQuery.close();
                Log.i(TAG, "All tables: " + hashSetNewHashSet);
                if (hashSetNewHashSet.contains(UserConfigure.TABLE) && hashSetNewHashSet.contains(SimpleSettings.Misc.TABLE)) {
                    zContains = true;
                }
                if (zContains) {
                    try {
                        cursorRawQuery2 = sQLiteDatabase.rawQuery("PRAGMA table_info(misc)", null);
                        try {
                            HashSet hashSetNewHashSet2 = Sets.newHashSet();
                            while (cursorRawQuery2.moveToNext()) {
                                hashSetNewHashSet2.add(cursorRawQuery2.getString(1));
                            }
                            cursorRawQuery2.close();
                            Log.i(TAG, "misc All columns: " + hashSetNewHashSet2);
                            zContains = hashSetNewHashSet2.contains("name");
                        } catch (Throwable th3) {
                            th2 = th3;
                            if (cursorRawQuery2 == null) {
                                throw th2;
                            }
                            cursorRawQuery2.close();
                            throw th2;
                        }
                    } catch (Throwable th4) {
                        cursorRawQuery2 = null;
                        th2 = th4;
                    }
                }
                Log.i(TAG, "version6Valid: " + zContains);
                return zContains;
            } catch (Throwable th5) {
                th = th5;
                if (cursorRawQuery != null) {
                    cursorRawQuery.close();
                }
                throw th;
            }
        } catch (Throwable th6) {
            cursorRawQuery = null;
            th = th6;
        }
    }

    @Override // com.miui.powerkeeper.provider.SQLiteHelper
    public boolean isValidDatabase(SQLiteDatabase sQLiteDatabase) {
        boolean zVersion6Valid = sQLiteDatabase.getVersion() == 6 ? version6Valid(sQLiteDatabase) : false;
        Log.i(TAG, "isValidDatabase: " + zVersion6Valid);
        return zVersion6Valid;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        updateDatabase(sQLiteDatabase, 0, 6);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        Log.e(TAG, "Downgrade database from version " + i2 + " to " + i3);
        updateDatabase(sQLiteDatabase, 0, i3);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        updateDatabase(sQLiteDatabase, i2, 6);
    }
}
