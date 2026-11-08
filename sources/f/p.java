package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.MiawareBcasHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: MiawareBcas.java */
/* JADX INFO: loaded from: classes.dex */
public class p extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private MiawareBcasHandler f1810c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->MiawareBcas", " updateCloud.");
    }

    public j b(Context context) {
        this.f1812b = context;
        if (!e.f.b()) {
            return null;
        }
        this.f1810c = MiawareBcasHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        MiawareBcasHandler miawareBcasHandler;
        Log.d("M->MiawareBcas", " destroy.");
        if (!this.f1811a || (miawareBcasHandler = this.f1810c) == null) {
            return;
        }
        miawareBcasHandler.destroy();
        this.f1811a = false;
        this.f1810c = null;
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->MiawareBcas", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1810c == null) {
            printWriter.println("mBcasHandler disabled");
        } else {
            printWriter.println("MiawareBcasHandler enabled");
            this.f1810c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
