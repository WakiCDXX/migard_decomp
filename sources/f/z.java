package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.PadSleepModeController;
import com.miui.powerkeeper.statemachine.PhoneSleepModeController;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import miui.os.Build;
import org.json.JSONObject;

/* JADX INFO: compiled from: SleepModeController.java */
/* JADX INFO: loaded from: classes.dex */
public class z extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private volatile SleepModeControllerNew f1821c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Context f1822d;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        super.a(jSONObject);
    }

    public j b(Context context) {
        this.f1822d = context;
        if (!this.f1811a) {
            synchronized (z.class) {
                try {
                    if (!this.f1811a) {
                        if (Build.IS_TABLET) {
                            this.f1821c = PadSleepModeController.init(this.f1822d);
                        } else {
                            this.f1821c = PhoneSleepModeController.init(this.f1822d);
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        this.f1811a = true;
        StringBuilder sb = new StringBuilder();
        sb.append("init sleepMode type : ");
        sb.append(this.f1821c != null ? this.f1821c.getClass().getSimpleName() : "null");
        Log.d("M->SatelliteTelephony", sb.toString());
        return this;
    }

    public SleepModeControllerNew c() {
        return this.f1821c;
    }

    @Override // f.q, f.j
    public void destroy() {
        super.destroy();
        if (this.f1811a) {
            this.f1821c = null;
            this.f1822d = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
    }
}
