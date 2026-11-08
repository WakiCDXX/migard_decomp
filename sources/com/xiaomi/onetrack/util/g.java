package com.xiaomi.onetrack.util;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.xiaomi.onetrack.OneTrack;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
class g implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1558a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f1559b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f1560c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ d f1561d;

    g(d dVar, String str, String str2, String str3) {
        this.f1561d = dVar;
        this.f1558a = str;
        this.f1559b = str2;
        this.f1560c = str3;
    }

    @Override // java.lang.Runnable
    public void run() {
        String str;
        try {
            if (TextUtils.isEmpty(this.f1558a)) {
                return;
            }
            if (this.f1558a.contains("http://") || this.f1558a.contains("https://")) {
                str = this.f1558a + "/api/open/device/writeBack";
            } else {
                str = "https://" + this.f1558a + "/api/open/device/writeBack";
            }
            HashMap map = new HashMap();
            map.put("instanceId", o.a().b());
            map.put(OneTrack.Param.IMEI_MD5, DeviceUtil.b(this.f1561d.f1553j));
            map.put("oaid", com.xiaomi.onetrack.util.oaid.a.a().a(this.f1561d.f1553j));
            map.put("projectId", this.f1559b);
            map.put(NightStandbyRecord.KEY_USER, this.f1560c);
            String strB = com.xiaomi.onetrack.g.b.b(str, map, false);
            if (!TextUtils.isEmpty(strB) && !"".equals(strB)) {
                this.f1561d.b(strB);
                return;
            }
            Message messageObtain = Message.obtain();
            messageObtain.what = 100;
            Bundle bundle = new Bundle();
            bundle.putString("hint", "注册信息失败，请检查是网络环境是否在内网");
            messageObtain.setData(bundle);
            this.f1561d.f1554k.sendMessage(messageObtain);
        } catch (Exception e2) {
            p.b(d.f1544a, e2.getMessage());
        }
    }
}
