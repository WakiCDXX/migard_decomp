package com.miui.powerkeeper.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.internal.util.ArrayUtils;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import com.miui.powerkeeper.appcontrol.HideModeStateMachineConfig;
import com.miui.powerkeeper.controller.ActiveStateController;
import com.miui.powerkeeper.utils.GmsCoreUtils;
import com.miui.powerkeeper.utils.PackageUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperAppConfigure implements Cloneable {
    private static final int SCENARIO_B2 = 3;
    private static final int SCENARIO_B3 = 10;
    private static final int SCENARIO_B4 = 11;
    private static final int SCENARIO_D0 = 12;
    private static final int SCENARIO_D1 = 13;
    private static final int SCENARIO_K1 = 7;
    private static final int SCENARIO_N0 = 0;
    private static final int SCENARIO_N1 = 8;
    private static final int SCENARIO_P1 = 1;
    private static final int SCENARIO_S1 = 6;
    private static final int SCENARIO_W0 = 9;
    private static final int SCENARIO_W2 = 2;
    private boolean add2DeviceIdleWhiteList;
    private int bgClusterPolicy;
    private int bgDataDelayCount;
    private int bgDataDelayMinutes;
    private boolean bgDataEnable;
    private int bgDataMaxInactiveCount;
    private int bgDataMinDataKb;
    private int bgIdleDelayMinutes;
    private int bgKillDelayMinutes;
    private int bgKillDelayMinutesHot;
    private int bgKillDelayMinutesUser;
    public int bgKillPolicy;
    private int bgLocationDelayMinutes;
    private int bgLocationDelayMinutesHot;
    private int bgSDelayMinutes;
    public String bgSDisableType;
    private boolean hotFeature;
    private String pkg;
    private int scenario;
    private int uid;

    public PowerKeeperAppConfigure() {
        this.pkg = null;
        this.uid = -1;
        setParamToDefault();
    }

    public static List<PowerKeeperAppConfigure> createList(Context context, int i2) {
        return createList(context, i2, UserConfigureHelper.createFromTable(context, i2));
    }

    public static Map<String, PowerKeeperAppConfigure> createMap(Context context, int i2) {
        return createMap(context, i2, UserConfigureHelper.createFromTable(context, i2));
    }

    public static PowerKeeperAppConfigure createOne(Context context, int i2, UserConfigureHelper userConfigureHelper) {
        if (userConfigureHelper == null) {
            return null;
        }
        PowerKeeperAppConfigure powerKeeperAppConfigure = new PowerKeeperAppConfigure();
        Bundle bundleQueryGlobalConfigure = GlobalFeatureConfigureHelper.queryGlobalConfigure(context, i2);
        fillContent(context, i2, powerKeeperAppConfigure, userConfigureHelper, bundleQueryGlobalConfigure, GlobalFeatureConfigureHelper.getUserConfigure(bundleQueryGlobalConfigure), GlobalFeatureConfigureHelper.getSystemNonCoreApps(bundleQueryGlobalConfigure), GlobalFeatureConfigureHelper.getUltimateSpecialApps(bundleQueryGlobalConfigure), GlobalFeatureConfigureHelper.getDozeWhiteListApps(bundleQueryGlobalConfigure));
        return powerKeeperAppConfigure;
    }

    /* JADX WARN: Code duplicated, block: B:100:0x0272  */
    /* JADX WARN: Code duplicated, block: B:102:0x0278  */
    /* JADX WARN: Code duplicated, block: B:104:0x0281  */
    /* JADX WARN: Code duplicated, block: B:106:0x0289  */
    /* JADX WARN: Code duplicated, block: B:109:0x029a  */
    /* JADX WARN: Code duplicated, block: B:111:0x02a3  */
    /* JADX WARN: Code duplicated, block: B:113:0x02ab  */
    /* JADX WARN: Code duplicated, block: B:116:0x02bc  */
    /* JADX WARN: Code duplicated, block: B:117:0x02c3  */
    /* JADX WARN: Code duplicated, block: B:119:0x02cb  */
    /* JADX WARN: Code duplicated, block: B:84:0x0231  */
    /* JADX WARN: Code duplicated, block: B:85:0x0238  */
    /* JADX WARN: Code duplicated, block: B:87:0x0240  */
    /* JADX WARN: Code duplicated, block: B:92:0x0253  */
    /* JADX WARN: Code duplicated, block: B:94:0x0259  */
    /* JADX WARN: Code duplicated, block: B:95:0x0260  */
    /* JADX WARN: Code duplicated, block: B:97:0x0268  */
    private static void fillCloudContent(Context context, int i2, PowerKeeperAppConfigure powerKeeperAppConfigure, UserConfigureHelper userConfigureHelper, Bundle bundle, List<String> list) {
        Cursor cursor;
        Bundle bundle2;
        int i3;
        String str;
        PowerKeeperAppConfigure powerKeeperAppConfigure2;
        Object obj;
        int i4;
        boolean zEquals;
        int i5;
        int i6;
        String str2;
        int i7;
        String packageName = userConfigureHelper.getPackageName();
        if (bundle.containsKey(GlobalFeatureConfigure.HOT_FEEDBACK_FEATURE)) {
            powerKeeperAppConfigure.hotFeature = Boolean.parseBoolean(bundle.getString(GlobalFeatureConfigure.HOT_FEEDBACK_FEATURE));
        }
        if (list.contains(packageName)) {
            powerKeeperAppConfigure.add2DeviceIdleWhiteList = true;
        }
        Cursor cursorQuery = context.getContentResolverForUser(UserHandle.OWNER).query(Uri.withAppendedPath(CloudAppConfigure.CONTENT_URI, packageName), null, null, null, null);
        if (cursorQuery == null || !cursorQuery.moveToFirst()) {
            cursor = cursorQuery;
            if (bundle.containsKey("bgData")) {
                powerKeeperAppConfigure.bgDataEnable = bundle.getString("bgData").equals("true");
            }
            if (bundle.containsKey("bgDataDelayTime")) {
                powerKeeperAppConfigure.bgDataDelayMinutes = Integer.parseInt(bundle.getString("bgDataDelayTime"));
            }
            if (bundle.containsKey("bgDataDelayCount")) {
                powerKeeperAppConfigure.bgDataDelayCount = Integer.parseInt(bundle.getString("bgDataDelayCount"));
            }
            if (bundle.containsKey("bgDataMinDataKb")) {
                powerKeeperAppConfigure.bgDataMinDataKb = Integer.parseInt(bundle.getString("bgDataMinDataKb"));
            }
            if (bundle.containsKey("bgDataMaxInactiveCount")) {
                powerKeeperAppConfigure.bgDataMaxInactiveCount = Integer.parseInt(bundle.getString("bgDataMaxInactiveCount"));
            }
            if (bundle.containsKey("bgLocation") ? bundle.getString("bgLocation").equals("true") : false) {
                powerKeeperAppConfigure.bgLocationDelayMinutes = -2;
            } else if (bundle.containsKey("bgLocationDelayTime")) {
                powerKeeperAppConfigure.bgLocationDelayMinutes = Integer.parseInt(bundle.getString("bgLocationDelayTime"));
            }
            if (bundle.containsKey("k_delay")) {
                powerKeeperAppConfigure.bgKillDelayMinutes = Integer.parseInt(bundle.getString("k_delay"));
            }
            if (bundle.containsKey("k_policy")) {
                powerKeeperAppConfigure.bgKillPolicy = Integer.parseInt(bundle.getString("k_policy"));
            }
            if (bundle.containsKey("c_policy") && powerKeeperAppConfigure.bgClusterPolicy != -1) {
                powerKeeperAppConfigure.bgClusterPolicy = Integer.parseInt(bundle.getString("c_policy"));
            }
            if (bundle.containsKey("s_delay")) {
                powerKeeperAppConfigure.bgSDelayMinutes = Integer.parseInt(bundle.getString("s_delay"));
            }
            if (bundle.containsKey("i_delay")) {
                powerKeeperAppConfigure.bgIdleDelayMinutes = Integer.parseInt(bundle.getString("i_delay"));
            }
            if (powerKeeperAppConfigure.hotFeature) {
                if (bundle.containsKey("l_delay_hot")) {
                    powerKeeperAppConfigure.bgLocationDelayMinutesHot = Integer.parseInt(bundle.getString("l_delay_hot"));
                }
                if (bundle.containsKey("k_delay_hot")) {
                    powerKeeperAppConfigure.bgKillDelayMinutesHot = Integer.parseInt(bundle.getString("k_delay_hot"));
                }
            }
        } else {
            int columnIndex = cursorQuery.getColumnIndex("bgData");
            int columnIndex2 = cursorQuery.getColumnIndex("bgDataDelayTime");
            int columnIndex3 = cursorQuery.getColumnIndex("bgDataDelayCount");
            int columnIndex4 = cursorQuery.getColumnIndex("bgDataMinDataKb");
            int columnIndex5 = cursorQuery.getColumnIndex("bgDataMaxInactiveCount");
            int columnIndex6 = cursorQuery.getColumnIndex("bgLocation");
            int columnIndex7 = cursorQuery.getColumnIndex("bgLocationDelayTime");
            int columnIndex8 = cursorQuery.getColumnIndex("k_delay");
            int columnIndex9 = cursorQuery.getColumnIndex("k_policy");
            int columnIndex10 = cursorQuery.getColumnIndex("c_policy");
            int columnIndex11 = cursorQuery.getColumnIndex("s_delay");
            int columnIndex12 = cursorQuery.getColumnIndex("s_disable_type");
            int columnIndex13 = cursorQuery.getColumnIndex("l_delay_hot");
            int columnIndex14 = cursorQuery.getColumnIndex("k_delay_hot");
            int columnIndex15 = cursorQuery.getColumnIndex("i_delay");
            if (cursorQuery.isNull(columnIndex)) {
                bundle2 = bundle;
                i3 = columnIndex14;
                str = "i_delay";
                powerKeeperAppConfigure2 = powerKeeperAppConfigure;
                obj = "true";
                i4 = columnIndex15;
                if (bundle2.containsKey("bgData")) {
                    powerKeeperAppConfigure2.bgDataEnable = bundle2.getString("bgData").equals(obj);
                }
            } else {
                str = "i_delay";
                i4 = columnIndex15;
                obj = "true";
                powerKeeperAppConfigure2 = powerKeeperAppConfigure;
                powerKeeperAppConfigure2.bgDataEnable = cursorQuery.getString(columnIndex).equals("true");
                bundle2 = bundle;
                i3 = columnIndex14;
            }
            if (!cursorQuery.isNull(columnIndex2)) {
                powerKeeperAppConfigure2.bgDataDelayMinutes = cursorQuery.getInt(columnIndex2);
            } else if (bundle2.containsKey("bgDataDelayTime")) {
                powerKeeperAppConfigure2.bgDataDelayMinutes = Integer.parseInt(bundle2.getString("bgDataDelayTime"));
            }
            if (!cursorQuery.isNull(columnIndex3)) {
                powerKeeperAppConfigure2.bgDataDelayCount = cursorQuery.getInt(columnIndex3);
            } else if (bundle2.containsKey("bgDataDelayCount")) {
                powerKeeperAppConfigure2.bgDataDelayCount = Integer.parseInt(bundle2.getString("bgDataDelayCount"));
            }
            if (!cursorQuery.isNull(columnIndex4)) {
                powerKeeperAppConfigure2.bgDataMinDataKb = cursorQuery.getInt(columnIndex4);
            } else if (bundle2.containsKey("bgDataMinDataKb")) {
                powerKeeperAppConfigure2.bgDataMinDataKb = Integer.parseInt(bundle2.getString("bgDataMinDataKb"));
            }
            if (!cursorQuery.isNull(columnIndex5)) {
                powerKeeperAppConfigure2.bgDataMaxInactiveCount = cursorQuery.getInt(columnIndex5);
            } else if (bundle2.containsKey("bgDataMaxInactiveCount")) {
                powerKeeperAppConfigure2.bgDataMaxInactiveCount = Integer.parseInt(bundle2.getString("bgDataMaxInactiveCount"));
            }
            if (cursorQuery.isNull(columnIndex6)) {
                zEquals = bundle2.containsKey("bgLocation") ? bundle2.getString("bgLocation").equals(obj) : false;
            } else {
                zEquals = cursorQuery.getString(columnIndex6).equals(obj);
            }
            if (zEquals) {
                powerKeeperAppConfigure2.bgLocationDelayMinutes = -2;
            } else if (!cursorQuery.isNull(columnIndex7)) {
                powerKeeperAppConfigure2.bgLocationDelayMinutes = cursorQuery.getInt(columnIndex7);
            } else if (bundle2.containsKey("bgLocationDelayTime")) {
                powerKeeperAppConfigure2.bgLocationDelayMinutes = Integer.parseInt(bundle2.getString("bgLocationDelayTime"));
            }
            if (!cursorQuery.isNull(columnIndex8)) {
                powerKeeperAppConfigure2.bgKillDelayMinutes = cursorQuery.getInt(columnIndex8);
            } else if (bundle2.containsKey("k_delay")) {
                powerKeeperAppConfigure2.bgKillDelayMinutes = Integer.parseInt(bundle2.getString("k_delay"));
            }
            if (!cursorQuery.isNull(columnIndex9)) {
                powerKeeperAppConfigure2.bgKillPolicy = cursorQuery.getInt(columnIndex9);
            } else if (bundle2.containsKey("k_policy")) {
                powerKeeperAppConfigure2.bgKillPolicy = Integer.parseInt(bundle2.getString("k_policy"));
            }
            if (cursorQuery.isNull(columnIndex10)) {
                i5 = -1;
            } else {
                i5 = -1;
                if (powerKeeperAppConfigure2.bgClusterPolicy != -1) {
                    powerKeeperAppConfigure2.bgClusterPolicy = cursorQuery.getInt(columnIndex10);
                }
                if (!cursorQuery.isNull(columnIndex11)) {
                    powerKeeperAppConfigure2.bgSDelayMinutes = cursorQuery.getInt(columnIndex11);
                } else if (bundle2.containsKey("s_delay")) {
                    powerKeeperAppConfigure2.bgSDelayMinutes = Integer.parseInt(bundle2.getString("s_delay"));
                }
                if (powerKeeperAppConfigure2.bgSDelayMinutes != -2 || powerKeeperAppConfigure2.bgLocationDelayMinutes == -2) {
                    if (!cursorQuery.isNull(columnIndex12)) {
                        powerKeeperAppConfigure2.bgSDisableType = cursorQuery.getString(columnIndex12);
                    } else if (bundle2.containsKey("s_disable_type")) {
                        powerKeeperAppConfigure2.bgSDisableType = bundle2.getString("s_disable_type");
                    }
                }
                if (powerKeeperAppConfigure2.hotFeature) {
                    if (!cursorQuery.isNull(columnIndex13)) {
                        powerKeeperAppConfigure2.bgLocationDelayMinutesHot = cursorQuery.getInt(columnIndex13);
                    } else if (bundle2.containsKey("l_delay_hot")) {
                        powerKeeperAppConfigure2.bgLocationDelayMinutesHot = Integer.parseInt(bundle2.getString("l_delay_hot"));
                    }
                    i7 = i3;
                    if (!cursorQuery.isNull(i7)) {
                        powerKeeperAppConfigure2.bgKillDelayMinutesHot = cursorQuery.getInt(i7);
                    } else if (bundle2.containsKey("k_delay_hot")) {
                        powerKeeperAppConfigure2.bgKillDelayMinutesHot = Integer.parseInt(bundle2.getString("k_delay_hot"));
                    }
                }
                i6 = i4;
                if (cursorQuery.isNull(i6)) {
                    str2 = str;
                    if (bundle2.containsKey(str2)) {
                        powerKeeperAppConfigure2.bgIdleDelayMinutes = Integer.parseInt(bundle2.getString(str2));
                    }
                } else {
                    powerKeeperAppConfigure2.bgIdleDelayMinutes = cursorQuery.getInt(i6);
                }
                cursor = cursorQuery;
            }
            if (bundle2.containsKey("c_policy") && powerKeeperAppConfigure2.bgClusterPolicy != i5) {
                powerKeeperAppConfigure2.bgClusterPolicy = Integer.parseInt(bundle2.getString("c_policy"));
            }
            if (!cursorQuery.isNull(columnIndex11)) {
                powerKeeperAppConfigure2.bgSDelayMinutes = cursorQuery.getInt(columnIndex11);
            } else if (bundle2.containsKey("s_delay")) {
                powerKeeperAppConfigure2.bgSDelayMinutes = Integer.parseInt(bundle2.getString("s_delay"));
            }
            if (powerKeeperAppConfigure2.bgSDelayMinutes != -2) {
                if (!cursorQuery.isNull(columnIndex12)) {
                    powerKeeperAppConfigure2.bgSDisableType = cursorQuery.getString(columnIndex12);
                } else if (bundle2.containsKey("s_disable_type")) {
                    powerKeeperAppConfigure2.bgSDisableType = bundle2.getString("s_disable_type");
                }
            } else if (!cursorQuery.isNull(columnIndex12)) {
                powerKeeperAppConfigure2.bgSDisableType = cursorQuery.getString(columnIndex12);
            } else if (bundle2.containsKey("s_disable_type")) {
                powerKeeperAppConfigure2.bgSDisableType = bundle2.getString("s_disable_type");
            }
            if (powerKeeperAppConfigure2.hotFeature) {
                if (!cursorQuery.isNull(columnIndex13)) {
                    powerKeeperAppConfigure2.bgLocationDelayMinutesHot = cursorQuery.getInt(columnIndex13);
                } else if (bundle2.containsKey("l_delay_hot")) {
                    powerKeeperAppConfigure2.bgLocationDelayMinutesHot = Integer.parseInt(bundle2.getString("l_delay_hot"));
                }
                i7 = i3;
                if (!cursorQuery.isNull(i7)) {
                    powerKeeperAppConfigure2.bgKillDelayMinutesHot = cursorQuery.getInt(i7);
                } else if (bundle2.containsKey("k_delay_hot")) {
                    powerKeeperAppConfigure2.bgKillDelayMinutesHot = Integer.parseInt(bundle2.getString("k_delay_hot"));
                }
            }
            i6 = i4;
            if (cursorQuery.isNull(i6)) {
                powerKeeperAppConfigure2.bgIdleDelayMinutes = cursorQuery.getInt(i6);
            } else {
                str2 = str;
                if (bundle2.containsKey(str2)) {
                    powerKeeperAppConfigure2.bgIdleDelayMinutes = Integer.parseInt(bundle2.getString(str2));
                }
            }
            cursor = cursorQuery;
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private static void fillContent(Context context, int i2, PowerKeeperAppConfigure powerKeeperAppConfigure, UserConfigureHelper userConfigureHelper, Bundle bundle, String str, List<String> list, List<String> list2, List<String> list3) {
        fillScenarioContent(context, i2, powerKeeperAppConfigure, userConfigureHelper, str, list, list2);
        fillCloudContent(context, i2, powerKeeperAppConfigure, userConfigureHelper, bundle, list3);
    }

    private static void fillScenarioContent(Context context, int i2, PowerKeeperAppConfigure powerKeeperAppConfigure, UserConfigureHelper userConfigureHelper, String str, List<String> list, List<String> list2) {
        String bgControl = userConfigureHelper.getBgControl();
        String packageName = userConfigureHelper.getPackageName();
        powerKeeperAppConfigure.bgKillDelayMinutesUser = userConfigureHelper.getBgDelayMin();
        powerKeeperAppConfigure.pkg = packageName;
        powerKeeperAppConfigure.uid = PackageUtil.getUidByPackageName(context, packageName, i2);
        boolean zIsSystemApp = PackageUtil.isSystemApp(context, userConfigureHelper.getPackageName(), i2);
        boolean zIsCore = UserHandle.isCore(powerKeeperAppConfigure.uid);
        if (!zIsCore && str.equals(GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_ULTIMATE_EXTRA) && !ArrayUtils.contains(SimpleSettings.Misc.getString(context, SimpleSettingKeys.KEY_USER_DECONFIGURED_APPS, "").split(":"), userConfigureHelper.getPackageName())) {
            powerKeeperAppConfigure.scenario = 7;
            return;
        }
        if (bgControl.equals("miuiAuto")) {
            if (zIsCore || ((zIsSystemApp && !list.contains(userConfigureHelper.getPackageName())) || GmsCoreUtils.isGmsCoreApp(userConfigureHelper.getPackageName()))) {
                powerKeeperAppConfigure.scenario = 0;
            } else if (str.equals("disable")) {
                powerKeeperAppConfigure.scenario = 12;
            } else if (str.equals("enhance")) {
                powerKeeperAppConfigure.scenario = 2;
            } else if (str.equals("performance")) {
                powerKeeperAppConfigure.scenario = 1;
            } else if (str.equals(GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_SLEEP)) {
                powerKeeperAppConfigure.scenario = 6;
            } else if (list2.contains(userConfigureHelper.getPackageName())) {
                powerKeeperAppConfigure.scenario = 2;
            } else {
                powerKeeperAppConfigure.scenario = 3;
            }
        } else if (bgControl.equals(UserConfigure.BG_CONTROL_NO_RESTRICT)) {
            if (str.equals("disable")) {
                powerKeeperAppConfigure.scenario = 0;
            } else {
                powerKeeperAppConfigure.scenario = 8;
            }
        } else if (bgControl.equals(UserConfigure.BG_CONTROL_RESTRICT_BG)) {
            if (str.equals("disable")) {
                powerKeeperAppConfigure.scenario = 13;
            } else if (str.equals("enhance")) {
                powerKeeperAppConfigure.scenario = 10;
            } else {
                powerKeeperAppConfigure.scenario = 11;
            }
        } else if (str.equals("disable")) {
            powerKeeperAppConfigure.scenario = 13;
        } else {
            powerKeeperAppConfigure.scenario = 7;
        }
        if (powerKeeperAppConfigure.scenario == 0 && str.equals("performance")) {
            powerKeeperAppConfigure.bgClusterPolicy = -1;
        }
    }

    private void setParamToDefault() {
        this.bgDataEnable = true;
        this.bgDataDelayMinutes = 0;
        this.bgDataDelayCount = -1;
        this.bgDataMinDataKb = -1;
        this.bgDataMaxInactiveCount = -1;
        this.add2DeviceIdleWhiteList = false;
        this.bgLocationDelayMinutes = -2;
        this.bgLocationDelayMinutesHot = -2;
        this.hotFeature = false;
        this.scenario = 0;
        this.bgKillDelayMinutes = -2;
        this.bgKillPolicy = 896;
        this.bgKillDelayMinutesHot = -2;
        this.bgKillDelayMinutesUser = -2;
        this.bgSDelayMinutes = -2;
        this.bgIdleDelayMinutes = -2;
        this.bgClusterPolicy = 0;
    }

    public void clearResource() {
        this.pkg = null;
    }

    public String getPackage() {
        return this.pkg;
    }

    public int getScenario() {
        return this.scenario;
    }

    public int getUid() {
        return this.uid;
    }

    public boolean isNoRestrict() {
        int i2 = this.scenario;
        return i2 == 8 || i2 == 0;
    }

    public void resetToDefault() {
        setParamToDefault();
    }

    public void setPackage(String str) {
        this.pkg = str;
    }

    public void setScenarioAsDisable(String str, boolean z2) {
        if (str.equals("miuiAuto")) {
            if (z2) {
                this.scenario = 2;
            } else {
                this.scenario = 0;
            }
        }
    }

    public void setScenarioAsSystemCoreException(String str, String str2) {
        if (str.equals("miuiAuto")) {
            this.scenario = 0;
        } else if (str.equals(UserConfigure.BG_CONTROL_NO_RESTRICT)) {
            this.scenario = 0;
        }
    }

    public void setScenarioAsSystemOtherException(String str, String str2) {
        if (!str.equals("miuiAuto") || str2.equals("disable")) {
            return;
        }
        this.scenario = 9;
    }

    public void setUid(int i2) {
        this.uid = i2;
    }

    /* JADX WARN: Code duplicated, block: B:8:0x0016  */
    public Bundle toActiveStateControllerParams() {
        Bundle bundle = new Bundle();
        int i2 = this.scenario;
        if (i2 != 1 && i2 != 2) {
            switch (i2) {
                case 8:
                case 9:
                case 10:
                    bundle.putInt("POLICY", 3);
                    break;
                default:
                    bundle.putInt("POLICY", 1);
                    break;
            }
        } else {
            bundle.putInt("POLICY", 3);
        }
        return bundle;
    }

    /* JADX WARN: Code duplicated, block: B:10:0x0019  */
    public Bundle toAppActiveCheckerParams() {
        Bundle bundle = new Bundle();
        int i2 = this.scenario;
        if (i2 != 1 && i2 != 2 && i2 != 6) {
            switch (i2) {
                case 8:
                case 9:
                case 10:
                    bundle.putInt("POLICY", 0);
                    bundle.putInt(ActiveStateController.KEY_DELAY_PERIOD_MINUTE, this.bgDataDelayMinutes);
                    bundle.putInt("MAX_PERIOD_COUNT", this.bgDataDelayCount);
                    bundle.putInt("MIN_DATA_KBYTES", this.bgDataMinDataKb);
                    bundle.putInt("MAX_INACTIVE_COUNT", this.bgDataMaxInactiveCount);
                    break;
                default:
                    bundle.putInt("POLICY", 1);
                    break;
            }
        } else {
            bundle.putInt("POLICY", 0);
            bundle.putInt(ActiveStateController.KEY_DELAY_PERIOD_MINUTE, this.bgDataDelayMinutes);
            bundle.putInt("MAX_PERIOD_COUNT", this.bgDataDelayCount);
            bundle.putInt("MIN_DATA_KBYTES", this.bgDataMinDataKb);
            bundle.putInt("MAX_INACTIVE_COUNT", this.bgDataMaxInactiveCount);
        }
        return bundle;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0021  */
    /* JADX WARN: Code duplicated, block: B:15:0x0025  */
    /* JADX WARN: Code duplicated, block: B:16:0x0029 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:17:0x002b  */
    /* JADX WARN: Code duplicated, block: B:18:0x002f  */
    /* JADX WARN: Code duplicated, block: B:19:0x0033  */
    /* JADX WARN: Code duplicated, block: B:21:0x0037  */
    /* JADX WARN: Code duplicated, block: B:22:0x003b A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:23:0x003d  */
    /* JADX WARN: Code duplicated, block: B:24:0x0041  */
    public Bundle toAppClusterControllerParams() {
        int i2;
        int i3;
        Bundle bundle = new Bundle();
        int i4 = this.scenario;
        if (i4 == 0 || i4 == 2) {
            i2 = this.bgClusterPolicy;
            if (i2 == -1) {
                bundle.putInt("POLICY", 0);
            } else if (i2 != 2) {
                bundle.putInt("POLICY", 1);
            } else {
                bundle.putInt("POLICY", 2);
            }
        } else if (i4 != 3 && i4 != 6 && i4 != 7) {
            switch (i4) {
                case 9:
                    i2 = this.bgClusterPolicy;
                    if (i2 == -1) {
                        bundle.putInt("POLICY", 0);
                    } else if (i2 != 2) {
                        bundle.putInt("POLICY", 2);
                    } else {
                        bundle.putInt("POLICY", 1);
                    }
                    break;
                case 10:
                case 11:
                    i3 = this.bgClusterPolicy;
                    if (i3 == -1) {
                        bundle.putInt("POLICY", 0);
                    } else if (i3 != 1) {
                        bundle.putInt("POLICY", 1);
                    } else {
                        bundle.putInt("POLICY", 2);
                    }
                    break;
                default:
                    bundle.putInt("POLICY", 0);
                    break;
            }
        } else {
            i3 = this.bgClusterPolicy;
            if (i3 == -1) {
                bundle.putInt("POLICY", 0);
            } else if (i3 != 1) {
                bundle.putInt("POLICY", 2);
            } else {
                bundle.putInt("POLICY", 1);
            }
        }
        return bundle;
    }

    public Bundle toAppIdleAppRuleCheckerParams() {
        Bundle bundle = new Bundle();
        switch (this.scenario) {
            case 0:
            case 8:
            case 9:
            case 12:
            case 13:
                bundle.putInt("POLICY", 0);
                return bundle;
            case 1:
            case 2:
            case 10:
                if (this.bgDataEnable) {
                    bundle.putInt("POLICY", 0);
                } else {
                    bundle.putInt("POLICY", 3);
                }
                return bundle;
            case 3:
            case 11:
                bundle.putInt("POLICY", 2);
                bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 3);
                if (this.hotFeature) {
                    bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 1);
                }
                return bundle;
            case 4:
            case 5:
            default:
                bundle.putInt("POLICY", 2);
                bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 3);
                return bundle;
            case 6:
            case 7:
                bundle.putInt("POLICY", 1);
                return bundle;
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x002d  */
    /* JADX WARN: Code duplicated, block: B:16:0x0032  */
    /* JADX WARN: Code duplicated, block: B:17:0x0036  */
    /* JADX WARN: Code duplicated, block: B:19:0x0039  */
    /* JADX WARN: Code duplicated, block: B:20:0x003e  */
    /* JADX WARN: Code duplicated, block: B:21:0x0047  */
    public Bundle toBGIdleAppRuleCheckerParams() {
        int i2;
        Bundle bundle = new Bundle();
        int i3 = this.scenario;
        if (i3 == 0) {
            bundle.putInt("POLICY", 0);
        } else if (i3 == 1 || i3 == 2) {
            i2 = this.bgIdleDelayMinutes;
            if (i2 == -2) {
                bundle.putInt("POLICY", 0);
            } else if (i2 != -1) {
                bundle.putInt("POLICY", 3);
            } else {
                bundle.putInt("POLICY", 2);
                bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, this.bgIdleDelayMinutes);
            }
        } else if (i3 != 12 && i3 != 13) {
            switch (i3) {
                case 6:
                case 7:
                    bundle.putInt("POLICY", 2);
                    bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 0);
                    break;
                case 8:
                case 9:
                    bundle.putInt("POLICY", 0);
                    break;
                case 10:
                    i2 = this.bgIdleDelayMinutes;
                    if (i2 == -2) {
                        bundle.putInt("POLICY", 0);
                    } else if (i2 != -1) {
                        bundle.putInt("POLICY", 2);
                        bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, this.bgIdleDelayMinutes);
                    } else {
                        bundle.putInt("POLICY", 3);
                    }
                    break;
                default:
                    bundle.putInt("POLICY", 2);
                    bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 1);
                    break;
            }
        } else {
            bundle.putInt("POLICY", 0);
        }
        return bundle;
    }

    /* JADX WARN: Code duplicated, block: B:15:0x0026  */
    /* JADX WARN: Code duplicated, block: B:17:0x002a  */
    /* JADX WARN: Code duplicated, block: B:18:0x002e  */
    public Bundle toDeviceIdleAppRuleCheckerParams() {
        Bundle bundle = new Bundle();
        int i2 = this.scenario;
        if (i2 == 0 || i2 == 1 || i2 == 2) {
            if (this.add2DeviceIdleWhiteList) {
                bundle.putInt("POLICY", 0);
            } else {
                bundle.putInt("POLICY", 1);
            }
        } else if (i2 == 8) {
            bundle.putInt("POLICY", 0);
        } else if (i2 != 9 && i2 != 12) {
            bundle.putInt("POLICY", 1);
        } else if (this.add2DeviceIdleWhiteList) {
            bundle.putInt("POLICY", 0);
        } else {
            bundle.putInt("POLICY", 1);
        }
        return bundle;
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0029  */
    /* JADX WARN: Code duplicated, block: B:18:0x0032  */
    /* JADX WARN: Code duplicated, block: B:24:0x0040  */
    public Bundle toFrozenAppRuleCheckerParams() {
        Bundle bundle = new Bundle();
        int i2 = this.scenario;
        if (i2 == 1 || i2 == 2) {
            if (!this.bgDataEnable || this.bgLocationDelayMinutes == -2) {
                bundle.putInt("POLICY", 0);
            } else {
                bundle.putInt("POLICY", 3);
            }
        } else if (i2 == 3) {
            bundle.putInt("POLICY", 2);
            bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 3);
        } else if (i2 == 6 || i2 == 7) {
            bundle.putInt("POLICY", 0);
        } else if (i2 != 10) {
            if (i2 != 11) {
                bundle.putInt("POLICY", 0);
            } else {
                bundle.putInt("POLICY", 2);
                bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 3);
            }
        } else if (this.bgDataEnable) {
            bundle.putInt("POLICY", 0);
        } else {
            bundle.putInt("POLICY", 0);
        }
        return bundle;
    }

    public Bundle toKillProcessAppRuleCheckerParams() {
        int i2;
        Bundle bundle = new Bundle();
        boolean z2 = (this.bgKillPolicy & 15) == 1;
        int i3 = this.scenario;
        if (i3 == 1) {
            int i4 = this.bgKillDelayMinutes;
            if (i4 == 0) {
                bundle.putInt("POLICY", 1);
            } else if (i4 > 0) {
                bundle.putInt("POLICY", 2);
                bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, this.bgKillDelayMinutes);
            } else {
                bundle.putInt("POLICY", 0);
            }
        } else if (i3 == 2 || i3 == 3 || i3 == 6) {
            int i5 = this.bgKillDelayMinutes;
            if (i5 == -2 && !z2) {
                bundle.putInt("POLICY", 0);
            } else if (i5 == 0) {
                bundle.putInt("POLICY", 1);
            } else if (i5 > 0) {
                bundle.putInt("POLICY", 2);
                bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, this.bgKillDelayMinutes);
            } else if (i5 == -1 || z2) {
                bundle.putInt("POLICY", 3);
            }
            if (this.hotFeature) {
                int i6 = this.bgKillDelayMinutesHot;
                if (i6 == -2) {
                    bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 0);
                } else if (i6 == -1) {
                    bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 3);
                } else if (i6 == 0) {
                    bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 1);
                } else {
                    bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 2);
                    bundle.putInt(HideModeStateMachineConfig.KEY_HOT_DELAY_MINUTE, this.bgKillDelayMinutesHot);
                }
            }
        } else if (i3 == 7) {
            bundle.putInt("POLICY", 2);
            bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 3);
        } else if ((i3 != 10 && i3 != 11) || (i2 = this.bgKillDelayMinutesUser) == -2) {
            bundle.putInt("POLICY", 0);
        } else if (i2 == -1) {
            bundle.putInt("POLICY", 3);
        } else if (i2 == 0) {
            bundle.putInt("POLICY", 1);
        } else {
            bundle.putInt("POLICY", 2);
            bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, this.bgKillDelayMinutesUser);
        }
        return bundle;
    }

    public Bundle toLocationAppRuleCheckerParams() {
        Bundle bundle = new Bundle();
        switch (this.scenario) {
            case 0:
            case 8:
            case 9:
            case 12:
            case 13:
                bundle.putInt("POLICY", 0);
                return bundle;
            case 1:
            case 2:
            case 10:
                int i2 = this.bgLocationDelayMinutes;
                if (i2 == -2) {
                    bundle.putInt("POLICY", 0);
                } else if (i2 == -1) {
                    bundle.putInt("POLICY", 3);
                } else if (i2 == 0) {
                    bundle.putInt("POLICY", 1);
                } else {
                    bundle.putInt("POLICY", 2);
                    bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, this.bgLocationDelayMinutes);
                }
                if (this.hotFeature) {
                    int i3 = this.bgLocationDelayMinutesHot;
                    if (i3 == -2) {
                        bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 0);
                    } else if (i3 == -1) {
                        bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 3);
                    } else if (i3 == 0) {
                        bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 1);
                    } else {
                        bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 2);
                        bundle.putInt(HideModeStateMachineConfig.KEY_HOT_DELAY_MINUTE, this.bgLocationDelayMinutesHot);
                    }
                }
                return bundle;
            case 3:
            case 11:
                bundle.putInt("POLICY", 2);
                bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 1);
                if (this.hotFeature) {
                    bundle.putInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 1);
                }
                return bundle;
            case 4:
            case 5:
            default:
                bundle.putInt("POLICY", 2);
                bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 1);
                return bundle;
            case 6:
            case 7:
                bundle.putInt("POLICY", 1);
                return bundle;
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x002a  */
    /* JADX WARN: Code duplicated, block: B:29:0x005d  */
    public Bundle toSensorAppRuleCheckerParams() {
        Bundle bundle = new Bundle();
        int i2 = this.scenario;
        if (i2 == 0) {
            bundle.putInt("POLICY", 0);
        } else if (i2 == 1 || i2 == 2) {
            if ((this.bgLocationDelayMinutes == -2 || this.bgSDelayMinutes == -2) && TextUtils.isEmpty(this.bgSDisableType)) {
                bundle.putInt("POLICY", 0);
            } else {
                int i3 = this.bgSDelayMinutes;
                if (i3 == -1 || (i3 == -2 && !TextUtils.isEmpty(this.bgSDisableType))) {
                    bundle.putInt("POLICY", 3);
                } else {
                    bundle.putInt("POLICY", 2);
                    bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, this.bgSDelayMinutes);
                }
            }
        } else if (i2 != 12 && i2 != 13) {
            switch (i2) {
                case 6:
                case 7:
                    bundle.putInt("POLICY", 1);
                    break;
                case 8:
                case 9:
                    bundle.putInt("POLICY", 0);
                    break;
                case 10:
                    if (this.bgLocationDelayMinutes == -2) {
                        bundle.putInt("POLICY", 0);
                    } else {
                        bundle.putInt("POLICY", 0);
                    }
                    break;
                default:
                    bundle.putInt("POLICY", 2);
                    bundle.putInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE, 1);
                    break;
            }
        } else {
            bundle.putInt("POLICY", 0);
        }
        return bundle;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tpkg: " + this.pkg);
        sb.append("\tuid: " + this.uid);
        sb.append("\tbgDataEnable: " + this.bgDataEnable);
        sb.append("\tbgDataDelayMinutes: " + this.bgDataDelayMinutes);
        sb.append("\tbgDataDelayCount: " + this.bgDataDelayCount);
        sb.append("\tbgDataMinDataKb: " + this.bgDataMinDataKb);
        sb.append("\tbgDataMaxInactiveCount: " + this.bgDataMaxInactiveCount);
        sb.append("\tadd2DeviceIdleWhiteList: " + this.add2DeviceIdleWhiteList);
        sb.append("\tbgLocationDelayMinutes: " + this.bgLocationDelayMinutes);
        sb.append("\tbgLocationDelayMinutesHot: " + this.bgLocationDelayMinutesHot);
        sb.append("\tbgKillDelayMinutes: " + this.bgKillDelayMinutes);
        sb.append("\tbgKillPolicy: " + this.bgKillPolicy);
        sb.append("\tbgKillDelayMinutesHot: " + this.bgKillDelayMinutesHot);
        sb.append("\tbgKillDelayMinutesUser: " + this.bgKillDelayMinutesUser);
        sb.append("\tbgSDelayMinutes: " + this.bgSDelayMinutes);
        sb.append("\tbgSDisableType: " + this.bgSDisableType);
        sb.append("\tbgHotFeature: " + this.hotFeature);
        sb.append("\tscenario: " + this.scenario);
        sb.append("\tbgIdleDelay: " + this.bgIdleDelayMinutes);
        sb.append("\tbgClusterPolicy: " + this.bgClusterPolicy);
        return sb.toString();
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public PowerKeeperAppConfigure m1clone() {
        PowerKeeperAppConfigure powerKeeperAppConfigure = new PowerKeeperAppConfigure();
        powerKeeperAppConfigure.pkg = this.pkg;
        powerKeeperAppConfigure.uid = this.uid;
        powerKeeperAppConfigure.bgDataEnable = this.bgDataEnable;
        powerKeeperAppConfigure.bgDataDelayMinutes = this.bgDataDelayMinutes;
        powerKeeperAppConfigure.bgDataDelayCount = this.bgDataDelayCount;
        powerKeeperAppConfigure.bgDataMinDataKb = this.bgDataMinDataKb;
        powerKeeperAppConfigure.bgDataMaxInactiveCount = this.bgDataMaxInactiveCount;
        powerKeeperAppConfigure.add2DeviceIdleWhiteList = this.add2DeviceIdleWhiteList;
        powerKeeperAppConfigure.bgLocationDelayMinutes = this.bgLocationDelayMinutes;
        powerKeeperAppConfigure.bgLocationDelayMinutesHot = this.bgLocationDelayMinutesHot;
        powerKeeperAppConfigure.bgKillDelayMinutes = this.bgKillDelayMinutes;
        powerKeeperAppConfigure.bgKillPolicy = this.bgKillPolicy;
        powerKeeperAppConfigure.bgKillDelayMinutesHot = this.bgKillDelayMinutesHot;
        powerKeeperAppConfigure.bgKillDelayMinutesUser = this.bgKillDelayMinutesUser;
        powerKeeperAppConfigure.bgSDelayMinutes = this.bgSDelayMinutes;
        powerKeeperAppConfigure.bgSDisableType = this.bgSDisableType;
        powerKeeperAppConfigure.hotFeature = this.hotFeature;
        powerKeeperAppConfigure.scenario = this.scenario;
        powerKeeperAppConfigure.bgIdleDelayMinutes = this.bgIdleDelayMinutes;
        powerKeeperAppConfigure.bgClusterPolicy = this.bgClusterPolicy;
        return powerKeeperAppConfigure;
    }

    public static List<PowerKeeperAppConfigure> createList(Context context, int i2, List<UserConfigureHelper> list) {
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        Bundle bundleQueryGlobalConfigure = GlobalFeatureConfigureHelper.queryGlobalConfigure(context, i2);
        String userConfigure = GlobalFeatureConfigureHelper.getUserConfigure(bundleQueryGlobalConfigure);
        List<String> systemNonCoreApps = GlobalFeatureConfigureHelper.getSystemNonCoreApps(bundleQueryGlobalConfigure);
        List<String> ultimateSpecialApps = GlobalFeatureConfigureHelper.getUltimateSpecialApps(bundleQueryGlobalConfigure);
        List<String> dozeWhiteListApps = GlobalFeatureConfigureHelper.getDozeWhiteListApps(bundleQueryGlobalConfigure);
        for (UserConfigureHelper userConfigureHelper : list) {
            PowerKeeperAppConfigure powerKeeperAppConfigure = new PowerKeeperAppConfigure();
            fillContent(context, i2, powerKeeperAppConfigure, userConfigureHelper, bundleQueryGlobalConfigure, userConfigure, systemNonCoreApps, ultimateSpecialApps, dozeWhiteListApps);
            arrayListNewArrayList.add(powerKeeperAppConfigure);
            bundleQueryGlobalConfigure = bundleQueryGlobalConfigure;
        }
        return arrayListNewArrayList;
    }

    public static Map<String, PowerKeeperAppConfigure> createMap(Context context, int i2, List<UserConfigureHelper> list) {
        HashMap mapNewHashMap = Maps.newHashMap();
        Bundle bundleQueryGlobalConfigure = GlobalFeatureConfigureHelper.queryGlobalConfigure(context, i2);
        String userConfigure = GlobalFeatureConfigureHelper.getUserConfigure(bundleQueryGlobalConfigure);
        List<String> systemNonCoreApps = GlobalFeatureConfigureHelper.getSystemNonCoreApps(bundleQueryGlobalConfigure);
        List<String> ultimateSpecialApps = GlobalFeatureConfigureHelper.getUltimateSpecialApps(bundleQueryGlobalConfigure);
        List<String> dozeWhiteListApps = GlobalFeatureConfigureHelper.getDozeWhiteListApps(bundleQueryGlobalConfigure);
        for (UserConfigureHelper userConfigureHelper : list) {
            PowerKeeperAppConfigure powerKeeperAppConfigure = new PowerKeeperAppConfigure();
            fillContent(context, i2, powerKeeperAppConfigure, userConfigureHelper, bundleQueryGlobalConfigure, userConfigure, systemNonCoreApps, ultimateSpecialApps, dozeWhiteListApps);
            mapNewHashMap.put(powerKeeperAppConfigure.pkg, powerKeeperAppConfigure);
            bundleQueryGlobalConfigure = bundleQueryGlobalConfigure;
        }
        return mapNewHashMap;
    }

    public PowerKeeperAppConfigure(String str, int i2) {
        this.pkg = str;
        this.uid = i2;
        setParamToDefault();
    }

    public static PowerKeeperAppConfigure createOne(Context context, int i2, String str) {
        Cursor cursorQuery = context.getContentResolverForUser(UserHandle.OWNER).query(UserConfigure.CONTENT_URI, null, "userId = ? AND pkgName = ?", new String[]{Integer.toString(i2), str}, null);
        if (cursorQuery == null) {
            return null;
        }
        PowerKeeperAppConfigure powerKeeperAppConfigureCreateOne = createOne(context, i2, UserConfigureHelper.createFirstFromTable(cursorQuery));
        cursorQuery.close();
        return powerKeeperAppConfigureCreateOne;
    }
}
