package com.xiaomi.push.crash;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.channel.commonutils.android.CrashExceptionHandler;
import com.xiaomi.channel.commonutils.android.SystemProperties;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.BuildSettings;
import com.xiaomi.channel.commonutils.network.HttpResponse;
import com.xiaomi.channel.commonutils.network.Network;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class SdkCrashManager {
    private static volatile SdkCrashManager sInstance;
    private static ThreadPoolExecutor sPool = new ThreadPoolExecutor(1, 1, 15, TimeUnit.SECONDS, new LinkedBlockingQueue());
    private Context mAppCxt;
    private final Object mLock = new Object();
    private Thread.UncaughtExceptionHandler mOtherDefault = Thread.getDefaultUncaughtExceptionHandler();
    private SdkCrashStorage mStorage;

    private SdkCrashManager(Context context) {
        this.mAppCxt = context.getApplicationContext();
        this.mStorage = new SdkCrashStorage(context);
        Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler() { // from class: com.xiaomi.push.crash.SdkCrashManager.1
            @Override // com.xiaomi.channel.commonutils.android.CrashExceptionHandler
            public void handleCrashException(final Thread thread, final Throwable th, final int i2) {
                MyLog.e("scr receive ex t:" + thread + " p:" + i2, th);
                try {
                    SdkCrashManager.sPool.execute(new Runnable() { // from class: com.xiaomi.push.crash.SdkCrashManager.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SdkCrashManager.this.handleCrash(thread, th, i2);
                        }
                    });
                    synchronized (SdkCrashManager.this.mLock) {
                        try {
                            SdkCrashManager.this.mLock.wait(500L);
                        } catch (Exception unused) {
                        }
                    }
                } catch (Throwable unused2) {
                    SdkCrashManager.this.dispatchToOther(thread, th);
                }
            }
        });
    }

    private boolean canReport() {
        long jCurrentTimeMillis = System.currentTimeMillis() - this.mStorage.getLastUploadTime();
        if (jCurrentTimeMillis >= 180000) {
            if (Network.isWIFIConnected(this.mAppCxt)) {
                return true;
            }
            MyLog.i("scr not rep because net is other");
            return false;
        }
        MyLog.i("scr not rep because time " + jCurrentTimeMillis);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchToOther(Thread thread, Throwable th) {
        if (this.mOtherDefault != null) {
            MyLog.i("scr dispatch to " + this.mOtherDefault);
            this.mOtherDefault.uncaughtException(thread, th);
            return;
        }
        MyLog.w("scr kill process " + Process.myPid());
        Process.killProcess(Process.myPid());
        System.exit(1);
    }

    private static SdkCrashManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SdkCrashManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new SdkCrashManager(context);
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
            String str = SystemProperties.get("log.tag.debug.sdk.cr", "");
            if (!TextUtils.isEmpty(str)) {
                MyLog.w("[debug] scr rep url：" + str);
                return str;
            }
        }
        if (SdkCrashUtils.isChinaRegion()) {
            return "https://api.xmpush.xiaomi.com/v1/trace/report/sdk";
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCrash(Thread thread, Throwable th, int i2) {
        try {
            try {
                if (!isEnabled()) {
                    MyLog.w("scr not enabled, dispatch this ex");
                    dispatchToOther(thread, th);
                    return;
                }
                String stackTraceString = Log.getStackTraceString(th);
                if (SdkCrashUtils.isMipushStack(stackTraceString)) {
                    this.mStorage.insert(SdkCrashUtils.formatJavaStack(stackTraceString), i2);
                    tryReportCrash();
                } else {
                    MyLog.w("scr not found, dispatch this ex");
                    dispatchToOther(thread, th);
                }
            } catch (Throwable unused) {
                MyLog.w("scr handle error " + th);
            }
        } finally {
            dispatchToOther(thread, th);
        }
    }

    private void init() {
        MyLog.w("scr init in " + Process.myPid());
        sPool.execute(new Runnable() { // from class: com.xiaomi.push.crash.SdkCrashManager.2
            @Override // java.lang.Runnable
            public void run() {
                SdkCrashManager.this.tryReportCrash();
            }
        });
    }

    private boolean isEnabled() {
        return SdkCrashUtils.isOcEnabled(this.mAppCxt) && SdkCrashUtils.isChinaRegion();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryReportCrash() {
        try {
            if (canReport()) {
                JSONArray all = this.mStorage.getAll();
                if (all != null && all.length() > 0) {
                    HashMap map = new HashMap();
                    map.put("report", all.toString());
                    HttpResponse httpResponseDoGzipPost = Network.doGzipPost(this.mAppCxt, getReportUrl(), map);
                    int i2 = httpResponseDoGzipPost != null ? httpResponseDoGzipPost.responseCode : -1;
                    MyLog.i("scr response code " + i2);
                    if (i2 == 200) {
                        int i3 = new JSONObject(httpResponseDoGzipPost.getResponseString()).getInt("code");
                        if (i3 == 0) {
                            this.mStorage.deleteAll();
                            return;
                        }
                        MyLog.i("scr error code " + i3);
                        return;
                    }
                    return;
                }
                MyLog.i("scr no data to rep");
            }
        } catch (Throwable th) {
            MyLog.w("scr rep error " + th);
        }
    }

    public static void listenCrash(Context context) {
    }
}
