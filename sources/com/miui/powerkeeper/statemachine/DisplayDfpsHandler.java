package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Log;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import miui.hardware.display.DisplayFeatureManager;
import miui.process.ForegroundInfo;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DisplayDfpsHandler extends Handler {
    private static final String CLOUD_FPS_VIDEO = "dfps_group";
    private static DisplayDfpsHandler sInstance;
    private final boolean DBG_DISPLAY;
    private final int DFPS_STATE;
    private final int DISPALY_DFPS_OFF;
    private final int DISPALY_DFPS_ON;
    private final int MSG_CLOUD_UPDATE;
    private final String TAG;
    private Context mContext;
    private int mEnableBeginTime;
    private int mEnableTimes;
    private int mEnableTotalTime;
    private int mLastState;
    private LocalLog mLocalLog;
    private ArraySet<String> mVideoDfpsPkgSet;

    private DisplayDfpsHandler(Context context, Looper looper) {
        super(looper);
        this.TAG = "DisplayDfpsHandler";
        this.DBG_DISPLAY = Log.isLoggable("power.display", 3);
        this.DFPS_STATE = 21;
        this.DISPALY_DFPS_OFF = 0;
        this.DISPALY_DFPS_ON = 1;
        this.MSG_CLOUD_UPDATE = 1;
        this.mVideoDfpsPkgSet = new ArraySet<>(128);
        this.mLocalLog = new LocalLog(64);
        this.mLastState = 0;
        this.mEnableTimes = 0;
        this.mEnableTotalTime = 0;
        this.mEnableBeginTime = 0;
        this.mContext = context;
        updateDfpsGroup(SimpleSettings.Misc.getString(context, CLOUD_FPS_VIDEO, ""));
    }

    public static synchronized DisplayDfpsHandler init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new DisplayDfpsHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        String strOptString = jSONObject.optString(CLOUD_FPS_VIDEO, "");
        if (strOptString.equals(SimpleSettings.Misc.getString(context, CLOUD_FPS_VIDEO, ""))) {
            return;
        }
        SimpleSettings.Misc.putString(context, CLOUD_FPS_VIDEO, strOptString);
        DisplayDfpsHandler displayDfpsHandler = sInstance;
        if (displayDfpsHandler != null) {
            displayDfpsHandler.sendMessage(displayDfpsHandler.obtainMessage(1, strOptString));
        }
    }

    private void updateDfpsGroup(String str) {
        this.mVideoDfpsPkgSet.clear();
        if (str == null || str.isEmpty()) {
            return;
        }
        String[] strArrSplit = str.split(",");
        for (int i2 = 0; i2 < strArrSplit.length; i2++) {
            String str2 = strArrSplit[i2];
            if (str2 != null && !str2.isEmpty()) {
                this.mVideoDfpsPkgSet.add(strArrSplit[i2]);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mVideoDfpsPkgSet=" + this.mVideoDfpsPkgSet);
        StringBuilder sb = new StringBuilder(64);
        sb.append(" mEnableTimes=");
        sb.append(this.mEnableTimes);
        sb.append(" mEnableTotalTime=");
        sb.append(this.mEnableTotalTime);
        printWriter.println(sb.toString());
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what != 1) {
            return;
        }
        updateDfpsGroup((String) message.obj);
    }

    void onForegroundChanged(ForegroundInfo foregroundInfo) {
        String str;
        String str2;
        if (foregroundInfo == null || (str = foregroundInfo.mForegroundPackageName) == null || str.isEmpty()) {
            if (this.DBG_DISPLAY) {
                Log.d("DisplayDfpsHandler", "onForegroundChanged null appInfo=" + foregroundInfo);
                return;
            }
            return;
        }
        boolean zContains = this.mVideoDfpsPkgSet.contains(foregroundInfo.mForegroundPackageName);
        if (this.mLastState != zContains) {
            this.mLastState = zContains ? 1 : 0;
            if (zContains) {
                this.mEnableTimes++;
                this.mEnableBeginTime = (int) (SystemClock.elapsedRealtime() / 1000);
                str2 = "platform_set_dfps_cust_state enable";
            } else {
                this.mEnableTotalTime += ((int) (SystemClock.elapsedRealtime() / 1000)) - this.mEnableBeginTime;
                str2 = "platform_set_dfps_cust_state disable";
            }
            Log.d("DisplayDfpsHandler", str2);
            this.mLocalLog.log(str2);
            DisplayFeatureManager.getInstance().setScreenEffect(21, zContains ? 1 : 0);
        }
    }
}
