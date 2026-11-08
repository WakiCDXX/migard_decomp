package com.miui.powerkeeper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.miui.powerkeeper.millet.MilletPolicy;
import com.miui.powerkeeper.millet.MilletUtils;
import com.miui.powerkeeper.perfengine.PerfEngineManager;
import com.miui.powerkeeper.resource.bigdata.resourcelight.PowerOnetrackManager;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.whetstone.IPowerKeeperClient;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import com.xiaomi.push.service.MIPushNotificationHelper;
import java.util.HashSet;
import l.j;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperClientProxy extends IPowerKeeperClient.Stub {
    private static final String TAG = "PowerKeeper.ClientProxy";
    private Context mContext;
    private PowerKeeperManager powerKeeperManager;
    private Intent mATraceIntent = null;
    private boolean DEBUG = Log.isLoggable(TAG, 3);

    public PowerKeeperClientProxy(PowerKeeperManager powerKeeperManager, Context context) {
        this.powerKeeperManager = null;
        Log.d(TAG, "PowerKeeperClientProxy created!");
        this.powerKeeperManager = powerKeeperManager;
        this.mContext = context;
        offerPowerKeeperIBinder();
    }

    private void offerPowerKeeperIBinder() {
        try {
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            if (iWhetstoneActivityManagerAsInterface == null) {
                Log.d(TAG, "ws is null");
            } else {
                iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy().offerPowerKeeperIBinder(asBinder());
            }
        } catch (RemoteException unused) {
            Log.e(TAG, "IWhetstoneActivityManager work abnormal");
        }
    }

    public void notifyBluetooth(int i2, int i3, String str, int i4) {
        if (this.powerKeeperManager == null) {
            return;
        }
        Log.d(TAG, "notifyBluetooth state= " + i2 + " fd=" + str + " uid=" + i4);
        Handler milletHandler = this.powerKeeperManager.getMilletHandler();
        if (milletHandler != null) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 23;
            Bundle bundle = new Bundle();
            bundle.putInt(YellowPageContract.Permission.STATE, i2);
            bundle.putString("fd", str);
            bundle.putInt("uid", i4);
            messageObtain.setData(bundle);
            milletHandler.sendMessage(messageObtain);
        }
    }

    /* JADX WARN: Code duplicated, block: B:33:0x0083  */
    public void notifyEvent(int i2, Bundle bundle) {
        PowerKeeperManager powerKeeperManager = this.powerKeeperManager;
        if (powerKeeperManager == null) {
            return;
        }
        Handler milletHandler = powerKeeperManager.getMilletHandler();
        if (milletHandler != null) {
            if (this.DEBUG) {
                Log.d(TAG, "notifyEvent event=" + i2);
            }
            Message messageObtain = Message.obtain();
            if (i2 == 6) {
                messageObtain.what = 20;
                messageObtain.setData(bundle);
                int i3 = messageObtain.getData().getInt("eventcode");
                if (i3 == 354 || i3 == 0) {
                    if (this.DEBUG) {
                        Log.d(TAG, "return code =" + i3);
                        return;
                    }
                    return;
                }
                milletHandler.sendMessage(messageObtain);
            } else {
                if (i2 == 5) {
                    messageObtain.what = 23;
                    bundle.putString("profile", "y");
                } else if (i2 == 7) {
                    messageObtain.what = 22;
                } else if (i2 != 18) {
                    switch (i2) {
                        case 9:
                            messageObtain.what = 23;
                            bundle.putString("profile", "y");
                            break;
                        case 10:
                            messageObtain.what = 24;
                            break;
                        case 11:
                            messageObtain.what = 29;
                            break;
                    }
                } else {
                    messageObtain.what = 31;
                }
                messageObtain.setData(bundle);
                milletHandler.sendMessage(messageObtain);
            }
        }
        if (i2 == 8) {
            if (this.DEBUG) {
                Log.d(TAG, "notify details wakeup");
            }
            try {
                if (PowerOnetrackManager.getInstance() != null && PowerOnetrackManager.getInstance().getGreenifyServiceManager() != null) {
                    PowerOnetrackManager.getInstance().getGreenifyServiceManager().triggerGreenifyEngineFromDetailsWakeUp(bundle);
                }
            } catch (Exception e2) {
                Log.e(TAG, "GreenifyService: triggerGreenifyEngineFromDetailsWakeUp failed.", e2);
            }
        }
        if (i2 == 12) {
            if (this.DEBUG) {
                Log.d(TAG, "notify wakeup");
            }
            try {
                if (PowerOnetrackManager.getInstance() == null || PowerOnetrackManager.getInstance().getGreenifyServiceManager() == null) {
                    return;
                }
                PowerOnetrackManager.getInstance().getGreenifyServiceManager().triggerGreenifyEngineFromSuspend(bundle);
            } catch (Exception e3) {
                Log.e(TAG, "GreenifyService: triggerGreenifyEngineFromSuspend failed.", e3);
            }
        }
    }

    public void notifyFloatWindowScene(String str, boolean z2) {
        if (this.DEBUG) {
            Log.d(TAG, "float scene change: " + z2 + ", packageName: " + str);
        }
        PowerKeeperManager powerKeeperManager = this.powerKeeperManager;
        if (powerKeeperManager == null || powerKeeperManager.getFloatSceneObserver() == null || str == null || str.isEmpty()) {
            return;
        }
        this.powerKeeperManager.getFloatSceneObserver().notifyFloatSceneStateChange(str, null, z2);
    }

    public void notifyFocusChange(String str, String str2) {
        if (UnionPowerConfig.f657b) {
            j.u(str, str2);
        }
    }

    public void notifyFrozenAppWakeUpByHighPriorityAlarm(int i2) {
        PowerKeeperManager powerKeeperManager = this.powerKeeperManager;
        if (powerKeeperManager != null) {
            powerKeeperManager.notifyWakeUpFrozenAppEvent(i2);
        }
    }

    public void notifyScreenOn() {
        PowerKeeperManager powerKeeperManager = this.powerKeeperManager;
        if (powerKeeperManager == null || powerKeeperManager.getMilletHandler() == null) {
            return;
        }
        Log.d(TAG, "notifyScreenOn");
    }

    public void notifySyncEvent(int i2, int i3, String str, String str2, int[] iArr) {
        if (this.powerKeeperManager == null) {
            return;
        }
        HashSet hashSet = new HashSet();
        Handler milletHandler = this.powerKeeperManager.getMilletHandler();
        for (int i4 : iArr) {
            if (MilletUtils.isAppUid(i4)) {
                if (i2 == 1) {
                    MilletPolicy.SYNC_UIDS.add(Integer.valueOf(i4));
                } else if (i2 == 0) {
                    MilletPolicy.SYNC_UIDS.remove(Integer.valueOf(i4));
                }
                hashSet.add(Integer.valueOf(i4));
            }
        }
        if (hashSet.size() == 0) {
            return;
        }
        int[] array = hashSet.stream().mapToInt(new c()).toArray();
        if (milletHandler != null) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 19;
            Bundle bundle = new Bundle();
            bundle.putIntArray("uids", array);
            bundle.putString("pkg", str2);
            bundle.putString("reason", str);
            bundle.putInt(YellowPageContract.Permission.STATE, i2);
            bundle.putInt(MIPushNotificationHelper.MSG_BUSI_TYPE_SCENE, i3);
            messageObtain.setData(bundle);
            milletHandler.sendMessage(messageObtain);
        }
    }

    public void notifyTouchStatus(boolean z2) {
        if (UnionPowerConfig.f657b) {
            j.v(z2);
        }
    }

    public void perfThermalBreakAcquire(int i2) {
        if (e.e.o(4)) {
            if (PerfEngineManager.d() == null) {
                Log.d(TAG, "PeSchedManager has not initialized, abandon!");
            } else {
                PerfEngineManager.d().g().k(i2);
            }
        }
    }

    public void enableATrace(boolean z2, String str) {
    }
}
