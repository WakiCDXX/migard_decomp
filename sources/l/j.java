package l;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.push.extension.PluginConstants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: TouchObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class j extends b {

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static j f2064l;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final a f2068g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private boolean f2069h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private int f2070i;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private long f2065d = 3000;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private long f2066e = 22000;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private boolean f2067f = Log.isLoggable("UnionPower.TouchObserver", 3);

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private String f2071j = "";

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private String f2072k = "";

    /* JADX INFO: compiled from: TouchObserver.java */
    final class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == -5) {
                if (j.this.f2067f) {
                    Log.d("UnionPower.TouchObserver", "Screen off cancel messages");
                }
                j.this.s();
                return;
            }
            if (i2 == 1) {
                j.this.w(((Boolean) message.obj).booleanValue());
                return;
            }
            if (i2 == 2000) {
                Bundle data = message.getData();
                j.this.f2071j = data.getString("pkg", "");
                j.this.f2072k = data.getString("className", "");
                j.this.g(2, data);
                return;
            }
            if (i2 != 1000) {
                if (i2 != 1001) {
                    return;
                }
                if (j.this.f2067f) {
                    Log.d("UnionPower.TouchObserver", "Transfering state =3");
                }
                j.this.d(1, 3, 0);
                j.this.f2070i = 3;
                j.this.f2069h = false;
                return;
            }
            if (j.this.f2067f) {
                Log.d("UnionPower.TouchObserver", "Transfering state =2");
            }
            j.this.d(1, 2, 0);
            j.this.f2070i = 2;
            Message messageObtainMessage = j.this.f2068g.obtainMessage(1001);
            messageObtainMessage.setAsynchronous(true);
            long jUptimeMillis = SystemClock.uptimeMillis() + j.this.f2066e;
            if (j.this.f2067f) {
                Log.d("UnionPower.TouchObserver", "Send deep idle delay message at " + jUptimeMillis);
            }
            j.this.f2068g.sendMessageAtTime(messageObtainMessage, jUptimeMillis);
        }
    }

    private j(Context context, EventsAggregator eventsAggregator) {
        this.f2027a = context;
        this.f2028b = eventsAggregator;
        a aVar = new a(UnionPowerService.b().getLooper());
        this.f2068g = aVar;
        this.f2028b.registerForScreenOff(aVar, null);
        this.f2069h = false;
        this.f2070i = 1;
        x();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void s() {
        if (this.f2068g.hasMessages(1000)) {
            this.f2068g.removeMessages(1000);
        } else if (this.f2068g.hasMessages(1001)) {
            this.f2068g.removeMessages(1001);
        }
        this.f2069h = false;
    }

    public static synchronized l.a t(Context context, EventsAggregator eventsAggregator) {
        try {
            if (f2064l == null) {
                f2064l = new j(context, eventsAggregator);
            }
        } catch (Throwable th) {
            throw th;
        }
        return f2064l;
    }

    public static void u(String str, String str2) {
        if (f2064l != null) {
            Bundle bundle = new Bundle();
            bundle.putString("pkg", str);
            bundle.putString("className", str2);
            Message messageObtainMessage = f2064l.f2068g.obtainMessage(2000);
            messageObtainMessage.setData(bundle);
            messageObtainMessage.sendToTarget();
        }
    }

    public static void v(boolean z2) {
        j jVar = f2064l;
        if (jVar != null) {
            jVar.f2068g.obtainMessage(1, Boolean.valueOf(z2)).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void w(boolean z2) {
        if (this.f2069h) {
            s();
        }
        if (z2) {
            if (this.f2070i != 1) {
                if (this.f2067f) {
                    Log.d("UnionPower.TouchObserver", "Transferring state =1");
                }
                d(1, 1, 0);
                this.f2070i = 1;
                return;
            }
            return;
        }
        Message messageObtainMessage = this.f2068g.obtainMessage(1000);
        messageObtainMessage.setAsynchronous(true);
        long jUptimeMillis = SystemClock.uptimeMillis() + this.f2065d;
        if (this.f2067f) {
            Log.d("UnionPower.TouchObserver", "Send idle delay message at " + jUptimeMillis);
        }
        this.f2068g.sendMessageAtTime(messageObtainMessage, jUptimeMillis);
        this.f2069h = true;
    }

    private void x() {
        try {
            JSONArray jSONArray = new JSONArray(SimpleSettings.Misc.getString(this.f2027a, "touch_idle_time", ""));
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i2);
                String strOptString = jSONObjectOptJSONObject.optString("model_name", "");
                if (Utils.isBelongToDeviceSeries(strOptString)) {
                    this.f2065d = jSONObjectOptJSONObject.optLong("interval_time", PluginConstants.TTS_REMOVE_BEAN_TIME);
                    this.f2066e = jSONObjectOptJSONObject.optLong("interval_deep", PluginConstants.TTS_REVERT_VOLUME_TIME);
                    Log.i("UnionPower.TouchObserver", "Support device=" + strOptString + ", interval_time=" + this.f2065d + ", interval_deep=" + this.f2066e);
                    return;
                }
            }
        } catch (JSONException unused) {
            Log.e("UnionPower.TouchObserver", "parse json config failed");
        } catch (Exception e2) {
            Log.e("UnionPower.TouchObserver", "update config failed" + e2);
        }
    }

    public static void y() {
        l lVarB;
        if (f2064l == null || (lVarB = l.b(null, null)) == null) {
            return;
        }
        lVarB.d(f2064l.f2068g, UnionPowerConfig.CloudModuleEnum.BASE, 0, "touch_idle_time");
    }

    @Override // l.l.a
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("idle time=" + this.f2065d);
        printWriter.println("deep idle time=" + this.f2066e);
        printWriter.println("registrants: " + this.f2029c);
    }
}
