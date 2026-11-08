package j;

import android.content.Context;
import android.os.Handler;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;

/* JADX INFO: compiled from: CommonHandler.java */
/* JADX INFO: loaded from: classes.dex */
abstract class a extends Handler implements h {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    protected static Context f1894d;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected PowerKeeperManager f1895a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    protected EventsAggregator f1896b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    protected l.l f1897c;

    protected a() {
        super(UnionPowerService.b().getLooper());
    }

    public static boolean d() {
        return UnionPowerConfig.f657b;
    }

    protected abstract void e();

    protected abstract void f();

    protected void g(String str) {
        PowerKeeperManager powerKeeperManager = PowerKeeperManager.getInstance();
        this.f1895a = powerKeeperManager;
        EventsAggregator eventsAggregator = powerKeeperManager.getEventsAggregator();
        this.f1896b = eventsAggregator;
        this.f1897c = l.l.b(f1894d, eventsAggregator);
        e();
        f();
        if (b() != null) {
            this.f1897c.d(this, c(), b().intValue(), str);
        }
    }
}
