package com.xiaomi.push.log;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;
import android.util.Pair;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channel.commonutils.file.FileUtils;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Constants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MIPushLog2File implements LoggerInterface {
    private static final long FILE_MAX_SIZE = 1048576;
    private static final String LOCK_FILE = "log.lock";
    private static final int LOGS_MAX_LINE = 20000;
    private static final String NEW_FILE_NAME = "log1.txt";
    private static final String OLD_FILE_NAME = "log0.txt";
    private static volatile MIPushLog2File sInstance;
    private Handler mHandler;
    private String mTag;
    private Context sAppContext;
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aaa");
    public static String MIPUSH_LOG_PATH = "/MiPushLog";
    private static List<Pair<String, Throwable>> logs = Collections.synchronizedList(new ArrayList());

    private MIPushLog2File(Context context) {
        this.sAppContext = context;
        if (context.getApplicationContext() != null) {
            this.sAppContext = context.getApplicationContext();
        }
        this.mTag = this.sAppContext.getPackageName() + Constants.ACCEPT_TIME_SEPARATOR_SERVER + Process.myPid();
        HandlerThread handlerThread = new HandlerThread("Log2FileHandlerThread");
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper());
    }

    public static MIPushLog2File getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MIPushLog2File.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new MIPushLog2File(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeLog2File() throws Throwable {
        FileLock fileLockLock;
        RandomAccessFile randomAccessFile;
        BufferedWriter bufferedWriter = null;
        try {
            try {
                File file = new File(this.sAppContext.getFilesDir(), MIPUSH_LOG_PATH);
                if (!FileUtils.canWriteInternalFileDir(file)) {
                    Log.w(this.mTag, "Cannot wirte internal file: " + file);
                    return;
                }
                if ((!file.exists() || !file.isDirectory()) && !file.mkdirs()) {
                    Log.w(this.mTag, "Create mipushlog directory fail.");
                    return;
                }
                File file2 = new File(file, LOCK_FILE);
                if (!file2.exists() || file2.isDirectory()) {
                    file2.createNewFile();
                }
                randomAccessFile = new RandomAccessFile(file2, "rw");
                try {
                    fileLockLock = randomAccessFile.getChannel().lock();
                    try {
                        try {
                            BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file, NEW_FILE_NAME), true)));
                            while (!logs.isEmpty()) {
                                try {
                                    Pair<String, Throwable> pairRemove = logs.remove(0);
                                    String str = (String) pairRemove.first;
                                    if (pairRemove.second != null) {
                                        str = (str + ShellUtils.COMMAND_LINE_END) + Log.getStackTraceString((Throwable) pairRemove.second);
                                    }
                                    bufferedWriter2.write(str + ShellUtils.COMMAND_LINE_END);
                                } catch (Exception e2) {
                                    e = e2;
                                    bufferedWriter = bufferedWriter2;
                                    Log.e(this.mTag, "", e);
                                    if (bufferedWriter != null) {
                                        try {
                                            bufferedWriter.close();
                                        } catch (IOException e3) {
                                            Log.e(this.mTag, "", e3);
                                        }
                                    }
                                    if (fileLockLock != null && fileLockLock.isValid()) {
                                        try {
                                            fileLockLock.release();
                                        } catch (IOException e4) {
                                            Log.e(this.mTag, "", e4);
                                        }
                                    }
                                    if (randomAccessFile == null) {
                                        return;
                                    } else {
                                        randomAccessFile.close();
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    bufferedWriter = bufferedWriter2;
                                    if (bufferedWriter != null) {
                                        try {
                                            bufferedWriter.close();
                                        } catch (IOException e5) {
                                            Log.e(this.mTag, "", e5);
                                        }
                                    }
                                    if (fileLockLock != null && fileLockLock.isValid()) {
                                        try {
                                            fileLockLock.release();
                                        } catch (IOException e6) {
                                            Log.e(this.mTag, "", e6);
                                        }
                                    }
                                    if (randomAccessFile == null) {
                                        throw th;
                                    }
                                    try {
                                        randomAccessFile.close();
                                        throw th;
                                    } catch (IOException e7) {
                                        Log.e(this.mTag, "", e7);
                                        throw th;
                                    }
                                }
                            }
                            bufferedWriter2.flush();
                            bufferedWriter2.close();
                            File file3 = new File(file, NEW_FILE_NAME);
                            if (file3.length() >= 1048576) {
                                File file4 = new File(file, OLD_FILE_NAME);
                                if (file4.exists() && file4.isFile()) {
                                    file4.delete();
                                }
                                file3.renameTo(file4);
                            }
                            if (fileLockLock != null && fileLockLock.isValid()) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException e8) {
                                    Log.e(this.mTag, "", e8);
                                }
                            }
                            randomAccessFile.close();
                        } catch (Exception e9) {
                            e = e9;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (Exception e10) {
                    e = e10;
                    fileLockLock = null;
                } catch (Throwable th3) {
                    th = th3;
                    fileLockLock = null;
                }
            } catch (IOException e11) {
                Log.e(this.mTag, "", e11);
            }
        } catch (Exception e12) {
            e = e12;
            fileLockLock = null;
            randomAccessFile = null;
        } catch (Throwable th4) {
            th = th4;
            fileLockLock = null;
            randomAccessFile = null;
        }
    }

    @Override // com.xiaomi.channel.commonutils.logger.LoggerInterface
    public final void log(String str) {
        log(str, null);
    }

    @Override // com.xiaomi.channel.commonutils.logger.LoggerInterface
    public final void setTag(String str) {
        this.mTag = str;
    }

    @Override // com.xiaomi.channel.commonutils.logger.LoggerInterface
    public final void log(final String str, final Throwable th) {
        this.mHandler.post(new Runnable() { // from class: com.xiaomi.push.log.MIPushLog2File.1
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                MIPushLog2File.logs.add(new Pair(String.format("%1$s %2$s %3$s ", MIPushLog2File.dateFormatter.format(new Date()), MIPushLog2File.this.mTag, str), th));
                if (MIPushLog2File.logs.size() > MIPushLog2File.LOGS_MAX_LINE) {
                    int size = MIPushLog2File.logs.size() - 19950;
                    for (int i2 = 0; i2 < size; i2++) {
                        try {
                            if (MIPushLog2File.logs.size() > 0) {
                                MIPushLog2File.logs.remove(0);
                            }
                        } catch (IndexOutOfBoundsException unused) {
                        }
                    }
                    MIPushLog2File.logs.add(new Pair(String.format("%1$s %2$s %3$s ", MIPushLog2File.dateFormatter.format(new Date()), MIPushLog2File.this.mTag, "flush " + size + " lines logs."), null));
                }
                try {
                    MIPushLog2File.this.writeLog2File();
                } catch (Exception e2) {
                    Log.e(MIPushLog2File.this.mTag, "", e2);
                }
            }
        });
    }
}
