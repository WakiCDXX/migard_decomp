package com.miui.powerkeeper.millet.bright;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.Log;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.xiaomi.mipush.sdk.Constants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import miui.yellowpage.Tag;

/* JADX INFO: loaded from: classes.dex */
public class BrightMode {
    private static volatile BrightMode mInstance;
    private Context mContext;
    private Map<String, Set<String>> mScenarioApps = new ArrayMap();
    private Set<String> mCloudWhite = new HashSet();
    private Set<String> mCloudBlack = new HashSet();
    private Map<String, Integer> mScenarioEnable = new HashMap();
    private Set<String> forbidFreezeScenarios = new HashSet();
    private String TAG = "BrightMode";
    private boolean DEBUG = BrightController.DEBUG;
    private boolean mBrightMilletEnable = true;
    private int mBrightMilletMode = SystemProperties.getInt("persist.sys.brightmillet.mode", 0);

    private BrightMode(Context context) {
        this.mContext = null;
        this.mContext = context;
        if (dealBrightCloudWhiteList()) {
            return;
        }
        this.forbidFreezeScenarios.add("pay");
        this.forbidFreezeScenarios.add("nav");
        this.forbidFreezeScenarios.add("office");
    }

    private void dealCommonKey(String str) {
        this.mScenarioApps.clear();
        String[] strArrSplit = str.split("--");
        if (strArrSplit.length == 1 && str.endsWith("--")) {
            setWhiteBlackApps(strArrSplit[0], this.mCloudWhite);
            return;
        }
        if (strArrSplit.length == 2) {
            if (!strArrSplit[0].isEmpty()) {
                setWhiteBlackApps(strArrSplit[0], this.mCloudWhite);
            }
            if (strArrSplit[1].isEmpty()) {
                return;
            }
            setWhiteBlackApps(strArrSplit[1], this.mCloudBlack);
        }
    }

    private void dealDeviceKey(String str) {
        for (String str2 : str.split(",")) {
            if (Build.DEVICE.equals(str2.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER)[0])) {
                boolean z2 = this.mBrightMilletEnable && "true".equals(str2.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER)[1]);
                this.mBrightMilletEnable = z2;
                if (!z2) {
                    return;
                }
                if ("white".equals(str2.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER)[2])) {
                    this.mBrightMilletMode = 1;
                } else if ("black".equals(str2.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER)[2])) {
                    this.mBrightMilletMode = 0;
                }
            }
        }
    }

    private int dealPairs(String[] strArr) {
        if (strArr[0].equals("enable")) {
            boolean zEquals = "true".equals(strArr[1]);
            this.mBrightMilletEnable = zEquals;
            return !zEquals ? -1 : 1;
        }
        if (strArr[0].equals(Tag.TagPhone.IMEI)) {
            dealDeviceKey(strArr[1]);
            return 1;
        }
        if (!strArr[0].equals("forbid")) {
            return 0;
        }
        String[] strArrSplit = strArr[1].split(",");
        this.forbidFreezeScenarios.clear();
        for (String str : strArrSplit) {
            this.forbidFreezeScenarios.add(str);
        }
        return 1;
    }

    public static BrightMode getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BrightMode.class) {
                try {
                    if (mInstance == null) {
                        mInstance = new BrightMode(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return mInstance;
    }

    private boolean isIncludeCloudBlack(String str) {
        if (this.mScenarioApps.size() > 0) {
            for (Map.Entry<String, Set<String>> entry : this.mScenarioApps.entrySet()) {
                if (entry != null && entry.getValue() != null && entry.getValue().contains(str)) {
                    return true;
                }
            }
        }
        boolean zContains = this.mCloudBlack.contains(str);
        if (zContains) {
            Log.d(this.TAG, "isIncludeCloudBlack pkg=" + str);
        }
        return zContains;
    }

    private boolean isIncludeCloudWhite(String str) {
        return this.mCloudWhite.contains(str);
    }

    private void parseScenarioType(String str) {
        String[] strArrSplit = str.split(":");
        String[] strArrSplit2 = strArrSplit[0].split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
        if (this.DEBUG) {
            String str2 = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("nameEnable 0: ");
            sb.append(strArrSplit2[0]);
            sb.append(" ");
            sb.append(strArrSplit2.length == 2 ? strArrSplit2[1] : "");
            Log.d(str2, sb.toString());
        }
        if (strArrSplit2.length == 2) {
            this.mScenarioEnable.put(strArrSplit2[0], Integer.valueOf("true".equals(strArrSplit2[1]) ? 1 : 0));
        }
        if (strArrSplit[0].equals(BrightScenarioBase.mCommonKey)) {
            dealCommonKey(strArrSplit[1]);
        } else {
            if (strArrSplit2.length == 2) {
                this.mScenarioEnable.put(strArrSplit2[0], Integer.valueOf("true".equals(strArrSplit2[1]) ? 1 : 0));
            }
            this.mScenarioApps.remove(strArrSplit2[0]);
            setScenarioApps(strArrSplit[1], strArrSplit2[0], this.mScenarioApps);
        }
        if (this.DEBUG) {
            Log.d(this.TAG, " parserType white:" + this.mScenarioApps);
        }
    }

    private void setScenarioApps(String str, String str2, Map<String, Set<String>> map) {
        String[] strArrSplit = str.split(",");
        HashSet hashSet = new HashSet();
        for (String str3 : strArrSplit) {
            hashSet.add(str3);
        }
        map.put(str2, hashSet);
    }

    private void setWhiteBlackApps(String str, Set<String> set) {
        set.clear();
        for (String str2 : str.split(",")) {
            set.add(str2);
        }
    }

    public boolean dealBrightCloudWhiteList() {
        int iDealPairs;
        String cloudFrozenNewBright = FrozenAppController.newCloudBrightFrozen;
        if (cloudFrozenNewBright == null) {
            cloudFrozenNewBright = GlobalFeatureConfigureHelper.getCloudFrozenNewBright(this.mContext);
        }
        if (cloudFrozenNewBright == null || cloudFrozenNewBright.isEmpty()) {
            return false;
        }
        try {
            if (this.DEBUG) {
                Log.d(this.TAG, "cloud getdata");
            }
            String[] strArrSplit = cloudFrozenNewBright.split(";");
            for (int i2 = 0; i2 < strArrSplit.length && (iDealPairs = dealPairs(strArrSplit[i2].split(":"))) != -1; i2++) {
                if (iDealPairs != 1) {
                    parseScenarioType(strArrSplit[i2]);
                }
            }
            if (this.DEBUG) {
                Log.d(this.TAG, "cloud getdata end");
            }
        } catch (Exception e2) {
            Log.e(this.TAG, "dealBrightCloudWhiteList : " + cloudFrozenNewBright, e2);
        }
        return true;
    }

    public boolean getBrightEnable() {
        return this.mBrightMilletEnable;
    }

    public int getBrightMode() {
        return this.mBrightMilletMode;
    }

    public Set<String> getForbidTypes() {
        return this.forbidFreezeScenarios;
    }

    public Map<String, Set<String>> getScenarioApps() {
        return this.mScenarioApps;
    }

    public int getScenarioEnable(String str) {
        if (this.mScenarioEnable.containsKey(str)) {
            return this.mScenarioEnable.get(str).intValue();
        }
        return -1;
    }

    public boolean isAppInclude(String str) {
        if (this.mBrightMilletMode == 0) {
            return isIncludeCloudBlack(str);
        }
        return !isIncludeCloudWhite(str);
    }
}
