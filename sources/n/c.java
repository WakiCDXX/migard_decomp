package n;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.traceextension.TraceExtensionService;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import com.miui.powerkeeper.utils.TraceUtils;
import com.xiaomi.channel.commonutils.misc.DateTimeHelper;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: TraceLogHelper.java */
/* JADX INFO: loaded from: classes.dex */
public class c implements n.a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f2087a = "TraceExtension_" + c.class.getName();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Context f2088b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static String f2089c = "gfx,input,view,audio,hal,power,sched,freq,idle,binder_driver,binder_lock";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static String f2090d = "MiPowerTrace-";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static int f2091e = 4096;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static int f2092f = 40;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static int f2093g = DateTimeHelper.DAY_IN_MINUTE;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static boolean f2094h = true;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static boolean f2095i = false;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final Handler f2096j = new a(Looper.getMainLooper());

    /* JADX INFO: compiled from: TraceLogHelper.java */
    class a extends Handler {
        a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1000) {
                return;
            }
            c.j((String) message.obj);
        }
    }

    private static void f() {
        final String string = SimpleSettings.Misc.getString(f2088b, "trace_extension_file_prefix", "MiPowerTrace-");
        int i2 = SimpleSettings.Misc.getInt(f2088b, "trace_extension_max_file_num", 3);
        String[] list = new File("/data/local/traces/").list(new FilenameFilter() { // from class: n.b
            @Override // java.io.FilenameFilter
            public final boolean accept(File file, String str) {
                return c.g(string, file, str);
            }
        });
        if (list != null) {
            Arrays.sort(list);
            for (int i3 = 0; i3 < list.length - i2; i3++) {
                boolean zDelete = new File("/data/local/traces/" + list[i3]).delete();
                if (TraceExtensionService.f631k) {
                    String str = f2087a;
                    StringBuilder sb = new StringBuilder();
                    sb.append("delete ");
                    sb.append(list[i3]);
                    sb.append(zDelete ? "success" : "fail");
                    Log.d(str, sb.toString());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean g(String str, File file, String str2) {
        return str2.startsWith(str);
    }

    private static void h() {
        f2089c = "gfx,input,view,audio,hal,power,sched,freq,idle,binder_driver,binder_lock";
        f2090d = "MiPowerTrace-";
        f2091e = 4096;
        f2092f = 40;
        f2093g = DateTimeHelper.DAY_IN_MINUTE;
        f2094h = true;
        f2095i = false;
    }

    public static void i(String str) {
        if (TraceExtensionService.f631k) {
            Log.d(f2087a, "trace handle ...");
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            f2089c = jSONObject.optString("trace", "gfx,input,view,audio,hal,power,sched,freq,idle,binder_driver,binder_lock");
            f2090d = jSONObject.optString("file_prefix", "MiPowerTrace-");
            f2091e = jSONObject.optInt("singer_cpu_buffer", 4096);
            f2092f = jSONObject.optInt("max_file_size_mb", 40);
            f2093g = jSONObject.optInt("duration_min", DateTimeHelper.DAY_IN_MINUTE);
            f2094h = jSONObject.optBoolean("atrace_apps", true);
            f2095i = jSONObject.optBoolean("long_trace", false);
        } catch (Exception e2) {
            Log.d(f2087a, "startTrace parse error=" + e2.getMessage());
        }
        SharedPrefUtil.getInstance(f2088b, f2087a).save("trace_status", true);
        Intent intent = new Intent("com.android.traceur.AppReceiver");
        intent.setPackage("com.android.traceur");
        intent.putExtra("ACTION", "traceutil_start");
        intent.putExtra("TAGS", f2089c);
        intent.putExtra("SINGLE_CPU_BUFFER", f2091e);
        intent.putExtra("MAX_FILE_SIZE_MB", f2092f);
        intent.putExtra("ATRACE_APPS", f2094h);
        intent.putExtra("LONG_TRACE", f2095i);
        intent.putExtra("DURATION_MIN", f2093g);
        f2088b.sendBroadcast(intent);
        Message message = new Message();
        message.what = 1000;
        message.obj = f2090d;
        f2096j.sendMessageDelayed(message, 10000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void j(String str) {
        SharedPrefUtil.getInstance(f2088b, f2087a).save("trace_status", false);
        Intent intent = new Intent("com.android.traceur.AppReceiver");
        intent.setPackage("com.android.traceur");
        intent.putExtra("ACTION", "traceutil_dump");
        intent.putExtra("FILE_PREFIX", str);
        f2088b.sendBroadcast(intent);
        h();
    }

    @Override // n.a
    public void a() {
        f();
    }

    @Override // n.a
    public void b() {
        TraceUtils.traceStop(f2088b);
        f();
    }

    @Override // n.a
    public void c(Context context) {
        f2088b = context;
        if (SharedPrefUtil.getInstance(context, f2087a).getBooleanValue("trace_status", false)) {
            TraceUtils.traceStop(context);
        }
    }
}
