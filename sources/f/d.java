package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.DimController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: Dim.java */
/* JADX INFO: loaded from: classes.dex */
public class d extends q {
    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->Dim", " updateCloud.");
        if (this.f1811a) {
            DimController.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->Dim", " created.");
        this.f1812b = context;
        DimController.notifyDimConfigChanged(SimpleSettings.Misc.getString(context, DimController.CLOUD_DIM_CONFIG, ""));
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->Dim", " dump, args = " + Arrays.toString(strArr));
        if (this.f1811a) {
            DimController.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
