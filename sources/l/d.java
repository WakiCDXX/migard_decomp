package l;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: compiled from: FgObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class d extends l.b {

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static d f2037h;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Handler f2038d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private PowerKeeperManager f2039e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private PowerKeeperInterface.f f2040f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private PowerKeeperInterface.r f2041g = new a();

    /* JADX INFO: compiled from: FgObserver.java */
    class a implements PowerKeeperInterface.r {
        a() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.r
        public void onForegroundActivitiesChanged(int i2, boolean z2) {
            d.this.f(i2, 0, Boolean.valueOf(z2));
        }
    }

    /* JADX INFO: compiled from: FgObserver.java */
    private static class b extends Handler {
        public b(Looper looper) {
            super(looper);
        }
    }

    private d(Context context, EventsAggregator eventsAggregator) {
        this.f2027a = context;
        this.f2028b = eventsAggregator;
        this.f2039e = PowerKeeperManager.getInstance();
        b bVar = new b(UnionPowerService.b().getLooper());
        this.f2038d = bVar;
        PowerKeeperInterface.f fVar = new PowerKeeperInterface.f(bVar, this.f2041g);
        this.f2040f = fVar;
        this.f2039e.registerForegroundActivitiesChangedListener(fVar);
    }

    static synchronized l.a j(Context context, EventsAggregator eventsAggregator) {
        try {
            if (f2037h == null) {
                f2037h = new d(context, eventsAggregator);
            }
        } catch (Throwable th) {
            throw th;
        }
        return f2037h;
    }

    @Override // l.l.a
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
