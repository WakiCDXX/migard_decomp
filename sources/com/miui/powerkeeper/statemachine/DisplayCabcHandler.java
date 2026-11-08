package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Log;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import miui.hardware.display.DisplayFeatureManager;
import miui.process.ForegroundInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DisplayCabcHandler extends Handler {
    private static final String CLOUD_CABC_GROUP = "cabc_group";
    private static DisplayCabcHandler sInstance;
    private final int CABC_CUST_STATE;
    private final boolean DBG_DISPLAY;
    private final int DISPALY_CABC_MODE_DEFAULT;
    private final int DISPALY_CABC_MODE_GUI;
    private final int DISPALY_CABC_MODE_MAX;
    private final int DISPALY_CABC_MODE_MOVIE;
    private final int DISPALY_CABC_MODE_STILL;
    private final int DISPALY_CABC_OFF;
    private final int MSG_CLOUD_UPDATE;
    private final String TAG;
    private int mBeginTime;
    private ArrayMap<String, Integer> mCabcPkgMap;
    private b.a mCloudObserver;
    private Context mContext;
    private String mCurrentApp;
    private ArrayMap<String, Integer> mFullscreenCabcPkgMap;
    private boolean mIsFullScreen;
    private boolean mIsNewCloud;
    private int mLastState;
    private LocalLog mLocalLog;
    private PowerStateMachine.TimeStats[] mTimeStats;

    private DisplayCabcHandler(Context context, Looper looper) {
        JSONObject oldCloudData;
        super(looper);
        this.TAG = "DisplayCabcHandler";
        this.DBG_DISPLAY = Log.isLoggable("power.cabc", 3);
        int i2 = 0;
        this.mIsNewCloud = false;
        this.mIsFullScreen = false;
        this.CABC_CUST_STATE = 7;
        this.DISPALY_CABC_OFF = 0;
        this.DISPALY_CABC_MODE_DEFAULT = 1;
        this.DISPALY_CABC_MODE_GUI = 2;
        this.DISPALY_CABC_MODE_STILL = 3;
        this.DISPALY_CABC_MODE_MOVIE = 4;
        this.DISPALY_CABC_MODE_MAX = 5;
        this.MSG_CLOUD_UPDATE = 1;
        this.mCabcPkgMap = new ArrayMap<>(128);
        this.mFullscreenCabcPkgMap = new ArrayMap<>(128);
        this.mLocalLog = new LocalLog(128);
        this.mLastState = 1;
        this.mCurrentApp = null;
        this.mTimeStats = new PowerStateMachine.TimeStats[5];
        this.mBeginTime = (int) (SystemClock.elapsedRealtime() / 1000);
        this.mContext = context;
        while (true) {
            PowerStateMachine.TimeStats[] timeStatsArr = this.mTimeStats;
            if (i2 >= timeStatsArr.length) {
                break;
            }
            timeStatsArr[i2] = new PowerStateMachine.TimeStats();
            i2++;
        }
        this.mCloudObserver = b.a.p();
        registerCloudObserver();
        b.a aVar = this.mCloudObserver;
        int i3 = b.b.f24f;
        if (aVar.s(i3)) {
            writeLocalLog("new cloud configured");
            this.mIsNewCloud = true;
            oldCloudData = this.mCloudObserver.l(i3);
        } else {
            oldCloudData = getOldCloudData();
        }
        updateCabcGroup(oldCloudData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public JSONObject getOldCloudData() {
        writeLocalLog("old cloud configured");
        this.mIsNewCloud = false;
        if (!SimpleSettings.Misc.getString(this.mContext, CLOUD_CABC_GROUP, "").isEmpty()) {
            try {
                return new JSONObject(SimpleSettings.Misc.getString(this.mContext, CLOUD_CABC_GROUP, ""));
            } catch (JSONException unused) {
                writeLocalLog("CLOUD_CABC_GROUP to JsonObject fail");
            }
        }
        return null;
    }

    public static synchronized DisplayCabcHandler init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new DisplayCabcHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        JSONObject jSONObject2;
        String strOptString = jSONObject.optString(CLOUD_CABC_GROUP, "");
        if (strOptString.equals(SimpleSettings.Misc.getString(context, CLOUD_CABC_GROUP, ""))) {
            return;
        }
        SimpleSettings.Misc.putString(context, CLOUD_CABC_GROUP, strOptString);
        DisplayCabcHandler displayCabcHandler = sInstance;
        if (displayCabcHandler == null || displayCabcHandler.mIsNewCloud) {
            return;
        }
        try {
            jSONObject2 = new JSONObject(strOptString);
        } catch (JSONException unused) {
            displayCabcHandler.writeLocalLog("CLOUD_CABC_GROUP to JsonObject fail on onCloudFunctionUpdated");
            jSONObject2 = null;
        }
        displayCabcHandler.sendMessage(displayCabcHandler.obtainMessage(1, jSONObject2));
    }

    private void registerCloudObserver() {
        this.mCloudObserver.t(b.b.f24f, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DisplayCabcHandler.1
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                if (jSONObject == null) {
                    DisplayCabcHandler displayCabcHandler = DisplayCabcHandler.this;
                    displayCabcHandler.sendMessage(displayCabcHandler.obtainMessage(1, displayCabcHandler.getOldCloudData()));
                    return;
                }
                DisplayCabcHandler.this.writeLocalLog("Cloud-Observer onChange ID_CABC_GROUP:" + jSONObject.toString());
                DisplayCabcHandler.this.mIsNewCloud = true;
                DisplayCabcHandler displayCabcHandler2 = DisplayCabcHandler.this;
                displayCabcHandler2.sendMessage(displayCabcHandler2.obtainMessage(1, jSONObject));
            }
        });
    }

    private void setCabcScreenEffect(int i2, int i3) {
        int iElapsedRealtime = (int) (SystemClock.elapsedRealtime() / 1000);
        PowerStateMachine.TimeStats[] timeStatsArr = this.mTimeStats;
        timeStatsArr[i3].mTimes++;
        timeStatsArr[i2].mTotalSec += iElapsedRealtime - this.mBeginTime;
        this.mBeginTime = iElapsedRealtime;
        try {
            DisplayFeatureManager.getInstance().setScreenEffect(7, i3);
        } catch (NullPointerException unused) {
            writeLocalLog("DisplayFeatureManager obj is null");
        }
    }

    private String stateToString(int i2) {
        if (i2 == 0) {
            return "off";
        }
        if (i2 == 2) {
            return "gui";
        }
        if (i2 != 3) {
            return i2 != 4 ? "default" : "movie";
        }
        return "still";
    }

    private void updateCabcGroup(JSONObject jSONObject) {
        String str;
        this.mCabcPkgMap.clear();
        this.mFullscreenCabcPkgMap.clear();
        if (jSONObject == null || jSONObject.toString().isEmpty()) {
            return;
        }
        String strOptString = jSONObject.optString("off", "");
        if (!strOptString.isEmpty()) {
            for (String str2 : strOptString.split(",")) {
                this.mCabcPkgMap.put(str2, 0);
            }
        }
        String strOptString2 = jSONObject.optString("gui", "");
        if (!strOptString2.isEmpty()) {
            for (String str3 : strOptString2.split(",")) {
                this.mCabcPkgMap.put(str3, 2);
            }
        }
        String strOptString3 = jSONObject.optString("still", "");
        if (!strOptString3.isEmpty()) {
            for (String str4 : strOptString3.split(",")) {
                this.mCabcPkgMap.put(str4, 3);
            }
        }
        String strOptString4 = jSONObject.optString("movie", "");
        if (!strOptString4.isEmpty()) {
            for (String str5 : strOptString4.split(",")) {
                this.mCabcPkgMap.put(str5, 4);
            }
        }
        if (this.DBG_DISPLAY) {
            Log.d("DisplayCabcHandler", "updateCabcGroup mCabcPkgMap=" + this.mCabcPkgMap);
        }
        String strOptString5 = jSONObject.optString("fullscreen_movie", "");
        if (!strOptString5.isEmpty()) {
            for (String str6 : strOptString5.split(",")) {
                this.mFullscreenCabcPkgMap.put(str6, 4);
            }
        }
        String strOptString6 = jSONObject.optString("mime_list", "");
        if (!strOptString6.isEmpty()) {
            String[] strArrSplit = strOptString6.split(",");
            for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(Uri.parse("file://"), strArrSplit[i2]);
                List<ResolveInfo> listQueryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(intent, LogType.LOG_TYPE_SAMPLING_INTERRUPT);
                if (this.DBG_DISPLAY) {
                    Log.d("DisplayCabcHandler", "updateCabcGroup type=" + strArrSplit[i2]);
                }
                for (ResolveInfo resolveInfo : listQueryIntentActivities) {
                    String str7 = resolveInfo.activityInfo.applicationInfo.processName;
                    if (str7 != null && !str7.isEmpty() && (str = resolveInfo.activityInfo.packageName) != null) {
                        this.mFullscreenCabcPkgMap.put(str, 4);
                        if (this.DBG_DISPLAY) {
                            Log.d("DisplayCabcHandler", "updateCabcGroup pkg=" + resolveInfo.activityInfo.packageName);
                        }
                    }
                }
            }
        }
        if (this.DBG_DISPLAY) {
            Log.d("DisplayCabcHandler", "updateCabcGroup mFullscreenCabcPkgMap=" + this.mFullscreenCabcPkgMap + "mIsNewCloud" + this.mIsNewCloud);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeLocalLog(String str) {
        Log.i("DisplayCabcHandler", str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mCabcPkgMap=" + this.mCabcPkgMap);
        printWriter.println("mFullscreenCabcPkgMap=" + this.mFullscreenCabcPkgMap);
        printWriter.println("mIsNewCloud=" + this.mIsNewCloud);
        StringBuilder sb = new StringBuilder(512);
        for (int i2 = 0; i2 < this.mTimeStats.length; i2++) {
            sb.append("mode=");
            sb.append(stateToString(i2));
            sb.append(" stats=");
            sb.append(this.mTimeStats[i2]);
            sb.append('\n');
        }
        printWriter.println(sb.toString());
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what != 1) {
            return;
        }
        updateCabcGroup((JSONObject) message.obj);
    }

    void onForegroundChanged(ForegroundInfo foregroundInfo, boolean z2) {
        onForegroundChanged(foregroundInfo, z2, false);
    }

    void onFullScreenEnter() {
        Integer num;
        String str = this.mCurrentApp;
        if (str == null || (num = this.mFullscreenCabcPkgMap.get(str)) == null) {
            return;
        }
        this.mIsFullScreen = true;
        int i2 = this.mLastState;
        int iIntValue = num.intValue();
        this.mLastState = iIntValue;
        if (iIntValue != i2) {
            writeLocalLog("onFullScreenEnter pkg=" + this.mCurrentApp + " platform_set_lcd_mode " + stateToString(this.mLastState));
            setCabcScreenEffect(i2, this.mLastState);
        }
    }

    void onFullScreenExit() {
        String str = this.mCurrentApp;
        if (str == null) {
            return;
        }
        int i2 = this.mLastState;
        Integer num = this.mCabcPkgMap.get(str);
        if (num == null) {
            this.mLastState = 1;
        } else {
            this.mLastState = num.intValue();
        }
        this.mIsFullScreen = false;
        if (this.mLastState != i2) {
            writeLocalLog("onFullScreenExit pkg=" + this.mCurrentApp + " platform_set_lcd_mode " + stateToString(this.mLastState));
            setCabcScreenEffect(i2, this.mLastState);
        }
    }

    void onForegroundChanged(ForegroundInfo foregroundInfo, boolean z2, boolean z3) {
        Integer num;
        int i2 = this.mLastState;
        this.mLastState = 1;
        String str = foregroundInfo == null ? null : foregroundInfo.mForegroundPackageName;
        if (str != null && !z2) {
            Integer num2 = this.mCabcPkgMap.get(str);
            if (num2 != null) {
                this.mLastState = num2.intValue();
            }
            if (this.DBG_DISPLAY) {
                String str2 = SystemProperties.get("debug.power.cabc", "");
                String[] strArrSplit = str2.isEmpty() ? null : str2.split(":");
                if (strArrSplit != null && strArrSplit.length == 2 && str.equals(strArrSplit[0])) {
                    Log.w("DisplayCabcHandler", "debug and force into mode=" + strArrSplit[1]);
                    if ("movie".equals(strArrSplit[1])) {
                        this.mLastState = 4;
                    } else if ("still".equals(strArrSplit[1])) {
                        this.mLastState = 3;
                    } else if ("off".equals(strArrSplit[1])) {
                        this.mLastState = 0;
                    }
                }
            }
        }
        if (str != null && this.mIsFullScreen && (num = this.mFullscreenCabcPkgMap.get(str)) != null) {
            this.mLastState = num.intValue();
        }
        if (str != null) {
            this.mCurrentApp = str;
        }
        if (this.mLastState != i2 || z3) {
            StringBuilder sb = new StringBuilder(128);
            sb.append("onForegroundChanged pkg=");
            sb.append(this.mCurrentApp);
            sb.append(" platform_set_lcd_mode ");
            sb.append(stateToString(this.mLastState));
            if (z2) {
                sb.append(" isSplitScreen=");
                sb.append(z2);
            }
            sb.append(" isFullScreen ");
            sb.append(this.mIsFullScreen);
            writeLocalLog(sb.toString());
            setCabcScreenEffect(i2, this.mLastState);
        }
    }
}
