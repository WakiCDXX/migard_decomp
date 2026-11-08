package com.miui.powerkeeper.batterylife.monitor;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.batterylife.TrackData;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.Utils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class SleepMode implements Resource {
    public static final String TAG = "BL-SleepMode";
    private final Context mContext;
    private String mExitReason;
    private String mSuspendReason;
    private long mSleepStartTime = -1;
    private long mSleepDurationMs = 0;
    private boolean mInSleep = false;
    private Handler mHandler = new Handler(BackgroundThread.getHandler().getLooper()) { // from class: com.miui.powerkeeper.batterylife.monitor.SleepMode.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.d(SleepMode.TAG, "handleMessage what=" + message.what);
            if (message.what == -19) {
                Bundle bundle = (Bundle) message.obj;
                boolean z2 = bundle.getBoolean(YellowPageContract.Permission.STATE, false);
                String string = bundle.getString("reason");
                Log.d(SleepMode.TAG, "handleMessage state=" + z2 + ", reason=" + string);
                if (z2) {
                    if (string == null) {
                        SleepMode.this.mSleepStartTime = System.currentTimeMillis();
                        SleepMode.this.mSuspendReason = null;
                        SleepMode.this.mInSleep = true;
                    } else {
                        SleepMode.this.mSuspendReason = string;
                        SleepMode.this.mExitReason = null;
                    }
                } else {
                    if (SleepMode.this.mSleepStartTime == -1 || !SleepMode.this.mInSleep) {
                        return;
                    }
                    SleepMode.this.mInSleep = false;
                    SleepMode.this.mSleepDurationMs += System.currentTimeMillis() - SleepMode.this.mSleepStartTime;
                    SleepMode.this.mSleepStartTime = -1L;
                    SleepMode.this.mExitReason = string;
                }
            }
            super.handleMessage(message);
        }
    };

    public SleepMode() {
        Context contextB = PowerKeeperApplication.b();
        this.mContext = contextB;
        EventsAggregator.getInstance(contextB).registerSleepStateListener(this.mHandler);
    }

    private boolean getCloudConfigure() {
        return SimpleSettings.Misc.getBooleanForUser(this.mContext, Constant.FUNC_SLEEP_MODE_CLOUD, true, 0);
    }

    private boolean getUserConfigure() {
        return Utils.isSleepModeEnabled(this.mContext);
    }

    private boolean isSleepModeSupport() {
        return SleepModeControllerNew.getInstance() != null;
    }

    @Override // com.miui.powerkeeper.batterylife.monitor.Resource
    public void dump(PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
        indentingPrintWriter.println("SleepMode:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("isSleepModeSupport=" + isSleepModeSupport());
        if (this.mSleepStartTime != -1) {
            indentingPrintWriter.println("mSleepDurationMs=" + ((this.mSleepDurationMs + System.currentTimeMillis()) - this.mSleepStartTime));
        } else {
            indentingPrintWriter.println("mSleepDurationMs=" + this.mSleepDurationMs);
        }
        indentingPrintWriter.println("mSleepStartTime=" + this.mSleepStartTime);
        indentingPrintWriter.println("mExitReason=" + this.mExitReason);
        indentingPrintWriter.println("mSuspendReason=" + this.mSuspendReason);
        indentingPrintWriter.println("mUserConfiguration=" + getUserConfigure());
        indentingPrintWriter.println("mCloudConfiguration=" + getCloudConfigure());
    }

    @Override // com.miui.powerkeeper.batterylife.monitor.Resource
    public ArrayList<Map<String, Object>> getUploadData() {
        ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
        ArrayMap arrayMap = new ArrayMap();
        arrayMap.put(TrackData.SM_IS_SUPPORT, Boolean.valueOf(isSleepModeSupport()));
        arrayMap.put(TrackData.SM_USER_CONFIGURATION, Boolean.valueOf(getUserConfigure()));
        arrayMap.put(TrackData.SM_CLOUD_CONFIGURATION, Boolean.valueOf(getCloudConfigure()));
        if (this.mInSleep) {
            arrayMap.put(TrackData.SM_DURATION, Long.valueOf(((this.mSleepDurationMs + System.currentTimeMillis()) - this.mSleepStartTime) / 1000));
        } else {
            arrayMap.put(TrackData.SM_DURATION, Long.valueOf(this.mSleepDurationMs / 1000));
        }
        String str = this.mSuspendReason;
        if (str != null) {
            arrayMap.put(TrackData.SM_SUSPEND_REASON, str);
        } else {
            arrayMap.put(TrackData.SM_SUSPEND_REASON, "null");
        }
        String str2 = this.mExitReason;
        if (str2 != null) {
            arrayMap.put(TrackData.SM_EXIT_REASON, str2);
        } else {
            arrayMap.put(TrackData.SM_EXIT_REASON, "null");
        }
        arrayList.add(arrayMap);
        return arrayList;
    }

    @Override // com.miui.powerkeeper.batterylife.monitor.Resource
    public void reset() {
        this.mSleepDurationMs = 0L;
        if (this.mInSleep) {
            this.mSleepStartTime = System.currentTimeMillis();
        } else {
            this.mSleepStartTime = -1L;
        }
        this.mExitReason = null;
        this.mSuspendReason = null;
    }
}
