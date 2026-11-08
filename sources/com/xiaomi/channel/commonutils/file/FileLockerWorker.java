package com.xiaomi.channel.commonutils.file;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public abstract class FileLockerWorker implements Runnable {
    private static final String DEFAULT_LOCKER = "default_locker";
    private Context mContext;
    private File mFile;
    private Runnable mRunnable;

    public static void runMutiProcessJob(Context context, File file, final Runnable runnable) {
        new FileLockerWorker(context, file) { // from class: com.xiaomi.channel.commonutils.file.FileLockerWorker.1
            @Override // com.xiaomi.channel.commonutils.file.FileLockerWorker
            protected void doWork(Context context2) {
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        }.run();
    }

    protected abstract void doWork(Context context);

    @Override // java.lang.Runnable
    public final void run() {
        FileLocker fileLockerLock = null;
        try {
            if (this.mFile == null) {
                this.mFile = new File(this.mContext.getFilesDir(), DEFAULT_LOCKER);
            }
            fileLockerLock = FileLocker.lock(this.mContext, this.mFile);
            Runnable runnable = this.mRunnable;
            if (runnable != null) {
                runnable.run();
            }
            doWork(this.mContext);
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            if (fileLockerLock != null) {
                fileLockerLock.unlock();
            }
        }
    }

    private FileLockerWorker(Context context, File file) {
        this.mContext = context;
        this.mFile = file;
    }

    public static void runMutiProcessJob(Context context, String str, Runnable runnable) {
        runMutiProcessJob(context, !TextUtils.isEmpty(str) ? new File(context.getFilesDir(), str) : null, runnable);
    }
}
