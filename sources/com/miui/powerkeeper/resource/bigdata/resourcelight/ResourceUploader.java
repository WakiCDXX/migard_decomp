package com.miui.powerkeeper.resource.bigdata.resourcelight;

import android.content.Context;
import android.util.SparseBooleanArray;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ResourceUploader {
    private static SparseBooleanArray mUidServiceState = new SparseBooleanArray();
    private static ResourceUploader sInstance;
    public String Name;
    public Context mContext;
    public String TAG = "ResourceUploader";
    private Set<UidChange> mUidVersionChange = new HashSet();

    private class UidChange {
        public long count;
        public long install;
        public String packageName;
        public long remove;
        public int uid;
        public long upgrade;

        private UidChange() {
            this.install = 0L;
            this.remove = 0L;
            this.upgrade = 0L;
            this.count = 1L;
        }
    }

    public ResourceUploader(Context context) {
        this.mContext = context;
    }

    public static synchronized ResourceUploader getInstance(Context context) {
        try {
            if (sInstance == null) {
                sInstance = new ResourceUploader(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public boolean getPackageVersionChange(String str) {
        Iterator<UidChange> it = this.mUidVersionChange.iterator();
        while (it.hasNext()) {
            if (str.equals(it.next().packageName)) {
                return true;
            }
        }
        return false;
    }

    public boolean getUidState(int i2) {
        return mUidServiceState.get(i2, false);
    }

    public boolean getUidVersionChange(int i2) {
        Iterator<UidChange> it = this.mUidVersionChange.iterator();
        while (it.hasNext()) {
            if (i2 == it.next().uid) {
                return true;
            }
        }
        return false;
    }

    public void resetUidVersionChange() {
        this.mUidVersionChange.clear();
    }

    public void setUidState(int i2, boolean z2) {
        mUidServiceState.put(i2, z2);
    }

    public void setUidVersionChange(int i2, String str) {
        for (UidChange uidChange : this.mUidVersionChange) {
            if (i2 == uidChange.uid && str.equals(uidChange.packageName)) {
                uidChange.count++;
                return;
            }
        }
        UidChange uidChange2 = new UidChange();
        uidChange2.uid = i2;
        uidChange2.packageName = str;
        this.mUidVersionChange.add(uidChange2);
    }
}
