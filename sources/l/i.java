package l;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.thermal.setting.IProviderCallback;
import com.miui.powerkeeper.thermal.setting.SettingDTO;
import com.miui.powerkeeper.thermal.setting.SettingProvider;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: compiled from: ThermalScenarioObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class i extends b {

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static i f2058h;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final a f2059d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final int f2060e = 0;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private int f2061f = -1;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final Map<Integer, String> f2062g = new HashMap(32);

    /* JADX INFO: compiled from: ThermalScenarioObserver.java */
    final class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == -32) {
                String str = (String) i.this.f2062g.get(Integer.valueOf(i.this.f2061f));
                i.this.f2061f = ((Integer) message.obj).intValue();
                String str2 = (String) i.this.f2062g.getOrDefault(Integer.valueOf(i.this.f2061f), "");
                if (str2.equals(str)) {
                    return;
                }
                i.this.h(str2);
                return;
            }
            if (i2 == 0) {
                i.this.f2062g.clear();
                List<SettingDTO.ScenarioDTO> scenarios = ((SettingDTO) message.obj).getScenarios();
                if (scenarios != null) {
                    for (SettingDTO.ScenarioDTO scenarioDTO : scenarios) {
                        i.this.f2062g.put(Integer.valueOf(scenarioDTO.getID()), scenarioDTO.getName());
                    }
                }
            }
        }
    }

    private i(EventsAggregator eventsAggregator) {
        this.f2028b = eventsAggregator;
        a aVar = new a(UnionPowerService.b().getLooper());
        this.f2059d = aVar;
        SettingProvider.getInstance().registerCallback(new IProviderCallback() { // from class: l.h
            @Override // com.miui.powerkeeper.thermal.setting.IProviderCallback
            public final void onChange(Object obj) {
                this.f2057a.o((SettingDTO) obj);
            }
        });
        this.f2028b.registerScenarioListener(aVar);
    }

    public static synchronized l.a n(EventsAggregator eventsAggregator) {
        try {
            if (f2058h == null) {
                f2058h = new i(eventsAggregator);
            }
        } catch (Throwable th) {
            throw th;
        }
        return f2058h;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void o(SettingDTO settingDTO) {
        if (settingDTO != null) {
            this.f2059d.obtainMessage(0, settingDTO).sendToTarget();
        }
    }

    @Override // l.l.a
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
