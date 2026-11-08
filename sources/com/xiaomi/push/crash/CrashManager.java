package com.xiaomi.push.crash;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.channel.commonutils.android.CrashExceptionHandler;
import com.xiaomi.channel.commonutils.android.SystemProperties;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.misc.BuildSettings;
import com.xiaomi.channel.commonutils.network.HttpResponse;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.push.extension.LogPools;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.xmpush.thrift.ConfigKey;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CrashManager {
    private static final String CRASH_THREAD_NAME = "ht-CrashThread";
    private static final int DELAY_TIME = 5000;
    private static final int MAX_REPORT_LOOP_COUNT = 3;
    private static final int MSG_WHAT_KILL = 2;
    private static final int MSG_WHAT_SAVE_EXP = 0;
    private static final int MSG_WHAT_TRY_REPORT = 1;
    private static volatile CrashManager sInstance;
    private CrashDb mDb;
    private Handler mHandler;
    private volatile long mLastTriggerReportTime = 0;
    private volatile boolean mHasDatasNeedToUpload = true;

    private CrashManager(Context context) {
        this.mDb = new CrashDb(context);
        HandlerThread handlerThread = new HandlerThread(CRASH_THREAD_NAME);
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper(), new Handler.Callback() { // from class: com.xiaomi.push.crash.CrashManager.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                CrashManager.this.consumeMessage(message);
                return true;
            }
        });
    }

    private boolean canReport() {
        if (!isExceptionMonitorSwitch()) {
            LogPools.CrashLog.d("not rep because disable oc");
            return false;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime() - this.mLastTriggerReportTime;
        if (jElapsedRealtime >= 60000) {
            if (Network.isWIFIConnected(getContext())) {
                return true;
            }
            LogPools.CrashLog.d("not rep because net is other");
            return false;
        }
        LogPools.CrashLog.d("not rep because time " + jElapsedRealtime);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void consumeMessage(Message message) {
        if (message == null) {
            return;
        }
        int i2 = message.what;
        if (i2 == 0) {
            saveCrashInHandler((Throwable) message.obj, message.arg1);
            return;
        }
        if (i2 != 1) {
            if (i2 != 2) {
                return;
            }
            killProcess();
        } else if (canReport()) {
            triggerReportInHandler(String.valueOf(message.obj));
            this.mHasDatasNeedToUpload = this.mDb.checkDatas();
        }
    }

    private Context getContext() {
        return this.mDb.getContext();
    }

    public static CrashManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CrashManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new CrashManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private String getReportUrl() {
        if (BuildSettings.IsOneBoxBuild()) {
            String str = SystemProperties.get("log.tag.debug.xmsf.cr", "");
            if (!TextUtils.isEmpty(str)) {
                LogPools.CrashLog.w("[debug] cr rep url：" + str);
                return str;
            }
        }
        if (CrashUtils.isChinaRegion(getContext())) {
            return "https://api.xmpush.xiaomi.com/v1/trace/report";
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCrash(Thread thread, Throwable th, int i2) {
        if (!isEnabled()) {
            LogPools.CrashLog.d("disabled");
            return;
        }
        if (thread == null || !CRASH_THREAD_NAME.equals(thread.getName())) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 0;
            messageObtain.arg1 = i2;
            messageObtain.obj = th;
            this.mHandler.sendMessage(messageObtain);
        }
    }

    private boolean isEnabled() {
        return isExceptionMonitorSwitch() && CrashUtils.isChinaRegion(getContext());
    }

    private boolean isExceptionMonitorSwitch() {
        return OnlineConfig.getInstance(getContext()).getBooleanValue(ConfigKey.ExceptionMonitorSwitch.getValue(), true);
    }

    private void killProcess() {
        LogPools.CrashLog.w("exit");
        Process.killProcess(Process.myPid());
        System.exit(1);
    }

    public static void listenCrash(final Context context) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler() { // from class: com.xiaomi.push.crash.CrashManager.2
            @Override // com.xiaomi.channel.commonutils.android.CrashExceptionHandler
            public void handleCrashException(Thread thread, Throwable th, int i2) {
                LogPools.CrashLog.e("t:" + thread + ", p:" + i2, th);
                try {
                    CrashManager.getInstance(context).handleCrash(thread, th, i2);
                } catch (Throwable th2) {
                    LogPools.CrashLog.w("handle error " + th2);
                }
            }
        });
        try {
            getInstance(context).tryReportCrash("Init", PluginConstants.TTS_REMOVE_BEAN_TIME);
        } catch (Throwable th) {
            LogPools.CrashLog.w("try init error " + th);
        }
    }

    private void saveCrashInHandler(Throwable th, int i2) throws RuntimeException {
        try {
            this.mDb.insert(Log.getStackTraceString(th).replaceAll("\\t", "").replaceAll("\\n", "#n"), 0, i2);
            this.mHasDatasNeedToUpload = true;
        } catch (Exception e2) {
            LogPools.CrashLog.w("save fail " + e2);
        }
        tryReportCrash("Caught", 0L);
        if (i2 == 3) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 2;
            this.mHandler.sendMessage(messageObtain);
        }
    }

    /* JADX WARN: Code duplicated, block: B:30:0x00f2  */
    private void triggerReportInHandler(String str) {
        this.mLastTriggerReportTime = SystemClock.elapsedRealtime();
        LogPools.CrashLog.d("do rep from " + str);
        int i2 = 0;
        boolean z2 = true;
        while (z2) {
            i2++;
            if (i2 > 3) {
                LogPools.CrashLog.w("exit loop rep");
                return;
            }
            try {
                List<RecordBean> listQuery = this.mDb.query(12);
                if (ToolUtils.isEmptyList(listQuery)) {
                    z2 = false;
                } else {
                    JSONArray jSONArray = new JSONArray();
                    for (RecordBean recordBean : listQuery) {
                        JSONObject json = recordBean.toJson();
                        if (json == null) {
                            LogPools.CrashLog.w("json error " + recordBean);
                        } else {
                            jSONArray.put(json);
                        }
                    }
                    HashMap map = new HashMap();
                    map.put("report", jSONArray.toString());
                    HttpResponse httpResponseDoGzipPost = Network.doGzipPost(getContext(), getReportUrl(), map);
                    int i3 = httpResponseDoGzipPost != null ? httpResponseDoGzipPost.responseCode : -1;
                    LogPools.LogModule logModule = LogPools.CrashLog;
                    logModule.i("response code " + i3);
                    if (i3 != 200) {
                        z2 = false;
                    } else {
                        int i4 = new JSONObject(httpResponseDoGzipPost.getResponseString()).getInt("code");
                        if (i4 == 0) {
                            this.mDb.updateWhenUploaded(listQuery, true);
                            z2 = true;
                        } else {
                            logModule.i("response error code " + i4);
                            z2 = false;
                        }
                    }
                }
            } catch (Exception e2) {
                LogPools.CrashLog.w("report error " + e2);
            }
            if (z2) {
                try {
                    Thread.sleep(100L);
                } catch (Exception unused) {
                }
            }
        }
    }

    private void tryReportCrash(String str, long j2) {
        LogPools.CrashLog.d("try rep from " + str + " has:" + this.mHasDatasNeedToUpload);
        if (this.mHasDatasNeedToUpload) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 1;
            messageObtain.obj = str;
            this.mHandler.sendMessageDelayed(messageObtain, j2);
        }
    }

    public void onPong() {
        tryReportCrash("Pong", 0L);
    }
}
