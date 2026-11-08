package s;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import com.ot.pubsub.PubSubTrack;
import com.ot.pubsub.util.m;
import com.ot.pubsub.util.n;
import com.ot.pubsub.util.q;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class c {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static ExecutorService f2316f;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private b f2317a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private Context f2318b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private r.b f2319c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private q f2320d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private BroadcastReceiver f2321e = new g(this);

    public c(Context context, r.b bVar) {
        Context applicationContext = context.getApplicationContext();
        this.f2318b = applicationContext;
        this.f2319c = bVar;
        b(applicationContext);
    }

    private void b(Context context) {
        m.d(this.f2319c.h(), this.f2319c.g());
        if (f2316f == null) {
            f2316f = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        }
        q qVar = new q(this.f2319c);
        this.f2320d = qVar;
        this.f2317a = new j(this.f2318b, this.f2319c, qVar);
        g();
        m.c(this.f2319c.j());
        k();
        f2316f.execute(new d(this));
    }

    private void g() {
        try {
            Context contextC = com.ot.pubsub.util.b.c();
            if (contextC == null) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            contextC.registerReceiver(this.f2321e, intentFilter);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.j("PubSubTrackImp", "registerScreenReceiver: %s", e2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean h(String str) {
        boolean zH = n.h(str);
        if (!zH) {
            com.ot.pubsub.util.k.h("PubSubTrackImp", String.format("Invalid eventname: %s. Eventname can only consist of numbers, letters, underscores ,and can not start with a number or \"onetrack_\" or \"ot_\"", str));
        }
        return !zH;
    }

    static /* synthetic */ PubSubTrack.a i(c cVar) {
        cVar.getClass();
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public JSONObject j(String str) throws Throwable {
        try {
            JSONObject jSONObjectD = n.d(null, false);
            String strA = com.ot.pubsub.util.g.a(n.a(this.f2319c));
            return n.e(jSONObjectD, !TextUtils.isEmpty(strA) ? new JSONObject(strA) : null);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("PubSubTrackImp", "getCommonProperty: " + e2.toString());
            return null;
        }
    }

    private void k() {
        try {
            Context contextC = com.ot.pubsub.util.b.c();
            if (contextC == null) {
                return;
            }
            ((Application) contextC).registerActivityLifecycleCallbacks(new f(this));
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("PubSubTrackImp", "registerLifecycleCallback: " + e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void m() {
        if (y.i.i()) {
            com.ot.pubsub.util.e.a(new i(this));
        }
    }

    public void c(String str, String str2, String str3, Map<String, Object> map, Map<String, String> map2) {
        f2316f.execute(new e(this, str3, map, str, str2, map2));
    }
}
