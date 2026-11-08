package com.miui.powerkeeper.controller;

import android.annotation.RequiresApi;
import android.content.Context;
import android.media.AudioSystem;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.active.ActiveController;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.millet.MilletUtils;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import e.e;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class AudioDisguiseController extends Controller {
    private static final int DEFAULT_APP_BACKGROUND_TIME = 6000;
    private static final int MSG_INIT = 1;
    private static final String TAG = "PowerKeeper.Audio";
    private static String mAudioActiveConfig;
    private b.a mCloudObserver;
    private boolean mIsKillAppControl;
    private boolean mIsNewAudioActiveControl;
    private long mScreenOffPeriod;
    private long mScreenOnPeriod;
    private SparseArray<SparseLongArray> mUidDisguiseState;
    private SparseArray<SparseArray<ArrayList<Integer>>> mUidSessionIdDisguiseState;
    private SparseArray<SparseLongArray> mUidTimeDisguiseState;
    private JSONArray mWhiteAppList;
    private IWindowManager mWindowManager;

    public AudioDisguiseController(Context context, PowerKeeperManager powerKeeperManager) {
        super(context, powerKeeperManager);
        this.mIsNewAudioActiveControl = true;
        this.mIsKillAppControl = false;
        this.mCloudObserver = b.a.p();
        registerCloudObserver();
        Controller.MyHandler myHandler = new Controller.MyHandler(PowerKeeperManager.getThread().getLooper());
        this.mHandler = myHandler;
        myHandler.sendEmptyMessage(1);
        EventsAggregator eventsAggregator = this.mPowerKeeperManager.getEventsAggregator();
        eventsAggregator.registerForScreenOn(this.mHandler, null);
        eventsAggregator.registerForUserPresent(this.mHandler, null);
    }

    private boolean getKillAppEnable() {
        b.a aVarP = b.a.p();
        int i2 = b.b.I;
        JSONObject jSONObjectL = aVarP.l(i2);
        boolean zOptBoolean = false;
        if (b.a.p().s(i2) && jSONObjectL != null) {
            zOptBoolean = jSONObjectL.optBoolean("screen_on_witch", false);
            if (Controller.DEBUG) {
                Log.d(TAG, "getKillAppEnable screen_on_witch is " + zOptBoolean);
            }
        }
        return zOptBoolean;
    }

    private boolean getWhiteListApp(RunningProcessInfo runningProcessInfo) {
        for (String str : runningProcessInfo.mPkgList) {
            JSONArray jSONArray = this.mWhiteAppList;
            if (jSONArray != null && isKeyInJArray(jSONArray, str)) {
                return true;
            }
        }
        return false;
    }

    private IWindowManager getWindowManagerService() {
        if (this.mWindowManager == null) {
            this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
        }
        return this.mWindowManager;
    }

    private boolean isKeyInJArray(JSONArray jSONArray, String str) {
        if (!TextUtils.isEmpty(str) && jSONArray != null) {
            int length = jSONArray.length();
            for (int i2 = 0; i2 < length; i2++) {
                if (str.equals(jSONArray.optString(i2, ""))) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateUidAudioDisguise$0(int[] iArr, Integer num) {
        return iArr[0] == num.intValue();
    }

    private void parseFromDb() {
        String string;
        b.a aVar = this.mCloudObserver;
        int i2 = b.b.f40v;
        JSONObject jSONObjectL = aVar.l(i2);
        if (!this.mCloudObserver.s(i2) || jSONObjectL == null) {
            string = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_AUDIO_ACTIVE_CONTROL, "");
            Log.d(TAG, "parseFromDb old cloud configured ID_AUDIO_ACTIVE_CONTROL: " + string);
        } else {
            string = jSONObjectL.toString();
            Log.d(TAG, "parseFromDb new cloud configured ID_AUDIO_ACTIVE_CONTROL: " + string);
        }
        if (string.equals(mAudioActiveConfig)) {
            return;
        }
        mAudioActiveConfig = string;
        parseJson(string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseJson(String str) {
        if (str == null || str.isEmpty()) {
            Log.d(TAG, "The audio config is empty");
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            boolean zOptBoolean = jSONObject.optBoolean("fucSwitch", false);
            this.mIsNewAudioActiveControl = jSONObject.optBoolean("newFucSwitch", true);
            this.mIsKillAppControl = jSONObject.optBoolean("newKillAppSwitch", false);
            this.mWhiteAppList = jSONObject.optJSONArray("whiteAppList");
            this.mScreenOnPeriod = jSONObject.optLong("screen_on_period", 6000L);
            this.mScreenOffPeriod = jSONObject.optLong("screen_off_period", 6000L);
            if (zOptBoolean) {
                enable();
            } else {
                disable();
            }
        } catch (JSONException e2) {
            Log.e(TAG, "parseFromDb failed", e2);
        }
    }

    private void registerCloudObserver() {
        this.mCloudObserver.t(b.b.f40v, new b.a.d() { // from class: com.miui.powerkeeper.controller.AudioDisguiseController.1
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                if (jSONObject == null) {
                    Log.d(AudioDisguiseController.TAG, "Cloud-Observer onChange old ID_AUDIO_ACTIVE_CONTROL");
                    AudioDisguiseController.mAudioActiveConfig = null;
                    return;
                }
                Log.d(AudioDisguiseController.TAG, "Cloud-Observer onChange ID_AUDIO_ACTIVE_CONTROL: " + jSONObject.toString());
                AudioDisguiseController.mAudioActiveConfig = jSONObject.toString();
                AudioDisguiseController.this.parseJson(AudioDisguiseController.mAudioActiveConfig);
            }
        });
    }

    private void resumeAudioState(int i2) {
        try {
            List runningProcessInfoByUid = ProcessManager.getRunningProcessInfoByUid(i2);
            SparseArray<SparseArray<ArrayList<Integer>>> sparseArray = this.mUidSessionIdDisguiseState;
            if (sparseArray == null) {
                return;
            }
            SparseArray<ArrayList<Integer>> sparseArray2 = sparseArray.get(i2);
            if (runningProcessInfoByUid != null && sparseArray2 != null) {
                for (int i3 = 0; i3 < runningProcessInfoByUid.size(); i3++) {
                    Log.d(TAG, "record resume audio pid size is " + sparseArray2.size() + "App all pid size is " + runningProcessInfoByUid.size());
                    resumeAudioTracks(i2, ((RunningProcessInfo) runningProcessInfoByUid.get(i3)).mPid, sparseArray2);
                }
                if (this.mUidSessionIdDisguiseState.get(i2) != null) {
                    Log.i(TAG, "disguise audio record remove, uid: " + i2);
                    this.mUidSessionIdDisguiseState.delete(i2);
                }
            }
        } catch (RemoteException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            Log.e(TAG, "audio disguise resume audio track crash.");
            e3.printStackTrace();
        }
    }

    private void resumeAudioStateForScreenOn() {
        try {
            if (this.mUidSessionIdDisguiseState == null) {
                return;
            }
            Log.i(TAG, "resumeAudioState size is " + this.mUidSessionIdDisguiseState.size());
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.mUidSessionIdDisguiseState.size(); i2++) {
                arrayList.add(Integer.valueOf(this.mUidSessionIdDisguiseState.keyAt(i2)));
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                int iIntValue = ((Integer) it.next()).intValue();
                if (getWindowManagerService().checkAppOnWindowsStatus(iIntValue)) {
                    Log.i(TAG, "resumeAudioStateForScreenOn uid is " + iIntValue);
                    resumeAudioState(iIntValue);
                    SparseArray<SparseLongArray> sparseArray = this.mUidTimeDisguiseState;
                    if (sparseArray != null) {
                        sparseArray.delete(iIntValue);
                    }
                }
            }
        } catch (Exception e2) {
            Log.e(TAG, "resume audio state for screen on ", e2);
        }
    }

    private void resumeAudioTracks(int i2, int i3, SparseArray<ArrayList<Integer>> sparseArray) {
        for (int i4 = 0; i4 < sparseArray.size(); i4++) {
            int iKeyAt = sparseArray.keyAt(i4);
            ArrayList<Integer> arrayList = sparseArray.get(iKeyAt);
            Log.d(TAG, "resumeAudioTracks uid: " + i2 + ", keyPid is " + iKeyAt + ", pid is " + i3 + ", keyPidsessionId is " + arrayList);
            if (iKeyAt == i3) {
                Iterator<Integer> it = arrayList.iterator();
                while (it.hasNext()) {
                    int iIntValue = it.next().intValue();
                    AudioSystem.resumeAudioTracks(i2, iKeyAt, iIntValue);
                    Log.d(TAG, "Resumed audio tracks for msg.arg1=" + i2 + ", pidStats.keyAt(0)=" + sparseArray.keyAt(0) + ", keyPidsessionId=" + iIntValue);
                }
            }
        }
    }

    private void updateAppBgTime(int i2) {
        try {
            SparseLongArray sparseLongArray = this.mUidTimeDisguiseState.get(i2);
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            if (sparseLongArray == null) {
                sparseLongArray = new SparseLongArray(2);
                this.mUidTimeDisguiseState.put(i2, sparseLongArray);
            }
            int[] pidsForUid = MilletUtils.getPidsForUid(i2, this.mContext);
            if (pidsForUid.length == 0) {
                Log.d(TAG, "pids 0");
                return;
            }
            for (int i3 : pidsForUid) {
                sparseLongArray.put(i3, jElapsedRealtime);
            }
        } catch (Exception unused) {
            Log.e(TAG, "update app background error.");
        }
    }

    private void updateUidAudioDisguise(int i2, int i3) {
        String[] strArr;
        boolean z2 = SystemProperties.getBoolean("persist.sys.spc.powerkill.newpolicy.enable", false);
        if (!this.isEnabled || ((z2 && !getKillAppEnable()) || this.mUidDisguiseState == null)) {
            Log.d(TAG, "getKillAppEnable() is " + getKillAppEnable() + ", milletEnable is " + z2 + ", isEnabled is " + this.isEnabled);
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        SparseLongArray sparseLongArray = this.mUidDisguiseState.get(i2);
        if (sparseLongArray == null) {
            sparseLongArray = new SparseLongArray(2);
            this.mUidDisguiseState.put(i2, sparseLongArray);
        }
        long j2 = sparseLongArray.get(i3);
        if (j2 <= 0) {
            sparseLongArray.put(i3, jElapsedRealtime);
            return;
        }
        if (jElapsedRealtime - j2 <= (this.mPowerKeeperManager.isScreenOn() ? this.mScreenOnPeriod : this.mScreenOffPeriod) || this.mPowerKeeperManager.getAppActiveState(i2)) {
            return;
        }
        Log.d(TAG, "kill long time zero data process uid=" + i2);
        try {
            f.c cVar = (f.c) e.i(1073741826);
            if (cVar != null && cVar.c() != null && cVar.c().getPowerUsageMonitor() != null) {
                Log.i(TAG, "zero data process uid=" + i2);
                cVar.c().getPowerUsageMonitor().notifyAudioDisguise(i2);
            }
            RunningProcessInfo runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i3);
            if (runningProcessInfoByPid == null || runningProcessInfoByPid.mHasForegroundActivities || runningProcessInfoByPid.mLocationForeground || (strArr = runningProcessInfoByPid.mPkgList) == null) {
                return;
            }
            for (String str : strArr) {
                ProcessManager.kill(new ProcessConfig(11, str, i2));
            }
            this.mUidDisguiseState.delete(i2);
        } catch (Exception unused) {
        }
    }

    public void dispatchCloudFunctionUpdated() {
        this.mHandler.sendEmptyMessage(-17);
    }

    public boolean getNewAudioActiveControl() {
        return this.mIsNewAudioActiveControl;
    }

    public SparseArray<SparseArray<ArrayList<Integer>>> getUidSessionIdDisguiseState() {
        return this.mUidSessionIdDisguiseState;
    }

    public boolean killApp(int i2, RunningProcessInfo runningProcessInfo, int i3) {
        if (i2 != 0 && i2 != 3) {
            return false;
        }
        try {
            String[] strArr = runningProcessInfo.mPkgList;
            if (strArr.length <= 0) {
                return false;
            }
            ProcessManager.kill(new ProcessConfig(11, strArr[0], i3));
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onDisable() {
        EventsAggregator eventsAggregator = this.mPowerKeeperManager.getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.unregisterForegroundActivityChangeListener(this.mHandler);
            eventsAggregator.unregisterAudioDisguise(this.mHandler);
            eventsAggregator.unregisterForUserPresent(this.mHandler);
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onEnable() {
        this.mUidSessionIdDisguiseState = new SparseArray<>();
        this.mUidTimeDisguiseState = new SparseArray<>();
        this.mUidDisguiseState = new SparseArray<>();
        EventsAggregator eventsAggregator = this.mPowerKeeperManager.getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.registerForegroundActivityChangeListener(this.mHandler);
            eventsAggregator.registerAudioDisguise(this.mHandler);
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onHandleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -27) {
            if (this.mIsNewAudioActiveControl) {
                updateUidAudioDisguise(message.arg1, message.arg2, message.obj);
                return;
            } else {
                updateUidAudioDisguise(message.arg1, message.arg2);
                return;
            }
        }
        if (i2 == -17) {
            parseFromDb();
            return;
        }
        if (i2 != -10) {
            if (i2 == -7) {
                if (this.mIsNewAudioActiveControl) {
                    resumeAudioStateForScreenOn();
                    return;
                }
                return;
            } else {
                if (i2 != 1) {
                    return;
                }
                this.mPowerKeeperManager.getEventsAggregator().registerCloudUpdateListener(this.mHandler);
                parseFromDb();
                return;
            }
        }
        boolean z2 = this.isEnabled;
        if (!z2 || message.arg2 != 1) {
            if (z2 && this.mIsNewAudioActiveControl && this.mUidTimeDisguiseState != null && message.arg2 == 0) {
                updateAppBgTime(message.arg1);
                return;
            }
            return;
        }
        if (!this.mIsNewAudioActiveControl) {
            SparseArray<SparseLongArray> sparseArray = this.mUidDisguiseState;
            if (sparseArray != null) {
                sparseArray.delete(message.arg1);
                return;
            }
            return;
        }
        resumeAudioState(message.arg1);
        SparseArray<SparseLongArray> sparseArray2 = this.mUidTimeDisguiseState;
        if (sparseArray2 != null) {
            sparseArray2.delete(message.arg1);
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUidRemoved(int i2, int i3) {
        Log.i(TAG, "onUidRemoved disguise state is " + i3);
        if (!this.mIsNewAudioActiveControl) {
            SparseArray<SparseLongArray> sparseArray = this.mUidDisguiseState;
            if (sparseArray != null) {
                sparseArray.delete(i3);
                return;
            }
            return;
        }
        SparseArray<SparseArray<ArrayList<Integer>>> sparseArray2 = this.mUidSessionIdDisguiseState;
        if (sparseArray2 != null) {
            sparseArray2.delete(i3);
        }
        SparseArray<SparseLongArray> sparseArray3 = this.mUidTimeDisguiseState;
        if (sparseArray3 != null) {
            sparseArray3.delete(i3);
        }
    }

    @RequiresApi(api = 24)
    private void updateUidAudioDisguise(int i2, int i3, Object obj) {
        if (!this.isEnabled || this.mUidSessionIdDisguiseState == null || this.mUidTimeDisguiseState == null) {
            return;
        }
        try {
            final int[] iArr = (int[]) obj;
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            SparseLongArray sparseLongArray = this.mUidTimeDisguiseState.get(i2);
            if (sparseLongArray == null) {
                sparseLongArray = new SparseLongArray(2);
                this.mUidTimeDisguiseState.put(i2, sparseLongArray);
            }
            long j2 = sparseLongArray.get(i3);
            Log.i(TAG, "lastTime is " + j2 + ", uid is " + i2);
            if (j2 <= 0 && this.mPowerKeeperManager.isScreenOn()) {
                sparseLongArray.put(i3, jElapsedRealtime);
                return;
            }
            ActiveController activeController = PowerKeeperManager.getInstance().getActiveController();
            long j3 = this.mPowerKeeperManager.isScreenOn() ? this.mScreenOnPeriod : this.mScreenOffPeriod;
            int appActiveType = activeController.getAppActiveType(i2) & 28;
            Log.i(TAG, "uid is " + i2 + "active type is " + activeController.getAppActiveType(i2) + ", isDownloadVpnGpsActive is " + appActiveType);
            if ((jElapsedRealtime - j2 > j3 || !this.mPowerKeeperManager.isScreenOn()) && appActiveType == 0) {
                SparseArray<ArrayList<Integer>> sparseArray = this.mUidSessionIdDisguiseState.get(i2);
                if (sparseArray == null) {
                    sparseArray = new SparseArray<>(2);
                }
                RunningProcessInfo runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i3);
                Log.i(TAG, "pause zero data process uid is " + i2 + ", info is " + runningProcessInfoByPid + ", mHasForegroundServices is " + runningProcessInfoByPid.mHasForegroundServices);
                if ((runningProcessInfoByPid.mHasForegroundActivities && this.mPowerKeeperManager.isScreenOn()) || runningProcessInfoByPid.mPkgList == null) {
                    return;
                }
                if (getWhiteListApp(runningProcessInfoByPid)) {
                    Log.i(TAG, "white list app not release audio." + i2);
                    return;
                }
                f.c cVar = (f.c) e.i(1073741826);
                if (cVar != null && cVar.c() != null && cVar.c().getPowerUsageMonitor() != null) {
                    Log.i(TAG, "zero data process uid=" + i2);
                    cVar.c().getPowerUsageMonitor().notifyAudioDisguise(i2);
                }
                if (this.mIsKillAppControl && killApp(iArr[1], runningProcessInfoByPid, i2)) {
                    SparseArray<SparseLongArray> sparseArray2 = this.mUidTimeDisguiseState;
                    if (sparseArray2 != null) {
                        sparseArray2.delete(i2);
                        return;
                    }
                    return;
                }
                AudioSystem.pauseAudioTracks(i2, i3, iArr[0]);
                Log.i(TAG, "pauseAudioTracks sessionIdStatus[0] is " + iArr[0]);
                ArrayList<Integer> arrayList = sparseArray.get(i3);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                }
                if (arrayList.stream().noneMatch(new Predicate() { // from class: com.miui.powerkeeper.controller.c
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj2) {
                        return AudioDisguiseController.lambda$updateUidAudioDisguise$0(iArr, (Integer) obj2);
                    }
                })) {
                    arrayList.add(Integer.valueOf(iArr[0]));
                    sparseArray.put(i3, arrayList);
                }
                this.mUidSessionIdDisguiseState.put(i2, sparseArray);
                Log.i(TAG, "pauseAudioTracks uid is " + i2 + ", pid is " + i3 + ", sessionid is " + obj.toString() + ", sessionList is " + arrayList);
            }
        } catch (Exception e2) {
            Log.e(TAG, "can not find function in AudioSystem");
            e2.printStackTrace();
        }
    }
}
