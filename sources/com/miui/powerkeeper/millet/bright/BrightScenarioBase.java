package com.miui.powerkeeper.millet.bright;

import android.content.Context;
import android.os.Build;
import android.util.ArraySet;
import android.util.Log;
import com.miui.powerkeeper.controller.BroadcastController;
import com.miui.powerkeeper.millet.FreezeBinder;
import com.miui.powerkeeper.millet.MilletConfig;
import com.miui.powerkeeper.millet.MilletPolicy;
import com.miui.powerkeeper.millet.MilletUidObserver;
import com.miui.powerkeeper.millet.MilletUtils;
import com.miui.powerkeeper.utils.PowerKeeperPackageManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class BrightScenarioBase {
    public static String mCommonKey = "com";
    private static List<String> preloadWhiteApps = new ArrayList(Arrays.asList("com.miui.newhome", "com.mfashiongallery.emag", "com.miui.mediaeditor", "com.miui.weather2", "com.xiaomi.scanner"));
    private boolean DEBUG;
    private boolean isEnabled;
    private boolean isInitBroadcast;
    private BrightController mBrightCtrl;
    private BroadcastController mBroadcastCtrl;
    private Context mContext;
    private MilletPolicy mPolicy;
    boolean mUseHardConfig;
    public String TAG = "BrightScenarioBase";
    protected Set<String> mSceCurrentPackages = new HashSet();
    private String mCurrentPkg = null;
    protected String mScenarioName = "BrightScenarioBase";

    public BrightScenarioBase(MilletPolicy milletPolicy, Context context, boolean z2, BrightController brightController) {
        this.mPolicy = null;
        this.mContext = null;
        this.DEBUG = Log.isLoggable(this.TAG, 3) || Build.IS_DEBUGGABLE;
        this.mUseHardConfig = false;
        this.isInitBroadcast = false;
        this.mBroadcastCtrl = null;
        this.mPolicy = milletPolicy;
        this.mContext = context;
        this.mBrightCtrl = brightController;
        this.isEnabled = z2;
    }

    private void forbidFreezeScenario(Map<Integer, String> map) {
        BrightScenarioBase next;
        Set<String> forbidApps;
        for (String str : BrightMode.getInstance(this.mContext).getForbidTypes()) {
            Iterator<BrightScenarioBase> it = this.mBrightCtrl.getScenarioList().iterator();
            do {
                if (!it.hasNext()) {
                    next = null;
                    break;
                }
                next = it.next();
            } while (!str.equals(next.mScenarioName));
            if (next != null && (forbidApps = next.getForbidApps(str)) != null && forbidApps.size() != 0) {
                Iterator<Map.Entry<Integer, String>> it2 = map.entrySet().iterator();
                while (it2.hasNext()) {
                    String pkgNameByUid = MilletUidObserver.getPkgNameByUid(it2.next().getKey().intValue());
                    if (pkgNameByUid == this.mCurrentPkg || forbidApps.contains(pkgNameByUid)) {
                        it2.remove();
                        if (this.DEBUG) {
                            Log.d(this.TAG, "forbidFreeze remove: " + pkgNameByUid);
                        }
                    }
                }
            }
        }
    }

    private boolean getConfig() {
        return BrightMode.getInstance(this.mContext).getScenarioApps().size() == 0 || BrightMode.getInstance(this.mContext).getScenarioApps().get(this.mScenarioName) == null;
    }

    protected boolean canFreezePkg(String str, int i2) {
        if (str == null && ((str = MilletUidObserver.getPkgNameByUid(i2)) == null || str.length() < 8)) {
            Log.d(this.TAG, "canFreezePkg pkg: " + str + " uid=" + i2);
            if (!MilletUtils.isAppUid(i2)) {
                return false;
            }
        }
        if ((str != null && (str.equals("com.miui.home") || str == this.mCurrentPkg || preloadWhiteApps.contains(str))) || MilletUtils.isTopApp(i2) || MilletUtils.isWebviewSandboxUid(i2) || !this.mPolicy.isAllowFreeze(i2) || MilletUtils.isWidget(i2)) {
            return false;
        }
        if (str == null || !BrightMode.getInstance(this.mContext).isAppInclude(str)) {
            return MilletUtils.isAppUid(i2) && !MilletUidObserver.getIsSystemApp(i2);
        }
        return true;
    }

    public void controlScenaroApp(int i2) {
        if (BrightMode.getInstance(this.mContext).getBrightMode() == 0) {
            if (!this.isInitBroadcast) {
                this.isInitBroadcast = true;
                if (this.mBroadcastCtrl == null) {
                    this.mBroadcastCtrl = this.mBrightCtrl.getFrzCtrl().getPowerKeeperManager().getBroadcastController();
                }
                BroadcastController broadcastController = this.mBroadcastCtrl;
                if (broadcastController == null) {
                    Log.d(this.TAG, "controlScenaroAPp mBroadcast null");
                } else {
                    broadcastController.checkWhetstoneAMProxy(true);
                }
            }
            if (this.DEBUG) {
                Log.d(this.TAG, "controlScenaroApp uid =" + i2);
            }
            if (this.mBroadcastCtrl != null && !MilletConfig.getBroadcastCancel()) {
                this.mBroadcastCtrl.setBroadcastControlStat(i2, true);
                String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i2);
                if (pkgNameByUid == null || !MilletPolicy.MESSAGE_APPS.contains(pkgNameByUid)) {
                    this.mBroadcastCtrl.setBroadcastRestrict(i2, null);
                } else {
                    this.mBroadcastCtrl.setBroadcastRestrict(i2, MilletUtils.getRestrictActions());
                }
            }
        }
        this.mBrightCtrl.getFrzCtrl().updateWakelockBlockedUid(i2, true);
    }

    public void dealPackageAddRemove(String str, boolean z2) {
        BrightMode brightMode = BrightMode.getInstance(this.mContext);
        if (brightMode.getScenarioApps().containsKey(this.mScenarioName) && brightMode.getScenarioApps().get(this.mScenarioName).contains(str)) {
            if (z2) {
                this.mSceCurrentPackages.add(str);
            } else {
                this.mSceCurrentPackages.remove(str);
            }
        }
    }

    public Map<Integer, String> getAllValidApps() {
        Map<Integer, String> runningApp = MilletUtils.getRunningApp(this.mContext);
        forbidFreezeScenario(runningApp);
        return runningApp;
    }

    public boolean getEnabled() {
        int scenarioEnable = BrightMode.getInstance(this.mContext).getScenarioEnable(this.mScenarioName);
        if (scenarioEnable != -1) {
            return scenarioEnable == 1;
        }
        return this.isEnabled;
    }

    protected Set<String> getForbidApps(String str) {
        return getSceCurrentPackages();
    }

    public Set<String> getSceCurrentPackages() {
        return this.mSceCurrentPackages;
    }

    public String getScenarioName() {
        return this.mScenarioName;
    }

    public boolean includeInScenario(String str) {
        return this.mSceCurrentPackages.contains(str);
    }

    public void reInit() {
        Map<String, Set<String>> scenarioApps = BrightMode.getInstance(this.mContext).getScenarioApps();
        this.mSceCurrentPackages.clear();
        ArraySet<String> installedThirdPartApps = PowerKeeperPackageManager.getInstalledThirdPartApps();
        if (scenarioApps.get(this.mScenarioName) == null) {
            return;
        }
        for (String str : scenarioApps.get(this.mScenarioName)) {
            if (installedThirdPartApps.contains(str)) {
                this.mSceCurrentPackages.add(str);
            }
        }
    }

    public void restoreCtrl() {
        int[] frozenUidsPowerArray = FreezeBinder.getInstance().getFrozenUidsPowerArray();
        Log.d(this.TAG, "restroCtrl len=" + frozenUidsPowerArray.length);
        if (frozenUidsPowerArray.length > 0) {
            for (int i2 : frozenUidsPowerArray) {
                restoreCtrl(i2);
            }
        }
    }

    public boolean updateForegroundActivity(String str) {
        if (includeInScenario(str)) {
            this.mCurrentPkg = str;
            return true;
        }
        this.mCurrentPkg = null;
        return false;
    }

    protected void whiteAppsInit(String str, String str2) {
        this.mUseHardConfig = getConfig();
        ArraySet<String> installedThirdPartApps = PowerKeeperPackageManager.getInstalledThirdPartApps();
        String[] strArrSplit = str2.split(",");
        Map<String, Set<String>> scenarioApps = BrightMode.getInstance(this.mContext).getScenarioApps();
        if (this.mUseHardConfig) {
            scenarioApps.put(str, new HashSet());
        } else {
            Set<String> set = scenarioApps.get(str);
            if (set == null) {
                return;
            } else {
                strArrSplit = (String[]) set.toArray(new String[set.size()]);
            }
        }
        for (int i2 = 0; i2 < strArrSplit.length; i2++) {
            if (this.mUseHardConfig) {
                scenarioApps.get(str).add(strArrSplit[i2]);
            }
            if (installedThirdPartApps.contains(strArrSplit[i2])) {
                this.mSceCurrentPackages.add(strArrSplit[i2]);
            }
        }
        if (this.DEBUG) {
            Log.d(this.TAG, "whiteAppsInit name=" + str + " current =" + this.mSceCurrentPackages.toString() + " apps=" + str2);
        }
    }

    public void restoreCtrl(int i2) {
        if (this.DEBUG) {
            Log.d(this.TAG, "restoreCtrl uid = " + i2);
        }
        BroadcastController broadcastController = this.mBroadcastCtrl;
        if (broadcastController != null) {
            broadcastController.setBroadcastControlStat(i2, false);
        }
        this.mBrightCtrl.getFrzCtrl().updateWakelockBlockedUid(i2, false);
    }
}
