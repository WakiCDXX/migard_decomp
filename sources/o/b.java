package o;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.miui.powerkeeper.unionpower.traceextension.TraceExtensionService;
import java.util.HashMap;
import java.util.Set;
import org.json.JSONObject;

/* JADX INFO: compiled from: ITriggerHandler.java */
/* JADX INFO: loaded from: classes.dex */
public abstract class b extends Handler {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    String f2100a;

    public b(Looper looper) {
        super(looper);
        this.f2100a = "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void c(String str, String str2) {
        if (!str.isEmpty()) {
            Log.d(this.f2100a, "ACTION_TYPE_TRACE");
            n.c.i(str);
        }
        if (str2.isEmpty()) {
            return;
        }
        Log.d(this.f2100a, "ACTION_TYPE_OTHER");
        b(str2);
    }

    public Runnable d(JSONObject jSONObject) {
        if ((jSONObject != null ? Integer.parseInt(jSONObject.optString("type", "0")) : 0) == 0) {
            return null;
        }
        final String strOptString = jSONObject.optString("trace", "");
        final String strOptString2 = jSONObject.optString("other", "");
        return new Runnable() { // from class: o.a
            @Override // java.lang.Runnable
            public final void run() {
                this.f2097a.c(strOptString, strOptString2);
            }
        };
    }

    public void e(String str, HashMap<Integer, Runnable> map) {
        TraceExtensionService.B(str, map);
    }

    public void f(String str, Set<Integer> set) {
        TraceExtensionService.D(str, set);
    }

    public void b(String str) {
    }
}
