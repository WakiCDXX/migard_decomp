package com.xiaomi.push.service;

import android.os.SystemClock;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoCollector;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.util.concurrent.RejectedExecutionException;

/* JADX INFO: loaded from: classes.dex */
public class JobScheduler {
    private static long currentTime;
    private static long lastTime;
    private static long timerId;
    private final FinalizerHelper finalizer;
    private final SchedulerImpl impl;

    private static final class FinalizerHelper {
        private final SchedulerImpl impl;

        FinalizerHelper(SchedulerImpl schedulerImpl) {
            this.impl = schedulerImpl;
        }

        protected void finalize() throws Throwable {
            try {
                synchronized (this.impl) {
                    this.impl.finished = true;
                    this.impl.notify();
                }
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
                throw th;
            }
        }
    }

    public static abstract class Job implements Runnable {
        protected int type;

        public Job(int i2) {
            this.type = i2;
        }
    }

    private static final class SchedulerImpl extends Thread {
        private static final int MAX_SLEEP_TIME = 500;
        private static final int SLEEP_TIME_STEP = 50;
        private boolean cancelled;
        private boolean finished;
        private volatile long lastJob = 0;
        private volatile boolean executing = false;
        private long current_sleep_duration = 50;
        private TimerHeap tasks = new TimerHeap();

        SchedulerImpl(String str, boolean z2) {
            setName(str);
            setDaemon(z2);
            start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void insertTask(TaskWraper taskWraper) {
            this.tasks.insert(taskWraper);
            notify();
        }

        public synchronized void cancel() {
            this.cancelled = true;
            this.tasks.reset();
            notify();
        }

        public boolean isBlocked() {
            return this.executing && SystemClock.uptimeMillis() - this.lastJob > ThermalInfoCollector.DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL;
        }

        public int purge() {
            if (this.tasks.isEmpty()) {
                return 0;
            }
            this.tasks.deletedCancelledNumber = 0;
            this.tasks.deleteIfCancelled();
            return this.tasks.deletedCancelledNumber;
        }

        /* JADX WARN: Code restructure failed: missing block: B:59:0x009a, code lost:
        
            r10.lastJob = android.os.SystemClock.uptimeMillis();
            r10.executing = true;
            r2.job.run();
            r10.executing = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:61:0x00ab, code lost:
        
            r1 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:62:0x00ac, code lost:
        
            monitor-enter(r10);
         */
        /* JADX WARN: Code restructure failed: missing block: B:63:0x00ad, code lost:
        
            r10.cancelled = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:65:0x00b0, code lost:
        
            throw r1;
         */
        @Override // java.lang.Thread, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                r10 = this;
            L0:
                monitor-enter(r10)
                boolean r0 = r10.cancelled     // Catch: java.lang.Throwable -> L7
                if (r0 == 0) goto La
                monitor-exit(r10)     // Catch: java.lang.Throwable -> L7
                return
            L7:
                r0 = move-exception
                goto Lb8
            La:
                com.xiaomi.push.service.JobScheduler$SchedulerImpl$TimerHeap r0 = r10.tasks     // Catch: java.lang.Throwable -> L7
                boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> L7
                if (r0 == 0) goto L1d
                boolean r0 = r10.finished     // Catch: java.lang.Throwable -> L7
                if (r0 == 0) goto L18
                monitor-exit(r10)     // Catch: java.lang.Throwable -> L7
                return
            L18:
                r10.wait()     // Catch: java.lang.Throwable -> L7 java.lang.InterruptedException -> L1b
            L1b:
                monitor-exit(r10)     // Catch: java.lang.Throwable -> L7
                goto L0
            L1d:
                long r0 = com.xiaomi.push.service.JobScheduler.getCurrentTime()     // Catch: java.lang.Throwable -> L7
                com.xiaomi.push.service.JobScheduler$SchedulerImpl$TimerHeap r2 = r10.tasks     // Catch: java.lang.Throwable -> L7
                com.xiaomi.push.service.JobScheduler$TaskWraper r2 = r2.minimum()     // Catch: java.lang.Throwable -> L7
                java.lang.Object r3 = r2.lock     // Catch: java.lang.Throwable -> L7
                monitor-enter(r3)     // Catch: java.lang.Throwable -> L7
                boolean r4 = r2.cancelled     // Catch: java.lang.Throwable -> L37
                r5 = 0
                if (r4 == 0) goto L3a
                com.xiaomi.push.service.JobScheduler$SchedulerImpl$TimerHeap r0 = r10.tasks     // Catch: java.lang.Throwable -> L37
                r0.delete(r5)     // Catch: java.lang.Throwable -> L37
                monitor-exit(r3)     // Catch: java.lang.Throwable -> L37
                monitor-exit(r10)     // Catch: java.lang.Throwable -> L7
                goto L0
            L37:
                r0 = move-exception
                goto Lb6
            L3a:
                long r6 = r2.when     // Catch: java.lang.Throwable -> L37
                long r6 = r6 - r0
                monitor-exit(r3)     // Catch: java.lang.Throwable -> L37
                r0 = 0
                int r3 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
                r8 = 50
                if (r3 <= 0) goto L5d
                long r0 = r10.current_sleep_duration     // Catch: java.lang.Throwable -> L7
                int r2 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
                if (r2 <= 0) goto L4d
                r6 = r0
            L4d:
                long r0 = r0 + r8
                r10.current_sleep_duration = r0     // Catch: java.lang.Throwable -> L7
                r2 = 500(0x1f4, double:2.47E-321)
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 <= 0) goto L58
                r10.current_sleep_duration = r2     // Catch: java.lang.Throwable -> L7
            L58:
                r10.wait(r6)     // Catch: java.lang.Throwable -> L7 java.lang.InterruptedException -> L5b
            L5b:
                monitor-exit(r10)     // Catch: java.lang.Throwable -> L7
                goto L0
            L5d:
                r10.current_sleep_duration = r8     // Catch: java.lang.Throwable -> L7
                java.lang.Object r3 = r2.lock     // Catch: java.lang.Throwable -> L7
                monitor-enter(r3)     // Catch: java.lang.Throwable -> L7
                com.xiaomi.push.service.JobScheduler$SchedulerImpl$TimerHeap r4 = r10.tasks     // Catch: java.lang.Throwable -> L77
                com.xiaomi.push.service.JobScheduler$TaskWraper r4 = r4.minimum()     // Catch: java.lang.Throwable -> L77
                long r6 = r4.when     // Catch: java.lang.Throwable -> L77
                long r8 = r2.when     // Catch: java.lang.Throwable -> L77
                int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r4 == 0) goto L79
                com.xiaomi.push.service.JobScheduler$SchedulerImpl$TimerHeap r4 = r10.tasks     // Catch: java.lang.Throwable -> L77
                int r4 = com.xiaomi.push.service.JobScheduler.SchedulerImpl.TimerHeap.access$100(r4, r2)     // Catch: java.lang.Throwable -> L77
                goto L7a
            L77:
                r0 = move-exception
                goto Lb4
            L79:
                r4 = r5
            L7a:
                boolean r6 = r2.cancelled     // Catch: java.lang.Throwable -> L77
                if (r6 == 0) goto L8b
                com.xiaomi.push.service.JobScheduler$SchedulerImpl$TimerHeap r0 = r10.tasks     // Catch: java.lang.Throwable -> L77
                int r1 = com.xiaomi.push.service.JobScheduler.SchedulerImpl.TimerHeap.access$100(r0, r2)     // Catch: java.lang.Throwable -> L77
                r0.delete(r1)     // Catch: java.lang.Throwable -> L77
                monitor-exit(r3)     // Catch: java.lang.Throwable -> L77
                monitor-exit(r10)     // Catch: java.lang.Throwable -> L7
                goto L0
            L8b:
                long r6 = r2.when     // Catch: java.lang.Throwable -> L77
                r2.setScheduledTime(r6)     // Catch: java.lang.Throwable -> L77
                com.xiaomi.push.service.JobScheduler$SchedulerImpl$TimerHeap r6 = r10.tasks     // Catch: java.lang.Throwable -> L77
                r6.delete(r4)     // Catch: java.lang.Throwable -> L77
                r2.when = r0     // Catch: java.lang.Throwable -> L77
                monitor-exit(r3)     // Catch: java.lang.Throwable -> L77
                monitor-exit(r10)     // Catch: java.lang.Throwable -> L7
                r0 = 1
                long r3 = android.os.SystemClock.uptimeMillis()     // Catch: java.lang.Throwable -> Lab
                r10.lastJob = r3     // Catch: java.lang.Throwable -> Lab
                r10.executing = r0     // Catch: java.lang.Throwable -> Lab
                com.xiaomi.push.service.JobScheduler$Job r1 = r2.job     // Catch: java.lang.Throwable -> Lab
                r1.run()     // Catch: java.lang.Throwable -> Lab
                r10.executing = r5     // Catch: java.lang.Throwable -> Lab
                goto L0
            Lab:
                r1 = move-exception
                monitor-enter(r10)
                r10.cancelled = r0     // Catch: java.lang.Throwable -> Lb1
                monitor-exit(r10)     // Catch: java.lang.Throwable -> Lb1
                throw r1
            Lb1:
                r0 = move-exception
                monitor-exit(r10)     // Catch: java.lang.Throwable -> Lb1
                throw r0
            Lb4:
                monitor-exit(r3)     // Catch: java.lang.Throwable -> L77
                throw r0     // Catch: java.lang.Throwable -> L7
            Lb6:
                monitor-exit(r3)     // Catch: java.lang.Throwable -> L37
                throw r0     // Catch: java.lang.Throwable -> L7
            Lb8:
                monitor-exit(r10)     // Catch: java.lang.Throwable -> L7
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.push.service.JobScheduler.SchedulerImpl.run():void");
        }

        private static final class TimerHeap {
            private int DEFAULT_HEAP_SIZE;
            private int deletedCancelledNumber;
            private int size;
            private TaskWraper[] timers;

            private TimerHeap() {
                this.DEFAULT_HEAP_SIZE = 256;
                this.timers = new TaskWraper[256];
                this.size = 0;
                this.deletedCancelledNumber = 0;
            }

            private void downHeap(int i2) {
                int i3 = (i2 * 2) + 1;
                while (true) {
                    int i4 = this.size;
                    if (i3 >= i4 || i4 <= 0) {
                        return;
                    }
                    int i5 = i3 + 1;
                    if (i5 < i4) {
                        TaskWraper[] taskWraperArr = this.timers;
                        if (taskWraperArr[i5].when < taskWraperArr[i3].when) {
                            i3 = i5;
                        }
                    }
                    TaskWraper[] taskWraperArr2 = this.timers;
                    TaskWraper taskWraper = taskWraperArr2[i2];
                    long j2 = taskWraper.when;
                    TaskWraper taskWraper2 = taskWraperArr2[i3];
                    if (j2 < taskWraper2.when) {
                        return;
                    }
                    taskWraperArr2[i2] = taskWraper2;
                    taskWraperArr2[i3] = taskWraper;
                    int i6 = i3;
                    i3 = (i3 * 2) + 1;
                    i2 = i6;
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public int getTask(TaskWraper taskWraper) {
                int i2 = 0;
                while (true) {
                    TaskWraper[] taskWraperArr = this.timers;
                    if (i2 >= taskWraperArr.length) {
                        return -1;
                    }
                    if (taskWraperArr[i2] == taskWraper) {
                        return i2;
                    }
                    i2++;
                }
            }

            private void upHeap() {
                int i2 = this.size;
                int i3 = i2 - 1;
                int i4 = (i2 - 2) / 2;
                while (true) {
                    TaskWraper[] taskWraperArr = this.timers;
                    TaskWraper taskWraper = taskWraperArr[i3];
                    long j2 = taskWraper.when;
                    TaskWraper taskWraper2 = taskWraperArr[i4];
                    if (j2 >= taskWraper2.when) {
                        return;
                    }
                    taskWraperArr[i3] = taskWraper2;
                    taskWraperArr[i4] = taskWraper;
                    i3 = i4;
                    i4 = (i4 - 1) / 2;
                }
            }

            public void adjustMinimum() {
                downHeap(0);
            }

            public void delete(int i2) {
                int i3;
                if (i2 < 0 || i2 >= (i3 = this.size)) {
                    return;
                }
                TaskWraper[] taskWraperArr = this.timers;
                int i4 = i3 - 1;
                this.size = i4;
                taskWraperArr[i2] = taskWraperArr[i4];
                taskWraperArr[i4] = null;
                downHeap(i2);
            }

            public void deleteIfCancelled() {
                int i2 = 0;
                while (i2 < this.size) {
                    if (this.timers[i2].cancelled) {
                        this.deletedCancelledNumber++;
                        delete(i2);
                        i2--;
                    }
                    i2++;
                }
            }

            public boolean hasJob(int i2) {
                for (int i3 = 0; i3 < this.size; i3++) {
                    if (this.timers[i3].type == i2) {
                        return true;
                    }
                }
                return false;
            }

            public void insert(TaskWraper taskWraper) {
                TaskWraper[] taskWraperArr = this.timers;
                int length = taskWraperArr.length;
                int i2 = this.size;
                if (length == i2) {
                    TaskWraper[] taskWraperArr2 = new TaskWraper[i2 * 2];
                    System.arraycopy(taskWraperArr, 0, taskWraperArr2, 0, i2);
                    this.timers = taskWraperArr2;
                }
                TaskWraper[] taskWraperArr3 = this.timers;
                int i3 = this.size;
                this.size = i3 + 1;
                taskWraperArr3[i3] = taskWraper;
                upHeap();
            }

            public boolean isEmpty() {
                return this.size == 0;
            }

            public TaskWraper minimum() {
                return this.timers[0];
            }

            public void removeJobs(int i2) {
                for (int i3 = 0; i3 < this.size; i3++) {
                    TaskWraper taskWraper = this.timers[i3];
                    if (taskWraper.type == i2) {
                        taskWraper.cancel();
                    }
                }
                deleteIfCancelled();
            }

            public void reset() {
                this.timers = new TaskWraper[this.DEFAULT_HEAP_SIZE];
                this.size = 0;
            }

            public boolean hasJob(int i2, Job job) {
                for (int i3 = 0; i3 < this.size; i3++) {
                    if (this.timers[i3].job == job) {
                        return true;
                    }
                }
                return false;
            }

            public void removeJobs(int i2, Job job) {
                for (int i3 = 0; i3 < this.size; i3++) {
                    TaskWraper taskWraper = this.timers[i3];
                    if (taskWraper.job == job) {
                        taskWraper.cancel();
                    }
                }
                deleteIfCancelled();
            }
        }
    }

    static class TaskWraper {
        boolean cancelled;
        Job job;
        final Object lock = new Object();
        private long scheduledTime;
        int type;
        long when;

        TaskWraper() {
        }

        public boolean cancel() {
            boolean z2;
            synchronized (this.lock) {
                try {
                    z2 = !this.cancelled && this.when > 0;
                    this.cancelled = true;
                } catch (Throwable th) {
                    throw th;
                }
            }
            return z2;
        }

        long getWhen() {
            long j2;
            synchronized (this.lock) {
                j2 = this.when;
            }
            return j2;
        }

        public long scheduledExecutionTime() {
            long j2;
            synchronized (this.lock) {
                j2 = this.scheduledTime;
            }
            return j2;
        }

        void setScheduledTime(long j2) {
            synchronized (this.lock) {
                this.scheduledTime = j2;
            }
        }
    }

    static {
        long jElapsedRealtime = SystemClock.elapsedRealtime() > 0 ? SystemClock.elapsedRealtime() : 0L;
        currentTime = jElapsedRealtime;
        lastTime = jElapsedRealtime;
    }

    public JobScheduler(String str, boolean z2) {
        if (str == null) {
            throw new NullPointerException("name == null");
        }
        SchedulerImpl schedulerImpl = new SchedulerImpl(str, z2);
        this.impl = schedulerImpl;
        this.finalizer = new FinalizerHelper(schedulerImpl);
    }

    static synchronized long getCurrentTime() {
        try {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            long j2 = lastTime;
            if (jElapsedRealtime > j2) {
                currentTime += jElapsedRealtime - j2;
            }
            lastTime = jElapsedRealtime;
        } catch (Throwable th) {
            throw th;
        }
        return currentTime;
    }

    private static synchronized long nextId() {
        long j2;
        j2 = timerId;
        timerId = 1 + j2;
        return j2;
    }

    private void scheduleImpl(Job job, long j2) {
        synchronized (this.impl) {
            try {
                if (this.impl.cancelled) {
                    throw new IllegalStateException("Timer was canceled");
                }
                long currentTime2 = j2 + getCurrentTime();
                if (currentTime2 < 0) {
                    throw new IllegalArgumentException("Illegal delay to start the TimerTask: " + currentTime2);
                }
                TaskWraper taskWraper = new TaskWraper();
                taskWraper.type = job.type;
                taskWraper.job = job;
                taskWraper.when = currentTime2;
                this.impl.insertTask(taskWraper);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void executeJobDelayed(Job job, long j2) {
        if (j2 >= 0) {
            scheduleImpl(job, j2);
            return;
        }
        throw new IllegalArgumentException("delay < 0: " + j2);
    }

    public void executeJobNow(Job job) {
        if (MyLog.getLogLevel() >= 1 || Thread.currentThread() == this.impl) {
            job.run();
        } else {
            MyLog.e("run job outside job job thread");
            throw new RejectedExecutionException("Run job outside job thread");
        }
    }

    public boolean hasJob(int i2) {
        boolean zHasJob;
        synchronized (this.impl) {
            zHasJob = this.impl.tasks.hasJob(i2);
        }
        return zHasJob;
    }

    public boolean isBlocked() {
        return this.impl.isBlocked();
    }

    public int purge() {
        int iPurge;
        synchronized (this.impl) {
            iPurge = this.impl.purge();
        }
        return iPurge;
    }

    public void quit() {
        MyLog.w("quit. finalizer:" + this.finalizer);
        this.impl.cancel();
    }

    public void removeAllJobs() {
        synchronized (this.impl) {
            this.impl.tasks.reset();
        }
    }

    public void removeJobs(int i2) {
        synchronized (this.impl) {
            this.impl.tasks.removeJobs(i2);
        }
    }

    public boolean hasJob(int i2, Job job) {
        boolean zHasJob;
        synchronized (this.impl) {
            zHasJob = this.impl.tasks.hasJob(i2, job);
        }
        return zHasJob;
    }

    public void removeJobs(int i2, Job job) {
        synchronized (this.impl) {
            this.impl.tasks.removeJobs(i2, job);
        }
    }

    public JobScheduler(String str) {
        this(str, false);
    }

    public JobScheduler(boolean z2) {
        this("Timer-" + nextId(), z2);
    }

    public JobScheduler() {
        this(false);
    }
}
