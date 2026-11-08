package i;

import android.content.Context;
import android.os.Build;
import android.util.LocalLog;
import android.util.Log;
import com.miui.powerkeeper.event.EventsAggregator;
import j.e;
import j.g;
import j.h;
import j.i;
import j.j;
import j.k;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import k.b;
import l.l;

/* JADX INFO: compiled from: UnionPowerController.java */
/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static a f1888e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final LocalLog f1889f;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f1890a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private List<h> f1891b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final l f1892c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private b f1893d;

    static {
        f1889f = new LocalLog(Build.IS_DEBUGGABLE ? 256 : 64);
    }

    private a(Context context) {
        this.f1890a = context;
        l lVarB = l.b(context, EventsAggregator.getInstance(context));
        this.f1892c = lVarB;
        d();
        lVarB.c();
        this.f1893d = b.d(context);
    }

    public static void a(String str, String str2) {
        try {
            f1889f.log(str + ": " + str2);
        } catch (Exception unused) {
            Log.w("UnionPower.ctrl", "append opt failed");
        }
    }

    public static synchronized a c(Context context) {
        try {
            if (f1888e == null && context != null) {
                f1888e = new a(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return f1888e;
    }

    private void d() {
        ArrayList arrayList = new ArrayList();
        this.f1891b = arrayList;
        arrayList.add(j.n(this.f1890a));
        this.f1891b.add(e.A(this.f1890a));
        this.f1891b.add(i.i(this.f1890a));
        this.f1891b.add(j.l.n(this.f1890a));
        this.f1891b.add(g.h(this.f1890a));
        this.f1891b.add(k.h(this.f1890a));
    }

    public void b(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("UnionPowerController enable");
        for (h hVar : this.f1891b) {
            if (hVar != null) {
                printWriter.println(hVar.a() + ">>>");
                hVar.dump(fileDescriptor, printWriter, strArr);
                printWriter.println("<<<" + hVar.a());
                printWriter.println(" ");
            }
        }
        b bVar = this.f1893d;
        if (bVar != null) {
            bVar.b(fileDescriptor, printWriter, strArr);
        }
        this.f1892c.a(fileDescriptor, printWriter, strArr);
        f1889f.dump(printWriter);
    }

    public void e() {
    }
}
