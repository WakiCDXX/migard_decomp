package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.perfengine.PerfEngineManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

/* JADX INFO: compiled from: PerfEngine.java */
/* JADX INFO: loaded from: classes.dex */
public class s extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private PerfEngineManager f1814c;

    public j b(Context context) {
        Log.d("M->PerfEngine", " created.");
        this.f1812b = context;
        this.f1814c = PerfEngineManager.e(context);
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->PerfEngine", " destroy.");
        if (this.f1811a) {
            PerfEngineManager.a();
            this.f1814c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        PerfEngineManager perfEngineManager;
        Log.d("M->PerfEngine", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || (perfEngineManager = this.f1814c) == null) {
            return;
        }
        perfEngineManager.b(fileDescriptor, printWriter, strArr);
    }
}
