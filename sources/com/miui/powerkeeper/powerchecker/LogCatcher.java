package com.miui.powerkeeper.powerchecker;

import android.os.Build;
import android.util.LocalLog;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.FastPrintWriter;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class LogCatcher {
    private static final boolean DEBUG;
    private static final int MAX_HISTORY_ITEMS;
    private static String TAG = "PowerChecker.LogCatcher";
    private static LogCatcher sInstance;
    private SparseArray<LoggingRequest> mUidLoggingRequest = new SparseArray<>();
    private LocalLog mLog = new LocalLog(MAX_HISTORY_ITEMS);

    class LoggingRequest {
        boolean mLogcatRun = false;
        StringBuilder mLogBuffer = new StringBuilder();

        LoggingRequest() {
        }
    }

    static {
        boolean z2 = Build.IS_DEBUGGABLE;
        DEBUG = z2;
        MAX_HISTORY_ITEMS = z2 ? 512 : 256;
    }

    public static LogCatcher getInstance() {
        if (sInstance == null) {
            sInstance = new LogCatcher();
        }
        return sInstance;
    }

    public void clearLoggingRequest() {
        for (int i2 = 0; i2 < this.mUidLoggingRequest.size(); i2++) {
            LoggingRequest loggingRequestValueAt = this.mUidLoggingRequest.valueAt(i2);
            if (loggingRequestValueAt.mLogBuffer.length() > 0) {
                loggingRequestValueAt.mLogBuffer = null;
                loggingRequestValueAt.mLogcatRun = false;
            }
        }
        this.mUidLoggingRequest.clear();
    }

    public void debugLog(String str) {
        this.mLog.log(str);
    }

    public String getAllDebugLog() {
        StringWriter stringWriter = new StringWriter();
        FastPrintWriter fastPrintWriter = new FastPrintWriter(stringWriter, false, 1024);
        this.mLog.dump((FileDescriptor) null, fastPrintWriter, (String[]) null);
        fastPrintWriter.flush();
        fastPrintWriter.close();
        return stringWriter.toString();
    }

    public LoggingRequest getLoggingRequest(int i2) {
        if (this.mUidLoggingRequest.indexOfKey(i2) >= 0) {
            return this.mUidLoggingRequest.get(i2);
        }
        LoggingRequest loggingRequest = new LoggingRequest();
        this.mUidLoggingRequest.put(i2, loggingRequest);
        return loggingRequest;
    }

    public boolean hasLoggingRequestPending() {
        int i2 = 0;
        for (int i3 = 0; i3 < this.mUidLoggingRequest.size(); i3++) {
            if (!this.mUidLoggingRequest.valueAt(i3).mLogcatRun) {
                i2++;
            }
        }
        return i2 != 0;
    }

    public void startLogging(HashMap<Integer, List<String>> map) {
        if (map.size() > 0) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(Runtime.getRuntime().exec("logcat -v threadtime").getInputStream());
                try {
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    while (true) {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            String[] strArrSplit = line.split(" ");
                            if (strArrSplit.length > 6) {
                                StringBuilder sb = new StringBuilder(strArrSplit[5]);
                                sb.deleteCharAt(sb.length() - 1);
                                for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
                                    int iIntValue = entry.getKey().intValue();
                                    Iterator<String> it = entry.getValue().iterator();
                                    while (it.hasNext()) {
                                        if (sb.toString().trim().equalsIgnoreCase(it.next())) {
                                            this.mUidLoggingRequest.get(iIntValue).mLogBuffer.append(line);
                                        }
                                    }
                                }
                            }
                        } catch (Throwable th) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                        try {
                            inputStreamReader.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                        throw th;
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                } catch (Throwable th4) {
                    inputStreamReader.close();
                    throw th4;
                }
            } catch (IOException e2) {
                Log.e(TAG, "startLogging", e2);
            }
        }
        for (int i2 = 0; i2 < this.mUidLoggingRequest.size(); i2++) {
            this.mUidLoggingRequest.valueAt(i2).mLogcatRun = true;
        }
    }
}
