package com.miui.powerkeeper;

import android.os.Bundle;
import android.os.Handler;
import android.util.SparseIntArray;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperInterface {

    public interface a0 {
        void onSleepMode(boolean z2);
    }

    public interface b0 {
        void onUserEnableStatusChange(int i2, boolean z2);

        void onUserForegroundStatusChange(int i2, boolean z2);

        void onUserRunningStatusChange(int i2, boolean z2);

        void onXSpaceStatusChange(int i2, boolean z2);
    }

    public interface c0 {
        void onWidgetChange(int i2, Integer[] numArr);
    }

    public interface i {
        void onAlarmHappen(long j2);
    }

    public interface j {
        void onAppActiveChange(int i2, boolean z2);
    }

    public interface k {
        void onAppRuleChange(int i2, int i3);
    }

    public interface l {
        void disable();

        void enable();

        int getAppRule(int i2);

        SparseIntArray getAppsRule(int[] iArr);

        void registerAppRuleChangeListener(c cVar);

        void setUidPolicy(int i2, Bundle bundle);

        void unregisterAppRuleChangeListener(c cVar);
    }

    public interface m {
        void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7);
    }

    public interface n {
    }

    public interface o {
        void batteryBecomeHeat();

        void batteryCoolDown();
    }

    public interface p {
        void googleNetworkConnected();

        void googleNetworkDisconnect();
    }

    public interface q {
    }

    public interface r {
        void onForegroundActivitiesChanged(int i2, boolean z2);
    }

    public interface s {
        void onForegroundPreTaskChanged(int i2, boolean z2);
    }

    public interface t {
        void onForegroundServiceChanged(int i2, boolean z2);
    }

    public interface u {
        void a(int[] iArr);

        void b(String str);
    }

    public interface v {
        void onIMEChange(int i2, int i3);
    }

    public interface x {
        void onPackageAdded(int i2, String str, int i3);

        void onPackageRemoved(int i2, String str, int i3);

        void onRegistered();

        void onUidRemoved(int i2, int i3);

        void onUserReadyPackageAdded(int i2, Map<String, Integer> map);
    }

    public interface y {
        void onProcessDiedListener(int i2, int i3);
    }

    public interface z {
        void onRegistered(boolean z2);

        void onScreenChanged(boolean z2);
    }

    public static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f245a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public i f246b;

        public a() {
            a();
        }

        public void a() {
            this.f245a = null;
            this.f246b = null;
        }

        public a(Handler handler, i iVar) {
            this.f245a = handler;
            this.f246b = iVar;
        }
    }

    public static class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f247a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public j f248b;

        public b() {
            a();
        }

        public void a() {
            this.f247a = null;
            this.f248b = null;
        }

        public b(Handler handler, j jVar) {
            this.f247a = handler;
            this.f248b = jVar;
        }
    }

    public static class c {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f249a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public k f250b;

        public c() {
            a();
        }

        public void a() {
            this.f249a = null;
            this.f250b = null;
        }

        public c(Handler handler, k kVar) {
            this.f249a = handler;
            this.f250b = kVar;
        }
    }

    public static class d {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f251a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public m f252b;

        public d() {
            a();
        }

        public void a() {
            this.f251a = null;
            this.f252b = null;
        }

        public d(Handler handler, m mVar) {
            this.f251a = handler;
            this.f252b = mVar;
        }
    }

    public static class d0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f253a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public x f254b;

        public d0() {
            a();
        }

        public void a() {
            this.f253a = null;
            this.f254b = null;
        }

        public d0(Handler handler, x xVar) {
            this.f253a = handler;
            this.f254b = xVar;
        }
    }

    public static class e {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f255a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public o f256b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public p f257c;

        public e() {
            a();
        }

        public void a() {
            this.f255a = null;
            this.f256b = null;
            this.f257c = null;
        }

        public e(Handler handler, o oVar, q qVar, p pVar) {
            this.f255a = handler;
            this.f256b = oVar;
            this.f257c = pVar;
        }

        public e(Handler handler, o oVar, q qVar, p pVar, n nVar) {
            this.f255a = handler;
            this.f256b = oVar;
            this.f257c = pVar;
        }
    }

    public static class e0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f258a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public y f259b;

        public e0() {
            a();
        }

        public void a() {
            this.f258a = null;
            this.f259b = null;
        }

        public e0(Handler handler, y yVar) {
            this.f258a = handler;
            this.f259b = yVar;
        }
    }

    public static class f {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f260a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public r f261b;

        public f() {
            a();
        }

        public void a() {
            this.f260a = null;
            this.f261b = null;
        }

        public f(Handler handler, r rVar) {
            this.f260a = handler;
            this.f261b = rVar;
        }
    }

    public static class f0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f262a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public z f263b;

        public f0() {
            a();
        }

        public void a() {
            this.f262a = null;
            this.f263b = null;
        }

        public f0(Handler handler, z zVar) {
            this.f262a = handler;
            this.f263b = zVar;
        }
    }

    public static class g {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f264a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public s f265b;

        public g() {
            a();
        }

        public void a() {
            this.f264a = null;
            this.f265b = null;
        }

        public g(Handler handler, s sVar) {
            this.f264a = handler;
            this.f265b = sVar;
        }
    }

    public static class g0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f266a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public b0 f267b;

        public g0() {
            a();
        }

        public void a() {
            this.f266a = null;
            this.f267b = null;
        }

        public g0(Handler handler, b0 b0Var) {
            this.f266a = handler;
            this.f267b = b0Var;
        }
    }

    public static class h {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f268a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public t f269b;

        public h() {
            a();
        }

        public void a() {
            this.f268a = null;
            this.f269b = null;
        }

        public h(Handler handler, t tVar) {
            this.f268a = handler;
            this.f269b = tVar;
        }
    }

    public static class h0 {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f270a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public c0 f271b;

        public h0() {
            a();
        }

        public void a() {
            this.f270a = null;
            this.f271b = null;
        }

        public h0(Handler handler, c0 c0Var) {
            this.f270a = handler;
            this.f271b = c0Var;
        }
    }

    public static class w {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public Handler f272a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public v f273b;

        public w() {
            a();
        }

        public void a() {
            this.f272a = null;
            this.f273b = null;
        }

        public w(Handler handler, v vVar) {
            this.f272a = handler;
            this.f273b = vVar;
        }
    }
}
