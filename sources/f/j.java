package f;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import org.json.JSONObject;

/* JADX INFO: compiled from: IModule.java */
/* JADX INFO: loaded from: classes.dex */
public interface j {
    void a(JSONObject jSONObject);

    void destroy();

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);
}
