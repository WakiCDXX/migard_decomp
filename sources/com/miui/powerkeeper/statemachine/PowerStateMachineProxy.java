package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.UserHandle;
import android.util.Log;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.ScenarioManager;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.Utils;

/* JADX INFO: loaded from: classes.dex */
public class PowerStateMachineProxy {
    private static final String TAG = "PowerStateMachine";
    private static volatile PowerStateMachineProxy sInstance;
    private PowerStateMachine mPowerStateMachine;

    private PowerStateMachineProxy() {
        changePowerStateMachine();
        registerContentResolver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changePowerStateMachine() {
        boolean zIsAllow = isAllow();
        Log.d("PowerStateMachine", "updateSmEnableState isAllow=" + zIsAllow);
        Intent intent = new Intent();
        Context context = Utils.APP_CONTEXT;
        intent.setClass(context, PowerStateMachineService.class);
        if (zIsAllow) {
            intent.setAction(PowerStateMachineService.ENABLE_MACHINE);
        } else {
            intent.setAction(PowerStateMachineService.DISABLE_MACHINE);
        }
        context.startServiceAsUser(intent, UserHandle.OWNER);
    }

    public static synchronized PowerStateMachineProxy getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("PowerStateMachineProxy.getInstance() can't be called before init()");
        }
        return sInstance;
    }

    public static synchronized PowerStateMachineProxy init() {
        if (sInstance != null) {
            throw new RuntimeException("PowerStateMachineProxy.init() should only be called once");
        }
        sInstance = new PowerStateMachineProxy();
        return sInstance;
    }

    private boolean isAllow() {
        if (!isConfigOn()) {
            Log.d("PowerStateMachine", "isAllow isConfigOn false");
            return false;
        }
        if (Utils.isSystemServiceExists(Utils.SERVICE_NAME_POWER)) {
            return true;
        }
        Log.d("PowerStateMachine", "isAllow miui.whetstone.power not exist");
        return false;
    }

    private boolean isConfigOn() {
        String string = SimpleSettings.Misc.getString(Utils.APP_CONTEXT, Constant.FUNC_POWER_STATE_MACHINE);
        if (string == null) {
            return true;
        }
        return Boolean.parseBoolean(string);
    }

    public static boolean isGameModeApp(String str) {
        PowerStateMachine.PowerState primaryState;
        if (str == null || str.isEmpty() || sInstance == null) {
            return false;
        }
        ScenarioManager scenarioManager = ScenarioManager.getInstance();
        if (scenarioManager != null) {
            return scenarioManager.isGameModeApp(str);
        }
        if (sInstance.mPowerStateMachine == null || (primaryState = sInstance.mPowerStateMachine.getPrimaryState(str)) == null) {
            return false;
        }
        int i2 = primaryState.mId;
        return i2 == 9 || i2 == 13;
    }

    private void registerContentResolver() {
        Utils.APP_CONTEXT.getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(Constant.FUNC_POWER_STATE_MACHINE), false, new ContentObserver(null) { // from class: com.miui.powerkeeper.statemachine.PowerStateMachineProxy.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                PowerStateMachineProxy.this.changePowerStateMachine();
            }
        });
    }

    public void sendEvent(int i2, Object obj) {
        if (i2 == 4013) {
            changePowerStateMachine();
            return;
        }
        PowerStateMachine powerStateMachine = this.mPowerStateMachine;
        if (powerStateMachine != null) {
            powerStateMachine.sendMessage(powerStateMachine.obtainMessage(i2, obj));
        }
    }

    void setPowerStateMachine(PowerStateMachine powerStateMachine) {
        this.mPowerStateMachine = powerStateMachine;
    }
}
