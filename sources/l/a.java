package l;

import android.content.ComponentName;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: compiled from: AmProcessObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class a extends b {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static a f2023e;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final HandlerC0025a f2024d = new HandlerC0025a(UnionPowerService.b().getLooper());

    /* JADX INFO: renamed from: l.a$a, reason: collision with other inner class name */
    /* JADX INFO: compiled from: AmProcessObserver.java */
    final class HandlerC0025a extends Handler {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final Map<String, Long> f2025a;

        public HandlerC0025a(Looper looper) {
            super(looper);
            this.f2025a = new HashMap();
        }

        public long b(String str) {
            Long l2 = this.f2025a.get(str);
            if (l2 == null) {
                return -1L;
            }
            return l2.longValue();
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 0) {
                String str = (String) message.obj;
                this.f2025a.put(str, Long.valueOf(SystemClock.uptimeMillis()));
                a.this.h(str);
            }
        }
    }

    private a() {
    }

    public static synchronized l.a j() {
        try {
            if (f2023e == null) {
                f2023e = new a();
            }
        } catch (Throwable th) {
            throw th;
        }
        return f2023e;
    }

    public static long k(String str) {
        a aVar = f2023e;
        if (aVar != null) {
            return aVar.f2024d.b(str);
        }
        return -1L;
    }

    public static void l(int i2, int i3, int i4, String str, ComponentName componentName, String str2) {
        if (f2023e != null) {
            String packageName = componentName == null ? null : componentName.getPackageName();
            if (packageName != null) {
                f2023e.f2024d.obtainMessage(0, packageName.replace("{", "")).sendToTarget();
            }
        }
    }

    @Override // l.l.a
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (UnionPowerConfig.f656a) {
            printWriter.println(this.f2024d.f2025a);
        }
    }
}
