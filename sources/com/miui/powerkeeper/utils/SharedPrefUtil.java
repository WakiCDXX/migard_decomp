package com.miui.powerkeeper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class SharedPrefUtil {
    private static final String DEF_SHAREDPREF_NAME = "com.miui.powerkeeper_preferences";
    private SharedPreferences.Editor mEditor;
    private String mName;
    private final Object mObject = new Object();
    private SharedPreferences mPrefs;
    private static List<SharedPrefUtil> mSharedPrefWithNameList = new ArrayList();
    private static final Object mRulesLock = new Object();

    private SharedPrefUtil(SharedPreferences sharedPreferences, String str) {
        this.mPrefs = sharedPreferences;
        this.mName = str;
        this.mEditor = sharedPreferences.edit();
    }

    public static SharedPrefUtil getInstance(Context context) {
        return getSharedPrefWithNameInstance(context, DEF_SHAREDPREF_NAME);
    }

    private static SharedPrefUtil getSharedPrefWithNameInstance(Context context, String str) {
        synchronized (mRulesLock) {
            try {
                for (SharedPrefUtil sharedPrefUtil : mSharedPrefWithNameList) {
                    if (sharedPrefUtil.mName.equalsIgnoreCase(str)) {
                        return sharedPrefUtil;
                    }
                }
                SharedPrefUtil sharedPrefUtil2 = new SharedPrefUtil(context.getSharedPreferences(str, 0), str);
                mSharedPrefWithNameList.add(sharedPrefUtil2);
                while (mSharedPrefWithNameList.size() > 5) {
                    mSharedPrefWithNameList.remove(0);
                }
                return sharedPrefUtil2;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean clearAll() {
        boolean zCommit;
        synchronized (this.mObject) {
            this.mEditor.clear();
            zCommit = this.mEditor.commit();
        }
        return zCommit;
    }

    public boolean contains(String str) {
        boolean zContains;
        synchronized (this.mObject) {
            zContains = this.mPrefs.contains(str);
        }
        return zContains;
    }

    public boolean containsKey(String str) {
        boolean zContains;
        synchronized (this.mObject) {
            zContains = this.mPrefs.contains(str);
        }
        return zContains;
    }

    public boolean getBooleanValue(String str, boolean z2) {
        boolean z3;
        synchronized (this.mObject) {
            z3 = this.mPrefs.getBoolean(str, z2);
        }
        return z3;
    }

    public float getFloatValue(String str, float f2) {
        float f3;
        synchronized (this.mObject) {
            f3 = this.mPrefs.getFloat(str, f2);
        }
        return f3;
    }

    public int getIntValue(String str, int i2) {
        int i3;
        synchronized (this.mObject) {
            i3 = this.mPrefs.getInt(str, i2);
        }
        return i3;
    }

    public long getLongValue(String str, long j2) {
        long j3;
        synchronized (this.mObject) {
            j3 = this.mPrefs.getLong(str, j2);
        }
        return j3;
    }

    public String getStringValue(String str, String str2) {
        String string;
        synchronized (this.mObject) {
            string = this.mPrefs.getString(str, str2);
        }
        return string;
    }

    public boolean keyValueChanged(String str, String str2) {
        boolean zEqualsIgnoreCase;
        synchronized (this.mObject) {
            zEqualsIgnoreCase = getStringValue(str, "").equalsIgnoreCase(str2);
        }
        return zEqualsIgnoreCase;
    }

    public boolean removeKey(String str) {
        boolean zCommit;
        synchronized (this.mObject) {
            this.mEditor.remove(str);
            zCommit = this.mEditor.commit();
        }
        return zCommit;
    }

    public boolean save(String str, String str2) {
        boolean zCommit;
        synchronized (this.mObject) {
            this.mEditor.putString(str, str2);
            zCommit = this.mEditor.commit();
        }
        return zCommit;
    }

    public static SharedPrefUtil getInstance(Context context, String str) {
        return str == null ? getInstance(context) : getSharedPrefWithNameInstance(context, str);
    }

    public boolean keyValueChanged(String str, boolean z2) {
        synchronized (this.mObject) {
            try {
                if (getBooleanValue(str, false)) {
                    return z2;
                }
                return !z2;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean save(String str, boolean z2) {
        boolean zCommit;
        synchronized (this.mObject) {
            this.mEditor.putBoolean(str, z2);
            zCommit = this.mEditor.commit();
        }
        return zCommit;
    }

    public boolean save(String str, int i2) {
        boolean zCommit;
        synchronized (this.mObject) {
            this.mEditor.putInt(str, i2);
            zCommit = this.mEditor.commit();
        }
        return zCommit;
    }

    public boolean keyValueChanged(String str, int i2) {
        boolean z2;
        synchronized (this.mObject) {
            z2 = getIntValue(str, 0) == i2;
        }
        return z2;
    }

    public boolean save(String str, long j2) {
        boolean zCommit;
        synchronized (this.mObject) {
            this.mEditor.putLong(str, j2);
            zCommit = this.mEditor.commit();
        }
        return zCommit;
    }

    public boolean keyValueChanged(String str, long j2) {
        boolean z2;
        synchronized (this.mObject) {
            z2 = getLongValue(str, 0L) == j2;
        }
        return z2;
    }

    public boolean keyValueChanged(String str, float f2) {
        boolean z2;
        synchronized (this.mObject) {
            z2 = getFloatValue(str, 0.0f) == f2;
        }
        return z2;
    }

    public boolean save(String str, float f2) {
        boolean zCommit;
        synchronized (this.mObject) {
            this.mEditor.putFloat(str, f2);
            zCommit = this.mEditor.commit();
        }
        return zCommit;
    }
}
