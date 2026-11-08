package com.xiaomi.push.sweetnotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.mipush.sdk.Constants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
class SweetMilePostManager {

    private static class BaseManager {
        private String mFileName;
        private Map<String, String> maps = null;

        BaseManager(String str) {
            this.mFileName = str;
        }

        void add(Context context, String str, String str2) {
            if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str)) {
                return;
            }
            getMaps(context).put(str, str2);
            SharedPreferences.Editor editorEdit = getSp(context).edit();
            editorEdit.putString(str, str2);
            editorEdit.apply();
        }

        String get(Context context, String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            return getMaps(context).get(str);
        }

        Map<String, String> getMaps(Context context) {
            if (this.maps == null) {
                try {
                    this.maps = getSp(context).getAll();
                } catch (Exception unused) {
                }
                Map<String, String> map = this.maps;
                if (map == null) {
                    map = new HashMap<>();
                }
                this.maps = map;
            }
            return this.maps;
        }

        SharedPreferences getSp(Context context) {
            return context.getSharedPreferences(this.mFileName, 0);
        }

        void remove(Context context, String str) {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            SharedPreferences.Editor editorEdit = getSp(context).edit();
            editorEdit.remove(str);
            editorEdit.apply();
            getMaps(context).remove(str);
        }
    }

    private static class ClickManager {
        static final String SWEET_CLICKED_STATUS_FILE = "sweet_clicked_status";
        static BaseManager sBaseManager = new BaseManager(SWEET_CLICKED_STATUS_FILE);

        private ClickManager() {
        }

        static void addClickStatus(Context context, String str, String str2) {
            sBaseManager.add(context, str, str2);
        }

        static String readClickStatus(Context context, String str) {
            return sBaseManager.get(context, str);
        }

        static void removeClickStatus(Context context, String str) {
            sBaseManager.remove(context, str);
        }
    }

    private static class SequenceManager {
        static final String SWEET_SEQUENCE_FILE = "sweet_sequence";
        static BaseManager sSequenceCache = new BaseManager(SWEET_SEQUENCE_FILE);

        private SequenceManager() {
        }

        static void addSequence(Context context, String str, long j2) {
            sSequenceCache.add(context, str, String.valueOf(j2));
        }

        static long readSequence(Context context, String str) {
            return ToolUtils.parseLong(sSequenceCache.get(context, str), 0L);
        }

        static void removeSequence(Context context, String str) {
            sSequenceCache.remove(context, str);
        }
    }

    private static class StatusManager {
        static final String SWEET_MILEPOST_STATUS_FILE = "sweet_milepost_status";
        static BaseManager sBaseManager = new BaseManager(SWEET_MILEPOST_STATUS_FILE);

        private StatusManager() {
        }

        static void addStatus(Context context, String str, String str2) {
            sBaseManager.add(context, str, str2);
        }

        static Map<String, String> getMilePostMap(Context context) {
            Map<String, String> maps = sBaseManager.getMaps(context);
            long jCurrentTimeMillis = System.currentTimeMillis();
            Iterator<Map.Entry<String, String>> it = maps.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> next = it.next();
                try {
                    if (jCurrentTimeMillis - Long.parseLong(Tools.cutTime(next.getValue())) >= SweetNotificationHelper.fixSweetTimeOut(Tools.cutTimeout(next.getValue())) * 1000) {
                        it.remove();
                        ClickManager.removeClickStatus(context, next.getKey());
                    }
                } catch (Exception unused) {
                    it.remove();
                }
            }
            return maps;
        }

        static String readStatus(Context context, String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            return getMilePostMap(context).get(str);
        }

        static void removeStatus(Context context, String str) {
            sBaseManager.remove(context, str);
        }
    }

    private static class Tools {
        private Tools() {
        }

        static String cutStatus(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            String[] strArrSplit = str.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
            if (strArrSplit.length >= 2) {
                return strArrSplit[1];
            }
            return null;
        }

        static String cutTime(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            String[] strArrSplit = str.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
            if (strArrSplit.length >= 1) {
                return strArrSplit[0];
            }
            return null;
        }

        static int cutTimeout(String str) {
            if (!TextUtils.isEmpty(str)) {
                String[] strArrSplit = str.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
                if (strArrSplit.length >= 3) {
                    return ToolUtils.parseInt(strArrSplit[2], 0);
                }
            }
            return 0;
        }

        static String formValueByStatus(String str, int i2) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            return System.currentTimeMillis() + Constants.ACCEPT_TIME_SEPARATOR_SERVER + str + Constants.ACCEPT_TIME_SEPARATOR_SERVER + String.valueOf(i2);
        }

        static String milePostKey(int i2, String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            return str + Constants.ACCEPT_TIME_SEPARATOR_SERVER + String.valueOf(i2);
        }
    }

    SweetMilePostManager() {
    }

    public static void clickOrCancelSweetNotification(Context context, int i2, String str, String str2) {
        if (context == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        String strMilePostKey = Tools.milePostKey(i2, str);
        if (TextUtils.isEmpty(strMilePostKey) || TextUtils.isEmpty(str2)) {
            return;
        }
        synchronized (Tools.class) {
            ClickManager.addClickStatus(context, strMilePostKey, str2);
        }
    }

    public static String clickOrCanceledStatus(Context context, int i2, String str) {
        String clickStatus;
        String strMilePostKey = Tools.milePostKey(i2, str);
        if (TextUtils.isEmpty(strMilePostKey)) {
            return null;
        }
        synchronized (Tools.class) {
            clickStatus = ClickManager.readClickStatus(context, strMilePostKey);
        }
        return clickStatus;
    }

    public static void deleteStatus(Context context, int i2, String str) {
        String strMilePostKey = Tools.milePostKey(i2, str);
        if (TextUtils.isEmpty(strMilePostKey)) {
            return;
        }
        synchronized (Tools.class) {
            StatusManager.removeStatus(context, strMilePostKey);
            ClickManager.removeClickStatus(context, strMilePostKey);
            SequenceManager.removeSequence(context, strMilePostKey);
        }
    }

    public static Map<String, Set<String>> getSweetNotifications(Context context) {
        HashMap map;
        synchronized (Tools.class) {
            try {
                Map<String, String> milePostMap = StatusManager.getMilePostMap(context);
                map = new HashMap();
                Iterator<Map.Entry<String, String>> it = milePostMap.entrySet().iterator();
                while (it.hasNext()) {
                    try {
                        String[] strArrSplit = it.next().getKey().split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
                        if (strArrSplit.length == 2) {
                            String str = strArrSplit[0];
                            String str2 = strArrSplit[1];
                            Set hashSet = (Set) map.get(str);
                            if (hashSet == null) {
                                hashSet = new HashSet();
                            }
                            hashSet.add(str2);
                            map.put(str, hashSet);
                        }
                    } catch (Exception unused) {
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return map;
    }

    public static boolean isMilePost(Context context, String str, int i2, String str2) {
        boolean z2;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || context == null) {
            return false;
        }
        String strMilePostKey = Tools.milePostKey(i2, str2);
        synchronized (Tools.class) {
            z2 = !TextUtils.equals(str, Tools.cutStatus(StatusManager.readStatus(context, strMilePostKey)));
        }
        return z2;
    }

    public static boolean isSequenceCorrect(Context context, String str, int i2, String str2) {
        long sequence;
        if (TextUtils.isEmpty(str2) || context == null) {
            return false;
        }
        String strMilePostKey = Tools.milePostKey(i2, str2);
        synchronized (Tools.class) {
            sequence = SequenceManager.readSequence(context, strMilePostKey);
        }
        return sequence <= 0 || ToolUtils.parseLong(str, 0L) >= sequence;
    }

    public static void writeSequence(Context context, String str, int i2, String str2) {
        String strMilePostKey = Tools.milePostKey(i2, str2);
        if (TextUtils.isEmpty(strMilePostKey) || TextUtils.isEmpty(str2)) {
            return;
        }
        long j2 = ToolUtils.parseLong(str, -1L);
        if (j2 >= 0) {
            synchronized (Tools.class) {
                SequenceManager.addSequence(context, strMilePostKey, j2);
            }
        }
    }

    public static void writeStatus(Context context, String str, int i2, String str2, int i3) {
        String strMilePostKey = Tools.milePostKey(i2, str2);
        String strFormValueByStatus = Tools.formValueByStatus(str, i3);
        if (TextUtils.isEmpty(strMilePostKey) || TextUtils.isEmpty(strFormValueByStatus)) {
            return;
        }
        synchronized (Tools.class) {
            StatusManager.addStatus(context, strMilePostKey, strFormValueByStatus);
        }
    }
}
