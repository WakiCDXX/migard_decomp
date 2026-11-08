package com.xiaomi.onetrack.api;

import android.content.Context;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.ServiceQualityEvent;
import com.xiaomi.onetrack.util.DeviceUtil;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f1070a = "B";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f1071b = "H";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1072c = "EventDataBuilder";

    public static String a(String str, JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(f1071b, com.xiaomi.onetrack.f.b.a(str, configuration, iEventHook, vVar, z2, false));
        jSONObject3.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String b(JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(f1071b, com.xiaomi.onetrack.f.b.a(d.f1047d, configuration, iEventHook, vVar, z2, false));
        jSONObject3.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String c(JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(f1071b, com.xiaomi.onetrack.f.b.a("ot_login", configuration, iEventHook, vVar, z2, false));
        jSONObject3.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String d(JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(f1071b, com.xiaomi.onetrack.f.b.a("ot_logout", configuration, iEventHook, vVar, z2, false));
        jSONObject3.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String a(f fVar, JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(f1071b, com.xiaomi.onetrack.f.b.a(fVar, configuration, iEventHook, vVar, z2, false));
        jSONObject3.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String a(String str, String str2, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, boolean z2, com.xiaomi.onetrack.util.v vVar, boolean z3) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(f1071b, com.xiaomi.onetrack.f.b.a(str2, configuration, iEventHook, vVar, z3, false));
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("class", str);
        jSONObject3.put("type", 1);
        jSONObject3.put(d.f1063t, z2);
        jSONObject2.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(String str, String str2, long j2, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(f1071b, com.xiaomi.onetrack.f.b.a(str2, configuration, iEventHook, vVar, z2, false));
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("class", str);
        jSONObject3.put("type", 2);
        jSONObject3.put("duration", j2);
        jSONObject2.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(String str, String str2, String str3, String str4, String str5, long j2, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        JSONObject jSONObjectA = com.xiaomi.onetrack.f.b.a("onetrack_bug_report", configuration, iEventHook, vVar, z2, false);
        if (str5 != null) {
            jSONObjectA.put(com.xiaomi.onetrack.f.b.C0012b.f1395o, str5);
        }
        jSONObject2.put(f1071b, jSONObjectA);
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("exception", str);
        jSONObject3.put("type", str3);
        jSONObject3.put("message", str2);
        jSONObject3.put("feature", str4);
        jSONObject3.put(d.f1058o, j2);
        jSONObject2.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(f1071b, com.xiaomi.onetrack.f.b.a("onetrack_dau", configuration, iEventHook, vVar, z2, false));
        JSONObject jSONObject3 = new JSONObject();
        Context contextB = com.xiaomi.onetrack.f.a.b();
        boolean zS = com.xiaomi.onetrack.util.aa.s();
        if (zS) {
            com.xiaomi.onetrack.util.aa.c(false);
        }
        jSONObject3.put("first_open", zS);
        if (!(com.xiaomi.onetrack.util.q.a() ? com.xiaomi.onetrack.util.q.h() : configuration.isInternational())) {
            if (configuration.isIMEIEnable()) {
                jSONObject3.put(d.f1068y, DeviceUtil.q(contextB));
            }
            if (configuration.isIMSIEnable()) {
                jSONObject3.put(d.f1069z, DeviceUtil.v(contextB));
            }
        }
        jSONObject3.put(d.C, c.a(configuration));
        jSONObject2.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(f1071b, com.xiaomi.onetrack.f.b.a(d.f1046c, configuration, iEventHook, vVar, z2, false));
        jSONObject3.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String a(String str, String str2, JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(f1071b, com.xiaomi.onetrack.f.b.a(str2, configuration, iEventHook, str, vVar, z2, false));
        jSONObject3.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String a(Configuration configuration, OneTrack.IEventHook iEventHook, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(f1071b, com.xiaomi.onetrack.f.b.a(d.f1052i, configuration, iEventHook, vVar, z2, false));
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("value", com.xiaomi.onetrack.c.i.b());
        jSONObject.put(f1070a, jSONObject2);
        return jSONObject.toString();
    }

    public static String a(ServiceQualityEvent serviceQualityEvent, Configuration configuration, OneTrack.IEventHook iEventHook, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(f1071b, com.xiaomi.onetrack.f.b.a(d.f1048e, configuration, iEventHook, vVar, z2, false));
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(d.D, serviceQualityEvent.getScheme());
        jSONObject2.put("host", serviceQualityEvent.getHost());
        jSONObject2.put(d.F, serviceQualityEvent.getPort());
        jSONObject2.put("path", serviceQualityEvent.getPath());
        jSONObject2.put(d.H, serviceQualityEvent.getIps());
        jSONObject2.put(d.I, serviceQualityEvent.getResponseCode());
        jSONObject2.put("status", serviceQualityEvent.getStatusCode());
        jSONObject2.put("exception", serviceQualityEvent.getExceptionTag());
        jSONObject2.put(d.L, serviceQualityEvent.getResultType());
        jSONObject2.put("retry", serviceQualityEvent.getRetryCount());
        jSONObject2.put(d.N, serviceQualityEvent.getRequestTimestamp());
        jSONObject2.put(d.O, serviceQualityEvent.getRequestNetType());
        jSONObject2.put(d.P, serviceQualityEvent.getDnsLookupTime());
        jSONObject2.put(d.Q, serviceQualityEvent.getTcpConnectTime());
        jSONObject2.put(d.S, serviceQualityEvent.getHandshakeTime());
        jSONObject2.put(d.T, serviceQualityEvent.getReceiveFirstByteTime());
        jSONObject2.put(d.U, serviceQualityEvent.getReceiveAllByteTime());
        jSONObject2.put(d.R, serviceQualityEvent.getRequestDataSendTime());
        jSONObject2.put("duration", serviceQualityEvent.getDuration());
        jSONObject2.put(d.W, serviceQualityEvent.getNetSdkVersion());
        Map<String, Object> extraParams = serviceQualityEvent.getExtraParams();
        if (extraParams != null && extraParams.size() > 0) {
            for (Map.Entry<String, Object> entry : extraParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (com.xiaomi.onetrack.util.r.b(value)) {
                    jSONObject2.put(key, value);
                }
            }
        }
        jSONObject.put(f1070a, jSONObject2);
        return jSONObject.toString();
    }

    public static String a(long j2, String str, long j3, long j4, Configuration configuration, OneTrack.IEventHook iEventHook, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(f1071b, com.xiaomi.onetrack.f.b.a(d.f1053j, configuration, iEventHook, vVar, z2, false));
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(d.X, j2);
        jSONObject2.put(d.Y, str);
        jSONObject2.put(d.Z, j3);
        jSONObject2.put(d.aa, j4);
        jSONObject.put(f1070a, jSONObject2);
        return jSONObject.toString();
    }

    public static String a(String str, JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, JSONArray jSONArray, boolean z2) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        JSONObject jSONObjectA = com.xiaomi.onetrack.f.b.a(str, configuration, iEventHook, vVar, z2, true);
        if (jSONArray != null && jSONArray.length() > 0) {
            jSONObjectA.put(com.xiaomi.onetrack.f.b.C0012b.H, jSONArray);
        }
        jSONObject3.put(f1071b, jSONObjectA);
        jSONObject3.put(f1070a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }
}
