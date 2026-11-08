package l;

import android.content.Context;
import android.os.Handler;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: compiled from: UnionPowerObserverCenter.java */
/* JADX INFO: loaded from: classes.dex */
public class l {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static l f2082d;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final HashMap<UnionPowerConfig.ObserverEventEnum, a> f2084b;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final String f2083a = "UnionPower.observer";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final HashMap<UnionPowerConfig.CloudModuleEnum, k> f2085c = new HashMap<>();

    /* JADX INFO: compiled from: UnionPowerObserverCenter.java */
    public interface a {
        void a(Handler handler);

        void b(Handler handler, int... iArr);

        void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);
    }

    private l(Context context, EventsAggregator eventsAggregator) {
        for (UnionPowerConfig.CloudModuleEnum cloudModuleEnum : UnionPowerConfig.CloudModuleEnum.values()) {
            this.f2085c.put(cloudModuleEnum, new k(context, cloudModuleEnum));
        }
        HashMap<UnionPowerConfig.ObserverEventEnum, a> map = new HashMap<>();
        this.f2084b = map;
        map.put(UnionPowerConfig.ObserverEventEnum.TouchEvent, j.t(context, eventsAggregator));
        map.put(UnionPowerConfig.ObserverEventEnum.FgActivityEvent, d.j(context, eventsAggregator));
        map.put(UnionPowerConfig.ObserverEventEnum.FgComponentEvent, c.j(context, eventsAggregator));
        map.put(UnionPowerConfig.ObserverEventEnum.MultiWindowEvent, f.k());
        map.put(UnionPowerConfig.ObserverEventEnum.PowerModeEvent, g.j(eventsAggregator, context));
        map.put(UnionPowerConfig.ObserverEventEnum.AMProcessEvent, l.a.j());
        map.put(UnionPowerConfig.ObserverEventEnum.ThermalScenarioEvent, i.n(eventsAggregator));
    }

    public static synchronized l b(Context context, EventsAggregator eventsAggregator) {
        try {
            if (f2082d == null && context != null && eventsAggregator != null) {
                f2082d = new l(context, eventsAggregator);
            }
        } catch (Throwable th) {
            throw th;
        }
        return f2082d;
    }

    public void a(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("---------- UnionPower.observer ----------");
        a aVar = this.f2084b.get(UnionPowerConfig.ObserverEventEnum.TouchEvent);
        if (aVar != null) {
            aVar.dump(fileDescriptor, printWriter, strArr);
        }
        a aVar2 = this.f2084b.get(UnionPowerConfig.ObserverEventEnum.AMProcessEvent);
        if (aVar2 != null) {
            aVar2.dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.println("---------- UnionPower.observer ----------");
    }

    public void c() {
        b.a aVarP = b.a.p();
        if (aVarP != null) {
            for (Map.Entry<UnionPowerConfig.CloudModuleEnum, k> entry : this.f2085c.entrySet()) {
                aVarP.t(entry.getKey().g(), entry.getValue());
            }
        }
        j.y();
    }

    public void d(Handler handler, UnionPowerConfig.CloudModuleEnum cloudModuleEnum, int i2, String str) {
        k kVar = this.f2085c.get(cloudModuleEnum);
        if (kVar != null) {
            kVar.d(handler, i2, str);
        }
    }

    public void e(Handler handler, UnionPowerConfig.ObserverEventEnum observerEventEnum, int... iArr) {
        a aVar = this.f2084b.get(observerEventEnum);
        if (aVar != null) {
            aVar.b(handler, iArr);
        }
    }

    public void f(Handler handler, UnionPowerConfig.ObserverEventEnum observerEventEnum) {
        a aVar = this.f2084b.get(observerEventEnum);
        if (aVar != null) {
            aVar.a(handler);
        }
    }
}
