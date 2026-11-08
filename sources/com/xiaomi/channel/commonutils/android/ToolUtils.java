package com.xiaomi.channel.commonutils.android;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Looper;
import android.os.PowerManager;
import android.text.TextUtils;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ToolUtils {

    public static class StringMap {
        private final String mGroupSeparator;
        private final String mItemSeparator;
        private final StringBuilder resultBuilder;

        public StringMap() {
            this(":", ",");
        }

        public StringMap put(String str, Object obj) {
            if (!TextUtils.isEmpty(str)) {
                if (this.resultBuilder.length() > 0) {
                    this.resultBuilder.append(this.mGroupSeparator);
                }
                StringBuilder sb = this.resultBuilder;
                sb.append(str);
                sb.append(this.mItemSeparator);
                sb.append(obj);
            }
            return this;
        }

        public String toString() {
            return this.resultBuilder.toString();
        }

        public StringMap(String str, String str2) {
            this.resultBuilder = new StringBuilder();
            this.mItemSeparator = str;
            this.mGroupSeparator = str2;
        }
    }

    public static List<String> asList(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        Collections.addAll(arrayList, str.split(str2));
        return arrayList;
    }

    public static boolean contains(String str, String str2) {
        return !TextUtils.isEmpty(str) && str.contains(str2);
    }

    public static int dp2px(Context context, int i2) {
        return (int) ((i2 * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int generateRealID(String str, int i2) {
        return !TextUtils.isEmpty(str) ? ((str.hashCode() / 10) * 10) + i2 : i2;
    }

    public static boolean isEmptyList(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isKeyguardLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        return keyguardManager != null && keyguardManager.isKeyguardLocked();
    }

    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(NightStandbyRecord.KEY_POWER);
        return powerManager != null && powerManager.isInteractive();
    }

    public static boolean isUIThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static <T> String join(List<T> list, String str) {
        if (isEmptyList(list)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append(str);
        }
        return sb.toString();
    }

    public static float parseFloat(String str, float f2) {
        if (TextUtils.isEmpty(str)) {
            return f2;
        }
        try {
            return Float.parseFloat(str);
        } catch (Exception unused) {
            return f2;
        }
    }

    public static int parseInt(String str, int i2) {
        if (TextUtils.isEmpty(str)) {
            return i2;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception unused) {
            return i2;
        }
    }

    public static long parseLong(String str, long j2) {
        if (TextUtils.isEmpty(str)) {
            return j2;
        }
        try {
            return Long.parseLong(str);
        } catch (Exception unused) {
            return j2;
        }
    }
}
