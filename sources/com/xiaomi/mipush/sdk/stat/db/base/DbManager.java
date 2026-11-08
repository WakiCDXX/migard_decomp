package com.xiaomi.mipush.sdk.stat.db.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.FileLockerWorker;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobConstants;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.string.MD5;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.xmpush.thrift.ConfigKey;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class DbManager {
    private static final int EXEC_DELAY = 5;
    private static volatile DbManager sDbManager;
    private BaseDbHelperFactory mBaseDbHelperFactory;
    private Context mContext;
    private final HashMap<String, BaseDbHelper> mDbHelperMap = new HashMap<>();
    private ThreadPoolExecutor mPool = new ThreadPoolExecutor(1, 1, 15, TimeUnit.SECONDS, new LinkedBlockingQueue());
    private final ArrayList<BaseJob> mPendingList = new ArrayList<>();

    public static abstract class BaseQueryJob<T> extends BaseJob {
        private List<String> mBackRows;
        private String mGroupBy;
        private String mHaving;
        private int mLimit;
        private String mOrderBy;
        private List<T> mResults;
        private String mWhereClause;
        private String[] mWhereValues;

        public BaseQueryJob(String str, List<String> list, String str2, String[] strArr, String str3, String str4, String str5, int i2) {
            super(str);
            this.mResults = new ArrayList();
            this.mBackRows = list;
            this.mWhereClause = str2;
            this.mWhereValues = strArr;
            this.mGroupBy = str3;
            this.mHaving = str4;
            this.mOrderBy = str5;
            this.mLimit = i2;
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public void doRun(Context context, SQLiteDatabase sQLiteDatabase) throws Exception {
            String[] strArr;
            this.mResults.clear();
            List<String> list = this.mBackRows;
            if (list == null || list.size() <= 0) {
                strArr = null;
            } else {
                String[] strArr2 = new String[this.mBackRows.size()];
                this.mBackRows.toArray(strArr2);
                strArr = strArr2;
            }
            int i2 = this.mLimit;
            Cursor cursorQuery = sQLiteDatabase.query(this.mTableName, strArr, this.mWhereClause, this.mWhereValues, this.mGroupBy, this.mHaving, this.mOrderBy, i2 > 0 ? String.valueOf(i2) : null);
            if (cursorQuery != null && cursorQuery.moveToFirst()) {
                do {
                    T tProcessOneData = processOneData(context, cursorQuery);
                    if (tProcessOneData != null) {
                        this.mResults.add(tProcessOneData);
                    }
                } while (cursorQuery.moveToNext());
                cursorQuery.close();
            }
            notifyResult(context, this.mResults);
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public SQLiteDatabase getDatabase() {
            return this.mDbHelper.getReadableDatabase();
        }

        protected int getLimit() {
            return this.mLimit;
        }

        public abstract void notifyResult(Context context, List<T> list);

        public abstract T processOneData(Context context, Cursor cursor);

        protected void setLimit(int i2) {
            this.mLimit = i2;
        }
    }

    public static class BatchJob extends BaseJob {
        private ArrayList<BaseJob> mJobs;

        public BatchJob(String str, ArrayList<BaseJob> arrayList) {
            super(str);
            ArrayList<BaseJob> arrayList2 = new ArrayList<>();
            this.mJobs = arrayList2;
            arrayList2.addAll(arrayList);
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public String description() {
            return "BatchJob";
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public void doRun(Context context, SQLiteDatabase sQLiteDatabase) throws Exception {
            for (BaseJob baseJob : this.mJobs) {
                if (baseJob != null) {
                    baseJob.doRun(context, sQLiteDatabase);
                }
            }
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public final void finish(Context context) {
            super.finish(context);
            for (BaseJob baseJob : this.mJobs) {
                if (baseJob != null) {
                    baseJob.finish(context);
                }
            }
        }
    }

    public static class DeleteJob extends BaseJob {
        private String mWhereClause;
        protected String[] mWhereValues;

        public DeleteJob(String str, String str2, String[] strArr) {
            super(str);
            this.mWhereClause = str2;
            this.mWhereValues = strArr;
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public String description() {
            return "DeleteJob";
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public void doRun(Context context, SQLiteDatabase sQLiteDatabase) throws Exception {
            sQLiteDatabase.delete(this.mTableName, this.mWhereClause, this.mWhereValues);
        }
    }

    public static class InsertJob extends BaseJob {
        private ContentValues mContentValues;

        public InsertJob(String str, ContentValues contentValues) {
            super(str);
            this.mContentValues = contentValues;
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public String description() {
            return "InsertJob";
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public void doRun(Context context, SQLiteDatabase sQLiteDatabase) throws Exception {
            sQLiteDatabase.insert(this.mTableName, null, this.mContentValues);
        }
    }

    public static class UpdateJob extends BaseJob {
        private ContentValues mContentValues;
        private String mWhereClause;
        private String[] mWhereValues;

        public UpdateJob(String str, String str2, String[] strArr, ContentValues contentValues) {
            super(str);
            this.mContentValues = contentValues;
            this.mWhereClause = str2;
            this.mWhereValues = strArr;
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public String description() {
            return "UpdateJob";
        }

        @Override // com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob
        public void doRun(Context context, SQLiteDatabase sQLiteDatabase) throws Exception {
            sQLiteDatabase.update(this.mTableName, this.mContentValues, this.mWhereClause, this.mWhereValues);
        }
    }

    private DbManager(Context context) {
        this.mContext = context;
    }

    private BaseDbHelper getDbHelper(String str) {
        BaseDbHelper dbHelper = this.mDbHelperMap.get(str);
        if (dbHelper == null) {
            synchronized (this.mDbHelperMap) {
                if (dbHelper == null) {
                    try {
                        dbHelper = this.mBaseDbHelperFactory.getDbHelper(this.mContext, str);
                        this.mDbHelperMap.put(str, dbHelper);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }
        return dbHelper;
    }

    public static DbManager getInstance(Context context) {
        if (sDbManager == null) {
            synchronized (DbManager.class) {
                try {
                    if (sDbManager == null) {
                        sDbManager = new DbManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sDbManager;
    }

    private void postDelayed(final Runnable runnable, int i2) {
        ScheduledJobManager.getInstance(this.mContext).addOneShootJob(new Runnable() { // from class: com.xiaomi.mipush.sdk.stat.db.base.DbManager.1
            @Override // java.lang.Runnable
            public void run() {
                if (DbManager.this.mPool.isShutdown()) {
                    return;
                }
                DbManager.this.mPool.execute(runnable);
            }
        }, i2);
    }

    private void sendExecCmd() {
        ScheduledJobManager.getInstance(this.mContext).addOneShootJob(new ScheduledJobManager.Job() { // from class: com.xiaomi.mipush.sdk.stat.db.base.DbManager.2
            @Override // com.xiaomi.channel.commonutils.misc.ScheduledJobManager.Job
            public String getJobId() {
                return ScheduledJobConstants.SEND_EXEC_CMD_JOB_ID;
            }

            @Override // java.lang.Runnable
            public void run() {
                synchronized (DbManager.this.mPendingList) {
                    try {
                        if (DbManager.this.mPendingList.size() > 0) {
                            if (DbManager.this.mPendingList.size() > 1) {
                                DbManager dbManager = DbManager.this;
                                dbManager.exec(dbManager.mPendingList);
                            } else {
                                DbManager dbManager2 = DbManager.this;
                                dbManager2.execNow((BaseJob) dbManager2.mPendingList.get(0));
                            }
                            DbManager.this.mPendingList.clear();
                            System.gc();
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }, OnlineConfig.getInstance(this.mContext).getIntValue(ConfigKey.StatDataProcessFrequency.getValue(), 5));
    }

    public void exec(BaseJob baseJob) {
        BaseDbHelper dbHelper;
        if (baseJob == null) {
            return;
        }
        if (this.mBaseDbHelperFactory == null) {
            throw new IllegalStateException("should exec init method first!");
        }
        String dataPath = baseJob.getDataPath();
        synchronized (this.mDbHelperMap) {
            try {
                dbHelper = this.mDbHelperMap.get(dataPath);
                if (dbHelper == null) {
                    dbHelper = this.mBaseDbHelperFactory.getDbHelper(this.mContext, dataPath);
                    this.mDbHelperMap.put(dataPath, dbHelper);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        if (this.mPool.isShutdown()) {
            return;
        }
        baseJob.attachInfo(dbHelper, this.mContext);
        synchronized (this.mPendingList) {
            this.mPendingList.add(baseJob);
            sendExecCmd();
        }
    }

    public void execDelay(final BaseJob baseJob, int i2) {
        if (i2 == 0) {
            execNow(baseJob);
        } else {
            ScheduledJobManager.getInstance(this.mContext).addOneShootJob(new Runnable() { // from class: com.xiaomi.mipush.sdk.stat.db.base.DbManager.3
                @Override // java.lang.Runnable
                public void run() {
                    DbManager.this.execNow(baseJob);
                }
            }, i2);
        }
    }

    public void execNow(BaseJob baseJob) {
        BaseDbHelper dbHelper;
        if (baseJob == null) {
            return;
        }
        if (this.mBaseDbHelperFactory == null) {
            throw new IllegalStateException("should exec init method first!");
        }
        String dataPath = baseJob.getDataPath();
        synchronized (this.mDbHelperMap) {
            try {
                dbHelper = this.mDbHelperMap.get(dataPath);
                if (dbHelper == null) {
                    dbHelper = this.mBaseDbHelperFactory.getDbHelper(this.mContext, dataPath);
                    this.mDbHelperMap.put(dataPath, dbHelper);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        if (this.mPool.isShutdown()) {
            return;
        }
        baseJob.attachInfo(dbHelper, this.mContext);
        execR(baseJob);
    }

    public void execR(Runnable runnable) {
        if (this.mPool.isShutdown()) {
            return;
        }
        this.mPool.execute(runnable);
    }

    public String getTableName(String str) {
        return getDbHelper(str).getTableName();
    }

    public void init(BaseDbHelperFactory baseDbHelperFactory) {
        this.mBaseDbHelperFactory = baseDbHelperFactory;
    }

    public void exec(ArrayList<BaseJob> arrayList) {
        if (this.mBaseDbHelperFactory != null) {
            HashMap map = new HashMap();
            if (this.mPool.isShutdown()) {
                return;
            }
            for (BaseJob baseJob : arrayList) {
                if (baseJob.needAttachInfo()) {
                    baseJob.attachInfo(getDbHelper(baseJob.getDataPath()), this.mContext);
                }
                ArrayList arrayList2 = (ArrayList) map.get(baseJob.getDataPath());
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    map.put(baseJob.getDataPath(), arrayList2);
                }
                arrayList2.add(baseJob);
            }
            for (String str : map.keySet()) {
                ArrayList arrayList3 = (ArrayList) map.get(str);
                if (arrayList3 != null && arrayList3.size() > 0) {
                    BatchJob batchJob = new BatchJob(str, arrayList3);
                    batchJob.attachInfo(((BaseJob) arrayList3.get(0)).mDbHelper, this.mContext);
                    this.mPool.execute(batchJob);
                }
            }
            return;
        }
        throw new IllegalStateException("should exec setDbHelperFactory method first!");
    }

    public static abstract class BaseJob implements Runnable, ISerialSchedule {
        private String mDataPath;
        private BaseJob mNextJob;
        protected String mTableName;
        private WeakReference<Context> mWRContext;
        protected BaseDbHelper mDbHelper = null;
        private Random mRandom = new Random();
        private int mRetryCount = 0;

        public BaseJob(String str) {
            this.mDataPath = str;
        }

        public void append(BaseJob baseJob) {
            this.mNextJob = baseJob;
        }

        void attachInfo(BaseDbHelper baseDbHelper, Context context) {
            this.mDbHelper = baseDbHelper;
            this.mTableName = baseDbHelper.getTableName();
            this.mWRContext = new WeakReference<>(context);
        }

        public abstract String description();

        public abstract void doRun(Context context, SQLiteDatabase sQLiteDatabase) throws Exception;

        void finish(Context context) {
            BaseJob baseJob = this.mNextJob;
            if (baseJob != null) {
                baseJob.input(context, output());
            }
            onFinish(context);
        }

        public String getDataPath() {
            return this.mDataPath;
        }

        public SQLiteDatabase getDatabase() {
            return this.mDbHelper.getWritableDatabase();
        }

        public void input(Context context, Object obj) {
            DbManager.getInstance(context).exec(this);
        }

        public boolean needAttachInfo() {
            return this.mDbHelper == null || TextUtils.isEmpty(this.mTableName) || this.mWRContext == null;
        }

        public Object output() {
            return null;
        }

        @Override // java.lang.Runnable
        public final void run() {
            final Context context;
            WeakReference<Context> weakReference = this.mWRContext;
            if (weakReference == null || (context = weakReference.get()) == null || context.getFilesDir() == null || this.mDbHelper == null || TextUtils.isEmpty(this.mDataPath)) {
                return;
            }
            File file = new File(this.mDataPath);
            FileLockerWorker.runMutiProcessJob(context, new File(file.getParentFile(), MD5.MD5_16(file.getAbsolutePath())), new Runnable() { // from class: com.xiaomi.mipush.sdk.stat.db.base.DbManager.BaseJob.1
                /* JADX WARN: Code duplicated, block: B:19:0x002f A[Catch: Exception -> 0x0027, DONT_GENERATE, TRY_LEAVE, TryCatch #3 {Exception -> 0x0027, blocks: (B:14:0x0023, B:17:0x0029, B:19:0x002f), top: B:50:0x0023 }] */
                /* JADX WARN: Code duplicated, block: B:30:0x004f A[Catch: Exception -> 0x0047, DONT_GENERATE, TRY_LEAVE, TryCatch #2 {Exception -> 0x0047, blocks: (B:25:0x0043, B:28:0x0049, B:30:0x004f), top: B:48:0x0043 }] */
                /* JADX WARN: Code duplicated, block: B:39:0x0062 A[Catch: Exception -> 0x005a, FINALLY_INSNS, TRY_LEAVE, TryCatch #1 {Exception -> 0x005a, blocks: (B:34:0x0056, B:37:0x005c, B:39:0x0062), top: B:46:0x0056 }] */
                @Override // java.lang.Runnable
                public void run() {
                    BaseDbHelper baseDbHelper;
                    SQLiteDatabase database = null;
                    try {
                        database = BaseJob.this.getDatabase();
                        if (database != null && database.isOpen()) {
                            database.beginTransaction();
                            BaseJob.this.doRun(context, database);
                            database.setTransactionSuccessful();
                        }
                        if (database != null) {
                            try {
                                database.endTransaction();
                                if (BaseJob.this.mDbHelper != null) {
                                }
                            } catch (Exception e2) {
                                e = e2;
                                MyLog.e(e);
                            }
                        } else if (BaseJob.this.mDbHelper != null) {
                        }
                    } catch (Exception e3) {
                        MyLog.e(e3);
                        if (database != null) {
                            try {
                                database.endTransaction();
                                if (BaseJob.this.mDbHelper != null) {
                                }
                            } catch (Exception e4) {
                                e = e4;
                                MyLog.e(e);
                            }
                        } else if (BaseJob.this.mDbHelper != null) {
                        }
                    } finally {
                        if (database != null) {
                            try {
                                database.endTransaction();
                                baseDbHelper = BaseJob.this.mDbHelper;
                                if (baseDbHelper != null) {
                                    baseDbHelper.close();
                                }
                            } catch (Exception e5) {
                                MyLog.e(e5);
                                BaseJob.this.finish(context);
                            }
                        } else {
                            baseDbHelper = BaseJob.this.mDbHelper;
                            if (baseDbHelper != null) {
                                baseDbHelper.close();
                            }
                        }
                        BaseJob.this.finish(context);
                    }
                }
            });
        }

        public void onFinish(Context context) {
        }
    }
}
