package com.miui.powerkeeper.provider;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class PreSetGroup {
    private static HashMap<String, Integer> mGroupHeadUidMap = new HashMap<>();

    public static String getGroupHead(int i2) {
        String key = "";
        if (mGroupHeadUidMap.containsValue(Integer.valueOf(i2))) {
            for (Map.Entry<String, Integer> entry : mGroupHeadUidMap.entrySet()) {
                if (entry.getValue().equals(Integer.valueOf(i2))) {
                    key = entry.getKey();
                }
            }
        }
        return key;
    }

    public static void initGroup() {
        mGroupHeadUidMap.clear();
        mGroupHeadUidMap.put("android", -1);
        mGroupHeadUidMap.put("com.android.providers.telephony", -1);
        mGroupHeadUidMap.put("com.android.contacts", -1);
        mGroupHeadUidMap.put("com.google.android.gsf", -1);
        mGroupHeadUidMap.put("com.android.providers.downloads.ui", -1);
    }

    public static boolean isGroupHead(String str) {
        return mGroupHeadUidMap.containsKey(str);
    }

    public static boolean isGroupUid(int i2) {
        return mGroupHeadUidMap.containsValue(Integer.valueOf(i2));
    }

    public static void setGroupUid(String str, int i2) {
        if (mGroupHeadUidMap.containsKey(str)) {
            mGroupHeadUidMap.put(str, Integer.valueOf(i2));
        }
    }
}
