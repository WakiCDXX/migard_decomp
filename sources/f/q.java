package f;

import android.content.Context;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: Module.java */
/* JADX INFO: loaded from: classes.dex */
public abstract class q implements j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    volatile boolean f1811a = false;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    Context f1812b;

    @Override // f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->Base", " updateCloud.  ##DO NOTHING##");
    }

    @Override // f.j
    public void destroy() {
        Log.d("M->Base", " destroy.  ##DO NOTHING##");
    }

    @Override // f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->Base", " dump, args = " + Arrays.toString(strArr) + "  ##DO NOTHING##");
    }
}
