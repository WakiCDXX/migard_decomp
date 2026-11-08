package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public abstract class SleepModeControllerNew {
    public static final String FUNC_SLEEP_REBOOT = "sleep_reboot";
    public static final int REASON_CODE_EARTHQUAKE_WARNING = 3;
    public static final int REASON_CODE_OLD_USER_BY_UI = 2;
    public static final int REASON_CODE_OPTIMIZED_EARTHQUAKE_CLOUD = 4;
    public static final int REASON_CODE_USER_BY_UI = 1;
    public static final String SLEEP_MODE_OFF = "off";
    public static final String SLEEP_MODE_ON = "on";
    private static final String TAG = "power.sleep";
    public static final String USER_CLOSE_SLEEP_MODE_BY_BUTTON = "userCloseSleepModeByUIButton";
    public static boolean sUserCloseSleepModeByUIButton = SimpleSettings.Misc.getBooleanForUser(PowerKeeperApplication.b(), "userCloseSleepModeByUIButton", false, 0);

    public static SleepModeControllerNew getInstance() {
        f.z zVar = (f.z) e.e.i(19);
        if (zVar == null) {
            return null;
        }
        SleepModeControllerNew sleepModeControllerNewC = zVar.c();
        if (sleepModeControllerNewC == null) {
            return sleepModeControllerNewC;
        }
        sleepModeControllerNewC.logWithsLocal("SleepModeControllerNew current type -> " + sleepModeControllerNewC.getClass().getSimpleName());
        return sleepModeControllerNewC;
    }

    public static void setSleepModeSwitch(boolean z2, int i2) {
        Context contextB = PowerKeeperApplication.b();
        String stringForUser = SimpleSettings.Misc.getStringForUser(contextB, Constant.FUNC_SLEEP_MODE_USER, SLEEP_MODE_ON, 0);
        SleepModeControllerNew sleepModeControllerNew = getInstance();
        if (sleepModeControllerNew == null) {
            Log.d(TAG, "device don't support sleepMode");
            return;
        }
        if (z2) {
            if ("off".equals(stringForUser)) {
                if (SimpleSettings.Misc.putStringForUser(contextB, Constant.FUNC_SLEEP_MODE_USER, SLEEP_MODE_ON, 0)) {
                    sleepModeControllerNew.logWithsLocal("ReasonCode -> " + i2 + " open sleep mode.  currentSleepModeState:" + Utils.isSleepModeEnabled(contextB));
                }
                if (i2 == 1) {
                    sUserCloseSleepModeByUIButton = false;
                    SimpleSettings.Misc.putBooleanForUser(contextB, "userCloseSleepModeByUIButton", false, 0);
                    Log.d(TAG, "current sUserCloseSleepModeByUIButton : " + sUserCloseSleepModeByUIButton);
                    return;
                }
                return;
            }
            return;
        }
        if (SLEEP_MODE_ON.equals(stringForUser)) {
            if (SimpleSettings.Misc.putStringForUser(contextB, Constant.FUNC_SLEEP_MODE_USER, "off", 0)) {
                sleepModeControllerNew.logWithsLocal("ReasonCode -> " + i2 + " close sleep mode.  currentSleepModeState:" + Utils.isSleepModeEnabled(contextB));
            }
            if (i2 == 1) {
                sUserCloseSleepModeByUIButton = true;
                SimpleSettings.Misc.putBooleanForUser(contextB, "userCloseSleepModeByUIButton", true, 0);
                Log.d(TAG, "current sUserCloseSleepModeByUIButton : " + sUserCloseSleepModeByUIButton);
            }
        }
    }

    public abstract void dispose();

    public abstract void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    public abstract boolean getSleepModeForEarthquakeWarnFlag();

    public abstract boolean isInSleep();

    public abstract void logWithsLocal(String str);

    public abstract void registerSleepListener(PowerKeeperInterface.a0 a0Var);

    public abstract void unregisterSleepListener(PowerKeeperInterface.a0 a0Var);
}
