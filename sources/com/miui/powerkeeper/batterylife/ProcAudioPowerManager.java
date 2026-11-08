package com.miui.powerkeeper.batterylife;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import com.miui.powerkeeper.batterylife.data.AppAudioData;
import com.miui.powerkeeper.batterylife.observer.AudioChangeObserver;
import com.miui.powerkeeper.utils.ShellUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class ProcAudioPowerManager implements AudioChangeObserver.AudioChangeListener {
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    public static final String TAG = "ProcAudioPowerManager";
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private static final int VOLUME_UNKNOWN = -1;
    private AudioChangeObserver mAudioChangeObserver;
    private AudioManager mAudioManager;
    private Context mContext;
    private int mMaxVolume;
    private boolean mOnBattery;
    private int mLastVolume = -1;
    private SparseArray mUidState = new SparseArray();
    private SparseArray mUidStartTime = new SparseArray();
    private SparseArray mUidTimer = new SparseArray();

    public ProcAudioPowerManager(Context context) {
        this.mContext = context;
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
        }
        AudioManager audioManager = this.mAudioManager;
        this.mMaxVolume = audioManager != null ? audioManager.getStreamMaxVolume(3) / 10 : 15;
        AudioChangeObserver audioChangeObserver = new AudioChangeObserver(context);
        this.mAudioChangeObserver = audioChangeObserver;
        audioChangeObserver.setAudioChangeListener(this);
        int intExtra = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4).getIntExtra("status", -1);
        boolean z2 = (intExtra == 2 || intExtra == 5) ? false : true;
        this.mOnBattery = z2;
        if (z2) {
            this.mAudioChangeObserver.registerReceiver();
        }
    }

    public static String getCurrAudioMode(Context context) {
        MediaRouter mediaRouter = (MediaRouter) context.getSystemService("media_router");
        int routeCount = mediaRouter.getRouteCount();
        for (int i2 = 0; i2 < routeCount; i2++) {
            Log.e("RouteInfo", mediaRouter.getRouteAt(i2).toString());
        }
        return mediaRouter.getSelectedRoute(1).getName().toString();
    }

    private void startTheTimer() {
        if (this.mAudioManager.isMusicActive()) {
            for (int i2 = 0; i2 < this.mUidState.size(); i2++) {
                int iKeyAt = this.mUidState.keyAt(i2);
                if (((Integer) this.mUidState.get(iKeyAt)).intValue() > 0) {
                    String currAudioMode = getCurrAudioMode(this.mContext);
                    if (currAudioMode.equals("手机") || currAudioMode.equals("Phone")) {
                        int streamVolume = this.mAudioManager.getStreamVolume(3) / 10;
                        this.mLastVolume = streamVolume;
                        long jElapsedRealtime = SystemClock.elapsedRealtime();
                        long[] jArr = this.mUidStartTime.contains(iKeyAt) ? (long[]) this.mUidStartTime.get(iKeyAt) : new long[this.mMaxVolume + 1];
                        Arrays.fill(jArr, 0L);
                        jArr[streamVolume] = jElapsedRealtime;
                        this.mUidStartTime.put(iKeyAt, jArr);
                        Log.i(TAG, "uid is" + iKeyAt + ", startTime is " + Arrays.toString(jArr));
                    }
                }
            }
        }
    }

    private void stopTheTimer(int i2) {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (!this.mUidStartTime.contains(i2)) {
            Log.i(TAG, "时长计算数据不匹配");
            return;
        }
        long[] jArr = (long[]) this.mUidStartTime.get(i2);
        if (jArr[this.mLastVolume] == 0) {
            Log.i(TAG, "时长计算数据不匹配");
            return;
        }
        long[] jArr2 = this.mUidTimer.contains(i2) ? (long[]) this.mUidTimer.get(i2) : new long[this.mMaxVolume + 1];
        int i3 = this.mLastVolume;
        jArr2[i3] = jArr2[i3] + (jElapsedRealtime - jArr[i3]);
        this.mUidTimer.put(i2, jArr2);
        Log.i(TAG, "mUidTimer is " + Arrays.toString(jArr2));
        jArr[this.mLastVolume] = 0;
        this.mUidStartTime.put(i2, jArr);
    }

    public void destroy() {
        SparseArray sparseArray = this.mUidTimer;
        if (sparseArray != null) {
            sparseArray.clear();
            this.mUidTimer = null;
        }
        SparseArray sparseArray2 = this.mUidStartTime;
        if (sparseArray2 != null) {
            sparseArray2.clear();
            this.mUidStartTime = null;
        }
        SparseArray sparseArray3 = this.mUidState;
        if (sparseArray3 != null) {
            sparseArray3.clear();
            this.mUidState = null;
        }
    }

    public List<AppAudioData> doUploadAppAudioData() {
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.mUidTimer.size(); i2++) {
            int iKeyAt = this.mUidTimer.keyAt(i2);
            arrayList.add(new AppAudioData(BLUtils.getPackageForUid(this.mContext, iKeyAt), (long[]) this.mUidTimer.get(iKeyAt)));
        }
        return arrayList;
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < this.mUidTimer.size(); i2++) {
            int iKeyAt = this.mUidTimer.keyAt(i2);
            long[] jArr = (long[]) this.mUidTimer.get(iKeyAt);
            sb.append(BLUtils.getPackageForUid(this.mContext, iKeyAt) + ", ");
            sb.append(Arrays.toString(jArr));
            sb.append(ShellUtils.COMMAND_LINE_END);
        }
        return sb.toString();
    }

    public void noteStartAudioInNeed(int i2, int i3, int i4, int i5, int i6) {
        Log.i(TAG, "noteStartAudioInNeed uid is " + i2 + ", pid is " + i3 + ", sessionid is " + i4 + ", port is " + i5 + ", type is " + i6);
        String currAudioMode = getCurrAudioMode(this.mContext);
        int iIntValue = this.mUidState.contains(i2) ? ((Integer) this.mUidState.get(i2)).intValue() : 0;
        if (iIntValue == 0 && this.mAudioManager.isMusicActive() && (currAudioMode.equals("手机") || currAudioMode.equals("Phone"))) {
            int streamVolume = this.mAudioManager.getStreamVolume(3) / 10;
            this.mLastVolume = streamVolume;
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            long[] jArr = this.mUidStartTime.contains(i2) ? (long[]) this.mUidStartTime.get(i2) : new long[this.mMaxVolume + 1];
            Arrays.fill(jArr, 0L);
            jArr[streamVolume] = jElapsedRealtime;
            this.mUidStartTime.put(i2, jArr);
            Log.i(TAG, "volume is " + streamVolume + ", startTime is " + Arrays.toString(jArr));
        }
        this.mUidState.put(i2, Integer.valueOf(iIntValue + 1));
    }

    public void noteStopAudioInNeed(int i2, int i3, int i4, int i5, int i6) {
        Log.i(TAG, "noteStopAudioInNeed uid is " + i2 + ", pid is " + i3 + ", sessionid is " + i4 + ", port is " + i5 + ", type is " + i6);
        String currAudioMode = getCurrAudioMode(this.mContext);
        int iIntValue = this.mUidState.contains(i2) ? ((Integer) this.mUidState.get(i2)).intValue() : 0;
        if (iIntValue > 0 && (iIntValue = iIntValue - 1) == 0 && ((currAudioMode.equals("手机") || currAudioMode.equals("Phone")) && this.mOnBattery)) {
            stopTheTimer(i2);
        }
        this.mUidState.put(i2, Integer.valueOf(iIntValue));
    }

    /* JADX WARN: Code duplicated, block: B:18:0x0050  */
    @Override // com.miui.powerkeeper.batterylife.observer.AudioChangeObserver.AudioChangeListener
    public void onAudioChanged(Intent intent) {
        byte b2;
        AudioManager audioManager;
        String action = intent.getAction();
        Log.d(TAG, "action is " + action);
        int iHashCode = action.hashCode();
        int i2 = 0;
        if (iHashCode != -1940635523) {
            if (iHashCode != -1676458352) {
                if (iHashCode == 1244161670 && action.equals("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED")) {
                    b2 = 2;
                } else {
                    b2 = -1;
                }
            } else if (action.equals("android.intent.action.HEADSET_PLUG")) {
                b2 = 1;
            } else {
                b2 = -1;
            }
        } else if (action.equals(VOLUME_CHANGED_ACTION)) {
            b2 = 0;
        } else {
            b2 = -1;
        }
        if (b2 == 0) {
            if (intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == 3 && (audioManager = this.mAudioManager) != null && audioManager.isMusicActive()) {
                int streamVolume = this.mAudioManager.getStreamVolume(3) / 10;
                if (streamVolume < 0 || this.mLastVolume == streamVolume) {
                    Log.i(TAG, "volume don't changed");
                    return;
                }
                String currAudioMode = getCurrAudioMode(this.mContext);
                if (currAudioMode.equals("Phone") || currAudioMode.equals("手机")) {
                    while (i2 < this.mUidState.size()) {
                        int iKeyAt = this.mUidState.keyAt(i2);
                        if (((Integer) this.mUidState.get(iKeyAt)).intValue() > 0) {
                            stopTheTimer(iKeyAt);
                            long jElapsedRealtime = SystemClock.elapsedRealtime();
                            long[] jArr = this.mUidStartTime.contains(iKeyAt) ? (long[]) this.mUidStartTime.get(iKeyAt) : new long[this.mMaxVolume + 1];
                            Arrays.fill(jArr, 0L);
                            jArr[streamVolume] = jElapsedRealtime;
                            this.mUidStartTime.put(iKeyAt, jArr);
                            Log.i(TAG, "uid is" + iKeyAt + ", startTime is " + Arrays.toString(jArr));
                        }
                        i2++;
                    }
                    this.mLastVolume = streamVolume;
                    return;
                }
                return;
            }
            return;
        }
        if (b2 == 1) {
            int intExtra = intent.getIntExtra(YellowPageContract.Permission.STATE, -1);
            Log.d(TAG, "state = " + intExtra);
            if (intExtra == 0) {
                startTheTimer();
                return;
            }
            if (intExtra == 1) {
                while (i2 < this.mUidState.size()) {
                    int iKeyAt2 = this.mUidState.keyAt(i2);
                    if (((Integer) this.mUidState.get(iKeyAt2)).intValue() > 0) {
                        stopTheTimer(iKeyAt2);
                    }
                    i2++;
                }
                return;
            }
            return;
        }
        if (b2 != 2) {
            return;
        }
        int intExtra2 = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
        Log.d(TAG, "stateBl = " + intExtra2);
        if (intExtra2 != 2) {
            if (intExtra2 == 0) {
                startTheTimer();
            }
        } else {
            while (i2 < this.mUidState.size()) {
                int iKeyAt3 = this.mUidState.keyAt(i2);
                if (((Integer) this.mUidState.get(iKeyAt3)).intValue() > 0) {
                    stopTheTimer(iKeyAt3);
                }
                i2++;
            }
        }
    }

    public void onBatteryChanged(boolean z2) {
        Log.i(TAG, "onBatteryChanged, onBattery is " + z2);
        if (this.mOnBattery == z2) {
            return;
        }
        this.mOnBattery = z2;
        if (z2) {
            this.mAudioChangeObserver.registerReceiver();
            startTheTimer();
            return;
        }
        for (int i2 = 0; i2 < this.mUidState.size(); i2++) {
            int iKeyAt = this.mUidState.keyAt(i2);
            if (((Integer) this.mUidState.get(iKeyAt)).intValue() > 0) {
                stopTheTimer(iKeyAt);
            }
        }
        this.mAudioChangeObserver.unregisterReceiver();
    }

    public void resetAllData() {
        this.mUidStartTime.clear();
        this.mUidState.clear();
        this.mUidTimer.clear();
    }
}
