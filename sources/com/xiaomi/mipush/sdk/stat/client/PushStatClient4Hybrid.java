package com.xiaomi.mipush.sdk.stat.client;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.mipush.sdk.stat.PushStatClientManager;
import com.xiaomi.mipush.sdk.stat.upload.IDbPathGetter;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PushStatClient4Hybrid {
    private static Context sContext;

    public static void init(Context context, String str, String str2, String str3, IDbPathGetter iDbPathGetter) {
        sContext = context.getApplicationContext();
        String str4 = TextUtils.isEmpty(str2) ? "appId can not be null. " : "";
        if (TextUtils.isEmpty(str3)) {
            str4 = str4 + "channel can not be null. ";
        }
        if (TextUtils.isEmpty(str)) {
            str4 = str4 + "packageName can not be null.";
        }
        if (!TextUtils.isEmpty(str4)) {
            throw new IllegalArgumentException(str4);
        }
        PushStatClientManager.getInstance(sContext).init(str, str2, str3, iDbPathGetter);
    }

    private static void record(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        PushStatClientManager.getInstance(sContext).record(str);
    }

    public static void recordCalculateEvent(String str, String str2, long j2) {
        record(EventDataItem.getCalculateEvent(str, str2, j2).toJson());
    }

    public static void recordCountEvent(String str, String str2) {
        record(EventDataItem.getCountEvent(str, str2).toJson());
    }

    public static void recordNumericPropertyEvent(String str, String str2, long j2) {
        record(EventDataItem.getNumericPropertyEvent(str, str2, j2).toJson());
    }

    public static void recordStringPropertyEvent(String str, String str2, String str3) {
        record(EventDataItem.getPropertyEvent(str, str2, str3).toJson());
    }

    public static void schedule() {
        PushStatClientManager.getInstance(sContext).schedule();
    }

    public static void recordCalculateEvent(String str, String str2, long j2, Map<String, String> map) {
        record(EventDataItem.getCalculateEvent(str, str2, j2, map).toJson());
    }

    public static void recordCountEvent(String str, String str2, Map<String, String> map) {
        record(EventDataItem.getCountEvent(str, str2, map).toJson());
    }

    private static void record(JSONObject jSONObject) {
        if (jSONObject != null) {
            PushStatClientManager.getInstance(sContext).record(jSONObject.toString());
        }
    }
}
