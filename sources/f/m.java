package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.utils.GmsObserver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: MGmsObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class m extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private GmsObserver f1808c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->Sync", " updateCloud.");
    }

    public j b(Context context) {
        Log.d("M->Sync", " created.");
        this.f1812b = context;
        this.f1808c = new GmsObserver(context);
        this.f1811a = true;
        return this;
    }

    public GmsObserver c() {
        Log.d("M->Sync", " getInstance. instance = " + this.f1808c);
        return this.f1808c;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->Sync", " destroy.");
        if (this.f1811a) {
            this.f1808c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->Sync", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1808c == null) {
            printWriter.println("mGmsObserver disabled");
        } else {
            printWriter.println("mGmsObserver enabled");
            this.f1808c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
