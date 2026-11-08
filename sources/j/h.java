package j;

import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: compiled from: ICommonHandler.java */
/* JADX INFO: loaded from: classes.dex */
public interface h {
    String a();

    Integer b();

    UnionPowerConfig.CloudModuleEnum c();

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);
}
