package com.miui.powerkeeper.provider;

import android.os.SystemProperties;
import com.xiaomi.push.service.PushConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class PreSetApp {
    private static List<String> mCustomizedISP;
    private static Map<String, Boolean> mPreSetAppMap = new HashMap();

    static {
        ArrayList arrayList = new ArrayList();
        mCustomizedISP = arrayList;
        arrayList.clear();
        mCustomizedISP.add("com.trendmicro.tmmssuite.oemkddi.jp");
        mCustomizedISP.add("com.airtelx.airtelkiosk");
        mPreSetAppMap.clear();
        if (!SystemProperties.getBoolean("ro.mi.os.custfeatureresolve", false)) {
            Map<String, Boolean> map = mPreSetAppMap;
            Boolean bool = Boolean.TRUE;
            map.put("com.android.phone", bool);
            mPreSetAppMap.put("com.android.deskclock", bool);
            mPreSetAppMap.put(PushConstants.MMS_PACKAGE_NAME, bool);
            mPreSetAppMap.put("com.android.providers.downloads.ui", bool);
            mPreSetAppMap.put("com.controlmovil.telcel", bool);
            mPreSetAppMap.put("com.kddi.android.cmail", bool);
            mPreSetAppMap.put("com.payjoy.status", bool);
            return;
        }
        try {
            Class<?> cls = Class.forName("miui.os.HyperOSCustFeatureResolve");
            Map map2 = (Map) cls.getDeclaredMethod("getMap", String.class).invoke(cls, "config_disable_background_apps");
            if (map2 != null) {
                for (Map.Entry entry : map2.entrySet()) {
                    mPreSetAppMap.put((String) entry.getKey(), Boolean.valueOf((String) entry.getValue()));
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static Map<String, Boolean> getPreSetAppMap() {
        return mPreSetAppMap;
    }

    public static boolean isCustomizedIspApp(String str) {
        return mCustomizedISP.contains(str);
    }

    public static boolean isPreSetApp(String str) {
        return mPreSetAppMap.containsKey(str);
    }
}
