package f;

import android.annotation.Nullable;
import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.dfs.DfsPowerManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import miui.os.Build;
import miui.system.R;
import org.json.JSONObject;

/* JADX INFO: compiled from: DfsPower.java */
/* JADX INFO: loaded from: classes.dex */
public class c extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private DfsPowerManager f1802c = null;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->DfsPower", " updateCloud.");
    }

    public j b(Context context) {
        Log.d("M->DfsPower", " created.");
        this.f1812b = context;
        for (String str : context.getResources().getStringArray(Build.IS_INTERNATIONAL_BUILD ? R.array.dfs_support_devices_international : R.array.dfs_support_devices)) {
            if (str.equals(android.os.Build.DEVICE)) {
                this.f1802c = new DfsPowerManager(context);
                this.f1811a = true;
                return this;
            }
        }
        Log.d("M->DfsPower", " Dfs is not support for device " + android.os.Build.DEVICE);
        return null;
    }

    @Nullable
    public DfsPowerManager c() {
        return this.f1802c;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->DfsPower", " destroy.");
        if (this.f1811a) {
            this.f1802c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->DfsPower", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1802c == null) {
            printWriter.println("mDfsPowerManager disabled");
        } else {
            printWriter.println("mDfsPowerManager enabled");
            this.f1802c.dump(printWriter, strArr);
        }
    }
}
