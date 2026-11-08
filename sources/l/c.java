package l;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: compiled from: FgComponentObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class c extends b {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static c f2033f;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Handler f2034d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private PowerKeeperManager f2035e;

    /* JADX INFO: compiled from: FgComponentObserver.java */
    class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != -9) {
                return;
            }
            c.this.h(message.obj);
        }
    }

    private c(Context context, EventsAggregator eventsAggregator) {
        this.f2027a = context;
        this.f2028b = eventsAggregator;
        this.f2035e = PowerKeeperManager.getInstance();
        a aVar = new a(UnionPowerService.b().getLooper());
        this.f2034d = aVar;
        this.f2028b.registerForForegroundInfo(aVar);
    }

    static synchronized l.a j(Context context, EventsAggregator eventsAggregator) {
        try {
            if (f2033f == null) {
                f2033f = new c(context, eventsAggregator);
            }
        } catch (Throwable th) {
            throw th;
        }
        return f2033f;
    }

    @Override // l.l.a
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
