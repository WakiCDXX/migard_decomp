package com.xiaomi.onetrack.c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
public class p extends Handler {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1305a = "UploadTimer";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final int f1306b = 5000;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final int f1307c = 15000;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final int f1308d = 1200000;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final int f1309e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final int f1310f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final int f1311g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private int f1312h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private AtomicBoolean f1313i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private BroadcastReceiver f1314j;

    public p(Looper looper) {
        super(looper);
        this.f1309e = 1000;
        this.f1310f = 10000;
        this.f1311g = f1308d;
        this.f1312h = 10000;
        this.f1313i = new AtomicBoolean(false);
        this.f1314j = new q(this);
        a(com.xiaomi.onetrack.f.a.b());
    }

    private void b() {
        if (t.a().a(2)) {
            this.f1312h = 10000;
            com.xiaomi.onetrack.util.p.a(f1305a, "retry success");
            return;
        }
        removeMessages(1000);
        int i2 = this.f1312h * 2;
        this.f1312h = i2;
        if (i2 > f1308d) {
            this.f1312h = f1308d;
        }
        com.xiaomi.onetrack.util.p.a(f1305a, "will restart retry msg after " + this.f1312h);
        sendEmptyMessageDelayed(1000, (long) this.f1312h);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        super.handleMessage(message);
        if (!com.xiaomi.onetrack.b.n.a() || !com.xiaomi.onetrack.b.n.c() || com.xiaomi.onetrack.b.n.b()) {
            com.xiaomi.onetrack.util.p.a(f1305a, "不用处理消息, available=" + com.xiaomi.onetrack.b.n.a() + ", 是否有网=" + com.xiaomi.onetrack.b.n.c() + ", 数据库是否为空=" + com.xiaomi.onetrack.b.n.b());
            return;
        }
        int i2 = message.what;
        if (i2 == 1000) {
            b();
            return;
        }
        boolean zA = t.a().a(i2);
        com.xiaomi.onetrack.util.p.a(f1305a, "handleCheckUpload ret=" + zA + ", prio=" + i2);
        if (zA) {
            return;
        }
        com.xiaomi.onetrack.util.p.a(f1305a, "handleCheckUpload failed, will check if need to send retry msg");
        if (hasMessages(1000)) {
            return;
        }
        sendEmptyMessageDelayed(1000, this.f1312h);
        com.xiaomi.onetrack.util.p.a(f1305a, "fire retry timer after " + this.f1312h);
    }

    public void a(int i2, boolean z2) {
        if (hasMessages(1000)) {
            com.xiaomi.onetrack.util.p.a(f1305a, "in retry mode, return, prio=" + i2);
            return;
        }
        if (z2) {
            removeMessages(i2);
        }
        if (hasMessages(i2)) {
            return;
        }
        long jA = z2 ? 0L : com.xiaomi.onetrack.b.n.a(i2);
        com.xiaomi.onetrack.util.p.a(f1305a, "will check prio=" + i2 + ", delay=" + jA);
        a(i2, jA);
    }

    private void a(Context context) {
        if (context == null) {
            return;
        }
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            context.registerReceiver(this.f1314j, intentFilter, null, this);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.a(f1305a, "registerNetReceiver: " + e2);
        }
    }

    private void a(int i2, long j2) {
        removeMessages(i2);
        com.xiaomi.onetrack.util.p.a(f1305a, "will post msg, prio=" + i2 + ", delay=" + j2);
        sendEmptyMessageDelayed(i2, j2);
    }

    public void a() {
        com.xiaomi.onetrack.util.i.a(new s(this));
    }
}
