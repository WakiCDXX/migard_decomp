package l;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.powerkeeper.utils.Utils;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: UnionPowerCloudObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class k implements b.a.d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f2074a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final UnionPowerConfig.CloudModuleEnum f2075b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private a f2076c;

    /* JADX INFO: compiled from: UnionPowerCloudObserver.java */
    class a extends Handler {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private ArrayList<b> f2077a;

        a() {
            super(UnionPowerService.b().getLooper());
        }

        private void a(String str) {
            if (str != null) {
                List listAsList = Arrays.asList(str.split(","));
                if (this.f2077a.isEmpty() || listAsList.isEmpty()) {
                    return;
                }
                for (b bVar : this.f2077a) {
                    if (listAsList.contains(bVar.f2081c)) {
                        bVar.b();
                    }
                }
            }
        }

        private void b(Handler handler) {
            for (int size = this.f2077a.size() - 1; size >= 0; size--) {
                Handler handlerA = this.f2077a.get(size).a();
                if (handlerA == null || handlerA == handler) {
                    this.f2077a.remove(size);
                }
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == 0) {
                this.f2077a = new ArrayList<>();
                return;
            }
            if (i2 == 1) {
                this.f2077a.add((b) message.obj);
            } else if (i2 == 2) {
                b((Handler) message.obj);
            } else if (i2 == 3) {
                a((String) message.obj);
            } else {
                if (i2 != 4) {
                    return;
                }
                ((PrintWriter) message.obj).println(this.f2077a);
            }
        }
    }

    /* JADX INFO: compiled from: UnionPowerCloudObserver.java */
    private static class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        WeakReference<Handler> f2079a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        int f2080b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        String f2081c;

        b(Handler handler, int i2, String str) {
            this.f2079a = new WeakReference<>(handler);
            this.f2080b = i2;
            this.f2081c = str;
        }

        Handler a() {
            WeakReference<Handler> weakReference = this.f2079a;
            if (weakReference == null) {
                return null;
            }
            return weakReference.get();
        }

        void b() {
            WeakReference<Handler> weakReference = this.f2079a;
            if (weakReference != null) {
                Handler handler = weakReference.get();
                if (handler == null) {
                    this.f2079a = null;
                } else {
                    handler.sendEmptyMessage(this.f2080b);
                }
            }
        }
    }

    public k(Context context, UnionPowerConfig.CloudModuleEnum cloudModuleEnum) {
        this.f2074a = context;
        this.f2075b = cloudModuleEnum;
        a aVar = new a();
        this.f2076c = aVar;
        aVar.sendEmptyMessage(0);
        c(null);
    }

    private JSONObject a(String str, JSONObject jSONObject, int i2) {
        if (!this.f2075b.i()) {
            return jSONObject == null ? b.a.p().l(this.f2075b.g()) : jSONObject;
        }
        if (!str.isEmpty()) {
            if (i2 > (jSONObject == null ? -1 : jSONObject.optInt("p", 0))) {
                return null;
            }
            return jSONObject;
        }
        List<JSONObject> listM = b.a.p().m(this.f2075b.g());
        if (listM == null) {
            return null;
        }
        JSONObject jSONObject2 = listM.get(0);
        for (int i3 = 1; i3 < listM.size(); i3++) {
            JSONObject jSONObject3 = listM.get(i3);
            if (jSONObject2.optInt("p", 0) < jSONObject3.optInt("p", 0)) {
                jSONObject2 = jSONObject3;
            }
        }
        return jSONObject2;
    }

    private void b(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        this.f2076c.obtainMessage(3, String.join(",", list)).sendToTarget();
    }

    private void c(JSONObject jSONObject) {
        JSONObject jSONObjectA;
        String str;
        String strF = this.f2075b.f(this.f2074a);
        String strE = this.f2075b.e(this.f2074a);
        String strQ = b.a.p().q(this.f2075b.g());
        JSONObject jSONObject2 = new JSONObject();
        if (strF.isEmpty()) {
            JSONObject jSONObjectD = this.f2075b.d(this.f2074a);
            if (jSONObjectD == null) {
                str = "-1";
            } else {
                String strMD5 = Utils.MD5(jSONObjectD.toString());
                Iterator<String> itKeys = jSONObjectD.keys();
                while (itKeys.hasNext()) {
                    String next = itKeys.next();
                    try {
                        jSONObject2.put(next, jSONObjectD.optString(next, ""));
                    } catch (JSONException unused) {
                    }
                }
                str = strMD5;
            }
            this.f2075b.k(this.f2074a, str);
        }
        if (!strE.equals(strQ) && (jSONObjectA = a(strE, jSONObject, this.f2075b.h(this.f2074a))) != null) {
            Iterator<String> itKeys2 = jSONObjectA.keys();
            while (itKeys2.hasNext()) {
                String next2 = itKeys2.next();
                try {
                    jSONObject2.put(next2, jSONObjectA.optString(next2, ""));
                } catch (JSONException unused2) {
                }
            }
            this.f2075b.j(this.f2074a, Utils.MD5(jSONObjectA.toString()), jSONObjectA.optInt("p", 0));
        }
        ArrayList arrayList = new ArrayList();
        Iterator<String> itKeys3 = jSONObject2.keys();
        while (itKeys3.hasNext()) {
            String next3 = itKeys3.next();
            String strOptString = jSONObject2.optString(next3, "");
            if (!strOptString.equals(SimpleSettings.Misc.getString(this.f2074a, next3, ""))) {
                SimpleSettings.Misc.putString(this.f2074a, next3, strOptString);
                arrayList.add(next3);
            }
        }
        b(arrayList);
    }

    public void d(Handler handler, int i2, String str) {
        b bVar = new b(handler, i2, str);
        this.f2076c.obtainMessage(2, handler).sendToTarget();
        this.f2076c.obtainMessage(1, bVar).sendToTarget();
    }

    @Override // b.a.d
    public void onChanged(JSONObject jSONObject) {
        c(jSONObject);
    }
}
