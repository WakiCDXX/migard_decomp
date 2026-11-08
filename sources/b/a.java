package b;

import android.annotation.Nullable;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MiuiSettings;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import com.miui.powerkeeper.utils.Utils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: compiled from: CloudObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static a f9g;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f10a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final SparseArray<d> f11b = new SparseArray<>();

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Object f12c = new Object();

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final Object f13d = new Object();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final SparseBooleanArray f14e = new SparseBooleanArray();

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final Handler f15f = new HandlerC0000a(BackgroundThread.getHandler().getLooper());

    /* JADX INFO: renamed from: b.a$a, reason: collision with other inner class name */
    /* JADX INFO: compiled from: CloudObserver.java */
    class HandlerC0000a extends Handler {
        HandlerC0000a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2;
            if (message.what != 1 || (i2 = message.getData().getInt("key")) < 0) {
                return;
            }
            a.this.f14e.append(i2, true);
            MiuiSettings.SettingsCloudData.CloudData cloudDataO = a.this.o(i2);
            if (!a.this.r(SharedPrefUtil.getInstance(a.this.f10a, "cloud_data_sub_module_md5"), cloudDataO, i2) || a.this.f11b.get(i2) == null) {
                return;
            }
            ((d) a.this.f11b.get(i2)).onChanged(cloudDataO == null ? null : cloudDataO.json());
        }
    }

    /* JADX INFO: compiled from: CloudObserver.java */
    class b implements Runnable {
        b() {
        }

        @Override // java.lang.Runnable
        public void run() {
            a.this.u();
        }
    }

    /* JADX INFO: compiled from: CloudObserver.java */
    class c extends ContentObserver {
        c(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            Log.d("Cloud-Observer", "observer changed: " + z2);
            synchronized (a.this.f13d) {
                a.this.i();
            }
        }
    }

    /* JADX INFO: compiled from: CloudObserver.java */
    public interface d {
        void onChanged(JSONObject jSONObject);
    }

    private a() {
        Log.d("Cloud-Observer", "CloudObserver Version = 1.0");
        this.f10a = PowerKeeperApplication.b();
        new Handler(BackgroundThread.getHandler().getLooper()).post(new b());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void i() {
        SharedPrefUtil sharedPrefUtil = SharedPrefUtil.getInstance(this.f10a, "cloud_data_sub_module_md5");
        synchronized (this.f12c) {
            for (int i2 = 0; i2 < this.f11b.size(); i2++) {
                try {
                    int iKeyAt = this.f11b.keyAt(i2);
                    MiuiSettings.SettingsCloudData.CloudData cloudDataO = o(iKeyAt);
                    this.f14e.append(iKeyAt, true);
                    if (r(sharedPrefUtil, cloudDataO, iKeyAt)) {
                        this.f11b.get(iKeyAt).onChanged(cloudDataO == null ? null : cloudDataO.json());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private void k(PrintWriter printWriter) {
        printWriter.println("Supported modules cloud data:");
        for (int i2 = 0; i2 <= b.b.W; i2++) {
            JSONObject jSONObjectL = l(i2);
            printWriter.println("ID:" + i2);
            printWriter.println("    Name:" + b.b.X.get(i2));
            printWriter.println("    Data:" + jSONObjectL);
            printWriter.println("");
        }
    }

    private List<MiuiSettings.SettingsCloudData.CloudData> n(int i2) {
        synchronized (this.f13d) {
            if (i2 >= 0) {
                try {
                    if (i2 <= b.b.W) {
                        return MiuiSettings.SettingsCloudData.getCloudDataList(this.f10a.getContentResolver(), b.b.X.get(i2));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            Log.d("Cloud-Observer", "getCloud Error. index=" + i2 + "is not defined.");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MiuiSettings.SettingsCloudData.CloudData o(int i2) {
        if (i2 >= 0 && i2 <= b.b.W) {
            return MiuiSettings.SettingsCloudData.getCloudDataSingle(this.f10a.getContentResolver(), b.b.X.get(i2), (String) null, (String) null, false);
        }
        Log.d("Cloud-Observer", "getCloud Error. index=" + i2 + "is not defined.");
        return null;
    }

    public static synchronized a p() {
        try {
            if (f9g == null) {
                f9g = new a();
            }
        } catch (Throwable th) {
            throw th;
        }
        return f9g;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean r(SharedPrefUtil sharedPrefUtil, MiuiSettings.SettingsCloudData.CloudData cloudData, int i2) {
        String str = b.b.X.get(i2);
        String stringValue = sharedPrefUtil.getStringValue(str, "");
        if (cloudData == null) {
            sharedPrefUtil.save(str, "");
            StringBuilder sb = new StringBuilder();
            sb.append("isCloudDataChanged data is null for module: ");
            sb.append(str);
            sb.append(", changed: ");
            sb.append(stringValue.length() > 0);
            Log.d("Cloud-Observer", sb.toString());
            this.f14e.delete(i2);
            return stringValue.length() > 0;
        }
        String strMD5 = Utils.MD5(cloudData.toString());
        Log.d("Cloud-Observer", "isCloudDataChanged for " + str + " new newMd5=" + strMD5);
        Log.d("Cloud-Observer", "isCloudDataChanged for " + str + " old newMd5=" + sharedPrefUtil.getStringValue(str, ""));
        if (stringValue.equals(strMD5)) {
            return false;
        }
        sharedPrefUtil.save(str, strMD5);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void u() {
        try {
            Log.d("Cloud-Observer", "registerObserver");
            this.f10a.getContentResolver().registerContentObserver(MiuiSettings.SettingsCloudData.getCloudDataNotifyUri(), true, new c(BackgroundThread.getHandler()));
        } catch (Exception e2) {
            Log.e("Cloud-Observer", "CloudObserver registerObserver exception message =" + e2.getMessage());
        }
    }

    /* JADX WARN: Code duplicated, block: B:21:0x0039  */
    public void j(PrintWriter printWriter, String[] strArr) {
        byte b2;
        if (strArr == null || strArr.length <= 1) {
            printWriter.println("Cloud Dump Start");
            printWriter.println("CloudVersion=1.0");
            k(printWriter);
            printWriter.println("Cloud Dump End");
            return;
        }
        String str = strArr[1];
        int iHashCode = str.hashCode();
        if (iHashCode != 1495) {
            if (iHashCode != 1500) {
                if (iHashCode == 1504 && str.equals("-m")) {
                    b2 = 1;
                } else {
                    b2 = -1;
                }
            } else if (str.equals("-i")) {
                b2 = 2;
            } else {
                b2 = -1;
            }
        } else if (str.equals("-d")) {
            b2 = 0;
        } else {
            b2 = -1;
        }
        if (b2 == 0) {
            SharedPrefUtil sharedPrefUtil = SharedPrefUtil.getInstance(this.f10a, "cloud_data_sub_module_md5");
            printWriter.println("Supported modules:");
            for (int i2 = 0; i2 <= b.b.W; i2++) {
                ArrayList<String> arrayList = b.b.X;
                if (sharedPrefUtil.getStringValue(arrayList.get(i2), "").length() > 1) {
                    printWriter.println("    ID:" + i2 + ", \tName:" + arrayList.get(i2));
                }
                printWriter.println();
            }
            return;
        }
        if (b2 == 1) {
            k(printWriter);
            return;
        }
        if (b2 != 2) {
            return;
        }
        if (strArr.length <= 2) {
            printWriter.println("Param error.");
            return;
        }
        int i3 = Integer.parseInt(strArr[2]);
        printWriter.println("ID:" + i3 + ", \tName:" + b.b.X.get(i3));
        JSONObject jSONObjectL = l(i3);
        StringBuilder sb = new StringBuilder();
        sb.append("Data:");
        sb.append(jSONObjectL);
        printWriter.println(sb.toString());
    }

    @Nullable
    public JSONObject l(int i2) {
        MiuiSettings.SettingsCloudData.CloudData cloudDataO = o(i2);
        if (cloudDataO != null) {
            return cloudDataO.json();
        }
        SharedPrefUtil.getInstance(this.f10a, "cloud_data_sub_module_md5").save(b.b.X.get(i2), "");
        return null;
    }

    public List<JSONObject> m(int i2) {
        List<MiuiSettings.SettingsCloudData.CloudData> listN = n(i2);
        if (listN == null || listN.isEmpty()) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (MiuiSettings.SettingsCloudData.CloudData cloudData : listN) {
            if (cloudData != null) {
                arrayList.add(cloudData.json());
            }
        }
        return arrayList;
    }

    public String q(int i2) {
        return SharedPrefUtil.getInstance(this.f10a, "cloud_data_sub_module_md5").getStringValue(b.b.X.get(i2), "");
    }

    public boolean s(int i2) {
        return this.f14e.get(i2, false) || SharedPrefUtil.getInstance(this.f10a, "cloud_data_sub_module_md5").getStringValue(b.b.X.get(i2), "").length() > 0;
    }

    public void t(int i2, d dVar) {
        if (i2 < 0 || i2 > b.b.W) {
            Log.d("Cloud-Observer", "registerForCloudChangedCallback Error. index=" + i2 + "is not defined.");
            return;
        }
        Log.d("Cloud-Observer", "registerForCloudChangedCallback index=" + i2);
        synchronized (this.f12c) {
            this.f11b.put(i2, dVar);
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("key", i2);
            message.setData(bundle);
            message.what = 1;
            this.f15f.sendMessage(message);
        }
    }
}
