package com.xiaomi.channel.commonutils.logger;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Process;
import android.util.Log;
import com.xiaomi.channel.channelcommonutils.BuildConfig;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public abstract class MyLog {
    public static final int DEBUG = 1;
    private static String DEFAULT_TAG = null;
    public static final int ERROR = 4;
    public static final int FATAL = 5;
    public static final int INFO = 0;
    private static int LOG_LEVEL = 0;
    private static final Integer NEGATIVE_CODE;
    private static final String TAG = "MyLog";
    public static final int WARN = 2;
    private static boolean isMiui;
    private static boolean isXMSF;
    private static LoggerInterface logger;
    private static final HashMap<Integer, String> mActionNames;
    private static AtomicInteger mCodeGenerator;
    private static final HashMap<Integer, Long> mStartTimes;
    private static Context sContext;

    static class DefaultAndroidLogger implements LoggerInterface {
        private String mTag = MyLog.DEFAULT_TAG;

        DefaultAndroidLogger() {
        }

        @Override // com.xiaomi.channel.commonutils.logger.LoggerInterface
        public void log(String str) {
            Log.v(this.mTag, str);
        }

        @Override // com.xiaomi.channel.commonutils.logger.LoggerInterface
        public void setTag(String str) {
            this.mTag = str;
        }

        @Override // com.xiaomi.channel.commonutils.logger.LoggerInterface
        public void log(String str, Throwable th) {
            Log.v(this.mTag, str, th);
        }
    }

    static {
        LOG_LEVEL = BuildConfig.DEBUG ? 0 : 2;
        isXMSF = false;
        isMiui = false;
        DEFAULT_TAG = "XMPush-" + Process.myPid();
        logger = new DefaultAndroidLogger();
        mStartTimes = new HashMap<>();
        mActionNames = new HashMap<>();
        NEGATIVE_CODE = -1;
        mCodeGenerator = new AtomicInteger(1);
    }

    public static void e(String str, Throwable th) {
        if (needLog(4)) {
            log(4, wrapMessage(str), th);
        }
    }

    private static boolean enableDebugLog() {
        return BuildConfig.DEBUG;
    }

    public static int getLogLevel() {
        return LOG_LEVEL;
    }

    public static void heapInfo(Context context, String str) {
        Debug.MemoryInfo[] processMemoryInfo = ((ActivityManager) context.getSystemService("activity")).getProcessMemoryInfo(new int[]{Process.myPid()});
        i("+++Heap Info+++" + str + " total:" + processMemoryInfo[0].getTotalPss() + ", managed:" + processMemoryInfo[0].dalvikPss + ", native:" + processMemoryInfo[0].nativePss);
    }

    public static void i(String str) {
        if (needLog(0)) {
            log(0, wrapMessage(str));
        }
    }

    public static void init(Context context) {
        sContext = context;
        if (MIUIUtils.isXMSF(context)) {
            isXMSF = true;
        }
        if (MIUIUtils.isMIUI()) {
            isMiui = true;
        }
    }

    private static String jointThreadId() {
        return "[Tid:" + Thread.currentThread().getId() + "] ";
    }

    public static void log(int i2, String str) {
        if (i2 >= LOG_LEVEL) {
            logger.log(str);
            return;
        }
        if (enableDebugLog()) {
            Log.d(TAG, "-->log(" + i2 + "): " + str);
        }
    }

    private static boolean needLog(int i2) {
        return i2 >= LOG_LEVEL || enableDebugLog();
    }

    public static void pe(Integer num) {
        if (LOG_LEVEL <= 1) {
            HashMap<Integer, Long> map = mStartTimes;
            if (map.containsKey(num)) {
                long jLongValue = map.remove(num).longValue();
                String strRemove = mActionNames.remove(num);
                long jCurrentTimeMillis = System.currentTimeMillis() - jLongValue;
                logger.log(strRemove + " ends in " + jCurrentTimeMillis + " ms");
            }
        }
    }

    public static void persist(String str) {
        if (isXMSF) {
            w(str);
            return;
        }
        Log.w(DEFAULT_TAG, wrapMessage(str));
        if (isMiui) {
            return;
        }
        w(str);
    }

    public static void printCallStack(String str) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println(str);
        printWriter.println(String.format("Current thread id (%s); thread name (%s)", Long.valueOf(Thread.currentThread().getId()), Thread.currentThread().getName()));
        new Throwable("Call stack").printStackTrace(printWriter);
        v(stringWriter.toString());
    }

    public static Integer ps(String str) {
        if (LOG_LEVEL > 1) {
            return NEGATIVE_CODE;
        }
        Integer numValueOf = Integer.valueOf(mCodeGenerator.incrementAndGet());
        mStartTimes.put(numValueOf, Long.valueOf(System.currentTimeMillis()));
        mActionNames.put(numValueOf, str);
        logger.log(str + " starts");
        return numValueOf;
    }

    public static void setLogLevel(int i2) {
        if (i2 < 0 || i2 > 5) {
            log(2, "set log level as " + i2);
        }
        LOG_LEVEL = i2;
    }

    public static void setLogger(LoggerInterface loggerInterface) {
        logger = loggerInterface;
    }

    public static void v(String str) {
        if (needLog(0)) {
            log(1, wrapMessage(str));
        }
    }

    public static void w(String str) {
        if (needLog(2)) {
            log(2, wrapMessage(str));
        }
    }

    private static String wrapMessage(String str) {
        return jointThreadId() + str;
    }

    public static String wrapTagMsg(String str, String str2) {
        return "[" + str + "] " + str2;
    }

    private static String wrapMessage(String str, String str2) {
        return jointThreadId() + wrapTagMsg(str, str2);
    }

    public static void e(Throwable th) {
        if (needLog(4)) {
            log(4, th);
        }
    }

    public static void i(Object... objArr) {
        if (needLog(0)) {
            log(0, wrapMessage(objArr));
        }
    }

    public static void v(String str, String str2) {
        if (needLog(1)) {
            log(1, wrapMessage(str, str2));
        }
    }

    public static void w(Object... objArr) {
        if (needLog(2)) {
            log(2, wrapMessage(objArr));
        }
    }

    private static String wrapMessage(Object... objArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Tid:");
        sb.append(Thread.currentThread().getId());
        sb.append("] ");
        if (objArr != null && objArr.length > 0) {
            for (Object obj : objArr) {
                sb.append(obj);
            }
        }
        return sb.toString();
    }

    public static void e(String str) {
        if (needLog(4)) {
            log(4, wrapMessage(str));
        }
    }

    public static void log(int i2, Throwable th) {
        if (i2 >= LOG_LEVEL) {
            logger.log("", th);
            return;
        }
        if (enableDebugLog()) {
            Log.w(TAG, "-->log(" + i2 + "): ", th);
        }
    }

    public static void v(String str, Object... objArr) {
        if (needLog(1)) {
            log(1, wrapMessage(str, objArr));
        }
    }

    public static void w(String str, String str2) {
        if (needLog(2)) {
            log(2, wrapMessage(str, str2));
        }
    }

    public static void persist(String str, String str2) {
        if (isXMSF) {
            w(str, str2);
            return;
        }
        Log.w(DEFAULT_TAG, wrapMessage(str, str2));
        if (isMiui) {
            return;
        }
        w(str, str2);
    }

    public static void e(Object... objArr) {
        if (needLog(4)) {
            log(4, wrapMessage(objArr));
        }
    }

    public static void v(Object[] objArr) {
        if (needLog(1)) {
            log(1, XMStringUtils.join(objArr, ","));
        }
    }

    public static void w(String str, Object... objArr) {
        if (needLog(2)) {
            log(2, wrapMessage(str, objArr));
        }
    }

    public static void log(int i2, String str, Throwable th) {
        if (i2 >= LOG_LEVEL) {
            logger.log(str, th);
            return;
        }
        if (enableDebugLog()) {
            Log.w(TAG, "-->log(" + i2 + "): " + str, th);
        }
    }

    private static String wrapMessage(String str, Object... objArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Tid:");
        sb.append(Thread.currentThread().getId());
        sb.append("] ");
        sb.append("[");
        sb.append(str);
        sb.append("] ");
        if (objArr != null && objArr.length > 0) {
            for (Object obj : objArr) {
                sb.append(obj);
            }
        }
        return sb.toString();
    }
}
