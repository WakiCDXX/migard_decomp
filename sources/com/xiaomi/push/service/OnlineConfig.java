package com.xiaomi.push.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.CollectionUtils;
import com.xiaomi.channel.commonutils.string.Base64Coder;
import com.xiaomi.xmpush.thrift.ConfigKey;
import com.xiaomi.xmpush.thrift.ConfigListType;
import java.util.HashSet;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class OnlineConfig {
    private static final String OC_PREFIX = "oc_";
    private static final String OC_VERSION_PREFIX = "oc_version_";
    private static final String SP_NAME_CUSTOM = "mipush_oc_custom";
    private static final String SP_NAME_NORMAL = "mipush_oc_normal";
    private static volatile OnlineConfig instance;
    private HashSet<OCUpdateCallback> mCallbacks = new HashSet<>();
    protected SharedPreferences spCustom;
    protected SharedPreferences spNormal;

    public static abstract class OCUpdateCallback implements Runnable {
        private String mDescription;
        private int mId;

        public OCUpdateCallback(int i2, String str) {
            this.mId = i2;
            this.mDescription = str;
        }

        public boolean equals(Object obj) {
            return (obj instanceof OCUpdateCallback) && this.mId == ((OCUpdateCallback) obj).mId;
        }

        public int hashCode() {
            return this.mId;
        }

        protected abstract void onCallback();

        @Override // java.lang.Runnable
        public final void run() {
            onCallback();
        }
    }

    private OnlineConfig(Context context) {
        this.spNormal = context.getSharedPreferences(SP_NAME_NORMAL, 0);
        this.spCustom = context.getSharedPreferences(SP_NAME_CUSTOM, 0);
    }

    public static OnlineConfig getInstance(Context context) {
        if (instance == null) {
            synchronized (OnlineConfig.class) {
                try {
                    if (instance == null) {
                        instance = new OnlineConfig(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return instance;
    }

    private String getOcKey(int i2) {
        return OC_PREFIX + i2;
    }

    private String getVersionKey(ConfigListType configListType) {
        return OC_VERSION_PREFIX + configListType.getValue();
    }

    private void putConfig(SharedPreferences.Editor editor, Pair<Integer, Object> pair, String str) {
        Object obj = pair.second;
        if (obj instanceof Integer) {
            editor.putInt(str, ((Integer) obj).intValue());
            return;
        }
        if (obj instanceof Long) {
            editor.putLong(str, ((Long) obj).longValue());
            return;
        }
        if (!(obj instanceof String)) {
            if (obj instanceof Boolean) {
                editor.putBoolean(str, ((Boolean) obj).booleanValue());
            }
        } else {
            String str2 = (String) obj;
            if (str.equals(getOcKey(ConfigKey.AppIsInstalledList.getValue()))) {
                editor.putString(str, Base64Coder.encodeString(str2));
            } else {
                editor.putString(str, str2);
            }
        }
    }

    public synchronized void addOCUpdateCallbacks(OCUpdateCallback oCUpdateCallback) {
        if (!this.mCallbacks.contains(oCUpdateCallback)) {
            this.mCallbacks.add(oCUpdateCallback);
        }
    }

    public synchronized void clearCallbacks() {
        this.mCallbacks.clear();
    }

    public boolean getBooleanValue(int i2, boolean z2) {
        try {
            String ocKey = getOcKey(i2);
            if (this.spCustom.contains(ocKey)) {
                return this.spCustom.getBoolean(ocKey, false);
            }
            return this.spNormal.contains(ocKey) ? this.spNormal.getBoolean(ocKey, false) : z2;
        } catch (Exception e2) {
            MyLog.w(i2 + " oc boolean error " + e2);
            return z2;
        }
    }

    public int getIntValue(int i2, int i3) {
        try {
            String ocKey = getOcKey(i2);
            if (this.spCustom.contains(ocKey)) {
                return this.spCustom.getInt(ocKey, 0);
            }
            return this.spNormal.contains(ocKey) ? this.spNormal.getInt(ocKey, 0) : i3;
        } catch (Exception e2) {
            MyLog.w(i2 + " oc int error " + e2);
            return i3;
        }
    }

    public long getLongValue(int i2, long j2) {
        try {
            String ocKey = getOcKey(i2);
            if (this.spCustom.contains(ocKey)) {
                return this.spCustom.getLong(ocKey, 0L);
            }
            return this.spNormal.contains(ocKey) ? this.spNormal.getLong(ocKey, 0L) : j2;
        } catch (Exception e2) {
            MyLog.w(i2 + " oc long error " + e2);
            return j2;
        }
    }

    public int getNormalVersion(ConfigListType configListType, int i2) {
        try {
            return this.spNormal.getInt(getVersionKey(configListType), i2);
        } catch (Exception e2) {
            MyLog.w(configListType + " version error " + e2);
            return i2;
        }
    }

    public String getStringValue(int i2, String str) {
        try {
            String ocKey = getOcKey(i2);
            if (this.spCustom.contains(ocKey)) {
                return this.spCustom.getString(ocKey, null);
            }
            return this.spNormal.contains(ocKey) ? this.spNormal.getString(ocKey, null) : str;
        } catch (Exception e2) {
            MyLog.w(i2 + " oc string error " + e2);
            return str;
        }
    }

    void runCallback() {
        MyLog.v("OC_Callback : receive new oc data");
        HashSet<OCUpdateCallback> hashSet = new HashSet();
        synchronized (this) {
            hashSet.addAll(this.mCallbacks);
        }
        for (OCUpdateCallback oCUpdateCallback : hashSet) {
            if (oCUpdateCallback != null) {
                oCUpdateCallback.run();
            }
        }
        hashSet.clear();
    }

    public void updateCustomConfigs(List<Pair<Integer, Object>> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        SharedPreferences.Editor editorEdit = this.spCustom.edit();
        for (Pair<Integer, Object> pair : list) {
            Object obj = pair.first;
            if (obj != null) {
                String ocKey = getOcKey(((Integer) obj).intValue());
                if (pair.second == null) {
                    editorEdit.remove(ocKey);
                } else {
                    putConfig(editorEdit, pair, ocKey);
                }
            }
        }
        editorEdit.apply();
    }

    public void updateNormalConfigs(List<Pair<ConfigListType, Integer>> list, List<Pair<Integer, Object>> list2) {
        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(list2)) {
            MyLog.w("not update oc, because versions or configs are empty");
            return;
        }
        SharedPreferences.Editor editorEdit = this.spNormal.edit();
        editorEdit.clear();
        for (Pair<ConfigListType, Integer> pair : list) {
            Object obj = pair.first;
            if (obj != null && pair.second != null) {
                editorEdit.putInt(getVersionKey((ConfigListType) obj), ((Integer) pair.second).intValue());
            }
        }
        for (Pair<Integer, Object> pair2 : list2) {
            Object obj2 = pair2.first;
            if (obj2 != null && pair2.second != null) {
                putConfig(editorEdit, pair2, getOcKey(((Integer) obj2).intValue()));
            }
        }
        editorEdit.apply();
    }
}
