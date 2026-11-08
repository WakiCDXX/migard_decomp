package l;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: compiled from: PowerModeObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class g extends b {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static g f2047e;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private a f2048d;

    /* JADX INFO: compiled from: PowerModeObserver.java */
    private class a extends Handler {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final String f2049a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final int f2050b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private boolean f2051c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private int f2052d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private ContentObserver f2053e;

        /* JADX INFO: renamed from: l.g$a$a, reason: collision with other inner class name */
        /* JADX INFO: compiled from: PowerModeObserver.java */
        class C0026a extends ContentObserver {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            final /* synthetic */ g f2055a;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            C0026a(Handler handler, g gVar) {
                super(handler);
                this.f2055a = gVar;
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                super.onChange(z2);
                a.this.obtainMessage(0, Settings.System.getInt(g.this.f2027a.getContentResolver(), "power_supersave_mode_open", 0), 0).sendToTarget();
            }
        }

        a(Looper looper) {
            super(looper);
            this.f2049a = "power_supersave_mode_open";
            this.f2050b = 0;
            this.f2051c = Settings.System.getInt(g.this.f2027a.getContentResolver(), "power_supersave_mode_open", 0) == 1;
            this.f2052d = g.this.f2028b.getPowerMode();
            this.f2053e = new C0026a(this, g.this);
            g.this.f2027a.getContentResolver().registerContentObserver(Settings.System.getUriFor("power_supersave_mode_open"), false, this.f2053e);
        }

        public int a() {
            if (this.f2051c) {
                return 4;
            }
            return this.f2052d;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == -22) {
                this.f2052d = message.arg1;
            } else if (i2 == 0) {
                this.f2051c = message.arg1 == 1;
            }
            g.this.c(this.f2051c ? 4 : this.f2052d, 0);
        }
    }

    g(EventsAggregator eventsAggregator, Context context) {
        this.f2028b = eventsAggregator;
        this.f2027a = context;
        a aVar = new a(UnionPowerService.b().getLooper());
        this.f2048d = aVar;
        this.f2028b.registerPowerModeListener(aVar);
    }

    static synchronized l.a j(EventsAggregator eventsAggregator, Context context) {
        try {
            if (f2047e == null) {
                f2047e = new g(eventsAggregator, context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return f2047e;
    }

    public static synchronized int k() {
        a aVar;
        g gVar = f2047e;
        if (gVar == null || (aVar = gVar.f2048d) == null) {
            return -1;
        }
        return aVar.a();
    }

    @Override // l.l.a
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
