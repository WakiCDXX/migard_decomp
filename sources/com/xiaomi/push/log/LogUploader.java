package com.xiaomi.push.log;

import android.content.Context;
import android.content.SharedPreferences;
import com.xiaomi.channel.commonutils.file.FileUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.ServiceConfig;
import com.xiaomi.smack.util.TaskExecutor;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class LogUploader {
    private static final int EXPIRE_TIME = 172800000;
    private static final int MAX_PENDING_TASKS = 6;
    private static final int MAX_TIMES_PER_DAY = 10;
    private static final String PREF_KEY_REQUEST = "log.requst";
    private static final String PREF_NAME = "log.timestamp";
    private static final String ZIPPED_LOG_PATH = "/.logcache";
    private static volatile LogUploader sInstance;
    private Context mContext;
    private final ConcurrentLinkedQueue<Task> mTasks;

    class CleanUpTask extends Task {
        CleanUpTask() {
            super();
        }

        @Override // com.xiaomi.push.log.LogUploader.Task, com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
        public void process() {
            LogUploader.this.cleanUp();
        }
    }

    class UploadTask extends Task {
        File file;
        boolean force;
        int retryNum;
        String token;
        boolean uploaded;
        String url;

        UploadTask(String str, String str2, File file, boolean z2) {
            super();
            this.url = str;
            this.token = str2;
            this.file = file;
            this.force = z2;
        }

        private boolean checkLimit() {
            int i2;
            int i3 = 0;
            SharedPreferences sharedPreferences = LogUploader.this.mContext.getSharedPreferences(LogUploader.PREF_NAME, 0);
            String string = sharedPreferences.getString(LogUploader.PREF_KEY_REQUEST, "");
            long jCurrentTimeMillis = System.currentTimeMillis();
            try {
                JSONObject jSONObject = new JSONObject(string);
                jCurrentTimeMillis = jSONObject.getLong("time");
                i2 = jSONObject.getInt("times");
            } catch (JSONException unused) {
                i2 = 0;
            }
            if (System.currentTimeMillis() - jCurrentTimeMillis >= 86400000) {
                jCurrentTimeMillis = System.currentTimeMillis();
            } else {
                if (i2 > 10) {
                    return false;
                }
                i3 = i2;
            }
            JSONObject jSONObject2 = new JSONObject();
            try {
                jSONObject2.put("time", jCurrentTimeMillis);
                jSONObject2.put("times", i3 + 1);
                sharedPreferences.edit().putString(LogUploader.PREF_KEY_REQUEST, jSONObject2.toString()).commit();
            } catch (JSONException e2) {
                MyLog.v("JSONException on put " + e2.getMessage());
            }
            return true;
        }

        @Override // com.xiaomi.push.log.LogUploader.Task
        public boolean canExcuteNow() {
            return Network.isWIFIConnected(LogUploader.this.mContext) || (this.force && Network.hasNetwork(LogUploader.this.mContext));
        }

        @Override // com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
        public void postProcess() {
            if (!this.uploaded) {
                int i2 = this.retryNum + 1;
                this.retryNum = i2;
                if (i2 < 3) {
                    LogUploader.this.mTasks.add(this);
                }
            }
            if (this.uploaded || this.retryNum >= 3) {
                this.file.delete();
            }
            LogUploader.this.uploadIfNeed((1 << this.retryNum) * 1000);
        }

        @Override // com.xiaomi.push.log.LogUploader.Task, com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
        public void process() {
            try {
                if (checkLimit()) {
                    HashMap map = new HashMap();
                    map.put("uid", ServiceConfig.getDeviceUUID());
                    map.put("token", this.token);
                    map.put("net", Network.getActiveConnPoint(LogUploader.this.mContext));
                    Network.uploadFile(this.url, map, this.file, PushConstants.UPLOAD_FILE_POST_KEY);
                }
                this.uploaded = true;
            } catch (IOException unused) {
            }
        }
    }

    private LogUploader(Context context) {
        ConcurrentLinkedQueue<Task> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
        this.mTasks = concurrentLinkedQueue;
        this.mContext = context;
        concurrentLinkedQueue.add(new CleanUpTask());
        executeTask(0L);
    }

    private void cleanExpiredTask() {
        while (!this.mTasks.isEmpty()) {
            Task taskPeek = this.mTasks.peek();
            if (taskPeek != null) {
                if (!taskPeek.isExpired() && this.mTasks.size() <= 6) {
                    return;
                }
                MyLog.v("remove Expired task");
                this.mTasks.remove(taskPeek);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanUp() {
        try {
            File file = new File(this.mContext.getFilesDir() + ZIPPED_LOG_PATH);
            if (file.exists() && file.isDirectory()) {
                for (File file2 : file.listFiles()) {
                    file2.delete();
                }
            }
        } catch (NullPointerException unused) {
        }
    }

    private void executeTask(long j2) {
        if (this.mTasks.isEmpty()) {
            return;
        }
        TaskExecutor.execute(new SerializedAsyncTaskProcessor.SerializedAsyncTask() { // from class: com.xiaomi.push.log.LogUploader.2
            SerializedAsyncTaskProcessor.SerializedAsyncTask current;

            @Override // com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
            public void postProcess() {
                SerializedAsyncTaskProcessor.SerializedAsyncTask serializedAsyncTask = this.current;
                if (serializedAsyncTask != null) {
                    serializedAsyncTask.postProcess();
                }
            }

            @Override // com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
            public void process() {
                Task task = (Task) LogUploader.this.mTasks.peek();
                if (task == null || !task.canExcuteNow()) {
                    return;
                }
                if (LogUploader.this.mTasks.remove(task)) {
                    this.current = task;
                }
                SerializedAsyncTaskProcessor.SerializedAsyncTask serializedAsyncTask = this.current;
                if (serializedAsyncTask != null) {
                    serializedAsyncTask.process();
                }
            }
        }, j2);
    }

    public static LogUploader getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LogUploader.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new LogUploader(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        sInstance.mContext = context;
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void uploadIfNeed(long j2) {
        Task taskPeek = this.mTasks.peek();
        if (taskPeek == null || !taskPeek.canExcuteNow()) {
            return;
        }
        executeTask(j2);
    }

    public void checkUpload() {
        cleanExpiredTask();
        uploadIfNeed(0L);
    }

    public void upload(final String str, final String str2, final Date date, final Date date2, final int i2, final boolean z2) {
        this.mTasks.add(new Task() { // from class: com.xiaomi.push.log.LogUploader.1
            File file;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            @Override // com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
            public void postProcess() {
                File file = this.file;
                if (file != null && file.exists()) {
                    LogUploader.this.mTasks.add(LogUploader.this.new UploadTask(str, str2, this.file, z2));
                }
                LogUploader.this.uploadIfNeed(0L);
            }

            @Override // com.xiaomi.push.log.LogUploader.Task, com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
            public void process() {
                try {
                    File file = new File(LogUploader.this.mContext.getFilesDir() + LogUploader.ZIPPED_LOG_PATH);
                    if (FileUtils.canWriteInternalFileDir(file)) {
                        file.mkdirs();
                        if (file.isDirectory()) {
                            LogFilter logFilter = new LogFilter();
                            logFilter.setMaxLen(i2);
                            this.file = logFilter.filter(LogUploader.this.mContext, date, date2, file);
                        }
                    }
                } catch (NullPointerException unused) {
                }
            }
        });
        executeTask(0L);
    }

    class Task extends SerializedAsyncTaskProcessor.SerializedAsyncTask {
        long timestamp = System.currentTimeMillis();

        Task() {
        }

        public boolean canExcuteNow() {
            return true;
        }

        final boolean isExpired() {
            return System.currentTimeMillis() - this.timestamp > 172800000;
        }

        @Override // com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
        public void process() {
        }
    }
}
