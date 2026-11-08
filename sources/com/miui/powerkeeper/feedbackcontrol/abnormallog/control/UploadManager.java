package com.miui.powerkeeper.feedbackcontrol.abnormallog.control;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoCollector;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.LogPrinter;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.PublicSpace;
import com.miui.powerkeeper.thermal.ThermalService;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.Utils;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class UploadManager {
    private final AlarmManager mAlarmManager;
    private final Context mContext;
    private final Handler mHandler;
    private PendingIntent mPendingIntent;
    private final String TAG = "ThermalUploadManager";
    private final String MODULE = "PowerKeeper";
    private final String ACTION_LIST = "ps,mlog,slog,elog,top,meminfo,cpuinfo,binderinfo,kernellog";
    private final long CLEAR_LOG_DELAY_MILLIS = ThermalInfoCollector.DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL;
    private final int UPLOAD_LOG_HOUR_OF_DAY = 4;
    private final LogPrinter mLog = LogPrinter.getLogPrinter();
    private final PublicSpace mPublicSpace = PublicSpace.getInstance();

    public UploadManager(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        alarmStart();
    }

    private void alarmStart() {
        this.mLog.d("ThermalUploadManager", "alarmStart");
        Intent intent = new Intent(this.mContext, (Class<?>) ThermalService.class);
        intent.setAction(MessageType.ACTION_SETTLEMENT);
        this.mPendingIntent = PendingIntent.getService(this.mContext, 0, intent, 67108864);
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 4);
        this.mAlarmManager.setRepeating(1, calendar.getTimeInMillis(), 86400000L, this.mPendingIntent);
    }

    private void clearLog() {
        this.mLog.d("ThermalUploadManager", "clear log");
        this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.control.UploadManager.1
            @Override // java.lang.Runnable
            public void run() {
                UploadManager.this.mLog.d("ThermalUploadManager", "start clear log");
                Objects.requireNonNull(UploadManager.this.mPublicSpace);
                FileUtil.deleteFile("/data/vendor/thermal/thermalLog");
            }
        }, ThermalInfoCollector.DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL);
    }

    private List<String> getRules() {
        this.mLog.d("ThermalUploadManager", "get rules");
        try {
            Class<?> cls = Class.forName("miui.mqsas.sdk.MQSEventManagerDelegate");
            Class[] clsArr = new Class[0];
            return (List) cls.getMethod("getRules", String.class).invoke(cls.getMethod("getInstance", null).invoke(null, null), "PowerKeeper");
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private void realUpload(String str) {
        this.mLog.d("ThermalUploadManager", "real upload");
        try {
            Class<?> cls = Class.forName("miui.mqsas.sdk.MQSEventManagerDelegate");
            Class[] clsArr = new Class[0];
            Method method = cls.getMethod("getInstance", null);
            Method method2 = cls.getMethod("commonCaptureLog", String.class, String.class, String.class, String.class);
            Object objInvoke = method.invoke(null, null);
            Objects.requireNonNull(this.mPublicSpace);
            method2.invoke(objInvoke, str, "PowerKeeper", "ps,mlog,slog,elog,top,meminfo,cpuinfo,binderinfo,kernellog", "/data/vendor/thermal/thermalLog");
            this.mLog.d("ThermalUploadManager", "real upload over");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void unregisterAlarmManager() {
        this.mAlarmManager.cancel(this.mPendingIntent);
    }

    public void uploadLog() {
        this.mLog.d("ThermalUploadManager", "upload log");
        List<String> rules = getRules();
        LogPrinter logPrinter = this.mLog;
        StringBuilder sb = new StringBuilder();
        sb.append("rules is empty");
        sb.append(String.valueOf(rules == null));
        logPrinter.d("ThermalUploadManager", sb.toString());
        if (rules != null && Utils.isUserExperienceAndPrivacyAllowed(this.mContext)) {
            try {
                this.mLog.d("ThermalUploadManager", rules.get(0));
                realUpload(String.valueOf(new JSONObject(rules.get(0)).getInt("id")));
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        clearLog();
    }
}
