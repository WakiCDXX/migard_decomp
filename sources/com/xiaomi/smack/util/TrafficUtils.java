package com.xiaomi.smack.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.network.NetworkInfoWrapper;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.providers.TrafficDatabaseHelper;
import com.xiaomi.push.providers.TrafficProvider;
import com.xiaomi.push.service.PushConstants;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TrafficUtils {
    private static final long IDLE_INTERVAL = 30000;
    private static final long TRAFFIC_INTERVAL = 5000;
    private static final long WRITE2DB_INTERVAL = 5000;
    private static SerializedAsyncTaskProcessor mAsyncProcessor = new SerializedAsyncTaskProcessor(true);
    private static volatile int networkType = -1;
    private static long lastRxTs = System.currentTimeMillis();
    private static final Object lock = new Object();
    private static List<TrafficInfo> trafficList = Collections.synchronizedList(new ArrayList());
    private static String imsi = "";
    private static TrafficDatabaseHelper dbHelper = null;

    static class TrafficInfo {
        public long bytes;
        public String imsi;
        public long messageTs;
        public int networkType;
        public String packageName;
        public int rcv;

        public TrafficInfo(String str, long j2, int i2, int i3, String str2, long j3) {
            this.packageName = str;
            this.messageTs = j2;
            this.networkType = i2;
            this.rcv = i3;
            this.imsi = str2;
            this.bytes = j3;
        }

        public boolean canAccumulate(TrafficInfo trafficInfo) {
            return TextUtils.equals(trafficInfo.packageName, this.packageName) && TextUtils.equals(trafficInfo.imsi, this.imsi) && trafficInfo.networkType == this.networkType && trafficInfo.rcv == this.rcv && Math.abs(trafficInfo.messageTs - this.messageTs) <= PluginConstants.TTS_REMOVE_BEAN_TIME;
        }
    }

    public static void distributionTraffic(Context context, String str, long j2, boolean z2, boolean z3, long j3) {
        saveTraffic(context, str, getTraffic(getNetworkType(context), j2, z2, j3, z3), z2, j3);
    }

    private static int getActiveNetworkType(Context context) {
        NetworkInfoWrapper networkInfo = Network.getNetworkInfo();
        if (networkInfo == null) {
            return -1;
        }
        return networkInfo.getType();
    }

    private static synchronized String getIMSI(Context context) {
        if (TextUtils.isEmpty(imsi)) {
            return "";
        }
        return imsi;
    }

    public static int getNetworkType(Context context) {
        if (networkType == -1) {
            networkType = getActiveNetworkType(context);
        }
        return networkType;
    }

    private static long getTraffic(int i2, long j2, boolean z2, long j3, boolean z3) {
        if (z2 && z3) {
            long j4 = lastRxTs;
            lastRxTs = j3;
            if (j3 - j4 > 30000 && j2 > 1024) {
                return j2 * 2;
            }
        }
        return (j2 * ((long) (i2 == 0 ? 13 : 11))) / 10;
    }

    private static TrafficDatabaseHelper getTrafficDatabaseHelper(Context context) {
        TrafficDatabaseHelper trafficDatabaseHelper = dbHelper;
        if (trafficDatabaseHelper != null) {
            return trafficDatabaseHelper;
        }
        TrafficDatabaseHelper trafficDatabaseHelper2 = new TrafficDatabaseHelper(context);
        dbHelper = trafficDatabaseHelper2;
        return trafficDatabaseHelper2;
    }

    public static int getTrafficFlow(String str) {
        try {
            return str.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes().length;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void insertTraffic(Context context, List<TrafficInfo> list) {
        try {
            synchronized (TrafficDatabaseHelper.DataBaseLock) {
                try {
                    SQLiteDatabase writableDatabase = getTrafficDatabaseHelper(context).getWritableDatabase();
                    writableDatabase.beginTransaction();
                    try {
                        for (TrafficInfo trafficInfo : list) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("package_name", trafficInfo.packageName);
                            contentValues.put(TrafficProvider.TrafficColumns.MESSAGE_TS, Long.valueOf(trafficInfo.messageTs));
                            contentValues.put(TrafficProvider.TrafficColumns.NETWORK_TYPE, Integer.valueOf(trafficInfo.networkType));
                            contentValues.put(TrafficProvider.TrafficColumns.BYTES, Long.valueOf(trafficInfo.bytes));
                            contentValues.put(TrafficProvider.TrafficColumns.RCV, Integer.valueOf(trafficInfo.rcv));
                            contentValues.put("imsi", trafficInfo.imsi);
                            writableDatabase.insert("traffic", null, contentValues);
                        }
                        writableDatabase.setTransactionSuccessful();
                        writableDatabase.endTransaction();
                    } catch (Throwable th) {
                        writableDatabase.endTransaction();
                        throw th;
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
        } catch (Throwable th3) {
            MyLog.e(th3);
        }
    }

    private static void insertTrafficInfo2List(TrafficInfo trafficInfo) {
        for (TrafficInfo trafficInfo2 : trafficList) {
            if (trafficInfo2.canAccumulate(trafficInfo)) {
                trafficInfo2.bytes += trafficInfo.bytes;
                return;
            }
        }
        trafficList.add(trafficInfo);
    }

    public static void notifyNetworkChanage(Context context) {
        networkType = getActiveNetworkType(context);
    }

    private static void saveTraffic(final Context context, String str, long j2, boolean z2, long j3) {
        int networkType2;
        boolean zIsEmpty;
        if (context == null || TextUtils.isEmpty(str) || !PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName()) || PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(str) || -1 == (networkType2 = getNetworkType(context))) {
            return;
        }
        synchronized (lock) {
            try {
                zIsEmpty = trafficList.isEmpty();
                insertTrafficInfo2List(new TrafficInfo(str, j3, networkType2, z2 ? 1 : 0, networkType2 == 0 ? getIMSI(context) : "", j2));
            } catch (Throwable th) {
                throw th;
            }
        }
        if (zIsEmpty) {
            mAsyncProcessor.addNewTaskWithDelayed(new SerializedAsyncTaskProcessor.SerializedAsyncTask() { // from class: com.xiaomi.smack.util.TrafficUtils.1
                @Override // com.xiaomi.channel.commonutils.misc.SerializedAsyncTaskProcessor.SerializedAsyncTask
                public void process() {
                    ArrayList arrayList;
                    synchronized (TrafficUtils.lock) {
                        arrayList = new ArrayList(TrafficUtils.trafficList);
                        TrafficUtils.trafficList.clear();
                    }
                    TrafficUtils.insertTraffic(context, arrayList);
                }
            }, PluginConstants.TTS_REMOVE_BEAN_TIME);
        }
    }

    public static synchronized void updateIMSI(String str) {
        if (!MIUIUtils.isGlobalRegion() && !TextUtils.isEmpty(str)) {
            imsi = str;
        }
    }
}
