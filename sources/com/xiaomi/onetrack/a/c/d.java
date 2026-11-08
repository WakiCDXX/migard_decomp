package com.xiaomi.onetrack.a.c;

import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f940a = "PollingUploader";

    public static void a() {
        ArrayList<com.xiaomi.onetrack.a.b.a> arrayList;
        try {
            if (b()) {
                p.a(f940a, "即将读取数据库并上传数据");
                int i2 = 0;
                while (i2 <= 20) {
                    com.xiaomi.onetrack.a.a.a().d();
                    a aVarB = com.xiaomi.onetrack.a.a.a().b();
                    if (aVarB != null && (arrayList = aVarB.f932b) != null && arrayList.size() > 0) {
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        ArrayList<Integer> arrayList3 = new ArrayList<>();
                        for (com.xiaomi.onetrack.a.b.a aVar : aVarB.f932b) {
                            boolean zB = com.xiaomi.onetrack.g.b.b(aVar.c());
                            int iB = aVar.b();
                            if (zB) {
                                arrayList2.add(Integer.valueOf(iB));
                            } else {
                                arrayList3.add(Integer.valueOf(iB));
                            }
                        }
                        if (arrayList2.size() > 0) {
                            com.xiaomi.onetrack.a.a.a().a(arrayList2);
                        }
                        if (arrayList3.size() > 0) {
                            com.xiaomi.onetrack.a.a.a().b(arrayList3);
                        }
                        i2++;
                        if (aVarB.f933c) {
                            p.a(f940a, "No more ad monitor records");
                            return;
                        }
                    }
                    p.a(f940a, "满足条件的adMonitor记录为空，即将返回");
                    return;
                }
            }
        } catch (Throwable th) {
            p.a(f940a, "uploadData Throwable:" + th.getMessage());
        }
    }

    private static boolean b() {
        if (q.a(f940a)) {
            p.a(f940a, "the device is not provisioned, stop poll!");
            return false;
        }
        if (!com.xiaomi.onetrack.g.c.a()) {
            p.a(f940a, "network is unconnected, stop poll!");
            return false;
        }
        if (com.xiaomi.onetrack.a.a.a().e() != 0) {
            return true;
        }
        p.a(f940a, "no data remain in db, stop poll!");
        return false;
    }
}
