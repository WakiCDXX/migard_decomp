package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import miui.hardware.display.DisplayFeatureManager;
import miui.process.ForegroundInfo;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DisplayStandardVideoHandler extends Handler {
    private static final String CLOUD_STANDARD_VIDEO_GROUP = "standard_video_group";
    private static final String TAG = "DisplayStandardVideoHandler";
    private static DisplayStandardVideoHandler sInstance;
    private final boolean DBG_DISPLAY;
    private final int MODE_STANDARD_VIDEO;
    private final int MSG_CLOUD_UPDATE;
    private final int VALUE_OFF;
    private Context mContext;
    private int mCurrentValue;
    private LocalLog mLocalLog;
    private ArrayMap<String, Integer> mVideoPkgMap;

    private DisplayStandardVideoHandler(Context context, Looper looper) {
        super(looper);
        this.DBG_DISPLAY = Log.isLoggable("power.display", 3);
        this.MODE_STANDARD_VIDEO = 28;
        this.VALUE_OFF = 0;
        this.MSG_CLOUD_UPDATE = 3;
        this.mLocalLog = new LocalLog(32);
        this.mVideoPkgMap = new ArrayMap<>(10);
        this.mCurrentValue = 0;
        this.mContext = context;
        updateConfig(SimpleSettings.Misc.getString(context, CLOUD_STANDARD_VIDEO_GROUP, ""));
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.registerForForegroundInfo(this);
            eventsAggregator.registerForUserPresent(this, null);
        }
    }

    public static synchronized DisplayStandardVideoHandler init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new DisplayStandardVideoHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        String strOptString = jSONObject.optString(CLOUD_STANDARD_VIDEO_GROUP, null);
        if (strOptString == null || strOptString.equals(SimpleSettings.Misc.getString(context, CLOUD_STANDARD_VIDEO_GROUP, ""))) {
            return;
        }
        SimpleSettings.Misc.putString(context, CLOUD_STANDARD_VIDEO_GROUP, strOptString);
        DisplayStandardVideoHandler displayStandardVideoHandler = sInstance;
        if (displayStandardVideoHandler != null) {
            displayStandardVideoHandler.sendMessage(displayStandardVideoHandler.obtainMessage(3, strOptString));
        }
    }

    private void updateConfig(String str) {
        String[] strArrSplit;
        this.mVideoPkgMap.clear();
        if (this.DBG_DISPLAY) {
            Log.i(TAG, "updateConfig videoGroup=" + str);
        }
        if (str == null || str.isEmpty()) {
            return;
        }
        String[] strArrSplit2 = str.split(",");
        for (int i2 = 0; i2 < strArrSplit2.length; i2++) {
            String str2 = strArrSplit2[i2];
            if (str2 != null && !str2.isEmpty() && (strArrSplit = strArrSplit2[i2].split(":")) != null && strArrSplit.length == 2) {
                try {
                    this.mVideoPkgMap.put(strArrSplit[0], Integer.valueOf(Integer.parseInt(strArrSplit[1], 10)));
                } catch (Exception unused) {
                }
            }
        }
    }

    private void updateStandardVideo(ForegroundInfo foregroundInfo) {
        String str;
        if (foregroundInfo == null || (str = foregroundInfo.mForegroundPackageName) == null) {
            return;
        }
        Integer num = this.mVideoPkgMap.get(str);
        int iIntValue = num == null ? 0 : num.intValue();
        if (iIntValue == this.mCurrentValue) {
            return;
        }
        this.mCurrentValue = iIntValue;
        String str2 = "platform_set_standard_video_state value=" + this.mCurrentValue;
        Log.i(TAG, str2);
        this.mLocalLog.log(str2);
        DisplayFeatureManager.getInstance().setScreenEffect(28, iIntValue);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mVideoPkgMap=" + this.mVideoPkgMap);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -9) {
            updateStandardVideo((ForegroundInfo) message.obj);
            return;
        }
        if (i2 != -7) {
            if (i2 != 3) {
                Log.w(TAG, "warning for access here");
                return;
            } else {
                updateConfig((String) message.obj);
                return;
            }
        }
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            updateStandardVideo(eventsAggregator.getForegroundInfo());
        }
    }
}
