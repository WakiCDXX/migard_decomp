package e;

import android.annotation.Nullable;
import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.logsystem.config.LogType;
import f.a0;
import f.b0;
import f.c0;
import f.g;
import f.h;
import f.i;
import f.j;
import f.k;
import f.l;
import f.m;
import f.n;
import f.o;
import f.p;
import f.r;
import f.s;
import f.t;
import f.u;
import f.v;
import f.w;
import f.x;
import f.y;
import f.z;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: ModulesManager.java */
/* JADX INFO: loaded from: classes.dex */
public class e {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static e f1784g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static JSONObject f1785h;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Context f1790c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final SparseBooleanArray f1782e = new SparseBooleanArray();

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final SparseArray<j> f1783f = new SparseArray<>();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static volatile boolean f1786i = false;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static ArrayList<a> f1787j = new ArrayList<>();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private int f1788a = 0;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private int f1789b = 0;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final Object f1791d = new Object();

    /* JADX INFO: compiled from: ModulesManager.java */
    public interface a {
        void a();
    }

    private e(Context context) {
        Log.d("M->ModulesManager", "ModulesManager create");
        this.f1790c = context;
        new Thread(new Runnable() { // from class: e.c
            @Override // java.lang.Runnable
            public final void run() {
                this.f1780a.q();
            }
        }).start();
        f1784g = this;
    }

    private int d(String str) {
        Log.d("M->ModulesManager", "StringToBinary s=" + str);
        if (str == null) {
            return 0;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            if (str.charAt(i3) == '1') {
                i2 = (i2 << 1) | 1;
            } else if (str.charAt(i3) == '0') {
                i2 <<= 1;
            }
        }
        return i2;
    }

    private void e(int i2, j jVar) {
        if (jVar != null) {
            f1783f.put(i2, jVar);
        }
    }

    @Deprecated
    public static synchronized e h() {
        try {
            if (f1784g == null) {
                f1784g = new e(PowerKeeperApplication.b());
            }
        } catch (Throwable th) {
            throw th;
        }
        return f1784g;
    }

    @Nullable
    public static j i(int i2) {
        if (k()) {
            return f1783f.get(i2);
        }
        return null;
    }

    private void j() {
        if (f.a()) {
            this.f1788a = d(SystemProperties.get("persist.power.module.base", (String) null));
            int iD = d(SystemProperties.get("persist.power.module", (String) null));
            this.f1789b = iD;
            if (this.f1788a != 0 || iD != 0) {
                Log.d("M->ModulesManager", "init debug mBaseModules=" + String.format("%x", Integer.valueOf(this.f1788a)));
                Log.d("M->ModulesManager", "init debug mOtherModules=" + String.format("%x", Integer.valueOf(this.f1789b)));
                m();
                return;
            }
        }
        w();
        JSONObject jSONObjectL = b.a.p().l(b.b.H);
        if (jSONObjectL == null) {
            this.f1788a = -1;
            this.f1789b = -1;
            Log.d("M->ModulesManager", "init default mBaseModules=" + String.format("%x", Integer.valueOf(this.f1788a)));
            Log.d("M->ModulesManager", "init default mOtherModules=" + String.format("%x", Integer.valueOf(this.f1789b)));
            m();
            return;
        }
        try {
            this.f1788a = d(jSONObjectL.getString("feature_base"));
            this.f1789b = d(jSONObjectL.getString("feature_other1"));
            Log.d("M->ModulesManager", "init Cloud mBaseModules=" + String.format("%x", Integer.valueOf(this.f1788a)));
            Log.d("M->ModulesManager", "init Cloud mOtherModules=" + String.format("%x", Integer.valueOf(this.f1789b)));
            m();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static boolean k() {
        return f1784g != null && f1786i;
    }

    public static boolean l(a aVar) {
        if (k()) {
            return true;
        }
        synchronized (e.class) {
            try {
                ArrayList<a> arrayList = f1787j;
                if (arrayList == null) {
                    return true;
                }
                arrayList.add(aVar);
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void m() {
        n();
        int i2 = 0;
        while (true) {
            SparseBooleanArray sparseBooleanArray = f1782e;
            if (i2 >= sparseBooleanArray.size()) {
                return;
            }
            Log.d("M->ModulesManager", "Module " + e.a.a(sparseBooleanArray.keyAt(i2)) + " = " + sparseBooleanArray.valueAt(i2));
            SparseArray<j> sparseArray = f1783f;
            j jVar = sparseArray.get(sparseBooleanArray.keyAt(i2));
            if (sparseBooleanArray.valueAt(i2)) {
                if (jVar == null) {
                    x(sparseBooleanArray.keyAt(i2));
                }
            } else if (jVar != null) {
                jVar.destroy();
                sparseArray.put(sparseBooleanArray.keyAt(i2), null);
            }
            i2++;
        }
    }

    private void n() {
        synchronized (this.f1791d) {
            try {
                int i2 = this.f1788a;
                for (int i3 = 0; i3 < e.a.f1778a; i3++) {
                    if (((i2 >> i3) & 1) != 0) {
                        f1782e.put(1073741824 | i3, true);
                    } else {
                        f1782e.put(i3 | LogType.LOG_TYPE_F_SAMPLING_CURRENT, false);
                    }
                }
                int i4 = this.f1789b;
                for (int i5 = 0; i5 < e.a.f1779b; i5++) {
                    if (((i4 >> i5) & 1) != 0) {
                        f1782e.put(i5, true);
                    } else {
                        f1782e.put(i5, false);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static boolean o(int i2) {
        e eVar = f1784g;
        return eVar != null && eVar.p(i2);
    }

    private boolean p(int i2) {
        return f1783f.get(i2) != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void q() {
        j();
        f1786i = true;
        synchronized (e.class) {
            try {
                Iterator<a> it = f1787j.iterator();
                while (it.hasNext()) {
                    it.next().a();
                }
                f1787j = null;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void r() {
        f1784g.t();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void s(JSONObject jSONObject) {
        if (jSONObject == null) {
            Log.d("M->ModulesManager", "cloud onChanged, config is null");
            return;
        }
        try {
            int iD = d(jSONObject.getString("feature_base"));
            int iD2 = d(jSONObject.getString("feature_other1"));
            if (this.f1788a == iD && iD2 == this.f1789b) {
                Log.d("M->ModulesManager", "cloud config is not changed.");
                return;
            }
            this.f1788a = iD;
            this.f1789b = iD2;
            Log.d("M->ModulesManager", "Cloud-Observer mBaseModules=" + String.format("%x", Integer.valueOf(this.f1788a)));
            Log.d("M->ModulesManager", "Cloud-Observer mOtherModules=" + String.format("%x", Integer.valueOf(this.f1789b)));
            m();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    private void t() {
        Log.d("M->ModulesManager", "notifyCloudChanged");
        if (f1785h == null) {
            Log.w("M->ModulesManager", "notifyCloudChanged ignored for empty data.");
            return;
        }
        int i2 = 0;
        while (true) {
            SparseArray<j> sparseArray = f1783f;
            if (i2 >= sparseArray.size()) {
                break;
            }
            j jVarValueAt = sparseArray.valueAt(i2);
            if (jVarValueAt != null) {
                jVarValueAt.a(f1785h);
            }
            i2++;
        }
        if (f1785h != null) {
            f1785h = null;
        }
    }

    public static void u(JSONObject jSONObject) {
        f1785h = jSONObject;
        if (l(new a() { // from class: e.b
            @Override // e.e.a
            public final void a() {
                e.r();
            }
        })) {
            f1784g.t();
        }
    }

    private void v(PrintWriter printWriter) {
        for (int i2 = 0; i2 < e.a.f1778a; i2++) {
            printWriter.println(i2 + "\t " + e.a.a(1073741824 | i2));
        }
        printWriter.println();
        for (int i3 = 0; i3 < e.a.f1779b; i3++) {
            printWriter.println(i3 + "\t " + e.a.a(i3));
        }
    }

    private void w() {
        b.a.p().t(b.b.H, new b.a.d() { // from class: e.d
            @Override // b.a.d
            public final void onChanged(JSONObject jSONObject) {
                this.f1781a.s(jSONObject);
            }
        });
    }

    private void x(int i2) {
        if (i2 == 0) {
            e(i2, new l().b(this.f1790c));
        }
        switch (i2) {
            case 2:
                e(i2, new f.d().b(this.f1790c));
                break;
            case 3:
                e(i2, new k().b(this.f1790c));
                break;
            case 4:
                e(i2, new s().b(this.f1790c));
                break;
            case 5:
                e(i2, new c0().b(this.f1790c));
                break;
            case 6:
                e(i2, new f.b().b(this.f1790c));
                break;
            case 7:
                e(i2, new r().b(this.f1790c));
                break;
            default:
                switch (i2) {
                    case 9:
                        e(i2, new u().b(this.f1790c));
                        break;
                    case 10:
                        e(i2, new f.f().b(this.f1790c));
                        break;
                    case 11:
                        e(i2, new f.e().b(this.f1790c));
                        break;
                    case 12:
                        e(i2, new a0().b(this.f1790c));
                        break;
                    case 13:
                        e(i2, new h().b(this.f1790c));
                        break;
                    case 14:
                        e(i2, new g().b(this.f1790c));
                        break;
                    case 15:
                        e(i2, new x().b(this.f1790c));
                        break;
                    case 16:
                        e(i2, new b0().b(this.f1790c));
                        break;
                    case 17:
                        e(i2, new m().b(this.f1790c));
                        break;
                    case 18:
                        e(i2, new y().b(this.f1790c));
                        break;
                    case 19:
                        e(i2, new z().b(this.f1790c));
                        break;
                    case 20:
                        e(i2, new v().b(this.f1790c));
                        break;
                    case 21:
                        e(i2, new p().b(this.f1790c));
                        break;
                    case 22:
                        e(i2, new i().b(this.f1790c));
                        break;
                    case 23:
                        e(i2, new w().b(this.f1790c));
                        break;
                    default:
                        switch (i2) {
                            case LogType.LOG_TYPE_F_SAMPLING_CURRENT /* 1073741824 */:
                                e(i2, new f.a().b(this.f1790c));
                                break;
                            case 1073741825:
                                e(i2, new t().b(this.f1790c));
                                break;
                            case 1073741826:
                                e(i2, new f.c().b(this.f1790c));
                                break;
                            case 1073741827:
                                e(i2, new n().b(this.f1790c));
                                break;
                            case 1073741828:
                                e(i2, new o().b(this.f1790c));
                                break;
                        }
                        break;
                }
                break;
        }
    }

    public void f(int i2) {
        SparseArray<j> sparseArray = f1783f;
        j jVar = sparseArray.get(i2);
        if (jVar != null) {
            jVar.destroy();
        }
        sparseArray.delete(i2);
    }

    public void g(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->ModulesManager", "args = " + Arrays.toString(strArr));
        int i2 = 0;
        if (strArr != null && strArr.length >= 1 && (strArr[0].equals("-m") || strArr[0].equals("-bm"))) {
            if (strArr.length == 1) {
                printWriter.println("param is error.");
                return;
            }
            Log.d("M->ModulesManager", "arg = " + strArr[1]);
            if (strArr[1].equals("list")) {
                v(printWriter);
                return;
            }
            String[] strArr2 = (String[]) Arrays.copyOfRange(strArr, 2, strArr.length);
            Log.d("M->ModulesManager", "newArgs = " + Arrays.toString(strArr2));
            try {
                int i3 = Integer.parseInt(strArr[1]);
                if (strArr[0].equals("-bm")) {
                    i3 |= LogType.LOG_TYPE_F_SAMPLING_CURRENT;
                }
                j jVar = f1783f.get(i3);
                if (jVar != null) {
                    jVar.dump(fileDescriptor, printWriter, strArr2);
                    return;
                }
                printWriter.println("Module " + e.a.a(i3) + " is NOT enabled.");
                return;
            } catch (NumberFormatException unused) {
                printWriter.println("param is error.");
                return;
            }
        }
        while (true) {
            SparseArray<j> sparseArray = f1783f;
            if (i2 >= sparseArray.size()) {
                return;
            }
            j jVarValueAt = sparseArray.valueAt(i2);
            if (jVarValueAt != null) {
                jVarValueAt.dump(fileDescriptor, printWriter, strArr);
            }
            i2++;
        }
    }
}
