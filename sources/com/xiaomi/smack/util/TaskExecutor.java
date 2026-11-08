package com.xiaomi.smack.util;

import com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor;

/* JADX INFO: loaded from: classes.dex */
public class TaskExecutor {
    private static SerializedAsyncTaskProcessor mAsyncProcessor = new SerializedAsyncTaskProcessor(true, 20);

    public static void execute(final Runnable runnable) {
        mAsyncProcessor.addNewTask(new SerializedAsyncTaskProcessor.SerializedAsyncTask() { // from class: com.xiaomi.smack.util.TaskExecutor.1
            @Override // com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
            public void process() {
                runnable.run();
            }
        });
    }

    public static void execute(SerializedAsyncTaskProcessor.SerializedAsyncTask serializedAsyncTask) {
        mAsyncProcessor.addNewTask(serializedAsyncTask);
    }

    public static void execute(SerializedAsyncTaskProcessor.SerializedAsyncTask serializedAsyncTask, long j2) {
        mAsyncProcessor.addNewTaskWithDelayed(serializedAsyncTask, j2);
    }
}
