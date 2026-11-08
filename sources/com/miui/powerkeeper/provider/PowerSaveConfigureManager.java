package com.miui.powerkeeper.provider;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserHandle;
import android.util.Slog;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class PowerSaveConfigureManager {
    public static final String CONFIGURE_MIUI_AUTO = "miui_auto";
    public static final String CONFIGURE_NO_BG = "no_bg";
    public static final String CONFIGURE_NO_RESTRICT = "no_restrict";
    public static final String CONFIGURE_RESTRICT_BG = "restrict_bg";
    public static final String KEY_APP = "App";
    public static final String KEY_APP_CONFIGURE = "AppConfigure";
    public static final String KEY_DELAY_MINUTES = "BgDelayMin";
    public static final String KEY_USER_ID = "UserId";
    private static final String TAG = "PowerSaveConfigureManager";
    private static volatile PowerSaveConfigureManager mPowerSaveConfigureManager;
    public static final Set<String> sHasVisited = new HashSet();
    private Context mContext;
    private UserConfigureHelper mUserConfigureHelper;

    private PowerSaveConfigureManager(Context context) {
        this.mContext = context;
    }

    public static PowerSaveConfigureManager getInstance(Context context) {
        if (mPowerSaveConfigureManager == null) {
            synchronized (PowerSaveConfigureManager.class) {
                try {
                    if (mPowerSaveConfigureManager == null) {
                        mPowerSaveConfigureManager = new PowerSaveConfigureManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return mPowerSaveConfigureManager;
    }

    private String getInterfaceConfigureValue(String str) {
        if (str == null) {
            return null;
        }
        if (str.equals(UserConfigure.BG_CONTROL_NO_RESTRICT)) {
            return CONFIGURE_NO_RESTRICT;
        }
        if (str.equals("miuiAuto")) {
            return CONFIGURE_MIUI_AUTO;
        }
        if (str.equals(UserConfigure.BG_CONTROL_RESTRICT_BG)) {
            return CONFIGURE_RESTRICT_BG;
        }
        if (str.equals(UserConfigure.BG_CONTROL_NO_BG)) {
            return CONFIGURE_NO_BG;
        }
        return null;
    }

    private String getUserConfigureValue(String str) {
        if (str == null) {
            return null;
        }
        if (str.equals(CONFIGURE_NO_RESTRICT)) {
            return UserConfigure.BG_CONTROL_NO_RESTRICT;
        }
        if (str.equals(CONFIGURE_MIUI_AUTO)) {
            return "miuiAuto";
        }
        if (str.equals(CONFIGURE_RESTRICT_BG)) {
            return UserConfigure.BG_CONTROL_RESTRICT_BG;
        }
        if (str.equals(CONFIGURE_NO_BG)) {
            return UserConfigure.BG_CONTROL_NO_BG;
        }
        return null;
    }

    public int getPowerSaveAppConfigure(Bundle bundle, Bundle bundle2) {
        String string;
        String bgControl;
        if (bundle == null || bundle2 == null || (string = bundle.getString(KEY_APP, null)) == null) {
            return -1;
        }
        UserConfigureHelper userConfigureHelperByPkg = UserConfigureHelper.getUserConfigureHelperByPkg(this.mContext, bundle.containsKey(KEY_USER_ID) ? bundle.getInt(KEY_USER_ID) : UserHandle.getUserId(Binder.getCallingUid()), string);
        this.mUserConfigureHelper = userConfigureHelperByPkg;
        if (userConfigureHelperByPkg.getId() < 0 || (bgControl = this.mUserConfigureHelper.getBgControl()) == null) {
            return -1;
        }
        Set<String> set = sHasVisited;
        if (!set.contains(string) && bgControl.equals("miuiAuto") && ((PowerManager) this.mContext.getSystemService(PowerManager.class)).isIgnoringBatteryOptimizations(string)) {
            Bundle bundle3 = new Bundle();
            bundle3.putString(KEY_APP, string);
            bundle3.putString(KEY_APP_CONFIGURE, CONFIGURE_NO_RESTRICT);
            setPowerSaveAppConfigure(bundle3);
            bgControl = UserConfigure.BG_CONTROL_NO_RESTRICT;
        }
        set.add(string);
        String interfaceConfigureValue = getInterfaceConfigureValue(bgControl);
        if (interfaceConfigureValue == null) {
            return -1;
        }
        bundle2.putString(KEY_APP_CONFIGURE, interfaceConfigureValue);
        return 0;
    }

    public int setPowerSaveAppConfigure(Bundle bundle) {
        int i2;
        if (bundle == null) {
            Slog.w(TAG, "setPowerSaveAppConfigure failed param null");
            return -1;
        }
        String string = bundle.getString(KEY_APP, null);
        if (string == null) {
            Slog.w(TAG, "setPowerSaveAppConfigure failed pkg null");
            return -1;
        }
        String string2 = bundle.getString(KEY_APP_CONFIGURE, null);
        if (string2 == null) {
            Slog.w(TAG, "setPowerSaveAppConfigure failed config null pkg=" + string);
            return -1;
        }
        String userConfigureValue = getUserConfigureValue(string2);
        if (userConfigureValue == null) {
            Slog.w(TAG, "setPowerSaveAppConfigure failed parse pkg=" + string + " configure=" + string2);
            return -1;
        }
        Slog.w(TAG, "setPowerSaveAppConfigure success pkg=" + string + " configure=" + string2);
        if (userConfigureValue.equals(UserConfigure.BG_CONTROL_RESTRICT_BG)) {
            i2 = bundle.getInt(KEY_DELAY_MINUTES, -1);
            if (i2 < 0) {
                i2 = 10;
            }
        } else {
            i2 = -2;
        }
        UserConfigureHelper userConfigureHelperByPkg = UserConfigureHelper.getUserConfigureHelperByPkg(this.mContext, bundle.containsKey(KEY_USER_ID) ? bundle.getInt(KEY_USER_ID) : UserHandle.getUserId(Binder.getCallingUid()), string);
        this.mUserConfigureHelper = userConfigureHelperByPkg;
        if (userConfigureHelperByPkg.getId() < 0) {
            return -1;
        }
        this.mUserConfigureHelper.setBgControl(userConfigureValue);
        if (i2 >= 0) {
            this.mUserConfigureHelper.setBgDelayMin(i2);
        }
        UserConfigureHelper.updateToTable(this.mContext, this.mUserConfigureHelper.toContentValues());
        return 0;
    }
}
