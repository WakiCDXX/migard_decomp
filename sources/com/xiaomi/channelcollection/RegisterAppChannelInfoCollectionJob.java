package com.xiaomi.channelcollection;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.AppPermissions;
import com.xiaomi.push.service.ChannelPermissions;
import com.xiaomi.push.service.MIPushAccountUtils;
import com.xiaomi.push.service.NotificationManagerHelper;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.PushServiceConstants;
import com.xiaomi.push.service.TinyDataHelper;
import com.xiaomi.push.service.TinyDataStorage;
import com.xiaomi.push.service.clientReport.ReportConstants;
import com.xiaomi.push.stat.Reporter;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes.dex */
public class RegisterAppChannelInfoCollectionJob implements Runnable {
    private static final int COLLECTION_TIME_RANGE = 46800000;
    public static final boolean DEBUG = false;
    public static final boolean DEBUG_LOG = false;
    public static final String JSON_KEY_APP_COUNT = "c";
    public static final String JSON_KEY_APP_ID = "a";
    public static final String JSON_KEY_CHANNEL_ID = "c";
    public static final String JSON_KEY_CHANNEL_LIST = "c";
    public static final String JSON_KEY_CHANNEL_TYPE = "t";
    public static final String JSON_KEY_SWITCH = "s";
    public static final String JSON_KEY_UPLOAD_DATA = "d";
    public static final String KEY_JOB_RESULT_TIME_PREFIX = "dc_job_result_time_";
    private static final int MAX_UPLOAD_DAY = 3;
    public static final long MAX_UPLOAD_INTERVAL = 259200000;
    public static final String MIPUSH_CHANNEL_NEW_CONCAT = "|";
    public static final String MIPUSH_CHANNEL_NEW_PREFIX = "mipush|";
    public static final String MIPUSH_CHANNEL_OLD_CONCAT = "_";
    public static final String MIPUSH_CHANNEL_OLD_PREFIX = "mipush_";
    public static final int MIPUSH_CHANNEL_TYPE_NEW = 2;
    public static final int MIPUSH_CHANNEL_TYPE_OLD = 1;
    public static final String PREF_EXTRA = "mipush_extra";
    public static final long SAFE_CALL_TIME_MS = 50;
    public static final long SAFE_CALL_WINDOW_TIME_MS = 1000;
    public static final String TAG = "UPLOAD_APP_CHANNEL_SWITCH";
    public static SharedPreferences mExtraSp;

    private static String convertDateTime(long j2) {
        return String.valueOf(j2);
    }

    public static String getJobResultTimeKey() {
        return "dc_job_result_time_25";
    }

    private static void log(String str) {
        MyLog.w(TAG, str);
    }

    private void saveToTinyData(Context context, JSONObjectBytesCount jSONObjectBytesCount, CollectionChannelTracker collectionChannelTracker) {
        ClientUploadDataItem clientUploadDataItem = new ClientUploadDataItem();
        clientUploadDataItem.setCategory(PushConstants.DOT_APP_CHANNEL_INFO);
        clientUploadDataItem.setName(PushConstants.APP_CHANNEL_INFO);
        clientUploadDataItem.setData(jSONObjectBytesCount.toString());
        clientUploadDataItem.setFromSdk(false);
        clientUploadDataItem.setCounter(1L);
        clientUploadDataItem.setChannel(TinyDataHelper.XMSF_CHANNEL);
        clientUploadDataItem.setTimestamp(System.currentTimeMillis());
        clientUploadDataItem.setPkgName(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        clientUploadDataItem.setSourcePackage(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        clientUploadDataItem.setId(TinyDataHelper.nextTinyDataItemId());
        TinyDataStorage.cacheTinyData(context, clientUploadDataItem);
    }

    private void trackCollection(CollectionChannelTracker collectionChannelTracker, CallController callController, Exception exc) {
        HashMap map = new HashMap();
        String uuid = MIPushAccountUtils.getUUID(SystemUtils.getContext());
        if (!TextUtils.isEmpty(uuid)) {
            map.put("uuid", uuid);
        }
        map.put(ReportConstants.PARAM_KEY_APP_COUNT, Long.valueOf(collectionChannelTracker.getTotalApp()));
        map.put(ReportConstants.PARAM_KEY_CHANNELS, Long.valueOf(collectionChannelTracker.getChannelCount()));
        map.put(ReportConstants.PARAM_KEY_PACK_COUNT, Long.valueOf(collectionChannelTracker.getPackCount()));
        map.put(ReportConstants.PARAM_KEY_TOTAL_SIZE, Long.valueOf(collectionChannelTracker.getTotalSize()));
        map.put(ReportConstants.PARAM_KEY_IS_BATCH, Integer.valueOf(collectionChannelTracker.isBatch()));
        map.put(ReportConstants.PARAM_KEY_MAX_CALL_TIME, Long.valueOf(callController.getMaxCallTimeNs()));
        map.put(ReportConstants.PARAM_KEY_MIN_CALL_TIME, Long.valueOf(callController.getMinCallTimeNs()));
        map.put(ReportConstants.PARAM_KEY_CALL_AVG, Long.valueOf(callController.getAvgTime()));
        map.put("duration", Long.valueOf(callController.getTotalDuration()));
        if (exc != null) {
            map.put("exception", exc.toString());
        }
        Reporter.getInstance().trackEvent(ReportConstants.EVENT_APP_SWITCH_UPLOAD, map);
    }

    public static void uploadAppChannelInfo(Context context) {
        if (context == null || !context.getPackageName().equals(PushConstants.PUSH_SERVICE_PACKAGE_NAME)) {
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = jCurrentTimeMillis % 86400000;
        if (mExtraSp == null) {
            mExtraSp = context.getSharedPreferences("mipush_extra", 0);
        }
        String jobResultTimeKey = getJobResultTimeKey();
        long j3 = mExtraSp.getLong(jobResultTimeKey, 0L);
        if (j3 <= 0) {
            Random random = new Random(jCurrentTimeMillis);
            mExtraSp.edit().putLong(jobResultTimeKey, jCurrentTimeMillis + ((long) random.nextInt(COLLECTION_TIME_RANGE)) + (86400000 - j2) + ((long) (random.nextInt(3) * 86400000))).apply();
            return;
        }
        long j4 = jCurrentTimeMillis - j3;
        if (j4 >= 0) {
            mExtraSp.edit().putLong(jobResultTimeKey, j3 + (((j4 / 259200000) + 1) * 259200000)).apply();
            new Thread(new RegisterAppChannelInfoCollectionJob()).start();
        } else if (Math.abs(j4) > 259200000) {
            mExtraSp.edit().putLong(jobResultTimeKey, jCurrentTimeMillis + ((long) new Random(jCurrentTimeMillis).nextInt(COLLECTION_TIME_RANGE)) + (86400000 - j2)).apply();
        }
    }

    /* JADX WARN: Code duplicated, block: B:48:0x018d  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v10, types: [com.xiaomi.channelcollection.JSONObjectBytesCount] */
    /* JADX WARN: Type inference failed for: r10v11 */
    /* JADX WARN: Type inference failed for: r10v12 */
    /* JADX WARN: Type inference failed for: r10v15 */
    /* JADX WARN: Type inference failed for: r10v16 */
    /* JADX WARN: Type inference failed for: r10v17 */
    /* JADX WARN: Type inference failed for: r10v18 */
    /* JADX WARN: Type inference failed for: r10v7, types: [com.xiaomi.channelcollection.JSONObjectBytesCount] */
    /* JADX WARN: Type inference failed for: r10v8, types: [com.xiaomi.channelcollection.JSONObjectBytesCount] */
    /* JADX WARN: Type inference failed for: r11v10 */
    /* JADX WARN: Type inference failed for: r11v13 */
    /* JADX WARN: Type inference failed for: r11v14 */
    /* JADX WARN: Type inference failed for: r11v16 */
    /* JADX WARN: Type inference failed for: r11v17 */
    /* JADX WARN: Type inference failed for: r11v18 */
    /* JADX WARN: Type inference failed for: r11v19 */
    /* JADX WARN: Type inference failed for: r11v20 */
    /* JADX WARN: Type inference failed for: r11v21 */
    /* JADX WARN: Type inference failed for: r11v22 */
    /* JADX WARN: Type inference failed for: r11v4, types: [org.json.JSONArray] */
    /* JADX WARN: Type inference failed for: r11v5, types: [com.xiaomi.channelcollection.JSONArrayBytesCount, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r11v6 */
    /* JADX WARN: Type inference failed for: r11v7 */
    /* JADX WARN: Type inference failed for: r15v3, types: [com.xiaomi.channelcollection.JSONObjectBytesCount, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r19v0 */
    /* JADX WARN: Type inference failed for: r19v2 */
    /* JADX WARN: Type inference failed for: r19v3 */
    /* JADX WARN: Type inference failed for: r19v4 */
    /* JADX WARN: Type inference failed for: r23v0, types: [com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob] */
    @Override // java.lang.Runnable
    public void run() {
        CollectionChannelTracker collectionChannelTracker;
        CollectionChannelTracker collectionChannelTracker2;
        String str;
        CollectionChannelTracker collectionChannelTracker3;
        ?? r11;
        String str2;
        ?? r19;
        ?? jSONArrayBytesCount;
        ?? r10;
        String str3 = MIPUSH_CHANNEL_OLD_PREFIX;
        String str4 = PushConstants.PUSH_SERVICE_PACKAGE_NAME;
        final Context context = SystemUtils.getContext();
        if (context != null) {
            CollectionChannelTracker collectionChannelTracker4 = new CollectionChannelTracker();
            CallController callController = new CallController(50L, 1000L);
            try {
                Map<String, ?> all = SystemUtils.getContext().getSharedPreferences(PushServiceConstants.PREF_KEY_REGISTERED_PKGS, 0).getAll();
                if (all == null || all.isEmpty()) {
                    collectionChannelTracker = collectionChannelTracker4;
                } else {
                    Set<String> setKeySet = all.keySet();
                    collectionChannelTracker4.setTotalApp(setKeySet.contains(PushConstants.PUSH_SERVICE_PACKAGE_NAME) ? setKeySet.size() - 1 : setKeySet.size());
                    JSONObjectBytesCount jSONObjectBytesCount = new JSONObjectBytesCount();
                    jSONObjectBytesCount.put("c", collectionChannelTracker4.getTotalApp());
                    Set<Map.Entry<String, ?>> setEntrySet = all.entrySet();
                    JSONArrayBytesCount jSONArrayBytesCount2 = new JSONArrayBytesCount();
                    Iterator<Map.Entry<String, ?>> it = setEntrySet.iterator();
                    ?? r12 = jSONObjectBytesCount;
                    ?? r13 = jSONArrayBytesCount2;
                    while (it.hasNext()) {
                        Map.Entry<String, ?> next = it.next();
                        final String key = next.getKey();
                        String str5 = (String) next.getValue();
                        if (TextUtils.isEmpty(key)) {
                            str = str3;
                            str4 = str4;
                            collectionChannelTracker3 = collectionChannelTracker4;
                            it = it;
                            r10 = r12;
                            jSONArrayBytesCount = r13;
                        } else {
                            try {
                                if (str4.equals(key) || TextUtils.isEmpty(str5)) {
                                    str = str3;
                                    str4 = str4;
                                    collectionChannelTracker3 = collectionChannelTracker4;
                                    it = it;
                                    r10 = r12;
                                    jSONArrayBytesCount = r13;
                                } else {
                                    ?? jSONObjectBytesCount2 = new JSONObjectBytesCount();
                                    jSONObjectBytesCount2.put(JSON_KEY_APP_ID, str5);
                                    jSONObjectBytesCount2.put(JSON_KEY_SWITCH, (String) callController.call(new Callable<String>() { // from class: com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob.1
                                        @Override // java.util.concurrent.Callable
                                        public String call() {
                                            return String.valueOf(AppPermissions.getAppNotificationStatus(context, key));
                                        }
                                    }));
                                    List<NotificationChannel> notificationChannels = NotificationManagerHelper.from(context, key).getNotificationChannels();
                                    if (notificationChannels == null || notificationChannels.isEmpty()) {
                                        str = str3;
                                        collectionChannelTracker3 = collectionChannelTracker4;
                                        r11 = r13;
                                    } else {
                                        JSONArrayBytesCount jSONArrayBytesCount3 = new JSONArrayBytesCount();
                                        collectionChannelTracker4.addChannelCount(notificationChannels.size());
                                        Iterator<NotificationChannel> it2 = notificationChannels.iterator();
                                        ?? r14 = r13;
                                        while (it2.hasNext()) {
                                            final NotificationChannel next2 = it2.next();
                                            String id = next2.getId();
                                            JSONObjectBytesCount jSONObjectBytesCount3 = new JSONObjectBytesCount();
                                            Iterator<NotificationChannel> it3 = it2;
                                            collectionChannelTracker2 = collectionChannelTracker4;
                                            if (id.startsWith(str3)) {
                                                r19 = r14;
                                                try {
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append(str3);
                                                    sb.append(key);
                                                    str2 = str3;
                                                    sb.append(MIPUSH_CHANNEL_OLD_CONCAT);
                                                    String strReplace = id.replace(sb.toString(), "");
                                                    jSONObjectBytesCount3.put(JSON_KEY_CHANNEL_TYPE, 1);
                                                    jSONObjectBytesCount3.put("c", strReplace);
                                                } catch (Exception e2) {
                                                    e = e2;
                                                    collectionChannelTracker = collectionChannelTracker2;
                                                    trackCollection(collectionChannelTracker, callController, e);
                                                }
                                            } else {
                                                str2 = str3;
                                                r19 = r14;
                                                if (id.startsWith(MIPUSH_CHANNEL_NEW_PREFIX)) {
                                                    String strReplace2 = id.replace(MIPUSH_CHANNEL_NEW_PREFIX + key + "|", "");
                                                    jSONObjectBytesCount3.put(JSON_KEY_CHANNEL_TYPE, 2);
                                                    jSONObjectBytesCount3.put("c", strReplace2);
                                                }
                                            }
                                            jSONObjectBytesCount3.put(JSON_KEY_SWITCH, (String) callController.call(new Callable() { // from class: com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob.2
                                                @Override // java.util.concurrent.Callable
                                                public String call() throws Exception {
                                                    return String.valueOf(ChannelPermissions.getChannelAllStatus(context, key, next2));
                                                }
                                            }));
                                            jSONArrayBytesCount3.put(jSONObjectBytesCount3);
                                            r14 = r19;
                                            it2 = it3;
                                            collectionChannelTracker4 = collectionChannelTracker2;
                                            str3 = str2;
                                        }
                                        str = str3;
                                        collectionChannelTracker3 = collectionChannelTracker4;
                                        jSONObjectBytesCount2.put("c", jSONArrayBytesCount3);
                                        r11 = r14;
                                    }
                                    r11.put(jSONObjectBytesCount2);
                                    ?? r15 = r12;
                                    r15.put("d", r11);
                                    r10 = r15;
                                    jSONArrayBytesCount = r11;
                                }
                            } catch (Exception e3) {
                                e = e3;
                                collectionChannelTracker2 = collectionChannelTracker4;
                            }
                        }
                        if (r10.getBytesCount() > 30720) {
                            collectionChannelTracker3.incrementPackCount();
                            collectionChannelTracker = collectionChannelTracker3;
                            try {
                                collectionChannelTracker.addTotalSize(r10.getBytesCount());
                                saveToTinyData(context, r10, collectionChannelTracker);
                                JSONObjectBytesCount jSONObjectBytesCount4 = new JSONObjectBytesCount();
                                jSONObjectBytesCount4.put("c", collectionChannelTracker.getTotalApp());
                                r10 = jSONObjectBytesCount4;
                                jSONArrayBytesCount = new JSONArrayBytesCount();
                            } catch (Exception e4) {
                                e = e4;
                            }
                        } else {
                            collectionChannelTracker = collectionChannelTracker3;
                        }
                        collectionChannelTracker4 = collectionChannelTracker;
                        str4 = str4;
                        it = it;
                        str3 = str;
                        r12 = r10;
                        r13 = jSONArrayBytesCount;
                    }
                    collectionChannelTracker = collectionChannelTracker4;
                    if (r13.length() > 0) {
                        collectionChannelTracker.incrementPackCount();
                        collectionChannelTracker.addTotalSize(r12.getBytesCount());
                        saveToTinyData(context, r12, collectionChannelTracker);
                    }
                }
                e = null;
            } catch (Exception e5) {
                e = e5;
                collectionChannelTracker = collectionChannelTracker4;
            }
            trackCollection(collectionChannelTracker, callController, e);
        }
    }
}
